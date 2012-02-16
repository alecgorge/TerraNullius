package com.TerraNullius;

import com.TerraNullius.entity.Entity;
import com.TerraNullius.entity.Mob;
import com.TerraNullius.entity.Player;
import com.TerraNullius.entity.Zombie;
import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.control.CameraControl.ControlDirection;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.system.AppSettings;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.logging.*;


public class Game extends SimpleApplication {
    
    static Game instance;
    
    CameraNode camNode;
    Node playerNode;
    public Node mobs;
    
    static AppSettings settings;
    
    //Node shootables;
    Geometry mark;
    Geometry line; //debug
    
    Vector2f cursorPos;
    
    long shootTimer;
    int shootInterval = 50;
    boolean fireNext;
    
    public Player player;
    public ArrayList<Mob> mobList = new ArrayList();
    
    public static void main(String[] args) {
        Game app = new Game();
        Game.instance = app;
        app.setShowSettings(false);
        settings = new AppSettings(true);
        settings.put("Width", 1280);
        settings.put("Height", 720);
        settings.put("Title", "Terra Nullius");
        settings.put("VSync", true);
        //Anti-Aliasing
        settings.put("Samples", 4);
        app.setSettings(settings);
        app.start();
    }


    @Override
    public void simpleInitApp() {
        /*
         * TODO: Look into using RigidBodyControl for everything in physics so we dont have to use walkDirection()
         * 
         */
        Logger.getLogger("").setLevel(Level.SEVERE);
        
        rootNode.attachChild(createTiles(6,0));   
        
        player = new Player(instance);
                
        playerNode = new Node("PlayerNode");
        playerNode.attachChild(player.getGeom());
        
        //Camera
        flyCam.setEnabled(false);
        camNode = new CameraNode("Camera Node", cam);
        camNode.setControlDir(ControlDirection.SpatialToCamera);
        camNode.setLocalTranslation(new Vector3f(25,25,16));
        camNode.lookAt(player.getPos(), Vector3f.UNIT_Z);
        playerNode.attachChild(camNode);
                
        rootNode.attachChild(playerNode);
        
        mobs = new Node("Mobs");
        rootNode.attachChild(mobs);
        createZombies(50, 30);
        
        //Hit detection testing - DEBUG
        Sphere sphere = new Sphere(30, 30, 0.2f);
        mark = new Geometry("BOOM!", sphere);
        Material mark_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat.setColor("Color", ColorRGBA.White);
        mark.setMaterial(mark_mat);
               
        Mesh lineMesh = new Mesh();
        lineMesh.setMode(Mesh.Mode.Lines);
        lineMesh.setLineWidth(5f);
        lineMesh.setBuffer(VertexBuffer.Type.Position, 3, new float[]{0,0,0,1,1,1});
        lineMesh.setBuffer(VertexBuffer.Type.Index, 2, new short[]{ 0, 1 });
        lineMesh.updateBound();
        lineMesh.updateCounts();
        line = new Geometry("line", lineMesh);
        Material matL = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matL.setColor("Color", ColorRGBA.Black);
        line.setMaterial(matL);
        rootNode.attachChild(line);
        
    
        initKeys();
    }
   
    //creates grid of 2^(depth+1) tiles as background
    private Node createTiles(int depth, int index){
        Node n = new Node("n" + depth);
        if(depth > 1){
            for(int i=0;i<=3;i++) n.attachChild(createTiles(depth-1, i));
        }else{
            for(int i=0;i<=3;i++){
                Box b = new Box(getOffsetFromIndex(i,depth-1), 1,1,0);
                Geometry geom = new Geometry("Box", b);

                Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                mat.setColor("Color", getColorFromIndex(i));
                geom.setMaterial(mat);

                n.attachChild(geom);
            }  
        }
        n.move(getOffsetFromIndex(index,depth));
        return n;
    }
    
    private Vector3f getOffsetFromIndex(int index, int tier){
        if(index==0){
            return new Vector3f((int)Math.pow(2, tier),(int)Math.pow(2, tier),0);
        }else if(index==1){
            return new Vector3f((int)Math.pow(2, tier),-(int)Math.pow(2, tier),0);
        }else if(index==2){
            return new Vector3f(-(int)Math.pow(2, tier),(int)Math.pow(2, tier),0);
        }else{
            return new Vector3f(-(int)Math.pow(2, tier),-(int)Math.pow(2, tier),0); 
        }
    }
    
    private ColorRGBA getColorFromIndex(int index){
        if(index==0){
            return ColorRGBA.Blue;
        }else if(index==1){
            return ColorRGBA.Green;
        }else if(index==2){
            return ColorRGBA.Red;
        }else{
            return ColorRGBA.Yellow; 
        }
    }
    
    //Creates new zombies of number amount a random distance less than maxDist away from the player
    private void createZombies(int amount, int maxDist){
        maxDist *= 2;   //convert radius to diameter
        Zombie zombie;
        Random rand = new Random();
        Vector3f offset;
//        Material mat;
        for(int i=0; i<amount; i++){
            zombie = new Zombie(instance);
            
            offset = new Vector3f(rand.nextInt(maxDist) - maxDist/2, rand.nextInt(maxDist) - maxDist/2, 0);
            zombie.setPos(player.getPos().add(offset));
            
//            mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");        
//            mat.setColor("Color", ColorRGBA.randomColor());
//            zombie.getGeom().setMaterial(mat);
            
            mobList.add(zombie);
            mobs.attachChild(zombie.getGeom());
        }
    }
      
    protected Geometry makeCube(String name, float x, float y, float z) {
        Box box = new Box(player.getPos().add(x, y, z), 1, 1, 1);
        Geometry cube = new Geometry(name, box);
        Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", ColorRGBA.randomColor());
        cube.setMaterial(mat1);
        return cube;
    }
    
    public void shoot(){
        //get rotation
        //ray trace to target
        //detect hit
        //draw line to target
        //notify target if its hit

        //BUG: Ray does not line up perfectly with line

        CollisionResults results = new CollisionResults();

        Vector2f mousePosNoOff = new Vector2f();
        System.out.println(cursorPos); 
        mousePosNoOff.x = (cursorPos.x - settings.getWidth()/2)/(settings.getWidth()/2);
        mousePosNoOff.y = (cursorPos.y - settings.getHeight()/2)/(settings.getHeight()/2);
        float polarAngle = mousePosNoOff.getAngle() - FastMath.PI/4;
        float polarMag = FastMath.sqrt(FastMath.pow(mousePosNoOff.x, 2) + FastMath.pow(mousePosNoOff.y, 2));

        Vector2f mousePos = new Vector2f(polarMag * FastMath.cos(polarAngle),
                                         polarMag * FastMath.sin(polarAngle));

        Vector3f playerPos = player.getWorldPos();

        Vector3f rayCoords = new Vector3f(((mousePos.x * 300)),
                                          ((mousePos.y * 300)),
                                          1f);

        Ray ray = new Ray(playerPos, rayCoords);
        System.out.println(ray);   

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
        line.setMesh(lineMesh);

        mobs.collideWith(ray, results);

        if (results.size() > 0) {
            CollisionResult col = results.getCollision(0);
            Geometry tempGeom = col.getGeometry();
            System.out.println("  You shot " + tempGeom.getName() + " at " + col.getContactPoint() + ", " + col.getDistance() + " wu away.");
            for(Mob m : mobList){
                if(m.getGeom() == tempGeom ){
                    m.hurt(player);
                    break;
                }
            }
            mark.setLocalTranslation(col.getContactPoint());
            rootNode.attachChild(mark);
        } else {
            rootNode.detachChild(mark);
        }
          
    }
    
    
    private void initKeys() {
    inputManager.addMapping("Left",   new KeyTrigger(KeyInput.KEY_A));
    inputManager.addMapping("Right",  new KeyTrigger(KeyInput.KEY_D));
    inputManager.addMapping("Up",  new KeyTrigger(KeyInput.KEY_W));
    inputManager.addMapping("Down",  new KeyTrigger(KeyInput.KEY_S));
    inputManager.addMapping("Mouse Up", new MouseAxisTrigger(MouseInput.AXIS_Y, true));
    inputManager.addMapping("Mouse Down", new MouseAxisTrigger(MouseInput.AXIS_Y, false)); 
    inputManager.addMapping("Mouse Right", new MouseAxisTrigger(MouseInput.AXIS_X, true)); 
    inputManager.addMapping("Mouse Left", new MouseAxisTrigger(MouseInput.AXIS_X, false));
    inputManager.addMapping("Left Click", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
                
        
    inputManager.addListener(analogListener, new String[]{"Left", "Right", "Up", "Down", "Mouse Up", "Mouse Down", "Mouse Right", "Mouse Left"});
    inputManager.addListener(actionListener, "Left Click");
    }
    
    private ActionListener actionListener = new ActionListener() {
        public void onAction(String name, boolean isPressed, float tpf) {
            if(name.equals("Left Click") && isPressed){
                fireNext = true;
            }else if(!isPressed){
                fireNext = false;
            }
        }
    };
    
    private AnalogListener analogListener = new AnalogListener() {
        public void onAnalog(String name, float value, float tpf) {
            if (name.equals("Right")) {
                playerNode.move(tpf*player.getSpeed(),-tpf*player.getSpeed(), 0);
                player.move(tpf*player.getSpeed(),-tpf*player.getSpeed(), 0);
            }
            if (name.equals("Left")) {
                playerNode.move(-tpf*player.getSpeed(), tpf*player.getSpeed(), 0);
                player.move(-tpf*player.getSpeed(), tpf*player.getSpeed(), 0);                
            }
            if (name.equals("Up")) {
                playerNode.move(tpf*player.getSpeed(),tpf*player.getSpeed(), 0);
                player.move(tpf*player.getSpeed(),tpf*player.getSpeed(), 0);
            }
            if (name.equals("Down")) {
                playerNode.move(-tpf*player.getSpeed(),-tpf*player.getSpeed(), 0);
                player.move(-tpf*player.getSpeed(),-tpf*player.getSpeed(), 0);
            }            
            if (name.equals("Mouse Up") || name.equals("Mouse Down") || name.equals("Mouse Right") || name.equals("Mouse Left")) {
                cursorPos = inputManager.getCursorPosition();
                float angle = (float)(Math.PI + Math.PI/4 + Math.atan2((cursorPos.y - settings.getHeight()/2),(cursorPos.x - settings.getWidth()/2)));
                player.setRot((new Quaternion()).fromAngles(0, 0, angle));
            }
            System.out.println(player.getPos());
        }
    };
    
     
    @Override
    public void simpleUpdate(float tpf) {
        player.update();
        for(Mob m : mobList){
            m.update(tpf);    
        }
        if((System.currentTimeMillis() - shootTimer > shootInterval) && fireNext){
            shoot();
            shootTimer = System.currentTimeMillis();
        }
        
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
