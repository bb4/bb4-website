/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.multiplayer.trivial;

import com.becker.game.multiplayer.common.MultiGameOptions;

/**
 * @author Barry Becker Date: Sep 2, 2006
 */
public class TrivialOptions extends MultiGameOptions {

    /**
     * this constructor uses all default values.
     */
    public TrivialOptions() {}

    /**
     * User specified values for options.
     */
    public TrivialOptions(int maxNumPlayers, int numRobotPlayers ) {
        super(maxNumPlayers, numRobotPlayers);
    }

    /**
     * Verify trivial option constraints satisfied.
     * @return error messages to show in a dlg.
     */
    @Override
    public String testValidity() {
        String superMsgs = super.testValidity();
        String msgs = "" + (superMsgs != null ? superMsgs : "");

        return (msgs.length() > 0) ? msgs : null;
    }

}
