package com.becker.game.twoplayer.common.persistence.tokens;

import ca.dj.jigo.sgf.SGFException;
import ca.dj.jigo.sgf.tokens.*;
import java.io.StreamTokenizer;
import java.io.IOException;

/**
 * The size of the game board.
 * Unlike the size of a go board, 2 dimensions must be specified.
 */
public class Size2Token extends SGFToken implements InfoToken
{
    
    private int numRows;
    private int numColumns;
    
    public Size2Token() { 
        
    }
  
    /**
     * Parse the dimensions of the board
     */
    protected boolean parseContent( StreamTokenizer st )  throws IOException, SGFException
    {
 
        try
        {   
            int token = st.nextToken();              
            numRows = Integer.parseInt(st.sval);
            if (st.nextToken() != (int)']') return false;
            if (st.nextToken() != (int)'[') return false;            
            st.nextToken();          
            numColumns = Integer.parseInt(st.sval);
            if (st.nextToken() != (int)']') return false;    
        }
        catch( NumberFormatException nfe )
        {
            return false;
        }

        return true;
    }

    public int getNumRows() { 
        return numRows; 
    }
    
    public int getNumColumns() { 
        return numColumns; 
    }
 
}

