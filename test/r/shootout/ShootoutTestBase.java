package r.shootout;

import java.io.*;

import org.junit.*;

import r.*;
import r.Console;

public class ShootoutTestBase extends FileTestBase {

    public static String sourceFilePath(String benchDir, String benchFileBase) {
        String prefix = "";
        return prefix + "test" + File.separator + "r" + File.separator + "shootout" + File.separator + benchDir + File.separator + benchFileBase + ".r";
    }

    // shootouts that take an integer size as input
    public static void assertShootout(String benchDir, String benchFileBase, int size, String expectedOutput, String expectedErrorOutput, String expectedResult) {
        String sourceFile = sourceFilePath(benchDir, benchFileBase);
        assertRun(sourceFile, new String[] {"--args", Integer.toString(size)}, expectedOutput, expectedErrorOutput, expectedResult);
    }

    // shootouts that take an input file name as input
    public static void assertShootout(String benchDir, String benchFileBase, String inputFileName, String expectedOutput, String expectedErrorOutput, String expectedResult) {
        String sourceFile = sourceFilePath(benchDir, benchFileBase);
        assertRun(sourceFile, new String[] {"--args", inputFileName}, expectedOutput, expectedErrorOutput, expectedResult);
    }

    public static void generateFastaOutput(int size, String captureFile) {
        generateOutput("fastaredux", "fastaredux", size, captureFile);
    }

    protected static String prepareSource(String code, int size, String benchName) {
        String lastLine = "run(" + size + "L)";
        return prepareSource(code, lastLine, benchName);
    }

    protected static String prepareSource(String code, String functionName, String inputFileName, String benchName) {
        String lastLine = functionName + "(\"" + inputFileName + "\")";
        return prepareSource(code, lastLine, benchName);
    }

    private static String prepareSource(String code, String lastLine, String benchName) {
        try {
            BufferedReader in = new BufferedReader(new FileReader(code));
            String tmp = ".tmp.unit." + benchName + ".torun.r";
            PrintWriter out = new PrintWriter(new FileWriter(tmp));

            String line;
            while ((line = in.readLine()) != null) {
                out.println(line);
            }
            out.println(lastLine);
            out.close();
            in.close();
            return tmp;
        } catch (FileNotFoundException e) {
            Assert.fail("Cannot open shootout source file " + code + ":" + e.toString());
        } catch (IOException e) {
            Assert.fail("Error creating run file for " + code + "(" + benchName + "):" + e.toString());
        }
        return null;
    }

    public static void generateOutput(String benchDir, String benchFileBase, int size, String captureFile) {
        String sourceFile = sourceFilePath(benchDir, benchFileBase);
        Console.storeCommandLineArguments(new String[] {"--args", Integer.toString(size)});
        captureOutputToFile(sourceFile, captureFile);
    }

}
