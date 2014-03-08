package com.barrybecker4.apps.misc.brian.cs2014projects;

public class Teddy {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        int x;

        for (int i = 42; i < 1000; i++) {
            x = bears(i);
            if (x == 42)
                System.out.println("bears(" + i + ")");
        }
    }

    public static int bears(int n) {

        if (n % 3 == 0 || n % 4 == 0) return (n % 10) * (n % 100);
        else if (n % 2 == 0) return bears(n / 2);
        else if (n % 5 == 0) return n - 42;
        return 0;
    }

}