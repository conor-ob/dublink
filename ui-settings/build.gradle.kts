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

    kotlinOptions {
        jvmTarget = "1.8"
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

    implementation(Libraries.AndroidX.preferenceKtx)
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
//    compileOptions {
//        sourceCompatibility = 1.8
//        targetCompatibility = 1.8
//    }
//
//    kotlinOptions {
//        jvmTarget = "1.8"
//    }
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
//    implementation "androidx.preference:preference-ktx:1.1.0"
//
//    implementation "androidx.fragment:fragment-ktx:1.2.0-rc02"
//
//    testImplementation testLibraries.junit
//    testImplementation testLibraries.truth
//    testImplementation group: 'io.mockk', name: 'mockk', version: '1.9.3'
//}
