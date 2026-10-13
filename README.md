<div align="center"><img alt="Static Badge" src="https://img.shields.io/badge/🐵Gold-Monkey-yellow?style=for-the-badge" height="100" align="center">

# XML and JSON Builder for dataset

[![Maven Central](https://img.shields.io/maven-central/v/io.github.pesegato/GoldMonkey?style=for-the-badge)](https://central.sonatype.com/artifact/io.github.pesegato/GoldMonkey)
[![GitHub Packages](https://img.shields.io/github/v/tag/Pesegato/GoldMonkey?label=GitHub%20Packages&style=for-the-badge)](https://github.com/Pesegato/GoldMonkey/packages/3277776)
[![javadoc](https://javadoc.io/badge2/io.github.pesegato/GoldMonkey/javadoc.svg?style=for-the-badge)](https://javadoc.io/doc/io.github.pesegato/GoldMonkey)

</div>

<div align="center"><img alt="GoldMonkey Logo" src="goldmonkey-logo.png" align="center">
</div>

GoldMonkey is a lightweight Java library designed for [jMonkeyEngine](https://jmonkeyengine.org/) (jme3) applications that lets you externalize game and dataset values (colors, strings, booleans, and numeric parameters) into

**JSON** or **XML** files, complete with support for contextual overrides (such as localized variants, game states, or
level-specific tags).

---

## Features

- **JSON & XML Support**: Load definitions from either format seamlessly.
- **Context System**: Define default values with wildcards (`*`) and provide context-specific overrides (e.g. `key context_id`).
- **Classpath or External Storage**: Load bundled assets inside JARs or external files in `assets/GoldMonkey/` (customizable path).
- **jMonkeyEngine Integration**: Plug-and-play GoldMonkeyAppState.java (`BaseAppState`) with optional live polling (`continuousUpdate`).
- **Type-safe Retrieval**: Simple accessors for `ColorRGBA`, `AWT Color`, `float`, `int`, `double`, `boolean`, and `String`.

---

## Installation

### Gradle

```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation 'io.github.pesegato:goldmonkey:0.7.7'
}
```

### Maven

<dependency>
    <groupId>io.github.pesegato</groupId>
    <artifactId>goldmonkey</artifactId>
    <version>0.7.7</version>
</dependency>

──────

## Quick Start

### 1. Register AppState in jMonkeyEngine

Register GoldMonkeyAppState.java in your application's state manager:

    // Parameters:
    // continuousUpdate: boolean (re-read definitions periodically)
    // external: boolean (true to load from file system, false to load from classpath/JAR resources)
    // context: String (initial context string, e.g. "DEFAULT", "EN", or "*")
    // files: String... (XML definition files to parse, optional if using JSON only)
    
    GoldMonkeyAppState gmState = new GoldMonkeyAppState(false, false, "*", "definitions.xml");
    stateManager.attach(gmState);

Per impostare un percorso personalizzato per i file JSON esterni:

    gmState.setBasePath("assets/GoldMonkey/");

──────

## JSON Datasets

I file JSON possono trovarsi nel classpath (/GoldMonkey/) o nel filesystem (assets/GoldMonkey/).

### ColorRGBA.json

    [
      { "id": "Red",   "r": 1.0, "g": 0.0, "b": 0.0, "a": 1.0 },
      { "id": "Green", "r": 0.0, "g": 1.0, "b": 0.0, "a": 1.0 },
      { "id": "Blue",  "r": 0.0, "g": 0.0, "b": 1.0, "a": 1.0 }
    ]

Lettura tramite GM.java:
ColorRGBA color = GM.getColor ("Red");

### String.json

    [
      { "id": "WelcomeMessage", "string": "Hello, Monkey World!" }
    ]

Lettura tramite GM.java:

    String msg = GM.getStringG("WelcomeMessage");

### Data.json (con Context)

Supporta fallback con wildcard * e contesti specifici:

    [
      { "id": "PlayerSpeed *",    "value": 10.0 },
      { "id": "PlayerSpeed HARD", "value": 15.0 },
      { "id": "InitialLives",     "value": 3 }
    ]

Lettura tramite GM.java:

    GM.setContext("HARD");
    float speed = GM.getF("PlayerSpeed"); // Restituisce 15.0f

    int lives = GM.getI("InitialLives");   // Restituisce 3

──────

## XML Definitions

I file XML utilizzano l'elemento radice <catalog> e i builder registrati:

    <catalog>
        <com.pesegato.goldmonkey.ColorBuilder id="CustomCyan">
            <Red val="0"/>
            <Green val="255"/>
            <Blue val="255"/>
            <Alpha val="255"/>
        </com.pesegato.goldmonkey.ColorBuilder>

        <com.pesegato.goldmonkey.DataBuilder id="MaxHealth">
            <Int val="100"/>
        </com.pesegato.goldmonkey.DataBuilder>
    </catalog>

Lettura:

    ColorRGBA cyan = GM.getColorRGBA("CustomCyan");
    int maxHp = GM.getIntXML("MaxHealth");

──────

## API Facade (GM.java)

 Metodo                        | Origine                 | Tipo Ritorno | Descrizione                                                     
  -------------------------------|-------------------------|--------------|-----------------------------------------------------------------
 GM.setContext(String context) | Any                     | void         | Imposta il contesto corrente e resetta la cache                 
 GM.getColor(String id)        | ColorRGBA.json          | ColorRGBA    | Carica il colore da JSON                                        
 GM.getStringG(String id)      | String.json             | String       | Carica la stringa da JSON                                       
 GM.getN(String id)            | Data.json               | Number       | Recupera il valore numerico con supporto a context e fallback * 
 GM.getI(String id)            | Data.json               | int          | Helper intero per getN                                          
 GM.getF(String id)            | Data.json               | float        | Helper float per getN                                           
 GM.getColorRGBA(String id)    | XML (ColorBuilder.java) | ColorRGBA    | Costruisce ColorRGBA da XML                                     
 GM.getIntXML(String id)       | XML (DataBuilder.java)  | int          | Estrae int da XML                                               
 GM.getFloatXML(String id)     | XML (DataBuilder.java)  | float        | Estrae float da XML                                             
 GM.getBool(String id)         | XML (DataBuilder.java)  | boolean      | Estrae boolean da XML                                           
 GM.getString(String id)       | XML (DataBuilder.java)  | String       | Estrae String da XML                                            

──────

## License

This project is licensed under the BSD 3-Clause License.
