plugins {
    id("template.android.library")
    id("template.android.compose")
    id("template.android.feature")
    id("template.android.hilt")
}

android {
    namespace = "com.template.auth"
}

dependencies {
    implementation(project(":core:ui"))
    implementation(project(":core:common"))
    implementation(project(":core:network"))
    implementation(project(":core:datastore"))
    implementation(project(":core:analytics"))

    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.truth)
    testImplementation(libs.turbine)

    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.androidx.test.junit)
}
