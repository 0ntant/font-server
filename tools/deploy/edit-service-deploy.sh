#!/bin/bash

git pull https://Loksli17:{token}@github.com/Loksli17/shapy-server.git 
    && docker compose build client-editor 
    && docker compose up -d cleint-editor