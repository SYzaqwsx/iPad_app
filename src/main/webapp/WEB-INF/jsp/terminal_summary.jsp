
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>端末集計結果</title>
    <link rel="stylesheet" href="<c:url value='/static/css/base.css'     .card {
            background: var(--card);
            border: 1px solid var(--border);
            padding: 24px 28px;
            min-height: 420px;
            position: relative;
        }

        .radio-group {
            margin-top: 24px;
            display: flex;
            flex-direction: column;
            gap: 16px;
            font-size: 16px;
        }

        .actions {
            position: absolute;
            right: 24px;
            bottom: 24px;
        }

        .footer-bar {
            background: var(--navy);
            padding: 14px 24px;
        }
    </style>
</head>
<body>

<!-- ===== ヘッダー ===== -->
<header class="topbar">
    <div class="topbar__inner">
        <div class="topbar__title">端末集計結果</div>
    </div>
</header>

<!-- ===== メイン ===== -->
<main class="container">
    <div class="card">

        <form method="post" action="<c:url value='/io-group">
                <label>
                    <input type="radio" name="outputType" value="INNO_SUM" checked>
                    イノテックス品番別合計金額
                </label>

                <label>
                    <input type="radio" name="outputType" value="OTHER">
                    その他の帳票
                </label>
            </div>

            <div class="actions">
                <button type="submit" class="btn">Excel出力</button>
            </div>
        </form>

    </div>
</main>

<!-- ===== フッター（戻る） ===== -->
<footer class="footer-bar">
    <form method="get" action="<c:url value='/it" class="btn ghost">メニューに戻る</button>
    </form>
</footer>

</body>
</html>
