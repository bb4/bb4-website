package com.barrybecker4.apps.misc.brian.cs2014projects;

import java.util.Scanner;

public class Dice {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        Scanner kbd = new Scanner(System.in);
        int sides;
        int count = 0;

        while (count < 10000) {
            sides = kbd.nextInt();
            System.out.print(Dice(sides));
            count++;
            System.out.println(" count: " + count);
        }
    }

    public static int Dice(int size) {
        return (int) (Math.random() * size + 1);
    }
}