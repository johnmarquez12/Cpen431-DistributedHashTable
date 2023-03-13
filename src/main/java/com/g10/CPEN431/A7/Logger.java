package com.g10.CPEN431.A7;

import java.time.LocalTime;
import java.time.ZoneId;

public class Logger {

    public static void log(Object message) {
        System.out.println("["+now()+"] "+message);
    }

    public static void log(String format, Object... args) {
        System.out.print("["+now()+"] ");
        System.out.printf(format, args);
    }

    public static void err(Object message) {

        System.err.println("["+now()+"] (!) "+message);
    }

    private static LocalTime now() {
        return LocalTime.now(ZoneId.systemDefault());
    }
}
