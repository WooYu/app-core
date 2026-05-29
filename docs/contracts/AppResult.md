# AppResult 契约

双端（Android / Flutter）的 Result 类型必须遵守此契约。

## 类型定义

| 状态 | 含义 | 携带数据 |
|------|------|---------|
| `Success<T>` | 操作成功 | `data: T`（非空） |
| `Failure` | 操作失败 | `error: AppError` |
| `Loading` | 操作进行中 | 无 |

## 使用规范

- UI 层：订阅 StateFlow，根据三种状态渲染不同 UI
- Repository 层：所有方法返回 `AppResult<T>`，不允许直接抛出异常
- UseCase 层：透传 Repository 的 AppResult，可做 map 变换

## 变更规范

修改此类型（增删字段、变更语义）必须：
1. 更新本文档
2. 同步更新 Flutter app_core/lib/base/result/
3. 在 BREAKING_CHANGES.md 记录
