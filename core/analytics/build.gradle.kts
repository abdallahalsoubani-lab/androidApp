plugins {
    id("template.android.library")
    id("template.android.hilt")
}

android {
    namespace = "com.template.analytics"
}

dependencies {
    implementation(libs.timber)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.truth)
}
