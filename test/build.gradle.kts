plugins {
    id(BuildPlugins.kotlin)
}

dependencies {
    implementation(project(":domain"))

    implementation(Libraries.Kotlin.stdLib)
    implementation(Libraries.Rtpi.rtpiApi)
    implementation(Libraries.Rtpi.rtpiUtil)
    implementation(Libraries.Rtpi.rtpiStaticData)
}

sourceSets.getByName("main") {
    java.srcDir("src/main/kotlin")
}
