/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.TerraNullius.entity;

import com.TerraNullius.Game;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;
import com.jme3.scene.shape.Sphere.TextureMode;

/**
 *
 * @author Griffin
 */
public class Bullet extends Entity {
    public Bullet(Game game, Vector3f origin, Vector3f direction){

        Sphere bullet = new Sphere(32, 32, 0.1f, true, false);
        bullet.setTextureMode(TextureMode.Projected);
        Geometry bulletg = new Geometry("bullet", bullet);
        Material mat = new Material(game.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.White);
        bulletg.setMaterial(mat);
        bulletg.setShadowMode(ShadowMode.CastAndReceive);
        bulletg.setLocalTranslation(origin);

        SphereCollisionShape bulletCollisionShape = new SphereCollisionShape(0.1f);
        RigidBodyControl bulletNode = new RigidBodyControl(bulletCollisionShape, 0.01f);
        bulletNode.setLinearVelocity(direction.normalize().mult(200));
        bulletg.addControl(bulletNode);
        game.getRootNode().attachChild(bulletg);
        game.bulletAppState.getPhysicsSpace().add(bulletNode);
    }
    
}
