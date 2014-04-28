#!/bin/bash
set -e

mvn test -Pjboss-embedded
mvn test -Ptomcat-embedded

