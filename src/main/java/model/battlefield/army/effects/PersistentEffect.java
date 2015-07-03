package model.battlefield.army.effects;

import geometry.math.MyRandom;

import java.util.ArrayList;

import model.builders.EffectBuilder;

/**
 * This effect is made to launch its children with temporality.
 * 
 * It can delay the launch, or launch many times the same child effect.
 * 
 */
public class PersistentEffect extends Effect {
    protected final int periodCount;
    protected final ArrayList<Double> durations;
    protected final ArrayList<Double> ranges;
    
    private boolean launched = false;
    public boolean terminated = false;
    
    private int count = 0;
    private int periodIndex = 0;
    private int effectIndex = 0;
    
    private long lastPeriod;
    private double currentPeriodDuration;

    public PersistentEffect(int periodCount,
            ArrayList<Double> durations,
            ArrayList<Double> ranges,
            ArrayList<EffectBuilder> effectBuilders,
            EffectSource source,
            EffectTarget target) {
        super(effectBuilders, source, target);
        this.periodCount = periodCount;
        this.durations = durations;
        this.ranges = ranges;
    }

    @Override
    public void launch(){
        launched = true;
        setNextPeriodDuration();
        lastPeriod = System.currentTimeMillis();
    }
    
    public void update(){
        if(!launched)
            return;
        if(!source.isStillActiveSource())
            terminated = true;
        if(!terminated && lastPeriod+currentPeriodDuration < System.currentTimeMillis()){
            childEffectBuilders.get(effectIndex).build(source, target, null).launch();
            
            if(++effectIndex >= childEffectBuilders.size())
                effectIndex = 0;
            if(++periodIndex >= durations.size())
                periodIndex = 0;
            setNextPeriodDuration();
            lastPeriod = System.currentTimeMillis();

            if(++count >= periodCount)
                terminated = true;
        }
    }
    
    private void setNextPeriodDuration(){
        currentPeriodDuration = durations.get(periodIndex)+ MyRandom.between(0, ranges.get(periodIndex));
    }
    
}
