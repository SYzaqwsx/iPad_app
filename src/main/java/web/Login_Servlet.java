package web;
import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import dao.UserDao;
import dto.User_dto;

/**
 * Servlet implementation class Login_Servlet
 */

@WebServlet("/login")
public class Login_Servlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        final String username = trimToNull(req.getParameter("username"));
        final String password = req.getParameter("password"); // 空文字は不可とする

        // 入力バリデーション
        boolean ok = username != null && password != null && !password.isEmpty();
        if (!ok) {
            setAuthErrorAndBack(req, resp);
            return;
        }

        // ユーザー取得
        UserDao userDao = new UserDao();
        User_dto u = userDao.findByUserId(username);

        // 存在しない or 無効
        if (u == null || u.getIsActive() == 0) {
            setAuthErrorAndBack(req, resp);
            return;
        }

        // パスワード照合（BCrypt対応 / 非BCryptは安全な固定時間比較）
        String stored = u.getPasswordHash();
        boolean passwordOK = verifyPassword(password, stored);
        if (!passwordOK) {
            setAuthErrorAndBack(req, resp);
            return;
        }

        // ---- ここから成功処理 ----
        // セッション固定化対策：古いセッションは破棄し、新しいセッションを払い出す
        HttpSession old = req.getSession(false);
        if (old != null) {
            try { old.invalidate(); } catch (IllegalStateException ignore) {}
        }
        HttpSession session = req.getSession(true);
        session.setMaxInactiveInterval(30 * 60); // 30分（必要に応じて変更）

        // セッションへ必要情報を格納
        session.setAttribute("authUserId", String.valueOf(u.getUserId()));
        session.setAttribute("authRoleId", u.getRoleId());
        session.setAttribute("authUserName", u.getUserName());

        System.out.println("[Login_Servlet] OK user=" + u.getUserId() + ", JSESSIONID=" + session.getId());

        // メニューへ遷移（リダイレクト）
        resp.sendRedirect(resp.encodeRedirectURL(req.getContextPath() + "/Menu_Servlet"));
    }

    // ================= ヘルパー =================

    private static void setAuthErrorAndBack(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("loginError", true);
        req.getRequestDispatcher("/Login_iPad.jsp").forward(req, resp);
    }

    private static String trimToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    /**
     * パスワード検証。
     *  - 保存値が $2a$/$2b$/$2y$ で始まる場合は BCrypt でチェック
     *  - それ以外は、固定時間比較でプレーン一致
     */
    private static boolean verifyPassword(String rawPassword, String storedValue) {
        if (storedValue == null) return false;

        String sv = storedValue.trim();
        boolean looksBCrypt = sv.startsWith("$2a$") || sv.startsWith("$2b$") || sv.startsWith("$2y$");

        if (looksBCrypt) {
            // org.mindrot.jbcrypt.BCrypt がクラスパスにある場合のみ使用
            try {
                Class<?> bcryptClass = Class.forName("org.mindrot.jbcrypt.BCrypt");
                // BCrypt.checkpw(raw, hash) をリフレクションで呼び出す（依存が無い環境でもコンパイル可能にするため）
                java.lang.reflect.Method checkpw =
                        bcryptClass.getMethod("checkpw", String.class, String.class);
                Object result = checkpw.invoke(null, rawPassword, sv);
                return (result instanceof Boolean) && ((Boolean) result);
            } catch (ClassNotFoundException e) {
                System.err.println("[Login_Servlet] WARN: BCrypt が利用できません。BCrypt形式のハッシュですが検証不可のためNG扱いにします。");
                return false;
            } catch (Exception e) {
                System.err.println("[Login_Servlet] ERROR: BCrypt検証中に例外: " + e.getMessage());
                return false;
            }
        }

        // 非BCrypt（プレーンテキスト想定）の場合は固定時間比較
        return constantTimeEquals(rawPassword, sv);
    }

    /**
     * 固定時間（タイミング攻撃耐性）での比較。
     */
    private static boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null) return false;
        byte[] aa = a.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        byte[] bb = b.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        if (aa.length != bb.length) {
            // 長さ差分も走査して時間を近づける
            int result = aa.length ^ bb.length;
            int max = Math.max(aa.length, bb.length);
            for (int i = 0; i < max; i++) {
                byte xa = i < aa.length ? aa[i] : 0;
                byte xb = i < bb.length ? bb[i] : 0;
                result |= (xa ^ xb);
            }
            return result == 0;
        }
        int result = 0;
        for (int i = 0; i < aa.length; i++) {
            result |= aa[i] ^ bb[i];
        }
        return result == 0;
    }
}

