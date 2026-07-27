
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import dto.Register_dto;

public class RegisterDao extends BaseDao {

    /**
     * 資産番号の存在チェック
     * ※既存処理や他画面で使っている可能性があるため残しています。
     */
    public boolean existsByAssetNumber(String assetNumber) throws Exception {
        String sql = "SELECT COUNT(*) FROM terminal_management WHERE asset_number = ? AND is_deleted = '0'";

        System.out.println("[SQL][RegisterDao.existsByAssetNumber] " + sql);
        System.out.println("[SQL][RegisterDao.existsByAssetNumber] params: assetNumber=" + assetNumber);

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, assetNumber);

            long execStart = System.currentTimeMillis();

            try (ResultSet rs = ps.executeQuery()) {
                long execTook = System.currentTimeMillis() - execStart;

                if (rs.next()) {
                    int count = rs.getInt(1);

                    System.out.println("[SQL][RegisterDao.existsByAssetNumber] count=" + count
                            + " (query took " + execTook + " ms)");

                    return count > 0;
                }
            }
        }

        return false;
    }

    /**
     * シリアル番号の存在チェック
     */
    public boolean existsBySerialNumber(String serialNumber) throws Exception {
        String sql = "SELECT COUNT(*) FROM terminal_management WHERE serial_number = ? AND is_deleted = '0'";

        System.out.println("[SQL][RegisterDao.existsBySerialNumber] " + sql);
        System.out.println("[SQL][RegisterDao.existsBySerialNumber] params: serialNumber=" + serialNumber);

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, serialNumber);

            long execStart = System.currentTimeMillis();

            try (ResultSet rs = ps.executeQuery()) {
                long execTook = System.currentTimeMillis() - execStart;

                if (rs.next()) {
                    int count = rs.getInt(1);

                    System.out.println("[SQL][RegisterDao.existsBySerialNumber] count=" + count
                            + " (query took " + execTook + " ms)");

                    return count > 0;
                }
            }
        }

        return false;
    }

    /**
     * 現在の最大ID + 1 を取得
     */
    private long getNextId(Connection con) throws Exception {
        String sql = "SELECT COALESCE(MAX(id), 0) + 1 FROM terminal_management";

        System.out.println("[SQL][RegisterDao.getNextId] " + sql);

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                long nextId = rs.getLong(1);

                System.out.println("[SQL][RegisterDao.getNextId] nextId=" + nextId);

                return nextId;
            }
        }

        return 1L;
    }

    /**
     * 登録（id は Java側で MAX(id)+1 を計算）
     */
    public int insert(Register_dto dto, String loginUserId) throws Exception {
        long start = System.currentTimeMillis();

        String sql =
            "INSERT INTO terminal_management (" +
            " id," +
            " asset_number," +
            " serial_number," +
            " inno_hin," +
            " tel," +
            " contract_date," +
            " contract_period," +
            " termination_date," +
            " rental_company," +
            " tanka," +
            " udid," +
            " mac_address," +
            " is_fault_return," +
            " isd_inventory," +
            " is_deleted," +
            " reserve1," +
            " reserve2," +
            " reserve3," +
            " reserve4," +
            " reserve5," +
            " reserve6," +
            " reserve7," +
            " reserve8," +
            " reserve9," +
            " reserve10," +
            " insert_date," +
            " insert_user," +
            " update_date," +
            " update_user" +
            ") VALUES (" +
            " ?, ?, ?, ?, ?, ?, ?, NULL, ?, ?, ?, ?," +
            " '0', '0', '0'," +
            " NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL," +
            " NOW(), ?, NOW(), ?" +
            ")";

        System.out.println("[SQL][RegisterDao.insert] " + sql);

        try (Connection con = getConnection()) {

            long nextId = getNextId(con);

            System.out.println("[SQL][RegisterDao.insert] params:"
                    + " id=" + nextId
                    + ", asset_number=" + dto.getAssetNumber()
                    + ", serial_number=" + dto.getSerialNumber()
                    + ", inno_hin=" + dto.getInnoHin()
                    + ", tel=" + dto.getTel()
                    + ", contract_date=" + dto.getContractDate()
                    + ", contract_period=" + dto.getContractPeriod()
                    + ", rental_company=" + dto.getRentalCompany()
                    + ", tanka=" + dto.getTanka()
                    + ", udid=" + dto.getUdid()
                    + ", mac_address=" + dto.getMacAddress()
                    + ", insert_user=" + loginUserId
                    + ", update_user=" + loginUserId);

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                int idx = 1;

                ps.setLong(idx++, nextId);
                ps.setString(idx++, dto.getAssetNumber());
                ps.setString(idx++, dto.getSerialNumber());
                ps.setString(idx++, dto.getInnoHin());
                ps.setString(idx++, dto.getTel());
                ps.setDate(idx++, dto.getContractDate());
                ps.setDate(idx++, dto.getContractPeriod());
                ps.setString(idx++, dto.getRentalCompany());
                ps.setBigDecimal(idx++, dto.getTanka());
                ps.setString(idx++, dto.getUdid());
                ps.setString(idx++, dto.getMacAddress());
                ps.setString(idx++, loginUserId);
                ps.setString(idx++, loginUserId);

                long execStart = System.currentTimeMillis();

                int result = ps.executeUpdate();

                long execTook = System.currentTimeMillis() - execStart;

                System.out.println("[SQL][RegisterDao.insert] result=" + result
                        + " (query took " + execTook + " ms)");

                return result;
            }

        } catch (Exception e) {
            System.out.println("[SQL][RegisterDao.insert] ERROR: "
                    + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace(System.out);
            throw e;

        } finally {
            long took = System.currentTimeMillis() - start;
            System.out.println("[SQL][RegisterDao.insert] total took " + took + " ms");
        }
    }
}
