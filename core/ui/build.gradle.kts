plugins {
    id("template.android.library")
    id("template.android.compose")
}

android {
    namespace = "com.template.ui"
}

dependencies {
    implementation(project(":core:common"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.google.material)
    implementation(libs.timber)

    testImplementation(libs.junit)
}
