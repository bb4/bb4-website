package com.becker.game.twoplayer.blockade.persistence;

import com.becker.game.twoplayer.common.persistence.*;
import com.becker.game.twoplayer.blockade.persistence.tokens.Player1BlockadeMoveToken;
import com.becker.game.twoplayer.blockade.persistence.tokens.Player2BlockadeMoveToken;
import com.becker.game.twoplayer.common.persistence.tokens.TwoPlayerMoveToken;


/**
 * Loads a blockade game from a text file.
 *
 * @author Barry Becker
 */
public class BlockadeSGFLoader extends TwoPlayerSGFLoader {
    
    /**
     * Creates a new instance of SGF Loader
     */
    public BlockadeSGFLoader() {
    }

    protected TwoPlayerMoveToken createPlayer1MoveToken() {
        return new Player1BlockadeMoveToken();
    }
    protected TwoPlayerMoveToken createPlayer2MoveToken() {
        return new Player2BlockadeMoveToken();
    }
}
