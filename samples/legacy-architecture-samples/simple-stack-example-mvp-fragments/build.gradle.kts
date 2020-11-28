import io.realm.transformer.RealmTransformer

plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-parcelize")
}

android {
    compileSdkVersion(28)

    defaultConfig {
        applicationId = "com.zhuinden.simplestackdemoexamplefragments"
        minSdkVersion(21)
        targetSdkVersion(28)
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("debug") {
            multiDexEnabled = true
        }

        getByName("release") {
            multiDexEnabled = true
            isMinifyEnabled = true
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

android.registerTransform(RealmTransformer(project))

dependencies {
    //implementation(mapOf("dir" to "libs", "include" to listOf("*.jar")))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.4.20")

    implementation(project(":simple-stack"))

    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
    androidTestImplementation("androidx.test:runner:1.3.0")
    androidTestImplementation("androidx.test:rules:1.3.0")

    implementation("io.realm:realm-annotations:10.1.0")
    implementation("io.realm:realm-android-library:10.1.0")
    implementation("io.realm:realm-android-kotlin-extensions:10.1.0")
    annotationProcessor("io.realm:realm-annotations-processor:10.1.0")

    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("androidx.core:core:1.3.2")
    implementation("androidx.activity:activity:1.1.0")
    implementation("androidx.fragment:fragment-ktx:1.2.5")

    implementation("androidx.lifecycle:lifecycle-common-java8:2.2.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.2.0")

    implementation("com.github.Zhuinden.simple-stack-extensions:fragments:2.0.1") {
        exclude(module = "simple-stack") // only needed because of jitpack vs local
    }
    implementation("com.github.Zhuinden.simple-stack-extensions:fragments-ktx:2.0.1") {
        exclude(module = "simple-stack") // only needed because of jitpack vs local
    }
    implementation("com.github.Zhuinden.simple-stack-extensions:navigator-ktx:2.0.1") {
        exclude(module = "simple-stack") // only needed because of jitpack vs local
    }
    implementation("com.github.Zhuinden.simple-stack-extensions:services:2.0.1") {
        exclude(module = "simple-stack") // only needed because of jitpack vs local
    }
    implementation("com.github.Zhuinden.simple-stack-extensions:services-ktx:2.0.1") {
        exclude(module = "simple-stack") // only needed because of jitpack vs local
    }

    implementation("com.github.Zhuinden:rx-combinetuple-kt:1.1.0")


    implementation("androidx.recyclerview:recyclerview:1.1.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("com.google.android.material:material:1.2.1")

    testImplementation("junit:junit:4.13.1")

    implementation("io.reactivex.rxjava2:rxjava:2.2.19")
    implementation("io.reactivex.rxjava2:rxandroid:2.1.1")
    implementation("com.jakewharton.rxbinding2:rxbinding:2.2.0")
    implementation("com.jakewharton.rxrelay2:rxrelay:2.1.1")

    implementation("com.andkulikov:transitionseverywhere:1.7.0")
    implementation("org.javatuples:javatuples:1.2")
}
