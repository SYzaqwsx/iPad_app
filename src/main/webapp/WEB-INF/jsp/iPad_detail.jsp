
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="dto.Detail_dto" %>
<%@ page import="java.util.List" %>

<%!
    private String h(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
%>

<%
    Detail_dto detail = (Detail_dto) request.getAttribute("detail");
    List<Detail_dto> ownerHistory = (List<Detail_dto>) request.getAttribute("ownerHistory");
    String tab = (String) request.getAttribute("tab");
    if (tab == null || tab.isEmpty()) {
        tab = "terminal";
    }

    String message = (String) request.getAttribute("message");
    List<String> errors = (List<String>) request.getAttribute("errors");
%>
<%
    Boolean companyFixedObj = (Boolean) request.getAttribute( "companyFixed");
    Boolean ownerAddReadOnlyObj = (Boolean) request.getAttribute( "ownerAddReadOnly");
    boolean companyFixed = Boolean.TRUE.equals( companyFixedObj);
    boolean ownerAddReadOnly = Boolean.TRUE.equals( ownerAddReadOnlyObj);
%>

<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>iPad台帳システム - 詳細</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/base.css?v=20260218">

<style>
html, body{
    height:100%;
    margin:0;
}

body{
    min-height:100vh;
    display:flex;
    flex-direction:column;
}

.page-subtitle{
    color:#fff;
    font-size:20px;
    font-weight:700;
    margin-top:4px;
}

.tabs{
    display:flex;
    gap:0;
}

.tab-btn{
    display:inline-block;
    padding:8px 14px;
    background:#f4f4f4;
    border:1px solid #aaa;
    border-bottom:none;
    color:#000;
    text-decoration:none;
    font-size:14px;
}

.tab-btn.active{
    background:#fff;
    font-weight:700;
}

/* タブ切替しても高さが大きく変わらないように */
.panel{
    background:#fff;
    border:1px solid #bcbcbc;
    padding:18px;
    box-sizing:border-box;
}

/* ===== 入力フォーム ===== */
.form-grid{
    width:100%;
    display:grid;
    grid-template-columns: 170px 1fr 170px 1fr;
    border-top:1px solid #999;
    border-left:1px solid #999;
    box-sizing:border-box;
}

.cell{
    border-right:1px solid #999;
    border-bottom:1px solid #999;
    min-height:34px;
    box-sizing:border-box;
}

.cell.label{
    background:var(--navy);
    color:#fff;
    display:flex;
    align-items:center;
    justify-content:center;
    font-weight:700;
    padding:4px 8px;
    text-align:center;
    white-space:nowrap;
}

.cell.input{
    background:#fff;
    padding:4px 8px;
    display:flex;
    align-items:center;
}

.cell.input input,
.cell.input select{
    width:100%;
    height:28px;
    box-sizing:border-box;
}


.stock-input-area{
    display:flex;
    align-items:center;
    justify-content:space-between;
    gap:16px;
}

.stock-check-label{
    display:flex;
    align-items:center;
}

.stock-checkbox{
    width:18px !important;
    height:18px !important;
    margin:0;
}

.stock-date{
    width:180px !important;
    min-width:180px;
}


.cell.input input:focus,
.cell.input select:focus{
    outline:2px solid #9cc3ff;
    border-color:#6aa0ee;
}

/* ===== 編集不可項目 ===== */
.cell.input.readonly-cell{
    background:#e5e5e5;
}

.readonly-input{
    background:#e5e5e5 !important;
    color:#555;
    border:1px solid #b5b5b5;
    cursor:not-allowed;
}

.readonly-input:focus{
    outline:none !important;
    border:1px solid #b5b5b5;
}

.span3{
    grid-column: span 3;
}

/* ===== ボタンエリア ===== */
.actions{
    width:100%;
    margin:20px 0 0;
    display:flex;
    justify-content:space-between;
    align-items:center;
    gap:12px;
    box-sizing:border-box;
}

.actions-right{
    justify-content:flex-end;
}

/* ===== 利用者履歴テーブル ===== */
.history-wrap{
    width:100%;
    overflow-x:auto;
    padding-bottom:8px;
    box-sizing:border-box;
}

.history-table{
    border-collapse:collapse;
    background:#fff;
    min-width:760px;
    width:760px;
}

.history-table th,
.history-table td{
    border:1px solid #aaa;
    padding:6px 8px;
    text-align:center;
    white-space:nowrap;
    box-sizing:border-box;
}

.history-table th{
    background:var(--navy);
    color:#fff;
    font-weight:700;
}

/* 列幅 */
.col-company      { width:180px; }
.col-department   { width:180px; }
.col-owner        { width:140px; }
.col-dist         { width:120px; }
.col-return       { width:120px; }
.col-doc          { width:90px; }
.col-tel          { width:130px; }
.col-longrange    { width:200px; }
.col-fault        { width:95px; }

/* 書類リンク */
.doc-link{
    color:#0066cc;
    text-decoration:underline;
    cursor:pointer;
    background:none;
    border:none;
    padding:0;
    font:inherit;
}

.fault-check{
    transform:scale(1.05);
}

/* ===== フッター ===== */
.footer{
    margin-top:auto;
}

/* ===== 書類モーダル ===== */
.doc-modal{
    display:none;
    position:fixed;
    inset:0;
    background:rgba(0,0,0,0.4);
    align-items:center;
    justify-content:center;
    z-index:9999;
}

.doc-modal-box{
    width:min(92vw, 620px);
    background:#fff;
    border:1px solid #bcbcbc;
    box-shadow:0 8px 24px rgba(0,0,0,.25);
    overflow:hidden;
}

.doc-modal-header{
    background:var(--navy);
    color:#fff;
    padding:14px 18px;
    font-size:18px;
    font-weight:700;
}

.doc-modal-body{
    background:#efefef;
    padding:20px 24px;
}

.doc-section{
    margin-bottom:28px;
}

.doc-section-title{
    font-weight:700;
    font-size:20px;
    margin-bottom:8px;
}

.doc-status{
    margin-bottom:10px;
    font-size:16px;
}

.doc-actions{
    display:flex;
    gap:8px;
    align-items:center;
    margin-bottom:8px;
    flex-wrap:wrap;
}

.doc-file-name{
    font-size:14px;
    color:#333;
}

.doc-modal-footer{
    background:var(--navy);
    padding:14px 18px;
    display:flex;
    justify-content:space-between;
    align-items:center;
    gap:12px;
}

/* ===== iPad詳細画面専用 ===== */
body.detail-page{
    --detail-page-width:1034px;
    --detail-inner-width:760px;
}

/* base.css や既存 .container の幅指定を、この画面だけ上書き */
body.detail-page .container{
    flex:1;
    width:calc(100% - 74px);
    max-width:var(--detail-page-width);
    margin:24px auto;
    padding:0;
    box-sizing:border-box;
}

/* タブの開始位置をパネル左端に合わせる */
body.detail-page .tabs{
    width:100%;
    max-width:var(--detail-page-width);
    margin:0 auto;
}

/* 3タブ共通の外枠サイズ */
body.detail-page .panel{
    width:100%;
    max-width:var(--detail-page-width);
    margin:0 auto;
    min-height:585px;
}

/*
  詳細エリア内の共通幅。
  表、資産番号表示、ボタンをすべてこの中に入れることで端を揃える。
*/
.detail-content{

    width:96%;
    margin:0 auto;

}

/* 資産番号・シリアル番号の表示エリア */
.detail-summary{
    width:100%;
    margin:0 0 8px;
    box-sizing:border-box;
    line-height:1.7;
}

/* detail-content 内では個別に中央寄せしない */
.detail-content .form-grid,
.detail-content .actions,
.detail-content .history-wrap{
    max-width:none;
    margin-left:0;
    margin-right:0;
}

/* 履歴テーブルの登録ボタンもテーブル幅の右端に合わせる */
.detail-content .history-actions{
    margin-top:16px;
}
.stock-grid{
    margin-top:20px;
}

.stock-input-area{
    display:flex;
    align-items:center;
}

.stock-date{
    width:180px !important;
    min-width:180px;
}

.disabled-input {
    background-color: #e9ecef;
    color: #666;
    cursor: not-allowed;
}


.disabled-input {
    background-color: #e9ecef;
    color: #666;
    cursor: not-allowed;
}
/* メッセージ表示も外枠幅に合わせる */
body.detail-page .note{
    width:100%;
    max-width:var(--detail-page-width);
    margin-left:auto;
    margin-right:auto;
    box-sizing:border-box;
}


@media (max-width: 900px){
    body.detail-page .container{
        width:calc(100% - 24px);
        margin:16px auto;
    }

    body.detail-page .panel{
        padding:12px;
    }

    .detail-content{
        max-width:100%;
    }

    .form-grid{
        grid-template-columns:1fr;
    }

    .span3{
        grid-column:auto;
    }
    
.span2{
    grid-column: span 2;
}
    

    .actions{
        flex-direction:column;
        align-items:stretch;
    }

    .actions button{
        width:100%;
    }

    .history-table{
        min-width:760px;
        width:760px;
    }

    .doc-modal-footer{
        flex-direction:column;
        align-items:stretch;
    }

    .doc-modal-footer .btn-soft{
        width:100%;
    }
    
.return-date-input{
    width:130px;
    height:28px;
    box-sizing:border-box;
}

    
}
</style>

<body class="detail-page">
    <header class="topbar">
        <div class="topbar__inner" style="display:block; padding-top:8px;">
            <div class="topbar__title">iPad台帳システム</div>
            <div class="page-subtitle">詳細</div>
        </div>
    </header>

    <main class="container">
        <% if (message != null) { %>
            <div class="note success"><%= h(message) %></div>
        <% } %>
        <% if (errors != null && !errors.isEmpty()) { %>
            <div class="note error">
                <ul>
                    <% for (String e : errors) { %>
                        <li><%= h(e) %></li>
                    <% } %>
                </ul>
            </div>
        <% } %>

        <div class="tabs">
            <a class="tab-btn <%= "terminal".equals(tab) ? "active" : "" %>"
               href="<%= request.getContextPath() %>/IPadDetailServlet?id=<%= detail.getId() %>&amp;tab=terminal">端末情報</a>
            <a class="tab-btn <%= "ownerAdd".equals(tab) ? "active" : "" %>"
               href="<%= request.getContextPath() %>/IPadDetailServlet?id=<%= detail.getId() %>&amp;tab=ownerAdd">利用者を追加</a>
            <a class="tab-btn <%= "ownerHistory".equals(tab) ? "active" : "" %>"
               href="<%= request.getContextPath() %>/IPadDetailServlet?id=<%= detail.getId() %>&amp;tab=ownerHistory">利用者履歴</a>
        </div>

        <div class="panel">
            <% if ("terminal".equals(tab)) { %>
                 <form method="post" action="<%= request.getContextPath() %>/IPadDetailServlet">
<!--                    <input type="hidden" name="action" value="updateTerminal">-->
                    <input type="hidden" name="tab" value="terminal">
                    <input type="hidden" name="id" value="<%= detail.getId() %>">

                    <div class="detail-content">
                        <div class="form-grid">
                            <div class="cell label">資産番号</div>
                            <div class="cell input span3 readonly-cell">
                                <input
                                    type="text"
                                    name="assetNumber"
                                    value="<%= h(detail.getAssetNumber()) %>"
                                    readonly
                                    class="readonly-input">
                            </div>
                            <div class="cell label">シリアル番号</div>
                            <div class="cell input span3 readonly-cell">
                                <input
                                    type="text"
                                    name="serialNumber"
                                    value="<%= h(detail.getSerialNumber()) %>"
                                    readonly
                                    class="readonly-input">
                            </div>
                            <div class="cell label">イノテックス品番</div>
                            <div class="cell input span3">
                                <input
                                    type="text"
                                    name="innoHin"
                                    value="<%= h(detail.getInnoHin()) %>">
                            </div>
                            <div class="cell label">端末電話番号</div>
                            <div class="cell input span3">
                                <input
                                    type="text"
                                    name="tel"
                                    value="<%= h(detail.getTel()) %>">
                            </div>
                            <div class="cell label">契約日</div>
                            <div class="cell input">
                                <input
                                    type="date"
                                    name="contractDate"
                                    value="<%= detail.getContractDate() != null ? detail.getContractDate().toString() : "" %>">
                            </div>
                            <div class="cell label">契約満了日</div>
                            <div class="cell input">
                                <input
                                    type="date"
                                    name="contractPeriod"
                                    value="<%= detail.getContractPeriod() != null ? detail.getContractPeriod().toString() : "" %>">
                            </div>
                            <div class="cell label">レンタル会社</div>
                            <div class="cell input span3">
                                <input
                                    type="text"
                                    name="rentalCompany"
                                    value="<%= h(detail.getRentalCompany()) %>">
                            </div>
                            <div class="cell label">解約日</div>
                            <div class="cell input span3">
                                <input
                                    type="date"
                                    name="terminationDate"
                                    value="<%= detail.getTerminationDate() != null ? detail.getTerminationDate().toString() : "" %>">
                            </div>
                            <div class="cell label">単価</div>
                            <div class="cell input span3">
                                <input
                                    type="text"
                                    name="tanka"
                                    value="<%= detail.getTanka() != null ? detail.getTanka() : "" %>">
                            </div>
                            <div class="cell label">UDID</div>
                            <div class="cell input span3 readonly-cell">
                                <input
                                    type="text"
                                    name="udid"
                                    value="<%= h(detail.getUdid()) %>"
                                    readonly
                                    class="readonly-input">
                            </div>
                            <div class="cell label">MACアドレス</div>
                            <div class="cell input span3 readonly-cell">
                                <input
                                    type="text"
                                    name="macAddress"
                                    value="<%= h(detail.getMacAddress()) %>"
                                    readonly
                                    class="readonly-input">
                            </div>
                        </div>
                        <div class="actions">
                            <button
                                type="submit"
                                name="action"
                                value="deleteTerminal"
                                class="btn-danger"
                                onclick="return confirm('端末情報を削除しますか？');">
                                端末情報削除
                            </button>
                            <button
                                 type="submit"
                                 name="action"
                                 value="updateTerminal"
                                 class="btn btn-primary">
                                 更新
                            </button>
                        </div>
                    </div>
                </form>

            <% } else if ("ownerAdd".equals(tab)) { %>

                <%
                    String selectedCompanyId = request.getParameter("companyId") != null ? request.getParameter("companyId") : "";
                    String selectedDepartmentId = request.getParameter("departmentId") != null ? request.getParameter("departmentId") : "";
                    String selectedEmployeeId = request.getParameter("employeeId") != null ? request.getParameter("employeeId") : "";
                    String selectedDistributionDate = request.getParameter("distributionDate") != null ? request.getParameter("distributionDate") : "";
                    String selectedLongRangeUser = request.getParameter("longRangeUser") != null ? request.getParameter("longRangeUser") : "";
                    String selectedJisysInventoryDate = request.getParameter("jisysInventoryDate") != null ? request.getParameter("jisysInventoryDate") : "";
                %>

                 <form method="post" action="<%= request.getContextPath() %>/IPadDetailServlet" id="ownerAddForm">
                    <input type="hidden" name="action" value="addOwner">
                    <input type="hidden" name="tab" value="ownerAdd">
                    <input type="hidden" name="id" value="<%= detail.getId() %>">
                    <input type="hidden" name="assetNumber" value="<%= h(detail.getAssetNumber()) %>">
                    <input type="hidden" name="serialNumber" value="<%= h(detail.getSerialNumber()) %>">
                    

                    <div class="detail-content">
                        <div class="detail-summary">
                            <div><strong>資産番号　　　　　　 </strong>： <%= h(detail.getAssetNumber()) %></div>
                            <div><strong>シリアル番号　　　　 </strong>： <%= h(detail.getSerialNumber()) %></div>
                        </div>

                        <div class="form-grid">
                        <div class="cell label">会社</div>
                        <div class="cell input span3 <%= companyFixed || ownerAddReadOnly  ? "readonly-cell" : "" %>">
                        <% if (companyFixed) { %>
                        <select
                         id="companyIdDisplay"
                         class="readonly-input"
                         disabled>
                         <% for (String[] c : detail.getCompanyOptions()) { %>
                         <% if ("7000".equals(c[0])) { %>
                         <option value="7000" selected>
                         <%= h(c[1]) %>
                         </option>
                         <% } %>
                         <% } %>
                        </select>
                        <input
                        type="hidden"
                        id="companyId"
                        name="companyId"
                        value="7000">
                        <% } else if (ownerAddReadOnly) { %>
                        <select
                         id="companyId"
                         name="companyId"
                         class="readonly-input"
                         disabled>
                         <option value="">--選択--</option>
                         <% for (String[] c : detail.getCompanyOptions()) { %>
                         <option
                         value="<%= h(c[0]) %>"
                         <%= c[0].equals(selectedCompanyId) ? "selected" : "" %>>
                         <%= h(c[1]) %>
                         </option>
                         <% } %>
                        </select>
                        <% } else { %>
                        <select
                         id="companyId"
                         name="companyId">
                         <option value="">--選択--</option>
                         <% for (String[] c : detail.getCompanyOptions()) { %>
                         <option value="<%= h(c[0]) %>" <%= c[0].equals(selectedCompanyId) ? "selected" : "" %>>
                         <%= h(c[1]) %>
                         </option>
                         <% } %>
                        </select>
                        <% } %>
                        </div>
    <div class="cell label">部署</div>
    <div class="cell input span3">
        <select
        name="departmentId"
        id="departmentId"
        <%= ownerAddReadOnly ? "disabled class=\"readonly-input\"" : "" %>>
        <option value="">--選択--</option>
        </select>
    </div>
    <div class="cell label">使用者</div>
    <div class="cell input span3">
        <select
        name="employeeId"
        id="employeeId"
        <%= ownerAddReadOnly ? "disabled class=\"readonly-input\"" : "" %>>
            <option value="">--選択--</option>
        </select>
    </div>
    <div class="cell label">配付日</div>
    <div class="cell input span3">
        <input
            type="date"
            id="distributionDate"
            name="distributionDate"
            value="<%= h(selectedDistributionDate) %>"
            <%= ownerAddReadOnly ? "disabled class=\"readonly-input\"": "" %>>
    </div>
<div class="cell label"> LongRangeユーザ名</div>
<div class="cell input span3 <%= ownerAddReadOnly ? "readonly-cell" : "" %>">
    <input
        type="text"
        id="longRangeUser"
        name="longRangeUser"
        value="<%= h(selectedLongRangeUser) %>"
        <%= ownerAddReadOnly ? "disabled class=\"readonly-input\"" : "" %>>
</div>
</div>
<div class="form-grid stock-grid">
    <div class="cell label">情シス在庫</div>
    <div class="cell input span3 stock-input-area <%= ownerAddReadOnly ? "readonly-cell" : "" %>">
        <input
            type="date"
            id="jisysInventoryDate"
            name="jisysInventoryDate"
            value="<%= h(selectedJisysInventoryDate) %>"
            class="stock-date<%= ownerAddReadOnly ? " readonly-input" : "" %>"
            <%= ownerAddReadOnly ? "disabled" : "" %>>
    </div>
</div>
<div class="actions actions-right">
    <button
        type="submit"
        class="btn btn-primary"
        <%= ownerAddReadOnly ? "disabled" : "" %>>
        追加
    </button>
    </div>
</div>
    <script>
    document.addEventListener("DOMContentLoaded", function() {

    const companyId = document.getElementById("companyId");
    const departmentId = document.getElementById("departmentId");
    const employeeId = document.getElementById("employeeId");
    const distributionDate = document.getElementById("distributionDate");
    const longRangeUser = document.getElementById("longRangeUser");
    const jisysInventoryDate = document.getElementById("jisysInventoryDate");
    const ownerAddReadOnly = <%= ownerAddReadOnly ? "true" : "false" %>;
    
    const ownerInputs = [
        departmentId,
        employeeId,
        distributionDate,
        longRangeUser
    ];

    function hasOwnerData() {
        return ownerInputs.some(function(el) {
        return el && el.value && el.value.trim() !== "";
        });
    }

    function hasInventoryDate() {
        return jisysInventoryDate.value !== "";
    }

    function updateControlState() {
    	if (ownerAddReadOnly) {
        	ownerInputs.forEach(function(el) {
    	        if (!el) { return; }
    	        el.disabled = true;
    	        el.classList.add( "disabled-input");
    	    });
    	    if (companyId && companyId.type !== "hidden") {
    	        companyId.disabled = true;
    	        companyId.classList.add( "disabled-input");
    	    }
    	    if (jisysInventoryDate) {
    	        jisysInventoryDate.disabled = true;
    	        jisysInventoryDate.classList.add( "disabled-input");
    	    }
    	    return;
    	}

    	if (hasInventoryDate()) {
    	    ownerInputs.forEach(function(el) {
    	        if (!el) {
    	            return;
    	        }
    	        el.disabled = true;
    	        el.classList.add("disabled-input");
    	    });
    	    jisysInventoryDate.disabled = false;
    	    jisysInventoryDate.classList.remove("disabled-input");
    	    return;
    	}

        if (hasOwnerData()) {

            jisysInventoryDate.disabled = true;
            jisysInventoryDate.classList.add("disabled-input");

            ownerInputs.forEach(function(el) {
                el.disabled = false;
                el.classList.remove("disabled-input");
            });

            return;
        }

        ownerInputs.forEach(function(el) {
            el.disabled = false;
            el.classList.remove("disabled-input");
        });

        jisysInventoryDate.disabled = false;
        jisysInventoryDate.classList.remove("disabled-input");
    }

    ownerInputs.forEach(function(el) {
        if (!el) {
            return;
        }
        el.addEventListener( "change", updateControlState);
        el.addEventListener( "input", updateControlState);
    });
    if (jisysInventoryDate) { jisysInventoryDate.addEventListener( "change", updateControlState);
    }

    const form = document.getElementById("ownerAddForm");

    form.addEventListener("submit", function(e) {
    if (ownerAddReadOnly) {
    e.preventDefault();
    return false;
}

        if (jisysInventoryDate.value !== "") {
            const result =
                confirm("情シス在庫として登録しますがよろしいですか？");
            if (!result) {
                e.preventDefault();
            }
        }
    });

    updateControlState();

});
</script>
</form>
            <% } else if ("ownerHistory".equals(tab)) { %>
            <form method="post" action="<%= request.getContextPath() %>/IPadDetailServlet">
                    <input type="hidden" name="action" value="registerFault">
                    <input type="hidden" name="tab" value="ownerHistory">
                    <input type="hidden" name="id" value="<%= detail.getId() %>">

                    <div class="detail-content">
                        <div class="history-wrap">
                            <table class="history-table">
                                <thead>
                                    <tr>
                                        <th class="col-company">会社名</th>
                                        <th class="col-department">部署名</th>
                                        <th class="col-owner">氏名</th>
                                        <th class="col-dist">配付日</th>
                                        <th class="col-return">返却日</th>
                                        <th class="col-doc">書類</th>
                                        <th class="col-tel">電話番号</th>
                                        <th class="col-longrange">LongRangeユーザ名</th>
                                        <th class="col-fault">故障有無</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <% if (ownerHistory != null && !ownerHistory.isEmpty()) { %>
                                        <% for (Detail_dto hDto : ownerHistory) { %>
                                            <%
                                                boolean hasReturnDate = hDto.getReturnDate() != null;
                                                boolean alreadyFault = hDto.isFaultFlag();

                                                String distributionDateStr = hDto.getDistributionDate() != null
                                                        ? hDto.getDistributionDate().toString() : "";
                                            %>
                                            <tr>
                                                <td><%= h(hDto.getCompanyName()) %></td>
                                                <td><%= h(hDto.getDepartmentName()) %></td>
                                                <td><%= h(hDto.getOwnerName()) %></td>
                                                <td><%= h(distributionDateStr) %></td>
                                                
<td>
    <%
        String returnDateStr = hDto.getReturnDate() != null
                ? hDto.getReturnDate().toString() : "";
    %>

    <input type="hidden"
           name="returnTarget"
           value="<%= h(hDto.getEmployeeId()) %>|<%= h(distributionDateStr) %>">
    <input type="hidden"
           name="originalReturnDate"
           value="<%= h(returnDateStr) %>">
    <input type="date"
           name="returnDate"
           value="<%= h(returnDateStr) %>"
           class="return-date-input">
</td>

                                                <td>
                                                    <button type="button"
                                                            class="doc-link"
                                                            onclick="openDocModal(
                                                                '<%= h(hDto.getOwnerName()) %>',
                                                                '<%= h(hDto.getSerialNumber()) %>',
                                                                '<%= h(distributionDateStr) %>',
                                                                '<%= h(hDto.getReceiptPdf()) %>',
                                                                '<%= h(hDto.getReturnPdf()) %>',
                                                                '<%= detail.getId() %>'
                                                            )">
                                                        書類
                                                    </button>
                                                </td>
                                                <td><%= h(hDto.getTel()) %></td>
                                                <td><%= h(hDto.getLongRangeUser()) %></td>
                                                <td>
                                                    <% if (alreadyFault) { %>
                                                        <input type="checkbox"
                                                               class="fault-check"
                                                               checked
                                                               disabled>
                                                    <% } else if (hasReturnDate) { %>
                                                        <input type="checkbox"
                                                               class="fault-check"
                                                               disabled>
                                                    <% } else { %>
                                                        <input type="checkbox"
                                                               class="fault-check"
                                                               name="faultTarget"
                                                               value="<%= h(hDto.getEmployeeId()) %>|<%= h(distributionDateStr) %>">
                                                    <% } %>
                                                </td>
                                            </tr>
                                        <% } %>
                                    <% } else { %>
                                        <tr>
                                            <td colspan="9">履歴はありません。</td>
                                        </tr>
                                    <% } %>
                                </tbody>
                            </table>
                        </div>

                        <div class="actions actions-right history-actions">
                            <button type="submit" class="btn btn-primary">登録</button>
                        </div>
                    </div>
                </form>

            <% } %>
        </div>
    </main>

    <!-- 書類モーダル -->
    <div id="docModal" class="doc-modal" aria-hidden="true">
        <div class="doc-modal-box" role="dialog" aria-modal="true" aria-labelledby="docModalTitle">
            <div class="doc-modal-header" id="docModalTitle">書類</div>

             <form method="post" action="<%= request.getContextPath() %>/OwnerDocumentUploadServlet" id="docUploadForm" enctype="multipart/form-data">
                <input type="hidden" name="id" id="docModalId">
                <input type="hidden" name="tab" value="ownerHistory">
                <input type="hidden" name="serialNumber" id="docSerialNumber">
                <input type="hidden" name="distributionDate" id="docDistributionDate">

                <div class="doc-modal-body">
                    <div class="doc-section">
                        <div class="doc-section-title">受領書</div>
                        <div class="doc-status" id="receiptStatus"></div>
                        <div class="doc-actions">
                            <label class="btn-soft" for="receiptFile" id="receiptLabel">ファイルの選択</label>
                            <input id="receiptFile" name="receiptFile" type="file" accept="application/pdf,.pdf" style="display:none;">
                            <button type="button" class="btn-soft" id="receiptViewBtn" style="display:none;">表示</button>
                            <button type="button" class="btn-soft" id="receiptDeleteBtn" style="display:none;">削除</button>
                        </div>
                        <div class="doc-file-name">選択されたファイル：<span id="receiptFileName"></span></div>
                    </div>

                    <div class="doc-section">
                        <div class="doc-section-title">返却書</div>
                        <div class="doc-status" id="returnStatus"></div>
                        <div class="doc-actions">
                            <label class="btn-soft" for="returnFile" id="returnLabel">ファイルの選択</label>
                            <input id="returnFile" name="returnFile" type="file" accept="application/pdf,.pdf" style="display:none;">
                            <button type="button" class="btn-soft" id="returnViewBtn" style="display:none;">表示</button>
                            <button type="button" class="btn-soft" id="returnDeleteBtn" style="display:none;">削除</button>
                        </div>
                        <div class="doc-file-name">選択されたファイル：<span id="returnFileName"></span></div>
                    </div>
                </div>

                <div class="doc-modal-footer">
                    <button type="button" class="btn-soft" onclick="closeDocModal()">戻る</button>
                    <button type="submit" class="btn-soft">アップロード</button>
                </div>
            </form>
        </div>
    </div>

    <!-- 削除用 hidden form -->
     <form id="docDeleteForm" method="post" action="<%= request.getContextPath() %>/OwnerDocumentDeleteServlet" style="display:none;">
        <input type="hidden" name="id" id="deleteId">
        <input type="hidden" name="tab" value="ownerHistory">
        <input type="hidden" name="kind" id="deleteKind">
        <input type="hidden" name="serialNumber" id="deleteSerialNumber">
        <input type="hidden" name="distributionDate" id="deleteDistributionDate">
    </form>

    <div class="footer">
        <div class="footer-inner">
            <a class="btn-soft" href="<%= request.getContextPath() %>/TerminalServlet">
                一覧に戻る
            </a>
        </div>
    </div>

    <script>
        // ===== 書類モーダル =====
        function openDocModal(ownerName, serialNumber, distributionDate, receiptPdf, returnPdf, detailId){
            const modal = document.getElementById("docModal");
            const title = document.getElementById("docModalTitle");

            document.getElementById("docModalId").value = detailId;
            document.getElementById("docSerialNumber").value = serialNumber || "";
            document.getElementById("docDistributionDate").value = distributionDate || "";

            if (title && ownerName) {
                title.textContent = ownerName + "さんの書類";
            }

            // 受領書
            const receiptStatus = document.getElementById("receiptStatus");
            const receiptLabel = document.getElementById("receiptLabel");
            const receiptViewBtn = document.getElementById("receiptViewBtn");
            const receiptDeleteBtn = document.getElementById("receiptDeleteBtn");
            const receiptFileName = document.getElementById("receiptFileName");

            receiptFileName.textContent = "";

            if (receiptPdf && receiptPdf.trim() !== "") {
                receiptStatus.textContent = "✔ アップロード済";
                receiptLabel.textContent = "差替：ファイルの選択";
                receiptViewBtn.style.display = "inline-flex";
                receiptDeleteBtn.style.display = "inline-flex";

                receiptViewBtn.onclick = function(){
                    window.open(
                        "<%= request.getContextPath() %>/OwnerDocumentViewServlet?kind=receipt"
                        + "&serialNumber=" + encodeURIComponent(serialNumber)
                        + "&distributionDate=" + encodeURIComponent(distributionDate),
                        "_blank"
                    );
                };

                receiptDeleteBtn.onclick = function(){
                    if (confirm("受領書の登録情報を削除しますか？")) {
                        document.getElementById("deleteId").value = detailId;
                        document.getElementById("deleteKind").value = "receipt";
                        document.getElementById("deleteSerialNumber").value = serialNumber;
                        document.getElementById("deleteDistributionDate").value = distributionDate;
                        document.getElementById("docDeleteForm").submit();
                    }
                };
            } else {
                receiptStatus.textContent = "× 未アップロード";
                receiptLabel.textContent = "ファイルの選択";
                receiptViewBtn.style.display = "none";
                receiptDeleteBtn.style.display = "none";
            }

            // 返却書
            const returnStatus = document.getElementById("returnStatus");
            const returnLabel = document.getElementById("returnLabel");
            const returnViewBtn = document.getElementById("returnViewBtn");
            const returnDeleteBtn = document.getElementById("returnDeleteBtn");
            const returnFileName = document.getElementById("returnFileName");

            returnFileName.textContent = "";

            if (returnPdf && returnPdf.trim() !== "") {
                returnStatus.textContent = "✔ アップロード済";
                returnLabel.textContent = "差替：ファイルの選択";
                returnViewBtn.style.display = "inline-flex";
                returnDeleteBtn.style.display = "inline-flex";
                returnViewBtn.onclick = function(){
                    window.open(
                        "<%= request.getContextPath() %>/OwnerDocumentViewServlet?kind=return"
                        + "&serialNumber=" + encodeURIComponent(serialNumber)
                        + "&distributionDate=" + encodeURIComponent(distributionDate),
                        "_blank"
                    );
                };
                returnDeleteBtn.onclick = function(){
                    if (confirm("返却書の登録情報を削除しますか？")) {
                        document.getElementById("deleteId").value = detailId;
                        document.getElementById("deleteKind").value = "return";
                        document.getElementById("deleteSerialNumber").value = serialNumber;
                        document.getElementById("deleteDistributionDate").value = distributionDate;
                        document.getElementById("docDeleteForm").submit();
                    }
                };
            } else {
                returnStatus.textContent = "× 未アップロード";
                returnLabel.textContent = "ファイルの選択";
                returnViewBtn.style.display = "none";
                returnDeleteBtn.style.display = "none";
            }
            modal.style.display = "flex";
            modal.setAttribute("aria-hidden", "false");
            document.body.style.overflow = "hidden";
        }
        function closeDocModal(){
            const modal = document.getElementById("docModal");
            if (modal) {
                modal.style.display = "none";
                modal.setAttribute("aria-hidden", "true");
                document.body.style.overflow = "";
            }
        }
        const receiptFile = document.getElementById("receiptFile");
        if (receiptFile) {
            receiptFile.addEventListener("change", function(){
                validatePdfFile(this, "receiptFileName");
            });
        }
        const returnFile = document.getElementById("returnFile");
        if (returnFile) {
            returnFile.addEventListener("change", function(){
                validatePdfFile(this, "returnFileName");
            });
        }
        function isPdfFile(file){
            if (!file) {
                return true;
            }

            const fileName = file.name || "";
            const fileType = file.type || "";

            return fileName.toLowerCase().endsWith(".pdf")
                && (fileType === "" || fileType === "application/pdf");
        }

        function validatePdfFile(input, fileNameSpanId){
            const file = input.files && input.files[0];

            if (!file) {
                document.getElementById(fileNameSpanId).textContent = "";
                return true;
            }

            if (!isPdfFile(file)) {
                alert("PDFファイルのみ登録できます。");

                input.value = "";
                document.getElementById(fileNameSpanId).textContent = "";

                return false;
            }

            document.getElementById(fileNameSpanId).textContent = file.name;
            return true;
        }

        const docUploadForm = document.getElementById("docUploadForm");
        if (docUploadForm) {
            docUploadForm.addEventListener("submit", function(e){
                const receiptInput = document.getElementById("receiptFile");
                const returnInput = document.getElementById("returnFile");

                if (receiptInput && receiptInput.files && receiptInput.files[0]) {
                    if (!isPdfFile(receiptInput.files[0])) {
                        alert("受領書はPDFファイルのみ登録できます。");
                        e.preventDefault();
                        return false;
                    }
                }

                if (returnInput && returnInput.files && returnInput.files[0]) {
                    if (!isPdfFile(returnInput.files[0])) {
                        alert("返却書はPDFファイルのみ登録できます。");
                        e.preventDefault();
                        return false;
                    }
                }
            });
        }

        document.addEventListener("keydown", function(e){
            const modal = document.getElementById("docModal");
            if (e.key === "Escape" && modal && modal.style.display === "flex") {
                closeDocModal();
            }
        });

        document.addEventListener("click", function(e){
            const modal = document.getElementById("docModal");
            if (modal && e.target === modal) {
                closeDocModal();
            }
        });

        // ===== 連動プルダウン =====
        const departmentMaster = [
        <% if (detail != null && detail.getDepartmentOptions() != null) {
             for (int i = 0; i < detail.getDepartmentOptions().size(); i++) {
                 String[] d = detail.getDepartmentOptions().get(i);
        %>
            {
                companyId: "<%= h(d[0]) %>",
                departmentId: "<%= h(d[1]) %>",
                departmentName: "<%= h(d[2]) %>"
            }<%= (i < detail.getDepartmentOptions().size() - 1) ? "," : "" %>
        <%   }
           } %>
        ];

        const employeeMaster = [
        <% if (detail != null && detail.getEmployeeOptions() != null) {
             for (int i = 0; i < detail.getEmployeeOptions().size(); i++) {
                 String[] e = detail.getEmployeeOptions().get(i);
        %>
            {
                companyId: "<%= h(e[0]) %>",
                departmentId: "<%= h(e[1]) %>",
                employeeId: "<%= h(e[2]) %>",
                employeeName: "<%= h(e[3]) %>"
            }<%= (i < detail.getEmployeeOptions().size() - 1) ? "," : "" %>
        <%   }
           } %>
        ];

        function resetSelect(select, placeholder){
            if (!select) return;

            select.innerHTML = "";

            const opt = document.createElement("option");
            opt.value = "";
            opt.textContent = placeholder || "--選択--";
            select.appendChild(opt);
        }

        function fillDepartmentOptions(companyId, selectedDepartmentId){
            const departmentSelect = document.getElementById("departmentId");
            const employeeSelect = document.getElementById("employeeId");

            resetSelect(departmentSelect, "--選択--");
            resetSelect(employeeSelect, "--選択--");

            if (!companyId) {
                return;
            }

            const filtered = departmentMaster.filter(d => d.companyId === companyId);

            filtered.forEach(d => {
                const opt = document.createElement("option");
                opt.value = d.departmentId;
                opt.textContent = d.departmentName;

                if (selectedDepartmentId && d.departmentId === selectedDepartmentId) {
                    opt.selected = true;
                }

                departmentSelect.appendChild(opt);
            });
        }

        function fillEmployeeOptions(companyId, departmentId, selectedEmployeeId){
            const employeeSelect = document.getElementById("employeeId");

            resetSelect(employeeSelect, "--選択--");

            if (!companyId || !departmentId) {
                return;
            }

            const filtered = employeeMaster.filter(e =>
                e.companyId === companyId && e.departmentId === departmentId
            );

            filtered.forEach(e => {
                const opt = document.createElement("option");
                opt.value = e.employeeId;
                opt.textContent = e.employeeName;

                if (selectedEmployeeId && e.employeeId === selectedEmployeeId) {
                    opt.selected = true;
                }

                employeeSelect.appendChild(opt);
            });
        }

        document.addEventListener("DOMContentLoaded", function(){
            const companySelect = document.getElementById("companyId");
            const departmentSelect = document.getElementById("departmentId");

            const selectedCompanyId = "<%= companyFixed ? "7000" : (request.getParameter("companyId") != null ? h(request.getParameter("companyId")) : "") %>";
            const selectedDepartmentId = "<%= request.getParameter("departmentId") != null ? h(request.getParameter("departmentId")) : "" %>";
            const selectedEmployeeId = "<%= request.getParameter("employeeId") != null ? h(request.getParameter("employeeId")) : "" %>";

            if (companySelect && selectedCompanyId) {
            	companySelect.value = selectedCompanyId;
            	fillDepartmentOptions(
                    	selectedCompanyId,
                    	selectedDepartmentId);
            	if (selectedDepartmentId) {
                	fillEmployeeOptions(
                        selectedCompanyId,
                        selectedDepartmentId,
                        selectedEmployeeId);
            	}
            	}
            
            if (companySelect
                    && companySelect.type !== "hidden") {
                companySelect.addEventListener( "change", function() {
                    fillDepartmentOptions( this.value, "");
                });
            }

            if (departmentSelect) {
                departmentSelect.addEventListener("change", function(){
                    fillEmployeeOptions(
                        document.getElementById("companyId").value,
                        this.value,
                        ""
                    );
                });
            }
        });
    </script>
</body>

</html>
