# FastR

FastR implements the [R Language](http://www.r-project.org/). Currently,
FastR can run the [R implementation](http://r.cs.purdue.edu/hg/r-shootout/)
of the [Language Shootout Benchmarks](http://shootout.alioth.debian.org/) and 
the [Benchmark 25 suite](http://r.research.att.com/benchmarks/).

## Quick Start

1. download the [latest code](https://github.com/allr/fastr/archive/master.zip): `wget https://github.com/allr/fastr/archive/master.zip`
2. unzip it: `unzip master.zip`
3. build: `cd fastr-master ; ant`
4. run the console: `./r.sh`
5. run the binarytrees benchmark for size 5: `./r.sh --args 5 -f test/r/shootout/binarytrees/binarytrees.r`

## Minimal Requirements

To run the binarytrees benchmark as shown above, FastR requires Java. All
Shootout benchmarks can be run this way, but some of the mandelbrot
only on Unix, as they spawn the `cat` process. 

## Full Installation

To run the benchmarks from the Benchmark 25 suite, and for best performance
of all benchmarks, build native glue code which links FastR to the GNU-R
Math Library, system Math library, and openBLAS.  The build scripts are
tested on Ubuntu 13.10. Any platform supported by GNU-R and Java could
be supported by FastR.

1. install Oracle JDK8 (for best performance); if you must use JDK7, customize `native/netlib-java/build.sh`
2. set `JAVA_HOME` and `PATH` accordingly
3. follow the steps in Quick Start
4. install Ubuntu packages `r-base`, `r-mathlib`, `libopenblas-base`
5. build glue code for system libraries and GNU-R: `cd native ; ./build.sh`
6. build glue code for native BLAS and LAPACK: `cd netlib-java ; ./build.sh` 
7. check the glue code can be loaded: `cd ../.. ; ./nr.sh` should give output  
`Using LAPACK: org.netlib.lapack.NativeLAPACK`  
`Using BLAS: org.netlib.blas.NativeBLAS`  
`Using GNUR: yes`  
`Using System libraries (C/M): yes`  
`Using MKL: not available`  
8. run the matfunc-1 benchmark: `./nr.sh -f test/r/benchmark25/perfres/b25-matfunc-1.r`

To ensure that the openBLAS library is used, run the matcal-4 benchmark with
the system profiler: 
`perf record ./nr.sh -f test/r/benchmark25/perfres/b25-matcal-4.r`.

Check with `perf report` that DGEMM from openBLAS is used, e.g. 
`dgemm_kernel_SANDYBRIDGE` from `libopenblas.so.0`.  Also expect to see the
random number generator, e.g.  `qnorm5` from `libRmath.so.1.0.0`.

## Running Tests

`ant tests` 

The outputs will appear in `junit` directory.

## Eclipse

The code contains settings and project configuration for Eclipse Juno.

# Authors:

Tomas Kalibera, Petr Maj and Jan Vitek
