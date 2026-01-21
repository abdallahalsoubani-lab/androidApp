plugins {
    id("template.android.library")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.template.testing"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    implementation(project(":core:datastore"))
    implementation(project(":core:analytics"))

    implementation(libs.junit)
    implementation(libs.mockk)
    implementation(libs.truth)
    implementation(libs.turbine)
    implementation(libs.bundles.kotlin.coroutines)
    implementation(libs.androidx.test.core)
    implementation(libs.google.hilt.testing)
}
