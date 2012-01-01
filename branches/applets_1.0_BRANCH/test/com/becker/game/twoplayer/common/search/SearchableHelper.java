package com.becker.game.twoplayer.common.search;

import com.becker.common.util.FileUtil;

/**
 * Created by IntelliJ IDEA. User: becker Date: Dec 31, 2009 Time: 7:32:13 AM To change this template use File |
 * Settings | File Templates.
 */
public abstract class SearchableHelper implements ISearchableHelper {

    /** moved all test cases here so they are not included in the jar and do not need to be searched */
    protected static final String EXTERNAL_TEST_CASE_DIR =
            FileUtil.getHomeDir() +"/test/com/becker/game/twoplayer/";

    private static final String SGF_EXTENSION = ".sgf";

    public String getTestFile(String problemFileBase) {
        return getTestCaseDir() + problemFileBase + SGF_EXTENSION;
    }

    public String getDefaultTestFile() {
        return getTestFile(getDefaultFileName());
    }

    public String getTestFile(Progress progress, boolean player1) {
        String fName = null;
        switch (progress) {
            case BEGINNING : fName = getStartGameMoveFileName(player1);
                break;
            case MIDDLE : fName = getMiddleGameMoveFileName(player1);
                break;
            case END : fName = getEndGameMoveFileName(player1);
                break;
        }
        return getTestFile(fName);
    }


    protected abstract String getDefaultFileName();

    protected abstract String getStartGameMoveFileName(boolean player1);
    protected abstract String getMiddleGameMoveFileName(boolean player1);
    protected abstract String getEndGameMoveFileName(boolean player1);


    /**
     * @return directory location of test files.
     */
    protected abstract String getTestCaseDir();
}