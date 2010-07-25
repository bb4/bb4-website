package com.becker.game.twoplayer.blockade.ui;

import com.becker.common.*;
import com.becker.game.common.*;
import com.becker.game.common.ui.GameBoardRenderer;
import com.becker.game.common.ui.ViewerMouseListener;
import com.becker.game.twoplayer.blockade.*;
import com.becker.game.twoplayer.common.ui.*;

import javax.swing.*;
import java.awt.event.*;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 *  This class takes a BlockadeController as input and displays the
 *  Current state of the Blockade Game. The BlockadeController contains a BlockadeBoard
 *  which describes this state.
 *
 *  @author Barry Becker
 */
public class BlockadeBoardViewer extends AbstractTwoPlayerBoardViewer
                                 
{
    /**
     * Construct the viewer.
     */
    public BlockadeBoardViewer() {}

    @Override
    protected GameController createController()
    {
        return new BlockadeController();
    }

    @Override
    protected GameBoardRenderer getBoardRenderer() {
        return BlockadeBoardRenderer.getRenderer();
    }

    @Override
    protected ViewerMouseListener createViewerMouseListener() {
        return new BlockadeViewerMouseListener(this);
    }


    /**
     * @return the tooltip for the panel given a mouse event.
     */
    @Override
    public String getToolTipText( MouseEvent e )
    {
        Location loc = getBoardRenderer().createLocation(e);
        StringBuffer sb = new StringBuffer( "<html><font=-3>" );

        BlockadeBoardPosition space = (BlockadeBoardPosition)controller_.getBoard().getPosition( loc );
        if ( space != null && GameContext.getDebugMode() > 0 ) {
            sb.append(space.toString());
            sb.append(space.isVisited()?":Visited":"");
            sb.append((space.isHomeBase()?(space.isHomeBase(true)?" P1 Home":"p2 Home"):""));
        }
        sb.append( "</font></html>" );
        return sb.toString();
    }

}
