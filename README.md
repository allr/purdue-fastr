# FastR

FastR is an implementation of the [R Language](http://www.r-project.org/). 
At the moment, FastR can run the [R
implementation](http://r.cs.purdue.edu/hg/r-shootout/) of the [Language
Shootout Benchmarks](http://shootout.alioth.debian.org/).

## Quick Start

1. download the [latest code](https://github.com/allr/fastr/archive/master.zip): `wget https://github.com/allr/fastr/archive/master.zip`
2. unzip it: `unzip master.zip`
3. build: `cd fastr-master ; ant`
4. run the console: `./r.sh`
5. run the binarytrees benchmark for size 5: `./r.sh --args 5 -f test/r/shootout/binarytrees/binarytrees.r`

## Requirements

FastR requires Java. By default FastR uses Java implementations of LAPACK
and BLAS.  Some of the mandelbrot shootout benchmarks will only run on Unix,
because they spawn the `cat` process. 

## Running Tests

`ant tests` 

The outputs will appear in `junit` directory.

## Eclipse

The code contains settings and project configuration for Eclipse Juno.

# Authors:

Tomas Kalibera, Petr Maj and Jan Vitek
