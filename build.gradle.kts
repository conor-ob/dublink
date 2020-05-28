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
        classpath (BuildPlugins.jacocoGradlePlugin)
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        mavenLocal()
        mavenCentral()
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
    apply(from = "$rootDir/quality/lint/ktlint.gradle.kts")
    apply(from = "$rootDir/quality/test/logging.gradle")
}

tasks.register("clean").configure {
    delete("build")
}
