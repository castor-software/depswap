#!/bin/zsh

#LIB=$1
#echo "Path to lib dir: $LIB"
PWD=$(pwd)
LIB="$PWD/jars/"

#Shaded
SHADED=('yasjf4j-json-simple' 'yasjf4j-json' 'yasjf4j-gson' 'yasjf4j-jackson-databind' 'yasjf4j-fastjson')

GOAL="install"

if [ -d $LIB ] ; then
	rm -rf $LIB
fi
mkdir $LIB


echo " ------------------------------------------ "
echo "dir: yasjf4j-api"
cd yasjf4j-api
mvn $GOAL -DskipTests=True > /dev/null
cp target/yasjf4j-api-1.0-SNAPSHOT-jar-with-dependencies.jar $LIB
cd ..


for d in $(ls -d yasjf4j-*)
do
	if [ -d $d ];
	then
		echo " ------------------------------------------ "
		echo "dir: $d"
		if [[ ${SHADED[(r)$d]} == $d ]]; then
				echo "To be compiled"
				cd $d
				mvn $GOAL -DskipTests=True > /dev/null
				JAR=$(find . -name "$d-1.0-SNAPSHOT.jar")
				cp $JAR $LIB/$d-1.0-SNAPSHOT-jar-with-dependencies.jar
				cd ..
		else
			if [[ $d == *"yasjf4j"* ]]; then
				echo "To be compiled"
				cd $d
				mvn $GOAL -DskipTests=True > /dev/null
				JAR=$(find . -name "*-jar-with-dependencies.jar")
				cp $JAR $LIB
				cd ..
			fi
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
			mvn $GOAL -DskipTests=True > /dev/null
			JAR=$(find . -name "*-jar-with-dependencies.jar")
			cp $JAR $LIB
			cd ..
		fi
	fi
done


echo " ------------------------------------------ "
echo "dir: test-harness"
cd ../test-harness
mvn $GOAL -DskipTests=True > /dev/null
cp target/depswap-test-harness-0.1-SNAPSHOT-jar-with-dependencies.jar $LIB










