plugins {
    id(BuildPlugins.androidLibrary)
    id(BuildPlugins.kotlinAndroid)
    id(BuildPlugins.kotlinAndroidExtensions)
    id(BuildPlugins.kotlinKapt)
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
        java.srcDirs("src/test/kotlin")
    }

    androidExtensions {
        isExperimental = true
    }
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":ui-app"))

    implementation(Libraries.Apache.luceneAnalyzersCommon)
    implementation(Libraries.Apache.luceneCore)
//    implementation(Libraries.Apache.luceneMemory)
    implementation(Libraries.Apache.luceneQueryParser)
    implementation(Libraries.Rx.rxAndroid)

    testImplementation("androidx.test.ext:junit-ktx:1.1.1")
    testImplementation("androidx.test:core-ktx:1.2.0")
    testImplementation("org.robolectric:robolectric:4.3.1")

    kapt(Libraries.Dagger.daggerCompiler)

    testImplementation("com.google.code.gson:gson:2.8.6")

    testImplementation(project(":test"))
    testImplementation(project(":test-ui"))
    testImplementation(TestLibraries.Junit.junit)
    testImplementation("pl.pragmatists:JUnitParams:1.1.1")
    testImplementation(TestLibraries.Mockk.mockk)
    testImplementation(TestLibraries.Truth.truth)
    testImplementation(Libraries.Rtpi.rtpiStaticData)
}
