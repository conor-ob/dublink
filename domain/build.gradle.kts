plugins {
    id(BuildPlugins.kotlin)
//    id(BuildPlugins.kotlinKapt)
}

dependencies {
    implementation(project(":core"))

    implementation(Libraries.Dagger.dagger)
    implementation(Libraries.Kotlin.stdLib)
    implementation(Libraries.Rtpi.rtpiApi)
    implementation(Libraries.Rx.rxJava)

//    kapt(Libraries.Dagger.daggerCompiler)

    testImplementation(TestLibraries.Junit.junit)
    testImplementation(TestLibraries.Truth.truth)
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
//
//    kapt libraries.dagger_compiler
//
//    implementation libraries.rtpi_api
//
//    testImplementation testLibraries.junit
//    testImplementation testLibraries.truth
//}
