package r.shootout.knucleotide;

import org.junit.*;

import r.shootout.*;


public class TestKNucleotide extends ShootoutTestBase {
    @Test
    public void testKNucleotide() {
        String inputFile = ".tmp.unit.knucleotide.input";
        generateFastaOutput(10, inputFile);
        assertShootout("knucleotide", "knucleotide", "knucleotide", inputFile,
                        "t 38\n" +
"g 24\n" +
"a 20\n" +
"c 18 \n" +
"gt 14.285714285714286\n" +
"ct 10.204081632653061\n" +
"ta 10.204081632653061\n" +
"tg 10.204081632653061\n" +
"tt 10.204081632653061\n" +
"ag 8.16326530612245\n" +
"tc 8.16326530612245\n" +
"aa 6.122448979591836\n" +
"cg 6.122448979591836\n" +
"ac 4.081632653061225\n" +
"ga 4.081632653061225\n" +
"gc 4.081632653061225\n" +
"at 2.0408163265306123\n" +
"cc 2.0408163265306123 \n" +
"0\tGGT\t\n" +
"0\tGGTA\t\n" +
"0\tGGTATT\t\n" +
"0\tGGTATTTTAATT\t\n" +
"0\tGGTATTTTAATTTATAGT\t\n",
                        null, "NULL");
    }
}
