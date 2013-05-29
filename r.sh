#! /bin/bash
java -classpath bin:lib/antlr-runtime-3.5.jar:lib/arpack_combined_all.jar:lib/junit-4.8.jar:lib/netlib-java-0.9.3.jar:lib/truffle-api-03-16-2013.jar:lib/jline-2.12.jar -ea -esa r.Console $*
