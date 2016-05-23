/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package model.builders.definitions;

import java.util.HashMap;
import java.util.Map;

import model.builders.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Benoît
 */
public class BuilderManager {

	static Logger log = LoggerFactory.getLogger( BuilderManager.class );

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
                    log.debug("Added {}"+def.getType());
                    builders.put(def.getType(), new HashMap<String, Builder>());
                    typed = builders.get(def.getType());
		}
                try {
                    typed.put(def.getId(), (Builder) Class.forName(def.getType()).getDeclaredConstructor(new Class[]{Definition.class}).newInstance(def));
                } catch (Exception ex) {
                    log.error(ex.getMessage());
                }

	}

	public static <T extends Builder> T getBuilder(String type, String id, Class<T> clazz) {
            log.debug("1 {}",type);
            log.debug("{}",builders.get(type));
            log.debug("2");
            log.debug("{}",builders.get(type).get(id));
            log.debug("3");
		Builder res = builders.get(type).get(id);
		if (res == null) {
			throw new IllegalArgumentException(ERROR + type + "/" + id);
		}
		return (T) res;
	}

}
