plugins {
    id("template.android.library")
    id("template.android.hilt")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.template.network"
}

dependencies {
    implementation(project(":core:common"))

    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.timber)
    implementation(libs.bundles.kotlin.coroutines)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.truth)
}
