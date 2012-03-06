/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.TerraNullius.entity;

import com.TerraNullius.Game;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

/**
 *
 * @author Griffin
 */
public class Bullet extends Entity {
    RigidBodyControl rbControl;
    public Bullet(Game game, Vector3f origin, Vector3f direction){
        Box b = new Box(0.1f, 0.1f, 0.1f);
        geom = new Geometry("Bullet", b);
        rbControl = new RigidBodyControl(new BoxCollisionShape(), 0.1f);
        geom.addControl(rbControl);
        game.bulletAppState.getPhysicsSpace().add(geom);
        
        rbControl.setPhysicsLocation(origin);
        rbControl.setLinearVelocity(direction.mult(10));//arbitrary speed value
    }
    
}
