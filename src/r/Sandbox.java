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


    static String nbody2 = "#! nbody-2\n" +
            "#!g size = (100000L)\n" +
            "\n" +
            "# ------------------------------------------------------------------\n" +
            "# The Computer Language Shootout\n" +
            "# http://shootout.alioth.debian.org/\n" +
            "#\n" +
            "# Contributed by Leo Osvald\n" +
            "# ------------------------------------------------------------------\n" +
            "\n" +
            "pi <- 3.141592653589793\n" +
            "solar_mass <- 4 * pi * pi\n" +
            "days_per_year <- 365.24\n" +
            "n_bodies <- 5\n" +
            "\n" +
            "body_r <- matrix(c(\n" +
            "    c(  # sun\n" +
            "\t0,\n" +
            "\t0,\n" +
            "\t0),\n" +
            "    c(  # jupiter\n" +
            "\t4.84143144246472090e+00,\n" +
            "\t-1.16032004402742839e+00,\n" +
            "\t-1.03622044471123109e-01),\n" +
            "    c(  # saturn\n" +
            "\t8.34336671824457987e+00,\n" +
            "\t4.12479856412430479e+00,\n" +
            "\t-4.03523417114321381e-01),\n" +
            "    c(  # uranus\n" +
            "\t1.28943695621391310e+01,\n" +
            "\t-1.51111514016986312e+01,\n" +
            "\t-2.23307578892655734e-01),\n" +
            "    c(  # neptune\n" +
            "\t1.53796971148509165e+01,\n" +
            "\t-2.59193146099879641e+01,\n" +
            "\t1.79258772950371181e-01)\n" +
            "), 3)\n" +
            "\n" +
            "body_v <- matrix(c(\n" +
            "    c(  #sun\n" +
            "\t0,\n" +
            "\t0,\n" +
            "\t0),\n" +
            "    c(  # jupiter\n" +
            "\t1.66007664274403694e-03 * days_per_year,\n" +
            "\t7.69901118419740425e-03 * days_per_year,\n" +
            "\t-6.90460016972063023e-05 * days_per_year),\n" +
            "    c(  # saturn\n" +
            "\t-2.76742510726862411e-03 * days_per_year,\n" +
            "\t4.99852801234917238e-03 * days_per_year,\n" +
            "\t2.30417297573763929e-05 * days_per_year),\n" +
            "    c(  # uranus\n" +
            "\t2.96460137564761618e-03 * days_per_year,\n" +
            "\t2.37847173959480950e-03 * days_per_year,\n" +
            "\t-2.96589568540237556e-05 * days_per_year),\n" +
            "    c(  # neptune\n" +
            "\t2.68067772490389322e-03 * days_per_year,\n" +
            "\t1.62824170038242295e-03 * days_per_year,\n" +
            "\t-9.51592254519715870e-05 * days_per_year)\n" +
            "), 3)\n" +
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
            "    body_v[, 1] <<- -(body_v %*% body_mass) / solar_mass\n" +
            "}\n" +
            "\n" +
            "advance <- function(dt) {\n" +
            "    drr <- array(dim=c(n_bodies, n_bodies, 3))\n" +
            "    for (i in 1:n_bodies) {\n" +
            "        for (j in 1:n_bodies) {\n" +
            "            drr[i, j,] <- body_r[,i] - body_r[,j]\n" +
            "        }\n" +
            "    }\n" +
            "\n" +
            "    distance <- sqrt(t(colSums(aperm(drr * drr))))\n" +
            "    mag <- dt / (distance * distance * distance)  # ~fast as distance^3\n" +
            "    diag(mag) <- 0\n" +
            "    for (d in 1:3)\n" +
            "        body_v[d,] <<- body_v[d,] - as.vector((drr[,,d] * mag) %*% body_mass)\n" +
            "\n" +
            "    body_r <<- body_r + dt * body_v\n" +
            "}\n" +
            "\n" +
            "energy <- function() {\n" +
            "    drr <- array(dim=c(n_bodies, n_bodies, 3))\n" +
            "    for (i in 1:n_bodies) {\n" +
            "        for (j in 1:n_bodies)\n" +
            "            drr[i, j,] <- body_r[,i] - body_r[,j]\n" +
            "    }\n" +
            "    distance <- sqrt(t(colSums(aperm(drr * drr))))\n" +
            "    q <- (body_mass %o% body_mass) / distance\n" +
            "    return(sum(0.5 * body_mass * colSums(body_v * body_v)) -\n" +
            "           sum(q[upper.tri(q)]))\n" +
            "}\n" +
            "\n" +
            "nbody_2 <- function(args) {\n" +
            "    time = proc.time()[[3]]\n" +
            "    n = if (length(args)) as.integer(args[[1]]) else 1000L\n" +
            "    options(digits=9)\n" +
            "    offset_momentum()\n" +
            "    cat(energy(), \"\\n\")\n" +
            "    for (i in 1:n)\n" +
            "        advance(0.01)\n" +
            "    cat(energy(), \"\\n\")\n" +
            "    proc.time()[[3]] - time\n" +
            "}\n" +
            "\n" +
            "nbody_2(100000L)\n" +
            "nbody_2(100000L)\n" +
            "cat(\"__TIMER__\",nbody_2(100000L),\"tmr\\n\")";

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
        ASTNode tree = RContext.parseFile(new ANTLRStringStream(simpleCode));
        if (tree != null) {
            RAny result = RContext.eval(tree);
            //System.out.println(result.pretty());
        }

    }
}
