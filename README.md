# OpenCode & DeepSeek Mobile-Harness for Android

[![Build Android Release APK](https://github.com/developer/opencode-android/actions/workflows/build-release-apk.yml/badge.svg)](https://github.com/developer/opencode-android/actions/workflows/build-release-apk.yml)
[![Platform](https://img.shields.io/badge/Platform-Android%207.0%2B%20(API%2024--36)-3DDC84?logo=android&logoColor=white)](https://developer.android.com)
[![Architecture](https://img.shields.io/badge/Arch-ARM64%20%7C%20x86__64-blue?logo=arm)](https://arm.com)
[![License](https://img.shields.io/badge/License-Apache%202.0-orange.svg)](LICENSE)
[![Engines](https://img.shields.io/badge/Engines-OpenCode%20%7C%20DeepSeek--Harness-00E5FF)](https://github.com/anomalyco/opencode)

A high-performance, production-ready Android IDE and autonomous AI coding workstation that runs the real **OpenCode** (`anomalyco/opencode`) and **DeepSeek Mobile-Harness** (`techjarves/Mobile-Harness`) architectures directly on Android devices.

Transform any Android phone or tablet into an autonomous developer environment capable of editing code, staging git commits, serving local HTTP and SSH daemons, executing terminal commands, and driving agentic coding workflows powered by DeepSeek-R1 and OpenCode.

---

## 🚀 Core Mission: Running Real OpenCode & DeepSeek-Harness on Android

Both **OpenCode** and **DeepSeek Mobile-Harness** were originally conceived for desktop Linux/macOS environments. This application bridges mobile operating system boundaries to make their authentic execution stacks runnable on Android.

### What It Takes to Run Both Apps on Android

```
┌────────────────────────────────────────────────────────────────────────┐
│                        Android Application Layer                       │
│      Jetpack Compose UI  •  Material 3 Theming  •  Room Database       │
├────────────────────────────────────────────────────────────────────────┤
│                     Local Loopback & Daemon Bridge                     │
│    HTTP Code Server (Port 8080)   •   SSH Daemon Bridge (Port 2222)    │
├────────────────────────────────────────────────────────────────────────┤
│                       Execution Subsystem Layers                       │
│  ┌──────────────────────────────────┐ ┌──────────────────────────────┐ │
│  │     OpenCode (anomalyco)         │ │ DeepSeek Harness (techjarves)│ │
│  │  • Node.js v20+ ARM64 Runtime    │ │ • Python 3.12 Evaluation Env │ │
│  │  • `opencode serve` Daemon       │ │ • SWE-bench Assertion Engine │ │
│  │  • Web Workbench (localhost:8080)│ │ • <think> Reasoning Stream   │ │
│  │  • AST Diffs & Staging Engine    │ │ • Local GGUF / DeepSeek API  │ │
│  └──────────────────────────────────┘ └──────────────────────────────┘ │
├────────────────────────────────────────────────────────────────────────┤
│              PRoot Linux Subsystem (No Root Privileges Required)       │
│  • Emulated Linux kernel syscalls via ptrace (Ubuntu 22.04 / Alpine)   │
│  • Sandboxed private storage: `/data/data/com.example/files/rootfs`    │
│  • Real arm64 binaries: node, python3, git, bash, clang, curl          │
└────────────────────────────────────────────────────────────────────────┘
```

1. **PRoot Linux Subsystem (No Root Required):**
   Utilizes PRoot userland emulation to run an isolated Linux filesystem inside Android's application sandbox. Users get real standard GNU/Linux tools without voiding warranty or needing root privileges.
2. **ARM64 Native Toolchains:**
   Packages Node.js (v20+) and Python 3.12 compiled for `aarch64`. OpenCode's TypeScript engine runs via the Node runtime, while Mobile-Harness executes SWE-bench assertions via Python.
3. **DeepSeek Reasoning & SWE-Bench Testbed:**
   Captures and renders the full `<think>` reasoning chain from DeepSeek-R1 in real time, validating algorithmic logic against synthetic SWE-bench test assertions. Supports both cloud endpoints (DeepSeek API, OpenRouter) and local on-device quantized models (GGUF via llama.cpp).
4. **Local Loopback Daemons:**
   A background service binds to `127.0.0.1:8080` (HTTP) and `127.0.0.1:2222` (SSH), serving the authentic web IDE workbench and enabling remote access from any laptop on the local Wi-Fi network.

---

## 📱 Application Screens & Features

### 1. AI Harness & Workbench (`AiHarnessScreen.kt`)
- **Engine Switcher:** Seamlessly toggle between **OpenCode** (`anomalyco/opencode`) and **DeepSeek Mobile-Harness** (`techjarves/Mobile-Harness`).
- **Interactive Agent Console:** Multi-step agent execution with real tool calls (`read_file`, `edit_file`, `bash`, `ast_diff`).
- **OpenCode Real GUI Workbench:**
  - Build vs. Plan mode toggle.
  - Active session manager.
  - Web workbench served over `http://localhost:8080` with 1-tap browser launcher and ping tester.
  - Unified file diff viewer with AST patch statistics (`+14 / -4 lines`).
- **DeepSeek Mobile-Harness Real GUI:**
  - Real-time `<think>` reasoning stream visualizer with 1-tap **Copy Reasoning** clipboard action.
  - Provider switcher: DeepSeek Direct, OpenRouter, and Local GGUF (via `llama.cpp`).
  - Live SWE-bench assertion tester with timing benchmarks and status indicators.
  - **Save Solution to Disk**: Persists generated algorithmic solution directly into SQLite database and workspace.
- **Android Architecture Inspector:** Built-in technical reference card detailing subsystem mechanics.

### 2. UNIX Terminal Shell (`TerminalScreen.kt`)
- High-performance terminal emulator with accessory toolbar for fast mobile typing (`|`, `/`, `-`, `~`, `Tab`, `Ctrl`, `Esc`).
- Integrated command suite:
  - `opencode status`, `opencode doctor`, `opencode run <task>`
  - `deepseek status`, `deepseek models`, `harness bench`
  - `git status`, `git diff`, `git commit -m "..."`, `git push`, `git pull`, `git log`
  - `node <script>`, `python <script>`, `bash <script>`
  - `code-server status`, `ssh status`, `top`, `ls`, `cat`, `pwd`, `whoami`

### 3. Code Editor (`CodeEditorScreen.kt`)
- Syntax-highlighted code editor for JavaScript, TypeScript, Python, Markdown, JSON, and Shell scripts.
- Line numbers, file switcher tab bar, and real-time disk persistence backed by SQLite / Room.
- Search and replace with instant match navigation.

### 4. Git Version Control (`GitScreen.kt`)
- Visual git workspace status showing modified and untracked files.
- Commit history log with author avatars, timestamps, and commit hashes.
- Branch manager and remote repository synchronization.

### 5. Remote Server Dashboard (`ServerDashboardScreen.kt`)
- Live telemetry for the local HTTP code server (port 8080) and SSH daemon (port 2222).
- One-tap daemon start/stop toggle.
- Local Wi-Fi IP detector and QR code/LAN connection dialog with token-based authentication.

### 6. Battery Governor & OLED Mode (`SyncSettingsScreen.kt`)
- **OLED True Black Mode:** Eliminates battery drain on OLED/AMOLED screens.
- **Low Battery Governor:** Throttles background indexing and caps CPU usage to 75% when battery falls below 20%.
- **Peer-to-Peer Sync:** Encrypted file synchronization using AES-256-GCM.

---

## 🔄 CI/CD & Automated Ascending Versioning

The project includes an enterprise-grade GitHub Actions workflow (`.github/workflows/build-release-apk.yml`) that builds and packages a release APK on every commit.

### How Ascending Versioning Works

```bash
# Total commit count across git history
COMMIT_COUNT=$(git rev-list --count HEAD 2>/dev/null || echo 1)
RUN_NUMBER=${{ github.run_number }}

# Monotonically ascending integer for Android versionCode
VERSION_CODE=$(( 100 + COMMIT_COUNT + RUN_NUMBER ))

# Ascending semantic version name
VERSION_NAME="1.0.${COMMIT_COUNT}"
```

- **`versionCode`**: Automatically increments with every single commit and workflow run (e.g., `105`, `106`, `107`).
- **`versionName`**: Automatically tracks the commit count (e.g., `1.0.5`, `1.0.6`).
- **Release Signing Hierarchy:**
  - **CI with Repository Secrets:** If custom secrets (`KEYSTORE_BASE64`, `STORE_PASSWORD`, `KEY_PASSWORD`) are configured, the workflow uses your production release keystore.
  - **CI Automated Keystore:** If secrets are omitted (e.g., forks or pull requests), the workflow generates a valid upload keystore (`my-upload-key.jks`) using `keytool` on-the-fly.
  - **Local Development Fallback:** When running `gradle :app:assembleRelease` locally without environment variables, the build configuration automatically falls back to `${rootDir}/debug.keystore`, ensuring local release testing works out-of-the-box without manual key setup.
- **Artifacts:** Every build outputs:
  - `opencode-devbox-v<version>-b<build>-release.apk`
  - `opencode-devbox-v<version>-b<build>-release.apk.sha256`
- **GitHub Releases:** Tagging a commit with `v*` (e.g. `v1.0.5`) automatically creates a GitHub Release with the APK and checksum attached.

---

## 🛠️ Building & Developing Locally

### Prerequisites
- **JDK:** Java 17 or higher (`openjdk-17-jdk`)
- **Android SDK:** API Level 36 (`compileSdk = 36`, `minSdk = 24`)
- **Gradle:** Version 8.x+ (managed via Gradle Kotlin DSL)

### Build Commands

```bash
# Clone the repository
git clone https://github.com/developer/opencode-android.git
cd opencode-android

# Build Debug APK
gradle :app:assembleDebug

# Build Release APK with custom or ascending version
gradle :app:assembleRelease -PversionCode=120 -PversionName="1.0.20"

# Run JVM Unit & Robolectric Tests
gradle :app:testDebugUnitTest
```

### Environment Configuration (`.env`)
Copy `.env.example` to `.env` to configure optional external API keys:
```env
# DeepSeek API (Optional - required for cloud DeepSeek-R1 inference)
DEEPSEEK_API_KEY=your_deepseek_api_key_here

# OpenRouter API (Optional - alternative cloud inference provider)
OPENROUTER_API_KEY=your_openrouter_api_key_here
```

---

## 📐 Architecture & Technology Stack

| Layer | Technology |
|---|---|
| **Language** | Kotlin 2.0+ (100% Kotlin DSL) |
| **UI Framework** | Jetpack Compose & Material Design 3 (M3) |
| **Local Persistence** | Android Room Database & SQLite |
| **State Management** | Android Jetpack ViewModel & Kotlin StateFlow |
| **Local Server** | Kotlin Coroutines Asynchronous ServerSocket (HTTP & SSH) |
| **Cryptography** | AES-256-GCM (Android Keystore / Java Cryptography) |
| **CI/CD** | GitHub Actions (`.github/workflows/build-release-apk.yml`) |

---

## 📜 Attributions & Upstream Projects

This project integrates the architectures and concepts from:
- [anomalyco/opencode](https://github.com/anomalyco/opencode) — The autonomous AI software engineering agent.
- [techjarves/Mobile-Harness](https://github.com/techjarves/Mobile-Harness) — The mobile testbed harness for DeepSeek and LLM evaluations.

---

## 📄 License
This project is licensed under the Apache 2.0 License. See the [LICENSE](LICENSE) file for details.
