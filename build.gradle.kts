import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    `java-library`
    id("java")
    kotlin("jvm") version "2.0.0"
    id("io.github.goooler.shadow") version "8.1.7"
}

group = "com.skillw.skillx"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven { url = uri("https://repo.tabooproject.org/repository/releases/") }
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
    maven("https://repo.hypera.dev/snapshots")
}

dependencies {
    testImplementation(kotlin("test"))

    // Add shrinkwrap resolver
    implementation("org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-depchain:3.1.4")
    implementation("org.yaml:snakeyaml:2.2")
    implementation("com.typesafe:config:1.4.3")
    implementation("com.electronwill.night-config:core:3.6.7")
    implementation("com.electronwill.night-config:toml:3.6.7")
    implementation("com.electronwill.night-config:json:3.6.7")
    implementation("com.electronwill.night-config:hocon:3.6.7")
    implementation("com.electronwill.night-config:core-conversion:6.0.0")

    implementation("org.tabooproject.reflex:analyser:1.0.23")
    implementation("org.tabooproject.reflex:fast-instance-getter:1.0.23")
    implementation("org.tabooproject.reflex:reflex:1.0.23") // 需要 analyser 模块
    implementation("com.google.code.gson:gson:2.11.0")
    implementation("com.google.guava:guava:21.0")

    implementation("com.github.Minestom:DependencyGetter:v1.0.1")
    //terminal
    implementation("org.jline:jline-reader:3.25.0")
    implementation("org.jline:jline-terminal:3.25.0")
    implementation("org.jline:jline-terminal-jna:3.25.0")
    implementation("org.tinylog:tinylog-api:2.7.0")
    implementation("org.tinylog:tinylog-impl:2.7.0")
    implementation("org.fusesource.jansi:jansi:2.4.1")

    implementation("ch.qos.logback:logback-classic:1.5.12")
    implementation("net.minestom:minestom-snapshots:d955f51899")

}

tasks.compileJava {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-Xpkginfo:always")
    options.compilerArgs.add("-Xlint:unchecked")
    java.sourceCompatibility = JavaVersion.VERSION_21
    java.targetCompatibility = JavaVersion.VERSION_21
}

tasks.test {
    useJUnitPlatform()
}

sourceSets {
    main {
        resources {
            srcDirs("src/main/resources")
        }
    }
    test {
        resources {
            srcDirs("src/test/resources")
        }
    }
}
tasks.withType<Jar> {
    manifest {
        // Change this to your main class
        attributes["Main-Class"] = "com.skillw.skillx.Start"
    }

}

tasks.withType<ShadowJar> {
    relocate("kotlin.", "com.skillw.skillx.libs.kotlin.")
    relocate("com.google.", "com.skillw.skillx.libs.google.")
    relocate("org.tabooproject.reflex.", "com.skillw.skillx.libs.taboolib.")
    relocate("com.electronwill.nightconfig.", "com.skillw.skillx.libs.nightconfig.")
    relocate("com.typesafe.", "com.skillw.skillx.libs.typesafe.")
}

kotlin {
    jvmToolchain(21)
}
