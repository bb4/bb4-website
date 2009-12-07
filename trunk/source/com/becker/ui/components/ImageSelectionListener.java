package com.becker.ui.components;

import java.awt.image.BufferedImage;
import java.util.EventListener;

/**
 * This interface must be implemented by any class that wants to receive ImageSelectedEvents
 *
 * @author Barry Becker
 */
public interface ImageSelectionListener extends EventListener
{
    void imageSelected(BufferedImage img);
}
