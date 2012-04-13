/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.TerraNullius.entity;

import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import java.util.ArrayList;

/**
 *
 * @author Griffin
 */
public class EntityIDMapper {
    ArrayList<String> IDs = new ArrayList();
    ArrayList<Spatial> spatials = new ArrayList();
    ArrayList<Entity> entities = new ArrayList();
    
    public EntityIDMapper(){
        
    }
    
    public void add(Spatial spatial, Entity e){
        String ID = "" + IDs.size() + 1 + "";
        IDs.add(ID);
        spatials.add(spatial);
        entities.add(e);
    }
    
    public void remove(Spatial spatial){
        int index = spatials.indexOf(spatial);
        if(index == -1) return;
        spatials.remove(index);
        IDs.remove(index);
        entities.remove(index);
    }
    
    public void remove(String ID){
        int index = IDs.indexOf(ID);
        if(index == -1) return;
        spatials.remove(index);
        IDs.remove(index); 
        entities.remove(index);        
    }
    
    public void setID(Spatial spatial, String ID){
        int index = spatials.indexOf(spatial); 
        if(index == -1) return;
        IDs.set(index, ID);
    }
    
    public void setSpatial(Spatial spatial, String ID){
        int index = IDs.indexOf(ID); 
        if(index == -1) return;
        spatials.set(index, spatial);
    }
    
    public String getID(Spatial spatial){
        int index = spatials.indexOf(spatial); 
        if(index == -1) return null;
        return IDs.get(index);
    }
    
    public Spatial getSpatial(String ID){
        int index = IDs.indexOf(ID);
        if(index == -1) return null;
        return spatials.get(index);
    }
    
    public Entity getEntity(Spatial spatial){
        int index = spatials.indexOf(spatial); 
        if(index == -1) return null;
        return entities.get(index);   
    }
    
}
