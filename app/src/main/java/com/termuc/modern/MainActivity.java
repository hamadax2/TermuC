package com.termuc.modern;

import android.app.*;
import android.content.*;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.util.Base64;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.*;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.drawerlayout.widget.DrawerLayout;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class MainActivity extends AppCompatActivity {
    public static String currentLanguage="Text";
    private DrawerLayout drawer; private CodeEditorView editor; private TextView title, langStatus, positionStatus, tabName, welcomeTitle; private LinearLayout explorerList; private Uri currentUri; private String currentName="Untitled.cpp"; private boolean dirty=false;
    private final ActivityResultLauncher<Intent> openFile=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),r->{if(r.getResultCode()==RESULT_OK&&r.getData()!=null&&r.getData().getData()!=null)loadUri(r.getData().getData());});
    @Override protected void onCreate(Bundle b){super.onCreate(b);getWindow().setStatusBarColor(Color.rgb(24,24,24));getWindow().setNavigationBarColor(Color.rgb(16,16,16));buildUi();applyPrefs();handleIntent(getIntent());}
    @Override protected void onNewIntent(Intent i){super.onNewIntent(i);handleIntent(i);}
    private void handleIntent(Intent i){if(i!=null&&i.getData()!=null)loadUri(i.getData());}
    private int dp(int n){return(int)(n*getResources().getDisplayMetrics().density+.5f);}
    private TextView tv(String text,float size,int color){AppCompatTextView t=new AppCompatTextView(this);t.setText(text);t.setTextSize(size);t.setTextColor(color);t.setGravity(Gravity.CENTER_VERTICAL);return t;}
    private GradientDrawable bg(int color,float radius){GradientDrawable g=new GradientDrawable();g.setColor(color);g.setCornerRadius(dp((int)radius));return g;}
    private TextView action(String icon,String label){TextView t=tv(icon,20,Color.WHITE);t.setGravity(Gravity.CENTER);t.setContentDescription(label);t.setBackground(bg(0xFF252526,10));t.setPadding(dp(8),0,dp(8),0);return t;}
    private void buildUi(){
        drawer=new DrawerLayout(this); drawer.setBackgroundColor(0xFF1E1E1E); setContentView(drawer);
        LinearLayout main=new LinearLayout(this);main.setOrientation(LinearLayout.VERTICAL);drawer.addView(main,new DrawerLayout.LayoutParams(-1,-1));
        // VS Code-like top bar
        LinearLayout bar=new LinearLayout(this);bar.setGravity(Gravity.CENTER_VERTICAL);bar.setPadding(dp(8),dp(4),dp(8),dp(4));bar.setBackgroundColor(0xFF181818);main.addView(bar,new LinearLayout.LayoutParams(-1,dp(58)));
        TextView menu=action("☰","Explorer");bar.addView(menu,new LinearLayout.LayoutParams(dp(44),dp(44)));menu.setOnClickListener(v->drawer.openDrawer(Gravity.START));
        title=tv("TermuC",20,Color.WHITE);title.setTypeface(null,1);title.setPadding(dp(12),0,0,0);bar.addView(title,new LinearLayout.LayoutParams(0,-1,1));
        TextView save=action("▣","Save");bar.addView(save,new LinearLayout.LayoutParams(dp(44),dp(44)));save.setOnClickListener(v->saveFile());
        TextView run=action("▶","Run C++");
        run.setTextSize(17);
        run.setText("▶");
        run.setBackground(bg(0xFF16825D,10));
        run.setPadding(dp(12),0,dp(12),0);
        bar.addView(run,new LinearLayout.LayoutParams(dp(52),dp(44)));
        run.setOnClickListener(v->runTermux());
        TextView more=action("⋮","More");bar.addView(more,new LinearLayout.LayoutParams(dp(44),dp(44)));more.setOnClickListener(v->showMore());
        // tab
        LinearLayout tab=new LinearLayout(this);tab.setGravity(Gravity.CENTER_VERTICAL);tab.setPadding(dp(12),0,dp(8),0);tab.setBackgroundColor(0xFF202020);main.addView(tab,new LinearLayout.LayoutParams(-1,dp(42)));
        tabName=tv("  Welcome",14,0xFFD4D4D4);tab.addView(tabName,new LinearLayout.LayoutParams(0,-1,1));TextView close=tv("×",22,0xFFAAAAAA);close.setGravity(Gravity.CENTER);tab.addView(close,new LinearLayout.LayoutParams(dp(40),-1));close.setOnClickListener(v->showWelcome());
        FrameLayout editorFrame=new FrameLayout(this);main.addView(editorFrame,new LinearLayout.LayoutParams(-1,0,1));
        editor=new CodeEditorView(this);editorFrame.addView(editor,new FrameLayout.LayoutParams(-1,-1));
        buildWelcome(editorFrame);
        // status bar
        LinearLayout status=new LinearLayout(this);status.setGravity(Gravity.CENTER_VERTICAL);status.setPadding(dp(10),0,dp(10),0);status.setBackgroundColor(0xFF007ACC);main.addView(status,new LinearLayout.LayoutParams(-1,dp(28)));
        langStatus=tv("C++",12,Color.WHITE);status.addView(langStatus,new LinearLayout.LayoutParams(0,-1,1));positionStatus=tv("Ln 1, Col 1   UTF-8   Spaces: 4",12,Color.WHITE);positionStatus.setGravity(Gravity.CENTER);status.addView(positionStatus,new LinearLayout.LayoutParams(-2,-1));
        editor.getEditText().setOnFocusChangeListener((v,has)->updateStatus());
        editor.getEditText().addTextChangedListener(new android.text.TextWatcher(){public void beforeTextChanged(CharSequence s,int st,int c,int a){} public void onTextChanged(CharSequence s,int st,int b,int c){dirty=true; updateStatus(); tabName.setText("  "+currentName+" •");} public void afterTextChanged(android.text.Editable e){}});
        // explorer drawer
        LinearLayout side=new LinearLayout(this);side.setOrientation(LinearLayout.VERTICAL);side.setBackgroundColor(0xFF181818);side.setPadding(dp(8),dp(12),dp(8),dp(8));
        TextView head=tv("EXPLORER",12,0xFFAAAAAA);head.setPadding(dp(10),0,0,0);side.addView(head,new LinearLayout.LayoutParams(-1,dp(42)));
        LinearLayout fileActions=new LinearLayout(this);fileActions.setPadding(dp(4),0,dp(4),dp(6));TextView nf=action("＋","New file");TextView of=action("▱","Open file");fileActions.addView(nf,new LinearLayout.LayoutParams(0,dp(42),1));fileActions.addView(of,new LinearLayout.LayoutParams(0,dp(42),1));side.addView(fileActions);nf.setOnClickListener(v->newFile());of.setOnClickListener(v->openFile());
        TextView filesHead=tv("OPEN EDITORS",11,0xFF888888);filesHead.setPadding(dp(10),dp(8),0,dp(4));side.addView(filesHead,new LinearLayout.LayoutParams(-1,dp(34)));
        explorerList=new LinearLayout(this);explorerList.setOrientation(LinearLayout.VERTICAL);side.addView(explorerList,new LinearLayout.LayoutParams(-1,0,1));
        TextView settings=tv("⚙  Settings",15,0xFFD4D4D4);settings.setPadding(dp(12),0,0,0);settings.setBackground(bg(0xFF252526,8));side.addView(settings,new LinearLayout.LayoutParams(-1,dp(48)));settings.setOnClickListener(v->{startActivity(new Intent(this,SettingsActivity.class));});
        DrawerLayout.LayoutParams sp=new DrawerLayout.LayoutParams(dp(300),-1);sp.gravity=Gravity.START;drawer.addView(side,sp);
    }
    private void buildWelcome(FrameLayout frame){LinearLayout w=new LinearLayout(this);w.setOrientation(LinearLayout.VERTICAL);w.setGravity(Gravity.CENTER);w.setPadding(dp(28),dp(30),dp(28),dp(30));w.setBackgroundColor(0xFF1E1E1E);
        welcomeTitle=tv("Welcome to TermuC",26,Color.WHITE);welcomeTitle.setGravity(Gravity.CENTER);w.addView(welcomeTitle,new LinearLayout.LayoutParams(-1,dp(55)));
        TextView sub=tv("A modern C/C++ editor for Android",15,0xFF9DA3AE);sub.setGravity(Gravity.CENTER);w.addView(sub,new LinearLayout.LayoutParams(-1,dp(42)));
        LinearLayout actions=new LinearLayout(this);actions.setGravity(Gravity.CENTER);actions.setPadding(0,dp(24),0,dp(16));Button open=new Button(this);open.setText("Open File");Button create=new Button(this);create.setText("New C++ File");actions.addView(open,new LinearLayout.LayoutParams(dp(145),dp(52)));actions.addView(create,new LinearLayout.LayoutParams(dp(160),dp(52)));w.addView(actions);open.setOnClickListener(v->openFile());create.setOnClickListener(v->newFile());
        TextView hint=tv("☰  Explorer     •     ⚙  Settings     •     Ctrl + Space  Autocomplete",13,0xFF777777);hint.setGravity(Gravity.CENTER);w.addView(hint,new LinearLayout.LayoutParams(-1,dp(48)));frame.addView(w,new FrameLayout.LayoutParams(-1,-1));frame.setTag(w);
    }
    private void showWelcome(){FrameLayout f=(FrameLayout)editor.getParent();View w=(View)f.getTag();editor.setVisibility(View.GONE);w.setVisibility(View.VISIBLE);tabName.setText("  Welcome");currentUri=null;currentName="Untitled.cpp";updateStatus();}
    private void showEditor(){FrameLayout f=(FrameLayout)editor.getParent();View w=(View)f.getTag();w.setVisibility(View.GONE);editor.setVisibility(View.VISIBLE);tabName.setText("  "+currentName+(dirty?" •":""));}
    private void openFile(){Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);i.addCategory(Intent.CATEGORY_OPENABLE);i.setType("*/*");i.putExtra(Intent.EXTRA_MIME_TYPES,new String[]{"text/*","application/octet-stream"});openFile.launch(i);}
    private void loadUri(Uri uri){try{String name=queryName(uri);InputStream in=getContentResolver().openInputStream(uri);if(in==null)throw new IOException("Cannot open file");ByteArrayOutputStream out=new ByteArrayOutputStream();byte[] b=new byte[8192];int n;while((n=in.read(b))>0)out.write(b,0,n);in.close();currentUri=uri;currentName=name;currentLanguage=Language.of(name);editor.setText(out.toString(StandardCharsets.UTF_8.name()));dirty=false;showEditor();updateStatus();addExplorerItem();try{getContentResolver().takePersistableUriPermission(uri,Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_WRITE_URI_PERMISSION);}catch(Exception ignored){}}catch(Exception e){toast("Open failed: "+e.getMessage());}}
    private String queryName(Uri uri){CursorLike c=new CursorLike(getContentResolver(),uri);String n=c.name();return n==null?"Untitled.cpp":n;}
    private void newFile(){final EditText input=new EditText(this);input.setHint("main.cpp");input.setSingleLine();new AlertDialog.Builder(this).setTitle("New file").setView(input).setNegativeButton("Cancel",null).setPositiveButton("Create",(d,w)->{String n=input.getText().toString().trim();if(n.isEmpty())n="main.cpp";currentUri=null;currentName=n;currentLanguage=Language.of(n);editor.setText(defaultTemplate(currentLanguage));dirty=false;showEditor();updateStatus();addExplorerItem();}).show();}
    private String defaultTemplate(String lang){if(lang.equals("Python"))return "def main():\n    print(\"Hello from TermuC\")\n\nif __name__ == \"__main__\":\n    main()\n";if(lang.equals("Java"))return "public class Main {\n    public static void main(String[] args) {\n        System.out.println(\"Hello from TermuC\");\n    }\n}\n";return "#include <iostream>\n#include <vector>\n\nint main() {\n    std::cout << \"Hello from TermuC\" << std::endl;\n    return 0;\n}\n";}
    private void saveFile(){if(currentUri==null){saveAs();return;}try(OutputStream out=getContentResolver().openOutputStream(currentUri,"wt")){if(out==null)throw new IOException("Cannot write");out.write(editor.getText().getBytes(StandardCharsets.UTF_8));dirty=false;tabName.setText("  "+currentName);toast("Saved");}catch(Exception e){toast("Save failed: "+e.getMessage());}}
    private void saveAs(){Intent i=new Intent(Intent.ACTION_CREATE_DOCUMENT);i.addCategory(Intent.CATEGORY_OPENABLE);i.setType("text/plain");i.putExtra(Intent.EXTRA_TITLE,currentName);saveLauncher.launch(i);}
    private final ActivityResultLauncher<Intent> saveLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),r->{if(r.getResultCode()==RESULT_OK&&r.getData()!=null&&r.getData().getData()!=null){currentUri=r.getData().getData();String n=queryName(currentUri);if(n!=null)currentName=n;saveFile();}});
    private void addExplorerItem(){explorerList.removeAllViews();TextView f=tv("  ◉  "+currentName,14,0xFFD4D4D4);f.setPadding(dp(6),0,0,0);explorerList.addView(f,new LinearLayout.LayoutParams(-1,dp(42)));f.setOnClickListener(v->{showEditor();});}
    private void updateStatus(){langStatus.setText(currentLanguage);int p=editor.getEditText().getSelectionStart();if(p<0)p=0;String s=editor.getText();int line=1,col=1;for(int i=0;i<p&&i<s.length();i++){if(s.charAt(i)=='\n'){line++;col=1;}else col++;}positionStatus.setText("Ln "+line+", Col "+col+"   UTF-8   Spaces: 4");}
    private void applyPrefs(){editor.configure(Prefs.theme(this),Prefs.font(this),Prefs.size(this),Prefs.wordWrap(this));}
    @Override protected void onResume(){super.onResume();applyPrefs();}
    private void showMore(){PopupWindow p=new PopupWindow(this);LinearLayout box=new LinearLayout(this);box.setOrientation(LinearLayout.VERTICAL);box.setPadding(dp(6),dp(6),dp(6),dp(6));box.setBackgroundColor(0xFF252526);String[] items={"⚙  Settings","⌕  Find / Replace","▶  Run with Termux","↶  Undo","↷  Redo"};for(String x:items){TextView t=tv(x,15,Color.WHITE);t.setPadding(dp(14),0,dp(40),0);box.addView(t,new LinearLayout.LayoutParams(dp(230),dp(48)));if(x.contains("Settings"))t.setOnClickListener(v->{p.dismiss();startActivity(new Intent(this,SettingsActivity.class));});else if(x.contains("Find"))t.setOnClickListener(v->{p.dismiss();showFind();});else if(x.contains("Run"))t.setOnClickListener(v->{p.dismiss();runTermux();});else if(x.contains("Undo"))t.setOnClickListener(v->{p.dismiss();editor.getEditText().onKeyDown(android.view.KeyEvent.KEYCODE_Z,new android.view.KeyEvent(0,android.view.KeyEvent.KEYCODE_Z));});else if(x.contains("Redo"))t.setOnClickListener(v->{p.dismiss();});}p.setContentView(box);p.setWidth(dp(240));p.setHeight(dp(5*48+12));p.setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(0xFF252526));p.setOutsideTouchable(true);p.setFocusable(true);p.setElevation(dp(10));p.showAsDropDown(title,dp(100),-dp(8));}
    private void showFind(){final EditText e=new EditText(this);e.setHint("Find text");new AlertDialog.Builder(this).setTitle("Find in file").setView(e).setPositiveButton("Find",(d,w)->{String q=e.getText().toString();int p=editor.getText().indexOf(q,Math.max(0,editor.getEditText().getSelectionStart()));if(p>=0){editor.getEditText().requestFocus();editor.getEditText().setSelection(p,p+q.length());}else toast("Not found");}).setNegativeButton("Cancel",null).show();}
    private void runTermux(){
        if (!isTermuxInstalled()) {
            new AlertDialog.Builder(this)
                    .setTitle("Termux not installed")
                    .setMessage("Install Termux first, then grant TermuC the 'Run commands in Termux environment' permission.")
                    .setPositiveButton("OK", null).show();
            return;
        }

        final String lang = currentLanguage;
        final String source = editor.getText();
        if (source.trim().isEmpty()) { toast("Nothing to run"); return; }

        String commandPath;
        String[] args;
        String stdin = source;
        String workdir = "/data/data/com.termux/files/home";

        if (lang.equals("C++")) {
            commandPath = "/data/data/com.termux/files/usr/bin/bash";
            args = new String[]{"-c", "dir=\"$HOME/.termuc-run\"; mkdir -p \"$dir\"; trap 'rm -f \"$dir/main.cpp\" \"$dir/app\"' EXIT; cat > \"$dir/main.cpp\"; g++ -std=c++17 \"$dir/main.cpp\" -o \"$dir/app\" && \"$dir/app\""};
        } else if (lang.equals("C")) {
            commandPath = "/data/data/com.termux/files/usr/bin/bash";
            args = new String[]{"-c", "dir=\"$HOME/.termuc-run\"; mkdir -p \"$dir\"; trap 'rm -f \"$dir/main.c\" \"$dir/app\"' EXIT; cat > \"$dir/main.c\"; gcc -std=c17 \"$dir/main.c\" -o \"$dir/app\" && \"$dir/app\""};
        } else if (lang.equals("Python")) {
            commandPath = "/data/data/com.termux/files/usr/bin/python3";
            args = new String[]{"-"};
        } else if (lang.equals("Java")) {
            commandPath = "/data/data/com.termux/files/usr/bin/bash";
            String cls = javaMainClass(source);
            String safe = cls.replaceAll("[^A-Za-z0-9_$]", "");
            if (safe.isEmpty()) safe = "Main";
            final String javaFile = safe + ".java";
            args = new String[]{"-c", "dir=\"$HOME/.termuc-run\"; mkdir -p \"$dir\"; trap 'rm -rf \"$dir\"' EXIT; cat > \"$dir/" + javaFile + "\"; javac \"$dir/" + javaFile + "\" && java -cp \"$dir\" " + safe};
        } else {
            toast("Run currently supports C, C++, Java and Python");
            return;
        }

        try {
            Intent i = new Intent("com.termux.RUN_COMMAND");
            i.setClassName("com.termux", "com.termux.app.RunCommandService");
            i.putExtra("com.termux.RUN_COMMAND_PATH", commandPath);
            i.putExtra("com.termux.RUN_COMMAND_ARGUMENTS", args);
            i.putExtra("com.termux.RUN_COMMAND_WORKDIR", workdir);
            i.putExtra("com.termux.RUN_COMMAND_STDIN", stdin);
            i.putExtra("com.termux.RUN_COMMAND_BACKGROUND", false);
            i.putExtra("com.termux.RUN_COMMAND_SESSION_ACTION", "0");
            i.putExtra("com.termux.RUN_COMMAND_COMMAND_LABEL", "TermuC: Run " + currentName);
            startService(i);
            toast("Running in Termux…");
        } catch (SecurityException e) {
            showTermuxPermissionHelp();
        } catch (Exception e) {
            showTermuxPermissionHelp();
        }
    }

    private boolean isTermuxInstalled(){
        try { getPackageManager().getPackageInfo("com.termux", 0); return true; }
        catch (Exception e) { return false; }
    }

    private String javaMainClass(String source){
        java.util.regex.Matcher m=java.util.regex.Pattern.compile("(?:public\\s+)?class\\s+([A-Za-z_$][A-Za-z0-9_$]*)").matcher(source);
        return m.find()?m.group(1):"Main";
    }

    private void showTermuxPermissionHelp(){
        new AlertDialog.Builder(this)
                .setTitle("Enable Termux integration")
                .setMessage("1. Open Termux and run:\n\nmkdir -p ~/.termux && echo 'allow-external-apps=true' >> ~/.termux/termux.properties\n\n2. Restart Termux.\n3. Android Settings → Apps → TermuC → Permissions → Additional permissions → enable 'Run commands in Termux environment'.\n\nThen press Run again.")
                .setPositiveButton("OK", null).show();
    }

    private void toast(String s){Toast.makeText(this,s,Toast.LENGTH_SHORT).show();}
    @Override public void onBackPressed(){if(drawer.isDrawerOpen(Gravity.START))drawer.closeDrawer(Gravity.START);else super.onBackPressed();}
    private static class CursorLike{android.content.ContentResolver r;Uri u;CursorLike(android.content.ContentResolver r,Uri u){this.r=r;this.u=u;}String name(){android.database.Cursor c=null;try{c=r.query(u,new String[]{OpenableColumns.DISPLAY_NAME},null,null,null);if(c!=null&&c.moveToFirst())return c.getString(0);}catch(Exception ignored){}finally{if(c!=null)c.close();}return null;}}
}
