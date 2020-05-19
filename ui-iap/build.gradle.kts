plugins {
    id(BuildPlugins.androidLibrary)
    id(BuildPlugins.kotlinAndroid)
    id(BuildPlugins.kotlinAndroidExtensions)
    id(BuildPlugins.kotlinKapt)
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

    androidExtensions {
        isExperimental = true
    }
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":ui-app"))

    implementation(Libraries.Android.googlePlayBilling)
    implementation(Libraries.Android.googlePlayBillingKtx)

    implementation(Libraries.Kotlin.stdLib)

    implementation(Libraries.Dagger.dagger)
    implementation(Libraries.Rtpi.rtpiApi)
    implementation(Libraries.Rtpi.rtpiUtil)
    implementation(Libraries.Rx.rxJava)
    api(Libraries.Timber.timber)

    kapt(Libraries.Dagger.daggerCompiler)

    testImplementation(TestLibraries.Junit.junit)
    testImplementation(TestLibraries.Truth.truth)
}
