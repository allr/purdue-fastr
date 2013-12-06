#include <jni.h>

#define MATHLIB_STANDALONE 1
#include <R.h>
#include <Rmath.h>
#include <R_ext/Applic.h>

#include "r_ext_SystemLibs.h"

// LICENSE: this code includes several copy pasted fragments and transcribed code from GNU R and GNU R Math library.


// libc/libm =================================================================================================

JNIEXPORT jdouble JNICALL Java_r_ext_SystemLibs_pow__DD
  (JNIEnv *jenv, jclass jcls, jdouble a, jdouble b) {
  
  return pow(a,b);
}
    
JNIEXPORT void JNICALL Java_r_ext_SystemLibs_pow___3D_3D_3DI
  (JNIEnv *jenv, jclass jcls, jdoubleArray xArg, jdoubleArray yArg, jdoubleArray resArg, jint size) {
  
  double *x = (*jenv)->GetPrimitiveArrayCritical(jenv, xArg, 0);
  double *y = (*jenv)->GetPrimitiveArrayCritical(jenv, yArg, 0);
  double *res = (*jenv)->GetPrimitiveArrayCritical(jenv, resArg, 0);
  int i;
   
  for (i = 0; i < size; i++) {
    double a = x[i];
    double b = y[i];
    double d = pow(a, b);
    if (ISNAN(d)) {
      if (ISNA(a) || ISNA(b)) {
        d = NA_REAL;
      }
    }
    res[i] = d;
  }
   
  (*jenv)->ReleasePrimitiveArrayCritical(jenv, xArg, x, 0);
  (*jenv)->ReleasePrimitiveArrayCritical(jenv, yArg, y, 0);
  (*jenv)->ReleasePrimitiveArrayCritical(jenv, resArg, res, 0);
}

JNIEXPORT void JNICALL Java_r_ext_SystemLibs_pow___3DD_3DI
  (JNIEnv *jenv, jclass jcls, jdoubleArray xArg, jdouble y, jdoubleArray resArg, jint size) {
  
  double *x = (*jenv)->GetPrimitiveArrayCritical(jenv, xArg, 0);
  double *res = (*jenv)->GetPrimitiveArrayCritical(jenv, resArg, 0);
  int i;

  for (i = 0; i < size; i++) {
     double a = x[i];
     double d = pow(a, y);
     if (ISNAN(d)) {
       if (ISNA(a) || ISNA(y)) {
         d = NA_REAL;
       }
     }
     res[i] = d;
   }
   
   (*jenv)->ReleasePrimitiveArrayCritical(jenv, xArg, x, 0);
   (*jenv)->ReleasePrimitiveArrayCritical(jenv, resArg, res, 0);
}

JNIEXPORT void JNICALL Java_r_ext_SystemLibs_pow__D_3D_3DI
  (JNIEnv *jenv, jclass jcls, jdouble x, jdoubleArray yArg, jdoubleArray resArg, jint size) {
  
  double *y = (*jenv)->GetPrimitiveArrayCritical(jenv, yArg, 0);
  double *res = (*jenv)->GetPrimitiveArrayCritical(jenv, resArg, 0);
  int i;

  for (i = 0; i < size; i++) {
     double b = y[i];
     double d = pow(x, b);
     if (ISNAN(d)) {
       if (ISNA(x) || ISNA(b)) {
         d = NA_REAL;
       }
     }
     res[i] = d;
   }
   
   (*jenv)->ReleasePrimitiveArrayCritical(jenv, yArg, y, 0);
   (*jenv)->ReleasePrimitiveArrayCritical(jenv, resArg, res, 0);
}

JNIEXPORT jboolean JNICALL Java_r_ext_SystemLibs_fmod
  (JNIEnv *jenv, jclass jcls, jdoubleArray xArg, jdoubleArray yArg, jdoubleArray resArg, jint size) {
  
  double *x = (*jenv)->GetPrimitiveArrayCritical(jenv, xArg, 0);
  double *y = (*jenv)->GetPrimitiveArrayCritical(jenv, yArg, 0);
  double *res = (*jenv)->GetPrimitiveArrayCritical(jenv, resArg, 0);
  int i;
  int lostAccuracy = 0;

  for (i = 0; i < size; i++) {
    double a = x[i];
    double b = y[i];

    if (b == 0) {  // LICENSE: transcribed from GNU-R, which is licensed under GPL
      res[i] = R_NaN;
    } else {
      double q = a / b;
      double tmp = a - floor(q) * b;
      if ((fabs(q) > 1/DBL_EPSILON ) && R_FINITE(q)) {   // R_AccuracyInfo.eps
        lostAccuracy = 1;
      }
      res[i] = tmp - floor(tmp/b) * b;  
    }    
  }
   
  (*jenv)->ReleasePrimitiveArrayCritical(jenv, xArg, x, 0);
  (*jenv)->ReleasePrimitiveArrayCritical(jenv, yArg, y, 0);
  (*jenv)->ReleasePrimitiveArrayCritical(jenv, resArg, res, 0);  
  
  return lostAccuracy ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jdouble JNICALL Java_r_ext_SystemLibs_exp
  (JNIEnv *jenv, jclass jcls, jdouble x) {
  
  return exp(x);
}

JNIEXPORT void JNICALL Java_r_ext_SystemLibs_exp___3D_3DI
  (JNIEnv *jenv, jclass jcls, jdoubleArray xArg, jdoubleArray resArg, jint size) {
  
  double *x = (*jenv)->GetPrimitiveArrayCritical(jenv, xArg, 0);
  double *res = (*jenv)->GetPrimitiveArrayCritical(jenv, resArg, 0);
  int i;

  for (i = 0; i < size; i++) {
    res[i] = exp(x[i]);  // FIXME: equivalent to R wrt to NA/NaN?
  }
   
  (*jenv)->ReleasePrimitiveArrayCritical(jenv, xArg, x, 0);
  (*jenv)->ReleasePrimitiveArrayCritical(jenv, resArg, res, 0);
}  
