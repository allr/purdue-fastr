#! /bin/bash

# Runs Fast-R on Hotspot - please update your PATHs below

FH=/home/tomas/work/fastr
export JAVA_HOME=/opt/jdk7

# -------------------------------

export PATH=$JAVA_HOME/bin:$PATH
java -cp $FH/truffle/lib/antlr-runtime-3.4.jar:$FH/fastr/bin:$FH/truffle/graal/com.oracle.truffle/bin r.Console $*

