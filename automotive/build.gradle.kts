plugins {
    id("com.android.application") version "8.7.3"
    id("org.jetbrains.kotlin.android") version "2.0.21"
}
android {
    namespace = "kr.mycarpet.vehicle"
    compileSdk = 35
    defaultConfig {
        applicationId = "kr.mycarpet.vehicle"
        minSdk = 28
        targetSdk = 34
    }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
    kotlinOptions { jvmTarget = "17" }
}
dependencies { implementation("ai.pleos.playground:Vehicle:2.3.2") }
