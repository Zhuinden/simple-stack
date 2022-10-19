// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()
        mavenCentral()
        maven { setUrl("https://clojars.org/repo/") }
        maven { setUrl("https://jitpack.io") }
        maven { setUrl("https://maven.google.com") }
        jcenter()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.3.1")
        classpath("me.tatarka:gradle-retrolambda:3.7.0")
        classpath("io.realm:realm-gradle-plugin:10.12.0")
        classpath("io.realm:realm-transformer:10.12.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.10")
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven { setUrl("https://clojars.org/repo/") }
        maven { setUrl("https://jitpack.io") }
        maven { setUrl("https://oss.sonatype.org/content/repositories/snapshots/") }
        maven { setUrl("https://maven.google.com") }
        jcenter()
    }
}

task("clean") {
    delete(rootProject.buildDir)
}
