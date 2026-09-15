# TermuC Modern

A clean rebuild of the TermuC idea: a lightweight Android code editor focused on C/C++ and Termux workflows.

## Highlights
- VS Code-inspired mobile editor UI
- Explorer drawer with Open/New file
- Android Storage Access Framework (no broad storage permission required)
- Save and Save As
- 15+ editor themes
- Bundled JetBrains Mono, Cascadia Code, Source Code Pro, Roboto Mono, DejaVu Sans Mono, Noto Sans Mono, Liberation Mono and Inter
- Syntax highlighting for C/C++ and Python
- Prefix-based autocomplete + Ctrl+Space
- Find in file
- Optional Termux command broadcast
- Java 17 + Android Gradle Plugin 8.2

## Build

```bash
chmod +x gradlew
./gradlew assembleDebug
```

APK: `app/build/outputs/apk/debug/app-debug.apk`
