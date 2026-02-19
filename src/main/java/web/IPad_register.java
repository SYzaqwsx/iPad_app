package web;


import java.io.IOException;
import java.nio.charset.StandardCharsets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class IPad_register
 */

@WebServlet("/IPad_register")
public class IPad_register extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding(StandardCharsets.UTF_8.name());

        String assetNo      = req.getParameter("assetNo");
        String indexNo      = req.getParameter("indexNo");
        String serialNo     = req.getParameter("serialNo");
        String contractDate = req.getParameter("contractDate"); // yyyy-MM-dd
        String expiryDate   = req.getParameter("expiryDate");   // yyyy-MM-dd
        String unitPrice    = req.getParameter("unitPrice");

        if (assetNo == null || assetNo.trim().isEmpty()) {
            req.setAttribute("error", "資産番号は必須です。");
        } else {
            String msg = String.format("ダミー登録完了：資産番号=%s, 単価=%s", assetNo, unitPrice);
            req.setAttribute("message", msg);
        }

        // ← ここを JSP の実配置に合わせる
        req.getRequestDispatcher("/WEB-INF/jsp/iPad_register.jsp").forward(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // 初期表示（GET）の時も同様に JSP へ
        req.getRequestDispatcher("/WEB-INF/jsp/iPad_register.jsp").forward(req, resp);
    }
}
