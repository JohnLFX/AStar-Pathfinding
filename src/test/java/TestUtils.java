import edu.usf.myweb.jcameron2.Bootstrap;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class TestUtils {

    private TestUtils() {
    }

    public static List<String> executeProgram(byte[] inputData) throws IOException {

        Path tempConnectionsFile = Files.createTempFile("connections", ".txt");
        Path tempLocationsFile = Files.createTempFile("locations", ".txt");

        try (
                InputStream inCon = TestUtils.class.getResourceAsStream("/connections.txt");
                InputStream inLoc = TestUtils.class.getResourceAsStream("/locations.txt")
        ) {

            Files.copy(inCon, tempConnectionsFile, StandardCopyOption.REPLACE_EXISTING);
            Files.copy(inLoc, tempLocationsFile, StandardCopyOption.REPLACE_EXISTING);

        }

        String[] programArguments = new String[]{
                tempLocationsFile.toAbsolutePath().toString(),
                tempConnectionsFile.toAbsolutePath().toString()
        };

        Path tempFileOut = Files.createTempFile("", "output.txt");

        InputStream stdin = System.in;
        PrintStream stdout = System.out;

        try (InputStream in = new ByteArrayInputStream(inputData);
             PrintStream out = new PrintStream(tempFileOut.toFile())) {

            System.setIn(in);
            System.setOut(out);

            Bootstrap.main(programArguments);

            return Files.readAllLines(tempFileOut);

        } finally {
            System.setIn(stdin);
            System.setOut(stdout);
            Files.deleteIfExists(tempFileOut);

            Files.deleteIfExists(tempConnectionsFile);
            Files.deleteIfExists(tempLocationsFile);
        }

    }

}
