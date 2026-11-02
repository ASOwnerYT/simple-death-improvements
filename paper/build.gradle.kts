plugins {
    java
}

// Shares name, version and description with the mod builds
val modProperties: Map<String, String> = rootDir.resolve("../stonecutter.properties.toml").readLines()
    .takeWhile { !it.startsWith("[") }
    .mapNotNull { Regex("""^(mod\.\w+)\s*=\s*"(.*)"\s*$""").find(it) }
    .associate { it.groupValues[1] to it.groupValues[2] }

group = modProperties.getValue("mod.group")
version = modProperties.getValue("mod.version")
val javaVersion = (property("java.version") as String).toInt()

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") { name = "PaperMC" }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:${property("paper.api")}")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(javaVersion)
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release = javaVersion
}

tasks.processResources {
    val props = mapOf(
        "version" to version.toString(),
        "description" to modProperties.getValue("mod.description").replace("'", "''"),
    )
    inputs.properties(props)
    filesMatching("plugin.yml") { expand(props) }
}

tasks.jar {
    val modId = modProperties.getValue("mod.id")
    archiveBaseName = "$modId-paper"
    from(rootDir.resolve("../LICENSE")) { rename { "LICENSE_$modId" } }
}
