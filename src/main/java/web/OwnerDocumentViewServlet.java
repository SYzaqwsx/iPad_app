
package web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Date;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import dao.DetailDao;
import dto.Detail_dto;

@WebServlet("/OwnerDocumentViewServlet")
public class OwnerDocumentViewServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // ★本番環境に合わせて変更してください
    private static final String RECEIPT_DIR = "C:/ipad-docs/receipt";
    private static final String RETURN_DIR  = "C:/ipad-docs/return";


@Override
protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {

    System.out.println("=== OwnerDocumentViewServlet START ===");

    String kind = req.getParameter("kind"); // receipt / return
    String serialNumber = req.getParameter("serialNumber");
    String distributionDateStr = req.getParameter("distributionDate");

    System.out.println("[param] kind=" + kind);
    System.out.println("[param] serialNumber=" + serialNumber);
    System.out.println("[param] distributionDate=" + distributionDateStr);

    if (kind == null || kind.isEmpty()
            || serialNumber == null || serialNumber.isEmpty()
            || distributionDateStr == null || distributionDateStr.isEmpty()) {
        System.out.println("[error] missing parameter");
        resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "必要なパラメータが不足しています。");
        return;
    }

    Date distributionDate;
    try {
        distributionDate = Date.valueOf(distributionDateStr);
        System.out.println("[parsed] distributionDate=" + distributionDate);
    } catch (IllegalArgumentException e) {
        System.out.println("[error] invalid date format: " + distributionDateStr);
        resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "日付形式が不正です。");
        return;
    }

    DetailDao dao = new DetailDao();
    Detail_dto dto;
    try {
        dto = dao.findOwnerDocument(serialNumber, distributionDate);
        System.out.println("[dao] dto is " + (dto == null ? "null" : "found"));
    } catch (Exception e) {
        System.out.println("[error] dao.findOwnerDocument failed");
        e.printStackTrace();
        resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "書類情報の取得に失敗しました。");
        return;
    }

    if (dto == null) {
        System.out.println("[error] dto is null");
        resp.sendError(HttpServletResponse.SC_NOT_FOUND, "対象データが見つかりません。");
        return;
    }

    String fileName;
    File file;

    if ("receipt".equals(kind)) {
        fileName = dto.getReceiptPdf();
        System.out.println("[dto] receiptPdf=" + fileName);
        file = new File(RECEIPT_DIR, fileName == null ? "" : fileName);
    } else if ("return".equals(kind)) {
        fileName = dto.getReturnPdf();
        System.out.println("[dto] returnPdf=" + fileName);
        file = new File(RETURN_DIR, fileName == null ? "" : fileName);
    } else {
        System.out.println("[error] invalid kind=" + kind);
        resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "kindの値が不正です。");
        return;
    }

    if (fileName == null || fileName.isEmpty()) {
        System.out.println("[error] fileName is null or empty");
        resp.sendError(HttpServletResponse.SC_NOT_FOUND, "登録済みファイルがありません。");
        return;
    }

    System.out.println("[file] path=" + file.getAbsolutePath());
    System.out.println("[file] exists=" + file.exists());
    System.out.println("[file] isFile=" + file.isFile());

    if (!file.exists() || !file.isFile()) {
        System.out.println("[error] file not found on server");
        resp.sendError(HttpServletResponse.SC_NOT_FOUND, "サーバー上にファイルが存在しません。");
        return;
    }

    resp.setContentType("application/pdf");
    resp.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");
    resp.setContentLengthLong(file.length());

    try (FileInputStream fis = new FileInputStream(file);
         OutputStream os = resp.getOutputStream()) {

        byte[] buffer = new byte[8192];
        int len;
        while ((len = fis.read(buffer)) != -1) {
            os.write(buffer, 0, len);
        }
        os.flush();
    }

    System.out.println("[success] PDF response completed");
    System.out.println("=== OwnerDocumentViewServlet END ===");
}

}
