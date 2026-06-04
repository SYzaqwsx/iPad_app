
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<!DOCTYPE html>
<html lang="ja">
<head>

<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/base.css?v=20260218">

<!-- TomSelect -->
<link href="https://cdn.jsdelivr.net/npm/tom-select/dist/css/tom-select.css" rel="stylesheet">
<script src="https://cdn.jsdelivr.net/npm/tom-select/dist/js/tom-select.complete.min.js"></script>

<style>
/* ===== レイアウト ===== */
html, body{
    height:100%;
    margin:0;
}
body{
    display:flex;
    flex-direction:column;
}
.container{
    flex:1;
}

/* ===== テーブル ===== */
.table-wrap{ overflow-x:auto; }

table{
    width:100%;
    min-width:1200px;
    border-collapse:separate;
    border-spacing:0;
    background:#fff;
}



th, td{
    white-space: nowrap;
    border:1px solid #ccc;   /* ← 統一 */
}

th{
    background:#083b65;
    color:#fff;
    padding:6px;
    position:sticky;
    top:0;
    z-index:5;
}

td{
    border:1px solid #ccc;
    text-align:center;
    padding:6px;
}

/* 固定列 */
th.fixed{ left:0; z-index:6;}
th.fixed2{ left:43px; z-index:6;}
td.fixed{
  position:sticky;
  left:0;
  background:#fff;
  border-right:1px solid #ccc;
  border-bottom:1px solid #ccc;
  z-index:3;
}
td.fixed2{
  position:sticky;
  left:43px;
  background:#fff;
  border-right:1px solid #ccc;
  border-bottom:1px solid #ccc;
  z-index:3;
}


/* ===== ページネーション ===== */
.pager-area{
    display:flex;
    justify-content:space-between;
    align-items:center;
    margin-top:15px;
}

.pager{
    flex:1;
    text-align:center;
}

.pager a{ margin:0 5px; }

.excel-box{
    display:flex;
    justify-content:flex-end;
}


/* ===== 検索モーダル ===== */
.modal{
    display:none;
    position:fixed;
    inset:0;
    background:rgba(0,0,0,0.4);
    align-items:center;
    justify-content:center;
    z-index:9999;
}

.modal-box{
    width:520px;
    background:#fff;
}

.modal-header{
    background:#083b65;
    color:#fff;
    padding:10px;
}

.modal-body{ padding:20px; }


.modal-footer{
  background:#083b65;
  padding:15px;
  text-align:center;

  display:flex;
  justify-content:center;
  gap:17%;
}

/* 入力フォーム */
.form-row{
    display:flex;
    align-items:center;
    margin-bottom:12px;
}

.form-row label{
    width:160px;
    font-weight:bold;
}


/* ===== トグルスイッチ ===== */
.switch {
  position: relative;
  display: inline-block;
  width: 46px;
  height: 24px;
}

.switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

.form-row label.switch{
    width:46px;
}

.slider {
  position: absolute;
  cursor: pointer;
  inset: 0;
  background-color: #ccc;
  transition: .3s;
  border-radius: 24px;
}

.slider:before {
  position: absolute;
  content: "";
  height: 18px;
  width: 18px;
  left: 3px;
  bottom: 3px;
  background-color: white;
  transition: .3s;
  border-radius: 50%;
}

/* ON状態 */
.switch input:checked + .slider {
  background-color: #083b65;
}

.switch input:checked + .slider:before {
  transform: translateX(22px);
}

/* 解約済みレコードの背景はグレーにする */
.terminated {
    background-color:#e0e0e0;
}

.terminated td.fixed,
.terminated td.fixed2{
    background-color:#e0e0e0;
}

</style>

<script>
function openModal(){
    document.getElementById("modal").style.display="flex";
}
function closeModal(){
    document.getElementById("modal").style.display="none";
}
function resetForm(){
    document.getElementById("searchForm").reset();
}
</script>
</head>

<body>
<header class="topbar">
  <div class="topbar__inner">
    <div class="topbar__title">iPad台帳システム</div>
    <button class="btn-soft" onclick="openModal()">検索</button>
  </div>
</header>

<div class="container">
<div class="table-wrap">
<table>
<tr>
<th class="fixed">項番</th>
<th class="fixed2">資産番号</th>
<th>イノテックス品番</th>
<th>シリアル番号</th>
<th>契約日</th>
<th>契約満了日</th>
<th>解約日</th>
<th>単価</th>
<th>会社名</th>
<th>部門名</th>
<th>使用者</th>
<th>配布日</th>
<th>詳細</th>
</tr>

<c:set var="now" value="<%= new java.util.Date() %>" />
<c:set var="limitDate" value="<%= new java.util.Date(System.currentTimeMillis() + 90L*24*60*60*1000) %>" />

<c:forEach var="t" items="${list}" varStatus="st">
<tr class="${t.terminationDate != null ? 'terminated' : ''}">
<td class="fixed">${st.index+1}</td>
<td class="fixed2">${t.assetNumber}</td>
<td>${t.innoHin}</td>
<td>${t.serialNumber}</td>
<td><fmt:formatDate value="${t.contractDate}" pattern="yyyy/MM/dd"/></td>

<td>
<c:choose>
    <c:when test="${t.contractPeriod != null && t.contractPeriod <= limitDate}">
        <span style="color:red;">
            <fmt:formatDate value="${t.contractPeriod}" pattern="yyyy/MM/dd"/>
        </span>
    </c:when>
    <c:otherwise>
        <fmt:formatDate value="${t.contractPeriod}" pattern="yyyy/MM/dd"/>
    </c:otherwise>
</c:choose>
</td>
<td>
    <fmt:formatDate value="${t.terminationDate}" pattern="yyyy/MM/dd"/>
</td>
<td>${t.tanka}</td>
<td>${t.companyName}</td>
<td>${t.departmentName}</td>
<td>${t.ownerName}</td>
<td><fmt:formatDate value="${t.distributionDate}" pattern="yyyy/MM/dd"/></td>
<td><a href="detail?id=${t.id}">＞</a></td>
</tr>
</c:forEach>
</table>
</div>

<div class="pager-area">
<div class="pager">

[ ${page} / ${totalPage} ]
<c:if test="${totalPage > 1}">

<c:set var="query">
&company=${param.company}
<c:forEach var="i" items="${paramValues.innoHin}">&innoHin=${i}</c:forEach>
<c:forEach var="o" items="${paramValues.ownerName}">&ownerName=${o}</c:forEach>
<c:if test="${param.includeTerminated != null}">&includeTerminated=on</c:if>
<c:if test="${param.stockOnly != null}">&stockOnly=on</c:if>
<c:if test="${param.replacementTarget != null}">&replacementTarget=on</c:if>
</c:set>

    <!-- 最初 -->
    <c:if test="${page > 1}">
        <a href="TerminalServlet_ibs?page=1${query}">&lt;&lt;</a>
    </c:if>
    <!-- 前 -->
    <c:if test="${page > 1}">
        <a href="TerminalServlet_ibs?page=${page-1}${query}">前へ</a>
    </c:if>
    <!-- 次 -->
    <c:if test="${page < totalPage}">
        <a href="TerminalServlet_ibs?page=${page+1}${query}">次へ</a>
    </c:if>
    <!-- 最後 -->
    <c:if test="${page < totalPage}">
        <a href="TerminalServlet_ibs?page=${totalPage}${query}">&gt;&gt;</a>
    </c:if>
    
</c:if>

<form action="TerminalServlet_ibs" method="get" style="text-align:center;">
    <input type="number" name="page" min="1" max="${totalPage}" 
           style="width:75px;" placeholder="ページ">
    <!-- 条件保持 -->
    <input type="hidden" name="company" value="${param.company}"/>
    <c:forEach var="i" items="${paramValues.innoHin}">
        <input type="hidden" name="innoHin" value="${i}"/>
    </c:forEach>
    <c:forEach var="o" items="${paramValues.ownerName}">
        <input type="hidden" name="ownerName" value="${o}"/>
    </c:forEach>
    <c:if test="${param.includeTerminated != null}">
        <input type="hidden" name="includeTerminated" value="on"/>
    </c:if>
    <c:if test="${param.stockOnly != null}">
        <input type="hidden" name="stockOnly" value="on"/>
    </c:if>
    <c:if test="${param.replacementTarget != null}">
        <input type="hidden" name="replacementTarget" value="on"/>
    </c:if>
    <button type="submit">移動</button>
</form>

</div>
</div>

<div class="excel-box">
<form action="Terminal_Ibs_Excel_Servlet" method="get">
<input type="hidden" name="company" value="${param.company}" />
<c:forEach var="i" items="${paramValues.innoHin}">
  <input type="hidden" name="innoHin" value="${i}" />
</c:forEach>
<c:forEach var="o" items="${paramValues.ownerName}">
  <input type="hidden" name="ownerName" value="${o}" />
</c:forEach>
<c:if test="${param.includeTerminated != null}">
  <input type="hidden" name="includeTerminated" value="on" />
</c:if>
<c:if test="${param.stockOnly != null}">
  <input type="hidden" name="stockOnly" value="on" />
</c:if>
<c:if test="${param.replacementTarget != null}">
  <input type="hidden" name="replacementTarget" value="on" />
</c:if>
<button class="btn">Excel出力</button>
</form>
</div>
</div>
</div>

<!-- フッター -->
<div class="footer">
  <div class="footer-inner">
    <a class="btn-soft" href="<%= request.getContextPath() %>/Menu_Servlet">
      メニューに戻る
    </a>
  </div>
</div>

<!-- 検索モーダル -->
<div id="modal" class="modal">
<div class="modal-box">
<div class="modal-header">検索</div>
<form id="searchForm" action="TerminalServlet_ibs" method="get">
<div class="modal-body">

<div class="form-row">
<label>利用者名：</label>
<select id="ownerSelect" name="ownerName" multiple>
<c:forEach var="o" items="${ownerList}">
<option value="${o}"
<c:if test="${paramValues.ownerName != null 
    && fn:contains(fn:join(paramValues.ownerName, ','), o)}">
selected
</c:if>>
${o}
</option>
</c:forEach>
</select>
</div>

<div class="form-row">
  <label>解約済みも表示：</label>
  <input type="checkbox" name="includeTerminated"
    <c:if test="${param.includeTerminated != null}">checked</c:if>>
</div>

<div class="form-row">
  <label>在庫のみ表示：</label>
  <label class="switch">
    <input type="checkbox" name="stockOnly"
      <c:if test="${param.stockOnly != null}">checked</c:if>>
    <span class="slider"></span>
  </label>
</div>

<div class="form-row">
  <label>定期入替対象：</label>
  <label class="switch">
    <input type="checkbox" name="replacementTarget"
      <c:if test="${param.replacementTarget != null}">checked</c:if>>
    <span class="slider"></span>
  </label>
</div>
</div>

<div class="modal-footer">
  <button type="button" class="btn-soft" onclick="closeModal()">戻る</button>
  <button type="button" class="btn-soft" onclick="resetForm()">リセット</button>
  <button type="submit" class="btn-soft">検索</button>
</div>

</form>
</div>
</div>

<!-- TomSelect 初期化 -->
<script>
document.addEventListener("DOMContentLoaded", function(){
  new TomSelect("#innoSelect", {
    plugins:['remove_button'],
    placeholder:"--選択--",
    searchField:['text'],
    maxOptions:1000,
    hideSelected:false,
    closeAfterSelect:false
  });
  new TomSelect("#ownerSelect", {
    plugins:['remove_button'],
    placeholder:"--選択--",
    searchField:['text'],
    maxOptions:1000,
    hideSelected:false,
    closeAfterSelect:false
  });
});
</script>

</body>
</html>
