plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
    id("realm-android")
}

android {
    compileSdkVersion(28)

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    defaultConfig {
        applicationId = "com.zhuinden.simplestackdemoexamplemvp"
        minSdkVersion(14)
        targetSdkVersion(28)
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
}

androidExtensions {
    isExperimental = true
}

dependencies {
    //implementation(mapOf("dir" to "libs", "include" to listOf("*.jar")))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.3.31")

    androidTestImplementation("com.android.support.test.espresso:espresso-core:3.0.1") {
        exclude(group = "com.android.support", module = "support-annotations")
    }
    implementation("com.android.support:appcompat-v7:28.0.0")
    implementation("com.android.support:recyclerview-v7:28.0.0")
    implementation("com.android.support:design:28.0.0")

    testImplementation("junit:junit:4.12")

    implementation("com.jakewharton:butterknife:8.8.1")
    kapt("com.jakewharton:butterknife-compiler:8.8.1")

    kapt("frankiesardo:auto-parcel:1.0.3")

    kapt("com.google.dagger:dagger-compiler:2.22.1")
    implementation("com.google.dagger:dagger:2.22.1")
    compileOnly("org.glassfish:javax.annotation:10.0-b28")

    implementation("io.reactivex.rxjava2:rxjava:2.2.2")
    implementation("io.reactivex.rxjava2:rxandroid:2.1.0")
    implementation("com.jakewharton.rxbinding2:rxbinding:2.1.1")
    implementation("com.jakewharton.rxrelay2:rxrelay:2.0.0")

    kapt("dk.ilios:realmfieldnameshelper:1.1.1")

    implementation("com.andkulikov:transitionseverywhere:1.7.0") {
        exclude(group = "com.android.support", module = "support-v4")
    }

    implementation("org.javatuples:javatuples:1.2")
    implementation(project(":simple-stack"))
}
