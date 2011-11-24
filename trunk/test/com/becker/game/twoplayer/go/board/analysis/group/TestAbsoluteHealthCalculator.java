// Copyright by Barry G. Becker, 2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.game.twoplayer.go.board.analysis.group;

import com.becker.common.format.FormatUtil;
import com.becker.game.twoplayer.go.GoTestCase;
import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.analysis.eye.information.E1Information;
import com.becker.game.twoplayer.go.board.analysis.eye.information.E2Information;
import com.becker.game.twoplayer.go.board.analysis.eye.information.EyeInformation;
import com.becker.game.twoplayer.go.board.analysis.eye.information.FalseEyeInformation;
import com.becker.game.twoplayer.go.board.elements.eye.GoEyeSet;
import com.becker.game.twoplayer.go.board.elements.eye.IGoEye;
import com.becker.game.twoplayer.go.board.elements.group.IGoGroup;
import junit.framework.Assert;
import org.igoweb.igoweb.client.gtp.A;

import java.util.Arrays;

import static com.becker.game.twoplayer.go.board.analysis.eye.information.E4Information.Eye4Type.E2222;
import static com.becker.game.twoplayer.go.board.analysis.eye.information.E5Information.Eye5Type.E12223;
import static com.becker.game.twoplayer.go.board.analysis.eye.information.E6Information.Eye6Type.E222233;
import static com.becker.game.twoplayer.go.board.analysis.eye.information.EyeType.*;


/**
* Mostly test that the scoring of groups works correctly.
* @author Barry Becker
*/
public class TestAbsoluteHealthCalculator extends GoTestCase {

    private static final String PREFIX = "board/analysis/grouphealth/";
    
    private AbsoluteHealthCalculator absHealthCalculator;

    @Override
    public void setUp() {
        IGoGroup group = new StubGoGroup(0.0f, true, 10);
                GroupAnalyzerMap analyzerMap = new GroupAnalyzerMap();

                absHealthCalculator = new AbsoluteHealthCalculator(group, analyzerMap);
    }

    public void testDefaultEyePotential() {
        assertEquals("Unexpected eye potential",
                0.0f, absHealthCalculator.getEyePotential());
    }

    public void testIsValid() {
            assertFalse("Unexpected isValidValue", absHealthCalculator.isValid());
    }

}
