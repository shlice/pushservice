<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Api Test</title>
    <script type="text/javascript" src="lib/jquery/jquery.js"></script>
    <script>
        $(document).ready(function () {
        });
    </script>
</head>
<body>
<h3>All Users</h3>
<style>
    table {
        border-collapse: collapse;
        border: 1px solid #ddd;
        background: #fff;
        width: 80%;
    }

    th {
        padding: 2px 5px;
        line-height: 20px;
        vertical-align: top;
        border: 1px solid #ddd;
        background: #f6f6f6;
        text-align: center;
        font-weight: bold;
    }

    td {
        padding: 2px 5px;
        line-height: 20px;
        border: 1px solid #ddd;
    }
</style>
<table border="1">
    <tr>
        <th>uid</th>
        <th>name</th>
        <th>token</th>
        <th>ip</th>
    </tr>
    <c:forEach items="${users}" var="u">
    <tr align=center>
        <td><c:out value="${u.user_id}"/></td>
        <td><c:out value="${u.name}"/></td>
        <td><c:out value="${u.device_token}"/></td>
        <td><c:out value="${u.ip_address}"/></td>
        </c:forEach>
</table>
</body>
</html>
