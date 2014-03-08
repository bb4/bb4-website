package com.barrybecker4.apps.misc.brian.cs2014projects;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * @author Period 3
 */
public class Populate {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        ArrayList<Integer> myStrings = new ArrayList<Integer>();
        Scanner kbd = new Scanner(System.in);

        System.out.print("what range integers would you like?");
        int n = kbd.nextInt();
        System.out.print("how many numbers do you want?");

        int size = kbd.nextInt();
        for (int i = 0; i < size; i++) {

            int num = (int) (Math.random() * n + 1);
            myStrings.add(num);
        }
        for (int i = 0; i < size; i++) System.out.print(myStrings.get(i) + " ,");
    }

}