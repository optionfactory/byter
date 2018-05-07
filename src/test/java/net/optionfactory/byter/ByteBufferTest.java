package net.optionfactory.byter;

import java.nio.ByteOrder;
import org.junit.Assert;
import org.junit.Test;

public class ByteBufferTest {

    @Test
    public void readsCorrectlyNegativeNumbers() {
        final ByteBuffer testing = ByteBuffer.wrap(new byte[]{(byte) 0x80}, ByteOrder.BIG_ENDIAN);
        Assert.assertEquals(-128, testing.getRelativeLong(1));
    }

    @Test
    public void readsCorrectlyNegativeNumbersLittleEndian() {
        final ByteBuffer testing = ByteBuffer.wrap(new byte[]{(byte) 0xec, (byte) 0xff, (byte) 0xff, (byte) 0xff}, ByteOrder.LITTLE_ENDIAN);
        Assert.assertEquals(-20, testing.getRelativeLong(4));
    }
}
