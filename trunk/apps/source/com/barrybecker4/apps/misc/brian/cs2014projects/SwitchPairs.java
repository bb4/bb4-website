package com.barrybecker4.apps.misc.brian.cs2014projects;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * @author Period 3
 */
public class SwitchPairs {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        ArrayList<String> myStrings = new ArrayList<String>();
        String done = "balls";
        Scanner kbd = new Scanner(System.in);
        String word;
        String temp, temp2;

        while (!done.equals("stop")) {
            word = kbd.nextLine();
            if (!word.equals("stop")) myStrings.add(word);
            done = word;
        }

        for (int n = 0; n <= myStrings.size() - 2; n = n + 2) {
            temp = myStrings.get(n);
            temp2 = myStrings.get(n + 1);
            myStrings.set(n, temp2);
            myStrings.set(n + 1, temp);
            System.out.println(myStrings);
        }
    }
}