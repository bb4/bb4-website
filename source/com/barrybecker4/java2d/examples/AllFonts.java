package com.barrybecker4.java2d.examples;

import com.barrybecker4.ui.application.ApplicationFrame;

import javax.swing.*;
import java.awt.*;

public class AllFonts {

    private static final int ROW_HEIGHT = 30;


    private static void showAllFonts() {
        Font[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
        for ( int i = 0; i < fonts.length; i++ ) {
            System.out.println( fonts[i].getFontName() + " : " + fonts[i].getFamily() + " : " + fonts[i].getName() );
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
         public void paint( Graphics g ) {
              super.paint(g);
              Graphics2D g2 = (Graphics2D) g;

              g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
                      RenderingHints.VALUE_ANTIALIAS_ON );

              drawAllFonts(g2);
         }

         private void drawAllFonts(Graphics2D g2) {

             String[] fonts =
                    GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames(); //getAllFonts();
             for ( int i = 0; i < fonts.length; i++ ) {
                 Font font = new Font(fonts[i], Font.PLAIN, 24 );
                 drawFontMessage(i, font, g2);
             }
         }

        private void drawFontMessage(int i, Font font, Graphics2D g2)  {
            //Font font = new Font( "Serif", Font.PLAIN, 24 );

            g2.setFont( font );
            int yPos = 10 + ROW_HEIGHT * i;
            g2.drawString(font.getName() , 40, yPos );
            g2.drawString("The quick brown fox jumped over the lazy dog.", 500, yPos );
        }
    }
}