package com.pesegato.goldmonkey;

import com.jme3.app.Application;
import java.util.ArrayList;
import java.util.Arrays;

import com.jme3.app.state.BaseAppState;
import model.builders.definitions.DefParser;

public class GoldMonkeyAppState extends BaseAppState {

    boolean continuousUpdate = false;
    private static final double UPDATE_DELAY = 1;
    private static DefParser parser;
    String[] files;
    public static boolean external;

    public GoldMonkeyAppState(boolean continuousUpdate, boolean external, String... files) {
        this.continuousUpdate = continuousUpdate;
        this.external=external;
        ArrayList<String> ffiles=new ArrayList<>();
        ffiles.addAll(Arrays.asList(files));
        this.files=ffiles.toArray(files);
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

}
