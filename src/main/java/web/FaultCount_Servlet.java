
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

import dao.FaultCountDao;
import dto.Fault_count_row_dto;

@WebServlet("/FaultCountServlet")
public class FaultCount_Servlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String JSP_PATH = "/WEB-INF/jsp/fault_count.jsp";

    // 一覧画面と同じ件数に合わせたい場合はここを既存値に合わせて変更
    private static final int PAGE_SIZE = 10;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        FaultCountDao dao = new FaultCountDao();

        int year = parseInt(req.getParameter("year"), LocalDate.now().getYear());
        int page = parseInt(req.getParameter("page"), 1);

        String companyId = trim(req.getParameter("companyId"));
        String departmentId = trim(req.getParameter("departmentId"));

        List<Fault_count_row_dto> allRows = dao.findFaultCountRows(year, companyId, departmentId);

        // 会社合計 / 部署合計
        Map<String, Integer> companyTotals = new LinkedHashMap<>();
        Map<String, Integer> departmentTotals = new LinkedHashMap<>();

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

        int totalCount = allRows.size();
        int totalPages = totalCount == 0 ? 1 : (int) Math.ceil((double) totalCount / PAGE_SIZE);

        if (page < 1) {
            page = 1;
        }
        if (page > totalPages) {
            page = totalPages;
        }

        int fromIndex = (page - 1) * PAGE_SIZE;
        int toIndex = Math.min(fromIndex + PAGE_SIZE, totalCount);

        List<Fault_count_row_dto> pageRows = new ArrayList<>();
        if (fromIndex < toIndex) {
            pageRows = allRows.subList(fromIndex, toIndex);
        }

        req.setAttribute("year", year);
        req.setAttribute("page", page);
        req.setAttribute("pageSize", PAGE_SIZE);
        req.setAttribute("totalCount", totalCount);
        req.setAttribute("totalPages", totalPages);

        req.setAttribute("companyId", companyId);
        req.setAttribute("departmentId", departmentId);

        req.setAttribute("rows", pageRows);
        req.setAttribute("companyOptions", dao.getCompanyList());
        req.setAttribute("departmentOptions", dao.getDepartmentList());

        req.getRequestDispatcher(JSP_PATH).forward(req, resp);
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
