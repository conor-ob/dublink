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
//        classpath (BuildPlugins.fabricGradlePlugin)
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven(url = "https://jitpack.io") {
            credentials {
                username = username
                password = loadProperties("gradle.properties").getProperty("authToken")
            }
        }
    }
    apply(from = "$rootDir/ktlint.gradle.kts")
}

tasks.register("clean").configure {
    delete("build")
}
