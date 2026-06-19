
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import dto.Register_dto;

public class RegisterDao extends BaseDao {

    /**
     * 資産番号の存在チェック
     */
    public boolean existsByAssetNumber(String assetNumber) throws Exception {
        String sql = "SELECT COUNT(*) FROM terminal_management WHERE asset_number = ? AND is_deleted = '0'";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, assetNumber);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
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

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getLong(1);
            }
        }
        return 1L;
    }

    /**
     * 登録（id は Java側で MAX(id)+1 を計算）
     */
    public int insert(Register_dto dto, String loginUserId) throws Exception {
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

        try (Connection con = getConnection()) {

            long nextId = getNextId(con);

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

                return ps.executeUpdate();
            }
        }
    }
}
