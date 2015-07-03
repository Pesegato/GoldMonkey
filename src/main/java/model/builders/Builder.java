/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.builders;

import tools.LogUtil;
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
		LogUtil.logger.warning("Element '"+elementName+"' unknown in definition '"+getId()+"'.");
	}

	public void printUnknownArgument(String elementName, String argumentName){
		LogUtil.logger.warning("Argument '"+argumentName+"' unknown for element '"+elementName+"' in definition '"+getId()+"'.");
	}

	public void printUnknownValue(String elementName, String value){
		LogUtil.logger.warning("value '"+value+"' unknown for element '"+elementName+"' in definition '"+getId()+"'.");
	}
}
