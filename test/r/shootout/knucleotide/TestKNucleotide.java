package r.shootout.knucleotide;

import java.io.*;

import org.junit.*;

import r.shootout.*;


public class TestKNucleotide extends ShootoutTestBase {
    @Test
    public void testKNucleotide() {

        String sourceFile = sourceFilePath("fastaredux", "fastaredux");
        String fileToRun = prepareSource(sourceFile, 100, "fastaredux");
        RunResult result = run(fileToRun);
        String inputFile = ".tmp.knucleotide.input";
        try {
            PrintWriter out = new PrintWriter(inputFile);
            out.print(result.output);
            out.close();
        } catch (IOException e) {
            Assert.fail("I/O error while creating input for knucleotide: " + e.toString());
        }
        assertShootout("knucleotide", "knucleotide", "knucleotide", inputFile, null /* TODO - make this work and add correct output */,
                        null, "NULL");
    }
}
