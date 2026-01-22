@echo off
setlocal enabledelayedexpansion

echo [INFO] Starting Easemob App Server (All-in-One Docker)...

:: 1. Check if .env exists, create if missing
if not exist .env (
    if exist .env.example (
        echo [WARN] .env file not found. Creating from .env.example...
        copy .env.example .env
        echo [INFO] Created .env file. Please update it with your App Key and Credentials.
        echo [INFO] Press any key to continue after you have checked the .env file...
        pause
    ) else (
        echo [ERROR] .env file not found and .env.example is missing.
        echo [TIP] Please create a .env file with APPLICATION_APPKEY, APPLICATION_CLIENTID, etc.
        pause
        exit /b 1
    )
)

:: 2. Build the Docker Image
echo.
echo [INFO] Building Docker Image (easemob/im-server-allinone:latest)...
echo [INFO] This might take a few minutes...
docker build -f Dockerfile.easemob -t easemob/im-server-allinone:latest .

if %ERRORLEVEL% neq 0 (
    echo.
    echo [ERROR] Docker build failed.
    echo [TIP] Please check if Docker Desktop is running.
    pause
    exit /b 1
)

:: 3. Clean up existing container
echo.
echo [INFO] Cleaning up old container...
docker rm -f easemob-server-instance >nul 2>&1

:: 4. Run the Container
echo.
echo [INFO] Starting Container...
docker run -d ^
  --name easemob-server-instance ^
  -p 8096:8096 ^
  -p 3307:3306 ^
  -p 6379:6379 ^
  --env-file .env ^
  easemob/im-server-allinone:latest

if %ERRORLEVEL% neq 0 (
    echo.
    echo [ERROR] Failed to start container.
    pause
    exit /b 1
)

:success
echo.
echo [SUCCESS] Environment started!
echo.
echo App Server: http://localhost:8096
echo MySQL:      localhost:3307 (User: root / Pass: cy990810)
echo Redis:      localhost:6379
echo.
echo Logs:       docker logs -f easemob-server-instance
pause
