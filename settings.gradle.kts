pluginManagement { repositories { google(); mavenCentral(); gradlePluginPortal() } }
dependencyResolutionManagement { repositories { google(); mavenCentral() } }
dependencyResolutionManagement { repositories { maven("https://nexus-playground.pleos.ai/repository/maven-releases/") { content { includeGroupByRegex("ai[.]pleos.*") } } } }
include(":automotive")
rootProject.name = "MyCarPet"
include(":app")

