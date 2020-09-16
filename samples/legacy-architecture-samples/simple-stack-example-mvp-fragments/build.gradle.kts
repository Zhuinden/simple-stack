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
        applicationId = "com.zhuinden.simplestackdemoexamplefragments"
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
}

dependencies {
    //implementation(mapOf("dir" to "libs", "include" to listOf("*.jar")))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.3.72")

    implementation(project(":simple-stack"))

    androidTestImplementation("androidx.test.espresso:espresso-core:3.2.0")
    androidTestImplementation("androidx.test:runner:1.2.0")
    androidTestImplementation("androidx.test:rules:1.2.0")

    implementation("androidx.appcompat:appcompat:1.1.0")
    implementation("androidx.core:core:1.3.1")
    implementation("androidx.activity:activity:1.1.0")
    implementation("androidx.fragment:fragment-ktx:1.2.5")

    implementation("com.github.Zhuinden.simple-stack-extensions:fragments:2.0.0") {
        exclude(module = "simple-stack") // only needed because of jitpack vs local
    }
    implementation("com.github.Zhuinden.simple-stack-extensions:fragments-ktx:2.0.0") {
        exclude(module = "simple-stack") // only needed because of jitpack vs local
    }
    implementation("com.github.Zhuinden.simple-stack-extensions:navigator-ktx:2.0.0") {
        exclude(module = "simple-stack") // only needed because of jitpack vs local
    }
    implementation("com.github.Zhuinden.simple-stack-extensions:services:2.0.0") {
        exclude(module = "simple-stack") // only needed because of jitpack vs local
    }
    implementation("com.github.Zhuinden.simple-stack-extensions:services-ktx:2.0.0") {
        exclude(module = "simple-stack") // only needed because of jitpack vs local
    }


    implementation("androidx.recyclerview:recyclerview:1.1.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.0.0")
    implementation("com.google.android.material:material:1.1.0")

    testImplementation("junit:junit:4.13")

    implementation("com.jakewharton:butterknife:10.0.0")
    kapt("com.jakewharton:butterknife-compiler:10.0.0")

    kapt("frankiesardo:auto-parcel:1.0.3")

    kapt("com.google.dagger:dagger-compiler:2.27")
    implementation("com.google.dagger:dagger:2.27")
    compileOnly("org.glassfish:javax.annotation:10.0-b28")

    implementation("io.reactivex.rxjava2:rxjava:2.2.19")
    implementation("io.reactivex.rxjava2:rxandroid:2.1.1")
    implementation("com.jakewharton.rxbinding2:rxbinding:2.2.0")
    implementation("com.jakewharton.rxrelay2:rxrelay:2.1.1")

    kapt("dk.ilios:realmfieldnameshelper:1.1.1")

    implementation("com.andkulikov:transitionseverywhere:1.7.0")
    implementation("org.javatuples:javatuples:1.2")
}
