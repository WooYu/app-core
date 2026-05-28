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
        val version = "1.0.0" // TODO: Replace with libs.versions.appCore.get() once version catalog accessor is available
        api("com.skybound.space:core-base:$version")
        api("com.skybound.space:core-foundation:$version")
        api("com.skybound.space:core-domain:$version")
        api("com.skybound.space:core-data:$version")
    }
}
