package com.becker.optimization.parameter;

/**
 * @author Barry Becker
 */
public enum Direction {
    FORWARD {
        @Override
        public int getMultiplier() {
            return 1;
        }
    },
    BACKWARD  {
        @Override
        public int getMultiplier() {
            return -1;
        }
    };


    /** plus or minus one depending on which direction we are headed */
    public abstract int getMultiplier();
}
