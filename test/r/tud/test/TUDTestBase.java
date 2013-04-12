package r.tud.test;

import java.io.*;

import r.*;

public class TUDTestBase extends FileTestBase {
    public static void assertTUD(String benchFileBase, String expectedResult) {
        assertTUD(benchFileBase, "", "", expectedResult);  // empty output, empty error output
    }

    public static void assertTUD(String benchFileBase, String expectedOutput, String expectedErrorOutput, String expectedResult) {
        if (RContext.hasGNUR()) {  // FIXME: change this if there show up any tests not requiring native R libraries
            String sourceFile = sourceFilePath(benchFileBase);
            assertRun(sourceFile, new String[] {}, expectedOutput, expectedErrorOutput, expectedResult);
        } else {
            System.err.println("NOT running TUD benchmark " + benchFileBase + " as GNUR is not available.");
        }
    }

    public static String sourceFilePath(String benchFileBase) {
//      String prefix = ".." + File.separator + "fastr" + File.separator; // For execution by mx tool from the graal directory
        String prefix = "";
        return prefix + "test" + File.separator + "r" + File.separator + "tud" + File.separator + "test" + File.separator + benchFileBase + ".r";
    }

}
