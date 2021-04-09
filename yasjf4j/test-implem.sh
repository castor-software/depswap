#!/bin/zsh


if [ "$#" -lt 1 ]; then
  echo "Usage: $0 <path_to_libs> [implem] [bridge]" >&2
  exit 1
fi

ROOT=$(pwd)
CSV_OUTPUT="$ROOT/differential_testing_results.csv"
echo "Implementation,Bridge,Outcome,Failures" > $CSV_OUTPUT

JARS_PATH=$1
echo "Path to lib dir: $JARS_PATH"

BRIDGES=('json-simple-over-yasjf4j' 'json-over-yasjf4j' 'gson-over-yasjf4j' 'jackson-databind-over-yasjf4j')
#BRIDGES=('json-simple-over-yasjf4j' 'json-over-yasjf4j' 'gson-over-yasjf4j')
TMP_LIST=$(ls | grep "yasjf4j-" | grep -v "yasjf4j-api" | grep -v "yasjf4j-nothing")
IMPLEMENTATIONS=("${(@f)$(echo $TMP_LIST)}")

if [ "$#" -gt 1 ]; then
	b=$2
	if [[ ${BRIDGES[(ie)$b]} -le ${#BRIDGES} ]] ; then
		echo "Run with only bridge: $2"
		BRIDGES=$2
	else
		IMPLEMENTATIONS=$2
	fi
fi

echo "IMPLEMENTATIONS: $IMPLEMENTATIONS"
echo "BRIDGES: $BRIDGES"


ROOT_DIR=$(pwd)
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
	cd $ROOT_DIR
	RESULTS=$(printf "$RESULTS %25s |\n" $i)
	RESULTS=$(printf "$RESULTS %30s |\n" "-----")
	cd $i
	mvn clean install -DskipTests 2>&1 >> log-$i.log
	mvn test 2>&1 >> log-$i.log
	if [ $? -eq 0 ];
	then
		#RESULTS="$RESULTS   ${GREEN}OK${NC}\n"
		RESULTS=$(printf "$RESULTS ${GREEN}%8s${NC} |" "OK")
		RESULTS="$RESULTS ${GREEN}0${NC}\n"
		echo "Alls test passed"
	else
		cat log-$i.log
		#RESULTS="$RESULTS   ${RED}KO${NC}\n"
		RESULTS=$(printf "$RESULTS ${RED}%8s${NC} |" "KO")
		failures=$(grep "Tests run: " log-$i.log | tail -n 1 | cut -d ',' -f2,3 |  cut -d ' ' -f3,5 | sed 's/,/ +/' | bc)
		total=$(grep "Tests run: " log-$i.log | tail -n 1 | cut -d ',' -f1 | cut -d ' ' -f4)
		RESULTS="$RESULTS ${RED}$failures / $total${NC}\n"
	fi
	#RESULTS="$RESULTS $i |"
	for b in $BRIDGES
	do
		cd $ROOT_DIR
		bb=$(echo $b | sed 's/-over-yasjf4j//')
		ii=$(echo $i | sed 's/yasjf4j-//')
		#if [ $bb = $ii ];
		#then
		#	echo "----------- Skip $b for implem $i -------------------"
		#		RESULTS=$(printf "$RESULTS %25s |" $i)
		#		RESULTS=$(printf "$RESULTS %30s |" $b)
		#		RESULTS=$(printf "$RESULTS ${GREY}%8s${NC} |" "NA")
		#		RESULTS="$RESULTS ${GREY}NA${NC}\n"
		#		echo "Skipped"
		#else
			echo "----------------- Bridge $b -------------------------"
			#RESULTS="$RESULTS $b |"
			RESULTS=$(printf "$RESULTS %25s |" $i)
			RESULTS=$(printf "$RESULTS %30s |" $b)
			cd $b
			java -cp $JARS_PATH/depswap-test-harness-0.1-SNAPSHOT-jar-with-dependencies.jar se.kth.assertteam.depswap.SwapTestDep ./ "$g:$a:$v" "$g:$i:$v" $JARS_PATH
			#mvn dependency:tree -DoutputFile="tree-$b-$i.log" 2>1 > log-$b-$i.log
			#cat tree-$b-$i.log
			mvn clean test 2>&1 > log-$b-$i.log
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
				failures=$(grep "Tests run: " log-$b-$i.log | tail -n 1 | cut -d ',' -f2,3 |  cut -d ' ' -f3,5 | sed 's/,/ +/' | bc)
				total=$(grep "Tests run: " log-$b-$i.log | tail -n 1 | cut -d ',' -f1 | cut -d ' ' -f4)
				RESULTS="$RESULTS ${RED}$failures / $total${NC}\n"
				echo "$i,$b,false,$failures" >> $CSV_OUTPUT
			fi
			java -cp $JARS_PATH/depswap-test-harness-0.1-SNAPSHOT-jar-with-dependencies.jar se.kth.assertteam.depswap.SwapTestDep ./ "$g:$a:$v" "$g:$b:$v" $JARS_PATH -r
		#fi
	done
done
RESULTS="$RESULTS-----------------------------------------------------------------------------------\n"

echo ""
echo ""
echo ""

printf $RESULTS


