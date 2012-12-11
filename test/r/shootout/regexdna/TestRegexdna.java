package r.shootout.regexdna;

import java.io.*;

import org.junit.*;

import r.shootout.*;

public class TestRegexdna extends ShootoutTestBase {

    @Test
    public void testRegexdna() {

        String sourceFile = sourceFilePath("fastaredux", "fastaredux");
        String fileToRun = prepareSource(sourceFile, 1000, "fastaredux");
        RunResult result = run(fileToRun);
        String inputFile = ".tmp.regexdna.input";
        try {
            PrintWriter out = new PrintWriter(inputFile);
            out.println(result.output);
            out.close();
        } catch (IOException e) {
            Assert.fail("I/O error while creating input for regexdna: " + e.toString());
        }
        assertShootout("regexdna", "regexdna", "regexdna", inputFile,
"agggtaaa|tttaccct 7495 \n" +
"[cgt]gggtaaa|tttaccc[acg] 0 \n" +
"a[act]ggtaaa|tttacc[agt]t 0 \n" +
"ag[act]gtaaa|tttac[agt]ct 0 \n" +
"agg[act]taaa|ttta[agt]cct 2533 \n" +
"aggg[acg]aaa|ttt[cgt]ccct 0 \n" +
"agggt[cgt]aa|tt[acg]accct 0 \n" +
"agggta[cgt]a|t[acg]taccct 0 \n" +
"agggtaa[cgt]|[acg]ttaccct 2 \n" +
"\n" +
"10246\n" +
"10000\n" +
"13348",
                        null, "NULL");
    }
}
