pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "android-template"

includeBuild("build-logic")

// App
include(":app")

// Core modules
include(":core:ui")
include(":core:network")
include(":core:database")
include(":core:datastore")
include(":core:common")
include(":core:analytics")
include(":core:testing")

// Feature modules
include(":feature:auth")
include(":feature:template")
