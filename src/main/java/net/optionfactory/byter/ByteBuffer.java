package net.optionfactory.byter;

import com.google.common.primitives.UnsignedLong;
import java.nio.ByteOrder;
import net.emaze.dysfunctional.contracts.dbc;
import org.apache.commons.lang3.ArrayUtils;

/**
 * A wrapper for java.nio.ByteBuffer, capable for handling both signed and
 * unsigned values, little endian and big endian
 *
 * @author dsalvador
 */
public class ByteBuffer {

    public static byte[] getRelativeSlice(java.nio.ByteBuffer source, int bytes) {
        final byte[] res = new byte[bytes];
        source.get(res);
        return res;
    }

    public static ByteBuffer wrap(byte[] data) {
        return new ByteBuffer(java.nio.ByteBuffer.wrap(data), ByteOrder.LITTLE_ENDIAN);
    }

    public static ByteBuffer wrap(byte[] data, ByteOrder byteOrder) {
        return new ByteBuffer(java.nio.ByteBuffer.wrap(data), byteOrder);
    }

    private final java.nio.ByteBuffer inner;
    private final ByteOrder byteOrder;

    public java.nio.ByteBuffer getInner() {
        return inner;
    }

    public ByteBuffer(java.nio.ByteBuffer inner, ByteOrder byteOrder) {
        this.inner = inner;
        this.byteOrder = byteOrder;
    }

    public byte[] getRelativeSlice(int bytes) {
        final byte[] res = new byte[bytes];
        inner.get(res);
        return res;
    }

    public int getRelativeUInt(int bytes) {
        dbc.precondition(bytes <= 2, "bytes must be <= 2");
        final Long result = doGetLong(bytes, byteOrder);
        return (int) Integer.toUnsignedLong(result.intValue());
    }

    public int getRelativeInt(int bytes) {
        dbc.precondition(bytes <= 4, "bytes must be <= 4");
        final Long result = doGetLong(bytes, byteOrder);
        return result.intValue();
    }

    public long getRelativeLong(int bytes) {
        dbc.precondition(bytes <= 8, "bytes must be <= 8");
        return doGetLong(bytes, byteOrder);
    }

    public UnsignedLong getRelativeULong(int bytes) {
        dbc.precondition(bytes <= 8, "bytes must be <= 8");
        final Long res = doGetLong(bytes, byteOrder);
        return UnsignedLong.valueOf(res);
    }

    private Long doGetLong(int bytes, ByteOrder byteOrder) {
        byte[] data = new byte[bytes];
        inner.get(data);
        if (byteOrder == ByteOrder.LITTLE_ENDIAN) {
            ArrayUtils.reverse(data);
        }
        long result = 0;
        for (int offset = 0; offset < bytes; offset++) {
            result = (result << 8) + Byte.toUnsignedLong(data[offset]);
        }
        return result;
    }

    //ByteBuffer wrappers
    public byte get() {
        return inner.get();
    }

    public int position() {
        return inner.position();
    }

    public int limit() {
        return inner.limit();
    }

    public int remaining() {
        return inner.remaining();
    }

    void position(int i) {
        inner.position(i);
    }

    byte get(int i) {
        return inner.get(i);
    }

}
