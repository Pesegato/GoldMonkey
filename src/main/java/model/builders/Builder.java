/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.builders;

import java.util.logging.Level;
import java.util.logging.Logger;
import model.builders.definitions.Definition;

/**
 *
 * @author Benoît
 */
public abstract class Builder {
	protected Definition def;

	public Builder(Definition def) {
		this.def = def;
	}

	public abstract void readFinalizedLibrary();

	public String getId(){
		return def.getId();
	}

	public void printUnknownElement(String elementName){
            Logger.getLogger(Builder.class.getName()).log(Level.SEVERE, "Element ''{0}'' unknown in definition ''{1}''.", new Object[]{elementName, getId()});
	}

	public void printUnknownArgument(String elementName, String argumentName){
            Logger.getLogger(Builder.class.getName()).log(Level.SEVERE, "Argument ''{0}'' unknown for element ''{1}'' in definition ''{2}''.", new Object[]{argumentName, elementName, getId()});
	}

	public void printUnknownValue(String elementName, String value){
            Logger.getLogger(Builder.class.getName()).log(Level.SEVERE, "value ''{0}'' unknown for element ''{1}'' in definition ''{2}''.", new Object[]{value, elementName, getId()});
	}
}
