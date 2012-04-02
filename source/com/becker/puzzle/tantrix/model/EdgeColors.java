// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix.model;

import java.util.ArrayList;
import java.util.List;

/**
 * The list of 6 edge colors starting from the right side and going counter clockwise
 *
 * @author Barry Becker
 */
public class EdgeColors extends ArrayList<Color> {

      EdgeColors(Color c1, Color c2, Color c3, Color c4, Color c5, Color c6)  {
          super(6);
          add(c1);
          add(c2);
          add(c3);
          add(c4);
          add(c5);
          add(c6);
      }
}
