package cn.rbc.codeeditor.view;

import static cn.rbc.codeeditor.view.ColorScheme.Colorable.*;

/**
 * VS Code inspired editor themes.
 *
 * This is intentionally self contained so the app does not need a large
 * third-party theme library. Add another theme by adding a branch below.
 */
public final class ColorSchemeVSCode extends ColorScheme {
    private final boolean dark;

    private ColorSchemeVSCode(String id) {
        dark = !("light-plus".equals(id) || "github-light".equals(id) || "solarized-light".equals(id));
        if ("light-plus".equals(id)) lightPlus();
        else if ("github-light".equals(id)) githubLight();
        else if ("solarized-light".equals(id)) solarizedLight();
        else if ("monokai".equals(id)) monokai();
        else if ("dracula".equals(id)) dracula();
        else if ("one-dark".equals(id)) oneDark();
        else if ("nord".equals(id)) nord();
        else if ("solarized-dark".equals(id)) solarizedDark();
        else if ("github-dark".equals(id)) githubDark();
        else if ("tokyo-night".equals(id)) tokyoNight();
        else if ("abyss".equals(id)) abyss();
        else if ("hc-dark".equals(id)) highContrast();
        else darkPlus();
    }

    private void base(int bg, int fg, int sel, int line) {
        setColor(FOREGROUND, fg);
        setColor(BACKGROUND, bg);
        setColor(BACKGROUND_PURE, bg);
        setColor(SELECTION_FOREGROUND, 0xFFFFFFFF);
        setColor(SELECTION_BACKGROUND, sel);
        setColor(CARET_FOREGROUND, fg);
        setColor(CARET_BACKGROUND, 0xFF4FC1FF);
        setColor(CARET_DISABLED, fg);
        setColor(LINE_HIGHLIGHT, line);
        setColor(NON_PRINTING_GLYPH, 0xFF667085);
        setColor(HIGHLIGHT, 0x806A7280);
    }
    private void code(int keyword, int type, int name, int number, int string, int comment, int op, int note, int secondary) {
        setColor(KEYWORD, keyword); setColor(TYPE, type); setColor(NAME, name);
        setColor(NUMBER, number); setColor(STRING, string); setColor(COMMENT, comment);
        setColor(OPERATOR, op); setColor(NOTE, note); setColor(SECONDARY, secondary);
    }

    private void darkPlus() {
        base(0xFF1E1E1E, 0xFFD4D4D4, 0xFF264F78, 0x241F1F1F);
        code(0xFF569CD6,0xFF4EC9B0,0xFFDCDCAA,0xFFB5CEA8,0xFFCE9178,0xFF6A9955,0xFFD4D4D4,0xFFD7BA7D,0xFF9CDCFE);
    }
    private void lightPlus() {
        base(0xFFFFFFFF,0xFF333333,0xFFADD6FF,0x12000000);
        code(0xFF0000FF,0xFF267F99,0xFF795E26,0xFF098658,0xFFA31515,0xFF008000,0xFF333333,0xFFAF00DB,0xFF001080);
    }
    private void monokai() {
        base(0xFF272822,0xFFF8F8F2,0xFF49483E,0x22272722);
        code(0xFFF92672,0xFFA6E22E,0xFFF8F8F2,0xFFAE81FF,0xFFE6DB74,0xFF75715E,0xFFF8F8F2,0xFF66D9EF,0xFFA6E22E);
    }
    private void dracula() {
        base(0xFF282A36,0xFFF8F8F2,0xFF44475A,0x24282A36);
        code(0xFFFF79C6,0xFF8BE9FD,0xFFF8F8F2,0xFFBD93F9,0xFFF1FA8C,0xFF6272A4,0xFFFF79C6,0xFFFFB86C,0xFF50FA7B);
    }
    private void oneDark() {
        base(0xFF282C34,0xFFABB2BF,0xFF3E4451,0x20282C34);
        code(0xFFC678DD,0xFFE5C07B,0xFFE06C75,0xFFD19A66,0xFF98C379,0xFF5C6370,0xFF56B6C2,0xFF61AFEF,0xFF61AFEF);
    }
    private void nord() {
        base(0xFF2E3440,0xFFD8DEE9,0xFF434C5E,0x202E3440);
        code(0xFF81A1C1,0xFF8FBCBB,0xFFD8DEE9,0xFFB48EAD,0xFFA3BE8C,0xFF616E88,0xFF81A1C1,0xFFEBCB8B,0xFF88C0D0);
    }
    private void solarizedDark() {
        base(0xFF002B36,0xFF839496,0xFF073642,0x22002B36);
        code(0xFF859900,0xFF2AA198,0xFF93A1A1,0xFFD33682,0xFF2AA198,0xFF586E75,0xFF839496,0xFFB58900,0xFF268BD2);
    }
    private void solarizedLight() {
        base(0xFFFDF6E3,0xFF657B83,0xFFEEE8D5,0x18FDF6E3);
        code(0xFF859900,0xFF2AA198,0xFF657B83,0xFFD33682,0xFF2AA198,0xFF93A1A1,0xFF586E75,0xFFB58900,0xFF268BD2);
    }
    private void githubDark() {
        base(0xFF0D1117,0xFFC9D1D9,0xFF264F78,0x220D1117);
        code(0xFFFF7B72,0xFFA5D6FF,0xFFD2A8FF,0xFF79C0FF,0xFFA5D6FF,0xFF8B949E,0xFFFF7B72,0xFFFFA657,0xFF7EE787);
    }
    private void githubLight() {
        base(0xFFFFFFFF,0xFF24292F,0xFFB6D7FF,0x12000000);
        code(0xFFCF222E,0xFF8250DF,0xFF24292F,0xFF0550AE,0xFF0A3069,0xFF6E7781,0xFFCF222E,0xFF953800,0xFF116329);
    }
    private void tokyoNight() {
        base(0xFF1A1B26,0xFFA9B1D6,0xFF283457,0x221A1B26);
        code(0xFFBB9AF7,0xFF2AC3DE,0xFFA9B1D6,0xFFFF9E64,0xFFA3BE8C,0xFF565F89,0xFF89DDFF,0xFFE0AF68,0xFF7AA2F7);
    }
    private void abyss() {
        base(0xFF000C18,0xFFD7E3F4,0xFF003B5C,0x22000C18);
        code(0xFFC586C0,0xFF4EC9B0,0xFFD7E3F4,0xFFB5CEA8,0xFFCE9178,0xFF5B8DB8,0xFF4FC1FF,0xFFDCDCAA,0xFF9CDCFE);
    }
    private void highContrast() {
        base(0xFF000000,0xFFFFFFFF,0xFF3F3F3F,0x33000000);
        code(0xFFFFFF00,0xFF00FFFF,0xFFFFFFFF,0xFF00FF00,0xFFFFA000,0xFF7CFC00,0xFFFFFFFF,0xFFFF00FF,0xFF00FFFF);
    }

    public static ColorScheme get(String id) {
        return new ColorSchemeVSCode(id == null ? "dark-plus" : id);
    }

    @Override public boolean isDark() { return dark; }
}
