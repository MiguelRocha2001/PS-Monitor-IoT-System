#!/bin/bash

echo "Exeting docker containers..."
docker compose down -volumes5

# Stop and remove all containers
echo "Stopping and removing all containers..."
docker stop $(docker ps -a -q) >/dev/null 2>&1
docker rm $(docker ps -a -q) >/dev/null 2>&1

# Remove all images
echo "Removing all images..."
docker rmi -f $(docker images -aq) >/dev/null 2>&1
docker system prune -a --volumes >/dev/null 2>&1

echo "Re-starting docker service..."
sudo sudo systemctl restart docker

echo "Cleaning Gradle cache..."
gradle clean

echo "Gradle build..."
gradle build -x test

echo "Building docker images..."
docker compose up

echo "Finished script correctly!"
