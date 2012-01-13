/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.entity;

import com.Game;
import com.jme3.app.Application;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

/**
 *
 * @author Griffin
 */

public class Entity {
    
    Game game;

    Geometry geom;
    Material mat;
    Vector3f pos;
    Quaternion rot;
    
    public void update() {
        geom.setLocalTranslation(pos);
        geom.setLocalRotation(rot);
    }
    
    public void setGeom(Geometry geom){this.geom = geom;}
    
    public Geometry getGeom(){return this.geom;}
    
    public void setMat(Material mat){this.mat = mat;}
    
    public Material getMat(){return this.mat;}

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

    
}
