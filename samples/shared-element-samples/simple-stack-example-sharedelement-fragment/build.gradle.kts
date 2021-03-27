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
        minSdkVersion(16)
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

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    //implementation(mapOf("dir" to "libs", "include" to listOf("*.jar")))
    implementation(project(":simple-stack"))
    
    implementation("com.github.Zhuinden.simple-stack-extensions:fragments:2.2.0") {
        exclude(module = "simple-stack") // only needed because of jitpack vs local
    }
    implementation("com.github.Zhuinden.simple-stack-extensions:fragments-ktx:2.2.0") {
        exclude(module = "simple-stack") // only needed because of jitpack vs local
    }
    implementation("com.github.Zhuinden.simple-stack-extensions:navigator-ktx:2.2.0") {
        exclude(module = "simple-stack") // only needed because of jitpack vs local
    }

    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("androidx.recyclerview:recyclerview:1.1.0")
    implementation("androidx.vectordrawable:vectordrawable:1.1.0")
    compileOnly("com.google.auto.value:auto-value:1.5.2")
    annotationProcessor("com.google.auto.value:auto-value:1.5.2")
    implementation("nz.bradcampbell:paperparcel:2.0.4")
    annotationProcessor("nz.bradcampbell:paperparcel-compiler:2.0.4")
    annotationProcessor("com.github.reggar:auto-value-ignore-hash-equals:1.1.4")
    implementation("com.google.dagger:dagger:2.29.1")
    annotationProcessor("com.google.dagger:dagger-compiler:2.29.1")
}

configurations.compile.get().dependencies.forEach { compileDependency ->
    println("Excluding implementation dependency: ${compileDependency.name}")
    configurations.androidTestCompile.get().dependencies.forEach {
        configurations.androidTestCompile.get().exclude(module = compileDependency.name)
    }
}
