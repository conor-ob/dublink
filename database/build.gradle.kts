plugins {
    id(BuildPlugins.androidLibrary)
    id(BuildPlugins.kotlinAndroid)
    id(BuildPlugins.kotlinKapt)
    id(BuildPlugins.sqlDelight)
//    id(BuildPlugins.kotlinAndroidExtensions)
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
    sourceSets.getByName("androidTest") {
        java.srcDir("src/androidTest/kotlin")
    }
    sourceSets.getByName("test") {
        java.srcDir("src/test/kotlin")
    }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":data-access"))
    implementation(project(":domain"))

    implementation(Libraries.Dagger.dagger)
    kapt(Libraries.Dagger.daggerCompiler)
    implementation(Libraries.Rtpi.rtpiApi)
    implementation(Libraries.Rtpi.rtpiUtil)
    implementation(Libraries.Rx.rxJava)
    implementation(Libraries.SqlDelight.androidDriver)
    implementation(Libraries.SqlDelight.rxJavaExtensions)

    testImplementation(TestLibraries.Junit.junit)
    testImplementation(TestLibraries.SqlDelight.sqliteDriver)
}

//apply plugin: 'com.android.library'
//apply plugin: 'kotlin-android'
//apply plugin: 'kotlin-kapt'
//apply plugin: "com.squareup.sqldelight"
//
//android {
//    compileSdkVersion buildConfig.compileSdk
//
//    defaultConfig {
//        minSdkVersion buildConfig.minSdk
//        targetSdkVersion buildConfig.targetSdk
//        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
//    }
//
//    sourceSets {
//        main.java.srcDirs += 'src/main/kotlin'
//        test.java.srcDirs += 'src/test/kotlin'
//        androidTest.java.srcDirs += 'src/androidTest/kotlin'
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
//
//}
//
//dependencies {
//    implementation project(':core')
//    implementation project(':domain')
//    implementation project(':data-access')
//
//    implementation libraries.rtpi_api
//    implementation libraries.rtpi_util
//
//    implementation "com.squareup.sqldelight:android-driver:1.2.0"
//    implementation "com.squareup.sqldelight:rxjava2-extensions:1.2.0"
//
//    testImplementation 'junit:junit:4.12'
//    testImplementation "com.squareup.sqldelight:sqlite-driver:1.2.0"
//
//    kapt "com.google.dagger:dagger-compiler:${versions.dagger_compiler}"
//}
