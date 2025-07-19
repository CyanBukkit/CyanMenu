package cn.cyanbukkit.cyanmenu.cyanlib.launcher;

import cn.cyanbukkit.cyanmenu.cyanlib.loader.KotlinBootstrap;

public class JavaHandle {

    public static void getJavaVersionToDownloadHikaricp() {
        double classVersion = Double.parseDouble(System.getProperty("java.class.version"));
        System.out.println("classVersion: " + classVersion);
        // 17 = 61.0
        // 11 = 55.0
        // 8 = 52.0
        if (classVersion >= 55.0) {
            KotlinBootstrap.loadDepend("com.zaxxer", "HikariCP", "5.1.0");
        } else if (classVersion < 55.0 && classVersion >= 52.0) {
            KotlinBootstrap.loadDepend("com.zaxxer","HikariCP","4.0.3");
        } else  {
            KotlinBootstrap.loadDepend("com.zaxxer","HikariCP","2.4.13");
        }
        KotlinBootstrap.loadDepend("mysql","mysql-connector-java","8.0.25");
        KotlinBootstrap.loadDepend("org.slf4j","slf4j-api","1.7.30");
    }

}
