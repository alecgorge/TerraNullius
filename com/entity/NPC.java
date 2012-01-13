/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.entity;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

/**
 *
 * @author Griffin
 */
public class NPC extends Mob {
    public NPC(){
        x = 40;
        y = 40;
        z = 1;
        sX = 0.5f;
        sY = 0.5f;
        sZ = 1f;
        
        Box b = new Box(new Vector3f(0,0,0), sX, sY, sZ);
        geom = new Geometry("Player", b);
        mat.setColor("Color", ColorRGBA.White);
        geom.setMaterial(mat);
        geom.setLocalTranslation(new Vector3f(40,40,1));
    }
    
}
