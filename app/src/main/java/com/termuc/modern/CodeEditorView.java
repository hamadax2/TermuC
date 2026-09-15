package com.termuc.modern;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.TextView;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Lightweight editor optimized for Android: debounce expensive work and never rebuild spans per keystroke. */
public class CodeEditorView extends FrameLayout {
    private final TextView gutter;
    private final EditText edit;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private PopupWindow completion;
    private ThemeManager.Theme theme;
    private Typeface typeface;
    private int fontSize = 15;
    private boolean highlighting;
    private boolean applyingHighlight;
    private boolean suppressCompletion;
    private Runnable highlightTask;
    private Runnable completionTask;

    private static final String[] CPP = {"alignas","alignof","auto","bool","break","case","catch","char","class","const","constexpr","continue","default","delete","do","double","else","enum","explicit","extern","false","float","for","friend","if","inline","int","long","namespace","new","nullptr","operator","private","protected","public","return","short","signed","sizeof","static","std","struct","switch","template","this","throw","true","try","typedef","typename","union","unsigned","using","virtual","void","volatile","while","vector","string","cout","cin","endl","array","map","set","unordered_map","unique_ptr","shared_ptr","make_unique","make_shared","push_back","emplace_back","begin","end","size","empty"};
    private static final String[] PY = {"and","as","assert","async","await","break","class","continue","def","del","elif","else","False","finally","for","from","global","if","import","in","is","lambda","None","not","or","pass","raise","return","True","try","while","with","yield","print","len","range","str","int","float","list","dict","set","tuple","open","enumerate","zip","map","filter","self"};
    private static final Map<String,String> LOWER = new HashMap<>();
    static {
        for (String w : CPP) LOWER.put(w, w.toLowerCase(Locale.US));
        for (String w : PY) LOWER.put(w, w.toLowerCase(Locale.US));
    }
    private static final Pattern WORD_PATTERN = Pattern.compile("[A-Za-z_][A-Za-z0-9_]*$");
    private static final Pattern CPP_COMMENT = Pattern.compile("//[^\\n]*|/\\*[\\s\\S]*?\\*/");
    private static final Pattern PY_COMMENT = Pattern.compile("#[^\\n]*");
    private static final Pattern STRING_PATTERN = Pattern.compile("\"(?:\\\\.|[^\"\\\\])*\"|'(?:\\\\.|[^'\\\\])*'");
    private static final Pattern NUMBER_PATTERN = Pattern.compile("\\b(?:0x[0-9A-Fa-f]+|\\d+(?:\\.\\d+)?)\\b");

    public CodeEditorView(Context c) { this(c, null); }
    public CodeEditorView(Context c, AttributeSet a) {
        super(c, a);
        setBackgroundColor(Color.BLACK);
        LinearLayout row = new LinearLayout(c);
        row.setOrientation(LinearLayout.HORIZONTAL);
        gutter = new TextView(c);
        gutter.setGravity(Gravity.TOP | Gravity.RIGHT);
        gutter.setPadding(dp(6), dp(8), dp(8), 0);
        gutter.setTextSize(12);
        gutter.setIncludeFontPadding(false);
        row.addView(gutter, new LinearLayout.LayoutParams(dp(44), -1));

        edit = new EditText(c);
        edit.setGravity(Gravity.TOP | Gravity.START);
        edit.setPadding(dp(8), dp(8), dp(18), dp(24));
        edit.setTextSize(fontSize);
        edit.setSingleLine(false);
        edit.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE | android.text.InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        edit.setHorizontallyScrolling(true);
        edit.setBackgroundColor(Color.TRANSPARENT);
        edit.setTypeface(Typeface.MONOSPACE);
        edit.setTextColor(Color.LTGRAY);
        edit.setHighlightColor(0x664C9AFF);
        edit.setTextIsSelectable(true);
        edit.setOverScrollMode(OVER_SCROLL_ALWAYS);
        row.addView(edit, new LinearLayout.LayoutParams(0, -1, 1));
        addView(row, new FrameLayout.LayoutParams(-1, -1));

        edit.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            public void onTextChanged(CharSequence s, int st, int before, int count) {
                if (applyingHighlight) return;
                updateGutter();
                scheduleHighlight();
                if (!suppressCompletion && count > 0) scheduleCompletion();
                else if (suppressCompletion) hideCompletions();
            }
            public void afterTextChanged(Editable e) {}
        });
        edit.setOnKeyListener((v, key, event) -> {
            if (event.getAction() != KeyEvent.ACTION_DOWN) return false;
            if (key == KeyEvent.KEYCODE_SPACE && event.isCtrlPressed()) {
                showCompletions();
                return true;
            }
            if (key == KeyEvent.KEYCODE_ESCAPE) {
                hideCompletions();
            }
            return false;
        });
        updateGutter();
    }

    private int dp(int v) { return (int) (v * getResources().getDisplayMetrics().density + .5f); }
    public EditText getEditText() { return edit; }

    public void configure(String themeName, String font, int size, boolean wrap) {
        theme = ThemeManager.get(themeName);
        typeface = FontManager.get(getContext(), font);
        fontSize = size;
        edit.setTextSize(size);
        edit.setTypeface(typeface);
        edit.setHorizontallyScrolling(!wrap);
        applyTheme();
    }

    public void applyTheme() {
        if (theme == null) theme = ThemeManager.get("Dark+");
        setBackgroundColor(theme.background);
        edit.setTextColor(theme.foreground);
        edit.setHighlightColor(theme.selection);
        gutter.setTextColor(theme.gutter);
        gutter.setTypeface(typeface == null ? Typeface.MONOSPACE : typeface);
        edit.setTypeface(typeface == null ? Typeface.MONOSPACE : typeface);
        scheduleHighlight();
    }

    public void setText(String s) {
        suppressCompletion = true;
        hideCompletions();
        edit.setText(s == null ? "" : s);
        edit.setSelection(edit.length());
        suppressCompletion = false;
        updateGutter();
        scheduleHighlight();
    }

    public String getText() { return edit.getText().toString(); }

    private void updateGutter() {
        int lines = 1;
        String text = getText();
        for (int i = 0; i < text.length(); i++) if (text.charAt(i) == '\n') lines++;
        StringBuilder b = new StringBuilder(lines * 3);
        for (int i = 1; i <= lines; i++) { b.append(i); if (i < lines) b.append('\n'); }
        gutter.setText(b.toString());
    }

    private void scheduleHighlight() {
        if (highlightTask != null) handler.removeCallbacks(highlightTask);
        highlightTask = () -> {
            // Avoid doing a full-document regex pass while the user is typing very large files.
            if (getText().length() <= 50000) highlight();
        };
        handler.postDelayed(highlightTask, 280);
    }

    private void scheduleCompletion() {
        if (completionTask != null) handler.removeCallbacks(completionTask);
        completionTask = () -> {
            String p = prefix();
            // Only auto-open after two characters. Ctrl+Space remains available at any point.
            if (p.length() >= 2) showCompletions();
            else hideCompletions();
        };
        handler.postDelayed(completionTask, 180);
    }

    private void highlight() {
        if (highlighting || theme == null) return;
        highlighting = true;
        Editable editable = edit.getText();
        String s = editable.toString();
        ForegroundColorSpan[] old = editable.getSpans(0, editable.length(), ForegroundColorSpan.class);
        for (ForegroundColorSpan span : old) editable.removeSpan(span);
        String lang = MainActivity.currentLanguage;
        if (lang.equals("C++") || lang.equals("C")) {
            highlightWords(editable, s, CPP, theme.keyword);
            highlightRegex(editable, CPP_COMMENT, theme.comment);
            highlightRegex(editable, STRING_PATTERN, theme.string);
            highlightRegex(editable, NUMBER_PATTERN, theme.number);
        } else if (lang.equals("Python")) {
            highlightWords(editable, s, PY, theme.keyword);
            highlightRegex(editable, PY_COMMENT, theme.comment);
            highlightRegex(editable, STRING_PATTERN, theme.string);
            highlightRegex(editable, NUMBER_PATTERN, theme.number);
        } else {
            highlightRegex(editable, STRING_PATTERN, theme.string);
            highlightRegex(editable, NUMBER_PATTERN, theme.number);
        }
        highlighting = false;
    }

    private void highlightWords(Editable sp, String s, String[] words, int color) {
        for (String w : words) {
            int from = 0;
            while (from < s.length()) {
                int at = s.indexOf(w, from);
                if (at < 0) break;
                int end = at + w.length();
                boolean left = at == 0 || !Character.isJavaIdentifierPart(s.charAt(at - 1));
                boolean right = end == s.length() || !Character.isJavaIdentifierPart(s.charAt(end));
                if (left && right) sp.setSpan(new ForegroundColorSpan(color), at, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                from = end;
            }
        }
    }

    private void highlightRegex(Editable sp, Pattern pattern, int color) {
        Matcher m = pattern.matcher(sp);
        while (m.find()) sp.setSpan(new ForegroundColorSpan(color), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private String prefix() {
        int p = edit.getSelectionStart();
        if (p < 0) return "";
        String s = getText();
        p = Math.min(p, s.length());
        Matcher m = WORD_PATTERN.matcher(s.substring(0, p));
        return m.find() ? m.group() : "";
    }

    private String[] words() { return MainActivity.currentLanguage.equals("Python") ? PY : CPP; }

    public void showCompletions() {
        String p = prefix();
        String lower = p.toLowerCase(Locale.US);
        ArrayList<String> list = new ArrayList<>();
        for (String w : words()) {
            if (lower.isEmpty() || LOWER.get(w).startsWith(lower)) list.add(w);
        }
        Collections.sort(list);
        if (list.size() > 14) list.subList(14, list.size()).clear();
        if (list.isEmpty()) { hideCompletions(); return; }

        hideCompletions();
        LinearLayout box = new LinearLayout(getContext());
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(4), dp(4), dp(4), dp(4));
        box.setBackgroundColor(theme == null ? 0xFF252526 : theme.gutter);
        for (String w : list) {
            TextView item = new TextView(getContext());
            item.setText(w);
            item.setTextSize(14);
            item.setTextColor(theme == null ? Color.WHITE : theme.foreground);
            item.setGravity(Gravity.CENTER_VERTICAL);
            item.setPadding(dp(12), 0, dp(12), 0);
            box.addView(item, new LinearLayout.LayoutParams(dp(240), dp(38)));
            item.setOnClickListener(v -> { insertCompletion(((TextView) v).getText().toString()); hideCompletions(); });
        }
        completion = new PopupWindow(box, dp(250), Math.min(dp(20 + 38 * list.size()), dp(330)), true);
        completion.setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(theme == null ? 0xFF252526 : theme.gutter));
        completion.setOutsideTouchable(true);
        completion.setElevation(dp(8));
        completion.showAsDropDown(edit, dp(35), -dp(65));
    }

    private void insertCompletion(String value) {
        int p = edit.getSelectionStart();
        String s = getText();
        Matcher m = WORD_PATTERN.matcher(s.substring(0, Math.max(0, p)));
        int st = m.find() ? m.start() : p;
        edit.getText().replace(st, p, value);
        edit.setSelection(st + value.length());
    }

    public void hideCompletions() {
        if (completion != null) { completion.dismiss(); completion = null; }
    }

    @Override public boolean onTouchEvent(MotionEvent e) { return true; }
}
