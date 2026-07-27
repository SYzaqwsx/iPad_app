
package web;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import dao.FaultDetailDao;
import dto.Fault_detail_dto;


@WebServlet("/FaultDetail_Servlet")
public class FaultDetail_Servlet extends HttpServlet {


    private static final long serialVersionUID = 1L;

    private static final String JSP_PATH = "/WEB-INF/jsp/fault_detail.jsp";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        long start = System.currentTimeMillis();
        System.out.println("[FaultDetailServlet] doGet start: " + req.getRequestURI());

        HttpSession session = req.getSession(false);
        System.out.println("[FaultDetailServlet] session="
                + (session != null ? session.getId() : "null"));

        if (session != null) {
            System.out.println("[FaultDetailServlet] attrs: authUserId="
                    + session.getAttribute("authUserId")
                    + ", authRoleId=" + session.getAttribute("authRoleId")
                    + ", authUserName=" + session.getAttribute("authUserName"));
        }

        try {
            int year = parseInt(req.getParameter("year"), LocalDate.now().getYear());

            String companyId = trim(req.getParameter("companyId"));
            String departmentId = trim(req.getParameter("departmentId"));
            String employeeId = trim(req.getParameter("employeeId"));

            System.out.println("[FaultDetailServlet] params:"
                    + " year=" + year
                    + ", companyId=" + companyId
                    + ", departmentId=" + departmentId
                    + ", employeeId=" + employeeId);

            if (employeeId == null || employeeId.isEmpty()) {
                System.out.println("[FaultDetailServlet] employeeIdが未指定です");
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "employeeId is required");
                return;
            }

            FaultDetailDao dao = new FaultDetailDao();
            List<Fault_detail_dto> list =
                    dao.findFaultDetails(year, companyId, departmentId, employeeId);

            String companyName = "";
            String departmentName = "";
            String employeeName = "";

            if (list != null && !list.isEmpty()) {
            	Fault_detail_dto first = list.get(0);
                companyName = first.getCompanyName();
                departmentName = first.getDepartmentName();
                employeeName = first.getEmployeeName();
            }

            req.setAttribute("year", year);
            req.setAttribute("companyId", companyId);
            req.setAttribute("departmentId", departmentId);
            req.setAttribute("employeeId", employeeId);

            req.setAttribute("companyName", companyName);
            req.setAttribute("departmentName", departmentName);
            req.setAttribute("employeeName", employeeName);

            req.setAttribute("list", list);

            System.out.println("[FaultDetailServlet] list.size="
                    + (list == null ? 0 : list.size()));

            System.out.println("[FaultDetailServlet] forwarding to " + JSP_PATH);
            req.getRequestDispatcher(JSP_PATH).forward(req, resp);
            System.out.println("[FaultDetailServlet] forwarded to " + JSP_PATH);

        } catch (Exception e) {
            System.out.println("[FaultDetailServlet] 例外発生: "
                    + e.getClass().getName() + " - " + e.getMessage());
            e.printStackTrace(System.out);
            throw new ServletException("FaultDetailServlet error", e);
        } finally {
            long took = System.currentTimeMillis() - start;
            System.out.println("[FaultDetailServlet] doGet end (took " + took + " ms)");
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
}
