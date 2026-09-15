package com.termuc.modern;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    private LinearLayout root;
    private TextView themeValue, fontValue;
    private Switch wrap;
    private int dp(int n){return(int)(n*getResources().getDisplayMetrics().density+.5f);}
    private TextView text(String s,float z,int c){TextView t=new TextView(this);t.setText(s);t.setTextSize(z);t.setTextColor(c);t.setGravity(Gravity.CENTER_VERTICAL);return t;}
    @Override protected void onCreate(Bundle b){super.onCreate(b);getWindow().setStatusBarColor(0xFF181818);build();}
    private void build(){
        root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setBackgroundColor(0xFF1E1E1E);setContentView(root);
        LinearLayout top=new LinearLayout(this);top.setGravity(Gravity.CENTER_VERTICAL);top.setPadding(dp(8),0,dp(12),0);top.setBackgroundColor(0xFF181818);root.addView(top,new LinearLayout.LayoutParams(-1,dp(60)));
        TextView back=text("‹",34,Color.WHITE);back.setGravity(Gravity.CENTER);top.addView(back,new LinearLayout.LayoutParams(dp(48),-1));back.setOnClickListener(v->finish());
        TextView title=text("Settings",20,Color.WHITE);title.setTypeface(null,1);top.addView(title,new LinearLayout.LayoutParams(0,-1,1));
        ScrollView scroll=new ScrollView(this);LinearLayout body=new LinearLayout(this);body.setOrientation(LinearLayout.VERTICAL);body.setPadding(dp(16),dp(16),dp(16),dp(28));scroll.addView(body);root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
        addHeader(body,"EDITOR");
        themeValue=text(Prefs.theme(this),15,0xFFD4D4D4);addRow(body,"Color Theme","15+ VS Code-inspired themes",themeValue,v->chooseTheme());
        fontValue=text(Prefs.font(this),15,0xFFD4D4D4);addRow(body,"Editor Font","Bundled developer fonts",fontValue,v->chooseFont());
        addRow(body,"Font Size",Prefs.size(this)+" sp","Tap to choose a size",v->chooseSize());
        LinearLayout wrapRow=new LinearLayout(this);wrapRow.setGravity(Gravity.CENTER_VERTICAL);wrapRow.setPadding(dp(14),dp(8),dp(10),dp(8));wrapRow.setBackground(round(0xFF252526,10));
        TextView wt=text("Word Wrap\nLong lines wrap inside the editor",15,0xFFD4D4D4);wrap=new Switch(this);wrap.setChecked(Prefs.wordWrap(this));wrap.setOnCheckedChangeListener((button,checked)->Prefs.wordWrap(this,checked));wrapRow.addView(wt,new LinearLayout.LayoutParams(0,dp(64),1));wrapRow.addView(wrap,new LinearLayout.LayoutParams(dp(60),dp(60)));body.addView(wrapRow,new LinearLayout.LayoutParams(-1,dp(82)));
        addHeader(body,"THEMES INCLUDED");TextView info=text("Dark+ • Light+ • Monokai • Dracula • One Dark Pro • Nord • Solarized Dark/Light • GitHub Dark/Light • Tokyo Night • Abyss • High Contrast • Quiet Light • Kimbie Dark • Cobalt2",14,0xFF9DA3AE);info.setPadding(dp(14),dp(4),dp(14),dp(20));body.addView(info);
        addHeader(body,"FONTS INCLUDED");TextView fonts=text("JetBrains Mono • Cascadia Code • Source Code Pro • Roboto Mono • DejaVu Sans Mono • Noto Sans Mono • Liberation Mono • Inter",14,0xFF9DA3AE);fonts.setPadding(dp(14),dp(4),dp(14),dp(20));body.addView(fonts);
        addHeader(body,"FEATURES");TextView features=text("✓ Android file picker\n✓ Save / Save As\n✓ Syntax highlighting\n✓ C/C++ and Python autocomplete\n✓ Ctrl + Space completion\n✓ Find in file\n✓ Optional Termux integration\n✓ Phone and tablet friendly",14,0xFF9DA3AE);features.setPadding(dp(14),dp(4),dp(14),dp(20));body.addView(features);
    }
    private void addHeader(LinearLayout b,String s){TextView h=text(s,11,0xFF6E7681);h.setPadding(dp(14),dp(18),dp(14),dp(6));b.addView(h,new LinearLayout.LayoutParams(-1,dp(38)));}
    private void addRow(LinearLayout b,String title,String sub,String value,android.view.View.OnClickListener listener){LinearLayout row=new LinearLayout(this);row.setOrientation(LinearLayout.VERTICAL);row.setPadding(dp(14),dp(8),dp(14),dp(8));row.setBackground(round(0xFF252526,10));TextView a=text(title,16,Color.WHITE);TextView c=text(sub,12,0xFF9DA3AE);TextView v=text(value,14,0xFF9DA3AE);row.addView(a,new LinearLayout.LayoutParams(-1,dp(30)));row.addView(c,new LinearLayout.LayoutParams(-1,dp(25)));row.addView(v,new LinearLayout.LayoutParams(-1,dp(30)));row.setOnClickListener(listener);LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,dp(96));lp.setMargins(0,0,0,dp(10));b.addView(row,lp);}
    private GradientDrawable round(int c,int r){GradientDrawable g=new GradientDrawable();g.setColor(c);g.setCornerRadius(dp(r));return g;}
    private void chooseTheme(){String[] n=ThemeManager.names();new AlertDialog.Builder(this).setTitle("Color Theme").setSingleChoiceItems(n,index(n,Prefs.theme(this)),(d,w)->{Prefs.theme(this,n[w]);d.dismiss();recreate();}).show();}
    private void chooseFont(){String[] n=FontManager.names();new AlertDialog.Builder(this).setTitle("Editor Font").setSingleChoiceItems(n,index(n,Prefs.font(this)),(d,w)->{Prefs.font(this,n[w]);d.dismiss();recreate();}).show();}
    private void chooseSize(){int[] v={12,13,14,15,16,17,18,20,22};String[] n={"12 sp","13 sp","14 sp","15 sp","16 sp","17 sp","18 sp","20 sp","22 sp"};new AlertDialog.Builder(this).setTitle("Editor Font Size").setSingleChoiceItems(n,indexSize(v,Prefs.size(this)),(d,w)->{Prefs.size(this,v[w]);d.dismiss();recreate();}).show();}
    private int index(String[] a,String x){for(int i=0;i<a.length;i++)if(a[i].equals(x))return i;return 0;} private int indexSize(int[] a,int x){for(int i=0;i<a.length;i++)if(a[i]==x)return i;return 3;}
}
