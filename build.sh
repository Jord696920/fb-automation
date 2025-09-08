#!/bin/bash

echo "🚗 SEQ Automotive FB Automation - Build Script"
echo "=============================================="

# Check if we're in the android-app directory
if [ ! -f "build.gradle" ]; then
    echo "❌ Error: Please run this script from the android-app directory"
    exit 1
fi

# Clean previous builds
echo "🧹 Cleaning previous builds..."
./gradlew clean

# Build debug APK
echo "🔨 Building debug APK..."
./gradlew assembleDebug

# Check if build was successful
if [ $? -eq 0 ]; then
    echo "✅ Build successful!"
    echo "📱 APK location: app/build/outputs/apk/debug/app-debug.apk"
    echo ""
    echo "📋 Next steps:"
    echo "1. Install on Samsung S24 Ultra: adb install app/build/outputs/apk/debug/app-debug.apk"
    echo "2. Grant overlay permission: Settings → Apps → FB Automation → Display over other apps → Allow"
    echo "3. Grant usage access: Settings → Apps → Special access → Usage access → FB Automation → Allow"
    echo "4. Toggle session ON and test with Facebook Marketplace"
else
    echo "❌ Build failed!"
    exit 1
fi