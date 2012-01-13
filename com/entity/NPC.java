/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.entity;

import com.Game;
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
public class NPC extends Mob {
    public NPC(Game game){
        this.game = game;
        
        pos = new Vector3f(40,40,1);
        rot = new Quaternion();
        
        Box b = new Box(new Vector3f(0,0,0), 0.5f, 0.5f, 1f);
        geom = new Geometry("NPC", b);
        mat = new Material(game.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Green);
        geom.setMaterial(mat);
        
        update();
    } 
}
