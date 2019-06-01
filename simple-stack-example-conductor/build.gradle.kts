plugins {
    id("com.android.application")
}

android {
    compileSdkVersion(27)

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    defaultConfig {
        applicationId = "com.zhuinden.navigationexamplecond"
        minSdkVersion(16)
        targetSdkVersion(27)
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    //implementation(mapOf("dir" to "libs", "include" to listOf("*.jar")))
    implementation(project(":simple-stack"))
    androidTestImplementation("com.android.support.test.espresso:espresso-core:3.0.1") {
        exclude(group = "com.android.support", module = "support-annotations")
    }
    implementation("com.android.support:appcompat-v7:27.1.1")
    implementation("com.android.support:design:27.1.1")
    implementation("com.android.support:support-vector-drawable:27.1.1")
    implementation("com.jakewharton:butterknife:8.8.1")
    annotationProcessor("com.jakewharton:butterknife-compiler:8.8.1")
    annotationProcessor("frankiesardo:auto-parcel:1.0.3")
    implementation("com.bluelinelabs:conductor:2.1.5-SNAPSHOT")
    testImplementation("junit:junit:4.12")
}
