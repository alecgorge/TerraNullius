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
import com.jme3.scene.shape.Box;

/**
 *
 * @author Griffin
 */
public class Weapon extends Collectable {

    public enum WeaponType{
        HANDS(0.5, 10, 2, 0),
        MACHINEGUN(0.05, 8, 50, 1),
        PISTOL(0.33, 20, 50, 2),
        RIFLE(1, 50, 100, 3);
        
        public double fireRate;
        public int fireDamage;
        public int range;
        public int modelNum;
        
        private WeaponType(double fireRate, int fireDamage, int range, int modelNum){
            this.fireRate = fireRate;
            this.fireDamage = fireDamage;
            this.range = range;
            this.modelNum = modelNum;
        }
        
    }
    
    WeaponType weapType;
    
    public Weapon(WeaponType weapType, Game game){
        this.weapType = weapType;
        this.game = game;
        
        health = 100;
        strength = 0;
        pos = new Vector3f(0,10,0);
        
        //TODO: Add model based on modelNum
        Box b = new Box(Vector3f.ZERO, 0.5f, 0.25f, 0.5f);
        geom = new Geometry("Weapon", b);
        Material mat = new Material(game.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Pink);
        geom.setMaterial(mat);
        game.getRootNode().attachChild(geom);
        
        //TODO: Add corresponding animation to modelNum
        

        update();
    }

}
