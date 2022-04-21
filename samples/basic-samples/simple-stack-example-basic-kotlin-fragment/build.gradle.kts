plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-parcelize")
    kotlin("kapt")
}

android {
    compileSdk = 31

    defaultConfig {
        applicationId = "com.zhuinden.simplestackexamplekotlinfragment"
        minSdk = 16
        targetSdk = 31
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

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    //implementation(mapOf("dir" to "libs", "include" to listOf("*.jar")))
    implementation(project(":simple-stack"))

    implementation("com.github.Zhuinden.simple-stack-extensions:core-ktx:2.2.2") {
        exclude(module = "simple-stack")
    }
    implementation("com.github.Zhuinden.simple-stack-extensions:fragments:2.2.2") {
        exclude(module = "simple-stack")
    }
    implementation("com.github.Zhuinden.simple-stack-extensions:fragments-ktx:2.2.2") {
        exclude(module = "simple-stack")
    }
    implementation("com.github.Zhuinden.simple-stack-extensions:navigator-ktx:2.2.2") {
        exclude(module = "simple-stack")
    }
    implementation("com.github.Zhuinden.simple-stack-extensions:services:2.2.2") {
        exclude(module = "simple-stack")
    }
    implementation("com.github.Zhuinden.simple-stack-extensions:services-ktx:2.2.2") {
        exclude(module = "simple-stack")
    }

    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation("androidx.test:runner:1.4.0")
    androidTestImplementation("androidx.test:rules:1.4.0")
    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("androidx.core:core:1.7.0")
    implementation("androidx.activity:activity:1.4.0")
    implementation("androidx.fragment:fragment-ktx:1.4.1")

    implementation("com.google.android.material:material:1.5.0")
    testImplementation("junit:junit:4.13.2")


}
