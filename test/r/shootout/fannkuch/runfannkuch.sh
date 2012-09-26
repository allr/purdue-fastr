#! /bin/bash

# Runs Fannkuch (on Linux) - please customize sizes below and check r.sh and rg.sh scripts work

SIZES="8 9 10 11 12"
FASTR=../../../../r.sh 
GFASTR=../../../../rg.sh 

# --------------

ITER=3
PREF=fannkuch

# run name input cmds
function run {
  NAME=$1
  shift 1
  INPUT=$1
  shift 1
  TF=${NAME}.tm
  OF=${NAME}.out
  rm -f $TF
  rm -f $OF
  date
  for N in `seq 1 $ITER` ; do
    /usr/bin/time -a -o $TF -p $* <$INPUT
  done > $OF 
}


for S in $SIZES ; do
  cat fannkuchredux.r > torun.r
  echo -e "\nfannkuch(${S}L)\n" >> torun.r

  run ${PREF}_${S}_gnur_jit torun.r env R_ENABLE_JIT=3 R --slave
  run ${PREF}_${S}_gnur_int torun.r env R_ENABLE_JIT=0 R --slave
  run ${PREF}_${S}_fastr_graal /dev/null $GFASTR torun.r     
  run ${PREF}_${S}_fastr_int /dev/null $FASTR torun.r     

done

rm -f torun.r
