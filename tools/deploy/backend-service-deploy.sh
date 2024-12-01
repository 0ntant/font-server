#!/bin/bash
token=$(cat token)
cd /root/shapy-server

git pull "https://Loksli17:$token@github.com/Loksli17/shapy-server.git" \
    && cd backend-service/ \
    && ./mvnw clean package \
    && cd /root/shapy-server \
    && docker compose build backend-service \
    && docker compose up -d backend-service \
    && iptables -D DOCKER 1
