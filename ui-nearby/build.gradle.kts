plugins {
    id(BuildPlugins.androidLibrary)
    id(BuildPlugins.kotlinAndroid)
    id(BuildPlugins.kotlinAndroidExtensions)
}

android {
    compileSdkVersion(AndroidSdk.compile)

    defaultConfig {
        minSdkVersion(AndroidSdk.min)
        targetSdkVersion(AndroidSdk.target)
    }

    sourceSets.getByName("main") {
        java.srcDir("src/main/kotlin")
    }

    androidExtensions {
        isExperimental = true
    }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":core-ui"))
    implementation(project(":domain"))
}

//apply plugin: 'com.android.library'
//apply plugin: 'kotlin-android'
//apply plugin: 'kotlin-android-extensions'
//
//android {
//    compileSdkVersion buildConfig.compileSdk
//
//    defaultConfig {
//        minSdkVersion buildConfig.minSdk
//        targetSdkVersion buildConfig.targetSdk
//    }
//
//    sourceSets {
//        main.java.srcDirs += 'src/main/kotlin'
//        test.java.srcDirs += 'src/test/kotlin'
//        androidTest.java.srcDirs += 'src/androidTest/kotlin'
//    }
//}
//
//dependencies {
//    implementation project(':core')
//    implementation project(':core-ui')
//
//    implementation libraries.rtpi_api
//}
