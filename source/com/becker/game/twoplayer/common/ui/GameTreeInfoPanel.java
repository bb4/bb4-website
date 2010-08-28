package com.becker.game.twoplayer.common.ui;

import com.becker.game.common.PlayerList;
import com.becker.game.twoplayer.common.search.tree.SearchTreeNode;
import com.becker.common.util.Util;
import com.becker.ui.*;
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
public class GameTreeInfoPanel extends JPanel {

    private JLabel infoLabel_;
    private JLabel leafDetailLabel_;


    public GameTreeInfoPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
                             BorderFactory.createEmptyBorder(5,5,5,5)));

        infoLabel_ = new JLabel();
        leafDetailLabel_ = new JLabel();
        add(panelWrap(infoLabel_), BorderLayout.CENTER);
        add(panelWrap(leafDetailLabel_), BorderLayout.EAST) ;
    }

    private JPanel panelWrap(JLabel label) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEtchedBorder());
        panel.add(label);
        panel.add(Box.createVerticalGlue());
        return panel;
    }

    /**
     *  Display all the relevant info for the moused over move.
     */
    public void setText(AbstractTwoPlayerBoardViewer viewer, TwoPlayerMove m, SearchTreeNode lastNode) {
        TwoPlayerPieceRenderer renderer = (TwoPlayerPieceRenderer)viewer.getPieceRenderer();
        TwoPlayerController controller = (TwoPlayerController)viewer.getController();   // ?? correct?
        String passSuffix = m.isPassingMove() ? " (Pass)" : "";
        String entity = "Human's move";
        int numKids = lastNode.getChildMoves()==null? 0 : lastNode.getChildMoves().length;

        Color c = renderer.getPlayer2Color();
        if ( m.isPlayer1() )
            c = renderer.getPlayer1Color();
        PlayerList players = controller.getPlayers();
        if ( (m.isPlayer1() && !players.getPlayer1().isHuman()) ||
             (!m.isPlayer1() && !players.getPlayer2().isHuman()) )
            entity = "Computer's move";

        StringBuilder sBuf = new StringBuilder("<html>");
        sBuf.append("<font size=\"+1\" color=" + ColorUtil.getHTMLColorFromColor(c) +
                    " bgcolor=#99AA99>" + entity + passSuffix + "</font><br>");
        sBuf.append("Static value = " + Util.formatNumber(m.getValue()) +"<br>");
        sBuf.append("Inherited value = " + Util.formatNumber(m.getInheritedValue()) +"<br>");
        sBuf.append("Alpha = "+Util.formatNumber(lastNode.getAlpha())+"<br>");
        sBuf.append("Beta = "+Util.formatNumber(lastNode.getBeta())+"<br>");
        sBuf.append(((lastNode.getComment()!=null) ? lastNode.getComment() : "") + "<br>");
        sBuf.append("Number of descendants = " + numKids + "<br>");
        if (m.isUrgent())
            sBuf.append( "<font color=#FF6611>Urgent move!</font>");
        sBuf.append("</html>");
        infoLabel_.setText(sBuf.toString());

        setLeafDetail(m, numKids==0);
    }

    private void setLeafDetail(TwoPlayerMove m, boolean isLeaf) {
        StringBuilder buf = new StringBuilder("");
        if (isLeaf) {
            if (m.getScoreDescription() != null)  {
                buf.append(m.getScoreDescription());
            }
        }  else {
            buf.append("Not a leaf");
        }
        leafDetailLabel_.setText(buf.toString());
    }



}
