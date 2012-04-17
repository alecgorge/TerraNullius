/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.TerraNullius.render;

import com.jme3.asset.AssetManager;
import com.jme3.post.Filter;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.CartoonEdgeFilter;
import com.jme3.scene.Spatial;

/**
 *
 * @author Griffin
 */
public class RenderHelper extends FilterPostProcessor {
    
    public RenderHelper(AssetManager assetManager){
        super(assetManager);
    }
    
    //Set an outline on a spatial for mouse hovering/selection
    public void setOutline(){
        Filter edgeFilter = new CartoonEdgeFilter();
        addFilter(edgeFilter);
    }
}
