plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
  kotlin("kapt")
  id("com.google.dagger.hilt.android")
  id("de.undercouch.download") version "5.0.0"
}

android {
  namespace = "com.example.camscan"
  compileSdk = 34

  defaultConfig {
    applicationId = "com.example.camscan"
    minSdk = 24
    targetSdk = 34
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro"
      )
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }

  kotlinOptions {
    jvmTarget = "1.8"
  }

  buildFeatures {
    viewBinding = true
  }
  androidResources {
    noCompress("tflite")
  }

}
val assetDir = "$projectDir/src/main/assets"
extra["ASSET_DIR"] = assetDir

// Download default models; if you wish to use your own models then
// place them in the "assets" directory and comment out this line.
apply(from = "download_models.gradle")

dependencies {
  val hilt_version = "2.48"
  val coroutines_version = "1.7.3"
  implementation("androidx.core:core-ktx:1.13.1")
  implementation("androidx.appcompat:appcompat:1.7.0")
  implementation("com.google.android.material:material:1.12.0")
  implementation("androidx.constraintlayout:constraintlayout:2.1.4")
  implementation("androidx.activity:activity-ktx:1.7.2")

  // Hilt for Dependency Injection
  implementation("com.google.dagger:hilt-android:$hilt_version")
  kapt("com.google.dagger:hilt-compiler:$hilt_version")

  // Coroutines
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines_version")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutines_version")

  // Room Database dependencies
  implementation("androidx.room:room-ktx:2.6.1")
  kapt("androidx.room:room-compiler:2.6.1")
  implementation("com.google.mediapipe:tasks-vision:0.10.14")
  implementation("androidx.fragment:fragment-ktx:1.6.2")

/*  // MediaPipe dependencies
  implementation("com.google.mediapipe:mediapipe-framework:0.8.10")
  implementation("com.google.mediapipe:mediapipe-holistic:0.8.10")
  implementation("com.google.mediapipe:mediapipe-android:0.9.0")
  implementation("com.google.mediapipe:mediapipe-android-facemesh:0.9.0")*/

  // Coil for image loading
  implementation("io.coil-kt:coil:2.3.0")

  testImplementation("junit:junit:4.13.2")
  androidTestImplementation("androidx.test.ext:junit:1.2.1")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}