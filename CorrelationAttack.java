import java.util.ArrayList;
import java.util.List;

public class CorrelationAttack {
    private final int[] keystream;

    public CorrelationAttack () {
        this.keystream = convertStringToArray(
                "0111001010001100111111110011000100001100110100000111001111010011101000011110110001111111011110100011001101010011111111101011101100011101110100010111001101100110100101100001100001101110101111011" );
    }

    /**
     * Helper method to convert keystream string to an array.
     *
     * @param binaryString
     * @return
     */
    private static int[] convertStringToArray ( final String binaryString ) {
        final int[] binaryArray = new int[binaryString.length()];
        for ( int i = 0; i < binaryString.length(); i++ ) {

            binaryArray[i] = Character.getNumericValue( binaryString.charAt( i ) );
        }
        return binaryArray;
    }

    /**
     * Method to check eo
     *
     * @param generatedKeystream
     * @return
     */
    public double calculateAccuracy ( final int[] generatedKeystream ) {
        int correctBits = 0;

        for ( int i = 0; i < keystream.length; i++ ) {
            if ( generatedKeystream[i] == keystream[i] ) {
                correctBits++;
            }
        }

        return (double) correctBits / keystream.length * 100;
    }

    public static List<Integer> getBestState ( final List<Integer> LFSR, final List<Integer> keystream, final int N ) {
        List<Integer> key = new ArrayList<>( N );
        int bestDistance = N; // Maximum possible distance
        double maxP = 0.0; // Initialize correlation value

        for ( int i = 1; i < ( 1 << LFSR.get( LFSR.size() - 1 ) ); ++i ) {
            final List<Integer> possibleState = toBinary( i, LFSR.get( LFSR.size() - 1 ) );

            // Generate the LFSR sequence
            generateLFSRSequence( LFSR, possibleState, N );

            // Calculate Hamming distance
            final int hammingDistance = getHammingDistance( possibleState, keystream );

            // Calculate correlation
            final double correlation = 1.0 - (double) hammingDistance / N;

            if ( hammingDistance < bestDistance ) {
                bestDistance = hammingDistance;
                key = new ArrayList<>( possibleState );
                maxP = correlation;
            }

        }
        System.out.printf( "\nMax Probability: %f", maxP );
        return key;
    }

    //
    public static List<Integer> toBinary ( final int n, final int size ) {
        final List<Integer> binary = new ArrayList<>();
        for ( int i = size - 1; i >= 0; --i ) {
            binary.add( ( n >> i ) & 1 );
        }
        return binary;
    }

    // Generate the LFSR sequence
    public static void generateLFSRSequence ( final List<Integer> LFSR, final List<Integer> test, final int N ) {

        for ( int j = 0; j < N; ++j ) {
            if ( j >= LFSR.get( LFSR.size() - 1 ) ) {
                int next = 0;
                for ( int k = 0; k < LFSR.size(); ++k ) {
                    next ^= test.get( j - LFSR.get( k ) );
                }
                test.add( next );
            }
        }
    }

    // Calculate Hamming distance between two sequences
    public static int getHammingDistance ( final List<Integer> possibleInitialState, final List<Integer> keystream ) {
        int hammingDistance = 0;
        for ( int i = 0; i < keystream.size(); ++i ) {
            if ( !possibleInitialState.get( i ).equals( keystream.get( i ) ) ) {
                ++hammingDistance;
            }
        }
        return hammingDistance;
    }

    public int[] getKeystream () {
        return this.keystream;
    }
}
