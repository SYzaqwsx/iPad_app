
package web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/ManualDownloadServlet")
public class ManualDownloadServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // ★ 手順書の配置先
    private static final String FILE_PATH = "C:/ipad-docs/新iPad管理台帳　運用手順書.xlsx";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        File file = new File(FILE_PATH);

        if (!file.exists() || !file.isFile()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "手順書ファイルが見つかりません。");
            return;
        }

        String fileName = file.getName();

        resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        resp.setContentLengthLong(file.length());

        // 日本語ファイル名対応
        String encodedFileName = URLEncoder.encode(fileName, "UTF-8").replace("+", "%20");
        resp.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFileName);

        try (FileInputStream fis = new FileInputStream(file);
             OutputStream os = resp.getOutputStream()) {

            byte[] buffer = new byte[8192];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
            os.flush();
        }
    }
}
