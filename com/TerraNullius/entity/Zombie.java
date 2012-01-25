/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.TerraNullius.entity;

import com.TerraNullius.Game;
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
public class Zombie extends Mob {
    public Zombie(Game game){
        this.game = game;
        
        pos = new Vector3f(40,40,1);
        rot = new Quaternion();
        
        speed = 0.9f;
        turnSpeed = 0.9f;
        
        Box b = new Box(new Vector3f(0,0,0), 0.5f, 1f, 0.5f);
        geom = new Geometry("Zombie", b);
        mat = new Material(game.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Black);
        geom.setMaterial(mat);
        
        geom.lookAt(new Vector3f(0,0,1f), Vector3f.UNIT_Z);
        
        update();
    }
    
    @Override
    public void update(float tpf){
        Vector3f target = game.player.getWorldPos();
        
        //move from zombie pos to player pos by 1 speed per frame
        //use geom.move(Vector3f offset) ?
        pos = pos.add((target.subtract(pos)).normalize().mult(speed*tpf));
        
        Quaternion old = new Quaternion(geom.getLocalRotation());
        geom.lookAt(target, Vector3f.UNIT_Z);
        geom.getLocalRotation().slerp(old, turnSpeed); // the higher the value, the slower missilesrotation

        geom.setLocalTranslation(pos);
        rot = geom.getLocalRotation();
    }
}
