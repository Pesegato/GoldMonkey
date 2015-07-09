/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package model.builders.definitions;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 * @author Benoît
 */
public class DefParser {
	private static final String ID = "id";
	Map<File, Long> filesAndTimers = new HashMap<>();
	String[] filesToRead;

	public DefParser(String... files) {
            filesToRead=files;
		readFile();
	}

	private ArrayList<File> getFiles(String folderPath) {
		ArrayList<File> res = new ArrayList<>();
		File folder = new File(folderPath);
		if (!folder.exists()) {
			throw new Error("the folder " + folderPath +  " was not found.");
		}
		for (File f : folder.listFiles()) {
			res.add(f);
		}
		return res;
	}

	public void addFile(File f) {
		filesAndTimers.put(f, 0l);
	}

	public void readFile() {
		String log = "updated : ";
                boolean notempty=false;
		for (String fileName : filesToRead) {
			try {
                            notempty=true;
				log = log.concat(fileName+", ");
				XMLInputFactory inputFactory = XMLInputFactory.newInstance();
				InputStream in = this.getClass().getResourceAsStream(fileName);
				XMLEventReader eventReader = inputFactory.createXMLEventReader(in);

				Definition def = null;
				// read the XML document
				while (eventReader.hasNext()) {
					XMLEvent event = eventReader.nextEvent();
					if (event.isStartElement()) {
						def = parseEvent(event, def);
					} else if (event.isEndElement()) {
						String elementName = event.asEndElement().getName().getLocalPart();
						if (def != null && elementName.equals(def.getType())) {
							BuilderManager.submit(def);
							def = null;
						}
						// else
						// throw new
						// RuntimeException("("+fileName+") At line "+event.getLocation().getLineNumber()+", find a closing element that is not closing a definition"+elementName);
					}
				}
			} catch (XMLStreamException e) {
				e.printStackTrace();
			}
		}
		if (notempty) {
			BuilderManager.buildLinks();
		}

	}

	private Definition parseEvent(XMLEvent event, Definition def) {
		StartElement se = event.asStartElement();
		String elementName = se.getName().getLocalPart();
		if (elementName.equals("catalog")) {
			return null;
		}

		Iterator<Attribute> attributes = se.getAttributes();

		if (def == null) {
			Attribute id = attributes.next();
			if (id.getName().toString() != ID) {
				throw new RuntimeException("At line " + event.getLocation().getLineNumber() + ", problem with definition '" + elementName
						+ "'. The first attribute of a definition must be called '" + ID + "'.");
			}
			def = new Definition(elementName, id.getValue());
			// LogUtil.logger.info("def cree "+def.type+" - "+def.id);
		} else {
			DefElement de = new DefElement(elementName);
			while (attributes.hasNext()) {
				Attribute a = attributes.next();
				de.addVal(a.getName().toString(), a.getValue());
			}
			def.getElements().add(de);
			// LogUtil.logger.info("    element ajouté : "+de.name+" - "+de.getVal());
		}
		return def;
	}
}
