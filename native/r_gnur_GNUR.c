#include <jni.h>

#define MATHLIB_STANDALONE 1
#include <Rmath.h>

#include "r_gnur_GNUR.h"

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
  