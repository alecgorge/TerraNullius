/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.TerraNullius.entity;

import com.TerraNullius.Game;
import com.TerraNullius.entity.Weapon.WeaponType;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
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
        
        health = 50;
        weap = WeaponType.HANDS;
        
        pos = new Vector3f(0,0,1);
        rot = new Quaternion();
        
        speed = 0.9f;
        turnSpeed = 0.9f;
        
        Box b = new Box(new Vector3f(0,0,0), 0.5f, 1f, 0.5f);
        geom = new Geometry("Zombie", b);
        Material mat = new Material(game.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Black);
        geom.setMaterial(mat);
        game.mobs.attachChild(geom);
        
        physChar = new CharacterControl(new CapsuleCollisionShape(1.5f, 6f, 1), 0.01f);
        geom.addControl(physChar);
        game.bulletAppState.getPhysicsSpace().add(physChar);
        
        geom.lookAt(game.player.getWorldPos(), Vector3f.UNIT_Z);
        
        update();
    }
    
    @Override
    public void update(float tpf){
        if(!isDead()){
            Vector3f target = game.player.getWorldPos();

            pos = pos.add((target.subtract(pos)).normalize());

            if((System.currentTimeMillis() - hurtTimer > weap.fireRate * 1000) && pos.distance(game.player.pos) <= weap.range){
                shoot(game.player);
                //e.hurt(this);
                hurtTimer = System.currentTimeMillis();
            }
            Quaternion old = new Quaternion(geom.getLocalRotation());
            geom.lookAt(target, Vector3f.UNIT_Z);
            geom.getLocalRotation().slerp(old, turnSpeed); // the higher the value, the slower rotation

            //geom.setLocalTranslation(pos);
            physChar.setWalkDirection(pos);
            rot = geom.getLocalRotation();

            //mat.setColor("Color", ColorRGBA.Black);
        }
    }
}
