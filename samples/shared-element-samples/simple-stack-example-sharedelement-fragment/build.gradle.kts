plugins {
    id("com.android.application")
}

android {
    compileSdk = 31

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    defaultConfig {
        applicationId = "com.example.fragmenttransitions"
        minSdk = 16
        targetSdk = 31
        versionCode = 1
        versionName = "1.0"
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

    implementation("com.github.Zhuinden.simple-stack-extensions:fragments:2.2.2") {
        exclude(module = "simple-stack") // only needed because of jitpack vs local
    }
    implementation("com.github.Zhuinden.simple-stack-extensions:fragments-ktx:2.2.2") {
        exclude(module = "simple-stack") // only needed because of jitpack vs local
    }
    implementation("com.github.Zhuinden.simple-stack-extensions:navigator-ktx:2.2.2") {
        exclude(module = "simple-stack") // only needed because of jitpack vs local
    }

    implementation("androidx.appcompat:appcompat:1.3.1")
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("androidx.vectordrawable:vectordrawable:1.1.0")
    compileOnly("com.google.auto.value:auto-value:1.5.2")
    annotationProcessor("com.google.auto.value:auto-value:1.5.2")
    implementation("nz.bradcampbell:paperparcel:2.0.4")
    annotationProcessor("nz.bradcampbell:paperparcel-compiler:2.0.4")
    annotationProcessor("com.github.reggar:auto-value-ignore-hash-equals:1.1.4")
    implementation("com.google.dagger:dagger:2.39")
    annotationProcessor("com.google.dagger:dagger-compiler:2.39")
}