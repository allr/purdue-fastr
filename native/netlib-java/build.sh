#! /bin/bash

# for Ubuntu 13.04 and JDK 1.7 (also Ubuntu 13.10, also JDK 1.8)
# uncomment the correct invocation of javadoc if JDK 1.7 is used

# This version of netlib-java includes several fixes needed to build and run
# on recent versions of Ubuntu.  Also the generated JNI wrappers can pass
# null primitive arrays to the native code of BLAS/LAPACK.

if [ -n "$MKLROOT" ] ; then
  echo
  echo "MKL found: $MKLROOT"
else
  echo
  echo "MKL is not available, using system BLAS, LAPACK"
fi

echo
echo "Print enter to continue..."
read DUMMY  


ant generate
ant compile

# Javadoc invocation for JDK 1.8
ant -Djavadoc.additionalparam="-Xdoclint:none" package

# Javadoc invocation for JDK 1.7
#ant -Djavadoc.additionalparam="-Xdoclint:none" package

cd jni
bash ./configure
make
ls -l *.so
