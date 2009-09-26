package com.becker.puzzle.adventure;

import org.w3c.dom.*;

import java.io.*;
import java.util.*;

/**
 * Run your own adventure story.
 * This version runs the adventure in text only mode.
 * @see Adventure
 *
 * @author Barry Becker
 */
public final class TextAdventure {


    /**
     * Adventure application entrance point.
     */
    public static void main( String[] args ) throws IOException {

        Document document = Story.retrieveStoryDocument(args);
       
        Story story = new Story(document);

        Scanner scanner = new Scanner(System.in);
        do {
            System.out.println(story.getCurrentScene());

            int c = -1;

            if (story.getCurrentScene().hasChoices())  {
                int nextInt = scanner.nextInt();
                c = nextInt - 1;
            }
            story.advanceScene(c);

        } while (!story.isOver());
    }
}



