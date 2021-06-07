plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-parcelize")
    kotlin("kapt")
}

android {
    compileSdkVersion(29)

    defaultConfig {
        applicationId = "com.zhuinden.simplestackexamplekotlinfragment"
        minSdkVersion(16)
        targetSdkVersion(29)
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

    implementation("com.github.Zhuinden.simple-stack-extensions:fragments:2.2.1") {
        exclude(module = "simple-stack") // only needed because of jitpack vs local
    }
    implementation("com.github.Zhuinden.simple-stack-extensions:fragments-ktx:2.2.1") {
        exclude(module = "simple-stack") // only needed because of jitpack vs local
    }
    implementation("com.github.Zhuinden.simple-stack-extensions:navigator-ktx:2.2.1") {
        exclude(module = "simple-stack") // only needed because of jitpack vs local
    }

    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
    androidTestImplementation("androidx.test:runner:1.3.0")
    androidTestImplementation("androidx.test:rules:1.3.0")
    implementation("androidx.appcompat:appcompat:1.3.0")
    implementation("androidx.core:core:1.5.0")
    implementation("androidx.activity:activity:1.2.3")
    implementation("androidx.fragment:fragment-ktx:1.3.4")

    implementation("com.google.android.material:material:1.3.0")
    testImplementation("junit:junit:4.13.2")


}
