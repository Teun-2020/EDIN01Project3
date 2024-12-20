import java.util.ArrayList;
import java.util.List;

public class CorrelationAttack {
    private final int[] keystream;
    private final int[] lfsr1 = { 0, 1, 3, 5, 6, 9, 10, 12 };

    public CorrelationAttack () {
        this.keystream = convertStringToArray(
                "0111001010001100111111110011000100001100110100000111001111010011101000011110110001111111011110100011001101010011111111101011101100011101110100010111001101100110100101100001100001101110101111011" );

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

    public static int[] convertStringToArray ( final String binaryString ) {
        final int[] binaryArray = new int[binaryString.length()];
        for ( int i = 0; i < binaryString.length(); i++ ) {
            // Convert each character to its numeric value
            binaryArray[i] = Character.getNumericValue( binaryString.charAt( i ) );
        }
        return binaryArray;
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
