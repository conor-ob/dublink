plugins {
    id(BuildPlugins.kotlin)
}

dependencies {
    implementation(Libraries.Kotlin.stdLib)
    implementation(Libraries.Rtpi.rtpiApi)
    implementation(Libraries.Rx.rxJava)

    testImplementation(TestLibraries.Junit.junit)
    testImplementation(TestLibraries.Truth.truth)
}
