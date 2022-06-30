#!/usr/bin/env /bin/sh

set -e

function main {
  java -jar /stocknews.jar $@
}

main $@
