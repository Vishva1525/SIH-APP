# Google Sign-In Setup Instructions

## Important: google-services.json Configuration

**⚠️ CRITICAL STEP**: You must download the latest `google-services.json` file from Firebase Console after adding your SHA-1 fingerprint.

### Steps to configure:

1. **Get your SHA-1 fingerprint**:
   ```bash
   ./gradlew signingReport
   ```
   Copy the SHA-1 fingerprint from the debug variant.

2. **Add SHA-1 to Firebase Console**:
   - Go to [Firebase Console](https://console.firebase.google.com/)
   - Select your project
   - Go to Project Settings → General
   - Under "Your apps", find your Android app
   - Click "Add fingerprint"
   - Paste your SHA-1 fingerprint
   - Click "Save"

3. **Download updated google-services.json**:
   - In the same Firebase Console page
   - Click "Download google-services.json"
   - Replace the file in your `app/` directory

4. **Verify package name**:
   - Ensure your `applicationId` in `app/build.gradle` matches the package name in Firebase Console
   - Current applicationId: `com.pmis.app`

### Common Issues:
- **DEVELOPER_ERROR (10)**: Usually means SHA-1 fingerprint not added or wrong google-services.json
- **Package name mismatch**: Verify applicationId matches Firebase Console
- **Outdated google-services.json**: Must re-download after adding SHA-1

### Files to update:
- ✅ `app/google-services.json` (download from Firebase Console)
- ✅ `app/src/main/res/values/strings.xml` (add Web Client ID)
- ✅ Google Sign-In code (already configured)

**Remember**: The google-services.json file contains your project configuration and must be updated whenever you change SHA-1 fingerprints or package names.
