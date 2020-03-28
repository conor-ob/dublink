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

    api(Libraries.AndroidX.appCompat)
    api(Libraries.AndroidX.navigationFragmentKtx)
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
