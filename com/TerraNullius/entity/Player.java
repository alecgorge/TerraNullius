/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.TerraNullius.entity;

import com.TerraNullius.Game;
import com.TerraNullius.entity.Weapon.WeaponType;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.shape.Box;

/**
 *
 * @author Griffin
 */
public class Player extends Mob {    
    public Player(Game game){
        this.game = game;
        
        pos = new Vector3f(40,40,1);
        rot = new Quaternion();
        
        speed = 5f;
        health = 100;
        strength = 1.5;
        weap = WeaponType.PISTOL;
        
        Box b = new Box(new Vector3f(0,0,0), 0.5f, 0.5f, 1f);
        geom = new Geometry("Player", b);
        Material mat = new Material(game.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.White);
        geom.setMaterial(mat);
        geom.setLocalTranslation(pos);
        
        update();
    }
    
    @Override
    public void update() {
        //geom.setLocalTranslation(pos);

        pos = geom.getWorldTranslation();
        geom.setLocalRotation(rot);
        
        //checkCollisions(pos);
    }
    
    public void shoot(){
        //get rotation
        //ray trace to target
        //detect hit
        //draw line to target
        //notify target if its hit

        CollisionResults results = new CollisionResults();

        Vector2f mousePosNoOff = new Vector2f();
        mousePosNoOff.x = (game.cursorPos.x - game.settings.getWidth()/2)/(game.settings.getWidth()/2);
        mousePosNoOff.y = (game.cursorPos.y - game.settings.getHeight()/2)/(game.settings.getHeight()/2);
        float polarAngle = mousePosNoOff.getAngle() - FastMath.PI/4;
        float polarMag = FastMath.sqrt(FastMath.pow(mousePosNoOff.x, 2) + FastMath.pow(mousePosNoOff.y, 2));

        Vector2f mousePos = new Vector2f(polarMag * FastMath.cos(polarAngle), polarMag * FastMath.sin(polarAngle));
        Vector3f playerPos = this.getWorldPos();
        Vector3f rayCoords = new Vector3f(((mousePos.x * 300)), ((mousePos.y * 300)), 1f);

        Ray ray = new Ray(playerPos, rayCoords);

        //Temp line
        Mesh lineMesh = new Mesh();
        lineMesh.setMode(Mesh.Mode.Lines);
        lineMesh.setLineWidth(5f);
        lineMesh.setBuffer(VertexBuffer.Type.Position, 3, new float[]{
            playerPos.x,
            playerPos.y,
            playerPos.z,
            rayCoords.x + playerPos.x,
            rayCoords.y + playerPos.y,
            1f
        });
        lineMesh.setBuffer(VertexBuffer.Type.Index, 2, new short[]{ 0, 1 });
        lineMesh.updateBound();
        lineMesh.updateCounts();
        game.line.setMesh(lineMesh);

        game.mobs.collideWith(ray, results);

        if (results.size() > 0) {
            CollisionResult col = results.getCollision(0);
            Geometry tempGeom = col.getGeometry();
            if(col.getDistance() <= weap.range){
                System.out.println("  You shot " + tempGeom.getName() + " at " + col.getContactPoint() + ", " + col.getDistance() + " wu away.");
                for(Mob m : game.mobList){
                    if(m.getGeom() == tempGeom ){
                        m.hurt(this);
                        break;
                    }
                }
            }
        } 
    }
    
    @Override
    public void die(){
        game.getRootNode().detachChild(game.playerNode);
        game.gameOver();
    }
}
