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
 * @see GamePanel
 * @author Barry Becker
 */
public class GameApplet extends JApplet
{
    private GamePanel gamePanel_ = null;
    private static final long serialVersionUID = 0L;
    private static final Dimension INITIAL_SIZE =  new Dimension(600, 500);

    @Override
    public void init() {
        GUIUtil.setCustomLookAndFeel();
        GUIUtil.setStandAlone(true);

        String gameName = getParameter("name");
        String localeName = getParameter("locale");
        System.out.println("locale=" + localeName);
        if (localeName == null)
            localeName = "ENGLISH";

        LocaleType locale = GameContext.getLocale(localeName, true);
        System.out.println("setting the locale to " + locale + " for language="+ localeName);
        GameContext.setLocale(locale);
        GameContext.loadGameResources(gameName);

        gamePanel_ = PluginManager.getInstance().getPlugin(gameName).getPanelInstance();
        gamePanel_.init(null);   // applet has no frame.

        gamePanel_.setSize(INITIAL_SIZE);
        this.getContentPane().add(gamePanel_);
    }

    /**
     * This method allow javascript to resize the applet from the browser.
     * Usually applets are not resizable within a web page, but this is a neat trick that allows you to do it.
     */
    @Override
    public final void setSize( int width, int height )
    {
        GameContext.log(3, "in setSize w="+width+" h="+height);
        gamePanel_.setSize( width, height );
    }
}
