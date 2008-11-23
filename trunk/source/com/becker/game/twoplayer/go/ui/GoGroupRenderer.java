package com.becker.game.twoplayer.go.ui;

import static com.becker.game.twoplayer.go.GoControllerConstants.*;   // jdk 1.5 feature
import com.becker.common.ColorMap;
import com.becker.game.twoplayer.common.ui.TwoPlayerBoardViewer;
import com.becker.game.twoplayer.go.*;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;

/**
 *  Static 8tility methods for rendering GoGroup deciration
 *  A GoString by comparison, is composed of a strongly connected set of one or more same color stones.
 *  A GoArmy is a loosely coupled set of Groups
 *  Groups may be connected by diagonals or one space jumps, or uncut knights moves, but not nikken tobi
 *
 *  @author Barry Becker
 */
final class GoGroupRenderer
{

    private static final float BORDER_OFFSET = 0.5f;
    private static final Color EYE_TEXT_COLOR = new Color( 30, 10, 10 );

    /**
     * cache the border area, color, and cellSize in hashMaps
     * so that we don't have to recompute them if they have not changed.
     */ 
    private static final Map<GoGroup, GroupRegion> hmRegionCache_ = new HashMap<GoGroup, GroupRegion>();

    private GoGroupRenderer() {
    }


    /**
     * accumulate an area geometry that can be rendered to show the group border.
     */
    private static Area calcGroupBorder( Set groupStones, float cellSize, GoBoard board )
    {
        if (groupStones == null || groupStones.isEmpty())
          return null;  // nothing to draw an area for.

        GoBoardPosition firstStone = (GoBoardPosition) groupStones.iterator().next();

        if ( groupStones.size() == 1 ) {
            float margin = TwoPlayerBoardViewer.BOARD_MARGIN;
            float offset = + BORDER_OFFSET + 0.5f;
            // case where the group contains only 1 stone
            float x = margin + (firstStone.getCol() - offset) * cellSize;
            float y = margin + (firstStone.getRow() - offset) * cellSize;
            return new Area( new Ellipse2D.Float( x, y, cellSize, cellSize ) );
        }

        // to avoid adding the same stone to the queue twice we maintain a hashSet
        // which does not allow dupes.
        List q = new ArrayList();
        Set qset = new HashSet();
        List visitedSet = new ArrayList();
        q.add( firstStone.copy() );   // avoid cc mod exception?
        qset.add( firstStone );
        Area area = new Area();

        while ( !q.isEmpty() ) {
            GoBoardPosition stone = (GoBoardPosition) q.remove( 0 );
            qset.remove( stone );
            stone.setVisited( true );
            visitedSet.add(stone);
            Set nbrs = board.getGroupNeighbors( stone, true );
            Iterator it = nbrs.iterator();
            while ( it.hasNext() ) {
                GoBoardPosition nbrStone = (GoBoardPosition) it.next();
                // accumulate all the borders to arrive at the final group border
                area.add( new Area( getBorderBetween( stone, nbrStone, cellSize ) ) );
                if ( !nbrStone.isVisited() && !qset.contains( nbrStone ) ) {
                    q.add( nbrStone );
                    qset.add( nbrStone );
                }
            }
        }
        // mark all the stones in the group unvisited again.
        GoBoardUtil.unvisitPositions( visitedSet );
        return area;
    }

    /**
     * return a path marking the border between the 2 specified stones.
     */
    private static GeneralPath getBorderBetween( GoBoardPosition s1, GoBoardPosition s2, float cellSize )
    {
        // we can tell which case we have by how far apart the two stones are
        double dist = s1.getDistanceFrom( s2 );
        GeneralPath border = null;
  
        if ( dist == 1.0 || dist == 2.0 ) {
            // **  or *_*
            border = getLinearNbrBorder( s1, s2, cellSize );
        }
        else if ( (dist - Math.sqrt( 2.0 )) < 0.001 ) {
            // *_
            // _*
            border = getDiagonalNbrBorder( s1, s2, cellSize );
        }
        else if ( (dist - Math.sqrt( 5.0 )) < 0.001 ) {
            // *__
            // __*
            border = getKogeimaNbrBorder( s1, s2, cellSize );
        }
        else
            assert false: "error! dist="+dist ;
        assert ( border!=null) : "the border was null. dist=" + dist;

        return border;
    }

    /**
     * @return  path corresponding to a linear neighbor border.
     */
    private static GeneralPath getLinearNbrBorder( GoBoardPosition s1, GoBoardPosition s2, float cellSize )
    {
        GeneralPath border = new GeneralPath();
        float celld2 = cellSize / 2.0f;
        float margin = TwoPlayerBoardViewer.BOARD_MARGIN;

        if ( s1.getRow() == s2.getRow() ) { // horizontal
            GoBoardPosition leftStone;
            GoBoardPosition rightStone;
            if ( s1.getCol() < s2.getCol() ) {
                leftStone = s1;
                rightStone = s2;
            }
            else {
                leftStone = s2;
                rightStone = s1;
            }
            float xleft = margin + ((float) leftStone.getCol() - BORDER_OFFSET) * cellSize;
            float yleft = margin + ((float) leftStone.getRow() - BORDER_OFFSET) * cellSize;
            float xright = margin + ((float) rightStone.getCol() - BORDER_OFFSET) * cellSize;
            float yright = margin + ((float) rightStone.getRow() - BORDER_OFFSET) * cellSize;
            border.moveTo( xleft, yleft + celld2 );
            border.quadTo( xleft - celld2, yleft + celld2, xleft - celld2, yleft );
            border.quadTo( xleft - celld2, yleft - celld2, xleft, yleft - celld2 );
            border.lineTo( xright, yright - celld2 );
            border.quadTo( xright + celld2, yright - celld2, xright + celld2, yright );
            border.quadTo( xright + celld2, yright + celld2, xright, yright + celld2 );
            border.closePath();
        }
        else { // vertical
            GoBoardPosition topStone;
            GoBoardPosition bottomStone;
            if ( s1.getRow() < s2.getRow() ) {
                topStone = s1;
                bottomStone = s2;
            }
            else {
                topStone = s2;
                bottomStone = s1;
            }
            float xtop = margin + ((float) topStone.getCol() - BORDER_OFFSET ) * cellSize;
            float ytop = margin + ((float) topStone.getRow() - BORDER_OFFSET) * cellSize;
            float xbottom = margin + ((float) bottomStone.getCol() - BORDER_OFFSET) * cellSize;
            float ybottom = margin + ((float) bottomStone.getRow() - BORDER_OFFSET) * cellSize;
            border.moveTo( xtop - celld2, ytop );
            border.quadTo( xtop - celld2, ytop - celld2, xtop, ytop - celld2 );
            border.quadTo( xtop + celld2, ytop - celld2, xtop + celld2, ytop );
            border.lineTo( xbottom + celld2, ybottom );
            border.quadTo( xbottom + celld2, ybottom + celld2, xbottom, ybottom + celld2 );
            border.quadTo( xbottom - celld2, ybottom + celld2, xbottom - celld2, ybottom );
            border.closePath();
        }
        return border;
    }

    /**
     * @return a path correspodning to a diagonal neighbor border.
     */
    private static GeneralPath getDiagonalNbrBorder( GoBoardPosition s1, GoBoardPosition s2, float cellSize )
    {
        GeneralPath border = new GeneralPath();
        float celld2 = cellSize / 2.0f;
        float margin = TwoPlayerBoardViewer.BOARD_MARGIN;

        // upper left = ul, lr = lower right, ...
        GoBoardPosition ulStone = null, lrStone = null, llStone = null, urStone = null;
        if ( s1.getRow() < s2.getRow() ) {
            if ( s1.getCol() < s2.getCol() ) {
                ulStone = s1;
                lrStone = s2;
            }
            else {
                llStone = s2;
                urStone = s1;
            }
        }
        else {
            if ( s1.getCol() < s2.getCol() ) {
                llStone = s1;
                urStone = s2;
            }
            else {
                lrStone = s1;
                ulStone = s2;
            }
        }
        if ( urStone == null ) {
            float ulx = margin + ((float) ulStone.getCol() - BORDER_OFFSET) * cellSize;
            float uly = margin + ((float) ulStone.getRow() - BORDER_OFFSET) * cellSize;
            float lrx = margin + ((float) lrStone.getCol() - BORDER_OFFSET) * cellSize;
            float lry = margin + ((float) lrStone.getRow() - BORDER_OFFSET) * cellSize;
            border.moveTo( ulx, uly + celld2 );
            border.quadTo( ulx - celld2, uly + celld2, ulx - celld2, uly );
            border.quadTo( ulx - celld2, uly - celld2, ulx, uly - celld2 );
            border.quadTo( ulx + celld2, uly - celld2, ulx + celld2, uly );
            border.quadTo( ulx + celld2, uly + celld2, lrx, lry - celld2 );
            border.quadTo( lrx + celld2, lry - celld2, lrx + celld2, lry );
            border.quadTo( lrx + celld2, lry + celld2, lrx, lry + celld2 );
            border.quadTo( lrx - celld2, lry + celld2, lrx - celld2, lry );
            border.quadTo( ulx + celld2, lry - celld2, ulx, uly + celld2 );
            border.closePath();
        }
        else {
            float llx = margin + ((float) llStone.getCol() - BORDER_OFFSET) * cellSize;
            float lly = margin + ((float) llStone.getRow() - BORDER_OFFSET) * cellSize;
            float urx = margin + ((float) urStone.getCol() - BORDER_OFFSET) * cellSize;
            float ury = margin + ((float) urStone.getRow() - BORDER_OFFSET) * cellSize;
            border.moveTo( llx + celld2, lly );
            border.quadTo( llx + celld2, lly + celld2, llx, lly + celld2 );
            border.quadTo( llx - celld2, lly + celld2, llx - celld2, lly );
            border.quadTo( llx - celld2, lly - celld2, llx, lly - celld2 );
            border.quadTo( llx + celld2, lly - celld2, urx - celld2, ury );
            border.quadTo( urx - celld2, ury - celld2, urx, ury - celld2 );
            border.quadTo( urx + celld2, ury - celld2, urx + celld2, ury );
            border.quadTo( urx + celld2, ury + celld2, urx, ury + celld2 );
            border.quadTo( urx - celld2, ury + celld2, llx + celld2, lly );
            border.closePath();
        }

        return border;
    }

    /**
     * @return a border corresponding to a kogeima (nights move) neighbor border.
     */
    private static GeneralPath getKogeimaNbrBorder( GoBoardPosition s1Stone, GoBoardPosition s2Stone, float cellSize )
    {
        GeneralPath border = new GeneralPath();
        float celld2 = cellSize / 2.0f;
        float margin = TwoPlayerBoardViewer.BOARD_MARGIN;

        // calc vector from s1 to s2
        Point2D.Float p = new Point2D.Float( celld2 * (s2Stone.getCol() - s1Stone.getCol()), celld2 * (s2Stone.getRow() - s1Stone.getRow()) );
        float s1x = margin + ((float) s1Stone.getCol() - BORDER_OFFSET) * cellSize;
        float s1y = margin + ((float) s1Stone.getRow() - BORDER_OFFSET) * cellSize;
        float s2x = margin + ((float) s2Stone.getCol() - BORDER_OFFSET) * cellSize;
        float s2y = margin + ((float) s2Stone.getRow() - BORDER_OFFSET) * cellSize;
        if ( Math.abs( p.x ) > Math.abs( p.y ) ) {  // horizontal
            p.x /= 2.0;
            p.y = -p.y;
            border.moveTo( s1x, s1y + p.y );
            border.quadTo( s1x - p.x, s1y + p.y, s1x - p.x, s1y );
            border.quadTo( s1x - p.x, s1y - p.y, s1x, s1y - p.y );
            border.lineTo( s2x, s2y + p.y );
            border.quadTo( s2x + p.x, s2y + p.y, s2x + p.x, s2y );
            border.quadTo( s2x + p.x, s2y - p.y, s2x, s2y - p.y );
            border.quadTo( s2x - p.x, s2y - p.y, s2x - p.x, s2y );
            border.quadTo( s2x - p.x, s2y + p.y, s2x - 2.0f * p.x, s2y + p.y );
            border.quadTo( s1x + p.x, s1y - p.y, s1x + p.x, s1y );
            border.quadTo( s1x + p.x, s1y + p.y, s1x, s1y + p.y );
            border.closePath();
        }
        else { // vertical
            p.y /= 2.0;
            border.moveTo( s1x - p.x, s1y );
            border.quadTo( s1x - p.x, s1y - p.y, s1x, s1y - p.y );
            border.quadTo( s1x + p.x, s1y - p.y, s1x + p.x, s1y );
            border.lineTo( s1x + p.x, s1y + 2.0f * p.y );
            border.quadTo( s2x - p.x, s2y - p.y, s2x, s2y - p.y );
            border.quadTo( s2x + p.x, s2y - p.y, s2x + p.x, s2y );
            border.quadTo( s2x + p.x, s2y + p.y, s2x, s2y + p.y );
            border.quadTo( s2x - p.x, s2y + p.y, s2x - p.x, s2y );
            border.lineTo( s2x - p.x, s2y - 2.0f * p.y );
            border.quadTo( s1x + p.x, s1y + p.y, s1x, s1y + p.y );
            border.quadTo( s1x - p.x, s1y + p.y, s1x - p.x, s1y );
            border.closePath();
        }
        return border;
    }

    /**
     * draw the group's eyes (for debugging/understanding purposes).
     */
    private static void drawEyes( float cellSize, Graphics2D g2, Set eyes )
    {
        float margin = TwoPlayerBoardViewer.BOARD_MARGIN;
        if ( !eyes.isEmpty() ) {
            Font font = new Font( "SanSerif", Font.PLAIN, (int) (1.6 * Math.sqrt( cellSize ) - 1) );
            g2.setFont( font );
            g2.setColor( EYE_TEXT_COLOR );
            Iterator eyeIt = eyes.iterator();

            while ( eyeIt.hasNext() ) {
                GoEye eye = (GoEye) eyeIt.next();
                String eyeName = eye.getEyeTypeName();
                Set eyeSet = eye.getMembers();
                Iterator it = eyeSet.iterator();
                while ( it.hasNext() ) {
                    GoBoardPosition stone = (GoBoardPosition) it.next();
                    float x = margin + ((float) stone.getCol() - BORDER_OFFSET - 0.5f) * cellSize;
                    float y = margin + ((float) stone.getRow() - BORDER_OFFSET + 0.1f) * cellSize;
                    g2.drawString( eyeName, x, y );
                }
            }
        }
    }


    /**
     * draw debugging information about the group like its border and eyeshapes.
     * @see GoGroupRenderer
     */
    public static void drawGroupDecoration(GoGroup group, ColorMap colormap, float cellSize, GoBoard board, Graphics2D g2)
    {
        GroupRegion cachedRegion = hmRegionCache_.get(group);
     
        /* giving cc mod error */
        if ( group.hasChanged() || cachedRegion == null || cellSize != cachedRegion.cellSize ) {

            // the colormap will show red if close to dead,
            // so reverse the health value for the other player
            double h = (USE_RELATIVE_GROUP_SCORING ? group.getRelativeHealth():group.getAbsoluteHealth());
            if (!group.isOwnedByPlayer1())
                h = -h;

            cachedRegion.borderArea = calcGroupBorder( group.getStones(), cellSize, board );
            cachedRegion.borderColor = colormap.getColorForValue( h );
            cachedRegion.cellSize = cellSize;

            // cache these new values (until something changes again)
            hmRegionCache_.put(group, cachedRegion);      
        }


        // fill in the cumulative group border
        if ( cachedRegion !=null ) {
            g2.setColor( cachedRegion.borderColor );
            g2.fill( cachedRegion.borderArea );
            g2.setColor( Color.black );
            g2.draw( cachedRegion.borderArea );
        }

        if (!group.getEyes(board).isEmpty())   {
            drawEyes( cellSize, g2, group.getEyes(board) );
        }
    }

    private static class GroupRegion {
        Area borderArea;
        Color borderColor;
        float cellSize;
    }
}