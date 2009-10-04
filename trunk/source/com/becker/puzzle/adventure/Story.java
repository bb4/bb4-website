package com.becker.puzzle.adventure;

import com.becker.common.xml.DomUtil;
import com.becker.ui.GUIUtil;
import java.io.File;
import java.net.URL;
import org.w3c.dom.*;

import java.util.*;

/**
 * Run your own adventure story.
 * Just modify the script in SceneData and run.
 *
 * This program is meant as a very simple example of how you can
 * approach creating a simple adventure game on a somputer.
 *
 * There are many improvements that  I will leave as an exercise for reader (Brian I hope).
 * Next to each is a number which is the number of hours I expect it would take me to implement.
 *
 * 1) Keep track of the items that the player has. They initially start with about 10 things but then find/use
 *   items as their adventure progresses.
 * 2) Automatic fighting with monsters. We know the hit points and armor class of the player and monster.
 *  It should be a simple matter to have the compat automatically carried out in order to determine the winner and
 *  supbract hit point losses as appropriate. We can also take into other effects like disease or healing effects.
 *  The player should also be given the option to flee, or instigate other action during the melee.
 * 3) Add a graphical User Interface so that the text will wrap nicely in a text area and we can show pictures
 *  for each scene. Furthermore we could have windows that pop up to show the players stats or item inventory.
 * 4) Make multi-player (hard)
 * 5) Create a user interface instead of text only mode. Support both.
 * 6) Add pictures and sound. The pictures and sound should be kept in a /resources directory
 *  and be loaded by convention. The name of the resource could correspond to the scene name.
 *  The alternative is to explicitly name the resource in the xml file.
 * 7) Allow the user to edit the scenes - live through the UI. Perhaps the edit mode could be
 *  password protected. When editing a scene you are prsented with a form that has all the
 * attributes for the scene including a dropdown for selecting which scenes navigate to it and
 * where you can navigate to from this scene. Save and load the xml that defines the game.
 * 8) This type of application could be used for more than just games. Tutorials or an expert system would
 * be other nice applicaitons.
 * 9) Have probabalistic choices. For example, if you encounter a monster and choose to fight it, then
 * the outcome may be one of several different things. We can also influence the outcome by what sort of
 * items the player has.
 * 10) fix sound deploy in ant
 *
 * @author Barry Becker
 */
public class Story {

    /** title of the story */
    private String title;

    /** maps scene name to the scene */
    private Map<String, Scene> sceneMap_;

    private Scene currentScene_;

    /**
     * A stack of currently visited scenes. There may be duplicates if you visit the same scene twice.
     * If you backup, then we pop the stack.
     */
    private LinkedList<Scene> visitedScenes_;

    /** all the stories need to be stored at this location */
    public static final String STORIES_ROOT = "com/becker/puzzle/adventure/stories/";
   
    /**
     * Construct an adventure given an xml document object
     * @param document containing the scene data
     */
    public Story(Document document) {
        Node root = document.getDocumentElement();
        title = DomUtil.getAttribute(root, "title");
        String resourcePath = STORIES_ROOT + DomUtil.getAttribute(root, "name")  + "/";
        NodeList children = root.getChildNodes();
        Scene[] scenes = new Scene[children.getLength()];
        for (int i=0; i < children.getLength(); i++) {
            scenes[i] = new Scene(children.item(i), resourcePath);
        }
        initFromScenes(scenes);
    }

    /** @return the title of the story */
    public String getTitle() {
        return title;
    }
     

    /**
     * If args[0] does not have the name of the document to use, use a default.
     * @param args command line args (0 or 1 if name of xml doc is specified.)
     * @return the loaded Document that contains the asventure.
     */
    public static Document retrieveStoryDocument(String[] args) {
        Document document = null;
        if (args.length == 1) {
            File file = new File(args[0]);
            document = DomUtil.parseXMLFile(file);
        }
        else { // default
            URL url = GUIUtil.getURL("com/becker/puzzle/adventure/stories/ludlow/ludlowScript.xml");
            System.out.println("about to parse url="+url +"\n plugin file location="+ url);
            document = DomUtil.parseXML(url);
        }
        //DomUtil.printTree(document, 0);

        return document;
    }

    /**
     * Construct an adventure given a list of scenes.
     * @param scenes
     */
    public Story(Scene[] scenes) {
        initFromScenes(scenes);
    }

    public void initFromScenes(Scene[] scenes)  {

        sceneMap_ = new HashMap<String, Scene>(scenes.length);
        for (final Scene scene : scenes) {
            if (scene.getChoices() == null) {
                scene.setChoices(new Choice[] {new Choice(Choice.QUIT, null)} ) ;
            }
            sceneMap_.put(scene.getName(), scene);
        }

        verifyScenes();
        scenes[0].setFirst();
        currentScene_ = scenes[0];
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


    /**
     * Advance the story to the next scene based on the specified choice
     * @param choice index of the selected choice.
     */
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
                currentScene_ = sceneMap_.get( nextSceneName );
                assert (currentScene_ != null)  : "Could not find a scene named '"+ nextSceneName+"'.";
            }
        }
    }

    /**
     * make sure the set of scenes in internally consistent.
     */
    private void verifyScenes() {
        for (Scene scene : sceneMap_.values()) {
            for (Choice choice : scene.getChoices())  {
                String dest = choice.getDestination();
                if (dest != null && !Choice.PREVIOUS_SCENE.equals(choice.getDestination())
                       &&  sceneMap_.get(choice.getDestination()) == null) {
                    assert false : "No scene named "+ choice.getDestination();
                }
            }
        }
    }

}

