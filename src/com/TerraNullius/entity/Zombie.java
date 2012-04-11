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
        
        pos = new Vector3f(0, 2, 0);
        rot = new Quaternion();
        
        speed = 0.009f;
        turnSpeed = 0.9f;
        
//        Box b = new Box(Vector3f.ZERO, 0.5f, 1f, 0.5f);
//        spatial = new Geometry("Zombie", b);
//        mat = new Material(game.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
//        mat.setColor("Color", ColorRGBA.Black);
//        spatial.setMaterial(mat);
//        game.mobs.attachChild(spatial);
        
        spatial = game.getAssetManager().loadModel("Models/Human/meHumanMale.mesh.xml");
        spatial.setName("Zombie");
        mat = new Material(game.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Black);
        spatial.setMaterial(mat);
        spatial.scale(1.5f, 1.5f, 1.5f);
        game.mobs.attachChild(spatial);
        
        physChar = new CharacterControl(new CapsuleCollisionShape(.2f, 0.75f, 1), 0.1f);
        physChar.setJumpSpeed(20);
        physChar.setFallSpeed(20);
        physChar.setGravity(30);
        physChar.setUseViewDirection(true);
        spatial.addControl(physChar);
        game.bulletAppState.getPhysicsSpace().add(physChar);
        
        spatial.lookAt(game.player.getWorldPos(), Vector3f.UNIT_Y);

        update();
    }
    
    @Override
    public void update(float tpf){
        if(!isDead()){
            Vector3f target = game.player.getPos();

            pos = physChar.getPhysicsLocation();


            if((System.currentTimeMillis() - hurtTimer > weap.fireRate * 1000) && pos.distance(game.player.pos) <= weap.range){
                shoot(game.player);
                //e.hurt(this);
                hurtTimer = System.currentTimeMillis();
            }
            
//            Quaternion old = new Quaternion(spatial.getLocalRotation());
//            spatial.lookAt(target, Vector3f.UNIT_Y);
//            spatial.getLocalRotation().slerp(old, turnSpeed); // the higher the value, the slower rotation
            
            Vector3f tempView = target.subtract(pos).normalize();
            physChar.setViewDirection(new Vector3f(tempView.getX(), 0f, tempView.getZ()));

            physChar.setWalkDirection(target.subtract(pos).normalize().mult(speed));

            rot = spatial.getLocalRotation();
        }
    }
}
