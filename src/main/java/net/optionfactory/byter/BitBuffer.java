package net.optionfactory.byter;

import com.google.common.primitives.UnsignedLong;
import java.nio.ByteOrder;
import net.emaze.dysfunctional.contracts.dbc;
import net.emaze.dysfunctional.tuples.Pair;

/**
 * A bit-view of a ByteBuffer. Works on entire bytes only (at the moment)
 * Supports reading up to 64 bits at once, as either signed or unsigned long
 * relative reads DO advance wrapped ByteBuffer's position, but not vice-versa
 * MSB-first only
 *
 * @author fdegrassi
 * @author dsalvador
 */
public class BitBuffer {

    private static class State {

        private final long bitPosition;

        public State(long position) {
            this.bitPosition = position;
        }

        @Override
        public String toString() {
            return "State{" + "bitPosition=" + bitPosition + '}';
        }

    }

    private final ByteBuffer inner;
    private State state;

    public BitBuffer(ByteBuffer inner) {
        this.inner = inner;
        this.state = new State(inner.position() * 8);
    }

    public static BitBuffer fromRelativeSlice(ByteBuffer source, int bytesToSlice) {
        final byte[] slice = source.getRelativeSlice(bytesToSlice);
        return new BitBuffer(ByteBuffer.wrap(slice));
    }

    public static BitBuffer frombytes(byte[] source) {
        return new BitBuffer(ByteBuffer.wrap(source));
    }

    public int getRelativeUInt(int bits) {
        dbc.precondition(bits <= 16, "bits must be <= 16");
        final Pair<State, Long> stateAndResult = doGetLong(state, bits);
        inner.position((int) (stateAndResult.first().bitPosition / 8));
        this.state = stateAndResult.first();
        return (int) Integer.toUnsignedLong(stateAndResult.second().intValue());
    }

    public int getRelativeUInt(int bits, ByteOrder byteOrder) {
        dbc.precondition(bits <= 16, "bits must be <= 16");
        final Pair<State, Long> stateAndResult = doGetLong(state, bits);
        inner.position((int) (stateAndResult.first().bitPosition / 8));
        this.state = stateAndResult.first();
        return (int) Integer.toUnsignedLong(stateAndResult.second().intValue());
    }

    public boolean getRelativeBit() {
        return getRelativeUInt(1) > 0;
    }

    public int getRelativeInt(int bits) {
        dbc.precondition(bits <= 32, "bits must be <= 32");
        final Pair<State, Long> stateAndResult = doGetLong(state, bits);
        this.state = stateAndResult.first();
        inner.position((int) (stateAndResult.first().bitPosition / 8));
        final long result = stateAndResult.second();
        return (int) ((result >>> (bits - 1)) == 1
                ? -(~(result - 1) & ((1l << bits) - 1))
                : result);
    }

    public long getRelativeLong(int bits) {
        dbc.precondition(bits <= 64, "bits must be <= 64");
        final Pair<State, Long> stateAndResult = doGetLong(state, bits);
        inner.position((int) (stateAndResult.first().bitPosition / 8));
        this.state = stateAndResult.first();
        final long result = stateAndResult.second();
        return (result >>> (bits - 1)) == 1
                ? -(~(result - 1) & ((1l << bits) - 1))
                : result;
    }

    public UnsignedLong getRelativeULong(int bits) {
        dbc.precondition(bits <= 64, "bits must be <= 64");
        final Pair<State, Long> stateAndResult = doGetLong(state, bits);
        inner.position((int) (stateAndResult.first().bitPosition / 8));
        this.state = stateAndResult.first();
        return UnsignedLong.fromLongBits(stateAndResult.second());
    }

    public long getAbsoluteUInt(int position, int bits) {
        dbc.precondition(bits <= 32, "bits must be <= 32");
        dbc.precondition(bits >= 1, "bits must be >= 1");
        final State absoluteState = new State(position);
        return Integer.toUnsignedLong(doGetLong(absoluteState, bits).second().intValue());
    }

    public int getAbsoluteInt(int position, int bits) {
        dbc.precondition(bits <= 32, "bits must be <= 32");
        dbc.precondition(bits >= 1, "bits must be >= 1");
        final State absoluteState = new State(position);
        final int result = doGetLong(absoluteState, bits).second().intValue();
        return (result >>> (bits - 1)) == 1
                ? ~(result - 1)
                : result;

    }

    public boolean getAbsoluteBit(int position) {
        return getAbsoluteUInt(position, 1) > 0;
    }

    private Pair<State, Long> doGetLong(State state, int bits) {
        long position = state.bitPosition;
        byte currentByte = inner.get((int) (position / 8));
        int bitOffsetInByte = (int) (state.bitPosition % 8);

        long result = 0;
        for (int offset = 0; offset < bits;) {
            if (bitOffsetInByte == 8) {
                bitOffsetInByte = 0;
                position += 8;
                currentByte = inner.get((int) (position / 8));
            }
            final int bitsInByte = Math.min(8 - bitOffsetInByte, bits - offset);
            final long partial = Byte.toUnsignedLong((byte) ((currentByte << bitOffsetInByte & 0xFF) >>> (8 - bitsInByte)));
            bitOffsetInByte += bitsInByte;
            result = result << bitsInByte | partial;
            offset += bitsInByte;
        }
        return Pair.of(new State(state.bitPosition + bits), result);
    }

    public long position() {
        return this.state.bitPosition;
    }

}
