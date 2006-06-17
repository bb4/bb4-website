package com.becker.game.online;

import java.io.*;

/**
 * These game commands are passed over the wire betwween client and server
 * as a means fo communication between the two.
 * Implements command pattern.
 * Immutable.
 *
 * @author Barry Becker Date: May 20, 2006
 */
public class GameCommand implements Serializable {

    private static final long serialVersionUID = 1;

    public enum Name {
        ENTER_ROOM,
        ADD_TABLE,
        JOIN_TABLE,
        CHANGE_NAME,
        UPDATE_TABLES;
    }


    // name of the command
    Name name_;

    // corresponding argument. Null if none.
    Object argument_;

    /**
     *
     * @param name of the comman
     * @param arg serializable argument. null if none.
     */
    public GameCommand(Name name, Serializable arg) {
        name_ = name;
        argument_ = arg;
    }

    public Name getName() {
        return name_;
    }

    public Object getArgument() {
        return argument_;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder("Command: "+ name_ + '\n');
        buf.append(argument_.toString());
        return buf.toString();
    }
}
