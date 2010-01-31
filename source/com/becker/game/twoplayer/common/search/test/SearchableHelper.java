package com.becker.game.twoplayer.common.search.test;

import com.becker.common.util.FileUtil;

/**
 * Created by IntelliJ IDEA. User: becker Date: Dec 31, 2009 Time: 7:32:13 AM To change this template use File |
 * Settings | File Templates.
 */
public abstract class SearchableHelper implements ISearchableHelper {

    /** moved all test cases here so they are not included in the jar and do not need to be searched */
    protected static final String EXTERNAL_TEST_CASE_DIR =
            FileUtil.getHomeDir() +"/test/";

    private static final String SGF_EXTENSION = ".sgf";


    public String getDefaultTestFile() {
        return getTestFile(getDefaultFileName());
    }

    public String getTestFile(String problemFileBase) {
        return getTestCaseDir() + problemFileBase + SGF_EXTENSION;
    }
    
    /**
     * @return test file containing state of saved game to restore.
     */
    protected abstract String getDefaultFileName();

    /**
     * @return directory location of test files.
     */
    protected abstract String getTestCaseDir();
}
