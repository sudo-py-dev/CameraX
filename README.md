<p align="center">
  <img src="icon.svg" width="128" height="128" alt="CameraX Icon">
</p>

<h1 align="center">CameraX - Ultimate Media & QR Tool</h1>

<p align="center">
  <a href="https://github.com/sudo-py-dev/CameraX/releases/download/v1.0.0/app-release.apk">
    <img src="https://img.shields.io/badge/Download-APK-green.svg?style=for-the-badge&logo=android" alt="Download APK">
  </a>
</p>

CameraX is a high-performance, security-hardened Android application built to provide a seamless experience for photography, video recording, and high-speed QR/Barcode scanning. Leveraging modern Android development practices, it offers a polished UI with robust background processing.

## 🚀 Features

### 📸 Pro Camera Suite
*   **High-Quality Photography:** Optimized image capture with HDR support and grid overlays.
*   **Video Recording:** Smooth video capture with real-time duration tracking.
*   **Intelligent Flash:** Smart toggle between Auto, On, Off, and Torch modes with hardware verification.
*   **Advanced Controls:** Precision pinch-to-zoom and tap-to-focus for professional results.
*   **Timer System:** Integrated countdown timer (3s, 5s, 10s) for stable captures.

### 🔍 QR & Barcode Scanner
*   **Lightning Fast Scanning:** Powered by Google ML Kit for near-instant recognition.
*   **Multi-Format Support:** Recognizes QR Codes, EAN, UPC, Code 128, Data Matrix, and more.
*   **Smart Content Handling:** Automatically categorizes scans (URLs, Wi-Fi, Contacts, Emails, etc.).
*   **Scan History:** Persistent, searchable history of all previous scans with batch management.

### 🛡️ Security & Privacy
*   **Minimal Permissions:** Removed unnecessary location tracking for enhanced user privacy.
*   **Secure Storage:** Hardened database with batch-safe operations.
*   **Anti-Extraction:** Disabled ADB backups to protect user data from external extraction.
*   **Release Ready:** Fully configured with R8/Proguard obfuscation and secure signing protocols.

### 🎨 Modern Experience
*   **Dynamic Theme:** Full support for System, Light, and Dark modes.
*   **Localization:** Multilingual support including English, Spanish, French, German, and Hebrew.
*   **Intuitive UX:** Clean, gesture-based interface built entirely with Jetpack Compose.

## 🛠 Tech Stack
*   **Language:** Kotlin
*   **UI Framework:** Jetpack Compose (Material 3)
*   **Camera API:** CameraX (v1.3.4)
*   **Vision API:** Google ML Kit Barcode Scanning
*   **Architecture:** Clean Architecture + MVVM + Repository Pattern
*   **Dependency Injection:** Manual DI (AppContainer Pattern)
*   **Database:** Room Persistence Library
*   **Async:** Kotlin Coroutines & Flow
*   **Image Loading:** Coil

## 📦 Installation & Build

### Prerequisites
*   Android Studio Koala or newer
*   JDK 17
*   Android SDK 35 (Compile SDK)

### Build Instructions
1.  Clone the repository:
    ```bash
    git clone git@github.com:sudo-py-dev/CameraX.git
    ```
2.  Open the project in Android Studio.
3.  The project is pre-configured with a release signing setup. To build a signed APK:
    ```bash
    ./gradlew assembleRelease
    ```

## 📜 License
This project is for demonstration purposes. All rights reserved.

---
*Built with ❤️ by [sudo-py-dev](https://github.com/sudo-py-dev)*
