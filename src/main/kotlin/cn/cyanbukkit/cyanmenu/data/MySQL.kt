package cn.cyanbukkit.cyanmenu.data

import cn.cyanbukkit.cyanmenu.CyanMenu
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender


lateinit var mysql : HikariDataSource

fun linkMySQL() {
    val sqlConfig = HikariConfig().apply {
        driverClassName = CyanMenu.mysqlConfig.getString("driver")
        jdbcUrl =  CyanMenu.mysqlConfig.getString("jdbcUrl")
        username =  CyanMenu.mysqlConfig.getString("username")
        password = CyanMenu.mysqlConfig.getString("password")
        maximumPoolSize = 16
        minimumIdle = 5
        idleTimeout = 60000
        connectionTimeout = 30000
        maxLifetime = 1800000
        // 创表 名字叫 cyanmenu 然后 一个数字 一个 非常长的字符串 几乎无限长度  CREATE TABLE IF NOT EXISTS `` (  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
        connectionTestQuery = """
            CREATE TABLE IF NOT EXISTS `cyanmenu` (
                `id` INT NOT NULL AUTO_INCREMENT,
                `name` VARCHAR(255) NOT NULL,
                `data` TEXT NOT NULL,
                PRIMARY KEY (`id`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
        """.trimIndent()
    }
    mysql = HikariDataSource(sqlConfig)
    Bukkit.getConsoleSender().sendMessage("§a[§bCyanMenu§a] §aMySQL连接成功")
}


fun CommandSender.put(menu: String) {
    val data = CyanMenu.menuFolder.resolve(menu).readText().originToBase64()
    if (::mysql.isInitialized.not()) {
        sendMessage("§c[§bCyanMenu§c] §cMySQL未连接无法上传至数据库")
        return
    }
    mysql.connection.use {
        // 检查数据库中是否已存在该name的记录
        val existsStatement = it.prepareStatement("SELECT COUNT(*) FROM `cyanmenu` WHERE `name` = ?")
        existsStatement.setString(1, menu)
        val existsResult = existsStatement.executeQuery()
        if (existsResult.next()) {
            if (existsResult.getInt(1) > 0) {
                // 如果存在，更新数据
                val updateStatement = it.prepareStatement("UPDATE `cyanmenu` SET `data` = ? WHERE `name` = ?")
                updateStatement.setString(1, data)
                updateStatement.setString(2, menu)
                updateStatement.executeUpdate()
                sendMessage("§a[§bCyanMenu§a] §a菜单 '$menu' 已更新")
            } else {
                // 如果不存在，插入新记录
                val insertStatement = it.prepareStatement("INSERT INTO `cyanmenu` (`name`, `data`) VALUES (?, ?)")
                insertStatement.setString(1, menu)
                insertStatement.setString(2, data)
                insertStatement.executeUpdate()
                sendMessage("§a[§bCyanMenu§a] §a菜单 '$menu' 已添加")
            }
        } else {
            sendMessage("§c[§bCyanMenu§c] §c数据库查询失败")
        }
    }
}


fun CommandSender.getMySQLMenuList(): List<String> {
    val list = mutableListOf<String>()
    if (::mysql.isInitialized.not()) {
        Bukkit.getConsoleSender().sendMessage("§c[§bCyanMenu§c] §cMySQL未连接无法获取数据")
        list.add("§cMySQL未连接")
        return list
    }
    mysql.connection.use {
        val statement = it.prepareStatement("SELECT `name` FROM `cyanmenu`")
        val result = statement.executeQuery()
        while (result.next()) {
            list.add(result.getString("name"))
        }
    }
    return list
}