package cn.cyanbukkit.cyanmenu.command

import cn.cyanbukkit.cyanmenu.CyanMenu
import cn.cyanbukkit.cyanmenu.core.Handle.toItemStack
import cn.cyanbukkit.cyanmenu.core.Handle.toStr
import cn.cyanbukkit.cyanmenu.cyanlib.launcher.CyanPluginLauncher.cyanPlugin
import org.apache.commons.lang3.RandomStringUtils
import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import java.io.File

class EditorListener(
    private val isNewCreate : Boolean
) : Listener {

                        private lateinit var editingAction : (Inventory) -> Unit

    init {
        cyanPlugin.server.pluginManager.registerEvents(this, cyanPlugin)
    }


    fun editorThisMenu(p: Player ,file: File? = null, na: String = "", si: Int = 9) {
        val fileName = if (isNewCreate) {
            if (na.isEmpty()) { // 如果时新创的文件 还是没写名字
                RandomStringUtils.randomAlphabetic(10)
            } else {
                na
            }
        } else { // 如果是编辑已有的文件
            file!!.name
        }
        val targetFile = file ?: File(CyanMenu.menuFolder, "$fileName.yml")
        val menuConfig = YamlConfiguration.loadConfiguration(targetFile)
        val title = menuConfig.getString("Title") ?: "新建菜单_${fileName}"
        val size =  if (isNewCreate) {
            if (si == 0) {
                arrayOf(9, 18, 27, 36, 45, 54).random()
            } else si
        }  else {
            menuConfig.getInt("Size")
        }
        val inv = Bukkit.createInventory(null, size, "编辑菜单....$title")
        // 已有内容放置在菜单中
        for (it in 0 until size) {
            if (menuConfig.contains("Item.$it.ItemStackCompiler")) {
                val itemStackCompiler = menuConfig.getString("Item.$it.ItemStackCompiler")!!
                val item = itemStackCompiler.toItemStack()
                inv.setItem(it, item)
            }
        }
        p.openInventory(inv)
        editingAction = { consumerInv ->
            val time = System.currentTimeMillis()
            Bukkit.getConsoleSender().sendMessage("§a修改菜单....$title")
            p.sendMessage("§a修改菜单....$title")
            if (isNewCreate) {
                menuConfig.putTutorial()
                menuConfig.set("Title", title)
                menuConfig.set("Size", size)
                menuConfig.set("Permission", "")
            }
            for (slot in 0 until consumerInv.size) {
                val item = consumerInv.contents[slot]
                if (item == null) {
                    menuConfig.set("Item.$slot", null)
                    continue
                }
                Bukkit.getConsoleSender().sendMessage("§a保存物品....${slot}:${item.toStr()}")
                menuConfig.set("Item.$slot.ItemStackCompiler", item.toStr())
                menuConfig.set("Item.$slot.PermissionShow", "")
                menuConfig.set("Item.$slot.RightClick", mutableListOf(
                    "op: say op强制执行",
                    "console: say 剩余看的教程"
                ))
                menuConfig.set("Item.$slot.LeftClick", mutableListOf(
                    "op: say op强制执行",
                    "console: say 剩余看的教程"
                ))
                menuConfig.set("Item.$slot.MiddleClick", mutableListOf(
                    "op: say op强制执行",
                    "console: say 剩余看的教程"
                ))
                menuConfig.set("Item.$slot.ShiftRightClick", mutableListOf(
                    "op: say op强制执行",
                    "console: say 剩余看的教程"
                ))
                menuConfig.set("Item.$slot.ShiftLeftClick", mutableListOf(
                    "op: say op强制执行",
                    "console: say 剩余看的教程"
                ))
            }
            menuConfig.save(targetFile)
            p.sendMessage("§a创建成功 耗时${System.currentTimeMillis() - time}ms")
        }
    }


    @EventHandler
    fun onInventoryClick(e: InventoryCloseEvent) {
        val p = e.player
        editingAction.invoke(e.inventory)
        e.handlers.unregister(this)
    }


    private fun YamlConfiguration.putTutorial() {
        this.options().copyDefaults()
        this.options().header("""
        Click触发教程（全局支持Papi）
        - op: 指令
        - console: 指令
        - player: 指令
        常规 一看就能看的懂 这意味着这行指令直接执行
        - tellmsg: 这是一句语言
        在玩家聊天栏显示一行字
        - bungee: 服务器名字
        bungee 传送子服
        - cyanbukkit:
        ??? 哈哈哈 QwQ
        - demand: 如果?是∴不是执行
        比如 demand: %player_health% != 20 ? op: heal ∴ tellmsg: 你的血量不需要恢复
    """.trimIndent())
    }



}