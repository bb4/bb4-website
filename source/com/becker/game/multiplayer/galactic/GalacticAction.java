/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.becker.game.multiplayer.galactic;

import com.becker.game.common.PlayerAction;
import java.util.List;

/**
 *
 * @author Barry Becker
 */
public class GalacticAction extends PlayerAction {
    
     // a list of outstanding Orders
     protected List<Order> orders_;
     
     
     public GalacticAction(String playerName, List<Order> orders) {
         super(playerName);
         orders_ = orders;
     }
   

     public List<Order> getOrders() {
         return orders_;
     }
}
