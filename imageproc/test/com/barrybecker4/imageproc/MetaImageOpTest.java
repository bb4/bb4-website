// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.imageproc;

import com.barrybecker4.java2d.imageproc.MetaImageOp;
import junit.framework.TestCase;

import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;

/**
 * @author Barry Becker
 */
public class MetaImageOpTest extends TestCase {


    public void testConstruction() {
        BufferedImageOp bop = new StubBufferedOp();
        MetaImageOp op = new MetaImageOp(bop);
        assertNotNull(op);
    }

    public void testCopy() {

        BufferedImageOp bop = new StubBufferedOp();

        MetaImageOp op = new MetaImageOp(bop);
        MetaImageOp op2 = op.copy();

        assertNotNull(op2);
        assertNotSame("Unexpectedly same", op2, op);
    }


    private static class StubBufferedOp implements BufferedImageOp {
        @Override
        public BufferedImage filter(BufferedImage src, BufferedImage dest) {
            return null;
        }

        @Override
        public Rectangle2D getBounds2D(BufferedImage src) {
            return null;
        }

        @Override
        public BufferedImage createCompatibleDestImage(BufferedImage src, ColorModel destCM) {
            return null;
        }

        @Override
        public Point2D getPoint2D(Point2D srcPt, Point2D dstPt) {
            return null;
        }

        @Override
        public RenderingHints getRenderingHints() {
            return null;
        }
    }
}
