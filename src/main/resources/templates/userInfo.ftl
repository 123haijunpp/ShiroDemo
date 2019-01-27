<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
<h1>userInfo</h1>
<@shiro.hasPermission name="userInfo:view">
    [<@shiro.principal property="name"  />]
</@shiro.hasPermission>
<@shiro.hasRole name="admin">Hello admin!</@shiro.hasRole>
<@shiro.user>退出</@shiro.user>
<@shiro.authenticated>11</@shiro.authenticated>
<p>
    <@shiro.hasPermission name="userInfo:add">
        增加
    </@shiro.hasPermission>
    <@shiro.hasPermission name="userInfo:del">
        删除
    </@shiro.hasPermission>
</p>
</body>
</html>