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
    implementation(project(":domain"))

//    api("androidx.navigation:navigation-runtime:2.1.0")
    api(Libraries.AndroidX.navigationFragmentKtx)

    api(Libraries.AndroidX.appCompat)
    api(Libraries.AndroidX.lifecycleExtensions)
    api(Libraries.AndroidX.fragment)
    api(Libraries.AndroidX.lifecycleLiveData)
    api(Libraries.Dagger.dagger)
    api(Libraries.Dagger.daggerAndroid)
    api(Libraries.Dagger.daggerAndroidSupport)
    api(Libraries.Groupie.groupie)
    api(Libraries.Groupie.groupieKtx)
    api(Libraries.Roxie.roxie)
    api(Libraries.Rtpi.rtpiApi)
    api(Libraries.Rx.rxKotlin)
    api(Libraries.Kotlin.stdLib)
    api(Libraries.Android.material)
    api(Libraries.Orika.orikaCore)
    api(Libraries.Timber.timber)
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
//
//    androidExtensions {
//        experimental = true
//    }
//}
//
//dependencies {
//    api project(':domain')
//    implementation project(':core')
//
//    api "com.google.android.material:material:1.2.0-alpha02"
//
//    api libraries.rtpi_api
//
//    api "androidx.appcompat:appcompat:${versions.androidx.appcompat}"
////    api "androidx.core:core-ktx:${versions.androidx.core_ktx}"
//
////    api "androidx.navigation:navigation-runtime:2.1.0"
//        api "android.arch.navigation:navigation-ui-ktx:1.0.0"
//    api "android.arch.navigation:navigation-fragment-ktx:1.0.0"
//
//    api "android.arch.lifecycle:extensions:1.1.1"
////    api "android.arch.lifecycle:viewmodel:1.1.1"
//
////    api "com.google.dagger:dagger-android:${versions.dagger}"
//    api "com.google.dagger:dagger-android-support:${versions.dagger}"
////    kapt "com.google.dagger:dagger-compiler:${versions.dagger_compiler}"
////    kapt "com.google.dagger:dagger-android-processor:${versions.dagger}"
//
//    api "com.jakewharton.timber:timber:${versions.timber}"
//
//    api 'androidx.constraintlayout:constraintlayout:1.1.3'
//
//    compile group: 'org.apache.commons', name: 'commons-lang3', version: '3.9'
//
//    compile group: 'androidx.swiperefreshlayout', name: 'swiperefreshlayout', version: '1.0.0'
//
//
//
//    api libraries.androidx.constraintlayout
//    api libraries.roxie
//
//    api libraries.rx_android
//
//    api "com.xwray:groupie:${versions.groupie}"
//    api "com.xwray:groupie-kotlin-android-extensions:${versions.groupie}"
//
//    api "ma.glasnost.orika:orika-core:${versions.orika_core}"
//}
