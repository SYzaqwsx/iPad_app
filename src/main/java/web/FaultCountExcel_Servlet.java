
package web;

import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import dao.FaultCountDao;
import dto.Fault_count_row_dto;

@WebServlet("/FaultCountExcel_Servlet")
public class FaultCountExcel_Servlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // 一覧とそろえる
    private static final int PAGE_SIZE = 10;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        FaultCountDao dao = new FaultCountDao();

        int year = parseInt(req.getParameter("year"), LocalDate.now().getYear());
        int page = parseInt(req.getParameter("page"), 1);

        String companyId = trim(req.getParameter("companyId"));
        String departmentId = trim(req.getParameter("departmentId"));

        List<Fault_count_row_dto> allRows = dao.findFaultCountRows(year, companyId, departmentId);

        Map<String, Integer> companyTotals = new LinkedHashMap<>();
        Map<String, Integer> departmentTotals = new LinkedHashMap<>();

        for (Fault_count_row_dto row : allRows) {
            String companyKey = nvl(row.getCompanyId());
            String departmentKey = nvl(row.getCompanyId()) + "|" + nvl(row.getDepartmentId());

            companyTotals.put(companyKey,
                    companyTotals.getOrDefault(companyKey, 0) + row.getFaultCount());

            departmentTotals.put(departmentKey,
                    departmentTotals.getOrDefault(departmentKey, 0) + row.getFaultCount());
        }

        for (Fault_count_row_dto row : allRows) {
            String companyKey = nvl(row.getCompanyId());
            String departmentKey = nvl(row.getCompanyId()) + "|" + nvl(row.getDepartmentId());

            row.setCompanyTotal(companyTotals.getOrDefault(companyKey, 0));
            row.setDepartmentTotal(departmentTotals.getOrDefault(departmentKey, 0));
        }

        int totalCount = allRows.size();
        int totalPages = totalCount == 0 ? 1 : (int) Math.ceil((double) totalCount / PAGE_SIZE);

        if (page < 1) {
            page = 1;
        }
        if (page > totalPages) {
            page = totalPages;
        }

        int fromIndex = (page - 1) * PAGE_SIZE;
        int toIndex = Math.min(fromIndex + PAGE_SIZE, totalCount);

        List<Fault_count_row_dto> pageRows = new ArrayList<>();
        if (fromIndex < toIndex) {
            pageRows = allRows.subList(fromIndex, toIndex);
        }

        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("故障台数");

        int r = 0;

        Row titleRow = sheet.createRow(r++);
        titleRow.createCell(0).setCellValue("故障台数");
        titleRow.createCell(1).setCellValue(year + "年");
        

     // 2行目：空行
     r++;

  // 3行目：ヘッダ
  Row header = sheet.createRow(r++);
  header.createCell(0).setCellValue("会社名");
  header.createCell(1).setCellValue("部署名");
  header.createCell(2).setCellValue("氏名");
  header.createCell(3).setCellValue("故障台数");

//4行目以降：データ
for (Fault_count_row_dto row : pageRows) {
   Row dataRow = sheet.createRow(r++);
   dataRow.createCell(0).setCellValue(nvl(row.getCompanyName()));
   dataRow.createCell(1).setCellValue(nvl(row.getDepartmentName()));
   dataRow.createCell(2).setCellValue(nvl(row.getEmployeeName()));
   dataRow.createCell(3).setCellValue(row.getFaultCount());
}


String fileName = "fault_count_" + year + ".xlsx";
String encoded = URLEncoder.encode(fileName, "UTF-8").replace("+", "%20");


        resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        resp.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encoded);

        wb.write(resp.getOutputStream());
        wb.close();
    }

    private String trim(String s) {
        return s == null ? null : s.trim();
    }

    private int parseInt(String s, int defaultValue) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private String nvl(String s) {
        return s == null ? "" : s;
    }
}
