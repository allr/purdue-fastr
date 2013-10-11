#include <jni.h>
#include <mkl.h>

#include "r_ext_MKL.h"

// LICENSE: this code interfaces to the Intel MKL (Math Kernel Library)

// double binary

JNIEXPORT void JNICALL Java_r_ext_MKL_vdAdd
  (JNIEnv *jenv, jclass jcls, jint n, jdoubleArray aArg, jdoubleArray bArg, jdoubleArray yArg) {
  
  double *a = (*jenv)->GetPrimitiveArrayCritical(jenv, aArg, 0);
  double *b = (*jenv)->GetPrimitiveArrayCritical(jenv, bArg, 0);
  double *y = (*jenv)->GetPrimitiveArrayCritical(jenv, yArg, 0);
  
  vdAdd(n, a, b, y);
  
  (*jenv)->ReleasePrimitiveArrayCritical(jenv, aArg, a, 0);
  (*jenv)->ReleasePrimitiveArrayCritical(jenv, bArg, b, 0);
  (*jenv)->ReleasePrimitiveArrayCritical(jenv, yArg, y, 0);

}
  

JNIEXPORT void JNICALL Java_r_ext_MKL_vdSub
  (JNIEnv *jenv, jclass jcls, jint n, jdoubleArray aArg, jdoubleArray bArg, jdoubleArray yArg) {
  
  double *a = (*jenv)->GetPrimitiveArrayCritical(jenv, aArg, 0);
  double *b = (*jenv)->GetPrimitiveArrayCritical(jenv, bArg, 0);
  double *y = (*jenv)->GetPrimitiveArrayCritical(jenv, yArg, 0);
  
  vdSub(n, a, b, y);
  
  (*jenv)->ReleasePrimitiveArrayCritical(jenv, aArg, a, 0);
  (*jenv)->ReleasePrimitiveArrayCritical(jenv, bArg, b, 0);
  (*jenv)->ReleasePrimitiveArrayCritical(jenv, yArg, y, 0);

}

JNIEXPORT void JNICALL Java_r_ext_MKL_vdMul
  (JNIEnv *jenv, jclass jcls, jint n, jdoubleArray aArg, jdoubleArray bArg, jdoubleArray yArg) {
  
  double *a = (*jenv)->GetPrimitiveArrayCritical(jenv, aArg, 0);
  double *b = (*jenv)->GetPrimitiveArrayCritical(jenv, bArg, 0);
  double *y = (*jenv)->GetPrimitiveArrayCritical(jenv, yArg, 0);
  
  vdMul(n, a, b, y);
  
  (*jenv)->ReleasePrimitiveArrayCritical(jenv, aArg, a, 0);
  (*jenv)->ReleasePrimitiveArrayCritical(jenv, bArg, b, 0);
  (*jenv)->ReleasePrimitiveArrayCritical(jenv, yArg, y, 0);

}

JNIEXPORT void JNICALL Java_r_ext_MKL_vdDiv
  (JNIEnv *jenv, jclass jcls, jint n, jdoubleArray aArg, jdoubleArray bArg, jdoubleArray yArg) {
  
  double *a = (*jenv)->GetPrimitiveArrayCritical(jenv, aArg, 0);
  double *b = (*jenv)->GetPrimitiveArrayCritical(jenv, bArg, 0);
  double *y = (*jenv)->GetPrimitiveArrayCritical(jenv, yArg, 0);
  
  vdDiv(n, a, b, y);
  
  (*jenv)->ReleasePrimitiveArrayCritical(jenv, aArg, a, 0);
  (*jenv)->ReleasePrimitiveArrayCritical(jenv, bArg, b, 0);
  (*jenv)->ReleasePrimitiveArrayCritical(jenv, yArg, y, 0);

}

JNIEXPORT void JNICALL Java_r_ext_MKL_vdPow
  (JNIEnv *jenv, jclass jcls, jint n, jdoubleArray aArg, jdoubleArray bArg, jdoubleArray yArg) {
  
  double *a = (*jenv)->GetPrimitiveArrayCritical(jenv, aArg, 0);
  double *b = (*jenv)->GetPrimitiveArrayCritical(jenv, bArg, 0);
  double *y = (*jenv)->GetPrimitiveArrayCritical(jenv, yArg, 0);
  
  vdPow(n, a, b, y);
  
  (*jenv)->ReleasePrimitiveArrayCritical(jenv, aArg, a, 0);
  (*jenv)->ReleasePrimitiveArrayCritical(jenv, bArg, b, 0);
  (*jenv)->ReleasePrimitiveArrayCritical(jenv, yArg, y, 0);

}

// complex binary

JNIEXPORT void JNICALL Java_r_ext_MKL_vzAdd
  (JNIEnv *jenv, jclass jcls, jint n, jdoubleArray aArg, jdoubleArray bArg, jdoubleArray yArg) {
  
  double *a = (*jenv)->GetPrimitiveArrayCritical(jenv, aArg, 0);
  double *b = (*jenv)->GetPrimitiveArrayCritical(jenv, bArg, 0);
  double *y = (*jenv)->GetPrimitiveArrayCritical(jenv, yArg, 0);
  
  vzAdd(n, (MKL_Complex16 *)a, (MKL_Complex16 *)b, (MKL_Complex16 *)y);
  
  (*jenv)->ReleasePrimitiveArrayCritical(jenv, aArg, a, 0);
  (*jenv)->ReleasePrimitiveArrayCritical(jenv, bArg, b, 0);
  (*jenv)->ReleasePrimitiveArrayCritical(jenv, yArg, y, 0);

}


JNIEXPORT void JNICALL Java_r_ext_MKL_vzSub
  (JNIEnv *jenv, jclass jcls, jint n, jdoubleArray aArg, jdoubleArray bArg, jdoubleArray yArg) {
  
  double *a = (*jenv)->GetPrimitiveArrayCritical(jenv, aArg, 0);
  double *b = (*jenv)->GetPrimitiveArrayCritical(jenv, bArg, 0);
  double *y = (*jenv)->GetPrimitiveArrayCritical(jenv, yArg, 0);
  
  vzSub(n, (MKL_Complex16 *)a, (MKL_Complex16 *)b, (MKL_Complex16 *)y);
  
  (*jenv)->ReleasePrimitiveArrayCritical(jenv, aArg, a, 0);
  (*jenv)->ReleasePrimitiveArrayCritical(jenv, bArg, b, 0);
  (*jenv)->ReleasePrimitiveArrayCritical(jenv, yArg, y, 0);

}

JNIEXPORT void JNICALL Java_r_ext_MKL_vzMul
  (JNIEnv *jenv, jclass jcls, jint n, jdoubleArray aArg, jdoubleArray bArg, jdoubleArray yArg) {
  
  double *a = (*jenv)->GetPrimitiveArrayCritical(jenv, aArg, 0);
  double *b = (*jenv)->GetPrimitiveArrayCritical(jenv, bArg, 0);
  double *y = (*jenv)->GetPrimitiveArrayCritical(jenv, yArg, 0);
  
  vzMul(n, (MKL_Complex16 *)a, (MKL_Complex16 *)b, (MKL_Complex16 *)y);
  
  (*jenv)->ReleasePrimitiveArrayCritical(jenv, aArg, a, 0);
  (*jenv)->ReleasePrimitiveArrayCritical(jenv, bArg, b, 0);
  (*jenv)->ReleasePrimitiveArrayCritical(jenv, yArg, y, 0);

}

JNIEXPORT void JNICALL Java_r_ext_MKL_vzDiv
  (JNIEnv *jenv, jclass jcls, jint n, jdoubleArray aArg, jdoubleArray bArg, jdoubleArray yArg) {
  
  double *a = (*jenv)->GetPrimitiveArrayCritical(jenv, aArg, 0);
  double *b = (*jenv)->GetPrimitiveArrayCritical(jenv, bArg, 0);
  double *y = (*jenv)->GetPrimitiveArrayCritical(jenv, yArg, 0);
  
  vzDiv(n, (MKL_Complex16 *)a, (MKL_Complex16 *)b, (MKL_Complex16 *)y);
  
  (*jenv)->ReleasePrimitiveArrayCritical(jenv, aArg, a, 0);
  (*jenv)->ReleasePrimitiveArrayCritical(jenv, bArg, b, 0);
  (*jenv)->ReleasePrimitiveArrayCritical(jenv, yArg, y, 0);

}

JNIEXPORT void JNICALL Java_r_ext_MKL_vzPow
  (JNIEnv *jenv, jclass jcls, jint n, jdoubleArray aArg, jdoubleArray bArg, jdoubleArray yArg) {
  
  double *a = (*jenv)->GetPrimitiveArrayCritical(jenv, aArg, 0);
  double *b = (*jenv)->GetPrimitiveArrayCritical(jenv, bArg, 0);
  double *y = (*jenv)->GetPrimitiveArrayCritical(jenv, yArg, 0);
  
  vzPow(n, (MKL_Complex16 *)a, (MKL_Complex16 *)b, (MKL_Complex16 *)y);
  
  (*jenv)->ReleasePrimitiveArrayCritical(jenv, aArg, a, 0);
  (*jenv)->ReleasePrimitiveArrayCritical(jenv, bArg, b, 0);
  (*jenv)->ReleasePrimitiveArrayCritical(jenv, yArg, y, 0);

}

// binary double vector, double scalar (power to a constant)

JNIEXPORT void JNICALL Java_r_ext_MKL_vdPowx
  (JNIEnv *jenv, jclass jcls, jint n, jdoubleArray aArg, jdouble b, jdoubleArray yArg) {
    
  double *a = (*jenv)->GetPrimitiveArrayCritical(jenv, aArg, 0);
  double *y = (*jenv)->GetPrimitiveArrayCritical(jenv, yArg, 0);
  
  vdPowx(n, a, b, y);
  
  (*jenv)->ReleasePrimitiveArrayCritical(jenv, aArg, a, 0);
  (*jenv)->ReleasePrimitiveArrayCritical(jenv, yArg, y, 0);

}

// unary double

JNIEXPORT void JNICALL Java_r_ext_MKL_vdSqr
  (JNIEnv *jenv, jclass jcls, jint n, jdoubleArray aArg, jdoubleArray yArg) {

  double *a = (*jenv)->GetPrimitiveArrayCritical(jenv, aArg, 0);
  double *y = (*jenv)->GetPrimitiveArrayCritical(jenv, yArg, 0);
  
  vdSqr(n, a, y);
  
  (*jenv)->ReleasePrimitiveArrayCritical(jenv, aArg, a, 0);
  (*jenv)->ReleasePrimitiveArrayCritical(jenv, yArg, y, 0);  
  
}

JNIEXPORT void JNICALL Java_r_ext_MKL_vdAbs
  (JNIEnv *jenv, jclass jcls, jint n, jdoubleArray aArg, jdoubleArray yArg) {

  double *a = (*jenv)->GetPrimitiveArrayCritical(jenv, aArg, 0);
  double *y = (*jenv)->GetPrimitiveArrayCritical(jenv, yArg, 0);
  
  vdAbs(n, a, y);
  
  (*jenv)->ReleasePrimitiveArrayCritical(jenv, aArg, a, 0);
  (*jenv)->ReleasePrimitiveArrayCritical(jenv, yArg, y, 0);  
  
}

JNIEXPORT void JNICALL Java_r_ext_MKL_vdSqrt
  (JNIEnv *jenv, jclass jcls, jint n, jdoubleArray aArg, jdoubleArray yArg) {

  double *a = (*jenv)->GetPrimitiveArrayCritical(jenv, aArg, 0);
  double *y = (*jenv)->GetPrimitiveArrayCritical(jenv, yArg, 0);
  
  vdSqrt(n, a, y);
  
  (*jenv)->ReleasePrimitiveArrayCritical(jenv, aArg, a, 0);
  (*jenv)->ReleasePrimitiveArrayCritical(jenv, yArg, y, 0);  
  
}

// unary complex

JNIEXPORT void JNICALL Java_r_ext_MKL_vzAbs
  (JNIEnv *jenv, jclass jcls, jint n, jdoubleArray aArg, jdoubleArray yArg) {

  double *a = (*jenv)->GetPrimitiveArrayCritical(jenv, aArg, 0);
  double *y = (*jenv)->GetPrimitiveArrayCritical(jenv, yArg, 0);
  
  vzAbs(n, (MKL_Complex16 *)a, y);
  
  (*jenv)->ReleasePrimitiveArrayCritical(jenv, aArg, a, 0);
  (*jenv)->ReleasePrimitiveArrayCritical(jenv, yArg, y, 0);  
  
}
