#!/bin/zsh


if [ "$#" -lt 1 ]; then
  echo "Usage: $0 <path_to_libs> [implem]" >&2
  exit 1
fi

JARS_PATH=$1
echo "Path to lib dir: $JARS_PATH"

BRIDGES=('json-implem-tester')
#IMPLEMENTATIONS=('yasjf4j-json' 'yasjf4j-cookjson' 'yasjf4j-gson' 'yasjf4j-jackson' 'yasjf4j-jsonp' 'yasjf4j-json-io' 'yasjf4j-json-lib' 'yasjf4j-json-simple' 'yasjf4j-jsonutil' 'yasjf4j-klaxon' 'yasjf4j-mjson' 'yasjf4j-fastjson')
IMPLEMENTATIONS=('yasjf4j-json' 'yasjf4j-cookjson' 'yasjf4j-gson' 'yasjf4j-jackson-databind' 'yasjf4j-jsonp' 'yasjf4j-json-io' 'yasjf4j-json-lib' 'yasjf4j-jjson' 'yasjf4j-json-simple' 'yasjf4j-jsonutil' 'yasjf4j-klaxon' 'yasjf4j-mjson' 'yasjf4j-fastjson' 'yasjf4j-nothing')
#excludes  'yasjf4j-jjson'



if [ "$#" -gt 1 ]; then
	IMPLEMENTATIONS=$2
fi


ROOT_DIR=$(pwd)
g="se.kth.castor"
a="yasjf4j-json"
v="1.0-SNAPSHOT"

GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # No Color



RESULTS="-------------------- Results ----------------------------\n"
RESULTS="\n"
RESULTS=$(printf "$RESULTS %25s |" "Implem")
RESULTS=$(printf "$RESULTS %30s |" "Bridge")
RESULTS=$(printf "$RESULTS %8s |" "Outcome")
RESULTS="$RESULTS Failures\n"
RESULTS="$RESULTS---------------------------------------------------------------------\n"

for i in $IMPLEMENTATIONS
do
	echo "----------------- Implem $i -------------------------"
	cd $ROOT_DIR
	RESULTS=$(printf "$RESULTS %25s |\n" $i)
	RESULTS=$(printf "$RESULTS %30s |\n" "-----")
	cd $i
	mvn clean install -DskipTests 2>&1 >> log-$i
	mvn test 2>&1 >> log-$i
	if [ $? -eq 0 ];
	then
		#RESULTS="$RESULTS   ${GREEN}OK${NC}\n"
		RESULTS=$(printf "$RESULTS ${GREEN}%8s${NC} |" "OK")
		RESULTS="$RESULTS ${GREEN}0${NC}\n"
		echo "Alls test passed"
	else
		cat log-$i
		#RESULTS="$RESULTS   ${RED}KO${NC}\n"
		RESULTS=$(printf "$RESULTS ${RED}%8s${NC} |" "KO")
		failures=$(grep "Tests run: " log-$i | tail -n 1 | cut -d ',' -f2,3 |  cut -d ' ' -f3,5 | sed 's/,/ +/' | bc)
		total=$(grep "Tests run: " log-$i | tail -n 1 | cut -d ',' -f1 | cut -d ' ' -f4)
		RESULTS="$RESULTS ${RED}$failures / $total${NC}\n"
	fi
	#RESULTS="$RESULTS $i |"
	for b in $BRIDGES
	do
		cd $ROOT_DIR
		echo "----------------- Bridge $b -------------------------"
		#RESULTS="$RESULTS $b |"
		RESULTS=$(printf "$RESULTS %25s |" $i)
		RESULTS=$(printf "$RESULTS %30s |" $b)
		cd $b
		java -cp $JARS_PATH/depswap-test-harness-0.1-SNAPSHOT-jar-with-dependencies.jar se.kth.assertteam.depswap.SwapTestDep ./ "$g:$a:$v" "$g:$i:$v" $JARS_PATH
		#mvn dependency:tree -DoutputFile="tree-$b-$i" 2>1 > log-$b-$i
		#cat tree-$b-$i
		mvn clean test 2>&1 > log-$b-$i
		if [ $? -eq 0 ];
		then
			#RESULTS="$RESULTS   ${GREEN}OK${NC}\n"
			RESULTS=$(printf "$RESULTS ${GREEN}%8s${NC} |" "OK")
			RESULTS="$RESULTS ${GREEN}0${NC}\n"
			echo "Alls test passed"
		else
			cat log-$b-$i
			#RESULTS="$RESULTS   ${RED}KO${NC}\n"
			RESULTS=$(printf "$RESULTS ${RED}%8s${NC} |" "KO")
			failures=$(grep "Tests run: " log-$b-$i | tail -n 1 | cut -d ',' -f2,3 |  cut -d ' ' -f3,5 | sed 's/,/ +/' | bc)
			total=$(grep "Tests run: " log-$b-$i | tail -n 1 | cut -d ',' -f1 | cut -d ' ' -f4)
			RESULTS="$RESULTS ${RED}$failures / $total${NC}\n"
		fi
		java -cp $JARS_PATH/depswap-test-harness-0.1-SNAPSHOT-jar-with-dependencies.jar se.kth.assertteam.depswap.SwapTestDep ./ "$g:$a:$v" "$g:$b:$v" $JARS_PATH -r
	done
done
RESULTS="$RESULTS---------------------------------------------------------------------\n"

echo ""
echo ""
echo ""

printf $RESULTS


