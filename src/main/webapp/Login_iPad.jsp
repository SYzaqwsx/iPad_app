<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>

<html lang="ja">
<head>
  <meta charset="UTF-8">
  <title>iPad管理台帳　ログイン画面</title>
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <!-- 共通部品（ヘッダー/フォント/テーマ） -->
  <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/base.css?v=20260218">
  <!-- ▼ この画面だけの細かいスタイル（埋め込み） -->
  <style>
    .container.is-login{
      margin: 40px auto 80px;
      display: grid;
      grid-template-columns: 1fr;
      justify-items: center;
    }
    .card{
      background: var(--card);
      border: 1px solid var(--border);
      border-radius: var(--radius);
      padding: 24px 28px 28px;
      width: min(520px, 92vw);
    }
    .login-title{
      display:flex; align-items:center; gap:10px;
      margin: 8px 0 22px;
      font-size:22px; font-weight:700;
    }
    .login-title .lock{ font-size: 26px; }

    /* ラベルと入力欄の間隔をやや広めに */
    .form-label{
      font-size: 12px; color: var(--muted);
      margin: 20px 0 6px;
    }
    /* 下線入力 */
    .underline-input{
      width: 100%;
      font-size: 15px;
      padding: 10px 4px 8px;
      border: none;
      border-bottom: 1px solid #8f98a3;
      background: transparent;
      outline: none;
      transition: border-color .18s ease, box-shadow .18s ease;
    }
    .underline-input::placeholder{ color: #b2b8bf; }
    .underline-input:focus{
      border-bottom-color: var(--navy);
      box-shadow: 0 1px 0 0 var(--navy);
    }
    /* ログインボタンの上の余白を広めに */
    .btn.login{
      width:180px; height:44px;
      margin-top: 44px; /* ← 調整箇所 */
    }

    @media (max-width: 520px){
      .btn.login{ width: 100%; }
    }
  </style>
</head>
<body>
  <!-- ログイン画面は中央寄せヘッダーにしたいので topbar--center を付加 -->
  <header class="topbar topbar--center">
    <div class="topbar__inner">
      <div class="topbar__title">iPad 管理システム</div>
    </div>
  </header>
  <main class="container is-login">
    <section class="card" aria-labelledby="login-title">
      <h1 class="login-title" id="login-title">
        <span class="lock" aria-hidden="true">🔒</span>
        <span>ログイン</span>
      </h1>
      <form method="post" action="<%=request.getContextPath()%>/login" autocomplete="off" novalidate>
        <label class="form-label" for="username">アカウント</label>
        <input id="username" name="username" type="text" class="underline-input" required autocomplete="off" autofocus>

        <label class="form-label" for="password">パスワード</label>
        <input id="password" name="password" type="password" class="underline-input" required autocomplete="off">

        <button type="submit" class="btn login">ログイン</button>
      </form>
    </section>
  </main>
  <!-- 認証NG時はポップアップ -->
  <% if (request.getAttribute("loginError") != null) { %>
  <script>
    alert("アカウントまたは\nパスワードが間違っています");
  </script>
  <% } %>
</body>
</html>
