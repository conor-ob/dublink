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
        classpath (BuildPlugins.googleServiceGradlePlugin)
        classpath (BuildPlugins.firebaseCrashlyticsGradlePlugin)
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        mavenLocal()
        maven(url = "https://jitpack.io")
        maven(url = "http://streamreasoning.org/maven/")
    }
    apply(from = "$rootDir/ktlint.gradle.kts")
}

tasks.register("clean").configure {
    delete("build")
}
