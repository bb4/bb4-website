package com.becker.game.common.ui;

import com.becker.ui.file.ExtensionFileFilter;

/**
 * File filter for SGF files.
 *
 * @author Barry Becker 
 */
public class SgfFileFilter extends ExtensionFileFilter {

    public static final String SGF_EXTENSION = "sgf";


    public SgfFileFilter() {
        super(SGF_EXTENSION);
    }

}
