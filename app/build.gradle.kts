import org.jetbrains.kotlin.konan.properties.loadProperties
import java.io.BufferedReader
import java.time.format.DateTimeFormatter
import java.time.ZonedDateTime
import java.time.ZoneId
import java.util.Properties

plugins {
    id(BuildPlugins.androidApplication)
    id(BuildPlugins.kotlinAndroid)
    id(BuildPlugins.kotlinAndroidExtensions)
    id(BuildPlugins.kotlinKapt)
    id(BuildPlugins.googleServices)
    id(BuildPlugins.firebaseCrashlytics)
}

val gitBranch = executeGitCommand("git rev-parse --abbrev-ref HEAD")
val gitCommitHash = executeGitCommand("git rev-parse --short HEAD")
val apkBuildDateTime: String = ZonedDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ISO_LOCAL_DATE)
val properties = if (project.rootProject.file("release.properties").exists()) {
    loadProperties("release.properties")
} else {
    Properties()
}

android {
    compileSdkVersion(AndroidSdk.compile)

    defaultConfig {
        applicationId = BuildConfig.applicationId
        minSdkVersion(AndroidSdk.min)
        targetSdkVersion(AndroidSdk.target)
        versionCode = BuildConfig.Version.code
        versionName = BuildConfig.Version.name
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "YWZBCDLN", "\"${properties.getProperty("YWZBCDLN")}\"")
        buildConfigField("String", "NZAYAPNK", "\"${properties.getProperty("NZAYAPNK")}\"")
        buildConfigField("String", "RNXDCRHY", "\"${properties.getProperty("RNXDCRHY")}\"")
        buildConfigField("String", "XVTOHYIW", "\"${properties.getProperty("XVTOHYIW")}\"")
        buildConfigField("String", "KQVBLPMG", "\"${properties.getProperty("KQVBLPMG")}\"")
    }

    signingConfigs {
        create("release") {
            keyAlias(properties.getProperty("keyAlias") ?: null)
            keyPassword(properties.getProperty("keyPassword") ?: null)
            storePassword(properties.getProperty("storePassword") ?: null)
            storeFile(
                if (properties.getProperty("storeFile") == null) {
                    null
                } else {
                    file(properties.getProperty("storeFile", ""))
                }
            )
            isV2SigningEnabled = true
        }
    }

    buildTypes {
        getByName("release") {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }
        getByName("debug") {
            isDebuggable = true
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-$gitBranch-$apkBuildDateTime-$gitCommitHash"
        }
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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(project(":database"))
    implementation(project(":domain"))
    implementation(project(":repository"))
    implementation(project(":ui-app"))
    implementation(project(":ui-favourites"))
    implementation(project(":ui-iap"))
    implementation(project(":ui-livedata"))
    implementation(project(":ui-search"))
    implementation(project(":ui-settings"))
    implementation(project(":ui-web"))

    implementation(Libraries.Android.googlePlayBilling)
    implementation(Libraries.Android.googlePlayBillingKtx)
    implementation(Libraries.Android.playServicesLocation)
    implementation(Libraries.AndroidX.constraintLayout)
    implementation(Libraries.AndroidX.navigationUiKtx)
    implementation(Libraries.AndroidX.preferenceKtx)
    implementation(Libraries.Dagger.dagger)
    implementation(Libraries.Dagger.daggerAndroid)
    implementation(Libraries.Firebase.analytics)
    implementation(Libraries.Firebase.crashlytics)
    implementation(Libraries.Location.reactiveLocation)
    implementation(Libraries.Nodes.logViewer)
    implementation(Libraries.OkHttp.okhttp)
    implementation(Libraries.OkHttp.loggingInterceptor)
    implementation(Libraries.Rtpi.rtpiClient)
    implementation(Libraries.Rx.rxAndroid)
    implementation(Libraries.Store.store3)

    debugImplementation(Libraries.LeakCanary.leakCanary)

    kapt(Libraries.Dagger.daggerCompiler)
    kapt(Libraries.Dagger.daggerAndroidProcessor)
}

fun executeGitCommand(command: String): String =
    Runtime
        .getRuntime()
        .exec(command)
        .let<Process, String> { process ->
            process.waitFor()
            val output = process.inputStream.use {
                it.bufferedReader().use(BufferedReader::readText)
            }
            process.destroy()
            output.trim()
        }
