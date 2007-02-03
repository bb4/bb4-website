package com.becker.xml;

import org.w3c.dom.Document;

import javax.swing.tree.*;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeModelEvent;
import java.util.*;

    /**
     * This adapter converts the current Document (a DOM) into
     * a JTree model.
     */
    public class DomToTreeModelAdapter implements TreeModel
    {

      private Document document_;

      private List<TreeModelListener> listenerList_ = new LinkedList<TreeModelListener>();


      public DomToTreeModelAdapter(Document document) {
          document_ = document;
      }

      // Basic TreeModel operations
      public Object  getRoot() {
        //System.err.println("Returning root: " +document);
        return new AdapterNode(document_);
      }

      public boolean isLeaf(Object aNode) {
        // Determines whether the icon shows up to the left.
        // Return true for any node with no children
        AdapterNode node = (AdapterNode) aNode;
        if (node.childCount() > 0) return false;
        return true;
      }

      public int getChildCount(Object parent) {
        AdapterNode node = (AdapterNode) parent;
        return node.childCount();
      }

      public Object getChild(Object parent, int index) {
        AdapterNode node = (AdapterNode) parent;
        return node.child(index);
      }

      public int getIndexOfChild(Object parent, Object child) {
        AdapterNode node = (AdapterNode) parent;
        return node.index((AdapterNode) child);
      }

      public void valueForPathChanged(TreePath path, Object newValue) {
        // Null. We won't be making changes in the GUI
        // If we did, we would ensure the new value was really new,
        // adjust the model, and then fire a TreeNodesChanged event.
      }

      /*
       * Use these methods to add and remove event listeners.
       * (Needed to satisfy TreeModel interface, but not used.)
       */

      public void addTreeModelListener(TreeModelListener listener) {
        if ( listener != null
        && ! listenerList_.contains( listener ) ) {
           listenerList_.add( listener );
        }
      }

      public void removeTreeModelListener(TreeModelListener listener) {
        if ( listener != null ) {
           listenerList_.remove( listener );
        }
      }

      /*
       * Invoke these methods to inform listeners of changes.
       * (Not needed for this example.)
       * Methods taken from TreeModelSupport class described at
       *   http://java.sun.com/products/jfc/tsc/articles/jtree/index.html
       * That architecture (produced by Tom Santos and Steve Wilson)
       * is more elegant. I just hacked 'em in here so they are
       * immediately at hand.
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