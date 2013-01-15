package r.shootout.regexdna;

import org.junit.*;

import r.shootout.*;

public class TestRegexdna extends ShootoutTestBase {

    @Test
    public void testRegexdna() {
        String inputFile = ".tmp.unit.regexdna.input";
        generateFastaOutput(1000, inputFile);
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
"10245\n" +
"10000\n" +
"13348",
                        null, "NULL");
    }
}
