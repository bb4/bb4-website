package com.becker.game.twoplayer.go;


/**
 * This interface must be implemented by any class which becomes GoSet members (or members of derived classes) .
 *
 * @see com.becker.game.twoplayer.go.GoSet
 *
 * @author Barry Becker
 */
public interface GoMember extends Cloneable
{
    public Object clone() throws CloneNotSupportedException;


}
