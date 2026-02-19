package web;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class CsvTemplate_Servlet
 */



@WebServlet("/download-csv-template")
public class CsvTemplate_Servlet extends HttpServlet {

    private static final String TEMPLATE_CLASSPATH = "/templates/ipad_template.csv";
    private static final String DOWNLOAD_FILENAME  = "ipad_template.csv";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // クラスパスから取得（/ はクラスパスのルート）
        InputStream in = getClass().getResourceAsStream(TEMPLATE_CLASSPATH);
        if (in == null) {
            resp.setContentType("text/plain; charset=UTF-8");
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().println("テンプレートが見つかりません: " + TEMPLATE_CLASSPATH);
            return;
        }

        // ダウンロード名設定（日本語名にも耐える形）
        String encoded = URLEncoder.encode(DOWNLOAD_FILENAME, StandardCharsets.UTF_8.name())
                                   .replaceAll("\\+", "%20");

        resp.setContentType("text/csv; charset=UTF-8");
        resp.setHeader("Content-Disposition",
            "attachment; filename=\"" + DOWNLOAD_FILENAME + "\"; filename*=UTF-8''" + encoded);

        // 必要に応じて UTF-8 BOM を付与（Excel 対策）
        boolean addBom = false;
        if (addBom) {
            resp.getOutputStream().write(new byte[]{(byte)0xEF,(byte)0xBB,(byte)0xBF});
        }

        // Java 8 互換の手動コピー
        try (InputStream is = in; jakarta.servlet.ServletOutputStream os = resp.getOutputStream()) {
            byte[] buffer = new byte[8192];
            int len;
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
            os.flush();
        }
    }
}
