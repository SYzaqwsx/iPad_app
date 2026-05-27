
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

        long start = System.currentTimeMillis();
        System.out.println("=================================");
        System.out.println("[TerminalServlet] START");

        try {
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
            String[] innoArr = req.getParameterValues("innoHin");
            String[] ownerArr = req.getParameterValues("ownerName");
            boolean terminated = req.getParameter("includeTerminated") != null;
            boolean inventory = req.getParameter("stockOnly") != null;
            boolean soon = req.getParameter("replacementTarget") != null;

            // ✅ デバッグログ
            System.out.println("[PARAM]");
            System.out.println("company=" + company);

            if (innoArr != null) {
                System.out.print("innoHin=");
                for (String s : innoArr) System.out.print(s + " ");
                System.out.println();
            }
            if (ownerArr != null) {
                System.out.print("ownerName=");
                for (String s : ownerArr) System.out.print(s + " ");
                System.out.println();
            }
            System.out.println("includeTerminated=" + req.getParameter("includeTerminated"));
            System.out.println("terminated(boolean)=" + terminated);
            System.out.println("inventory=" + inventory);
            System.out.println("replacementTarget=" + soon);

            TerminalDao dao = new TerminalDao();

            // ✅ 件数（完全一致）
            int totalCount = dao.countAll(
                    company,
                    innoArr,
                    ownerArr,
                    terminated,
                    inventory,
                    soon
            );

            int totalPage = (int) Math.ceil((double) totalCount / pageSize);

            if (totalPage == 0) totalPage = 1;
            if (page > totalPage) page = totalPage;

            int offset = (page - 1) * pageSize;

            // ✅ 一覧取得
            List<Terminal_dto> list =
                    dao.findAll(
                            offset,
                            pageSize,
                            company,
                            innoArr,
                            ownerArr,
                            terminated,
                            inventory,
                            soon
                    );

            // ✅ プルダウン
            req.setAttribute("companyList", dao.getCompanyList());
            req.setAttribute("innoList", dao.getInnoHinList());
            req.setAttribute("ownerList", dao.getOwnerNameList());

            // ✅ JSP
            req.setAttribute("list", list);
            req.setAttribute("page", page);
            req.setAttribute("totalPage", totalPage);
            req.getRequestDispatcher("/WEB-INF/jsp/terminal.jsp")
                    .forward(req, resp);

        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        } finally {
            long took = System.currentTimeMillis() - start;
            System.out.println("[TerminalServlet] END (" + took + " ms)");
            System.out.println("=================================");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doGet(req, resp);
    }
}
