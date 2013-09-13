#! /bin/bash

# for Ubuntu 13.04 and JDK 1.7

# This version of netlib-java includes several fixes needed to build and run
# on recent versions of Ubuntu.  Also the generated JNI wrappers can pass
# null primitive arrays to the native code of BLAS/LAPACK.

export JAVA_HOME=/opt/jdk7

ant generate
ant compile
ant package
cd jni
bash ./configure
make
ls -l *.so
