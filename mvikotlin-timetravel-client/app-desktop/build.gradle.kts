import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform") // kotlin("jvm") doesn't work well in IDEA/AndroidStudio (https://github.com/JetBrains/compose-jb/issues/22)
    id("org.jetbrains.compose")
}

kotlin {
    jvm {
        withJava()
    }

    sourceSets {
        jvmMain {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(compose.materialIconsExtended)
                implementation(Deps.Badoo.Reaktive.Reaktive)
                implementation(Deps.Badoo.Reaktive.CoroutinesInterop)
                implementation(project(":mvikotlin"))
                implementation(project(":mvikotlin-timetravel-client:client-internal"))
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "com.arkivanov.mvikotlin.timetravel.client.desktop.MainKt"
        jvmArgs("-Dsun.java2d.uiScale=2.0") // Workaround due to https://github.com/JetBrains/compose-jb/issues/188

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "mvikotlin-time-travel-client"
            version = property("mvikotlin.version") as String

            windows {
                menuGroup = "MVIKotlin"
                upgradeUuid = "B0B34196-90BE-4398-99BE-8E650EBECC78"
            }
        }
    }
}
