// Copyright by Barry G. Becker, 2014. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.apps.aikido.generation;

/**
 * Some options for how the "All Techniques" page is shown.
 * @author Barry Becker
 */
public class AllTechniqueConfig {

    /**
     * if in debug mode then we do the following things differently
     * 1) in the all techniques page, show the ids instead of the cut-points, and make the images bigger.
     * 2) when replacing refs, don't substitute the whole subtree, just the subtree root node.
     */
    boolean debug = false;

    boolean showImages = true;
    int fontSize = 9;
    int borderWidth = 1;
    int imageSize = 80;

    public AllTechniqueConfig() {
    }

    public AllTechniqueConfig(boolean debug, int imageSize) {
        this.debug = debug;
        showImages = (imageSize > 0);
        this.imageSize = imageSize;
    }
}
