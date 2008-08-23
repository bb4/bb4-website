package com.becker.java2d.imageproc;

import com.becker.common.*;

import com.becker.optimization.Parameter;
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
        //createTransformations();
        //createRescales();
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
       params.add(new Parameter(0.0, 0, 5.0, "time"));
       params.add(new Parameter(32.0, 0.5, 100.0, "scale"));
       // params.add(new Parameter(10, 1, 30, "brightness", true));
       params.add(new Parameter(0.9, 0.0, 1.0, "turbulence"));
       params.add(new Parameter(0.0, 0.0, 1.0, "dispersion"));
       params.add(new Parameter(1.0, 0.3, 1.0, "amount"));
       
        mOps.put( "Caustics", new MetaImageOp(CausticsFilter.class, params));       
        /*
        mOps.put("Bump Filter (small)", new BumpFilter(0.5f));
        mOps.put("Bump Filter (large)", new BumpFilter(1.5f));
        CellularFilter cfilter = new CellularFilter();
        cfilter.setTurbulence(1.4f);
        cfilter.setF1(0.1f);
        cfilter.setF2(0.4f);
        cfilter.setF3(0.3f);
        cfilter.setF3(0.2f);
        cfilter.setRandomness(0.8f);
        cfilter.setAmount(1.3f);
        cfilter.setGradientCoefficient(1.0f);
        cfilter.setScale(16);
        cfilter.setUseColor(true);
        mOps.put("Cellular", cfilter);                
        mOps.put("Contour", new ContourFilter());
        mOps.put("Crystallize", new CrystallizeFilter());
        CurvesFilter curvesFilter = new CurvesFilter();
        float x[] = {0f, 0.1f, 0.8f, 1f};
        float y[] = {0f, 0.01f, .95f, 1f};
        CurvesFilter.Curve c = 
                new CurvesFilter.Curve(x, y);
        curvesFilter.setCurve(c);
        mOps.put("Curve", curvesFilter);
        mOps.put("Diffusion", new DiffusionFilter());
        EmbossFilter embFilter = new EmbossFilter();      
        mOps.put("Emboss", embFilter);
        mOps.put("Equalize", new EqualizeFilter());
        
        int op = PixelUtils.AVERAGE; // ADD and MULTIPLY also good.
        float amount = 0.8f;
        FBMFilter fbm1 = new FBMFilter();
        fbm1.setOperation(op);
        fbm1.setAmount(amount);
        fbm1.setBasisType(FBMFilter.NOISE);
        mOps.put("FBM (ridged)", fbm1);
        
        FBMFilter fbm2 = new FBMFilter();
        fbm2.setOperation(op);
        fbm2.setAmount(amount);
        fbm2.setBasisType(FBMFilter.RIDGED);
        mOps.put("FBM (noise)", fbm2);
        
        FBMFilter fbm3 = new FBMFilter();
        fbm3.setOperation(op);
        fbm3.setAmount(amount);
        fbm3.setBasisType(FBMFilter.CELLULAR);
        mOps.put("FBM (cellular)", fbm3);
        
        FBMFilter fbm4 = new FBMFilter();
        fbm4.setOperation(op);
        fbm4.setAmount(amount);
        fbm4.setBasisType(FBMFilter.SCNOISE);
        mOps.put("FBM (scnoise)", fbm4);
        
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
         * */  
    }

}