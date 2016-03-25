package com.barrybecker4.apps.misc.codeforces.problem584A;

/**
 * @author Sally Poon
 * @author Marisa Aquilina
 */
public class Problem  implements ProblemSolver
{

    public static int solve(int n,int t)
    {

        if ((n <= 1) || (n >= 100))
        {
            throw new IllegalArgumentException("n isn't between the argument");
        }
        if ( (t <= 1) || (t >=10))
        {
            throw new IllegalArgumentException("t isn't between the argument");
        }
       int fromlength= (int) Math.pow(10,n-1);
       int tolength= (int) Math.pow(10,n);
        for (int i = fromlength; i < tolength; i++)
        {
            if (i % t == 0) {
                return i;
            }
        }
        return -1;
    }

    public String findSolution(int n, int t)
    {
        return Integer.toString(Problem.solve(n, t));
    }


    public static void main (String [] args)
    {
        int n= Integer.parseInt(args[0]);
        int t= Integer.parseInt(args[1]);
        int result=Problem.solve(n,t);
        System.out.println("The result of " + n+ " , "+ t + " is " + result);
    }
}