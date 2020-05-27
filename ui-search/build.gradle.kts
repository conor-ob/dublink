plugins {
    id(BuildPlugins.androidLibrary)
    id(BuildPlugins.kotlinAndroid)
    id(BuildPlugins.kotlinAndroidExtensions)
}

apply(from = "$rootDir/jacoco.gradle")

// ./gradlew :ui-search:testDebugUnitTestCoverage
// bash <(curl -s https://codecov.io/bash) -t <token> -f ui-search/build/reports/jacoco/testDebugUnitTestCoverage/testDebugUnitTestCoverage.xml

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

    testImplementation("androidx.test.ext:junit-ktx:1.1.1")
    testImplementation("androidx.test:core-ktx:1.2.0")
    testImplementation("org.robolectric:robolectric:4.3.1")

    testImplementation(project(":test-ui"))
    testImplementation(TestLibraries.Junit.junit)
    testImplementation(TestLibraries.Mockk.mockk)
    testImplementation(TestLibraries.Truth.truth)
    testImplementation(Libraries.Rtpi.rtpiStaticData)
}
