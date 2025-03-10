package cn.cyanbukkit.cyanmenu.core

import org.bukkit.event.inventory.ClickType

data class MenuData(
    val title: String,
    val size: Int,//9 18 27 36 45 54
    val permission: String = "",
    val demand: MutableList<String> = mutableListOf(),
    val itemData: MutableMap<Int, ItemData>
)

data class ItemData(
    val itemStackCompiler: String,
    val permissionShow: String = "",
    val actions: MutableMap<ClickType, MutableList<String>>,
    val task : Int
)


