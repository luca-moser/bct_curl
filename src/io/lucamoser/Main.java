package io.lucamoser;

import jota.utils.Converter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    private final static int HASHERS_COUNT = 4;
    private final static int HASH_LENGTH_TRITS = 243;
    private final static int NUMBER_OF_ROUNDS = 81;

    private static AtomicInteger count = new AtomicInteger();

    public static void main(String[] args) throws InterruptedException {
        String txTrytes = "MPPBPXJFQQRMFKWUQKQIGLCEUXXQMNJTFUESPVMIAUXRHCWAWQYHD9A9EMFWYBHCGJNX9TOYVTKUVK9NXBBIPRUMMYGQXBQKKQHJLFWTKOCGUBRKIHVMTKZWTX9DCLNJ9CZIT9JODBVVPSJFIMFXSSIZIBNGQVZMDCNCLISRVJISWVFRKGANILMRZPRVMVEIDVQQQERNTZCNOQZJGMZK9YAOSMVBWEAFBYHXCBNTTRJVCNOCQIW9TRTUT9POLILKQWND9MAGCCWCFPDQY9UCBJDQZNJSLOVCEMWTYDILWLDDA9NWXEIVTWZEUKZJYAYGUAAXOP9FXXUTZB9RCVSCHGYXVESIIWTPLFOAFPB9YLNKFD9ECFDWHRKBVAOIXOL9YXMOSXVIKTOFTLXNPSCEYIGNMS9UJTXFOITHUTLJQTEBZKIVNCRGHKSABNEIXRWKKSSHQHCYIMQRW9BEOITKWIEYSILVHWZUEOITYACYXENOIXWIRPPVHUFTEFNBJVFBWJWNSXNZEY9QWOSJDMCCTYMOUBNU9UOOGYYKHJFBDA9LMLWKXMZMDWAUGYCBUAZKGKWCORFFJBDA9FTIOWCGFS9ISQUF9ELLVKFMPTGTTESZKUFR9MGAFMHGZKGZKAZDAEFTAIJDZKXUTDHTFBPXJLBBIYZDLODKGXGZBJUFHUBRBUFTBNZMSRMMYKX9GFWQOUDSOESJRHNRVLXDSXAVCZ9RBURH9YPBOVPPBDVGZWBRWO9XMXXNAHT9FN9NHKOXPQYKHUTBXYJOJFGAUVADQILVS9SR9NNOXYTWNAXLHXT9ZDFMJHKYYKUNZMWZCJEE9MQCUMUIML9NEGSATM9GNKNUHNCSYGXZFWGJRFSOYWFBCYXKSIFNGDNDMOXKROOFVDSQIAHMMDSRBJWBFTISTSWALEHHLMPUHXAZQRNKNNMLPGRETOMEDWDNHHDFR9WNXTATTLDSINGDSY9SABOZPVODVPZRWTYLJZUS9CIBETWNHRBXG9NZNTIPKNNFECMPDCOIKXPLQRZTWUZVEJQRVVOJTNKBABXDUOIDBRTBFNAULTGFRJJNHCKXMVXXPOUUODCAPT9RHXAXWSHVLQQBAWOIRVNCOVNIDBTWHJQWJACCHCYD9YNDKLAKAOUEFVVVIBWVHMMPLYRQQMTK9PNHZZDNTULDMCN9HXCWWSCLKJSYOHOORMOUPDURGSMXWGDH9VRXJNVDB9XUDBRALKEGQFOGEBMNOPYKEKBNRNFGKECX99YCBVJXLZKNSFYGRMWMTIGPVGAFKVGYKUZXSHZUI9QL9V9TIJQQU9DQQCNRYLGLPXI99SAEHIKMLD9UW9BCOFBOZKTQGABG9WEQJPS9CNPBHLYIUDCTDKLGTUNQFMKYOH9DBUD9HBDFJFBXGUJ9DEUHQXUYPZRIFACEOPVGXHMFNKMWPJZNLOUIHQXVKWEYWPF9BYQMWJTJZXGTXUZPQHVVRFRXGXEWSEYEIWBLALUY9VFPNQYJFTGZXLVHFKSZMZD9RRMXJEVIA9KKFNJOCRWTEMQDYUJOSXWMW9JTXAAWHOBNMRZB9XSRODFOQMOP9OIHZNDJZOHPDCHVGYWOLQRQWKZVNBCLAL9YWAYBDNQEVVGXYYFSQUOCWBAHGEF9KABPMCLPTVEJPJPNGKHIZIMKKDOZXQGGEMTOJEQTUZUFQKJGDTCGSXALWQNAU9Q9ZPNGUY9EEHTZHKHKBD9EBIIADVKXHA9SKWKBJHU9TEOWUG9YYLHAQ9IXEUZAHAWLSJZHAIUSMAQCYXXDZ9NIRHZOAJBX9ZWNEWPAJSOYKFQLDPAWXQRKANCHOTLAJXAJQH9BVNRVXSWAWHAOITXFQFKIVPWNVQDYZNMGTONJCDKVYHOEFKAFABJXMQUYSKDXHOEPUGXSUVCIVKU9YMHYDWFDGGKXRVFA9LFMZVMBAPM9EJANLPIQIUCKMPBDGNP9SOTAPDFDJPZRXABFTJCIQQTRQNWZMODXLHTHDQRV9LBTVFSZJKGOEVLZYYLBBAXSGVAI9EOUE9NNSBMRPMWFFXDMMEXKVKDNQBKJGZJGRGMTKJVN9CHMLFG9UY9APZVXEAHFSXCQFIAJIALBDRVJAWATSQXQFZPSZXEVMKIKVRRIMGBEQGAMJHKMYDPFHWYGQROSEFQTRPNAHEOSVCYCHGHKEXLCEKYPZXNN9OHUZIDLFMRAAFKPEUIOCSWVLSZHASSBLNS9UOFMZGTFZOPWQGRPKHPZFRQSMCRXAFM9TEDWLHMCZ99999999999999999999999999999999999999999999999999999JXBNB9D99A99999999B99999999NZDRXHFP9IZKBVYJZJNHWY9FRGWGSQYKKUKQH9WQYFZQZQXXGRDHK9SERDJDAJZZGIFCXBZWW9AHEFWXXUPD9WQHOIKHMWEUVQXLLXSWSIHRCYIYZZJEGJHYZTAPCVPVCXOVGOJDLFAKTXZPCILOBYVAPIHDEZ9999PVIRULMUXTMVTSVCEXAOTTOIVBMLTPUSXHVIDDARWF9FHZFCLDDTXWVYBHBGIBITIXVMWRUTDFRG99999999999999999999999999999999CHVLCJHME999999999K99999999POWSRVIO9999999UEMENNNNNNNT";
        int[] txTritsInt = Converter.trits(txTrytes);
        byte[] txTrits = new byte[txTritsInt.length];
        for (int i = 0; i < txTritsInt.length; i++) {
            txTrits[i] = (byte) txTritsInt[i];
        }
        if (args.length > 0) {
            runSlowCurl(txTrits);
            return;
        }
        runBatchedBCTCurl(txTrits);
    }

    public static void printHash(byte[] trits) {
        int[] intArray = new int[trits.length];
        for (int i = 0; i < trits.length; i++) {
            intArray[i] = (int) trits[i];
        }
        System.out.println(Converter.trytes(intArray));
    }

    public static void printHashesPerSecond() throws InterruptedException {
        int last = 0;
        for (; ; ) {
            Thread.sleep(TimeUnit.SECONDS.toMillis(1));
            int hashed = count.get();
            System.out.printf("hashed %d (%d/s)\r", hashed, hashed - last);
            last = hashed;
        }
    }

    public static void runBatchedBCTCurl(byte[] txTrits) throws InterruptedException {
        HashReq req = new HashReq();
        req.input = txTrits;
        req.callback = trits -> {
            count.incrementAndGet();
        };

        BatchHasher[] hashers = new BatchHasher[HASHERS_COUNT];
        for (int i = 0; i < HASHERS_COUNT; i++) {
            hashers[i] = new BatchHasher(HASH_LENGTH_TRITS, NUMBER_OF_ROUNDS);
        }

        ExecutorService feedThreadsPool = Executors.newFixedThreadPool(HASHERS_COUNT);
        for (int i = 0; i < HASHERS_COUNT; i++) {
            final int id = i;
            feedThreadsPool.submit(() -> {
                try {
                    for (; ; ) {
                        hashers[id].hash(req);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

        ExecutorService hashersPool = Executors.newFixedThreadPool(HASHERS_COUNT);
        for (int i = 0; i < HASHERS_COUNT; i++) {
            final int id = i;
            hashersPool.submit(() -> {
                try {
                    hashers[id].runDispatcher();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

        printHashesPerSecond();
    }

    public static void runSlowCurl(byte[] txTrits) throws InterruptedException {
        byte[] hashTrits = new byte[Curl.HASH_LENGTH];
        Thread curlThread = new Thread(() -> {
            for (; ; ) {
                Curl curl = new Curl();
                curl.absorb(txTrits, 0, txTrits.length);
                curl.squeeze(hashTrits, 0, Curl.HASH_LENGTH);
                count.incrementAndGet();
            }
        });
        curlThread.start();

        printHashesPerSecond();
    }
}
