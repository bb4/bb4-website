package com.becker.game.twoplayer.go;


/**
 * This interface must by any class which become GoSet members (or members of derived classes) .
 *
 * @see com.becker.game.twoplayer.go.GoSet
 *
 * @author Barry Becker
 */
public interface GoMember extends Cloneable
{
    public Object clone() throws CloneNotSupportedException;
}
