# Project Setup Instructions

This document provides step-by-step instructions for setting up your project.

## Initiate docker containers without scale:

### windows:
```bash
docker compose down -v && docker system prune -a --volumes && gradlew clean && gradlew build -x test && docker compose up -d
```

### linux:  
```bash 
sudo ./lunch_docker.sh
```

## Initiate docker containers with scale:``

### linux:
```bash 
sudo docker compose up -d --scale postgresdb=3 --scale iot-service=3
```