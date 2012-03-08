package com.TerraNullius;

import com.TerraNullius.entity.Entity;
import com.TerraNullius.entity.Mob;
import com.TerraNullius.entity.Player;
import com.TerraNullius.entity.Weapon;
import com.TerraNullius.entity.Weapon.WeaponType;
import com.TerraNullius.entity.Zombie;
import com.TerraNullius.physics.TNPhysicsListener;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
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
import com.jme3.system.AppSettings;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.*;


public class Game extends SimpleApplication {
    
    static Game instance;
    public static AppSettings settings;
    
    public BulletAppState bulletAppState;
    
    public Vector2f cursorPos;
    public long shootTimer;
    
    public CameraNode camNode;
    public Node playerNode;
    public Node mobs;
    
    //Entities
    public Player player;
    public ArrayList<Mob> mobList = new ArrayList();
    public ArrayList<Entity> entityList = new ArrayList();
    
    public Geometry line; //debug
    
    //HUD
    public BitmapText healthText;
    public BitmapText weapText;

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
        
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        bulletAppState.getPhysicsSpace().setGravity(new Vector3f(0f,0f,-1f));
        bulletAppState.getPhysicsSpace().setAccuracy(0.005f);
        TNPhysicsListener pListener = new TNPhysicsListener(bulletAppState);
        
        rootNode.attachChild(createTiles(6,0));   
        
        player = new Player(instance);
                
        playerNode = new Node("PlayerNode");
        playerNode.attachChild(player.getGeom());
        
        //Camera
        flyCam.setEnabled(false);
        camNode = new CameraNode("Camera Node", cam);
        camNode.setControlDir(ControlDirection.SpatialToCamera);
        camNode.setLocalTranslation(player.getPos().add(new Vector3f(-14,-14,14)));
        camNode.lookAt(player.getPos(), Vector3f.UNIT_Z);
        playerNode.attachChild(camNode);
                
        rootNode.attachChild(playerNode);
        
        mobs = new Node("Mobs");
        rootNode.attachChild(mobs);
        createZombies(50, 30);
        createTestWeaps();
               
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
        
        guiNode.detachAllChildren();
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        healthText = new BitmapText(guiFont, false);
        healthText.setSize(guiFont.getCharSet().getRenderedSize());
        healthText.setText("Health: " + player.getCurrentHealth());
        healthText.setLocalTranslation(300, healthText.getLineHeight(), 0);
        guiNode.attachChild(healthText);
        weapText = new BitmapText(guiFont, false);
        weapText.setSize(guiFont.getCharSet().getRenderedSize());
        weapText.setText("Weap: " + player.getWeap().toString());
        weapText.setLocalTranslation(100, weapText.getLineHeight(), 0);
        guiNode.attachChild(weapText);
    
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
    public void createZombies(int amount, int maxDist){
        maxDist *= 2;   //convert radius to diameter
        Zombie zombie;
        Random rand = new Random();
        Vector3f offset;
        for(int i=0; i<amount; i++){
            zombie = new Zombie(instance);
            
            offset = new Vector3f(rand.nextInt(maxDist) - maxDist/2, rand.nextInt(maxDist) - maxDist/2, 0);
            zombie.setPos(player.getPos().add(offset));

            mobList.add(zombie);
            mobs.attachChild(zombie.getGeom());
        }
    }
    
        public void createTestWeaps(){
            Weapon weap = new Weapon(WeaponType.HANDS, instance);
            weap.setPos(player.getPos().add(new Vector3f(5f, 5f, 0f)));
            entityList.add(weap);
            Weapon weap1 = new Weapon(WeaponType.PISTOL, instance);
            weap1.setPos(player.getPos().add(new Vector3f(-5f, 5f, 0f)));
            entityList.add(weap1);
            Weapon weap2 = new Weapon(WeaponType.MACHINEGUN, instance);
            weap2.setPos(player.getPos().add(new Vector3f(-5f, -5f, 0f)));
            entityList.add(weap2);
            Weapon weap3 = new Weapon(WeaponType.RIFLE, instance);
            weap3.setPos(player.getPos().add(new Vector3f(5f, -5f, 0f)));
            entityList.add(weap3);

    }
      
    protected Geometry makeCube(String name, float x, float y, float z) {
        Box box = new Box(player.getPos().add(x, y, z), 1, 1, 1);
        Geometry cube = new Geometry(name, box);
        Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", ColorRGBA.randomColor());
        cube.setMaterial(mat1);
        return cube;
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
                player.fireOn();
            }else if(!isPressed){
                player.fireOff();
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
        }
    };
    
     
    @Override
    public void simpleUpdate(float tpf) {
        player.update();
        for(Mob m : mobList){
            m.update(tpf);    
        }
        for(Entity e : entityList){
            e.update();    
        }
        if((System.currentTimeMillis() - shootTimer > player.getWeap().fireRate * 1000) && player.isFireing()){
            player.shoot();
            shootTimer = System.currentTimeMillis();
        }
        healthText.setText("Health: " + player.getCurrentHealth());
        weapText.setText("Weap: " + player.getWeap().toString());
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
    
    public void gameOver(){
        guiNode.detachAllChildren();
//        BitmapFont font = game.getAssetManager().loadFont("Interface/Fonts/Default.fnt");
        BitmapText deathText = new BitmapText(guiFont, false);
        deathText.setSize(30);
        deathText.setText("Game Over");
        deathText.setLocalTranslation(settings.getWidth()/2 - deathText.getLineWidth()/2, deathText.getLineHeight() + settings.getHeight()/2, 0);
        guiNode.attachChild(deathText);
    }
}
