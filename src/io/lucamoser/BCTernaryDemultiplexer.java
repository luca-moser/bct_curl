package io.lucamoser;

public class BCTernaryDemultiplexer {

    private BCTrinary bcTrinary;

    public BCTernaryDemultiplexer(BCTrinary bcTrinary) {
        this.bcTrinary = bcTrinary;
    }

    public byte[] get(int index) {
        int length = bcTrinary.low.length;
        byte[] result = new byte[length];

        for (int i = 0; i < length; i++) {
            long low = (bcTrinary.low[i] >> index) & 1;
            long high = (bcTrinary.high[i] >> index) & 1;

            if (low == 1 && high == 0) {
                result[i] = -1;
                continue;
            }

            if (low == 0 && high == 1) {
                result[i] = 1;
                continue;
            }

            result[i] = 0;
        }
        return result;
    }

}