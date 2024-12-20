public class Main {

    public static void main ( final String[] args ) {
        final CorrelationAttack attack = new CorrelationAttack();
        final int[] array = attack.calculateLFSR1( new int[] { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 } );

        final double pstar = attack.getBestPstar1();
        System.out.print( 0.5 - pstar );

    }
}
