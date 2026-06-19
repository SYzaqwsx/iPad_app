
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

<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>iPad台帳システム - 詳細</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/base.css?v=20260218">



<style>
 height:100%;    html, body{
        margin:0;
    }

    body{
        min-height:100vh;
        display:flex;
        flex-direction:column;
    }

    .container{
        flex:1;
        max-width:min(90vw, 1180px);
        margin:24px auto;
        padding:0 16px;
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
        margin-bottom:0;
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
        min-height:420px;
        box-sizing:border-box;
    }

    /* 中身の横幅を揃える */
    .tab-inner{
        max-width:760px;
        margin:0 auto;
    }

    .form-grid{
        width:100%;
        max-width:760px;
        margin:0 auto;
        display:grid;
        grid-template-columns: 170px 1fr 170px 1fr;
        border-top:1px solid #999;
        border-left:1px solid #999;
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

    .span3{
        grid-column: span 3;
    }

    .actions{
        max-width:100%;
        margin:20px auto 0;
        display:flex;
        justify-content:space-between;
        gap:12px;
    }

    /* 履歴テーブルは横スクロール */
    .history-wrap{
        max-width:1120px;
        margin:0 auto;
        overflow-x:auto;
        padding-bottom:8px;
    }

    .history-table{
        border-collapse:collapse;
        background:#fff;
        min-width:1450px; /* 右スクロール前提 */
        width:1450px;
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

    .footer{
        margin-top:auto;
    }

    /* 書類モーダル */
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

    @media (max-width: 900px){
        .form-grid{
            grid-template-columns:1fr;
        }

        .span3{
            grid-column:auto;
        }

        .actions{
            flex-direction:column;
        }

        .history-table{
            min-width:1450px;
            width:1450px;
        }

        .doc-modal-footer{
            flex-direction:column;
            align-items:stretch;
        }

        .doc-modal-footer .btn-soft{
            width:100%;
        }
    }
</style>


</head>

<body>
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
               href="<%= request.getContextPath() %>/IPadDetailServlet?id=<%= detail.getId() %>&tab=terminal">端末情報</a>

            <a class="tab-btn <%= "ownerAdd".equals(tab) ? "active" : "" %>"
               href="<%= request.getContextPath() %>/IPadDetailServlet?id=<%= detail.getId() %>&tab=ownerAdd">利用者を追加</a>

            <a class="tab-btn <%= "ownerHistory".equals(tab) ? "active" : "" %>"
               href="<%= request.getContextPath() %>/IPadDetailServlet?id=<%= detail.getId() %>&tab=ownerHistory">利用者履歴</a>
        </div>

        <div class="panel">
            <% if ("terminal".equals(tab)) { %>
                <form method="post" action="<%= request.getContextPath() %>/IPadDetailServlet">
                    <input type="hidden" name="action" value="updateTerminal">
                    <input type="hidden" name="tab" value="terminal">
                    <input type="hidden" name="id" value="<%= detail.getId() %>">

                    <div class="form-grid">
                        <div class="cell label">資産番号</div>
                        <div class="cell input span3"><input type="text" name="assetNumber" value="<%= h(detail.getAssetNumber()) %>"></div>

                        <div class="cell label">シリアル番号</div>
                        <div class="cell input span3"><input type="text" name="serialNumber" value="<%= h(detail.getSerialNumber()) %>"></div>

                        <div class="cell label">イノテックス品番</div>
                        <div class="cell input span3"><input type="text" name="innoHin" value="<%= h(detail.getInnoHin()) %>"></div>

                        <div class="cell label">端末電話番号</div>
                        <div class="cell input span3"><input type="text" name="tel" value="<%= h(detail.getTel()) %>"></div>

                        <div class="cell label">契約日</div>
                        <div class="cell input"><input type="date" name="contractDate" value="<%= detail.getContractDate() != null ? detail.getContractDate().toString() : "" %>"></div>

                        <div class="cell label">契約満了日</div>
                        <div class="cell input"><input type="date" name="contractPeriod" value="<%= detail.getContractPeriod() != null ? detail.getContractPeriod().toString() : "" %>"></div>

                        <div class="cell label">レンタル会社</div>
                        <div class="cell input span3"><input type="text" name="rentalCompany" value="<%= h(detail.getRentalCompany()) %>"></div>

                        <div class="cell label">単価</div>
                        <div class="cell input span3"><input type="text" name="tanka" value="<%= detail.getTanka() != null ? detail.getTanka() : "" %>"></div>

                        <div class="cell label">UDID</div>
                        <div class="cell input span3"><input type="text" name="udid" value="<%= h(detail.getUdid()) %>"></div>

                        <div class="cell label">MACアドレス</div>
                        <div class="cell input span3"><input type="text" name="macAddress" value="<%= h(detail.getMacAddress()) %>"></div>
                    </div>

                    <div class="actions">
                        <button type="submit" name="action" value="deleteTerminal" class="btn-danger"
                                onclick="return confirm('端末情報を削除しますか？');">端末情報削除</button>
                        <button type="submit" class="btn btn-primary">更新</button>
                    </div>
                </form>

            <% } else if ("ownerAdd".equals(tab)) { %>
                <form method="post" action="<%= request.getContextPath() %>/IPadDetailServlet">
                    <input type="hidden" name="action" value="addOwner">
                    <input type="hidden" name="tab" value="ownerAdd">
                    <input type="hidden" name="id" value="<%= detail.getId() %>">

                    <div style="max-width:760px; margin:0 auto 14px;">
                        <div><strong>資産番号　　</strong> ： <%= h(detail.getAssetNumber()) %></div>
                        <div><strong>シリアル番号</strong> ： <%= h(detail.getSerialNumber()) %></div>
                    </div>

                    <div class="form-grid">
                        <div class="cell label">会社</div>
                        <div class="cell input span3">
                            <select name="companyId">
                                <option value="">--選択--</option>
                                <% for (String[] c : detail.getCompanyOptions()) { %>
                                    <option value="<%= h(c[0]) %>"><%= h(c[1]) %></option>
                                <% } %>
                            </select>
                        </div>

                        <div class="cell label">部署</div>
                        <div class="cell input span3">
                            <select name="departmentId">
                                <option value="">--選択--</option>
                                <% for (String[] d : detail.getDepartmentOptions()) { %>
                                    <option value="<%= h(d[0]) %>"><%= h(d[1]) %></option>
                                <% } %>
                            </select>
                        </div>

                        <div class="cell label">使用者</div>
                        <div class="cell input span3">
                            <select name="employeeId">
                                <option value="">--選択--</option>
                                <% if (detail.getEmployeeOptions() != null) { %>
                                    <% for (String[] e : detail.getEmployeeOptions()) { %>
                                        <option value="<%= h(e[0]) %>"><%= h(e[1]) %></option>
                                    <% } %>
                                <% } %>
                            </select>
                        </div>

                        <div class="cell label">配付日</div>
                        <div class="cell input span3"><input type="date" name="distributionDate"></div>

                        <div class="cell label">LongRangeユーザ名</div>
                        <div class="cell input span3"><input type="text" name="longRangeUser"></div>

                        <div class="cell label">返品日</div>
                        <div class="cell input span3"><input type="date" name="returnDate"></div>
                    </div>

                    <div class="actions" style="justify-content:flex-end;">
                        <button type="submit" class="btn btn-primary">追加</button>
                    </div>
                </form>

            <% } else if ("ownerHistory".equals(tab)) { %>
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
                                    <tr>
                                        <td><%= h(hDto.getCompanyName()) %></td>
                                        <td><%= h(hDto.getDepartmentName()) %></td>
                                        <td><%= h(hDto.getOwnerName()) %></td>
                                        <td><%= h(hDto.getDistributionDate() != null ? hDto.getDistributionDate().toString() : "") %></td>
                                        <td><%= h(hDto.getReturnDate() != null ? hDto.getReturnDate().toString() : "") %></td>
                                        <td>
                                            <button type="button"
                                                    class="doc-link"
                                                    onclick="openDocModal(
                                                        '<%= h(hDto.getOwnerName()) %>',
                                                        '<%= h(hDto.getSerialNumber()) %>',
                                                        '<%= h(hDto.getDistributionDate() != null ? hDto.getDistributionDate().toString() : "") %>',
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
                                            <input type="checkbox"
                                                   class="fault-check"
                                                   disabled
                                                   <%= hDto.isFaultFlag() ? "checked" : "" %>>
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

                <div class="actions" style="justify-content:flex-end; margin-top:16px;">
                    <button type="button" class="btn btn-primary">更新</button>
                </div>
            <% } %>
        </div>

        <div style="margin-top:18px;">
            <a class="btn-soft" href="<%= request.getContextPath() %>/Menu_Servlet">メニューに戻る</a>
        </div>
    </main>

    <!-- 書類モーダル -->
    <div id="docModal" class="doc-modal" aria-hidden="true">
        <div class="doc-modal-box" role="dialog" aria-modal="true" aria-labelledby="docModalTitle">
            <div class="doc-modal-header" id="docModalTitle">書類</div>

            <form method="post" action="<%= request.getContextPath() %>/OwnerDocumentUploadServlet" enctype="multipart/form-data">
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
                            <input id="receiptFile" name="receiptFile" type="file" accept=".pdf" style="display:none;">
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
                            <input id="returnFile" name="returnFile" type="file" accept=".pdf" style="display:none;">
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
        <div class="footer-inner"></div>
    </div>

    <script>
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

        document.getElementById("receiptFile").addEventListener("change", function(){
            const f = this.files && this.files[0];
            document.getElementById("receiptFileName").textContent = f ? f.name : "";
        });

        document.getElementById("returnFile").addEventListener("change", function(){
            const f = this.files && this.files[0];
            document.getElementById("returnFileName").textContent = f ? f.name : "";
        });

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
    </script>
</body>
</html>
