import org.jetbrains.kotlin.konan.properties.loadProperties

buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath (BuildPlugins.androidGradlePlugin)
        classpath (BuildPlugins.kotlinGradlePlugin)
        classpath (BuildPlugins.sqlDelightGradlePlugin)
//        classpath (BuildPlugins.googleServiceGradlePlugin)
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        mavenLocal()
        maven(url = "https://jitpack.io") {
            credentials {
                username = properties.getOrDefault(
                    "authToken",
                    if (project.rootProject.file("release.properties").exists()) {
                        loadProperties("release.properties").getProperty("authToken")
                    } else {
                        null
                    }
                ) as String?
            }
        }
    }
    apply(from = "$rootDir/ktlint.gradle.kts")
}

tasks.register("clean").configure {
    delete("build")
}
