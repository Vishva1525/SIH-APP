# 🚨 URGENT: Google Console Setup Steps

## Your Configuration Details:
- **Package Name**: `com.pmis.app`
- **SHA-1 Fingerprint**: `9C:BD:9E:F9:45:BE:B8:EF:37:33:4B:BD:11:00:A5:65:22:0F:32:ED`
- **Web Client ID**: `777495340370-kghb0uoo328bjfmk2un9letqp2lbqo2b.apps.googleusercontent.com`
- **Project Number**: `777495340370`

## 🔥 IMMEDIATE STEPS TO FIX THE ERROR:

### Step 1: Google Cloud Console Setup
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Select your project (Project Number: 777495340370)
3. Go to **APIs & Services** → **Credentials**
4. Find your **OAuth 2.0 Client IDs**

### Step 2: Add Android Client
1. Click **"+ CREATE CREDENTIALS"** → **"OAuth 2.0 Client ID"**
2. Choose **"Android"** as application type
3. Fill in:
   - **Name**: `PMIS App Android`
   - **Package name**: `com.pmis.app`
   - **SHA-1 certificate fingerprint**: `9C:BD:9E:F9:45:BE:B8:EF:37:33:4B:BD:11:00:A5:65:22:0F:32:ED`
4. Click **"Create"**

### Step 3: Firebase Console Setup
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Select your project
3. Go to **Project Settings** → **General**
4. Under **"Your apps"**, find your Android app
5. Click **"Add fingerprint"**
6. Paste: `9C:BD:9E:F9:45:BE:B8:EF:37:33:4B:BD:11:00:A5:65:22:0F:32:ED`
7. Click **"Save"**

### Step 4: Download Updated google-services.json
1. In Firebase Console, click **"Download google-services.json"**
2. Replace the file in your `app/` directory

### Step 5: Enable Google Sign-In API
1. In Google Cloud Console, go to **APIs & Services** → **Library**
2. Search for **"Google Sign-In API"**
3. Click **"Enable"**

## ✅ Verification Checklist:
- [ ] Android OAuth client created with correct SHA-1
- [ ] SHA-1 added to Firebase Console
- [ ] Updated google-services.json downloaded
- [ ] Google Sign-In API enabled
- [ ] Package name matches: `com.pmis.app`

## 🚨 Common Issues:
1. **Wrong SHA-1**: Make sure you copied the exact SHA-1 from debug variant
2. **Package name mismatch**: Must be exactly `com.pmis.app`
3. **Missing Android client**: You need BOTH Web and Android OAuth clients
4. **Outdated google-services.json**: Must download fresh copy after adding SHA-1

## 🎯 After completing these steps:
1. Clean and rebuild your project
2. Test Google Sign-In again
3. The "Developer error" should be resolved
