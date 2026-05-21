
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

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
    border-collapse:collapse;
    background:#fff;
}

th, td{ white-space: nowrap; }

th{
    background:#083b65;
    color:#fff;
    border:1px solid #fff;
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

td.fixed{ position:sticky; left:0; background:#fff; }
td.fixed2{ position:sticky; left:43px; background:#fff; }

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

.excel-box{ text-align:right; }

/* ===== モーダル ===== */
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
    <button onclick="openModal()">検索</button>
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

<c:forEach var="t" items="${list}" varStatus="st">
<tr>
<td class="fixed">${st.index+1}</td>
<td class="fixed2">${t.assetNumber}</td>
<td>${t.innoHin}</td>
<td>${t.serialNumber}</td>

<td><fmt:formatDate value="${t.contractDate}" pattern="yyyy/MM/dd"/></td>
<td><fmt:formatDate value="${t.contractDate}" pattern="yyyy/MM/dd"/></td>
<td><fmt:formatDate value="${t.terminationDate}" pattern="yyyy/MM/dd"/></td>

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

<a href="TerminalServlet?page=1">&lt;&lt;</a>

<c:if test="${page > 1}">
<a href="TerminalServlet?page=${page-1}">前へ</a>
</c:if>

[ ${page} / ${totalPage} ]

<c:if test="${page < totalPage}">
<a href="TerminalServlet?page=${page+1}">次へ</a>
</c:if>

<a href="TerminalServlet?page=${totalPage}">&gt;&gt;</a>

</div>
</div>

<div class="excel-box">
<form action="ExcelServlet" method="get">
<button class="btn">Excel出力</button>
</form>
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

<!-- モーダル -->
<div id="modal" class="modal">
<div class="modal-box">

<div class="modal-header">検索</div>

<form id="searchForm" action="TerminalServlet" method="get">

<div class="modal-body">

<div class="form-row">
<label>会社名：</label>
<select name="company">
<option value="">--選択--</option>
<c:forEach var="c" items="${companyList}">
<option value="${c}">${c}</option>
</c:forEach>
</select>
</div>

<div class="form-row">
<label>イノテックス品番：</label>
<select id="innoSelect" name="innoHin" multiple>
<c:forEach var="i" items="${innoList}">
<option value="${i}">${i}</option>
</c:forEach>
</select>
</div>

<div class="form-row">
<label>利用者名：</label>
<select id="ownerSelect" name="ownerName" multiple>
<c:forEach var="o" items="${ownerList}">
<option value="${o}">${o}</option>
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
<button type="button" onclick="closeModal()">戻る</button>
<button type="button" onclick="resetForm()">リセット</button>
<button type="submit">検索</button>
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
