package web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

/**
 * Servlet implementation class Csv_UploadServlet
 */

@WebServlet("/upload-csv")
@MultipartConfig
public class Csv_UploadServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Part filePart = req.getPart("file");
        if (filePart == null || filePart.getSize() == 0L) {
            req.setAttribute("csvError", "CSVファイルを選択してください。");
            req.getRequestDispatcher("/WEB-INF/jsp/iPad_register.jsp").forward(req, resp);
            return;
        }

        // ★ここにCSVのバリデーション・登録処理を実装
        // 例：先頭行だけ読み込んで件数をダミーで数える
        int lines = 0;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(filePart.getInputStream(), StandardCharsets.UTF_8))) {
            while (br.readLine() != null) lines++;
        } catch (Exception e) {
            req.setAttribute("csvError", "CSVの読み込み中にエラーが発生しました。");
            req.getRequestDispatcher("/WEB-INF/jsp/iPad_register.jsp").forward(req, resp);
            return;
        }

        req.setAttribute("csvMessage", String.format("CSVアップロード完了（行数: %d）", lines));
        req.getRequestDispatcher("/WEB-INF/jsp/iPad_register.jsp").forward(req, resp);
    }
}
