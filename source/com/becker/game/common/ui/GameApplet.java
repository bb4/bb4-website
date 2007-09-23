package com.becker.game.common.ui;

import com.becker.common.*;
import com.becker.game.common.*;
import com.becker.ui.*;

import javax.swing.*;
import java.awt.*;

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
    private static final long serialVersionUID = 0L;
    private static final Dimension INITIAL_SIZE =  new Dimension(600, 500);

    public void init() {
        GUIUtil.setCustomLookAndFeel();
        GUIUtil.setStandAlone(true);

        String gameName = getParameter("program_name");
        // these values can now be retrieved from plugins.xml instead from applet 
        String className = PluginManager.getInstance().getPlugin(gameName).getPanelClass();
                
        //String className = getParameter("panel_class");       
        String localeName = getParameter("locale");
   
        // this will load the resources for the specified game.        
        GameContext.loadGameResources(gameName);
        
        Class gameClass = Util.loadClass(className);            

        try {
            gamePanel_ = (GamePanel)gameClass.newInstance();
            gamePanel_.init(null);   // applet has no frame.

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        gamePanel_.setSize(INITIAL_SIZE);
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
