/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.twoplayer.blockade.persistence;

import com.becker.game.twoplayer.blockade.persistence.tokens.Player1BlockadeMoveToken;
import com.becker.game.twoplayer.blockade.persistence.tokens.Player2BlockadeMoveToken;
import com.becker.game.twoplayer.common.persistence.TwoPlayerSGFLoader;
import com.becker.game.twoplayer.common.persistence.tokens.TwoPlayerMoveToken;


/**
 * Loads a com.becker.game.twoplayer.blockade game from a text file.
 *
 * @author Barry Becker
 */
public class BlockadeSGFLoader extends TwoPlayerSGFLoader {
    
    /**
     * Creates a new instance of SGF Loader
     */
    public BlockadeSGFLoader() {
    }

    @Override
    protected TwoPlayerMoveToken createPlayer1MoveToken() {
        return new Player1BlockadeMoveToken();
    }
    @Override
    protected TwoPlayerMoveToken createPlayer2MoveToken() {
        return new Player2BlockadeMoveToken();
    }
}