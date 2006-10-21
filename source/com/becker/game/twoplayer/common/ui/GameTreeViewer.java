package com.becker.game.twoplayer.common.ui;

import com.becker.common.ColorMap;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.search.SearchTreeNode;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

/**
 *  This class takes a root node and displays the portion of the tree visible in the text tree control.
 *  An actual graphicsl tree is shown to represent the game search tree.
 *
 *  @author Barry Becker
 */
final class GameTreeViewer extends JPanel implements MouseMotionListener
{

    private static final Color BACKGROUND_COLOR = Color.white;
    private static final int BACKGROUND_BAND_OPACITY = 60;
    private static final int MARGIN = 8;

    private static final BasicStroke THIN_STROKE = new BasicStroke(0.4f);
    private static final BasicStroke HIGHLIGHT_STROKE = new BasicStroke(1.4f);
    private static final BasicStroke BRUSH_HIGHLIGHT_STROKE = new BasicStroke(3.7f);
    private static final int PRUNE_SPACING = 12;
    // circle around highlighted node
    private static final int HL_NODE_RADIUS = 3;
    private static final int HL_NODE_DIAMETER = 8;
    private static final long serialVersionUID = 0L;



    private Color backgroundColor_ = BACKGROUND_COLOR;
    private ColorMap colormap_;
    private TwoPlayerPieceRenderer pieceRenderer_;

    private SearchTreeNode root_;
    private int depth_ = 0;
    // sum of the number of descendents of all nodes at the level
    private int[] totalAtLevel_;

    private int width_;
    private int levelHeight_;

    // most recently highlighted path
    private TreePath oldHighlightPath_;


    /**
     * Construct the viewer
     * @param root root of text tree to base tree graph on.
     */
    GameTreeViewer( SearchTreeNode root, int depth, ColorMap cmap, TwoPlayerPieceRenderer pieceRenderer )
    {
        colormap_ = cmap;
        pieceRenderer_ = pieceRenderer;
        setRoot(root, depth);
    }

    public synchronized void setRoot( SearchTreeNode root, int depth )
    {
        root_ = root;
        // this is very expensive because it traverses the whole tree.
        // it might be simpler to just use the depth from the controller, but that would
        // not account for those times when we drill deeper using quiescent search.
        depth_ = root_.getDepth();

        totalAtLevel_ = new int[depth_+2];
        oldHighlightPath_ = null;

        if (root_ != null && root_.getUserObject() != null)
            initializeTreeStats(root_, 0);
    }

    /**
     *  draw the currently visible game tree.
     */
    public synchronized void refresh()
    {
        // this will paint the component immediately
        paint( getGraphics() );
        oldHighlightPath_ = null;
    }

    /**
     * @param c  the new color of the tree.
     */
    public synchronized void setBackground( Color c )
    {
        backgroundColor_ = c;
        refresh();
    }

    private synchronized void initializeTreeStats( SearchTreeNode node, int depth)
    {
        if (node.isLeaf())  {
            if (node.isPruned()) {
                // give pruned nodes a little more space
                node.setSpaceAllocation(node.getSpaceAllocation() + Math.max(1, 4*PRUNE_SPACING - PRUNE_SPACING*depth));
            }
            else {
                node.setNumDescendants(0);
                node.setSpaceAllocation(1);  // never 0;
            }
            totalAtLevel_[depth] += node.getSpaceAllocation();
            return;
        }



        //System.out.println("num children="+node.getChildCount());
        //
        Enumeration it = node.children();
        while (it.hasMoreElements()) {
            SearchTreeNode child = (SearchTreeNode)it.nextElement();
            initializeTreeStats( child, depth+1 );
            node.setSpaceAllocation(node.getSpaceAllocation() + child.getSpaceAllocation());
            node.setNumDescendants(node.getNumDescendants() + child.getNumDescendants());
        }
        // count the node as a descendant
        node.setSpaceAllocation(node.getSpaceAllocation() + 1);
        totalAtLevel_[depth] += node.getSpaceAllocation();
    }


    // ---  these methods implement the MouseMotionListener interface   ---
    public void mouseMoved( MouseEvent e ) {}
    public void mouseDragged( MouseEvent e ) {}


    /**
     * perform a sequence of moves from somewhere in the game;
     * not necessarily the start. We do, however,
     * assume the moves are valid. It is for display purposes only.
     *
     * @param path path corresponding to a the list of moves to make
     */
    public synchronized void highlightPath( TreePath path )
    {
        // unhighlight the old path and highlight the new one without redrawing the whole tree.
        //GameContext.log(2, "about to highlight "+path );
        Graphics2D g2 = (Graphics2D)getGraphics();
        g2.setXORMode(Color.WHITE);
        g2.setStroke(BRUSH_HIGHLIGHT_STROKE);

        // first unhighlight the old path
        if (oldHighlightPath_ != null)  {
            highlight( oldHighlightPath_, g2);
        }

        highlight( path, g2);
        g2.setPaintMode();
        oldHighlightPath_ = path;
    }

    private synchronized void highlight( TreePath path, Graphics2D g2)
    {
        Object[] pathNodes = path.getPath();
        SearchTreeNode lastNode = (SearchTreeNode)pathNodes[0];
        g2.drawOval(lastNode.getX()-HL_NODE_RADIUS, lastNode.getY() - HL_NODE_RADIUS, HL_NODE_DIAMETER, HL_NODE_DIAMETER);
        for (int i=1; i<pathNodes.length; i++) {
            SearchTreeNode node = (SearchTreeNode)pathNodes[i];
            TwoPlayerMove m = (TwoPlayerMove)node.getUserObject();
            g2.setColor(colormap_.getColorForValue(m.getInheritedValue()));
            g2.drawLine(lastNode.getX(),lastNode.getY(), node.getX(), node.getY());
            g2.setColor(colormap_.getColorForValue(m.getValue()));
            g2.drawOval(node.getX()-HL_NODE_RADIUS, node.getY()-HL_NODE_RADIUS, HL_NODE_DIAMETER, HL_NODE_DIAMETER);
            lastNode = node;
        }
    }

    /**
     * @param node
     * @param depth
     * @param offset
     * @param g2
     */
    private synchronized void drawNode( SearchTreeNode node, int depth, int offset, Graphics2D g2)
    {
        TwoPlayerMove m = (TwoPlayerMove)node.getUserObject();
        g2.setColor( colormap_.getColorForValue(m.getValue()));
        int x = MARGIN + (int) (width_*(offset + node.getSpaceAllocation() / 2.0)/totalAtLevel_[depth]);
        int y = MARGIN + depth*levelHeight_;
        node.setX(x);
        node.setY(y);

        int width = 2;
        if (m.isSelected()) {
            width = 3;
            g2.fillRect(x, y, width, 2);
        }
        else {
            g2.fillRect(x, y, width, 2);
        }
        if (node.isPruned())  {
           // draw a marker to show that it has been pruned
           g2.setColor(Color.black);
           //g2.drawLine(x-1, y+1, x+4, y+5);
           //g2.drawLine(x-1, y+5, x+4, y+1);
           g2.fillRect(x, y+3, width, 1);
        }
    }

    /**
     * @param parent
     * @param child child node move
     * @param depth
     * @param offset1 offset at parent level.
     * @param offset2 offset at child level in the tree.
     * @param g2
     */
    private synchronized void drawArc( SearchTreeNode parent, SearchTreeNode child, int depth, int offset1, int offset2, Graphics2D g2)
    {
        TwoPlayerMove m = (TwoPlayerMove)child.getUserObject();
        boolean highlighted = m.isSelected() && ((TwoPlayerMove)parent.getUserObject()).isSelected();
        if (highlighted)
            g2.setStroke(HIGHLIGHT_STROKE);
        g2.setColor( colormap_.getColorForValue(m.getInheritedValue()));
        g2.drawLine(MARGIN + (int) (width_*(offset1 + parent.getSpaceAllocation() / 2.0)/totalAtLevel_[depth]), MARGIN + depth*levelHeight_,
                    MARGIN + (int) (width_*(offset2 + child.getSpaceAllocation() / 2.0)/totalAtLevel_[depth+1]), MARGIN + (depth+1)*levelHeight_);
        if (highlighted)
            g2.setStroke(THIN_STROKE);

    }

    /**
     * Draw the nodes and arcs in the game tree.
     * It can get quite huge.
     */
    private synchronized void drawTree( SearchTreeNode node, Graphics2D g2)
    {
        int oldDepth = 0;
        int depth;
        int[] offsetAtLevel = new int[depth_+2];

        drawNode(node, 0, 0, g2);    // draw last?
        List<SearchTreeNode> q = new LinkedList<SearchTreeNode>();
        q.add(node);

        while (q.size() > 0) {
            SearchTreeNode p = q.remove(0);
            depth = p.getLevel();
            // draw the arc and child node for each child c of p
            if (depth > oldDepth) {
                oldDepth = depth;
                offsetAtLevel[depth] = 0;
            }
            Enumeration enumXXX = p.children();
            while (enumXXX.hasMoreElements()) {
                SearchTreeNode c = (SearchTreeNode)enumXXX.nextElement();
                drawArc(p, c, depth, offsetAtLevel[depth],  offsetAtLevel[depth+1], g2);
                drawNode(c, depth+1,  offsetAtLevel[depth+1], g2);
                offsetAtLevel[depth+1] += c.getSpaceAllocation();
                q.add(c);
            }
            offsetAtLevel[depth] += p.getSpaceAllocation();
        }
        // initialize offsets
    }

    /**
     * draw colored bands to give an indication of who is moving on each ply.
     * @param g2
     */
    private synchronized void drawBackground( Graphics2D g2 )
    {
        g2.setColor( backgroundColor_ );
        g2.fillRect( 0, 0, getWidth(), getHeight() );


        Color c = pieceRenderer_.getPlayer1Color(); //colormap_.getMaxColor();
        Color p1Color = new Color(c.getRed(), c.getGreen(), c.getBlue(), BACKGROUND_BAND_OPACITY);
        c = pieceRenderer_.getPlayer2Color();  //colormap_.getMinColor();
        Color p2Color = new Color(c.getRed(), c.getGreen(), c.getBlue(), BACKGROUND_BAND_OPACITY);
        GradientPaint gp = new GradientPaint(0, (float)levelHeight_/4.0f, p2Color, 0, 5.0f*levelHeight_/4.0f, p1Color, true);
        g2.setPaint(gp);

        g2.fillRect( 0, 0, getWidth(), getHeight() );
    }

    /**
     * This renders the current tree to the canvas
     */
    protected synchronized void paintComponent( Graphics g )
    {

        Graphics2D g2 = (Graphics2D)g;

        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        super.paintComponents( g );
        drawBackground( g2 );

        if (root_==null || root_.getUserObject()==null) return;

        width_ = getWidth()-2*MARGIN;
        int height_= getHeight()-2*MARGIN;
        if (depth_ == 0)
            return; // tree not ready to be painted.
        levelHeight_ = height_ /depth_;

        g2.setStroke(THIN_STROKE);
        drawTree(root_, g2);
    }
}