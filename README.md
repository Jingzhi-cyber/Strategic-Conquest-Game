# ece651-spr23-team6-risc
========================================

The code in this project is for a web service - RISC board game

This project includes CI/CD using Duke's Gitlab and Duke's VM.

Maintainer: ece651-spr23-team6

[![pipeline status](https://gitlab.oit.duke.edu/mw515/ece651-spr23-team6-risc/badges/main/pipeline.svg)](https://gitlab.oit.duke.edu/mw515/ece651-spr23-team6-risc/-/commits/main)

[![coverage report](https://gitlab.oit.duke.edu/mw515/ece651-spr23-team6-risc/badges/main/coverage.svg)](https://gitlab.oit.duke.edu/mw515/ece651-spr23-team6-risc/-/commits/main)

[Detailed coverage](https://mw515.pages.oit.duke.edu/ece651-spr23-team6-risc/dashboard.html)

## Usage
```bash
./gradlew build
./gradlew installDist

launch server: ./server/build/install/server/bin/server <server_port> <player_num>
launch client: ./client/build/install/client/bin/client <server_addr> <server_port>
```


```bash
# port 1651 inside docker container
# port 6651 localhost in the vm
netcat -N -w 1 vcm-30756.vm.duke.edu 6651
```

### Some useful tips during development
```bash
git push -o ci.skip # not run any CI pipelines on this push
```


