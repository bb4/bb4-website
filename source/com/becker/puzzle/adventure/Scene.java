package com.becker.puzzle.adventure;

import org.w3c.dom.*;
import com.becker.common.xml.*;
import com.becker.sound.SoundUtil;
import com.becker.ui.GUIUtil;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


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
    private List<Choice> choices_;
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
          Choice firstChoice = this.getChoices().get(0);
          if (firstChoice != Choice.QUIT_CHOICE) {
              Element choicesElem =  document.createElement("choices");
              sceneElem.appendChild(choicesElem);
              for (int i=0; i<this.getChoices().size()-1; i++) {
                  Choice choice = this.getChoices().get(i);
                  choicesElem.appendChild(choice.createElement(document));
              }
          }

          Element rootElement = document.getDocumentElement();
          rootElement.appendChild(sceneElem);
    }

    private void commonInit(String name, String text, List<Choice> choices, String resourcePath) {
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
    private List<Choice> getChoices(Node sceneNode) {
        List<Choice> choices = null;
        // if there are choices they will be the second element (right after description).
        NodeList children = sceneNode.getChildNodes();
        if (children.getLength() > 1) {
            Node choicesNode = children.item(1);
            NodeList choiceList = choicesNode.getChildNodes();
            int numChoices = choiceList.getLength();
            choices = new ArrayList<Choice>(numChoices + (isFirst()? 0 : 1));
            for (int i=0; i<numChoices; i++) {
                assert choiceList.item(i) != null;
                choices.add(new Choice(choiceList.item(i)));
            }
            if ( !isFirst() ) {
                choices.add(new Choice("Go back to last scene.", Choice.PREVIOUS_SCENE));
            }
        }
        
        return choices;
    }

    /**
     * @return  choices that will lead to the next scene.
     */
    public List<Choice> getChoices() {
        return choices_;
    }

    public void setChoices(List<Choice> choices) {
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

    public void setText(String text) {
        text_ = text;
    }

    /**
     * @param scene to see if parent
     * @return true if the specified scene is our immediate parent.
     */
    public boolean isParentOf(Scene scene) {
        String sName = scene.getName();
        assert sName != null;
        int numChoices = choices_.size();
        for (int i=0; i<numChoices-1; i++) {
            Choice choice = choices_.get(i);
            assert choice != null;
            assert choice.getDestination() != null;
            if (choice.getDestination().equals(sName)) {
                return true;
            }
        }
        return false;
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

        if (choice == choices_.size()) {
            return Choice.PREVIOUS_SCENE;
        }

        if (choice < 0 || choice > choices_.size()-1)
            return null;
        
        String destination = choices_.get(choice).getDestination();
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
            int len = choices_.size();
            for (int i=0; i < len; i++)  {
                buf.append((1+i) + ") " + choices_.get(i).getDescription() + '\n');
            }
        }
        return buf.toString();
    }
}
