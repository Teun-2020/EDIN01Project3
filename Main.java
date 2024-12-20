import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main ( final String[] args ) {
        final CorrelationAttack attack = new CorrelationAttack();

        final List<Integer> LFSR = List.of( 1, 2, 4, 6, 7, 10, 11, 13 );
        final List<Integer> keystream = new ArrayList<>();
        for ( final int i : attack.getKeystream() ) {
            keystream.add( i );
        }
        final int N = keystream.size();
        final double[] maxP = new double[1];

        System.out.printf( "\nLSFR 1" );
        final List<Integer> lfsr1 = attack.getBestState( LFSR, keystream, N );
        System.out.printf( "\nInitialState: " );
        for ( int i = 0; i < 13 - 1; i++ ) {
            System.out.printf( "%d", lfsr1.get( i ) );
        }
        System.out.print( lfsr1.get( 12 ) );
        System.out.printf( "\n" );
        System.out.printf( "\nLSFR 2" );
        final List<Integer> lfsr2 = attack.getBestState( List.of( 2, 4, 6, 7, 10, 11, 13, 15 ), keystream, N );
        System.out.printf( "\nInitialState: " );
        for ( int i = 0; i < 15 - 1; i++ ) {
            System.out.printf( "%d", lfsr2.get( i ) );
        }
        System.out.print( lfsr2.get( 15 - 1 ) );
        System.out.printf( "\n" );
        System.out.printf( "\nLSFR 3" );
        final List<Integer> lfsr3 = attack.getBestState( List.of( 2, 4, 5, 8, 10, 13, 16, 17 ), keystream, N );
        System.out.printf( "\nInitialState: " );
        for ( int i = 0; i < 17 - 1; i++ ) {
            System.out.printf( "%d", lfsr3.get( i ) );
        }
        System.out.print( lfsr3.get( 17 - 1 ) );
        System.out.printf( "\n" );
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
        System.out.print( "\nInitial Keystream:\n" );
        System.out.print( keystream );
        System.out.print( "\nCalculated Keystream: \n" );
        System.out.println( Arrays.toString( result ) );
        System.out.print( "Accuracy: \n" );
        System.out.println( attack.calculateAccuracy( result ) );

    }
}
