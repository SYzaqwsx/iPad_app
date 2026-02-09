import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet implementation class Login_Servlet
 */


@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class Login_Servlet extends HttpServlet {

    private DataSource dataSource;

    @Override
    public void init() throws ServletException {
        try {
            InitialContext init = new InitialContext();
            Context env = (Context) init.lookup("java:/comp/env");
            this.dataSource = (DataSource) env.lookup("jdbc/ipadDS"); // JNDI 名を context.xml と一致させる
        } catch (Exception e) {
            throw new ServletException("DataSource取得に失敗しました", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        String userId  = trim(req.getParameter("username")); // 画面の「アカウント」＝ user_id
        String rawPass = trim(req.getParameter("password"));

        if (userId.isEmpty() || rawPass.isEmpty()) {
            backWithError(req, resp);
            return;
        }

        final String sql =
            "SELECT user_id, user_name, password_hash, role_id " +
            "  FROM ipad.m_user " +
            " WHERE user_id = ? " +
            "   AND (is_active = 1 OR is_active = TRUE)";

        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password_hash");

                    // いまはプレーン照合（DBの値がそのままの前提）
                    boolean ok = rawPass.equals(storedHash);

                    // BCrypt運用へ移行するならこちらに差し替え：
                    // boolean ok = org.springframework.security.crypto.bcrypt.BCrypt.checkpw(rawPass, storedHash);

                    if (ok) {
                        HttpSession session = req.getSession(true);
                        session.setAttribute("loginUserId", rs.getString("user_id"));
                        session.setAttribute("loginUserName", rs.getString("user_name"));
                        session.setAttribute("loginRoleId", rs.getString("role_id"));
                        resp.sendRedirect(req.getContextPath() + "/login_success.jsp");
                        return;
                    }
                }
            }
        } catch (SQLException e) {
            throw new ServletException("ログイン時にDBエラーが発生しました", e);
        }

        // NG → ポップアップ表示のためフラグ渡し
        backWithError(req, resp);
    }

    private String trim(String s) { return s == null ? "" : s.trim(); }

    private void backWithError(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("loginError", Boolean.TRUE);
        req.getRequestDispatcher("/login.jsp").forward(req, resp);
    }
}
