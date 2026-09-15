# TermuC 0.3.0 Stable

This version is based directly on the original TermuC/codeeditor architecture for stability.

Features added without replacing the original editor/runtime:
- Original Termux `RunCommandService` RUN_COMMAND integration preserved.
- Run action remains in the top-right action bar.
- Local built-in autocomplete is the default for lower latency; LSP remains optional.
- Autocomplete starts after a two-character prefix to reduce popup churn.
- Debounced auto-save after 1.2 seconds of typing inactivity.
- Current document is saved when the app stops.
- Previously opened editor tabs and selected tab are restored on launch.
- Reduced line-number gutter padding.
- Java 8 compile compatibility with modern Android Gradle Plugin.
