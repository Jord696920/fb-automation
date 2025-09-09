#!/bin/bash

echo "🚗 SEQ Automotive FB Automation - GitHub Setup Script"
echo "====================================================="

# Check if git is available
if ! command -v git &> /dev/null; then
    echo "❌ Git is not installed. Please install Git first."
    exit 1
fi

# Initialize git repository
echo "📁 Initializing Git repository..."
git init

# Add all files
echo "📝 Adding all files..."
git add .

# Create initial commit
echo "💾 Creating initial commit..."
git commit -m "Initial commit: SEQ Automotive FB Automation Android App

Features:
- Floating button that appears only on Facebook/Marketplace
- OCR extraction of car model and seller name
- Automatic message generation for Jordan Lansbury/SEQ Automotive
- Copy to clipboard functionality
- Contacted cars history
- Samsung S24 Ultra optimized"

echo ""
echo "✅ Git repository initialized!"
echo ""
echo "📋 Next steps:"
echo "1. Create a new repository on GitHub.com"
echo "2. Copy the repository URL"
echo "3. Run: git remote add origin <YOUR_GITHUB_URL>"
echo "4. Run: git branch -M main"
echo "5. Run: git push -u origin main"
echo ""
echo "🤖 GitHub Actions will automatically build your APK!"
echo "📱 Download the APK from the Actions tab on GitHub"