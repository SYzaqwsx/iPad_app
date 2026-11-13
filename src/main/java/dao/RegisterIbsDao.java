package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import dto.Register_dto;

public class RegisterIbsDao extends BaseDao {

    private static final int IBS_COMPANY_ID = 7000;

    public boolean existsBySerialNumber( String serialNumber) throws Exception {

        String sql = "SELECT COUNT(1) "
                + "FROM terminal_management "
                + "WHERE serial_number = ? "
                + "AND is_deleted = '0'";

        System.out.println( "[SQL][RegisterIbsDao.existsBySerialNumber] "
                        + sql);

        System.out.println( "[SQL][RegisterIbsDao.existsBySerialNumber] params:"
                        + " serialNumber="
                        + serialNumber);

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, serialNumber);

            long execStart = System.currentTimeMillis();

            try (ResultSet rs = ps.executeQuery()) {

                long execTook = System.currentTimeMillis() - execStart;

                if (rs.next()) {
                    int count = rs.getInt(1);

                    System.out.println( "[SQL][RegisterIbsDao.existsBySerialNumber]"
                                    + " count="
                                    + count
                                    + " queryTook="
                                    + execTook
                                    + "ms");

                    return count > 0;
                }
            }
        }
        return false;
    }
    
    public boolean existsBySerialNumberDeleted(String serialNumber) throws Exception {
        String sql = "SELECT COUNT(*) FROM terminal_management WHERE serial_number = ? AND is_deleted = '1'";

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

    public int insertTerminalAndOwner(
            Register_dto dto,
            String loginUserId) throws Exception {

        long start = System.currentTimeMillis();
        Connection con = null;

        try {
            con = getConnection();
            con.setAutoCommit(false);

            if (existsBySerialNumber(
                    con,
                    dto.getSerialNumber())) {

                throw new IllegalStateException(
                        "このシリアル番号は既に登録されています。");
            }

            long terminalId = getNextTerminalId(con);

            int terminalResult = insertTerminalManagement(
                            con,
                            terminalId,
                            dto,
                            loginUserId);

            if (terminalResult != 1) {
                throw new SQLException( "terminal_managementへの登録件数が不正です。"
                                + " result="
                                + terminalResult);
            }

            long ownerId = getNextOwnerId(con);

            int ownerResult = insertOwnerManagement(
                            con,
                            ownerId,
                            dto.getSerialNumber(),
                            loginUserId);

            if (ownerResult != 1) {
                throw new SQLException( "owner_managementへの登録件数が不正です。"
                                + " result="
                                + ownerResult);
            }

            con.commit();

            System.out.println( "[SQL][RegisterIbsDao.insertTerminalAndOwner]"
                            + " COMMIT");

            return terminalResult + ownerResult;

        } catch (Exception e) {

            if (con != null) {
                try {
                    con.rollback();

                    System.out.println( "[SQL][RegisterIbsDao.insertTerminalAndOwner]"
                                    + " ROLLBACK");

                } catch (SQLException rollbackException) {

                    System.out.println( "[SQL][RegisterIbsDao.insertTerminalAndOwner]"
                                    + " rollbackError="
                                    + rollbackException.getMessage());

                    rollbackException.printStackTrace(System.out);
                }
            }

            System.out.println( "[SQL][RegisterIbsDao.insertTerminalAndOwner]"
                            + " errorType="
                            + e.getClass().getSimpleName()
                            + " message="
                            + e.getMessage());

            e.printStackTrace(System.out);

            throw e;

        } finally {

            if (con != null) {
                try {
                    con.setAutoCommit(true);

                } catch (SQLException autoCommitException) {

                    System.out.println( "[SQL][RegisterIbsDao.insertTerminalAndOwner]"
                                    + " autoCommitError="
                                    + autoCommitException.getMessage());
                }
                try {
                    con.close();

                } catch (SQLException closeException) {

                    System.out.println( "[SQL][RegisterIbsDao.insertTerminalAndOwner]"
                                    + " closeError="
                                    + closeException.getMessage());
                }
            }

            long took = System.currentTimeMillis() - start;

            System.out.println( "[SQL][RegisterIbsDao.insertTerminalAndOwner]"
                            + " totalTook="
                            + took
                            + "ms");
        }
    }

    private boolean existsBySerialNumber( Connection con, String serialNumber) throws Exception {

        String sql =
                "SELECT COUNT(1) "
                + "FROM terminal_management "
                + "WHERE serial_number = ? "
                + "AND is_deleted = '0'";

        System.out.println( "[SQL][RegisterIbsDao.existsBySerialNumberInTransaction] " + sql);

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, serialNumber);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) { int count = rs.getInt(1);

                    System.out.println("[SQL][RegisterIbsDao"
                                    + ".existsBySerialNumberInTransaction]"
                                    + " count="
                                    + count);

                    return count > 0;
                }
            }
        }
        return false;
    }

    private long getNextTerminalId(
            Connection con) throws Exception {

        String sql = "SELECT COALESCE(MAX(id), 0) + 1 " + "FROM terminal_management";

        System.out.println( "[SQL][RegisterIbsDao.getNextTerminalId] " + sql);

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) { long nextId = rs.getLong(1);

                System.out.println("[SQL][RegisterIbsDao.getNextTerminalId]"
                                + " nextId="
                                + nextId);
                return nextId;
            }
        }
        return 1L;
    }

    private long getNextOwnerId( Connection con) throws Exception {

        String sql = "SELECT COALESCE(MAX(id), 0) + 1 " + "FROM owner_management";
        System.out.println("[SQL][RegisterIbsDao.getNextOwnerId] " + sql);

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {long nextId = rs.getLong(1);

                System.out.println("[SQL][RegisterIbsDao.getNextOwnerId]"
                                + " nextId="
                                + nextId);
                return nextId;
            }
        }
        return 1L;
    }

    private int insertTerminalManagement(
            Connection con,
            long terminalId,
            Register_dto dto,
            String loginUserId) throws Exception {

        String sql =
                "INSERT INTO terminal_management ("
                + "id, "
                + "asset_number, "
                + "serial_number, "
                + "inno_hin, "
                + "tel, "
                + "contract_date, "
                + "contract_period, "
                + "termination_date, "
                + "rental_company, "
                + "tanka, "
                + "udid, "
                + "mac_address, "
                + "is_fault_return, "
                + "isd_inventory, "
                + "is_deleted, "
                + "reserve1, "
                + "reserve2, "
                + "reserve3, "
                + "reserve4, "
                + "reserve5, "
                + "reserve6, "
                + "reserve7, "
                + "reserve8, "
                + "reserve9, "
                + "reserve10, "
                + "insert_date, "
                + "insert_user, "
                + "update_date, "
                + "update_user"
                + ") VALUES ("
                + "?, "
                + "?, "
                + "?, "
                + "NULL, "
                + "?, "
                + "?, "
                + "?, "
                + "NULL, "
                + "?, "
                + "?, "
                + "?, "
                + "?, "
                + "'0', "
                + "'0', "
                + "'0', "
                + "NULL, "
                + "NULL, "
                + "NULL, "
                + "NULL, "
                + "NULL, "
                + "NULL, "
                + "NULL, "
                + "NULL, "
                + "NULL, "
                + "NULL, "
                + "NOW(), "
                + "?, "
                + "NOW(), "
                + "?"
                + ")";

        System.out.println("[SQL][RegisterIbsDao.insertTerminalManagement] " + sql);

        System.out.println( "[SQL][RegisterIbsDao.insertTerminalManagement]"
                        + " id="
                        + terminalId
                        + " serialNumber="
                        + dto.getSerialNumber()
                        + " tel="
                        + dto.getTel()
                        + " contractDate="
                        + dto.getContractDate()
                        + " contractPeriod="
                        + dto.getContractPeriod()
                        + " rentalCompany="
                        + dto.getRentalCompany()
                        + " tanka="
                        + dto.getTanka()
                        + " udid="
                        + dto.getUdid()
                        + " macAddress="
                        + dto.getMacAddress()
                        + " loginUserId="
                        + loginUserId);

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            int index = 1;

            ps.setLong( index++, terminalId);
            ps.setString( index++, "");
            ps.setString( index++, emptyToNull(dto.getSerialNumber()));
            ps.setString( index++, emptyToNull(dto.getTel()));
            ps.setDate( index++, dto.getContractDate());
            ps.setDate( index++, dto.getContractPeriod());
            ps.setString( index++, emptyToNull(dto.getRentalCompany()));
            ps.setBigDecimal( index++, dto.getTanka());
            ps.setString( index++, emptyToNull(dto.getUdid()));
            ps.setString( index++, emptyToNull(dto.getMacAddress()));
            ps.setString( index++, loginUserId);
            ps.setString(index++, loginUserId);

            System.out.println("[SQL][RegisterIbsDao.insertTerminalManagement]"
                            + " parameterCount="
                            + (index - 1));

            long execStart = System.currentTimeMillis();

            int result = ps.executeUpdate();

            long execTook = System.currentTimeMillis() - execStart;

            System.out.println("[SQL][RegisterIbsDao.insertTerminalManagement]"
                            + " result="
                            + result
                            + " queryTook="
                            + execTook
                            + "ms");
            return result;
        }
    }
    
    private int insertOwnerManagement(
            Connection con,
            long ownerId,
            String serialNumber,
            String loginUserId) throws Exception {

        String sql =
                "INSERT INTO owner_management ("
                + "id, "
                + "asset_number, "
                + "serial_number, "
                + "company_id, "
                + "department_id, "
                + "owner_num, "
                + "owner_name, "
                + "longrange_usr, "
                + "distribution_date, "
                + "return_date, "
                + "receipt_pdf, "
                + "return_pdf, "
                + "is_deleted, "
                + "reserve1, "
                + "reserve2, "
                + "reserve3, "
                + "reserve4, "
                + "reserve5, "
                + "reserve6, "
                + "reserve7, "
                + "reserve8, "
                + "reserve9, "
                + "reserve10, "
                + "insert_date, "
                + "insert_user, "
                + "update_date, "
                + "update_user"
                + ") VALUES ("
                + "?, "
                + "?, "
                + "?, "
                + "?, "
                + "?, "
                + "?, "
                + "?, "
                + "NULL, "
                + "NOW(), "
                + "NULL, "
                + "NULL, "
                + "NULL, "
                + "'0', "
                + "NULL, "
                + "NULL, "
                + "NULL, "
                + "NULL, "
                + "NULL, "
                + "NULL, "
                + "NULL, "
                + "NULL, "
                + "NULL, "
                + "NULL, "
                + "NOW(), "
                + "?, "
                + "NOW(), "
                + "?"
                + ")";

        System.out.println( "[SQL][RegisterIbsDao.insertOwnerManagement] "
                        + sql);

        System.out.println( "[SQL][RegisterIbsDao.insertOwnerManagement]"
                        + " id="
                        + ownerId
                        + " assetNumber=blank"
                        + " serialNumber="
                        + serialNumber
                        + " companyId="
                        + IBS_COMPANY_ID
                        + " departmentId=blank"
                        + " distributionDate=NOW()"
                        + " loginUserId="
                        + loginUserId);

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            int index = 1;

            ps.setLong( index++, ownerId);
            ps.setString( index++, "");
            ps.setString( index++, serialNumber);
            ps.setInt( index++, IBS_COMPANY_ID);
            ps.setString( index++, "7001");
            ps.setString( index++, "7999");
            ps.setString( index++, "IBSシステム部在庫");
            ps.setString( index++, loginUserId);
            ps.setString( index++, loginUserId);
            System.out.println( "[SQL][RegisterIbsDao.insertOwnerManagement]"
                            + " parameterCount="
                            + (index - 1));
            long execStart = System.currentTimeMillis();
            int result = ps.executeUpdate();
            long execTook = System.currentTimeMillis() - execStart;
            System.out.println( "[SQL][RegisterIbsDao.insertOwnerManagement]"
                            + " result="
                            + result
                            + " queryTook="
                            + execTook
                            + "ms");
            return result;
        }
    }

    private String emptyToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }
}
