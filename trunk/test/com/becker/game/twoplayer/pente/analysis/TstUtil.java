package com.becker.game.twoplayer.pente.analysis;

import com.becker.game.common.BoardPosition;
import com.becker.game.common.GamePiece;
import com.becker.game.twoplayer.pente.Patterns;
import com.becker.optimization.parameter.ParameterArray;

import java.util.List;

/**
 * Created by IntelliJ IDEA. User: becker Date: Dec 27, 2009 Time: 8:15:01 AM To change this template use File |
 * Settings | File Templates.
 */
public class TstUtil {

    public static LineRecorder createLine(String linePattern, Patterns patterns, ParameterArray weights) {
        LineRecorder line = new LineRecorder(patterns, weights);
        for (int i=0; i<linePattern.length(); i++) {
             GamePiece piece = null;
             char c = linePattern.charAt(i);
              if (c == 'X') {
                  piece = new GamePiece(true);
              }
              if (c == 'O') {
                  piece = new GamePiece(false);
              }
              BoardPosition pos = new BoardPosition(0, 0, piece);
              line.append(pos);
        }
        return line;
    }


    public static void printLines(List<Line> lines) {
        StringBuilder bldr = new StringBuilder();
        int len = lines.size();
        for (int i=0; i<len; i++)  {
            bldr.append('"').append(lines.get(i)).append('"');
            if (i<len-1)
                bldr.append(", ");
        }
        System.out.println("lines = " + bldr.toString());
    }

    public static String quoteStringList(List<String> strings) {
        StringBuilder bldr = new StringBuilder();
        int len = strings.size();
        for (int i=0; i<len; i++)  {
            bldr.append('"').append(strings.get(i)).append('"');
            if (i<len-1)
                bldr.append(", ");
        }
        return bldr.toString();
    }


}
