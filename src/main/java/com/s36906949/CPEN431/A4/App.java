package com.s36906949.CPEN431.A4;

import java.io.IOException;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException {
        System.out.println( "Hello World!" );
        new UDPServerThread().start();
    }
}
