package com.termuc.modern;

import android.content.Context;
import android.graphics.Typeface;
import java.io.IOException;

public final class FontManager {
    private static final String[] NAMES={"JetBrains Mono","Cascadia Code","Source Code Pro","Roboto Mono","DejaVu Sans Mono","Noto Sans Mono","Liberation Mono","Inter"};
    private static final String[] FILES={"JetBrainsMono-Regular.otf","CascadiaCode-Regular.otf","SourceCodePro-Regular.otf","RobotoMono-Regular.otf","DejaVuSansMono.ttf","NotoSansMono-Regular.ttf","LiberationMono-Regular.ttf","InterDisplay-Regular.otf"};
    public static String[] names(){return NAMES.clone();}
    public static Typeface get(Context c,String name){
        for(int i=0;i<NAMES.length;i++) if(NAMES[i].equals(name)){
            try{return Typeface.createFromAsset(c.getAssets(),"fonts/"+FILES[i]);}catch(Exception ignored){return Typeface.MONOSPACE;}
        }
        return Typeface.MONOSPACE;
    }
}
