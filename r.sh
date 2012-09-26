#! /bin/bash

# Runs Fast-R on Hotspot - please update your PATHs below

FH=/home/tomas/work/fastr
export JAVA_HOME=/opt/jdk7

# -------------------------------

export PATH=$JAVA_HOME/bin:$PATH

function bpath() {

  for P in $* ; do
    echo -n ":$FH/graalvm-truffle/graal/"$P"/bin"
  done

}

export PATH=$JAVA_HOME/bin:$PATH

CP=`bpath \
	com.oracle.truffle com.oracle.truffle.compiler com.oracle.graal.api.code com.oracle.graal.api.meta com.oracle.graal.compiler com.oracle.graal.java \
	com.oracle.graal.hotspot com.oracle.graal.api com.oracle.max.asm com.oracle.max.cri com.oracle.graal.nodes com.oracle.graal.debug \
`

java -cp $FH/antlr-runtime-3.4.jar:$FH/fastr/bin:$CP r.Console $*



