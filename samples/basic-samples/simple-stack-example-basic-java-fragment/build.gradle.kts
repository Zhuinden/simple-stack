plugins {
    id("com.android.application")
}

android {
    compileSdkVersion(29)

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    defaultConfig {
        applicationId = "com.zhuinden.navigationexamplefrag"
        minSdkVersion(16)
        targetSdkVersion(29)
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
    implementation("androidx.appcompat:appcompat:1.3.0")
    implementation("androidx.fragment:fragment:1.3.2")
    implementation("com.google.android.material:material:1.3.0")
    implementation("androidx.vectordrawable:vectordrawable:1.1.0")
    implementation("com.github.Zhuinden.simple-stack-extensions:fragments:2.2.1") {
        exclude(module = "simple-stack") // only needed because of jitpack vs local
    }
    annotationProcessor("frankiesardo:auto-parcel:1.0.3")
    testImplementation("junit:junit:4.13.2")
}
