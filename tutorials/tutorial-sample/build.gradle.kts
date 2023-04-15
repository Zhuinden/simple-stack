plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-parcelize")
    kotlin("kapt")
}

android {
    compileSdk = 33

    defaultConfig {
        applicationId = "com.zhuinden.simplestacktutorials"
        minSdk = 21
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        multiDexEnabled = true
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
        languageVersion = "1.9" // data objects
    }

    buildFeatures {
        viewBinding = true
    }
}

kotlin.sourceSets.all {
    languageSettings.enableLanguageFeature("DataObjects")
}

dependencies {
    //implementation(mapOf("dir" to "libs", "include" to listOf("*.jar")))
    implementation(project(":simple-stack"))

    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.activity:activity-ktx:1.7.0")
    implementation("androidx.fragment:fragment-ktx:1.5.6")
    implementation("com.google.android.material:material:1.8.0")
    testImplementation("junit:junit:4.13.2")

    implementation("androidx.lifecycle:lifecycle-common-java8:2.5.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.5.1")

    implementation("com.github.Zhuinden.simple-stack-extensions:core-ktx:2.3.1") {
        exclude(module = "simple-stack")
    }
    implementation("com.github.Zhuinden.simple-stack-extensions:fragments:2.3.1") {
        exclude(module = "simple-stack")
    }
    implementation("com.github.Zhuinden.simple-stack-extensions:fragments-ktx:2.3.1") {
        exclude(module = "simple-stack")
    }
    implementation("com.github.Zhuinden.simple-stack-extensions:lifecycle-ktx:2.3.1") {
        exclude(module = "simple-stack")
    }
    implementation("com.github.Zhuinden.simple-stack-extensions:navigator-ktx:2.3.1") {
        exclude(module = "simple-stack")
    }
    implementation("com.github.Zhuinden.simple-stack-extensions:services:2.3.1") {
        exclude(module = "simple-stack")
    }
    implementation("com.github.Zhuinden.simple-stack-extensions:services-ktx:2.3.1") {
        exclude(module = "simple-stack")
    }

    implementation("com.github.Zhuinden:fragmentviewbindingdelegate-kt:1.0.2")

    implementation("com.github.Zhuinden:rx-combinetuple-kt:1.2.1")
    implementation("io.reactivex.rxjava2:rxjava:2.2.21")
    implementation("io.reactivex.rxjava2:rxkotlin:2.4.0")
    implementation("io.reactivex.rxjava2:rxandroid:2.1.1")
    implementation("com.jakewharton.rxrelay2:rxrelay:2.1.1")

    implementation("androidx.lifecycle:lifecycle-viewmodel:2.5.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.5.1")

    implementation("androidx.multidex:multidex:2.0.1")

    implementation("com.github.Zhuinden:live-event:1.3.0")


}
