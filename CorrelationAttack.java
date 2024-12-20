import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CorrelationAttack {
    private int[]       keystream;
    private final int[] lfsr1 = { 0, 1, 3, 5, 6, 9, 10, 12 };

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

    public int hammingDistance ( final int[] x ) {
        int distance = 0;
        final int[] y = this.keystream;
        for ( int i = 0; i < x.length; i++ ) {
            if ( x[i] != y[i] ) {
                distance++;
            }
        }
        return distance;
    }

    // Function to estimate correlation p*
    public double calcCorrelation ( final int[] guessedOutput ) {
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

        final int[] array2 = new int[fileContent.length() - 3]; // Hardcoded for
                                                                // now
        int amountSkipped = 0;

        for ( int i = 0; i < fileContent.length(); i++ ) {
            if ( array[i] == 0 || array[i] == 1 ) {
                array2[i - amountSkipped] = array[i];
            }
            else {
                amountSkipped++;
            }
        }

        return array2;
    }

    // need method to calculate lsfr method given initial state
    //

    public int[] calculateLFSR ( final int[] initialState, final int[] taps, final int length ) {
        final int N = keystream.length; // Length of the sequence to generate
        final int[] output = new int[N];
        final int[] state = Arrays.copyOf( initialState, length );

        for ( int i = 0; i < N; i++ ) {
            output[i] = state[length - 1]; // Output the current state bit

            int nextBit = 0;
            for ( final int tap : taps ) {
                nextBit ^= state[length - tap];
            }

            System.arraycopy( state, 0, state, 1, length - 1 );
            state[0] = nextBit;
        }
        return output;
    }

    public List<int[]> generateAllInitialStates ( final int length ) {
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

    public double getBestPstar ( final int length, final int[] taps ) {
        double maxPStar = 0.0;
        final List<int[]> possibleStates = generateAllInitialStates( length );

        for ( final int[] possibleState : possibleStates ) {
            final int[] lfsr = calculateLFSR( possibleState, taps, length );
            final double pstar = calcCorrelation( lfsr );
            if ( pstar > maxPStar ) {
                maxPStar = pstar;
            }
        }
        // System.out.println( "Best initial state: " + Arrays.toString(
        // maxInitialState ) );
        return maxPStar;
    }

    public int[] getBestInitialState ( final int length, final int[] taps ) {
        double maxPStar = 0.0;
        int[] maxInitialState = new int[length];
        final List<int[]> possibleStates = generateAllInitialStates( length );

        for ( final int[] possibleState : possibleStates ) {
            final int[] lfsr = calculateLFSR( possibleState, taps, length );
            final double pstar = calcCorrelation( lfsr );
            if ( pstar > maxPStar ) {
                maxPStar = pstar;
                maxInitialState = possibleState;
            }
        }
        // System.out.println( "Best initial state: " + Arrays.toString(
        // maxInitialState ) );
        return maxInitialState;
    }

    public int[] getKeyStream () {
        // Testing LFSR1
        final int[] result = new int[keystream.length];
        final int[] pStar1 = getBestInitialState( 13, new int[] { 1, 2, 4, 6, 7, 10, 11, 13 } );
        // System.out.println( "Best p* for LFSR1: " + pStar1 );

        // Testing LFSR2
        final int[] pStar2 = getBestInitialState( 15, new int[] { 2, 4, 6, 7, 10, 11, 13, 15 } );
        // System.out.println( "Best p* for LFSR2: " + pStar2 );

        // Testing LFSR3
        final int[] pStar3 = getBestInitialState( 17, new int[] { 2, 4, 5, 8, 10, 13, 16, 17 } );
        // System.out.println( "Best p* for LFSR3: " + pStar3 );

        final int[] lfsr1 = calculateLFSR( pStar1, new int[] { 1, 2, 4, 6, 7, 10, 11, 13 }, 13 );
        final int[] lfsr2 = calculateLFSR( pStar2, new int[] { 2, 4, 6, 7, 10, 11, 13, 15 }, 15 );
        final int[] lfsr3 = calculateLFSR( pStar3, new int[] { 2, 4, 5, 8, 10, 13, 16, 17 }, 17 );

        for ( int i = 0; i < keystream.length; i++ ) {
            final int sum = lfsr1[i] + lfsr2[i] + lfsr3[i];
            if ( sum > 1 ) {
                result[i] = 1;
            }
            else {
                result[i] = 0;
            }
        }

        return result;
    }

    public double calculateAccuracy ( final int[] generatedKeystream ) {
        int correctBits = 0;

        for ( int i = 0; i < keystream.length; i++ ) {
            if ( generatedKeystream[i] == keystream[i] ) {
                correctBits++;
            }
        }

        return (double) correctBits / keystream.length * 100;
    }

    public static List<Integer> closestHamming ( final List<Integer> LFSR, final List<Integer> keystream, final int N,
            final double[] maxP ) {
        List<Integer> key = new ArrayList<>( N );
        int bestDistance = N; // Maximum possible distance
        maxP[0] = 0.0; // Initialize correlation value

        for ( int i = 1; i < ( 1 << LFSR.get( LFSR.size() - 1 ) ); ++i ) {
            final List<Integer> test = decToBinary( i, LFSR.get( LFSR.size() - 1 ) );
            int hammingDistance = 0;

            // Generate the sequence using the LFSR
            for ( int j = 0; j < N; ++j ) {
                if ( j >= LFSR.get( LFSR.size() - 1 ) ) {
                    int next = 0;
                    for ( int k = 0; k < LFSR.size(); ++k ) {
                        next ^= test.get( j - LFSR.get( k ) );
                    }
                    test.add( next );
                }
                // Calculate Hamming distance
                if ( !test.get( j ).equals( keystream.get( j ) ) ) {
                    ++hammingDistance;
                }
            }

            // Calculate p*
            final double correlation = 1.0 - (double) hammingDistance / N;

            // Update the best key and correlation
            if ( hammingDistance < bestDistance || i == 1 ) {
                bestDistance = hammingDistance;
                key = new ArrayList<>( test );
                maxP[0] = correlation;
            }
        }
        return key;
    }

    // Helper method to convert a decimal number to binary representation as a
    // List of Integers
    public static List<Integer> decToBinary ( final int n, final int size ) {
        final List<Integer> binary = new ArrayList<>();
        for ( int i = size - 1; i >= 0; --i ) {
            binary.add( ( n >> i ) & 1 );
        }
        return binary;
    }

    public int[] getKeystream () {
        return this.keystream;
    }
}
