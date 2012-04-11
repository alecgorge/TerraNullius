/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.TerraNullius.entity;

import com.TerraNullius.Game;
import com.TerraNullius.entity.Weapon.WeaponType;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 *
 * @author Griffin
 */
public class Player extends Mob {
    Vector3f walkDirection = new Vector3f(0,0,0);
    Vector3f viewDirection = new Vector3f(0,0,0);
    public Player(Game game) {
        this.game = game;

        pos = new Vector3f(0, 0, 0);
        rot = new Quaternion();

        speed = 0.1f;
        health = 100;
        strength = 1.5;
        weap = WeaponType.PISTOL;

//        Box b = new Box(Vector3f.ZERO, 0.5f, 1f, 0.5f);
//        spatial = new Geometry("Player", b);

        spatial = game.getAssetManager().loadModel("Models/Human/meHumanMale.mesh.xml");
        mat = new Material(game.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.White);
        spatial.setName("Player");
        spatial.setMaterial(mat);
        spatial.scale(1.5f, 1.5f, 1.5f);
        spatial.setLocalTranslation(pos);

        physChar = new CharacterControl(new CapsuleCollisionShape(.2f, 0.75f, 1), 0.1f);
        physChar.setJumpSpeed(20);
        physChar.setFallSpeed(20);
        physChar.setGravity(30);
        physChar.setUseViewDirection(true);
        spatial.addControl(physChar);
        game.bulletAppState.getPhysicsSpace().add(physChar);

        update();
    }

    @Override
    public void update() {
        if (!isDead()) {
            physChar.setWalkDirection(walkDirection);
            physChar.setViewDirection(viewDirection);
            
            //checkCollisions(pos);
            
            pos = physChar.getPhysicsLocation();
        }
    }
    
    public void jump(){
        physChar.jump();
    }
    
    public void setViewDirection(Vector3f vec){
        viewDirection.set(vec);
    }

    public void addWalkDirection(float x, float y, float z) {
        walkDirection.addLocal(x, y, z);
    }
    
    public void setWalkDirection(float x, float y, float z){
        walkDirection.set(x, y, z);
    }
    
    public void setWalkDirection(Vector3f vec){
        walkDirection.set(vec);
    }

    public void give(Entity e) {
        if (e instanceof Weapon) {
            weap = ((Weapon) e).weapType;
        }
    }

    public void shoot() {
        //get rotation
        //ray trace to target
        //detect hit
        //draw line to target
        //notify target if its hit

        CollisionResults results = new CollisionResults();

        Vector2f mousePosNoOff = new Vector2f();
        mousePosNoOff.x = (game.cursorPos.x - game.settings.getWidth() / 2) / (game.settings.getWidth() / 2);
        mousePosNoOff.y = (game.cursorPos.y - game.settings.getHeight() / 2) / (game.settings.getHeight() / 2);
        float polarAngle = mousePosNoOff.getAngle() - FastMath.PI / 4;
        float polarMag = FastMath.sqrt(FastMath.pow(mousePosNoOff.x, 2) + FastMath.pow(mousePosNoOff.y, 2));

        Vector2f mousePos = new Vector2f(polarMag * FastMath.cos(polarAngle), polarMag * FastMath.sin(polarAngle));
        Vector3f playerPos = this.getWorldPos().add(new Vector3f(0, 1, 0));
        Vector3f rayCoords = new Vector3f(((mousePos.y * 300)), 1f, ((mousePos.x * 300)));

        Ray ray = new Ray(playerPos, rayCoords);

//        //Temp line
//        Mesh lineMesh = new Mesh();
//        lineMesh.setMode(Mesh.Mode.Lines);
//        lineMesh.setLineWidth(5f);
//        lineMesh.setBuffer(VertexBuffer.Type.Position, 3, new float[]{
//            playerPos.x,
//            playerPos.y,
//            playerPos.z,
//            rayCoords.x + playerPos.x,
//            rayCoords.y + playerPos.y,
//            1f
//        });
//        lineMesh.setBuffer(VertexBuffer.Type.Index, 2, new short[]{ 0, 1 });
//        lineMesh.updateBound();
//        lineMesh.updateCounts();
//        game.line.setMesh(lineMesh);

        game.mobs.collideWith(ray, results);

        Bullet bul = new Bullet(game, playerPos, rayCoords);

        if (results.size() > 0) {
            CollisionResult col = results.getCollision(0);
            Spatial tempGeom = col.getGeometry();
            if (col.getDistance() <= weap.range) {
                System.out.println("  You shot " + tempGeom.getName() + " at " + col.getContactPoint() + ", " + col.getDistance() + " wu away.");
                for (Mob m : game.mobList) {
                    if (m.getSpatial() == tempGeom) {
                        m.hurt(this);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void die() {
        game.getRootNode().detachChild(game.playerNode);
        game.gameOver();
    }
}
