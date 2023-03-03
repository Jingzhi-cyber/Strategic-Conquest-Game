#!/bin/bash

# first terminate any old ones
docker kill risc-651
docker rm risc-651

# now run the new one
docker run -d --name risc-651 -p 1651:1651 -t risc ./gradlew run


