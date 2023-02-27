package com.g10.CPEN431.A6;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws Exception {
        System.out.println( "Hello World!" );



        InetAddress tmp = InetAddress.getByName("localhost");

        // Todo: parse a txt file given
        Host[] servers = {new Host(tmp, 1), new Host(tmp, 5555), new Host(tmp, 3)};
        int port = 5555;

        Host me = new Host(getMyHost(), port);

        NodePool.create(me, servers);

        UDPServer.run(port);
    }

    public static InetAddress getMyHost() {
        // todo: ping the ec2 service for this.
        try {
            return InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
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
