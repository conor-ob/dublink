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

    implementation(Libraries.FuzzyWuzzy.javaWuzzy)
    implementation(Libraries.Rx.rxAndroid)

    testImplementation(TestLibraries.Junit.junit)
//    testImplementation(TestLibraries.Mockk.mockk)
    testImplementation(TestLibraries.Truth.truth)
    testImplementation(Libraries.Rtpi.rtpiStaticData)
}
