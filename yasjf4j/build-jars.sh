#!/bin/bash

LIB=$1
echo "Path to lib dir: $LIB"

for d in $(ls -d *)
do
	if [ -d $d ];
	then
		echo "dir: $d"
		cd $d
		mvn clean install
		JAR=$(ls target/*-jar-with-dependencies.jar)
		cp $JAR $LIB
		cd ..
	fi
done
