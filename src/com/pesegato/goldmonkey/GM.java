
package com.pesegato.goldmonkey;

import com.jme3.math.ColorRGBA;
import model.builders.definitions.BuilderManager;

public class GM {
    public static ColorRGBA getColorRGBA(String id){
        return ((ColorBuilder)BuilderManager.getBuilder("com.pesegato.goldmonkey.ColorBuilder",id, ColorBuilder.class)).buildColorRGBA();
    }
    public static boolean existsData(String id) {
        try {
            DataBuilder d = ((DataBuilder) BuilderManager.getBuilder("com.pesegato.goldmonkey.DataBuilder", id, DataBuilder.class));
            return true;
        } catch (java.lang.IllegalArgumentException e) {
            return false;
        }
    }
    public static boolean getBool(String id){
        return ((DataBuilder)BuilderManager.getBuilder("com.pesegato.goldmonkey.DataBuilder",id, DataBuilder.class)).buildBoolean();
    }
    public static int getInt(String id){
        return ((DataBuilder)BuilderManager.getBuilder("com.pesegato.goldmonkey.DataBuilder",id, DataBuilder.class)).buildInt();
    }
    public static float getFloat(String id){
        return ((DataBuilder)BuilderManager.getBuilder("com.pesegato.goldmonkey.DataBuilder",id, DataBuilder.class)).buildFloat();
    }
    public static double getDouble(String id){
        return ((DataBuilder)BuilderManager.getBuilder("com.pesegato.goldmonkey.DataBuilder",id, DataBuilder.class)).buildDouble();
    }
    public static String getString(String id){
        return ((DataBuilder)BuilderManager.getBuilder("com.pesegato.goldmonkey.DataBuilder",id, DataBuilder.class)).buildString();
    }
}
