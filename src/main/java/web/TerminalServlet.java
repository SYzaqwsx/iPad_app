package web;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import dao.TerminalDao;
import dto.Terminal_dto;

@WebServlet("/TerminalServlet")
public class TerminalServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req,
                         HttpServletResponse resp)
            throws ServletException, IOException {

        long start = System.currentTimeMillis();

        System.out.println("=================================");
        System.out.println("[TerminalServlet] START");

        try {

            HttpSession session =
                    req.getSession(false);
            Object roleObj = session.getAttribute( "authRoleId");
            Integer roleId = null;
            if (roleObj != null) {
                roleId = Integer.parseInt( roleObj.toString());
            }
            boolean ibsOnly =
                    roleId != null
                    && roleId >= 80
                    && roleId <= 89;
            boolean adminView =
                    roleId != null
                    && roleId == 99;
            System.out.println(
                    "[TerminalServlet] roleId="
                            + roleId
                            + ", ibsOnly="
                            + ibsOnly);

            // ===== ページング =====
            int page = 1;
            int pageSize = 10;

            String p = req.getParameter("page");
            if (p != null && !p.isEmpty()) {

                try {
                    page = Integer.parseInt(p);
                } catch (Exception e) {
                    page = 1;
                }
            }

            // ===== 検索条件 =====

            String company = req.getParameter("company");
            String[] innoArr = req.getParameterValues( "innoHin");
            String[] ownerArr = req.getParameterValues( "ownerName");
            boolean terminated = req.getParameter( "includeTerminated") != null;
            boolean inventory = req.getParameter( "stockOnly") != null;
            boolean soon = req.getParameter( "replacementTarget") != null;
            System.out.println("[PARAM]");
            System.out.println( "company=" + company);
            TerminalDao dao = new TerminalDao();
            int totalCount =
                    dao.countAll(
                            company,
                            innoArr,
                            ownerArr,
                            terminated,
                            inventory,
                            soon,
                            ibsOnly,
                            adminView);
            int totalPage = (int) Math.ceil( (double) totalCount / pageSize);
            if (totalPage == 0) {
                totalPage = 1;
            }
            if (page > totalPage) {
                page = totalPage;
            }
            int offset = (page - 1) * pageSize;
            List<Terminal_dto> list =
                    dao.findAll(
                            offset,
                            pageSize,
                            company,
                            innoArr,
                            ownerArr,
                            terminated,
                            inventory,
                            soon,
                            ibsOnly
                            ,adminView);

            req.setAttribute( "companyList", dao.getCompanyList());
            req.setAttribute( "innoList", dao.getInnoHinList());
            req.setAttribute( "ownerList", dao.getOwnerNameList());
            req.setAttribute( "list", list);
            req.setAttribute( "page", page);
            req.setAttribute( "totalPage", totalPage);
            req.setAttribute( "roleId", roleId);
            req.setAttribute( "ibsOnly", ibsOnly);
            req.getRequestDispatcher( "/WEB-INF/jsp/terminal.jsp") .forward(req, resp);
            
        } catch (Exception e) {

            e.printStackTrace();
            throw new ServletException(e);

        } finally {

            long took = System.currentTimeMillis() - start;

            System.out.println(
                    "[TerminalServlet] END ("
                            + took
                            + " ms)");
            System.out.println( "=================================");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req,
                          HttpServletResponse resp)
            throws ServletException, IOException {

        doGet(req, resp);
    }
}