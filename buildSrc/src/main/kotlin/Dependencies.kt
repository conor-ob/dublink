private const val kotlinVersion = "1.3.71"

object BuildConfig {
    const val applicationId = "io.dublink"

    object Version {
        private const val major = 1
        private const val minor = 1
        private const val patch = 2

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
    const val androidGradlePlugin = "com.android.tools.build:gradle:4.1.0-alpha08" // https://mvnrepository.com/artifact/com.android.tools.build/gradle?repo=google
    const val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion" // https://mvnrepository.com/artifact/org.jetbrains.kotlin/kotlin-gradle-plugin
    const val ktLint = "com.pinterest:ktlint:0.36.0"
    const val sqlDelightGradlePlugin = "com.squareup.sqldelight:gradle-plugin:1.2.2" // https://mvnrepository.com/artifact/com.squareup.sqldelight/gradle-plugin
    const val googleServiceGradlePlugin = "com.google.gms:google-services:4.3.3" // https://mvnrepository.com/artifact/com.google.gms/google-services?repo=google
    const val firebaseCrashlyticsGradlePlugin = "com.google.firebase:firebase-crashlytics-gradle:2.1.0"
    const val jacocoGradlePlugin = "org.jacoco:org.jacoco.core:0.7.9"

    const val androidApplication = "com.android.application"
    const val androidLibrary = "com.android.library"
    const val firebaseCrashlytics = "com.google.firebase.crashlytics"
    const val googleServices = "com.google.gms.google-services"
    const val kotlin = "kotlin"
    const val kotlinAndroid = "kotlin-android"
    const val kotlinAndroidExtensions = "kotlin-android-extensions"
    const val kotlinKapt = "kotlin-kapt"
    const val sqlDelight = "com.squareup.sqldelight"
}

object Libraries {

    object Android {
        const val material = "com.google.android.material:material:1.2.0-alpha02" // https://mvnrepository.com/artifact/com.google.android.material/material
        const val flexBox = "com.google.android:flexbox:2.0.1" // https://mvnrepository.com/artifact/com.google.android/flexbox?repo=bt-google-flexbox-layout
        const val googlePlayBilling = "com.android.billingclient:billing:2.2.0" // https://mvnrepository.com/artifact/com.android.billingclient/billing
        const val googlePlayBillingKtx = "com.android.billingclient:billing-ktx:2.2.0" // https://mvnrepository.com/artifact/com.android.billingclient/billing-ktx
        const val playServicesLocation = "com.google.android.gms:play-services-location:17.0.0" // https://mvnrepository.com/artifact/com.google.android.gms/play-services-location?repo=google
    }

    object AndroidX {
        const val appCompat = "androidx.appcompat:appcompat:1.1.0" // https://mvnrepository.com/artifact/androidx.appcompat/appcompat
        const val constraintLayout = "androidx.constraintlayout:constraintlayout:1.1.3" // https://mvnrepository.com/artifact/androidx.constraintlayout/constraintlayout
        const val coreKtx = "androidx.core:core-ktx:1.2.0" // https://mvnrepository.com/artifact/androidx.core/core-ktx
        const val fragment = "androidx.fragment:fragment:1.2.3" // https://mvnrepository.com/artifact/androidx.fragment/fragment
        const val lifecycleExtensions = "androidx.lifecycle:lifecycle-extensions:2.2.0" // https://mvnrepository.com/artifact/androidx.lifecycle/lifecycle-extensions
        const val lifecycleLiveData = "androidx.lifecycle:lifecycle-livedata-core:2.2.0" // https://mvnrepository.com/artifact/androidx.lifecycle/lifecycle-livedata-core
        const val lifecycleViewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0" // https://mvnrepository.com/artifact/androidx.lifecycle/lifecycle-viewmodel-ktx
        const val navigationFragmentKtx = "androidx.navigation:navigation-fragment-ktx:2.2.1" // https://mvnrepository.com/artifact/androidx.navigation/navigation-fragment-ktx
        const val navigationUiKtx = "androidx.navigation:navigation-ui-ktx:2.2.1" // https://mvnrepository.com/artifact/androidx.navigation/navigation-ui-ktx
        const val preferenceKtx = "androidx.preference:preference-ktx:1.1.0" // https://mvnrepository.com/artifact/androidx.preference/preference-ktx
        const val recyclerView = "androidx.recyclerview:recyclerview:1.1.0" // https://mvnrepository.com/artifact/androidx.recyclerview/recyclerview
        const val swipeRefreshLayout = "androidx.swiperefreshlayout:swiperefreshlayout:1.0.0" // https://mvnrepository.com/artifact/androidx.swiperefreshlayout/swiperefreshlayout
    }

    object PlayServices {
        const val location = "com.google.android.gms:play-services-location:17.0.0"
    }

    object Apache {
        const val luceneAnalyzersCommon = "org.apache.lucene:lucene-analyzers-common:4.8.1"
        const val luceneCore = "org.apache.lucene:lucene-core:4.8.1"
        const val luceneQueryParser = "org.apache.lucene:lucene-queryparser:4.8.1"
    }

    object Dagger {
        const val dagger = "com.google.dagger:dagger:2.27" // https://mvnrepository.com/artifact/com.google.dagger/dagger
        const val daggerAndroid = "com.google.dagger:dagger-android:2.27" // https://mvnrepository.com/artifact/com.google.dagger/dagger-android
        const val daggerAndroidProcessor =  "com.google.dagger:dagger-android-processor:2.27" // https://mvnrepository.com/artifact/com.google.dagger/dagger-android-processor
        const val daggerAndroidSupport = "com.google.dagger:dagger-android-support:2.27" // https://mvnrepository.com/artifact/com.google.dagger/dagger-android-support
        const val daggerCompiler =  "com.google.dagger:dagger-compiler:2.27" // https://mvnrepository.com/artifact/com.google.dagger/dagger-compiler
    }

    object Firebase {
        const val analytics = "com.google.firebase:firebase-analytics:17.4.1" // https://mvnrepository.com/artifact/com.google.firebase/firebase-analytics
        const val crashlytics = "com.google.firebase:firebase-crashlytics:17.0.0" // https://mvnrepository.com/artifact/com.google.firebase/firebase-crashlytics
    }

    object Google {
        const val gson = "com.google.code.gson:gson:2.8.6"
    }

    object Groupie {
        const val groupie = "com.xwray:groupie:2.8.0" // https://mvnrepository.com/artifact/com.xwray/groupie?repo=jcenter
        const val groupieKtx = "com.xwray:groupie-kotlin-android-extensions:2.8.0" // https://mvnrepository.com/artifact/com.xwray/groupie-kotlin-android-extensions?repo=jcenter
    }

    object Kotlin {
        const val stdLib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion" // https://mvnrepository.com/artifact/org.jetbrains.kotlin/kotlin-stdlib-jdk8
    }

    object LeakCanary {
        const val leakCanary = "com.squareup.leakcanary:leakcanary-android:2.2" // https://mvnrepository.com/artifact/com.squareup.leakcanary/leakcanary-android
    }

    object Location {
        const val reactiveLocation = "pl.charmas.android:android-reactive-location2:2.1@aar" // https://mvnrepository.com/artifact/pl.charmas.android/android-reactive-location2
    }

    object OkHttp {
        const val okhttp = "com.squareup.okhttp3:okhttp:3.12.1" // https://mvnrepository.com/artifact/com.squareup.okhttp3/okhttp
        const val loggingInterceptor = "com.squareup.okhttp3:logging-interceptor:3.12.1" //
    }

    object Roxie {
        const val roxie = "com.ww:roxie:0.4.0" // https://mvnrepository.com/artifact/com.ww/roxie
    }

    object Rtpi {
        const val rtpiApi = "io.rtpi:rtpi-api:1.1.0"
        const val rtpiClient = "io.rtpi:rtpi-client-standalone:1.1.0"
        const val rtpiUtil = "io.rtpi:rtpi-util:1.1.0"
        const val rtpiStaticData = "io.rtpi:rtpi-static-data:1.1.0"
    }

    object Rx {
        const val rxAndroid = "io.reactivex.rxjava2:rxandroid:2.1.1" // https://mvnrepository.com/artifact/io.reactivex.rxjava2/rxandroid
        const val rxJava = "io.reactivex.rxjava2:rxjava:2.2.19" // https://mvnrepository.com/artifact/io.reactivex.rxjava2/rxjava
        const val rxKotlin = "io.reactivex.rxjava2:rxkotlin:2.4.0" // https://mvnrepository.com/artifact/io.reactivex.rxjava2/rxkotlin
    }

    object SqlDelight {
        const val androidDriver = "com.squareup.sqldelight:android-driver:1.2.2" // https://mvnrepository.com/artifact/com.squareup.sqldelight/android-driver
        const val rxJavaExtensions = "com.squareup.sqldelight:rxjava2-extensions:1.2.0" // https://mvnrepository.com/artifact/com.squareup.sqldelight/rxjava2-extensions
    }

    object Sourceforge {
        const val javaMl = "net.sourceforge:javaml:0.1.7" // https://mvnrepository.com/artifact/net.sourceforge/javaml?repo=jena-bio-libs-release-oss
    }

    object Store {
        const val store3 = "com.nytimes.android:store3:3.1.1" // https://mvnrepository.com/artifact/com.nytimes.android/store3
        const val store4 = "com.dropbox.mobile.store:store4:4.0.0-alpha03" // https://mvnrepository.com/artifact/com.dropbox.mobile.store/store4
    }

    object Timber {
        const val timber = "com.jakewharton.timber:timber:4.7.1" // https://mvnrepository.com/artifact/com.jakewharton.timber/timber
    }

    object Twitter  {
        const val tweetUi = "com.twitter.sdk.android:tweet-ui:3.3.0" // https://mvnrepository.com/artifact/com.twitter.sdk.android/tweet-ui
    }
}

object TestLibraries {

    object Junit {
        const val junit = "junit:junit:4.12" // https://mvnrepository.com/artifact/junit/junit
        const val params = "pl.pragmatists:JUnitParams:1.1.1"
    }

    object Mockk {
        const val mockk = "io.mockk:mockk:1.9.3" // https://mvnrepository.com/artifact/io.mockk/mockk
    }

    object Truth {
        const val truth = "com.google.truth:truth:1.0.1" // https://mvnrepository.com/artifact/com.google.truth/truth
    }

    object SqlDelight {
        const val sqliteDriver = "com.squareup.sqldelight:sqlite-driver:1.2.0" // https://mvnrepository.com/artifact/com.squareup.sqldelight/sqlite-driver
    }
}
