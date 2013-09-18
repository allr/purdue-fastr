package r.benchmark25.test;

import java.io.*;

import r.*;

public class B25TestBase extends FileTestBase {

    public static void assertB25(String benchFileBase, String expectedResult) {
        assertB25(benchFileBase, "", "", expectedResult);  // empty output, empty error output
    }

    public static void assertB25(String benchFileBase, String expectedOutput, String expectedErrorOutput, String expectedResult) {
        if (RContext.hasGNUR()) {
            String sourceFile = sourceFilePath(benchFileBase);
            assertRun(sourceFile, new String[] {}, expectedOutput, expectedErrorOutput, expectedResult);
        } else {
            System.err.println("NOT running benchmark25 " + benchFileBase + " as GNUR is not available.");
        }
    }

    public static String sourceFilePath(String benchFileBase) {
        String prefix = "";
        return prefix + "test" + File.separator + "r" + File.separator + "benchmark25" + File.separator + "test" + File.separator + benchFileBase + ".r";
    }
}
