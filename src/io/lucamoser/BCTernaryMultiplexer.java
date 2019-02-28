package io.lucamoser;

import java.util.ArrayList;

public class BCTernaryMultiplexer {

    private ArrayList<byte[]> trinaries;

    public BCTernaryMultiplexer() {
        this.trinaries = new ArrayList<>();
    }

    public void add(byte[] trits) {
        trinaries.add(trits);
    }

    public byte[] get(int index) {
        return trinaries.get(index);
    }

    public BCTrinary extract() {
        int trinariesCount = trinaries.size();
        int tritsCount = trinaries.get(0).length;

        BCTrinary result = new BCTrinary(new long[tritsCount], new long[tritsCount]);
        for (int i = 0; i < tritsCount; i++) {
            BCTrit bcTrit = new BCTrit();

            for (int j = 0; j < trinariesCount; j++) {
                switch (trinaries.get(j)[i]) {
                    case -1:
                        bcTrit.low |= 1 << j;
                        break;
                    case 1:
                        bcTrit.high |= 1 << j;
                        break;
                    case 0:
                        bcTrit.low |= 1 << j;
                        bcTrit.high |= 1 << j;
                        break;
                    default:
                        // TODO: throw an error
                }
            }

            result.low[i] = bcTrit.low;
            result.high[i] = bcTrit.high;
        }

        return result;
    }

}
