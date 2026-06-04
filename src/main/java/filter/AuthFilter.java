
package filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import dao.MenuDao;

@WebFilter("/*")
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();

        // =========================
        // 除外（ログイン・静的ファイル）
        // =========================
        if (uri.equals(contextPath + "/login")
                || uri.endsWith("Login_iPad.jsp")
                || uri.contains("login_success.jsp")
                || uri.startsWith(contextPath + "/static/")
                || uri.contains(".css")
                || uri.contains(".js")
                || uri.contains(".png")
                || uri.contains(".jpg")) {

            chain.doFilter(request, response);
            return;
        }

        // =========================
        // セッションチェック
        // =========================
        HttpSession session = req.getSession(false);

        if (session == null) {
            resp.sendRedirect(contextPath + "/Login_iPad.jsp");
            return;
        }

        String roleId = (String) session.getAttribute("authRoleId");

        if (roleId == null) {
            resp.sendRedirect(contextPath + "/Login_iPad.jsp");
            return;
        }

        // =========================
        // パス取得（contextPath除去）
        // =========================
        String path = uri.substring(contextPath.length());

        // ※クエリパラメータ除去（?以降）
        if (path.contains("?")) {
            path = path.substring(0, path.indexOf("?"));
        }

        // =========================
        // 全員許可URL（メニュー画面など）
        // =========================
        if (path.equals("/Menu_Servlet")) {
            chain.doFilter(request, response);
            return;
        }

        if (path.equals("/Terminal_Excel_Servlet")) {
            chain.doFilter(request, response);
            return;
        }
        if (path.equals("/Terminal_Ibs_Excel_Servlet")) {
            chain.doFilter(request, response);
            return;
        }



        // =========================
        // デバッグログ
        // =========================
        System.out.println("[AuthFilter] URI=" + uri);
        System.out.println("[AuthFilter] PATH=" + path);
        System.out.println("[AuthFilter] ROLE=" + roleId);

        // =========================
        // DB権限チェック
        // =========================
        MenuDao dao = new MenuDao();
        boolean allowed = dao.existsByRoleAndUrl(roleId, path);

        if (!allowed) {
            System.out.println("[AuthFilter] ACCESS DENIED");

            // ★ forbiddenページへ
            resp.sendRedirect(contextPath + "/error/forbidden.jsp");
            return;
        }

        // OK
        chain.doFilter(request, response);
    }
}
