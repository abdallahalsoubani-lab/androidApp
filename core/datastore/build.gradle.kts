plugins {
    id("template.android.library")
    id("template.android.hilt")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.template.datastore"
}

dependencies {
    implementation(project(":core:common"))

    implementation(libs.androidx.datastore)
    implementation(libs.androidx.datastore.preferences.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.timber)
    implementation(libs.bundles.kotlin.coroutines)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.truth)
}
