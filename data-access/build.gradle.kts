plugins {
    id(BuildPlugins.kotlin)
}

dependencies {
    implementation(Libraries.Kotlin.stdLib)
    implementation(Libraries.Rtpi.rtpiApi)
    implementation(Libraries.Rx.rxJava)
}

sourceSets.getByName("main") {
    java.srcDir("src/main/kotlin")
}
sourceSets.getByName("test") {
    java.srcDir("src/test/kotlin")
}
