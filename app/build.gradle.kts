import java.io.File

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.google.devtools.ksp)
  alias(libs.plugins.roborazzi)
  alias(libs.plugins.secrets)
  alias(libs.plugins.google.services)
}

android {
  namespace = "com.example"
  compileSdk { version = release(36) { minorApiLevel = 1 } }

  defaultConfig {
    applicationId = "com.smartx.academy"
    minSdk = 24
    targetSdk = 36
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  signingConfigs {
    create("release") {
      val storeFilePath = System.getenv("SIGNING_STORE_FILE") ?: "${rootDir}/my-upload-key.jks"
      storeFile = file(storeFilePath)
      storePassword = System.getenv("SIGNING_STORE_PASSWORD") ?: ""
      keyAlias = System.getenv("SIGNING_KEY_ALIAS") ?: ""
      keyPassword = System.getenv("SIGNING_KEY_PASSWORD") ?: ""
    }
    create("debugConfig") {
      val keystoreFile = file("${rootDir}/debug.keystore")
      if (!keystoreFile.exists()) {
        try {
          ProcessBuilder(
            "keytool", "-genkeypair", "-v",
            "-keystore", keystoreFile.absolutePath,
            "-storepass", "android",
            "-alias", "androiddebugkey",
            "-keypass", "android",
            "-keyalg", "RSA",
            "-keysize", "2048",
            "-validity", "10000",
            "-dname", "CN=Unknown, OU=Unknown, O=Unknown, L=Unknown, S=Unknown, C=US"
          ).start().waitFor()
        } catch (e: Exception) {
          e.printStackTrace()
        }
      }
      storeFile = keystoreFile
      storePassword = "android"
      keyAlias = "androiddebugkey"
      keyPassword = "android"
    }
  }

  buildTypes {
    release {
      isCrunchPngs = false
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      
      val releaseConfig = signingConfigs.getByName("release")
      if (releaseConfig.storeFile != null && releaseConfig.storeFile!!.exists()) {
        signingConfig = releaseConfig
      } else {
        signingConfig = signingConfigs.getByName("debugConfig")
      }
    }
    debug {
      signingConfig = signingConfigs.getByName("debugConfig")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  buildFeatures {
    compose = true
    buildConfig = true
  }
  testOptions { unitTests { isIncludeAndroidResources = true } }
}

// Configure the Secrets Gradle Plugin to use .env and .env.example files
// to match the convention used in Web projects.
secrets {
  propertiesFileName = ".env"
  defaultPropertiesFileName = ".env.example"
}

// Some unused dependencies are commented out below instead of being removed.
// This makes it easy to add them back in the future if needed.
dependencies {
  implementation(platform(libs.androidx.compose.bom))
  implementation(platform(libs.firebase.bom))
  implementation("com.google.firebase:firebase-messaging-ktx:24.0.0")
  // implementation("io.github.jan-tennert.supabase:postgrest-kt:2.5.0")
  // implementation("io.github.jan-tennert.supabase:gotrue-kt:2.5.0")
  implementation("com.pierfrancescosoffritti.androidyoutubeplayer:core:12.1.0")
  // implementation(libs.accompanist.permissions)
  implementation(libs.lottie.compose)
  implementation(libs.androidx.activity.compose)
  // implementation(libs.androidx.camera.camera2)
  // implementation(libs.androidx.camera.core)
  // implementation(libs.androidx.camera.lifecycle)
  // implementation(libs.androidx.camera.view)
  implementation(libs.androidx.compose.material.icons.core)
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.core.ktx)
  // implementation(libs.androidx.datastore.preferences)
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.lifecycle.viewmodel.compose)
  implementation(libs.androidx.navigation.compose)
  implementation(libs.play.services.ads)
  implementation(libs.androidx.room.ktx)
  implementation(libs.androidx.room.runtime)
  implementation(libs.coil.compose)
  implementation(libs.converter.moshi)
  // implementation(libs.firebase.ai)
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.logging.interceptor)
  implementation(libs.moshi.kotlin)
  implementation(libs.okhttp)
  // implementation(libs.play.services.location)
  implementation(libs.retrofit)
  testImplementation(libs.androidx.compose.ui.test.junit4)
  testImplementation(libs.androidx.core)
  testImplementation(libs.androidx.junit)
  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.robolectric)
  testImplementation(libs.roborazzi)
  testImplementation(libs.roborazzi.compose)
  testImplementation(libs.roborazzi.junit.rule)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.runner)
  debugImplementation(libs.androidx.compose.ui.test.manifest)
  debugImplementation(libs.androidx.compose.ui.tooling)
  "ksp"(libs.androidx.room.compiler)
  "ksp"(libs.moshi.kotlin.codegen)
}

val buildDirFile = layout.buildDirectory.get().asFile
val rootDirFile = rootDir

tasks.register("copyApkToBuildOutputs") {
    dependsOn("assembleDebug")
    val srcFile = File(buildDirFile, "outputs/apk/debug/app-debug.apk")
    val destDir = File(rootDirFile, ".build-outputs")
    val targetFile1 = File(destDir, "app-debug.apk")
    val targetFile2 = File(destDir, "app.debug.apk")
    
    doLast {
        if (srcFile.exists()) {
            destDir.mkdirs()
            srcFile.copyTo(targetFile1, overwrite = true)
            srcFile.copyTo(targetFile2, overwrite = true)
            println("Successfully copied APK to .build-outputs/app-debug.apk and app.debug.apk")
        } else {
            println("Source APK not found at ${srcFile.absolutePath}")
        }
    }
}

