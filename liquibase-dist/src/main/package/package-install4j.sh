#!/usr/bin/env bash

###################################################################
## This script creates the installer files given an unziped directory
###################################################################

set -e
#set -x

if [ -z ${1+x} ]; then
  echo "This script requires the version to be passed to it. Example: package-install4j.sh 4.5.0";
  exit 1;
fi
version=$1

if [ -z ${INSTALL4J_LICENSE+x} ]; then
  echo "INSTALL4J_LICENSE must be set";
  exit 1
fi


install4jc="/usr/local/bin/install4jc"

INSTALL4J_ARGS="$INSTALL4J_ARGS --release=$version -D liquibaseVersion=$version"

if [ ! -f target/keys ]; then
  echo "WARNING: not signing installer because target/keys directory does not exist."
  INSTALL4J_ARGS="$INSTALL4J_ARGS --disable-signing"
fi

"$install4jc" --license=$INSTALL4J_LICENSE
"$install4jc" $INSTALL4J_ARGS src/main/install4j/liquibase.install4j
