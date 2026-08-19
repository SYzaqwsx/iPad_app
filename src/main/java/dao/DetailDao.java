
package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import dto.Detail_dto;

public class DetailDao extends BaseDao {

    public Detail_dto findTerminalById(int id) {
        Detail_dto dto = null;

        String sql =
            "SELECT id, asset_number, serial_number, inno_hin, tel, " +
            "       contract_date, contract_period, termination_date, " +
            "       rental_company, tanka, udid, mac_address " +
            "FROM terminal_management " +
            "WHERE id = ? AND is_deleted = '0'";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    dto = new Detail_dto();
                    dto.setId(rs.getInt("id"));
                    dto.setAssetNumber(rs.getString("asset_number"));
                    dto.setSerialNumber(rs.getString("serial_number"));
                    dto.setInnoHin(rs.getString("inno_hin"));
                    dto.setTel(rs.getString("tel"));
                    dto.setContractDate(rs.getDate("contract_date"));
                    dto.setContractPeriod(rs.getDate("contract_period"));
                    dto.setTerminationDate(rs.getDate("termination_date"));
                    dto.setRentalCompany(rs.getString("rental_company"));

                    int tanka = rs.getInt("tanka");
                    if (rs.wasNull()) {
                        dto.setTanka(null);
                    } else {
                        dto.setTanka(tanka);
                    }

                    dto.setUdid(rs.getString("udid"));
                    dto.setMacAddress(rs.getString("mac_address"));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return dto;
    }

    public List<String[]> getCompanyList() {
        List<String[]> list = new ArrayList<>();

        String sql =
            "SELECT company_id, company_name " +
            "FROM company_management " +
            "ORDER BY company_name";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new String[] {
                    rs.getString("company_id"),
                    rs.getString("company_name")
                });
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    public List<String[]> getDepartmentList() {
        List<String[]> list = new ArrayList<>();

        String sql =
            "SELECT company_id, department_id, department_name " +
            "FROM department_management " +
            "ORDER BY department_name";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new String[] {
                    rs.getString("company_id"),
                    rs.getString("department_id"),
                    rs.getString("department_name")
                });
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    public List<Detail_dto> findOwnerHistoryBySerialNumber(String serialNumber) {
        List<Detail_dto> list = new ArrayList<>();

        String sql =
            "SELECT o.serial_number, o.owner_num, o.owner_name, o.distribution_date, o.return_date, " +
            "       o.longrange_usr, o.receipt_pdf, o.return_pdf, o.company_id, o.department_id, " +
            "       cm.company_name, dm.department_name, " +
            "       t.tel, " +
            "       CASE WHEN EXISTS ( " +
            "           SELECT 1 " +
            "           FROM fault_management f " +
            "           WHERE f.employee_id = o.owner_num " +
            "             AND f.serial_number = o.serial_number " +
            "             AND f.distribution_date = o.distribution_date " +
            "       ) THEN '1' ELSE '0' END AS fault_flag " +
            "FROM owner_management o " +
            "LEFT JOIN company_management cm ON o.company_id = cm.company_id " +
            "LEFT JOIN department_management dm ON o.department_id = dm.department_id " +
            "LEFT JOIN terminal_management t ON o.serial_number = t.serial_number " +
            "WHERE o.serial_number = ? " +
            "ORDER BY o.distribution_date DESC";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, serialNumber);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Detail_dto d = new Detail_dto();

                    d.setSerialNumber(rs.getString("serial_number"));
                    d.setOwnerNum(rs.getString("owner_num"));
                    d.setEmployeeId(rs.getString("owner_num"));
                    d.setOwnerName(rs.getString("owner_name"));
                    d.setDistributionDate(rs.getDate("distribution_date"));
                    d.setReturnDate(rs.getDate("return_date"));
                    d.setLongRangeUser(rs.getString("longrange_usr"));
                    d.setReceiptPdf(rs.getString("receipt_pdf"));
                    d.setReturnPdf(rs.getString("return_pdf"));
                    d.setCompanyId(rs.getString("company_id"));
                    d.setDepartmentId(rs.getString("department_id"));
                    d.setCompanyName(rs.getString("company_name"));
                    d.setDepartmentName(rs.getString("department_name"));
                    d.setTel(rs.getString("tel"));
                    d.setFaultFlag("1".equals(rs.getString("fault_flag")));

                    list.add(d);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    public boolean existsFault(String employeeId, String serialNumber, Date distributionDate) {
        String sql =
            "SELECT COUNT(*) " +
            "FROM fault_management " +
            "WHERE employee_id = ? " +
            "  AND serial_number = ? " +
            "  AND distribution_date = ?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, employeeId);
            ps.setString(2, serialNumber);
            ps.setDate(3, distributionDate);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return false;
    }

    public boolean isReturnedOwner(String serialNumber, String employeeId, Date distributionDate) {
        String sql =
            "SELECT COUNT(*) " +
            "FROM owner_management " +
            "WHERE serial_number = ? " +
            "  AND owner_num = ? " +
            "  AND distribution_date = ? " +
            "  AND return_date IS NOT NULL";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, serialNumber);
            ps.setString(2, employeeId);
            ps.setDate(3, distributionDate);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return false;
    }

    public int insertFaultManagement(Detail_dto terminal, String employeeId, Date distributionDate, String loginUserId) {
        String sql =
            "INSERT INTO fault_management (" +
            " employee_id, asset_number, registration_date, serial_number, inno_hin, " +
            " contract_date, termination_date, tanka, distribution_date, " +
            " reserve1, reserve2, reserve3, reserve4, reserve5, " +
            " reserve6, reserve7, reserve8, reserve9, reserve10, " +
            " insert_date, insert_user, update_date, update_user" +
            ") VALUES (" +
            " ?, ?, CURDATE(), ?, ?, ?, ?, ?, ?, " +
            " NULL, NULL, NULL, NULL, NULL, " +
            " NULL, NULL, NULL, NULL, NULL, " +
            " NOW(), ?, NOW(), ?" +
            ")";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, employeeId);
            ps.setString(2, terminal.getAssetNumber());
            ps.setString(3, terminal.getSerialNumber());
            ps.setString(4, terminal.getInnoHin());
            ps.setDate(5, terminal.getContractDate());
            ps.setDate(6, terminal.getTerminationDate());

            if (terminal.getTanka() != null) {
                ps.setInt(7, terminal.getTanka());
            } else {
                ps.setNull(7, Types.INTEGER);
            }

            ps.setDate(8, distributionDate);
            ps.setString(9, loginUserId);
            ps.setString(10, loginUserId);

            return ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private long getNextOwnerId(Connection con) throws Exception {
        String sql = "SELECT COALESCE(MAX(id), 0) + 1 FROM owner_management";

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getLong(1);
            }
        }

        return 1L;
    }


    public int insertOwner(Detail_dto dto, String loginUserId) {
        String sql =
            "INSERT INTO owner_management (" +
            " id, " +
            " asset_number, " +
            " serial_number, " +
            " company_id, " +
            " department_id, " +
            " owner_num, " +
            " owner_name, " +
            " distribution_date, " +
            " return_date, " +
            " longrange_usr, " +
            " insert_date, " +
            " insert_user, " +
            " update_date, " +
            " update_user" +
            ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), ?, NOW(), ?)";

        long start = System.currentTimeMillis();

        try (Connection con = getConnection()) {

            long nextId = getNextOwnerId(con);

            System.out.println(
                "[SQL][DetailDao.insertOwner] " + sql
            );

            System.out.println(
                "[SQL][DetailDao.insertOwner] params: "
                + "id=" + nextId
                + ", assetNumber=" + dto.getAssetNumber()
                + ", serialNumber=" + dto.getSerialNumber()
                + ", companyId=" + dto.getCompanyId()
                + ", departmentId=" + dto.getDepartmentId()
                + ", ownerNum=" + dto.getOwnerNum()
                + ", ownerName=" + dto.getOwnerName()
                + ", distributionDate=" + dto.getDistributionDate()
                + ", returnDate=" + dto.getReturnDate()
                + ", longRangeUser=" + dto.getLongRangeUser()
                + ", insertUser=" + loginUserId
                + ", updateUser=" + loginUserId
            );

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setLong(1, nextId);
                ps.setString(2, dto.getAssetNumber());
                ps.setString(3, dto.getSerialNumber());
                ps.setString(4, dto.getCompanyId());
                ps.setString(5, dto.getDepartmentId());
                ps.setString(6, dto.getOwnerNum());
                ps.setString(7, dto.getOwnerName());
                ps.setDate(8, dto.getDistributionDate());
                ps.setDate(9, dto.getReturnDate());
                ps.setString(10, dto.getLongRangeUser());
                ps.setString(11, loginUserId);
                ps.setString(12, loginUserId);

                int count = ps.executeUpdate();

                long end = System.currentTimeMillis();

                System.out.println(
                    "[SQL][DetailDao.insertOwner] count="
                    + count
                    + " (query took "
                    + (end - start)
                    + " ms)"
                );

                return count;
            }

        } catch (Exception e) {
            System.out.println(
                "[SQL][DetailDao.insertOwner] ERROR: "
                + e.getMessage()
            );

            throw new RuntimeException(e);
        }
    }


    public int updateTerminal(Detail_dto dto, String loginUserId) {
        String sql =
            "UPDATE terminal_management SET " +
            " inno_hin = ?, " +
            " tel = ?, " +
            " contract_date = ?, " +
            " contract_period = ?, " +
            " termination_date = ?, " +
            " rental_company = ?, " +
            " tanka = ?, " +
            " update_date = NOW(), " +
            " update_user = ? " +
            "WHERE id = ? " +
            "  AND is_deleted = '0'";

System.out.println(
    "[SQL][DetailDao.updateTerminal] " +
    sql
);

System.out.println(
    "[SQL][DetailDao.updateTerminal] " +
    "params: " +
    "id=" + dto.getId() +
    ", innoHin=" + dto.getInnoHin() +
    ", tel=" + dto.getTel() +
    ", contractDate=" + dto.getContractDate() +
    ", contractPeriod=" + dto.getContractPeriod() +
    ", terminationDate=" + dto.getTerminationDate() +
    ", rentalCompany=" + dto.getRentalCompany() +
    ", tanka=" + dto.getTanka() +
    ", loginUserId=" + loginUserId
);

long start = System.currentTimeMillis();


        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, dto.getInnoHin());
            ps.setString(2, dto.getTel());
            ps.setDate(3, dto.getContractDate());
            ps.setDate(4, dto.getContractPeriod());
            ps.setDate(5, dto.getTerminationDate());
            ps.setString(6, dto.getRentalCompany());

            if (dto.getTanka() != null) {
                ps.setInt(7, dto.getTanka());
            } else {
                ps.setNull(7, Types.INTEGER);
            }

            ps.setString(8, loginUserId);
            ps.setInt(9, dto.getId());

            return ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


public int deleteTerminalBySerialNumber(
        String serialNumber,
        String loginUserId) {

    String sql =
        "UPDATE terminal_management SET " +
        " is_deleted = '1', " +
        " update_date = NOW(), " +
        " update_user = ? " +
        "WHERE serial_number = ? " +
        "  AND is_deleted = '0'";

    System.out.println(
        "[SQL][DetailDao.deleteTerminalBySerialNumber] "
        + sql
    );

    System.out.println(
        "[SQL][DetailDao.deleteTerminalBySerialNumber] "
        + "params: loginUserId=" + loginUserId
        + ", serialNumber=" + serialNumber
    );

    long start = System.currentTimeMillis();

    try (
        Connection con = getConnection();
        PreparedStatement ps = con.prepareStatement(sql)
    ) {

        ps.setString(1, loginUserId);
        ps.setString(2, serialNumber);

        int count = ps.executeUpdate();

        long end = System.currentTimeMillis();

        System.out.println(
            "[SQL][DetailDao.deleteTerminalBySerialNumber] "
            + "updateCount=" + count
            + " (update took "
            + (end - start)
            + " ms)"
        );

        return count;

    } catch (Exception e) {

        System.out.println(
            "[SQL][DetailDao.deleteTerminalBySerialNumber] ERROR: "
            + e.getMessage()
        );

        throw new RuntimeException(e);
    }
}


    public List<String[]> getLatestEmployeeList() {
        List<String[]> list = new ArrayList<>();

        String sql =
            "SELECT e1.company_id, e1.department_id, e1.employee_id, e1.employee_name " +
            "FROM employee_management e1 " +
            "WHERE e1.is_deleted = '0' " +
            "  AND e1.start_date = ( " +
            "    SELECT MAX(e2.start_date) " +
            "    FROM employee_management e2 " +
            "    WHERE e2.employee_id = e1.employee_id " +
            "      AND e2.is_deleted = '0' " +
            "  ) " +
            "ORDER BY e1.employee_name";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new String[] {
                    rs.getString("company_id"),
                    rs.getString("department_id"),
                    rs.getString("employee_id"),
                    rs.getString("employee_name")
                });
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    public String[] findLatestEmployeeById(String employeeId) {
        String[] result = null;

        String sql =
            "SELECT e1.company_id, e1.department_id, e1.employee_id, e1.employee_name " +
            "FROM employee_management e1 " +
            "WHERE e1.employee_id = ? " +
            "  AND e1.is_deleted = '0' " +
            "  AND e1.start_date = ( " +
            "    SELECT MAX(e2.start_date) " +
            "    FROM employee_management e2 " +
            "    WHERE e2.employee_id = e1.employee_id " +
            "      AND e2.is_deleted = '0' " +
            "  )";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, employeeId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    result = new String[] {
                        rs.getString("company_id"),
                        rs.getString("department_id"),
                        rs.getString("employee_id"),
                        rs.getString("employee_name")
                    };
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    public Detail_dto findOwnerDocument(String serialNumber, Date distributionDate) {
        Detail_dto dto = null;

        String sql =
            "SELECT serial_number, owner_name, distribution_date, receipt_pdf, return_pdf " +
            "FROM owner_management " +
            "WHERE serial_number = ? AND distribution_date = ?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, serialNumber);
            ps.setDate(2, distributionDate);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    dto = new Detail_dto();
                    dto.setSerialNumber(rs.getString("serial_number"));
                    dto.setOwnerName(rs.getString("owner_name"));
                    dto.setDistributionDate(rs.getDate("distribution_date"));
                    dto.setReceiptPdf(rs.getString("receipt_pdf"));
                    dto.setReturnPdf(rs.getString("return_pdf"));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return dto;
    }

    public int updateReceiptPdf(String serialNumber, Date distributionDate, String fileName, String loginUserId) {
        String sql =
            "UPDATE owner_management SET " +
            " receipt_pdf = ?, " +
            " update_date = NOW(), " +
            " update_user = ? " +
            "WHERE serial_number = ? AND distribution_date = ?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, fileName);
            ps.setString(2, loginUserId);
            ps.setString(3, serialNumber);
            ps.setDate(4, distributionDate);

            return ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public int updateReturnPdf(String serialNumber, Date distributionDate, String fileName, String loginUserId) {
        String sql =
            "UPDATE owner_management SET " +
            " return_pdf = ?, " +
            " update_date = NOW(), " +
            " update_user = ? " +
            "WHERE serial_number = ? AND distribution_date = ?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, fileName);
            ps.setString(2, loginUserId);
            ps.setString(3, serialNumber);
            ps.setDate(4, distributionDate);

            return ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public int clearReceiptPdf(String serialNumber, Date distributionDate, String loginUserId) {
        String sql =
            "UPDATE owner_management SET " +
            " receipt_pdf = NULL, " +
            " update_date = NOW(), " +
            " update_user = ? " +
            "WHERE serial_number = ? AND distribution_date = ?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, loginUserId);
            ps.setString(2, serialNumber);
            ps.setDate(3, distributionDate);

            return ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public int clearReturnPdf(String serialNumber, Date distributionDate, String loginUserId) {
        String sql =
            "UPDATE owner_management SET " +
            " return_pdf = NULL, " +
            " update_date = NOW(), " +
            " update_user = ? " +
            "WHERE serial_number = ? AND distribution_date = ?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, loginUserId);
            ps.setString(2, serialNumber);
            ps.setDate(3, distributionDate);

            return ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    

public int registerJisysInventoryOwner(String serialNumber, Date distributionDate, String loginUserId) {
    String updateTerminalSql =
        "UPDATE terminal_management SET " +
        " isd_inventory = '1', " +
        " update_date = NOW(), " +
        " update_user = ? " +
        "WHERE serial_number = ? " +
        "  AND is_deleted = '0' " +
        "  AND isd_inventory = '0'";

    String insertOwnerSql =
        "INSERT INTO owner_management (" +
        " id, " +
        " asset_number, " +
        " serial_number, " +
        " company_id, " +
        " department_id, " +
        " owner_num, " +
        " owner_name, " +
        " longrange_usr, " +
        " distribution_date, " +
        " return_date, " +
        " receipt_pdf, " +
        " return_pdf, " +
        " is_deleted, " +
        " reserve1, reserve2, reserve3, reserve4, reserve5, " +
        " reserve6, reserve7, reserve8, reserve9, reserve10, " +
        " insert_date, insert_user, update_date, update_user " +
        ") VALUES (" +
        " ?, " +
        " (SELECT tm.asset_number FROM terminal_management tm WHERE tm.serial_number = ? AND tm.is_deleted = '0' LIMIT 1), " +
        " ?, " +
        " '1000', " +
        " '1013', " +
        " '999', " +
        " '在庫', " +
        " NULL, " +
        " ?, " +
        " NULL, " +
        " NULL, " +
        " NULL, " +
        " 0, " +
        " NULL, NULL, NULL, NULL, NULL, " +
        " NULL, NULL, NULL, NULL, NULL, " +
        " NOW(), ?, NOW(), ? " +
        ")";

    long transactionStart = System.currentTimeMillis();

    try (Connection con = getConnection()) {
        con.setAutoCommit(false);

        try {
            /*
             * 1. terminal_management.isd_inventory を 0 から 1 に更新
             */
            System.out.println("[SQL][DetailDao.registerJisysInventoryOwner] " + updateTerminalSql);
            System.out.println(
                "[SQL][DetailDao.registerJisysInventoryOwner] params: "
                + "loginUserId=" + loginUserId
                + ", serialNumber=" + serialNumber
            );

            int updateCount;
            long updateStart = System.currentTimeMillis();

            try (PreparedStatement ps = con.prepareStatement(updateTerminalSql)) {
                ps.setString(1, loginUserId);
                ps.setString(2, serialNumber);

                updateCount = ps.executeUpdate();
            }

            long updateEnd = System.currentTimeMillis();

            System.out.println(
                "[SQL][DetailDao.registerJisysInventoryOwner] count="
                + updateCount
                + " (query took "
                + (updateEnd - updateStart)
                + " ms)"
            );

            if (updateCount == 0) {
                con.rollback();

                System.out.println(
                    "[SQL][DetailDao.registerJisysInventoryOwner] rollback: "
                    + "terminal_management update target not found. "
                    + "serialNumber=" + serialNumber
                );

                return 0;
            }

            /*
             * 2. owner_management に「在庫」行を追加
             */
            long nextId = getNextOwnerId(con);

            System.out.println("[SQL][DetailDao.registerJisysInventoryOwner.insertOwner] " + insertOwnerSql);
            System.out.println(
                "[SQL][DetailDao.registerJisysInventoryOwner.insertOwner] params: "
                + "id=" + nextId
                + ", assetNumber=(select by serialNumber)"
                + ", serialNumber=" + serialNumber
                + ", companyId=1000"
                + ", departmentId=1013"
                + ", ownerNum=999"
                + ", ownerName=在庫"
                + ", distributionDate=" + distributionDate
                + ", returnDate=null"
                + ", insertUser=" + loginUserId
                + ", updateUser=" + loginUserId
            );

            int insertCount;
            long insertStart = System.currentTimeMillis();

            try (PreparedStatement ps = con.prepareStatement(insertOwnerSql)) {
                ps.setLong(1, nextId);
                ps.setString(2, serialNumber);
                ps.setString(3, serialNumber);
                ps.setDate(4, distributionDate);
                ps.setString(5, loginUserId);
                ps.setString(6, loginUserId);

                insertCount = ps.executeUpdate();
            }

            long insertEnd = System.currentTimeMillis();

            System.out.println(
                "[SQL][DetailDao.registerJisysInventoryOwner.insertOwner] count="
                + insertCount
                + " (query took "
                + (insertEnd - insertStart)
                + " ms)"
            );

            con.commit();

            long transactionEnd = System.currentTimeMillis();

            System.out.println(
                "[SQL][DetailDao.registerJisysInventoryOwner] transaction commit "
                + "updateCount=" + updateCount
                + ", insertCount=" + insertCount
                + " (transaction took "
                + (transactionEnd - transactionStart)
                + " ms)"
            );

            return insertCount;

        } catch (Exception e) {
            con.rollback();

            System.out.println(
                "[SQL][DetailDao.registerJisysInventoryOwner] ERROR: "
                + e.getMessage()
            );

            throw e;

        } finally {
            con.setAutoCommit(true);
        }

    } catch (Exception e) {
        throw new RuntimeException(e);
    }
}


public int updateOwnerReturnDate(
        String serialNumber,
        String ownerNum,
        Date distributionDate,
        Date returnDate,
        String loginUserId) {

    String sql =
        "UPDATE owner_management SET " +
        " return_date = ?, " +
        " update_date = NOW(), " +
        " update_user = ? " +
        "WHERE serial_number = ? " +
        "  AND owner_num = ? " +
        "  AND distribution_date = ? " +
        "  AND (is_deleted = '0' OR is_deleted IS NULL)";

    System.out.println("[SQL][DetailDao.updateOwnerReturnDate] " + sql);

    System.out.println(
        "[SQL][DetailDao.updateOwnerReturnDate] params: "
        + "returnDate=" + returnDate
        + ", loginUserId=" + loginUserId
        + ", serialNumber=" + serialNumber
        + ", ownerNum=" + ownerNum
        + ", distributionDate=" + distributionDate
    );

    long start = System.currentTimeMillis();

    try (Connection con = getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setDate(1, returnDate);
        ps.setString(2, loginUserId);
        ps.setString(3, serialNumber);
        ps.setString(4, ownerNum);
        ps.setDate(5, distributionDate);

        int count = ps.executeUpdate();

        long end = System.currentTimeMillis();

        System.out.println(
            "[SQL][DetailDao.updateOwnerReturnDate] count="
            + count
            + " (query took "
            + (end - start)
            + " ms)"
        );

        return count;

    } catch (Exception e) {
        System.out.println(
            "[SQL][DetailDao.updateOwnerReturnDate] ERROR: "
            + e.getMessage()
        );

        throw new RuntimeException(e);
    }
}


public int updateTerminalFaultReturn(String serialNumber, String loginUserId) {
    String sql =
        "UPDATE terminal_management SET " +
        " is_fault_return = '1', " +
        " update_date = NOW(), " +
        " update_user = ? " +
        "WHERE serial_number = ? " +
        "  AND is_deleted = '0'";

    System.out.println("[SQL][DetailDao.updateTerminalFaultReturn] " + sql);
    System.out.println(
        "[SQL][DetailDao.updateTerminalFaultReturn] params: "
        + "loginUserId=" + loginUserId
        + ", serialNumber=" + serialNumber
    );

    long start = System.currentTimeMillis();

    try (Connection con = getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setString(1, loginUserId);
        ps.setString(2, serialNumber);

        int count = ps.executeUpdate();

        long end = System.currentTimeMillis();

        System.out.println(
            "[SQL][DetailDao.updateTerminalFaultReturn] count="
            + count
            + " (query took "
            + (end - start)
            + " ms)"
        );

        return count;

    } catch (Exception e) {
        System.out.println(
            "[SQL][DetailDao.updateTerminalFaultReturn] ERROR: "
            + e.getMessage()
        );

        throw new RuntimeException(e);
    }
}

public int updateTerminalInventoryOff(String serialNumber, String loginUserId) {
    String sql =
        "UPDATE terminal_management SET " +
        " isd_inventory = '0', " +
        " update_date = NOW(), " +
        " update_user = ? " +
        "WHERE serial_number = ? " +
        "  AND is_deleted = '0' " +
        "  AND isd_inventory = '1'";

    System.out.println("[SQL][DetailDao.updateTerminalInventoryOff] " + sql);

    System.out.println(
        "[SQL][DetailDao.updateTerminalInventoryOff] params: "
        + "loginUserId=" + loginUserId
        + ", serialNumber=" + serialNumber
    );

    long start = System.currentTimeMillis();

    try (Connection con = getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setString(1, loginUserId);
        ps.setString(2, serialNumber);

        int count = ps.executeUpdate();

        long end = System.currentTimeMillis();

        System.out.println(
            "[SQL][DetailDao.updateTerminalInventoryOff] count="
            + count
            + " (query took "
            + (end - start)
            + " ms)"
        );

        return count;

    } catch (Exception e) {
        System.out.println(
            "[SQL][DetailDao.updateTerminalInventoryOff] ERROR: "
            + e.getMessage()
        );

        throw new RuntimeException(e);
    }
}




}
