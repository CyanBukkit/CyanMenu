package cn.cyanbukkit.cyanmenu.cyanlib.launcher;


import cn.cyanbukkit.cyanmenu.cyanlib.loader.KotlinBootstrap;

public class JavaHandle {

    public static void getJavaVersionToDownloadHikaricp() {
        String javaVersion = System.getProperty("java.version");
        String[] split = javaVersion.split("\\.");
        int version = Integer.parseInt(split[1]);
//        if (version >= 11) {
            KotlinBootstrap.loadDepend("com.zaxxer","HikariCP","5.1.0");
            // 下载 slf4j
        KotlinBootstrap.loadDepend("org.slf4j", "slf4j-api","2.1.0-alpha1");
//        } else if (version == 8) {
//            KotlinBootstrap.loadDepend("com.zaxxer","HikariCP","4.0.3");
//        } else if (version == 7) {
//            KotlinBootstrap.loadDepend("com.zaxxer","HikariCP","2.4.13");
//        } else if (version == 6) {
//            KotlinBootstrap.loadDepend("com.zaxxer","HikariCP","2.3.13");
//        } else {
//            System.out.println("Java " + version);
//        }
        KotlinBootstrap.loadDepend("mysql","mysql-connector-java","8.0.25");
    }

}
