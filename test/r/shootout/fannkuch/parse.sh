#! /bin/bash

# Parses outputs of Fannkuch benchmark (on Linux)

SIZES=`ls -1 fannkuch*tm | cut -d_ -f2 | sort -n | uniq`

echo -n " "
for S in $SIZES ; do
  echo -n " $S"
done
echo

for N in fastr_int fastr_graal ; do
  echo -n $N
  for S in $SIZES ; do
    P="fannkuch_${S}"
    F=${P}_${N}.out
    T=`cat $F | grep Elapsed | tail -1  | cut -d' ' -f2`
    echo -n " "
    perl -e 'print '$T'/1000.0'
  done
  echo
done

for N in gnur_int gnur_jit ; do
  echo -n $N
  for S in $SIZES ; do
    P="fannkuch_${S}"
    F=${P}_${N}.tm
    T=`cat $F | grep real | tail -1  | cut -d' ' -f2`
    echo -n " $T"
  done
  echo
done

