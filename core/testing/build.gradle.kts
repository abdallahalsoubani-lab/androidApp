plugins {
    id("com.android.library") version "8.2.0"
    kotlin("android") version "1.9.22"
    kotlin("plugin.serialization") version "1.9.22"
}

android {
    namespace = "com.template.testing"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
        targetSdk = 34
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    implementation(project(":core:datastore"))
    implementation(project(":core:analytics"))

    implementation("junit:junit:4.13.2")
    implementation("io.mockk:mockk:1.13.8")
    implementation("com.google.truth:truth:1.1.5")
    implementation("app.cash.turbine:turbine:1.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("androidx.test:core:1.5.0")
    implementation("com.google.dagger:hilt-android-testing:2.50")
}
