# FoundryVTT Module Gradle Plugin

## Usage

Set up the plugin in your plugins block and configure some base variables:

```kt
plugins {
    id("at.posselt.foundryvtt-module")
}

foundryvttModule {
    releaseModuleJson = layout.buildDirectory.file("module.json") // default if absent
    githubUser = "BernhardPosselt"
    githubRepo = "pf2e-kingmaker-tools"
}

// extend base task that already includes the changelog and module file
tasks.named<Zip>("foundryvttModulePackage") {
    val moduleId: String by extra
    from("dist") { into("$moduleId/dist") }
    from("docs") { into("$moduleId/docs") }
    from("img") { into("$moduleId/img") }
    from("packs") { into("$moduleId/packs") }
    from("styles") { into("$moduleId/styles") }
    from("templates") { into("$moduleId/templates") }
    from("LICENSE") { into("$moduleId/") }
    from("README.md") { into("$moduleId/") }
}
```

If you add a CHANGELOG.md file into the zip's module directory, it will be parsed in the [keep a changelog format](https://keepachangelog.com/en/1.1.0/) and added to the github release page

## Tasks

This adds the following Gradle Tasks to your project:

* **foundryvttModuleUpdateManifest**: modifies your module.json file with the new version and download links 
* **foundryvttModulePackage**: creates a **build/foundryvttModule/release.zip** file; includes your module.json file by default, but you'll likely want to add additional files by extending the task as noted above
* **foundryvttModuleUploadGithubRelease**: Pushes your module.json and build.gradle.kts file and adds a git tag based on the version in build.gradle.kts
* **foundryvttRelease**: Task that executes both **foundryvttModuleRelease** and **foundryvttModuleUploadGithubRelease**
* **foundryvttModuleUploadGithubRelease**: Uploads the package in build/foundryvttModule/release.zip to GitHub and publishes a new release on foundryvtt.com
* **foundryvttModuleCreateRelease**: Creates a new release over foundryvtt.com's REST API


## Schemas

FoundryVTT allows you to define Data Models blabla

To generate code, first provide a JSON schema for the type you wish to generate:

```json

```