#!/bin/bash

echo ">>> 1. Git Pull..."
git pull origin main

echo ">>> 2. Build Gradle..."
./gradlew clean build

# 빌드 실패 시 중단
if [ $? -ne 0 ]; then
    echo "❌ Build Failed! Aborting deployment."
    exit 1
fi

echo ">>> 3. Stop Current Process..."
# application.properties의 server.port=8088 기준
SERVER_PORT=8088
PID=$(lsof -ti:$SERVER_PORT)

if [ -n "$PID" ]; then
    kill -9 $PID
    echo " -> Killed process (PID: $PID) on port $SERVER_PORT"
else
    echo " -> No process running on port $SERVER_PORT"
fi

echo ">>> 4. Start Application..."
# build 명령어로 생성된 jar 경로 확인 (libs 폴더 내)
JAR_PATH=build/libs/NYPIYouthChild-1.0.0.jar

if [ -f "$JAR_PATH" ]; then
    nohup java -jar $JAR_PATH > /dev/null 2>&1 &
    echo "✅ Deployment Started! (Running in background)"
else
    echo "❌ Error: JAR file not found at $JAR_PATH"
fi
