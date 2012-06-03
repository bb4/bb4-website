// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.simulation.habitat.creatures;

import com.becker.common.math.function.Function;
import com.becker.common.math.function.PopulationFunction;
import com.becker.simulation.habitat.model.Cell;
import com.becker.simulation.habitat.model.HabitatGrid;
import com.becker.ui.renderers.MultipleFunctionRenderer;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Create populations for all our creatures.
 *
 * @author Barry Becker
 */
public class SerengetiPopulations extends Populations {

    @Override
    public void addPopulations() {

        this.add(Population.createPopulation(CreatureType.GRASS, 40));
        this.add(Population.createPopulation(CreatureType.WILDEBEEST, 10));
        this.add(Population.createPopulation(CreatureType.RAT, 15));
        this.add(Population.createPopulation(CreatureType.CAT, 9));
        this.add(Population.createPopulation(CreatureType.LION, 4));
    }
}
