package com.pesegato.goldmonkey;

import com.jme3.math.ColorRGBA;

class GoldColorRGBA {
    String id;
    int red;
    int green;
    int blue;
    int alpha;
    GoldColorRGBA(String id, int red, int green, int blue, int alpha){
        this.id=id;
        this.red=red;
        this.green=green;
        this.blue=blue;
        this.alpha=alpha;
    }

    ColorRGBA getColorRGBA(){
        ColorRGBA c = new ColorRGBA(0, 0, 0, 0);
        c.r = red / 255f;
        c.g = green / 255f;
        c.b = blue / 255f;
        c.a = alpha / 255f;
        return c;
    }
}
