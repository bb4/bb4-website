package com.becker.game.twoplayer.blockade;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * A path that connects a pawn to an opponent home base.
 * Each element of the path is a BlockadeMove
 * Created on May 12, 2007, 5:41 AM
 *
 * @author Barry Becker
 */
public class Path {

    /** the path elements that represent steps to the opponent home. */
    private List<BlockadeMove> elements_;
    
    /**
     * Creates a new instance of Path 
     */
    private Path() {
        elements_ = new LinkedList<BlockadeMove>();
    }
    
    public Path(DefaultMutableTreeNode node) {
        this();
        addPathElements(node);
    }

    public Path(BlockadeMove[] moves) {
        this();
        for (BlockadeMove m : moves) {
            add(m);
        }
    }
    
    void add(BlockadeMove move) {
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
       for (BlockadeMove move: elements_) {          
            if (move.isMoveBlockedByWall(wall, b) )
                return true;
        }
        return false;
    }

    
    void addPathElements(DefaultMutableTreeNode node) {
        Object[] ps = node.getUserObjectPath();
        if (ps.length > 1)  {
            // skip the first null move.
            for (int k = 1; k < ps.length; k++) {
                add((BlockadeMove)ps[k]); 
            }
        }
    }
    
    /**
     *@return the magnitude of the path.
     */
    public int getLength() {
        return elements_.size();
    }
    
    /**
     *return true if the 2 paths are equal.
     */
    @Override
    public boolean equals(Object path) {
        Path comparisonPath = (Path) path;
        if (comparisonPath.getLength() != this.getLength())
            return false;
        int i = 0;
        for (BlockadeMove move : elements_) {
            if (!move.equals(comparisonPath.get(i++)))
                return false;
        } 
        return true;
    }
    
    @Override
    public int hashCode() {
        int hash =  this.getLength() * 100000;
        for (BlockadeMove move : elements_) {
            hash += move.hashCode() / 20;            
        } 
        return hash; 
    }
    
    /**
     *Stringify list path.
     */
    @Override
    public String toString() {
        if (elements_.isEmpty()) return "Path has 0 magnitude";
        
        StringBuilder bldr = new StringBuilder(32);
        for (BlockadeMove move: elements_) {
            bldr.append('[').append(move.toString()).append("],");
        }
        // remove trailing comma
        bldr.deleteCharAt(bldr.length() -1);
        bldr.append("\n");       
        return bldr.toString();
    }
    
}
