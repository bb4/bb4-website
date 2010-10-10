package com.becker.game.twoplayer.common.search;

import com.becker.game.common.GamePiece;
import com.becker.game.common.Move;
import com.becker.game.common.MoveList;
import com.becker.game.twoplayer.common.TwoPlayerMove;


/**
 * Stub implementation of TwoPlayerMove to help test the search strategy classes without needing
 * a specific game implementation.
 *
 * @author Barry Becker
 */
public class TwoPlayerMoveStub extends TwoPlayerMove {

    public static final String ROOT_ID = "root";

    /** child moves */
    private MoveList children_;

    /** every move but the root of the tree has a parent_ */
    private TwoPlayerMoveStub parent_;


    public TwoPlayerMoveStub(int val, boolean isPlayer1, TwoPlayerMoveStub parent) {
        super(1, 1, val, new GamePiece(isPlayer1));
        this.parent_ = parent;
        this.children_ = new MoveList();
    }

    public TwoPlayerMoveStub getParent() {
        return parent_;
    }

    public void setChildren(MoveList children) {
        children_.clear();
        children_.addAll(children);
    }

    public MoveList getChildren() {
        return children_;
    }

    public boolean isInJeopardy() {
        return false;
    }

    /**
     *
     * Unique move id that defines where in the game tree this move resides.
     * Something we can match against when testing.
     *
     * @return unique id for move in tree
     */
    public String getId() {
        if (parent_ == null) {
            return ROOT_ID;
        }

        TwoPlayerMoveStub current = this;
        TwoPlayerMoveStub parentMove = getParent();
        String id = "";
        while (parentMove != null) {
            int index = parentMove.getChildren().indexOf(current);
            if (index<0)
                System.out.println(" not found among " + getParent().getChildren().size() + " parent children");
            id += Integer.toString(index);
            current = parentMove;
            parentMove = parentMove.getParent();
        }
        return id;
    }

    public void print() {
        print("", this);
    }

    private void print(String indent, TwoPlayerMoveStub subtreeRoot)  {

        MoveList childList = subtreeRoot.getChildren();
        System.out.println(indent + subtreeRoot);
        for (Move move : childList) {
            print("  " + indent, (TwoPlayerMoveStub)move);
        }
    }


    public String toString() {
        StringBuilder bldr = new StringBuilder();
        bldr.append("id:").append(getId()).append(" value:").append(getValue());
        bldr.append(isUrgent()?" urgent":" ").append(isInJeopardy()?"jeopardy":"");
        return bldr.toString();
    }

    /*
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        TwoPlayerMoveStub that = (TwoPlayerMoveStub) o;


        return that.getValue() == ((TwoPlayerMoveStub) o).getValue();
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + getValue();
        return result;
    }*/
}
