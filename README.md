# ⚡ Automexa — Autonomous Multi-Platform Job Application Engine & Intelligence Network

<p align="center">
  <img src="https://img.shields.io/badge/Kotlin-2.0.0-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white"/>
  <img src="https://img.shields.io/badge/Jetpack%20Compose-2024.06.00-4285F4?style=for-the-badge&logo=android&logoColor=white"/>
  <img src="https://img.shields.io/badge/Python-3.11-3776AB?style=for-the-badge&logo=python&logoColor=white"/>
  <img src="https://img.shields.io/badge/FastAPI-0.111-009688?style=for-the-badge&logo=fastapi&logoColor=white"/>
  <img src="https://img.shields.io/badge/Google%20Gemini-2.5%20Flash-8E75B2?style=for-the-badge&logo=google&logoColor=white"/>
  <img src="https://img.shields.io/badge/Render-Cloud-46E3B7?style=for-the-badge&logo=render&logoColor=white"/>
  <img src="https://img.shields.io/badge/GitHub%20Actions-CI%2FCD-2088FF?style=for-the-badge&logo=githubactions&logoColor=white"/>
  <img src="https://img.shields.io/badge/License-MIT-brightgreen?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/Version-1.0.3-blue?style=for-the-badge"/>
</p>

<p align="center">
  <strong>Automexa</strong> is an autonomous job application assistant engineered with native Android on-device intelligence, high-security local Room storage, an atmospheric full-screen video onboarding experience, and an autonomous lock-screen wake & navigation engine built on the SKEDit automation architecture.
</p>

---

## ✨ Features

- **Full-Screen Video Onboarding** — High-definition video intro powered by Media3 and ExoPlayer (`intro_video.mp4`) with an instant top-right skip controller and automatic state caching.
- **Pure Native Soft-Minimalist UI** — Faithful implementation of the modern iOS/Android aesthetic with linen backgrounds (`#E6E4DC`), pure white cards (`#FEFEFE`), tactile scene triggers, and custom green power switches (`#A3D993`).
- **Autonomous Wake & Lockscreen Handling** — Implements the SKEDit execution pattern utilizing Android `PowerManager.WakeLock` and `AccessibilityService.dispatchGesture` routines to wake display and dismiss lockscreens.
- **Zero-Cloud Local Privacy Storage** — 100% of candidate profiles, resumes, salary expectations (CTC), notice periods, and application logs are encrypted and persisted on-device via Android Room Database.
- **Persistent High-Priority Foreground Notifications** — Real-time progress monitoring in the notification shade displaying applied counts, active target portals, and quick `[Pause]` / `[Stop]` buttons.
- **Cloud RAG Reasoning Engine (Render)** — Lightweight FastAPI service deployed on Render running ChromaDB vector search and Google Gemini 2.5 Flash to answer unpredictable job questionnaire fields with high precision.
- **In-App Self-Updater** — Direct integration with the GitHub Releases API. Checks for new releases in-app, downloads the new APK, and triggers zero-friction installation.
- **Universal Multi-Platform CI/CD Pipeline** — GitHub Actions workflow automatically building release-ready binaries for **Android (`.apk`)**, **Windows (`.exe`/`.zip`)**, **macOS (`.dmg`)**, and **iOS runner archive** on every tag push.

---

## 🔗 Project Repository

| Repository | Purpose | Status |
|---|---|---|
| [Automexa](https://github.com/HelloWorld-Farhan/Automexa) | Main Mobile Application, Backend Intelligence & Automation Engine | Active |

---

## 🖼 Application Architecture

```text
Automexa/
├── .github/
│   └── workflows/
│       └── build-all.yml                           → Multi-Platform CI/CD (.apk, .exe, .dmg, iOS)
├── app/                                            → Native Android Client (Kotlin)
│   ├── src/
│   │   └── main/
│   │       ├── AndroidManifest.xml                 → App Permissions & Service Registrations
│   │       ├── java/com/automexa/app/
│   │       │   ├── AutomexaApplication.kt          → App Context & Notification Setup
│   │       │   ├── ui/
│   │       │   │   ├── MainActivity.kt             → Root Activity & Navigation Flow
│   │       │   │   ├── intro/
│   │       │   │   │   └── VideoIntroScreen.kt     → Fullscreen ExoPlayer with Top-Right Skip
│   │       │   │   ├── dashboard/
│   │       │   │   │   └── DashboardScreen.kt      → Minimalist Card Dashboard & Controls
│   │       │   │   └── theme/
│   │       │   │       ├── Color.kt                → Linen, White, and Scene Palette
│   │       │   │       ├── Theme.kt                → Edge-to-Edge System Bars Theme
│   │       │   │       └── Type.kt                 → Custom Typography System
│   │       │   ├── data/local/
│   │       │   │   ├── AutomexaDatabase.kt         → Room SQLite Database Configuration
│   │       │   │   ├── dao/                        → UserProfile & JobLog DAOs
│   │       │   │   └── entities/                   → Stored Profile & Log Schemas
│   │       │   ├── service/
│   │       │   │   ├── AutomexaAccessibilityService.kt → UI Reader & Lockscreen Bypass
│   │       │   │   ├── AutomexaForegroundService.kt    → Persistent Keep-Alive Service
│   │       │   │   └── AutomexaNotificationManager.kt  → Heads-Up Notification System
│   │       │   └── updater/
│   │       │       └── AppUpdateManager.kt         → In-App GitHub Releases Updater
│   │       └── res/
│   │           ├── raw/intro_video.mp4             → Packaged Onboarding Video Asset
│   │           ├── drawable/logo.jpg               → Official Branding Logo
│   │           ├── xml/accessibility_service_config.xml
│   │           └── xml/file_paths.xml              → FileProvider Directives for APK Install
│   ├── build.gradle.kts                            → Android Module Configuration & Deps
│   └── proguard-rules.pro
├── backend/                                        → Cloud Reasoning Service (Python / Render)
│   ├── main.py                                     → FastAPI + Gemini 2.5 Flash + ChromaDB RAG
│   ├── requirements.txt                            → Python Cloud Dependencies
│   └── render.yaml                                 → 1-Click Render Deployment Blueprint
├── build.gradle.kts                                → Root Project Gradle Config
├── settings.gradle.kts                             → Repository & Module Includes
└── gradle.properties
```

---

## 🎨 Design System

### Minimalist Linen & Accent Tokens
| Token | Hex / Value | Architectural Representation |
|---|---|---|
| Linen Canvas | `#E6E4DC` | Calming, low-fatigue application background |
| Pure Card | `#FEFEFE` | High-contrast elevated metric cards (`rounded-3xl`) |
| Dark Slate | `#1C1B20` | Primary action buttons and selected filter pills |
| Green Switch | `#A3D993` | Active platform automation power toggle |
| Scene Blue | `#C7D6DF` | Wake Up scene trigger accent |
| Scene Purple | `#D4CFEA` | Sleep / Idle session trigger accent |
| Scene Teal | `#BAD8DC` | Turbo multi-platform execution accent |
| Scene Pink | `#E8CBCF` | Session reset trigger accent |
| Text Primary | `#1D1C21` | High-contrast primary typography |
| Text Secondary | `#6E6D72` | Subtitles, timestamps, and secondary labels |

---

## 🚀 How to Run Locally

### 1. Android Application (Kotlin)
1. Open the project in **Android Studio** (Koala or Ladybug+).
2. Connect your Android device via USB with **USB Debugging** enabled.
3. Sync Gradle and run:
   ```bash
   ./gradlew installDebug
   ```
4. Enable the **Automexa Accessibility Service** in `Settings > Accessibility > Downloaded Apps > Automexa`.

### 2. Python Backend (Render / Local)
1. Navigate to the backend directory:
   ```bash
   cd backend
   ```
2. Create and activate a virtual environment:
   ```bash
   python -m venv venv
   source venv/bin/activate  # On Windows: venv\Scripts\activate
   ```
3. Install dependencies:
   ```bash
   pip install -r requirements.txt
   ```
4. Configure your Gemini API Key and start the server:
   ```bash
   export GEMINI_API_KEY="your-api-key"
   uvicorn main:app --host 0.0.0.0 --port 8000 --reload
   ```

---

## ☁️ Cloud Deployment on Render

To deploy the Python AI backend on Render:
1. Fork or push this repository to GitHub.
2. In the [Render Dashboard](https://dashboard.render.com), click **New + > Web Service**.
3. Select `HelloWorld-Farhan/Automexa` and set **Root Directory** to `backend`.
4. Configure Environment Variables:
   * `PYTHON_VERSION`: `3.11.9`
   * `GEMINI_API_KEY`: *(Your Google AI Studio Gemini API Key)*
5. Click **Create Web Service**.

---

## 📦 Multi-Platform CI/CD with GitHub Actions

Automated builds for **Android (`.apk`)**, **Windows (`.exe`/`.zip`)**, **macOS (`.dmg`)**, and **iOS** are handled by `.github/workflows/build-all.yml`.

To trigger an automated release build:
```bash
git tag v1.0.0
git push origin v1.0.0
```
GitHub Actions will automatically compile all 4 platforms and attach the binaries directly to the GitHub Release.

---

## 👨💻 Author

**Farhan Khalid**  
📧 farhankhalid17968@gmail.com  
🔗 [LinkedIn](https://www.linkedin.com/in/farhan-khalid-117514259/)  
🐙 [GitHub](https://github.com/HelloWorld-Farhan)

---

## 📄 License

```text
MIT License

Copyright (c) 2026 Farhan Khalid

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
```

---

## 🌟 Support

If Automexa helps automate and streamline your career journey, please give it a ⭐ on GitHub!

<p align="center">Made with ❤️ — Kotlin · Jetpack Compose · Python · FastAPI · Gemini · Render</p>

