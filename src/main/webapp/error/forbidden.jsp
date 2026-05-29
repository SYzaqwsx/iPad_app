
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>アクセスエラー</title>
    <script>
        window.onload = function() {
            alert("権限のない画面にアクセスしました");
            window.location.href = "<%= request.getContextPath() %>/Login_iPad.jsp";
        }
    </script>
</head>
<body>
</body>
</html>
