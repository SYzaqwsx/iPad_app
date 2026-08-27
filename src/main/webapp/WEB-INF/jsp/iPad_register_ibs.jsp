
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="dto.Register_dto" %>
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
    Register_dto form = (Register_dto) request.getAttribute("form");
    if (form == null) {
        form = new Register_dto();
    }

    String message = (String) request.getAttribute("message");
    List<String> errors = (List<String>) request.getAttribute("errors");

    /*
     * Servlet側でrequest属性に設定されたroleIdを取得します。
     *
     * role_id=89：登録可能
     * role_id=99：閲覧のみ
     */
    Integer roleId = (Integer) request.getAttribute("roleId");
    boolean canRegister = roleId != null && roleId.intValue() == 89;

    /*
     * 入力値
     */
    String serialNumber = form.getSerialNumber() == null ? "" : form.getSerialNumber();
    String tel = form.getTel() == null ? "" : form.getTel();
    String rentalCompany = form.getRentalCompany() == null ? "" : form.getRentalCompany();
    String udid = form.getUdid() == null ? "" : form.getUdid();
    String macAddress = form.getMacAddress() == null ? "" : form.getMacAddress();
    String contractDate = form.getContractDate() == null ? "" : form.getContractDate().toString();
    String contractPeriod = form.getContractPeriod() == null ? "" : form.getContractPeriod().toString();
    String tanka = form.getTanka() == null ? "" : form.getTanka()
    .stripTrailingZeros()
    .toPlainString();
%>

<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>IBS専用新規端末登録</title>
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

.container{
    flex:1;
    width:100%;
}

.card{
    background: var(--card);
    border:1px solid var(--border);
    border-radius: var(--radius);
    padding: 1rem;
    margin: 1rem 0;
}

.form-grid{
    display:grid;
    grid-template-columns: 180px 1fr;
    border-top:1px solid #a9a9a9;
    border-left:1px solid #a9a9a9;
    background:#dcdcdc;
}

.cell{
    border-right:1px solid #a9a9a9;
    border-bottom:1px solid #a9a9a9;
    min-height:46px;
    box-sizing:border-box;
}

.cell.label{
    background: var(--navy);
    color:#ffffff;
    display:flex;
    align-items:center;
    justify-content:center;
    font-weight:700;
}

.cell.input{
    background:#ffffff;
    display:flex;
    align-items:center;
    padding:4px 10px;
    min-width:0;
}

.cell.input input{
    width:100%;
    height:31px;
}

.cell.input input:focus{
    outline:2px solid #9cc3ff;
    border-color:#6aa0ee;
}

.cell.input input[readonly]{
    background:#eeeeee;
    color:#555555;
    cursor:default;
}

.span3{
    grid-column:auto;
}

.required{
    color:#ff2b2b;
    margin-left:2px;
    font-weight:700;
}

.actions-row{
    display:flex;
    justify-content:flex-end;
    align-items:center;
    margin-top:18px;
    gap:12px;
}

.actions-row .btn,
.actions-row .btn-soft,
.actions-row .btn-primary{
    min-width:124px;
}

.btn:disabled,
.btn-primary:disabled{
    cursor:not-allowed;
    opacity:.5;
}

.note{
    margin-bottom:16px;
}

.note ul{
    margin:0;
    padding-left:1.2rem;
}

.note p{
    margin:0;
}

@media (max-width: 900px){

    .form-grid{
        grid-template-columns:180px 1fr;
    }

    .actions-row{
        flex-direction:column;
        align-items:stretch;
    }

    .actions-row .btn,
    .actions-row .btn-soft,
    .actions-row .btn-primary{
        width:100%;
    }
}

</style>

</head>

<body>

    <header class="topbar">
        <div class="topbar__inner">
            <div class="topbar__title">
                IBS専用新規端末登録
            </div>
        </div>
    </header>

    <main class="container">
        <section class="card">

            <%-- 登録完了メッセージ --%>
            <% if (message != null) { %>
                <div class="note success">
                    <p><%= h(message) %></p>
                </div>
            <% } %>

            <%-- 入力エラー・登録エラー --%>
            <% if (errors != null && !errors.isEmpty()) { %>
                <div class="note error">
                    <ul>
                        <% for (String error : errors) { %>
                            <li><%= h(error) %></li>
                        <% } %>
                    </ul>
                </div>
            <% } %>

            <%-- role_id=99向けの閲覧専用メッセージ --%>
            <% if (!canRegister) { %>
                <div class="note">
                    <p>
                        この画面は閲覧専用です。
                        IBS専用端末の登録はできません。
                    </p>
                </div>
            <% } %>

             <form method="post" action="<%= request.getContextPath() %>/IPad_register_ibs">

                <div class="form-grid">

                    <%-- シリアル番号 --%>
                    <label class="cell label"
                           for="serialNo">
                        シリアル番号
                        <span class="required">(*)</span>
                    </label>

                    <div class="cell input">
                        <input
                            id="serialNo"
                            name="serialNo"
                            type="text"
                            maxlength="100"
                            value="<%= h(serialNumber) %>"
                            autocomplete="off"
                            <%= canRegister ? "" : "readonly" %>>
                    </div>

                    <%-- 端末電話番号 --%>
                    <label class="cell label"
                           for="phoneNo">
                        端末電話番号
                    </label>

                    <div class="cell input">
                        <input
                            id="phoneNo"
                            name="phoneNo"
                            type="text"
                            inputmode="numeric"
                            maxlength="50"
                            value="<%= h(tel) %>"
                            autocomplete="off"
                            <%= canRegister ? "" : "readonly" %>>
                    </div>

                    <%-- 契約日 --%>
                    <label class="cell label"
                           for="contractDate">
                        契約日
                        <span class="required">(*)</span>
                    </label>

                    <div class="cell input">
                        <input
                            id="contractDate"
                            name="contractDate"
                            type="date"
                            value="<%= h(contractDate) %>"
                            <%= canRegister ? "" : "readonly" %>>
                    </div>

                    <%-- 契約満了日 --%>
                    <label class="cell label"
                           for="expiryDate">
                        契約満了日
                        <span class="required">(*)</span>
                    </label>

                    <div class="cell input">
                        <input
                            id="expiryDate"
                            name="expiryDate"
                            type="date"
                            value="<%= h(contractPeriod) %>"
                            <%= canRegister ? "" : "readonly" %>>
                    </div>

                    <%-- レンタル会社 --%>
                    <label class="cell label"
                           for="rentalCompany">
                        レンタル会社
                    </label>

                    <div class="cell input">
                        <input
                            id="rentalCompany"
                            name="rentalCompany"
                            type="text"
                            maxlength="100"
                            value="<%= h(rentalCompany) %>"
                            autocomplete="off"
                            <%= canRegister ? "" : "readonly" %>>
                    </div>

                    <%-- 単価 --%>
                    <label class="cell label"
                           for="unitPrice">
                        単価(円)
                        <span class="required">(*)</span>
                    </label>

                    <div class="cell input">
                        <input
                            id="unitPrice"
                            name="unitPrice"
                            type="text"
                            inputmode="numeric"
                            maxlength="20"
                            value="<%= h(tanka) %>"
                            placeholder="例）120000"
                            autocomplete="off"
                            <%= canRegister ? "" : "readonly" %>>
                    </div>

                    <%-- UDID --%>
                    <label class="cell label"
                           for="udid">
                        UDID
                    </label>

                    <div class="cell input">
                        <input
                            id="udid"
                            name="udid"
                            type="text"
                            maxlength="100"
                            value="<%= h(udid) %>"
                            autocomplete="off"
                            <%= canRegister ? "" : "readonly" %>>
                    </div>

                    <%-- MACアドレス --%>
                    <label class="cell label"
                           for="macAddress">
                        MACアドレス
                    </label>

                    <div class="cell input">
                        <input
                            id="macAddress"
                            name="macAddress"
                            type="text"
                            maxlength="100"
                            value="<%= h(macAddress) %>"
                            autocomplete="off"
                            <%= canRegister ? "" : "readonly" %>>
                    </div>

                </div>

                <div class="actions-row">
                    <button
                        class="btn btn-primary"
                        type="submit"
                        <%= canRegister ? "" : "disabled" %>>
                        登録
                    </button>
                </div>

            </form>
        </section>
    </main>

    <!-- フッター -->
    <div class="footer">
        <div class="footer-inner">
            <a class="btn-soft" href="<%= request.getContextPath() %>/Menu_Servlet">
                メニューに戻る
            </a>
        </div>
    </div>

</body>
</html>