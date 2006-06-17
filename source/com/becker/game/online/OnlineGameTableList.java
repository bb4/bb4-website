package com.becker.game.online;

import com.becker.game.online.*;

import java.io.*;
import java.util.*;

/**
 * @author Barry Becker Date: May 21, 2006
 */
public class OnlineGameTableList extends ArrayList<OnlineGameTable>
                                 implements Serializable {

    private static final long serialVersionUID = 1L;

    public OnlineGameTableList() {
    }

    public String toString()  {
        StringBuilder bldr = new StringBuilder("Tables:\n");
        for (OnlineGameTable t : this) {
             bldr.append(t + "\n");
        }
        return bldr.toString();
    }
}
