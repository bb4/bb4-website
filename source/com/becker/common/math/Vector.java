package com.becker.common.math;

/**
 * Represents an n dimensional vector class.
 * @author Barry Becker
 */
public class Vector {

    /** the vector values */
    private double[] data;

    /**
     * Constructor
     * @param length  number of elements
     */
    public Vector(int length) {
        data = new double[length];
    }

    /**
     * Constructor
     * @param data array of values to use for data
     */
    public Vector(double[] data) {
        this.data = data;
    }

    public void set(int  i, double value) {
        this.data[i] = value;
    }

    public double get(int i) {
        return data[i];
    }

    public void copyFrom(Vector b) {
        System.arraycopy(this.data, 0, b.data, 0, size());
    }

    /**
     * Find the dot product of ourselves with another vector.
     * @return the dot product with another vector
     */
    public double dot(Vector b)  {
        checkDims(b);

        double dotProduct = 0.0;
        for ( int i = 0; i < size(); i++ ) {
            dotProduct += data[i] * b.get(i);
        }
        return dotProduct;
    }

    /**
     * @param factor amount to scale by.
     * @return this Vector, scaled by a constant factor
     */
    public Vector scale(double factor) {
        double[] newData = new double[size()];
        for (int i = 0; i < size(); i++) {
            newData[i] = factor * data[i];
        }
        return new Vector(newData);
    }

    /**
     * @return pairwise sum of this Vector a and b
     */
    public Vector plus(Vector b) {

        checkDims(b);
        double[] d = new double[size()];
        for (int i = 0; i < size(); i++)
            d[i] = this.data[i] + b.data[i];
        return new Vector(d);
    }

    /**
     * @param b vector to find distance to.
     * @return Euclidean distance between this Vector and b
     */
    public double distanceTo(Vector b) {

        checkDims(b);
        double sum = 0.0;
        for (int i = 0; i < size(); i++)
            sum += (this.data[i] - b.data[i]) * (this.data[i] - b.data[i]);
        return Math.sqrt(sum);
    }

    /** @return magnitude of the vector. */
    public double magnitude() {
        double sum = 0.0;
        for (int i = 0; i < size(); i++)
            sum += this.data[i] * this.data[i];
        return sum;
    }

    /**
     * @return a vector in the same direction as vec, but with unit magnitude.
     */
    public Vector normalize( ) {
        double len = this.magnitude();
        Vector unitVec = new Vector(data.length);
        for ( int i = 0; i < data.length; i++ ) {
            unitVec.set(i, data[i] / len);
        }
        return unitVec;
    }

    public int size() {
        return data.length;
    }

    private void checkDims(Vector b) {
       if (this.size() != b.size()) throw new IllegalArgumentException("Dimensions don't match");
    }

    /**
     * @return a string representation of the vector
     */
    public String toString() {
        String s = "";
        for (int i = 0; i < size(); i++) {
            s = s + data[i] + " ";
        }
        return s;
    }
}
