package r.shootout.fasta;

import org.junit.*;

import r.shootout.*;

public class TestFasta extends ShootoutTestBase {
    @Test
    public void testFasta() {
        assertShootout("fasta", "fasta", 10,
">ONE Homo sapiens alu\n" +
"GGCCGGGCGCGGTGGCTCAC\n" +
">TWO IUB ambiguity codes\n" +
"cttBtatcatatgctaKggNcataaaSatg\n" +
">THREE Homo sapiens frequency\n" +
"taaatcttgtgcttcgttagaagtctcgactacgtgtagcctagtgtttg\n",
                        "", "NULL");
    }
}
