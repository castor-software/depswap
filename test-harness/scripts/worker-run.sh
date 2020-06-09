#!/bin/bash

DISPATCHER_URL="$1"

REPOS_PATH="/home/nharrand/Documents/depswap/test"
JARS_PATH="/home/nharrand/Documents/depswap/test-harness/lib"
LOGDIR="/home/nharrand/Documents/depswap/test/log"

echo "Clean up"

cd $REPOS_PATH

if [[ -f hostname.json ]]
then
    rm hostname.json
fi

echo "Get Name"
#GET A NAME
http GET $DISPATCHER_URL/getHostName > hostname.json

HOSTNAME=`jq .workerName hostname.json | sed "s/\"/'workerName:/" | sed "s/\"/'/"`
HOSTNAME='workerName:Worker-1'
echo "Get Name $HOSTNAME"

#LOOP
for i in {1..100};
do 
	#Clean up previous configuration
	echo "Loop"
	cd $REPOS_PATH
	if [[ -f cfg.json ]]
	then
		rm cfg.json
	fi

	#Get new configuration
	echo "http GET $DISPATCHER_URL/getConfiguration $HOSTNAME > cfg.json"
	http GET $DISPATCHER_URL/getConfiguration $HOSTNAME > cfg.json

	REPO=`jq .repo cfg.json | sed 's/"//g'`
	URL=`jq .url cfg.json | sed 's/"//g'`
	commit=`jq .commit cfg.json | sed 's/"//g'`
	g=`jq .g cfg.json | sed 's/"//g'`
	a=`jq .a cfg.json | sed 's/"//g'`
	v=`jq .v cfg.json | sed 's/"//g'`
	rg=`jq .rg cfg.json | sed 's/"//g'`
	ra=`jq .ra cfg.json | sed 's/"//g'`
	rv=`jq .rv cfg.json | sed 's/"//g'`

	PACKAGES=`jq .packages cfg.json | sed 's/"//g'`

	echo "repo:\"$REPO\",\"url\":\"$URL\",\"commit\":\"$commit\",\"g\":\"$g\",\"a\":\"$a\",\"v\":\"$v\",\"rg\":\"$rg\",\"ra\":\"$ra\",\"rv\":\"$rv\""


	#Get code
	if [[ ! -d $REPO ]]
	then
		git clone $URL
	fi

	#Prepare logs
	LOG_REPO="$LOGDIR/$REPO"
	LOG_FILE="$LOG_REPO/to-$rg:$ra:$rv.log"
	if [[ ! -d $LOG_REPO ]]
	then
		mkdir -p $LOG_REPO
	fi
	if [[ -f $LOG_FILE ]]
	then
		rm $LOG_FILE
	fi
	
	cd $REPO
	git checkout $commit

	#compile
	mvn compile > /dev/null 2>&1
	if [ $? -eq 0 ]; then
		COMPILE=$(echo "true")

		mvn dependency:tree -Doutput=$LOG_REPO/tree.log
		NB_DEP_TO_REPLACED=$(grep "$g:$a" $LOG_REPO/tree.log | wc -l)

		#test
		mvn test -Dsurefire.skipAfterFailureCount=1 > $LOGDIR/test.log 2>&1
		if [ $? -eq 0 ]; then
			TEST1=$(echo "true")

			NB_TESTS=$(grep "Tests run: " $LOGDIR/test.log | cut -d ',' -f1 | cut -d ' ' -f3 | paste -sd+ | bc)
			NB_TESTS=$((NB_TESTS / 2))
			echo "NB_TESTS: $NB_TESTS"
			
			CUR_DIR=$(pwd)
			#Static analysis
			echo "java -cp $JARS_PATH/depswap-test-harness-0.1-SNAPSHOT-jar-with-dependencies.jar se.kth.assertteam.depanalyzer.Analyzer $CUR_DIR $PACKAGES"
			STATIC_USAGES=$(java -cp $JARS_PATH/depswap-test-harness-0.1-SNAPSHOT-jar-with-dependencies.jar se.kth.assertteam.depanalyzer.Analyzer $CUR_DIR $PACKAGES)
			echo $STATIC_USAGES
			
			#Transform
			java -jar $JARS_PATH/depswap-test-harness-0.1-SNAPSHOT-jar-with-dependencies.jar ./ "$g:$a:$v" "$rg:$ra:$rv" $JARS_PATH
			#Re test
			mvn test -Dsurefire.skipAfterFailureCount=1 > $LOG_FILE 2>&1
			if [ $? -eq 0 ]; then
				TEST_T=$(echo "true")
			else
				TEST_T=$(echo "false")
			fi

			#Collect trace
			#java -cp /home/nharrand/Documents/yajta/yajta-offline/target/yajta-offline-2.0.0-jar-with-dependencies.jar fr.inria.offline.RemoteUserReader -i traceDir -f -o usages.json
			#TRACES=$(cat usages.json)

			#Restore
			java -jar $JARS_PATH/depswap-test-harness-0.1-SNAPSHOT-jar-with-dependencies.jar ./ "$g:$a:$v" "$rg:$ra:$rv" $JARS_PATH r
		else
			TEST1=$(echo "false")
		fi
	else
		COMPILE=$(echo "false")
	fi

	echo "COMPILE: $COMPILE, PASS_TESTS_ORIGINAL: $TEST1, PASS_TESTS_TRANSFORMED: $TEST_T, NB_TESTS: $NB_TESTS"



	#report
	echo "{\"repo\":\"$REPO\",\"url\":\"$URL\",\"commit\":\"$commit\",\"g\":\"$g\",\"a\":\"$a\",\"v\":\"$v\",\"rg\":\"$rg\",\"ra\":\"$ra\",\"rv\":\"$rv\",\"compile\":\"$COMPILE\",\"test1\":\"$TEST1\",\"nbTests\":\"$NB_TESTS\",\"testTransformed\":\"$TEST_T\", \"nbDepToReplaced\":\"$NB_DEP_TO_REPLACED\", \"staticUsages\":$STATIC_USAGES}" | http POST $DISPATCHER_URL/postResult $HOSTNAME
done

