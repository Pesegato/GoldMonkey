package com.pesegato.goldmonkey;

import com.jme3.app.Application;

import java.util.ArrayList;
import java.util.Arrays;

import com.jme3.app.state.BaseAppState;
import io.github.pesegato.goldmonkey.GoldMonkeyBuildInfo;
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
        GoldMonkeyAppState.external = external;
        externalJSON = external;
        GM.setContext(context);
        ArrayList<String> ffiles = new ArrayList<>(Arrays.asList(files));
        this.files = ffiles.toArray(files);
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
        log.info("GoldMonkey build date: " + GoldMonkeyBuildInfo.BUILD_DATE);
    }
}
