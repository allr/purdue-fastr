package r.shootout.fastaredux;

import org.junit.*;

import r.shootout.*;


public class TestFastaRedux extends ShootoutTestBase {
    @Test
    public void testFastaRedux() {
        assertShootout("fastaredux", "fastaredux", 10, // output same as Fasta
">ONE Homo sapiens alu\n" +
"GGCCGGGCGCGGTGGCTCAC\n" +
">TWO IUB ambiguity codes\n" +
"cttBtatcatatgctaKggNcataaaSatg\n" +
">THREE Homo sapiens frequency\n" +
"taaatcttgtgcttcgttagaagtctcgactacgtgtagcctagtgtttg\n",
                        "", "NULL");
    }
}
