# 检索当前用户文档信息集

请求
- Service Key - ranch.doc.query-by-author
- URI - /doc/query-by-author

参数
- pageSize 每页显示记录数。
- pageNum 当前显示页数。

返回值
```json
{
    "count": "总记录数",
    "size": "每页显示记录数",
    "number": "当前显示页数",
    "list": [
        {
            "id": "ID值",
            "key": "类型KEY",
            "owner": {},
            "author": {},
            "scoreMin": "最小分值",
            "scoreMax": "最大分值",
            "sort": "顺序",
            "subject": "标题",
            "image": "主图URI地址",
            "thumbnail": "缩略图URI地址",
            "summary": "摘要",
            "label": "标签",
            "read": "阅读次数",
            "favorite": "收藏次数",
            "comment": "评论次数",
            "praise": "点赞数",
            "score": "得分",
            "time": "更新时间"
        }
    ]
}
```

- owner 所有者信息，未找到则仅包含id属性。
- author 作者信息，未找到则仅包含id属性。
