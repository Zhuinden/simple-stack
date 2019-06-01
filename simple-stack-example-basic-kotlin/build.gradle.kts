apply plugin: 'com.android.application'
apply plugin: 'kotlin-android'
apply plugin: 'kotlin-android-extensions'
apply plugin: 'kotlin-kapt'

android {
    compileSdkVersion 27

    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
    defaultConfig {
        applicationId "com.zhuinden.simplestackexamplekotlin"
        minSdkVersion 14
        targetSdkVersion 27
        versionCode 1
        versionName "1.0"
        testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
    }
    sourceSets {
        main.java.srcDirs += 'src/main/kotlin'
    }
}

kapt {
    generateStubs = true
}

androidExtensions {
    experimental = true
}

dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])
    implementation project(':simple-stack')

    androidTestImplementation('com.android.support.test.espresso:espresso-core:3.0.1', {
        exclude group: 'com.android.support', module: 'support-annotations'
    })
    implementation 'com.android.support:appcompat-v7:27.1.1'
    implementation 'com.android.support:design:27.1.1'
    testImplementation 'junit:junit:4.12'

    implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.3.21"
}
