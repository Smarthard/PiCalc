package net.smarthard;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.*;

public class PiCalc {

    private List<Thread> threadsList = new ArrayList<>();

    @Parameter(names = {"-w", "--nowarnings"}, description = "Disables warning messages")
    private static boolean isNoWarnings = false;

    @Parameter(names = {"-s", "--scale"}, description = "Count of numbers after dot")
    private static Integer SCALE = 2;

    @Parameter(names = {"-t", "--threads"}, description = "Number of threads to calculate")
    private static Integer THREADS = 1;

    private static BigDecimal accuracy = new BigDecimal(1).setScale(SCALE, BigDecimal.ROUND_HALF_UP);

    private static void printWarning(String warningMsg) {
        if (!isNoWarnings) {
            System.out.println("WARNING: " + warningMsg);
        }
    }

    public static void main(String[] args) {
        PiCalc piCalc = new PiCalc();
        MultyThreadPiCalc mtPiCalc = new MultyThreadPiCalc();

        Runnable picalcer = new Runnable() {
            public void run() {

                synchronized (this) {
                    while (mtPiCalc.k < MultyThreadPiCalc.MAX_K) {
//                    if (sumPi(k, SCALE).compareTo(accuracy) <= 0) {
//                        k = Integer.MAX_VALUE;
//                    }
                        mtPiCalc.pi = mtPiCalc.pi.add(MultyThreadPiCalc.sumPi(mtPiCalc.k, SCALE));
                        mtPiCalc.k++;
                    }
                }
            }
        };

        JCommander.newBuilder()
                .addObject(piCalc)
                .build()
                .parse(args);

        for (int i = 0; i < SCALE; i++) {
            accuracy = accuracy.divide(new BigDecimal(10), BigDecimal.ROUND_UP);
        }

        try {
            if (Runtime.getRuntime().availableProcessors() < THREADS) {
                printWarning("Your computer have only " + Runtime.getRuntime().availableProcessors() + " cores");
            }

            for (int i = 0; i < THREADS; i++) {
                piCalc.threadsList.add(new Thread(picalcer));
            }

            piCalc.threadsList.forEach(Thread::start);

            while (mtPiCalc.k < MultyThreadPiCalc.MAX_K - 1) {
                Thread.sleep(0, 1);
            }
        } catch (NumberFormatException | InterruptedException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        System.out.printf("%." + SCALE + "f", mtPiCalc.pi);
    }
}
