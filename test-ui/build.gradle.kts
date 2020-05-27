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
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    sourceSets.getByName("main") {
        java.srcDir("src/main/kotlin")
    }

    androidExtensions {
        isExperimental = true
    }
}

dependencies {
    implementation(Libraries.Kotlin.stdLib)

    implementation(Libraries.Groupie.groupie)
    implementation(Libraries.Groupie.groupieKtx)
    implementation(Libraries.AndroidX.lifecycleLiveData)
    implementation(Libraries.AndroidX.recyclerView)

    implementation(TestLibraries.Junit.junit)
    implementation(TestLibraries.Truth.truth)
}
