# 🚀 Supabase + Google Sign-In Setup Instructions

## ✅ Your Configuration Details:
- **Package Name**: `com.pmis.app`
- **SHA-1 Fingerprint**: `9C:BD:9E:F9:45:BE:B8:EF:37:33:4B:BD:11:00:A5:65:22:0F:32:ED`
- **Google Client ID**: `[YOUR_CLIENT_ID]` (Get from Google Cloud Console)

## 🔥 IMMEDIATE STEPS TO FIX THE DEVELOPER ERROR:

### Step 1: Google Cloud Console Setup
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Select your project (Project Number: [YOUR_PROJECT_NUMBER])
3. Go to **APIs & Services** → **Credentials**
4. Click **"+ CREATE CREDENTIALS"** → **"OAuth 2.0 Client ID"**
5. Choose **"Android"** as application type
6. Fill in:
   - **Name**: `PMIS App Android`
   - **Package name**: `com.pmis.app`
   - **SHA-1 certificate fingerprint**: `9C:BD:9E:F9:45:BE:B8:EF:37:33:4B:BD:11:00:A5:65:22:0F:32:ED`
7. Click **"Create"**

### Step 2: Supabase Dashboard Setup
1. Go to [Supabase Dashboard](https://supabase.com/dashboard)
2. Select your project
3. Go to **Authentication** → **Providers**
4. Find **Google** and click **"Enable"**
5. Fill in:
   - **Client ID**: `[YOUR_GOOGLE_CLIENT_ID]`
   - **Client Secret**: `[YOUR_GOOGLE_CLIENT_SECRET]`
6. Click **"Save"**

### Step 3: Get Your Supabase Credentials
1. In Supabase Dashboard, go to **Settings** → **API**
2. Copy your **Project URL** (looks like: `https://abcdefghijklmnop.supabase.co`)
3. Copy your **anon public key** (starts with `eyJ...`)

### Step 4: Update Your App Configuration
Update `app/src/main/res/values/strings.xml`:
```xml
<string name="supabase_url">https://your-project-ref.supabase.co</string>
<string name="supabase_anon_key">eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...</string>
```

### Step 5: Enable Google Sign-In API
1. In Google Cloud Console, go to **APIs & Services** → **Library**
2. Search for **"Google Sign-In API"**
3. Click **"Enable"**

## ✅ Verification Checklist:
- [ ] Android OAuth client created in Google Cloud Console with correct SHA-1
- [ ] Google provider enabled in Supabase Dashboard
- [ ] Client ID and Secret configured in Supabase
- [ ] Supabase URL and anon key updated in strings.xml
- [ ] Google Sign-In API enabled in Google Cloud Console

## 🎯 How It Works:
1. User clicks "Continue with Google"
2. Google Sign-In flow starts
3. User selects Google account
4. App receives Google ID token
5. App can send token to Supabase for authentication
6. User is authenticated in your app

## 🚨 Common Issues:
1. **Wrong SHA-1**: Make sure you copied the exact SHA-1: `9C:BD:9E:F9:45:BE:B8:EF:37:33:4B:BD:11:00:A5:65:22:0F:32:ED`
2. **Package name mismatch**: Must be exactly `com.pmis.app`
3. **Missing Android client**: You need Android OAuth client (not just Web)
4. **Wrong Supabase URL**: Must include your project reference
5. **Disabled Google provider**: Make sure it's enabled in Supabase Dashboard

## 🔧 After Setup:
1. Clean and rebuild your project
2. Test Google Sign-In
3. Check Supabase Dashboard → Authentication → Users to see new users

## 📱 Current Status:
- ✅ Google Sign-In is configured with your client ID
- ✅ App builds successfully
- ✅ Ready for Supabase integration
- ⚠️ Need to complete Google Console Android client setup
- ⚠️ Need to add Supabase credentials

**The "Developer error" should be resolved once you complete Step 1 (Google Cloud Console Android client setup)!**
