package com.matei.eece411.util;

/**
 * Various static routines to help with strings
 */
public class StringUtils {

        public static String byteArrayToHexString(byte[] bytes) {
            return byteArrayToHexString(bytes, 0, bytes.length);
        }


        public static String byteArrayToHexString(byte[] bytes, int offset, int length) {
        StringBuffer buf=new StringBuffer();
        String       str;
        int val;

        for (int i = offset; i < offset + length; i++) {
            val = ByteOrder.ubyte2int(bytes[i]);
            str = Integer.toHexString(val);
            while ( str.length() < 2 )
                str = "0" + str;
            buf.append( str );
        }
        return buf.toString().toUpperCase();
    }
}