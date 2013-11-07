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


    static String fannkuchRedux = "fannkuch <- function(n) {\n" +
            "    time = proc.time()[[3]]\n" +
            "    one_two = c(1, 2)\n" +
            "    two_one = c(2, 1)\n" +
            "    two_three = c(2, 3)\n" +
            "    three_two = c(3, 2)\n" +
            "    if (n > 3L)\n" +
            "        rxrange = 3:(n - 1)\n" +
            "    else\n" +
            "        rxrange = integer(0)\n" +
            "\n" +
            "    max_flip_count <- 0L\n" +
            "    perm_sign <- TRUE\n" +
            "    checksum <- 0L\n" +
            "    perm1 <- 1:n\n" +
            "    count <- 0:(n - 1L)\n" +
            "    while (TRUE) {\n" +
            "        if (k <- perm1[[1L]]) {\n" +
            "            perm <- perm1\n" +
            "            flip_count <- 1L\n" +
            "            while ((kk <- perm[[k]]) > 1L) {\n" +
            "                k_range = 1:k\n" +
            "                perm[k_range] <- rev.default(perm[k_range])\n" +
            "                flip_count <- flip_count + 1L\n" +
            "                k <- kk\n" +
            "                kk <- perm[[kk]]\n" +
            "            }\n" +
            "            max_flip_count <- max(max_flip_count, flip_count)\n" +
            "            checksum <- checksum + if (perm_sign) flip_count else -flip_count\n" +
            "        }\n" +
            "\n" +
            "        # Use incremental change to generate another permutation\n" +
            "        if (perm_sign) {\n" +
            "            perm1[one_two] <- perm1[two_one]\n" +
            "            perm_sign = FALSE\n" +
            "        } else {\n" +
            "            perm1[two_three] <- perm1[three_two]\n" +
            "            perm_sign = TRUE\n" +
            "            was_break <- FALSE\n" +
            "            for (r in rxrange) {\n" +
            "                if (count[[r]]) {\n" +
            "                    was_break <- TRUE\n" +
            "                    break\n" +
            "                }\n" +
            "                count[[r]] <- r - 1L\n" +
            "                perm0 <- perm1[[1L]]\n" +
            "                perm1[1:r] <- perm1[2:(r + 1L)]\n" +
            "                perm1[[r + 1L]] <- perm0\n" +
            "            }\n" +
            "            if (!was_break) {\n" +
            "                r <- n\n" +
            "                if (!count[[r]]) {\n" +
            "                    #cat(checksum, \"\\n\", sep=\"\")\n" +
            "                    return(proc.time()[[3]] - time)\n" +
            "                }\n" +
            "            }\n" +
            "            count[[r]] <- count[[r]] - 1L\n" +
            "        }\n" +
            "    }\n" +
            "    proc.time()[[3]] - time\n" +
            "}\n" +
            "\n";
/*            "fannkuch(9)\n" +
            "fannkuch(9)\n" +
            "measurementTime <- 0\n" +
            "for (i in 1:10) {\n" +
            "    measurementTime <- measurementTime + fannkuch(9)\n" +
            "    cat(\".\")\n" +
            "}\n" +
            "cat(\"__TIMER__\",measurementTime / 10,\"tmr\\n\")\n"; */

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
        tree = RContext.parseFile(new ANTLRStringStream("fannkuch(9)"));
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
