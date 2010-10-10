package com.becker.game.twoplayer.common;

import com.becker.game.common.GameContext;
import com.becker.game.common.GameOptions;
import com.becker.game.twoplayer.common.search.SearchOptions;
import com.becker.sound.MusicMaker;

/**
 * Encapsulate two player options here to keep the TwoPlayerController class mush simpler.
 *
 * @author Barry Becker
 * Date: Aug 20, 2005
 */
public class TwoPlayerOptions extends GameOptions
 {
    /** Sound played when move is made */
    private static final String DEFAULT_TONE = MusicMaker.TAIKO_DRUM;

    /** if true then try to show a dialog visualizing the game tree.  */
    private boolean showGameTree_ = false;

    private boolean autoOptimize_;
    private String autoOptimizeFile_ = null;

    private String preferredTone_ = null;

    private String player1Name_ = GameContext.getLabel("PLAYER1");
    private String player2Name_ = GameContext.getLabel("PLAYER2");

    private SearchOptions searchOptions_;

    /**
     * Default Constructor
     */
    public TwoPlayerOptions() {
        searchOptions_ = createDefaultSearchOptions();
        preferredTone_ = getDefaultTone();
    }

    /**
     * Constructor
     * @param searchOptions search options to use.
     * @param preferredTone sound to make on each move.
     */
    public TwoPlayerOptions(SearchOptions searchOptions, String preferredTone) {

        searchOptions_ = searchOptions;
        preferredTone_ = preferredTone;
        if (preferredTone == null) {
           preferredTone_ = MusicMaker.TAIKO_DRUM;
        }
    }

    protected SearchOptions createDefaultSearchOptions() {
        return new SearchOptions();
    }

    public SearchOptions getSearchOptions() {
        return searchOptions_;
    }

    /**
     * Two player games can never have more than 2 players. Duh.
     * @return 2
     */
    @Override
    public int getMaxNumPlayers() {
        return 2;
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
     * @param player1  true if player one.
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

    protected String getDefaultTone() {
        return DEFAULT_TONE;
    }
}
