package com.becker.simulation.snake.data;

import com.becker.common.math.function.ArrayFunction;
import com.becker.common.math.function.ErrorFunction;
import com.becker.common.math.function.Function;

/**
 * Different types of snakes to test.
 *
 * @author Barry Becker
 */
public enum SnakeType {

    TEST_SNAKE("Test Snake", new TestSnakeData()),
    BASIC_SNAKE("Basic Snake", new BasicSnakeData()),
    BUMPY_SNAKE("Bumpy Snake", new BumpySnakeData()),
    LONG_SNAKE("Long Snake", new LongSnakeData()),
    STRANGE_SNAKE("Strange Snake", new StrangeSnakeData()),
    FAT_SNAKE("Fat Snake", new FatSnakeData());

    private String name;
    private ISnakeData snakeData;

    /**
     * Constructor
     */
    SnakeType(String name, ISnakeData snakeData) {
        this.name = name;
        this.snakeData = snakeData;
    }

    public ISnakeData getData() {
        return snakeData;
    }

    public String toString() {
        return name;
    }
}