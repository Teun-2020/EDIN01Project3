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

    public static int hammingDistance ( final int[] x, final int[] y ) {
        int distance = 0;
        for ( int i = 0; i < x.length; i++ ) {
            if ( x[i] != y[i] ) {
                distance++;
            }
        }
        return distance;
    }

    // Function to estimate correlation p*
    public static double calcCorrelation ( final int[] guessedOutput, final int[] keystream ) {
        final int N = keystream.length;
        final int hammingDistance = hammingDistance( guessedOutput, keystream );
        return 1.0 - (double) hammingDistance / N;
    }

}
