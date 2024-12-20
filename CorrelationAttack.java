import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CorrelationAttack {
    private static int[] keystream;
    private final int[]  lfsr1 = { 0, 1, 3, 5, 6, 9, 10, 12 };

    public CorrelationAttack () {
        final String filePath = "unique_sequence.txt"; // Replace with the path
                                                       // to your file
        try {
            final String fileContent = new String( Files.readAllBytes( Paths.get( filePath ) ) );
            this.keystream = convertKeyStreamToArray( fileContent );
        }
        catch ( final IOException e ) {
            System.out.println( "An error occurred while reading the file." );
            e.printStackTrace();
        }

        // final take keystream and final ocnvert it to final a list
        // secret key = inital state of each of the 3 lsfrs, can write the key
        // as a 3 tuple
    }

    public static int hammingDistance ( final int[] x ) {
        int distance = 0;
        final int[] y = keystream;
        for ( int i = 0; i < x.length; i++ ) {
            if ( x[i] != y[i] ) {
                distance++;
            }
        }
        return distance;
    }

    // Function to estimate correlation p*
    public static double calcCorrelation ( final int[] guessedOutput ) {
        final int N = keystream.length;
        final int hammingDistance = hammingDistance( guessedOutput );
        return 1.0 - (double) hammingDistance / N;
    }

    /**
     * Helper method to convert the file contents to an array.
     *
     * @param fileContent
     * @return
     */
    private int[] convertKeyStreamToArray ( final String fileContent ) {
        final int[] array = new int[fileContent.length()];
        for ( int i = 0; i < fileContent.length(); i++ ) {
            array[i] = Character.getNumericValue( fileContent.charAt( i ) );
        }
        return array;
    }

    // need method to calculate lsfr method given initial state
    //

    public int[] calculateLFSR1 ( final int[] initialState ) {
        // length of initital states is L = 13
        final int N = this.keystream.length; // want to eventually be length of
                                             // keystream
        final int L = initialState.length; // Length of the LFSR

        final int[] output = new int[keystream.length]; // To store the output
                                                        // sequence
        final int[] state = Arrays.copyOf( initialState, L ); // Copy the
                                                              // initial state

        for ( int i = 0; i < N; i++ ) {
            // Output the last bit in the state (current bit of the LFSR)
            output[i] = state[L - 1];

            // Calculate the next bit using the feedback positions
            int nextBit = 0;
            for ( final int position : lfsr1 ) {

                nextBit ^= state[L - position - 1]; // XOR the bits at the
                // feedback positions
            }

            // Shift the state to the right and insert the new bit at the front
            System.arraycopy( state, 0, state, 1, L - 1 );
            state[0] = nextBit;
        }
        return output;

    }

    public int[] calculateLFSR2 ( final int[] initialState ) {
        return null;
    }

    public int[] calculateLFSR3 ( final int[] initialState ) {
        return null;
    }

    public static List<int[]> generateAllInitialStates ( final int length ) {
        final int totalStates = (int) Math.pow( 2, length ); // Total possible
                                                             // combinations =
                                                             // 2^length
        final List<int[]> initialStates = new ArrayList<>( totalStates );

        for ( int i = 0; i < totalStates; i++ ) {
            final int[] state = new int[length];
            for ( int j = 0; j < length; j++ ) {
                state[j] = ( i >> ( length - j - 1 ) ) & 1; // Extract each bit
                                                            // from the integer
            }
            initialStates.add( state );
        }
        return initialStates;
    }

    public double getBestPstar1 () {
        double maxPStar = 0.0;
        double minpstar = 1.0;
        int[] maxInitialState = new int[keystream.length];
        final List<int[]> possibleStates = generateAllInitialStates( 13 );
        for ( final int[] possibleState : possibleStates ) {

            final int[] lfsr = calculateLFSR1( possibleState );
            final double pstar = calcCorrelation( lfsr );
            if ( pstar > maxPStar ) {
                maxPStar = pstar;
                maxInitialState = possibleState;
            }
            if ( pstar < minpstar ) {
                minpstar = pstar;
                // maxInitialState = possibleState;
            }
        }
        System.out.println( 0.5 - minpstar );
        return maxPStar;
    }

}
