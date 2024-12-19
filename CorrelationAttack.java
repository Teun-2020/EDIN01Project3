import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CorrelationAttack {
    String sequence;

    public CorrelationAttack () {
        final String filePath = "unique_sequence.txt"; // Replace with the path
                                                       // to your file
        try {
            final String fileContent = new String( Files.readAllBytes( Paths.get( filePath ) ) );
            System.out.println( fileContent );
        }
        catch ( final IOException e ) {
            System.out.println( "An error occurred while reading the file." );
            e.printStackTrace();
        }
    }

}
