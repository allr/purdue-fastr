#! /bin/bash

# for Ubuntu 13.04 and JDK 1.7

export JAVA_HOME=/opt/jdk7

ant generate
ant compile
ant package
cd jni
bash ./configure
make
ls -l *.so
