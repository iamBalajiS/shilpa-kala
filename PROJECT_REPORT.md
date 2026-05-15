# ShilpaKala Project Report

## 1. Problem Statement

Wood carvers, Gombe makers, and other artisans often create high-quality handmade products but share dull or poorly framed photos on WhatsApp and social media. Poor photography lowers perceived value and makes premium handmade work look inexpensive.

## 2. Solution Overview

ShilpaKala is a Digital Portfolio Assistant Android app. It helps artisans capture product photos with a guided camera, adds professional branding overlays, generates a heritage-style product label, stores the work locally, and makes sharing easy.

The app is offline-first for camera, image processing, gallery, QR, and local database features. Firebase and Gemini integrations are included as configurable cloud/AI extensions.

## 3. Features

- Email sign up, login, logout, session persistence, and Google Sign-In flow
- Editable artisan profile with name, workshop location, contact, and QR code
- Home hub that acts as the main control center for camera, gallery, profile, settings, reminders, and recent work
- CameraX preview with product outline, grid, and alignment guide
- Camera state machine with live preview hints and theme/overlay preview
- Bitmap-based product photo enhancement
- Handmade in Karnataka logo, artisan name, wood type, and price overlay
- Adjustable overlay positions
- Studio, wooden, and festival background themes
- Heuristic on-device background removal abstraction with optional API-backed expansion path
- Gemini API heritage label generation with offline fallback
- Before/after comparison slider
- Room database tables: users, photos, metadata
- Persisted theme and language preferences
- Firestore sync hooks for photo records
- Gallery grid with preview, delete, and share
- Android share sheet support for WhatsApp, Facebook, Instagram, and other apps
- Daily reminder notification via WorkManager
- Analytics dashboard for photos created and shares count
- Material 3 Compose UI with bottom navigation

## 4. Tech Stack

- Kotlin
- Jetpack Compose + Material 3
- MVVM with data, domain, ui, di, and utils layers
- CameraX
- Room Database
- Firebase Auth / Firestore SDK hooks
- Optional FirebaseAuth credential path with local fallback when config is absent
- Google Sign-In
- Gemini REST API integration example
- Hilt dependency injection
- Navigation Compose
- WorkManager notifications
- ZXing QR code generation
- MediaStore local storage

## 5. Architecture Diagram

```text
UI: Compose Screens
  Auth | Home | Camera | Gallery | Profile
        |
ViewModels
  AuthViewModel | HomeViewModel | CameraViewModel | GalleryViewModel | ProfileViewModel | SettingsViewModel
        |
Repositories
  AuthRepository | PhotoRepository | ProfileRepository | SettingsRepository
        |
Data Sources
  Room DAOs -> Users / Photos / Metadata
  Firestore Sync -> photos collection
        |
Utilities
  BackgroundRemovalEngine | ImageProcessor | HeritageLabelGenerator | QrCodeGenerator | ReminderScheduler | SyncWorker
```

## 6. How to Run

1. Open `ShilpaKalaApp` in Android Studio.
2. Use Android Studio Embedded JDK 17 or newer.
3. Sync Gradle.
4. Run on an Android device or emulator.
5. Allow camera permission.
6. Sign up with email/password or use Google Sign-In.

### Optional Gemini API

Add this to `gradle.properties` or your local Gradle properties:

```properties
GEMINI_API_KEY=your_api_key_here
```

If no key is configured, the app still works and uses an offline heritage label generator.

### Optional Firebase

To enable real Firebase Authentication and Firestore backend configuration, add your Firebase project `google-services.json` and apply the Google Services Gradle plugin. The current project compiles without this file so it can run locally for academic/demo use.

## 7. Future Enhancements

- Product-specific ML background segmentation model
- Real Firebase Storage image backup
- Kannada string resources for every label
- Buyer-facing public portfolio web page
- In-app price recommendation model
- Export PDF catalog for shops and exhibitions
