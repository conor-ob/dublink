plugins {
    id(BuildPlugins.kotlin)
    id(BuildPlugins.kotlinKapt)
}

apply(from = "$rootDir/quality/coverage/kotlinJacoco.gradle")

dependencies {
    implementation(project(":domain"))

    implementation(Libraries.Dagger.dagger)
    implementation(Libraries.Kotlin.stdLib)
    implementation(Libraries.Rtpi.rtpiApi)
    implementation(Libraries.Rtpi.rtpiClient)
    implementation(Libraries.Rx.rxJava)
    implementation(Libraries.Sourceforge.javaMl)
    implementation(Libraries.Store.store3)

    kapt(Libraries.Dagger.daggerCompiler)

    testImplementation(project(":test"))
    testImplementation(TestLibraries.Junit.junit)
    testImplementation(TestLibraries.Mockk.mockk)
    testImplementation(TestLibraries.Truth.truth)
    testImplementation(Libraries.Rtpi.rtpiStaticData)
}

sourceSets.getByName("main") {
    java.srcDir("src/main/kotlin")
}
sourceSets.getByName("test") {
    java.srcDir("src/test/kotlin")
}
