package com.becker.game.twoplayer.go;

import com.becker.game.twoplayer.common.*;
import com.becker.sound.*;

/**
 * @author Barry Becker Date: Nov 23, 2006
 */
public class GoOptions extends TwoPlayerOptions {


    // The komi can vary, but 5.5 seems most commonly used.
    public static final float DEFAULT_KOMI = 5.5f;


    // initial look ahead factor.
    static final int DEFAULT_LOOKAHEAD = 2;
    // for any given ply never consider more that BEST_PERCENTAGE of the top moves
    static final int BEST_PERCENTAGE = 70;


    // additional score given to black or white to bring things into balance.
    // sort of like giving a partial handicap stone.
    private float komi_ = DEFAULT_KOMI;

    public GoOptions() {
        this(DEFAULT_LOOKAHEAD, BEST_PERCENTAGE, MusicMaker.SHAMISEN, DEFAULT_KOMI);
    }

    public GoOptions(int defaultLookAhead, int defaultBestPercentage, String preferredTone, float komi) {
        super(defaultLookAhead, defaultBestPercentage, preferredTone);
        setKomi(komi);
    }

    public float getKomi() {
        return komi_;
    }

    public void setKomi(float komi) {
        this.komi_ = komi;
    }


}
