plugins {
    id(BuildPlugins.kotlin)
    id(BuildPlugins.kotlinKapt)
}

dependencies {
    implementation(project(":domain"))

    implementation(Libraries.Dagger.dagger)
    implementation(Libraries.Kotlin.stdLib)
    implementation(Libraries.Rtpi.rtpiApi)
    implementation(Libraries.Rtpi.rtpiClient)
    implementation(Libraries.Rx.rxJava)
    implementation(Libraries.Store.store3)
    implementation(Libraries.Store.store4)

    kapt(Libraries.Dagger.daggerCompiler)

    testImplementation(TestLibraries.Junit.junit)
    testImplementation(TestLibraries.Mockk.mockk)
    testImplementation(TestLibraries.Truth.truth)
}

sourceSets.getByName("main") {
    java.srcDir("src/main/kotlin")
}
sourceSets.getByName("test") {
    java.srcDir("src/test/kotlin")
}
