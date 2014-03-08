package com.barrybecker4.apps.misc.brian.cs2014projects;

/**
 * @author Period 3
 */
public class Ackermann {

    public int Ackermann(int m, int n) {

        if (m == 0)
        {
            return n + 1;
        }
        else if (n == 0)
        {
            return Ackermann(m - 1, 1);
        } else
        {
            return Ackermann(m - 1, Ackermann(m, n - 1));
        }

    }

    public static void main(String[] args)
    {
        Ackermann c = new Ackermann();
        System.out.println(c.Ackermann(3, 3));
    }

}