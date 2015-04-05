#!/bin/bash

# activator clean and then dist
activator clean
activator dist

# docker compose start-up
docker-compose up -d