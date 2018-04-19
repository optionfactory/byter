package net.optionfactory.byter;

import org.junit.Assert;
import org.junit.Test;

public class BitBufferTest {

    @Test
    public void canReadABitAsUnsignedInteger() {
        BitBuffer instance = new BitBuffer(ByteBuffer.wrap(new byte[]{(byte) 0b1_0000000}));
        int i = instance.getRelativeUInt(1);
        Assert.assertEquals(1, i);
    }

    @Test
    public void canReadTwoBitsAsInteger() {
        BitBuffer instance = new BitBuffer(ByteBuffer.wrap(new byte[]{(byte) 0b01_000000}));
        int i = instance.getRelativeInt(2);
        Assert.assertEquals(1, i);
    }

    @Test
    public void canRead8BitsAsInteger() {
        BitBuffer instance = new BitBuffer(ByteBuffer.wrap(new byte[]{0b00000001}));
        int i = instance.getRelativeInt(8);
        Assert.assertEquals(0b0000_0001, i);
    }

    @Test
    public void canReadSignedInteger() {
        BitBuffer instance = new BitBuffer(ByteBuffer.wrap(new byte[]{0b0000_0000, (byte) 0b1111_1111}));
        int i = instance.getRelativeInt(8);
        i = instance.getRelativeInt(8);
        Assert.assertEquals(-1, i);
    }

    @Test
    public void canRead32BitsSignedInteger() {
        BitBuffer instance = new BitBuffer(ByteBuffer.wrap(new byte[]{(byte) 0b1000_0000, (byte) 0b0000_0000, (byte) 0b0000_0000, (byte) 0b0000_0000}));
        int i = instance.getRelativeInt(32);
        Assert.assertEquals(Integer.MIN_VALUE, i);
    }

    @Test
    public void canRead9BitsAsInteger() {
        BitBuffer instance = new BitBuffer(ByteBuffer.wrap(new byte[]{0b0000_0000, (byte) 0b1_0000000}));
        int i = instance.getRelativeInt(9);
        Assert.assertEquals(0b0000_0000_1, i);
    }

    @Test
    public void canRead9BitsAsIntegerBis() {
        BitBuffer instance = new BitBuffer(ByteBuffer.wrap(new byte[]{(byte) 0b0100_0000, (byte) 0b1000_0000}));
        int i = instance.getRelativeInt(9);
        Assert.assertEquals(0b0_1000_0001, i);
    }

    @Test
    public void canReadTwo4BitIntsFromASingleByte() {
        BitBuffer instance = new BitBuffer(ByteBuffer.wrap(new byte[]{(byte) 0b0110_0011}));
        int a = instance.getRelativeInt(4);
        int b = instance.getRelativeInt(4);
        Assert.assertEquals(0b0110, a);
        Assert.assertEquals(0b0011, b);
    }

    @Test
    public void canReadTwo5BitIntsFromTwoBytes() {
        BitBuffer instance = new BitBuffer(ByteBuffer.wrap(new byte[]{(byte) 0b00011_001, (byte) 0b10_000000}));
        int a = instance.getRelativeInt(5);
        int b = instance.getRelativeInt(5);
        Assert.assertEquals(0b00011, a);
        Assert.assertEquals(0b00110, b);
    }

    @Test
    public void initialPosition() {
        BitBuffer instance = new BitBuffer(ByteBuffer.wrap(new byte[]{(byte) 0b00011_001, (byte) 0b10_000000}));
        Assert.assertEquals(0, instance.position());
    }

    @Test
    public void absoluteReadDoNotAdvancePosition() {
        BitBuffer instance = new BitBuffer(ByteBuffer.wrap(new byte[]{(byte) 0b00011_001, (byte) 0b1_0000000}));
        final long got = instance.getAbsoluteUInt(5, 4);
        Assert.assertEquals(0, instance.position());
        Assert.assertEquals(3, got);
    }

    @Test
    public void relativeReadDoAdvancePosition() {
        BitBuffer instance = new BitBuffer(ByteBuffer.wrap(new byte[]{(byte) 0b00011_001, (byte) 0b10_000000}));
        instance.getRelativeInt(4);
        Assert.assertEquals(4, instance.position());
    }

    @Test
    public void relativeMultipleBytesReadDoAdvancePosition() {
        BitBuffer instance = new BitBuffer(ByteBuffer.wrap(new byte[]{(byte) 0b00011_001, (byte) 0b10_000000}));
        instance.getRelativeInt(12);
        Assert.assertEquals(12, instance.position());
    }

    @Test
    public void noReadRemainsInInitialPosition() {
        BitBuffer instance = new BitBuffer(ByteBuffer.wrap(new byte[]{(byte) 0b00011_001, (byte) 0b10_000000}));
        instance.getRelativeInt(0);
        Assert.assertEquals(0, instance.position());
    }

    @Test
    public void positionIsProperlyCalculatedOnByteBoundary() {
        BitBuffer instance = new BitBuffer(ByteBuffer.wrap(new byte[]{(byte) 0b00011_001, (byte) 0b10_000000}));
        instance.getRelativeInt(8);
        Assert.assertEquals(8, instance.position());
    }

    @Test
    public void readTwoBytesReturnsCorrectPosition() {
        BitBuffer instance = new BitBuffer(ByteBuffer.wrap(new byte[]{(byte) 0b00011_001, (byte) 0b10_000000}));
        instance.getRelativeInt(16);
        Assert.assertEquals(16, instance.position());
    }

    @Test
    public void wrappedBufferIsMoved() {
        final ByteBuffer bb = ByteBuffer.wrap(new byte[]{(byte) 0b00011_001, (byte) 0b10_000000});
        BitBuffer instance = new BitBuffer(bb);
        instance.getRelativeInt(16);
        Assert.assertEquals(2, bb.position());
    }
}
