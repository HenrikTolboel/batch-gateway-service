SHELL=/bin/bash
PROJECT_NAME=$(shell grep "rootProject.name" settings.gradle.kts | sed "s/rootProject.name = //g")
TEST_FILES=$(wildcard *.test.yml)
USER_ID=$(shell id -u)
GROUP_ID=$(shell id -g)

#BUILDER_IMAGE=${PROJECT_NAME}-builder:latest
BUILDER_IMAGE=ghcr.io/henriktolboel/gradle-builder:17
BIND_DOCKER=-v /var/run/docker.sock:/var/run/docker.sock
# https://docs.docker.com/storage/volumes/
BIND_GRADLE=--mount source=gradlevolume,target=/.gradle
BIND_PWD=-v "${PWD}":"${PWD}" -w "${PWD}"
USER_GROUP=--env RUN_USER_ID=${USER_ID} --env RUN_GROUP_ID=${GROUP_ID}
NETWORK=--network host --add-host=host.docker.internal:host-gateway
GRADLE_RUNNER=docker run --rm ${BIND_DOCKER} ${BIND_GRADLE} ${BIND_PWD} ${USER_GROUP} ${NETWORK} ${BUILDER_IMAGE}
GRADLE_SHELL=docker run -it --rm ${BIND_DOCKER} ${BIND_GRADLE} ${BIND_PWD} ${USER_GROUP} ${NETWORK} ${BUILDER_IMAGE}

ifndef BRANCH_NAME
BRANCH_NAME_PAR=
else
BRANCH_NAME_PAR=-Pbranchname="${BRANCH_NAME}"
endif
ifndef BUILD_NUMBER
BUILD_NUMBER_PAR=
else
BUILD_NUMBER_PAR=-PbuildNumber="${BUILD_NUMBER}"
endif

all: build

.PHONY: build
build:
	./gradlew build --scan --rerun-tasks
	@[ -f build/dockerImageName.txt ] && docker image ls `cat build/dockerImageName.txt` || true

buildCI:
	./gradlew clean build -PisCIBuild=1 ${BRANCH_NAME_PAR} ${BUILD_NUMBER_PAR} --scan --rerun-tasks
	@[ -f build/dockerImageName.txt ] && docker image ls `cat build/dockerImageName.txt` || true

# Target for locally fixing buildserver problems. Use with care!
buildCI-force:
	./gradlew clean build -PisCIBuild=1 ${BRANCH_NAME_PAR} ${BUILD_NUMBER_PAR} -x check -x sonar -x sonarqube -x jacocoTestReport --scan --rerun-tasks
	@[ -f build/dockerImageName.txt ] && docker image ls `cat build/dockerImageName.txt` || true

build-in-docker:
	${GRADLE_RUNNER} ./gradlew clean build --scan --rerun-tasks --no-daemon
	@[ -f build/dockerImageName.txt ] && docker image ls `cat build/dockerImageName.txt` || true

buildCI-in-docker:
	${GRADLE_RUNNER} ./gradlew clean build -PisCIBuild=1 ${BRANCH_NAME_PAR} ${BUILD_NUMBER_PAR} --scan --rerun-tasks --no-daemon
	@[ -f build/dockerImageName.txt ] && docker image ls `cat build/dockerImageName.txt` || true

buildshell:
	${GRADLE_SHELL} bash

.PHONY: push
# We want to have 2 images pushed: one with branchname (e.g. "master" as tag), and one with branchname and buildno (e.g. "master-123" as tag)
# normally when running in Jenkins, we have supplied a jenkins buildno, and the gradle build will build the last one.
PUSH_IMAGE := $(shell [ -f build/dockerImageName.txt ] && cat build/dockerImageName.txt)
PUSH_IMAGE2 := $(shell [ -f build/dockerImageName.txt ] && cat build/dockerImageName.txt | sed -E 's/-[0-9]*$$//g' )
push:
	[ -f build/dockerImageName.txt ] && docker push $(PUSH_IMAGE)
	[ -f build/dockerImageName.txt ] && [[ "$(PUSH_IMAGE)" != "$(PUSH_IMAGE2)" ]] && \
docker tag $(PUSH_IMAGE) $(PUSH_IMAGE2) && \
docker push $(PUSH_IMAGE2) || true



.PHONY: run start stop restart
run:
	docker run -it --rm -p 8080:8080 -p 8081:8081 `cat build/dockerImageName.txt`
start:
	docker run -d -it --rm --name managed-pairings-dev -p 8080:8080 -p 8081:8081 `cat build/dockerImageName.txt`
stop:
	-docker stop managed-pairings-dev
restart: stop start

.PHONY: docker-compose-test ${TEST_FILES}
docker-compose-test: ${TEST_FILES}
*.test.yml: #test-clean
	USER_ID=${USER_ID} IMAGE=`cat build/dockerImageName.txt` \
		docker compose -p ${PROJECT_NAME} -f $@ up -V -t 2 \
			--force-recreate --always-recreate-deps \
			--exit-code-from sut --abort-on-container-exit --attach sut

test-clean:
	echo ${USER_ID}
	echo ${RUN_ID}
	docker ps -f name=${RUN_ID} -a -q | xargs -n 1 -r docker rm -f
