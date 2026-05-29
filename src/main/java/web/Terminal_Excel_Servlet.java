package web;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import dao.TerminalDao;
import dto.Terminal_dto;

/**
 * Servlet implementation class Terminal_Excel_Servlet
 */
@WebServlet("/Terminal_Excel_Servlet")
public class Terminal_Excel_Servlet extends HttpServlet {

	  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
	            throws ServletException, IOException {

	        try {
	            req.setCharacterEncoding("UTF-8");

	            // ===== 条件取得 =====
	            String company = req.getParameter("company");
	            String[] innoArr = req.getParameterValues("innoHin");
	            String[] ownerArr = req.getParameterValues("ownerName");

	            boolean terminated = req.getParameter("includeTerminated") != null;
	            boolean inventory = req.getParameter("stockOnly") != null;
	            boolean soon = req.getParameter("replacementTarget") != null;

	            TerminalDao dao = new TerminalDao();

	            // ✅ Excelは全件取得（limitなし）
	            List<Terminal_dto> list =
	                dao.findAll(0, 999999,
	                        company, innoArr, ownerArr,
	                        terminated, inventory, soon);

	            // ===== ファイル名 =====
	            String timestamp = new java.text.SimpleDateFormat("yyyyMMddHHmmss")
	                    .format(new java.util.Date());

	            String fileName = "Terminal_" + timestamp + ".xlsx";

	            // ===== レスポンス設定 =====
	            resp.setContentType(
	                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
	            resp.setHeader("Content-Disposition",
	                "attachment; filename=\"" + fileName + "\"");

	            // ===== Excel作成 =====
	            org.apache.poi.xssf.usermodel.XSSFWorkbook wb =
	                    new org.apache.poi.xssf.usermodel.XSSFWorkbook();

	            org.apache.poi.ss.usermodel.Sheet sheet = wb.createSheet("Terminal");

	            // ヘッダ
	            org.apache.poi.ss.usermodel.Row header = sheet.createRow(0);

	            String[] cols = {
	                    "資産番号","品番","シリアル",
	                    "契約日","解約日","配布日",
	                    "会社名","部門名","使用者","単価"
	            };

	            for (int i = 0; i < cols.length; i++) {
	                header.createCell(i).setCellValue(cols[i]);
	            }

	            // データ
	            int rowIdx = 1;

	            for (Terminal_dto t : list) {
	                org.apache.poi.ss.usermodel.Row row =
	                        sheet.createRow(rowIdx++);

	                row.createCell(0).setCellValue(t.getAssetNumber());
	                row.createCell(1).setCellValue(t.getInnoHin());
	                row.createCell(2).setCellValue(t.getSerialNumber());

	                row.createCell(3).setCellValue(
	                    t.getContractDate() != null ? t.getContractDate().toString() : "");

	                row.createCell(4).setCellValue(
	                    t.getTerminationDate() != null ? t.getTerminationDate().toString() : "");

	                row.createCell(5).setCellValue(
	                    t.getDistributionDate() != null ? t.getDistributionDate().toString() : "");

	                row.createCell(6).setCellValue(t.getCompanyName());
	                row.createCell(7).setCellValue(t.getDepartmentName());
	                row.createCell(8).setCellValue(t.getOwnerName());
	                row.createCell(9).setCellValue(t.getTanka());
	            }

	            wb.write(resp.getOutputStream());
	            wb.close();

	        } catch (Exception e) {
	            e.printStackTrace();
	            throw new ServletException(e);
	        }
	    }


}
