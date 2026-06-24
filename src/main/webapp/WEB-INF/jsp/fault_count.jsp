
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="dto.Fault_count_row_dto" %>

<%!
    private String h(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private int i(Object o, int def) {
        try {
            if (o == null) return def;
            return Integer.parseInt(o.toString());
        } catch (Exception e) {
            return def;
        }
    }
%>

<%
    List<Fault_count_row_dto> rows = (List<Fault_count_row_dto>) request.getAttribute("rows");
    List<String[]> companyOptions = (List<String[]>) request.getAttribute("companyOptions");
    List<String[]> departmentOptions = (List<String[]>) request.getAttribute("departmentOptions");

    int year = i(request.getAttribute("year"), java.time.LocalDate.now().getYear());
    int pageNo = i(request.getAttribute("page"), 1);
    int totalPages = i(request.getAttribute("totalPages"), 1);

    String companyId = request.getAttribute("companyId") != null ? request.getAttribute("companyId").toString() : "";
    String departmentId = request.getAttribute("departmentId") != null ? request.getAttribute("departmentId").toString() : "";
%>

<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>iPad・PC台帳システム - 故障台数</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/base.css?v=20260218">

    <style>
        html, body {
            height: 100%;
            margin: 0;
        }

        body {
            display: flex;
            flex-direction: column;
        }

        .container {
            flex: 1;
            width: 100%;
            box-sizing: border-box;
            padding: 18px 0 0;
        }

        .page-subtitle {
            color: #fff;
            font-size: 20px;
            font-weight: 700;
            margin-top: 4px;
        }

        .topbar__inner.topbar__inner--double {
            display: flex;
            justify-content: space-between;
            align-items: flex-start;
        }

        .topbar-title-wrap {
            display: flex;
            flex-direction: column;
        }

        .fault-wrap {
            width: 100%;
            max-width: 780px;
            margin: 0 auto;
        }

        .year-nav {
            text-align: center;
            margin: 4px 0 8px;
            font-size: 18px;
            color: #333;
        }

        .year-nav a {
            text-decoration: none;
            color: #333;
            font-weight: 700;
            margin: 0 18px;
        }

        .hint {
            width: 100%;
            max-width: 510px;
            margin: 0 auto 4px;
            text-align: right;
            font-size: 14px;
            color: #333;
        }

        .list-box {
            width: 100%;
            max-width: 510px;
            height: 255px;
            margin: 0 auto;
            overflow-y: auto;
            overflow-x: hidden;
            border: 1px solid #9aa7b2;
            background: #fff;
        }

        .fault-table {
            width: 100%;
            border-collapse: collapse;
            table-layout: fixed;
        }

        .fault-table td {
            border-bottom: 1px solid #9aa7b2;
            padding: 2px 8px;
            font-size: 14px;
            line-height: 1.35;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
        }

        .row-company td {
            background: #005a9c;
            color: #fff;
            font-weight: 700;
        }

        .row-department td {
            background: #b9cee3;
            color: #222;
        }

        .row-employee td {
            background: #d7e4f0;
            color: #222;
        }

        .col-name {
            width: 72%;
            text-align: left;
        }

        .col-count {
            width: 18%;
            text-align: right;
        }

        .col-link {
            width: 10%;
            text-align: center;
        }

        .indent-dept {
            padding-left: 34px !important;
        }

        .indent-emp {
            padding-left: 66px !important;
        }

        .detail-link {
            color: #0066cc;
            text-decoration: underline;
            font-weight: 400;
        }

        .pager-line {
            text-align: center;
            margin-top: 14px;
            font-size: 18px;
            color: #222;
        }

        .pager-line a {
            color: #222;
            text-decoration: none;
            margin: 0 3px;
        }

        .pager-line .current {
            font-weight: 700;
            margin: 0 4px;
        }

        .excel-box {
            width: 100%;
            max-width: 510px;
            margin: 10px auto 0;
            display: flex;
            justify-content: flex-end;
        }

        .footer {
            margin-top: 18px;
        }

        .footer-inner {
            display: flex;
            justify-content: flex-start;
            padding: 14px 30px;
        }

        /* ===== 検索モーダル（一覧画面寄せ） ===== */
        .modal {
            display: none;
            position: fixed;
            inset: 0;
            background: rgba(0,0,0,0.4);
            align-items: center;
            justify-content: center;
            z-index: 9999;
        }

        .modal-box {
            width: 560px;
            background: #dcdcdc;
            overflow: hidden;
        }

        .modal-box-sm {
            width: 560px;
        }

        .modal-header {
            background: #083b65;
            color: #fff;
            padding: 16px 22px;
            font-size: 22px;
            font-weight: 700;
        }

        .modal-body {
            padding: 36px 40px 56px;
        }

        .form-row {
            display: grid;
            grid-template-columns: 120px 24px 1fr;
            align-items: center;
            gap: 10px;
            margin-bottom: 14px;
            font-size: 18px;
        }

        .form-row label {
            font-weight: 700;
        }

        .form-row .colon {
            text-align: center;
        }

        .form-row select {
            width: 100%;
            height: 30px;
            box-sizing: border-box;
        }

        .modal-footer {
            background: #083b65;
            padding: 16px 20px;
            display: flex;
            justify-content: space-between;
            gap: 12px;
        }

        .modal-footer-right {
            display: flex;
            gap: 12px;
        }
    </style>
</head>

<body>

<header class="topbar">
    <div class="topbar__inner topbar__inner--double">
        <div class="topbar-title-wrap">
            <div class="topbar__title">iPad・PC台帳システム</div>
            <div class="page-subtitle">故障台数</div>
        </div>
        <button type="button" class="btn-soft" onclick="openModal()">検索</button>
    </div>
</header>

<div class="container">
    <div class="fault-wrap">

        <div class="year-nav">
            <a href="<%= request.getContextPath() %>/FaultCount_Servlet?year=<%= year - 1 %>&companyId=<%= h(companyId) %>&departmentId=<%= h(departmentId) %>&page=1">◀</a>
            <span><%= year %>年</span>
            <a href="<%= request.getContextPath() %>/FaultCount_Servlet?year=<%= year + 1 %>&companyId=<%= h(companyId) %>&departmentId=<%= h(departmentId) %>&page=1">▶</a>
        </div>

        <div class="hint">＞押下で詳細を表示</div>

        <div class="list-box">
            <table class="fault-table">
                <tbody>
                <%
                    String prevCompany = "";
                    String prevDept = "";

                    if (rows != null && !rows.isEmpty()) {
                        for (Fault_count_row_dto row : rows) {
                            String companyKey = row.getCompanyId() == null ? "" : row.getCompanyId();
                            String deptKey = companyKey + "|" + (row.getDepartmentId() == null ? "" : row.getDepartmentId());

                            if (!companyKey.equals(prevCompany)) {
                %>
                    <tr class="row-company">
                        <td class="col-name"><%= h(row.getCompanyName()) %></td>
                        <td class="col-count"><%= row.getCompanyTotal() %></td>
                        <td class="col-link"></td>
                    </tr>
                <%
                                prevDept = "";
                            }

                            if (!deptKey.equals(prevDept)) {
                %>
                    <tr class="row-department">
                        <td class="col-name indent-dept"><%= h(row.getDepartmentName()) %></td>
                        <td class="col-count"><%= row.getDepartmentTotal() %></td>
                        <td class="col-link"></td>
                    </tr>
                <%
                            }
                %>
                    <tr class="row-employee">
                        <td class="col-name indent-emp"><%= h(row.getEmployeeName()) %></td>
                        <td class="col-count"><%= row.getFaultCount() %></td>
                        <td class="col-link">
                            <a class="detail-link"
                               href="<%= request.getContextPath() %>/FaultCountDetailServlet?year=<%= year %>&employeeId=<%= h(row.getEmployeeId()) %>">＞</a>
                        </td>
                    </tr>
                <%
                            prevCompany = companyKey;
                            prevDept = deptKey;
                        }
                    } else {
                %>
                    <tr class="row-employee">
                        <td colspan="3" style="text-align:center;">データはありません。</td>
                    </tr>
                <%
                    }
                %>
                </tbody>
            </table>
        </div>

        <div class="pager-line">
            <% if (pageNo > 1) { %>
                <a href="<%= request.getContextPath() %>/FaultCount_Servlet?year=<%= year %>&companyId=<%= h(companyId) %>&departmentId=<%= h(departmentId) %>&page=<%= pageNo - 1 %>">≪ 前へ</a>
            <% } %>

            <% for (int p = 1; p <= totalPages; p++) { %>
                <% if (p == pageNo) { %>
                    <span class="current">[<%= p %>]</span>
                <% } else { %>
                    <a href="<%= request.getContextPath() %>/FaultCount_Servlet?year=<%= year %>&companyId=<%= h(companyId) %>&departmentId=<%= h(departmentId) %>&page=<%= p %>"><%= p %></a>
                <% } %>
            <% } %>

            <% if (pageNo < totalPages) { %>
                <a href="<%= request.getContextPath() %>/FaultCount_Servlet?year=<%= year %>&companyId=<%= h(companyId) %>&departmentId=<%= h(departmentId) %>&page=<%= pageNo + 1 %>">次へ ≫</a>
            <% } %>
        </div>

        <div class="excel-box">
            <form action="<%= request.getContextPath() %>/FaultCountExcel_Servlet" method="get">
                <input type="hidden" name="year" value="<%= year %>">
                <input type="hidden" name="companyId" value="<%= h(companyId) %>">
                <input type="hidden" name="departmentId" value="<%= h(departmentId) %>">
                <input type="hidden" name="page" value="<%= pageNo %>">
                <button type="submit" class="btn-primary">Excel出力</button>
            </form>
        </div>

    </div>
</div>

<div class="footer">
    <div class="footer-inner">
        <a class="btn-soft" href="<%= request.getContextPath() %>/Menu_Servlet">メニューに戻る</a>
    </div>
</div>

<!-- 検索モーダル -->
<div id="modal" class="modal">
    <div class="modal-box modal-box-sm">
        <div class="modal-header">検索</div>

        <form id="searchForm" action="<%= request.getContextPath() %>/FaultCount_Servlet" method="get">
            <input type="hidden" name="year" value="<%= year %>">
            <input type="hidden" name="page" value="1">

            <div class="modal-body">
                <div class="form-row">
                    <label>会社名</label>
                    <div class="colon">：</div>
                    <select name="companyId" id="companyId">
                        <option value="">--選択--</option>
                        <% if (companyOptions != null) {
                               for (String[] c : companyOptions) { %>
                            <option value="<%= h(c[0]) %>" <%= c[0].equals(companyId) ? "selected" : "" %>>
                                <%= h(c[1]) %>
                            </option>
                        <%     }
                           } %>
                    </select>
                </div>

                <div class="form-row">
                    <label>部署名</label>
                    <div class="colon">：</div>
                    <select name="departmentId" id="departmentId">
                        <option value="">--選択--</option>
                    </select>
                </div>
            </div>

            <div class="modal-footer">
                <button type="button" class="btn-soft" onclick="closeModal()">戻る</button>

                <div class="modal-footer-right">
                    <button type="button" class="btn-soft" onclick="resetForm()">リセット</button>
                    <button type="submit" class="btn-soft">フィルター適用</button>
                </div>
            </div>
        </form>
    </div>
</div>

<script>
    const departmentMaster = [
    <% if (departmentOptions != null) {
         for (int i = 0; i < departmentOptions.size(); i++) {
             String[] d = departmentOptions.get(i); %>
        {
            companyId: "<%= h(d[0]) %>",
            departmentId: "<%= h(d[1]) %>",
            departmentName: "<%= h(d[2]) %>"
        }<%= (i < departmentOptions.size() - 1) ? "," : "" %>
    <%   }
       } %>
    ];

    function openModal() {
        document.getElementById("modal").style.display = "flex";
    }

    function closeModal() {
        document.getElementById("modal").style.display = "none";
    }

    function resetSelect(select, placeholder) {
        if (!select) return;
        select.innerHTML = "";
        const opt = document.createElement("option");
        opt.value = "";
        opt.textContent = placeholder || "--選択--";
        select.appendChild(opt);
    }

    function fillDepartmentOptions(companyId, selectedDepartmentId) {
        const departmentSelect = document.getElementById("departmentId");
        resetSelect(departmentSelect, "--選択--");

        if (!companyId) {
            return;
        }

        const filtered = departmentMaster.filter(function(d) {
            return d.companyId === companyId;
        });

        filtered.forEach(function(d) {
            const opt = document.createElement("option");
            opt.value = d.departmentId;
            opt.textContent = d.departmentName;
            if (selectedDepartmentId && d.departmentId === selectedDepartmentId) {
                opt.selected = true;
            }
            departmentSelect.appendChild(opt);
        });
    }

    function resetForm() {
        document.getElementById("companyId").value = "";
        fillDepartmentOptions("", "");
    }

    document.addEventListener("DOMContentLoaded", function() {
        const companySelect = document.getElementById("companyId");
        const selectedCompanyId = "<%= h(companyId) %>";
        const selectedDepartmentId = "<%= h(departmentId) %>";

        fillDepartmentOptions(selectedCompanyId, selectedDepartmentId);

        if (companySelect) {
            companySelect.addEventListener("change", function() {
                fillDepartmentOptions(this.value, "");
            });
        }
    });
</script>

</body>
</html>
