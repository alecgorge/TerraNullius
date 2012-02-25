/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.TerraNullius.entity;

/**
 *
 * @author Griffin
 */
public class Weapon extends Entity {

    public enum WeaponType{
        MACHINEGUN(0.05, 8, 50),
        PISTOL(0.33, 20, 50),
        RIFLE(1, 50, 100),
        HANDS(0.5, 10, 2);
        
        public double fireRate;
        public int fireDamage;
        public int range;
        
        private WeaponType(double fireRate, int fireDamage, int range){
            this.fireRate = fireRate;
            this.fireDamage = fireDamage;
            this.range = range;
        }
        
    }
    
}
