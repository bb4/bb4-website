package com.becker.game.twoplayer.go.board.analysis.group;

import com.becker.game.twoplayer.go.board.elements.group.IGoGroup;

import java.util.Map;
import java.util.WeakHashMap;


/**
 * Maintains a map from groups to GroupAnalyzers.
 * If we request an analyzer for a group that is not in the map, we add it.
 *
 * @author Barry Becker
 */
public class GroupAnalyzerMap {

    private Map<IGoGroup, GroupAnalyzer> analyzerMap;

    /**
     * Constructor.
     * Use a weak HashMap because we do not want this to be the only thing
     * keeps it around when it is no longer on the board.
     */
    public GroupAnalyzerMap() {
        analyzerMap = new WeakHashMap<IGoGroup, GroupAnalyzer>();
    }

    /**
     * @param group
     * @return the analyzer for the specified group
     */
    public GroupAnalyzer getAnalyzer(IGoGroup group) {
        if (analyzerMap.containsKey(group)) {
            return analyzerMap.get(group);
        }
        GroupAnalyzer analyzer = new GroupAnalyzer(group, this);
        analyzerMap.put(group, analyzer);
        return analyzer;
    }

}
