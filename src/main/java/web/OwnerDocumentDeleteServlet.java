
package web;

import java.io.IOException;
import java.sql.Date;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import dao.DetailDao;

@WebServlet("/OwnerDocumentDeleteServlet")
public class OwnerDocumentDeleteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        String id = req.getParameter("id");
        String tab = req.getParameter("tab");
        String kind = req.getParameter("kind"); // receipt / return
        String serialNumber = req.getParameter("serialNumber");
        String distributionDateStr = req.getParameter("distributionDate");

        if (tab == null || tab.isEmpty()) {
            tab = "ownerHistory";
        }

        if (id == null || id.isEmpty()
                || kind == null || kind.isEmpty()
                || serialNumber == null || serialNumber.isEmpty()
                || distributionDateStr == null || distributionDateStr.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/IPadDetailServlet?id=" + id + "&tab=" + tab);
            return;
        }

        Date distributionDate;
        try {
            distributionDate = Date.valueOf(distributionDateStr);
        } catch (IllegalArgumentException e) {
            resp.sendRedirect(req.getContextPath() + "/IPadDetailServlet?id=" + id + "&tab=" + tab);
            return;
        }

        HttpSession session = req.getSession(false);
        String loginUserId = null;
        if (session != null && session.getAttribute("authUserId") != null) {
            loginUserId = session.getAttribute("authUserId").toString();
        }

        DetailDao dao = new DetailDao();

        try {
            if ("receipt".equals(kind)) {
                dao.clearReceiptPdf(serialNumber, distributionDate, loginUserId);
            } else if ("return".equals(kind)) {
                dao.clearReturnPdf(serialNumber, distributionDate, loginUserId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        resp.sendRedirect(req.getContextPath() + "/IPadDetailServlet?id=" + id + "&tab=" + tab);
    }
}
