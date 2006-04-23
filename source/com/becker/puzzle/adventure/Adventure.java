package com.becker.puzzle.adventure;

import java.io.*;
import java.util.*;

/**
 * Run your own adventure story.
 * Just modify the script in SceneData and run.
 *
 * @author Barry Becker
 */
public class Adventure {

    private Scene[] scenes_;
    private Map sceneSet_;
    private Scene currentScene_;

    /**
     * Construct an adventure given a list of scenes.
     * @param scenes
     */
    public Adventure(Scene[] scenes) {
        scenes_ = scenes;
        sceneSet_ = new HashMap(scenes_.length);
        for (final Scene scene : scenes) {
            sceneSet_.put(scene.getName(), scene);
        }
        verifyScenes();
        currentScene_ = scenes_[0];
    }

    public Scene getCurrentScene()  {
        return currentScene_;
    }

    public boolean isOver() {
       return getCurrentScene() == null;

    }


    public void advanceScene(int choice) {
        String nextSceneName =  currentScene_.getNextSceneName(choice);
        if (nextSceneName != null) {
            currentScene_ = (Scene) sceneSet_.get( nextSceneName );
            if (currentScene_ == null) {
                System.out.println("Could not find a scene named "+ nextSceneName);
            }
        } else {
            currentScene_ = null;
        }
    }


    private void verifyScenes() {
        for (Scene scene : scenes_) {
            for (Choice choice : scene.getChoices())  {
                if (choice.getDestination()!= null && sceneSet_.get(choice.getDestination()) == null) {
                    System.out.println("Could not find scene named: "+ choice.getDestination());
                    assert false : "No scene named "+ choice.getDestination();
                }
            }
        }
    }


    /**
     * Adventure application entrance point.
     */
    public static void main( String[] args ) throws IOException {

        Adventure story = new Adventure(SceneData.getScenes());

        Scanner scanner = new Scanner(System.in);
        do {
            System.out.println(story.getCurrentScene());

            int c = 0;
            if (story.getCurrentScene().hasChoices())  {
                c = scanner.nextInt() - 1;
            }
            story.advanceScene(c);

        } while (!story.isOver());

    }
}



