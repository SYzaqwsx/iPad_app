
package web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import dao.RegisterDao;
import dto.Register_dto;

@WebServlet("/IPad_CSV_register")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,      // 1MB
    maxFileSize       = 1024 * 1024 * 10, // 10MB
    maxRequestSize    = 1024 * 1024 * 20  // 20MB
)
public class IPad_CSV_register extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String JSP_PATH = "/WEB-INF/jsp/iPad_register.jsp";

    // CSV列番号（0始まり）
    private static final int COL_ASSET_NUMBER   = 0;
    private static final int COL_SERIAL_NUMBER  = 1;
    private static final int COL_INNO_HIN       = 2;
    private static final int COL_TEL            = 3;
    private static final int COL_CONTRACT_DATE  = 4;
    private static final int COL_CONTRACT_END   = 5;
    private static final int COL_RENTAL_COMPANY = 6;
    private static final int COL_TANKA          = 7;
    private static final int COL_UDID           = 8;
    private static final int COL_MAC_ADDRESS    = 9;

    private static final int REQUIRED_COL_COUNT = 10;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding(StandardCharsets.UTF_8.name());

        Part filePart = req.getPart("file");
        if (filePart == null || filePart.getSize() == 0) {
            req.setAttribute("csvError", "CSVファイルを選択してください。");
            req.getRequestDispatcher(JSP_PATH).forward(req, resp);
            return;
        }

        String submittedName = filePart.getSubmittedFileName();
        req.setAttribute("csvUploadedFileName", submittedName);

        HttpSession session = req.getSession(false);
        String loginUserId = null;
        if (session != null) {
            Object userIdObj = session.getAttribute("authUserId");
            if (userIdObj != null) {
                loginUserId = userIdObj.toString();
            }
        }

        if (isBlank(loginUserId)) {
            req.setAttribute("csvError", "ログインユーザー情報が取得できませんでした。再ログインしてください。");
            req.getRequestDispatcher(JSP_PATH).forward(req, resp);
            return;
        }

        RegisterDao dao = new RegisterDao();

        int totalLines = 0;      // 総行数（ヘッダ含む）
        int dataLines = 0;       // データ行数（空行除く、ヘッダ除く）
        int successCount = 0;    // 登録成功件数
        int skipCount = 0;       // スキップ件数
        List<String> errorList = new ArrayList<>();

        // UTF-8前提。Excel保存CSVで文字化けするなら "MS932" に変更
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(filePart.getInputStream(), StandardCharsets.UTF_8))) {

            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                totalLines++;

                // 1行目はヘッダ行としてスキップ
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                // 空行スキップ
                if (isBlank(line)) {
                    continue;
                }

                dataLines++;

                List<String> cols = parseCsvLine(line);

                if (cols.size() < REQUIRED_COL_COUNT) {
                    errorList.add(dataLines + "行目：列数が不足しています。（必要:"
                            + REQUIRED_COL_COUNT + "列 / 実際:" + cols.size() + "列）");
                    skipCount++;
                    continue;
                }

                String assetNumber   = trim(getCol(cols, COL_ASSET_NUMBER));
                String serialNumber  = trim(getCol(cols, COL_SERIAL_NUMBER));
                String innoHin       = trim(getCol(cols, COL_INNO_HIN));
                String tel           = trim(getCol(cols, COL_TEL));
                String contractDateS = trim(getCol(cols, COL_CONTRACT_DATE));
                String contractEndS  = trim(getCol(cols, COL_CONTRACT_END));
                String rentalCompany = trim(getCol(cols, COL_RENTAL_COMPANY));
                String tankaStr      = trim(getCol(cols, COL_TANKA));
                String udid          = trim(getCol(cols, COL_UDID));
                String macAddress    = trim(getCol(cols, COL_MAC_ADDRESS));


                List<String> rowErrors = new ArrayList<>();

                Date contractDate = null;
                Date contractPeriod = null;
                BigDecimal tanka = null;

                // 必須チェック（単票登録と同じ）
                if (isBlank(serialNumber)) {
                	rowErrors.add("シリアル番号は必須です。");
                }
                if (isBlank(contractDateS)) {
                	rowErrors.add("契約日は必須です。");
                }
                if (isBlank(contractEndS)) {
                	rowErrors.add("契約満了日は必須です。");
                }
                if (isBlank(tankaStr)) {
                	rowErrors.add("単価は必須です。");
                }

                // 契約日
                if (!isBlank(contractDateS)) {
                	contractDate = parseDateFlexible(contractDateS);
                	if (contractDate == null) {
                		rowErrors.add("契約日の形式が不正です。");
                	}
                }

                // 契約満了日
                if (!isBlank(contractEndS)) {
                	contractPeriod = parseDateFlexible(contractEndS);
                	if (contractPeriod == null) {
                		rowErrors.add("契約満了日の形式が不正です。");
                	}
                }
                
                // 契約日 <= 契約満了日
                if (contractDate != null && contractPeriod != null && contractPeriod.before(contractDate)) {
                	rowErrors.add("契約満了日は契約日以降を入力してください。");
                }

                // 単価
                if (!isBlank(tankaStr)) {
                	try {
                		tanka = new BigDecimal(tankaStr.replace(",", ""));
                		if (tanka.compareTo(BigDecimal.ZERO) < 0) {
                			rowErrors.add("単価は0以上を入力してください。");
                		}
                	} catch (NumberFormatException e) {
                		rowErrors.add("単価は数値で入力してください。");
                	}
                }

                // 資産番号重複チェック（資産番号がある場合だけ）
                if (!isBlank(assetNumber)) {
                	if (dao.existsByAssetNumber(assetNumber)) {
                		rowErrors.add("資産番号[" + assetNumber + "]は既に登録されています。");
                	}
                }

                if (!rowErrors.isEmpty()) {
                	errorList.add(dataLines + "行目：" + String.join(" / ", rowErrors));
                	skipCount++;
                	continue;
                }


                // DTOへ詰める
                Register_dto dto = new Register_dto();
                dto.setAssetNumber(assetNumber);
                dto.setSerialNumber(serialNumber);
                dto.setInnoHin(innoHin);
                dto.setTel(tel);
                dto.setContractDate(contractDate);
                dto.setContractPeriod(contractPeriod);
                dto.setRentalCompany(rentalCompany);
                dto.setTanka(tanka);
                dto.setUdid(udid);
                dto.setMacAddress(macAddress);

                // DB登録
                try {
                    int result = dao.insert(dto, loginUserId);
                    if (result > 0) {
                        successCount++;
                    } else {
                        errorList.add(dataLines + "行目：登録に失敗しました。");
                        skipCount++;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    errorList.add(dataLines + "行目：DB登録エラーが発生しました。");
                    skipCount++;
                }
            }

            // 結果メッセージ
            String resultMessage = "CSV一括登録結果：ファイル=" + submittedName
                    + "、データ行数=" + dataLines
                    + "件、登録成功=" + successCount
                    + "件、スキップ=" + skipCount + "件";

            // 成功1件以上のときだけ success を表示
            if (successCount > 0) {
                req.setAttribute("csvMessage", resultMessage);
            }

            // エラーがあるときは error を表示
            if (!errorList.isEmpty()) {
                int maxShow = Math.min(errorList.size(), 10);
                List<String> showErrors = errorList.subList(0, maxShow);

                StringBuilder err = new StringBuilder();
                err.append(resultMessage).append(" / エラー詳細：");
                for (String s : showErrors) {
                    err.append(" ").append(s);
                }
                if (errorList.size() > maxShow) {
                    err.append(" ... 他 ").append(errorList.size() - maxShow).append(" 件");
                }

                req.setAttribute("csvError", err.toString());
            }

            // データ行が1件もなかった場合
            if (dataLines == 0 && errorList.isEmpty()) {
                req.setAttribute("csvError", "CSVファイルに登録対象データがありません。");
            }

        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("csvError", "CSVアップロード中にシステムエラーが発生しました。");
        }

        req.getRequestDispatcher(JSP_PATH).forward(req, resp);
    }

    private String getCol(List<String> cols, int index) {
        if (index < 0 || index >= cols.size()) {
            return "";
        }
        return cols.get(index);
    }

    private String trim(String s) {
        return s == null ? null : s.trim();
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    /**
     * 簡易CSVパーサ
     * - カンマ区切り
     * - ダブルクォート対応
     * - "" は " として扱う
     */
    private List<String> parseCsvLine(String line) {
        List<String> cols = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    sb.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                cols.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }

        cols.add(sb.toString());
        return cols;
    }
    

private Date parseDateFlexible(String s) {
    if (isBlank(s)) {
        return null;
    }

    String normalized = s.trim()
            .replace("/", "-")
            .replace(".", "-");

    String[] parts = normalized.split("-");
    if (parts.length != 3) {
        return null;
    }

    try {
        int y = Integer.parseInt(parts[0]);
        int m = Integer.parseInt(parts[1]);
        int d = Integer.parseInt(parts[2]);

        String yyyy = String.format("%04d", y);
        String mm   = String.format("%02d", m);
        String dd   = String.format("%02d", d);

        return Date.valueOf(yyyy + "-" + mm + "-" + dd);
    } catch (Exception e) {
        return null;
    }
}

}
