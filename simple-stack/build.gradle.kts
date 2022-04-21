plugins {
    id("com.android.library")
    id("maven-publish")
}

android {
    compileSdk = 31

    defaultConfig {
        minSdk = 9
        targetSdk = 31
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }

    lintOptions {
        isAbortOnError = false
    }

    compileOptions {
        this.sourceCompatibility = JavaVersion.VERSION_1_7
        this.targetCompatibility = JavaVersion.VERSION_1_7
    }
}

dependencies {
    //implementation(mapOf("dir" to "libs", "include" to listOf("*.jar")))
    api("com.google.code.findbugs:jsr305:3.0.2")
    api("com.github.Zhuinden:state-bundle:1.4.0")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.assertj:assertj-core:3.16.1")
    testImplementation("org.mockito:mockito-core:3.8.0")
    testImplementation("org.robolectric:robolectric:4.2.1")
    testImplementation("org.apache.maven:maven-ant-tasks:2.1.3")
    androidTestImplementation("junit:junit:4.13.2")
}

// build a jar with source files
val sourcesJar by tasks.registering(Jar::class) {
    from(android.sourceSets["main"].java.srcDirs)
    archiveClassifier.set("sources")
}

val javadoc by tasks.registering(Javadoc::class) {
    configurations.implementation.get().isCanBeResolved = true
    configurations.api.get().isCanBeResolved = true

    isFailOnError = false
    source = android.sourceSets["main"].java.getSourceFiles()
    classpath += project.files(android.bootClasspath.joinToString(separator = File.pathSeparator))
    classpath += configurations.api
}

// build a jar with javadoc
val javadocJar by tasks.registering(Jar::class) {
    dependsOn(javadoc)
    archiveClassifier.set("javadoc")
    from(javadoc.get().destinationDir)
}

artifacts {
    archives(sourcesJar)
    archives(javadocJar)
}

afterEvaluate {
    publishing {
        publications {
            register("mavenJava", MavenPublication::class) {
                groupId = "com.github.Zhuinden"
                artifactId = "simple-stack"
                version = "2.6.3"

                from(components["release"])
                artifact(sourcesJar.get())

                pom.withXml {
                    val dependenciesNode = asNode().appendNode("dependencies")
                    val configurationNames = arrayOf("implementation", "api")

                    configurationNames.forEach { configurationName ->
                        configurations[configurationName].allDependencies.forEach {
                            if (it.group != null && it.version != "unspecified") {
                                val dependencyNode = dependenciesNode.appendNode("dependency")
                                dependencyNode.appendNode("groupId", it.group)
                                dependencyNode.appendNode("artifactId", it.name)
                                dependencyNode.appendNode("version", it.version)
                            }
                        }
                    }
                }
            }
        }
    }
}

