#!/usr/bin/env bash

set -e

function main {
    pushd "$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/.."

    local sbt
    if [ -z ${CI+x} ]; then
        sbt=sbt
    else
        sbt=ci/sbt-runner
    fi

    $sbt 'set assembly / test := {}' assembly

    docker build --no-cache -t stocknews -f docker/Dockerfile .

    popd
}

main $@
