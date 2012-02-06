/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.TerraNullius.entity;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

/**
 *
 * @author Griffin
 */
public class Mob extends Entity{
    
    float speed = 1f;
    float turnSpeed = 1f; //Higher is slower (turn interpolation time)
    
    public void update(float tpf){}
    
    public float getSpeed(){return speed;}
    
    public float getTurnSpeed(){return turnSpeed;}
    
    public void hurt(Entity e){
        damage += strength;
        move(pos.subtract(e.pos).normalize().mult(0.1f));
        geom.getMaterial().setColor("Color", ColorRGBA.Red);
        
        if(damage >= health) die();
    }
    
    public void die(){
        game.mobs.detachChild(geom);
        game.mobList.remove(this);
    }


}
