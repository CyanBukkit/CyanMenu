package cn.cyanbukkit.cyanmenu.api

import cn.cyanbukkit.cyanmenu.command.CreateListener
import cn.cyanbukkit.cyanmenu.command.OpenListener.openMenu
import cn.cyanbukkit.cyanmenu.command.OpenListener.slotData
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player

object MenuAPI {


    fun open(p: Player, text : String) {
        val yamlContext = YamlConfiguration()
        yamlContext.load(text)
        p.openMenu(yamlContext)
    }

    fun Player.isOpenCyanMenu(): Boolean {
        return slotData.contains(this) || CreateListener.editingAction.containsKey(this)
    }

}