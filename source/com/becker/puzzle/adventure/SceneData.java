package com.becker.puzzle.adventure;

/**
 * @author Barry Becker Date: Apr 22, 2006
 */
public final class SceneData {


     private static final Scene[] SCENES = {
        new Scene("start",
                  "You are at the beginning. There is a haunted house in front of you.",
                  new Choice[]{
                      new Choice("open door", "inside entrance"),
                      new Choice("scream", "sky falls")
                  }),

        new Scene("inside entrance",
                  "You see the inside of a scary house. There is a door to your left, a door to your right, and stairs leading down. ",
                  new Choice[]{
                      new Choice("left door", "tiger room"),
                      new Choice("right door", "boogie"),
                      new Choice("stairs", "gold"),
                      new Choice("exit", "quit")
                  }),

        new Scene("tiger room",
                  "A Tiger jumps out and eats you.",
                  "finish"),

        new Scene("boogie",
                  "The boogy man jumps out and eats you.",
                  "finish"),

        new Scene("gold",
                  "You discover a pot of gold at the bottom.",
                  "finish"),

        new Scene("quit",
                  "You go home. have a nice day.",
                  "finish"),

        new Scene("sky falls",
                  "The sky falls on you.",
                  "finish"),

        new Scene("finish",
                  "The story is over.",
                  (String) null),
    };

    // private so instances cannot be made.
    private SceneData() {}

    public static Scene[] getScenes() {
        return  SCENES;
    }
}
