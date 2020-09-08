#!/bin/bash

JARS_PATH=$1
echo "Path to lib dir: $JARS_PATH"

BRIDGES="json-simple-over-yasjf4j"
IMPLEMENTATIONS="yasjf4j-json yasjf4j-json-lib yasjf4j-simple yasjf4j-jsonutil yasjf4j-klaxon yasjf4j-mjson"

ROOT_DIR=$(pwd)
g="se.kth.castor"
a="yasjf4j-json"
v="1.0-SNAPSHOT"

for i in $IMPLEMENTATIONS
do
	echo "----------------- Implem $i -------------------------"
	for b in $BRIDGES
	do
		cd $ROOT_DIR
		echo "----------------- Bridge $b -------------------------"
		cd $b
		java -cp $JARS_PATH/depswap-test-harness-0.1-SNAPSHOT-jar-with-dependencies.jar se.kth.assertteam.depswap.SwapTestDep ./ "$g:$a:$v" "$g:$b:$v" $JARS_PATH
		mvn clean test
		java -cp $JARS_PATH/depswap-test-harness-0.1-SNAPSHOT-jar-with-dependencies.jar se.kth.assertteam.depswap.SwapTestDep ./ "$g:$a:$v" "$g:$b:$v" $JARS_PATH -r
	done
done
