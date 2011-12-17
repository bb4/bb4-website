/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.twoplayer.common.search.examples;

/**
 * Date: Oct 24, 2010
 *
 * @author Barry Becker
 */
public enum EvaluationPerspective {

    /**
     * Always evaluate from player ones point of view.
     * This is done for minimax for example.
     */
    ALWAYS_PLAYER1,

    /**
     * At a given level, evaluate the potential moves according to the player who's turn it is.
     */
    CURRENT_PLAYER
}
