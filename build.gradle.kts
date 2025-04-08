version = "2.8"

plugins {
    kotlin("jvm") version "1.9.20"
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
}

repositories {
    maven("https://lanternmc-maven.pkg.coding.net/repository/cyanbukkit/public")
}


dependencies {
    implementation( kotlin("stdlib-jdk8"))
    compileOnly("org.spigotmc:spigot:1.8.8")
    compileOnly("me.clip:placeholderapi:2.10.9")
    compileOnly("org.apache.commons:commons-lang3:3.12.0")
    compileOnly(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    compileOnly("com.zaxxer:HikariCP:5.1.0")
}



kotlin {
    jvmToolchain(8)
}

bukkit {
//    apiVersion = "1.20"
    main = "cn.cyanbukkit.cyanmenu.cyanlib.launcher.CyanPluginLauncher"
    name = project.name
    description = "CyanNextMenu"
    website = "https://cyanbukkit.cn"
    authors = listOf("CyanBukkit")
    prefix = "§bCyanMenu§f"
    softDepend = listOf("PlaceholderAPI")
}



tasks {

    compileJava {
        options.encoding = "UTF-8"
    }

    jar {
        archiveFileName.set("${project.name}-${version}.jar")
    }

}