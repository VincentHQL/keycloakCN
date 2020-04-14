#!/usr/bin/env bash

# 编译
mvn clean package -Dmaven.test.skip=true

# 打包运行
docker-compose up --build