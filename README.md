# TermuC Modern

A lightweight Android C/C++ IDE inspired by VS Code, designed around Termux workflows.

## This build
- VS Code-style editor header with a dedicated **Run ▶ button in the top-right**.
- Explorer drawer, Open/New/Save, Settings and editor tabs.
- JetBrains Mono and other bundled developer fonts.
- VS Code-inspired themes.
- Debounced autocomplete and syntax highlighting to reduce typing lag.
- C/C++/Python/Java basic completion.
- Direct Termux Run Command integration using stdin, so the editor does not need broad filesystem permissions just to execute the current buffer.
- C++ uses `g++ -std=c++17`, C uses `gcc -std=c17`, Java uses `javac/java`, and Python runs through `python3`.
- Launcher icon included.

## Termux setup

TermuC uses the official Termux `RUN_COMMAND` service. Termux must allow external commands and Android must grant TermuC the **Run commands in Termux environment** permission.

In Termux:

```bash
mkdir -p ~/.termux
echo 'allow-external-apps=true' >> ~/.termux/termux.properties
```

Restart Termux after changing the property. Then in Android settings grant TermuC its additional **Run commands in Termux environment** permission.

For C/C++ install a compiler if needed:

```bash
pkg update
pkg install clang
```

TermuC sends the current editor buffer to Termux through stdin. It creates temporary files under `~/.termuc-run`, compiles/runs them, and removes the temporary files when the command exits.

## Build

The included GitHub Actions workflow uses Ubuntu 22.04 and JDK 17 and runs:

```bash
./gradlew --no-daemon assembleDebug
```
