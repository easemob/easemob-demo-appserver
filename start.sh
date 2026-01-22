#!/bin/bash

# Define colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${GREEN}[INFO] Starting Easemob App Server (All-in-One Docker)...${NC}"

# 1. Check if .env exists, create if missing
if [ ! -f .env ]; then
    if [ -f .env.example ]; then
        echo -e "${YELLOW}[WARN] .env file not found. Creating from .env.example...${NC}"
        cp .env.example .env
        echo -e "${GREEN}[INFO] Created .env file. Please update it with your App Key and Credentials.${NC}"
        read -p "Press any key to continue after you have checked the .env file..."
    else
        echo -e "${RED}[ERROR] .env file not found and .env.example is missing.${NC}"
        echo -e "${YELLOW}[TIP] Please create a .env file with APPLICATION_APPKEY, APPLICATION_CLIENTID, etc.${NC}"
        exit 1
    fi
fi

# 2. Build the Docker Image
echo
echo -e "${GREEN}[INFO] Building Docker Image (easemob/im-server-allinone:latest)...${NC}"
echo -e "${GREEN}[INFO] This might take a few minutes...${NC}"

docker build -f Dockerfile.easemob -t easemob/im-server-allinone:latest .

if [ $? -ne 0 ]; then
    echo
    echo -e "${RED}[ERROR] Docker build failed.${NC}"
    echo -e "${YELLOW}[TIP] Please check if Docker Desktop is running.${NC}"
    exit 1
fi

# 3. Clean up existing container
echo
echo -e "${GREEN}[INFO] Cleaning up old container...${NC}"
docker rm -f easemob-server-instance >/dev/null 2>&1

# 4. Run the Container
echo
echo -e "${GREEN}[INFO] Starting Container...${NC}"
docker run -d \
  --name easemob-server-instance \
  -p 8096:8096 \
  -p 3307:3306 \
  -p 6379:6379 \
  --env-file .env \
  easemob/im-server-allinone:latest

if [ $? -ne 0 ]; then
    echo
    echo -e "${RED}[ERROR] Failed to start container.${NC}"
    exit 1
fi

# Success
echo
echo -e "${GREEN}[SUCCESS] Environment started!${NC}"
echo
echo "App Server: http://localhost:8096"
echo "MySQL:      localhost:3307 (User: root / Pass: cy990810)"
echo "Redis:      localhost:6379"
echo
echo "Logs:       docker logs -f easemob-server-instance"
