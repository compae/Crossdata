#!/usr/bin/env bash

if [ "$#" -lt 1 ]; then
    echo "Usage: crossdata-deploy.sh /path/to/conf/server-application.conf"
    exit 1
fi

configFile="$1"

echo "Configuration File: $1"

function process_line {
    #echo "Args: $#"
    if [ "$#" -lt 2 ]
    then
        echo "ERROR: Bad line format"
        exit 1
    else
        KEY=$1
        echo "KEY: $KEY"
        VALUE="${2%\"}"
        VALUE="${VALUE#\"}"
        echo "VALUE: $VALUE"
        if [[ $KEY == spark.path ]]
        then
            XD_SPARK_PATH=$VALUE
        fi
        if [[ $KEY == spark.mainClass ]]
        then
            XD_APP_MAINCLASS=$VALUE
        fi
        if [[ $KEY == spark.master ]]
        then
            XD_SPARK_MASTER=$VALUE
        fi
        if [[ $KEY == server.config ]]
        then
            XD_SERVER_CONFIG=$VALUE
        fi
        if [[ $KEY == spark.appJar ]]
        then
            XD_JAR_PATH=$VALUE
        fi
        if [[ $KEY == spark.args ]]
        then
            XD_APP_ARGS=$VALUE
        fi
    fi
}

while read line
do
    if [[ $line == crossdata-deploy* ]] ;
    then
        #echo "CONFIG: $line"
        tmp1=${line/crossdata-deploy./}
        tmp1="${tmp1/=/}"
        #echo "tmp1 = $tmp1"
        process_line $tmp1
    fi

done < $1

COMMAND="$XD_SPARK_PATH/bin/spark-submit --class $XD_APP_MAINCLASS --master $XD_SPARK_MASTER --files $XD_SERVER_CONFIG --jars $XD_JAR_PATH $XD_JAR_PATH $XD_APP_ARGS"

echo "COMMAND=$COMMAND"

$COMMAND

