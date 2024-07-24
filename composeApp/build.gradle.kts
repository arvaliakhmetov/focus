import org.gradle.internal.snapshot.PathUtil.getFileName
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.extraProperties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.squareup.sqldelight)
}

sqldelight {
    databases {
        create("FocusDatabase") {
            packageName = "ru.aval.focus.db"
        }
    }
}
kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
            linkerOpts.add("-lsqlite3")
            export(libs.decompose)
            export(libs.mvikotlin)
            export(libs.mvikotlin.main)
            export(libs.decompose.lifecycle)
            export(libs.decompose.extensions)

        }
    }
    
    sourceSets {
        
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.squareup.sqldelight.android.driver)
            implementation(libs.koin.android)
        }
        commonMain.dependencies {
            implementation(libs.icons.extended)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            api(libs.decompose)
            api(libs.decompose.extensions)
            api(libs.mvikotlin)
            api(libs.mvikotlin.main)
            api(libs.decompose.lifecycle)
            implementation(libs.mvikotlin.extensions.coroutines)
            implementation(libs.mvikotlin.logging)
            implementation(libs.mvikotlin.timetravel)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.koin.core)
            implementation(libs.squareup.sqldelight.coroutines)
        }
        iosMain.dependencies {
            implementation("co.touchlab:stately-common:2.0.6")
            implementation(libs.ktor.client.darwin)
            implementation(libs.squareup.sqldelight.native.driver)
        }
    }
}

android {
    namespace = "ru.aval.focus"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "ru.aval.focus"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
        getByName("debug") {
            isDebuggable = true
            isMinifyEnabled = false
            proguardFiles(getFileName("proguard-rules.pro"))
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
    dependencies {
        debugImplementation(compose.uiTooling)
    }
}


