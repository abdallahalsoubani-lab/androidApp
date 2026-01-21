package com.template.convention

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion

internal fun configureKotlinAndroid(
    commonExtension: CommonExtension<*, *, *, *, *>,
) {
    commonExtension.apply {
        compileSdk = 34

        defaultConfig {
            minSdk = 24
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }

        lint {
            baseline = File("lint-baseline.xml")
        }
    }
}
