public class Main {

    public static void main ( final String[] args ) {
        final CorrelationAttack attack = new CorrelationAttack();
        final int[] array = attack.calculateLFSR1( new int[] { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 } );

        final int[] pstar = attack.getBestPstar1();
        for ( final int i : pstar ) {
            System.out.printf( "%d ", i );
        }

    }
}
