package cn.cyanbukkit.cyanmenu.command

import cn.cyanbukkit.cyanmenu.core.Handle.toItemStack
import cn.cyanbukkit.cyanmenu.core.Handle.toStr
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import java.io.File
import java.util.*

object EditorListener : Listener {

     val editingAction: MutableMap<Player, (Inventory) -> Unit> = mutableMapOf()

    fun Player.openThisMenu(file: File) {
        val menuConfig = YamlConfiguration.loadConfiguration(file)
        val title = menuConfig.getString("Title")
        val size = menuConfig.getInt("Size")
        val inv = Bukkit.createInventory(null, size, "修改菜单....$title")
        // 已有内容放置在菜单中
        for (it in 0 until size) {
            if (menuConfig.contains("Item.$it.ItemStackCompiler")) {
                val itemStackCompiler = menuConfig.getString("Item.$it.ItemStackCompiler")!!
                val item = itemStackCompiler.toItemStack()
                inv.setItem(it, item)
            }
        }
        this.openInventory(inv)
        editingAction[this] = { consumerInv ->
            val time = System.currentTimeMillis()
            Bukkit.getConsoleSender().sendMessage("§a修改菜单....$title")
            this.sendMessage("§a修改菜单....$title")
            menuConfig.set("Title", title)
            menuConfig.set("Size", size)
            menuConfig.set("Permission", "")
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
            menuConfig.save(file)
            this.sendMessage("§a创建成功 耗时${System.currentTimeMillis() - time}ms")
        }
    }


    @EventHandler
    fun onInventoryClick(e: InventoryCloseEvent) {
        val p = e.player
        if (editingAction.contains(p)) {
            editingAction[p]?.invoke(e.inventory)
            editingAction.remove(p)
        }
    }
}