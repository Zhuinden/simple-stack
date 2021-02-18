plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-parcelize")
    kotlin("kapt")
}

android {
    compileSdkVersion(28)

    defaultConfig {
        applicationId = "com.zhuinden.simplestackexamplescoping"
        minSdkVersion(16)
        targetSdkVersion(28)
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
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
}

dependencies {
    //implementation(mapOf("dir" to "libs", "include" to listOf("*.jar")))
    implementation(project(":simple-stack"))

    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("com.google.android.material:material:1.2.1")
    testImplementation("junit:junit:4.13.2")

    implementation("androidx.lifecycle:lifecycle-common-java8:2.3.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.3.0")

    implementation("com.github.Zhuinden.simple-stack-extensions:fragments:2.1.0") {
        exclude(module = "simple-stack") // only needed because of jitpack vs local
    }
    implementation("com.github.Zhuinden.simple-stack-extensions:fragments-ktx:2.1.0") {
        exclude(module = "simple-stack") // only needed because of jitpack vs local
    }
    implementation("com.github.Zhuinden.simple-stack-extensions:navigator-ktx:2.1.0") {
        exclude(module = "simple-stack") // only needed because of jitpack vs local
    }
    implementation("com.github.Zhuinden.simple-stack-extensions:services:2.1.0") {
        exclude(module = "simple-stack") // only needed because of jitpack vs local
    }
    implementation("com.github.Zhuinden.simple-stack-extensions:services-ktx:2.1.0") {
        exclude(module = "simple-stack") // only needed because of jitpack vs local
    }

    implementation("com.github.Zhuinden:fragmentviewbindingdelegate-kt:1.0.0")

    implementation("io.reactivex.rxjava2:rxjava:2.2.20")
    implementation("io.reactivex.rxjava2:rxandroid:2.1.1")
    implementation("com.jakewharton.rxbinding2:rxbinding:2.2.0")
    implementation("com.jakewharton.rxrelay2:rxrelay:2.1.1")

    implementation("androidx.lifecycle:lifecycle-viewmodel:2.3.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.3.0")

    implementation("com.github.Zhuinden:live-event:1.2.0")


}
