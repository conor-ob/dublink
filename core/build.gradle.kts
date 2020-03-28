plugins {
    id(BuildPlugins.kotlin)
}

dependencies {
    implementation(Libraries.Kotlin.stdLib)
    implementation(Libraries.Rtpi.rtpiApi)
    implementation(Libraries.Rx.rxJava)

    testImplementation(TestLibraries.junit)
    testImplementation(TestLibraries.truth)
}


//apply plugin: 'kotlin'
//apply plugin: 'kotlin-kapt'
//
//dependencies {
//    api libraries.dagger
//    api libraries.kotlin
//    api libraries.rx_java
//    api libraries.rx_kotlin
//    api libraries.slf4j_api
//    api libraries.slf4j_timber
//    api "com.google.dagger:dagger:${versions.dagger}"
//
//    implementation libraries.rtpi_api
//
//    testApi testLibraries.junit
//    testApi testLibraries.truth
//}
