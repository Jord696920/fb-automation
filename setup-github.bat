@echo off
echo 🚗 SEQ Automotive FB Automation - GitHub Setup Script
echo =====================================================

REM Check if git is available
git --version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo ❌ Git is not installed. Please install Git first from: https://git-scm.com/
    pause
    exit /b 1
)

REM Initialize git repository
echo 📁 Initializing Git repository...
git init

REM Add all files
echo 📝 Adding all files...
git add .

REM Create initial commit
echo 💾 Creating initial commit...
git commit -m "Initial commit: SEQ Automotive FB Automation Android App - Features: Floating button, OCR extraction, message generation, Samsung S24 Ultra optimized"

echo.
echo ✅ Git repository initialized!
echo.
echo 📋 Next steps:
echo 1. Create a new repository on GitHub.com
echo 2. Copy the repository URL
echo 3. Run: git remote add origin YOUR_GITHUB_URL
echo 4. Run: git branch -M main
echo 5. Run: git push -u origin main
echo.
echo 🤖 GitHub Actions will automatically build your APK!
echo 📱 Download the APK from the Actions tab on GitHub

pause