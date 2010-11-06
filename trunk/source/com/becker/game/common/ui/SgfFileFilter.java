package com.becker.game.common.ui;

import com.becker.ui.file.ExtensionFileFilter;

/**
 * File filter for SGF files.
 *
 * @author Barry Becker Date: Oct 29, 2006
 */
class SgfFileFilter extends ExtensionFileFilter {

    public static final String SGF_EXTENSION = "sgf";


    SgfFileFilter() {
        super(SGF_EXTENSION);
    }

}
