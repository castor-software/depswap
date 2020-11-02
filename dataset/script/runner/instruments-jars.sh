#!/bin/zsh

#LIB=$1
#echo "Path to lib dir: $LIB"
PWD=$(pwd)
LIB="$PWD/instrumented_jars/"
YAJTA=/home/runner/yajta.jar

if [ -d $LIB ] ; then
	rm -rf $LIB
fi
mkdir $LIB


for d in $(ls jars/*.jar)
do
	if [ -f $d ];
	then
		echo " ------------------------------------------ "
		echo "JAR: $d"
		if [[ $d == *"yasjf4j"* ]]; then
		  echo "java -cp $YAJTA se.kth.castor.offline.RemoteUserInstrumenter -i $d -o $LIB -y"
			java -cp $YAJTA se.kth.castor.offline.RemoteUserInstrumenter -i $d -o $LIB -y;
			rm -rf $LIB/yajta-tmp*
		fi
	fi
done
