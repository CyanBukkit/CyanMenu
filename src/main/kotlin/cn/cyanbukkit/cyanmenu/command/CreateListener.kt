package cn.cyanbukkit.cyanmenu.command

import cn.cyanbukkit.cyanmenu.CyanMenu
import cn.cyanbukkit.cyanmenu.core.Handle.toStr
import org.apache.commons.lang3.RandomStringUtils
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import java.io.File

object CreateListener : Listener {

     val editingAction: MutableMap<Player, (Inventory) -> Unit> = mutableMapOf()

    // Lorem without space
    fun Player.openDefaultMenu() = this.openDefaultMenu(
        RandomStringUtils.randomAlphabetic(10),
        arrayOf(9, 18, 27, 36, 45, 54).random()
    )

    fun Player.openDefaultMenu(title: String, size: Int) {
        val inv = Bukkit.createInventory(null, size, title)
        this.openInventory(inv)
        editingAction[this] = { consumerInv ->
            val time = System.currentTimeMillis()
            Bukkit.getConsoleSender().sendMessage("§a创建菜单....$title")
            this.sendMessage("§a创建菜单....$title")
            val menu = File(CyanMenu.menuFolder, "$title.yml")
            menu.createNewFile()
            // 放置教程
            val menuConfig = YamlConfiguration.loadConfiguration(menu)
            menuConfig.putTutorial()
            menuConfig.set("Title", title)
            menuConfig.set("Size", size)
            menuConfig.set("Permission", "")
            for (slot in 0 until consumerInv.size) {
                val item = consumerInv.contents[slot]
                if (item == null) {
                    menuConfig.set("Item.$slot", null)
                    continue
                }
                val itemMeta = item.itemMeta
                if (itemMeta?.hasDisplayName() == false) {
                    itemMeta.displayName = item.type.name
                }
                item.itemMeta = itemMeta
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
            menuConfig.save(menu)
            this.sendMessage("§a创建成功 耗时${System.currentTimeMillis() - time}ms")
        }
    }

    @EventHandler
    fun on(e: InventoryCloseEvent) {
        val player = e.player
        if (editingAction.containsKey(player)) {
            editingAction[player]?.invoke(e.inventory)
            editingAction.remove(player)
        }
    }


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
