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
