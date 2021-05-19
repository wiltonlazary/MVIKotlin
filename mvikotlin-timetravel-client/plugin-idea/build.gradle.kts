plugins {
    id("org.jetbrains.intellij").version("0.7.3")
    kotlin("jvm")
    id("org.jetbrains.compose")
}

group = "org.arkivanov.mvikotlin.plugin.idea.timetravel"
version = requireNotNull(property("mvikotlin.version"))

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":mvikotlin"))
    implementation(project(":mvikotlin-main"))
    implementation(project(":mvikotlin-timetravel-client:client-internal"))
    implementation(Deps.Badoo.Reaktive.Reaktive)
    implementation(Deps.Badoo.Reaktive.CoroutinesInterop)
    implementation(Deps.RusshWolf.MultiplatformSettings)
    implementation(compose.desktop.currentOs)
}

project.withGroovyBuilder {
    "patchPluginXml" {
        "sinceBuild"("193")
    }
}

intellij {
    version = "2019.3"
    updateSinceUntilBuild = false
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "11"
    }
}
