
package web;

import java.io.IOException;
import java.math.BigDecimal;
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

import dao.RegisterDao;
import dto.Register_dto;

@WebServlet("/IPad_register")
public class IPad_register extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String JSP_PATH = "/WEB-INF/jsp/iPad_register.jsp";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setAttribute("form", new Register_dto());
        req.getRequestDispatcher(JSP_PATH).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding(StandardCharsets.UTF_8.name());

        String assetNumber     = trim(req.getParameter("assetNo"));
        String serialNumber    = trim(req.getParameter("serialNo"));
        String innoHin         = trim(req.getParameter("indexNo"));
        String tel             = trim(req.getParameter("phoneNo"));
        String contractDateStr = trim(req.getParameter("contractDate"));
        String contractEndStr  = trim(req.getParameter("expiryDate"));
        String rentalCompany   = trim(req.getParameter("rentalCompany"));
        String tankaStr        = trim(req.getParameter("unitPrice"));
        String udid            = trim(req.getParameter("udid"));
        String macAddress      = trim(req.getParameter("macAddress"));

        System.out.println("[IPad_register] doPost start");
        System.out.println("[IPad_register] params:"
                + " assetNumber=" + assetNumber
                + ", serialNumber=" + serialNumber
                + ", innoHin=" + innoHin
                + ", tel=" + tel
                + ", contractDate=" + contractDateStr
                + ", contractEnd=" + contractEndStr
                + ", rentalCompany=" + rentalCompany
                + ", tanka=" + tankaStr
                + ", udid=" + udid
                + ", macAddress=" + macAddress);

        List<String> errors = new ArrayList<>();

        // 必須チェック
        if (isBlank(serialNumber)) {
            errors.add("シリアル番号は必須です。");
        }
        if (isBlank(contractDateStr)) {
            errors.add("契約日は必須です。");
        }
        if (isBlank(contractEndStr)) {
            errors.add("契約満了日は必須です。");
        }
        if (isBlank(tankaStr)) {
            errors.add("単価は必須です。");
        }

        Date contractDate = null;
        Date contractPeriod = null;
        BigDecimal tanka = null;

        // 端末電話番号：入力がある場合は数字のみ
        if (!isBlank(tel) && !tel.matches("^[0-9]+$")) {
            errors.add("端末電話番号は数字のみで入力してください。");
        }

        // 契約日
        if (!isBlank(contractDateStr)) {
            try {
                contractDate = Date.valueOf(contractDateStr);
            } catch (IllegalArgumentException e) {
                errors.add("契約日の形式が不正です。");
            }
        }

        // 契約満了日
        if (!isBlank(contractEndStr)) {
            try {
                contractPeriod = Date.valueOf(contractEndStr);
            } catch (IllegalArgumentException e) {
                errors.add("契約満了日の形式が不正です。");
            }
        }

        // 契約満了日は契約日より後のみOK
        // beforeだけだと「同日」が通ってしまうため、afterで判定
        if (contractDate != null && contractPeriod != null && !contractPeriod.after(contractDate)) {
            errors.add("契約満了日は契約日より後の日付を入力してください。");
        }

        // 単価：整数のみOK
        // カンマは許容して除去するが、小数・マイナス・英字・記号はNG
        if (!isBlank(tankaStr)) {
            String normalizedTanka = tankaStr.replace(",", "");

            if (!normalizedTanka.matches("^[0-9]+$")) {
                errors.add("単価は整数で入力してください。");
            } else {
                tanka = new BigDecimal(normalizedTanka);
            }
        }

        Register_dto form = new Register_dto();
        form.setAssetNumber(assetNumber);
        form.setSerialNumber(serialNumber);
        form.setInnoHin(innoHin);
        form.setTel(tel);
        form.setContractDate(contractDate);
        form.setContractPeriod(contractPeriod);
        form.setRentalCompany(rentalCompany);
        form.setTanka(tanka);
        form.setUdid(udid);
        form.setMacAddress(macAddress);

        req.setAttribute("form", form);

        if (!errors.isEmpty()) {
            System.out.println("[IPad_register] validation errors=" + errors);
            req.setAttribute("errors", errors);
            req.getRequestDispatcher(JSP_PATH).forward(req, resp);
            return;
        }

        try {
            HttpSession session = req.getSession(false);

            String loginUserId = null;
            if (session != null) {
                Object userIdObj = session.getAttribute("authUserId");
                if (userIdObj != null) {
                    loginUserId = userIdObj.toString();
                }
            }

            if (isBlank(loginUserId)) {
                errors.add("ログインユーザー情報が取得できませんでした。再ログインしてください。");
                req.setAttribute("errors", errors);
                req.getRequestDispatcher(JSP_PATH).forward(req, resp);
                return;
            }

            RegisterDao dao = new RegisterDao();

            // シリアル番号重複チェック
            if (dao.existsBySerialNumber(serialNumber)) {
                errors.add("このシリアル番号は既に登録されています。");
                req.setAttribute("errors", errors);
                req.getRequestDispatcher(JSP_PATH).forward(req, resp);
                return;
            }
            
            // 削除済み端末シリアル番号重複チェック
            if (dao.existsBySerialNumberDeleted(serialNumber)) {
                errors.add("このシリアル番号の端末は既に登録されており、削除済みです。再度一覧に表示させたい場合は管理者にお問い合わせください");
                req.setAttribute("errors", errors);
                req.getRequestDispatcher(JSP_PATH).forward(req, resp);
                return;
            }

            int result = dao.insert(form, loginUserId);

            if (result > 0) {
                req.setAttribute("message", "登録が完了しました。");
                req.setAttribute("form", new Register_dto());
            } else {
                errors.add("登録に失敗しました。");
                req.setAttribute("errors", errors);
            }

        } catch (Exception e) {
            System.out.println("[IPad_register] ERROR: "
                    + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace(System.out);

            errors.add("システムエラーが発生しました。管理者にお問い合わせください。");
            req.setAttribute("errors", errors);
        }

        System.out.println("[IPad_register] doPost end");
        req.getRequestDispatcher(JSP_PATH).forward(req, resp);
    }

    private String trim(String s) {
        return s == null ? null : s.trim();
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
