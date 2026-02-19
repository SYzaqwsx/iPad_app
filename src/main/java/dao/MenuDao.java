package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import dto.Menu_dto;

public class MenuDao extends BaseDao {

    // 実行するSQL
    private static final String SQL =
        "SELECT role_id, menu_id, menu_name, menu_url " +
        "FROM m_menu WHERE role_id = ? ORDER BY menu_id";

    public List<Menu_dto> findByRoleId(String roleId) {
        long start = System.currentTimeMillis(); // 全体時間の計測開始

        // --- ログ（SQL とパラメータ） ---
        System.out.println("[SQL][MenuDao.findByRoleId] " + SQL);
        System.out.println("[SQL][MenuDao.findByRoleId] params: roleId=" + roleId);

        List<Menu_dto> list = new ArrayList<>();

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(SQL)) {

            ps.setString(1, roleId);

            long execStart = System.currentTimeMillis(); // 実行時間の計測開始
            try (ResultSet rs = ps.executeQuery()) {
                long execTook = System.currentTimeMillis() - execStart;

                int count = 0;
                while (rs.next()) {
                    Menu_dto m = new Menu_dto();
                    m.setRoleId(rs.getString("role_id"));
                    m.setMenuId(rs.getString("menu_id"));
                    m.setMenuName(rs.getString("menu_name"));
                    m.setMenuUrl(rs.getString("menu_url"));
                    list.add(m);
                    count++;

                    // 先頭数件だけ中身の一部を覗く（出し過ぎ防止）
                    if (count <= 3) {
                        System.out.println("[SQL][MenuDao.findByRoleId] row" + count +
                            " {role_id=" + m.getRoleId() +
                            ", menu_id=" + m.getMenuId() +
                            ", menu_name=" + m.getMenuName() +
                            ", menu_url=" + m.getMenuUrl() + "}");
                    }
                }

                // --- 実行結果のサマリ ---
                System.out.println("[SQL][MenuDao.findByRoleId] rows=" + count +
                                   " (query took " + execTook + " ms)");
            }
        } catch (Exception e) {
            System.out.println("[SQL][MenuDao.findByRoleId] ERROR: " +
                               e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace(System.out); // スタックトレースも出力
            throw new RuntimeException("MenuDao.findByRoleId error", e);
        } finally {
            long took = System.currentTimeMillis() - start;
            System.out.println("[SQL][MenuDao.findByRoleId] total took " + took + " ms");
        }

        return list;
    }
}
