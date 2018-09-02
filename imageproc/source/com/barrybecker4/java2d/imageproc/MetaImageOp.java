package com.barrybecker4.java2d.imageproc;

import com.barrybecker4.optimization.parameter.types.Parameter;
import scala.util.Random;

import java.awt.image.BufferedImageOp;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Contains an image operator and information about it (such as parameters).
 *
 * @author Barry Becker
 */
public class MetaImageOp {

    private Class<? extends BufferedImageOp> opClass;
    private BufferedImageOp op;

    /** list of base params based for creating concrete imageOps. */
    private volatile List<Parameter> parameters;

    /** last used list of params used to create recent imageOp. */
    private List<Parameter> lastUsedParameters;

    private boolean isDynamic;

    /** Ensures that all randomness is repeatable. */
    private static final Random RANDOM = new Random(1);


    /**
     * Use this constructor if no parameters.
     * @param op image operator
     */
    public MetaImageOp(BufferedImageOp op) {
        // an empty list of parameters because there are none.
        this.op = op;
        isDynamic = false;
    }

    /**
     * @param opClass the operator class.
     * @param params all the parameters that need to be set on the op.
     */
    MetaImageOp(Class<? extends BufferedImageOp> opClass, List<Parameter> params) {

        this.opClass = opClass;
        this.parameters = params;
        isDynamic = true;
    }

    /**
     * @return a concrete filter operator instance.
     */
    public  BufferedImageOp getInstance() {
        return getRandomInstance(0);
    }

    /**
     * @param randomVariance number of standard deviations to use when randomizing params.
     * @return a concrete instance with tweaked parameters.
     */
    public BufferedImageOp getRandomInstance(float randomVariance) {
        System.out.println("geting random. isDynamic=" + isDynamic + " randomVariance=" + randomVariance);
        if (!isDynamic) {
            return op;
        }
        try {

            this.op = opClass.newInstance();
            lastUsedParameters = tweakParameters(op, randomVariance);

        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.getLogger(MetaImageOp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return op;
    }

    public List<Parameter> getBaseParameters() {
        return parameters;
    }

    public List<Parameter> getLastUsedParameters() {
        return lastUsedParameters;
    }

    public MetaImageOp copy() {
        if (isDynamic) {
             return new MetaImageOp(opClass, parameters);
        } else {
             return new MetaImageOp(op);
        }
    }

    /**
     * Call the methods on the filter to set its custom parameters.
     * @param filter
     * @param randomVariance
     */
    private synchronized List<Parameter> tweakParameters(BufferedImageOp filter, float randomVariance)
            throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        System.out.println("op="+filter.getClass().getSimpleName() + " randomVariance=" + randomVariance);
        List<Parameter> newParams = new ArrayList<Parameter>(parameters.size());

        for (Parameter p : parameters) {
            // the name must match the property (e.g. foo will be set using setFoo)
            String methodName =
                    "set" +  p.name().substring(0, 1).toUpperCase() + p.name().substring(1);
            System.out.println("methodName = " + methodName);
            Method method = filter.getClass().getDeclaredMethod(methodName, p.getType()); // p.getNaturalValue().getClass());

            Object[] args = new Object[1];
            Parameter param = p.copy();

            if (randomVariance > 0) {
                param.tweakValue(randomVariance, RANDOM);
            }
            System.out.println("tweaked min = " + param.minValue() + " max = " + param.maxValue() +"  v = " + param.getValue());
            System.out.println("tweaked value = " + param);
            newParams.add(param);

            // @@ This should work with autoboxing, but does not for some reason, so we resort to ugly case statement.
            //args[0] = param.getNaturalValue();

            Class paramType = param.getType();
            if (paramType.equals(float.class)) {
                args[0] = (float) param.getValue();
            }
            else if (paramType.equals(int.class)) {
                args[0] = (int) param.getValue();
            }
            else if (paramType.equals(boolean.class)) {
                args[0] = param.getNaturalValue();
            }
            else if (paramType.equals(String.class)) {
                args[0] = param.getNaturalValue();
            }
            else  {
                throw new IllegalArgumentException("Unexpected param type = "+paramType);
            }

            method.invoke(filter, args); // p.getInformation().cast(p.getValue()));
        }
        return newParams;
    }
}
