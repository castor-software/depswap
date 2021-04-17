#!/bin/zsh

OUTPUT="assert-results.csv"

echo "library,nbAssert,assertType" > $OUTPUT

DIRS=('json-simple' 'json' 'gson' 'jackson-databind')

for d in $DIRS
do
	find $d/**/src/test/java -name "*.java" | xargs grep "//ARGO_" | sed 's|//ARGO_|#|' | cut -d '#' -f2 | sed "s/[^[:alpha:][_]]//g" | sed 's/\r//g' | sort | uniq -c | sed 's/\s\{1,\}/,/g' | sed "s/^/$d/" >>  $OUTPUT
done
