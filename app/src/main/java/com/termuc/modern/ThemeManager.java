package com.termuc.modern;

import android.graphics.Color;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ThemeManager {
    public static final class Theme {
        public final int background, foreground, keyword, string, number, comment, selection, gutter, accent;
        Theme(int bg,int fg,int kw,int str,int num,int com,int sel,int gut,int acc){background=bg;foreground=fg;keyword=kw;string=str;number=num;comment=com;selection=sel;gutter=gut;accent=acc;}
    }
    private static final Map<String,Theme> THEMES = new LinkedHashMap<>();
    static {
        add("Dark+", 0xFF1E1E1E,0xFFD4D4D4,0xFF569CD6,0xFFCE9178,0xFFB5CEA8,0xFF6A9955,0xFF264F78,0xFF858585,0xFF007ACC);
        add("Light+",0xFFFFFFFF,0xFF242424,0xFF0000FF,0xFFA31515,0xFF098658,0xFF008000,0xFFADD6FF,0xFF777777,0xFF007ACC);
        add("Monokai",0xFF272822,0xFFF8F8F2,0xFFF92672,0xFFE6DB74,0xFFAE81FF,0xFFA6E22E,0xFF49483E,0xFFB0B0A8,0xFFF92672);
        add("Dracula",0xFF282A36,0xFFF8F8F2,0xFFFF79C6,0xFFF1FA8C,0xFFBD93F9,0xFF6272A4,0xFF44475A,0xFF9AA0B0,0xFFBD93F9);
        add("One Dark Pro",0xFF282C34,0xFFABB2BF,0xFFC678DD,0xFFE5C07B,0xFFD19A66,0xFF98C379,0xFF3E4451,0xFF7F848E,0xFF61AFEF);
        add("Nord",0xFF2E3440,0xFFD8DEE9,0xFF81A1C1,0xFFEBCB8B,0xFFB48EAD,0xFF616E88,0xFF434C5E,0xFF7B8497,0xFF88C0D0);
        add("Solarized Dark",0xFF002B36,0xFF839496,0xFF268BD2,0xFF2AA198,0xFFD33682,0xFF859900,0xFF073642,0xFF657B83,0xFF2AA198);
        add("Solarized Light",0xFFFDF6E3,0xFF657B83,0xFF268BD2,0xFF2AA198,0xFFD33682,0xFF859900,0xFFEEE8D5,0xFF93A1A1,0xFF268BD2);
        add("GitHub Dark",0xFF0D1117,0xFFE6EDF3,0xFFFF7B72,0xFFA5D6FF,0xFFD2A8FF,0xFF8B949E,0xFF161B22,0xFF8B949E,0xFF58A6FF);
        add("GitHub Light",0xFFFFFFFF,0xFF1F2328,0xFFCF222E,0xFF0A3069,0xFF8250DF,0xFF6E7781,0xFFF6F8FA,0xFF656D76,0xFF0969DA);
        add("Tokyo Night",0xFF1A1B26,0xFFA9B1D6,0xFFBB9AF7,0xFFE0AF68,0xFF9ECE6A,0xFF565F89,0xFF24283B,0xFF7982A9,0xFF7AA2F7);
        add("Abyss",0xFF000C18,0xFFDDEEFF,0xFF75BEFF,0xFFCE9178,0xFFD7BA7D,0xFF6A9955,0xFF001F33,0xFF7F9DB9,0xFF75BEFF);
        add("High Contrast",0xFF000000,0xFFFFFFFF,0xFF00FFFF,0xFFFFFF00,0xFFFF00FF,0xFF00FF00,0xFF3F3F46,0xFFFFFFFF,0xFF00FFFF);
        add("Quiet Light",0xFFF5F5F5,0xFF333333,0xFFAF00DB,0xFF008000,0xFF795E26,0xFF008000,0xFFE7E7E7,0xFF777777,0xFF0066B8);
        add("Kimbie Dark",0xFF221A0F,0xFFD3AF86,0xFFF06431,0xFFD8B174,0xFFF8F8F2,0xFF889B4A,0xFF3E3228,0xFF9A8F84,0xFFD8B174);
        add("Cobalt2",0xFF193549,0xFFFF9D00,0xFFA5FF90,0xFFFF628C,0xFF0088FF,0xFF0088FF,0xFF234E70,0xFF8DA5B5,0xFFFFC600);
    }
    private static void add(String n,int bg,int fg,int kw,int st,int num,int co,int sel,int gut,int acc){THEMES.put(n,new Theme(bg,fg,kw,st,num,co,sel,gut,acc));}
    public static String[] names(){return THEMES.keySet().toArray(new String[0]);}
    public static Theme get(String name){Theme t=THEMES.get(name); return t==null?THEMES.get("Dark+"):t;}
}
