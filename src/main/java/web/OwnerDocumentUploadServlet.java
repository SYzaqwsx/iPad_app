
package web;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Date;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import dao.DetailDao;

@WebServlet("/OwnerDocumentUploadServlet")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,
    maxFileSize       = 1024 * 1024 * 10,
    maxRequestSize    = 1024 * 1024 * 20
)
public class OwnerDocumentUploadServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // ★本番環境に合わせて変更
    private static final String RECEIPT_DIR = "C:/ipad-docs/receipt";
    private static final String RETURN_DIR  = "C:/ipad-docs/return";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        String id = req.getParameter("id");
        String tab = req.getParameter("tab");
        String serialNumber = req.getParameter("serialNumber");
        String distributionDateStr = req.getParameter("distributionDate");

        if (tab == null || tab.isEmpty()) {
            tab = "ownerHistory";
        }

        if (id == null || id.isEmpty() || serialNumber == null || serialNumber.isEmpty()
                || distributionDateStr == null || distributionDateStr.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/IPadDetailServlet?id=" + id + "&tab=" + tab);
            return;
        }

        Date distributionDate = Date.valueOf(distributionDateStr);

        HttpSession session = req.getSession(false);
        String loginUserId = null;
        if (session != null && session.getAttribute("authUserId") != null) {
            loginUserId = session.getAttribute("authUserId").toString();
        }

        DetailDao dao = new DetailDao();

        try {
            Part receiptFile = req.getPart("receiptFile");
            if (receiptFile != null && receiptFile.getSize() > 0) {
                String fileName = new File(receiptFile.getSubmittedFileName()).getName();
                File dir = new File(RECEIPT_DIR);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File target = new File(dir, fileName);
                Files.copy(receiptFile.getInputStream(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
                dao.updateReceiptPdf(serialNumber, distributionDate, fileName, loginUserId);
            }

            Part returnFile = req.getPart("returnFile");
            if (returnFile != null && returnFile.getSize() > 0) {
                String fileName = new File(returnFile.getSubmittedFileName()).getName();
                File dir = new File(RETURN_DIR);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File target = new File(dir, fileName);
                Files.copy(returnFile.getInputStream(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
                dao.updateReturnPdf(serialNumber, distributionDate, fileName, loginUserId);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        resp.sendRedirect(req.getContextPath() + "/IPadDetailServlet?id=" + id + "&tab=" + tab);
    }
}
