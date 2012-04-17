/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.TerraNullius.entity;

import com.TerraNullius.Game;
import com.jme3.collision.CollisionResults;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import java.util.ArrayList;

/**
 * The general entity type.
 * 
 * @author Griffin
 */

public class Entity {
    
    Game game;
   
    Spatial spatial;
    Material mat;
    Vector3f pos = Vector3f.ZERO;
    Quaternion rot = new Quaternion();
    
    boolean dead = false;
    
    int damage = 0;
    int health = 100;
    double strength = 1;    //Multiplier for damage
    long hurtTimer = 0;
    
    public void update() {
        spatial.setLocalTranslation(pos);
        spatial.setLocalRotation(rot);
    }
    
    public int getCurrentHealth(){
        return health - damage;
    }
    
    public void setSpatial(Geometry geom){this.spatial = geom;}
    
    public Spatial getSpatial(){return this.spatial;}
    
//    public void setTargetPos(Vector3f pos) {this.targetPos = pos;}
    
    public void move(Vector3f pos) {this.pos.add(pos);}
    
    public void move(float x, float y, float z) {this.pos.add(new Vector3f(x, y, z));}

    public void setPos(Vector3f pos) {this.pos = pos;}
    
    public Vector3f getPos() {return this.pos;}
    
    public Vector3f getWorldPos() {return this.spatial.getWorldTranslation();}
    
    public void setRot(Quaternion rot) {this.rot = rot;}
    
    public void setRot(float yaw, float roll, float pitch) {
        this.rot.fromAngles(yaw, roll, pitch);
    }
    
    public void setOutlineGlow(boolean value, ColorRGBA color){
        this.mat.setColor("GlowColor", color);
    }
    
    public Quaternion getRot() {return this.rot;}
    
    public ArrayList<Entity> checkCollisions(Vector3f targetPos){
        ArrayList<Entity> collidingWith = new ArrayList();
        for(Mob m : game.mobList){
            CollisionResults results = new CollisionResults();
            this.spatial.collideWith(m.spatial.getWorldBound(), results);
            if(results.size() > 0){
                m.push(this);
                collidingWith.add(m);
            }
        }
        return collidingWith;
    }
    
    public void hurt(Entity e){
        damage += e.strength;
        push(e);
        mat.setColor("Color", ColorRGBA.Red);
        
        if(damage >= health) die();
    }
    
    public void hurt(Mob m){
        damage += (m.strength * m.weap.fireDamage);
        push(m);
        mat.setColor("Color", ColorRGBA.Red);
        
        if(damage >= health) die();
    }
    
    public void push(Entity e){
        move(pos.subtract(e.pos).normalize().mult(0.1f));    
    }

    public void die(){
        game.getRootNode().detachChild(spatial);
        dead = true;
    }
    
    public boolean isDead(){
        return dead;
    }

    
}
