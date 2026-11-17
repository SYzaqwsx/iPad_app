package web;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import dao.TerminalDao;
import dto.Terminal_dto;

@WebServlet("/Terminal_Excel_Servlet")
public class Terminal_Excel_Servlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(
            HttpServletRequest req,
            HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            req.setCharacterEncoding("UTF-8");

            /*
             * 検索条件取得
             */
            String company = trim(req.getParameter("company"));
            String[] innoArr = req.getParameterValues("innoHin");
            String[] ownerArr = req.getParameterValues("ownerName");
            boolean terminated = req.getParameter("includeTerminated") != null;
            boolean inventory = req.getParameter("stockOnly") != null;
            boolean soon = req.getParameter("replacementTarget") != null;

            /*
             * ログインセッション取得
             *
             * Excel出力範囲は、ブラウザーから送られた値ではなく
             * サーバー側のログイン権限から決定する。
             */
            HttpSession session =
                    req.getSession(false);

            if (session == null) {
                resp.sendError(
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "ログイン情報が取得できませんでした。");
                return;
            }

            Integer roleId = getRoleId(session);

            if (roleId == null) {
                resp.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "権限情報が取得できませんでした。");
                return;
            }

            /*
             * 権限別の表示範囲
             *
             * role89: IBS専用データのみ
             *
             * role99: IBSを含む全データ
             *
             * その他: IBSデータを除外
             */
            boolean ibsOnly;
            boolean adminView;

            if (roleId.intValue() == 89) {
                ibsOnly = true;
                adminView = false;
            } else if (roleId.intValue() == 99) {
                ibsOnly = false;
                adminView = true;
            } else {
                ibsOnly = false;
                adminView = false;            }

            System.out.println(
                    "[Terminal_Excel_Servlet] "
                    + "roleId=" + roleId
                    + ", company=" + company
                    + ", terminated=" + terminated
                    + ", inventory=" + inventory
                    + ", soon=" + soon
                    + ", ibsOnly=" + ibsOnly
                    + ", adminView=" + adminView
            );

            TerminalDao dao = new TerminalDao();

            /*
             * Excel出力ではページングを行わず全件取得する。
             */
            List<Terminal_dto> list =
                    dao.findAll(
                            0,
                            999999,
                            company,
                            innoArr,
                            ownerArr,
                            terminated,
                            inventory,
                            soon,
                            ibsOnly,
                            adminView
                    );

            System.out.println(
                    "[Terminal_Excel_Servlet] exportCount="
                    + list.size()
            );

            /*
             * ファイル名
             */
            String timestamp = new SimpleDateFormat("yyyyMMddHHmmss") .format(new Date());
            String fileName = "Terminal_" + timestamp + ".xlsx";

            /*
             * レスポンス設定
             */
            resp.setContentType(
                    "application/vnd.openxmlformats-officedocument."
                    + "spreadsheetml.sheet");
            resp.setHeader(
                    "Content-Disposition",
                    "attachment; filename=\""
                    + fileName
                    + "\""
            );

            /*
             * Excel作成
             */
            try (XSSFWorkbook wb = new XSSFWorkbook()) {

                Sheet sheet = wb.createSheet("Terminal");

                /*
                 * ヘッダー
                 */
                Row header = sheet.createRow(0);

                String[] cols = {
                        "資産番号",
                        "品番",
                        "シリアル",
                        "契約日",
                        "解約日",
                        "配布日",
                        "会社名",
                        "部門名",
                        "使用者",
                        "単価"
                };

                for (int i = 0; i < cols.length; i++) {
                    header.createCell(i)
                            .setCellValue(cols[i]);
                }

                /*
                 * データ
                 */
                int rowIdx = 1;

                for (Terminal_dto t : list) {

                    Row row = sheet.createRow(rowIdx++);
                    row.createCell(0).setCellValue(
                            nullToEmpty(
                                    t.getAssetNumber()));

                    row.createCell(1).setCellValue(
                            nullToEmpty(
                                    t.getInnoHin()));

                    row.createCell(2).setCellValue(
                            nullToEmpty(
                                    t.getSerialNumber()));

                    row.createCell(3).setCellValue(
                            t.getContractDate() != null
                                    ? t.getContractDate().toString()
                                    : "");

                    row.createCell(4).setCellValue(
                            t.getTerminationDate() != null
                                    ? t.getTerminationDate().toString()
                                    : "");

                    row.createCell(5).setCellValue(
                            t.getDistributionDate() != null
                                    ? t.getDistributionDate().toString()
                                    : "");

                    row.createCell(6).setCellValue(
                            nullToEmpty(
                                    t.getCompanyName()));

                    row.createCell(7).setCellValue(
                            nullToEmpty(
                                    t.getDepartmentName()));

                    row.createCell(8).setCellValue(
                            nullToEmpty(
                                    t.getOwnerName()));

                    row.createCell(9).setCellValue(
                            t.getTanka());
                }

                /*
                 * 見やすさのため列幅を自動調整
                 */
                for (int i = 0; i < cols.length; i++) {
                    sheet.autoSizeColumn(i);
                }
                wb.write(
                        resp.getOutputStream());
                resp.getOutputStream()
                        .flush();
            }

            System.out.println(
                    "[Terminal_Excel_Servlet] export success. "
                    + "fileName=" + fileName
            );

        } catch (Exception e) {

            System.out.println(
                    "[Terminal_Excel_Servlet] ERROR: "
                    + e.getClass().getSimpleName()
                    + " - "
                    + e.getMessage()
            );

            e.printStackTrace();

            throw new ServletException(e);
        }
    }

    private Integer getRoleId(
            HttpSession session) {

        if (session == null) {
            return null;
        }

        Object roleObj =
                session.getAttribute(
                        "authRoleId");

        if (roleObj == null) {
            return null;
        }

        if (roleObj instanceof Number) {
            return ((Number) roleObj)
                    .intValue();
        }

        try {
            return Integer.valueOf(
                    roleObj.toString().trim());

        } catch (NumberFormatException e) {

            System.out.println(
                    "[Terminal_Excel_Servlet] "
                    + "invalid roleId="
                    + roleObj
            );

            return null;
        }
    }

    private String trim(String value) {

        if (value == null) {
            return null;
        }

        return value.trim();
    }

    private String nullToEmpty(
            String value) {

        return value == null
                ? ""
                : value;
    }
}