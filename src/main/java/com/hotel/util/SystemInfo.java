package com.hotel.util;

public final class SystemInfo {

    private SystemInfo() {
    }

    public static void printSystemInfo() {
        System.out.println("======================================");
        System.out.println("Hotel Management System");
        System.out.println("Java Version : " + System.getProperty("java.version"));
        System.out.println("OS           : " + System.getProperty("os.name"));
        System.out.println("Architecture : " + System.getProperty("os.arch"));
        System.out.println("User         : " + System.getProperty("user.name"));
        System.out.println("======================================");
    }
}