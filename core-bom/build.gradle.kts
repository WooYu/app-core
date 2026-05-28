plugins {
    `java-platform`
    id("skybound.android.publish")
}

// BOM 不能有依赖约束以外的依赖
javaPlatform {
    allowDependencies()
}

val versionCatalog = extensions.getByType<org.gradle.api.artifacts.VersionCatalogsExtension>().named("libs")
val appCoreVersion = versionCatalog.findVersion("app-core").get().requiredVersion

dependencies {
    constraints {
        api("com.skybound.space:core-base:$appCoreVersion")
        api("com.skybound.space:core-foundation:$appCoreVersion")
        api("com.skybound.space:core-domain:$appCoreVersion")
        api("com.skybound.space:core-data:$appCoreVersion")
    }
}
