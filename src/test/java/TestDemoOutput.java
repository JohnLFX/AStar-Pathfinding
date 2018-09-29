import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TestDemoOutput {

    @Test
    public void testOutput1() throws Exception {

        ByteArrayOutputStream inputData = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(inputData);

        writer.println("A1");
        writer.println("G5");
        writer.println();
        writer.println("STRAIGHT_LINE_DISTANCE");
        writer.println("Y");
        writer.println();

        writer.close();

        List<String> lines = TestUtils.executeProgram(inputData.toByteArray());

        assertOutput(lines, TestDemoOutput.class.getResourceAsStream("/demo_output1.txt"), 2);

    }

    @Test
    public void testOutput2() throws Exception {

        ByteArrayOutputStream inputData = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(inputData);

        writer.println("A1");
        writer.println("G5");
        writer.println();
        writer.println("FEWEST_CITIES");
        writer.println("Y");
        writer.println();

        writer.close();

        List<String> lines = TestUtils.executeProgram(inputData.toByteArray());

        System.out.println("--- CONSOLE OUTPUT ---");
        for (String line : lines)
            System.out.println(line);
        System.out.println("-------------");

        // Cannot directly compare to file due to sorting procedure in Astar algorithm

        List<String> expectedOutput;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(TestDemoOutput.class.getResourceAsStream("/demo_output2.txt")))) {
            expectedOutput = br.lines().collect(Collectors.toList());
        }

        int expectedIndex = IntStream.range(0, expectedOutput.size()).filter(i -> expectedOutput.get(i).startsWith("The final solution path is:")).findFirst().orElse(-1);
        int actualIndex = IntStream.range(0, lines.size()).filter(i -> lines.get(i).startsWith("The final solution path is:")).findFirst().orElse(-1);

        for (int i = expectedIndex; i < expectedOutput.size(); i++, actualIndex++) {

            String expected = expectedOutput.get(i).trim();
            String actual = lines.get(actualIndex).trim();

            Assert.assertEquals("Index " + i + " comparison failed", expected, actual);

        }

    }

    @Test
    public void testOutput3() throws Exception {

        ByteArrayOutputStream inputData = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(inputData);

        writer.println("A1");
        writer.println("G5");
        writer.println("C3"); // Exclude C3
        writer.println("STRAIGHT_LINE_DISTANCE");
        writer.println("Y");
        writer.println();

        writer.close();

        List<String> lines = TestUtils.executeProgram(inputData.toByteArray());

        assertOutput(lines, TestDemoOutput.class.getResourceAsStream("/demo_output3.txt"), 3);

    }

    private void assertOutput(List<String> actualOutput, InputStream expectedOutputStream, int offset) throws IOException {

        try (BufferedReader br = new BufferedReader(new InputStreamReader(expectedOutputStream))) {

            for (int i = offset; i < actualOutput.size(); i++) {

                String expectedLine = br.readLine().trim();
                String actualLine = actualOutput.get(i).trim();

                int line = i - 1;

                Assert.assertEquals("Line " + line + " comparison failed", expectedLine, actualLine);

            }

        }

    }

}
