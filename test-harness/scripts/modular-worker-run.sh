#!/bin/bash


#local config
DISPATCHER_URL="$1"

REPOS_PATH="/home/nharrand/Documents/depswap/test"
JARS_PATH="/home/nharrand/Documents/depswap/test-harness/lib"
LOGDIR="/home/nharrand/Documents/depswap/test/log"
TASKS="/home/nharrand/Documents/depswap/test-harness/scripts/tasks"

echo "Clean up"

cd $REPOS_PATH

if [[ -f hostname.json ]]
then
    rm hostname.json
fi

#connect to remote and get info
echo "Get Name"
#GET A NAME
http GET $DISPATCHER_URL/getHostName > hostname.json

HOSTNAME=`jq .workerName hostname.json | sed "s/\"/'workerName:/" | sed "s/\"/'/"`
HOSTNAME='workerName:Worker-1'
echo "Get Name $HOSTNAME"

while true
do
	echo " ------------------------ New Config ------------------------ "
	cd $REPOS_PATH
	if [[ -f cfg.json ]]
	then
		rm cfg.json
	fi

	#connect to remote and get work
	#Get new configuration
	echo "http GET $DISPATCHER_URL/getConfiguration $HOSTNAME > cfg.json"
	http GET $DISPATCHER_URL/getConfiguration $HOSTNAME > cfg.json

	#If no work exit
	if [[ ! -f cfg.json ]]
	then
		echo "break"
		break
	fi
	if [[ ! -s cfg.json ]]
	then
		echo "break empty"
		break
	fi


	#for task
	#for raw in $(jq -r '.steps | keys[] as $k | "\($k)|\(.[$k])"' cfg.json); do
	for raw in $(jq -c '.steps[]' cfg.json); do
		step=$(echo $raw | jq -r .step)
		echo " ------------ $step ------------ "
		echo " dir: $PWD "
		
		if [ $step == "cd" ]; then
			result="{}"
			repo=$(echo $raw | jq -r .repo)
			cd $repo
		else
			#Clean up
			if [[ -f $step.in ]]
			then
				rm $step.in
			fi
			if [[ -f $step.out ]]
			then
				rm $step.out
			fi
			
			echo $raw > $step.in
			$TASKS/$step.sh
			if [ $? -ne 0 ]; then
				result="{\"failure\":true}"
				echo "{\"step\":\"$step\",\"result\":$result}" | http POST $DISPATCHER_URL/postResult $HOSTNAME
				break
			else
				if [[ -f $step.out ]]
				then
					result=$(cat $step.out)
				else
					result="{}"
				fi
				
			fi
		fi
		echo "{\"step\":\"$step\",\"result\":$result}" | http POST $DISPATCHER_URL/postResult $HOSTNAME
	done
		#get task config
		#call script with param
		#report
done

echo "Done"
