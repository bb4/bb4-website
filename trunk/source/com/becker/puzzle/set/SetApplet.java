package com.becker.puzzle.set;

import com.becker.ui.*;

import javax.swing.*;

/**
 * @author Barry Becker Date: Feb 5, 2006
 */
public class SetApplet extends JApplet
{
    private SetPanel setPanel_ = null;
    private static final long serialVersionUID = 0L;

    public SetApplet() {
        GUIUtil.setCustomLookAndFeel();
        GUIUtil.setStandAlone(true);

        setPanel_ = new SetPanel();
        setPanel_.setSize(600,500);
        getContentPane().add(setPanel_);
    }

    /**
     *  Overrides the applet init() method
     */
    public void init() {
        //this.repaint();
    }
    

    /**
     * This method allow javascript to resize the applet from the browser.
     * Usually applets are not resizable within a web page, but this is a neat trick that allows you to do it.
     */
    public final void setSize( int width, int height )
    {
        setPanel_.setSize( width, height );
    }

    /**Get a parameter value*/
    public final String getParameter( String key, String def )
    {
        return (getParameter( key ) != null ? getParameter( key ) : def);
    }


    //------ Main method --------------------------------------------------------
    public static void main( String[] args )
    {

        SetApplet setGame = new SetApplet();

        GUIUtil.showApplet( setGame, "Set Game" );
    }
}

