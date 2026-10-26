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

## 📱 Two Authentic Systems: Original Default UIs for OpenCode & Mobile-Harness

The application accurately copies and faithfully reproduces the **original default user interfaces** of both upstream projects. Tapping the header engine switcher dynamically transforms the entire navigation, theming, status telemetry, and tab set to match that specific system:

### 1. OpenCode Original System (`anomalyco/opencode`)
- **Top Brand Bar:** Minimalist `opencode` wordmark with `>_` glyph (`v1.4.2`), working directory context (`~/workspace/opencode-project`), git branch badge (`git:(main*)`), and 1-tap engine toggle.
- **Agent TUI & Chat:**
  - Build vs. Plan mode toggle (`[BUILD]` autonomous code modifier vs. `[PLAN]` architectural advisor).
  - Multi-session drawer (`sess_01_refactor_server`, `sess_02_deepseek_harness`, etc.).
  - Authentic OpenCode Tool Execution Cards:
    - `[read]` with line offsets and encoding detection.
    - `[edit]` with unified AST patch diff viewer showing added/removed lines (`+14 / -4`).
    - `[bash]` command executor with real-time stdout and exit code badges.
  - Subagent delegation chip (`@general` OpenCode subagent).
  - OpenCode slash command palette: `/init`, `/diff`, `/test`, `/compact`, `/undo`.
- **Web Workbench (`WebPreviewScreen.kt`):**
  - Served directly from local Hono daemon on `http://127.0.0.1:8080`.
  - Browser preview with back/forward/reload, address bar, and external browser launcher.
  - Integrated DevTools Console Drawer with real-time HTTP and WebSocket log inspection.
- **OpenCode Terminal:** Interactive command shell with mobile touch accessory keys (`Tab`, `Ctrl`, `Esc`, `|`, `~`, `/`, `-`).
- **Diffs & Git:** AST diff review, file staging, and commit history.
- **Daemon Server:** Local HTTP (port 8080) and SSH (port 2222) server control with LAN QR connection.

---

### 2. TechJarves Mobile-Harness Original System (`techjarves/Mobile-Harness`)
- **Top Brand Bar:** TechJarves `Mobile-Harness` wordmark with `D` neural glyph, `Ubuntu 20.04 LTS (PRoot ARM64)` status badge, real-time RAM/CPU monitor, and 1-tap engine toggle.
- **Agent Workspace:**
  - Real-time `<think>` DeepSeek-R1 reasoning stream visualizer with token counter (`1,842 reasoning tokens`) and 1-tap **Copy Reasoning** clipboard action.
  - Multi-provider selector: DeepSeek API Direct, OpenRouter, and Local GGUF (via `llama.cpp`).
  - SWE-bench assertion evaluation engine with pass/fail benchmarks and automated assert verification.
  - Task checkpoints and rollback controls.
  - **On-Device APK Compilation Button:** Triggers local debug APK compilation and signing directly in memory without requiring a computer.
- **Linux Subsystem & Terminal (PRoot):**
  - Full Ubuntu 20.04 LTS Linux terminal running `python3`, `node`, `pip`, `git`, `gcc`, `make`, `gradle`.
  - Mobile touch accessory toolbar for terminal navigation (`Tab`, `Ctrl`, `Esc`, `|`, `~`, `/`, `-`, `↑`, `↓`).
- **Files & Editor:** Native project tree with Storage Access Framework (SAF) integration, multi-tab code editor, and rollback diffs.
- **Web Preview & DevTools:** Localhost browser preview for web servers running on ports 8080, 3000, 5000 with DevTools console.
- **57 Built-In Tools Catalog (`ToolsExplorerScreen.kt`):**
  - Full interactive explorer of TechJarves' 57 built-in tools across 6 categories:
    - **Files (12 tools):** `read_file`, `write_file`, `edit_file`, `patch_ast`, `list_dir`, `search_files`, `file_info`, `move_file`, `delete_file`, `create_dir`, `stat`, `saf_export`
    - **Shell (9 tools):** `run_shell`, `bash_command`, `exec_proot`, `bg_process`, `kill_process`, `process_status`, `env_get`, `env_set`, `pty_stream`
    - **Git (8 tools):** `git_status`, `git_diff`, `git_commit`, `git_log`, `git_branch`, `git_push`, `git_pull`, `git_stash`
    - **Diagnostics (10 tools):** `sys_info`, `cpu_telemetry`, `ram_usage`, `battery_governor`, `proot_status`, `port_check`, `disk_free`, `sensor_read`, `net_stats`, `apk_builder`
    - **Vision & Web (8 tools):** `web_fetch`, `http_request`, `browser_preview`, `screenshot`, `html_render`, `console_log`, `dns_lookup`, `ping`
    - **Agent & MCP (10 tools):** `checkpoint_create`, `checkpoint_rollback`, `mcp_connect`, `mcp_list_tools`, `mcp_call_tool`, `memory_store`, `memory_recall`, `subagent_delegate`, `swe_bench_eval`, `token_count`
  - Interactive test runner for each tool with live JSON results.
- **System Telemetry & Governor:** CPU frequency monitor, RAM allocation, true-black OLED mode, and battery governor.

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
- **JDK & Toolchain:** Java 21 (`openjdk-21-jdk` / Eclipse Temurin 21) required by Android Gradle Plugin (AGP) 9.1.1 and Gradle 9.3.1.
- **CI Environment:** GitHub Actions runner configured with Temurin JDK 21, Gradle 9.3.1, automated base64 keystore restoration, and universal branch trigger support.
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
- **JDK:** Java 21 (`openjdk-21-jdk` or Eclipse Temurin 21)
- **Android SDK:** API Level 36 (`compileSdk = 36`, `minSdk = 24`)
- **Gradle:** Gradle 9.3.1 (configured in `gradle/wrapper/gradle-wrapper.properties`)

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
