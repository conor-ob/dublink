plugins {
    id(BuildPlugins.kotlin)
//    id(BuildPlugins.kotlinKapt)
}

dependencies {
//    implementation(project(":core"))
//
//    implementation(Libraries.Dagger.dagger)
    implementation(Libraries.Kotlin.stdLib)
    implementation(Libraries.Rtpi.rtpiApi)
    implementation(Libraries.Rx.rxJava)
//
////    kapt(Libraries.Dagger.daggerCompiler)
//
//    testImplementation(TestLibraries.junit)
//    testImplementation(TestLibraries.truth)
}

//apply plugin: 'kotlin'
//apply plugin: 'kotlin-kapt'
//
//dependencies {
//    implementation project(':core')
//    implementation project(':domain')
//
//    implementation libraries.rtpi_api
//}

sourceSets.getByName("main") {
    java.srcDir("src/main/kotlin")
}
sourceSets.getByName("test") {
    java.srcDir("src/test/kotlin")
}
