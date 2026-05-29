# Android Core Framework 架构设计文档

**日期：** 2026-05-28
**作者：** WooYu
**状态：** 已确认，待实施
**参照项目：** [SnapReceipt](https://github.com/WooYu/SnapReceipt)

---

## 1. 项目背景与目标

### 背景

以 SnapReceipt（Kotlin Android 多模块项目）为参照，从零设计一套可复用的基础架构，便于 Flutter App、Android App 快速搭建，省去重复工作，直接进行业务开发。框架仅供个人使用，要求新 App 方便引入，出问题时方便调试。

### 三阶段目标

| 阶段 | 内容 | 本文覆盖 |
|------|------|---------|
| Phase 1 | 设计并实现 Android 基础框架（修复 SnapReceipt 问题，补全缺失功能，输出文档） | ✅ 完整覆盖 |
| Phase 2 | 设计 Flutter 单包 `app_core`，对齐 Android 四层结构 | 概览 |
| Phase 3 | 双端一致性方案 | 概览 |

---

## 2. 关键决策

| 决策项 | 选择 | 理由 |
|--------|------|------|
| 产物类型 | 代码 + 文档 | 既要落地实现，又要输出架构文档 |
| 发布方式 | Maven Local（初期），后期可迁移 JitPack | 个人项目，先本地验证 |
| 模块粒度 | BOM + 4 子模块 | 兼顾灵活性与版本统一管理 |
| 调试方式 | Composite Build 切换 | 平时用 Maven 包，调试时一行切换为源码引用 |
| 包名规范 | `com.skybound.space.*` | 统一 SnapReceipt 现有命名，消除不一致 |
| 监控/埋点 | 仅预留接口，不引入 SDK | 个人项目按需接入，避免强依赖 |
| 分页支持 | 包含（Jetpack Paging 3） | App 频繁使用无限滚动列表 |
| Dialog/BottomSheet | 包含基类 | App 频繁使用，避免重复实现 |

---

## 3. 整体架构

### 仓库结构

```
android-core/                   ← 框架仓库根目录
  ├── build-logic/              ← Convention Plugins（Gradle 构建逻辑）
  ├── core-bom/                 ← BOM，统一锁定四个子模块版本
  ├── core-base/                ← 基础层
  ├── core-foundation/          ← 基础设施层
  ├── core-domain/              ← 领域契约层
  ├── core-data/                ← 数据层基础
  ├── sample/                   ← 功能演示 App（框架内自测用）
  ├── docs/                     ← 架构文档
  └── gradle/libs.versions.toml ← 统一版本目录
```

### 模块依赖图

```
         ┌──────────────────────────────────┐
         │           Consumer App           │
         └────┬──────────┬─────────────────┘
              │          │
         ┌────▼───┐  ┌───▼──────────┐
         │core-   │  │core-         │
         │domain  │  │foundation    │
         └────┬───┘  └───┬──────────┘
              │          │
         ┌────▼──────────▼──────────┐
         │         core-base        │
         └──────────────────────────┘
              ▲          ▲
         ┌────┴───────────┴─────────┐
         │         core-data        │
         └──────────────────────────┘
```

### 依赖规则（严格单向，禁止循环）

| 模块 | 允许依赖 |
|------|---------|
| `core-base` | 无框架内依赖（纯 Kotlin / AndroidX Core） |
| `core-domain` | `core-base` |
| `core-foundation` | `core-base` |
| `core-data` | `core-base` + `core-domain` + `core-foundation` |

### Maven 坐标

```
com.skybound.space:core-bom:x.x.x
com.skybound.space:core-base:x.x.x
com.skybound.space:core-foundation:x.x.x
com.skybound.space:core-domain:x.x.x
com.skybound.space:core-data:x.x.x
```

---

## 4. SnapReceipt 问题修复清单

| 问题 | 原状态 | 修复方案 |
|------|--------|---------|
| 包名不一致 | `core-base/foundation` 用 `com.skybound.space.*`，`core-data/domain` 用 `com.snapreceipt.io.*` | 全部统一为 `com.skybound.space.*` |
| 缺少 Storage 抽象 | 无 DataStore / EncryptedPrefs 封装 | `core-foundation/storage` 补全 |
| 缺少 Logging 门面 | 无统一日志接口 | `core-foundation/log` 新增 `AppLogger` |
| Result 类型分裂 | `NetworkResult` 和领域 `Result` 两套，需手动转换 | `core-base` 定义统一 `AppResult<T>`，全层复用 |
| 缺少 Coroutine Dispatcher 注入 | 调度器硬编码，测试时无法替换 | `core-foundation/dispatcher` 提供可注入接口 |
| 缺少图片加载抽象 | 无 Coil/Glide wrapper | `core-foundation/imageloader` 新增 `ImageLoader` 门面 |

---

## 5. 各模块详细设计

### 5.1 `core-base`

**职责：** 最底层基础，只依赖 Kotlin 标准库 + AndroidX Core，无第三方库。

**包结构：**

```
com.skybound.space.base/
├── result/
│   ├── AppResult.kt                      ← 统一 Result 类型
│   └── AppResultExt.kt                   ← map / onSuccess / onFailure / getOrNull
├── presentation/
│   ├── BaseActivity.kt                   ← 泛型 <S:UiState, E:UiEvent>
│   ├── BaseFragment.kt
│   ├── BaseViewModel.kt                  ← 持有 StateFlow<S> + EventFlow<E>
│   ├── BaseDialogFragment.kt             ← Dialog 基类
│   ├── BaseBottomSheetFragment.kt        ← BottomSheet 基类
│   ├── UiState.kt                        ← 标记接口
│   ├── UiEvent.kt                        ← 标记接口（one-shot 事件）
│   ├── UiEventDispatcher.kt              ← Channel-based，防止事件丢失
│   ├── FlowExt.kt                        ← collectWhenStarted / collectWhenResumed
│   ├── loading/
│   │   ├── LoadingOverlayHost.kt
│   │   ├── LoadingDialogController.kt
│   │   └── FullscreenLoadingDialogFragment.kt
│   ├── navigation/
│   │   └── NavDestination.kt             ← 导航目标抽象接口
│   ├── viewmodel/
│   │   └── ViewModelExt.kt
│   └── widget/
│       └── (基础自定义 View 基类)
├── coroutines/
│   └── CoroutineDispatchers.kt           ← 接口定义（core-domain UseCase 依赖此接口）
├── platform/
│   └── permission/
│       ├── PermissionHandler.kt          ← 接口定义
│       └── ActivityPermissionHandler.kt  ← 默认实现
└── ext/
    ├── ContextExt.kt
    ├── StringExt.kt
    └── ViewExt.kt
```

**核心类型：`AppResult<T>`**

```kotlin
sealed class AppResult<out T> {
    data class Success<T>(val data: T) : AppResult<T>()
    data class Failure(val error: AppError) : AppResult<Nothing>()
    data object Loading : AppResult<Nothing>()
}

sealed class AppError {
    data class Network(val code: Int, val message: String) : AppError()
    data class Server(val code: Int, val message: String) : AppError()
    data class Local(val cause: Throwable) : AppError()
    data object Unauthorized : AppError()
    data object Unknown : AppError()
}
```

**`BaseViewModel` 设计：**

```kotlin
abstract class BaseViewModel<S : UiState, E : UiEvent>(
    initialState: S
) : ViewModel() {
    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<S> = _uiState.asStateFlow()

    private val _events = UiEventDispatcher<E>()
    val events: Flow<E> = _events.flow

    protected fun updateState(reducer: S.() -> S) {
        _uiState.update(reducer)
    }

    protected fun sendEvent(event: E) = _events.send(event)
}
```

---

### 5.2 `core-foundation`

**职责：** 技术基础设施，依赖第三方库（Retrofit、OkHttp、Hilt、Timber 等）。App 通过接口或 `CoreFoundation` 统一入口使用，不直接接触实现细节。

**包结构：**

```
com.skybound.space.core/
├── CoreFoundation.kt                     ← 统一初始化入口
├── network/
│   ├── NetworkManager.kt                 ← 创建/管理 Retrofit 实例
│   ├── NetworkConfig.kt                  ← baseUrl、timeout、证书配置（DSL 风格）
│   ├── ApiService.kt                     ← 泛型 API 接口基础定义
│   ├── BaseResponse.kt                   ← 标准响应包装 { code, message, data }
│   ├── ApiException.kt                   ← 网络异常类型定义
│   ├── auth/
│   │   ├── TokenAuthenticator.kt         ← 401 自动刷新 Token
│   │   └── AuthInterceptor.kt            ← 请求头注入 Bearer token
│   ├── interceptor/
│   │   ├── LoggingInterceptor.kt         ← OkHttp 请求日志（Debug 专用）
│   │   └── CommonHeaderInterceptor.kt
│   └── serializer/
│       └── JsonSerializer.kt             ← Gson/Moshi 统一配置
├── storage/
│   ├── DataStoreManager.kt               ← 类型安全的 DataStore 封装
│   ├── EncryptedPrefsManager.kt          ← EncryptedSharedPreferences 封装
│   └── StorageKeys.kt                    ← Key 定义规范（防止 typo）
├── log/
│   ├── AppLogger.kt                      ← 日志门面接口（d/i/w/e/wtf）
│   ├── TimberLogger.kt                   ← Timber 实现（默认）
│   └── LogConfig.kt                      ← Debug 详细日志，Release 上报
├── di/
│   ├── AppInjector.kt                    ← Hilt EntryPoint 工具方法
│   └── CoreModule.kt                     ← 提供 NetworkManager/Logger/Storage 的 Hilt Module
├── dispatcher/
│   └── AppCoroutineDispatchers.kt        ← CoroutineDispatchers 接口的默认实现（Hilt 注入）
├── monitoring/
│   ├── IExceptionMonitor.kt              ← 接口：reportException / recordBreadcrumb
│   ├── IPerformanceMonitor.kt            ← 接口：startTrace / stopTrace
│   ├── ITrackManager.kt                  ← 接口：trackEvent / trackScreen
│   └── MonitoringNames.kt                ← 事件名常量
├── navigation/
│   └── NavigationManager.kt             ← 全局导航控制（深链接、跨模块跳转）
├── security/
│   ├── EncryptionUtils.kt                ← AES 加解密工具
│   └── CertificatePinner.kt             ← SSL 证书锁定配置
└── imageloader/
    ├── ImageLoader.kt                    ← 接口（load / loadCircle / loadRounded）
    └── CoilImageLoader.kt               ← Coil 默认实现
```

**`CoreFoundation` 统一初始化：**

```kotlin
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        CoreFoundation.init(this) {
            network {
                baseUrl = "https://api.example.com"
                connectTimeout = 30
                enableLogging = BuildConfig.DEBUG
            }
            logging {
                minLevel = if (BuildConfig.DEBUG) LogLevel.DEBUG else LogLevel.WARN
                crashReporting = true
            }
            storage {
                encryptionEnabled = true
            }
        }
    }
}
```

**监控接口（无 SDK 依赖，App 层自行实现注入）：**

```kotlin
interface ITrackManager {
    fun trackEvent(name: String, params: Map<String, Any> = emptyMap())
    fun trackScreen(screenName: String)
}

// 框架提供 NoOp 默认实现，App 不配置时静默忽略
object NoOpTrackManager : ITrackManager {
    override fun trackEvent(name: String, params: Map<String, Any>) = Unit
    override fun trackScreen(screenName: String) = Unit
}
```

---

### 5.3 `core-domain`

**职责：** 领域层契约，纯 Kotlin，无 Android 依赖，无第三方库。

**包结构：**

```
com.skybound.space.domain/
├── usecase/
│   ├── UseCase.kt                        ← 单次请求基类
│   └── FlowUseCase.kt                    ← 流式数据基类
├── repository/
│   └── IRepository.kt                    ← 标记接口，约束命名规范
└── model/
    └── BaseDomainModel.kt               ← 可选标记接口
```

**UseCase 基类（`CoroutineDispatchers` 来自 `core-base`）：**

```kotlin
abstract class UseCase<in P, out R>(
    private val dispatchers: CoroutineDispatchers  // core-base 定义的接口
) {
    suspend operator fun invoke(params: P): AppResult<R> =
        withContext(dispatchers.io) { execute(params) }

    protected abstract suspend fun execute(params: P): AppResult<R>
}

abstract class FlowUseCase<in P, out R>(
    private val dispatchers: CoroutineDispatchers
) {
    operator fun invoke(params: P): Flow<AppResult<R>> =
        execute(params)
            .catch { emit(AppResult.Failure(AppError.Unknown)) }
            .flowOn(dispatchers.io)

    protected abstract fun execute(params: P): Flow<AppResult<R>>
}
```

---

### 5.4 `core-data`

**职责：** 数据层通用基础，提供 Room、网络请求、Mapper、分页的基类，消除 App 层重复的 try-catch 和类型转换。

**包结构：**

```
com.skybound.space.data/
├── repository/
│   └── BaseRepository.kt                 ← safeApiCall / safeDbCall 封装
├── local/
│   ├── dao/
│   │   └── BaseDao.kt                    ← 通用 CRUD 接口
│   └── db/
│       └── BaseDatabase.kt              ← RoomDatabase 抽象基类
├── remote/
│   └── BaseRemoteDataSource.kt          ← 统一 try-catch → AppResult 转换
├── mapper/
│   └── BaseMapper.kt                    ← interface Mapper<From, To>
├── paging/
│   ├── BasePagingSource.kt              ← 封装 load() 的异常处理
│   └── PagingExt.kt                     ← Flow<PagingData<T>> 常用扩展
└── di/
    └── DataModule.kt                    ← Hilt base module
```

**`BaseRepository` 消除重复 try-catch：**

```kotlin
abstract class BaseRepository {

    protected suspend fun <T> safeApiCall(
        call: suspend () -> BaseResponse<T>
    ): AppResult<T> = try {
        val response = call()
        if (response.isSuccess) AppResult.Success(response.data!!)
        else AppResult.Failure(AppError.Server(response.code, response.message))
    } catch (e: HttpException) {
        AppResult.Failure(AppError.Network(e.code(), e.message()))
    } catch (e: IOException) {
        AppResult.Failure(AppError.Local(e))
    }

    protected suspend fun <T> safeDbCall(
        call: suspend () -> T
    ): AppResult<T> = try {
        AppResult.Success(call())
    } catch (e: Exception) {
        AppResult.Failure(AppError.Local(e))
    }
}
```

**`BasePagingSource` 分页基类：**

```kotlin
abstract class BasePagingSource<T : Any> : PagingSource<Int, T>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        val page = params.key ?: 1
        return try {
            val result = fetch(page, params.loadSize)
            LoadResult.Page(
                data = result.items,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (result.hasMore) page + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    protected abstract suspend fun fetch(page: Int, pageSize: Int): PagedResult<T>
}

data class PagedResult<T>(val items: List<T>, val hasMore: Boolean)
```

---

## 6. 构建体系

### BOM 结构

```kotlin
// core-bom/build.gradle.kts
plugins { `java-platform` }

dependencies {
    constraints {
        api("com.skybound.space:core-base:${version}")
        api("com.skybound.space:core-foundation:${version}")
        api("com.skybound.space:core-domain:${version}")
        api("com.skybound.space:core-data:${version}")
    }
}
```

**Consumer App 引入方式：**

```kotlin
dependencies {
    implementation(platform("com.skybound.space:core-bom:1.0.0"))
    implementation("com.skybound.space:core-base")
    implementation("com.skybound.space:core-foundation")
    implementation("com.skybound.space:core-domain")
    implementation("com.skybound.space:core-data")
}
```

### Convention Plugins（`build-logic`）

| Plugin | 作用 |
|--------|------|
| `AndroidLibraryConventionPlugin` | 统一所有 core 模块的 compileSdk、minSdk、Kotlin 版本 |
| `AndroidPublishConventionPlugin` | 统一 Maven 发布配置（groupId、sourcesJar、版本） |
| `AndroidHiltConventionPlugin` | 统一 Hilt 依赖和 ksp 配置 |

### Composite Build 调试切换

```
E:\Code\
  ├── Code_Base\          ← 框架仓库
  └── MyApp\              ← Consumer App
      └── settings.gradle.kts
```

**App 的 `settings.gradle.kts`：**

```kotlin
val debugFramework = false   // 调试时改为 true

if (debugFramework) {
    includeBuild("../Code_Base") {
        dependencySubstitution {
            substitute(module("com.skybound.space:core-base"))
                .using(project(":core-base"))
            substitute(module("com.skybound.space:core-foundation"))
                .using(project(":core-foundation"))
            substitute(module("com.skybound.space:core-domain"))
                .using(project(":core-domain"))
            substitute(module("com.skybound.space:core-data"))
                .using(project(":core-data"))
        }
    }
}
```

**调试工作流：**
1. `debugFramework = true` → Sync
2. 直接在 App 里断点进入框架源码
3. 修复完成 → `debugFramework = false`
4. `./gradlew publishAllToMavenLocal` → App Sync

### 版本管理

```toml
# gradle/libs.versions.toml
[versions]
app-core = "1.0.0"

[libraries]
core-bom = { group = "com.skybound.space", name = "core-bom", version.ref = "app-core" }
core-base = { group = "com.skybound.space", name = "core-base" }
core-foundation = { group = "com.skybound.space", name = "core-foundation" }
core-domain = { group = "com.skybound.space", name = "core-domain" }
core-data = { group = "com.skybound.space", name = "core-data" }
```

---

## 7. Phase 2：Flutter `app_core` 概览

单包结构，内含四个 sub-library，与 Android 四层一一对应：

| Android 模块 | Flutter 对应路径 | 核心内容 |
|-------------|----------------|---------|
| `core-base` | `app_core/lib/base` | `AppResult<T>`、`BaseViewModel`（Riverpod/Bloc）、UiState/UiEvent |
| `core-foundation` | `app_core/lib/foundation` | Dio 网络封装、SharedPreferences/SecureStorage、Logger、ImageLoader |
| `core-domain` | `app_core/lib/domain` | UseCase 基类、IRepository 接口 |
| `core-data` | `app_core/lib/data` | BaseRepository、safeApiCall、BaseMapper、分页支持 |

**引入方式：**

```yaml
# pubspec.yaml
dependencies:
  app_core:
    git:
      url: https://github.com/WooYu/Code_Base
      path: flutter/app_core
      ref: v1.0.0
```

---

## 8. Phase 3：双端一致性方案

### 契约文档目录

```
docs/contracts/
├── AppResult.md          ← Result 类型语义和字段，双端共同遵守
├── ErrorTypes.md         ← 统一错误码和错误分类
├── UseCase.md            ← UseCase 调用契约（输入/输出/错误处理）
└── NetworkConfig.md      ← API 配置约定

docs/changelog/
└── BREAKING_CHANGES.md   ← 破坏性变更必须记录，并标注双端影响
```

### 变更流程

```
修改 Android 框架
    ↓
判断是否影响契约层（AppResult / UseCase / Error 等核心类型）
    ↓ 是
更新 docs/contracts/ 对应文档
    ↓
在 BREAKING_CHANGES.md 记录变更内容和影响范围
    ↓
同步更新 Flutter app_core 对应部分
    ↓
双端同步发版
（Android: publishToMavenLocal / Flutter: 新增 git tag）
```

---

## 9. 实施优先级

Phase 1 建议实施顺序：

| 优先级 | 内容 | 原因 |
|--------|------|------|
| P0 | 搭建 `build-logic` + `core-bom` + 基础 Gradle 结构 | 所有模块的前提 |
| P0 | 实现 `core-base` | 其他模块的依赖基础 |
| P1 | 实现 `core-foundation`（网络 + 存储 + 日志） | 业务开发最高频使用 |
| P1 | 实现 `core-domain`（UseCase 基类） | 轻量，快速完成 |
| P2 | 实现 `core-data`（BaseRepository + 分页） | 依赖 core-foundation 完成后 |
| P2 | 发布到 Maven Local + 验证 Composite Build | 验证整体流程 |
| P3 | 输出 `docs/contracts/` 契约文档 | 为 Phase 2 Flutter 做准备 |
