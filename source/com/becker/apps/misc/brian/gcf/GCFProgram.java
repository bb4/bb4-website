package com.becker.apps.misc.brian.gcf;

/**
 * Compare two different ways to find the Greatest common factor of two numbers.
 */
public final class GCFProgram {

    private static final GCFSolver bruteSolver = new BruteGCFSolver();
    private static final GCFSolver euclidSolver = new EuclidGCFSolver();


    public static void main( String[] args ) {

        // this will take about a minute for the brute force approach.
        long a = 2342343454L;
        long b = 456787697786L;
        //long a = 36618;
        //long b = 8105362;

        System.out.println("Finding Greatest Common Factor of a=" + a + " and b="+ b);

        showResult(a, b, euclidSolver);
        showResult(a, b, bruteSolver);
    }


    private static void showResult(long a, long b, GCFSolver solver) {

        System.out.println("finding answer using "+ solver.getClass().getName() + " ... ");

        long time = System.currentTimeMillis();
        long answer = solver.findSolution(a, b);

        long elapsedTime = (System.currentTimeMillis() - time);

        System.out.println("found answer = "+ answer +" in time = "+ elapsedTime + " miliseconds\n");
    }
}
