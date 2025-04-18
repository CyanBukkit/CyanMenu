package cn.cyanbukkit.cyanmenu.core

import cn.cyanbukkit.cyanmenu.cyanlib.launcher.CyanPluginLauncher
import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.plugin.Plugin
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.IOException
import java.util.*

object Handle {

    fun String.analysisCommand(p: Player) {
        val newThis = this.placeHolderAPI(p)
        when {
            // OP执行权限
            newThis.startsWith("op:") -> {
                val old = p.isOp
                p.isOp = true
                p.performCommand(newThis.substring(4).trim())
                p.isOp = old
            }
            // 控制台执行
            newThis.startsWith("console:") -> {
                CyanPluginLauncher.cyanPlugin.server.dispatchCommand(
                    CyanPluginLauncher.cyanPlugin.server.consoleSender,
                    newThis.substring(9).trim()
                )
            }
            // 玩家自己
            newThis.startsWith("player:") -> {
                p.performCommand(newThis.substring(7).trim())
            }

            // TellMsg
            newThis.startsWith("tellmsg:") -> {
                p.sendMessage(newThis.substring(8).trim())
            }

            newThis.startsWith("sound:") -> {
                val sound = newThis.substring(6).trim().split(" ")
                p.playSound(p.location, org.bukkit.Sound.valueOf(sound[0]), sound[1].toFloat(), sound[2].toFloat())
            }


            newThis.startsWith("group:") -> {
                val group = newThis.substring(6).trim()

            }
            //
            newThis.startsWith("bungee:")  -> {
                p.connectTo(newThis.substring(7).trim(), CyanPluginLauncher.cyanPlugin)
            }


            newThis.startsWith("cyanbukkit:") -> {
                p.sendMessage("§b啦啦啦啦啦啦啦啦从前有个小玄易他创建了一个桶.........")
            }


            newThis.startsWith("demand:") -> {
                val demand = newThis.substring(7).trim().split("?")
                if (demand.size == 2) {
                    val run = demand[1].split("∴")
                    if (demand[0].yesOrNo()) {
                        run[0].analysisCommand(p)
                    } else {
                        run[1].analysisCommand(p)
                    }
                }
            }

        }
    }

    /**
     *  变量自需求判定 收String 发boolean
     * @
     */
    private fun String.yesOrNo(): Boolean {
        return when {
            this.contains("==") -> {
                val split = this.split("==")
                split[0].trim() == split[1].trim()
            }

            this.contains("!=") -> {
                val split = this.split("!=")
                split[0].trim() != split[1].trim()
            }

            this.contains(">=") -> {
                val split = this.split(">=")
                split[0].trim().toDouble() >= split[1].trim().toDouble()
            }


            this.contains("<=") -> {
                val split = this.split("<=")
                split[0].trim().toDouble() <= split[1].trim().toDouble()
            }


            this.contains(">") -> {
                val split = this.split(">")
                split[0].trim().toDouble() > split[1].trim().toDouble()
            }

            this.contains("<") -> {
                val split = this.split("<")
                split[0].trim().toDouble() < split[1].trim().toDouble()
            }

            else -> this.toBoolean()
        }
    }

    fun String.placeHolderAPI(p: Player): String {
        // 找不到插件 PlaceholderAPI
        val newText = this.replace("&", "§")
        val placeholderApi = Bukkit.getServer().pluginManager.getPlugin("PlaceholderAPI")
        return if (placeholderApi != null && placeholderApi.isEnabled) {
            PlaceholderAPI.setPlaceholders(p,newText )
        } else newText
    }


    fun MutableList<String>.placeHolderAPI(p: Player): MutableList<String> {
        val placeholderApi = Bukkit.getServer().pluginManager.getPlugin("PlaceholderAPI")
        return if (placeholderApi != null && placeholderApi.isEnabled) {
            PlaceholderAPI.setPlaceholders(p, this.apply {
                this.forEachIndexed { index, s ->
                    this[index] = s.replace("&", "§")
                }
            })
        } else {
            this.apply {
                this.forEachIndexed { index, s ->
                    this[index] = s.replace("&", "§")
                }
            }
        }
    }

    // 将物品转换为字符串
    fun ItemStack.toStr(): String { 
        val aConfig = YamlConfiguration()
        aConfig.set("item", this.apply {
            val mate = this.itemMeta
            if (!this.hasItemMeta()) {
                this.itemMeta = mate
            }
            if (!mate.hasLore()) {
                val lore = mutableListOf<String>()
                lore.add("自己去配置文件改或者删但是编辑时是空的你还会见到这句话")
                mate.lore = lore
            }
            if (!mate.hasDisplayName()) {
                mate.displayName = "自己去配置文件改或者删但是编辑时是空的你还会见到这句话"
            }
            this.itemMeta = mate
        })
        return aConfig.saveToString()
    }

    fun String.toItemStack(): ItemStack {
        val aConfig = YamlConfiguration()
        aConfig.loadFromString(this)
        
        try {
            // 先尝试官方反序列化方法
            return aConfig.getItemStack("item")!!
        } catch (e: Exception) {
            // 反序列化失败时手动处理
            println("找不到这东西，aConfig内容如下：")
            aConfig.getKeys(false).forEach { key ->
                println("$key: ${aConfig.get(key)}")
            }
            if (aConfig.contains("item")) {
                val itemMap = aConfig.getConfigurationSection("item")?.getValues(false)
                if (itemMap != null) {
                    val type = Material.matchMaterial(itemMap["type"]?.toString() ?: "STONE")
                    val amount = (itemMap["amount"] as? Number)?.toInt() ?: 1
                    return ItemStack(type, amount)
                }
            }
            // 如果所有方法都失败，返回默认物品
            return ItemStack(Material.STONE)
        }
    }


    fun ItemStack.getCustomSkull(base64: String): ItemStack {
        val skull = itemMeta as SkullMeta
        val profile = GameProfile(UUID.randomUUID(), null)
        profile.properties.put("textures", Property("textures", base64))
        try {
            val profileField = skull.javaClass.getDeclaredField("profile")
            profileField.isAccessible = true
            profileField[skull] = profile
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        }
        itemMeta = skull
        return this
    }


    fun String.toItemStack(p: Player): ItemStack {
        val aConfig = YamlConfiguration()
        aConfig.loadFromString(this)
        val item = this.toItemStack().apply {
            val met = this.itemMeta
            met.apply {
                if (this!!.displayName != null) {
                    this.displayName = this.displayName.placeHolderAPI(p)
                }
                if (this.lore != null) {
                    this.lore = this.lore?.placeHolderAPI(p)
                }
            }
            this.itemMeta = met
            // 如果是头颅 获取player的皮肤
            if (aConfig.contains("isplayer")) {
                // 有两个类型一个是PLAYER 是玩家 BASE64
                val skullMeta = this.itemMeta as SkullMeta
                skullMeta.owner = p.name
                this.itemMeta = skullMeta
            }
        }
        return item
    }

    fun randomString(length: Int): String {
        val str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val sb = StringBuffer()
        for (i in 0 until length) {
            sb.append(str.random())
        }
        return sb.toString()
    }


    private fun Player.connectTo(server: String, plugin: Plugin) {
        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord")
        // 这个怎么办
        val buf = ByteArrayOutputStream()
        val out = DataOutputStream(buf)
        try {
            out.writeUTF("Connect")
            out.writeUTF(server)
            sendPluginMessage(plugin, "BungeeCord", buf.toByteArray())
        } catch (e1: IOException) {
            e1.printStackTrace()
        }
    }

}