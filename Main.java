import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main ( final String[] args ) {
        final CorrelationAttack attack = new CorrelationAttack();

        // // Testing LFSR1
        // final double pStar1 = attack.getBestPstar( 13, new int[] { 1, 2, 4,
        // 6, 7, 10, 11, 13 } );
        // System.out.println( "Best p* for LFSR1: " + pStar1 );
        //
        // // Testing LFSR2
        // final double pStar2 = attack.getBestPstar( 15, new int[] { 2, 4, 6,
        // 7, 10, 11, 13, 15 } );
        // System.out.println( "Best p* for LFSR2: " + pStar2 );
        //
        // // Testing LFSR3
        // final double pStar3 = attack.getBestPstar( 17, new int[] { 2, 4, 5,
        // 8, 10, 13, 16, 17 } );
        // System.out.println( "Best p* for LFSR3: " + pStar3 );
        //
        // int[] attackResult = attack.getKeyStream();
        // double accuracy = attack.calculateAccuracy(attackResult);

        final List<Integer> LFSR = List.of( 1, 2, 4, 6, 7, 10, 11, 13 );
        final List<Integer> keystream = new ArrayList<>();
        for ( final int i : attack.getKeystream() ) {
            keystream.add( i );
        }
        final int N = keystream.size();
        final double[] maxP = new double[1];

        final List<Integer> lfsr1 = attack.closestHamming( LFSR, keystream, N, maxP );
        final List<Integer> lfsr2 = attack.closestHamming( List.of( 2, 4, 6, 7, 10, 11, 13, 15 ), keystream, N, maxP );
        final List<Integer> lfsr3 = attack.closestHamming( List.of( 2, 4, 5, 8, 10, 13, 16, 17 ), keystream, N, maxP );

        final int[] result = new int[keystream.size()];
        for ( int i = 0; i < keystream.size(); i++ ) {
            final int sum = lfsr1.get( i ) + lfsr2.get( i ) + lfsr3.get( i );
            if ( sum > 1 ) {
                result[i] = 1;
            }
            else {
                result[i] = 0;
            }
        }

        System.out.println( Arrays.toString( result ) );
    }
}
