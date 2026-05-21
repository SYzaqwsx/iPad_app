
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import dto.Terminal_dto;

public class TerminalDao extends BaseDao {

    // 一覧取得

public List<Terminal_dto> findAll(int offset, int limit,
        String company,
        String[] innoArr,
        String[] ownerArr,
        boolean terminated,
        boolean inventory,
        boolean soon) {

    List<Terminal_dto> list = new ArrayList<>();

    StringBuilder sql = new StringBuilder();

    sql.append("SELECT ");
    sql.append("t.id, t.asset_number, t.serial_number, t.inno_hin, ");
    sql.append("t.contract_date, t.termination_date, t.tanka, ");
    sql.append("o.company_id, o.department_id, o.owner_name, o.distribution_date, ");
    sql.append("cm.company_name, dm.department_name ");

    sql.append("FROM terminal_management t ");
    sql.append("INNER JOIN owner_management o ON t.serial_number = o.serial_number ");
    sql.append("LEFT JOIN company_management cm ON o.company_id = cm.company_id ");
    sql.append("LEFT JOIN department_management dm ON o.department_id = dm.department_id ");

        sql.append("AND t.termination_date IS NULL ");
        sql.append("AND o.distribution_date = ( ");
        sql.append(" SELECT MAX(om.distribution_date) ");
        sql.append(" FROM owner_management om ");
        sql.append(" WHERE om.serial_number = o.serial_number) ");


        // ===== 条件 =====
        if (!terminated) {
            sql.append("AND t.termination_date IS NULL ");
        }

        if (company != null && !company.isEmpty()) {
            sql.append("AND o.company_id = ? ");
        }

        if (innoArr != null && innoArr.length > 0) {
            sql.append("AND t.inno_hin IN (");
            for (int i = 0; i < innoArr.length; i++) {
                sql.append("?");
                if (i < innoArr.length - 1) sql.append(",");
            }
            sql.append(") ");
        }

        if (ownerArr != null && ownerArr.length > 0) {
            sql.append("AND o.owner_name IN (");
            for (int i = 0; i < ownerArr.length; i++) {
                sql.append("?");
                if (i < ownerArr.length - 1) sql.append(",");
            }
            sql.append(") ");
        }

        if (inventory) {
            sql.append("AND t.isd_inventory = 1 ");
        }

        if (soon) {
            sql.append("AND t.contract_period <= 90 ");
        }

        sql.append("ORDER BY t.id ");
        sql.append("LIMIT ? OFFSET ?");

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            int idx = 1;

            if (company != null && !company.isEmpty()) {
                ps.setString(idx++, company);
            }

            if (innoArr != null) {
                for (String v : innoArr) {
                    ps.setString(idx++, v);
                }
            }

            if (ownerArr != null) {
                for (String v : ownerArr) {
                    ps.setString(idx++, v);
                }
            }

            ps.setInt(idx++, limit);
            ps.setInt(idx, offset);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Terminal_dto d = new Terminal_dto();

                d.setId(rs.getInt("id"));
                d.setAssetNumber(rs.getString("asset_number"));
                d.setInnoHin(rs.getString("inno_hin"));
                d.setSerialNumber(rs.getString("serial_number"));
                d.setCompanyName(rs.getString("company_name"));
                d.setDepartmentName(rs.getString("department_name"));
                d.setOwnerName(rs.getString("owner_name"));
                d.setTanka(rs.getInt("tanka"));

                list.add(d);
            }

            long start = System.currentTimeMillis();
            long took = System.currentTimeMillis() - start;
            System.out.println("[TerminalDao] rows=" + list.size() + " / took " + took + " ms");
            System.out.println("=================================");

        } catch (Exception e) {
            System.out.println("[TerminalDao] 例外: " + e.getMessage());
            e.printStackTrace(System.out);
            throw new RuntimeException(e);
        }

        return list;
    }

    // 件数取得
    public int countAll(String company) {

        int count = 0;

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(*) ");
        sql.append("FROM terminal_management t ");
        sql.append("INNER JOIN owner_management o ");
        sql.append("ON t.serial_number = o.serial_number ");

        sql.append("LEFT JOIN company_management cm ");
        sql.append("ON o.company_id = cm.company_id ");

        sql.append("LEFT JOIN department_management dm ");
        sql.append("ON o.department_id = dm.department_id ");

        sql.append("WHERE t.is_deleted = '0' ");
        sql.append("AND t.termination_date IS NULL ");
        
        sql.append("AND o.distribution_date = ( ");
        sql.append(" SELECT MAX(om.distribution_date) ");
        sql.append(" FROM owner_management om ");
        sql.append(" WHERE om.serial_number = o.serial_number) ");

        if (company != null && !company.isEmpty()) {
            sql.append("AND cm.company_name = ? ");
        }

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            // ===== SQLログ =====
            System.out.println("=================================");
            System.out.println("[TerminalDao] countAll SQL");
            System.out.println(sql.toString());
            System.out.println("[PARAM] company=" + company);
            // ====================

            int idx = 1;

            if (company != null && !company.isEmpty()) {
                ps.setString(idx++, company);
            }

            long start = System.currentTimeMillis();

            ResultSet rs = ps.executeQuery();

            if (rs.next()) count = rs.getInt(1);

            long took = System.currentTimeMillis() - start;
            System.out.println("[TerminalDao] count=" + count + " / took " + took + " ms");
            System.out.println("=================================");

        } catch (Exception e) {
            System.out.println("[TerminalDao] 例外: " + e.getMessage());
            e.printStackTrace(System.out);
            throw new RuntimeException(e);
        }

        return count;
    }

    // 会社プルダウン
    public List<String> getCompanyList() {

        List<String> list = new ArrayList<>();

        String sql = "SELECT company_name FROM company_management ORDER BY company_name";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // ===== SQLログ =====
            System.out.println("=================================");
            System.out.println("[TerminalDao] getCompanyList SQL");
            System.out.println(sql);
            // ====================

            long start = System.currentTimeMillis();

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(rs.getString("company_name"));
            }

            long took = System.currentTimeMillis() - start;
            System.out.println("[TerminalDao] rows=" + list.size() + " / took " + took + " ms");
            System.out.println("=================================");

        } catch (Exception e) {
            System.out.println("[TerminalDao] 例外: " + e.getMessage());
            e.printStackTrace(System.out);
            throw new RuntimeException(e);
        }

        return list;
    }
    

 // イノテックス品番リスト
public List<String> getInnoHinList() {
    List<String> list = new ArrayList<>();
    String sql = "SELECT DISTINCT inno_hin FROM terminal_management ORDER BY inno_hin";

    try (Connection con = getConnection();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            list.add(rs.getString("inno_hin"));
        }
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
     return list;
 }
 

//利用者名リスト
public List<String> getOwnerNameList() {
    List<String> list = new ArrayList<>();
    String sql = "SELECT DISTINCT owner_name FROM owner_management ORDER BY owner_name";

    try (Connection con = getConnection();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            list.add(rs.getString("owner_name"));
        }
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
    return list;
}



}
