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

    public int[] calculateLFSR(int[] initialState, int[] taps, int length) {
        int N = keystream.length; // Length of the sequence to generate
        int[] output = new int[N];
        int[] state = Arrays.copyOf(initialState, length);

        for (int i = 0; i < N; i++) {
            output[i] = state[length - 1]; // Output the current state bit

            int nextBit = 0;
            for (int tap : taps) {
                nextBit ^= state[length - tap];
            }

            System.arraycopy(state, 0, state, 1, length - 1);
            state[0] = nextBit;
        }
        return output;
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

    public double getBestPstar(int length, int[] taps) {
        double maxPStar = 0.5;
        int[] maxInitialState = new int[length];
        List<int[]> possibleStates = generateAllInitialStates(length);

        for (int[] possibleState : possibleStates) {
            int[] lfsr = calculateLFSR(possibleState, taps, length);
            double pstar = calcCorrelation(lfsr);
            if (Math.abs(0.5 - pstar) > Math.abs(0.5 - maxPStar)) {
                maxPStar = pstar;
                maxInitialState = possibleState;
            }
        }
        System.out.println("Best initial state: " + Arrays.toString(maxInitialState));
        return maxPStar;
    }
}