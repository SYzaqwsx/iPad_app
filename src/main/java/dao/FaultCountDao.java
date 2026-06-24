
package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import dto.Fault_count_row_dto;

public class FaultCountDao extends BaseDao {

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

    /**
     * 指定年(1/1～12/31)の故障件数を、
     * 故障登録日時点で有効な employee_management の所属で集計する
     */
    public List<Fault_count_row_dto> findFaultCountRows(int year, String companyId, String departmentId) {
        List<Fault_count_row_dto> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append(
            "SELECT " +
            "    em.company_id, " +
            "    cm.company_name, " +
            "    em.department_id, " +
            "    dm.department_name, " +
            "    fm.employee_id, " +
            "    em.employee_name, " +
            "    COUNT(*) AS fault_count " +
            "FROM fault_management fm " +
            "INNER JOIN employee_management em " +
            "    ON em.employee_id = fm.employee_id " +
            "   AND em.is_deleted = '0' " +
            "   AND em.start_date = ( " +
            "       SELECT MAX(e2.start_date) " +
            "       FROM employee_management e2 " +
            "       WHERE e2.employee_id = fm.employee_id " +
            "         AND e2.is_deleted = '0' " +
            "         AND e2.start_date <= fm.registration_date " +
            "   ) " +
            "LEFT JOIN company_management cm " +
            "    ON em.company_id = cm.company_id " +
            "LEFT JOIN department_management dm " +
            "    ON em.company_id = dm.company_id " +
            "   AND em.department_id = dm.department_id " +
            "WHERE fm.registration_date >= ? " +
            "  AND fm.registration_date < ? "
        );

        if (companyId != null && !companyId.isEmpty()) {
            sql.append(" AND em.company_id = ? ");
        }
        if (departmentId != null && !departmentId.isEmpty()) {
            sql.append(" AND em.department_id = ? ");
        }

        sql.append(
            "GROUP BY " +
            "    em.company_id, cm.company_name, " +
            "    em.department_id, dm.department_name, " +
            "    fm.employee_id, em.employee_name " +
            "ORDER BY " +
            "    cm.company_name ASC, " +
            "    dm.department_name ASC, " +
            "    em.employee_name ASC "
        );

        Date from = Date.valueOf(year + "-01-01");
        Date to = Date.valueOf((year + 1) + "-01-01");

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            int idx = 1;
            ps.setDate(idx++, from);
            ps.setDate(idx++, to);

            if (companyId != null && !companyId.isEmpty()) {
                ps.setString(idx++, companyId);
            }
            if (departmentId != null && !departmentId.isEmpty()) {
                ps.setString(idx++, departmentId);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                	Fault_count_row_dto dto = new Fault_count_row_dto();
                    dto.setCompanyId(rs.getString("company_id"));
                    dto.setCompanyName(rs.getString("company_name"));
                    dto.setDepartmentId(rs.getString("department_id"));
                    dto.setDepartmentName(rs.getString("department_name"));
                    dto.setEmployeeId(rs.getString("employee_id"));
                    dto.setEmployeeName(rs.getString("employee_name"));
                    dto.setFaultCount(rs.getInt("fault_count"));
                    list.add(dto);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return list;
    }
}
