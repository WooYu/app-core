plugins {
    `kotlin-dsl`
}

kotlin {
    jvmToolchain(17)
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
