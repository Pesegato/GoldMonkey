package model.battlefield.actors;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a scene for all actors. Actors come and leave that scene whenever they want.
 * 
 * The view draw all actors present in the pool at each frame, and erase those that leave.
 * 
 * @author Benoît
 */
public class ActorPool {
    
    public List<Actor> activeActors = new ArrayList<>();
    public List<Actor> deletedActors = new ArrayList<>();
    
    public void registerActor(Actor actor){
        activeActors.add(actor);
    }
    
    public void deleteActor(Actor actor){
        activeActors.remove(actor);
        deletedActors.add(actor);
    }
    public ArrayList<Actor> grabDeletedActors(){
        ArrayList<Actor> res = new ArrayList<>(deletedActors);
        deletedActors.clear();
        return res;
    }
    public ArrayList<Actor> getActors(){
        ArrayList<Actor> res = new ArrayList<>(activeActors);
        return res;
    }
}
