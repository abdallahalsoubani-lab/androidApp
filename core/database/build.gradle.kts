plugins {
    id("template.android.library")
    id("template.android.hilt")
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.template.database"
}

dependencies {
    implementation(project(":core:common"))

    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.timber)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.truth)
}
