package com.becker.game.common.ui;

import com.becker.common.Util;
import com.becker.ui.GUIUtil;
import com.becker.game.common.GameContext;
import com.becker.game.common.LocaleType;

import javax.swing.*;

/**
 * This is the applet wrapper for the game programs.
 * It contains a TwoPlayerPanel corresponding to the specified game.
 *
 * @see com.becker.game.common.ui.GamePanel
 * @author Barry Becker
 */
public class GameApplet extends JApplet
{
    private GamePanel gamePanel_ = null;

    public void init() {
        GUIUtil.setCustomLookAndFeel();
        GUIUtil.setStandAlone(true);

        // these parameters are specified in the html page that embeds the applet.
        // They determing the game to play, and the locale to run it.
        String className = getParameter("panel_class");
        String gameName = getParameter("program_name");
        String localeName = getParameter("locale");
        LocaleType locale = GameContext.get(localeName, true);

        // these must be called before anything else
        GameContext.loadGameResources(gameName, className);
        GameContext.setLocale(locale);

        Class gameClass = Util.loadClass(className);

        try {
            
            gamePanel_ = (GamePanel)gameClass.newInstance();

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        gamePanel_.setSize(600,500);
        this.getContentPane().add(gamePanel_);
    }

    /**
     * This method allow javascript to resize the applet from the browser.
     * Usually applets are not resizable within a web page, but this is a neat trick that allows you to do it.
     */
    public final void setSize( int width, int height )
    {
        GameContext.log(3, "in setSize w="+width+" h="+height);
        gamePanel_.setSize( width, height );
    }

    /**Get a parameter value*/
    public final String getParameter( String key, String def )
    {
        return (getParameter( key ) != null ? getParameter( key ) : def);
    }

}
