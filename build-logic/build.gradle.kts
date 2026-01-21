plugins {
    `kotlin-dsl`
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.ksp.gradle.plugin)
    compileOnly(libs.hilt.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "template.android.application"
            implementationClass = "com.template.convention.AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "template.android.library"
            implementationClass = "com.template.convention.AndroidLibraryConventionPlugin"
        }
        register("androidCompose") {
            id = "template.android.compose"
            implementationClass = "com.template.convention.AndroidComposeConventionPlugin"
        }
        register("androidFeature") {
            id = "template.android.feature"
            implementationClass = "com.template.convention.AndroidFeatureConventionPlugin"
        }
        register("androidHilt") {
            id = "template.android.hilt"
            implementationClass = "com.template.convention.AndroidHiltConventionPlugin"
        }
        register("jvmLibrary") {
            id = "template.jvm.library"
            implementationClass = "com.template.convention.JvmLibraryConventionPlugin"
        }
    }
}
