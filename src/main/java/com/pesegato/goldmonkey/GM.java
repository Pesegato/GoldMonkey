
package com.pesegato.goldmonkey;

import com.jme3.math.ColorRGBA;
import model.builders.definitions.BuilderManager;

public class GM {
    public static ColorRGBA getColorRGBA(String id){
        return ((ColorBuilder)BuilderManager.getBuilder("com.pesegato.goldmonkey.ColorBuilder",id, ColorBuilder.class)).buildColorRGBA();
    }
}
