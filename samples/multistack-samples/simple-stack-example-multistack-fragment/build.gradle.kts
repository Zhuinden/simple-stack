plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
}

android {
    compileSdkVersion(28)

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    defaultConfig {
        applicationId = "com.zhuinden.simplestackdemomultistackfragment"
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
    implementation(project(":simple-stack"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.4.10")

    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
    testImplementation("junit:junit:4.13.1")

    compileOnly("org.glassfish:javax.annotation:10.0-b28")
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("androidx.recyclerview:recyclerview:1.1.0")
    implementation("com.google.android.material:material:1.2.1")
    implementation("com.bartoszlipinski:viewpropertyobjectanimator:1.4.5")

    implementation("it.sephiroth.android.library.bottomnavigation:bottom-navigation:2.0.1-rc1")
}