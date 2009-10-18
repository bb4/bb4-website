package com.becker.puzzle.adventure;

import org.w3c.dom.*;
import com.becker.common.xml.*;
import com.becker.sound.SoundUtil;
import com.becker.ui.GUIUtil;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;


/**
 * Every scene has some text_ which describes the scene and a list of
 * choices which the actor chooses from to decide what to do next.
 * There is a "Return to last scene" choice automatically appened to all list of choices.
 * A scene may also have an associated sound and image.
 *
 * @author Barry Becker Date: Apr 22, 2006
 */
public class Scene {

    private String name_;
    private String text_;
    private Choice[] choices_;
    private boolean isFirst_;
    private URL soundURL_;
    private BufferedImage image_;


    /**
     *
     * @param sceneNode
     * @param resourcePath
     */
    public Scene(Node sceneNode, String resourcePath) {
        String description = sceneNode.getFirstChild().getTextContent();

        commonInit(DomUtil.getAttribute(sceneNode, "name"),
                  description, getChoices(sceneNode), resourcePath);
    }

    /**
     *
     * @param document the document to which to append this scene as a child.
     */
    public void appendToDocument(Document document) {

          Element sceneElem = document.createElement("scene");
          sceneElem.setAttribute("name", getName());
          Element descElem = document.createElement("description");
          descElem.setTextContent(getText());
          sceneElem.appendChild(descElem);

          // if we only have the QUIT choice, don't bother to add the choice list.
          Choice firstChoice = this.getChoices()[0];
          if (firstChoice != Choice.QUIT_CHOICE) {
              Element choicesElem =  document.createElement("choices");
              sceneElem.appendChild(choicesElem);
              for (int i=0; i<this.getChoices().length-1; i++) {
                  Choice choice = this.getChoices()[i];
                  choicesElem.appendChild(choice.createElement(document));
              }
          }

          Element rootElement = document.getDocumentElement();
          rootElement.appendChild(sceneElem);
    }

    private void commonInit(String name, String text, Choice[] choices, String resourcePath) {
        name_ = name;
        text_ = text;
        choices_ = choices;

        try {
            String soundPath = resourcePath + "sounds/" + name + ".au";
            soundURL_ = GUIUtil.getURL(soundPath, false);

            String imagePath = resourcePath + "images/" +name + ".jpg";
            image_ = GUIUtil.getBufferedImage(imagePath);
        } catch (NoClassDefFoundError e) {
            System.out.println("You are trying to load sounds and images when text only scenes are supported. " +
                    "If you need this to work add the jai library to your classpath");
        }
    }

    /**
     * @param sceneNode scene to get choices for.
     * @return the coices for the scpecified scene.
     */
    private Choice[] getChoices(Node sceneNode) {
        Choice[] choices = null;
        // if there are choices they will be the second element (right after description).
        NodeList children = sceneNode.getChildNodes();
        if (children.getLength() > 1) {
            Node choicesNode = children.item(1);
            NodeList choiceList = choicesNode.getChildNodes();
            int numChoices = choiceList.getLength();
            choices = new Choice[numChoices + (isFirst()?0:1)];
            for (int i=0; i<numChoices; i++) {
                assert choiceList.item(i) != null;
                choices[i] = new Choice(choiceList.item(i));
            }
            if ( !isFirst() ) {
                choices[numChoices] = new Choice("Go back to last scene.", Choice.PREVIOUS_SCENE);
            }
        }
        
        return choices;
    }

    /**
     * @return  choices that will lead to the next scene.
     */
    public Choice[] getChoices() {
        return choices_;
    }

    public void setChoices(Choice[] choices) {
        choices_ = choices;
    }

    /**
     * @return the name of the scene
     */
    public String getName() {
        return name_;
    }

    /**
     * @return some text that describes the scene.
     */
    public String getText() {
        return text_;
    }

    /**
     * @return image associated with this scene if there is one (else null)
     */
    public BufferedImage getImage() {
         return image_;
    }

    public boolean hasSound() {
        if (soundURL_ == null) return false;
        File file = new File(soundURL_.getFile());
        return file.exists();
    }


    public void playSound() {
        if (hasSound()) {
             SoundUtil.playSound(soundURL_);
        }
    }

    public void setFirst() {
        isFirst_ = true;
    }

    public boolean isFirst() {
        return isFirst_;
    }

    /**
     *
     * @param choice
     * @return the name of the next scene given the number of the choice.
     */
    public String getNextSceneName(int choice) {

        if (choice == choices_.length) {
            return Choice.PREVIOUS_SCENE;
        }

        if (choice < 0 || choice > choices_.length-1)
            return null;
        
        String destination = choices_[choice].getDestination();
        if (destination == null)
        {
            System.out.println("Goodbye");
            System.exit(0);
        }
        return destination;
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

    /**
     * @return the text and choices.
     */
    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append('\n' + this.getText() +'\n');

        if (choices_ != null) {
            int len = choices_.length;
            for (int i=0; i < len; i++)  {
                buf.append((1+i) + ") " + choices_[i].getDescription() + '\n');
            }
        }
        return buf.toString();
    }
}
