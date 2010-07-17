/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.becker.game.twoplayer.go.board;

import com.becker.game.common.GameContext;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Barry Becker
 */
public final class BoardDebugUtil {

   private BoardDebugUtil() {}

    /**
     * pretty print a list of all the current groups (and the strings they contain)
     * @param stones list of stones to print
     */
    public static void debugPrintList( int logLevel, String title, Collection<GoBoardPosition> stones)
      {
           GameContext.log(logLevel, debugPrintListText(logLevel, title, stones));
      }

    /**
     * pretty print a list of all the current groups (and the strings they contain)
     * @param stones list of stones to print
     */
    static String debugPrintListText( int logLevel, String title, Collection<GoBoardPosition> stones)
    {
        if (stones == null)
            return "";
        StringBuffer buf = new StringBuffer(title);
        buf.append("\n  ");
        if (logLevel <= GameContext.getDebugMode())  {
            for (GoBoardPosition stone : stones) {
                buf.append(stone.toString()).append(", ");
            }
        }
        return buf.substring(0, buf.length() - 2);
    }

    /**
     * pretty print a list of all the current groups (and the strings they contain)
     */
    public static void debugPrintGroups( int logLevel, Set groups)
    {
        debugPrintGroups( logLevel,  "---The groups currently on the board are:", true, true, groups);
    }

    /**
     * pretty print a list of all the current groups (and the strings they contain)
     */
    public static void debugPrintGroups( int logLevel, String title, boolean showBlack, boolean showWhite, Set groups)
    {
        if (logLevel <= GameContext.getDebugMode())  {
            GameContext.log( logLevel, title );
            GameContext.log( logLevel, getGroupsText(showBlack, showWhite, groups));
            GameContext.log( logLevel, "----" );
        }
    }


    /**
     * create a nice list of all the current groups (and the strings they contain)
     * @return String containing the current groups
     */
    public static String getGroupsText(Set groups )
    {
        return getGroupsText(true, true, groups);
    }

    /**
     * create a nice list of all the current groups (and the strings they contain)
     * @return String containing the current groups
     */
    static String getGroupsText(boolean showBlack, boolean showWhite, Set groups)
    {
        StringBuffer groupText = new StringBuffer( "" );
        StringBuffer blackGroupsText = new StringBuffer(showBlack? "The black groups are :\n" : "" );
        StringBuffer whiteGroupsText =
                new StringBuffer((showBlack?"\n":"") + (showWhite? "The white groups are :\n" : ""));

        for (Object group1 : groups) {
            GoGroup group = (GoGroup) group1;
            if (group.isOwnedByPlayer1() && (showBlack)) {
                //blackGroupsText.append( "black group owner ="+ group.isOwnedByPlayer1());
                blackGroupsText.append(group);
            } else if (!group.isOwnedByPlayer1() && showWhite) {
                //whiteGroupsText.append( "white group owner ="+ group.isOwnedByPlayer1());
                whiteGroupsText.append(group);
            }
        }
        groupText.append( blackGroupsText );
        groupText.append( whiteGroupsText );

        return groupText.toString();
    }
    
    /**
     * 
     * @param stone
     * @param board
     */
    static void debugPrintNobiNeighborsOf(GoBoardPosition stone, GoBoard board)
    {
        int row = stone.getRow();
        int col = stone.getCol();
        GameContext.log(0,  "Nobi Neigbors of "+stone+" are : " );
        if ( row > 1 )
            System.out.println( board.getPosition(row - 1, col ) );
        if ( row + 1 <= board.getNumRows() )
            System.out.println( board.getPosition(row + 1, col) );
        if ( col > 1 )
            System.out.println( board.getPosition(row, col-1) );
        if ( col + 1 <= board.getNumCols() )
            System.out.println( board.getPosition(row, col+1) );
    }

}
