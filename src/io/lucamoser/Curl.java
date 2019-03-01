package io.lucamoser;

import java.util.Arrays;

public class Curl {
    public final static int HASH_LENGTH = 243;

    private final static int NUMBER_OF_ROUNDS = 81;
    private static final int STATE_LENGTH = 3 * HASH_LENGTH;

    private static final byte[] TRUTH_TABLE = {1, 0, -1, 2, 1, -1, 0, 2, -1, 1, 0};

    private final int numberOfRounds;
    private final byte[] state;
    private final byte[] scratchpad = new byte[STATE_LENGTH];

    protected Curl() {
        numberOfRounds = NUMBER_OF_ROUNDS;
        state = new byte[STATE_LENGTH];
    }

    public void absorb(final byte[] trits, int offset, int length) {

        do {
            System.arraycopy(trits, offset, state, 0, length < HASH_LENGTH ? length : HASH_LENGTH);
            transform();
            offset += HASH_LENGTH;
        } while ((length -= HASH_LENGTH) > 0);
    }

    public void squeeze(final byte[] trits, int offset, int length) {

        do {
            System.arraycopy(state, 0, trits, offset, length < HASH_LENGTH ? length : HASH_LENGTH);
            transform();
            offset += HASH_LENGTH;
        } while ((length -= HASH_LENGTH) > 0);
    }

    private void transform() {

        int scratchpadIndex = 0;
        int prevScratchpadIndex = 0;
        for (int round = 0; round < numberOfRounds; round++) {
            System.arraycopy(state, 0, scratchpad, 0, STATE_LENGTH);
            for (int stateIndex = 0; stateIndex < STATE_LENGTH; stateIndex++) {
                prevScratchpadIndex = scratchpadIndex;
                if (scratchpadIndex < 365) {
                    scratchpadIndex += 364;
                } else {
                    scratchpadIndex += -365;
                }
                state[stateIndex] = TRUTH_TABLE[scratchpad[prevScratchpadIndex] + (scratchpad[scratchpadIndex] << 2) + 5];
            }
        }
    }

    public void reset() {
        Arrays.fill(state, (byte) 0);
    }
}
