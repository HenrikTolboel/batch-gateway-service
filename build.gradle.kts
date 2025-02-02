plugins {
    java
    id("org.springframework.boot") version "3.4.2"
    id("io.spring.dependency-management") version "1.1.7"
    id("jacoco")
    alias(libs.plugins.sonarqube)
    pmd

    alias(libs.plugins.gitproperties)
}


extra["kafka.version"] = "3.9.0"

// This block of dependencies are standard / default

// See https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html#mockito-instrumentation
val mockitoAgent = configurations.create("mockitoAgent")
dependencies {
//    #//            // This is a ?temporary? error in the springboot settings. This artifact is missing for
//    #//            // Apple M1. The version specified should match the version for the other architectures,
//    #//            // see "External Libraries" in intellij Idea "Project" panel.
//    #//            // When upgrading springboot version please check whether this is still needed.
//    #//            version("tempnetty", "io.netty:netty-resolver-dns-native-macos:4.1.117.Final:osx-aarch_64")
//    implementation("io.netty:netty-resolver-dns-native-macos:4.1.117.Final:osx-aarch_64")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-log4j2")
    implementation("org.apache.logging.log4j:log4j-spring-cloud-config-client")
    implementation("org.apache.logging.log4j:log4j-api")
    implementation("org.apache.logging.log4j:log4j-core")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    implementation("org.springframework.boot:spring-boot-starter-security")

    implementation("org.springframework.cloud:spring-cloud-starter-config")

    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")

    implementation(libs.bundles.openapi)

    implementation("io.micrometer:micrometer-tracing")
    implementation("io.micrometer:micrometer-tracing-bridge-otel")
    implementation("io.opentelemetry:opentelemetry-exporter-otlp")
    implementation("io.micrometer:micrometer-registry-prometheus")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
//    testImplementation(libs.mockwebserver)

    testImplementation("io.micrometer:micrometer-tracing-test")
    testImplementation("io.micrometer:micrometer-observation-test")
    testImplementation (libs.archunit)
    testImplementation(libs.mockito)
    mockitoAgent(libs.mockito) { isTransitive = false }
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// This block of dependencies are special for the application / service
dependencies {

    implementation("org.springframework.boot:spring-boot-starter-jdbc")

    implementation(libs.minio)
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

    implementation("org.springframework.retry:spring-retry")

    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    runtimeOnly("org.mariadb.jdbc:mariadb-java-client")
    runtimeOnly("org.flywaydb:flyway-core")
    runtimeOnly("org.flywaydb:flyway-mysql")

    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:mariadb")

//    implementation("org.springframework.boot:spring-boot-starter-cache")

    implementation("org.springframework.kafka:spring-kafka")
    testImplementation("org.springframework.kafka:spring-kafka-test")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2024.0.0")
        mavenBom(libs.micrometertracingBom.get().toString())
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenLocal()
//    maven {
//        isAllowInsecureProtocol = true
//        val snapshotsRepoUrl = uri(property("MAVEN_PUBLIC_URL").toString())
//        url = snapshotsRepoUrl
//        name = "nexus"
////		credentials(PasswordCredentials::class)
//    }
    mavenCentral()
    maven {
        isAllowInsecureProtocol = false
        val snapshotsRepoUrl = uri("https://maven.pkg.github.com/henriktolboel/repository")
        url = snapshotsRepoUrl
        name = "GitHubPackages"
		credentials(PasswordCredentials::class)
    }

}

// <editor-fold defaultstate="collapsed" desc="calculate variables, tags, and names from environment">
group = "org.example"
version = "latest"

val buildNumber: String? by project
extra.set("buildNumber", buildNumber ?: "unknownBuildNumber")

val branchname: String? by project
extra.set("branchname", branchname ?: "unknownBranchname")

val buildVcsNumber: String? by project
extra.set("buildVcsNumber", buildVcsNumber ?: "unknownVcsNumber")

val isCIBuild: String? by project
if (!hasProperty("isCIBuild")) {
    extra.set("isCIBuild", 0)
}

version = imageTag()
val dockerImageName = dockerImageName(project.name.lowercase(), version.toString())
extra.set("dockerImageName", dockerImageName)

// <editor-fold defaultstate="collapsed" desc="calculate if a different imageTag is needed">
fun imageTag() : String {
    var tag: String
    if ("0".equals(property("isCIBuild"))) {
        return "$version"
    }
    if (!null.equals(branchname)) {
        tag = branchname.toString()
    } else {
        tag = "master"
    }
    if (!"unknownBuildNumber".equals(buildNumber)) {
        tag = tag.plus("-" + buildNumber)
    }

    return tag.replace("[^a-zA-Z0-9]".toRegex(), "-")
}
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="section: print debug of parameters">
fun printDebug(propertyName: String, str: String) {
    if (hasProperty(propertyName)
        && "1".equals(property(propertyName))
    ) {
        println(str)
    }
}
printDebug("buildGradleDebug", "group = $group")
printDebug("buildGradleDebug", "version = $version")
extra.properties.forEach {
    if (!it.key.startsWith("library.")
        && !it.key.startsWith("version.")
        && !it.key.startsWith("platform.")
        && !it.key.startsWith("gradleKotlinDsl.")
        && !it.key.contains("Password")
        && !it.key.contains("TOKEN")
    ) {
        printDebug("buildGradleDebug", it.key + " = " + it.value)
    }
}
// </editor-fold>
// </editor-fold>

configurations {
    all {
        // turns off logback when switching to log4j2
        exclude("org.springframework.boot", "spring-boot-starter-logging")
        exclude("ch.qos.logback", "logback-classic")
        exclude("org.springframework.cloud", "spring-cloud-bus")
    }
}

pmd {
    isConsoleOutput = false
    toolVersion = libs.versions.pmdplugin.get()
    rulesMinimumPriority = 5
    isIgnoreFailures = true // TODO: temporary dont fail build on PMD
    ruleSets = listOf("$rootDir/gradle/buildenv/pmd-ruleset.xml")
}

tasks.test {
    useJUnitPlatform()

    jvmArgs("-javaagent:${mockitoAgent.asPath}")
}

if ("1".equals(property("isCIBuild"))) {
    tasks.findByName("build")?.finalizedBy(tasks.bootJar, tasks.sonar, tasks.bootBuildImage)
}

// <editor-fold defaultstate="collapsed" desc="Automatic properties expansion">
// https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto-properties-and-configuration
tasks.getByName<ProcessResources>("processResources") {
    filesMatching("application.properties") {
        expand(project.properties)
    }
    outputs.upToDateWhen { false }
}

tasks.getByName<ProcessResources>("processTestResources") {
    filesMatching("application.properties") {
        expand(project.properties)
    }
    outputs.upToDateWhen { false }
}
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="setup jacoco">
tasks.withType<Test> {
    configure<JacocoTaskExtension> {
        isEnabled = true
    }
}
configure<JacocoPluginExtension> {
    toolVersion = libs.versions.jacocoplugin.get()
}

tasks.withType<JacocoReport> {
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

tasks.jacocoTestReport {
    dependsOn(tasks.findByName("check"))
}
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="setup sonar / sonarqube">
sonarqube {
    properties {
        property("sonar.projectName", project.name.lowercase())
        property("sonar.projectKey", project.name.lowercase())
        // properties from gradle.properties in GRADLE_USER_HOME
        property("sonar.token", property("SONAR_TOKEN").toString())
        property("sonar.host.url", property("SONAR_HOST_URL").toString())
        property("sonar.sourceEncoding", "UTF-8")

        property("sonar.jacoco.xmlReportPaths", "${project.layout.buildDirectory}/test-results/**/TEST-*.xml")
        property("sonar.dynamicAnalysis", "reuseReports")
//	    property("sonar.qualitygate.wait", "1".equals(property("isCIBuild")))
    }
}
// sonarqube needs the jacoco xml report for the root project, hence
tasks.findByName("sonar")?.dependsOn(tasks.jacocoTestReport)
tasks.findByName("sonarqube")?.dependsOn(tasks.jacocoTestReport)
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Docker image naming and build">
fun dockerImageName(name: String, tag: String): Any {
    if ("0".equals(property("isCIBuild"))) {
        return "$name:" + tag
    }
    return property("REGISTRY_PUSH_URL").toString().plus("/$name:").plus(tag)
}

fun saveDockerImageName(dockerImageName: String) {
    mkdir(layout.buildDirectory)
    file(layout.buildDirectory.get().toString().plus("/dockerImageName.txt")).printWriter().use {
        it.println(dockerImageName)
    }
}

//afterEvaluate {
val saveDockerImageName = tasks.register("saveDockerImageName") {
    description= "Save name of build docker image in file"
    group = "build"
    doFirst {
        saveDockerImageName(dockerImageName.toString())
    }
}
tasks.findByName("bootBuildImage")?.finalizedBy(saveDockerImageName)

val removeDockerImageName = tasks.register("removeDockerImageName") {
    description= "Remove docker image name file"
    group = "build"
    doFirst {
        file(layout.buildDirectory.get().toString().plus("/dockerImageName.txt")).delete()
    }
}
tasks.findByName("build")?.dependsOn(removeDockerImageName)
//}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootBuildImage>("bootBuildImage") {
    imageName.set(dockerImageName.toString())
    // https://stackoverflow.com/questions/75805837/why-does-my-spring-boot-native-image-build-take-so-long-on-macos
    // https://github.com/buildpacks/lifecycle/issues/435#issuecomment-1721587499
    builder.set("dashaun/builder:base")    // :tiny   was the default here...
}
// </editor-fold>

springBoot {
    buildInfo {
        properties {
            additional.set(mapOf(
                Pair("BUILD_VCS_NUMBER", project.property("buildVcsNumber") as String),
                Pair("BUILD_NUMBER", project.property("buildNumber") as String),
                Pair("BUILD_VERSION", version),
                Pair("BUILD_IMAGE_NAME", dockerImageName.toString())
            ))
        }
    }
}

// <editor-fold defaultstate="collapsed" desc="git properties settings">
gitProperties {
    extProperty = "gitProps"
    keys = mutableListOf(
        "git.branch",
        "git.commit.id",
        "git.commit.id.abbrev",
        "git.commit.id.describe",
        "git.commit.message.full",
        "git.commit.message.short",
        "git.commit.time",
        "git.commit.user.email",
        "git.commit.user.name",
        "git.dirty",
        "git.remote.origin.url",
        "git.tags",
    )
}
tasks.generateGitProperties {
    outputs.upToDateWhen { false }
    doLast {
        val gitProps = project.extra.properties["gitProps"].toString()
        printDebug("buildGradleDebug", "gitProps = $gitProps")

        project.extra.set("gitCommitId", gitProps.substringAfter("git.commit.id=").substringBefore(","))
        project.extra.set("gitBranch", gitProps.substringAfter("git.branch=").substringBefore(","))
    }
}
// </editor-fold>
