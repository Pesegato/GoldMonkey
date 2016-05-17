/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package model.builders.definitions;

import java.util.HashMap;
import java.util.Map;

import model.builders.Builder;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Benoît
 */
public class BuilderManager {
	private static final String ERROR = "Impossible to find ";

	private static Map<String, Map<String, Builder>> builders = new HashMap<>();

	private BuilderManager() {
	}


	public static void buildLinks() {
		for (Map<String, Builder> map : builders.values()) {
			for (Builder b : map.values()) {
				b.readFinalizedLibrary();
			}
		}
	}

	public static void submit(Definition def) {
		Map<String, Builder> typed = builders.get(def.getType());
		if (typed == null) {
                    System.out.println("Added *"+def.getType()+"*");
                    builders.put(def.getType(), new HashMap<String, Builder>());
                    typed = builders.get(def.getType());
		}
                try {
                    typed.put(def.getId(), (Builder) Class.forName(def.getType()).getDeclaredConstructor(new Class[]{Definition.class}).newInstance(def));
                } catch (Exception ex) {
                    Logger.getLogger(BuilderManager.class.getName()).log(Level.SEVERE, null, ex);
                }

	}

        static boolean veryVerbose=false;
	public static <T extends Builder> T getBuilder(String type, String id, Class<T> clazz) {
            if (veryVerbose){
            System.out.println("1 *"+type+"*");
            System.out.println(builders.get(type));
            System.out.println("2");
            System.out.println(builders.get(type).get(id));
            System.out.println("3");
            }
		Builder res = builders.get(type).get(id);
		if (res == null) {
			throw new IllegalArgumentException(ERROR + type + "/" + id);
		}
		return (T) res;
	}

}
