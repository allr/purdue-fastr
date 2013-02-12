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

    @Test
    public void testFasta2() {
        assertShootout("fasta", "fasta-2", 10,
">ONE Homo sapiens alu\n" +
"GGCCGGGCGCGGTGGCTCAC\n" +
">TWO IUB ambiguity codes\n" +
"cttBtatcatatgctaKggNcataaaSatg\n" +
">THREE Homo sapiens frequency\n" +
"taaatcttgtgcttcgttagaagtctcgactacgtgtagcctagtgtttg\n",
                        "", "NULL");
    }

    @Test
    public void testFasta3() {
        assertShootout("fasta", "fasta-3", 10,
">ONE Homo sapiens alu\n" +
"GGCCGGGCGCGGTGGCTCAC\n" +
">TWO IUB ambiguity codes\n" +
"cttBtatcatatgctaKggNcataaaSatg\n" +
">THREE Homo sapiens frequency\n" +
"taaatcttgtgcttcgttagaagtctcgactacgtgtagcctagtgtttg\n",
                        "", "NULL");
    }

    @Test
    public void testFastaNaive() {
        assertShootout("fasta", "fasta-naive", 10,
">ONE Homo sapiens alu\n" +
"GGCCGGGCGCGGTGGCTCAC\n" +
">TWO IUB ambiguity codes\n" +
"cttBtatcatatgctaKggNcataaaSatg\n" +
">THREE Homo sapiens frequency\n" +
"taaatcttgtgcttcgttagaagtctcgactacgtgtagcctagtgtttg\n",
                        "", "NULL");
    }

    @Test
    public void testFastaNaive2() {
        assertShootout("fasta", "fasta-naive2", 10,
">ONE Homo sapiens alu\n" +
"GGCCGGGCGCGGTGGCTCAC\n" +
">TWO IUB ambiguity codes\n" +
"cttBtatcatatgctaKggNcataaaSatg\n" +
">THREE Homo sapiens frequency\n" +
"taaatcttgtgcttcgttagaagtctcgactacgtgtagcctagtgtttg\n",
                        "", "NULL");
    }

}
