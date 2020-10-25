#!/bin/zsh

#LIB=$1
#echo "Path to lib dir: $LIB"
PWD=$(pwd)
LIB="$PWD/jars/"

if [ -d $LIB ] ; then
	rm -rf $LIB
fi
mkdir $LIB


echo " ------------------------------------------ "
echo "dir: yasjf4j-api"
cd yasjf4j-api
mvn clean install -DskipTests=True > /dev/null
cd ..


for d in $(ls -d yasjf4j-*)
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


for d in $(ls -d *-over-yasjf4j)
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
