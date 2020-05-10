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
        buildConfigField("String", "JCDECAUX_API_KEY", "\"${properties.getProperty("jcDecauxApiKey")}\"")
    }

    buildTypes {
        getByName("release") {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        getByName("debug") {
            isDebuggable = true
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-$gitBranch-$apkBuildDateTime-$gitCommitHash"
//            ext.enableCrashlytics = false
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
    implementation(project(":ui-livedata"))
    implementation(project(":ui-search"))
    implementation(project(":ui-settings"))

    implementation(Libraries.Android.playServicesLocation)
    implementation(Libraries.AndroidX.constraintLayout)
    implementation(Libraries.AndroidX.navigationUiKtx)
    implementation(Libraries.AndroidX.preferenceKtx)
    implementation(Libraries.Dagger.dagger)
    implementation(Libraries.Dagger.daggerAndroid)
    implementation(Libraries.Location.reactiveLocation)
    implementation(Libraries.Nodes.logViewer)
    implementation(Libraries.OkHttp.okhttp)
    implementation(Libraries.OkHttp.loggingInterceptor)
    implementation(Libraries.Rtpi.rtpiClient)
    implementation(Libraries.Rx.rxAndroid)
    implementation(Libraries.Store.store3)
    implementation(Libraries.Twitter.tweetUi)

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

if (file("google-services.json").exists()) {
    plugins {
        id(BuildPlugins.googleServices)
    }
}
