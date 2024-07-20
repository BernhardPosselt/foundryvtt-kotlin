//plugins {
//    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
//}

rootProject.name = "kingmaker-tools-ng"

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("./libs.versions.toml"))
        }
    }
}
