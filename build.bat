@echo off
echo 🚗 SEQ Automotive FB Automation - Build Script
echo ==============================================

REM Check if we're in the android-app directory
if not exist "build.gradle" (
    echo ❌ Error: Please run this script from the android-app directory
    pause
    exit /b 1
)

REM Clean previous builds
echo 🧹 Cleaning previous builds...
call gradlew.bat clean

REM Build debug APK
echo 🔨 Building debug APK...
call gradlew.bat assembleDebug

REM Check if build was successful
if %ERRORLEVEL% EQU 0 (
    echo ✅ Build successful!
    echo 📱 APK location: app\build\outputs\apk\debug\app-debug.apk
    echo.
    echo 📋 Next steps:
    echo 1. Install on Samsung S24 Ultra: adb install app\build\outputs\apk\debug\app-debug.apk
    echo 2. Grant overlay permission: Settings → Apps → FB Automation → Display over other apps → Allow
    echo 3. Grant usage access: Settings → Apps → Special access → Usage access → FB Automation → Allow
    echo 4. Toggle session ON and test with Facebook Marketplace
) else (
    echo ❌ Build failed!
    pause
    exit /b 1
)

pause