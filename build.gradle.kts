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
        maven(url = uri("https://maven.pkg.github.com/conor-ob/dublin-rtpi-service")) {
            name = "GitHubPackages"
            credentials {
                username = properties.getOrDefault(
                    "usr",
                    if (project.rootProject.file("github.properties").exists()) {
                        loadProperties("github.properties").getProperty("usr")
                    } else {
                        null
                    }
                ) as String?
                password = properties.getOrDefault(
                    "key",
                    if (project.rootProject.file("github.properties").exists()) {
                        loadProperties("github.properties").getProperty("key")
                    } else {
                        null
                    }
                ) as String?
            }
        }
    }
    apply(from = "$rootDir/ktlint.gradle.kts")
    apply(from = "$rootDir/test.gradle")
}

tasks.register("clean").configure {
    delete("build")
}
