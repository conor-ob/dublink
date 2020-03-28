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
    const val min = 29
    const val compile = 29
    const val target = 29
}

object BuildPlugins {

    private object Versions {
        const val buildToolsVersion = "3.5.3"
        const val fabric = "1.29.0"
        const val sqlDelight = "1.2.2"
    }

    const val androidGradlePlugin = "com.android.tools.build:gradle:${Versions.buildToolsVersion}"
    const val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion"
    const val sqlDelightGradlePlugin = "com.squareup.sqldelight:gradle-plugin:${Versions.sqlDelight}"

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

        object Location {
            const val playServicesLocation = "com.google.android.gms:play-services-location:17.0.0"
        }
    }

    object AndroidX {

        private object Versions {
        }

        const val appCompat = "androidx.appcompat:appcompat:1.1.0"

        object ConstraintLayout {
            const val constraintLayout = "androidx.constraintlayout:constraintlayout:1.1.3"
        }

        object Fragment {
            const val fragment = "androidx.fragment:fragment:1.2.3"
        }

        object Lifecycle {
            const val lifecycleExtensions = "androidx.lifecycle:lifecycle-extensions:2.2.0"
            const val liveData =  "androidx.lifecycle:lifecycle-livedata-core:2.2.0"
        }

        object Navigation {
            const val fragmentKtx = "androidx.navigation:navigation-fragment-ktx:2.1.0"
            const val uiKtx = "androidx.navigation:navigation-ui-ktx:2.1.0"
        }

        object Preference {
            const val preference = "androidx.preference:preference-ktx:1.1.0"
        }

        object SwipeRefresh {
            const val swipeRefreshLayout = "androidx.swiperefreshlayout:swiperefreshlayout:1.0.0"
        }
    }

    object Dagger {
        private const val version = 2.24
        const val dagger = "com.google.dagger:dagger:$version"
        const val daggerAndroid = "com.google.dagger:dagger-android:$version"
        const val daggerAndroidProcessor =  "com.google.dagger:dagger-android-processor:$version"
        const val daggerAndroidSupport = "com.google.dagger:dagger-android-support:$version"
        const val daggerCompiler =  "com.google.dagger:dagger-compiler:$version"
    }

    object Facebook {
        const val stetho = "com.facebook.stetho:stetho:1.5.1"
        const val stethoOkhttp = "com.facebook.stetho:stetho-okhttp3:1.5.1"
    }

    object Groupie {
        private const val version = "2.3.0"
        const val groupie = "com.xwray:groupie:$version"
        const val groupieKtx = "com.xwray:groupie-kotlin-android-extensions:$version"
    }

    object Kotlin {
        const val stdLib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion"
    }

    object Location {
        const val reactiveLocation = "pl.charmas.android:android-reactive-location2:2.1@aar"
    }

    object Material {
        const val material = "com.google.android.material:material:1.2.0-alpha02"
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

        private object Versions {
            const val rtpi = "0.1.0"
        }

        const val rtpiApi = "io.rtpi:rtpi-api:${Versions.rtpi}"
        const val rtpiClient = "io.rtpi:rtpi-client:${Versions.rtpi}"
        const val rtpiUtil = "io.rtpi:rtpi-util:${Versions.rtpi}"
    }

    object Rx {

        private object Versions {
            const val rxAndroid = "2.1.1"
            const val rxJava = "2.2.8"
            const val rxKotlin = "2.4.0"
        }

        const val rxAndroid = "io.reactivex.rxjava2:rxandroid:${Versions.rxAndroid}"
        const val rxJava = "io.reactivex.rxjava2:rxjava:${Versions.rxJava}"
        const val rxKotlin = "io.reactivex.rxjava2:rxkotlin:${Versions.rxKotlin}"
    }

    object SqlDelight {
        private const val version = "1.2.0"
        const val androidDriver = "com.squareup.sqldelight:android-driver:$version"
        const val rxJavaExtensions = "com.squareup.sqldelight:rxjava2-extensions:$version"
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

    private object Versions {
        const val junit = "4.12"
        const val truth = "0.42"
        const val sqlDelight = "1.2.0"
    }

    const val junit = "junit:junit:${Versions.junit}"
    const val truth = "com.google.truth:truth:${Versions.truth}"


    object SqlDelight {
        private const val version = "1.2.0"
        const val sqliteDriver = "com.squareup.sqldelight:sqlite-driver:$version"
    }
}
