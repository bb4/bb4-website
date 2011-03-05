package com.becker.game.multiplayer.common;

import com.becker.game.common.player.Player;
import com.becker.game.common.player.PlayerAction;
import com.becker.game.common.player.PlayerList;
import com.becker.game.common.online.IServerConnection;
import com.becker.game.multiplayer.common.online.SurrogateMultiPlayer;
import com.becker.ui.util.ColorUtil;

import java.awt.*;



/**
 * A player in a multi-player game.
 * @author Barry Becker Date: Mar 19, 2006
 */
public abstract class MultiGamePlayer extends Player {

    private static final long serialVersionUID = 1;

    private static final float SATURATION = 0.8f;
    private static final float BRIGHTNESS = 0.999f;

    protected MultiGamePlayer(String name, Color color, boolean isHuman)
    {
        super(name, color, isHuman);
    }

    /**
     * A key abstraction for multi game players.
     * @return this players action
     */
    public abstract PlayerAction getAction(MultiGameController controller);
    
    /**
     * 
     * @param action to set.
     */
    public abstract void setAction(PlayerAction action);

    /**
     * try to give a unique color based on the name
     * and knowing what the current player colors are.
     * @return new player color
     */
    public static Color getNewPlayerColor(PlayerList players)
    {
        boolean uniqueEnough;
        float candidateHue;

        do {
            // keep trying hues until we find one that is not within tolerance distance from another
            candidateHue = (float)Math.random();
            uniqueEnough = isHueUniqueEnough(candidateHue, players);
        } while (!uniqueEnough);

        return Color.getHSBColor(candidateHue, SATURATION, BRIGHTNESS);
    }
  
    /**
     * @@ this method could use some improvement
     * @param hue to check for uniqueness compared to other players.
     * @param players
     * @return true if hue is different enough from the others.
     */
    private static boolean isHueUniqueEnough(float hue, PlayerList players)
    {
        int ct=0;
        float tolerance = 1.0f/(1.0f+1.8f*players.size());
        while ( ct < players.size()) {
            if (players.get(ct) == null)
                ct++;
            else if (Math.abs(ColorUtil.getColorHue(players.get(ct).getColor()) - hue) > tolerance)
                ct++;
            else break;
        }
        return (ct == players.size());
    }
    
    public MultiPlayerMarker getPiece() {
        assert false: "no piece support for " + this.getClass().getName();
        return null;        
    }

    @Override
    public Player createSurrogate(IServerConnection connection) {
        return new SurrogateMultiPlayer(this, connection);
    }


}
