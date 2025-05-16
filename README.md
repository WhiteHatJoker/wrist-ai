# Nano GPT

**Nano GPT** is a Kotlin-based Wear OS application that brings powerful LLMs directly to your wrist. It uses voice input to let users interact with AI assistants like ChatGPT (OpenAI) and Gemini (Google), and shows the response text.

---

## 📱 Features

- Voice input using Wear OS speech recognizer
- Supports both **ChatGPT** and **Gemini** APIs
- LLM model switching via dropdown UI
- Clean UI built with **Jetpack Compose for Wear OS**
- MVVM architecture + Hilt dependency injection

---

## 📦 Requirements (versions used in the project)

- Latest Android Studio Meerkat Feature Drop | 2024.3.2
- Kotlin 2.0.21
- Java 17
- Internet access if physical device is used
- **Wear OS 3+ device or emulator API level 30+([emulator device version and settings in Android studio](img/emulator-device-settings.png))**

---

## 🚀 Setup

### 1. Clone the repository and open it in Android Studio IDE

```bash
git clone https://github.com/WhiteHatJoker/wrist-ai
```

### 2. Add API keys
It is very important to add API keys as the app won't work without them.
In `local.properties` file, add:
```bash
OPENAI_API_KEY=your_openai_api_key
GEMINI_API_KEY=your_gemini_api_key
```
Note that in order to use OpenAI API you need an OpenAI API key & funds on your account. Gemini API keys are free, except for model Gemini 2.5 Pro.

### 3. Build the project
Press Run button in Android studio. Please note that you can run the app on real smartwatch or on emulator. Below I will introduce three ways to run project.

### 4. Run the app
Following are the three options to run the app:

---

#### Option 1: Running in Emulator
1. Open Device Manager in Android Studio
2. Create Virtual Device with the following [settings](img/emulator-device-settings.png)
3. Click on Start for a newly created device and wait for it to launch the watch screen
4. You need to enable microphone support for a virtual device since our app requires voice input. To do this, you need to click on three dots to open Emulator settings, then enable all 3 microphone settings [as seen here](img/enable-mic.png)
5. Close the device
6. Finally just choose the virtual device and click Run. This will start the Nano GPT app on the emulator device.

---

#### Option 2: Running on real smartwatch using Android Studio
We need to connect our watch to Android studio. In order to do that we need to:
1. Enable Wi-fi debugging on the device using the following section https://developer.android.com/training/wearables/get-started/debugging#wifi-enable
2. Pair the debugger to the watch following this article https://developer.android.com/training/wearables/get-started/debugging#wifi-pair
3. Connect the debugger to the watch using this article https://developer.android.com/training/wearables/get-started/debugging#wifi-adb
4. After running adb connect command, we should now see our real smartwatch as an option to run the app on. Just choose the real smartwatch name and click Run.
5. This essentially runs the app on your watch, but also leaves it there for future usage as usual smartwatch app available in app drawer.

---

#### Option 3: Running on real smartwatch using APK
There is a signed APK file in the [releases folder](releases/nanogpt-v1.0.apk) with API keys embedded. In order to run it:
1. Download the APK file to your phone paired to your watch.
2. Install Wear Installer app from Google Play Store [here](https://play.google.com/store/apps/details?id=org.freepoc.wearinstaller&hl=en)
3. Open the app on your phone and select our downloaded APK file. Wear Installer will send it to your smartwatch for installation.
4. Once installed, you can run Nano GPT as usual smartwatch app.

---

## 🧪 Testing
This project includes unit tests for:
* VoiceInputViewModel
* MultiLlmRepository
* ChatGptRepositoryImpl
* GeminiRepositoryImpl

To run tests, you just need to right click [test folder](app/src/test/) in Android Studio and then click on "Run Tests" in option.

---

## 🙋‍♂️ Author
Developed by Ravshanjon Khudoyberdiev.
Part of a Bachelor’s Diploma Project (2025) at IT Park University.