/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jhlabs.image;

/**
 * What to do when processing at the edge of an image.
 * 
 * @author Barry Becker
 */
public enum EdgeAction {
    
    /** Treat pixels off the edge as zero. */
	ZERO,

    /** Clamp pixels to the image edges. */
	CLAMP,

    /** Wrap pixels off the edge onto the oppsoite edge.  */
	WRAP

}
