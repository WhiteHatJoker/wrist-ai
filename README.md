# Nano GPT

**Nano GPT** is a Kotlin-based Wear OS application that brings powerful LLMs directly to your wrist. It uses voice input to let users interact with AI assistants like ChatGPT (OpenAI) and Gemini (Google), and shows the response text.

Built in early 2025 as my bachelor's diploma project, at a time when no LLM assistant was available natively on Wear OS smartwatches. Google later brought Gemini to Wear OS watches in 2025; this app delivered the same idea earlier, with a choice of ChatGPT and Gemini models from the wrist.

---

## 📱 Features

- Voice input using Wear OS speech recognizer
- Supports both **ChatGPT** and **Gemini** APIs
- LLM model switching via dropdown UI: ChatGPT 4o, ChatGPT 4.1, o4-mini, Gemini 2.5 Pro, Gemini 2.5 Flash, Gemini 2.0 Flash
- Clean UI built with **Jetpack Compose for Wear OS**
- MVVM architecture + Hilt dependency injection
- Retrofit clients for the OpenAI and Gemini REST APIs behind a common `LlmRepository` interface
- Unit tests for the ViewModel and every repository

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
In `local.properties` file (not committed to git), add:
```bash
OPENAI_API_KEY=your_openai_api_key
GEMINI_API_KEY=your_gemini_api_key
```
The keys are injected at build time through `BuildConfig`, so they never live in the source code. Note that in order to use OpenAI API you need an OpenAI API key & funds on your account. Gemini API keys are free, except for model Gemini 2.5 Pro.

### 3. Build the project
Press Run button in Android studio. Please note that you can run the app on real smartwatch or on emulator. Below are two ways to run the project.

### 4. Run the app
Following are the two options to run the app:

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

## 🧪 Testing
This project includes unit tests for:
* VoiceInputViewModel
* MultiLlmRepository
* ChatGptRepositoryImpl
* GeminiRepositoryImpl

To run tests, you just need to right click [test folder](app/src/test/) in Android Studio and then click on "Run Tests" in option.

---

## 🙋‍♂️ Author
Developed by Ravshan Khudoyberdiev.
Part of a Bachelor’s Diploma Project (2025) at IT Park University.
