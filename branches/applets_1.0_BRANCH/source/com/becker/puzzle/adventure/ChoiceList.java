package com.becker.puzzle.adventure;

import com.becker.common.util.OrderedMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.*;

/**
 * A choice that you can make in a scene.
 *
 * @author Barry Becker
 */
public class ChoiceList implements List<Choice> {

    private List<Choice> choices_;


    /**
     * Default constructor.
     */
    public ChoiceList() {
        choices_ = new ArrayList<Choice>();
    }

    /**
     * Constructor.
     * @param scene use the choices from this scene to initialize from.
     */
    public ChoiceList(Scene scene) {
        this();
        choices_.addAll(scene.getChoices());
    }

    /**
     * Constructor.
     * Create an initialized choice list.
     * @param sceneNode to initalize from.
     * @param isFirst true if this is the first scene.
     */
    public ChoiceList(Node sceneNode, boolean isFirst) {
        
        // if there are choices they will be the second element (right after description).
        NodeList children = sceneNode.getChildNodes();
        if (children.getLength() > 1) {
            Node choicesNode = children.item(1);
            NodeList choiceList = choicesNode.getChildNodes();
            int numChoices = choiceList.getLength();
            choices_ = new ArrayList<Choice>(numChoices + (isFirst? 0 : 1));
            for (int i=0; i<numChoices; i++) {
                assert choiceList.item(i) != null;
                choices_.add(new Choice(choiceList.item(i)));
            }
        } else {
            choices_ = new ArrayList<Choice>();
        }
    }

    /**
     * @param sceneName  sceneName to look for as a destination.
     * @return true if sceneName is one of our choices.
     */
    public boolean isDestination(String sceneName) {
        for (Choice c : choices_) {
            if (c.getDestination().equals(sceneName)) {
                return true;
            }
        }
        return false;
    }

    public void sceneNameChanged(String oldSceneName, String newSceneName) {
        for (Choice c : choices_) {
            if (c.getDestination().equals(oldSceneName)) {
                c.setDestination(newSceneName);
            }
        }
    }

    /**
     * update the order and descriptions
     * @param choiceMap new order and descriptions to update with.
     */
    public void update(OrderedMap<String, String> choiceMap)  {
        assert choiceMap.size() == choices_.size() :
                "choiceMap.size()=" + choiceMap.size() + " not equal choices_.size()=" + choices_.size();
        List<Choice> newChoices = new ArrayList<Choice>(choiceMap.size());
        for (String dest : choiceMap.keyList()) {
            newChoices.add(new Choice(choiceMap.get(dest), dest));
        }
        choices_ = newChoices;
    }

    public int size() {
        return choices_.size();
    }

    public boolean isEmpty() {
        return choices_.isEmpty();
    }

    public boolean contains(Object o) {
        return choices_.contains(o);
    }

    public Iterator<Choice> iterator() {
        return choices_.iterator();
    }

    public Object[] toArray() {
       return choices_.toArray();
    }

    public boolean add(Choice choice) {
        return choices_.add(choice);
    }

    public boolean remove(Object o) {
        return choices_.remove(o);
    }

    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void clear() {
        choices_.clear();
    }

    public Choice get(int index) {
        return choices_.get(index);
    }

    public Choice set(int index, Choice element) {
        return  choices_.set(index, element);
    }

    public void add(int index, Choice element) {
        choices_.add(index, element);
    }

    public Choice remove(int index) {
        return choices_.remove(index);
    }

    public int indexOf(Object o) {
        return choices_.indexOf(o);
    }

    // unsupported stuff. implement only if needed.
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    public boolean addAll(Collection<? extends Choice> c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    public boolean addAll(int index, Collection<? extends Choice> c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    public int lastIndexOf(Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    public ListIterator<Choice> listIterator() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    public ListIterator<Choice> listIterator(int index) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    public List<Choice> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}