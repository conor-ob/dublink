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
        mavenLocal()
    }
    apply(from = "$rootDir/ktlint.gradle.kts")
}

tasks.register("clean").configure {
    delete("build")
}
