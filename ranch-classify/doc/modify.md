# 修改分类信息

请求
- Service Key - ranch.classify.modify
- URI - /classify/modify

参数
- id ID值，必须。
- code 编码，为空表示不修改。
- key 键，为空表示不修改。
- value 值，为空表示不修改。
- name 名称，为空表示不修改。

返回值
```json
{
    "id": "ID值",
    "code": "编码",
    "key": "键",
    "value": "值",
    "name": "名称"
}
```

> [扩展属性](json.md)

> [刷新缓存](refresh.md)

> 后台管理接口，需验证[请求参数签名](https://github.com/heisedebaise/tephra/blob/master/tephra-ctrl/doc/sign.md)。
