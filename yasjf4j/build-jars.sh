#!/bin/zsh

LIB=$1
echo "Path to lib dir: $LIB"

for d in $(ls -d *)
do
	if [ -d $d ];
	then
		echo " ------------------------------------------ "
		echo "dir: $d"
		if [[ $d == *"yasjf4j"* ]]; then
		  echo "To be compiled"
			cd $d
			mvn clean install -DskipTests=True > /dev/null
			JAR=$(ls ./**/target/*-jar-with-dependencies.jar)
			cp $JAR $LIB
			cd ..
		fi
	fi
done
