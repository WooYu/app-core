# AppError 错误类型契约

## 类型清单

| 类型 | 触发场景 | 建议 UI 处理 |
|------|---------|------------|
| `Network(code, message)` | HTTP 错误（4xx/5xx） | Toast + 可重试 |
| `Server(code, message)` | 业务错误（接口 code ≠ 200） | 展示 message |
| `Local(cause)` | 本地异常（DB / IO / 解析） | 通用错误提示 |
| `Unauthorized` | 401 / Token 过期 | 跳转登录页 |
| `Unknown` | 未归类异常 | 通用错误提示 |

## 双端映射

Android `AppError` ↔ Flutter `AppError`：字段名和语义保持一致。
