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

import dao.RegisterIbsDao;
import dto.Register_dto;

@WebServlet("/IPad_register_ibs")
public class IPad_register_ibs extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final String JSP_PATH =
            "/WEB-INF/jsp/iPad_register_ibs.jsp";

    private static final int IBS_REGISTER_ROLE_ID = 89;
    private static final int ADMIN_ROLE_ID = 99;

    @Override
    protected void doGet(
            HttpServletRequest req,
            HttpServletResponse resp)
            throws ServletException, IOException {

        System.out.println("[IPad_register_ibs] doGet start");

        HttpSession session = req.getSession(false);
        Integer roleId = getRoleId(session);

        System.out.println(
                "[IPad_register_ibs] doGet roleId=" + roleId);

        if (!canView(roleId)) {
            System.out.println(
                    "[IPad_register_ibs] doGet forbidden. roleId="
                            + roleId);

            resp.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "この画面を表示する権限がありません。");

            return;
        }

        req.setAttribute("roleId", roleId);
        req.setAttribute("form", new Register_dto());

        req.getRequestDispatcher(JSP_PATH).forward(req, resp);

        System.out.println("[IPad_register_ibs] doGet end");
    }

    @Override
    protected void doPost(
            HttpServletRequest req,
            HttpServletResponse resp)
            throws ServletException, IOException {

        System.out.println("[IPad_register_ibs] doPost start");

        req.setCharacterEncoding(StandardCharsets.UTF_8.name());

        HttpSession session = req.getSession(false);
        Integer roleId = getRoleId(session);

        System.out.println(
                "[IPad_register_ibs] doPost roleId=" + roleId);

        if (!canRegister(roleId)) {
            System.out.println(
                    "[IIPad_register_ibs] doPost forbidden. roleId="
                            + roleId);

            resp.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "IBS専用端末を登録する権限がありません。");

            return;
        }

        req.setAttribute("roleId", roleId);

        String serialNumber =
                trim(req.getParameter("serialNo"));

        String tel =
                trim(req.getParameter("phoneNo"));

        String contractDateStr =
                trim(req.getParameter("contractDate"));

        String contractEndStr =
                trim(req.getParameter("expiryDate"));

        String rentalCompany =
                trim(req.getParameter("rentalCompany"));

        String tankaStr =
                trim(req.getParameter("unitPrice"));

        String udid =
                trim(req.getParameter("udid"));

        String macAddress =
                trim(req.getParameter("macAddress"));

        System.out.println(
                "[IPad_register_ibs] params:"
                        + " serialNumber=" + serialNumber
                        + ", tel=" + tel
                        + ", contractDate=" + contractDateStr
                        + ", contractEnd=" + contractEndStr
                        + ", rentalCompany=" + rentalCompany
                        + ", tanka=" + tankaStr
                        + ", udid=" + udid
                        + ", macAddress=" + macAddress);

        List<String> errors = new ArrayList<>();

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

        if (!isBlank(tel) && !tel.matches("^[0-9]+$")) {
            errors.add(
                    "端末電話番号は数字のみで入力してください。");
        }

        if (!isBlank(contractDateStr)) {
            try {
                contractDate = Date.valueOf(contractDateStr);
            } catch (IllegalArgumentException e) {
                errors.add("契約日の形式が不正です。");
            }
        }

        if (!isBlank(contractEndStr)) {
            try {
                contractPeriod = Date.valueOf(contractEndStr);
            } catch (IllegalArgumentException e) {
                errors.add("契約満了日の形式が不正です。");
            }
        }

        if (contractDate != null
                && contractPeriod != null
                && !contractPeriod.after(contractDate)) {

            errors.add(
                    "契約満了日は契約日より後の日付を入力してください。");
        }

        if (!isBlank(tankaStr)) {
            String normalizedTanka =
                    tankaStr.replace(",", "");

            if (!normalizedTanka.matches("^[0-9]+$")) {
                errors.add("単価は整数で入力してください。");
            } else {
                try {
                    tanka = new BigDecimal(normalizedTanka);
                } catch (NumberFormatException e) {
                    errors.add("単価の形式が不正です。");
                }
            }
        }

        Register_dto form = new Register_dto();

        form.setAssetNumber(null);
        form.setSerialNumber(serialNumber);
        form.setInnoHin(null);
        form.setTel(tel);
        form.setContractDate(contractDate);
        form.setContractPeriod(contractPeriod);
        form.setRentalCompany(rentalCompany);
        form.setTanka(tanka);
        form.setUdid(udid);
        form.setMacAddress(macAddress);

        req.setAttribute("form", form);

        if (!errors.isEmpty()) {
            System.out.println(
                    "[IPad_register_ibs] validation errors="
                            + errors);

            req.setAttribute("errors", errors);
            req.getRequestDispatcher(JSP_PATH).forward(req, resp);

            return;
        }

        try {
            String loginUserId = getLoginUserId(session);

            if (isBlank(loginUserId)) {
                errors.add(
                        "ログインユーザー情報が取得できませんでした。"
                                + "再ログインしてください。");

                req.setAttribute("errors", errors);
                req.getRequestDispatcher(JSP_PATH)
                        .forward(req, resp);

                return;
            }

            RegisterIbsDao dao = new RegisterIbsDao();

            if (dao.existsBySerialNumber(serialNumber)) {
                errors.add(
                        "このシリアル番号は既に登録されています。");

                req.setAttribute("errors", errors);
                req.getRequestDispatcher(JSP_PATH)
                        .forward(req, resp);

                return;
            }

            // 削除済み端末シリアル番号重複チェック
            if (dao.existsBySerialNumberDeleted(serialNumber)) {
                errors.add("このシリアル番号の端末は既に登録されており、削除済みです。再度一覧に表示させたい場合は管理者にお問い合わせください");
                req.setAttribute("errors", errors);
                req.getRequestDispatcher(JSP_PATH).forward(req, resp);
                return;
            }

            int result =
                    dao.insertTerminalAndOwner(
                            form,
                            loginUserId);

            if (result == 2) {
                System.out.println(
                        "[IPad_register_ibs] insert success."
                                + " serialNumber="
                                + serialNumber);

                req.setAttribute(
                        "message",
                        "IBS専用端末の登録が完了しました。");

                req.setAttribute(
                        "form",
                        new Register_dto());

            } else {
                System.out.println(
                        "[IPad_register_ibs] unexpected result="
                                + result);

                errors.add("登録に失敗しました。");
                req.setAttribute("errors", errors);
            }

        } catch (IllegalStateException e) {
            System.out.println(
                    "[IPad_register_ibs] registration rejected: "
                            + e.getMessage());

            if (e.getMessage() != null
                    && e.getMessage().contains("既に登録")) {

                errors.add(
                        "このシリアル番号は既に登録されています。");

            } else {
                errors.add("登録に失敗しました。");
            }

            req.setAttribute("errors", errors);

        } catch (Exception e) {
            System.out.println(
                    "[IPad_register_ibs] ERROR: "
                            + e.getClass().getSimpleName()
                            + " - "
                            + e.getMessage());

            e.printStackTrace(System.out);

            errors.add(
                    "システムエラーが発生しました。"
                            + "管理者にお問い合わせください。");

            req.setAttribute("errors", errors);
        }

        System.out.println("[IPad_register_ibs] doPost end");

        req.getRequestDispatcher(JSP_PATH).forward(req, resp);
    }

    private boolean canView(Integer roleId) {
        if (roleId == null) {
            return false;
        }

        return roleId.intValue() == IBS_REGISTER_ROLE_ID
                || roleId.intValue() == ADMIN_ROLE_ID;
    }

    private boolean canRegister(Integer roleId) {
        return roleId != null
                && roleId.intValue() == IBS_REGISTER_ROLE_ID;
    }

    private String getLoginUserId(HttpSession session) {
        if (session == null) {
            return null;
        }

        Object userIdObject =
                session.getAttribute("authUserId");

        if (userIdObject == null) {
            return null;
        }

        return userIdObject.toString();
    }

    private Integer getRoleId(HttpSession session) {
        if (session == null) {
            System.out.println(
                    "[IPad_register_ibs] session is null");

            return null;
        }

        Object roleIdObject =
                session.getAttribute("authRoleId");

        if (roleIdObject == null) {
            roleIdObject =
                    session.getAttribute("role_id");
        }

        if (roleIdObject == null) {
            System.out.println(
                    "[IPad_register_ibs]"
                            + " roleId is not found in session");

            return null;
        }

        if (roleIdObject instanceof Number) {
            return ((Number) roleIdObject).intValue();
        }

        try {
            return Integer.valueOf(
                    roleIdObject.toString().trim());

        } catch (NumberFormatException e) {
            System.out.println(
                    "[IPad_register_ibs] invalid roleId="
                            + roleIdObject);

            return null;
        }
    }

    private String trim(String value) {
        return value == null
                ? null
                : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null
                || value.trim().isEmpty();
    }
}