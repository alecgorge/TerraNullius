package com.TerraNullius;

import com.TerraNullius.GUI.StartScreen;
import com.TerraNullius.entity.*;
import com.TerraNullius.entity.Weapon.WeaponType;
import com.TerraNullius.physics.TNPhysicsListener;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.ZipLocator;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.font.BitmapText;
import com.jme3.input.*;
import com.jme3.input.controls.*;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.*;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.CameraControl.ControlDirection;
import com.jme3.system.AppSettings;
import de.lessvoid.nifty.Nifty;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.*;

public class Game extends SimpleApplication {

    static Game instance;
    public static AppSettings settings;
    public EntityIDMapper idMap;
    public BulletAppState bulletAppState;
    public Vector2f cursorPos;
    public long shootTimer;
    public CameraNode camNode;
    public Node playerNode;
    public Node mobs;
    boolean isRunning = false;
    //Entities
    public Player player;
    public ArrayList<Mob> mobList = new ArrayList();
    public ArrayList<Entity> entityList = new ArrayList();
    public Geometry line; //debug
    //HUD
    public BitmapText healthText;
    public BitmapText weapText;
    public Nifty nifty;

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
        Logger.getLogger("").setLevel(Level.SEVERE);
        idMap = new EntityIDMapper();

        //Physics
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        bulletAppState.getPhysicsSpace().setGravity(new Vector3f(0f, -1f, 0f));
        bulletAppState.getPhysicsSpace().setAccuracy(0.005f);
        TNPhysicsListener pListener = new TNPhysicsListener(bulletAppState);

        //Light
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(1.3f));
        rootNode.addLight(al);

        DirectionalLight dl = new DirectionalLight();
        dl.setColor(ColorRGBA.White);
        dl.setDirection(new Vector3f(2.8f, -2.8f, -2.8f).normalizeLocal());
        rootNode.addLight(dl);
        
//        //Ground
//        Box b = new Box(Vector3f.ZERO, 128f, 1f, 128f);
//        b.scaleTextureCoordinates(new Vector2f(64f, 64f));
//        Geometry ground = new Geometry("Ground", b);
//        ground.setLocalTranslation(0f, -1f, 0f);
//        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
//        mat.setColor("Color", ColorRGBA.Green);
//        Texture groundTex = assetManager.loadTexture("Textures/BlandTile.png");
//        groundTex.setWrap(Texture.WrapMode.Repeat);
//        mat.setTexture("ColorMap", groundTex);
//        ground.setMaterial(mat);
//        RigidBodyControl groundPhys = new RigidBodyControl(0);
////        groundPhys.setFriction(1f);
////        groundPhys.setKinematic(false); 
//        ground.addControl(groundPhys);
//        bulletAppState.getPhysicsSpace().add(groundPhys);
//        rootNode.attachChild(ground);

        assetManager.registerLocator("town.zip", ZipLocator.class.getName());
        Spatial sceneModel = assetManager.loadModel("main.scene");
        sceneModel.setLocalTranslation(0, -1f, 0);
        sceneModel.setLocalScale(1f);
        CollisionShape sceneShape = CollisionShapeFactory.createMeshShape((Node) sceneModel);
        RigidBodyControl landscape = new RigidBodyControl(sceneShape, 0);
        sceneModel.addControl(landscape);
        rootNode.attachChild(sceneModel);
        bulletAppState.getPhysicsSpace().add(landscape);

        //Player
        player = new Player(instance);
        playerNode = new Node("PlayerNode");
        playerNode.attachChild(player.getSpatial());

        //Camera
        flyCam.setEnabled(false);
        camNode = new CameraNode("Camera Node", cam);
        camNode.setControlDir(ControlDirection.SpatialToCamera);
        camNode.setLocalTranslation(player.getPos().add(new Vector3f(-14, 14, -14)));
        camNode.lookAt(player.getPos(), Vector3f.UNIT_Y);
//        playerNode.attachChild(camNode);
        rootNode.attachChild(camNode);
        rootNode.attachChild(playerNode);

        //Mobs
        mobs = new Node("Mobs");
        rootNode.attachChild(mobs);

        createZombies(20, 30);

        //Collectables
        createTestWeaps();

//        Mesh lineMesh = new Mesh();
//        lineMesh.setMode(Mesh.Mode.Lines);
//        lineMesh.setLineWidth(5f);
//        lineMesh.setBuffer(VertexBuffer.Type.Position, 3, new float[]{0,0,0,1,1,1});
//        lineMesh.setBuffer(VertexBuffer.Type.Index, 2, new short[]{ 0, 1 });
//        lineMesh.updateBound();
//        lineMesh.updateCounts();
//        line = new Geometry("line", lineMesh);
//        Material matL = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
//        matL.setColor("Color", ColorRGBA.Black);
//        line.setMaterial(matL);
//        rootNode.attachChild(line);

        //HUD
//        guiNode.detachAllChildren();
//        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
//        healthText = new BitmapText(guiFont, false);
//        healthText.setSize(guiFont.getCharSet().getRenderedSize());
//        healthText.setText("Health: " + player.getCurrentHealth());
//        healthText.setLocalTranslation(300, healthText.getLineHeight(), 0);
//        guiNode.attachChild(healthText);
//        weapText = new BitmapText(guiFont, false);
//        weapText.setSize(guiFont.getCharSet().getRenderedSize());
//        weapText.setText("Weap: " + player.getWeap().toString());
//        weapText.setLocalTranslation(100, weapText.getLineHeight(), 0);
//        guiNode.attachChild(weapText);
        
        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(getAssetManager(),getInputManager(),getAudioRenderer(),getGuiViewPort());
        nifty = niftyDisplay.getNifty();
        nifty.fromXml("Interface/GUI.xml", "StartScreen", new StartScreen());
        getGuiViewPort().addProcessor(niftyDisplay);

        initKeys();
    }

    //Creates new zombies of number amount a random distance less than maxDist away from the player
    public void createZombies(int amount, int maxDist) {
        maxDist *= 2;   //convert radius to diameter
        Zombie zombie;
        Random rand = new Random();
        Vector3f offset;
        for (int i = 0; i < amount; i++) {
            zombie = new Zombie(instance);

            offset = new Vector3f(rand.nextInt(maxDist) - maxDist / 2, 0, rand.nextInt(maxDist) - maxDist / 2);
            zombie.setPos(player.getPos().add(offset));

            mobList.add(zombie);
        }
    }

    public void createTestWeaps() {
        Weapon weap = new Weapon(WeaponType.HANDS, instance);
        weap.setPos(player.getPos().add(new Vector3f(5f, 0f, 5f)));
        entityList.add(weap);
        Weapon weap1 = new Weapon(WeaponType.PISTOL, instance);
        weap1.setPos(player.getPos().add(new Vector3f(-5f, 0f, 5f)));
        entityList.add(weap1);
        Weapon weap2 = new Weapon(WeaponType.MACHINEGUN, instance);
        weap2.setPos(player.getPos().add(new Vector3f(-5f, 0f, -5f)));
        entityList.add(weap2);
        Weapon weap3 = new Weapon(WeaponType.RIFLE, instance);
        weap3.setPos(player.getPos().add(new Vector3f(5f, 0f, -5f)));
        entityList.add(weap3);

    }

    private void initKeys() {
        //Movement Controls
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("Mouse Up", new MouseAxisTrigger(MouseInput.AXIS_Y, true));
        inputManager.addMapping("Mouse Down", new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        inputManager.addMapping("Mouse Right", new MouseAxisTrigger(MouseInput.AXIS_X, true));
        inputManager.addMapping("Mouse Left", new MouseAxisTrigger(MouseInput.AXIS_X, false));
        inputManager.addMapping("Left Click", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        //HUD controls
        inputManager.addMapping("Inv", new KeyTrigger(KeyInput.KEY_I));
        inputManager.addMapping("Menu", new KeyTrigger(KeyInput.KEY_M));


        inputManager.addListener(analogListener, new String[]{"Mouse Up", "Mouse Down", "Mouse Right", "Mouse Left"});
        inputManager.addListener(actionListener, new String[]{"Left", "Right", "Up", "Down", "Mouse Up", "Jump", "Left Click", "Inv", "Menu"});
    }
    private boolean left = false, right = false, up = false, down = false,
                    fire = false, jump = false, invToggle = false, menuToggle = false;
    private ActionListener actionListener = new ActionListener() {
        public void onAction(String name, boolean isPressed, float tpf) {
            if (name.equals("Left")) {
                if (isPressed) {
                    left = true;
                } else {
                    left = false;
                }
            } else if (name.equals("Right")) {
                if (isPressed) {
                    right = true;
                } else {
                    right = false;
                }
            } else if (name.equals("Up")) {
                if (isPressed) {
                    up = true;
                } else {
                    up = false;
                }
            } else if (name.equals("Down")) {
                if (isPressed) {
                    down = true;
                } else {
                    down = false;
                }
            }else if (name.equals("Jump")) {
                if (isPressed) {
                    jump = true;
                } else {
                    jump = false;
                }
             }else if (name.equals("Left Click")) {
                if (isPressed) {
                    fire = true;
                } else {
                    fire = false;
                }
            }else if (name.equals("Inv")) {
                if (isPressed) {
                    invToggle = true;
                }
            }else if (name.equals("Menu")) {
                if (isPressed) {
                    menuToggle = true;                  
                }
            }
        }
    };
    
    private AnalogListener analogListener = new AnalogListener() {
        public void onAnalog(String name, float value, float tpf) {
            if (name.equals("Mouse Up") || name.equals("Mouse Down") || name.equals("Mouse Right") || name.equals("Mouse Left")) {
                cursorPos = inputManager.getCursorPosition();
                //float angle = (float)(Math.PI + Math.PI/4 + Math.atan2((cursorPos.y - settings.getHeight()/2),(cursorPos.x - settings.getWidth()/2)));
                //Correction for model rotation, above is normal
                float angle = (float) (Math.PI + (3 * Math.PI) / 4 + Math.atan2((cursorPos.y - settings.getHeight() / 2), (cursorPos.x - settings.getWidth() / 2)));
                player.setRot((new Quaternion()).fromAngles(0, angle, 0));
                //BUG: this method breaks for some quadrants
                player.setViewDirection(new Vector3f().set((float)(1/Math.cos(angle)), 0f, (float)(1/Math.sin(angle))));
            }
        }
    };

    @Override
    public void simpleUpdate(float tpf) {
        if (menuToggle) {
            menuToggle = false;
            if (nifty.getCurrentScreen() == nifty.getScreen("MenuScreen")) {
                nifty.gotoScreen("HUDScreen");
            } else {
                nifty.gotoScreen("MenuScreen");
            }
        }
        if (invToggle) {
            invToggle = false;
            if (nifty.getCurrentScreen() == nifty.getScreen("InventoryScreen")) {
                nifty.gotoScreen("HUDScreen");
            } else {
                nifty.gotoScreen("InventoryScreen");
            }
            //        healthText.setText("Health: " + player.getCurrentHealth());
            //        weapText.setText("Weap: " + player.getWeap().toString());
        }
        isRunning = (nifty.getCurrentScreen() == nifty.getScreen("HUDScreen"));
        if (isRunning) {
            Vector3f walkDirection = new Vector3f();
            if (left) {
                walkDirection.addLocal(player.getSpeed(), 0, -player.getSpeed());
            }
            if (right) {
                walkDirection.addLocal(-player.getSpeed(), 0, player.getSpeed());
            }
            if (up) {
                walkDirection.addLocal(player.getSpeed(), 0, player.getSpeed());
            }
            if (down) {
                walkDirection.addLocal(-player.getSpeed(), 0, -player.getSpeed());
            }

            player.setWalkDirection(walkDirection);
            if (jump) {
                player.jump();
            }

            if (fire && !player.isFiring()) {
                player.fireOn();
            } else if (!fire && player.isFiring()) {
                player.fireOff();
            }

            player.update();
            camNode.setLocalTranslation(player.getPos().add(new Vector3f(-14, 14, -14)));
            for (Mob m : mobList) {
                m.update(tpf);
            }
            for (Entity e : entityList) {
                e.update();
            }
            if ((System.currentTimeMillis() - shootTimer > player.getWeap().fireRate * 1000) && player.isFiring()) {
                player.shoot();
                shootTimer = System.currentTimeMillis();
            }
        } else {
            player.setWalkDirection(Vector3f.ZERO);
            for (Mob m : mobList) {
                m.setWalkDirection(Vector3f.ZERO);
            }
        }
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

    public void gameOver() {
        guiNode.detachAllChildren();
//        BitmapFont font = game.getAssetManager().loadFont("Interface/Fonts/Default.fnt");
        BitmapText deathText = new BitmapText(guiFont, false);
        deathText.setSize(30);
        deathText.setText("Game Over");
        deathText.setLocalTranslation(settings.getWidth() / 2 - deathText.getLineWidth() / 2, deathText.getLineHeight() + settings.getHeight() / 2, 0);
        guiNode.attachChild(deathText);
    }
}
