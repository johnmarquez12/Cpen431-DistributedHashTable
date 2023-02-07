package com.s36906949.CPEN431.A4;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws Exception {
        System.out.println( "Hello World!" );
        UDPServer.run(5555);
    }

    public static long freeMemory() {
        long max = Runtime.getRuntime().maxMemory();
        long free = Runtime.getRuntime().freeMemory();
        long total = Runtime.getRuntime().totalMemory();

        long used = total - free;
        return max - used - (3000 * 1024); // some buffer, so we never die
    }
    public static long trueFreeMemory() {
        long max = Runtime.getRuntime().maxMemory();
        long free = Runtime.getRuntime().freeMemory();
        long total = Runtime.getRuntime().totalMemory();

        long used = total - free;
        return max - used;
    }
}
