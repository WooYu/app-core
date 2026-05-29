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
