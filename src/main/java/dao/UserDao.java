package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import dto.User_dto;

public class UserDao extends BaseDao {

    public static class User {
        public String userId;
        public String userName;
        public String roleId;
        public String passwordHash;
        public boolean active;
    }
/**
     * ユーザーIDで1件取得（存在しなければ null）
*/

    public User_dto findByUserId(String userId) {
        final String sql =
            "SELECT user_id, user_name, password_hash, role_id, is_active " +
            "FROM m_user WHERE user_id = ?";

        System.out.println("[SQL][UserDao.findByUserId] " + sql);
        System.out.println("[SQL][UserDao.findByUserId] params: userId=" + userId);

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User_dto u = new User_dto();
                    // ★ DTO の型に合わせる（userId:int, roleId:String, isActive:int）
                    u.setUserId(rs.getInt("user_id"));                 // int
                    u.setUserName(rs.getString("user_name"));          // String
                    u.setPasswordHash(rs.getString("password_hash"));  // String（DTOに setter あり）
                    u.setRoleId(rs.getString("role_id"));              // String
                    u.setIsActive(rs.getInt("is_active"));             // int（0/1 など）
                    return u;
                }
            }
        } catch (Exception e) {
            System.out.println("[SQL][UserDao.findByUserId] ERROR: "
                    + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace(System.out);
            throw new RuntimeException("UserDao.findByUserId error", e);
        }
        return null;
    }
}

