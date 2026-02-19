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
 * Servlet implementation class IPad_CSV_register
 */
@WebServlet("/IPad_CSV_register")

@MultipartConfig(
        fileSizeThreshold = 1024 * 1024, // 1MB
        maxFileSize       = 1024 * 1024 * 10,  // 10MB
        maxRequestSize    = 1024 * 1024 * 20   // 20MB
)

public class IPad_CSV_register extends HttpServlet {

	   @Override
	    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
	            throws ServletException, IOException {

	        Part filePart = req.getPart("file");
	        if (filePart == null || filePart.getSize() == 0) {
	            req.setAttribute("error", "CSVファイルを選択してください。");
	            req.getRequestDispatcher("/register.jsp").forward(req, resp);
	            return;
	        }

	        String submittedName = filePart.getSubmittedFileName();
	        long lines = 0;

	        // 今は行数カウントのみ。後で CSV 解析→バリデーション→DB 登録へ差し替え。
	        try (BufferedReader br = new BufferedReader(
	                new InputStreamReader(filePart.getInputStream(), StandardCharsets.UTF_8))) {
	            String line;
	            while ((line = br.readLine()) != null) {
	                // TODO: 1行ずつパース。ヘッダスキップ等。
	                lines++;
	            }
	        }

	        // 結果メッセージ（JSP側の message/error で受け取れます）
	        req.setAttribute("message",
	                String.format("ダミー一括登録完了：ファイル=%s, 行数(ヘッダ含む)=%d", submittedName, lines));

	        req.getRequestDispatcher("/register.jsp").forward(req, resp);
	    }
	}


