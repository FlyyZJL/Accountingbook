pluginManagement {
    repositories {
        google {
            content {
                maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        maven { url = uri("https://jitpack.io")  }
        google()
        mavenCentral()
    }
}

rootProject.name = "Accountingbook"
include(":app")
 