#!/bin/zsh

JARS="jars"
OUT="instrumented-jars"
if [ -d $OUT ] ; then
	rm -rf $OUT
fi
mkdir $OUT


for j in $(ls $JARS)
do
	echo "Instrument $j"
	
	/home/nharrand/Documents/yajta/script/instrument_jar.sh $JARS/$j $OUT
done
