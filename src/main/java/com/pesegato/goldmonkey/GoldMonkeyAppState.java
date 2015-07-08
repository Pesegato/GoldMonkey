package com.pesegato.goldmonkey;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import model.builders.definitions.DefParser;

public class GoldMonkeyAppState extends AbstractAppState { //3.0 style :(

    boolean continuousUpdate = false;
    private static final double UPDATE_DELAY = 1;
    public static final String CONFIG_PATH = "assets/data";
    private static DefParser parser;

    public GoldMonkeyAppState(boolean continuousUpdate) {
        this.continuousUpdate = continuousUpdate;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        parser = new DefParser(CONFIG_PATH);
        parser.readFile();
        setEnabled(continuousUpdate);
    }

    @Override
    public void cleanup() {
        super.cleanup();
    }

    @Override
    public void setEnabled(boolean enabled) {
        // Pause and unpause
        super.setEnabled(enabled);
        if (enabled) {
            // init stuff that is in use while this state is RUNNING
        } else {
            // take away everything not needed while this state is PAUSED
        }
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
