#!/bin/bash
set -e

mvn clean test -Pjboss-embedded
mvn clean test -Ptomcat-embedded

