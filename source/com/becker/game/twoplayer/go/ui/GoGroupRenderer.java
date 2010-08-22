package com.becker.game.twoplayer.go.ui;

import com.becker.game.twoplayer.go.board.*;
import com.becker.game.twoplayer.go.board.analysis.GoBoardUtil;
import static com.becker.game.twoplayer.go.GoControllerConstants.*;   // jdk 1.5 feature
import com.becker.common.ColorMap;
import com.becker.game.common.BoardPosition;
import com.becker.game.common.GameContext;
import com.becker.game.twoplayer.go.board.analysis.neighbor.NeighborAnalyzer;
import com.becker.game.twoplayer.go.board.elements.*;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;

/**
 *  Static Utility methods for rendering GoGroup description.
 *  A GoString by comparison, is composed of a strongly connected set of one or more same color stones.
 *  Groups may be connected by diagonals or one space jumps, or uncut knights moves, but not nikken tobi
 *
 *  @author Barry Becker
 */
final class GoGroupRenderer {

    private static final float BORDER_OFFSET = 0.5f;
    private static final Color EYE_TEXT_COLOR = new Color( 30, 10, 10 );

    /**
     * Cache the border area, color, and cellSize in hashMaps
     * so that we don't have to recompute them if they have not changed.
     * Is this optimization needed? Is it dangerous?
     */
    private static final Map<GoGroup, GroupRegion> hmRegionCache_ = new HashMap<GoGroup, GroupRegion>();

    private ColorMap colormap_;
    private float cellSize_;
    private int margin_;
    private GoBoard board_;
    private Graphics2D g2_;


    public GoGroupRenderer(GoBoard board, ColorMap colormap, float cellSize,
                            int margin, Graphics2D g2)  {
        board_ = board;
        colormap_ = colormap;
        cellSize_ = cellSize;
        margin_ = margin;
        g2_ = g2;
    }


    /**
     * draw debugging information about the group like its border and eyeshapes.
     */
    public void drawGroupDecoration(GoGroup group)
    {
        GroupRegion cachedRegion = hmRegionCache_.get(group);

        if ( !group.isValid() || cachedRegion == null || cellSize_ != cachedRegion.cellSize ) {

            // the colormap will show red if close to dead,
            // so reverse the health value for the other player
            double h = (USE_RELATIVE_GROUP_SCORING ? group.getRelativeHealth():group.getAbsoluteHealth());
            if (!group.isOwnedByPlayer1())  {
                h = -h;
            }

            cachedRegion = new GroupRegion();
            cachedRegion.borderArea = calcGroupBorder( group.getStones());
            cachedRegion.borderColor = colormap_.getColorForValue( h );
            cachedRegion.cellSize = cellSize_;

            // cache these new values (until something changes again)
            hmRegionCache_.put(group, cachedRegion);
        }

        fillInRegion(cachedRegion);

        if (!group.getEyes(board_).isEmpty())   {
            drawEyes(group.getEyes(board_));
        }
    }


    /**
     * accumulate an area geometry that can be rendered to show the group border.
     * @return the groups border shape.
     */
    private Area calcGroupBorder( GoBoardPositionSet groupStones)  {
        if (groupStones == null || groupStones.isEmpty()) {
            return null;  // nothing to draw an area for.
        }

        GoBoardPosition firstStone = groupStones.iterator().next();

        if ( groupStones.size() == 1 ) {
            return createSingleStoneBorder(firstStone);
        }

        return createMultiStoneBorder(firstStone);
    }

    private GoBoard createBoardCopy(GoBoard board)
    {
        GoBoard boardCopy = null;
        try {
            boardCopy = (GoBoard)board.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return boardCopy;
    }

    /**
     * @return the boarder for a single stone.
     */
    private Area createSingleStoneBorder(GoBoardPosition firstStone) {
        float offset = + BORDER_OFFSET + 0.5f;
        // case where the group contains only 1 stone
        float x = margin_ + (firstStone.getCol() - offset) * cellSize_;
        float y = margin_ + (firstStone.getRow() - offset) * cellSize_;
        return new Area( new Ellipse2D.Float( x, y, cellSize_, cellSize_ ) );
    }


    /**
     * To avoid adding the same stone to the queue twice we maintain a hashSet
     * which does not allow dupes.
     * @return the border for a multistone group.
     */
    private Area createMultiStoneBorder(GoBoardPosition firstStone) {

        GoBoard boardCopy = createBoardCopy(board_);

        List<BoardPosition> q = new ArrayList<BoardPosition>();
        GoBoardPositionSet qset = new GoBoardPositionSet();
        GoBoardPositionList visitedSet = new GoBoardPositionList();
        q.add( firstStone.copy() );
        qset.add( firstStone );
        Area area = new Area();
        NeighborAnalyzer nbrAnalyzer = new NeighborAnalyzer(boardCopy);

        while ( !q.isEmpty() ) {
            GoBoardPosition stone = (GoBoardPosition) q.remove( 0 );
            qset.remove( stone );
            stone.setVisited( true );
            visitedSet.add(stone);
            GoBoardPositionSet nbrs = nbrAnalyzer.findGroupNeighbors( stone, true );
            for (GoBoardPosition nbrStone : nbrs) {
                // accumulate all the borders to arrive at the final group border
                area.add( new Area( getBorderBetween( stone, nbrStone ) ) );
                if ( !nbrStone.isVisited() && !qset.contains( nbrStone ) ) {
                    q.add( nbrStone );
                    qset.add( nbrStone );
                }
            }
        }
        // mark all the stones in the group unvisited again.
        GoBoardUtil.unvisitPositions( visitedSet );
        if (GameContext.getDebugMode() > 1) {
            new BoardValidator(boardCopy).confirmAllUnvisited();
        }
        return area;
    }

    /**
     * @return a path marking the border between the 2 specified stones.
     */
    private GeneralPath getBorderBetween( GoBoardPosition s1, GoBoardPosition s2)
    {
        // we can tell which case we have by how far apart the two stones are
        double dist = s1.getDistanceFrom( s2 );
        GeneralPath border = null;

        if ( dist == 1.0 || dist == 2.0 ) {
            // **  or *_*
            border = getLinearNbrBorder(s1, s2);
        }
        else if ( (dist - Math.sqrt( 2.0 )) < 0.001 ) {
            // *_
            // _*
            border = getDiagonalNbrBorder(s1, s2);
        }
        else if ( (dist - Math.sqrt( 5.0 )) < 0.001 ) {
            // *__
            // __*
            border = getKogeimaNbrBorder(s1, s2);
        }
        else
            assert false: "error! dist="+dist ;
        assert ( border!=null) : "the border was null. dist=" + dist;

        return border;
    }

    /**
     * @return  path corresponding to a linear neighbor border.
     */
    private GeneralPath getLinearNbrBorder( GoBoardPosition s1, GoBoardPosition s2)
    {
        GeneralPath border = new GeneralPath();
        float celld2 = cellSize_ / 2.0f;

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
            float xleft = margin_ + ((float) leftStone.getCol() - BORDER_OFFSET) * cellSize_;
            float yleft = margin_ + ((float) leftStone.getRow() - BORDER_OFFSET) * cellSize_;
            float xright = margin_ + ((float) rightStone.getCol() - BORDER_OFFSET) * cellSize_;
            float yright = margin_ + ((float) rightStone.getRow() - BORDER_OFFSET) * cellSize_;
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
            float xtop = margin_ + ((float) topStone.getCol() - BORDER_OFFSET ) * cellSize_;
            float ytop = margin_ + ((float) topStone.getRow() - BORDER_OFFSET) * cellSize_;
            float xbottom = margin_ + ((float) bottomStone.getCol() - BORDER_OFFSET) * cellSize_;
            float ybottom = margin_ + ((float) bottomStone.getRow() - BORDER_OFFSET) * cellSize_;
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
    private GeneralPath getDiagonalNbrBorder( GoBoardPosition s1, GoBoardPosition s2) {
        GeneralPath border = new GeneralPath();
        float celld2 = cellSize_ / 2.0f;

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
            float ulx = margin_ + ((float) ulStone.getCol() - BORDER_OFFSET) * cellSize_;
            float uly = margin_ + ((float) ulStone.getRow() - BORDER_OFFSET) * cellSize_;
            float lrx = margin_ + ((float) lrStone.getCol() - BORDER_OFFSET) * cellSize_;
            float lry = margin_ + ((float) lrStone.getRow() - BORDER_OFFSET) * cellSize_;
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
            float llx = margin_ + ((float) llStone.getCol() - BORDER_OFFSET) * cellSize_;
            float lly = margin_ + ((float) llStone.getRow() - BORDER_OFFSET) * cellSize_;
            float urx = margin_ + ((float) urStone.getCol() - BORDER_OFFSET) * cellSize_;
            float ury = margin_ + ((float) urStone.getRow() - BORDER_OFFSET) * cellSize_;
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
    private GeneralPath getKogeimaNbrBorder( GoBoardPosition s1Stone, GoBoardPosition s2Stone) {
        GeneralPath border = new GeneralPath();
        float celld2 = cellSize_ / 2.0f;

        // calc vector from s1 to s2
        Point2D.Float p =
                new Point2D.Float( celld2 * (s2Stone.getCol() - s1Stone.getCol()),
                                   celld2 * (s2Stone.getRow() - s1Stone.getRow()) );
        float s1x = margin_ + ((float) s1Stone.getCol() - BORDER_OFFSET) * cellSize_;
        float s1y = margin_ + ((float) s1Stone.getRow() - BORDER_OFFSET) * cellSize_;
        float s2x = margin_ + ((float) s2Stone.getCol() - BORDER_OFFSET) * cellSize_;
        float s2y = margin_ + ((float) s2Stone.getRow() - BORDER_OFFSET) * cellSize_;
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
    private void drawEyes(Set<GoEye> eyes)
    {
        if ( !eyes.isEmpty() ) {
            Font font = new Font( "SanSerif", Font.PLAIN, (int) (1.6 * Math.sqrt( cellSize_ ) - 1) );
            g2_.setFont( font );
            g2_.setColor( EYE_TEXT_COLOR );

            for (GoEye eye : eyes) {
                String eyeName = eye.getEyeTypeName();
                for (GoBoardPosition eyeSpace : eye.getMembers()) {
                    float x = margin_ + ((float) eyeSpace.getCol() - BORDER_OFFSET - 0.5f) * cellSize_;
                    float y = margin_ + ((float) eyeSpace.getRow() - BORDER_OFFSET + 0.1f) * cellSize_;
                    g2_.drawString(eyeName, x, y);
                }
            }
        }
    }


    /**
     * Draw the border for the group.
     * fill in the cumulative group border.
     */
    private void fillInRegion(GroupRegion region) {

        if ( region != null &&  region.borderArea != null) {
            g2_.setColor( region.borderColor );
            g2_.fill( region.borderArea );
            g2_.setColor( Color.black );
            g2_.draw( region.borderArea );
        }
    }

    private static class GroupRegion {
        Area borderArea;
        Color borderColor;
        float cellSize;
    }
}