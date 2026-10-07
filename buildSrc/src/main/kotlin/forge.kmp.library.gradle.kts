import com.android.build.gradle.LibraryExtension

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.library")
}

configure<LibraryExtension> {
    compileSdk = 35

    defaultConfig {
        minSdk = 26
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }
}
