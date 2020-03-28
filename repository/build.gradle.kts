plugins {
    id(BuildPlugins.kotlin)
    id(BuildPlugins.kotlinKapt)
}

dependencies {
    implementation(project(":core"))
    implementation(project(":data-access"))
    implementation(project(":domain"))

    implementation(Libraries.Dagger.dagger)
    implementation(Libraries.Kotlin.stdLib)
    implementation(Libraries.Rtpi.rtpiApi)
    implementation(Libraries.Rtpi.rtpiClient)
    implementation(Libraries.Rx.rxJava)
    implementation(Libraries.Store.store)

    kapt(Libraries.Dagger.daggerCompiler)
}

sourceSets.getByName("main") {
    java.srcDir("src/main/kotlin")
}
sourceSets.getByName("test") {
    java.srcDir("src/test/kotlin")
}
