package cn.cyanbukkit.cyanmenu.command

import cn.cyanbukkit.cyanmenu.CyanMenu
import cn.cyanbukkit.cyanmenu.command.OpenListener.openMenu
import cn.cyanbukkit.cyanmenu.core.Handle.toStr
import cn.cyanbukkit.cyanmenu.cyanlib.launcher.CyanPluginLauncher
import cn.cyanbukkit.cyanmenu.data.getMySQLMenuList
import cn.cyanbukkit.cyanmenu.data.put
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CMenuCommand : Command(
    "cmenu",
    "CyanMenu的主命令",
    "/cmenu",
    listOf("cm")
) {
    // cmenu reload
    // cmenu open <menuName> [player]
    // cmenu list
    // cmenu create <menuName> <size>
    // cmenu delete <menuName>
    override fun execute(sender: CommandSender, commandLabel: String, args: Array<String>): Boolean {
        if (args.isEmpty()) {
            sender.sendMessage("""
                        
                §7 - §a/cmenu reload §7- §a重载配置文件
                §7 - §a/cmenu open <menuName> [player] §7- §a打开菜单
                §7 - §a/cmenu list §7- §a列出所有菜单
                §7 - §a/cmenu create <menuName> <size> §7- §a创建菜单
                §7 - §a/cmenu edit <menuName> §7- §a编辑菜单
                §7 - §a/cmenu delete <menuName> §7- §a删除菜单
                §7 - §a/cmenu enable <enablename> <menuinfo> §7- §a启动菜单
                §7 - §a/cmenu hand §7- §手上的物品信息
                §7 - §a/cmenu upload <menuName> §7- §手上的物品信息
                
            """.trimIndent())
            return true
        }
        if (sender is Player) {
            when (args[0]) {
                "upload" -> {
                    // 上传到 mysql
                    if (args.size == 2) {
                        sender.put(args[1])
                    } else {
                        sender.sendMessage("§c参数错误")
                    }
                }

                "reload" -> {
                    // 重载
                    CyanMenu.menuConfig.load(CyanMenu.menuConfigFile)
                    sender.sendMessage("§a重载成功")
                }

                "enable" -> {
                    CyanMenu.menuConfig.set(args[1], args[2])
                    CyanMenu.menuConfig.save(CyanMenu.menuConfigFile)
                    sender.sendMessage("§a启动成功")
                }

                "open" -> {
                    // 打开
                    if (args.size == 2) {
                        Bukkit.getScheduler().runTaskAsynchronously(CyanPluginLauncher.cyanPlugin) {
                            sender.openMenu(args[1])
                        }
                    } else if (args.size == 3) {
                        val p = CyanPluginLauncher.cyanPlugin.server.getPlayer(args[2])
                        if (p != null) {
                            Bukkit.getScheduler().runTaskAsynchronously(CyanPluginLauncher.cyanPlugin) {
                                p.openMenu(args[1])
                            }
                        } else {
                            sender.sendMessage("§c玩家不在线")
                        }
                    } else {
                        sender.sendMessage("§c参数错误")
                    }
                }

                "hand" -> {
                    sender.sendMessage("§a手上的信息: §f" + sender.inventory.itemInHand.toStr())
                }

                "list" -> {
                    // 列表
                    sender.sendMessage("§7-= §b总配置文件启用的菜单  §7=-")
                    CyanMenu.menuConfig.getKeys(false).forEach {
                        sender.sendMessage("§7 - §a$it")
                    }

                    sender.sendMessage("§7-= §b本地菜单库文件  §7=-")
                    CyanMenu.menuFolder.listFiles()?.forEach {
                        sender.sendMessage("§7 - §a${it.name}")
                    }

                    sender.sendMessage("§7-= §b数据库中的菜单  §7=-")
                    sender.getMySQLMenuList().forEach {
                        sender.sendMessage("§7 - §a$it")
                    }
                }

                "create" -> {
                    // 创建
                    if (args.size == 3) {
                        val size = args[2].toIntOrNull()
                        if (!arrayOf(9, 18, 27, 36, 45, 54).contains(size)) {
                            sender.sendMessage("§c参数错误 不是9的倍数")
                        }
                        if (size != null) {
                            EditorListener(true).editorThisMenu(
                                sender, null,args[1], size
                                                                )
                            sender.sendMessage("§a创建成功")
                        } else {
                            sender.sendMessage("§c参数错误")
                        }
                    } else {
                        EditorListener(true).editorThisMenu(
                            sender, null
                                                            )
                        sender.sendMessage("§a创建随机执行菜单")
                    }
                }

                "delete" -> {
                    // 删除
                    if (args.size == 2) {
                        if (CyanMenu.menuFolder.listFiles()?.find { it.name == args[1] } == null) {
                            sender.sendMessage("§c菜单不存在这里只能删除本地的菜单如果网页,菜单请到总menu管理删除")
                            return true
                        } else {
                            CyanMenu.menuFolder.listFiles()?.find { it.name == args[1] }?.delete()
                            sender.sendMessage("§a删除成功")
                            runCatching {
                                CyanMenu.menuConfig.set(args[1], null)
                                CyanMenu.menuConfig.save(CyanMenu.menuConfigFile)
                            }
                        }
                    } else {
                        sender.sendMessage("§c参数错误")
                    }
                }

                "edit" -> {
                    // 编辑
                    if (args.size == 2) {
                        val name = if (!args[1].endsWith(".yml")) {
                            args[1] + ".yml"
                        } else {
                            args[1]
                        }

                        if (CyanMenu.menuFolder.listFiles()?.find { it.name == name } == null) {
                            sender.sendMessage("§c菜单不存在这里只能编辑本地的菜单哦~如果网页自己去网页编辑去,如果是数据库那请你自己转下base64放本地里我才能编辑哦~")
                            return true
                        } else {
                            EditorListener(false).editorThisMenu(
                                sender, CyanMenu.menuFolder.listFiles()?.find { it.name == name }!!
                                                                )
                        }
                    } else {
                        sender.sendMessage("§c参数错误")
                    }
                }

                else -> {
                    sender.sendMessage("§c参数错误")
                }
            }
        }
        return true
    }


    // cmenu reload
    // cmenu open <menuName> [player]
    // cmenu list
    // cmenu create <menuName> <size>
    // cmenu delete <menuName>
    override fun tabComplete(sender: CommandSender, alias: String, args: Array<out String>?): MutableList<String> {
        return when {
            args?.size == 1 -> {
                mutableListOf("reload", "open", "list", "create", "delete", "edit", "enable", "hand", "upload")
            }

            args?.size == 2 -> {
                when (args[0]) {
                    "open" -> {
                        CyanMenu.menuConfig.getKeys(false).toMutableList()
                    }

                    "create" -> {
                        mutableListOf()
                    }

                    "delete" -> {
                        CyanMenu.menuFolder.listFiles()?.map { it.name }?.toMutableList() ?: mutableListOf()
                    }

                    else -> {
                        mutableListOf()
                    }
                }
            }

            args?.size == 3 -> {
                when (args[0]) {
                    "open" -> {
                        CyanPluginLauncher.cyanPlugin.server.onlinePlayers.map { it.name }.toMutableList()
                    }

                    "create" -> {
                        mutableListOf()
                    }

                    "delete" -> {
                        mutableListOf()
                    }

                    else -> {
                        mutableListOf()
                    }
                }
            }

            else -> {
                mutableListOf()
            }
        }
    }
}