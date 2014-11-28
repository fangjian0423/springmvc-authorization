<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>权限测试页面</title>
    <script type="text/javascript" src="<%=request.getContextPath()%>/static/js/jquery-1.11.0.js"></script>
</head>
<body>
    <input id="updateuser" type="button" value="update user"/>
    <input id="deleteuser" type="button" value="delete user"/>
    <input id="batchdeleteuser" type="button" value="batch delete user"/>
    <input id="batchupdateuser" type="button" value="batch update user"/>
    <hr/>
    <a href="<%=request.getContextPath()%>/user/add">add user</a>
</body>

<script type="text/javascript">

    $(document).ready(function() {
       $('#updateuser').click(function() {
           $.post("<%=request.getContextPath()%>/user/update", function(data) {
               alert(data.msg);
           }).error(function() {
               alert('system error');
           });
       });
        $('#deleteuser').click(function() {
            $.post("<%=request.getContextPath()%>/user/delete", function(data) {
                alert(data.msg);
            }).error(function() {
                alert('system error');
            });
        });
        $('#batchdeleteuser').click(function() {
            $.post("<%=request.getContextPath()%>/user/batchDelete", function(data) {
                alert(data.msg);
            }).error(function() {
                alert('system error');
            });
        });
        $('#batchupdateuser').click(function() {
            $.post("<%=request.getContextPath()%>/user/batchUpdate", function(data) {
                alert(data.msg);
            }).error(function() {
                alert('system error');
            });
        });
    });

</script>

</html>
