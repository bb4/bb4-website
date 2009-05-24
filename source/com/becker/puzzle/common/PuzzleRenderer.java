package com.becker.puzzle.common;

import java.awt.Graphics;

/**
 * Singleton class that takes a PieceList and renders it for the RedPuzzleViewer.
 * Having the renderer separate from the viewer helps to separate out the rendering logic
 * from other features of the RedPuzzleViewer.
 *
 * @author Barry Becker
 */
public abstract class PuzzleRenderer <P> {


    /**
     * private constructor because this class is a singleton.
     * Use getPieceRenderer instead.
     */
    public PuzzleRenderer()
    {
    }


    /**
     * This renders the current state of the Board to the screen.
     */
    public abstract void render( Graphics g, P board, String status, int width, int height );


    protected void drawStatus(Graphics g, String status, int x, int y) {
        String[] lines = status.split("\n");
        int offset = 0;
        for (String line : lines) {
            offset += 14;
            g.drawString( line, x, y + offset );
        }
    }

}

