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


    static String fastaredux = "width = 60L\n" +
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

    static String fannkuch = "fannkuch <- function(n) {\n" +
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
            "                    cat(checksum, \"\\n\", sep=\"\")\n" +
            "                    return(proc.time()[[3]] - time)\n" +
            "                }\n" +
            "            }\n" +
            "            count[[r]] <- count[[r]] - 1L\n" +
            "        }\n" +
            "    }\n" +
            "    proc.time()[[3]] - time\n" +
            "}\n" +
            "\n";

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

    static String fasta2 = "width <- 60L\n" +
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
            "    psum = cumsum(genelist[1,])\n" +
            "    n = length(psum)\n" +
            "    while (count) {\n" +
            "\tline = min(width, count) \n" +
            "        rs <- double(line)\n" +
            "        for (i in 1:line)\n" +
            "          rs[[i]] <- myrandom(1)\n" +
            "\n" +
            "\t# Linear search (vectorized)\n" +
            "\tinds <- 1:line\n" +
            "       \tlo <- rep.int(1L, line)\n" +
            "\twhile (length(inds <- which(psum[lo] < rs)))\n" +
            "\t    lo[inds] <- lo[inds] + 1L\n" +
            "\n" +
            "\tcat(genelist[2, lo], \"\\n\", sep='')\n" +
            "\tcount <- count - line\n" +
            "    }\n" +
            "}\n" +
            "\n" +
            "fasta_2 <- function(args) {\n" +
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

    static String mandelbrotNoout = "\n" +
            "lim <- 2\n" +
            "iter <- 50\n" +
            "\n" +
            "mandelbrot_noout <- function(args) {\n" +
            "    time = proc.time()[[3]]\n" +
            "    n = if (length(args)) as.integer(args[[1]]) else 200L\n" +
            "    n_mod8 = n %% 8L\n" +
            "    pads <- if (n_mod8) rep.int(0, 8L - n_mod8) else integer(0)\n" +
            "    p <- rep(as.integer(rep.int(2, 8) ^ (7:0)), length.out=n)\n" +
            "\n" +
            "    cat(\"P4\\n\")\n" +
            "    cat(n, n, \"\\n\")\n" +
            "    #bin_con <- pipe(\"cat\", \"wb\")\n" +
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
            "    }\n" +
            "    proc.time()[[3]] - time\n" +
            "}\n";

    static String nbody = "pi <- 3.141592653589793\n" +
            "solar_mass <- 4 * pi * pi\n" +
            "days_per_year <- 365.24\n" +
            "n_bodies <- 5\n" +
            "\n" +
            "body_x <- c(\n" +
            "    0, # sun\n" +
            "    4.84143144246472090e+00, # jupiter\n" +
            "    8.34336671824457987e+00, # saturn\n" +
            "    1.28943695621391310e+01, # uranus\n" +
            "    1.53796971148509165e+01 # neptune\n" +
            ")\n" +
            "body_y <- c(\n" +
            "    0, # sun\n" +
            "    -1.16032004402742839e+00, # jupiter\n" +
            "    4.12479856412430479e+00, # saturn\n" +
            "    -1.51111514016986312e+01, # uranus\n" +
            "    -2.59193146099879641e+01 # neptune\n" +
            ")\n" +
            "body_z <- c(\n" +
            "    0, # sun\n" +
            "    -1.03622044471123109e-01, # jupiter\n" +
            "    -4.03523417114321381e-01, # saturn\n" +
            "    -2.23307578892655734e-01, # uranus\n" +
            "    1.79258772950371181e-01 # neptune\n" +
            ")\n" +
            "\n" +
            "body_vx <- c(\n" +
            "    0, # sun\n" +
            "    1.66007664274403694e-03 * days_per_year, # jupiter\n" +
            "    -2.76742510726862411e-03 * days_per_year, # saturn\n" +
            "    2.96460137564761618e-03 * days_per_year, # uranus\n" +
            "    2.68067772490389322e-03 * days_per_year # neptune\n" +
            ")\n" +
            "body_vy <- c(\n" +
            "    0, # sun\n" +
            "    7.69901118419740425e-03 * days_per_year, # jupiter\n" +
            "    4.99852801234917238e-03 * days_per_year, # saturn\n" +
            "    2.37847173959480950e-03 * days_per_year, # uranus\n" +
            "    1.62824170038242295e-03 * days_per_year # neptune\n" +
            ")\n" +
            "body_vz <- c(\n" +
            "    0, # sun\n" +
            "    -6.90460016972063023e-05 * days_per_year, # jupiter\n" +
            "    2.30417297573763929e-05 * days_per_year, # saturn\n" +
            "    -2.96589568540237556e-05 * days_per_year, # uranus\n" +
            "    -9.51592254519715870e-05 * days_per_year # neptune\n" +
            ")\n" +
            "\n" +
            "body_mass <- c(\n" +
            "    solar_mass, # sun\n" +
            "    9.54791938424326609e-04 * solar_mass, # jupiter\n" +
            "    2.85885980666130812e-04 * solar_mass, # saturn\n" +
            "    4.36624404335156298e-05 * solar_mass, # uranus\n" +
            "    5.15138902046611451e-05 * solar_mass # neptune\n" +
            ")\n" +
            "\n" +
            "offset_momentum <- function() {\n" +
            "    body_vx[[1]] <<- -sum(body_vx * body_mass) / solar_mass\n" +
            "    body_vy[[1]] <<- -sum(body_vy * body_mass) / solar_mass\n" +
            "    body_vz[[1]] <<- -sum(body_vz * body_mass) / solar_mass\n" +
            "}\n" +
            "\n" +
            "advance <- function(dt) {\n" +
            "    dxx <- outer(body_x, body_x, \"-\")  # ~2x faster then nested for loops\n" +
            "    dyy <- outer(body_y, body_y, \"-\")\n" +
            "    dzz <- outer(body_z, body_z, \"-\")\n" +
            "    distance <- sqrt(dxx * dxx + dyy * dyy + dzz * dzz)\n" +
            "    mag <- dt / (distance * distance * distance)  # ~fast as distance^3\n" +
            "    diag(mag) <- 0\n" +
            "    body_vx <<- body_vx - as.vector((dxx * mag) %*% body_mass)\n" +
            "    body_vy <<- body_vy - as.vector((dyy * mag) %*% body_mass)\n" +
            "    body_vz <<- body_vz - as.vector((dzz * mag) %*% body_mass)\n" +
            "    body_x <<- body_x + dt * body_vx\n" +
            "    body_y <<- body_y + dt * body_vy\n" +
            "    body_z <<- body_z + dt * body_vz\n" +
            "}\n" +
            "\n" +
            "energy <- function() {\n" +
            "    dxx <- outer(body_x, body_x, \"-\")\n" +
            "    dyy <- outer(body_y, body_y, \"-\")\n" +
            "    dzz <- outer(body_z, body_z, \"-\")\n" +
            "    distance <- sqrt(dxx * dxx + dyy * dyy + dzz * dzz)\n" +
            "    q <- (body_mass %o% body_mass) / distance\n" +
            "    return(sum(0.5 * body_mass *\n" +
            "               (body_vx * body_vx + body_vy * body_vy + body_vz * body_vz)) -\n" +
            "           sum(q[upper.tri(q)]))\n" +
            "}\n" +
            "\n" +
            "nbody <- function(args) {\n" +
            "    time = proc.time()[[3]]\n" +
            "    n = if (length(args)) as.integer(args[[1]]) else 1000L\n" +
            "    options(digits=9)\n" +
            "    offset_momentum()\n" +
            "    cat(energy(), \"\\n\")\n" +
            "    for (i in 1:n)\n" +
            "        advance(0.01)\n" +
            "    cat(energy(), \"\\n\")\n" +
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


    // fastaredux(500000L)
    // fannkuch(9L)
    // fasta(600000L)
    // fasta_2(300000L)
    // mandelbrot_noout(2000L)
    // nbody(190000L)


    public static void debugRun() {
        ASTNode tree = RContext.parseFile(new ANTLRStringStream(nbody));
        RAny result = RContext.eval(tree);
        tree = RContext.parseFile(new ANTLRStringStream("nbody(190000L)"));
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
        debugRun();
        //testRun();

    }
}
