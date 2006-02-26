package com.becker.puzzle.set;

import com.becker.game.common.*;

import java.awt.*;
import java.awt.geom.*;

/**
 * A static class that takes a card and renders it to the Viewer.
 * We use a separate card rendering class to avoid having ui in the card class itself.
 * This allows us to more cleanly separate the view from the model.
 *
 * @author Barry Becker Date: Feb 26, 2006
 */
public class CardRenderer {

    protected static final Font BASE_FONT = new Font( "Sans-serif", Font.PLAIN, 11 );
    private static final Color BACKGROUND_COLOR = new Color(240, 240, 255);

    private static final Color HIGHLIGHT_COLOR = new Color(255, 250, 55);
    private static final Color BORDER_COLOR = new Color(85, 80, 35);
    private static final float MARGIN_RAT = 0.04f;
    private static final Stroke BORDER_STROKE = new BasicStroke(3.0f);


    private static final float SHAPE_SIZE_FRAC = 0.9f;
    private static final float SHAPE_WIDTH_FRAC = 0.6f;
    private static final float SHAPE_HEIGHT_FRAC = 0.2f;

    // rounded edge
    private static final float ARC_RAT = 0.12f;


    // use static to avoid creating a lot of new objects.
    private static Point position_ = new Point(0,0);


    /**
     * private constructor because this class is a singleton.
     * Use getPieceRenderer instead
     */
    private CardRenderer()
    {}


    private static Color getColorForValue(Card.AttributeValue val)
    {
        switch (val) {
            case FIRST : return Color.RED;
            case SECOND : return Color.GREEN;
            case THIRD : return Color.BLUE;
        }
        return Color.BLACK;
    }

    private static Color getBorderColorForValue(Card.AttributeValue val)
    {
        switch (val) {
            case FIRST : return Color.RED.darker();
            case SECOND : return Color.GREEN.darker();
            case THIRD : return Color.BLUE.darker();
        }
        return Color.BLACK;
    }

    protected static Color getCardColor(Card card)
    {
        return getColorForValue(card.color());
    }

    protected static Color getBorderCardColor(Card card)
    {
        return getBorderColorForValue(card.color());
    }

    protected static Paint getCardTexture(Card card)
    {
        switch (card.texture()) {
            case FIRST : return BACKGROUND_COLOR;
            case SECOND : return getColorForValue(card.color());
            case THIRD : return new GradientPaint(75, 75, BACKGROUND_COLOR, 78, 75, getColorForValue(card.color()), true);
        }
        return  null;
    }


    private static Shape getShape(Card card, int width, int height)
    {
        Shape shape = null;
        int topMargin = (int) ((1-SHAPE_SIZE_FRAC) * height);
        int leftMargin = (int) ((1-SHAPE_SIZE_FRAC) * width);
        float w = width * SHAPE_SIZE_FRAC;
        float h = height * SHAPE_SIZE_FRAC;
        switch (card.shape()) {
            case FIRST : shape = new Ellipse2D.Float( leftMargin, topMargin, w, h );  break;
            case SECOND : shape = new Rectangle2D.Float( leftMargin, topMargin, w, h ); break;
            case THIRD :
                GeneralPath path = new GeneralPath();
                int hd2 = (int) h >> 1;
                int wd2 = (int) width >> 1;
                path.moveTo(leftMargin, hd2);
                path.lineTo(wd2, 0);
                path.lineTo(w, hd2);
                path.lineTo(wd2, h);
                path.lineTo(leftMargin, hd2);
                shape = path;
                break;
        }
        return shape;
    }

    private static int getNumber(Card card)
    {
        switch (card.number()) {
            case FIRST : return 1;
            case SECOND : return 2;
            case THIRD : return 3;
        }
        return 0;
    }

   /**
     * this draws the actual piece at this location (if there is one).
     * Uses the RoundGradientFill from Knudsen to put a specular highlight on the stone.
     *
     * @param g2 graphics context
     * @param position the position of the piece to render
     */
    public static void render(Graphics2D g2, Card card, Location position, int width, int height, boolean highlight) {
       int x = position.getRow();
       int y = position.getCol();

       int cardArc = (int) (ARC_RAT * width);
       int margin = (int) (MARGIN_RAT * width);

       g2.setColor(BORDER_COLOR);
       g2.fillRoundRect(x + margin, y + margin, width - margin, height - margin, cardArc, cardArc);
       g2.setColor(BACKGROUND_COLOR);
       g2.fillRoundRect(x + 2*margin, y + 2*margin, width - 3 * margin, height - 3 * margin, cardArc, cardArc);

       g2.setColor(getCardColor(card));

       Shape shape = getShape(card, (int) (width * SHAPE_WIDTH_FRAC), (int) (height * SHAPE_HEIGHT_FRAC));
       int num = getNumber(card);
       int startXoffset = (int)((1.0 - SHAPE_WIDTH_FRAC)/ 2.0 * width);
       int startYoffset = (int)((3.0 - num) * height * 0.1) +  (int) (0.2 * height);
       int offset = (int)((height - 2 * margin) >> 2);

       g2.setStroke(BORDER_STROKE);

       g2.translate(x + startXoffset, y);
       for (int i = 0; i < num; i++) {

           g2.translate(0, startYoffset + i*offset);
           g2.setPaint(getCardTexture(card));
           g2.fill(shape);
           g2.setPaint(getBorderCardColor(card));
           g2.draw(shape);
           g2.translate(0, -startYoffset - i*offset);
       }
       g2.translate(-x - startXoffset, -y);


    }

}

