# ShilpaKala – AI Powered Branding App 🎨📱

ShilpaKala is an Android application built using **Kotlin** and **Jetpack Compose** to help Karnataka artisans create professional branded product photos using AI-powered tools, smart image enhancement, and heritage label generation.

The app focuses on improving digital visibility for handmade products through modern Android development and AI integration.

---

# ✨ Features

- 🔐 Email & Password Authentication
- 🔑 Google Sign-In
- 📸 Smart Camera using CameraX
- 🖼️ AI-Based Image Enhancement
- 🤖 Gemini AI Heritage Label Generator
- 🏷️ Brand Overlay & Watermark Support
- 🗂️ Product Gallery Management
- ☁️ Room Database & Firestore Sync Hooks
- 📤 QR Code & Sharing Features
- 🔔 Reminder & Analytics Support
- 🌐 Offline Fallback Support

---

# 🛠️ Tech Stack

| Technology | Usage |
|---|---|
| Kotlin | Android Development |
| Jetpack Compose | UI Development |
| MVVM | Architecture |
| CameraX | Camera Features |
| Firebase | Authentication & Cloud |
| Room Database | Local Storage |
| Gemini API | AI Integration |
| Coroutines | Async Operations |

---

# 📂 Project Structure

```text
ShilpaKala/
│
├── app/
├── ui/
├── data/
├── viewmodel/
├── camera/
├── ai/
├── utils/
├── navigation/
└── PROJECT_REPORT.md
```

---

# 🚀 Installation & Setup

## Prerequisites

- Android Studio
- JDK 17
- Android SDK

---

## Clone Repository

```bash
git clone https://github.com/iamBalajiS/shilpa-kala.git
```

---

## Run the Project

1. Open the project in Android Studio
2. Sync Gradle
3. Connect an emulator or Android device
4. Click Run ▶️

---

# 🔧 Firebase Setup

1. Create a Firebase project
2. Add Android app
3. Download `google-services.json`
4. Place it inside:

```text
app/google-services.json
```

Enable:
- Firebase Authentication
- Firestore Database

---

# 🤖 Gemini API Setup

Add your Gemini API key inside:

```properties
local.properties
```

Example:

```properties
GEMINI_API_KEY=YOUR_API_KEY
```

---

# 📸 Screenshots

| | |
|---|---|
| ![Screenshot 1](https://github.com/iamBalajiS/shilpa-kala/blob/main/app/WhatsApp%20Image%202026-05-04%20at%2011.15.00%20PM.jpeg) | ![Screenshot 2](https://github.com/iamBalajiS/shilpa-kala/blob/main/app/WhatsApp%20Image%202026-05-04%20at%2011.15.30%20PM.jpeg) |
| ![Screenshot 3](https://github.com/iamBalajiS/shilpa-kala/blob/main/app/WhatsApp%20Image%202026-05-04%20at%2011.15.46%20PM.jpeg) | ![Screenshot 4](https://github.com/iamBalajiS/shilpa-kala/blob/main/app/WhatsApp%20Image%202026-05-04%20at%2011.16.14%20PM.jpeg) |

---

# 🧪 Build Commands

## Debug Build

```bash
./gradlew assembleDebug
```

## Run Tests

```bash
./gradlew test
```

---

# 📊 Project Highlights

- Modern Android Development
- Clean MVVM Architecture
- AI-Powered Features
- Responsive UI/UX
- Offline Support
- Modular Code Structure
- Real-world Application Workflow

---

# 🔮 Future Improvements

- Multi-language Support
- Marketplace Integration
- AI Background Removal
- Advanced Analytics
- E-commerce Support

---

# 📚 Documentation

Detailed project documentation is available in:

```text
PROJECT_REPORT.md
```

---

# 👨‍💻 Developer

**Balaji S**

---

# 📄 License

This project is developed for educational and innovation purposes.

---

# ⭐ GitHub

If you like this project, give it a ⭐ on GitHub!
