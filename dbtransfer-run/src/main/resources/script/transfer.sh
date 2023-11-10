#!/bin/bash

# params
FILE_PATH=$1
CURRENT_PATH=$(pwd)
ABSOLUTE_FILE_PATH=$CURRENT_PATH/$FILE_PATH
JAVA_HOME=$JAVA_HOME
EXECUTE_ARGS=$FILE_PATH


#JAVA_VERSION=`java -version 2>&1 |awk 'NR==1{ gsub(/"/,""); print $3 }'`
#VERSION_NUMBER=${JAVA_VERSION//./}
#pos=`expr index "$VERSION_NUMBER" "_"`
#pos=`expr $pos - 1`
#VERSION_NUMBER=`expr substr "$VERSION_NUMBER" 1 $pos`

# if [ $VERSION_NUMBER -lt 180 ]; then
#	echo "JDK version must be above level 1.7"
#	exit 0
#fi

if [ ! -n "$FILE_PATH" ]; then
	echo "ERROR: Please specify the configuration file"
	exit 0;
fi

if [ ! -f $ABSOLUTE_FILE_PATH ]; then
    if [ ! -f $FILE_PATH ]; then
       echo "ERROR: file does not exist; error in file path: "$FILE_PATH;
       exit 0
    fi
else
	EXECUTE_ARGS=$ABSOLUTE_FILE_PATH
fi
if [ ! -n "$JAVA_HOME" ]; then
	echo "ERROR: No JAVA_HOME was found"
	exit 0
fi

java -classpath transfer.jar com.bird.main.Application $EXECUTE_ARGS