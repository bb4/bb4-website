package com.barrybecker4.apps.misc.brian.cs2014projects;


import java.util.Scanner;

public class Fib {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        Scanner kbd = new Scanner(System.in);
        System.out.print("What place of the sequence would you like?");
        int x = kbd.nextInt();
        System.out.print(Fib(x, 0, 1));
    }

    public static int Fib(int place, int x, int y) {

        int temp;
        if (place <= 1)
            return y;
        temp = x;
        x = y;
        y = temp + y;

        return Fib(place - 1, x, y);
    }
}