import org.jetbrains.kotlin.konan.properties.loadProperties

plugins {
    id(BuildPlugins.androidApplication)
    id(BuildPlugins.kotlinAndroid)
    id(BuildPlugins.kotlinAndroidExtensions)
    id(BuildPlugins.kotlinKapt)
    //apply plugin: 'io.fabric'
}

val properties = if (project.rootProject.file("release.properties").exists()) {
    loadProperties("release.properties")
} else {
    loadProperties("debug.properties")
}

//def apkBuildDateTime = new Date().format('yyyy-MM-dd\'T\'HH:mm:ss')
//def apkBuildDateTime = new Date().format('yyyy-MM-dd')
//def gitCommitCount = "git rev-list HEAD --count".execute().text.trim()
//def gitCommitHash = "git rev-parse HEAD".execute().text.trim().substring(0, 7)

android {
    compileSdkVersion(AndroidSdk.compile)

    defaultConfig {
        applicationId = BuildConfig.applicationId
        minSdkVersion(AndroidSdk.min)
        targetSdkVersion(AndroidSdk.target)
        versionCode = BuildConfig.Version.code
        versionName = BuildConfig.Version.name
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        manifestPlaceholders = mapOf(
            "googleMapsApiKey" to properties.getProperty("com.google.android.geo.API_KEY"),
            "jcDecauxApiKey" to properties.getProperty("com.developer.jcdecaux.API_KEY"),
            "twitterConsumerKey" to properties.getProperty("com.twitter.sdk.android.CONSUMER_KEY"),
            "twitterConsumerSecret" to properties.getProperty("com.twitter.sdk.android.CONSUMER_SECRET")
        )
    }

    buildTypes {
        getByName("release") {
//            debuggable = false
//            minifyEnabled = true
//            shrinkResources = true
//            useProguard = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
        getByName("debug") {
//            debuggable = true
            applicationIdSuffix = ".debug"
//            versionNameSuffix = "-dev-$apkBuildDateTime-$gitCommitHash"
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

//    sourceSets {
//        main.java.srcDirs += 'src/main/kotlin'
//        test.java.srcDirs += 'src/test/kotlin'
//        androidTest.java.srcDirs += 'src/androidTest/kotlin'
//    }

//    flavorDimensions "default"
//    productFlavors {
//        prod {
//            dimension "default"
//        }
//        mock {
//            dimension "default"
//            applicationIdSuffix = ".mock"
//        }
//    }

//    android.variantFilter { variant ->
//        def names = variant.flavors*.name
//        if (variant.buildType.name == 'release' && names.get(0) != "prod") {
//            variant.setIgnore(true)
//        }
//    }

//    compileOptions {
//        sourceCompatibility JavaVersion.VERSION_1_8
//        targetCompatibility JavaVersion.VERSION_1_8
//    }

    // Enables view caching in viewholders.
    // See: https://github.com/Kotlin/KEEP/blob/master/proposals/android-extensions-entity-caching.md
//    androidExtensions {
//        experimental = true
//    }

}

dependencies {
    implementation(project(":core"))
    implementation(project(":database"))
    implementation(project(":data-access"))
    implementation(project(":domain"))
    implementation(project(":repository"))
    implementation(project(":core-ui"))
    implementation(project(":ui-favourites"))
    implementation(project(":ui-livedata"))
    implementation(project(":ui-nearby"))
    implementation(project(":ui-news"))
    implementation(project(":ui-search"))
    implementation(project(":ui-settings"))

    implementation(Libraries.Dagger.dagger)
    implementation(Libraries.Dagger.daggerAndroid)

    kapt(Libraries.Dagger.daggerCompiler)
    kapt(Libraries.Dagger.daggerAndroidProcessor)

    implementation(Libraries.Android.Location.playServicesLocation)
    implementation(Libraries.AndroidX.ConstraintLayout.constraintLayout)
    implementation(Libraries.AndroidX.Preference.preference)
    implementation(Libraries.AndroidX.Navigation.uiKtx)
    implementation(Libraries.Location.reactiveLocation)

    implementation(Libraries.Facebook.stetho)
    implementation(Libraries.Facebook.stethoOkhttp)
    implementation(Libraries.OkHttp.okhttp)
    implementation(Libraries.OkHttp.loggingInterceptor)
    implementation(Libraries.Rtpi.rtpiClient)
    implementation(Libraries.Rx.rxAndroid)
    implementation(Libraries.Store.store)

//    implementation "androidx.preference:preference-ktx:1.1.0"
//
//    implementation 'pl.charmas.android:android-reactive-location2:2.1@aar'
//    implementation 'com.google.android.gms:play-services-location:17.0.0'
//
//    implementation 'com.facebook.stetho:stetho:1.5.1'
//    implementation 'com.facebook.stetho:stetho-okhttp3:1.5.1'
//
//    implementation("com.squareup.okhttp3:okhttp:3.12.1")
//    implementation("com.squareup.okhttp3:logging-interceptor:${versions.logging_interceptor}")
//
//    implementation("com.squareup.retrofit2:converter-gson:2.5.0")
//    implementation("com.google.code.gson:gson:2.8.5")
//    implementation("com.squareup.retrofit2:converter-simplexml:2.5.0")
//    implementation "com.nytimes.android:store3:${versions.store3}"
//    implementation("com.squareup.okhttp3:okhttp:3.12.1")
//
//    implementation libraries.rtpi_client
//
//    kapt "com.google.dagger:dagger-compiler:${versions.dagger_compiler}"
//    kapt "com.google.dagger:dagger-android-processor:${versions.dagger}"
}

//apply plugin: 'com.google.gms.google-services'
