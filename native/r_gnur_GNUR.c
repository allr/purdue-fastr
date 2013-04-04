#include <jni.h>

#define MATHLIB_STANDALONE 1
#include <Rmath.h>

#include "r_gnur_GNUR.h"

// FIXME: the code could be simplified (performance optimized) for the case when
// the underlying generator is a known sane one, e.g. does not return NaNs or values
// out of range. However, the Rmath library and R also supports user-specified generators,
// for which a general checking version should be available.

JNIEXPORT jdouble JNICALL Java_r_gnur_GNUR_rnorm__DD
  (JNIEnv *jenv, jclass jcls, jdouble mu, jdouble sigma) {

  return rnorm(mu, sigma);
}

JNIEXPORT jboolean JNICALL Java_r_gnur_GNUR_rnorm___3DIDD
  (JNIEnv *jenv, jclass jcls, jdoubleArray resArg, jint n, jdouble mu, jdouble sigma) {
  
  int i;
  double *res = (*jenv)->GetPrimitiveArrayCritical(jenv, resArg, 0);
  int naProduced = 0;
  
  for (i = 0; i < n; i++) {
    double d = rnorm(mu, sigma);
    res[i] = d;
    naProduced = naProduced || ISNAN(d);
  }
  
  (*jenv)->ReleasePrimitiveArrayCritical(jenv, resArg, res, 0);
  
  return naProduced ? JNI_TRUE : JNI_FALSE;
}  

JNIEXPORT jboolean JNICALL Java_r_gnur_GNUR_rnormNonChecking
  (JNIEnv *jenv, jclass jcls, jdoubleArray resArg, jint n, jdouble mu, jdouble sigma) {
  
  int i;
  double *res = (*jenv)->GetPrimitiveArrayCritical(jenv, resArg, 0);
  int naProduced = 0;
  
  for (i = 0; i < n; i++) {
    double d = mu + sigma * norm_rand();
    res[i] = d;
    naProduced = naProduced || ISNAN(d);
  }
  
  (*jenv)->ReleasePrimitiveArrayCritical(jenv, resArg, res, 0);
  return naProduced ? JNI_TRUE : JNI_FALSE;
}  

JNIEXPORT jboolean JNICALL Java_r_gnur_GNUR_rnormStd
  (JNIEnv *jenv, jclass jcls, jdoubleArray resArg, jint n) {

  int i;
  double *res = (*jenv)->GetPrimitiveArrayCritical(jenv, resArg, 0);
  int naProduced = 0;
  
  for (i = 0; i < n; i++) {
    double d = norm_rand();
    res[i] = d;
    naProduced = naProduced || ISNAN(d); // FIXME: perhaps? only with user supplied
  }
  
  (*jenv)->ReleasePrimitiveArrayCritical(jenv, resArg, res, 0);
  return naProduced ? JNI_TRUE : JNI_FALSE;
  
}    

JNIEXPORT jboolean JNICALL Java_r_gnur_GNUR_runifStd
  (JNIEnv *jenv, jclass jcls, jdoubleArray resArg, jint n) {

  int i;
  double *res = (*jenv)->GetPrimitiveArrayCritical(jenv, resArg, 0);
  int naProduced = 0;
  
  for (i = 0; i < n; i++) {

    double d;
    /* This is true of all builtin generators, but protect against
       user-supplied ones */
    do {d = unif_rand();} while (d <= 0 || d >= 1);
    res[i] = d;
    naProduced = naProduced || ISNAN(d); // FIXME: only with user-supplied
  }
  
  (*jenv)->ReleasePrimitiveArrayCritical(jenv, resArg, res, 0);
  return naProduced ? JNI_TRUE : JNI_FALSE;
  
}    
