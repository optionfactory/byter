package net.optionfactory.byter;

public class Unsigner {

    public static long BytetoUnsignedLong(int x) {
        return ((long) x) & 0xff;
    }

    public static int BytetoUnsignedInt(byte x) {
        return ((int) x) & 0xff;
    }

    public static long IntegertoUnsignedLong(int x) {
        return ((long) x) & 0xffffffffL;
    }

}
