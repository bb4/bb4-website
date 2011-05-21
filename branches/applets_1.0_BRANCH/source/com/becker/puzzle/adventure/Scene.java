package com.becker.puzzle.adventure;

import com.becker.common.xml.DomUtil;
import com.becker.sound.SoundUtil;
import com.becker.ui.GUIUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.awt.image.BufferedImage;
import java.net.URL;


/**
 * Every scene has a name_, some text_ which describes the scene. and a list of
 * choices which the actor chooses from to decide what to do next.
 * There is a "Return to last scene" choice automatically appened to all list of choices.
 * A scene may also have an associated sound and image.
 *
 * @author Barry Becker
 */
public class Scene {

    private String name_;
    private String text_;
    private ChoiceList choices_;
    private boolean isFirst_;
    private URL soundURL_;
    private BufferedImage image_;

    /**
     * @param sceneNode  xml element to initialize from.
     * @param resourcePath where we can find images and sounds.
     */
    public Scene(Node sceneNode, String resourcePath, boolean isFirst) {
        String description = sceneNode.getFirstChild().getTextContent();

        isFirst_= isFirst;
        commonInit(DomUtil.getAttribute(sceneNode, "name"),
                  description, new ChoiceList(sceneNode, isFirst()), resourcePath);
    }

    /**
     * Copy constructor.
     * @param scene the scene to initialize from.
     */
    public Scene(Scene scene) {
        this.name_ = scene.getName();
        this.text_ = scene.getText();
        this.image_ = scene.getImage();
        this.soundURL_ = scene.soundURL_;
        this.choices_ = new ChoiceList(scene);
        this.isFirst_ = scene.isFirst();
    }

    /**
     * Constructor for a simple new scene with no media or initial choices
     */
    public Scene(String name, String text, String resourcePath) {
        ChoiceList choices = new ChoiceList();
        commonInit(name, text, choices, resourcePath);
    }

    /**
     * @param document the document to which to append this scene as a child.
     */
    public void appendToDocument(Document document) {

        Element sceneElem = document.createElement("scene");
        sceneElem.setAttribute("name", getName());
        Element descElem = document.createElement("description");
        descElem.setTextContent(getText());
        sceneElem.appendChild(descElem);

        Element choicesElem =  document.createElement("choices");
        sceneElem.appendChild(choicesElem);
        for (int i = 0; i < getChoices().size(); i++) {
            Choice choice = getChoices().get(i);
            choicesElem.appendChild(choice.createElement(document));
        }

        Element rootElement = document.getDocumentElement();
        rootElement.appendChild(sceneElem);
    }

    private void commonInit(String name, String text, ChoiceList choices, String resourcePath) {
        name_ = name;
        text_ = text;
        choices_ = choices;
        loadResources(name, resourcePath);
    }

    private void loadResources(String name, String resourcePath) {
        try {
            String soundPath = resourcePath + "sounds/" + name + ".au";
            soundURL_ = GUIUtil.getURL(soundPath, false);

            String imagePath = resourcePath + "images/" +name + ".jpg";
            image_ = GUIUtil.getBufferedImage(imagePath);
        } catch (NoClassDefFoundError e) {
            System.err.println("You are trying to load sounds and images when text only scenes are supported. " +
                    "If you need this to work add the jai library to your classpath");
        }
    }

    /**
     * @return  choices that will lead to the next scene.
     */
    public ChoiceList getChoices() {
        return choices_;
    }

    public void deleteChoice(int choice) {
        choices_.remove(choice);
    }

    /**
     * @return the name of the scene
     */
    public String getName() {
        return name_;
    }

    /**
     * When changing the name we must call sceneNameChanged on the listeners that are interested in the change.
     * @param name new scene name
     */
    public void setName(String name) {
        name_ = name;
    }

    /**
     * @return some text that describes the scene.
     */
    public String getText() {
        return text_;
    }

    public void setText(String text) {
        text_ = text;
    }

    /**
     * @param scene to see if parent
     * @return true if the specified scene is our immediate parent.
     */
    public boolean isParentOf(Scene scene) {
        String sName = scene.getName();
        return choices_.isDestination(sName);
    }

    /**
     * @return image associated with this scene if there is one (else null)
     */
    public BufferedImage getImage() {
         return image_;
    }

    public boolean hasSound() {
        if (soundURL_ == null) return false;
        //File file = new File(soundURL_.getFile());
        //return file.exists();
        return true;
    }

    public void playSound() {
        if (hasSound()) {
             SoundUtil.playSound(soundURL_);
        }
    }

    boolean isFirst() {
        return isFirst_;
    }

    /**
     *
     * @param choice navigate to the scene indicated by this choice.
     * @return the name of the next scene given the number of the choice.
     */
    public String getNextSceneName(int choice) {

        assert choice >= 0 || choice < choices_.size();

        return choices_.get(choice).getDestination();
    }

    /**
     * @return true if there are more than one coice for the user to select from.
     */
    public boolean hasChoices() {
        return choices_ != null;
    }

    /**
     * Prints what is missing if anything for this scene.
     * @return false if something is missing.
     */
    public boolean verifyMedia() {
        if (getImage() == null || !hasSound()) {
            System.out.print("scene: " + getName() );
            if (getImage() == null)
                System.out.print("  missing image");
            if (!hasSound())
                System.out.print("  missing sound" );
            System.out.println("");
            return false;
       }
        return true;
    }

    public String print() {
        StringBuilder buf = new StringBuilder();
        buf.append('\n').append(this.getText()).append('\n');

        if (choices_ != null) {
            int len = choices_.size();
            for (int i=0; i < len; i++)  {
                buf.append(1 + i).append(") ").append(choices_.get(i).getDescription()).append('\n');
            }
        }
        return buf.toString();
    }

    /**
     * @return the text and choices.
     */
    @Override
    public String toString() {
        
        return this.getName();
    }
}