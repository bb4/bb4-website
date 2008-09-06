package com.becker.java2d.imageproc;

import com.becker.common.*;

import com.becker.optimization.parameter.BooleanParameter;
import com.becker.optimization.parameter.DoubleParameter;
import com.becker.optimization.parameter.IntegerParameter;
import com.becker.optimization.parameter.Parameter;
import com.becker.optimization.parameter.StringParameter;
import com.jhlabs.image.*;
import java.awt.color.ColorSpace;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;

/**
 * A set of  available image processing operations.
 * 
 */
public class ProcessingOperators 
{
  
    private Map<String, MetaImageOp> mOps;

    private static float[] scaleFactors_ = {1.0f, 1.0f, 1.0f, 1.0f};
    private static final float[] OFFSETS = {0.0f, 0.0f, 0.0f, 0.0f};


    public ProcessingOperators()
    {
        createOps();        
    }
    
    public Map getOperationsMap()
    {
        return mOps;
    }
    
    public MetaImageOp getOperation(String key)
    {
        return mOps.get(key);
    }
    
    public java.awt.List getSortedKeys()
    {
        // Make a sorted list of the operators.
        java.util.List<String> names = new java.util.ArrayList<String>();
        names.addAll(mOps.keySet());

        Collections.sort( names );
        final java.awt.List list = new java.awt.List();
        for ( int i = 0; i < names.size(); i++ )
            list.add( names.get( i ) );
        return list;
    }

    private void createOps()
    {
        mOps = new HashMap<String, MetaImageOp>();
        createConvolutions();        
        createLookups();
        createColorOps();
        createJHLabsOps();
    }
       
    private void createConvolutions()
    {
        float ninth = 1.0f / 9.0f;
        float[] blurKernel = {
            ninth, ninth, ninth,
            ninth, ninth, ninth,
            ninth, ninth, ninth
        };
        mOps.put( "Blur", 
                new MetaImageOp(new ConvolveOp(
                    new Kernel( 3, 3, blurKernel ), ConvolveOp.EDGE_NO_OP, null ) ));

        float[] edge = {
            0f,  -0.8f,   0f,
            -0.8f, 4.0f, -0.8f,
            0f,  -0.8f,   0f
        };
        mOps.put( "Edge detector", 
                new MetaImageOp(new ConvolveOp(
                    new Kernel( 3, 3, edge ), ConvolveOp.EDGE_NO_OP, null ) ));

        float[] sharp = {
            0f, -1f, 0f,
            -1f, 5f, -1f,
            0f, -1f, 0f
        };
        mOps.put( "Sharpen", 
                new MetaImageOp(new ConvolveOp(
                    new Kernel( 3, 3, sharp ) ) ));
    }

    private void createLookups()
    {
        short[] brighten = new short[256];
        short[] betterBrighten = new short[256];
        short[] posterize = new short[256];
        short[] invert = new short[256];
        short[] straight = new short[256];
        short[] zero = new short[256];
        for ( int i = 0; i < 256; i++ ) {
            brighten[i] = (short) (128 + i / 2);
            betterBrighten[i] = (short) (Math.sqrt( (double) i / 255.0 ) * 255.0);
            posterize[i] = (short) (i - (i % 32));
            invert[i] = (short) (255 - i);
            straight[i] = (short) i;
            zero[i] = (short) 0;
        }
        short[][] brightenTable = {brighten, brighten, brighten, straight};
        short[][] betterBrightenTable = {betterBrighten, betterBrighten, betterBrighten,  straight};
        short[][] posterizeTable = {posterize, posterize, posterize, straight};
        short[][] invertTable = {invert, invert, invert, straight};
        
        mOps.put( "Brighten", 
                new MetaImageOp(new LookupOp( new ShortLookupTable( 0, brightenTable ), null ) ));
        mOps.put( "Better Brighten", new MetaImageOp(new LookupOp(
                new ShortLookupTable( 0, betterBrightenTable ), null ) ));
        mOps.put( "Posterize", new MetaImageOp(new LookupOp(
                new ShortLookupTable( 0, posterizeTable ), null ) ));
        mOps.put( "Invert",  new MetaImageOp(new LookupOp( 
                new ShortLookupTable( 0, invertTable ), null ) ));

        short[][] redOnly = {invert, straight, straight, straight};
        short[][] greenOnly = {straight, invert, straight, straight};
        short[][] blueOnly = {straight, straight, invert, straight};
        mOps.put( "Red invert", new MetaImageOp(new LookupOp( 
                new ShortLookupTable( 0, redOnly ),
                null ) ));
        mOps.put( "Green invert", new MetaImageOp(new LookupOp(
                new ShortLookupTable( 0, greenOnly ), null ) ));
        mOps.put( "Blue invert", new MetaImageOp(new LookupOp(
                new ShortLookupTable( 0, blueOnly ), null ) ));

        // did not have 4th arg initially
        short[][] redRemove = {zero, straight, straight, straight};
        short[][] greenRemove = {straight, zero, straight, straight};
        short[][] blueRemove = {straight, straight, zero, straight};
        mOps.put( "Red remove", new MetaImageOp(new LookupOp(
                new ShortLookupTable( 0, redRemove ), null ) ));
        mOps.put( "Green remove", new MetaImageOp(new LookupOp(
                new ShortLookupTable( 0, greenRemove ), null ) ));
        mOps.put( "Blue remove", new MetaImageOp(new LookupOp(
                new ShortLookupTable( 0, blueRemove ), null ) ));
    }

    private void createColorOps()
    {
        mOps.put( "Grayscale", new MetaImageOp(new ColorConvertOp(
                ColorSpace.getInstance( ColorSpace.CS_GRAY ), null ) ));
    }
    
    private void createJHLabsOps()
    {
       List<Parameter> params = new ArrayList<Parameter>();
       params.add(new DoubleParameter(0.0, 0, 10.0, "time"));
       params.add(new DoubleParameter(32.0, 0.1, 100.0, "scale"));
       params.add(new IntegerParameter(10, 1, 50, "brightness"));
       params.add(new DoubleParameter(0.0, 0.0, 10.0, "turbulence"));
       params.add(new DoubleParameter(0.0, 0.0, 1.0, "dispersion"));
       params.add(new DoubleParameter(1.0, 0.1, 2.0, "amount"));       
        mOps.put( "Caustics", new MetaImageOp(CausticsFilter.class, params));       
        
       params = new ArrayList<Parameter>();
       params.add(new DoubleParameter(1.0, 0.5, 1.8, "height"));
       mOps.put("Bumps", new MetaImageOp(BumpFilter.class, params));
            
       params = new ArrayList<Parameter>();
       params.add(new BooleanParameter(true, "useColor"));
       params.add(new StringParameter(CellularFilter.GridType.RANDOM, CellularFilter.GridType.values(), "gridType"));
       params.add(new IntegerParameter(1, 1, 20, "turbulence"));       
       params.add(new DoubleParameter(0.0, 0.0, 1.0, "F1"));
       params.add(new DoubleParameter(0.0, 0.0, 1.0, "F2"));
       params.add(new DoubleParameter(0.0, 0.0, 1.0, "randomness"));
       params.add(new DoubleParameter(.5, 0.0, 1.0, "amount"));
       params.add(new DoubleParameter(1.0, 0.0, 2.0, "gradientCoefficient"));
       params.add(new DoubleParameter(1.0, 1.0, 30.0, "stretch"));
       params.add(new DoubleParameter(0.0, 0.0, Math.PI, "angle")); // in radians
       params.add(new DoubleParameter(1.0, 0.0, 5.0, "angleCoefficient"));
       params.add(new IntegerParameter(1, 1, 6, "distancePower"));
       params.add(new DoubleParameter(16.0, 0.1, 64.0, "scale"));     
       mOps.put("Cellular", new MetaImageOp(CellularFilter.class, params));
       
       params = new ArrayList<Parameter>();
       params.add(new DoubleParameter(5.0, 0.1, 10.0, "levels"));
       params.add(new DoubleParameter(1.0, 0.1, 10.0, "scale"));
       params.add(new DoubleParameter(0.0, 0.0, 2.0, "offset"));
       params.add(new IntegerParameter(0xff2200aa, 0xff000000, 0xffffffff, "contourColor"));
       mOps.put("Contour", new MetaImageOp(ContourFilter.class, params));
       
       params = new ArrayList<Parameter>();
       params.add(new BooleanParameter(false, "fadeEdges"));
       params.add(new DoubleParameter(0.4, 0.1, 2.0, "edgeThickness"));       
       params.add(new IntegerParameter(0xff2200aa, 0xff000000, 0xffffffff, "edgeColor"));
       mOps.put("Crystallize", new MetaImageOp(CrystallizeFilter.class, params));
       
       params = new ArrayList<Parameter>();
       params.add(new BooleanParameter(true, "emboss"));
       params.add(new DoubleParameter(2.0, 0.0, Math.PI, "azimuth"));
       params.add(new DoubleParameter(0.4, 0.0, Math.PI/2.0, "elevation"));    
       params.add(new DoubleParameter(0.5, 0.1, 2.5, "bumpHeight"));
       mOps.put("Emboss", new MetaImageOp(EmbossFilter.class, params));       
       
       mOps.put("Equalize", new MetaImageOp(new EqualizeFilter()));
       
       params = new ArrayList<Parameter>();
       params.add(new StringParameter(FBMFilter.BasisType.CELLULAR, FBMFilter.BasisType.values(), "basisType"));
       params.add(new StringParameter(PixelUtils.OperationType.MULTIPLY, PixelUtils.OperationType.values(), "operation"));
       //params.add(new IntegerParameter(0, 0, 4, "basisType"));
       //params.add(new IntegerParameter(7, 0, 20, "operation"));
       params.add(new DoubleParameter(0.8, 0.0, 2.0, "amount"));
       params.add(new DoubleParameter(32, 4, 128, "scale"));
       params.add(new DoubleParameter(1, 1, 8, "stretch"));
       params.add(new DoubleParameter(0, 0, Math.PI, "angle"));
       params.add(new DoubleParameter(1.0, 0.0, 5.0, "H"));
       params.add(new DoubleParameter(2.0, 0.1, 4.0, "lacunarity"));
       params.add(new DoubleParameter(0.5, 0.1, 2.0, "gain"));
       params.add(new DoubleParameter(0.5, 0.0, 2.0, "bias"));
       params.add(new DoubleParameter(4.0, 0.1, 16.0, "octaves"));
       mOps.put("Fractal Noise", new MetaImageOp(FBMFilter.class, params));
       

       /* tricky
        params = new ArrayList<Parameter>();
        float x[] = {0f, 0.1f, 0.8f, 1f};
        float y[] = {0f, 0.01f, .95f, 1f};
        CurvesFilter.Curve c = 
                new CurvesFilter.Curve(x, y);
        curvesFilter.setCurve(c);
        mOps.put("Curves", new MetaImageOp(CurvesFilter.class, params));
        * 
        params = new ArrayList<Parameter>();       
        mOps.put("Diffusion", new MetaImageOp(DiffusionFilter.class, params));
       */
       
        /*                 
        //mOps.put("Field Warp", new FieldWarpFilter());
        mOps.put("JavaLnf", new JavaLnFFilter());
        PlasmaFilter plasmaFilter = new PlasmaFilter();
        plasmaFilter.setUseImageColors(true);
        plasmaFilter.setTurbulence(0.9f);
        
        mOps.put("Plasma", plasmaFilter);
        mOps.put("Polar Fit", new PolarFilter());
        mOps.put("Ripple", new RippleFilter());     
        mOps.put("Diffuse", new DiffuseFilter());  
        mOps.put("Gain", new GainFilter()); 
        mOps.put("Gamma", new GammaFilter());  
        mOps.put("Glint", new GlintFilter());  
        mOps.put("Glow", new GlowFilter());  
        mOps.put("Kaleidoscope", new KaleidoscopeFilter());  
        mOps.put("Lens Blur", new LensBlurFilter());  
        mOps.put("Life", new LifeFilter());  
        mOps.put("Light", new LightFilter());   // cool
        mOps.put("Marble", new MarbleFilter()); 
        mOps.put("MarbleTexture", new MarbleTexFilter()); 
        mOps.put("Median", new MedianFilter());  
        mOps.put("Mirror", new MirrorFilter());  
        //  float distance, float angle, float rotation, float zoom
        mOps.put("Motion blur", new MotionBlurFilter(2.0f, 3.0f, 0.0f, 0.0f));  
        mOps.put("Pointillize", new PointillizeFilter());  
        mOps.put("Quantize", new QuantizeFilter());         
        mOps.put("Rays", new RaysFilter());          
        mOps.put("Saturation", new SaturationFilter(2.0f));  
        //mOps.put("Scratch", new ScratchFilter());   white       
        //mOps.put("Shade", new ShadeFilter());  
        mOps.put("Shadow", new ShadowFilter(30.0f, 4.0f, 1.0f, 0.5f));  
        //mOps.put("Shatter", new ShatterFilter());  
        mOps.put("Shine", new ShineFilter());        
        //mOps.put("Sky", new SkyFilter());   // npe
        mOps.put("Blur (smart)", new SmartBlurFilter());  
        mOps.put("Smear", new SmearFilter());  
        mOps.put("Sparkle", new SparkleFilter());         
        SwimFilter swimFilter = new SwimFilter();
        swimFilter.setAmount(2.0f);
        mOps.put("Swim", swimFilter);  
        mOps.put("Threshold", new ThresholdFilter());  
        TwirlFilter twirl = new TwirlFilter();
        twirl.setAngle(1.0f);
        twirl.setRadius(200.0f);
        mOps.put("Twirl", twirl);  
        WaterFilter waterDrop = new WaterFilter();
        waterDrop.setCentreX((float)Math.random());
        waterDrop.setCentreY((float)Math.random());
        waterDrop.setRadius(10.0f + (float)(100.0 * Math.random()));
        mOps.put("Water Drop", waterDrop);  
        mOps.put("Weave", new WeaveFilter());  
        mOps.put("Wood", new WoodFilter());                 
        //mOps.put("Skeleton", new SkeletonFilter());      
         */
    }

}