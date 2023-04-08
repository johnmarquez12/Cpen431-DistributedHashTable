package com.g10.CPEN431.A11;

import java.time.LocalTime;
import java.time.ZoneId;

public class Logger {

    private static final boolean VERBOSE = false;

    public static void log(Object message) {
        System.out.println("["+now()+"] "+message);
    }

    public static void log(String format, Object... args) {
        System.out.print("["+now()+"] ");
        System.out.printf(format+"\n", args);
    }

    public static void logVerbose(Object message) {
        if (VERBOSE)
            System.out.println("["+now()+"] "+message);
    }
    public static void logVerbose(String format, Object... args) {
        if (VERBOSE) {
            System.out.print("["+now()+"] ");
            System.out.printf(format+"\n", args);
        }
    }

    public static void err(Object message) {
        System.err.println(NodePool.getInstance().getMyHost()+"["+now()+"] (!) "+message);
    }

    public static void err(String format, Object... args) {
        System.err.print("["+now()+"] ");
        System.err.printf(format+"\n", args);
    }

    public static void errVerbose(Object message) {
        if (VERBOSE)
            System.err.println(NodePool.getInstance().getMyHost()+"["+now()+"] (!) "+message);
    }

    private static LocalTime now() {
        return LocalTime.now(ZoneId.systemDefault());
    }
}
