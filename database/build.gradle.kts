plugins {
    id(BuildPlugins.androidLibrary)
    id(BuildPlugins.kotlinAndroid)
    id(BuildPlugins.kotlinKapt)
    id(BuildPlugins.sqlDelight)
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
    implementation(Libraries.Rtpi.rtpiApi)
    implementation(Libraries.Rtpi.rtpiUtil)
    implementation(Libraries.Rx.rxJava)
    implementation(Libraries.SqlDelight.androidDriver)
    implementation(Libraries.SqlDelight.rxJavaExtensions)

    kapt(Libraries.Dagger.daggerCompiler)

    testImplementation(TestLibraries.Junit.junit)
    testImplementation(TestLibraries.SqlDelight.sqliteDriver)
}
