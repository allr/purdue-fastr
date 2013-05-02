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

## Requirements

FastR requires Java. By default, FastR uses Java implementations of LAPACK
and BLAS.  Some of the mandelbrot shootout benchmarks will only run on Unix,
because they spawn the `cat` process.  FastR needs to be linked against the
GNU R implementation to be able to run the micro-benchmarks from the
[Benchmark 25 suite](http://r.research.att.com/benchmarks/).  A build script
for Linux/Ubuntu is available in the `native` directory.  The linking is not
necessary for the Shootout benchmarks.

## Running Tests

`ant tests` 

The outputs will appear in `junit` directory.

## Eclipse

The code contains settings and project configuration for Eclipse Juno.

# Authors:

Tomas Kalibera, Petr Maj and Jan Vitek
