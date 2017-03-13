package com.pesegato.goldmonkey;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.jme3.app.Application;
import java.util.ArrayList;
import java.util.Arrays;

import com.jme3.app.state.BaseAppState;
import model.builders.definitions.DefParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GoldMonkeyAppState extends BaseAppState {

    public static Logger log = LoggerFactory.getLogger(GoldMonkeyAppState.class);
    boolean continuousUpdate = false;
    private static final double UPDATE_DELAY = 1;
    private static DefParser parser;
    String[] files;
    public static boolean external;
    public static boolean externalJSON;
    static String basePath = "assets/GoldMonkey/";

    public GoldMonkeyAppState(boolean continuousUpdate, boolean external, String context, String... files) {
        this.continuousUpdate = continuousUpdate;
        this.external=external;
        this.externalJSON=external;
        GM.setContext(context);
        ArrayList<String> ffiles=new ArrayList<>();
        ffiles.addAll(Arrays.asList(files));
        this.files=ffiles.toArray(files);
        logBuildInfo();
    }

    public void setBasePath(String newPath) {
        basePath = newPath;
        externalJSON = true;
    }

    @Override
    protected void initialize(Application app) {
        parser = new DefParser(files);
        parser.readFile();
        setEnabled(continuousUpdate);
    }

    @Override
    protected void cleanup(Application app) {

    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }

    float currentCycle = 0;

    // Note that update is only called while the state is both attached and enabled.
    @Override
    public void update(float tpf) {
        currentCycle += tpf;
        if (currentCycle > UPDATE_DELAY) {
            currentCycle = 0;
            parser.readFile();
        }
    }

    protected void logBuildInfo() {
        try {
            java.net.URL u = Resources.getResource("GoldMonkey.build.date");
            String build = Resources.toString(u, Charsets.UTF_8);
            log.info("GoldMonkey build date: " + build);
        } catch( java.io.IOException e ) {
            log.error( "Error reading build info", e );
        }
    }
}
