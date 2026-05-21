
package web;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import dao.TerminalDao;
import dto.Terminal_dto;

@WebServlet("/TerminalServlet")
public class TerminalServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
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

            String[] innoArr = req.getParameterValues("innoHin");
            String[] ownerArr = req.getParameterValues("ownerName");

            boolean terminated = req.getParameter("includeTerminated") != null;
            boolean inventory = req.getParameter("stockOnly") != null;
            boolean soon = req.getParameter("replacementTarget") != null;

            TerminalDao dao = new TerminalDao();

            int totalCount = dao.countAll(company);
            int totalPage = (int) Math.ceil((double) totalCount / pageSize);

            if (totalPage == 0) totalPage = 1;
            if (page > totalPage) page = totalPage;

            int offset = (page - 1) * pageSize;

            // ===== 一覧取得（配列で渡す） =====
            List<Terminal_dto> list =
                    dao.findAll(offset, pageSize,
                            company, innoArr, ownerArr,
                            terminated, inventory, soon);

            // ===== プルダウン =====
            req.setAttribute("companyList", dao.getCompanyList());
            req.setAttribute("innoList", dao.getInnoHinList());
            req.setAttribute("ownerList", dao.getOwnerNameList());

            // ===== JSP =====
            req.setAttribute("list", list);
            req.setAttribute("page", page);
            req.setAttribute("totalPage", totalPage);

            req.getRequestDispatcher("/WEB-INF/jsp/terminal.jsp")
               .forward(req, resp);

        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doGet(req, resp);
    }
}
