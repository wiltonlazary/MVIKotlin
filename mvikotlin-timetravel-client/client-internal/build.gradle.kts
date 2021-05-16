import org.jetbrains.compose.compose

buildTargets = setOf(BuildTarget.Jvm)

setupMultiplatform()

plugins.apply("org.jetbrains.compose")

kotlinCompat {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":mvikotlin-timetravel-proto-internal"))
                api(project(":mvikotlin"))
                implementation(project(":rx"))
                implementation(project(":mvikotlin-main"))
                implementation(project(":mvikotlin-extensions-reaktive"))
                implementation(Deps.Badoo.Reaktive.Reaktive)
                implementation(Deps.Badoo.Reaktive.ReaktiveAnnotations)
                implementation(Deps.RusshWolf.MultiplatformSettings)
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.materialIconsExtended)
            }
        }
    }
}
