/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
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
public class Populations extends ArrayList<Population> {

    long dayCount = 0;

    /** associate population with function*/
    Map<Population, PopulationFunction> functionMap;

    HabitatGrid grid;

    /**
     * Constructor
     */
    public Populations() {

        initialize();
    }

    public void initialize() {
        functionMap = new HashMap<Population, PopulationFunction>();
        grid = new HabitatGrid(20, 15);

        this.clear();
        this.add(Population.createPopulation(CreatureType.GRASS, 40));
        this.add(Population.createPopulation(CreatureType.COW, 10));
        this.add(Population.createPopulation(CreatureType.RAT, 15));
        this.add(Population.createPopulation(CreatureType.CAT, 9));
        this.add(Population.createPopulation(CreatureType.LION, 4));

        updateGridCellCounts();
    }

    public void nextDay() {
        for (Population pop : this) {
            pop.nextDay(grid);
        }
        // remove any creatures that might have died by starvation or being eaten.
        for (Population pop : this) {
            pop.removeDead(grid);
        }
        updateFunctions(dayCount);
        dayCount++;
    }

    public MultipleFunctionRenderer createFunctionRenderer() {

        List<Function> functions = new ArrayList<Function>();
        List<Color> lineColors = new LinkedList<Color>();

        for (Population pop : this) {
            PopulationFunction func = new PopulationFunction(pop.getSize());
            functions.add(func);
            lineColors.add(pop.getType().getColor());

            functionMap.put(pop, func);
        }

        return new MultipleFunctionRenderer(functions, lineColors);
    }

    private void updateFunctions(long iteration) {

        for (Population pop : this) {
            functionMap.get(pop).addValue(iteration, pop.getSize());
        }
    }

    private void updateGridCellCounts() {
        for (Population pop : this) {
            for (Creature c : pop.getCreatures()) {
                Cell cell = grid.getCellForPosition(c.getLocation());
                cell.addCreature(c);
            }
        }
    }

}
