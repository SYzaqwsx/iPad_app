
package web;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import dao.FaultCountDao;
import dto.Fault_count_row_dto;

@WebServlet("/FaultCount_Servlet")
public class FaultCount_Servlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String JSP_PATH = "/WEB-INF/jsp/fault_count.jsp";

    // 一覧画面と同じ件数に合わせたい場合はここを既存値に合わせて変更
    private static final int PAGE_SIZE = 10;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        long start = System.currentTimeMillis();
        System.out.println("[FaultCount_Servlet] doGet start: " + req.getRequestURI());

        HttpSession session = req.getSession(false);
        System.out.println("[FaultCount_Servlet] session=" + (session != null ? session.getId() : "null"));
        if (session != null) {
            System.out.println("[FaultCount_Servlet] attrs: authUserId=" + session.getAttribute("authUserId")
                    + ", authRoleId=" + session.getAttribute("authRoleId")
                    + ", authUserName=" + session.getAttribute("authUserName"));
        }

        try {
            int year = parseInt(req.getParameter("year"), LocalDate.now().getYear());
            int page = parseInt(req.getParameter("page"), 1);

            String companyId = trim(req.getParameter("companyId"));
            String departmentId = trim(req.getParameter("departmentId"));

            System.out.println("[FaultCount_Servlet] params:"
                    + " year=" + year
                    + ", page=" + page
                    + ", companyId=" + companyId
                    + ", departmentId=" + departmentId);

            FaultCountDao dao = new FaultCountDao();
            System.out.println("[FaultCount_Servlet] DAO created");

            List<Fault_count_row_dto> allRows = dao.findFaultCountRows(year, companyId, departmentId);
            int allSize = (allRows == null ? 0 : allRows.size());
            System.out.println("[FaultCount_Servlet] allRows.size=" + allSize);

            // 会社合計 / 部署合計
            Map<String, Integer> companyTotals = new LinkedHashMap<>();
            Map<String, Integer> departmentTotals = new LinkedHashMap<>();

            if (allRows != null) {
                for (Fault_count_row_dto row : allRows) {
                    String companyKey = nvl(row.getCompanyId());
                    String departmentKey = nvl(row.getCompanyId()) + "|" + nvl(row.getDepartmentId());

                    companyTotals.put(companyKey,
                            companyTotals.getOrDefault(companyKey, 0) + row.getFaultCount());

                    departmentTotals.put(departmentKey,
                            departmentTotals.getOrDefault(departmentKey, 0) + row.getFaultCount());
                }

                for (Fault_count_row_dto row : allRows) {
                    String companyKey = nvl(row.getCompanyId());
                    String departmentKey = nvl(row.getCompanyId()) + "|" + nvl(row.getDepartmentId());

                    row.setCompanyTotal(companyTotals.getOrDefault(companyKey, 0));
                    row.setDepartmentTotal(departmentTotals.getOrDefault(departmentKey, 0));
                }
            }

            int totalCount = (allRows == null ? 0 : allRows.size());
            int totalPages = totalCount == 0 ? 1 : (int) Math.ceil((double) totalCount / PAGE_SIZE);

            if (page < 1) {
                page = 1;
            }
            if (page > totalPages) {
                page = totalPages;
            }

            int fromIndex = (page - 1) * PAGE_SIZE;
            int toIndex = Math.min(fromIndex + PAGE_SIZE, totalCount);

            System.out.println("[FaultCount_Servlet] paging:"
                    + " totalCount=" + totalCount
                    + ", totalPages=" + totalPages
                    + ", page=" + page
                    + ", fromIndex=" + fromIndex
                    + ", toIndex=" + toIndex);

            List<Fault_count_row_dto> pageRows = new ArrayList<>();
            if (allRows != null && fromIndex < toIndex) {
                pageRows = allRows.subList(fromIndex, toIndex);
            }

            int pageSizeActual = (pageRows == null ? 0 : pageRows.size());
            System.out.println("[FaultCount_Servlet] pageRows.size=" + pageSizeActual);

            List<?> companyOptions = dao.getCompanyList();
            List<?> departmentOptions = dao.getDepartmentList();

            System.out.println("[FaultCount_Servlet] companyOptions.size="
                    + (companyOptions == null ? 0 : companyOptions.size()));
            System.out.println("[FaultCount_Servlet] departmentOptions.size="
                    + (departmentOptions == null ? 0 : departmentOptions.size()));

            req.setAttribute("year", year);
            req.setAttribute("page", page);
            req.setAttribute("pageSize", PAGE_SIZE);
            req.setAttribute("totalCount", totalCount);
            req.setAttribute("totalPages", totalPages);

            req.setAttribute("companyId", companyId);
            req.setAttribute("departmentId", departmentId);

            req.setAttribute("rows", pageRows);
            req.setAttribute("companyOptions", companyOptions);
            req.setAttribute("departmentOptions", departmentOptions);

            System.out.println("[FaultCount_Servlet] forwarding to " + JSP_PATH);
            req.getRequestDispatcher(JSP_PATH).forward(req, resp);
            System.out.println("[FaultCount_Servlet] forwarded to " + JSP_PATH);

        } catch (Exception e) {
            System.out.println("[FaultCount_Servlet] 例外発生: "
                    + e.getClass().getName() + " - " + e.getMessage());
            e.printStackTrace(System.out);
            throw new ServletException("FaultCount_Servlet error", e);
        } finally {
            long took = System.currentTimeMillis() - start;
            System.out.println("[FaultCount_Servlet] doGet end (took " + took + " ms)");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doGet(req, resp);
    }

    private String trim(String s) {
        return s == null ? null : s.trim();
    }

    private int parseInt(String s, int defaultValue) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private String nvl(String s) {
        return s == null ? "" : s;
    }
}
