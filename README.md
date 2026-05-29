# app-core

Android 多模块基础框架（Phase 1），为多 App 场景提供统一的架构基座。

## 模块结构

```
app-core
├── core-bom          # 版本 BOM，统一依赖版本
├── core-base         # UI 基类、MVI、AppResult、扩展函数
├── core-foundation   # 网络、存储、日志、CoreFoundation 初始化
├── core-domain       # UseCase / FlowUseCase 基类
└── core-data         # BaseRepository、Room 基类、Paging 基类
```

依赖关系：`core-data` → `core-domain` → `core-base` ← `core-foundation`

## 技术栈

| 类别 | 技术 |
|------|------|
| 语言 | Kotlin 2.0.21 |
| 构建 | AGP 8.5.2 · Gradle 8.7 |
| DI | Hilt 2.51.1 |
| 网络 | Retrofit 2.11.0 · OkHttp 4.12.0 |
| 存储 | Room 2.6.1 · DataStore 1.1.1 |
| 分页 | Paging 3.3.2 |
| 图片 | Coil 2.7.0 |
| 日志 | Timber 5.0.1 |

## 快速接入

### 方式一：Maven Local（日常使用）

**1. 发布到本地**
```bash
./gradlew publishAllPublicationsToMavenLocalRepository
```

**2. 消费 App 的 `settings.gradle.kts` 添加 mavenLocal()**
```kotlin
dependencyResolutionManagement {
    repositories {
        mavenLocal()
        google()
        mavenCentral()
    }
}
```

**3. 引入依赖**
```kotlin
// build.gradle.kts（App 模块）
dependencies {
    implementation(platform("com.skybound.space:core-bom:1.0.0"))
    implementation("com.skybound.space:core-base")
    implementation("com.skybound.space:core-foundation")
    implementation("com.skybound.space:core-domain")
    implementation("com.skybound.space:core-data")
}
```

### 方式二：Composite Build（调试框架源码）

消费 App 的 `settings.gradle.kts`：
```kotlin
val debugFramework = false  // 调试时改为 true

if (debugFramework) {
    includeBuild("../app-core") {
        dependencySubstitution {
            substitute(module("com.skybound.space:core-base")).using(project(":core-base"))
            substitute(module("com.skybound.space:core-foundation")).using(project(":core-foundation"))
            substitute(module("com.skybound.space:core-domain")).using(project(":core-domain"))
            substitute(module("com.skybound.space:core-data")).using(project(":core-data"))
        }
    }
}
```

`debugFramework = true` 时 Sync 后可直接断点进入框架源码。

## 使用示例

### 初始化（Application）
```kotlin
class MyApp : Application() {

    @Inject lateinit var dispatchers: CoroutineDispatchers

    override fun onCreate() {
        super.onCreate()
        CoreFoundation.init(this) {
            debug(BuildConfig.DEBUG)
            useTimberLogger(true)
            network {
                baseUrl("https://api.example.com/")
                connectTimeout(30)
                addInterceptor(AuthInterceptor())
            }
        }
    }
}
```

### ViewModel（MVI）
```kotlin
data class HomeState(val isLoading: Boolean = false, val items: List<String> = emptyList()) : UiState
sealed class HomeEvent : UiEvent { data class ShowToast(val msg: String) : HomeEvent() }

class HomeViewModel @Inject constructor(
    private val getItemsUseCase: GetItemsUseCase
) : BaseViewModel<HomeState, HomeEvent>(HomeState()) {

    fun loadItems() {
        updateState { copy(isLoading = true) }
        viewModelScope.launch {
            when (val result = getItemsUseCase(Unit)) {
                is AppResult.Success -> updateState { copy(isLoading = false, items = result.data) }
                is AppResult.Failure -> sendEvent(HomeEvent.ShowToast("加载失败"))
                else -> Unit
            }
        }
    }
}
```

### UseCase
```kotlin
class GetItemsUseCase @Inject constructor(
    private val repository: ItemRepository,
    dispatchers: CoroutineDispatchers
) : UseCase<Unit, List<String>>(dispatchers) {
    override suspend fun execute(params: Unit) = repository.getItems()
}
```

## 契约文档

- [`docs/contracts/AppResult.md`](docs/contracts/AppResult.md) — AppResult / AppError 类型规范
- [`docs/contracts/ErrorTypes.md`](docs/contracts/ErrorTypes.md) — 错误类型清单
- [`docs/contracts/UseCase.md`](docs/contracts/UseCase.md) — UseCase 调用规范
- [`docs/changelog/BREAKING_CHANGES.md`](docs/changelog/BREAKING_CHANGES.md) — 破坏性变更记录

## License

[MIT](LICENSE)
