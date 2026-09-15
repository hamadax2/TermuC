package com.termuc.modern;

import java.util.Locale;

public final class Language {
    public static String of(String name){
        if(name==null)return "Text"; String n=name.toLowerCase(Locale.US);
        if(n.endsWith(".cpp")||n.endsWith(".cc")||n.endsWith(".cxx")||n.endsWith(".hpp"))return "C++";
        if(n.endsWith(".c")||n.endsWith(".h"))return "C";
        if(n.endsWith(".java"))return "Java";
        if(n.endsWith(".py"))return "Python";
        if(n.endsWith(".js")||n.endsWith(".ts"))return "JavaScript";
        if(n.endsWith(".json"))return "JSON";
        if(n.endsWith(".xml"))return "XML";
        if(n.endsWith(".kt")||n.endsWith(".kts"))return "Kotlin";
        if(n.endsWith(".sh"))return "Shell";
        if(n.endsWith(".md"))return "Markdown";
        return "Text";
    }
}
