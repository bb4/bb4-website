package com.becker.game.twoplayer.common.ui;

import com.becker.ui.*;
import com.becker.common.*;
import com.becker.game.twoplayer.common.*;
import com.becker.game.twoplayer.common.search.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

/**
 * Presents info to the user about the currently moused over node int eh tree.
 *
 * @author Barry Becker Date: Dec 24, 2006
 */
public class GameTreeInfoLabel extends JLabel {


    public GameTreeInfoLabel() {
        setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
                             BorderFactory.createEmptyBorder(5,5,5,5)));
    }

    /**
     *  Display all the relevant info for the moused over move.
     */
    public void setText(TwoPlayerBoardViewer viewer, TwoPlayerMove m, SearchTreeNode lastNode) {
        TwoPlayerPieceRenderer renderer = (TwoPlayerPieceRenderer)viewer.getPieceRenderer();
        TwoPlayerController controller = (TwoPlayerController)viewer.getController();   // ?? correct?
        String passSuffix = m.isPassingMove() ? " (Pass)" : "";
        String entity = "Human's move";
        Color c = renderer.getPlayer2Color();
        if ( m.isPlayer1() )
            c = renderer.getPlayer1Color();
        if ( (m.isPlayer1() && !controller.getPlayer1().isHuman()) ||
             (!m.isPlayer1() && !controller.getPlayer2().isHuman()) )
            entity = "Computer's move";

        StringBuffer sBuf = new StringBuffer("<html>");
        sBuf.append("<font size=\"+1\" color="+GUIUtil.getHTMLColorFromColor(c) +
                    " bgcolor=#99AA99>" + entity + passSuffix + "</font><br>");
        sBuf.append("Static value = " + Util.formatNumber(m.getValue()) +"<br>");
        sBuf.append("Inherited value = " + Util.formatNumber(m.getInheritedValue()) +"<br>");
        sBuf.append("Alpha = "+Util.formatNumber(lastNode.getAlpha())+"<br>");
        sBuf.append("Beta = "+Util.formatNumber(lastNode.getBeta())+"<br>");
        sBuf.append(((lastNode.getComment()!=null) ? lastNode.getComment() : "") + "<br>");
        sBuf.append("Number of descendants = "+lastNode.getNumDescendants()+"<br>");
        if (m.isUrgent())
            sBuf.append( "<font color=#FF6611>Urgent move!</font>");
        sBuf.append("</html>");
        setText(sBuf.toString());
    }

}
