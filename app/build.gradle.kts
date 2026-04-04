import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.masmultimedia.sospechapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.masmultimedia.sospechapp"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        manifestPlaceholders["ADMOB_APP_ID"] = "ca-app-pub-3940256099942544~3347511713"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // --- Keystore properties for release signing ---
    val keystoreProperties = Properties().apply {
        val keystoreFile = rootProject.file("keystore.properties")
        if (keystoreFile.exists()) {
            load(FileInputStream(keystoreFile))
        }
    }

    signingConfigs {
        create("release") {
            if (keystoreProperties.isNotEmpty()) {
                storeFile = file(keystoreProperties["storeFile"] as String)
                storePassword = keystoreProperties["storePassword"] as String
                keyAlias = keystoreProperties["keyAlias"] as String
                keyPassword = keystoreProperties["keyPassword"] as String
            }
        }
    }

    buildTypes {
        debug {
            buildConfigField("String", "PRIVACY_POLICY_URL", "\"https://manuelasan.github.io/SospechApp/privacy.html\"")
            buildConfigField("String", "ADMOB_BANNER_AD_UNIT_ID", "\"ca-app-pub-3940256099942544/6300978111\"")
        }

        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
            buildConfigField("String", "PRIVACY_POLICY_URL", "\"https://manuelasan.github.io/SospechApp/privacy.html\"")
            buildConfigField("String", "ADMOB_BANNER_AD_UNIT_ID", "\"ca-app-pub-3940256099942544/6300978111\"")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.datastore.preferences)

    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization)
    implementation(libs.okhttp.logging)
    implementation(libs.play.services.ads)
    implementation(libs.ump)

    // navigation
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.compose.animation)
    implementation(libs.androidx.room.common.jvm)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.foundation.layout)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    androidTestImplementation("androidx.test.uiautomator:uiautomator:2.3.0")

    // Modern unit testing dependencies
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.truth)
    testImplementation(libs.mockito.kotlin)
}