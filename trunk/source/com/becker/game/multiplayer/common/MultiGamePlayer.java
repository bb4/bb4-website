package com.becker.game.multiplayer.common;

import com.becker.game.common.*;
import com.becker.ui.*;

import java.awt.*;

/**
 *
 * @author Barry Becker Date: Mar 19, 2006
 */
public class MultiGamePlayer extends Player {


    private static final float SATURATION = 0.8f;
    private static final float BRIGHTNESS = 0.999f;

    protected MultiGamePlayer(String name, Color color, boolean isHuman)
    {
        super(name, color, isHuman);
    }


    /**
     * try to give a unique color based on the name
     * and knowing what the current player colors are.
     */
    public static Color getNewPlayerColor(Player[] players)
    {

        boolean uniqueEnough = false;
        float candidateHue;

        do {
            // keep trying hues until we find one that is not within tolerance distance from another
            candidateHue = (float)Math.random();
            uniqueEnough = isHueUniqueEnough(candidateHue, players);
        } while (!uniqueEnough);

        return Color.getHSBColor(candidateHue, SATURATION, BRIGHTNESS);
    }


    /**
     * @@ this method could use some improvment
     * @param hue to check for uniqueness compared to other players.
     * @param players
     * @return
     */
    private static boolean isHueUniqueEnough(float hue, Player[] players)
    {
        int ct=0;
        float tolerance = 1.0f/(1.0f+1.8f*players.length);
        while ( ct < players.length) {
            if (players[ct] == null)
                ct++;
            else if (Math.abs(GUIUtil.getColorHue(players[ct].getColor()) - hue) > tolerance)
                ct++;
            else break;
        }
        if (ct == players.length)
            return true;
        return false;
    }

}
