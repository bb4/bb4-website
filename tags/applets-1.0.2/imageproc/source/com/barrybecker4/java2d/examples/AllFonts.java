package com.barrybecker4.java2d.examples;

import com.barrybecker4.ui.application.ApplicationFrame;

import javax.swing.*;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;

/**
 * Derived from code accompanying "Java 2D Graphics" by Jonathan Knudsen.
 * It's interesting to note that very few fonts support all unicode characters
 * (and if they do, they probably take up a lot of memory).
 * About 10% support japanese characters and about 10% suport vietnamese characters for example, but
 * not the same 10%. The fonts that I found that support german, japanese, and vietnamese characters are:
 *  *Dialog
 *   DialogInput
 *   Serif      (font have a small line trailing from the edges of letters and symbols)
 *  *SansSerif  (font without the small trailing lines)
 *
 *  In my opinion, of these, SansSerif or Dialog look the best.
 */
public class AllFonts {

    private static final int ROW_HEIGHT = 30;
    private static final int FONT_SIZE = 20;


    private static final String ENGLISH = "The quick brown fox jumped over the lazy dog.";
    private static final String JAPANESE = "\u56f2\u7881\u30b2\u30fc\u30e0\u306e\u60c5\u5831";
    private static final String GERMAN = "ungefähr Ausführungen";
    private static final String VIETNAMESE = "m\u00F4 ph\u1ECFng";

    private static final String SPECIAL_CHARS = "\u56f2\u7881äü\u00F4\u1ECF";

    private static final String SEPARATOR = "  |  ";
    private static final String MULTI_LINGUAL_STRING =
            ENGLISH + SEPARATOR + JAPANESE + SEPARATOR + GERMAN + SEPARATOR + VIETNAMESE;


    private static void showAllFonts() {
        Font[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
        for (Font font : fonts) {
            System.out.println(font.getFontName() + " : " + font.getFamily() + " : " + font.getName());
        }
    }

    public static void main( String[] args ) {

        showAllFonts();

        Frame f = new FontsFrame();
        f.setSize(new Dimension(1200, 800));
        f.setVisible( true );
    }

    private static class FontsFrame extends ApplicationFrame {

        public FontsFrame() {

            FontsPanel fontsPanel = new FontsPanel();
            fontsPanel.setPreferredSize(new Dimension(1100, 14000));
            JScrollPane pane =
                    new JScrollPane(fontsPanel);
            this.getContentPane().add(pane);
        }
    }

    private static class FontsPanel extends JPanel {
         @Override
         public void paint( Graphics g ) {
              super.paint(g);
              Graphics2D g2 = (Graphics2D) g;

              g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
                      RenderingHints.VALUE_ANTIALIAS_ON );

              drawAllFonts(g2);
         }

        private void drawAllFonts(Graphics2D g2) {

            List<Font> fonts = getAllFonts();
            int i=0;

            for (Font font : fonts) {
                drawFontMessage(i++, font, g2);
            }
        }

        /** get a list of all fonts, with the ones that support japanese and vietnamese characters first */
        private List<Font> getAllFonts() {
            List<Font> multiLingualFonts = new ArrayList<Font>();
            List<Font> otherFonts = new ArrayList<Font>();

            String[] allFonts =
                    GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
            for (String fontName : allFonts) {
                Font font = new Font(fontName, Font.PLAIN, FONT_SIZE);
                if (supportsChars(font, SPECIAL_CHARS)) {
                    multiLingualFonts.add(font);
                } else {
                    otherFonts.add(font);
                }
            }
            List<Font> combinedList = new ArrayList<Font>(multiLingualFonts);
            combinedList.addAll(otherFonts);
            return combinedList;
        }

        private boolean supportsChars(Font font, String specialChars){

            for (int i=0; i< specialChars.length(); i++) {
                if (!font.canDisplay(specialChars.charAt(i))) {
                    return false;
                }
            }
            return true;
        }


        private void drawFontMessage(int i, Font font, Graphics2D g2)  {

            g2.setFont( font );
            int yPos = 25 + ROW_HEIGHT * i;
            g2.drawString(font.getName() , 40, yPos );
            g2.drawString(MULTI_LINGUAL_STRING , 500, yPos);
        }
    }

    private AllFonts() {}
}