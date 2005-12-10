package com.becker.puzzle.maze;

/**
 * @author Barry Becker Date: Nov 27, 2005
 */
public class Direction {


    // possible directions as we traverse
    public static final Integer FORWARD = 1 ;
    public static final Integer LEFT = 2 ;
    public static final Integer RIGHT = 3 ;

    // vary the probability that each direction occurs for interesting effects
    // the sum of these probabilities must sum to 1
    public static final double DEFAULT_FORWARD_PROB = 0.6;
    public static final double DEFAULT_LEFT_PROB = 0.39;
    public static final double DEFAULT_RIGHT_PROB = 0.01;


    // default probs
    private double forwardProb_ = DEFAULT_FORWARD_PROB;
    private double leftProb_ = DEFAULT_LEFT_PROB;
    private double rightProb_ = DEFAULT_RIGHT_PROB;


    public double getForwardProb() {
        return forwardProb_;
    }

    public void setForwardProb(double forwardProb) {
        this.forwardProb_ = forwardProb;
    }

    public double getLeftProb() {
        return leftProb_;
    }

    public void setLeftProb(double leftProb) {
        this.leftProb_ = leftProb_;
    }

    public double getRightProb() {
        return rightProb_;
    }

    public void setRightProb(double rightProb) {
        this.rightProb_ = rightProb_;
    }


}
