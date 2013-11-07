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


    static String spectralnorm = "spectralnorm <- function(args) {\n" +
            "    time = proc.time()[[3]]\n" +
            "    n = if (length(args)) as.integer(args[[1]]) else 100L\n" +
            "    options(digits=10)\n" +
            "\n" +
            "    eval_A <- function(i, j) 1 / ((i + j) * (i + j + 1) / 2 + i + 1)\n" +
            "    eval_A_times_u <- function(u) {\n" +
            "        ret <- double(n)\n" +
            "        for (i in 0:n1) {\n" +
            "            eval_A_col <- double(n)\n" +
            "            for (j in 0:n1)\n" +
            "\t    eval_A_col[[j + 1]] <- eval_A(i, j)\n" +
            "            ret[[i + 1]] <- u %*% eval_A_col\n" +
            "        }\n" +
            "        return(ret)\n" +
            "    }\n" +
            "    eval_At_times_u <- function(u) {\n" +
            "        ret <- double(n)\n" +
            "        for (i in 0:n1) {\n" +
            "            eval_At_col <- double(n)\n" +
            "            for (j in 0:n1)\n" +
            "\t    eval_At_col[[j + 1]] <- eval_A(j, i)\n" +
            "            ret[[i + 1]] <- u %*% eval_At_col\n" +
            "        }\n" +
            "        return(ret)\n" +
            "    }\n" +
            "    eval_AtA_times_u <- function(u) eval_At_times_u(eval_A_times_u(u))\n" +
            "\n" +
            "    n1 <- n - 1\n" +
            "    u <- rep(1, n)\n" +
            "    v <- rep(0, n)\n" +
            "    for (itr in seq(10)) {\n" +
            "        v <- eval_AtA_times_u(u)\n" +
            "        u <- eval_AtA_times_u(v)\n" +
            "    }\n" +
            "\n" +
            "    cat(sqrt(sum(u * v) / sum(v * v)), \"\\n\")\n" +
            "    proc.time()[[3]] - time\n" +
            "}\n";

    static String simpleCode = "f <- function() {\n" +
            "  time = proc.time()[[3]]\n" +
            "  for (i in 1:10000) a = a - b * c + b * d - b * d\n" +
            "  proc.time()[[3]] - time\n" +
            "}\n" +
            "\n" +
            "a = rep(1, 10000)\n" +
            "b = rep(1, 1000)\n" +
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



    public static void debugRun() {
        ASTNode tree = RContext.parseFile(new ANTLRStringStream(spectralnorm));
        RAny result = RContext.eval(tree);
        tree = RContext.parseFile(new ANTLRStringStream("spectralnorm(500L)"));
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

    public static void testRun() {
        ASTNode tree = RContext.parseFile(new ANTLRStringStream(simpleCode));
        RAny result = RContext.eval(tree);

    }



    public static void main(String[] args) {
        System.out.println("Executing sandbox...");
        debugRun();
        //testRun();

    }
}
