#!/bin/bash
mkdir coverage
docker run --rm -v `pwd`/coverage:/coverage-out  risc scripts/test.sh
