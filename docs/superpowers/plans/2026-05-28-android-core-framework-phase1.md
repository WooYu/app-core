# Android Core Framework (Phase 1) Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a reusable Android multi-module framework (core-bom + 4 modules) under `com.skybound.space`, publishable to Maven Local, with Composite Build debug switching.

**Architecture:** 4-layer Clean Architecture — core-base (pure Kotlin/AndroidX, no 3rd-party), core-foundation (infrastructure: network/storage/logging), core-domain (business contracts: UseCase/Repository interfaces), core-data (data layer base: Room/Paging/Mapper). A BOM locks all versions. Convention Plugins in build-logic unify Gradle config.

**Tech Stack:** Kotlin 2.0.21, AGP 8.5.2, Hilt 2.51.1, Retrofit 2.11.0, OkHttp 4.12.0, Room 2.6.1, DataStore 1.1.1, Coil 2.7.0, Timber 5.0.1, Paging 3.3.2, coroutines 1.8.1, JUnit 4.13.2, MockK 1.13.12, Turbine 1.1.0

---

## File Map

```
android-core/
├── .gitignore
├── settings.gradle.kts
├── build.gradle.kts
├── gradle/
│   ├── libs.versions.toml
│   └── wrapper/gradle-wrapper.properties
├── build-logic/
│   ├── settings.gradle.kts
│   └── convention/
│       ├── build.gradle.kts
│       └── src/main/kotlin/
│           ├── AndroidLibraryConventionPlugin.kt
│           ├── AndroidHiltConventionPlugin.kt
│           └── AndroidPublishConventionPlugin.kt
├── core-bom/
│   └── build.gradle.kts
├── core-base/
│   ├── build.gradle.kts
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   └── java/com/skybound/space/base/
│       │       ├── result/AppResult.kt
│       │       ├── result/AppResultExt.kt
│       │       ├── coroutines/CoroutineDispatchers.kt
│       │       ├── presentation/UiState.kt
│       │       ├── presentation/UiEvent.kt
│       │       ├── presentation/UiEventDispatcher.kt
│       │       ├── presentation/FlowExt.kt
│       │       ├── presentation/BaseViewModel.kt
│       │       ├── presentation/BaseActivity.kt
│       │       ├── presentation/BaseFragment.kt
│       │       ├── presentation/BaseDialogFragment.kt
│       │       ├── presentation/BaseBottomSheetFragment.kt
│       │       ├── presentation/loading/LoadingOverlayHost.kt
│       │       ├── presentation/loading/LoadingDialogController.kt
│       │       ├── presentation/loading/FullscreenLoadingDialogFragment.kt
│       │       ├── presentation/navigation/NavDestination.kt
│       │       ├── presentation/viewmodel/ViewModelExt.kt
│       │       ├── platform/permission/PermissionHandler.kt
│       │       ├── platform/permission/ActivityPermissionHandler.kt
│       │       ├── ext/ContextExt.kt
│       │       ├── ext/StringExt.kt
│       │       └── ext/ViewExt.kt
│       └── test/java/com/skybound/space/base/
│           ├── result/AppResultTest.kt
│           ├── presentation/BaseViewModelTest.kt
│           └── presentation/FlowExtTest.kt
├── core-foundation/
│   ├── build.gradle.kts
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   └── java/com/skybound/space/core/
│       │       ├── CoreFoundation.kt
│       │       ├── log/AppLogger.kt
│       │       ├── log/TimberLogger.kt
│       │       ├── log/LogConfig.kt
│       │       ├── dispatcher/AppCoroutineDispatchers.kt
│       │       ├── network/NetworkConfig.kt
│       │       ├── network/NetworkManager.kt
│       │       ├── network/ApiService.kt
│       │       ├── network/BaseResponse.kt
│       │       ├── network/ApiException.kt
│       │       ├── network/auth/AuthInterceptor.kt
│       │       ├── network/auth/TokenAuthenticator.kt
│       │       ├── network/interceptor/LoggingInterceptor.kt
│       │       ├── network/interceptor/CommonHeaderInterceptor.kt
│       │       ├── network/serializer/JsonSerializer.kt
│       │       ├── storage/DataStoreManager.kt
│       │       ├── storage/EncryptedPrefsManager.kt
│       │       ├── storage/StorageKeys.kt
│       │       ├── imageloader/ImageLoader.kt
│       │       ├── imageloader/CoilImageLoader.kt
│       │       ├── monitoring/IExceptionMonitor.kt
│       │       ├── monitoring/IPerformanceMonitor.kt
│       │       ├── monitoring/ITrackManager.kt
│       │       ├── monitoring/MonitoringNames.kt
│       │       ├── navigation/NavigationManager.kt
│       │       ├── security/EncryptionUtils.kt
│       │       ├── security/CertificatePinner.kt
│       │       ├── di/AppInjector.kt
│       │       └── di/CoreModule.kt
│       └── test/java/com/skybound/space/core/
│           ├── log/AppLoggerTest.kt
│           └── network/NetworkManagerTest.kt
├── core-domain/
│   ├── build.gradle.kts
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   └── java/com/skybound/space/domain/
│       │       ├── usecase/UseCase.kt
│       │       ├── usecase/FlowUseCase.kt
│       │       ├── repository/IRepository.kt
│       │       └── model/BaseDomainModel.kt
│       └── test/java/com/skybound/space/domain/
│           └── usecase/UseCaseTest.kt
└── core-data/
    ├── build.gradle.kts
    └── src/
        ├── main/
        │   ├── AndroidManifest.xml
        │   └── java/com/skybound/space/data/
        │       ├── repository/BaseRepository.kt
        │       ├── local/dao/BaseDao.kt
        │       ├── local/db/BaseDatabase.kt
        │       ├── remote/BaseRemoteDataSource.kt
        │       ├── mapper/BaseMapper.kt
        │       ├── paging/BasePagingSource.kt
        │       ├── paging/PagedResult.kt
        │       ├── paging/PagingExt.kt
        │       └── di/DataModule.kt
        └── test/java/com/skybound/space/data/
            ├── repository/BaseRepositoryTest.kt
            └── paging/BasePagingSourceTest.kt
```

---

## Task 1: Git + 项目根目录 Gradle 结构

**Files:**
- Create: `.gitignore`
- Create: `settings.gradle.kts`
- Create: `build.gradle.kts`
- Create: `gradle/libs.versions.toml`
- Create: `gradle/wrapper/gradle-wrapper.properties`

- [ ] **Step 1: 初始化 git 仓库**

```bash
cd E:\Code\Code_Base
git init
```

Expected: `Initialized empty Git repository in E:/Code/Code_Base/.git/`

- [ ] **Step 2: 创建 `.gitignore`**

```gitignore
# Gradle
.gradle/
build/
**/build/
*.class

# Local
local.properties
.idea/
*.iml
*.iws
*.ipr
.DS_Store

# Maven Local output (don't commit published artifacts)
repo/
```

- [ ] **Step 3: 创建 `gradle/wrapper/gradle-wrapper.properties`**

```properties
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-8.7-bin.zip
networkTimeout=10000
validateDistributionUrl=true
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
```

- [ ] **Step 4: 创建 `gradle/libs.versions.toml`**

```toml
[versions]
agp = "8.5.2"
kotlin = "2.0.21"
ksp = "2.0.21-1.0.28"
app-core = "1.0.0"

# AndroidX
core-ktx = "1.13.1"
appcompat = "1.7.0"
material = "1.12.0"
lifecycle = "2.8.4"
fragment = "1.8.2"
activity = "1.9.1"

# Architecture
hilt = "2.51.1"
room = "2.6.1"
datastore = "1.1.1"
paging = "3.3.2"

# Network
retrofit = "2.11.0"
okhttp = "4.12.0"
gson = "2.11.0"

# Image
coil = "2.7.0"

# Logging
timber = "5.0.1"

# Security
security-crypto = "1.1.0-alpha06"

# Coroutines
coroutines = "1.8.1"

# Test
junit = "4.13.2"
mockk = "1.13.12"
coroutines-test = "1.8.1"
turbine = "1.1.0"
arch-core-testing = "2.2.0"
mockwebserver = "4.12.0"

[libraries]
# AndroidX
androidx-core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "core-ktx" }
androidx-appcompat = { group = "androidx.appcompat", name = "appcompat", version.ref = "appcompat" }
androidx-material = { group = "com.google.android.material", name = "material", version.ref = "material" }
androidx-lifecycle-viewmodel = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-ktx", version.ref = "lifecycle" }
androidx-lifecycle-runtime = { group = "androidx.lifecycle", name = "lifecycle-runtime-ktx", version.ref = "lifecycle" }
androidx-fragment = { group = "androidx.fragment", name = "fragment-ktx", version.ref = "fragment" }
androidx-activity = { group = "androidx.activity", name = "activity-ktx", version.ref = "activity" }

# Hilt
hilt-android = { group = "com.google.dagger", name = "hilt-android", version.ref = "hilt" }
hilt-compiler = { group = "com.google.dagger", name = "hilt-android-compiler", version.ref = "hilt" }

# Room
room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "room" }
room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "room" }
room-paging = { group = "androidx.room", name = "room-paging", version.ref = "room" }

# DataStore
datastore-preferences = { group = "androidx.datastore", name = "datastore-preferences", version.ref = "datastore" }

# Paging
paging-runtime = { group = "androidx.paging", name = "paging-runtime-ktx", version.ref = "paging" }

# Network
retrofit-core = { group = "com.squareup.retrofit2", name = "retrofit", version.ref = "retrofit" }
retrofit-gson = { group = "com.squareup.retrofit2", name = "converter-gson", version.ref = "retrofit" }
okhttp-bom = { group = "com.squareup.okhttp3", name = "okhttp-bom", version.ref = "okhttp" }
okhttp-core = { group = "com.squareup.okhttp3", name = "okhttp" }
okhttp-logging = { group = "com.squareup.okhttp3", name = "logging-interceptor" }
gson = { group = "com.google.code.gson", name = "gson", version.ref = "gson" }

# Image
coil = { group = "io.coil-kt", name = "coil", version.ref = "coil" }

# Logging
timber = { group = "com.jakewharton.timber", name = "timber", version.ref = "timber" }

# Security
security-crypto = { group = "androidx.security", name = "security-crypto", version.ref = "security-crypto" }

# Coroutines
coroutines-core = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version.ref = "coroutines" }
coroutines-android = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-android", version.ref = "coroutines" }

# Test
junit = { group = "junit", name = "junit", version.ref = "junit" }
mockk = { group = "io.mockk", name = "mockk", version.ref = "mockk" }
coroutines-test = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-test", version.ref = "coroutines-test" }
turbine = { group = "app.cash.turbine", name = "turbine", version.ref = "turbine" }
arch-core-testing = { group = "androidx.arch.core", name = "core-testing", version.ref = "arch-core-testing" }
mockwebserver = { group = "com.squareup.okhttp3", name = "mockwebserver", version.ref = "mockwebserver" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
android-library = { id = "com.android.library", version.ref = "agp" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
kotlin-jvm = { id = "org.jetbrains.kotlin.jvm", version.ref = "kotlin" }
ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }
hilt = { id = "com.google.dagger.hilt.android", version.ref = "hilt" }
java-platform = { id = "java-platform" }
```

- [ ] **Step 5: 创建根 `settings.gradle.kts`**

```kotlin
pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        mavenLocal()
    }
}

rootProject.name = "android-core"

include(":core-bom")
include(":core-base")
include(":core-foundation")
include(":core-domain")
include(":core-data")
```

- [ ] **Step 6: 创建根 `build.gradle.kts`**

```kotlin
plugins {
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
}
```

- [ ] **Step 7: 生成 Gradle Wrapper（从 SnapReceipt 复制或运行）**

如果本机已有 Gradle 8.7：
```bash
gradle wrapper --gradle-version=8.7
```
或从 SnapReceipt 仓库复制 `gradlew`、`gradlew.bat`、`gradle/wrapper/gradle-wrapper.jar`。

- [ ] **Step 8: 验证 Gradle 配置可解析**

```bash
./gradlew help
```

Expected: `BUILD SUCCESSFUL`

- [ ] **Step 9: Commit**

```bash
git add .
git commit -m "chore: initialize project structure with Gradle version catalog"
```

---

## Task 2: build-logic — AndroidLibraryConventionPlugin

**Files:**
- Create: `build-logic/settings.gradle.kts`
- Create: `build-logic/convention/build.gradle.kts`
- Create: `build-logic/convention/src/main/kotlin/AndroidLibraryConventionPlugin.kt`

- [ ] **Step 1: 创建 `build-logic/settings.gradle.kts`**

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "build-logic"
include(":convention")
```

- [ ] **Step 2: 创建 `build-logic/convention/build.gradle.kts`**

```kotlin
plugins {
    `kotlin-dsl`
}

group = "com.skybound.space.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    compileOnly(libs.plugins.android.library.get().let { "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version}" })
    compileOnly(libs.plugins.kotlin.android.get().let { "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version}" })
}

gradlePlugin {
    plugins {
        register("androidLibrary") {
            id = "skybound.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidHilt") {
            id = "skybound.android.hilt"
            implementationClass = "AndroidHiltConventionPlugin"
        }
        register("androidPublish") {
            id = "skybound.android.publish"
            implementationClass = "AndroidPublishConventionPlugin"
        }
    }
}
```

- [ ] **Step 3: 创建 `build-logic/convention/src/main/kotlin/AndroidLibraryConventionPlugin.kt`**

```kotlin
import com.android.build.gradle.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
            }

            extensions.configure<LibraryExtension> {
                compileSdk = 35
                defaultConfig {
                    minSdk = 26
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                    consumerProguardFiles("consumer-rules.pro")
                }
                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_17
                    targetCompatibility = JavaVersion.VERSION_17
                }
                buildTypes {
                    release {
                        isMinifyEnabled = false
                    }
                }
            }

            tasks.withType<KotlinCompile>().configureEach {
                kotlinOptions {
                    jvmTarget = "17"
                    freeCompilerArgs += listOf(
                        "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
                    )
                }
            }
        }
    }
}
```

- [ ] **Step 4: Commit**

```bash
git add build-logic/
git commit -m "chore: add AndroidLibraryConventionPlugin"
```

---

## Task 3: build-logic — AndroidHiltConventionPlugin + AndroidPublishConventionPlugin

**Files:**
- Create: `build-logic/convention/src/main/kotlin/AndroidHiltConventionPlugin.kt`
- Create: `build-logic/convention/src/main/kotlin/AndroidPublishConventionPlugin.kt`

- [ ] **Step 1: 创建 `AndroidHiltConventionPlugin.kt`**

```kotlin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidHiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.google.dagger.hilt.android")
                apply("com.google.devtools.ksp")
            }

            val libs = extensions.getByType(
                org.gradle.api.artifacts.VersionCatalogsExtension::class.java
            ).named("libs")

            dependencies {
                add("implementation", libs.findLibrary("hilt-android").get())
                add("ksp", libs.findLibrary("hilt-compiler").get())
            }
        }
    }
}
```

- [ ] **Step 2: 创建 `AndroidPublishConventionPlugin.kt`**

```kotlin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get

class AndroidPublishConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("maven-publish")

            val libs = extensions.getByType(
                org.gradle.api.artifacts.VersionCatalogsExtension::class.java
            ).named("libs")
            val coreVersion = libs.findVersion("app-core").get().requiredVersion

            extensions.configure<PublishingExtension> {
                publications {
                    create<MavenPublication>("release") {
                        groupId = "com.skybound.space"
                        artifactId = project.name
                        version = coreVersion

                        afterEvaluate {
                            from(components["release"])
                        }
                    }
                }
                repositories {
                    mavenLocal()
                }
            }
        }
    }
}
```

- [ ] **Step 3: 验证 build-logic 能编译**

```bash
./gradlew help
```

Expected: `BUILD SUCCESSFUL`（Gradle 解析插件无报错）

- [ ] **Step 4: Commit**

```bash
git add build-logic/convention/src/
git commit -m "chore: add AndroidHiltConventionPlugin and AndroidPublishConventionPlugin"
```

---

## Task 4: core-bom

**Files:**
- Create: `core-bom/build.gradle.kts`

- [ ] **Step 1: 创建 `core-bom/build.gradle.kts`**

```kotlin
plugins {
    `java-platform`
    id("skybound.android.publish")
}

// BOM 不能有依赖约束以外的依赖
javaPlatform {
    allowDependencies()
}

dependencies {
    constraints {
        val version = libs.versions.appCore.get()
        api("com.skybound.space:core-base:$version")
        api("com.skybound.space:core-foundation:$version")
        api("com.skybound.space:core-domain:$version")
        api("com.skybound.space:core-data:$version")
    }
}
```

- [ ] **Step 2: 更新 `gradle/libs.versions.toml`，确认 `app-core` key 正确**

在 `[versions]` 下确认存在：
```toml
app-core = "1.0.0"
```

- [ ] **Step 3: 验证 BOM module 可 Sync**

```bash
./gradlew :core-bom:dependencies
```

Expected: `BUILD SUCCESSFUL`

- [ ] **Step 4: Commit**

```bash
git add core-bom/
git commit -m "feat: add core-bom for unified version management"
```

---

## Task 5: core-base 脚手架 + AppResult（TDD）

**Files:**
- Create: `core-base/build.gradle.kts`
- Create: `core-base/src/main/AndroidManifest.xml`
- Create: `core-base/src/main/java/com/skybound/space/base/result/AppResult.kt`
- Create: `core-base/src/main/java/com/skybound/space/base/result/AppResultExt.kt`
- Create: `core-base/src/test/java/com/skybound/space/base/result/AppResultTest.kt`

- [ ] **Step 1: 创建 `core-base/build.gradle.kts`**

```kotlin
plugins {
    id("skybound.android.library")
    id("skybound.android.publish")
}

android {
    namespace = "com.skybound.space.base"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.material)
    implementation(libs.coroutines.android)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(libs.arch.core.testing)
}
```

- [ ] **Step 2: 创建 `core-base/src/main/AndroidManifest.xml`**

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest />
```

- [ ] **Step 3: 先写测试（TDD — 红）**

创建 `core-base/src/test/java/com/skybound/space/base/result/AppResultTest.kt`:

```kotlin
package com.skybound.space.base.result

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AppResultTest {

    @Test
    fun `Success holds data`() {
        val result = AppResult.Success("hello")
        assertEquals("hello", result.data)
    }

    @Test
    fun `Failure holds error`() {
        val error = AppError.Unknown
        val result = AppResult.Failure(error)
        assertEquals(error, result.error)
    }

    @Test
    fun `map transforms Success value`() {
        val result: AppResult<Int> = AppResult.Success(5)
        val mapped = result.map { it * 2 }
        assertEquals(AppResult.Success(10), mapped)
    }

    @Test
    fun `map preserves Failure unchanged`() {
        val error = AppError.Unknown
        val result: AppResult<Int> = AppResult.Failure(error)
        val mapped = result.map { it * 2 }
        assertEquals(AppResult.Failure(error), mapped)
    }

    @Test
    fun `map preserves Loading unchanged`() {
        val result: AppResult<Int> = AppResult.Loading
        val mapped = result.map { it * 2 }
        assertEquals(AppResult.Loading, mapped)
    }

    @Test
    fun `getOrNull returns data for Success`() {
        val result = AppResult.Success(42)
        assertEquals(42, result.getOrNull())
    }

    @Test
    fun `getOrNull returns null for Failure`() {
        val result: AppResult<Int> = AppResult.Failure(AppError.Unknown)
        assertNull(result.getOrNull())
    }

    @Test
    fun `isSuccess returns true only for Success`() {
        assertTrue(AppResult.Success(1).isSuccess)
        assertTrue(!AppResult.Failure(AppError.Unknown).isSuccess)
        assertTrue(!AppResult.Loading.isSuccess)
    }

    @Test
    fun `onSuccess callback invoked for Success`() {
        var called = false
        AppResult.Success("x").onSuccess { called = true }
        assertTrue(called)
    }

    @Test
    fun `onSuccess callback not invoked for Failure`() {
        var called = false
        AppResult.Failure(AppError.Unknown).onSuccess { called = true }
        assertTrue(!called)
    }

    @Test
    fun `onFailure callback invoked for Failure`() {
        var called = false
        AppResult.Failure(AppError.Unknown).onFailure { called = true }
        assertTrue(called)
    }
}
```

- [ ] **Step 4: 运行测试，确认红（编译失败）**

```bash
./gradlew :core-base:test
```

Expected: BUILD FAILED — `AppResult` not found

- [ ] **Step 5: 实现 `AppResult.kt`**

创建 `core-base/src/main/java/com/skybound/space/base/result/AppResult.kt`:

```kotlin
package com.skybound.space.base.result

sealed class AppResult<out T> {
    data class Success<out T>(val data: T) : AppResult<T>()
    data class Failure(val error: AppError) : AppResult<Nothing>()
    data object Loading : AppResult<Nothing>()

    val isSuccess: Boolean get() = this is Success
    val isFailure: Boolean get() = this is Failure
    val isLoading: Boolean get() = this is Loading
}

sealed class AppError {
    data class Network(val code: Int, val message: String) : AppError()
    data class Server(val code: Int, val message: String) : AppError()
    data class Local(val cause: Throwable) : AppError()
    data object Unauthorized : AppError()
    data object Unknown : AppError()
}
```

- [ ] **Step 6: 实现 `AppResultExt.kt`**

创建 `core-base/src/main/java/com/skybound/space/base/result/AppResultExt.kt`:

```kotlin
package com.skybound.space.base.result

fun <T, R> AppResult<T>.map(transform: (T) -> R): AppResult<R> = when (this) {
    is AppResult.Success -> AppResult.Success(transform(data))
    is AppResult.Failure -> this
    is AppResult.Loading -> AppResult.Loading
}

fun <T> AppResult<T>.getOrNull(): T? = when (this) {
    is AppResult.Success -> data
    else -> null
}

fun <T> AppResult<T>.getOrDefault(default: T): T = when (this) {
    is AppResult.Success -> data
    else -> default
}

inline fun <T> AppResult<T>.onSuccess(action: (T) -> Unit): AppResult<T> {
    if (this is AppResult.Success) action(data)
    return this
}

inline fun <T> AppResult<T>.onFailure(action: (AppError) -> Unit): AppResult<T> {
    if (this is AppResult.Failure) action(error)
    return this
}

inline fun <T> AppResult<T>.onLoading(action: () -> Unit): AppResult<T> {
    if (this is AppResult.Loading) action()
    return this
}

suspend fun <T, R> AppResult<T>.mapSuspend(transform: suspend (T) -> R): AppResult<R> = when (this) {
    is AppResult.Success -> AppResult.Success(transform(data))
    is AppResult.Failure -> this
    is AppResult.Loading -> AppResult.Loading
}
```

- [ ] **Step 7: 运行测试，确认绿**

```bash
./gradlew :core-base:test
```

Expected: `BUILD SUCCESSFUL` — 11 tests passed

- [ ] **Step 8: Commit**

```bash
git add core-base/
git commit -m "feat(core-base): add AppResult and AppError sealed classes with extensions"
```

---

## Task 6: core-base — CoroutineDispatchers + FlowExt（TDD）

**Files:**
- Create: `core-base/src/main/java/com/skybound/space/base/coroutines/CoroutineDispatchers.kt`
- Create: `core-base/src/test/java/com/skybound/space/base/presentation/FlowExtTest.kt`
- Create: `core-base/src/main/java/com/skybound/space/base/presentation/FlowExt.kt`

- [ ] **Step 1: 创建 `CoroutineDispatchers.kt`**

```kotlin
package com.skybound.space.base.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

interface CoroutineDispatchers {
    val main: CoroutineDispatcher
    val io: CoroutineDispatcher
    val default: CoroutineDispatcher
    val unconfined: CoroutineDispatcher
}

// 测试用：TestCoroutineDispatchers（在 test source set 中提供）
class DefaultCoroutineDispatchers : CoroutineDispatchers {
    override val main: CoroutineDispatcher = Dispatchers.Main
    override val io: CoroutineDispatcher = Dispatchers.IO
    override val default: CoroutineDispatcher = Dispatchers.Default
    override val unconfined: CoroutineDispatcher = Dispatchers.Unconfined
}
```

- [ ] **Step 2: 写 FlowExt 测试（TDD — 红）**

创建 `core-base/src/test/java/com/skybound/space/base/presentation/FlowExtTest.kt`:

```kotlin
package com.skybound.space.base.presentation

import app.cash.turbine.test
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class FlowExtTest {

    @Test
    fun `flow emits expected values via turbine`() = runTest {
        val flow = MutableStateFlow(0)
        flow.test {
            assertEquals(0, awaitItem())
            flow.value = 1
            assertEquals(1, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
```

- [ ] **Step 3: 运行，确认绿（Turbine 已在依赖中）**

```bash
./gradlew :core-base:test --tests "*.FlowExtTest"
```

Expected: `BUILD SUCCESSFUL`

- [ ] **Step 4: 创建 `FlowExt.kt`**

```kotlin
package com.skybound.space.base.presentation

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

fun <T> Flow<T>.collectWhenStarted(owner: LifecycleOwner, action: suspend (T) -> Unit) {
    owner.lifecycleScope.launch {
        owner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            collect { action(it) }
        }
    }
}

fun <T> Flow<T>.collectWhenResumed(owner: LifecycleOwner, action: suspend (T) -> Unit) {
    owner.lifecycleScope.launch {
        owner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            collect { action(it) }
        }
    }
}
```

- [ ] **Step 5: Commit**

```bash
git add core-base/src/main/java/com/skybound/space/base/coroutines/ \
         core-base/src/main/java/com/skybound/space/base/presentation/FlowExt.kt \
         core-base/src/test/
git commit -m "feat(core-base): add CoroutineDispatchers interface and FlowExt lifecycle collectors"
```

---

## Task 7: core-base — BaseViewModel + UiState / UiEvent（TDD）

**Files:**
- Create: `core-base/src/main/java/com/skybound/space/base/presentation/UiState.kt`
- Create: `core-base/src/main/java/com/skybound/space/base/presentation/UiEvent.kt`
- Create: `core-base/src/main/java/com/skybound/space/base/presentation/UiEventDispatcher.kt`
- Create: `core-base/src/main/java/com/skybound/space/base/presentation/BaseViewModel.kt`
- Create: `core-base/src/test/java/com/skybound/space/base/presentation/BaseViewModelTest.kt`

- [ ] **Step 1: 创建标记接口**

`UiState.kt`:
```kotlin
package com.skybound.space.base.presentation

interface UiState
```

`UiEvent.kt`:
```kotlin
package com.skybound.space.base.presentation

interface UiEvent
```

- [ ] **Step 2: 创建 `UiEventDispatcher.kt`**

```kotlin
package com.skybound.space.base.presentation

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

class UiEventDispatcher<E : UiEvent> {
    private val _channel = Channel<E>(Channel.BUFFERED)
    val flow: Flow<E> = _channel.receiveAsFlow()

    fun send(event: E) {
        _channel.trySend(event)
    }
}
```

- [ ] **Step 3: 写 BaseViewModel 测试（TDD — 红）**

创建 `core-base/src/test/java/com/skybound/space/base/presentation/BaseViewModelTest.kt`:

```kotlin
package com.skybound.space.base.presentation

import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BaseViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // --- Concrete test implementations ---

    data class TestState(val count: Int = 0) : UiState

    sealed class TestEvent : UiEvent {
        data object Increment : TestEvent()
    }

    class TestViewModel : BaseViewModel<TestState, TestEvent>(TestState()) {
        fun increment() {
            updateState { copy(count = count + 1) }
        }
        fun fireEvent() {
            sendEvent(TestEvent.Increment)
        }
    }

    // --- Tests ---

    @Test
    fun `initial state is emitted`() = runTest {
        val vm = TestViewModel()
        assertEquals(TestState(count = 0), vm.uiState.value)
    }

    @Test
    fun `updateState emits new state`() = runTest {
        val vm = TestViewModel()
        vm.uiState.test {
            assertEquals(TestState(0), awaitItem())
            vm.increment()
            assertEquals(TestState(1), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `sendEvent delivers event via flow`() = runTest {
        val vm = TestViewModel()
        vm.events.test {
            vm.fireEvent()
            assertEquals(TestEvent.Increment, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `multiple state updates are sequential`() = runTest {
        val vm = TestViewModel()
        repeat(3) { vm.increment() }
        assertEquals(3, vm.uiState.value.count)
    }
}
```

- [ ] **Step 4: 运行，确认红**

```bash
./gradlew :core-base:test --tests "*.BaseViewModelTest"
```

Expected: BUILD FAILED — `BaseViewModel` not found

- [ ] **Step 5: 实现 `BaseViewModel.kt`**

```kotlin
package com.skybound.space.base.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

abstract class BaseViewModel<S : UiState, E : UiEvent>(
    initialState: S
) : ViewModel() {

    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<S> = _uiState.asStateFlow()

    private val _eventDispatcher = UiEventDispatcher<E>()
    val events: Flow<E> = _eventDispatcher.flow

    protected fun updateState(reducer: S.() -> S) {
        _uiState.update(reducer)
    }

    protected fun sendEvent(event: E) {
        _eventDispatcher.send(event)
    }
}
```

- [ ] **Step 6: 运行，确认绿**

```bash
./gradlew :core-base:test --tests "*.BaseViewModelTest"
```

Expected: `BUILD SUCCESSFUL` — 4 tests passed

- [ ] **Step 7: Commit**

```bash
git add core-base/src/main/java/com/skybound/space/base/presentation/
git commit -m "feat(core-base): add BaseViewModel with MVI state/event pattern"
```

---

## Task 8: core-base — Base UI 类（Activity / Fragment / Dialog / BottomSheet）

**Files:**
- Create: `BaseActivity.kt`, `BaseFragment.kt`, `BaseDialogFragment.kt`, `BaseBottomSheetFragment.kt`
- Create: `loading/` 三个文件
- Create: `navigation/NavDestination.kt`
- Create: `viewmodel/ViewModelExt.kt`

> 这些类是 Android UI 基类，不做单元测试（需要 Instrumented 测试），直接实现。

- [ ] **Step 1: 创建 `BaseActivity.kt`**

```kotlin
package com.skybound.space.base.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

abstract class BaseActivity<S : UiState, E : UiEvent> : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    protected abstract fun renderState(state: S)
    protected abstract fun handleEvent(event: E)
}
```

- [ ] **Step 2: 创建 `BaseFragment.kt`**

```kotlin
package com.skybound.space.base.presentation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

abstract class BaseFragment<S : UiState, E : UiEvent> : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
    }

    private fun observeViewModel() {
        val vm = provideViewModel() ?: return
        vm.uiState.collectWhenStarted(viewLifecycleOwner) { renderState(it) }
        vm.events.collectWhenStarted(viewLifecycleOwner) { handleEvent(it) }
    }

    protected open fun provideViewModel(): BaseViewModel<S, E>? = null
    protected abstract fun renderState(state: S)
    protected abstract fun handleEvent(event: E)
}
```

- [ ] **Step 3: 创建 `BaseDialogFragment.kt`**

```kotlin
package com.skybound.space.base.presentation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment

abstract class BaseDialogFragment<S : UiState, E : UiEvent> : DialogFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
    }

    private fun observeViewModel() {
        val vm = provideViewModel() ?: return
        vm.uiState.collectWhenStarted(viewLifecycleOwner) { renderState(it) }
        vm.events.collectWhenStarted(viewLifecycleOwner) { handleEvent(it) }
    }

    protected open fun provideViewModel(): BaseViewModel<S, E>? = null
    protected abstract fun renderState(state: S)
    protected abstract fun handleEvent(event: E)
}
```

- [ ] **Step 4: 创建 `BaseBottomSheetFragment.kt`**

```kotlin
package com.skybound.space.base.presentation

import android.os.Bundle
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class BaseBottomSheetFragment<S : UiState, E : UiEvent> : BottomSheetDialogFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
    }

    private fun observeViewModel() {
        val vm = provideViewModel() ?: return
        vm.uiState.collectWhenStarted(viewLifecycleOwner) { renderState(it) }
        vm.events.collectWhenStarted(viewLifecycleOwner) { handleEvent(it) }
    }

    protected open fun provideViewModel(): BaseViewModel<S, E>? = null
    protected abstract fun renderState(state: S)
    protected abstract fun handleEvent(event: E)
}
```

- [ ] **Step 5: 创建 Loading 相关类**

`loading/LoadingOverlayHost.kt`:
```kotlin
package com.skybound.space.base.presentation.loading

interface LoadingOverlayHost {
    fun showLoading()
    fun hideLoading()
}
```

`loading/LoadingDialogController.kt`:
```kotlin
package com.skybound.space.base.presentation.loading

import androidx.fragment.app.FragmentManager

class LoadingDialogController(private val fragmentManager: FragmentManager) {

    private val tag = "FullscreenLoadingDialog"

    fun show() {
        if (fragmentManager.findFragmentByTag(tag) == null) {
            FullscreenLoadingDialogFragment()
                .show(fragmentManager, tag)
        }
    }

    fun hide() {
        (fragmentManager.findFragmentByTag(tag) as? FullscreenLoadingDialogFragment)
            ?.dismissAllowingStateLoss()
    }
}
```

`loading/FullscreenLoadingDialogFragment.kt`:
```kotlin
package com.skybound.space.base.presentation.loading

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ProgressBar
import androidx.fragment.app.DialogFragment

class FullscreenLoadingDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).also { dialog ->
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            isCancelable = false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ProgressBar(requireContext())
}
```

- [ ] **Step 6: 创建 Navigation + ViewModelExt**

`navigation/NavDestination.kt`:
```kotlin
package com.skybound.space.base.presentation.navigation

interface NavDestination {
    val route: String
}
```

`viewmodel/ViewModelExt.kt`:
```kotlin
package com.skybound.space.base.presentation.viewmodel

import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

inline fun <reified VM : ViewModel> Fragment.viewModels(
    noinline factoryProducer: (() -> ViewModelProvider.Factory)? = null
): Lazy<VM> = androidx.fragment.app.viewModels(factoryProducer)

inline fun <reified VM : ViewModel> ComponentActivity.viewModels(
    noinline factoryProducer: (() -> ViewModelProvider.Factory)? = null
): Lazy<VM> = androidx.activity.viewModels(factoryProducer)
```

- [ ] **Step 7: 编译验证**

```bash
./gradlew :core-base:assembleRelease
```

Expected: `BUILD SUCCESSFUL`

- [ ] **Step 8: Commit**

```bash
git add core-base/src/main/java/com/skybound/space/base/presentation/
git commit -m "feat(core-base): add Base UI classes (Activity, Fragment, Dialog, BottomSheet, Loading)"
```

---

## Task 9: core-base — PermissionHandler + Extensions

**Files:**
- Create: `platform/permission/PermissionHandler.kt`
- Create: `platform/permission/ActivityPermissionHandler.kt`
- Create: `ext/ContextExt.kt`, `ext/StringExt.kt`, `ext/ViewExt.kt`

- [ ] **Step 1: 创建 `PermissionHandler.kt`**

```kotlin
package com.skybound.space.base.platform.permission

interface PermissionHandler {
    fun requestPermission(permission: String, onGranted: () -> Unit, onDenied: () -> Unit)
    fun requestPermissions(
        permissions: Array<String>,
        onAllGranted: () -> Unit,
        onDenied: (List<String>) -> Unit
    )
    fun hasPermission(permission: String): Boolean
}
```

- [ ] **Step 2: 创建 `ActivityPermissionHandler.kt`**

```kotlin
package com.skybound.space.base.platform.permission

import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

class ActivityPermissionHandler(private val activity: ComponentActivity) : PermissionHandler {

    private var onGranted: (() -> Unit)? = null
    private var onDenied: (() -> Unit)? = null
    private var onAllGranted: (() -> Unit)? = null
    private var onMultiDenied: ((List<String>) -> Unit)? = null

    private val singleLauncher = activity.registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) onGranted?.invoke() else onDenied?.invoke()
    }

    private val multiLauncher = activity.registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        val denied = results.filterValues { !it }.keys.toList()
        if (denied.isEmpty()) onAllGranted?.invoke() else onMultiDenied?.invoke(denied)
    }

    override fun requestPermission(
        permission: String,
        onGranted: () -> Unit,
        onDenied: () -> Unit
    ) {
        this.onGranted = onGranted
        this.onDenied = onDenied
        if (hasPermission(permission)) onGranted() else singleLauncher.launch(permission)
    }

    override fun requestPermissions(
        permissions: Array<String>,
        onAllGranted: () -> Unit,
        onDenied: (List<String>) -> Unit
    ) {
        this.onAllGranted = onAllGranted
        this.onMultiDenied = onDenied
        val missing = permissions.filter { !hasPermission(it) }.toTypedArray()
        if (missing.isEmpty()) onAllGranted() else multiLauncher.launch(missing)
    }

    override fun hasPermission(permission: String): Boolean =
        ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
}
```

- [ ] **Step 3: 创建扩展函数**

`ext/ContextExt.kt`:
```kotlin
package com.skybound.space.base.ext

import android.content.Context
import android.widget.Toast

fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun Context.dpToPx(dp: Float): Int =
    (dp * resources.displayMetrics.density).toInt()

fun Context.pxToDp(px: Int): Float =
    px / resources.displayMetrics.density
```

`ext/StringExt.kt`:
```kotlin
package com.skybound.space.base.ext

fun String?.orEmpty(): String = this ?: ""

fun String.isEmail(): Boolean =
    android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun String.isPhoneNumber(): Boolean =
    android.util.Patterns.PHONE.matcher(this).matches()
```

`ext/ViewExt.kt`:
```kotlin
package com.skybound.space.base.ext

import android.view.View

fun View.show() { visibility = View.VISIBLE }
fun View.hide() { visibility = View.GONE }
fun View.invisible() { visibility = View.INVISIBLE }

fun View.isVisible(): Boolean = visibility == View.VISIBLE

fun View.setVisible(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}
```

- [ ] **Step 4: 完整编译验证**

```bash
./gradlew :core-base:assembleRelease :core-base:test
```

Expected: `BUILD SUCCESSFUL`

- [ ] **Step 5: Commit**

```bash
git add core-base/src/main/java/com/skybound/space/base/platform/ \
         core-base/src/main/java/com/skybound/space/base/ext/
git commit -m "feat(core-base): add PermissionHandler and Kotlin extension functions"
```

---

## Task 10: core-foundation 脚手架 + AppLogger（TDD）

**Files:**
- Create: `core-foundation/build.gradle.kts`
- Create: `core-foundation/src/main/AndroidManifest.xml`
- Create: `core-foundation/src/main/java/com/skybound/space/core/log/AppLogger.kt`
- Create: `core-foundation/src/main/java/com/skybound/space/core/log/TimberLogger.kt`
- Create: `core-foundation/src/main/java/com/skybound/space/core/log/LogConfig.kt`
- Create: `core-foundation/src/test/java/com/skybound/space/core/log/AppLoggerTest.kt`

- [ ] **Step 1: 创建 `core-foundation/build.gradle.kts`**

```kotlin
plugins {
    id("skybound.android.library")
    id("skybound.android.hilt")
    id("skybound.android.publish")
}

android {
    namespace = "com.skybound.space.core"
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    api(project(":core-base"))

    // Network
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.gson)
    implementation(platform(libs.okhttp.bom))
    implementation(libs.okhttp.core)
    implementation(libs.okhttp.logging)
    implementation(libs.gson)

    // Storage
    implementation(libs.datastore.preferences)
    implementation(libs.security.crypto)

    // Image
    implementation(libs.coil)

    // Logging
    implementation(libs.timber)

    // Coroutines
    implementation(libs.coroutines.android)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.mockwebserver)
}
```

- [ ] **Step 2: 创建 `core-foundation/src/main/AndroidManifest.xml`**

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest>
    <uses-permission android:name="android.permission.INTERNET"/>
</manifest>
```

- [ ] **Step 3: 写 Logger 测试（TDD — 红）**

创建 `core-foundation/src/test/java/com/skybound/space/core/log/AppLoggerTest.kt`:

```kotlin
package com.skybound.space.core.log

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class AppLoggerTest {

    private val messages = mutableListOf<String>()

    private val testLogger = object : AppLogger {
        override fun d(tag: String, message: String) { messages.add("D/$tag: $message") }
        override fun i(tag: String, message: String) { messages.add("I/$tag: $message") }
        override fun w(tag: String, message: String) { messages.add("W/$tag: $message") }
        override fun e(tag: String, message: String, throwable: Throwable?) {
            messages.add("E/$tag: $message")
        }
    }

    @Before
    fun setUp() {
        messages.clear()
        AppLoggerProvider.setLogger(testLogger)
    }

    @Test
    fun `d logs debug message`() {
        AppLoggerProvider.d("TestTag", "debug message")
        assertEquals("D/TestTag: debug message", messages.first())
    }

    @Test
    fun `e logs error message`() {
        AppLoggerProvider.e("TestTag", "error message")
        assertEquals("E/TestTag: error message", messages.first())
    }

    @Test
    fun `no log when logger not set uses NoOp`() {
        AppLoggerProvider.setLogger(NoOpLogger)
        AppLoggerProvider.d("tag", "msg") // must not throw
        assertEquals(0, messages.size)
    }
}
```

- [ ] **Step 4: 运行，确认红**

```bash
./gradlew :core-foundation:test --tests "*.AppLoggerTest"
```

Expected: BUILD FAILED — `AppLogger` not found

- [ ] **Step 5: 实现 `AppLogger.kt`**

```kotlin
package com.skybound.space.core.log

interface AppLogger {
    fun d(tag: String, message: String)
    fun i(tag: String, message: String)
    fun w(tag: String, message: String)
    fun e(tag: String, message: String, throwable: Throwable? = null)
}

object NoOpLogger : AppLogger {
    override fun d(tag: String, message: String) = Unit
    override fun i(tag: String, message: String) = Unit
    override fun w(tag: String, message: String) = Unit
    override fun e(tag: String, message: String, throwable: Throwable?) = Unit
}

object AppLoggerProvider {
    private var logger: AppLogger = NoOpLogger

    fun setLogger(logger: AppLogger) { this.logger = logger }

    fun d(tag: String, message: String) = logger.d(tag, message)
    fun i(tag: String, message: String) = logger.i(tag, message)
    fun w(tag: String, message: String) = logger.w(tag, message)
    fun e(tag: String, message: String, throwable: Throwable? = null) =
        logger.e(tag, message, throwable)
}
```

- [ ] **Step 6: 创建 `TimberLogger.kt`**

```kotlin
package com.skybound.space.core.log

import timber.log.Timber

class TimberLogger : AppLogger {
    override fun d(tag: String, message: String) = Timber.tag(tag).d(message)
    override fun i(tag: String, message: String) = Timber.tag(tag).i(message)
    override fun w(tag: String, message: String) = Timber.tag(tag).w(message)
    override fun e(tag: String, message: String, throwable: Throwable?) {
        if (throwable != null) Timber.tag(tag).e(throwable, message)
        else Timber.tag(tag).e(message)
    }
}
```

- [ ] **Step 7: 创建 `LogConfig.kt`**

```kotlin
package com.skybound.space.core.log

import timber.log.Timber

enum class LogLevel { DEBUG, INFO, WARN, ERROR, NONE }

data class LogConfig(
    val minLevel: LogLevel = LogLevel.DEBUG,
    val crashReporting: Boolean = false
) {
    fun applyToTimber(isDebug: Boolean) {
        if (isDebug) {
            Timber.plant(Timber.DebugTree())
        }
        // Production tree 由 App 层自行植入（接入 Crashlytics 等）
    }
}
```

- [ ] **Step 8: 运行，确认绿**

```bash
./gradlew :core-foundation:test --tests "*.AppLoggerTest"
```

Expected: `BUILD SUCCESSFUL` — 3 tests passed

- [ ] **Step 9: Commit**

```bash
git add core-foundation/
git commit -m "feat(core-foundation): add AppLogger facade with TimberLogger and NoOp implementations"
```

---

## Task 11: core-foundation — Network 层

**Files:**
- Create: `network/NetworkConfig.kt`
- Create: `network/NetworkManager.kt`
- Create: `network/BaseResponse.kt`
- Create: `network/ApiException.kt`
- Create: `network/ApiService.kt`
- Create: `network/auth/AuthInterceptor.kt`
- Create: `network/auth/TokenAuthenticator.kt`
- Create: `network/interceptor/CommonHeaderInterceptor.kt`
- Create: `network/serializer/JsonSerializer.kt`
- Create: `src/test/.../NetworkManagerTest.kt`

- [ ] **Step 1: 创建 `BaseResponse.kt`**

```kotlin
package com.skybound.space.core.network

import com.google.gson.annotations.SerializedName

data class BaseResponse<T>(
    @SerializedName("code") val code: Int,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: T?
) {
    val isSuccess: Boolean get() = code == 200
}
```

- [ ] **Step 2: 创建 `ApiException.kt`**

```kotlin
package com.skybound.space.core.network

sealed class ApiException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    class NetworkException(message: String, cause: Throwable? = null) :
        ApiException("Network error: $message", cause)
    class ServerException(val code: Int, message: String) :
        ApiException("Server error $code: $message")
    class UnauthorizedException : ApiException("Unauthorized — token expired or invalid")
    class UnknownException(cause: Throwable) : ApiException("Unknown error", cause)
}
```

- [ ] **Step 3: 创建 `NetworkConfig.kt`**

```kotlin
package com.skybound.space.core.network

data class NetworkConfig(
    val baseUrl: String,
    val connectTimeoutSeconds: Long = 30,
    val readTimeoutSeconds: Long = 30,
    val writeTimeoutSeconds: Long = 30,
    val enableLogging: Boolean = false,
    val certificatePins: List<String> = emptyList()
)
```

- [ ] **Step 4: 创建 `auth/AuthInterceptor.kt`**

```kotlin
package com.skybound.space.core.network.auth

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenProvider: () -> String?) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokenProvider()
        val request = if (token != null) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }
        return chain.proceed(request)
    }
}
```

- [ ] **Step 5: 创建 `auth/TokenAuthenticator.kt`**

```kotlin
package com.skybound.space.core.network.auth

import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(
    private val tokenRefresher: suspend () -> String?
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        // 避免无限重试
        if (response.request.header("Authorization") == null) return null
        return null // App 层用协程刷新，此处返回 null 让 App 层处理 401
    }
}
```

- [ ] **Step 6: 创建 `interceptor/CommonHeaderInterceptor.kt`**

```kotlin
package com.skybound.space.core.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response

class CommonHeaderInterceptor(
    private val headersProvider: () -> Map<String, String>
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        headersProvider().forEach { (key, value) -> builder.addHeader(key, value) }
        return chain.proceed(builder.build())
    }
}
```

- [ ] **Step 7: 创建 `serializer/JsonSerializer.kt`**

```kotlin
package com.skybound.space.core.network.serializer

import com.google.gson.Gson
import com.google.gson.GsonBuilder

object JsonSerializer {
    fun createGson(): Gson = GsonBuilder()
        .setLenient()
        .create()
}
```

- [ ] **Step 8: 创建 `ApiService.kt`**

```kotlin
package com.skybound.space.core.network

import retrofit2.Retrofit

interface ApiService

inline fun <reified T : ApiService> Retrofit.createService(): T = create(T::class.java)
```

- [ ] **Step 9: 写 NetworkManager 测试（TDD — 红）**

创建 `core-foundation/src/test/java/com/skybound/space/core/network/NetworkManagerTest.kt`:

```kotlin
package com.skybound.space.core.network

import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import retrofit2.http.GET

class NetworkManagerTest {

    private lateinit var server: MockWebServer

    interface TestApi : ApiService {
        @GET("/test")
        suspend fun get(): BaseResponse<String>
    }

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `NetworkManager creates valid Retrofit instance`() {
        val config = NetworkConfig(
            baseUrl = server.url("/").toString(),
            enableLogging = false
        )
        val manager = NetworkManager(config)
        val api = manager.createApi(TestApi::class.java)
        assertNotNull(api)
    }

    @Test
    fun `NetworkManager executes GET request successfully`() = runTest {
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("""{"code":200,"message":"ok","data":"hello"}""")
        )
        val config = NetworkConfig(baseUrl = server.url("/").toString())
        val manager = NetworkManager(config)
        val api = manager.createApi(TestApi::class.java)
        val response = api.get()
        assert(response.isSuccess)
        assert(response.data == "hello")
    }
}
```

- [ ] **Step 10: 实现 `NetworkManager.kt`**

```kotlin
package com.skybound.space.core.network

import com.skybound.space.core.network.interceptor.CommonHeaderInterceptor
import com.skybound.space.core.network.serializer.JsonSerializer
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class NetworkManager(private val config: NetworkConfig) {

    private val okHttpClient: OkHttpClient by lazy { buildOkHttpClient() }
    private val retrofit: Retrofit by lazy { buildRetrofit() }

    private fun buildOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(config.connectTimeoutSeconds, TimeUnit.SECONDS)
            .readTimeout(config.readTimeoutSeconds, TimeUnit.SECONDS)
            .writeTimeout(config.writeTimeoutSeconds, TimeUnit.SECONDS)
            .apply {
                if (config.enableLogging) {
                    addInterceptor(
                        HttpLoggingInterceptor().apply {
                            level = HttpLoggingInterceptor.Level.BODY
                        }
                    )
                }
            }
            .build()
    }

    private fun buildRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(config.baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(JsonSerializer.createGson()))
            .build()
    }

    open fun <T> createApi(clazz: Class<T>): T = retrofit.create(clazz)

    fun withAuth(tokenProvider: () -> String?): NetworkManager {
        val authClient = okHttpClient.newBuilder()
            .addInterceptor(com.skybound.space.core.network.auth.AuthInterceptor(tokenProvider))
            .build()
        val authRetrofit = retrofit.newBuilder().client(authClient).build()
        return NetworkManagerWithClient(config, authRetrofit)
    }

    private class NetworkManagerWithClient(
        config: NetworkConfig,
        private val customRetrofit: Retrofit
    ) : NetworkManager(config) {
        override fun <T> createApi(clazz: Class<T>): T = customRetrofit.create(clazz)
    }
}
```

- [ ] **Step 11: 运行，确认绿**

```bash
./gradlew :core-foundation:test --tests "*.NetworkManagerTest"
```

Expected: `BUILD SUCCESSFUL` — 2 tests passed

- [ ] **Step 12: Commit**

```bash
git add core-foundation/src/main/java/com/skybound/space/core/network/ \
         core-foundation/src/test/
git commit -m "feat(core-foundation): add NetworkManager with Retrofit/OkHttp, auth interceptors"
```

---

## Task 12: core-foundation — Storage + ImageLoader + Monitoring 接口

**Files:**
- Create: `storage/DataStoreManager.kt`
- Create: `storage/EncryptedPrefsManager.kt`
- Create: `storage/StorageKeys.kt`
- Create: `imageloader/ImageLoader.kt`
- Create: `imageloader/CoilImageLoader.kt`
- Create: `monitoring/IExceptionMonitor.kt`
- Create: `monitoring/IPerformanceMonitor.kt`
- Create: `monitoring/ITrackManager.kt`
- Create: `monitoring/MonitoringNames.kt`

- [ ] **Step 1: 创建 `StorageKeys.kt`**

```kotlin
package com.skybound.space.core.storage

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object StorageKeys {
    // App 层通过继承此 object 或直接创建 Key 扩展
    fun stringKey(name: String): Preferences.Key<String> = stringPreferencesKey(name)
    fun intKey(name: String): Preferences.Key<Int> = intPreferencesKey(name)
    fun longKey(name: String): Preferences.Key<Long> = longPreferencesKey(name)
    fun boolKey(name: String): Preferences.Key<Boolean> = booleanPreferencesKey(name)
}
```

- [ ] **Step 2: 创建 `DataStoreManager.kt`**

```kotlin
package com.skybound.space.core.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_prefs")

class DataStoreManager(private val context: Context) {

    fun <T> get(key: Preferences.Key<T>, default: T): Flow<T> =
        context.dataStore.data.map { prefs -> prefs[key] ?: default }

    suspend fun <T> set(key: Preferences.Key<T>, value: T) {
        context.dataStore.edit { prefs -> prefs[key] = value }
    }

    suspend fun <T> remove(key: Preferences.Key<T>) {
        context.dataStore.edit { prefs -> prefs.remove(key) }
    }

    suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }
}
```

- [ ] **Step 3: 创建 `EncryptedPrefsManager.kt`**

```kotlin
package com.skybound.space.core.storage

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class EncryptedPrefsManager(context: Context, name: String = "secure_prefs") {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context,
        name,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun getString(key: String, default: String? = null): String? =
        prefs.getString(key, default)

    fun putString(key: String, value: String) =
        prefs.edit().putString(key, value).apply()

    fun remove(key: String) =
        prefs.edit().remove(key).apply()

    fun clearAll() =
        prefs.edit().clear().apply()
}
```

- [ ] **Step 4: 创建 ImageLoader 接口和 Coil 实现**

`imageloader/ImageLoader.kt`:
```kotlin
package com.skybound.space.core.imageloader

import android.widget.ImageView

interface ImageLoader {
    fun load(imageView: ImageView, url: String?)
    fun loadCircle(imageView: ImageView, url: String?)
    fun loadRounded(imageView: ImageView, url: String?, radiusDp: Int)
    fun loadWithPlaceholder(imageView: ImageView, url: String?, placeholderRes: Int)
}
```

`imageloader/CoilImageLoader.kt`:
```kotlin
package com.skybound.space.core.imageloader

import android.widget.ImageView
import coil.load
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation

class CoilImageLoader : ImageLoader {

    override fun load(imageView: ImageView, url: String?) {
        imageView.load(url)
    }

    override fun loadCircle(imageView: ImageView, url: String?) {
        imageView.load(url) {
            transformations(CircleCropTransformation())
        }
    }

    override fun loadRounded(imageView: ImageView, url: String?, radiusDp: Int) {
        val radiusPx = radiusDp * imageView.resources.displayMetrics.density
        imageView.load(url) {
            transformations(RoundedCornersTransformation(radiusPx))
        }
    }

    override fun loadWithPlaceholder(imageView: ImageView, url: String?, placeholderRes: Int) {
        imageView.load(url) {
            placeholder(placeholderRes)
            error(placeholderRes)
        }
    }
}
```

- [ ] **Step 5: 创建 Monitoring 接口（无 SDK 依赖）**

`monitoring/IExceptionMonitor.kt`:
```kotlin
package com.skybound.space.core.monitoring

interface IExceptionMonitor {
    fun recordException(throwable: Throwable)
    fun recordBreadcrumb(message: String, category: String = "app")
    fun setUserId(userId: String?)
}

object NoOpExceptionMonitor : IExceptionMonitor {
    override fun recordException(throwable: Throwable) = Unit
    override fun recordBreadcrumb(message: String, category: String) = Unit
    override fun setUserId(userId: String?) = Unit
}
```

`monitoring/IPerformanceMonitor.kt`:
```kotlin
package com.skybound.space.core.monitoring

interface IPerformanceMonitor {
    fun startTrace(name: String)
    fun stopTrace(name: String)
    fun putMetric(traceName: String, metricName: String, value: Long)
}

object NoOpPerformanceMonitor : IPerformanceMonitor {
    override fun startTrace(name: String) = Unit
    override fun stopTrace(name: String) = Unit
    override fun putMetric(traceName: String, metricName: String, value: Long) = Unit
}
```

`monitoring/ITrackManager.kt`:
```kotlin
package com.skybound.space.core.monitoring

interface ITrackManager {
    fun trackEvent(name: String, params: Map<String, Any> = emptyMap())
    fun trackScreen(screenName: String)
    fun setUserProperty(key: String, value: String)
}

object NoOpTrackManager : ITrackManager {
    override fun trackEvent(name: String, params: Map<String, Any>) = Unit
    override fun trackScreen(screenName: String) = Unit
    override fun setUserProperty(key: String, value: String) = Unit
}
```

`monitoring/MonitoringNames.kt`:
```kotlin
package com.skybound.space.core.monitoring

object MonitoringNames {
    // Screens — App 层扩展此 object 添加自己的屏幕名
    object Screens {
        const val HOME = "home"
        const val LOGIN = "login"
    }

    // Events — App 层扩展此 object
    object Events {
        const val APP_OPEN = "app_open"
        const val LOGIN_SUCCESS = "login_success"
        const val LOGIN_FAILED = "login_failed"
    }

    // Traces
    object Traces {
        const val APP_START = "app_start"
        const val DATA_LOAD = "data_load"
    }
}
```

- [ ] **Step 6: 编译验证**

```bash
./gradlew :core-foundation:assembleRelease
```

Expected: `BUILD SUCCESSFUL`

- [ ] **Step 7: Commit**

```bash
git add core-foundation/src/main/java/com/skybound/space/core/storage/ \
         core-foundation/src/main/java/com/skybound/space/core/imageloader/ \
         core-foundation/src/main/java/com/skybound/space/core/monitoring/
git commit -m "feat(core-foundation): add Storage, ImageLoader, and Monitoring interfaces"
```

---

## Task 13: core-foundation — CoreFoundation 初始化 + DI Module

**Files:**
- Create: `CoreFoundation.kt`
- Create: `dispatcher/AppCoroutineDispatchers.kt`
- Create: `security/EncryptionUtils.kt`
- Create: `navigation/NavigationManager.kt`
- Create: `di/AppInjector.kt`
- Create: `di/CoreModule.kt`

- [ ] **Step 1: 创建 `dispatcher/AppCoroutineDispatchers.kt`**

```kotlin
package com.skybound.space.core.dispatcher

import com.skybound.space.base.coroutines.CoroutineDispatchers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppCoroutineDispatchers @Inject constructor() : CoroutineDispatchers {
    override val main: CoroutineDispatcher = Dispatchers.Main
    override val io: CoroutineDispatcher = Dispatchers.IO
    override val default: CoroutineDispatcher = Dispatchers.Default
    override val unconfined: CoroutineDispatcher = Dispatchers.Unconfined
}
```

- [ ] **Step 2: 创建 `security/EncryptionUtils.kt`**

```kotlin
package com.skybound.space.core.security

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

object EncryptionUtils {

    private const val ALGORITHM = "AES/GCM/NoPadding"
    private const val GCM_IV_LENGTH = 12
    private const val GCM_TAG_LENGTH = 128

    fun encrypt(plainText: String, key: SecretKey): String {
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val iv = cipher.iv
        val encrypted = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
        val combined = iv + encrypted
        return Base64.encodeToString(combined, Base64.DEFAULT)
    }

    fun decrypt(encryptedText: String, key: SecretKey): String {
        val combined = Base64.decode(encryptedText, Base64.DEFAULT)
        val iv = combined.copyOfRange(0, GCM_IV_LENGTH)
        val encrypted = combined.copyOfRange(GCM_IV_LENGTH, combined.size)
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(GCM_TAG_LENGTH, iv))
        return String(cipher.doFinal(encrypted), Charsets.UTF_8)
    }

    fun generateKey(): SecretKey {
        val generator = KeyGenerator.getInstance("AES")
        generator.init(256)
        return generator.generateKey()
    }
}
```

- [ ] **Step 3: 创建 `navigation/NavigationManager.kt`**

```kotlin
package com.skybound.space.core.navigation

import android.content.Intent
import androidx.fragment.app.FragmentActivity
import com.skybound.space.base.presentation.navigation.NavDestination

class NavigationManager(private val activity: FragmentActivity) {

    fun navigateTo(destination: NavDestination) {
        // App 层重写或通过 NavController 实现
        // 此处提供基础深链接能力
    }

    fun navigateWithIntent(intent: Intent) {
        activity.startActivity(intent)
    }

    fun navigateBack() {
        activity.onBackPressedDispatcher.onBackPressed()
    }
}
```

- [ ] **Step 4: 创建 `di/CoreModule.kt`**

```kotlin
package com.skybound.space.core.di

import android.content.Context
import com.skybound.space.base.coroutines.CoroutineDispatchers
import com.skybound.space.core.dispatcher.AppCoroutineDispatchers
import com.skybound.space.core.imageloader.CoilImageLoader
import com.skybound.space.core.imageloader.ImageLoader
import com.skybound.space.core.log.AppLogger
import com.skybound.space.core.log.NoOpLogger
import com.skybound.space.core.monitoring.IExceptionMonitor
import com.skybound.space.core.monitoring.IPerformanceMonitor
import com.skybound.space.core.monitoring.ITrackManager
import com.skybound.space.core.monitoring.NoOpExceptionMonitor
import com.skybound.space.core.monitoring.NoOpPerformanceMonitor
import com.skybound.space.core.monitoring.NoOpTrackManager
import com.skybound.space.core.storage.DataStoreManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoreModule {

    @Provides
    @Singleton
    fun provideCoroutineDispatchers(impl: AppCoroutineDispatchers): CoroutineDispatchers = impl

    @Provides
    @Singleton
    fun provideDataStoreManager(@ApplicationContext context: Context): DataStoreManager =
        DataStoreManager(context)

    @Provides
    @Singleton
    fun provideImageLoader(): ImageLoader = CoilImageLoader()

    // Monitoring — NoOp 默认，App 层用 @Binds 替换为真实实现
    @Provides
    @Singleton
    fun provideExceptionMonitor(): IExceptionMonitor = NoOpExceptionMonitor

    @Provides
    @Singleton
    fun providePerformanceMonitor(): IPerformanceMonitor = NoOpPerformanceMonitor

    @Provides
    @Singleton
    fun provideTrackManager(): ITrackManager = NoOpTrackManager

    // AppLogger — NoOp 默认，App 层替换为 TimberLogger
    @Provides
    @Singleton
    fun provideAppLogger(): AppLogger = NoOpLogger
}
```

- [ ] **Step 5: 创建 `di/AppInjector.kt`**

```kotlin
package com.skybound.space.core.di

import android.content.Context
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.skybound.space.base.coroutines.CoroutineDispatchers
import com.skybound.space.core.imageloader.ImageLoader
import com.skybound.space.core.monitoring.ITrackManager
import com.skybound.space.core.storage.DataStoreManager

@EntryPoint
@InstallIn(SingletonComponent::class)
interface CoreEntryPoint {
    fun coroutineDispatchers(): CoroutineDispatchers
    fun dataStoreManager(): DataStoreManager
    fun imageLoader(): ImageLoader
    fun trackManager(): ITrackManager
}

object AppInjector {
    fun from(context: Context): CoreEntryPoint =
        EntryPoints.get(context.applicationContext, CoreEntryPoint::class.java)
}
```

- [ ] **Step 6: 创建 `CoreFoundation.kt`**

```kotlin
package com.skybound.space.core

import android.app.Application
import com.skybound.space.core.log.AppLogger
import com.skybound.space.core.log.AppLoggerProvider
import com.skybound.space.core.log.LogConfig
import com.skybound.space.core.log.TimberLogger
import com.skybound.space.core.network.NetworkConfig
import com.skybound.space.core.network.NetworkManager
import com.skybound.space.core.storage.DataStoreManager

class CoreFoundation private constructor() {

    companion object {
        private var _networkManager: NetworkManager? = null
        val networkManager: NetworkManager
            get() = _networkManager ?: error("CoreFoundation not initialized. Call init() first.")

        fun init(app: Application, block: CoreFoundationBuilder.() -> Unit) {
            val builder = CoreFoundationBuilder().apply(block)
            builder.logConfig?.applyToTimber(isDebug = builder.isDebug)
            if (builder.useTimberLogger) {
                AppLoggerProvider.setLogger(TimberLogger())
            }
            builder.networkConfig?.let { _networkManager = NetworkManager(it) }
        }
    }
}

class CoreFoundationBuilder {
    var isDebug: Boolean = false
    var useTimberLogger: Boolean = true
    internal var networkConfig: NetworkConfig? = null
    internal var logConfig: LogConfig? = null

    fun network(block: NetworkConfigBuilder.() -> Unit) {
        networkConfig = NetworkConfigBuilder().apply(block).build()
    }

    fun logging(block: LogConfigBuilder.() -> Unit) {
        logConfig = LogConfigBuilder().apply(block).build()
    }
}

class NetworkConfigBuilder {
    var baseUrl: String = ""
    var connectTimeout: Long = 30
    var readTimeout: Long = 30
    var enableLogging: Boolean = false

    fun build() = NetworkConfig(
        baseUrl = baseUrl,
        connectTimeoutSeconds = connectTimeout,
        readTimeoutSeconds = readTimeout,
        enableLogging = enableLogging
    )
}

class LogConfigBuilder {
    var minLevel: com.skybound.space.core.log.LogLevel = com.skybound.space.core.log.LogLevel.DEBUG
    var crashReporting: Boolean = false

    fun build() = LogConfig(minLevel = minLevel, crashReporting = crashReporting)
}
```

- [ ] **Step 7: 完整编译验证**

```bash
./gradlew :core-foundation:assembleRelease
```

Expected: `BUILD SUCCESSFUL`

- [ ] **Step 8: Commit**

```bash
git add core-foundation/src/main/java/com/skybound/space/core/
git commit -m "feat(core-foundation): add CoreFoundation init, Hilt CoreModule, AppInjector"
```

---

## Task 14: core-domain — UseCase 基类（TDD）

**Files:**
- Create: `core-domain/build.gradle.kts`
- Create: `core-domain/src/main/AndroidManifest.xml`
- Create: `core-domain/src/main/java/com/skybound/space/domain/usecase/UseCase.kt`
- Create: `core-domain/src/main/java/com/skybound/space/domain/usecase/FlowUseCase.kt`
- Create: `core-domain/src/main/java/com/skybound/space/domain/repository/IRepository.kt`
- Create: `core-domain/src/main/java/com/skybound/space/domain/model/BaseDomainModel.kt`
- Create: `core-domain/src/test/java/com/skybound/space/domain/usecase/UseCaseTest.kt`

- [ ] **Step 1: 创建 `core-domain/build.gradle.kts`**

```kotlin
plugins {
    id("skybound.android.library")
    id("skybound.android.publish")
}

android {
    namespace = "com.skybound.space.domain"
}

dependencies {
    api(project(":core-base"))
    implementation(libs.coroutines.core)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.turbine)
}
```

- [ ] **Step 2: 创建 `core-domain/src/main/AndroidManifest.xml`**

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest />
```

- [ ] **Step 3: 写 UseCase 测试（TDD — 红）**

创建 `core-domain/src/test/java/com/skybound/space/domain/usecase/UseCaseTest.kt`:

```kotlin
package com.skybound.space.domain.usecase

import app.cash.turbine.test
import com.skybound.space.base.coroutines.CoroutineDispatchers
import com.skybound.space.base.result.AppError
import com.skybound.space.base.result.AppResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UseCaseTest {

    private val testDispatchers = object : CoroutineDispatchers {
        override val main: CoroutineDispatcher = Dispatchers.Unconfined
        override val io: CoroutineDispatcher = Dispatchers.Unconfined
        override val default: CoroutineDispatcher = Dispatchers.Unconfined
        override val unconfined: CoroutineDispatcher = Dispatchers.Unconfined
    }

    private inner class DoubleUseCase : UseCase<Int, Int>(testDispatchers) {
        override suspend fun execute(params: Int): AppResult<Int> =
            AppResult.Success(params * 2)
    }

    private inner class FailingUseCase : UseCase<Unit, String>(testDispatchers) {
        override suspend fun execute(params: Unit): AppResult<String> =
            AppResult.Failure(AppError.Unknown)
    }

    private inner class StreamUseCase : FlowUseCase<Int, Int>(testDispatchers) {
        override fun execute(params: Int): Flow<AppResult<Int>> = flow {
            emit(AppResult.Loading)
            emit(AppResult.Success(params * 3))
        }
    }

    @Test
    fun `UseCase returns Success with doubled value`() = runTest {
        val result = DoubleUseCase()(5)
        assertEquals(AppResult.Success(10), result)
    }

    @Test
    fun `UseCase returns Failure`() = runTest {
        val result = FailingUseCase()(Unit)
        assertTrue(result is AppResult.Failure)
    }

    @Test
    fun `FlowUseCase emits Loading then Success`() = runTest {
        StreamUseCase()(4).test {
            assertEquals(AppResult.Loading, awaitItem())
            assertEquals(AppResult.Success(12), awaitItem())
            awaitComplete()
        }
    }
}
```

- [ ] **Step 4: 运行，确认红**

```bash
./gradlew :core-domain:test --tests "*.UseCaseTest"
```

Expected: BUILD FAILED — `UseCase` not found

- [ ] **Step 5: 实现 `UseCase.kt` 和 `FlowUseCase.kt`**

`usecase/UseCase.kt`:
```kotlin
package com.skybound.space.domain.usecase

import com.skybound.space.base.coroutines.CoroutineDispatchers
import com.skybound.space.base.result.AppResult
import kotlinx.coroutines.withContext

abstract class UseCase<in P, out R>(
    private val dispatchers: CoroutineDispatchers
) {
    suspend operator fun invoke(params: P): AppResult<R> =
        withContext(dispatchers.io) { execute(params) }

    protected abstract suspend fun execute(params: P): AppResult<R>
}
```

`usecase/FlowUseCase.kt`:
```kotlin
package com.skybound.space.domain.usecase

import com.skybound.space.base.coroutines.CoroutineDispatchers
import com.skybound.space.base.result.AppError
import com.skybound.space.base.result.AppResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn

abstract class FlowUseCase<in P, out R>(
    private val dispatchers: CoroutineDispatchers
) {
    operator fun invoke(params: P): Flow<AppResult<R>> =
        execute(params)
            .catch { e -> emit(AppResult.Failure(AppError.Local(e))) }
            .flowOn(dispatchers.io)

    protected abstract fun execute(params: P): Flow<AppResult<R>>
}
```

- [ ] **Step 6: 创建其余文件**

`repository/IRepository.kt`:
```kotlin
package com.skybound.space.domain.repository

interface IRepository
```

`model/BaseDomainModel.kt`:
```kotlin
package com.skybound.space.domain.model

interface BaseDomainModel
```

- [ ] **Step 7: 运行，确认绿**

```bash
./gradlew :core-domain:test
```

Expected: `BUILD SUCCESSFUL` — 3 tests passed

- [ ] **Step 8: Commit**

```bash
git add core-domain/
git commit -m "feat(core-domain): add UseCase and FlowUseCase base classes"
```

---

## Task 15: core-data — BaseRepository（TDD）

**Files:**
- Create: `core-data/build.gradle.kts`
- Create: `core-data/src/main/AndroidManifest.xml`
- Create: `core-data/src/main/java/com/skybound/space/data/repository/BaseRepository.kt`
- Create: `core-data/src/main/java/com/skybound/space/data/remote/BaseRemoteDataSource.kt`
- Create: `core-data/src/main/java/com/skybound/space/data/mapper/BaseMapper.kt`
- Create: `core-data/src/test/java/com/skybound/space/data/repository/BaseRepositoryTest.kt`

- [ ] **Step 1: 创建 `core-data/build.gradle.kts`**

```kotlin
plugins {
    id("skybound.android.library")
    id("skybound.android.hilt")
    id("skybound.android.publish")
}

android {
    namespace = "com.skybound.space.data"
}

dependencies {
    api(project(":core-base"))
    api(project(":core-domain"))
    implementation(project(":core-foundation"))

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.room.paging)
    ksp(libs.room.compiler)

    // Paging
    implementation(libs.paging.runtime)

    // Network (for BaseResponse)
    implementation(libs.retrofit.core)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutines.test)
}
```

- [ ] **Step 2: 创建 `core-data/src/main/AndroidManifest.xml`**

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest />
```

- [ ] **Step 3: 写 BaseRepository 测试（TDD — 红）**

创建 `core-data/src/test/java/com/skybound/space/data/repository/BaseRepositoryTest.kt`:

```kotlin
package com.skybound.space.data.repository

import com.skybound.space.base.result.AppError
import com.skybound.space.base.result.AppResult
import com.skybound.space.core.network.BaseResponse
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class BaseRepositoryTest {

    private val repo = object : BaseRepository() {
        suspend fun <T> callApi(block: suspend () -> BaseResponse<T>) = safeApiCall(block)
        suspend fun <T> callDb(block: suspend () -> T) = safeDbCall(block)
    }

    @Test
    fun `safeApiCall returns Success when response isSuccess`() = runTest {
        val result = repo.callApi {
            BaseResponse(code = 200, message = "ok", data = "payload")
        }
        assertEquals(AppResult.Success("payload"), result)
    }

    @Test
    fun `safeApiCall returns Server error when code is not 200`() = runTest {
        val result = repo.callApi<String> {
            BaseResponse(code = 400, message = "bad request", data = null)
        }
        assertTrue(result is AppResult.Failure)
        val error = (result as AppResult.Failure).error
        assertTrue(error is AppError.Server)
        assertEquals(400, (error as AppError.Server).code)
    }

    @Test
    fun `safeApiCall returns Local error on IOException`() = runTest {
        val result = repo.callApi<String> {
            throw IOException("timeout")
        }
        assertTrue(result is AppResult.Failure)
        assertTrue((result as AppResult.Failure).error is AppError.Local)
    }

    @Test
    fun `safeDbCall returns Success`() = runTest {
        val result = repo.callDb { 42 }
        assertEquals(AppResult.Success(42), result)
    }

    @Test
    fun `safeDbCall returns Local error on exception`() = runTest {
        val result = repo.callDb<Int> { throw RuntimeException("db error") }
        assertTrue(result is AppResult.Failure)
        assertTrue((result as AppResult.Failure).error is AppError.Local)
    }
}
```

- [ ] **Step 4: 运行，确认红**

```bash
./gradlew :core-data:test --tests "*.BaseRepositoryTest"
```

Expected: BUILD FAILED — `BaseRepository` not found

- [ ] **Step 5: 实现 `BaseRepository.kt`**

```kotlin
package com.skybound.space.data.repository

import com.skybound.space.base.result.AppError
import com.skybound.space.base.result.AppResult
import com.skybound.space.core.network.BaseResponse
import retrofit2.HttpException
import java.io.IOException

abstract class BaseRepository {

    protected suspend fun <T> safeApiCall(
        call: suspend () -> BaseResponse<T>
    ): AppResult<T> = try {
        val response = call()
        if (response.isSuccess && response.data != null) {
            AppResult.Success(response.data)
        } else {
            AppResult.Failure(AppError.Server(response.code, response.message))
        }
    } catch (e: HttpException) {
        if (e.code() == 401) AppResult.Failure(AppError.Unauthorized)
        else AppResult.Failure(AppError.Network(e.code(), e.message()))
    } catch (e: IOException) {
        AppResult.Failure(AppError.Local(e))
    } catch (e: Exception) {
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

- [ ] **Step 6: 创建 `BaseRemoteDataSource.kt`**

```kotlin
package com.skybound.space.data.remote

import com.skybound.space.base.result.AppError
import com.skybound.space.base.result.AppResult
import com.skybound.space.core.network.BaseResponse
import retrofit2.HttpException
import java.io.IOException

abstract class BaseRemoteDataSource {

    protected suspend fun <T> safeCall(
        call: suspend () -> BaseResponse<T>
    ): AppResult<T> = try {
        val response = call()
        if (response.isSuccess && response.data != null) {
            AppResult.Success(response.data)
        } else {
            AppResult.Failure(AppError.Server(response.code, response.message))
        }
    } catch (e: HttpException) {
        AppResult.Failure(AppError.Network(e.code(), e.message()))
    } catch (e: IOException) {
        AppResult.Failure(AppError.Local(e))
    }
}
```

- [ ] **Step 7: 创建 `BaseMapper.kt`**

```kotlin
package com.skybound.space.data.mapper

interface BaseMapper<in From, out To> {
    fun map(from: From): To
}

fun <From, To> BaseMapper<From, To>.mapList(from: List<From>): List<To> =
    from.map { map(it) }
```

- [ ] **Step 8: 运行，确认绿**

```bash
./gradlew :core-data:test --tests "*.BaseRepositoryTest"
```

Expected: `BUILD SUCCESSFUL` — 5 tests passed

- [ ] **Step 9: Commit**

```bash
git add core-data/
git commit -m "feat(core-data): add BaseRepository with safeApiCall/safeDbCall, BaseMapper"
```

---

## Task 16: core-data — Room 基类 + BasePagingSource（TDD）

**Files:**
- Create: `local/dao/BaseDao.kt`
- Create: `local/db/BaseDatabase.kt`
- Create: `paging/BasePagingSource.kt`
- Create: `paging/PagedResult.kt`
- Create: `paging/PagingExt.kt`
- Create: `di/DataModule.kt`
- Test: `paging/BasePagingSourceTest.kt`

- [ ] **Step 1: 创建 `local/dao/BaseDao.kt`**

```kotlin
package com.skybound.space.data.local.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

interface BaseDao<T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: T)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<T>)

    @Update
    suspend fun update(entity: T)

    @Delete
    suspend fun delete(entity: T)
}
```

- [ ] **Step 2: 创建 `local/db/BaseDatabase.kt`**

```kotlin
package com.skybound.space.data.local.db

import androidx.room.RoomDatabase

abstract class BaseDatabase : RoomDatabase() {
    // App 层继承，添加 @Database 注解和具体 DAO getter
}
```

- [ ] **Step 3: 创建 `paging/PagedResult.kt`**

```kotlin
package com.skybound.space.data.paging

data class PagedResult<T>(
    val items: List<T>,
    val hasMore: Boolean,
    val totalCount: Int = -1  // -1 表示未知
)
```

- [ ] **Step 4: 写 BasePagingSource 测试（TDD — 红）**

创建 `core-data/src/test/java/com/skybound/space/data/paging/BasePagingSourceTest.kt`:

```kotlin
package com.skybound.space.data.paging

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BasePagingSourceTest {

    private val fakeData = (1..25).map { "item$it" }

    private val pagingSource = object : BasePagingSource<String>() {
        override suspend fun fetch(page: Int, pageSize: Int): PagedResult<String> {
            val start = (page - 1) * pageSize
            val end = minOf(start + pageSize, fakeData.size)
            return if (start >= fakeData.size) {
                PagedResult(emptyList(), hasMore = false)
            } else {
                PagedResult(
                    items = fakeData.subList(start, end),
                    hasMore = end < fakeData.size
                )
            }
        }
    }

    @Test
    fun `first page load returns correct items`() = runTest {
        val params = PagingSource.LoadParams.Refresh(
            key = null,
            loadSize = 10,
            placeholdersEnabled = false
        )
        val result = pagingSource.load(params) as PagingSource.LoadResult.Page
        assertEquals(10, result.data.size)
        assertEquals("item1", result.data.first())
        assertNull(result.prevKey)
        assertEquals(2, result.nextKey)
    }

    @Test
    fun `last page has no nextKey`() = runTest {
        val params = PagingSource.LoadParams.Refresh(
            key = 3,
            loadSize = 10,
            placeholdersEnabled = false
        )
        val result = pagingSource.load(params) as PagingSource.LoadResult.Page
        assertEquals(5, result.data.size) // items 21-25
        assertNull(result.nextKey)
    }

    @Test
    fun `error during fetch returns LoadResult Error`() = runTest {
        val failingSource = object : BasePagingSource<String>() {
            override suspend fun fetch(page: Int, pageSize: Int): PagedResult<String> {
                throw RuntimeException("network error")
            }
        }
        val params = PagingSource.LoadParams.Refresh<Int>(
            key = null, loadSize = 10, placeholdersEnabled = false
        )
        assertTrue(failingSource.load(params) is PagingSource.LoadResult.Error)
    }
}
```

- [ ] **Step 5: 运行，确认红**

```bash
./gradlew :core-data:test --tests "*.BasePagingSourceTest"
```

Expected: BUILD FAILED — `BasePagingSource` not found

- [ ] **Step 6: 实现 `BasePagingSource.kt`**

```kotlin
package com.skybound.space.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState

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

    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
    }

    protected abstract suspend fun fetch(page: Int, pageSize: Int): PagedResult<T>
}
```

- [ ] **Step 7: 创建 `paging/PagingExt.kt`**

```kotlin
package com.skybound.space.data.paging

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

fun <T : Any> createPager(
    pageSize: Int = 20,
    prefetchDistance: Int = pageSize,
    enablePlaceholders: Boolean = false,
    sourceFactory: () -> BasePagingSource<T>
): Flow<PagingData<T>> = Pager(
    config = PagingConfig(
        pageSize = pageSize,
        prefetchDistance = prefetchDistance,
        enablePlaceholders = enablePlaceholders
    ),
    pagingSourceFactory = sourceFactory
).flow
```

- [ ] **Step 8: 创建 `di/DataModule.kt`**

```kotlin
package com.skybound.space.data.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    // 空 Module，App 层创建自己的 @Module 提供具体 DB / DAO 实例
    // 例：@Provides fun provideDb(@ApplicationContext ctx: Context): AppDatabase = ...
}
```

- [ ] **Step 9: 运行所有测试**

```bash
./gradlew :core-data:test
```

Expected: `BUILD SUCCESSFUL` — 8 tests passed (5 from Task 15 + 3 new)

- [ ] **Step 10: Commit**

```bash
git add core-data/src/
git commit -m "feat(core-data): add Room base classes, BasePagingSource, PagingExt"
```

---

## Task 17: 全量编译 + 发布到 Maven Local

**Files:**
- Modify: `settings.gradle.kts`（确认所有模块已 include）

- [ ] **Step 1: 全量编译验证**

```bash
./gradlew assembleRelease
```

Expected: `BUILD SUCCESSFUL` — 所有模块编译通过

- [ ] **Step 2: 全量测试**

```bash
./gradlew test
```

Expected: `BUILD SUCCESSFUL` — 所有单元测试通过

- [ ] **Step 3: 发布全部模块到 Maven Local**

```bash
./gradlew publishAllPublicationsToMavenLocalRepository
```

Expected: 各模块 `.m2` 目录下生成 `com/skybound/space/core-xxx/1.0.0/` 文件

- [ ] **Step 4: 验证 Maven Local 产物存在**

Windows:
```bash
dir %USERPROFILE%\.m2\repository\com\skybound\space\
```

Expected: 看到 `core-base/`, `core-foundation/`, `core-domain/`, `core-data/`, `core-bom/`

- [ ] **Step 5: 在消费 App 的 `settings.gradle.kts` 添加 Composite Build 支持**

> 在你的其他 App 项目（如 SnapReceipt 或新 App）的 `settings.gradle.kts` 中添加：

```kotlin
// 调试框架时改为 true，平时保持 false
val debugFramework = false

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

- [ ] **Step 6: Commit**

```bash
git add .
git commit -m "chore: verify full build and Maven Local publishing"
```

---

## Task 18: 契约文档（Phase 3 准备）

**Files:**
- Create: `docs/contracts/AppResult.md`
- Create: `docs/contracts/ErrorTypes.md`
- Create: `docs/contracts/UseCase.md`
- Create: `docs/changelog/BREAKING_CHANGES.md`

- [ ] **Step 1: 创建 `docs/contracts/AppResult.md`**

```markdown
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
```

- [ ] **Step 2: 创建 `docs/contracts/ErrorTypes.md`**

```markdown
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
```

- [ ] **Step 3: 创建 `docs/contracts/UseCase.md`**

```markdown
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

- UseCase 内部不 catch 异常（由 BaseRepository 处理）
- FlowUseCase 的 catch 操作符捕获流中异常并转为 `AppResult.Failure`
```

- [ ] **Step 4: 创建 `docs/changelog/BREAKING_CHANGES.md`**

```markdown
# Breaking Changes

记录所有影响双端（Android + Flutter）契约的破坏性变更。

## 格式

```
### vX.X.X — YYYY-MM-DD

**变更：** 描述变更内容
**影响：** Android / Flutter / 双端
**迁移：** 消费方需要做什么
```

## 历史记录

（暂无——v1.0.0 为首次发布）
```

- [ ] **Step 5: 最终 Commit**

```bash
git add docs/
git commit -m "docs: add contract documents for AppResult, ErrorTypes, UseCase, and BREAKING_CHANGES log"
```

---

## 完成验证清单

实施完成后，确认以下项目全部通过：

- [ ] `./gradlew test` — 全量单元测试绿
- [ ] `./gradlew assembleRelease` — 全量编译绿
- [ ] `./gradlew publishAllPublicationsToMavenLocalRepository` — Maven Local 发布成功
- [ ] 消费 App 中 `debugFramework = true` → Sync 成功 → 可断点进入框架源码
- [ ] 消费 App 中 `debugFramework = false` → 使用 Maven Local 包正常编译
- [ ] `docs/contracts/` 三个契约文档存在
- [ ] `docs/changelog/BREAKING_CHANGES.md` 存在
