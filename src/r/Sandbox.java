package r;

import org.antlr.runtime.ANTLRStringStream;
import r.data.RAny;
//import r.fusion.Fusion;
import r.fusion.Fusion;
import r.nodes.ast.ASTNode;
import r.shootout.ShootoutTestBase;

//import java.util.Vector;

//import static r.shootout.ShootoutTestBase.generateFastaOutput;

/**
 * Created with IntelliJ IDEA. User: Peta Date: 11/4/13 Time: 4:12 PM To change this template use File | Settings | File
 * Templates.
 */
public class Sandbox {


    static String fasta = "width <- 60L\n" +
            "myrandom_last <- 42L\n" +
            "myrandom <- function(m) {\n" +
            "    myrandom_last <<- (myrandom_last * 3877L + 29573L) %% 139968L\n" +
            "    return(m * myrandom_last / 139968)\n" +
            "}\n" +
            "\n" +
            "alu <- paste(\n" +
            "    \"GGCCGGGCGCGGTGGCTCACGCCTGTAATCCCAGCACTTTGG\",\n" +
            "    \"GAGGCCGAGGCGGGCGGATCACCTGAGGTCAGGAGTTCGAGA\",\n" +
            "    \"CCAGCCTGGCCAACATGGTGAAACCCCGTCTCTACTAAAAAT\",\n" +
            "    \"ACAAAAATTAGCCGGGCGTGGTGGCGCGCGCCTGTAATCCCA\",\n" +
            "    \"GCTACTCGGGAGGCTGAGGCAGGAGAATCGCTTGAACCCGGG\",\n" +
            "    \"AGGCGGAGGTTGCAGTGAGCCGAGATCGCGCCACTGCACTCC\",\n" +
            "    \"AGCCTGGGCGACAGAGCGAGACTCCGTCTCAAAAA\",\n" +
            "    sep=\"\", collapse=\"\")\n" +
            "\n" +
            "iub <- matrix(c(\n" +
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
            "homosapiens <- matrix(c(\n" +
            "    c(0.3029549426680, 'a'),\n" +
            "    c(0.1979883004921, 'c'),\n" +
            "    c(0.1975473066391, 'g'),\n" +
            "    c(0.3015094502008, 't')\n" +
            "), 2)\n" +
            "\n" +
            "repeat_fasta <- function(s, count) {\n" +
            "    chars <- strsplit(s, split=\"\")[[1]]\n" +
            "    len <- nchar(s)\n" +
            "    s2 <- c(chars, chars[1:width])\n" +
            "    pos <- 1L\n" +
            "    while (count) {\n" +
            "\tline <- min(width, count)\n" +
            "        next_pos <- pos + line\n" +
            "        cat(s2[pos:(next_pos - 1)], \"\\n\", sep=\"\")\n" +
            "        pos <- next_pos\n" +
            "        if (pos > len) pos <- pos - len\n" +
            "\tcount <- count - line\n" +
            "    }\n" +
            "}\n" +
            "\n" +
            "random_fasta <- function(genelist, count) {\n" +
            "    psum <- cumsum(genelist[1,])\n" +
            "    while (count) {\n" +
            "\tline <- min(width, count)\n" +
            "        \n" +
            "        rs <- double(line)\n" +
            "        for (i in 1:line)\n" +
            "          rs[[i]] <- myrandom(1)\n" +
            "\n" +
            "\tcat(genelist[2, colSums(outer(psum, rs, \"<\")) + 1], \"\\n\", sep='')\n" +
            "\tcount <- count - line\n" +
            "    }\n" +
            "}\n" +
            "\n" +
            "fasta <- function(args) {\n" +
            "    time = proc.time()[[3]]\n" +
            "    n = if (length(args)) as.integer(args[[1]]) else 1000L\n" +
            "    cat(\">ONE Homo sapiens alu\\n\")\n" +
            "    repeat_fasta(alu, 2 * n)\n" +
            "    cat(\">TWO IUB ambiguity codes\\n\")\n" +
            "    random_fasta(iub, 3L * n)\n" +
            "    cat(\">THREE Homo sapiens frequency\\n\")\n" +
            "    random_fasta(homosapiens, 5L * n)\n" +
            "    proc.time()[[3]] - time\n" +
            "}\n";

    static String mandelbrotNooutNaive = "lim <- 2\n" +
            "iter <- 50\n" +
            "\n" +
            "mandelbrot_noout_naive <- function(args) {\n" +
            "    time = proc.time()[[3]]\n" +
            "    n = if (length(args)) as.integer(args[[1]]) else 200L\n" +
            "    n_mod8 = n %% 8L\n" +
            "    pads <- if (n_mod8) rep.int(0, 8L - n_mod8) else integer(0)\n" +
            "    p <- rep(as.integer(rep.int(2, 8) ^ (7:0)), length.out=n)\n" +
            "\n" +
            "    cat(\"P4\\n\")\n" +
            "    cat(n, n, \"\\n\")\n" +
            "    C <- matrix(0, n, n)\n" +
            "    for (y in 0:(n-1)) {\n" +
            "        C[, y] <- 2 * 0:(n-1) / n - 1.5 + 1i * (2 * y / n - 1)\n" +
            "    }\n" +
            "\n" +
            "    m <- n\n" +
            "    Z <- 0                   # initialize Z to zero\n" +
            "    X <- array(0, c(m,m,20)) # initialize output 3D array\n" +
            "    for (k in 1:20) {        # loop with 20 iterations\n" +
            "        Z <- Z^2+C             # the central difference equation\n" +
            "          X[,,k] <- exp(-abs(Z)) # capture results\n" +
            "    }\n" +
            "    proc.time()[[3]] - time\n" +
            "}\n";

    static String knucleotide_brute2="gen_freq <- function(seq, frame) {\n" +
            "    frame <- frame - 1L\n" +
            "    ns <- nchar(seq) - frame\n" +
            "    n <- 0L\n" +
            "    cap <- 16L\n" +
            "    freqs <- integer(cap)\n" +
            "    for (i in 1:ns) {\n" +
            "        subseq = substr(seq, i, i + frame)\n" +
            "        cnt <- attr(freqs, subseq)\n" +
            "        if (is.null(cnt)) {\n" +
            "            cnt <- 0L\n" +
            "            # ensure O(N) resizing (instead of O(N^2))\n" +
            "            n <- n + 1L\n" +
            "            freqs[[cap <- if (cap < n) 2L * cap else cap]] <- 0L\n" +
            "        }\n" +
            "        attr(freqs, subseq) <- cnt + 1L\n" +
            "    }\n" +
            "    return(freqs)\n" +
            "}\n" +
            "\n" +
            "sort_seq <- function(seq, len) {\n" +
            "    cnt_map <- gen_freq(seq, len)\n" +
            "    #print(cnt_map)\n" +
            "    attrs <- attributes(cnt_map)\n" +
            "    fs <- unlist(attrs, use.names=FALSE)\n" +
            "    seqs <- toupper(paste(names(attrs)))\n" +
            "    inds <- order(-fs, seqs)\n" +
            "    #cat(paste(seqs[inds], fs[inds], collapse=\"\\n\"), \"\\n\")\n" +
            "    #cat(paste.(seqs[inds], 100 * fs[inds] / sum(fs), collapse=\"\\n\", digits=3),\n" +
            "    cat(paste(seqs[inds], 100 * fs[inds] / sum(fs), collapse=\"\\n\"),\n" +
            "        \"\\n\")\n" +
            "}\n" +
            "\n" +
            "find_seq <- function(seq, s) {\n" +
            "    cnt_map <- gen_freq(seq, nchar(s))\n" +
            "    if (!is.null(cnt <- attr(cnt_map, s)))\n" +
            "        return(cnt)\n" +
            "    return(0L)\n" +
            "}\n" +
            "\n" +
            "knucleotide_brute2 <- function(args) {\n" +
            "    in_filename = args[[1]]\n" +
            "    f <- file(in_filename, \"r\")\n" +
            "    while (length(line <- readLines(f, n=1, warn=FALSE))) {\n" +
            "        first_char <- substr(line, 1L, 1L)\n" +
            "        if (first_char == '>' || first_char == ';')\n" +
            "            if (substr(line, 2L, 3L) == 'TH')\n" +
            "                break\n" +
            "    }\n" +
            "\n" +
            "    n <- 0L\n" +
            "    cap <- 8L\n" +
            "    str_buf <- character(cap)\n" +
            "    while (length(line <- scan(f, what=\"\", nmax=1, quiet=TRUE))) {\n" +
            "        first_char <- substr(line, 1L, 1L)\n" +
            "        if (first_char == '>' || first_char == ';')\n" +
            "            break\n" +
            "        n <- n + 1L\n" +
            "\t# ensure O(N) resizing (instead of O(N^2))\n" +
            "        str_buf[[cap <- if (cap < n) 2L * cap else cap]] <- \"\"\n" +
            "        str_buf[[n]] <- line\n" +
            "    }\n" +
            "    length(str_buf) <- n\n" +
            "    close(f)\n" +
            "    seq <- paste(str_buf, collapse=\"\")\n" +
            "\n" +
            "    for (frame in 1:2)\n" +
            "        sort_seq(seq, frame)\n" +
            "    for (s in c(\"GGT\", \"GGTA\", \"GGTATT\", \"GGTATTTTAATT\", \"GGTATTTTAATTTATAGT\"))\n" +
            "        cat(find_seq(seq, tolower(s)), sep=\"\\t\", s, \"\\n\")\n" +
            "}\n" +
            "\n" +
            "paste. <- function (..., digits=16, sep=\" \", collapse=NULL) {\n" +
            "    args <- list(...)\n" +
            "    if (length(args) == 0)\n" +
            "        if (length(collapse) == 0) character(0)\n" +
            "        else \"\"\n" +
            "    else {\n" +
            "        for(i in seq(along=args))\n" +
            "            if(is.numeric(args[[i]])) \n" +
            "                args[[i]] <- as.character(round(args[[i]], digits))\n" +
            "            else args[[i]] <- as.character(args[[i]])\n" +
            "        .Internal(paste(args, sep, collapse))\n" +
            "    }\n" +
            "}\n";

    static String spectralnorm_alt2 = "spectralnorm_alt2 <- function(args) {\n" +
            "    n = if (length(args)) as.integer(args[[1]]) else 100L\n" +
            "    options(digits=10)\n" +
            "\n" +
            "    n = if (length(args)) as.integer(args[[1]]) else 100L\n" +
            "\n" +
            "    eval_A <- function(i, j) 1 / ((i + j - 2) * (i + j - 1) / 2 + i)\n" +
            "    eval_A_times_u <- function(u)\n" +
            "        u %*% outer(seq(n), seq(n), FUN=eval_A)\n" +
            "    eval_At_times_u <- function(u)\n" +
            "        u %*% t(outer(seq(n), seq(n), FUN=eval_A))\n" +
            "    eval_AtA_times_u <- function(u)\n" +
            "    eval_At_times_u(eval_A_times_u(u))\n" +
            "\n" +
            "    u <- rep(1, n)\n" +
            "    v <- rep(0, n)\n" +
            "    for (itr in seq(10)) {\n" +
            "        v <- eval_AtA_times_u(u)\n" +
            "        u <- eval_AtA_times_u(v)\n" +
            "    }\n" +
            "    cat(sqrt(sum(u * v) / sum(v * v)), \"\\n\")\n" +
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

    static String binarytrees2 = "tree <- function(item, depth) {\n" +
            "    if (depth == 0L)\n" +
            "        return(c(item, NA, NA))\n" +
            "    return(list(item,\n" +
            "        tree(2L * item - 1L, depth - 1L),\n" +
            "        tree(2L * item, depth - 1L)))\n" +
            "}\n" +
            "\n" +
            "check <- function(tree)\n" +
            "    if(is.na(tree[[2]][[1]])) tree[[1]] else tree[[1]] + check(tree[[2]]) - check(tree[[3]])\n" +
            "\n" +
            "binarytrees_2 <- function(args) {\n" +
            "    n = if (length(args)) as.integer(args[[1]]) else 10L\n" +
            "\n" +
            "    min_depth <- 4\n" +
            "    max_depth <- max(min_depth + 2, n)\n" +
            "    stretch_depth <- max_depth + 1\n" +
            "\n" +
            "    cat(sep=\"\", \"stretch tree of depth \", stretch_depth, \"\\t check: \",\n" +
            "        check(tree(0, stretch_depth)), \"\\n\")\n" +
            "\n" +
            "    long_lived_tree <- tree(0, max_depth)\n" +
            "\n" +
            "    for (depth in seq(min_depth, max_depth, 2)) {\n" +
            "        iterations <- as.integer(2^(max_depth - depth + min_depth))\n" +
            "        check_sum <- sum(sapply(\n" +
            "                1:iterations, \n" +
            "                function(i) check(tree(i, depth)) + check(tree(-i, depth))))\n" +
            "        cat(sep=\"\", iterations * 2L, \"\\t trees of depth \", depth, \"\\t check: \",\n" +
            "            check_sum, \"\\n\")\n" +
            "    }\n" +
            "\n" +
            "    cat(sep=\"\", \"long lived tree of depth \", max_depth, \"\\t check: \", \n" +
            "        check(long_lived_tree), \"\\n\")\n" +
            "}\n";

    static String knucleotide_brute3 = "gen_freq <- function(seq, frame) {\n" +
            "    frame <- frame - 1L\n" +
            "    ns <- nchar(seq) - frame\n" +
            "    n <- 0L\n" +
            "    cap <- 16L\n" +
            "    freqs <- integer(cap)\n" +
            "    for (i in 1:ns) {\n" +
            "        subseq = substr(seq, i, i + frame)\n" +
            " \t #if (subseq %in% names(freqs))\n" +
            " \t #    cnt <- freqs[[subseq]]\n" +
            "\t cnt <- freqs[subseq]\n" +
            "\t if (is.na(cnt)) {\n" +
            "         #else {\n" +
            "            cnt <- 0L\n" +
            "            # ensure O(N) resizing (instead of O(N^2))\n" +
            "            n <- n + 1L\n" +
            "\n" +
            "\t    #CTK - this optimization has no (positive) effect because the expanded space does not\n" +
            "            #CTK   have the names we will need; it has a detrimental effect, instead (even with GNU-R)\n" +
            "            freqs[[cap <- if (cap < n) 2L * cap else cap]] <- 0L\n" +
            "        }\n" +
            "        freqs[[subseq]] <- cnt + 1L\n" +
            "    }\n" +
            "    return(freqs)\n" +
            "}\n" +
            "\n" +
            "sort_seq <- function(seq, len) {\n" +
            "    cnt_map <- gen_freq(seq, len)\n" +
            "    #print(cnt_map)\n" +
            "\n" +
            "    #CTK needed these changes to make the benchmark work in GNU-R\n" +
            "    #CTK attrs <- attributes(cnt_map)\n" +
            "    #CTK fs <- unlist(attrs, use.names=FALSE)\n" +
            "    #CTK seqs <- toupper(paste(names(attrs)))\n" +
            "\n" +
            "    #CTK --- added lines starting from here\n" +
            "    fs <- cnt_map[cnt_map > 0]\n" +
            "    seqs <- toupper(paste(names(fs)))\n" +
            "    #CTK --- end of added lines\n" +
            "\n" +
            "    inds <- order(-fs, seqs)\n" +
            "    #cat(paste(seqs[inds], fs[inds], collapse=\"\\n\"), \"\\n\")\n" +
            "#    cat(paste.(seqs[inds], 100 * fs[inds] / sum(fs), collapse=\"\\n\", digits=3),\n" +
            "    cat(paste(seqs[inds], 100 * fs[inds] / sum(fs), collapse=\"\\n\"),\n" +
            "        \"\\n\")\n" +
            "}\n" +
            "\n" +
            "find_seq <- function(seq, s) {\n" +
            "    cnt_map <- gen_freq(seq, nchar(s))\n" +
            "\n" +
            "#CTK    if (!is.null(cnt <- attr(cnt_map, s)))\n" +
            "    if (!is.na(cnt <- cnt_map[s]))\n" +
            "        return(cnt)\n" +
            "    return(0L)\n" +
            "}\n" +
            "\n" +
            "knucleotide_brute3 <- function(args) {\n" +
            "    in_filename = args[[1]]\n" +
            "    f <- file(in_filename, \"r\")\n" +
            "    while (length(line <- readLines(f, n=1, warn=FALSE))) {\n" +
            "        first_char <- substr(line, 1L, 1L)\n" +
            "        if (first_char == '>' || first_char == ';')\n" +
            "            if (substr(line, 2L, 3L) == 'TH')\n" +
            "                break\n" +
            "    }\n" +
            "\n" +
            "    n <- 0L\n" +
            "    cap <- 8L\n" +
            "    str_buf <- character(cap)\n" +
            "    while (length(line <- scan(f, what=\"\", nmax=1, quiet=TRUE))) {\n" +
            "        first_char <- substr(line, 1L, 1L)\n" +
            "        if (first_char == '>' || first_char == ';')\n" +
            "            break\n" +
            "        n <- n + 1L\n" +
            "\t# ensure O(N) resizing (instead of O(N^2))\n" +
            "        str_buf[[cap <- if (cap < n) 2L * cap else cap]] <- \"\"\n" +
            "        str_buf[[n]] <- line\n" +
            "    }\n" +
            "    length(str_buf) <- n\n" +
            "    close(f)\n" +
            "    seq <- paste(str_buf, collapse=\"\")\n" +
            "\n" +
            "    for (frame in 1:2)\n" +
            "        sort_seq(seq, frame)\n" +
            "    for (s in c(\"GGT\", \"GGTA\", \"GGTATT\", \"GGTATTTTAATT\", \"GGTATTTTAATTTATAGT\"))\n" +
            "        cat(find_seq(seq, tolower(s)), sep=\"\\t\", s, \"\\n\")\n" +
            "}\n" +
            "\n" +
            "paste. <- function (..., digits=16, sep=\" \", collapse=NULL) {\n" +
            "    args <- list(...)\n" +
            "    if (length(args) == 0)\n" +
            "        if (length(collapse) == 0) character(0)\n" +
            "        else \"\"\n" +
            "    else {\n" +
            "        for(i in seq(along=args))\n" +
            "            if(is.numeric(args[[i]])) \n" +
            "                args[[i]] <- as.character(round(args[[i]], digits))\n" +
            "            else args[[i]] <- as.character(args[[i]])\n" +
            "        .Internal(paste(args, sep, collapse))\n" +
            "    }\n" +
            "}\n";


    // fastaredux(500000L)
    // fannkuch(9L)
    // fasta(600000L)
    // fasta_2(300000L)
    // mandelbrot_noout(2000L)
    // nbody(190000L)
    // knucleotide_brute2("c:/delete/fasta.txt")
    // spectralnorm_alt2(1100L)
    // mandelbrot_noout_naive(1000L)

    public static void benchmark(String name, String code, int iterations, int warmup) {
        System.err.println(name);
        System.out.println(name);
        ASTNode tree = RContext.parseFile(new ANTLRStringStream(code));
        System.err.print("Warmup...\n");
        for (int i = 0; i < warmup; ++i) {
            long t = System.nanoTime();
            RContext.eval(tree);
            t = System.nanoTime() - t;
            double tt = t / 1000000.0;
            //System.err.println("warmup iteration " + i + ": " + tt + "[ms]");
        }
        System.err.println("");
        double ttime = 0;
        double[] times = new double[iterations];
        double min_time = Double.POSITIVE_INFINITY;
        double max_time = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < iterations; ++i) {
            Fusion.clearStatistics();
            long t = System.nanoTime();
            RContext.eval(tree);
            t = System.nanoTime() - t;
            double tt = t / 1000000.0;
            //System.err.println("Measured iteration " + i + ": " + tt + "[ms]");
            ttime += tt;
            times[i] = tt;
            if (tt < min_time)
                min_time = tt;
            if (tt > max_time)
                max_time = tt;
            // System.out.println(Fusion.statistics());
            // Fusion.clearStatistics();
        }
        double avg = ttime / iterations;
        double stddev = 0;
        for (int i = 0; i < iterations; ++i)
            stddev += Math.pow(times[i] - avg, 2);
        stddev = Math.sqrt(stddev / iterations);
        //System.err.println("Iterations:   " + iterations);
        System.err.println("Average:      " + avg);
        //System.err.println("Min:          " + min_time);
        //System.err.println("Max:          " + max_time);
        System.err.println("Stddev:       " + stddev);
        //System.err.println("Stddev (rel): " + (stddev / avg));
        //System.err.println("OVERALL:      " + (avg - stddev) + " -- " + (avg + stddev));
        System.err.println(Fusion.statistics());
    }



    public static void debugRun() {
        //String inputFile = "c:\\delete\\fasta.txt";
        //generateFastaOutput(70000, inputFile);
        ASTNode tree = RContext.parseFile(new ANTLRStringStream(mandelbrotNooutNaive));
        RAny result = RContext.eval(tree);
        //benchmark("mandelbrot_noout_naive(5100L)", 10, 3);
    }

    public static void testRun() {
        ASTNode tree = RContext.parseFile(new ANTLRStringStream("{ c(NA,NA,NA)+c(1L,2L,3L) }"));
        RAny result = RContext.eval(tree);
        System.out.println(result.pretty());

    }

    public static void runTests() {
        String inputFile = "/home/peta/fasta6650.txt";
/*        ShootoutTestBase.generateFastaOutput(6650, inputFile);
        inputFile = "/home/peta/fasta4860.txt";
        ShootoutTestBase.generateFastaOutput(4860, inputFile);
        ASTNode tree = RContext.parseFile(new ANTLRStringStream(mandelbrotNooutNaive));
        RContext.eval(tree);
        benchmark("mandelbrot-noout-naive", "mandelbrot_noout_naive(5100L)", 20, 3); */
        ASTNode tree = RContext.parseFile(new ANTLRStringStream(spectralnorm_alt2));
        RContext.eval(tree);
        for (int i : new int[] { 5, 10, 20, 40, 80, 160, 320, 640, 1280, 2560, 5120}) {
            benchmark("spectralnorm-alt2:"+i, "spectralnorm_alt2("+i+"L)", 1, 3);
        }

/*        benchmark("spectralnorm-alt2", "spectralnorm_alt2(10L)", 20, 3);
        benchmark("spectralnorm-alt2", "spectralnorm_alt2(50L)", 20, 3);
        benchmark("spectralnorm-alt2", "spectralnorm_alt2(100L)", 20, 3);
        benchmark("spectralnorm-alt2", "spectralnorm_alt2(500L)", 20, 3);
        benchmark("spectralnorm-alt2", "spectralnorm_alt2(1000L)", 20, 3); */
/*        tree = RContext.parseFile(new ANTLRStringStream(knucleotide_brute2));
        RContext.eval(tree);
        benchmark("knucleotide-brute2","knucleotide_brute2(\"/home/peta/fasta6650.txt\")",20,3);
        tree = RContext.parseFile(new ANTLRStringStream(knucleotide_brute3));
        RContext.eval(tree);
        benchmark("knucleotide-brute3","knucleotide_brute3(\"/home/peta/fasta4860.txt\")",20,3);
        tree = RContext.parseFile(new ANTLRStringStream(binarytrees2));
        RContext.eval(tree);
        benchmark("binarytrees-2", "binarytrees_2(16L)", 20, 3);
        tree = RContext.parseFile(new ANTLRStringStream(fasta));
        RContext.eval(tree);
        benchmark("fasta", "fasta(1880000L)", 20, 3); */


    }



    public static void main(String[] args) {
        //System.e.println("Executing sandbox...\nFUSION ON");
        runTests();

        //debugRun();
        //testRun();

    }
}
