package cn.cyanbukkit.cyanmenu.cyanlib.launcher;


import cn.cyanbukkit.cyanmenu.CyanMenu;
import cn.cyanbukkit.cyanmenu.cyanlib.loader.KotlinBootstrap;
import cn.cyanbukkit.cyanmenu.command.CMenuCommand;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;

/**
 * 嵌套框架
 */

public class CyanPluginLauncher extends JavaPlugin {

    public static CyanPluginLauncher cyanPlugin;


    public CyanPluginLauncher() {
        cyanPlugin = this;
        KotlinBootstrap.init();
        JavaHandle.getJavaVersionToDownloadHikaricp();
    }


    @Override
    public void onEnable() {
        CyanMenu.INSTANCE.onEnable();
        regCommand();
    }

    public void registerCommand(Command cmd) {
        Class<?> clazz = getServer().getPluginManager().getClass();
        try {
            Field field = clazz.getDeclaredField("commandMap");
            field.setAccessible(true);
            SimpleCommandMap commandMap = (SimpleCommandMap) field.get(getServer().getPluginManager());
            commandMap.register(cyanPlugin.getName(), cmd);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void regCommand() {
        registerCommand(new CMenuCommand());
    }



}