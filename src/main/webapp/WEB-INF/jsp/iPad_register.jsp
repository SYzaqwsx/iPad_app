<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<html lang="ja">

<html lang="ja">
<head>
  <meta charset="UTF-8">
  <title>端末情報 登録フォーム</title>
  <meta name="viewport" content="width=device-width, initial-scale=1">

  <!-- ▼ 共通部品（必ず <link> で読み込む） -->
  <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/base.css?v=20260218">

  <!-- ▼ この画面だけの細かいスタイル（埋め込み） -->
  <style>
    .card{
      background: var(--card);
      border:1px solid var(--border);
      border-radius: var(--radius);
      padding: clamp(.5rem, .9vw, .7rem);
      margin: clamp(.7rem, 1.4vw, .9rem) 0;
    }

    /* 2列グリッド（左：ラベル/右：入力） */
    .form-grid{
      display:grid;
      grid-template-columns: minmax(9rem, 26%) 1fr;
      gap: clamp(.32rem, .8vw, .5rem);
      align-items:center;
    }
    .cell.label{
      background: var(--navy);
      color:#fff;
      padding:.48rem .6rem;
      border-radius: var(--radius);
      line-height:1.1; white-space:nowrap; display:flex; align-items:center;
    }
    .cell.input input{
      width:100%;
      padding:.48rem .6rem;
      border:1px solid var(--border);
      border-radius: var(--radius);
      background:#fff; min-height:2rem; font-size:1rem;
    }
    .cell.input input:focus{
      outline: 2px solid #9cc3ff;
      border-color:#6aa0ee;
    }

    .actions-row{
      display:flex; justify-content:space-between; align-items:center;
      gap:.6rem; margin-top: 2%; /* 指示の通り +2% 余白 */
    }

    .footer-actions{ margin: .9rem 0; text-align:left; }

    /* CSVモーダル */
    .modal-backdrop{
      position:fixed; inset:0; background:rgba(0,0,0,.45);
      display:none; align-items:center; justify-content:center; z-index:999;
    }
    .modal{
      width:min(92vw, 560px);
      background:#fff; border:1px solid var(--border);
      border-radius:var(--radius); box-shadow:0 8px 28px rgba(0,0,0,.25);
      overflow:hidden;
    }
    .modal-header{
      background:var(--navy); color:#fff;
      padding:.6rem .8rem; font-weight:700;
    }
    .modal-body{ padding:.9rem; }
    .modal-row{ margin:.5rem 0; color:var(--text); }
    .file-row{ display:flex; gap:.6rem; align-items:center; flex-wrap:wrap; }
    .file-row input[type=file]{ font-size:.95rem; }
    .filename{ color:var(--muted); font-size:.9rem; }
    .modal-actions{
      display:flex; gap:.6rem; justify-content:flex-end; padding:.8rem .9rem;
      border-top:1px solid var(--border); background:#fafbfc;
    }

    @media (max-width: 720px){
      .form-grid{ grid-template-columns: 1fr; }
      .actions-row{ flex-direction:column; align-items:stretch; }
      .actions-row .btn{ width:100%; }
      .modal-actions{ flex-direction:column; }
      .modal-actions .btn{ width:100%; }
    }
  </style>
</head>

<body>
  <!-- ヘッダー（ログインと同じ見た目） -->
  <header class="topbar">
    <div class="topbar__inner">
      <div class="topbar__title">端末情報 登録フォーム</div>
    </div>
  </header>

  <main class="container">
    <!-- 単票登録 -->
    <section class="card">
      <!-- ★ 正しい form 開始タグ（POST & action の指定） -->
      <form method="post" action="<%= request.getContextPath() %>/IPad_register" novalidate>
        <div class="form-grid">
          <label class="cell label" for="assetNo">資産番号</label>
          <div class="cell input"><input id="assetNo" name="assetNo" type="text" required maxlength="100" autocomplete="off"></div>

          <label class="cell label" for="indexNo">インデックス品番</label>
          <div class="cell input"><input id="indexNo" name="indexNo" type="text" maxlength="100" autocomplete="off"></div>

          <label class="cell label" for="serialNo">シリアル番号</label>
          <div class="cell input"><input id="serialNo" name="serialNo" type="text" maxlength="100" autocomplete="off"></div>

          <label class="cell label" for="contractDate">契約日</label>
          <div class="cell input"><input id="contractDate" name="contractDate" type="date" pattern="\\d{4}-\\d{2}-\\d{2}" placeholder="yyyy/mm/dd"></div>

          <label class="cell label" for="expiryDate">契約満了日</label>
          <div class="cell input"><input id="expiryDate" name="expiryDate" type="date" pattern="\\d{4}-\\d{2}-\\d{2}" placeholder="yyyy/mm/dd"></div>

          <label class="cell label" for="unitPrice">単価(円)</label>
          <div class="cell input"><input id="unitPrice" name="unitPrice" type="text" inputmode="numeric" pattern="\\d+(\\.\\d{1,2})?" placeholder="例）120000"></div>
        </div>

        <div class="actions-row">
          <button class="btn" type="submit">登録</button>
          <button class="btn" type="button" id="openCsvModalBtn" aria-haspopup="dialog" aria-controls="csvModal">CSV一括登録</button>
        </div>

        <%-- メッセージ --%>
        <%
          String msg = (String) request.getAttribute("message");
          String err = (String) request.getAttribute("error");
          if (msg != null) { %><p class="note success"><%= msg %></p><% }
          if (err != null) { %><p class="note error"><%= err %></p><% }
        %>
      </form>
    </section>

    <div class="footer-actions">
      <!-- ★ 正しいリンク -->
      <a class="btn ghost" href="<%= request.getContextPath() %>/Menu_Servlet">メニューに戻る</a>
    </div>
  </main>

  <!-- CSV モーダルダイアログ -->
  <div class="modal-backdrop" id="csvBackdrop" aria-hidden="true">
    <div class="modal" role="dialog" aria-modal="true" aria-labelledby="csvModalTitle" id="csvModal">
      <div class="modal-header" id="csvModalTitle">CSV一括登録</div>

      <!-- ★ 正しい form（CSVアップロード用、POST & enctype 必須） -->
      <form method="post" action="<%= request.getContextPath() %>/upload-csv" enctype="multipart/form-data" id="csvForm">
        <div class="modal-body">
          <div class="modal-row file-row">
            <label class="btn ghost" for="csvFile">ファイルの選択</label>
            <input id="csvFile" name="file" type="file" accept=".csv" required style="display:none;">
            <span class="filename" id="csvFilename">アップロードファイル名：</span>
          </div>

          <%-- CSVアップロード結果メッセージ --%>
          <%
            String csvMsg = (String) request.getAttribute("csvMessage");
            String csvErr = (String) request.getAttribute("csvError");
            if (csvMsg != null) { %><p class="note success"><%= csvMsg %></p><% }
            if (csvErr != null) { %><p class="note error"><%= csvErr %></p><% }
          %>
        </div>

        <div class="modal-actions">
          <button type="button" class="btn ghost" id="closeCsvModalBtn">戻る</button>
          <!-- ★ 正しいリンク -->
          <a class="btn ghost" href="<%= request.getContextPath() %>/download-csv-template">テンプレダウンロード</a>
          <button type="submit" class="btn" id="csvUploadBtn" disabled>アップロード</button>
        </div>
      </form>
    </div>
  </div>

  <script>
    // モーダルの簡易制御
    (function(){
      const openBtn   = document.getElementById('openCsvModalBtn');
      const closeBtn  = document.getElementById('closeCsvModalBtn');
      const backdrop  = document.getElementById('csvBackdrop');
      const fileInput = document.getElementById('csvFile');
      const fileName  = document.getElementById('csvFilename');
      const uploadBtn = document.getElementById('csvUploadBtn');

      function openModal(){
        if(!backdrop) return;
        backdrop.style.display = 'flex';
        backdrop.removeAttribute('aria-hidden');
        document.body.style.overflow = 'hidden';
        setTimeout(()=>document.querySelector('label[for="csvFile"]')?.focus(), 0);
      }
      function closeModal(){
        if(!backdrop) return;
        backdrop.style.display = 'none';
        backdrop.setAttribute('aria-hidden','true');
        document.body.style.overflow = '';
        openBtn && openBtn.focus();
      }

      openBtn && openBtn.addEventListener('click', openModal);
      closeBtn && closeBtn.addEventListener('click', closeModal);
      backdrop && backdrop.addEventListener('click', (e)=>{ if(e.target === backdrop){ closeModal(); }});
      document.addEventListener('keydown', (e)=>{ if(e.key === 'Escape' && backdrop.style.display === 'flex'){ closeModal(); }});

      fileInput && fileInput.addEventListener('change', ()=>{
        const f = fileInput.files && fileInput.files[0];
        fileName.textContent = 'アップロードファイル名：' + (f ? f.name : '');
        uploadBtn.disabled = !f;
      });
    })();
  </script>
</body>
</html>
