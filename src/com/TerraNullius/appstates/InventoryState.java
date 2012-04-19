/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.TerraNullius.appstates;

import com.TerraNullius.Game;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.InputManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;

/**
 *
 * @author Griffin
 */
public class InventoryState extends AbstractAppState {

    private SimpleApplication app;
    private Node rootNode;
    private AssetManager assetManager;
    private AppStateManager stateManager;
    private InputManager inputManager;
    private ViewPort viewPort;
    private BulletAppState bulletAppState;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (Game) app; // can cast Application to something more specific
        this.rootNode = this.app.getRootNode();
        this.assetManager = this.app.getAssetManager();
        this.stateManager = this.app.getStateManager();
        this.inputManager = this.app.getInputManager();
        this.viewPort = this.app.getViewPort();
        this.bulletAppState = this.stateManager.getState(BulletAppState.class);

        // init stuff that is independent of whether state is PAUSED or RUNNING
//      this.app.getRootNode().attachChild(getX()); // modify scene graph...
//      this.app.doSomething();                     // call custom methods...
    }

    @Override
    public void cleanup() {
        super.cleanup();
        // unregister all my listeners, detach all my nodes, etc...
//      this.app.getRootNode().detachChild(getX()); // modify scene graph...
//      this.app.doSomethingElse();                 // call custom methods...
    }

    @Override
    public void setEnabled(boolean enabled) {
        // Pause and unpause
        super.setEnabled(enabled);
        if (enabled) {
            // init stuff that is in use while this state is RUNNING
//        this.app.getRootNode().attachChild(getX()); // modify scene graph...
//        this.app.doSomethingElse();                 // call custom methods...
        } else {
            // take away everything not needed while this state is PAUSED
        }
    }

    @Override
    public void update(float tpf) {
        if (isEnabled()) {
            // do the following while game is RUNNING
//        this.app.getRootNode().getChild("blah").scale(tpf); // modify scene graph...
//        x.setUserData(...);                                 // call some methods...
        } else {
            // do the following while game is PAUSED, e.g. play an idle animation.    
        }
    }    
}
