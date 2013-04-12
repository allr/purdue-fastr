#include <jni.h>

#define MATHLIB_STANDALONE 1
#include <R.h>
#include <Rmath.h>
#include <R_ext/Applic.h>

#include "r_gnur_GNUR.h"

// FIXME: the code could be simplified (performance optimized) for the case when
// the underlying generator is a known sane one, e.g. does not return NaNs or values
// out of range. However, the Rmath library and R also supports user-specified generators,
// for which a general checking version should be available.

// LICENSE: this code interfaces directly with GNU R, which is licensed under GPL.
// LICENSE: this code includes several copy pasted fragments and transcribed code from GNU R and GNU R Math library.

// nmath =================================================================================================

JNIEXPORT void JNICALL Java_r_gnur_GNUR_set_1seed
  (JNIEnv *jenv, jclass jcls, jintArray kindArg) {
 
  int *kind = (*jenv)->GetPrimitiveArrayCritical(jenv, kindArg, 0);

  // FIXME: the generator ID is now ignored !  
  set_seed( (unsigned int) kind[1], (unsigned int) kind[2]);
  
  (*jenv)->ReleasePrimitiveArrayCritical(jenv, kindArg, kind, 0);
}

JNIEXPORT void JNICALL Java_r_gnur_GNUR_get_1seed
  (JNIEnv *jenv, jclass jcls, jintArray kindArg) {
  
  int *kind = (*jenv)->GetPrimitiveArrayCritical(jenv, kindArg, 0);
  
  get_seed( (unsigned int *) kind+1, (unsigned int *) kind+2);
  
  (*jenv)->ReleasePrimitiveArrayCritical(jenv, kindArg, kind, 0);  
}  


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

// sampling from a random distribution that takes two double arguments
jboolean randomTwoArg
  (JNIEnv *jenv, jclass jcls, double (*rfunc) (double, double), jdoubleArray resArg, jint n, jdoubleArray firstArg, jint firstLength, jdoubleArray secondArg, jint secondLength) {
  
  double *res = (*jenv)->GetPrimitiveArrayCritical(jenv, resArg, 0);
  double *firstArray = (*jenv)->GetPrimitiveArrayCritical(jenv, firstArg, 0);
  double *secondArray = (*jenv)->GetPrimitiveArrayCritical(jenv, secondArg, 0);
  
  int naProduced = 0;
  int firstIndex = 0;
  int secondIndex = 0;
  int i;

  for (i = 0; i < n; i++) {
    double first = firstArray[firstIndex++];
    double second = secondArray[secondIndex++];
    if (firstIndex == firstLength) {
      firstIndex = 0;
    }
    if (secondIndex == secondLength) {
      secondIndex = 0;
    }
    double d = rfunc(first, second);
    res[i] = d;
    naProduced = naProduced || ISNAN(d);  
  }
  
  (*jenv)->ReleasePrimitiveArrayCritical(jenv, resArg, res, 0);
  (*jenv)->ReleasePrimitiveArrayCritical(jenv, firstArg, firstArray, 0);
  (*jenv)->ReleasePrimitiveArrayCritical(jenv, secondArg, secondArray, 0);

  return naProduced ? JNI_TRUE : JNI_FALSE;
}


JNIEXPORT jboolean JNICALL Java_r_gnur_GNUR_rnorm___3DI_3DI_3DI
  (JNIEnv *jenv, jclass jcls, jdoubleArray resArg, jint n, jdoubleArray muArg, jint muLength, jdoubleArray sigmaArg, jint sigmaLength) {
  
  return randomTwoArg(jenv, jcls, rnorm, resArg, n, muArg, muLength, sigmaArg, sigmaLength);
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

JNIEXPORT jboolean JNICALL Java_r_gnur_GNUR_runif   
  (JNIEnv *jenv, jclass jcls, jdoubleArray resArg, jint n, jdoubleArray minArg, jint minLength, jdoubleArray maxArg, jint maxLength) {

  return randomTwoArg(jenv, jcls, runif, resArg, n, minArg, minLength, maxArg, maxLength);
}

JNIEXPORT jboolean JNICALL Java_r_gnur_GNUR_rgamma
  (JNIEnv *jenv, jclass jcls, jdoubleArray resArg, jint n, jdoubleArray shapeArg, jint shapeLength, jdoubleArray scaleArg, jint scaleLength) {

  return randomTwoArg(jenv, jcls, rgamma, resArg, n, shapeArg, shapeLength, scaleArg, scaleLength);
}
  
JNIEXPORT jboolean JNICALL Java_r_gnur_GNUR_rbinom
  (JNIEnv *jenv, jclass jcls, jdoubleArray resArg, jint n, jdoubleArray sizeArg, jint sizeLength, jdoubleArray probArg, jint probLength) {
  
  return randomTwoArg(jenv, jcls, rbinom, resArg, n, sizeArg, sizeLength, probArg, probLength);
}

JNIEXPORT jboolean JNICALL Java_r_gnur_GNUR_rlnormStd
  (JNIEnv *jenv, jclass jcls, jdoubleArray resArg, jint n) {

  int i;
  double *res = (*jenv)->GetPrimitiveArrayCritical(jenv, resArg, 0);
  int naProduced = 0;
  
  for (i = 0; i < n; i++) {
    double d = exp( norm_rand() );
    res[i] = d;
    naProduced = naProduced || ISNAN(d); // FIXME: perhaps? only with user supplied
  }
  
  (*jenv)->ReleasePrimitiveArrayCritical(jenv, resArg, res, 0);
  return naProduced ? JNI_TRUE : JNI_FALSE;

}

JNIEXPORT jboolean JNICALL Java_r_gnur_GNUR_rlnorm
  (JNIEnv *jenv, jclass jcls, jdoubleArray resArg, jint n, jdoubleArray meanlogArg, jint meanlogLength, jdoubleArray sdlogArg, jint sdlogLength) {
  
  return randomTwoArg(jenv, jcls, rlnorm, resArg, n, meanlogArg, meanlogLength, sdlogArg, sdlogLength);  
}

JNIEXPORT jboolean JNICALL Java_r_gnur_GNUR_rcauchyStd
  (JNIEnv *jenv, jclass jcls, jdoubleArray resArg, jint n) {
  
  int i;
  double *res = (*jenv)->GetPrimitiveArrayCritical(jenv, resArg, 0);
  int naProduced = 0;
  
  for (i = 0; i < n; i++) {
    double d = tan(M_PI * unif_rand());
    res[i] = d;
    naProduced = naProduced || ISNAN(d); // FIXME: perhaps? only with user supplied
  }
  
  (*jenv)->ReleasePrimitiveArrayCritical(jenv, resArg, res, 0);
  return naProduced ? JNI_TRUE : JNI_FALSE;
  
}

JNIEXPORT jboolean JNICALL Java_r_gnur_GNUR_rcauchy
  (JNIEnv *jenv, jclass jcls, jdoubleArray resArg, jint n, jdoubleArray locationArg, jint locationLength, jdoubleArray scaleArg, jint scaleLength) {
  
 return randomTwoArg(jenv, jcls, rcauchy, resArg, n, locationArg, locationLength, scaleArg, scaleLength); 
}


// appl =================================================================================================

JNIEXPORT void JNICALL Java_r_gnur_GNUR_fft_1factor
  (JNIEnv *jenv, jclass jcls, jint n, jintArray maxfArg, jintArray maxpArg) {

  int *maxf = (*jenv)->GetPrimitiveArrayCritical(jenv, maxfArg, 0);
  int *maxp = (*jenv)->GetPrimitiveArrayCritical(jenv, maxpArg, 0);
  
  fft_factor(n, maxf, maxp);
  
  (*jenv)->ReleasePrimitiveArrayCritical(jenv, maxfArg, maxf, 0);
  (*jenv)->ReleasePrimitiveArrayCritical(jenv, maxpArg, maxp, 0);  
}

JNIEXPORT jint JNICALL Java_r_gnur_GNUR_fft_1work
  (JNIEnv *jenv, jclass jcls, jdoubleArray abArg, jint nseg, jint n, jint nspn, jint isn, jdoubleArray workArg, jintArray iworkArg) {

  double *ab = (*jenv)->GetPrimitiveArrayCritical(jenv, abArg, 0);
  double *work = (*jenv)->GetPrimitiveArrayCritical(jenv, workArg, 0);
  int *iwork = (*jenv)->GetPrimitiveArrayCritical(jenv, iworkArg, 0);
  
  int res = fft_work(ab, ab + 1, nseg, n, nspn, isn, work, iwork);
  
  (*jenv)->ReleasePrimitiveArrayCritical(jenv, abArg, ab, 0);
  (*jenv)->ReleasePrimitiveArrayCritical(jenv, workArg, work, 0);
  (*jenv)->ReleasePrimitiveArrayCritical(jenv, iworkArg, iwork, 0);
  
  return res;
}

JNIEXPORT void JNICALL Java_r_gnur_GNUR_dqrdc2
  (JNIEnv *jenv, jclass jcls, jdoubleArray xArg, jint ldx, jint n, jint p, jdouble tol, jintArray kArg, jdoubleArray qrauxArg, jintArray jpvtArg, jdoubleArray workArg) {
 
   double *x = (*jenv)->GetPrimitiveArrayCritical(jenv, xArg, 0);
   int *k = (*jenv)->GetPrimitiveArrayCritical(jenv, kArg, 0);
   double *qraux = (*jenv)->GetPrimitiveArrayCritical(jenv, qrauxArg, 0);
   int *jpvt = (*jenv)->GetPrimitiveArrayCritical(jenv, jpvtArg, 0);
   double *work = (*jenv)->GetPrimitiveArrayCritical(jenv, workArg, 0);

   // calling to Fortran
   dqrdc2_(x, &ldx, &n, &p, &tol, k, qraux, jpvt, work);

   (*jenv)->ReleasePrimitiveArrayCritical(jenv, xArg, x, 0);
   (*jenv)->ReleasePrimitiveArrayCritical(jenv, kArg, k, 0);
   (*jenv)->ReleasePrimitiveArrayCritical(jenv, qrauxArg, qraux, 0);
   (*jenv)->ReleasePrimitiveArrayCritical(jenv, jpvtArg, jpvt, 0);
   (*jenv)->ReleasePrimitiveArrayCritical(jenv, workArg, work, 0);
}

JNIEXPORT void JNICALL Java_r_gnur_GNUR_dqrcf
  (JNIEnv *jenv, jclass jcls, jdoubleArray xArg, jint n, jint k, jdoubleArray qrauxArg, jdoubleArray yArg, jint ny, jdoubleArray bArg, jintArray infoArg) {
  
  double *x = (*jenv)->GetPrimitiveArrayCritical(jenv, xArg, 0);
  double *qraux = (*jenv)->GetPrimitiveArrayCritical(jenv, qrauxArg, 0);
  double *y = (*jenv)->GetPrimitiveArrayCritical(jenv, yArg, 0);
  double *b = (*jenv)->GetPrimitiveArrayCritical(jenv, bArg, 0);
  int *info = (*jenv)->GetPrimitiveArrayCritical(jenv, infoArg, 0);

  // calling to Fortran
  dqrcf_(x, &n, &k, qraux, y, &ny, b, info);
  
  (*jenv)->ReleasePrimitiveArrayCritical(jenv, xArg, x, 0);
  (*jenv)->ReleasePrimitiveArrayCritical(jenv, qrauxArg, qraux, 0);
  (*jenv)->ReleasePrimitiveArrayCritical(jenv, yArg, y, 0);
  (*jenv)->ReleasePrimitiveArrayCritical(jenv, bArg, b, 0);
  (*jenv)->ReleasePrimitiveArrayCritical(jenv, infoArg, info, 0);
}

// libc/libm =================================================================================================

JNIEXPORT jdouble JNICALL Java_r_gnur_GNUR_pow
  (JNIEnv *jenv, jclass jcls, jdouble a, jdouble b) {
  
  return pow(a,b);
}
    
