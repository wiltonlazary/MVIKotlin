plugins {
    `kotlin-dsl`
}

repositories {
    google()
    jcenter()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    implementation(Deps.Jetbrains.Kotlin.Plugin.Gradle)
    implementation(Deps.Jetbrains.Compose.Plugin.Gradle)
    implementation(Deps.Android.Tools.Build.Gradle)
    implementation(Deps.TouchLab.KotlinXcodeSync)
}

kotlin {
    // Add Deps to compilation, so it will become available in main project
    sourceSets.getByName("main").kotlin.srcDir("buildSrc/src/main/kotlin")
}
