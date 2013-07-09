package com.barrybecker4.ui.xmlviewer;

import org.w3c.dom.Document;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.LinkedList;
import java.util.List;

/**
 * This adapter converts the current Document (a DOM) into
 * a JTree model.
 */
public class DomToTreeModelAdapter implements TreeModel {

    private Document document_;

    private List<TreeModelListener> listenerList_ = new LinkedList<TreeModelListener>();


    public DomToTreeModelAdapter(Document document) {
        document_ = document;
    }

    // Basic TreeModel operations
    @Override
    public Object getRoot() {
        return new AdapterNode(document_);
    }

    /**
     * Determines whether the icon shows up to the left.
     * @param node node to check to see if leaf
     * @return  true for any node with no children
     */
    @Override
    public boolean isLeaf(Object node) {
        AdapterNode n = (AdapterNode) node;
        return n.childCount() <= 0;
    }

    @Override
    public int getChildCount(Object parent) {
        AdapterNode node = (AdapterNode) parent;
        return node.childCount();
    }

    @Override
    public Object getChild(Object parent, int index) {
        AdapterNode node = (AdapterNode) parent;
        return node.child(index);
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        AdapterNode node = (AdapterNode) parent;
        return node.index((AdapterNode) child);
    }

    /**
     * We won't be making changes in the GUI
     * If we did, we would ensure the new value was really new,
     * adjust the model, and then fire a TreeNodesChanged event.
     * @param path
     * @param newValue
     */
    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {}

    /*
     * Use these methods to add and remove event listeners.
     * (Needed to satisfy TreeModel interface, but not used.)
     */
    @Override
    public void addTreeModelListener(TreeModelListener listener) {
        if ( listener != null  && ! listenerList_.contains( listener ) ) {
           listenerList_.add( listener );
        }
    }

    @Override
    public void removeTreeModelListener(TreeModelListener listener) {
        if ( listener != null ) {
           listenerList_.remove( listener );
        }
    }

    /**
     * Invoke these methods to inform listeners of changes.
     * (Not needed for this example.)
     * Methods taken from TreeModelSupport class described at
     *   http://java.sun.com/products/jfc/tsc/articles/jtree/index.html
     * That architecture (produced by Tom Santos and Steve Wilson)
     * is more elegant.
     */
    public void fireTreeNodesChanged( TreeModelEvent e ) {
        for ( TreeModelListener listener : listenerList_ ) {
          listener.treeNodesChanged( e );
        }
    }

    public void fireTreeNodesInserted( TreeModelEvent e ) {
        for ( TreeModelListener listener : listenerList_ ) {
          listener.treeNodesInserted( e );
        }
    }

    public void fireTreeNodesRemoved( TreeModelEvent e ) {
        for ( TreeModelListener listener : listenerList_ ) {
          listener.treeNodesRemoved( e );
        }
    }

    public void fireTreeStructureChanged( TreeModelEvent e ) {
        for ( TreeModelListener listener : listenerList_ ) {
          listener.treeStructureChanged( e );
        }
    }
}