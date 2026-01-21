plugins {
    id("template.android.library")
    id("template.android.hilt")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.template.common"
}

dependencies {
    implementation(libs.bundles.kotlin.coroutines)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.timber)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.truth)
    testImplementation(libs.turbine)
}
