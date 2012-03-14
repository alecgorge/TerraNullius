/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.TerraNullius.entity;

import com.jme3.collision.CollisionResults;
import com.jme3.math.Vector3f;

/**
 *
 * @author Griffin
 */
public class Collectable extends Entity{

    @Override
    public void update(){
        if(!isDead()){
            if(!pos.equals(geom.getLocalTranslation())){
                geom.setLocalTranslation(pos);
            }
            if(!rot.equals(geom.getLocalRotation())){
                geom.setLocalRotation(rot);
            }
            CollisionResults results = new CollisionResults();
            geom.collideWith(game.player.geom.getWorldBound(), results);
            if(results.size() > 0){
                game.player.give(this);
                die();
            }
        }
    }
    
    
}
