<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%@ taglib prefix="c"  uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
    
<!DOCTYPE html>

<html lang="ja">
<head>
  <meta charset="UTF-8">
  <title>iPad 管理システム</title>
  <meta name="viewport" content="width=device-width, initial-scale=1">

  <!-- ▼ 正しい CSS 読み込み -->
  <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/base.css?v=20260218">

  <!-- ▼ この画面専用のCSS -->
  <style>
    .container{ max-width: min(90vw, 1080px); margin: 28px auto; padding: 0 16px; }

    .grid{
      display:grid;
      grid-template-columns: repeat(3, 1fr);
      gap:22px;
    }

    .tile{
      display:flex;
      align-items:center;
      justify-content:center;
      height:84px;
      text-decoration:none;
      color:#fff;
      background: var(--navy);
      font-size:16px;
      font-weight:600;
      border-radius: var(--radius);
      box-shadow:0 4px 16px rgba(0,0,0,0.12);
      transition: filter .12s ease, background .18s ease;
    }
    .tile:hover{ background: var(--navy-dark); filter: brightness(0.98); }

    .empty{ color:#555; text-align:center; margin-top:32px; }

    @media (max-width: 820px){
      .grid{ grid-template-columns: repeat(2, 1fr); }
    }
    @media (max-width: 520px){
      .grid{ grid-template-columns: 1fr; }
    }
  </style>
</head>

<body>
  <!-- ▼ ヘッダー（共通デザイン） -->
  <header class="topbar">
    <div class="topbar__inner">
      <div class="topbar__title">iPad 管理システム</div>

      <div class="user">
        <span><c:out value="${userName}" /></span>
        （権限: <span><c:out value="${roleId}" /></span>）
        &nbsp;|&nbsp;
        <a class="logout" href="${pageContext.request.contextPath}/Login_iPad.jsp">ログアウト</a>
      </div>
    </div>
  </header>

  <div class="container">
    <c:choose>
      <c:when test="${empty menus}">
        <p class="empty">表示可能なメニューがありません。</p>
      </c:when>

      <c:otherwise>
        <div class="grid">
          <c:forEach var="m" items="${menus}">
            <a class="tile" href="${pageContext.request.contextPath}${m.menuUrl}">
              <c:out value="${m.menuName}" />
            </a>
          </c:forEach>
        </div>
      </c:otherwise>
    </c:choose>
  </div>

</body>
</html>
