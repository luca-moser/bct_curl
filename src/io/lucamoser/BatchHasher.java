package io.lucamoser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class BatchHasher {

    private ArrayBlockingQueue<HashReq> reqQueue;
    private int hashLength;
    private int numberOfRounds;

    public BatchHasher(int hashLength, int numberOfRounds) {
        this.hashLength = hashLength;
        this.numberOfRounds = numberOfRounds;
        this.reqQueue = new ArrayBlockingQueue<>(100);
    }

    public void hash(HashReq req) throws InterruptedException {
        reqQueue.put(req);
    }

    public void runDispatcher() throws InterruptedException {
        for (; ; ) {
            List<HashReq> reqs = new ArrayList<>();

            // take first request before starting any processing
            HashReq firstReq = reqQueue.take();
            reqs.add(firstReq);

            for (; ; ) {
                HashReq newReq = reqQueue.poll(50, TimeUnit.MILLISECONDS);
                if (newReq == null) {
                    break;
                }
                if (reqs.size() == 64) {
                    break;
                }
                reqs.add(newReq);
            }
            process(reqs);
        }
    }

    public void process(List<HashReq> reqs) {
        BCTernaryMultiplexer multiplexer = new BCTernaryMultiplexer();
        reqs.forEach(req -> multiplexer.add(req.input));

        BCTrinary multiplexedData = multiplexer.extract();
        BCTCurl bctCurl = new BCTCurl(hashLength, numberOfRounds);
        bctCurl.reset();
        bctCurl.absorb(multiplexedData);

        BCTrinary result = bctCurl.squeeze(243);
        BCTernaryDemultiplexer demultiplexer = new BCTernaryDemultiplexer(result);
        for (int i = 0; i < reqs.size(); i++) {
            reqs.get(i).callback.process(demultiplexer.get(i));
        }
    }

}
