#! /bin/bash

# This script is for Ubuntu 12.10, 64-bit and JDK 1.7

JDK=$JAVA_HOME

gcc -O2 -fno-strict-aliasing -fPIC -fno-omit-frame-pointer -W -Wall -Wno-unused -Wno-parentheses \
  -I $JDK/include/ -I $JDK/include/linux/  -I. \
  -c r_gnur_GNUR.c
  
gcc -O2 -fno-strict-aliasing -fPIC -fno-omit-frame-pointer -W -Wall  -Wno-unused -Wno-parentheses \
  -Wl,-soname=libgnurglue.so -static-libgcc \
  -shared -o libgnurglue.so r_gnur_GNUR.o \
  -lRmath -lc


