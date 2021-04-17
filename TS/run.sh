#!/bin/zsh


if [ "$#" -lt 1 ]; then
  echo "Usage: $0 <path_to_libs> [implem] [bridge]" >&2
  exit 1
fi

ROOT_DIR=$(pwd)
CSV_OUTPUT="$ROOT_DIR/differential_testing_results.csv"
echo "Implementation,Bridge,Outcome,Failures" > $CSV_OUTPUT

JARS_PATH=$1
echo "Path to lib dir: $JARS_PATH"

#BRIDGES=('json-simple' 'json' 'gson' 'jackson-databind')
#BRIDGES=('gson')
BRIDGES=('json-simple')
# 'json'  'jackson-databind' 'gson'
#IMPLEMENTATIONS=('yasjf4j-argo' 'yasjf4j-cookjson' 'yasjf4j-corn')
IMPLEMENTATIONS=('yasjf4j-nothing' 'yasjf4j-argo' 'yasjf4j-cookjson' 'yasjf4j-corn' 'yasjf4j-fastjson' 'yasjf4j-flexjson' 'yasjf4j-genson' 'yasjf4j-gson' 'yasjf4j-jackson-databind' 'yasjf4j-jjson' 'yasjf4j-johnzon' 'yasjf4j-json' 'yasjf4j-jsonij' 'yasjf4j-json-io' 'yasjf4j-json-lib' 'yasjf4j-jsonp' 'yasjf4j-json-simple' 'yasjf4j-jsonutil' 'yasjf4j-mjson' 'yasjf4j-progbase-json' 'yasjf4j-sojo')




echo "IMPLEMENTATIONS: $IMPLEMENTATIONS"
echo "BRIDGES: $BRIDGES"


g="se.kth.castor"
a="yasjf4j-json"
v="1.0-SNAPSHOT"

GREEN='\033[0;32m'
GREY='\033[0;37m'
RED='\033[0;31m'
NC='\033[0m' # No Color



RESULTS="-------------------- Results ----------------------------\n"
RESULTS="\n"
RESULTS=$(printf "$RESULTS %25s |" "Implem")
RESULTS=$(printf "$RESULTS %30s |" "Bridge")
RESULTS=$(printf "$RESULTS %8s |" "Outcome")
RESULTS="$RESULTS Failures\n"
RESULTS="$RESULTS-----------------------------------------------------------------------------------\n"

for i in $IMPLEMENTATIONS
do
	echo "----------------- Implem $i -------------------------"
	#RESULTS="$RESULTS $i |"
	for b in $BRIDGES
	do
		cd $ROOT_DIR
		bb=$(echo $b | sed 's/-over-yasjf4j//')
		ii=$(echo $i | sed 's/yasjf4j-//')
		echo "----------------- Bridge $b -------------------------"
		#RESULTS="$RESULTS $b |"
		RESULTS=$(printf "$RESULTS %25s |" $i)
		RESULTS=$(printf "$RESULTS %30s |" $b)
		cd $b
		#echo "java -cp $JARS_PATH/depswap-test-harness-0.1-SNAPSHOT-jar-with-dependencies.jar se.kth.assertteam.depswap.SwapTestDep ./ "$g:yasjf4j-$b:$v" "$g:$i:$v" $JARS_PATH"
		java -cp $JARS_PATH/depswap-test-harness-0.1-SNAPSHOT-jar-with-dependencies.jar se.kth.assertteam.depswap.SwapTestDep ./ "$g:yasjf4j-$b:$v" "$g:$i:$v" $JARS_PATH
		#mvn dependency:tree -DoutputFile="tree-$b-$i.log" 2>1 > log-$b-$i.log
		#cat tree-$b-$i.log
		mvn test 2>&1 > log-$b-$i.log
		if [ $? -eq 0 ];
		then
			#RESULTS="$RESULTS   ${GREEN}OK${NC}\n"
			RESULTS=$(printf "$RESULTS ${GREEN}%8s${NC} |" "OK")
			RESULTS="$RESULTS ${GREEN}0${NC}\n"
			echo "Alls test passed"
			echo "$i,$b,true,0" >> $CSV_OUTPUT
		else
			cat log-$b-$i.log
			#RESULTS="$RESULTS   ${RED}KO${NC}\n"
			RESULTS=$(printf "$RESULTS ${RED}%8s${NC} |" "KO")
			failures=$(grep --text "Tests run: " log-$b-$i.log | tail -n 1 | cut -d ',' -f2,3 |  cut -d ' ' -f3,5 | sed 's/,/ +/' | bc)
			total=$(grep --text "Tests run: " log-$b-$i.log | tail -n 1 | cut -d ',' -f1 | cut -d ' ' -f4)
			RESULTS="$RESULTS ${RED}$failures / $total${NC}\n"
			echo "$i,$b,true,$failures" >> $CSV_OUTPUT
		fi
		java -cp $JARS_PATH/depswap-test-harness-0.1-SNAPSHOT-jar-with-dependencies.jar se.kth.assertteam.depswap.SwapTestDep ./ "$g:yasjf4j-$b:$v" "$g:$i:$v" $JARS_PATH -r
		#fi
	done
done
RESULTS="$RESULTS-----------------------------------------------------------------------------------\n"

echo ""
echo ""
echo ""

printf $RESULTS


