package com.g10.CPEN431.A7;

import java.time.LocalTime;
import java.time.ZoneId;

public class Logger {

    public static void log(Object message) {
        LocalTime now = LocalTime.now(ZoneId.systemDefault());

        System.out.println("["+now+"] "+message);
    }
}
