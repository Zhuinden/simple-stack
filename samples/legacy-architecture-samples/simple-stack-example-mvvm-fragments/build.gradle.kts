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
        applicationId = "com.zhuinden.simplestackexamplemvvm"
        minSdkVersion(16)
        targetSdkVersion(28)
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            // Uses new built-in shrinker http://tools.android.com/tech-docs/new-build-system/built-in-shrinker
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            testProguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguardTest-rules.pro")
        }

        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            testProguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguardTest-rules.pro")
        }
    }

    // Always show the result of every unit test, even if it passes.
    testOptions.unitTests.all {
        testLogging {
            events("passed", "skipped", "failed", "standardOut", "standardError")
        }
    }

    dataBinding.isEnabled = true
}

dependencies {
    implementation(project(":simple-stack"))

    // App's dependencies, including test
    implementation("androidx.appcompat:appcompat:1.1.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("com.google.android.material:material:1.1.0")
    implementation("androidx.recyclerview:recyclerview:1.1.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.0.0")


    // live data
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.2.0")
    implementation("androidx.lifecycle:lifecycle-livedata:2.2.0")

    // useful stuff
    implementation("com.jakewharton:butterknife:10.0.0")
    annotationProcessor("com.jakewharton:butterknife-compiler:10.0.0")
    compileOnly("com.google.auto.value:auto-value:1.5.2")
    annotationProcessor("com.google.auto.value:auto-value:1.5.2")
    implementation("nz.bradcampbell:paperparcel:2.0.4")
    annotationProcessor("nz.bradcampbell:paperparcel-compiler:2.0.4")
    annotationProcessor("com.github.reggar:auto-value-ignore-hash-equals:1.1.4")
    implementation("com.google.dagger:dagger:2.27")
    annotationProcessor("com.google.dagger:dagger-compiler:2.27")

    // Dependencies for local unit tests
    testImplementation("junit:junit:4.13")
    testImplementation("org.assertj:assertj-core:3.11.1")
    testImplementation("org.mockito:mockito-core:2.19.0")

    testImplementation("junit:junit:4.13")
    testImplementation("org.mockito:mockito-all:1.10.19")
    testImplementation("org.hamcrest:hamcrest-all:1.3")

    // Espresso UI Testing
    // Unit Tests
    testImplementation("junit:junit:4.13")
    testImplementation("org.hamcrest:hamcrest-all:1.3")
    testImplementation("org.powermock:powermock-module-junit4:1.6.5")
    // Espresso Idling Resource

    // Dependencies for Android unit tests
    androidTestImplementation("junit:junit:4.13")
    androidTestImplementation("org.mockito:mockito-core:2.19.0")
    androidTestImplementation("com.google.dexmaker:dexmaker:1.2")
    androidTestImplementation("com.google.dexmaker:dexmaker-mockito:1.2")

    // Android Testing Support Library's runner and rules
    androidTestImplementation("org.assertj:assertj-core:3.11.1")

    // Espresso UI Testing dependencies.
    implementation("androidx.test.espresso:espresso-idling-resource:3.1.0") {
        exclude(group = "javax.inject")
    }
    androidTestImplementation("androidx.test.espresso:espresso-core:3.2.0") {
        exclude(group = "javax.inject")
    }
    androidTestImplementation("androidx.test:runner:1.2.0")
    androidTestImplementation("androidx.test:rules:1.2.0")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.1.0") {
        exclude(group = "javax.inject")
    }
}

// This is a workaround for https://issuetracker.google.com/issues/78547461
fun com.android.build.gradle.internal.dsl.TestOptions.UnitTestOptions.all(block: Test.() -> Unit) =
        all(KotlinClosure1<Any, Test>({ (this as Test).apply(block) }, owner = this))
