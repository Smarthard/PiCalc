package net.smarthard;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import com.beust.jcommander.*;

public class PiCalc {

    @Parameter(names = {"-w", "--nowarnings"}, description = "Disables warning messages")
    private static boolean isNoWarnings = false;

    @Parameter(names = {"-s", "--scale"}, description = "Count of numbers after dot")
    private static Integer SCALE = 2;

    @Parameter(names = {"-t", "--threads"}, description = "Number of threads to calculate")
    private static Integer THREADS = 1;

    private static volatile AtomicInteger k = new AtomicInteger(0);

    private static volatile BigDecimal pi = new BigDecimal(0);

    private static void printWarning(String warningMsg) {
        if (!isNoWarnings) {
            System.out.println("WARNING: " + warningMsg);
        }
    }

    private static void printErrorAndExit(Exception e) {
        System.err.println(e.getMessage());
        System.exit(1);
    }

    /**
     * Realization of <a href="https://en.wikipedia.org/wiki/Bailey–Borwein–Plouffe_formula">this</a>
     * (Bailey–Borwein–Plouffe) Pi calculation formula.
     *
     * @param k     - is an index of one Pi's sum series to calculate
     * @param scale - BigDecimals' scale for calculations
     * @return one Pi's sum series
     */
    private static BigDecimal sumPi(int k, int scale) {
        final BigDecimal BIG_ONE = BigDecimal.ONE.setScale(scale + 1, RoundingMode.HALF_UP);
        final BigDecimal BIG_TWO = new BigDecimal(2).setScale(scale + 1, RoundingMode.HALF_UP);
        final BigDecimal BIG_FOUR = new BigDecimal(4).setScale(scale + 1, RoundingMode.HALF_UP);
        final BigDecimal BIG_FIVE = new BigDecimal(5).setScale(scale + 1, RoundingMode.HALF_UP);
        final BigDecimal BIG_SIX = new BigDecimal(6).setScale(scale + 1, RoundingMode.HALF_UP);
        final BigDecimal BIG_EIGHT = new BigDecimal(8).setScale(scale + 1, RoundingMode.HALF_UP);

        final BigDecimal BIG_K = new BigDecimal(k).setScale(scale + 1, RoundingMode.HALF_UP);
        final BigDecimal BIG_EIGHT_MUL_K = BIG_EIGHT.multiply(BIG_K).setScale(scale + 1, RoundingMode.HALF_UP);
        final BigDecimal BIG_SIXTEEN_IN_K = new BigDecimal(16).pow(k).setScale(scale + 1, RoundingMode.HALF_UP);

        BigDecimal sumK = BIG_ONE.divide(BIG_SIXTEEN_IN_K, RoundingMode.HALF_UP);

        BigDecimal secondPart = BIG_FOUR.divide(BIG_EIGHT_MUL_K.add(BIG_ONE), RoundingMode.HALF_UP);
        secondPart = secondPart.subtract(BIG_TWO.divide(BIG_EIGHT_MUL_K.add(BIG_FOUR), RoundingMode.HALF_UP));
        secondPart = secondPart.subtract(BIG_ONE.divide(BIG_EIGHT_MUL_K.add(BIG_FIVE), RoundingMode.HALF_UP));
        secondPart = secondPart.subtract(BIG_ONE.divide(BIG_EIGHT_MUL_K.add(BIG_SIX), RoundingMode.HALF_UP));

        return sumK.multiply(secondPart);
    }

    public static void main(String[] args) {
        PiCalc piCalc = new PiCalc();

        try {
            JCommander.newBuilder()
                    .addObject(piCalc)
                    .build()
                    .parse(args);
        } catch (ParameterException e) {
            printErrorAndExit(e);
        }

        ExecutorService executorService = Executors.newFixedThreadPool(THREADS);
        Callable<BigDecimal> picalcer = () -> sumPi(k.getAndIncrement(), SCALE);

        try {
            int coresCount = Runtime.getRuntime().availableProcessors();
            if (coresCount < THREADS) {
                printWarning("Your computer have only " + coresCount + " cores");
            }
            if (SCALE < 0) {
                printWarning("Scale can not be negative");
                SCALE = 0;
            }

            // accuracy calculation
            BigDecimal accuracy = BigDecimal.ONE.setScale(SCALE, RoundingMode.HALF_UP);
            for (int i = 0; i <= SCALE; i++) {
                accuracy = accuracy.divide(BigDecimal.TEN, i < SCALE ? RoundingMode.HALF_UP : RoundingMode.HALF_UP);
            }

            Future<BigDecimal> future;
            do {
                future = executorService.submit(picalcer);
                synchronized (PiCalc.class) {
                    pi = pi.add(future.get());
                }
            } while (future.get().compareTo(accuracy) > 0);

            executorService.shutdown();
        } catch (NumberFormatException | InterruptedException | ExecutionException e) {
            printErrorAndExit(e);
        }

        System.out.printf("%." + SCALE + "f", pi);
    }
}
