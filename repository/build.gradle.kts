plugins {
    id(BuildPlugins.kotlin)
    id(BuildPlugins.kotlinKapt)
}

dependencies {
    implementation(project(":core"))
    implementation(project(":data-access"))
    implementation(project(":domain"))
//
    implementation(Libraries.Dagger.dagger)
    implementation(Libraries.Kotlin.stdLib)
    implementation(Libraries.Rtpi.rtpiApi)
    implementation(Libraries.Rtpi.rtpiClient)
    implementation(Libraries.Rx.rxJava)
    implementation(Libraries.Store.store)
//
    kapt(Libraries.Dagger.daggerCompiler)
//
//    testImplementation(TestLibraries.junit)
//    testImplementation(TestLibraries.truth)
}

sourceSets.getByName("main") {
    java.srcDir("src/main/kotlin")
}
sourceSets.getByName("test") {
    java.srcDir("src/test/kotlin")
}

//apply plugin: 'kotlin'
//apply plugin: 'kotlin-kapt'
//
//dependencies {
//    implementation project(':core')
//    implementation project(':data-access')
//    implementation project(':domain')
//
//    implementation "com.nytimes.android:store3:${versions.store3}"
//    implementation "ma.glasnost.orika:orika-core:${versions.orika_core}"
//
//    implementation libraries.rtpi_api
//    implementation libraries.rtpi_client
//
//    kapt libraries.dagger_compiler
//}