package com.becker.puzzle.adventure;

import org.w3c.dom.*;

import java.io.*;
import java.util.*;

/**
 * Run your own adventure story.
 * This version runs the adventure in text only mode.
 * @see com.becker.puzzle.adventure.ui.GraphicalAdventure
 *
 * @author Barry Becker
 */
public final class TextAdventure {


    /**
     * Adventure application entrance point.
     */
    public static void main( String[] args ) throws IOException {

        Document document = Story.importStoryDocument(args);
       
        Story story = new Story(document);

        Scanner scanner = new Scanner(System.in);
        do {

            Scene currentScene = story.getCurrentScene();
            System.out.println(currentScene.print());

            int nextSceneIndex = getNextSceneIndex(currentScene, scanner);
            
            story.advanceScene(nextSceneIndex);

        } while (!story.isOver());
    }


    /**
     * Retrieve the selection from the player using the scanner.
     * @return the next scene to advance to.
     */
    private static int getNextSceneIndex(Scene scene, Scanner scanner) {

        int sceneIndex = -1;

        if (scene.hasChoices())  {
            int nextInt = scanner.nextInt();
            sceneIndex = nextInt - 1;
        }
        return sceneIndex;
    }
}



