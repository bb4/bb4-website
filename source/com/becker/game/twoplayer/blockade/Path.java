package com.becker.game.twoplayer.blockade;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * A path that connects a pawn to an opponent home base.
 * Each element of the path is a BlockadeMove
 * Created on May 12, 2007, 5:41 AM
 *
 * @author Barry Becker
 */
public class Path {
    
    List<BlockadeMove> elements_;
    
    /**
     * Creates a new instance of Path 
     */
    public Path() {
        elements_ = new LinkedList<BlockadeMove>();
    }
    
    public Path(DefaultMutableTreeNode node) {
        this();
        addPathElements(node);
    }
    
    public void add(BlockadeMove move) {
        elements_.add(move);
    }
    
    public Iterator iterator() {
        return elements_.iterator();
    }
    
    public BlockadeMove get(int index) {
        return elements_.get(index);
    }
    
    
    /**
     * @param wall
     * @return true if the wall is blocking the paths.
     */
    public boolean isBlockedByWall(BlockadeWall wall, BlockadeBoard b)
    {
        Iterator<BlockadeMove> it = iterator();
        while (it.hasNext()) {
            BlockadeMove move = it.next();
            if (b.isMoveBlockedByWall(move, wall) )
                return true;
        }
        return false;
    }

    
    public void addPathElements(DefaultMutableTreeNode node) {
        Object[] ps = node.getUserObjectPath();
        Path path = new Path();
        if (ps.length > 1)  {
            // skip the first null move.
            for (int k = 1; k < ps.length; k++) {
                add((BlockadeMove)ps[k]); 
            }
        }
    }
    
    /**
     *@return the length of the path.
     */
    public int getLength() {
        return elements_.size();
    }
    
}
