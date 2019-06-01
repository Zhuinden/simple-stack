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
        applicationId = "com.zhuinden.simplestackexamplemvvm"
        minSdkVersion(14)
        targetSdkVersion(27)
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = true
            // Uses new built-in shrinker http://tools.android.com/tech-docs/new-build-system/built-in-shrinker
            isUseProguard = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            testProguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguardTest-rules.pro")
        }

        getByName("release") {
            isMinifyEnabled = true
            isUseProguard = true
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
    implementation("com.android.support:appcompat-v7:27.1.1")
    implementation("com.android.support:cardview-v7:27.1.1")
    implementation("com.android.support:design:27.1.1")
    implementation("com.android.support:recyclerview-v7:27.1.1")
    implementation("com.android.support:support-v4:27.1.1")

    // live data
    implementation("android.arch.lifecycle:runtime:1.1.0")
    implementation("android.arch.lifecycle:extensions:1.1.0")
    annotationProcessor("android.arch.lifecycle:compiler:1.1.0")

    // useful stuff
    implementation("com.jakewharton:butterknife:8.8.1")
    annotationProcessor("com.jakewharton:butterknife-compiler:8.8.1")
    compileOnly("com.google.auto.value:auto-value:1.5.2")
    annotationProcessor("com.google.auto.value:auto-value:1.5.2")
    implementation("nz.bradcampbell:paperparcel:2.0.4")
    annotationProcessor("nz.bradcampbell:paperparcel-compiler:2.0.4")
    annotationProcessor("com.github.reggar:auto-value-ignore-hash-equals:1.1.4")
    implementation("com.google.dagger:dagger:2.14.1")
    annotationProcessor("com.google.dagger:dagger-compiler:2.14.1")

    // Dependencies for local unit tests
    testImplementation("junit:junit:4.12")
    testImplementation("org.assertj:assertj-core:3.9.1")
    testImplementation("org.mockito:mockito-core:2.15.0")

    testImplementation("junit:junit:4.12")
    testImplementation("org.mockito:mockito-all:1.10.19")
    testImplementation("org.hamcrest:hamcrest-all:1.3")

    // Android Testing Support Library's runner and rules
    androidTestImplementation("com.android.support.test:runner:1.0.1")
    androidTestImplementation("com.android.support.test:rules:1.0.1")

    // Espresso UI Testing
    // Unit Tests
    testImplementation("junit:junit:4.12")
    testImplementation("org.hamcrest:hamcrest-all:1.3")
    testImplementation("org.powermock:powermock-module-junit4:1.6.5")
    // Espresso Idling Resource

    // Dependencies for Android unit tests
    androidTestImplementation("junit:junit:4.12")
    androidTestImplementation("org.mockito:mockito-core:2.15.0")
    androidTestImplementation("com.google.dexmaker:dexmaker:1.2")
    androidTestImplementation("com.google.dexmaker:dexmaker-mockito:1.2")

    // Android Testing Support Library's runner and rules
    androidTestImplementation("org.assertj:assertj-core:3.9.1")

    androidTestImplementation("com.android.support.test:runner:1.0.1") {
        exclude(group = "com.android.support", module = "support-annotations")
    }
    androidTestImplementation("com.android.support.test:rules:1.0.1") {
        exclude(group = "com.android.support", module = "support-annotations")
    }

    // Espresso UI Testing dependencies.
    implementation("com.android.support.test.espresso:espresso-idling-resource:3.0.1") {
        exclude(group = "javax.inject")
        exclude(group = "com.android.support", module = "support-annotations")
    }
    androidTestImplementation("com.android.support.test.espresso:espresso-core:3.0.1") {
        exclude(group = "javax.inject")
        exclude(group = "com.android.support", module = "support-annotations")
    }
    androidTestImplementation("com.android.support.test.espresso:espresso-contrib:3.0.1") {
        exclude(group = "javax.inject")
        exclude(group = "com.android.support", module = "support-annotations")
        exclude(group = "com.android.support", module = "appcompat")
        exclude(group = "com.android.support", module = "design")
        exclude(group = "com.android.support", module = "support-v4")
        exclude(module = "recyclerview-v7")
    }
    androidTestImplementation("com.android.support.test.espresso:espresso-intents:3.0.1") {
        exclude(group = "com.android.support", module = "support-annotations")
    }
    androidTestImplementation("com.squareup.assertj:assertj-android:1.1.1") {
        exclude(group = "com.android.support", module = "support-annotations")
    }
    androidTestImplementation("com.squareup.assertj:assertj-android-support-v4:1.1.1") {
        exclude(group = "com.android.support", module = "support-v4")
        exclude(group = "com.android.support", module = "support-annotations")
    }
    //androidTestImplementation 'com.squareup.assertj:assertj-android-play-services:1.1.1'
    androidTestImplementation("com.squareup.assertj:assertj-android-appcompat-v7:1.1.1") {
        exclude(group = "com.android.support", module = "appcompat-v7")
        exclude(group = "com.android.support", module = "support-annotations")
    }
    androidTestImplementation("com.squareup.assertj:assertj-android-design:1.1.1") {
        exclude(group = "com.android.support", module = "design")
        exclude(group = "com.android.support", module = "support-annotations")
    }
    //androidTestImplementation("com.squareup.assertj:assertj-android-mediarouter-v7:1.1.1")
    //androidTestImplementation("com.squareup.assertj:assertj-android-gridlayout-v7:1.1.1")
    //androidTestImplementation("com.squareup.assertj:assertj-android-cardview-v7:1.1.1")
    androidTestImplementation("com.squareup.assertj:assertj-android-recyclerview-v7:1.1.1") {
        exclude(group = "com.android.support", module = "recyclerview-v7")
        exclude(group = "com.android.support", module = "support-annotations")
    }
    //androidTestImplementation("com.squareup.assertj:assertj-android-palette-v7:1.1.1")
}

configurations.all {
    resolutionStrategy.force("com.android.support:support-annotations:23.1.1")
}

//configurations.compile.get().dependencies.forEach { compileDependency ->
//    println("Excluding implementation dependency: ${compileDependency.name}")
//    configurations.androidTestCompile.get().dependencies.forEach {
//        configurations.androidTestCompile.get().exclude(module = compileDependency.name)
//    }
//}

// This is a workaround for https://issuetracker.google.com/issues/78547461
fun com.android.build.gradle.internal.dsl.TestOptions.UnitTestOptions.all(block: Test.() -> Unit) =
        all(KotlinClosure1<Any, Test>({ (this as Test).apply(block) }, owner = this))
