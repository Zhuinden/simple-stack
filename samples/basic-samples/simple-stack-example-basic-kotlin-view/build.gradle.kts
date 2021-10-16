plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-parcelize")
    kotlin("kapt")
}


android {
    compileSdk = 31

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    defaultConfig {
        applicationId = "com.zhuinden.navigationexamplekotlinview"
        minSdk = 16
        targetSdk = 31
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    //implementation(mapOf("dir" to "libs", "include" to listOf("*.jar")))
    implementation(project(":simple-stack"))
    implementation("com.github.Zhuinden.simple-stack-extensions:navigator-ktx:2.2.2") {
        exclude(module = "simple-stack") // only needed because of jitpack vs local
    }

    implementation("androidx.core:core:1.6.0")
    implementation("androidx.activity:activity:1.3.1")

    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation("androidx.test:runner:1.4.0")
    androidTestImplementation("androidx.test:rules:1.4.0")
    implementation("androidx.appcompat:appcompat:1.3.1")
    implementation("com.google.android.material:material:1.3.0")
    implementation("androidx.vectordrawable:vectordrawable:1.1.0")

    testImplementation("junit:junit:4.13.2")


}