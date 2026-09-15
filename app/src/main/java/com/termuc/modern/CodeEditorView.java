package com.termuc.modern;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.PopupWindow;
import android.widget.LinearLayout;
import android.view.View;
import android.view.ViewGroup;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeEditorView extends FrameLayout {
    private final TextView gutter;
    private final EditText edit;
    private PopupWindow completion;
    private ThemeManager.Theme theme;
    private Typeface typeface;
    private int fontSize=15;
    private boolean highlighting=false;
    private boolean applyingHighlight=false;
    private boolean suppressCompletion=false;
    private static final String[] CPP={"alignas","alignof","auto","bool","break","case","catch","char","class","const","constexpr","continue","default","delete","do","double","else","enum","explicit","extern","false","float","for","friend","if","inline","int","long","namespace","new","nullptr","operator","private","protected","public","return","short","signed","sizeof","static","std","struct","switch","template","this","throw","true","try","typedef","typename","union","unsigned","using","virtual","void","volatile","while","vector","string","cout","cin","endl"};
    private static final String[] PY={"and","as","assert","async","await","break","class","continue","def","del","elif","else","False","finally","for","from","global","if","import","in","is","lambda","None","not","or","pass","raise","return","True","try","while","with","yield","print","len","range"};
    public CodeEditorView(Context c){this(c,null);}
    public CodeEditorView(Context c, AttributeSet a){super(c,a); setBackgroundColor(Color.BLACK);
        LinearLayout row=new LinearLayout(c); row.setOrientation(LinearLayout.HORIZONTAL); row.setLayoutParams(new LayoutParams(-1,-1));
        gutter=new TextView(c); gutter.setGravity(Gravity.TOP|Gravity.RIGHT); gutter.setPadding(dp(6),dp(8),dp(8),0); gutter.setTextSize(12); gutter.setIncludeFontPadding(false);
        row.addView(gutter,new LinearLayout.LayoutParams(dp(44),-1));
        edit=new EditText(c); edit.setGravity(Gravity.TOP|Gravity.START); edit.setPadding(dp(8),dp(8),dp(18),dp(24)); edit.setTextSize(fontSize); edit.setSingleLine(false); edit.setInputType(android.text.InputType.TYPE_CLASS_TEXT|android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE|android.text.InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS); edit.setHorizontallyScrolling(true); edit.setBackgroundColor(Color.TRANSPARENT); edit.setTypeface(Typeface.MONOSPACE); edit.setTextColor(Color.LTGRAY); edit.setHighlightColor(0x664C9AFF); edit.setTextIsSelectable(true); edit.setOverScrollMode(OVER_SCROLL_ALWAYS);
        row.addView(edit,new LinearLayout.LayoutParams(0,-1,1)); addView(row);
        edit.addTextChangedListener(new TextWatcher(){boolean internal; public void beforeTextChanged(CharSequence s,int st,int c,int a){} public void onTextChanged(CharSequence s,int st,int b,int c){if(!internal && !applyingHighlight){updateGutter(); postDelayed(()->highlight(),80); if(b>0 && !suppressCompletion) maybeComplete();}} public void afterTextChanged(Editable e){}});
        edit.setOnKeyListener((v,key,event)->{if(event.getAction()==android.view.KeyEvent.ACTION_DOWN && key==android.view.KeyEvent.KEYCODE_SPACE && event.isCtrlPressed()){showCompletions();return true;} return false;});
        updateGutter();
    }
    private int dp(int v){return (int)(v*getResources().getDisplayMetrics().density+.5f);}
    public EditText getEditText(){return edit;}
    public void configure(String themeName,String font,int size,boolean wrap){theme=ThemeManager.get(themeName); typeface=FontManager.get(getContext(),font); fontSize=size; edit.setTextSize(size); edit.setTypeface(typeface); edit.setHorizontallyScrolling(!wrap); applyTheme();}
    public void applyTheme(){if(theme==null)theme=ThemeManager.get("Dark+"); setBackgroundColor(theme.background); edit.setTextColor(theme.foreground); edit.setHighlightColor(theme.selection); gutter.setTextColor(theme.gutter); gutter.setTypeface(typeface==null?Typeface.MONOSPACE:typeface); edit.setTypeface(typeface==null?Typeface.MONOSPACE:typeface);}
    public void setText(String s){suppressCompletion=true; edit.setText(s==null?"":s); edit.setSelection(edit.length()); suppressCompletion=false; updateGutter(); postDelayed(()->highlight(),100);}
    public String getText(){return edit.getText().toString();}
    private void updateGutter(){int lines=Math.max(1,getText().split("\\n",-1).length); StringBuilder b=new StringBuilder();for(int i=1;i<=lines;i++){b.append(i);if(i<lines)b.append('\n');}gutter.setText(b.toString());}
    private void highlight(){if(highlighting||theme==null)return; highlighting=true; String s=getText(); SpannableString sp=new SpannableString(s); String lang=MainActivity.currentLanguage;
        if(lang.equals("C++")||lang.equals("C")){highlightWords(sp,s,CPP,theme.keyword); highlightRegex(sp,s,"//[^\\n]*|/\\*[\\s\\S]*?\\*/",theme.comment); highlightRegex(sp,s,"\"(?:\\\\.|[^\"\\\\])*\"|'(?:\\\\.|[^'\\\\])*'",theme.string); highlightRegex(sp,s,"\\b(?:0x[0-9A-Fa-f]+|\\d+(?:\\.\\d+)?)\\b",theme.number);}
        else if(lang.equals("Python")){highlightWords(sp,s,PY,theme.keyword); highlightRegex(sp,s,"#[^\\n]*",theme.comment); highlightRegex(sp,s,"\"(?:\\\\.|[^\"\\\\])*\"|'(?:\\\\.|[^'\\\\])*'",theme.string); highlightRegex(sp,s,"\\b\\d+(?:\\.\\d+)?\\b",theme.number);}
        else {highlightRegex(sp,s,"\"(?:\\\\.|[^\"\\\\])*\"",theme.string); highlightRegex(sp,s,"\\b\\d+(?:\\.\\d+)?\\b",theme.number);}
        int cursor=edit.getSelectionStart(); applyingHighlight=true; edit.setText(sp,Spannable.BufferType.SPANNABLE); edit.setSelection(Math.min(Math.max(cursor,0),edit.length())); applyingHighlight=false; updateGutter(); highlighting=false;
    }
    private void highlightWords(SpannableString sp,String s,String[] words,int color){for(String w:words){Matcher m=Pattern.compile("\\b"+Pattern.quote(w)+"\\b").matcher(s);while(m.find())sp.setSpan(new ForegroundColorSpan(color),m.start(),m.end(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);}}
    private void highlightRegex(SpannableString sp,String s,String regex,int color){Matcher m=Pattern.compile(regex).matcher(s);while(m.find())sp.setSpan(new ForegroundColorSpan(color),m.start(),m.end(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);}
    private String prefix(){int p=edit.getSelectionStart();if(p<0)return "";String s=getText().substring(0,p);Matcher m=Pattern.compile("[A-Za-z_][A-Za-z0-9_]*$").matcher(s);return m.find()?m.group():"";}
    private String[] words(){String l=MainActivity.currentLanguage; if(l.equals("Python"))return PY; return CPP;}
    private void maybeComplete(){String p=prefix(); if(p.length()>=2 || getText().endsWith(".")) showCompletions(); else hideCompletions();}
    public void showCompletions(){String p=prefix(); ArrayList<String> list=new ArrayList<>(); for(String w:words())if(p.isEmpty()||w.toLowerCase(Locale.US).startsWith(p.toLowerCase(Locale.US)))list.add(w); Collections.sort(list); if(list.size()>16)list.subList(16,list.size()).clear(); if(list.isEmpty()){hideCompletions();return;}
        LinearLayout box=new LinearLayout(getContext());box.setOrientation(LinearLayout.VERTICAL);box.setPadding(dp(4),dp(4),dp(4),dp(4));box.setBackgroundColor(theme==null?0xFF252526:theme.gutter);
        for(String w:list){TextView item=new TextView(getContext());item.setText(w);item.setTextSize(14);item.setTextColor(theme==null?Color.WHITE:theme.foreground);item.setGravity(Gravity.CENTER_VERTICAL);item.setPadding(dp(12),0,dp(12),0);box.addView(item,new LinearLayout.LayoutParams(dp(220),dp(40)));item.setOnClickListener(v->{insertCompletion(((TextView)v).getText().toString());hideCompletions();});}
        completion=new PopupWindow(box,dp(240),Math.min(dp(16+40*list.size()),dp(360)),true);completion.setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(theme==null?0xFF252526:theme.gutter));completion.setOutsideTouchable(true);completion.setElevation(dp(8));completion.showAsDropDown(edit,dp(48),-dp(100));
    }
    private void insertCompletion(String value){int p=edit.getSelectionStart();String s=getText();Matcher m=Pattern.compile("[A-Za-z_][A-Za-z0-9_]*$").matcher(s.substring(0,p));int st=m.find()?m.start():p;edit.getText().replace(st,p,value);edit.setSelection(st+value.length());}
    public void hideCompletions(){if(completion!=null){completion.dismiss();completion=null;}}
    @Override public boolean onTouchEvent(MotionEvent e){return true;}
}
