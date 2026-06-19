
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import dto.Menu_dto;

public class MenuDao extends BaseDao {

    // メニュー表示用SQL（sys=0 のみ表示）
    private static final String SQL_FIND_BY_ROLE_ID =
        "SELECT role_id, menu_id, menu_name, menu_url " +
        "FROM m_menu " +
        "WHERE role_id = ? " +
        "  AND `sys` = 0 " +
        "ORDER BY menu_id";

    // 認可チェック用SQL（sysは見ない。0でも1でも許可対象）
    private static final String SQL_EXISTS_BY_ROLE_AND_URL =
        "SELECT COUNT(*) " +
        "FROM m_menu " +
        "WHERE role_id = ? " +
        "  AND ? LIKE CONCAT(menu_url, '%')";

    /**
     * メニュー画面表示用
     * sys = 0 のものだけ返す
     */
    public List<Menu_dto> findByRoleId(String roleId) {
        long start = System.currentTimeMillis();

        System.out.println("[SQL][MenuDao.findByRoleId] " + SQL_FIND_BY_ROLE_ID);
        System.out.println("[SQL][MenuDao.findByRoleId] params: roleId=" + roleId);

        List<Menu_dto> list = new ArrayList<>();

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_FIND_BY_ROLE_ID)) {

            ps.setString(1, roleId);

            long execStart = System.currentTimeMillis();
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

                    if (count <= 3) {
                        System.out.println("[SQL][MenuDao.findByRoleId] row" + count +
                            " {role_id=" + m.getRoleId() +
                            ", menu_id=" + m.getMenuId() +
                            ", menu_name=" + m.getMenuName() +
                            ", menu_url=" + m.getMenuUrl() + "}");
                    }
                }

                System.out.println("[SQL][MenuDao.findByRoleId] rows=" + count +
                                   " (query took " + execTook + " ms)");
            }

        } catch (Exception e) {
            System.out.println("[SQL][MenuDao.findByRoleId] ERROR: " +
                               e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace(System.out);
            throw new RuntimeException("MenuDao.findByRoleId error", e);

        } finally {
            long took = System.currentTimeMillis() - start;
            System.out.println("[SQL][MenuDao.findByRoleId] total took " + took + " ms");
        }

        return list;
    }

    /**
     * 認可チェック用
     * sys は見ない（0でも1でも許可対象）
     */
    public boolean existsByRoleAndUrl(String roleId, String url) {
        System.out.println("[SQL][MenuDao.existsByRoleAndUrl] " + SQL_EXISTS_BY_ROLE_AND_URL);
        System.out.println("[SQL][MenuDao.existsByRoleAndUrl] params: roleId=" + roleId + ", url=" + url);

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_EXISTS_BY_ROLE_AND_URL)) {

            ps.setString(1, roleId);
            ps.setString(2, url);

            long execStart = System.currentTimeMillis();

            try (ResultSet rs = ps.executeQuery()) {
                long execTook = System.currentTimeMillis() - execStart;

                if (rs.next()) {
                    int count = rs.getInt(1);

                    System.out.println("[SQL][MenuDao.existsByRoleAndUrl] count=" + count +
                                       " (query took " + execTook + " ms)");

                    return count > 0;
                }
            }

        } catch (Exception e) {
            System.out.println("[SQL][MenuDao.existsByRoleAndUrl] ERROR: " +
                    e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace(System.out);
            throw new RuntimeException("MenuDao.existsByRoleAndUrl error", e);
        }

        return false;
    }
}
