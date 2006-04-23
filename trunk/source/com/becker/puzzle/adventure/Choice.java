package com.becker.puzzle.adventure;

/**
 * A choice that you can make in a scene.
 * Immutable.
 *
 * @author Barry Becker
 */
public class Choice {

    private String description_;
    private String destination_;

    public Choice(String desc, String dest) {
        description_ = desc;
        destination_ = dest;
    }


    /**
     *
     * @return the test shown in the choice list.
     */
    public String getDescription() {
        return description_;
    }

    /**
     *
     * @return the name of the scene to go to if they select this choice.
     */
    public String getDestination() {
        return destination_;
    }
}
