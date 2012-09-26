#! /bin/bash

# Runs Fast-R on Graal - please update your PATHs below

FH=/home/tomas/work/fastr
export JAVA_HOME=/home/tomas/hg/graalvm-truffle/jdk1.7.0_06/product

# -------

export PATH=$JAVA_HOME/bin:$PATH
java -cp $FH/antlr-runtime-3.4.jar:$FH/fastr/bin:$FH/graalvm-truffle/graal/com.oracle.truffle/bin -graal r.Console $*
