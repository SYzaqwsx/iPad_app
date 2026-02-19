package web;


import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import dao.MenuDao;
import dto.Menu_dto;

/**
 * Servlet implementation class Menu_Servlet
 */

@WebServlet("/Menu_Servlet")
public class Menu_Servlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        long start = System.currentTimeMillis();
        System.out.println("[Menu_Servlet] doGet start: " + req.getRequestURI());

HttpSession session = req.getSession(false);
System.out.println("[Menu_Servlet] session=" + (session != null ? session.getId() : "null"));
if (session != null) {
    System.out.println("[Menu_Servlet] attrs: authUserId=" + session.getAttribute("authUserId")
        + ", authRoleId=" + session.getAttribute("authRoleId")
        + ", authUserName=" + session.getAttribute("authUserName"));
}


        String roleId = (String) session.getAttribute("authRoleId");
        String userName = (String) session.getAttribute("authUserName");

        if (roleId == null || roleId.isEmpty()) {
            // 役割が無い場合はメニュー作れないのでログインへ戻す（運用に応じてエラーページでも可）
            System.out.println("[Menu_Servlet] roleId が未設定のためログインへ戻します");
            resp.sendRedirect(req.getContextPath() + "/Login_iPad.jsp");
            return;
        }

        System.out.println("[Menu_Servlet] userName=" + userName + ", roleId=" + roleId);

        try {
            // --- メニュー取得 ---
            MenuDao dao = new MenuDao();
            List<Menu_dto> menus = dao.findByRoleId(roleId);
            int size = (menus == null ? 0 : menus.size());
            System.out.println("[Menu_Servlet] menus.size=" + size);

            // --- 画面に受け渡し ---
            req.setAttribute("menus", menus);
            req.setAttribute("userName", userName);
            req.setAttribute("roleId", roleId);

            // --- JSP へフォワード（※ WEB-INF 配下なので直接URLから開けません）---
            String view = "/WEB-INF/jsp/iPad_menu.jsp";
            req.getRequestDispatcher(view).forward(req, resp);
            System.out.println("[Menu_Servlet] forwarded to " + view);

        } catch (Exception e) {
            // 想定外エラーはログ出しつつ再スロー（サーブレットコンテナのエラーページへ）
            System.out.println("[Menu_Servlet] 例外発生: " + e.getClass().getName() + " - " + e.getMessage());
            e.printStackTrace(System.out);
            throw new ServletException("Menu_Servlet error", e);
        } finally {
            long took = System.currentTimeMillis() - start;
            System.out.println("[Menu_Servlet] doGet end (took " + took + " ms)");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // ログイン成功リダイレクト後などで POST されるケースがあれば GET に委譲
        doGet(req, resp);
    }
}
