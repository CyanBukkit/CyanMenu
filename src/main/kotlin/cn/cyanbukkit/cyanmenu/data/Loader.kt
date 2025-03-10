package cn.cyanbukkit.cyanmenu.data

import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import java.net.HttpURLConnection
import java.net.URI


//通过连接中的网页信息转换成
fun loadWebIndex(url: String): YamlConfiguration {
    val sb = StringBuilder()

    val http = URI.create(url).toURL().openConnection() as HttpURLConnection
    http.doInput = true
    http.doOutput = true
    val inputStream = http.inputStream
    val bytes = ByteArray(1024)
    var len = inputStream.read(bytes)
    while (len != -1) {
        sb.append(String(bytes, 0, len))
        len = inputStream.read(bytes)
    }

    val yaml = YamlConfiguration()
    yaml.loadFromString(sb.toString())
    return yaml
}




fun loadMySQLIndex(str: String): YamlConfiguration {
    val name = str.substring(8).trim()
    Bukkit.getConsoleSender().sendMessage("打开${name}")
    mysql.connection.use {
        val statement = it.prepareStatement("SELECT * FROM cyanmenu WHERE name = ?")
        statement.setString(1, name)
        val result = statement.executeQuery()
        if (result.next()) {
            val data = result.getString("data").base64ToOrigin()
            val yaml = YamlConfiguration()
            yaml.loadFromString(data)
            return yaml
        }
    }
    Bukkit.getConsoleSender().sendMessage("未识别到菜单")
    return YamlConfiguration()
}
