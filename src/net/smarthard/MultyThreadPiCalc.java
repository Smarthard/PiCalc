package net.smarthard;

import java.math.BigDecimal;

class MultyThreadPiCalc {

    static Integer MAX_K = 1000;

    Integer k = 0;

    BigDecimal pi = new BigDecimal(0);

    /**
     * Realization of <a href="https://en.wikipedia.org/wiki/Bailey%E2%80%93Borwein%E2%80%93Plouffe_formula">this</a>
     * Pi calculation formula.
     *
     * @param k     - is an index of one Pi's sum series to calculate
     * @param scale - BigDecimals' scale for calculations
     * @return one Pi's sum series
     */
    static BigDecimal sumPi(int k, int scale) {
        final BigDecimal BIG_ONE = BigDecimal.ONE.setScale(scale, BigDecimal.ROUND_HALF_UP);
        final BigDecimal BIG_TWO = new BigDecimal(2).setScale(scale, BigDecimal.ROUND_HALF_UP);
        final BigDecimal BIG_FOUR = new BigDecimal(4).setScale(scale, BigDecimal.ROUND_HALF_UP);
        final BigDecimal BIG_FIVE = new BigDecimal(5).setScale(scale, BigDecimal.ROUND_HALF_UP);
        final BigDecimal BIG_SIX = new BigDecimal(6).setScale(scale, BigDecimal.ROUND_HALF_UP);
        final BigDecimal BIG_EIGHT = new BigDecimal(8).setScale(scale, BigDecimal.ROUND_HALF_UP);

        final BigDecimal BIG_K = new BigDecimal(k).setScale(scale, BigDecimal.ROUND_HALF_UP);
        final BigDecimal BIG_EIGHT_MUL_K = BIG_EIGHT.multiply(BIG_K).setScale(scale, BigDecimal.ROUND_HALF_UP);
        final BigDecimal BIG_SIXTEEN_IN_K = new BigDecimal(16).pow(k).setScale(scale, BigDecimal.ROUND_HALF_UP);

        BigDecimal sumK = BIG_ONE.divide(BIG_SIXTEEN_IN_K, BigDecimal.ROUND_HALF_UP);

        BigDecimal secondPart = BIG_FOUR.divide(BIG_EIGHT_MUL_K.add(BIG_ONE), BigDecimal.ROUND_HALF_UP);
        secondPart = secondPart.subtract(BIG_TWO.divide(BIG_EIGHT_MUL_K.add(BIG_FOUR), BigDecimal.ROUND_HALF_UP));
        secondPart = secondPart.subtract(BIG_ONE.divide(BIG_EIGHT_MUL_K.add(BIG_FIVE), BigDecimal.ROUND_HALF_UP));
        secondPart = secondPart.subtract(BIG_ONE.divide(BIG_EIGHT_MUL_K.add(BIG_SIX), BigDecimal.ROUND_HALF_UP));

        return sumK.multiply(secondPart);
    }
}
