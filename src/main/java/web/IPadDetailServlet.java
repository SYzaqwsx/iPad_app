
package web;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import dao.DetailDao;
import dto.Detail_dto;

@WebServlet("/IPadDetailServlet")
public class IPadDetailServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String JSP_PATH = "/WEB-INF/jsp/iPad_detail.jsp";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String idStr = req.getParameter("id");
        String tab = req.getParameter("tab");

        if (tab == null || tab.isEmpty()) {
            tab = "terminal";
        }

        if (idStr == null || idStr.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/TerminalServlet");
            return;
        }

        int id = Integer.parseInt(idStr);

        DetailDao dao = new DetailDao();
        Detail_dto detail = dao.findTerminalById(id);

        if (detail == null) {
            resp.sendRedirect(req.getContextPath() + "/TerminalServlet");
            return;
        }

        detail.setCompanyOptions(dao.getCompanyList());
        detail.setDepartmentOptions(dao.getDepartmentList());
        detail.setEmployeeOptions(dao.getLatestEmployeeList());

        List<Detail_dto> ownerHistory = dao.findOwnerHistoryBySerialNumber(detail.getSerialNumber());

        req.setAttribute("detail", detail);
        req.setAttribute("ownerHistory", ownerHistory);
        req.setAttribute("tab", tab);

        req.getRequestDispatcher(JSP_PATH).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding(StandardCharsets.UTF_8.name());

        String action = req.getParameter("action");
        String tab = req.getParameter("tab");
        String idStr = req.getParameter("id");

        if (tab == null || tab.isEmpty()) {
            tab = "terminal";
        }

        if (idStr == null || idStr.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/TerminalServlet");
            return;
        }

        int id = Integer.parseInt(idStr);

        HttpSession session = req.getSession(false);
        String loginUserId = null;
        if (session != null) {
            Object userIdObj = session.getAttribute("authUserId");
            if (userIdObj != null) {
                loginUserId = userIdObj.toString();
            }
        }

        if (loginUserId == null || loginUserId.isEmpty()) {
            req.setAttribute("errors", java.util.Arrays.asList("ログインユーザー情報が取得できませんでした。"));
            doGet(req, resp);
            return;
        }

        DetailDao dao = new DetailDao();
        List<String> errors = new ArrayList<>();

        try {
            if ("updateTerminal".equals(action)) {
                Detail_dto dto = new Detail_dto();
                dto.setId(id);
                dto.setAssetNumber(trim(req.getParameter("assetNumber")));
                dto.setSerialNumber(trim(req.getParameter("serialNumber")));
                dto.setInnoHin(trim(req.getParameter("innoHin")));
                dto.setTel(trim(req.getParameter("tel")));
                dto.setContractDate(parseDate(req.getParameter("contractDate")));
                dto.setContractPeriod(parseDate(req.getParameter("contractPeriod")));
                dto.setRentalCompany(trim(req.getParameter("rentalCompany")));
                dto.setUdid(trim(req.getParameter("udid")));
                dto.setMacAddress(trim(req.getParameter("macAddress")));

                String tankaStr = trim(req.getParameter("tanka"));
                if (tankaStr != null && !tankaStr.isEmpty()) {
                    dto.setTanka(Integer.parseInt(tankaStr.replace(",", "")));
                }

                dao.updateTerminal(dto, loginUserId);
                req.setAttribute("message", "端末情報を更新しました。");
                tab = "terminal";
            }

            if ("deleteTerminal".equals(action)) {
                dao.deleteTerminal(id, loginUserId);
                resp.sendRedirect(req.getContextPath() + "/TerminalServlet");
                return;
            }

            if ("addOwner".equals(action)) {
                Detail_dto terminal = dao.findTerminalById(id);

                Detail_dto dto = new Detail_dto();
                dto.setSerialNumber(terminal.getSerialNumber());
                dto.setCompanyId(trim(req.getParameter("companyId")));
                dto.setDepartmentId(trim(req.getParameter("departmentId")));
                dto.setEmployeeId(trim(req.getParameter("employeeId")));
                dto.setDistributionDate(parseDate(req.getParameter("distributionDate")));
                dto.setLongRangeUser(trim(req.getParameter("longRangeUser")));
                dto.setReturnDate(parseDate(req.getParameter("returnDate")));

                if (dto.getDistributionDate() == null) {
                    errors.add("配付日は必須です。");
                }

                if (dto.getEmployeeId() == null || dto.getEmployeeId().isEmpty()) {
                    errors.add("使用者は必須です。");
                }

                String[] emp = null;
                if (errors.isEmpty()) {
                    emp = dao.findLatestEmployeeById(dto.getEmployeeId());
                    if (emp == null) {
                        errors.add("使用者情報が取得できませんでした。");
                    } else {
                        // emp[0] = company_id
                        // emp[1] = department_id
                        // emp[2] = employee_id
                        // emp[3] = employee_name
                        dto.setOwnerNum(emp[2]);
                        dto.setOwnerName(emp[3]);

                        // 会社・部署を employee_management の最新値で上書きしたい場合は以下を有効化
                        // dto.setCompanyId(emp[0]);
                        // dto.setDepartmentId(emp[1]);
                    }
                }

                if (!errors.isEmpty()) {
                    req.setAttribute("errors", errors);
                } else {
                    dao.insertOwner(dto, loginUserId);
                    req.setAttribute("message", "利用者情報を追加しました。");
                }

                tab = "ownerAdd";
            }

        } catch (Exception e) {
            e.printStackTrace();
            errors.add("処理中にエラーが発生しました。");
            req.setAttribute("errors", errors);
        }

        req.setAttribute("tab", tab);
        req.setAttribute("id", id);
        doGet(req, resp);
    }

    private String trim(String s) {
        return s == null ? null : s.trim();
    }

    private Date parseDate(String s) {
        if (s == null || s.trim().isEmpty()) {
            return null;
        }
        return Date.valueOf(s);
    }
}
