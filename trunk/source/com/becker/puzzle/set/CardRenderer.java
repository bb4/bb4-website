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
public final class CardRenderer {

    protected static final Font BASE_FONT = new Font( "Sans-serif", Font.PLAIN, 11 );
    private static final Color BACKGROUND_COLOR = new Color(250, 250, 255);

    private static final Color HIGHLIGHT_COLOR = new Color(255, 250, 55);
    private static final Color BORDER_COLOR = new Color(60, 60, 65);
    private static final float MARGIN_RAT = 0.03f;
    private static final Stroke SHAPE_BORDER_STROKE = new BasicStroke(4.0f);

    private static final float SHAPE_SIZE_FRAC = 0.88f;
    private static final float THIRD_SHAPE_FRAC = 0.95f; // slightly different for the diamond

    private static final float SHAPE_WIDTH_FRAC = 0.7f;
    private static final float SHAPE_HEIGHT_FRAC = 0.25f;

    private enum ColorType {
        SOLID, BORDER, HATCHED
    }

    private static final Color[][] symbolColors = {
        //   solid                  border                   hatched
        {new Color(255, 32, 1),  new Color(200, 5, 0),  new Color(255, 42, 22)},  // FIRST
        {new Color(0, 250, 0),  new Color(0, 180, 0),  new Color(0, 243, 1)},     // SECOND
        {new Color(85, 85, 255), new Color(0, 0, 210), new Color(75, 75, 255)}    // THIRD
    };

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

    private static Color getColorForValue(Card.AttributeValue val, ColorType style)
    {
        return symbolColors[val.ordinal()][style.ordinal()];
    }

    protected static Color getCardColor(Card card)
    {
        return getColorForValue(card.color(), ColorType.SOLID);
    }

    protected static Color getBorderCardColor(Card card)
    {
        return getColorForValue(card.color(), ColorType.BORDER);
    }

    protected static Paint getCardTexture(Card card)
    {
        switch (card.texture()) {
            case FIRST : return BACKGROUND_COLOR;
            case SECOND : return getCardColor(card);
            case THIRD :
                return new GradientPaint(75, 75, BACKGROUND_COLOR, 80, 75,
                                      getColorForValue(card.color(), ColorType.HATCHED), true);
        }
        return  null;
    }


    private static Shape getShape(Card card, int width, int height)
    {
        Shape shape = null;
        int topMargin = (int) ((1.0 - SHAPE_SIZE_FRAC) * height);
        int leftMargin = (int) ((1.0 - SHAPE_SIZE_FRAC) * width);
        float w = width * SHAPE_SIZE_FRAC;
        float h = height * SHAPE_SIZE_FRAC;
        switch (card.shape()) {
            case FIRST : shape = new Ellipse2D.Float( leftMargin, topMargin, w, h );  break;
            case SECOND : shape = new Rectangle2D.Float( leftMargin, topMargin, w, h ); break;
            case THIRD :
                float hh = (THIRD_SHAPE_FRAC * height);
                float ww = (THIRD_SHAPE_FRAC * width);
                float leftStart = (int) ((1.0 - THIRD_SHAPE_FRAC) * width);
                GeneralPath path = new GeneralPath();
                int hd2 = (int) hh >> 1;
                int wd2 = (int) ww >> 1;
                path.moveTo(leftStart, hd2);
                path.lineTo(leftStart + wd2, 0);
                path.lineTo(leftStart + ww, hd2);
                path.lineTo(leftStart + wd2, hh  );
                path.lineTo(leftStart, hd2);
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

       g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
       g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
       g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
       g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

       g2.setColor(BORDER_COLOR);
       g2.fillRoundRect(x + margin, y + margin, width - margin, height - margin, cardArc, cardArc);
       g2.setColor(BACKGROUND_COLOR);
       g2.fillRoundRect(x + 2*margin, y + 2*margin, width - 3 * margin, height - 3 * margin, cardArc, cardArc);

       g2.setColor(getCardColor(card));

       Shape shape = getShape(card, (int) (width * SHAPE_WIDTH_FRAC), (int) (height * SHAPE_HEIGHT_FRAC));
       int num = getNumber(card);
       int startXoffset = (int)((0.97 - SHAPE_WIDTH_FRAC)/ 2.0 * width);
       int startYoffset = (int)((3.0 - num) * height * 0.1) +  (int) ((0.99-3.0*SHAPE_HEIGHT_FRAC)/2.0 * height);
       int offset = (int)((height - 2 * margin) / 3.8);

       g2.setStroke(SHAPE_BORDER_STROKE);

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

