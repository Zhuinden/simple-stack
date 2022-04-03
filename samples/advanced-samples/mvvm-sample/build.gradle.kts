plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-parcelize")
    kotlin("kapt")
}

android {
    compileSdk = 31

    defaultConfig {
        applicationId = "com.zhuinden.simplestackexamplemvvm"
        minSdk = 16
        targetSdk = 31
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        multiDexEnabled = true
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            // Uses new built-in shrinker http://tools.android.com/tech-docs/new-build-system/built-in-shrinker
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            testProguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguardTest-rules.pro")
        }

        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            testProguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguardTest-rules.pro")
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

        dataBinding = false
    }
}

dependencies {
    implementation(project(":simple-stack"))
    implementation("com.github.Zhuinden:simple-stack-extensions:2.2.2") {
        exclude(module = "simple-stack") // only needed because of jitpack vs local
    }

    // App's dependencies, including test

    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.constraintlayout:constraintlayout:1.1.3")
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("com.github.lisawray.groupie:groupie:2.10.0")
    implementation("com.github.lisawray.groupie:groupie-viewbinding:2.10.0")

    implementation("com.github.Zhuinden:live-event:1.2.0")
    implementation("com.github.Zhuinden:livedata-combinetuple-kt:1.2.1")
    implementation("com.github.Zhuinden:rx-combinetuple-kt:1.2.1")
    implementation("com.github.Zhuinden:rx-validateby-kt:2.1.1")

    implementation("io.reactivex.rxjava2:rxkotlin:2.4.0")
    implementation("com.jakewharton.rxrelay2:rxrelay:2.1.1")

    implementation("androidx.activity:activity:1.4.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("com.google.android.material:material:1.5.0")
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    // live data
    implementation("androidx.lifecycle:lifecycle-common-java8:2.4.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.4.1")

    // Dependencies for local unit tests
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.assertj:assertj-core:3.11.1")
    testImplementation("org.mockito:mockito-core:2.19.0")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-all:1.10.19")
    testImplementation("org.hamcrest:hamcrest-all:1.3")

    // Espresso UI Testing
    // Unit Tests
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.hamcrest:hamcrest-all:1.3")
    testImplementation("org.powermock:powermock-module-junit4:1.6.5")
    // Espresso Idling Resource

    // Dependencies for Android unit tests
    androidTestImplementation("junit:junit:4.13.2")
    androidTestImplementation("org.mockito:mockito-core:2.19.0")
    androidTestImplementation("com.google.dexmaker:dexmaker:1.2")
    androidTestImplementation("com.google.dexmaker:dexmaker-mockito:1.2")

    // Android Testing Support Library's runner and rules
    androidTestImplementation("org.assertj:assertj-core:3.11.1")

    // Espresso UI Testing dependencies.
    implementation("androidx.test.espresso:espresso-idling-resource:3.1.0") {
        exclude(group = "javax.inject")
    }
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0") {
        exclude(group = "javax.inject")
    }
    androidTestImplementation("androidx.test:runner:1.4.0")
    androidTestImplementation("androidx.test:rules:1.4.0")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.1.0") {
        exclude(group = "javax.inject")
    }
}