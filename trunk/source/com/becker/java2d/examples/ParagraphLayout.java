package com.becker.java2d.examples;

import com.becker.ui.ApplicationFrame;

import java.awt.*;
import java.awt.font.*;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

public class ParagraphLayout
{
    public static void main( String[] args )
    {
        Frame f = new ApplicationFrame( "ParagraphLayout v1.0" )
        {
            public void paint( Graphics g )
            {
                Graphics2D g2 = (Graphics2D) g;

                g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON );

                // From _One Hundred Years of Solitude_ by Gabriel Garcia Marquez.
                String s = "Jos\u00e9 Arcadio Buend\u00eda spent the long months " +
                        "of the rainy season shut up in a small room that he " +
                        "had built in the rear of the house so that no one " +
                        "would disturb his experiments. Having completely abandoned " +
                        "his domestic obligations, he spent entire nights in the " +
                        "courtyard watching the course of the stars and he almost " +
                        "contracted sunstroke from trying to establish an exact method " +
                        "to ascertain noon. When he became an expert in the use and " +
                        "manipulation of his instruments, he conceived a notion of " +
                        "space that allowed him to navigate across unknown seas, " +
                        "to visit uninhabited territories, and to establish " +
                        "relations with splendid beings without having to leave " +
                        "his study. That was the period in which he acquired the habit " +
                        "of talking to himself, of walking through the house without " +
                        "paying attention to anyone...";
                Font font = new Font( "Serif", Font.PLAIN, 24 );
                AttributedString as = new AttributedString( s );
                as.addAttribute( TextAttribute.FONT, font );
                AttributedCharacterIterator aci = as.getIterator();

                FontRenderContext frc = g2.getFontRenderContext();
                LineBreakMeasurer lbm = new LineBreakMeasurer( aci, frc );
                Insets insets = getInsets();
                float wrappingWidth = getSize().width - insets.left - insets.right;
                float x = insets.left;
                float y = insets.top;

                while ( lbm.getPosition() < aci.getEndIndex() ) {
                    TextLayout textLayout = lbm.nextLayout( wrappingWidth );
                    y += textLayout.getAscent();
                    textLayout.draw( g2, x, y );
                    y += textLayout.getDescent() + textLayout.getLeading();
                    x = insets.left;
                }
            }
        };
        f.setVisible( true );
    }
}