package com.becker.simulation.habitat.creatures;

import com.becker.common.math.function.Function;
import com.becker.common.math.function.PopulationFunction;
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

    /** asociate population with function*/
    Map<Population, PopulationFunction> functionMap;

    public Populations() {

        functionMap = new HashMap<Population, PopulationFunction>();

        Population grassPop = new Population(CreatureType.GRASS);
        grassPop.createInitialSet(30);
        Population cowPop = new Population(CreatureType.COW);
        cowPop.createInitialSet(10);
        Population ratPop = new Population(CreatureType.RAT);
        ratPop.createInitialSet(20);
        Population catPop = new Population(CreatureType.CAT);
        catPop.createInitialSet(6);
        Population lionPop = new Population(CreatureType.LION);
        catPop.createInitialSet(2);

        this.add(grassPop);
        this.add(cowPop);
        this.add(ratPop);
        this.add(catPop);
        this.add(lionPop);
    }

    public void nextDay() {
        for (Population pop : this) {
            pop.nextDay();
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

}
