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
        applicationId = "com.example.fragmenttransitions"
        minSdkVersion(15)
        targetSdkVersion(27)
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
    implementation("com.android.support:appcompat-v7:27.1.1")
    implementation("com.android.support:recyclerview-v7:27.1.1")
    implementation("com.android.support:support-vector-drawable:27.1.1")
    implementation("com.jakewharton:butterknife:8.8.1")
    annotationProcessor("com.jakewharton:butterknife-compiler:8.8.1")
    compileOnly("com.google.auto.value:auto-value:1.5.2")
    annotationProcessor("com.google.auto.value:auto-value:1.5.2")
    implementation("nz.bradcampbell:paperparcel:2.0.4")
    annotationProcessor("nz.bradcampbell:paperparcel-compiler:2.0.4")
    annotationProcessor("com.github.reggar:auto-value-ignore-hash-equals:1.1.4")
    implementation("com.google.dagger:dagger:2.14.1")
    annotationProcessor("com.google.dagger:dagger-compiler:2.14.1")
}

configurations.all {
    resolutionStrategy.force("com.android.support:support-annotations:23.1.1")
}

configurations.compile.get().dependencies.forEach { compileDependency ->
    println("Excluding implementation dependency: ${compileDependency.name}")
    configurations.androidTestCompile.get().dependencies.forEach {
        configurations.androidTestCompile.get().exclude(module = compileDependency.name)
    }
}
