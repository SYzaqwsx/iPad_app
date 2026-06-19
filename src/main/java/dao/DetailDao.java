
package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
                    dto.setTanka(rs.getInt("tanka"));
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

        String sql = "SELECT company_id, company_name FROM company_management ORDER BY company_name";

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

        String sql = "SELECT department_id, department_name FROM department_management ORDER BY department_name";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new String[] {
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
            "       t.tel, t.is_fault_return " +
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
                    d.setFaultFlag("1".equals(rs.getString("is_fault_return")));
                    list.add(d);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    public int insertOwner(Detail_dto dto, String loginUserId) {
        String sql =
            "INSERT INTO owner_management (" +
            " serial_number, company_id, department_id, owner_num, owner_name, " +
            " distribution_date, return_date, longrange_usr, " +
            " insert_date, insert_user, update_date, update_user" +
            ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW(), ?, NOW(), ?)";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, dto.getSerialNumber());
            ps.setString(2, dto.getCompanyId());
            ps.setString(3, dto.getDepartmentId());
            ps.setString(4, dto.getOwnerNum());
            ps.setString(5, dto.getOwnerName());
            ps.setDate(6, dto.getDistributionDate());
            ps.setDate(7, dto.getReturnDate());
            ps.setString(8, dto.getLongRangeUser());
            ps.setString(9, loginUserId);
            ps.setString(10, loginUserId);

            return ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public int updateTerminal(Detail_dto dto, String loginUserId) {
        String sql =
            "UPDATE terminal_management SET " +
            " asset_number = ?, " +
            " serial_number = ?, " +
            " inno_hin = ?, " +
            " tel = ?, " +
            " contract_date = ?, " +
            " contract_period = ?, " +
            " rental_company = ?, " +
            " tanka = ?, " +
            " udid = ?, " +
            " mac_address = ?, " +
            " update_date = NOW(), " +
            " update_user = ? " +
            "WHERE id = ?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, dto.getAssetNumber());
            ps.setString(2, dto.getSerialNumber());
            ps.setString(3, dto.getInnoHin());
            ps.setString(4, dto.getTel());
            ps.setDate(5, dto.getContractDate());
            ps.setDate(6, dto.getContractPeriod());
            ps.setString(7, dto.getRentalCompany());
            ps.setInt(8, dto.getTanka() == null ? 0 : dto.getTanka());
            ps.setString(9, dto.getUdid());
            ps.setString(10, dto.getMacAddress());
            ps.setString(11, loginUserId);
            ps.setInt(12, dto.getId());

            return ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public int deleteTerminal(int id, String loginUserId) {
        String sql =
            "UPDATE terminal_management SET " +
            " is_deleted = '1', " +
            " update_date = NOW(), " +
            " update_user = ? " +
            "WHERE id = ?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, loginUserId);
            ps.setInt(2, id);

            return ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<String[]> getLatestEmployeeList() {
        List<String[]> list = new ArrayList<>();

        String sql =
            "SELECT e1.employee_id, e1.employee_name " +
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
}
