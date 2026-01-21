@echo off
setlocal enabledelayedexpansion

echo [INFO] Starting Easemob App Server Environment...

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

:: 2. Load DOCKER_MIRROR from .env if set
set "ENV_MIRROR="
for /f "tokens=1,2 delims==" %%a in (.env) do (
    if "%%a"=="DOCKER_MIRROR" set "ENV_MIRROR=%%b"
)

:: 3. Determine Mirror Strategy
:: Priority: 1. .env config  2. Huawei Cloud (Reliable) 3. DaoCloud 4. Official
if not "!ENV_MIRROR!"=="" (
    set "DOCKER_MIRROR=!ENV_MIRROR!"
    echo [INFO] Using configured Docker Mirror: !DOCKER_MIRROR!
) else (
    echo [INFO] No Docker Mirror configured in .env. Using Huawei Cloud mirror.
    set "DOCKER_MIRROR=swr.cn-north-4.myhuaweicloud.com/ddn-k8s/docker.io"
)

:: 4. Attempt Start
echo [INFO] Building and starting services using mirror: !DOCKER_MIRROR!
echo [INFO] This may take a few minutes for the first run (Maven Build)...

docker-compose up -d --build

if %ERRORLEVEL% neq 0 (
    echo.
    echo [WARN] Start failed with mirror: !DOCKER_MIRROR!
    
    :: Retry Strategy
    echo [INFO] Retrying with alternative mirrors...
    
    :: List of mirrors to try (space separated)
    set "MIRROR_LIST=docker.m.daocloud.io docker.1panel.live docker.rainbond.cc docker.io"
    
    for %%M in (!MIRROR_LIST!) do (
        if "%%M" neq "!DOCKER_MIRROR!" (
             echo.
             echo [INFO] Retrying with mirror: %%M
             set "DOCKER_MIRROR=%%M"
             docker-compose up -d --build
             
             if !ERRORLEVEL! equ 0 (
                 goto :success
             ) else (
                 echo [WARN] Mirror %%M failed.
             )
        )
    )
    
    :: All failed
    echo.
    echo [ERROR] All mirror attempts failed.
    echo [TIP] Your Docker Daemon might be configured with a broken registry mirror (e.g. ustc).
    echo [TIP] Please check your internet connection or try a VPN.
    pause
    exit /b 1
)

:success
echo.
echo [SUCCESS] Environment started!
echo.
echo App Server: http://localhost:8096
echo MySQL:      localhost:3307
echo Redis:      localhost:6379
echo.
echo Logs:       docker-compose logs -f
pause
