plugins {
    id("com.android.application")
}

android {
    compileSdkVersion(28)

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    defaultConfig {
        applicationId = "com.zhuinden.navigationexamplecond"
        minSdkVersion(16)
        targetSdkVersion(28)
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
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
    androidTestImplementation("androidx.test:runner:1.3.0")
    androidTestImplementation("androidx.test:rules:1.3.0")
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("androidx.core:core:1.3.2")
    implementation("androidx.activity:activity:1.1.0")
    implementation("androidx.fragment:fragment:1.2.5")
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.2.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:2.2.0")
    implementation("com.google.android.material:material:1.2.1")
    implementation("androidx.vectordrawable:vectordrawable:1.1.0")
    annotationProcessor("frankiesardo:auto-parcel:1.0.3")
    implementation("com.bluelinelabs:conductor:2.1.5")
    testImplementation("junit:junit:4.13.1")
}
