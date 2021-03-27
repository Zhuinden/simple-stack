plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-parcelize")
}

android {
    compileSdkVersion(28)

    defaultConfig {
        applicationId = "com.zhuinden.simplestackdemomultistack"
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
    testImplementation("junit:junit:4.13.2")

    compileOnly("org.glassfish:javax.annotation:10.0-b28")
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("androidx.core:core:1.3.2")
    implementation("androidx.activity:activity:1.2.0")
    implementation("androidx.fragment:fragment-ktx:1.3.0")

    implementation("androidx.lifecycle:lifecycle-common-java8:2.3.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.3.1")

    implementation("androidx.recyclerview:recyclerview:1.1.0")
    implementation("com.google.android.material:material:1.3.0")
    implementation("com.bartoszlipinski:viewpropertyobjectanimator:1.4.5")

    implementation("it.sephiroth.android.library.bottomnavigation:bottom-navigation:2.0.0")
}