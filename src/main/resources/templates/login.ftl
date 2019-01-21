<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
错误信息：<h4 style="color: red;">${msg}</h4>
<form action="/shiro/login" method="post">
    <p>账号：<input type="text" name="username" value="admin"/></p>
    <p>密码：<input type="password" name="password" value="123456"/></p>
    <p><input type="checkbox" name="rememberMe" value="true"/> 记住我</p>
    <div><label> 验证码 : <input type="text" name="captcha" placeholder="验证码"/> </label></div>
    <div><img src="kaptcha.jpg"</div>
    <input type="submit" value="登录"/>
    <p>点击 <a href="/shiro/logout">退出登录</a></p>
</form>
</body>
</html>