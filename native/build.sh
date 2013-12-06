#! /bin/bash

# This script is for Ubuntu 13.10, 64-bit and JDK 1.7 (or 1.8)
# it builds the wrappers for R library and libc
#
# (not for BLAS/LAPACK, see netlib-java for that)

#FIXME: a makefile?

JDK=$JAVA_HOME

gcc -O3 -msse4 -fno-strict-aliasing -fPIC -fno-omit-frame-pointer -W -Wall -Wno-unused -Wno-parentheses -Wno-unused-parameter \
  -I $JDK/include/ -I $JDK/include/linux/ -I /usr/share/R/include -I. \
  -c r_ext_GNUR.c r_ext_SystemLibs.c

gcc -O3 -msse4 -fno-strict-aliasing -fPIC -fno-omit-frame-pointer -W -Wall -Wno-unused -Wno-parentheses -Wno-unused-parameter \
  -I /usr/share/R/include -I. \
  -c gnur/src/library/stats/src/fft.c -o gnur/src/library/stats/src/fft.o
  
gcc -O3 -msse4 -fno-strict-aliasing -fPIC -fno-omit-frame-pointer -W -Wall  -Wno-unused -Wno-parentheses \
  -Wl,-soname=libgnurglue.so -static-libgcc \
  -shared -o libgnurglue.so r_ext_GNUR.o gnur/src/library/stats/src/fft.o \
  -lRmath -lR -lc

# FIXME: is the R library needed here?
gcc -O3 -msse4 -fno-strict-aliasing -fPIC -fno-omit-frame-pointer -W -Wall  -Wno-unused -Wno-parentheses \
  -Wl,-soname=libgnurglue.so -static-libgcc \
  -shared -o libsystemlibsglue.so r_ext_SystemLibs.o \
   -lRmath -lR -lc
  
if [ -n "$MKLROOT" ] ; then  
  echo "Building MKL glue: yes" >&2 
  

  gcc -O3 -msse4 -fno-strict-aliasing -fPIC -fno-omit-frame-pointer -W -Wall -Wno-unused -Wno-parentheses \
    -I $JDK/include/ -I $JDK/include/linux/ -I $MKLROOT/include  -I. \
    -c r_ext_MKL.c
    
  gcc -O3 -msse4 -fno-strict-aliasing -fPIC -fno-omit-frame-pointer -W -Wall  -Wno-unused -Wno-parentheses \
    -L${MKLROOT}/lib/intel64 \
    -Wl,-soname=libmklglue.so -static-libgcc \
    -shared -o libmklglue.so r_ext_MKL.o \
    -lmkl_rt -liomp5 -ldl -lpthread -lm
    
else
  echo "Not building MKL glue as MKL is not available." >&2
fi
  

# Note the linking above
#   libRmath is before libR
#
#   normally we would just need libR, because it contains nmath as well, however libR defines
#   a uniform random number generator with seeds inter-connected to the R workspace, and this cannot 
#   be re-used without the R workspace
#
#   so by linking libRmath before libR, we get a standalone uniform random number generator from libRmath,
#   which will get preference of that from libR
#
#   TODO: the standalone version supports only one generator, will have to
#   extract the R's generator from the R code
#
#
