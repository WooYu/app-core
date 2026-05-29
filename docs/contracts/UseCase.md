# UseCase 契约

## 类型

- `UseCase<P, R>`: 单次调用，返回 `AppResult<R>`
- `FlowUseCase<P, R>`: 持续流，返回 `Flow<AppResult<R>>`

## 调用规范

1. UseCase 只能被 ViewModel 调用
2. UseCase 内部只允许调用 Repository
3. UseCase 不持有 Android Context
4. 参数 `P` 为 Unit 时表示无参数：`useCase(Unit)`

## 错误处理规范

- `UseCase.invoke()` 自动捕获 `execute()` 内抛出的异常，转为 `AppResult.Failure(AppError.Local(e))`；`CancellationException` 会被重新抛出以保证协程取消正常传播
- `FlowUseCase.invoke()` 通过 `.catch` 操作符捕获流中异常，同样转为 `AppResult.Failure(AppError.Local(e))`
- Repository 的 `safeApiCall` / `safeDbCall` 提供额外的错误分层（Network / Server / Local / Unauthorized）
