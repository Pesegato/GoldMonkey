
package com.pesegato.goldmonkey;

import com.google.gson.Gson;
import com.jme3.math.ColorRGBA;
import model.builders.definitions.BuilderManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;

import static com.pesegato.goldmonkey.GoldMonkeyAppState.basePath;

public class GM {

    public static Logger log = LoggerFactory.getLogger(GM.class);
    private static HashMap<String, ColorRGBA> colorRGBAs;
    private static HashMap<String, String> strings;
    private static HashMap<String, Number> values;
    private static String context="*";

    public static void setContext(String context){
        GM.context=context;
        values=null;
    }

    public static String getContext(){
        return context;
    }

    public static Reader getJSON(String name) throws FileNotFoundException {
        if (GoldMonkeyAppState.externalJSON) {
            return new FileReader(basePath + name + ".json");
        } else {
            return new InputStreamReader(GM.class.getResourceAsStream("/GoldMonkey/" + name + ".json"));
        }
    }

    public static ColorRGBA getColor(String id) {
        if (colorRGBAs == null) {
            try {
                colorRGBAs = new HashMap<>();
                GoldColorRGBA[] data = new Gson().fromJson(getJSON("ColorRGBA"), GoldColorRGBA[].class);
                for (GoldColorRGBA c : data) {
                    colorRGBAs.put(c.id, c.getColorRGBA());
                }
            } catch (FileNotFoundException ex) {
                log.error(null, ex);
            }
        }
        return colorRGBAs.get(id);
    }

    public static String getStringG(String id) {
        if (strings == null) {
            try {
                strings = new HashMap<>();
                GoldString[] data = new Gson().fromJson(getJSON("String"), GoldString[].class);
                for (GoldString c : data) {
                    strings.put(c.id, c.string);
                }
            } catch (FileNotFoundException ex) {
                log.error(null, ex);
            }
        }
        return strings.get(id);
    }

    public static ColorRGBA getColorRGBA(String id) {
        return ((ColorBuilder) BuilderManager.getBuilder("com.pesegato.goldmonkey.ColorBuilder", id, ColorBuilder.class)).buildColorRGBA();
    }

    public static boolean existsData(String id) {
        if (values == null) {
            try {
                values = new HashMap<>();
                GoldNumber[] data = new Gson().fromJson(getJSON("Data"), GoldNumber[].class);
                for (GoldNumber c : data) {
                    values.put(c.id, c.value);
                }
            } catch (FileNotFoundException ex) {
                log.error(null, ex);
            }
        }
        return values.get(id)!=null;
    }

    public static Number getN(String key) {
        Number val = values.get(key);
        if (val == null) {
            if (GM.existsData(key)) {
                System.out.println("GM: Loading value * "+ key);
                val = values.get(key);
            } else if (GM.existsData(key + " *")) {
                System.out.println("GM: Loading value * "+ key);
                val = values.get(key + " *");
            } else {
                System.out.println("GM: Loading value "+key+" "+ context);
                val = values.get(key + " " + context);
                System.out.println(val+" val");
            }
            values.put(key, val);
        }
        return val;
    }

    public static int getI(String key){
        return getN(key).intValue();
    }

    public static float getF(String key){
        return getN(key).floatValue();
    }

    public static boolean existsDataXML(String id) {
        try {
            DataBuilder d = ((DataBuilder) BuilderManager.getBuilder("com.pesegato.goldmonkey.DataBuilder", id, DataBuilder.class));
            return true;
        } catch (java.lang.IllegalArgumentException e) {
            return false;
        }
    }

    public static float getFloat(String id) {
        if (values == null) {
            try {
                values = new HashMap<>();
                GoldNumber[] data = new Gson().fromJson(getJSON("Data"), GoldNumber[].class);
                System.out.println("data "+data.length);
                for (GoldNumber c : data) {
                    values.put(c.id, c.value);
                    System.out.println("ID "+c.id+" VALUE "+c.value);
                }
            } catch (FileNotFoundException ex) {
                log.error(null, ex);
            }
        }
        return values.get(id).floatValue();
    }
    public static boolean getBool(String id) {
        return ((DataBuilder) BuilderManager.getBuilder("com.pesegato.goldmonkey.DataBuilder", id, DataBuilder.class)).buildBoolean();
    }

    public static int getInt(String id) {
        if (values == null) {
            try {
                values = new HashMap<>();
                GoldNumber[] data = new Gson().fromJson(getJSON("Data"), GoldNumber[].class);
                System.out.println("data "+data.length);
                for (GoldNumber c : data) {
                    values.put(c.id, c.value);
                }
            } catch (FileNotFoundException ex) {
                log.error(null, ex);
            }
        }
        return values.get(id).intValue();
    }

    public static int getIntXML(String id) {
        return ((DataBuilder) BuilderManager.getBuilder("com.pesegato.goldmonkey.DataBuilder", id, DataBuilder.class)).buildInt();
    }

    public static float getFloatXML(String id) {
        return ((DataBuilder) BuilderManager.getBuilder("com.pesegato.goldmonkey.DataBuilder", id, DataBuilder.class)).buildFloat();
    }

    public static double getDouble(String id) {
        return ((DataBuilder) BuilderManager.getBuilder("com.pesegato.goldmonkey.DataBuilder", id, DataBuilder.class)).buildDouble();
    }

    public static String getString(String id) {
        return ((DataBuilder) BuilderManager.getBuilder("com.pesegato.goldmonkey.DataBuilder", id, DataBuilder.class)).buildString();
    }
}
