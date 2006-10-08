package com.becker.xml;

import org.w3c.dom.Document;

import javax.swing.tree.TreePath;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeModelEvent;
import java.util.Vector;
import java.util.Enumeration;

    /**
     * This adapter converts the current Document (a DOM) into
     * a JTree model.
     */
    public class DomToTreeModelAdapter implements javax.swing.tree.TreeModel
    {

      private Document document_;

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
      private Vector listenerList = new Vector();
      public void addTreeModelListener(TreeModelListener listener) {
        if ( listener != null
        && ! listenerList.contains( listener ) ) {
           listenerList.addElement( listener );
        }
      }

      public void removeTreeModelListener(TreeModelListener listener) {
        if ( listener != null ) {
           listenerList.removeElement( listener );
        }
      }

      // Note: Since XML works with 1.1, this example uses Vector.
      // If coding for 1.2 or later, though, I'd use this instead:
      //   private List listenerList = new LinkedList();
      // The operations on the List are then add(), remove() and
      // iteration, via:
      //  Iterator it = listenerList.iterator();
      //  while ( it.hasNext() ) {
      //    TreeModelListener listener = (TreeModelListener) it.next();
      //    ...
      //  }

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
        Enumeration listeners = listenerList.elements();
        while ( listeners.hasMoreElements() ) {
          TreeModelListener listener =
            (TreeModelListener) listeners.nextElement();
          listener.treeNodesChanged( e );
        }
      }

      public void fireTreeNodesInserted( TreeModelEvent e ) {
        Enumeration listeners = listenerList.elements();
        while ( listeners.hasMoreElements() ) {
           TreeModelListener listener =
             (TreeModelListener) listeners.nextElement();
           listener.treeNodesInserted( e );
        }
      }

      public void fireTreeNodesRemoved( TreeModelEvent e ) {
        Enumeration listeners = listenerList.elements();
        while ( listeners.hasMoreElements() ) {
          TreeModelListener listener =
            (TreeModelListener) listeners.nextElement();
          listener.treeNodesRemoved( e );
        }
      }

      public void fireTreeStructureChanged( TreeModelEvent e ) {
        Enumeration listeners = listenerList.elements();
        while ( listeners.hasMoreElements() ) {
          TreeModelListener listener =
            (TreeModelListener) listeners.nextElement();
          listener.treeStructureChanged( e );
        }
      }
    }