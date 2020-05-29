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
        applicationId = "com.example.fragmenttransitions"
        minSdkVersion(15)
        targetSdkVersion(28)
        versionCode = 1
        versionName = "1.0"
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
    implementation("androidx.appcompat:appcompat:1.1.0")
    implementation("androidx.recyclerview:recyclerview:1.1.0")
    implementation("androidx.vectordrawable:vectordrawable:1.1.0")
    implementation("com.jakewharton:butterknife:10.0.0")
    annotationProcessor("com.jakewharton:butterknife-compiler:10.0.0")
    compileOnly("com.google.auto.value:auto-value:1.5.2")
    annotationProcessor("com.google.auto.value:auto-value:1.5.2")
    implementation("nz.bradcampbell:paperparcel:2.0.4")
    annotationProcessor("nz.bradcampbell:paperparcel-compiler:2.0.4")
    annotationProcessor("com.github.reggar:auto-value-ignore-hash-equals:1.1.4")
    implementation("com.google.dagger:dagger:2.27")
    annotationProcessor("com.google.dagger:dagger-compiler:2.27")
}

configurations.compile.get().dependencies.forEach { compileDependency ->
    println("Excluding implementation dependency: ${compileDependency.name}")
    configurations.androidTestCompile.get().dependencies.forEach {
        configurations.androidTestCompile.get().exclude(module = compileDependency.name)
    }
}
