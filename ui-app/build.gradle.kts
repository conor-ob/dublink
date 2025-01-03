plugins {
    id(BuildPlugins.androidLibrary)
    id(BuildPlugins.kotlinAndroid)
    id(BuildPlugins.kotlinAndroidExtensions)
}

apply(from = "$rootDir/quality/coverage/androidJacoco.gradle")

android {
    compileSdkVersion(AndroidSdk.compile)

    defaultConfig {
        minSdkVersion(AndroidSdk.min)
        targetSdkVersion(AndroidSdk.target)
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    sourceSets.getByName("main") {
        java.srcDir("src/main/kotlin")
    }
    sourceSets.getByName("test") {
        java.srcDir("src/test/kotlin")
    }

    androidExtensions {
        isExperimental = true
    }
}

dependencies {
    implementation(project(":domain"))

    api(Libraries.Android.material)
    api(Libraries.AndroidX.appCompat)
    api(Libraries.AndroidX.constraintLayout)
    api(Libraries.AndroidX.coreKtx)
    api(Libraries.AndroidX.navigationFragmentKtx)
    api(Libraries.AndroidX.navigationUiKtx)
    api(Libraries.AndroidX.lifecycleExtensions)
    api(Libraries.AndroidX.fragment)
    api(Libraries.AndroidX.lifecycleLiveData)
    api(Libraries.AndroidX.lifecycleViewModel)
    api(Libraries.AndroidX.recyclerView)
    api(Libraries.AndroidX.swipeRefreshLayout)
    api(Libraries.Dagger.dagger)
    api(Libraries.Dagger.daggerAndroid)
    api(Libraries.Dagger.daggerAndroidSupport)
    api(Libraries.Groupie.groupie)
    api(Libraries.Groupie.groupieKtx)
    api(Libraries.Roxie.roxie)
    api(Libraries.Rtpi.rtpiApi)
    api(Libraries.Rtpi.rtpiUtil)
    api(Libraries.Rx.rxJava)
    api(Libraries.Rx.rxKotlin)
    api(Libraries.Kotlin.stdLib)
    api(Libraries.Timber.timber)

    testImplementation(project(":test-ui"))
    testImplementation(TestLibraries.Junit.junit)
//    testImplementation(TestLibraries.Mockk.mockk)
    testImplementation(TestLibraries.Truth.truth)
}
