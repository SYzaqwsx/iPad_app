
package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import dto.Terminal_dto;

public class TerminalDao extends BaseDao {


    // =============================
    // 一覧取得...検索条件で表示するデータを見つける
    // =============================
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
        sql.append("t.contract_date, t.contract_period, t.termination_date, t.tanka, ");
        sql.append("o.owner_num, o.owner_name, o.distribution_date, ");
        sql.append("cm.company_name, dm.department_name ");
        sql.append("FROM terminal_management t ");

        // ✅ 最新ownerのみJOIN（これが超重要）
        sql.append("LEFT JOIN ( ");
        sql.append("  SELECT o1.* ");
        sql.append("  FROM owner_management o1 ");
        sql.append("  WHERE o1.distribution_date = ( ");
        sql.append("    SELECT MAX(o2.distribution_date) ");
        sql.append("    FROM owner_management o2 ");
        sql.append("    WHERE o2.serial_number = o1.serial_number ");
        sql.append("  ) ");
        sql.append(") o ON t.serial_number = o.serial_number ");
        sql.append("LEFT JOIN company_management cm ON o.company_id = cm.company_id ");
        sql.append("LEFT JOIN department_management dm ON o.department_id = dm.department_id ");
        sql.append("WHERE t.is_deleted = '0' ");

        if (!terminated) {
            sql.append("AND t.termination_date IS NULL ");
        }
        if (company != null && !company.isEmpty()) {
            sql.append("AND cm.company_name = ? ");
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
            sql.append("AND o.owner_num = '999' ");
        }
        if (soon) {
            sql.append("AND t.contract_period <= ? ");
        }
        sql.append("ORDER BY t.asset_number ");
        sql.append("LIMIT ? OFFSET ?");

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            // ✅ SQLログ
            System.out.println("[SQL][TerminalDao.findAll]");
            System.out.println(sql.toString());

            int idx = 1;

            if (company != null && !company.isEmpty()) {
                ps.setString(idx++, company);
            }
            if (innoArr != null && innoArr.length > 0) {
                for (String v : innoArr) {
                    ps.setString(idx++, v);
                }
            }
            if (ownerArr != null && ownerArr.length > 0) {
                for (String v : ownerArr) {
                    ps.setString(idx++, v);
                }
            }
            if (soon) {
                LocalDate ninetyDaysLater = LocalDate.now().plusDays(90);
                ps.setDate(idx++, Date.valueOf(ninetyDaysLater));
            }

            ps.setInt(idx++, limit);
            ps.setInt(idx, offset);

            // ✅ 実行
            long start = System.currentTimeMillis();
            ResultSet rs = ps.executeQuery();
            long took = System.currentTimeMillis() - start;
            int row = 0;
            while (rs.next()) {
                row++;
                System.out.println("[SQL] row" + row +
                        " {asset=" + rs.getString("asset_number") +
                        ", termination=" + rs.getDate("termination_date") +
                        ", owner=" + rs.getString("owner_name") + "}");
                Terminal_dto d = new Terminal_dto();
                d.setId(rs.getInt("id"));
                d.setAssetNumber(rs.getString("asset_number"));
                d.setInnoHin(rs.getString("inno_hin"));
                d.setSerialNumber(rs.getString("serial_number"));
                d.setContractPeriod(rs.getDate("contract_period"));
                d.setContractDate(rs.getDate("contract_date"));
                d.setTerminationDate(rs.getDate("termination_date"));
                d.setDistributionDate(rs.getDate("distribution_date"));
                d.setCompanyName(rs.getString("company_name"));
                d.setDepartmentName(rs.getString("department_name"));
                d.setOwnerName(rs.getString("owner_name"));
                d.setTanka(rs.getInt("tanka"));
                list.add(d);
            }
            System.out.println("[SQL] rows=" + row + " (" + took + " ms)");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    
    // =============================
    // 件数取得...ページ数を決める
    // =============================
    public int countAll(String company,
            String[] innoArr,
            String[] ownerArr,
            boolean terminated,
            boolean inventory,
            boolean soon) 
    {
        int count = 0;
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(*) ");
        sql.append("FROM terminal_management t ");
        sql.append("LEFT JOIN ( ");
        sql.append("  SELECT o1.* ");
        sql.append("  FROM owner_management o1 ");
        sql.append("  WHERE o1.distribution_date = ( ");
        sql.append("    SELECT MAX(o2.distribution_date) ");
        sql.append("    FROM owner_management o2 ");
        sql.append("    WHERE o2.serial_number = o1.serial_number ");
        sql.append("  ) ");
        sql.append(") o ON t.serial_number = o.serial_number ");
        sql.append("LEFT JOIN company_management cm ON o.company_id = cm.company_id ");
        sql.append("WHERE t.is_deleted = '0' ");
        // ✅ 解約
        if (!terminated) {
            sql.append("AND t.termination_date IS NULL ");
        }
        // ✅ 会社
        if (company != null && !company.isEmpty()) {
            sql.append("AND cm.company_name = ? ");
        }
        // ✅ 品番
        if (innoArr != null && innoArr.length > 0) {
            sql.append("AND t.inno_hin IN (");
            for (int i = 0; i < innoArr.length; i++) {
                sql.append("?");
                if (i < innoArr.length - 1) sql.append(",");
            }
            sql.append(") ");
        }
        // ✅ 利用者
        if (ownerArr != null && ownerArr.length > 0) {
            sql.append("AND o.owner_name IN (");
            for (int i = 0; i < ownerArr.length; i++) {
                sql.append("?");
                if (i < ownerArr.length - 1) sql.append(",");
            }
            sql.append(") ");
        }
        // ✅ 在庫
        if (inventory) {
            sql.append("AND o.owner_num = '999' ");
        }

        // ✅ 定期入替
        if (soon) {
            sql.append("AND t.contract_period <= ? ");
        }
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            int idx = 1;
            if (company != null && !company.isEmpty()) {
                ps.setString(idx++, company);
            }
            if (innoArr != null && innoArr.length > 0) {
                for (String v : innoArr) {
                    ps.setString(idx++, v);
                }
            }
            if (ownerArr != null && ownerArr.length > 0) {
                for (String v : ownerArr) {
                    ps.setString(idx++, v);
                }
            }
            if (soon) {
                LocalDate ninetyDaysLater = LocalDate.now().plusDays(90);
                ps.setDate(idx++, Date.valueOf(ninetyDaysLater));
            }
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return count;
    }


    // =============================
    // 会社
    // =============================
    public List<String> getCompanyList() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT company_name FROM company_management ORDER BY company_name";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(rs.getString("company_name"));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    
    // =============================
    // 品番
    // =============================
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

    
    // =============================
    // 利用者
    // =============================
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
