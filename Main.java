public class Main {

    public static void main ( final String[] args ) {
        final CorrelationAttack attack = new CorrelationAttack();

        // Testing LFSR1
        double pStar1 = attack.getBestPstar(13, new int[]{1, 2, 4, 6, 7, 10, 11, 13});
        System.out.println("Best p* for LFSR1: " + pStar1);

        // Testing LFSR2
        double pStar2 = attack.getBestPstar(15, new int[]{2, 4, 6, 7, 10, 11, 13, 15});
        System.out.println("Best p* for LFSR2: " + pStar2);

        // Testing LFSR3
        double pStar3 = attack.getBestPstar(17, new int[]{2, 4, 5, 8, 10, 13, 16, 17});
        System.out.println("Best p* for LFSR3: " + pStar3);
    }
}