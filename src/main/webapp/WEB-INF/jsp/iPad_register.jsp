
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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

    String csvMessage = (String) request.getAttribute("csvMessage");
    String csvError   = (String) request.getAttribute("csvError");

    String assetNumber   = form.getAssetNumber();
    String serialNumber  = form.getSerialNumber();
    String innoHin       = form.getInnoHin();
    String tel           = form.getTel();
    String rentalCompany = form.getRentalCompany();
    String udid          = form.getUdid();
    String macAddress    = form.getMacAddress();

    String contractDate =
        (form.getContractDate() != null) ? form.getContractDate().toString() : "";
    String contractPeriod =
        (form.getContractPeriod() != null) ? form.getContractPeriod().toString() : "";
    String tanka =
        (form.getTanka() != null) ? form.getTanka().stripTrailingZeros().toPlainString() : "";

    boolean openCsvModal = (csvMessage != null || csvError != null);
%>

<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>端末情報 登録フォーム</title>
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
            grid-template-columns: 118px 1fr 118px 1fr;
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

        /* ← ここを base.css のボタン色と共通化 */
        .cell.label{
            background: var(--navy);
            color:#ffffff;
            display:flex;
            align-items:center;
            justify-content:center;
            font-weight:700;
            padding:0 .5rem;
            text-align:center;
            line-height:1.2;
            white-space:nowrap;
        }

        .cell.input{
            background:rgb(255, 255, 255);
            display:flex;
            align-items:center;
            padding:4px 10px;
        }

        .cell.input input{
            width:100%;
            height:31px;
            padding:0 .55rem;
            border:1px solid #7e7e7e;
            background:#ffffff;
            font-size:14px;
            box-sizing:border-box;
        }

        .cell.input input:focus{
            outline:2px solid #9cc3ff;
            border-color:#6aa0ee;
        }

        .span3{
            grid-column: span 3;
        }

        .required{
            color:#ff2b2b;
            margin-left:2px;
            font-weight:700;
        }

        .actions-row{
            display:flex;
            justify-content:space-between;
            align-items:center;
            margin-top:18px;
            gap:12px;
        }

        .footer-actions{
            margin:18px 0 0 0;
            text-align:left;
        }

        .actions-row .btn,
        .actions-row .btn-soft,
        .actions-row .btn-primary{
            min-width:124px;
        }

        .note ul{
            margin:0;
            padding-left:1.2rem;
        }

        .note p{
            margin:0;
        }

@media (max-width: 700px){
    .modal{
        width: min(96vw, 620px);
    }

    .modal-body{
        padding: 24px 20px 28px 20px;
        min-height: auto;
    }

    .modal-actions{
        flex-direction: column;
        align-items: stretch;
    }

    .modal-actions .btn-soft,
    .modal-actions .btn-primary,
    .modal-actions .btn-soft.small{
        width: 100%;
        min-width: auto;
    }
}


        @media (max-width: 900px){
            .form-grid{
                grid-template-columns: 1fr;
            }

            .span3{
                grid-column: auto;
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

            .modal-actions{
                flex-direction:column;
            }

            .modal-actions .btn,
            .modal-actions .btn-soft,
            .modal-actions .btn-primary{
                width:100%;
            }
        }
    </style>
</head>
<body>
    <header class="topbar">
        <div class="topbar__inner">
            <div class="topbar__title">端末情報 登録フォーム</div>
        </div>
    </header>

    <main class="container">
        <section class="card">

            <% if (message != null) { %>
                <div class="note success">
                    <p><%= h(message) %></p>
                </div>
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

            <form method="post" action="<%= request.getContextPath() %>/IPad_register" novalidate>
                <div class="form-grid">
                    <label class="cell label" for="assetNo">資産番号</label>
                    <div class="cell input span3">
                        <input
                            id="assetNo"
                            name="assetNo"
                            type="text"
                            maxlength="100"
                            value="<%= h(assetNumber) %>"
                            autocomplete="off">
                    </div>

                    <label class="cell label" for="serialNo">シリアル番号<span class="required">(*)</span></label>
                    <div class="cell input span3">
                        <input
                            id="serialNo"
                            name="serialNo"
                            type="text"
                            maxlength="100"
                            value="<%= h(serialNumber) %>"
                            autocomplete="off">
                    </div>

                    <label class="cell label" for="indexNo">イノテックス品番</label>
                    <div class="cell input span3">
                        <input
                            id="indexNo"
                            name="indexNo"
                            type="text"
                            maxlength="100"
                            value="<%= h(innoHin) %>"
                            autocomplete="off">
                    </div>

                    <label class="cell label" for="phoneNo">端末電話番号</label>
                    <div class="cell input span3">
                        <input
                            id="phoneNo"
                            name="phoneNo"
                            type="text"
                            maxlength="50"
                            value="<%= h(tel) %>"
                            autocomplete="off">
                    </div>

                    <label class="cell label" for="contractDate">契約日<span class="required">(*)</span></label>
                    <div class="cell input">
                        <input
                            id="contractDate"
                            name="contractDate"
                            type="date"
                            value="<%= h(contractDate) %>">
                    </div>

                    <label class="cell label" for="expiryDate">契約満了日<span class="required">(*)</span></label>
                    <div class="cell input">
                        <input
                            id="expiryDate"
                            name="expiryDate"
                            type="date"
                            value="<%= h(contractPeriod) %>">
                    </div>

                    <label class="cell label" for="rentalCompany">レンタル会社</label>
                    <div class="cell input span3">
                        <input
                            id="rentalCompany"
                            name="rentalCompany"
                            type="text"
                            maxlength="100"
                            value="<%= h(rentalCompany) %>"
                            autocomplete="off">
                    </div>

                    <label class="cell label" for="unitPrice">単価(円)<span class="required">(*)</span></label>
                    <div class="cell input span3">
                        <input
                            id="unitPrice"
                            name="unitPrice"
                            type="text"
                            inputmode="numeric"
                            maxlength="20"
                            value="<%= h(tanka) %>"
                            placeholder="例）120000"
                            autocomplete="off">
                    </div>

                    <label class="cell label" for="udid">UDID</label>
                    <div class="cell input span3">
                        <input
                            id="udid"
                            name="udid"
                            type="text"
                            maxlength="100"
                            value="<%= h(udid) %>"
                            autocomplete="off">
                    </div>

                    <label class="cell label" for="macAddress">MACアドレス</label>
                    <div class="cell input span3">
                        <input
                            id="macAddress"
                            name="macAddress"
                            type="text"
                            maxlength="100"
                            value="<%= h(macAddress) %>"
                            autocomplete="off">
                    </div>
                </div>

                <div class="actions-row">
                    <button class="btn btn-primary" type="submit">登録</button>
                    <button
                        class="btn btn-primary"
                        type="button"
                        id="openCsvModalBtn"
                        aria-haspopup="dialog"
                        aria-controls="csvModal">
                        CSV一括登録
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

 

<!-- CSV モーダル -->
<div id="csvBackdrop" class="modal" aria-hidden="true">
    <div class="modal-box modal-box-md" role="dialog" aria-modal="true" aria-labelledby="csvModalTitle">

        <div class="modal-header" id="csvModalTitle">CSV一括登録</div>

        <form method="post"
              action="<%= request.getContextPath() %>/IPad_CSV_register"
              enctype="multipart/form-data"
              id="csvForm">

            <div class="modal-body modal-body-gray">
                <div class="file-row">
                    <label class="file-select-btn" for="csvFile">ファイルの選択</label>
                    <input id="csvFile" name="file" type="file" accept=".csv" required style="display:none;">
                </div>

                <div class="filename-wrap">
                    アップロードファイル名：
                    <span class="filename" id="csvFilename"></span>
                </div>

                <% if (csvMessage != null) { %>
                    <div class="note success" style="margin-top:20px;">
                        <p><%= h(csvMessage) %></p>
                    </div>
                <% } %>

                <% if (csvError != null) { %>
                    <div class="note error" style="margin-top:20px;">
                        <p><%= h(csvError) %></p>
                    </div>
                <% } %>
            </div>

            <div class="modal-footer modal-footer-between">
                <button type="button" class="btn-soft small" id="closeCsvModalBtn">戻る</button>

                <a class="btn-soft" href="<%= request.getContextPath() %>/CsvTemplate_Servlet">
                    テンプレダウンロード
                </a>

                <button type="submit" class="btn-soft" id="csvUploadBtn" disabled>
                    アップロード
                </button>
            </div>

        </form>
    </div>
</div>


    <script>
        (function(){
            const openBtn   = document.getElementById('openCsvModalBtn');
            const closeBtn  = document.getElementById('closeCsvModalBtn');
            const backdrop  = document.getElementById('csvBackdrop');
            const fileInput = document.getElementById('csvFile');
            const fileName  = document.getElementById('csvFilename');
            const uploadBtn = document.getElementById('csvUploadBtn');

            function openModal(){
                if (!backdrop) return;
                backdrop.style.display = 'flex';
                backdrop.removeAttribute('aria-hidden');
                document.body.style.overflow = 'hidden';
            }

            function closeModal(){
                if (!backdrop) return;
                backdrop.style.display = 'none';
                backdrop.setAttribute('aria-hidden', 'true');
                document.body.style.overflow = '';
            }

            if (openBtn) {
                openBtn.addEventListener('click', openModal);
            }

            if (closeBtn) {
                closeBtn.addEventListener('click', closeModal);
            }

            if (backdrop) {
                backdrop.addEventListener('click', function(e){
                    if (e.target === backdrop) {
                        closeModal();
                    }
                });
            }

            document.addEventListener('keydown', function(e){
                if (e.key === 'Escape' && backdrop && backdrop.style.display === 'flex') {
                    closeModal();
                }
            });

            if (fileInput) {
                fileInput.addEventListener('change', function(){
                    const f = fileInput.files && fileInput.files[0];
                    fileName.textContent =  f ? f.name : '';
                    uploadBtn.disabled = !f;
                });
            }

            <% if (openCsvModal) { %>
                openModal();
            <% } %>
        })();
    </script>
</body>
</html>
