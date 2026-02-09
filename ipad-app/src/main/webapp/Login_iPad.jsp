<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>iPad管理台帳　ログイン画面</title>
 
<style>
        :root {
            --brand-blue: #215d91; /* ヘッダー＆ボタン色（画像に近い落ち着いた青） */
            --brand-blue-dark: #1a4c77;
            --text-main: #2b2b2b;
            --muted: #6f6f6f;
            --border: #bfc7cf;
            --bg: #ffffff;
        }
        html, body {
            height: 100%;
            margin: 0;
            background: var(--bg);
            color: var(--text-main);
            font-family: "Meiryo", "メイリオ", sans-serif;
        }

        /* 上部ヘッダー */
        .topbar {
            background: var(--brand-blue);
            color: #fff;
            height: 72px;
            display: flex;
            align-items: center;
            box-shadow: 0 1px 0 rgba(0,0,0,0.25);
        }
        .topbar__inner {
            width: min(1080px, 92vw);
            margin: 0 auto;
            display: flex;
            align-items: center;
            justify-content: center; /* 画像の中央寄せタイトルに合わせる */
        }
        .topbar__title {
            
            font-size: 22px;
            font-weight: 600;
            letter-spacing: 0.08em;

        }

        /* メイン領域（中央に配置） */
        .container {
            width: min(1080px, 92vw);
            margin: 40px auto 80px;
            display: grid;
            grid-template-columns: 1fr;
            justify-items: center;
        }

        /* ログインカード */
        .card {
            width: min(520px, 92vw);
            padding: 24px 28px 32px;
            background: #fff;
        }

        .login-title {
            display: flex;
            align-items: center;
            gap: 10px;
            margin: 8px 0 18px;
            font-size: 22px;
            font-weight: 700;
        }
        .login-title .lock {
            font-size: 26px;
        }

        /* ラベル */
        .form-label {
            font-size: 12px;
            color: var(--muted);
            margin: 16px 0 6px;
        }

        /* 下線のみのテキストボックス */
        .underline-input {
            width: 100%;
            font-size: 15px;
            padding: 10px 4px 8px;
            border: none;
            border-bottom: 1px solid #8f98a3;
            background: transparent;
            outline: none;
            transition: border-color .18s ease, box-shadow .18s ease;
        }
        .underline-input::placeholder {
            color: #b2b8bf;
        }
        .underline-input:focus {
            border-bottom-color: var(--brand-blue);
            box-shadow: 0 1px 0 0 var(--brand-blue);
        }

        /* ログインボタン */
        .btn {
            margin-top: 24px;
            width: 180px;
            height: 44px;
            border: none;
            color: #fff;
            background: var(--brand-blue);
            cursor: pointer;
            border-radius: 3px;
            font-size: 15px;
            letter-spacing: .08em;
            transition: background .18s ease, transform .02s ease;
        }
        .btn:hover { background: var(--brand-blue-dark); }
        .btn:active { transform: translateY(1px); }

        

        /* スモールスクリーン調整 */
        @media (max-width: 520px) {
            .topbar { height: 64px; }
            .login-title { font-size: 20px; }
            .btn { width: 100%; }
        }
    </style>

</head>

<body>
<header class="topbar">
    <div class="topbar__inner">
        <div class="topbar__title">iPad 管理システム</div>
    </div>
</header>

<main class="container">
    <section class="card" aria-labelledby="login-title">
        <h1 class="login-title" id="login-title">
            <span class="lock" aria-hidden="true">🔒</span>
            <span>ログイン</span>
        </h1>

       
       
 
<form method="post" action="<%=request.getContextPath()%>/login" autocomplete="off" novalidate>
            <label class="form-label" for="username">アカウント</label>
            <input id="username" name="username" type="text" class="underline-input" required autocomplete="off">

            <label class="form-label" for="password">パスワード</label>
            <input id="password" name="password" type="password" class="underline-input" required autocomplete="off">

            <button type="submit" class="btn">ログイン</button>

        </form>
        </section>
</main>

<!-- ▼ 認証NG時はポップアップを表示 -->
<% if (request.getAttribute("loginError") != null) { %>
<script>
  alert("アカウントまたは\nパスワードが間違っています");
</script>
<% } %>

</body>
</html>