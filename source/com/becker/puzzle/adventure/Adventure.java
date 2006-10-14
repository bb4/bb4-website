package com.becker.puzzle.adventure;

import com.becker.xml.*;
import com.becker.common.*;
import org.w3c.dom.*;

import java.io.*;
import java.util.*;

/**
 * Run your own adventure story.
 * Just modify the script in SceneData and run.
 *
 * This program is meant as a very simple example of how you can
 * approach creating a simple adventure game on a somputer.
 *
 * There are several obvious improvements that I will leave as an exercise for reader (Brian I hope).
 * 1) Keep track of the items tha the player has. They initially start with about 10 things but then find/use
 *   items as their adventure progresses.
 * 2) Automatic fighting with monsters. We know the hit points and armor class of the player and monster.
 *  It should be a simple matter to have the compat automatically carried out in order to determine the winner and
 *  supbract hit point losses as appropriate. We can also take into other effects like disease or healing effects.
 *  The player should also be given the option to flee, or instigate other action during the melee.
 * 3) Add a graphical User Interface so that the text will wrap nicely in a text area and we can show pictures
 *  for each scene. Furthermore we could have windows that pop up to show the players stats or item inventory.
 * 4) Make multi-player (hard)
 *
 * @author Barry Becker
 */
public class Adventure {

    private Scene[] scenes_;
    private Map sceneSet_;
    private Scene currentScene_;

    // as stack of current;y visited scenes. There may be duplicates if you visit the same scene twice.
    // If you backup, then we pop the stack.
    private LinkedList<Scene> visitedScenes_;

    public static final Scene TERMINAL_SCENE = new Scene(Choice.QUIT, "Goodbye", null);


    public Adventure(Document document) {
        Node root = document.getDocumentElement();  //DomUtil.getRootNode(document);
        NodeList children = root.getChildNodes();
        Scene[] scenes = new Scene[children.getLength()];
        for (int i=0; i < children.getLength(); i++) {
            //if (children.item(i).hasChildNodes())
            scenes[i] = new Scene(children.item(i));
        }
        initFromScenes(scenes);
    }

    /**
     * Construct an adventure given a list of scenes.
     * @param scenes
     */
    public Adventure(Scene[] scenes) {
        initFromScenes(scenes);
    }

    public void initFromScenes(Scene[] scenes)  {
        scenes_ = scenes;
        sceneSet_ = new HashMap(scenes_.length);
        for (final Scene scene : scenes) {
            if (scene.getChoices() == null) {
                scene.setChoices(new Choice[] {new Choice(Choice.QUIT, null)} ) ;
            }
            sceneSet_.put(scene.getName(), scene);
        }
        sceneSet_.put(TERMINAL_SCENE.getName(), TERMINAL_SCENE);

        verifyScenes();
        scenes_[0].setFirst();
        currentScene_ = scenes_[0];
        visitedScenes_ = new LinkedList<Scene>();
    }

    public Scene getCurrentScene()  {
        return currentScene_;
    }

    public Scene getLastScene() {
        return visitedScenes_.getLast();
    }

    public boolean isOver() {
       return getCurrentScene() == null;
    }


    public void advanceScene(int choice) {
        if (choice < 0) {
            currentScene_ = null;   // game over
            return;
        }
        String nextSceneName =  currentScene_.getNextSceneName(choice);
        if (nextSceneName != null) {
            if (nextSceneName.equals(Choice.QUIT))  {
                visitedScenes_.add(currentScene_);
                currentScene_ = null;
            }
            if (nextSceneName.equals(Choice.PREVIOUS_SCENE)) {
                currentScene_ = visitedScenes_.removeLast();
            }
            else {
                visitedScenes_.add(currentScene_);
                currentScene_ = (Scene) sceneSet_.get( nextSceneName );
                assert (currentScene_ != null)  : "Could not find a scene named '"+ nextSceneName+"'.";
            }
        }
    }


    /**
     * make sure the set of scenes in internally consistent.
     */
    private void verifyScenes() {
        for (Scene scene : scenes_) {
            for (Choice choice : scene.getChoices())  {
                if (choice.getDestination()!= null && sceneSet_.get(choice.getDestination()) == null) {
                    assert false : "No scene named "+ choice.getDestination();
                }
            }
        }
    }


    /**
     * Adventure application entrance point.
     */
    public static void main( String[] args ) throws IOException {

        //assert(args.length == 1) : "You must specify a script file as an argument (e.g. ludlowScript.xml).";
        File file = new File(Util.PROJECT_DIR + "source/com/becker/puzzle/adventure/ludlowScript.xml");       
        if (args.length == 1)
            file = new File(args[0]);

        Document document = DomUtil.parseXMLFile(file);
        //DomUtil.printTree(document, 0);

        //Adventure story = new Adventure(SceneData.getScenes());
        Adventure story = new Adventure(document);

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



