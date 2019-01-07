package net.optionfactory.byter;

public class Unsigner {

    public static long BytetoUnsignedLong(int x) {
        return ((long) x) & 255L;
    }

    public static long IntegertoUnsignedLong(int x) {
        return ((long) x) & 4294967295L;
    }

}
