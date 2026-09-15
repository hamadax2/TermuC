package com.termuc.modern;

import android.content.Context;

public final class Prefs {
    private static final String P = "termuc";
    private Prefs() {}
    public static String theme(Context c) { return c.getSharedPreferences(P,0).getString("theme","Dark+"); }
    public static void theme(Context c, String v) { c.getSharedPreferences(P,0).edit().putString("theme",v).apply(); }
    public static String font(Context c) { return c.getSharedPreferences(P,0).getString("font","JetBrains Mono"); }
    public static void font(Context c, String v) { c.getSharedPreferences(P,0).edit().putString("font",v).apply(); }
    public static int size(Context c) { return c.getSharedPreferences(P,0).getInt("size",15); }
    public static void size(Context c, int v) { c.getSharedPreferences(P,0).edit().putInt("size",v).apply(); }
    public static boolean wordWrap(Context c) { return c.getSharedPreferences(P,0).getBoolean("wrap",false); }
    public static void wordWrap(Context c, boolean v) { c.getSharedPreferences(P,0).edit().putBoolean("wrap",v).apply(); }
}
