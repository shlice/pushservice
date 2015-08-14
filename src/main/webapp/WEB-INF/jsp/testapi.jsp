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

        function testjoin() {
            var obj = {
                type: "JOIN",
                body: {
                    uid: $("form#joinform input[name='uid']").val(),
                    token: $("form#joinform input[name='token']").val(),
                    name: $("form#joinform input[name='name']").val(),
                    dateTime: getDateTime()
                },
                sign: "x123a@s!s(3@41^2!@^4"
            };

            postData(obj);
        }
        function testleave() {
            var obj = {
                type: "LEAVE",
                body: {
                    uid: $("form#leaveform input[name='uid']").val(),
                    token: $("form#leaveform input[name='token']").val(),
                    dateTime: getDateTime()
                },
                sign: "x123a@s!s(3@41^2!@^4"
            };

            postData(obj);
        }
        function testpush() {
            var obj = {
                type: "PUSH",
                body: {
                    uid: $("form#pushform input[name='uid']").val(),
                    //payload: "{\"aps\":{\"alert\":\"Hello,\ue106\ue055\ue415 world!\",\"sound\":\"default\"}}",
                    payload: $("form#pushform input[name='payload']").val(),
                    dateTime: getDateTime()
                },
                sign: "x123a@s!s(3@41^2!@^4"
            };

            postData(obj);
        }
        function testpushmsg() {
            var obj = {
                type: "PUSHMSG",
                body: {
                    uid: $("form#pushmsgform input[name='uid']").val(),
                    msg: $("form#pushmsgform input[name='message']").val(),
                    dateTime: getDateTime()
                },
                sign: "x123a@s!s(3@41^2!@^4"
            };

            postData(obj);
        }
        function testpushbadge() {
            var obj = {
                type: "PUSHBADGE",
                body: {
                    uid: $("form#pushbadgeform input[name='uid']").val(),
                    badge: parseInt($("form#pushbadgeform input[name='badge']").val()),
                    dateTime: getDateTime()
                },
                sign: "x123a@s!s(3@41^2!@^4"
            };

            postData(obj);
        }
        function testkgpush() {
            var obj = {
                type: "KGPUSH",
                body: {
                    uid: $("form#kgpushform input[name='uid']").val(),
                    msg: $("form#kgpushform input[name='msg']").val(),
                    url: $("form#kgpushform input[name='url']").val(),
                    func: $("form#kgpushform input[name='func']").val(),
                    dateTime: getDateTime()
                },
                sign: "x123a@s!s(3@41^2!@^4"
            };

            postData(obj);
        }

        function postData(obj)
        {
            $.ajax({
                url: "api",
                contentType: "application/octet-stream",
                type: "post",
                data: JSON.stringify(obj),
                success: function (data, status) {
                    alert("data: " + data + ",nStatus: " + status);
                }
            });
        }

        function getDateTime()
        {
            var d = new Date(new Date().getTime());
            return d.getFullYear()+"-"+(d.getMonth()+1)+"-"+d.getDate()+" "+d.getHours()+":"+d.getMinutes()+":"+d.getSeconds();
        }
    </script>
</head>
<body>
<h3>Join Test</h3>
<form id="joinform">
    <table>
        <tr>
            <td width="80">uid:</td>
            <td><input name="uid"/></td>
        </tr>
        <tr>
            <td>token:</td>
            <td><input name="token"/></td>
        </tr>
        <tr>
            <td>name:</td>
            <td><input name="name"/></td>
        </tr>
        <tr>
            <td colspan="2"><input type="button" onclick="testjoin()" value="JOIN"/></td>
        </tr>
    </table>
</form>

<h3>Leave Test</h3>
<form id="leaveform">
    <table>
        <tr>
            <td width="80">uid:</td>
            <td><input name="uid"/></td>
        </tr>
        <tr>
            <td>token:</td>
            <td><input name="token"/></td>
        </tr>
        <tr>
            <td colspan="2"><input type="button" onclick="testleave()" value="LEAVE"/></td>
        </tr>
    </table>
</form>

<h3>Push Payload Test</h3>
<form id="pushform">
    <table>
        <tr>
            <td width="80">uid:</td>
            <td><input name="uid"/></td>
        </tr>
        <tr>
            <td>payload:</td>
            <td><input name="payload"/></td>
        </tr>
        <tr>
            <td colspan="2"><input type="button" onclick="testpush()" value="PUSH"/></td>
        </tr>
    </table>
</form>

<h3>Push Msg Test</h3>
<form id="pushmsgform">
    <table>
        <tr>
            <td width="80">uid:</td>
            <td><input name="uid"/></td>
        </tr>
        <tr>
            <td>message:</td>
            <td><input name="message"/></td>
        </tr>
        <tr>
            <td colspan="2"><input type="button" onclick="testpushmsg()" value="PUSHMSG"/></td>
        </tr>
    </table>
</form>

<h3>Push Badge Test</h3>
<form id="pushbadgeform">
    <table>
        <tr>
            <td width="80">uid:</td>
            <td><input name="uid"/></td>
        </tr>
        <tr>
            <td>badge:</td>
            <td><input name="badge"/></td>
        </tr>
        <tr>
            <td colspan="2"><input type="button" onclick="testpushbadge()" value="PUSHBADGE"/></td>
        </tr>
    </table>
</form>

<h3>KGPush Test</h3>
<form id="kgpushform">
    <table>
        <tr>
            <td width="80">uid:</td>
            <td><input name="uid"/></td>
        </tr>
        <tr>
            <td>msg:</td>
            <td><input name="msg"/></td>
        </tr>
        <tr>
            <td>url:</td>
            <td><input name="url"/></td>
        </tr>
        <tr>
            <td>func:</td>
            <td><input name="func"/></td>
        </tr>
        <tr>
            <td colspan="2"><input type="button" onclick="testkgpush()" value="KGPUSH"/></td>
        </tr>
    </table>
</form>
</body>
</html>
