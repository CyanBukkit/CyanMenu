package cn.cyanbukkit.cyanmenu.command

import cn.cyanbukkit.cyanmenu.CyanMenu
import cn.cyanbukkit.cyanmenu.CyanMenu.isHttpUrl
import cn.cyanbukkit.cyanmenu.CyanMenu.isMySQLMenu
import cn.cyanbukkit.cyanmenu.core.Handle.analysisCommand
import cn.cyanbukkit.cyanmenu.core.Handle.placeHolderAPI
import cn.cyanbukkit.cyanmenu.core.Handle.toItemStack
import cn.cyanbukkit.cyanmenu.core.ItemData
import cn.cyanbukkit.cyanmenu.cyanlib.launcher.CyanPluginLauncher.cyanPlugin
import cn.cyanbukkit.cyanmenu.data.loadMySQLIndex
import cn.cyanbukkit.cyanmenu.data.loadWebIndex
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.scheduler.BukkitRunnable

object OpenListener : Listener {

    val slotData = mutableMapOf<Player, MutableMap<Int, ItemData>>()


    // 定义Player类的扩展函数openMenu，用于打开指定名称的菜单
    fun Player.openMenu(menuName: String) {
        // 打开菜单
        if (CyanMenu.menuConfig.contains(menuName)) {
            // 从配置中获取菜单字符串
            val str = CyanMenu.menuConfig.getString(menuName)!!
            // 如果字符串是HTTP链接
            val config = if (str.isHttpUrl()) {
                // 发送消息告知玩家正在打开云菜单
                this.sendMessage("§a正在打开云菜单...稍等一会哦~")
                // 加载Web菜单配置
                loadWebIndex(str)
            } else if (str.isMySQLMenu()) {
                // 发送消息告知控制台玩家正在打开数据库菜单
                Bukkit.getConsoleSender().sendMessage("${this.name}打开数据库菜单")
                // 加载MySQL菜单配置
                loadMySQLIndex(str)
            } else { // 不是http链接
                // 检查是否存在
                val menu = CyanMenu.menuFolder.resolve(str)
                if (!menu.exists()) {
                    this.sendMessage("§c打开菜单失败去找管理")
                    return
                }
                // 发送消息告知控制台玩家正在打开本地菜单
                Bukkit.getConsoleSender().sendMessage("${this.name}打开本地菜单")
                // 加载本地YAML菜单配置
                YamlConfiguration.loadConfiguration(menu)
            }
            // 延迟1个tick后打开菜单
            Bukkit.getScheduler().runTaskLater(cyanPlugin, {
                this.openMenu(config)
            }, 1)
        } else {
            // 不存在
            this.sendMessage("§c菜单不存在")
        }
    }


    // 定义一个函数，用于玩家打开菜单
    fun Player.openMenu(yamlContext: YamlConfiguration) {
        // 创建一个库存，大小和标题从YAML配置中读取
        val inv = Bukkit.createInventory(
            null,
            yamlContext.getInt("Size"),
            yamlContext.getString("Title")!!.placeHolderAPI(this)
                                        )
        // 从YAML配置中读取菜单权限
        val menuPermission = yamlContext.getString("Permission")
        // 如果菜单权限不为空且玩家没有该权限
        if (menuPermission.isNotEmpty() && !this.hasPermission(menuPermission)) {
            // 发送消息告知玩家没有权限
            this.sendMessage("${ChatColor.RED}你没有权限打开这个菜单")
            return
        }
        // 定义一个可变的菜单项映射
        val menu = mutableMapOf<Int, ItemData>() // 遍历所有的Item
        val items = yamlContext.getConfigurationSection("Item") ?: yamlContext.createSection("Item")
        for (slot in items.getKeys(false)) { // 如果当前槽位包含ItemStackCompiler配置
            if (items.contains("${slot}.ItemStackCompiler")) { // 获取当前槽位的配置部分
                val section = items.getConfigurationSection(slot) // 将配置中的ItemStackCompiler字符串转换为物品
                val permission = section.getString("PermissionShow")!! // 如果不为空并且有权限 或者 本身是空的
                if (permission.isNotEmpty() && this.hasPermission(permission) || permission.isEmpty()) {
                    // 将物品添加到库存中
                    val slots = slot.toInt()
                    println("即将安置在 $slots")
                    // 开始循环
                    val task = object : BukkitRunnable() {
                        override fun run() {
                            inv.setItem(slots, section.getString("ItemStackCompiler").toItemStack(this@openMenu))
                        }
                    }
                    // 创建一个ItemData对象并添加到菜单映射中
                    menu[slot.toInt()] = ItemData(
                        section.getString("ItemStackCompiler")!!,
                        section.getString("PermissionShow")!!,
                        mutableMapOf(
                            // 如果配置中包含ShiftLeftClick，将其转换为可变列表
                            ClickType.LEFT to section.getLists("LeftClick"),
                            ClickType.RIGHT to section.getLists("RightClick"),
                            ClickType.MIDDLE to section.getLists("MiddleClick"),
                            ClickType.SHIFT_LEFT to section.getLists("ShiftLeftClick"),
                            ClickType.SHIFT_RIGHT to section.getLists("ShiftRightClick"),
                                    ), task
                                                 )
                }
            }
        }
        // 添加到map
        slotData[this] = menu
        // 打开库存
        openInventory(inv)
        menu.forEach { (a, b) ->
            b.task.runTaskTimer(cyanPlugin, 0,20)
        }
    }


    private fun ConfigurationSection.getLists(str: String): MutableList<String> {
        return if (this.contains(str)) this.getStringList(str) else mutableListOf()
    }


    @EventHandler
    fun onInventoryClose(e: InventoryCloseEvent) {
        if (e.player is Player) {
            val p = e.player as Player
            if (slotData.containsKey(p)) {
                val data = slotData[p]!!
                data.forEach { (_, u) ->
                    u.task.cancel()
                }
                slotData.remove(p)
                System.gc()
            }
        }
    }

    @EventHandler
    fun onInventoryClick(e: InventoryClickEvent) {
        if (e.whoClicked is Player) {
            val p = e.whoClicked as Player
            if (slotData.containsKey(p)) {
                e.isCancelled = true
                val data = slotData[p]!!
                val item = e.currentItem
                if (item != null) {
                    if (data.containsKey(e.slot)) {
                        val itemData = data[e.slot]!!
                        if (itemData.permissionShow.isEmpty()
                            || p.hasPermission(itemData.permissionShow)
                        ) {
                            if (itemData.actions.containsKey(e.click)) {
                                itemData.actions[e.click]!!.forEach {
                                    it.analysisCommand(p)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}