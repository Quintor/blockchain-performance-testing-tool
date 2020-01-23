#!/usr/bin/env sh

cd ./performance-testing-tool && mvn clean verify sonar:sonar -Pcoverage  -Dsonar.projectKey=quintor_bptt
