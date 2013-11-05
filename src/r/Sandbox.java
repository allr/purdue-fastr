package r;

import r.fusion.*;

/**
 * Created with IntelliJ IDEA. User: Peta Date: 11/4/13 Time: 4:12 PM To change this template use File | Settings | File
 * Templates.
 */
public class Sandbox {

    static String code = "spectralnorm_alt <- function(args) {\n" +
            "    time = proc.time()[[3]]\n" +
            "    n = if (length(args)) as.integer(args[[1]]) else 100L\n" +
            "    options(digits=10)\n" +
            "\n" +
            "    eval_A <- function(i, j) 1 / ((i + j - 2) * (i + j - 1) / 2 + i)\n" +
            "    eval_A_times_u <- function(u) u %*% g_eval_A_mat\n" +
            "    eval_At_times_u <- function(u) u %*% g_eval_At_mat\n" +
            "    eval_AtA_times_u <- function(u) eval_At_times_u(eval_A_times_u(u))\n" +
            "\n" +
            "    g_eval_A_mat <- outer(seq(n), seq(n), FUN=eval_A)\n" +
            "    g_eval_At_mat <- t(g_eval_A_mat)\n" +
            "    u <- rep(1, n)\n" +
            "    v <- rep(0, n)\n" +
            "    for (itr in seq(10)) {\n" +
            "        v <- eval_AtA_times_u(u)\n" +
            "        u <- eval_AtA_times_u(v)\n" +
            "    }\n" +
            "\n" +
            "    cat(sqrt(sum(u * v) / sum(v * v)), \"\\n\")\n" +
            "    proc.time()[[3]] - time\n" +
            "}\n" +
            "\n" +
            "spectralnorm_alt(2600L)\n" +
            "cat(\"__TIMER__\",spectralnorm_alt(2600L),\"tmr\\n\")\n";

    static String simpleCode = "f <- function() {\n" +
            "  time = proc.time()[[3]]\n" +
            "  for (i in 1:10000) a = a + b * c + b * d - b * d\n" +
            "  proc.time()[[3]] - time\n" +
            "}\n" +
            "\n" +
            "a = rep(1, 10000)\n" +
            "b = rep(1, 10000)\n" +
            "c = rep(1, 10000)\n" +
            "d = rep(1, 10000)\n" +
            "f()\n" +
            "time = 0\n" +
            "for (i in 1:10) time = time + f()\n" +
            "time = time / 10\n" +
            "cat(time)\n";




    public static void fusion() {
        String signature = "B+VDE_VDB*VDE_VDB-VDA_VI_SI";
        FusionBuilder fb = new FusionBuilder(signature);
        System.out.println(fb);
        System.out.println(fb.buildMaterializeMethod());

    }

    public static void main(String[] args) {
        fusion();
        /*
        System.out.println("Executing sandbox...");
        ASTNode tree = RContext.parseFile(new ANTLRStringStream(simpleCode));
        if (tree != null) {
            RAny result = RContext.eval(tree);
            //System.out.println(result.pretty());
        } */

    }
}
