package r;

import org.antlr.runtime.ANTLRStringStream;
import r.data.RAny;
import r.fusion.*;
import r.nodes.ast.ASTNode;

import java.util.Vector;

/**
 * Created with IntelliJ IDEA. User: Peta Date: 11/4/13 Time: 4:12 PM To change this template use File | Settings | File
 * Templates.
 */
public class Sandbox {


    static String fannkuchRedux = "#! fastaredux\n" +
            "#!#g size = (2L # 5L # 10L # 20L # 50L # 100L)\n" +
            "#!g size = (500000L)\n" +
            "#!g measurements = (10L)\n" +
            "# ------------------------------------------------------------------\n" +
            "# The Computer Language Shootout\n" +
            "# http://shootout.alioth.debian.org/\n" +
            "#\n" +
            "# Contributed by Leo Osvald\n" +
            "# ------------------------------------------------------------------\n" +
            "width = 60L\n" +
            "lookup_size = 4096L\n" +
            "lookup_scale = as.double(lookup_size - 1L)\n" +
            "\n" +
            "alu = paste(\n" +
            "    \"GGCCGGGCGCGGTGGCTCACGCCTGTAATCCCAGCACTTTGG\",\n" +
            "    \"GAGGCCGAGGCGGGCGGATCACCTGAGGTCAGGAGTTCGAGA\",\n" +
            "    \"CCAGCCTGGCCAACATGGTGAAACCCCGTCTCTACTAAAAAT\",\n" +
            "    \"ACAAAAATTAGCCGGGCGTGGTGGCGCGCGCCTGTAATCCCA\",\n" +
            "    \"GCTACTCGGGAGGCTGAGGCAGGAGAATCGCTTGAACCCGGG\",\n" +
            "    \"AGGCGGAGGTTGCAGTGAGCCGAGATCGCGCCACTGCACTCC\",\n" +
            "    \"AGCCTGGGCGACAGAGCGAGACTCCGTCTCAAAAA\",\n" +
            "    sep=\"\", collapse=\"\")\n" +
            "\n" +
            "iub = matrix(c(\n" +
            "    c(0.27, 'a'),\n" +
            "    c(0.12, 'c'),\n" +
            "    c(0.12, 'g'),\n" +
            "    c(0.27, 't'),\n" +
            "    c(0.02, 'B'),\n" +
            "    c(0.02, 'D'),\n" +
            "    c(0.02, 'H'),\n" +
            "    c(0.02, 'K'),\n" +
            "    c(0.02, 'M'),\n" +
            "    c(0.02, 'N'),\n" +
            "    c(0.02, 'R'),\n" +
            "    c(0.02, 'S'),\n" +
            "    c(0.02, 'V'),\n" +
            "    c(0.02, 'W'),\n" +
            "    c(0.02, 'Y')\n" +
            "), 2)\n" +
            "\n" +
            "homosapiens = matrix(c(\n" +
            "    c(0.3029549426680, 'a'),\n" +
            "    c(0.1979883004921, 'c'),\n" +
            "    c(0.1975473066391, 'g'),\n" +
            "    c(0.3015094502008, 't')\n" +
            "), 2)\n" +
            "\n" +
            "random <- 42L\n" +
            "random_next_lookup <- function() {\n" +
            "    random <<- (random * 3877L + 29573L) %% 139968L\n" +
            "    return(random * (lookup_scale / 139968))  # TODO\n" +
            "}\n" +
            "\n" +
            "repeat_fasta <- function(s, count) {\n" +
            "    chars = strsplit(s, split=\"\")[[1]]\n" +
            "    len = nchar(s)\n" +
            "    s2 = c(chars, chars[1:width])\n" +
            "    pos <- 1L\n" +
            "    while (count) {\n" +
            "\tline = min(width, count)\n" +
            "        next_pos <- pos + line\n" +
            "        s2[pos:(next_pos - 1)]\n" +
            "        pos <- next_pos\n" +
            "        if (pos > len) pos <- pos - len\n" +
            "\tcount <- count - line\n" +
            "    }\n" +
            "}\n" +
            "\n" +
            "random_fasta <- function(genelist, count) {\n" +
            "    n = ncol(genelist)\n" +
            "    lookup <- integer(lookup_size)\n" +
            "    cprob_lookup <- cumsum(genelist[1, ]) * lookup_scale\n" +
            "    cprob_lookup[[n]] <- lookup_size - 1\n" +
            "\n" +
            "    j <- 1L\n" +
            "    for (i in 1:lookup_size) {\n" +
            "        while (cprob_lookup[[j]] + 1L < i)\n" +
            "            j <- j + 1L\n" +
            "        lookup[[i]] <- j\n" +
            "    }\n" +
            "\n" +
            "    while (count) {\n" +
            "\tline <- min(width, count)\n" +
            "        \n" +
            "        rs <- double(line)\n" +
            "        for (i in 1:line)\n" +
            "          rs[[i]] <- random_next_lookup()\n" +
            "\n" +
            "        inds <- lookup[rs + 1L]\n" +
            "        missed <- which(cprob_lookup[inds] < rs)\n" +
            "        if (length(missed))\n" +
            "            repeat {\n" +
            "                inds[missed] <- inds[missed] + 1L\n" +
            "                missed <- which(cprob_lookup[inds] < rs)\n" +
            "                if (!length(missed))\n" +
            "                    break\n" +
            "            }\n" +
            "\n" +
            "        paste(genelist[2, inds], collapse=\"\", sep=\"\")\n" +
            "\tcount <- count - line\n" +
            "    }\n" +
            "\n" +
            "}\n" +
            "\n" +
            "fastaredux <- function(args) {\n" +
            "    time = proc.time()[[3]]\n" +
            "    n = if (length(args)) as.integer(args[[1]]) else 1000L\n" +
            "    #cat(\">ONE Homo sapiens alu\\n\")\n" +
            "    repeat_fasta(alu, 2 * n)\n" +
            "    #cat(\">TWO IUB ambiguity codes\\n\")\n" +
            "    random_fasta(iub, 3L * n)\n" +
            "    #cat(\">THREE Homo sapiens frequency\\n\")\n" +
            "    random_fasta(homosapiens, 5L * n)\n" +
            "    proc.time()[[3]] - time\n" +
            "}\n";

    static String simpleCode = "f <- function() {\n" +
            "  time = proc.time()[[3]]\n" +
            "  for (i in 1:10000) a = a - b * c + b * d - b * d\n" +
            "  proc.time()[[3]] - time\n" +
            "}\n" +
            "\n" +
            "a = rep(1, 1000)\n" +
            "b = rep(1, 10000)\n" +
            "c = rep(1, 10000)\n" +
            "d = 4; #rep(1, 1000)\n" +
            "f()\n" +
            "time = 0\n" +
            "for (i in 1:10) time = time + f()\n" +
            "time = time / 10\n" +
            "cat(time)\n";


    static void benchmark(String name, String code, String size) {
        code = code.replaceAll("@size", size);
        System.out.println(name+"...");
        ASTNode tree = RContext.parseFile(new ANTLRStringStream(code));
        if (tree != null) {
            RAny result = RContext.eval(tree);
        }
    }





    public static void main(String[] args) {

        System.out.println("Executing sandbox...");
        ASTNode tree = RContext.parseFile(new ANTLRStringStream(fannkuchRedux));
        RAny result = RContext.eval(tree);
        tree = RContext.parseFile(new ANTLRStringStream("fastaredux(500000L)"));
        System.out.print("Warmup");
        for (int i = 0; i < 2; ++i) {
            RContext.eval(tree);
            System.out.print(".");
        }
        System.out.println("");
        double ttime = 0;
        for (int i = 0; i < 10; ++i) {
            long t = System.nanoTime();
            RContext.eval(tree);
            t = System.nanoTime() - t;
            double tt = t / 1000000.0;
            System.out.println("Measured iteration "+i+": "+tt+"[ms]");
            ttime += tt;
            System.out.println(Fusion.statistics());
            Fusion.clearStatistics();
        }
        System.out.println("\n\nAverage time: " + (ttime / 10) +" [ms]");

    }
}
