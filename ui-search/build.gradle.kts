plugins {
    id(BuildPlugins.androidLibrary)
    id(BuildPlugins.kotlinAndroid)
    id(BuildPlugins.kotlinAndroidExtensions)
}

apply(from = "$rootDir/jacoco.gradle")

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
        java.srcDirs("src/test/kotlin")
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

    testImplementation(project(":test-ui"))
    testImplementation(TestLibraries.Junit.junit)
    testImplementation(TestLibraries.Mockk.mockk)
    testImplementation(TestLibraries.Truth.truth)
    testImplementation(Libraries.Rtpi.rtpiStaticData)
}
