
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="ja">

<head>
<meta charset="UTF-8">
<title>故障台数 詳細</title>
<meta name="viewport" content="width=device-width, initial-scale=1">

<link rel="stylesheet"
      href="<%= request.getContextPath() %>/static/css/base.css?v=20260218">

<style>
html,
body{
    height:100%;
    margin:0;
}

body{
    display:flex;
    flex-direction:column;
}

/* 故障台数画面と合わせる */
.container{
    flex:1;
    width:100%;
    box-sizing:border-box;
    padding:18px 0 0;
}

/* 一覧画面の幅感に寄せる */
.detail-wrap{

    width:100%;
    max-width:780px;
    margin:0 auto;
}

/* サブタイトル */
.page-subtitle{
    color:#fff;
    font-size:20px;
    font-weight:700;
    margin-top:4px;
}

/* タイトル縦並び */
.topbar-title-wrap{
    display:flex;
    flex-direction:column;
}

/* 上部情報 */
.info-area{
    width:100%;
    max-width:780px;
    margin:20px auto;
    font-size:14px;
}

.info-row{
    display:flex;
    align-items:center;
    margin-bottom:6px;
}

.info-label{
    width:70px;
    font-weight:bold;
}

.info-colon{
    width:20px;
}

.info-value{
    min-width:220px;
}

/* 一覧部分 */
.detail-table-wrap{
    width:100%;
    max-width:780px;
    margin:0 auto;
    min-height:190px;
    background:#fff;
    border:1px solid #aaa;
}

.detail-table{
    width:100%;
    border-collapse:collapse;
}

.detail-table th{
    background:#083b65;
    color:#fff;
    border:1px solid #aaa;
    padding:4px 6px;
    font-size:13px;
    text-align:center;
    white-space:nowrap;
}

.detail-table td{
    border:1px solid #aaa;
    padding:4px 6px;
    font-size:13px;
    text-align:center;
    white-space:nowrap;
}

.no-data{
    padding:20px;
    text-align:center;
}

/* ボタン */
.button-area{
    width:100%;
    max-width:1200px;
    margin:18px auto 0;
    display:flex;
    justify-content:space-between;
    align-items:center;
}

.back-area{
    display:flex;
    justify-content:flex-start;
}

.excel-area{
    display:flex;
    justify-content:flex-end;
}

/* フッター */
.footer{
    margin-top:18px;
}
</style>
</head>

<body>

<header class="topbar">
    <div class="topbar__inner">
        <div class="topbar-title-wrap">
            <div class="topbar__title">
                iPad・PC台帳システム
            </div>
            <div class="page-subtitle">
                故障台数
            </div>
        </div>
    </div>
</header>

<div class="container">
    <div class="detail-wrap">

        <div class="info-area">
            <div class="info-row">
                <div class="info-label">会社名</div>
                <div class="info-colon">：</div>
                <div class="info-value">${companyName}</div>
            </div>

            <div class="info-row">
                <div class="info-label">部門名</div>
                <div class="info-colon">：</div>
                <div class="info-value">${departmentName}</div>
            </div>

            <div class="info-row">
                <div class="info-label">社員名</div>
                <div class="info-colon">：</div>
                <div class="info-value">${employeeName}</div>
            </div>
        </div>

        <div class="detail-table-wrap">
            <table class="detail-table">
                <thead>
                    <tr>
                        <th>資産番号</th>
                        <th>イノテックス品番</th>
                        <th>シリアル番号</th>
                        <th>契約日</th>
                        <th>解約日</th>
                        <th>単価(円)</th>
                        <th>配布日</th>
                    </tr>
                </thead>

                <tbody>
                    <c:choose>
                        <c:when test="${empty list}">
                            <tr>
                                <td colspan="7" class="no-data">データはありません</td>
                            </tr>
                        </c:when>

                        <c:otherwise>
                            <c:forEach var="d" items="${list}">
                                <tr>
                                    <td>${d.assetNumber}</td>
                                    <td>${d.innoHin}</td>
                                    <td>${d.serialNumber}</td>
                                    <td>
                                        <fmt:formatDate value="${d.contractDate}" pattern="yyyy/MM/dd"/>
                                    </td>
                                    <td>
                                        <fmt:formatDate value="${d.terminationDate}" pattern="yyyy/MM/dd"/>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${d.tanka != null}">
                                                <fmt:formatNumber value="${d.tanka}" pattern="#,##0"/>
                                            </c:when>
                                            <c:otherwise></c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <fmt:formatDate value="${d.distributionDate}" pattern="yyyy/MM/dd"/>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>

    </div>
</div>


<div class="footer">
    <div class="footer-inner">
        <a class="btn-soft"
           href="${pageContext.request.contextPath}/FaultCount_Servlet?year=${year}&companyId=${companyId}&departmentId=${departmentId}">
            故障台数一覧に戻る
        </a>
    </div>
</div>


</body>
</html>

