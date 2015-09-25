package com.pesegato.goldmonkey;

import model.builders.Builder;
import model.builders.definitions.DefElement;
import model.builders.definitions.Definition;

public class DataBuilder extends Builder {

    private static final String BOOLEAN = "Boolean";
    private static final String INT = "Int";
    private static final String FLOAT = "Float";
    private static final String DOUBLE = "Double";
    private static final String STRING = "String";

    boolean bool;
    int integer;
    float floatp;
    double doublep;
    String string;

    public DataBuilder(Definition def) {
        super(def);
        for (DefElement de : def.getElements()) {
            switch (de.name) {
                case BOOLEAN:
                    bool = de.getBoolVal();
                    break;
                case INT:
                    integer = de.getIntVal();
                    break;
                case FLOAT:
                    floatp = (float) de.getDoubleVal();
                    break;
                case DOUBLE:
                    doublep = de.getDoubleVal();
                    break;
                case STRING:
                    string = de.getVal();
                    break;
            }
        }
    }

    public boolean buildBoolean() {
        return bool;
    }

    public int buildInt() {
        return integer;
    }

    public float buildFloat() {
        return floatp;
    }

    public double buildDouble() {
        return doublep;
    }

    public String buildString() {
        return string;
    }

    @Override
    public void readFinalizedLibrary() {
    }

}
