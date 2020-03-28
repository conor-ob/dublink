private const val kotlinVersion = "1.3.71"

object BuildConfig {
    const val applicationId = "ie.dublinmapper"

    object Version {
        private const val major = 0
        private const val minor = 1
        private const val patch = 0

        const val name = "$major.$minor.$patch"
        const val code = major * 100000 + minor * 1000 + patch * 10
    }
}

object AndroidSdk {
    const val min = 26
    const val compile = 29
    const val target = 29
}

object BuildPlugins {
    const val androidGradlePlugin = "com.android.tools.build:gradle:3.5.3"
    const val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion"
    const val sqlDelightGradlePlugin = "com.squareup.sqldelight:gradle-plugin:1.2.2"

    const val androidApplication = "com.android.application"
    const val androidLibrary = "com.android.library"
    const val fabric = "io.fabric"
    const val googleServices = "com.google.gms.google-services"
    const val kotlin = "kotlin"
    const val kotlinAndroid = "kotlin-android"
    const val kotlinAndroidExtensions = "kotlin-android-extensions"
    const val kotlinKapt = "kotlin-kapt"
    const val sqlDelight = "com.squareup.sqldelight"
}

object Libraries {

    object Android {
        const val material = "com.google.android.material:material:1.2.0-alpha02"
        const val playServicesLocation = "com.google.android.gms:play-services-location:17.0.0"
    }

    object AndroidX {
        const val appCompat = "androidx.appcompat:appcompat:1.1.0"
        const val constraintLayout = "androidx.constraintlayout:constraintlayout:1.1.3"
        const val fragment = "androidx.fragment:fragment:1.2.3"
        const val lifecycleExtensions = "androidx.lifecycle:lifecycle-extensions:2.2.0"
        const val lifecycleLiveData =  "androidx.lifecycle:lifecycle-livedata-core:2.2.0"
        const val navigationFragmentKtx = "androidx.navigation:navigation-fragment-ktx:2.1.0"
        const val navigationUiKtx = "androidx.navigation:navigation-ui-ktx:2.1.0"
        const val preferenceKtx = "androidx.preference:preference-ktx:1.1.0"
        const val swipeRefreshLayout = "androidx.swiperefreshlayout:swiperefreshlayout:1.0.0"
    }

    object Dagger {
        const val dagger = "com.google.dagger:dagger:2.24"
        const val daggerAndroid = "com.google.dagger:dagger-android:2.24"
        const val daggerAndroidProcessor =  "com.google.dagger:dagger-android-processor:2.24"
        const val daggerAndroidSupport = "com.google.dagger:dagger-android-support:2.24"
        const val daggerCompiler =  "com.google.dagger:dagger-compiler:2.24"
    }

    object Stetho {
        const val stetho = "com.facebook.stetho:stetho:1.5.1"
        const val stethoOkhttp = "com.facebook.stetho:stetho-okhttp3:1.5.1"
    }

    object Groupie {
        const val groupie = "com.xwray:groupie:2.3.0"
        const val groupieKtx = "com.xwray:groupie-kotlin-android-extensions:2.3.0"
    }

    object Kotlin {
        const val stdLib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion"
    }

    object Location {
        const val reactiveLocation = "pl.charmas.android:android-reactive-location2:2.1@aar"
    }

    object OkHttp {
        const val okhttp = "com.squareup.okhttp3:okhttp:3.12.1"
        const val loggingInterceptor = "com.squareup.okhttp3:logging-interceptor:3.12.1"
    }

    object Orika {
        const val orikaCore = "ma.glasnost.orika:orika-core:1.5.4"
    }

    object Roxie {
        const val roxie = "com.ww:roxie:0.4.0"
    }

    object Rtpi {
        const val rtpiApi = "io.rtpi:rtpi-api:0.1.0"
        const val rtpiClient = "io.rtpi:rtpi-client:0.1.0"
        const val rtpiUtil = "io.rtpi:rtpi-util:0.1.0"
    }

    object Rx {
        const val rxAndroid = "io.reactivex.rxjava2:rxandroid:2.1.1"
        const val rxJava = "io.reactivex.rxjava2:rxjava:2.2.8"
        const val rxKotlin = "io.reactivex.rxjava2:rxkotlin:2.4.0"
    }

    object SqlDelight {
        const val androidDriver = "com.squareup.sqldelight:android-driver:1.2.0"
        const val rxJavaExtensions = "com.squareup.sqldelight:rxjava2-extensions:1.2.0"
    }

    object Store {
        const val store = "com.nytimes.android:store3:3.1.1"
    }

    object Timber {
        const val timber = "com.jakewharton.timber:timber:4.7.1"
    }

    object Twitter  {
        const val tweetUi = "com.twitter.sdk.android:tweet-ui:3.3.0"
    }
}

object TestLibraries {

    object Junit {
        const val junit = "junit:junit:4.12"
    }

    object Truth {
        const val truth = "com.google.truth:truth:0.42"
    }

    object SqlDelight {
        const val sqliteDriver = "com.squareup.sqldelight:sqlite-driver:1.2.0"
    }
}
