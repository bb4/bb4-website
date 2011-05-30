package com.becker.java2d.imageproc;

import com.becker.optimization.parameter.*;
import com.jhlabs.image.*;

import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.LookupOp;
import java.awt.image.ShortLookupTable;
import java.util.*;

/**
 * A set of  available image processing operations.
 */
public class ProcessingOperators 
{
  
    private Map<String, MetaImageOp> mOps;

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

    private void createColorOps()
    {
        mOps.put( "Grayscale", new MetaImageOp(new GrayscaleFilter()));       
        
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

    
    private void createJHLabsOps()
    {        
         List<Parameter> params = new ArrayList<Parameter>();
         mOps.put( "Caustics", createCausticsOp());       
        
         params = new ArrayList<Parameter>();
         params.add(DoubleParameter.createGaussianParameter(1.0, 0.2, 1.8, "height", 0.5, 0.2));
         mOps.put("Bumps", new MetaImageOp(BumpFilter.class, params));
                     
         mOps.put("Cellular", createCellularOp());
         mOps.put("Contour", createContourOp());
       
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
         mOps.put("Fractal Noise", createFractalOp());
              
        params = new ArrayList<Parameter>();
        params.add(new BooleanParameter(true, "useImageColors"));
        params.add(new DoubleParameter(0.9, 0.01, 2.0, "turbulence"));
        params.add(new DoubleParameter(1.0, 0.01, 3.0, "scaling"));    
        params.add(new BooleanParameter(true, "useImageColors"));    
        mOps.put("Plasma", new MetaImageOp(PlasmaFilter.class, params));     
       
        params = new ArrayList<Parameter>();
        params.add(new StringParameter(PolarFilter.PolarMappingType.RECT_TO_POLAR, PolarFilter.PolarMappingType.values(), "type"));
        params.add(new StringParameter(EdgeAction.WRAP, EdgeAction.values(), "edgeAction")); 
        mOps.put("Polar", new MetaImageOp(PolarFilter.class, params));     
       
        params = new ArrayList<Parameter>();
        params.add(new StringParameter(RippleFilter.RippleType.SINE, RippleFilter.RippleType.values(), "waveType"));
        params.add(new DoubleParameter(5.0, 0.0, 10.0, "xAmplitude"));
        params.add(new DoubleParameter(0.0, 0.0, 10.0, "yAmplitude"));
        params.add(new DoubleParameter(16, 1, 64, "xWavelength"));
        params.add(new DoubleParameter(16, 1, 64, "yWavelength"));
        mOps.put("Ripple", new MetaImageOp(RippleFilter.class, params));     
       
        params = new ArrayList<Parameter>();
        params.add(new StringParameter(EdgeAction.WRAP, EdgeAction.values(), "edgeAction")); 
        params.add(new DoubleParameter(2.0, 0.5, 6.0, "scale"));
        mOps.put("Diffuse", new MetaImageOp(DiffuseFilter.class, params));     
       
        params = new ArrayList<Parameter>();       
        params.add(new DoubleParameter(1.0, 0.1, 5.0, "redGamma"));
        params.add(new DoubleParameter(1.0, 0.1, 5.0, "greenGamma"));
        params.add(new DoubleParameter(1.0, 0.1, 5.0, "blueGamma"));
        mOps.put("Gamma", new MetaImageOp(GammaFilter.class, params));     
       
        params = new ArrayList<Parameter>();       
        params.add(new StringParameter(LightFilter.BumpShapeType.NONE, LightFilter.BumpShapeType.values(), "bumpShape"));
        params.add(new DoubleParameter(.5, 0.1, 2.0, "bumpHeight"));
        params.add(new DoubleParameter(0.0, 0.0, 3.0, "bumpSoftness"));
        params.add(new DoubleParameter(10000.0, 10.0, 10000.0, "viewDistance"));
        mOps.put("Light", new MetaImageOp(LightFilter.class, params));   
       
        params = new ArrayList<Parameter>();       
        params.add(new DoubleParameter(1.0, 0.8, 5.0, "amount"));
        params.add(new DoubleParameter(1.0, 0.5, 16.0, "turbulence"));
        params.add(new DoubleParameter(6.0, 1.0, 100.0, "xScale"));
        params.add(new DoubleParameter(6.0, 1.0, 100.0, "yScale"));
        mOps.put("Marble", new MetaImageOp(MarbleFilter.class, params));   
       
        params = new ArrayList<Parameter>();       
        params.add(new DoubleParameter(1.0, 0.5, 10.0, "turbulence"));
        params.add(new DoubleParameter(0.5, 0.1, 5.0, "turbulenceFactor"));
        params.add(new DoubleParameter(32.0, 8.0, 128.0, "scale"));
        params.add(new DoubleParameter(0.0, 0.0, Math.PI, "angle"));
        params.add(new DoubleParameter(1.0, 0.5, 10.0, "stretch"));       
        params.add(new DoubleParameter(1.0, 0.5, 6.0, "brightness"));   
        mOps.put("MarbleTexture", new MetaImageOp(MarbleTexFilter.class, params));   
       
        params = new ArrayList<Parameter>();       
        params.add(new BooleanParameter(true, "useOpacity"));
        params.add(new DoubleParameter(1.0, 0.1, 1.0, "opacity"));
        params.add(new DoubleParameter(0.5, 0.4, 0.9, "centreY"));
        mOps.put("Mirror", new MetaImageOp(MirrorFilter.class, params));   
       
        params = new ArrayList<Parameter>();       
        params.add(new BooleanParameter(false, "raysOnly"));
        params.add(new DoubleParameter(0.5, 0.1, 1.0, "opacity"));
        params.add(new DoubleParameter(0.5, 0.1, 1.0, "threshold"));
        params.add(new DoubleParameter(0.5, 0.0, 1.0, "strength"));    
        mOps.put("Rays", new MetaImageOp(RaysFilter.class, params));   
       
        params = new ArrayList<Parameter>();       
        params.add(new DoubleParameter(0.5, 0.2, 2.0, "amount")); 
        mOps.put("Saturation", new MetaImageOp(SaturationFilter.class, params));   
       
        params = new ArrayList<Parameter>();       
        params.add(new BooleanParameter(false, "shadowOnly"));
        params.add(new BooleanParameter(false, "addMargins"));
        params.add(new DoubleParameter(0.5, 0.0, 1.0, "opacity"));
        params.add(new DoubleParameter(5.0, 0.0, 10.0, "radius"));
        params.add(new DoubleParameter(Math.PI*6/4, 0.0, 2*Math.PI, "angle"));    
        params.add(new DoubleParameter(5.0, 1.0, 10.0, "distance")); 
        params.add(new IntegerParameter(0xff220066, 0xff000000, 0xffffffff, "shadowColor"));
        mOps.put("Shadow", new MetaImageOp(ShadowFilter.class, params));   
       
        mOps.put("Kaleidoscope", createKaleidoscopeOp());  
       
        params = new ArrayList<Parameter>();       
        params.add(new IntegerParameter(127, 0, 127, "lowerThreshold"));
        params.add(new IntegerParameter(127, 127, 255, "upperThreshold"));    
        mOps.put("Threshold", new MetaImageOp(ThresholdFilter.class, params));  
       
        params = new ArrayList<Parameter>();       
        params.add(new IntegerParameter(40, 8, 1000, "width")); 
        params.add(new IntegerParameter(40, 8, 1000, "height"));
        mOps.put("Scale", new MetaImageOp(ScaleFilter.class, params));  
        
        /*              
        mOps.put("Shine", new ShineFilter());                                    
        mOps.put("Gain", new GainFilter()); 
        mOps.put("Glint", new GlintFilter());  
        mOps.put("Glow", new GlowFilter());            
        mOps.put("Lens Blur", new LensBlurFilter());                  
        SwimFilter swimFilter = new SwimFilter();
        swimFilter.setAmount(2.0f);
        mOps.put("Swim", swimFilter);     
        WaterFilter waterDrop = new WaterFilter();
        waterDrop.setCentreX((float)Math.random());
        waterDrop.setCentreY((float)Math.random());
        waterDrop.setRadius(10.0f + (float)(100.0 * Math.random()));
        mOps.put("Water Drop", waterDrop);  
        mOps.put("Median", new MedianFilter());          
        
        //  float distance, float angle, float rotation, float zoom
        mOps.put("Motion blur", new MotionBlurFilter(2.0f, 3.0f, 0.0f, 0.0f));  
        mOps.put("Pointillize", new PointillizeFilter());  
        mOps.put("Quantize", new QuantizeFilter());         
             
        mOps.put("Blur (smart)", new SmartBlurFilter());  
        mOps.put("Smear", new SmearFilter());  
        mOps.put("Sparkle", new SparkleFilter());        
        mOps.put("Chrome", new ChromeFilter());         
        
        TwirlFilter twirl = new TwirlFilter();
        twirl.setAngle(1.0f);
        twirl.setRadius(200.0f);
        mOps.put("Twirl", twirl);  
                
        // secondary
        mOps.put("Weave", new WeaveFilter());  
        mOps.put("Wood", new WoodFilter());     
        mOps.put("Life", new LifeFilter());                       
         */
       
        /* tricky
        params = new ArrayList<Parameter>();
        float x[] = {0f, 0.1f, 0.8f, 1f};
        float y[] = {0f, 0.01f, .95f, 1f};
        CurvesFilter.Curve c = 
                new CurvesFilter.Curve(x, y);
        curvesFilter.setCurve(c);
        mOps.put("Curves", new MetaImageOp(CurvesFilter.class, params));
        params = new ArrayList<Parameter>();       
        mOps.put("Diffusion", new MetaImageOp(DiffusionFilter.class, params));
         
         //mOps.put("JavaLnf", new JavaLnFFilter());
         //mOps.put("Shatter", new ShatterFilter());  
         //mOps.put("Sky", new SkyFilter());   // npe
         //mOps.put("Scratch", new ScratchFilter());   white       
         //mOps.put("Shade", new ShadeFilter());  
         //mOps.put("Field Warp", new FieldWarpFilter());
         // mOps.put("Skeleton", new SkeletonFilter());  
       */
    }
    
    private static MetaImageOp createCausticsOp() {
        List<Parameter> params = new ArrayList<Parameter>();
        params.add(new DoubleParameter(0.0, 0, 10.0, "time"));
        params.add(new DoubleParameter(32.0, 0.1, 100.0, "scale"));
        params.add(new IntegerParameter(10, 1, 50, "brightness"));
        params.add(new DoubleParameter(0.0, 0.0, 10.0, "turbulence"));
        params.add(new DoubleParameter(0.0, 0.0, 1.0, "dispersion"));
        params.add(new DoubleParameter(1.0, 0.1, 2.0, "amount"));       
        return new MetaImageOp(CausticsFilter.class, params);               
    }
    
    private static MetaImageOp createCellularOp() {
         List<Parameter> params = new ArrayList<Parameter>();
         params.add(new BooleanParameter(true, "useColor"));
         Enum[] specValues = {CellularFilter.GridType.RANDOM};
         double[] specValueProbs = {0.6};
         params.add(
             StringParameter.createDiscreteParameter(
                         CellularFilter.GridType.RANDOM,
                         CellularFilter.GridType.values(), "gridType",  specValues, specValueProbs));
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
         return new MetaImageOp(CellularFilter.class, params);
    }
    
    private static MetaImageOp createFractalOp() {
         List<Parameter>  params = new ArrayList<Parameter>();    
         params.add(new StringParameter(FBMFilter.BasisType.CELLULAR, FBMFilter.BasisType.values(), "basisType"));
         
         Enum[] specValues = {
             OperationType.REPLACE,  OperationType.NORMAL, OperationType.MIN, OperationType.MAX, 
             OperationType.ADD, OperationType.SUBTRACT,  OperationType.MULTIPLY, 
             OperationType.HUE, OperationType.SATURATION, OperationType.VALUE, OperationType.COLOR,
             OperationType.SCREEN, OperationType.AVERAGE,  OperationType.CLEAR, 
             OperationType.EXCHANGE, OperationType.DISSOLVE, OperationType.DST_IN, OperationType.ALPHA, 
             OperationType.ALPHA_TO_GRAY};
         double[] specValueProbs = {
             0.010,  0.001,  0.050,  0.050,       
             0.060,  0.060,    0.120,            
             0.010,  0.040,  0.001,  0.060,           
             0.060,  0.140,   0.000,       
             0.001,  0.040,  0.010,  0.010,  0.001,  
         };                                                          
         params.add(StringParameter.createDiscreteParameter(
                          OperationType.MULTIPLY, OperationType.values(), "operation",  specValues, specValueProbs));
        
         params.add(new DoubleParameter(0.8, 0.1, 3.0, "amount"));
         params.add(new DoubleParameter(32, 4, 128, "scale"));
         double[] sv = {0.0};
         double[] svp = {0.2};
         params.add(DoubleParameter.createUniformParameter(1.0, 1.0, 8.0, "stretch", sv, svp));
         params.add(DoubleParameter.createUniformParameter(0.0, 0.0, Math.PI, "angle", sv, svp));
         params.add(new DoubleParameter(1.0, 0.0, 5.0, "H"));
         params.add(new DoubleParameter(2.0, 0.1, 4.0, "lacunarity"));
         params.add(new DoubleParameter(0.5, 0.1, 2.0, "gain"));
         params.add(DoubleParameter.createGaussianParameter(0.5, 0.0, 2.0, "bias",0.24, 0.2));
         params.add(new DoubleParameter(4.0, 0.1, 16.0, "octaves"));
         return new MetaImageOp(FBMFilter.class, params);
    }
    
    private static MetaImageOp createContourOp() {
         List<Parameter>params = new ArrayList<Parameter>();
         params.add(new DoubleParameter(5.0, 0.1, 10.0, "levels"));
         params.add(new DoubleParameter(1.0, 0.1, 10.0, "scale"));
         params.add(new DoubleParameter(0.0, 0.0, 2.0, "offset"));
         params.add(new IntegerParameter(0xff2200aa, 0xff000000, 0xffffffff, "contourColor"));
         return  new MetaImageOp(ContourFilter.class, params);
    }
    
    private static MetaImageOp createKaleidoscopeOp() {
         List<Parameter>  params = new ArrayList<Parameter>();       
         params.add(new IntegerParameter(3, 1, 6, "sides"));
         double[] sv = {0.0};
         double[] svp = {0.3};
         params.add(DoubleParameter.createUniformParameter(0.0, 0.0, 500.0, "radius", sv, svp));
         params.add(new DoubleParameter(0, 0.0, 2*Math.PI, "angle")); 
         params.add(new DoubleParameter(0, 0.0, Math.PI, "angle2"));       
         params.add(DoubleParameter.createGaussianParameter(0.5, 0.1, 0.9, "centreX", 0.5, .2));
         params.add(DoubleParameter.createGaussianParameter(0.5, 0.1, 0.9, "centreY", 0.5, 0.2));
         
         return new MetaImageOp(KaleidoscopeFilter.class, params);  
    }

}