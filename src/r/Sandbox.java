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


    static String mandelbrot = "lim <- 2\n" +
            "iter <- 50\n" +
            "\n" +
            "mandelbrot <- function(args) {\n" +
            "    time = proc.time()[[3]]\n" +
            "    n = if (length(args)) as.integer(args[[1]]) else 200L\n" +
            "    n_mod8 = n %% 8L\n" +
            "    pads <- if (n_mod8) rep.int(0, 8L - n_mod8) else integer(0)\n" +
            "    p <- rep(as.integer(rep.int(2, 8) ^ (7:0)), length.out=n)\n" +
            "\n" +
            "    #cat(\"P4\\n\")\n" +
            "    #cat(n, n, \"\\n\")\n" +
            "    for (y in 0:(n-1)) {\n" +
            "        c <- 2 * 0:(n-1) / n - 1.5 + 1i * (2 * y / n - 1)\n" +
            "        z <- rep(0+0i, n)\n" +
            "        i <- 0L\n" +
            "        while (i < iter) {  # faster than for loop\n" +
            "            z <- z * z + c\n" +
            "            i <- i + 1L\n" +
            "        }\n" +
            "        bits <- as.integer(abs(z) <= lim)\n" +
            "        bytes <- as.raw(colSums(matrix(c(bits * p, pads), 8L)))\n" +
            "#\tcat(bytes,\"\\n\")\n" +
            "    }\n" +
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
        ASTNode tree = RContext.parseFile(new ANTLRStringStream(mandelbrot));
        RAny result = RContext.eval(tree);
        tree = RContext.parseFile(new ANTLRStringStream("mandelbrot(2000L)"));
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
        ASTNode tree = RContext.parseFile(new ANTLRStringStream("{ c(NA,NA,NA)+c(1L,2L,3L) }"));
        RAny result = RContext.eval(tree);
        System.out.println(result.pretty());

    }



    public static void main(String[] args) {
        System.out.println("Executing sandbox...");
        //debugRun();
        testRun();

    }
}
