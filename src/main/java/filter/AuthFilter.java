
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

@WebFilter("/*") // ★ 全リクエスト対象
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();

        // ===== 除外URL（ログイン・静的リソース）=====
        if (uri.equals(contextPath + "/login")
                || uri.endsWith("Login_iPad.jsp")
                || uri.startsWith(contextPath + "/static/")) {
            chain.doFilter(request, response);
            return;
        }

        // ===== セッションチェック =====
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


        String auth = (String) session.getAttribute("auth");

        // ===== CSVアップロードは管理者のみ =====
        if (uri.equals(contextPath + "/upload-csv")) {
            if (!"99".equals(auth)) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
        }

        // OKならServletへ
        chain.doFilter(request, response);
    }
}
