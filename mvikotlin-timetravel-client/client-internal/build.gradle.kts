import org.jetbrains.compose.compose

buildTargets = setOf(BuildTarget.Jvm, BuildTarget.Android)

setupMultiplatform()

plugins.apply("org.jetbrains.compose")

kotlinCompat {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":rx"))
                implementation(project(":mvikotlin-timetravel-proto-internal"))
                implementation(project(":mvikotlin"))
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
