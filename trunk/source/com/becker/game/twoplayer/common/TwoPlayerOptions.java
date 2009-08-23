package com.becker.game.twoplayer.common;

import com.becker.game.twoplayer.common.search.strategy.SearchStrategyType;
import com.becker.game.twoplayer.common.search.strategy.SearchStrategy;
import com.becker.game.twoplayer.common.search.*;
import com.becker.game.common.*;
import com.becker.sound.MusicMaker;

/**
 * Encapsulate two player options here to keep the TwoPlayerController class mush simpler.
 *
 * @author Barry Becker
 * Date: Aug 20, 2005
 */
public class TwoPlayerOptions extends GameOptions
 {

    // if true then use alpha beta pruning
    private static final boolean ALPHA_BETA = true;
    // if true then use quiescent search
    private static final boolean QUIESCENCE = false;

    private boolean alphaBeta_ = ALPHA_BETA;
    private boolean quiescence_ = QUIESCENCE;

    // if true then try to show a dialog visualizing the game tree.
    private boolean showGameTree_ = false;
    // if true then show all the moves the computer is considering when it considers them.
    private boolean showComputerAnimation_ = false;

    // the default search method.
    private SearchStrategyType strategyMethod_ = SearchStrategyType.MINIMAX;
    private int lookAhead_;
    private int bestPercentage_;

    private boolean autoOptimize_;
    private String autoOptimizeFile_ = null;

    private String preferredTone_ = null;

    private String player1Name_ = GameContext.getLabel("PLAYER1");
    private String player2Name_ = GameContext.getLabel("PLAYER2");


    /**
     * Default Constructor
     * @param defaultLookAhead default number of moves to look ahead.
     * @param defaultBestPercentage default number of best moves to consider at each ply.
     */
    public TwoPlayerOptions() {
        lookAhead_ = 3;
        bestPercentage_ = 100;
        preferredTone_ = MusicMaker.TAIKO_DRUM;
    }

    /**
     * Constructor
     * @param defaultLookAhead default number of moves to look ahead.
     * @param defaultBestPercentage default number of best moves to consider at each ply.
     */
    public TwoPlayerOptions(int defaultLookAhead, int defaultBestPercentage, String preferredTone) {
        lookAhead_ = defaultLookAhead;
        bestPercentage_ = defaultBestPercentage;
        preferredTone_ = preferredTone;
        if (preferredTone == null)
               preferredTone_ = MusicMaker.TAIKO_DRUM;
        
    }

    /**
     * Two player games can never have more than 2 players. Duh.
     * @return 2
     */
    public int getMaxNumPlayers() {
        return 2;
    }

    /**
     * @return the strategy method currently being used.
     */
    public SearchStrategyType getSearchStrategyMethod()
    {
        return strategyMethod_;
    }

    /**
     * @param method the desired search strategy for evaluating the game tree.
     * (eg MINIMAX, NEGAMAX, etc)
     */
    public final void setSearchStrategyMethod(SearchStrategyType method )
    {
        strategyMethod_ = method;
    }


    /**
     * @return the amount of lookahead (number of plys) used by the search strategy
     */
    public final int getLookAhead()
    {
        return lookAhead_;
    }

    /**
     * @param look the number of plys to look ahaead.
     */
    public final void setLookAhead( int look )
    {
        lookAhead_ = look;
    }

    /**
     * @return  the percentage of top moves considered at each ply
     */
    public final int getPercentageBestMoves()
    {
        return bestPercentage_;
    }

    /**
     * @param bestPercentage  the percentage of top moves considered at each ply
     */
    public final void setPercentageBestMoves( int bestPercentage )
    {
        bestPercentage_ = bestPercentage;
    }


    /**
     * @return true if alpha-beta pruning is being employed by the search strategy.
     */
    public final boolean getAlphaBeta()
    {
        return alphaBeta_;
    }

    /**
     * @param ab set whether of not to use alpha-beta pruning
     */
    public final void setAlphaBeta( boolean ab )
    {
        alphaBeta_ = ab;
    }

    /**
     * @return whether or not the quiescent search option is being used by the search strategy
     */
    public final boolean getQuiescence()
    {
        return quiescence_;
    }

    public final void setQuiescence( boolean quiescence )
    {
        quiescence_ = quiescence;
    }

    /**
     * @return whether or not we are showing the game tree (primarily used for debugging)
     */
    public final boolean getShowGameTree()
    {
        return showGameTree_;
    }

    public final void setShowGameTree( boolean show )
    {
        showGameTree_ = show;
    }

    /**
     * @return whether we are showing an animated representation of the computers thought process for each move
     * (takes a long time)
     */
    public final boolean getShowComputerAnimation()
    {
        return showComputerAnimation_;
    }

    public final void setShowComputerAnimation( boolean computerAnimation )
    {
        showComputerAnimation_ = computerAnimation;
    }

    /**
     * Optimize the evaluation weights by running many games where the computer
     * plays against itself.
     * @param autoOptimize whether or not to do an optimization run when you press ok.
     */
    public final void setAutoOptimize(boolean autoOptimize)
    {
        autoOptimize_ = autoOptimize;
    }


    /**
     * @return true it the controller is set to auto optimize instead of play a regular game
     */
    public boolean isAutoOptimize()
    {
        return autoOptimize_;
    }

    /**
     * @param autoOptimizeFile the log file to write to when autp optimizing.
     */
    public final void setAutoOptimizeFile(String autoOptimizeFile)
    {
        autoOptimizeFile_ = autoOptimizeFile;
    }

    /**
     * @return the log file to write to when autp optimizing.
     */
    public final String getAutoOptimizeFile()
    {
        assert (autoOptimizeFile_!=null) : "There is no auto optimize file";
        return autoOptimizeFile_;
    }


    public String getPreferredTone() {
        return preferredTone_;
    }

    public void setPlayerName(boolean player1, String name)  {
         if (player1) {
            player1Name_ = name;
        }
        else {
            player2Name_ = name;
        }
    }

    /**
     * @param player1
     * @return player 1's name if pplayer 1 is true else p2's name
     */
    public String getPlayerName(boolean player1)
    {
        if (player1) {
            return player1Name_;
        }
        else {
            return player2Name_;
        }
    }

    /**
     *
     * @param searchable
     * @return the search strategy to use given a searchable object.
     */
    public SearchStrategy getSearchStrategy(Searchable searchable) {

        return getSearchStrategyMethod().createStrategy(searchable);
    }

}
