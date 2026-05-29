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
