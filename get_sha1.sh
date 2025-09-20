#!/bin/bash

echo "🔍 Getting SHA-1 Fingerprint for Google Sign-In Setup"
echo "=================================================="

echo "📱 Running Gradle signingReport..."
./gradlew signingReport

echo ""
echo "✅ Copy the SHA-1 fingerprint from the debug variant above"
echo "📋 Then add it to your Firebase Console:"
echo "   1. Go to Firebase Console → Project Settings → General"
echo "   2. Under 'Your apps', find your Android app"
echo "   3. Click 'Add fingerprint'"
echo "   4. Paste your SHA-1 fingerprint"
echo "   5. Click 'Save'"
echo "   6. Download the updated google-services.json"
echo ""
echo "🔗 Your configuration details:"
echo "   Web Client ID: 777495340370-kghb0uoo328bjfmk2un9letqp2lbqo2b.apps.googleusercontent.com"
echo "   Package Name: com.pmis.app"
echo "   Project Number: 777495340370"
