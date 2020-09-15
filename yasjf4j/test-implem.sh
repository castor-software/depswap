#!/bin/zsh

JARS_PATH=$1
echo "Path to lib dir: $JARS_PATH"

BRIDGES=('json-simple-over-yasjf4j' 'json-over-yasjf4j')
IMPLEMENTATIONS=('yasjf4j-json' 'yasjf4j-json-lib' 'yasjf4j-json-simple' 'yasjf4j-jsonutil' 'yasjf4j-klaxon' 'yasjf4j-mjson' 'yasjf4j-fastjson')

ROOT_DIR=$(pwd)
g="se.kth.castor"
a="yasjf4j-json"
v="1.0-SNAPSHOT"

GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # No Color



RESULTS="-------------------- Results ----------------------------\n"
RESULTS="\n"
RESULTS=$(printf "$RESULTS %19s |" "Implem")
RESULTS=$(printf "$RESULTS %24s |" "Bridge")
RESULTS="$RESULTS Outcome\n"
RESULTS="$RESULTS-------------------------------------------------------\n"

for i in $IMPLEMENTATIONS
do
	echo "----------------- Implem $i -------------------------"
	cd $ROOT_DIR
	RESULTS=$(printf "$RESULTS %19s |\n" $i)
	RESULTS=$(printf "$RESULTS %24s |\n" "-----")
	cd $i
	mvn clean test 2>&1 >> log-$i
	if [ $? -eq 0 ];
	then
		RESULTS="$RESULTS   ${GREEN}OK${NC}\n"
		echo "Alls test passed"
	else
		cat log-$i
		RESULTS="$RESULTS   ${RED}KO${NC}\n"
	fi
	#RESULTS="$RESULTS $i |"
	for b in $BRIDGES
	do
		cd $ROOT_DIR
		echo "----------------- Bridge $b -------------------------"
		#RESULTS="$RESULTS $b |"
		RESULTS=$(printf "$RESULTS %19s |\n" $i)
		RESULTS=$(printf "$RESULTS %24s |\n" $b)
		cd $b
		java -cp $JARS_PATH/depswap-test-harness-0.1-SNAPSHOT-jar-with-dependencies.jar se.kth.assertteam.depswap.SwapTestDep ./ "$g:$a:$v" "$g:$i:$v" $JARS_PATH
		#mvn dependency:tree -DoutputFile="tree-$b-$i" 2>1 > log-$b-$i
		#cat tree-$b-$i
		mvn clean test 2>&1 > log-$b-$i
		if [ $? -eq 0 ];
		then
			RESULTS="$RESULTS   ${GREEN}OK${NC}\n"
			echo "Alls test passed"
		else
			cat log-$b-$i
			RESULTS="$RESULTS   ${RED}KO${NC}\n"
		fi
		java -cp $JARS_PATH/depswap-test-harness-0.1-SNAPSHOT-jar-with-dependencies.jar se.kth.assertteam.depswap.SwapTestDep ./ "$g:$a:$v" "$g:$b:$v" $JARS_PATH -r
	done
done
RESULTS="$RESULTS-------------------------------------------------------\n"

echo ""
echo ""
echo ""

printf $RESULTS


