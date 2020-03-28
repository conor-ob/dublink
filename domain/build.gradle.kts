plugins {
    id(BuildPlugins.kotlin)
}

dependencies {
    implementation(project(":core"))

    implementation(Libraries.Dagger.dagger)
    implementation(Libraries.Kotlin.stdLib)
    implementation(Libraries.Rtpi.rtpiApi)
    implementation(Libraries.Rx.rxJava)

    testImplementation(TestLibraries.Junit.junit)
    testImplementation(TestLibraries.Truth.truth)
}

sourceSets.getByName("main") {
    java.srcDir("src/main/kotlin")
}
sourceSets.getByName("test") {
    java.srcDir("src/test/kotlin")
}
