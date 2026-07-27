
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import dto.Fault_detail_dto;

public class FaultDetailDao extends BaseDao {

    private static final String BASE_SQL =
        "SELECT " +
        "    cm.company_name, " +
        "    dm.department_name, " +
        "    em.employee_name, " +
        "    fm.asset_number, " +
        "    fm.registration_date, " +
        "    fm.serial_number, " +
        "    fm.inno_hin, " +
        "    fm.contract_date, " +
        "    fm.termination_date, " +
        "    fm.tanka, " +
        "    fm.distribution_date " +
        "FROM fault_management fm " +
        "INNER JOIN employee_management em " +
        "    ON fm.employee_id = em.employee_id " +
        "   AND fm.registration_date >= em.start_date " +
        "   AND (em.end_date IS NULL OR fm.registration_date <= em.end_date) " +
        "INNER JOIN department_management dm " +
        "    ON em.company_id = dm.company_id " +
        "   AND em.department_id = dm.department_id " +
        "INNER JOIN company_management cm " +
        "    ON em.company_id = cm.company_id " +
        "WHERE YEAR(fm.registration_date) = ? " +
        "  AND fm.employee_id = ? ";

    public List<Fault_detail_dto> findFaultDetails(
            int year,
            String companyId,
            String departmentId,
            String employeeId) {

        long start = System.currentTimeMillis();

        List<Fault_detail_dto> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder(BASE_SQL);

        if (companyId != null && !companyId.isEmpty()) {
            sql.append(" AND em.company_id = ? ");
        }

        if (departmentId != null && !departmentId.isEmpty()) {
            sql.append(" AND em.department_id = ? ");
        }

        sql.append(" ORDER BY fm.registration_date DESC, fm.asset_number ASC ");

        System.out.println("[SQL][FaultDetailDao.findFaultDetails] " + sql);
        System.out.println("[SQL][FaultDetailDao.findFaultDetails] params:"
                + " year=" + year
                + ", employeeId=" + employeeId
                + ", companyId=" + companyId
                + ", departmentId=" + departmentId);

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            int idx = 1;

            ps.setInt(idx++, year);
            ps.setString(idx++, employeeId);

            if (companyId != null && !companyId.isEmpty()) {
                ps.setString(idx++, companyId);
            }

            if (departmentId != null && !departmentId.isEmpty()) {
                ps.setString(idx++, departmentId);
            }

            long execStart = System.currentTimeMillis();

            try (ResultSet rs = ps.executeQuery()) {
                long execTook = System.currentTimeMillis() - execStart;

                int count = 0;

                while (rs.next()) {
                	Fault_detail_dto dto = new Fault_detail_dto();

                    dto.setCompanyName(rs.getString("company_name"));
                    dto.setDepartmentName(rs.getString("department_name"));
                    dto.setEmployeeName(rs.getString("employee_name"));

                    dto.setAssetNumber(rs.getString("asset_number"));
                    dto.setRegistrationDate(rs.getDate("registration_date"));
                    dto.setSerialNumber(rs.getString("serial_number"));
                    dto.setInnoHin(rs.getString("inno_hin"));
                    dto.setContractDate(rs.getDate("contract_date"));
                    dto.setTerminationDate(rs.getDate("termination_date"));

                    int tanka = rs.getInt("tanka");
                    if (rs.wasNull()) {
                        dto.setTanka(null);
                    } else {
                        dto.setTanka(tanka);
                    }

                    dto.setDistributionDate(rs.getDate("distribution_date"));

                    list.add(dto);
                    count++;

                    if (count <= 3) {
                        System.out.println("[SQL][FaultDetailDao.findFaultDetails] row" + count
                                + " {company_name=" + dto.getCompanyName()
                                + ", department_name=" + dto.getDepartmentName()
                                + ", employee_name=" + dto.getEmployeeName()
                                + ", asset_number=" + dto.getAssetNumber()
                                + ", serial_number=" + dto.getSerialNumber()
                                + ", inno_hin=" + dto.getInnoHin()
                                + "}");
                    }
                }

                System.out.println("[SQL][FaultDetailDao.findFaultDetails] rows=" + count
                        + " (query took " + execTook + " ms)");
            }

        } catch (Exception e) {
            System.out.println("[SQL][FaultDetailDao.findFaultDetails] ERROR: "
                    + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace(System.out);
            throw new RuntimeException("FaultDetailDao.findFaultDetails error", e);

        } finally {
            long took = System.currentTimeMillis() - start;
            System.out.println("[SQL][FaultDetailDao.findFaultDetails] total took " + took + " ms");
        }

        return list;
    }
}
