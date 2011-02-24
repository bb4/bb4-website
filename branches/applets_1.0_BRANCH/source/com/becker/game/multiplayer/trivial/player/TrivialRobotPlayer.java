package com.becker.game.multiplayer.trivial.player;

import com.becker.game.common.GameContext;
import com.becker.game.multiplayer.trivial.TrivialAction;
import com.becker.game.multiplayer.trivial.TrivialController;

import java.awt.*;

/**
 * Represents a Robot player.
 *
 * @author Barry Becker
 */
public class TrivialRobotPlayer extends TrivialPlayer
{
    private static final long serialVersionUID = 1;

  
    public TrivialRobotPlayer(String name, Color color )
    {        
        super(name,  color, false);    
        GameContext.log(0, "created a robot with name="+name);
    }

    /**
     * @return a string describing the type of robot.
     */
    public String getType() {
        return "Robot";
    }

    /**
     * Only reveal actionwith  a certain probability.
     * @param pc
     * @return the actoin
     */
    public TrivialAction getAction(TrivialController pc) {
        // 60/40 chance to reveal value
        TrivialAction.Name opt = (Math.random() >0.4) ? TrivialAction.Name.REVEAL: TrivialAction.Name.KEEP_HIDDEN;
        return new TrivialAction(this.getName(), opt);
    }
    
}



