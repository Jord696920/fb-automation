# SEQ Automotive Facebook Marketplace Automation

Android app for Samsung S24 Ultra that automates Facebook Marketplace car inquiries.

## Features

✅ **Floating Button** - Small circle that appears ONLY when Facebook or Facebook Marketplace is open
✅ **Sessionable Button** - Easy toggle on/off for car hunting sessions  
✅ **OCR Extraction** - Extracts car model and seller first name from screenshots
✅ **Message Template** - Generates exact message format for Jordan Lansbury/SEQ Automotive
✅ **Copy to Clipboard** - Formatted message ready to paste
✅ **Simple List** - Previous cars contacted with date/time

## Setup Instructions

### 1. Build the App
```bash
cd android-app
./gradlew assembleDebug
```

### 2. Install on Samsung S24 Ultra
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

### 3. Grant Permissions
The app requires two critical permissions:

**Overlay Permission:**
- Settings → Apps → FB Automation → Display over other apps → Allow

**Usage Access Permission:**
- Settings → Apps → Special access → Usage access → FB Automation → Allow

### 4. Usage Workflow

1. **Open the FB Automation app**
2. **Toggle "Car Hunting Session" ON**
3. **Open Facebook or Facebook Marketplace**
4. **Floating button appears automatically**
5. **Navigate to a car listing**
6. **Tap the floating button to:**
   - Take screenshot
   - Extract car model (e.g., "Hyundai Tucson")
   - Extract seller name (e.g., "Eugene")
   - Generate message with exact template
   - Copy to clipboard
   - Save to contacted cars list

### 5. Message Template
```
Hi [NAME],
I'm Jordan Lansbury from SEQ Automotive. We're interested in purchasing your [CAR] and would like to offer $15,000. If this works for you, we can arrange a quick inspection (We come to you) and handle all transfers without a roadworthy and the payment processed before we pick up the vehicle.

Feel free to contact me at 0408187145 with any questions.

Best regards,
Jordan Lansbury
SEQ Automotive
www.seqautomotive.com.au
```

## Technical Details

- **Target Device:** Samsung S24 Ultra
- **Min SDK:** 26 (Android 8.0)
- **Target SDK:** 34 (Android 14)
- **OCR Engine:** Google ML Kit Text Recognition
- **Database:** Room (SQLite)
- **Architecture:** Service-based with floating overlay

## Key Components

- `MainActivity` - Main interface with session toggle and contacted cars list
- `FloatingButtonService` - Manages floating button overlay
- `AppDetectionService` - Detects Facebook app state
- `OCRProcessor` - Extracts car model and seller name
- `MessageTemplate` - Generates exact message format
- `ContactedCarDatabase` - Stores contacted cars history

## Permissions Required

- `SYSTEM_ALERT_WINDOW` - For floating button overlay
- `PACKAGE_USAGE_STATS` - For detecting active apps
- `FOREGROUND_SERVICE` - For background services
- `READ_MEDIA_IMAGES` - For screenshot processing

## Testing Workflow

1. Install app on Samsung S24 Ultra
2. Grant required permissions
3. Toggle session ON
4. Open Facebook Marketplace
5. Navigate to car listing (like the Hyundai Tucson example)
6. Tap floating button
7. Verify message is copied to clipboard
8. Check contacted cars list

## Professional Use Case

Designed specifically for SEQ Automotive's car buying business to:
- Streamline Facebook Marketplace inquiries
- Maintain consistent professional messaging
- Track contacted sellers to avoid duplicates
- Automate repetitive tasks for efficiency

---

**Contact:** Jordan Lansbury - 0408187145  
**Website:** www.seqautomotive.com.au