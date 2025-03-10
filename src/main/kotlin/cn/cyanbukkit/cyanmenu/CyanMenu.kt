package cn.cyanbukkit.cyanmenu

import cn.cyanbukkit.cyanmenu.command.CreateListener
import cn.cyanbukkit.cyanmenu.command.EditorListener
import cn.cyanbukkit.cyanmenu.command.OpenListener
import cn.cyanbukkit.cyanmenu.cyanlib.launcher.CyanPluginLauncher.cyanPlugin
import cn.cyanbukkit.cyanmenu.data.linkMySQL
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

object CyanMenu  {


    fun String.isHttpUrl(): Boolean {
        //查看String是否为http链接
        return this.startsWith("https://") || this.startsWith("http://")
    }

    fun String.isMySQLMenu(): Boolean {
        //查看String是否为MySQL链接
        return this.startsWith("mysql://")
    }


    lateinit var menuFolder: File
    lateinit var menuConfigFile: File
    lateinit var menuConfig: YamlConfiguration
    lateinit var command : File
    lateinit var commandGroup : YamlConfiguration
    lateinit var mysqlFile : File
    lateinit var mysqlConfig : YamlConfiguration


    fun onEnable() {
        menuFolder = cyanPlugin.dataFolder.resolve("menu")
        menuConfigFile = cyanPlugin.dataFolder.resolve("menu.yml")
        command = cyanPlugin.dataFolder.resolve("commandGroup.yml")
        mysqlFile = cyanPlugin.dataFolder.resolve("mysql.yml")
        if (!menuFolder.exists()) {
            menuFolder.mkdirs()
        }
        if (!menuConfigFile.exists()) {
            menuConfigFile.createNewFile()
            menuConfigFile.writeText("""
                # 本地导入格式
                # menu: "xxx.yml"
                # 网络导入格式
                # menu: "https://raw.githubusercontent.com/CyanBukkit/CyanMenu/master/src/main/resources/menu/1.yml"
            """.trimIndent()
            )
        }
        if (!command.exists()) {
            command.createNewFile()
            command.writeText("""
                group:  #这个意思是分组指令
                    xxx:
                    - "say xx"
                random:  # 这个是随机指令设置
                   day:  # 名字
                    probability: 0.5  # 概率
                    list:  # 从这个组中随机值
                    - "give %player_name% minecraft:stone 1"
                    - "give %player_name% minecraft:stone 2"
            """.trimIndent())
        }
        if (!mysqlFile.exists()) {
            mysqlFile.createNewFile()
            mysqlFile.writeText("""
                enable: false
                jdbcUrl: "jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=GMT%2B8"
                # com.mysql.cj.jdbc.Driver 8.0  com.mysql.jdbc.Driver 5.0
                driver: ""
                username: "root"
                password: "root"
            """.trimIndent())
        }
        menuConfig = YamlConfiguration.loadConfiguration(menuConfigFile)
        commandGroup = YamlConfiguration.loadConfiguration(command)
        mysqlConfig = YamlConfiguration.loadConfiguration(mysqlFile)
        // 注册监听
        cyanPlugin.server.pluginManager.registerEvents(CreateListener, cyanPlugin)
        cyanPlugin.server.pluginManager.registerEvents(OpenListener, cyanPlugin)
        cyanPlugin.server.pluginManager.registerEvents(EditorListener, cyanPlugin)
        // qwq
        if (mysqlConfig.getBoolean("enable")) {
             linkMySQL()
        }
    }

}