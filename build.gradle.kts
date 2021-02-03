plugins {
    `kotlin-dsl`
    id("io.gitlab.arturbosch.detekt").version("1.14.2")
}

allprojects {
    repositories {
        google()
        jcenter()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }

    apply(plugin = "io.gitlab.arturbosch.detekt")

    detekt {
        toolVersion = "1.14.2"
        config = files("$rootDir/detekt.yml")
        input = files(file("src").listFiles()?.filter(File::isDirectory))
    }
}
