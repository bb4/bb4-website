/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.twoplayer.go.board.analysis.eye.information;

/**
 * Single space eye  - *
 *
 * @author Barry Becker
 */
public class E1Information extends AbstractEyeSubtypeInformation {

    public E1Information() {
        initialize(false, 1);
    }

    public String getTypeName() {
       return "E1";
    }

}