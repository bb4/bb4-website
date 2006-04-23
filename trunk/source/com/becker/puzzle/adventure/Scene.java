package com.becker.puzzle.adventure;



/**
 * @author Barry Becker Date: Apr 22, 2006
 */
public class Scene {

    private String name_;
    private String text_;
    private Choice[] choices_;

    public Scene(String name, String text, Choice[] choices) {
        name_ = name;
        text_ = text;
        choices_ = choices;
    }

    public Scene(String name, String text, String destination) {
        this(name, text, new Choice[]{new Choice("", destination)});
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

    /**
     *
     * @param choice
     * @return the name of the next scene given the number of the choice.
     */
    public String getNextSceneName(int choice) {
        if (choices_ == null || choice < 0 || choice > choices_.length-1)
            return null;
        return choices_[choice].getDestination();
    }

    /**
     *
     * @return true if there are more than one coice for the user to select from.
     */
    public boolean hasChoices() {
         return choices_ != null && choices_.length > 1;
    }

    /**
     * @return the text and choices.
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append('\n' + this.getText() +'\n');
        if (choices_ != null && choices_.length > 1) {
            for (int i=0; i < choices_.length; i++)  {
                buf.append((1+i) + ") " + choices_[i].getDescription() + '\n');
            }
        }
        return buf.toString();
    }
}
