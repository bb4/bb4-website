package com.becker.puzzle.adventure;



/**
 * Every scene has some text_ which describes the scene and a list of
 * choices which the actor chooses from to decide what to do next.
 * There is a "Return to last scene" choice automatically appened to all list of choices.
 * @author Barry Becker Date: Apr 22, 2006
 */
public class Scene {

    private String name_;
    private String text_;
    private Choice[] choices_;
    private boolean isFirst_;


    public Scene(String name, String text, Choice[] choices) {
        name_ = name;
        text_ = text;
        choices_ = choices;
    }

    /**
     * use this constructor if this is a terminal scene. (i.e. no choices)
     * @param name
     * @param text
     */
    public Scene(String name, String text) {
        this(name, text, new Choice[]{new Choice("Quit", Choice.QUIT)});
    }


    /**
     * @return the name of the scene
     */
    public String getName() {
        return name_;
    }

    /**
     *
     * @return some text that describes the scene.
     */
    public String getText() {
        return text_;
    }

    /**
     *
     * @return  choices that will lead to the next scene.
     */
    public Choice[] getChoices() {
        return choices_;
    }

    public void setChoices(Choice[] choices) {
        choices_ = choices;
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
        if (choices_ == null || choice < 0 || choice > choices_.length-1)
            return null;
        return choices_[choice].getDestination();
    }

    /**
     *
     * @return true if there are more than one coice for the user to select from.
     */
    public boolean hasChoices() {
        return choices_ != null;
    }

    /**
     * @return the text and choices.
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append('\n' + this.getText() +'\n');

        if (choices_ != null) {
            int len = choices_.length;
            for (int i=0; i < len; i++)  {
                buf.append((1+i) + ") " + choices_[i].getDescription() + '\n');
            }
            if ( !isFirst())
                buf.append((len + 1) + ") Go back to last scene.");
        }
        return buf.toString();
    }
}
