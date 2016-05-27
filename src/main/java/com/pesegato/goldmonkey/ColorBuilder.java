package com.pesegato.goldmonkey;

import com.jme3.math.ColorRGBA;
import java.awt.Color;
import model.builders.Builder;
import model.builders.definitions.DefElement;
import model.builders.definitions.Definition;

public class ColorBuilder extends Builder {

    private static final String RED = "Red";
    private static final String GREEN = "Green";
    private static final String BLUE = "Blue";
    private static final String ALPHA = "Alpha";

    float red;
    float green;
    float blue;
    float alpha;

    public ColorBuilder(Definition def) {
        super(def);
        for (DefElement de : def.getElements()) {
            switch (de.name) {
                case RED:
                    red = (float) de.getDoubleVal();
                    break;
                case GREEN:
                    green = (float) de.getDoubleVal();
                    break;
                case BLUE:
                    blue = (float) de.getDoubleVal();
                    break;
                case ALPHA:
                    alpha = (float) de.getDoubleVal();
                    break;
            }
        }
    }

    public Color buildAWTColor() {
        return new Color(red, green, blue, alpha);
    }

    public ColorRGBA buildColorRGBA() {
        ColorRGBA c = new ColorRGBA(0, 0, 0, 0);
        c.r = red / 255;
        c.g = green / 255;
        c.b = blue / 255;
        c.a = alpha / 255;
        return c;
    }

    @Override
    public void readFinalizedLibrary() {
    }

}
