# 修改当前用户认证密码

请求
- Service Key - ranch.user.password
- URI - /user/password

参数
- old 旧密码，如果尚未设置旧密码则为空。
- new 新密码。
- repeat 重复新密码。

返回值
```text
""
```

> 密码保存时使用“MD5(salt+SHA1(pwd+salt))”以确保密码安全。
