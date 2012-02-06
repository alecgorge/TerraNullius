/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.TerraNullius.entity;

import com.TerraNullius.Game;
import com.jme3.app.Application;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import java.util.ArrayList;

/**
 *
 * @author Griffin
 */

public class Entity {
    
    Game game;

    Geometry geom;
    Vector3f pos;
    Quaternion rot;
    
    int damage = 0;
    int health = 10;
    int strength = 1;
    
    public void update() {
        geom.setLocalTranslation(pos);
        geom.setLocalRotation(rot);
    }
    
    public void setGeom(Geometry geom){this.geom = geom;}
    
    public Geometry getGeom(){return this.geom;}
    
//    public void setTargetPos(Vector3f pos) {this.targetPos = pos;}
    
    public void move(Vector3f pos) {this.pos.add(pos);}
    
    public void move(float x, float y, float z) {this.pos.add(new Vector3f(x, y, z));}

    public void setPos(Vector3f pos) {this.pos = pos;}

    public void setX(float x) {this.pos.x = x;}

    public void setY(float y) {this.pos.y = y;}

    public void setZ(float z) {this.pos.z = z;}
    
    public Vector3f getPos() {return this.pos;}
    
    public Vector3f getWorldPos() {return this.geom.getWorldTranslation();}
    
    public float getX() {return this.pos.x;}

    public float getY() {return this.pos.y;}

    public float getZ() {return this.pos.z;}
    
    public void setRot(Quaternion rot) {this.rot = rot;}
    
    public void setRot(float yaw, float roll, float pitch) {
        this.rot.fromAngles(yaw, roll, pitch);
    }
    
    public Quaternion getRot() {return this.rot;}
    
    public ArrayList<Entity> checkCollisions(Vector3f targetPos){
        ArrayList<Entity> collidingWith = new ArrayList();
        for(Mob m : game.mobList){
            if((targetPos.x < m.pos.x + 0.2f && targetPos.x > m.pos.x - 0.2f) && (targetPos.y < m.pos.y + 0.2f && targetPos.y > m.pos.y - 0.2f)){
                collidingWith.add(m);
            }    
        }
        return collidingWith;
    }

    
}
