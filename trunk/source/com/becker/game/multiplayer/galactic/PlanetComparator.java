/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.multiplayer.galactic;

import java.util.Comparator;

/**
 * Sort planets by how far they are from a comparisonPlanet.
 *
 * @author Barry Becker Date: Aug 26, 2006
 */
public class PlanetComparator implements Comparator {

    private Planet comparisonPlanet_;

    public PlanetComparator(Planet p) {
        comparisonPlanet_ = p;
        assert(comparisonPlanet_ != null):
                "you must specify a comparison planet.";

    }

    
    public int compare(Object p1, Object p2) {

        double p1Dist = ((Planet)p1).getDistanceFrom(comparisonPlanet_);
        double p2Dist = ((Planet)p2).getDistanceFrom(comparisonPlanet_);

        if (p1Dist < p2Dist )
            return -1;
        else if ( p1Dist > p2Dist )
            return 1;
        else
            return 0;
    }

}
