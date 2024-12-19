import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CorrelationAttack {
    String keystream;

    public CorrelationAttack () {
        final String filePath = "unique_sequence.txt"; // Replace with the path
                                                       // to your file
        try {
            final String fileContent = new String( Files.readAllBytes( Paths.get( filePath ) ) );
            keystream = fileContent;
        }
        catch ( final IOException e ) {
            System.out.println( "An error occurred while reading the file." );
            e.printStackTrace();
        }
        // secret key = inital state of each of the 3 lsfrs, can write the key
        // as a 3 tuple
    }

    public int hammingDistance ( final int[] x, final int[] y ) {
        int distance = 0;
        for ( int i = 0; i < x.length; i++ ) {
            if ( x[i] != y[i] ) {
                distance++;
            }
        }
        return distance;
    }

    public static double calculateCorrelation ( final int[] guessedKeystream, final int[] actualKeystream ) {
        if ( guessedKeystream.length != actualKeystream.length ) {
            throw new IllegalArgumentException( "Keystreams must have the same length." );
        }

        final int N = guessedKeystream.length;
        int hammingDistance = 0;

        // Calculate Hamming distance between the two sequences
        for ( int i = 0; i < N; i++ ) {
            if ( guessedKeystream[i] != actualKeystream[i] ) {
                hammingDistance++;
            }
        }

        // Compute the correlation p*
        return 1.0 - (double) hammingDistance / N;
    }

}
