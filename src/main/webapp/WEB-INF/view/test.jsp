<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>权限测试页面</title>
    <script type="text/javascript" src="<%=request.getContextPath()%>/static/js/jquery-1.11.0.js"></script>
</head>
<body>
    <input id="updateuser" type="button" value="update user"/>
    <input id="deleteuser" type="button" value="delete user"/>
</body>

<script type="text/javascript">

    $(document).ready(function() {
       $('#updateuser').click(function() {
           $.post("<%=request.getContextPath()%>/user/update", function(data) {
               alert(data.msg);
           });
       });
        $('#deleteuser').click(function() {
            $.post("<%=request.getContextPath()%>/user/delete", function(data) {
                alert(data.msg);
            });
        });
    });

</script>

</html>
