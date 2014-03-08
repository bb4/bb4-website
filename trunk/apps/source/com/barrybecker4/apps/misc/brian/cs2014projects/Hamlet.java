package com.barrybecker4.apps.misc.brian.cs2014projects;

import java.io.*;

/**
 * @author Period 3
 */
public class Hamlet {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        try {
            FileOutputStream s = new FileOutputStream("test.txt");
            PrintWriter p = new PrintWriter(s, true);
            p.println("Here comes 10 random #'s ");

            for (int i = 0; i < 10; i++)
            {
                p.println(1 + (int) (Math.random() * 10));
            }

            s.close();

        }
        catch (IOException e)
        {
            System.out.println("Error opening file");
        }
    }
}