/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.TerraNullius.physics;

import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;

/**
 * A Listener that automatically attaches itself to the physics space and listens and responds to collision events
 * @author Griffin
 */
public class TNPhysicsListener implements PhysicsCollisionListener {
    private static BulletAppState bulletAppState;
    public TNPhysicsListener(BulletAppState bulletAppState) {
        this.bulletAppState = bulletAppState;
        bulletAppState.getPhysicsSpace().addCollisionListener(this);
    }

    @Override
    public void collision(PhysicsCollisionEvent event) {
      if((event.getNodeA()!=null)&&(event.getNodeB()!=null)) {
          String a = event.getNodeA().getName();
          String b = event.getNodeB().getName();

          if (a.equals("brick"))  {
            System.out.println("Collision with a brick!!!");
          } else if (b.equals("brick")) {
            System.out.println("Collision with a brick!!!");
          }
      }
    }
}