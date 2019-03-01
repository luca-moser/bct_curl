package io.lucamoser;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class BatchHasher {

    private final static int MAX_BATCH_SIZE = 63;

    private ArrayBlockingQueue<HashReq> reqQueue;
    private int hashLength;
    private int numberOfRounds;

    public BatchHasher(int hashLength, int numberOfRounds) {
        this.hashLength = hashLength;
        this.numberOfRounds = numberOfRounds;
        this.reqQueue = new ArrayBlockingQueue<>(MAX_BATCH_SIZE);
    }

    public void hash(HashReq req) throws InterruptedException {
        reqQueue.put(req);
    }

    public void runDispatcher() throws InterruptedException {
        List<HashReq> reqs = new ArrayList<>();
        for (; ; ) {

            // take first request before starting any processing
            HashReq firstReq = reqQueue.take();
            reqs.add(firstReq);
            for (; ; ) {
                HashReq newReq = reqQueue.poll(50, TimeUnit.MILLISECONDS);
                if (newReq == null) {
                    break;
                }

                reqs.add(newReq);
                if (reqs.size() == MAX_BATCH_SIZE) {
                    break;
                }
            }
            /*
            System.out.println(reqs.size());
            for(int i = 0; i < MAX_BATCH_SIZE; i++){
                System.out.printf("request %2d:", i);
                for(int j = 0; j < 50; j++){
                    System.out.print(reqs.get(i).input[j]);
                }
                System.out.println();
            }
            */
            process(reqs);
            reqs.clear();
            break;
        }
    }

    public void process(List<HashReq> reqs) {
        BCTernaryMultiplexer multiplexer = new BCTernaryMultiplexer();
        reqs.forEach(req -> multiplexer.add(req.input));

        BCTrinary multiplexedData = multiplexer.extract();
        for(int i = 0; i < multiplexedData.low.length; i++){
            System.out.printf("%s\n", Long.toBinaryString(multiplexedData.low[i]));
        }

        BCTCurl bctCurl = new BCTCurl(hashLength, numberOfRounds);
        bctCurl.absorb(multiplexedData);

        BCTrinary result = bctCurl.squeeze(243);

        /*
        for(int i = 0; i < result.low.length; i++){
            System.out.printf("%x\n",result.low[i]);
        }
        */

        BCTernaryDemultiplexer demultiplexer = new BCTernaryDemultiplexer(result);
        for (int i = 0; i < reqs.size(); i++) {
            byte[] demuxResult = demultiplexer.get(i);

            System.out.printf("demux %2d:", i);
            for(int j = 0; j < 50; j++){
                System.out.print(demuxResult[j]);
            }
            System.out.println();

            reqs.get(i).callback.process(demuxResult);
        }
    }

}
