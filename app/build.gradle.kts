import org.jetbrains.kotlin.konan.properties.loadProperties
import java.io.BufferedReader
import java.time.format.DateTimeFormatter
import java.time.ZonedDateTime
import java.time.ZoneId

plugins {
    id(BuildPlugins.androidApplication)
    id(BuildPlugins.kotlinAndroid)
    id(BuildPlugins.kotlinAndroidExtensions)
    id(BuildPlugins.kotlinKapt)
}

val properties = if (project.rootProject.file("release.properties").exists()) {
    loadProperties("release.properties")
} else {
    loadProperties("debug.properties")
}

val apkBuildDateTime: String = ZonedDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ISO_LOCAL_DATE)
val gitCommitHash = Runtime
    .getRuntime()
    .exec("git rev-parse --short HEAD")
    .let<Process, String> { process ->
        process.waitFor()
        val output = process.inputStream.use {
            it.bufferedReader().use(BufferedReader::readText)
        }
        process.destroy()
        output.trim()
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
        manifestPlaceholders = mapOf("googleMapsApiKey" to properties.getProperty("com.google.android.geo.API_KEY"))
        buildConfigField("String", "JCDECAUX_API_KEY", "\"${properties.getProperty("com.developer.jcdecaux.API_KEY")}\"")
        buildConfigField("String", "TWITTER_CONSUMER_KEY", "\"${properties.getProperty("com.twitter.sdk.android.CONSUMER_KEY")}\"")
        buildConfigField("String", "TWITTER_CONSUMER_SECRET", "\"${properties.getProperty("com.twitter.sdk.android.CONSUMER_SECRET")}\"")
    }

    buildTypes {
        getByName("release") {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            isUseProguard = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
        getByName("debug") {
            isDebuggable = true
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-dev-$apkBuildDateTime-$gitCommitHash"
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
    implementation(project(":ui-nearby"))
    implementation(project(":ui-news"))
    implementation(project(":ui-search"))
    implementation(project(":ui-settings"))

    implementation(Libraries.Android.playServicesLocation)
    implementation(Libraries.AndroidX.constraintLayout)
    implementation(Libraries.AndroidX.navigationUiKtx)
    implementation(Libraries.AndroidX.preferenceKtx)
    implementation(Libraries.Dagger.dagger)
    implementation(Libraries.Dagger.daggerAndroid)
    implementation(Libraries.Stetho.stetho)
    implementation(Libraries.Stetho.stethoOkhttp)
    implementation(Libraries.Location.reactiveLocation)
    implementation(Libraries.OkHttp.okhttp)
    implementation(Libraries.OkHttp.loggingInterceptor)
    implementation(Libraries.Rtpi.rtpiClient)
    implementation(Libraries.Rx.rxAndroid)
    implementation(Libraries.Store.store3)
    implementation(Libraries.Twitter.tweetUi)

    kapt(Libraries.Dagger.daggerCompiler)
    kapt(Libraries.Dagger.daggerAndroidProcessor)
}

if (file("google-services.json").exists()) {
    plugins {
        id(BuildPlugins.googleServices)
        id(BuildPlugins.fabric)
    }
}
