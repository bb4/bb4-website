package com.becker.imagebreeder;

import com.becker.common.*;
import com.becker.optimization.parameter.Parameter;
import com.becker.java2d.imageproc.MetaImageOp;

import java.awt.image.*;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * Create a set of images from a single MetaImageOp
 */
public class ImageBreeder
{
    private MetaImageOp metaOp;
    private float variance;

    private BufferedImage imageToBreed;

    private Parallelizer parallelizer = new Parallelizer();

    private Map<BufferedImage, List<Parameter>> imgToParamsMap;

    /**
     * Create permutations from the input image based on the metaOp passed in.
     * @param image image to breed
     * @param op metaOp to breed based on.
     * @param variance amount of variation to have in bred images.
     */
    public ImageBreeder(BufferedImage image,  MetaImageOp op, float variance)
    {
        metaOp = op;
        imageToBreed = image;
        this.variance = variance;
        imgToParamsMap = new HashMap<BufferedImage, List<Parameter>> ();
    }

    /**
     *
     * @param numChildImages
     * @return list of bred images
     */
    public List<BufferedImage> breedImages(int numChildImages)  {

        List<BufferedImage> images =
                Collections.synchronizedList(new ArrayList<BufferedImage>(numChildImages));

        imgToParamsMap.clear();

        List<Callable> filterTasks = new ArrayList<Callable>(numChildImages);
        for (int i=0; i<numChildImages; i++) {
            filterTasks.add(new Worker(metaOp));
        }

        List<Future<BufferedImage>> imageFutures = parallelizer.invokeAll(filterTasks);

        for (Future<BufferedImage> f : imageFutures) {
            try {
                BufferedImage img = f.get();
                images.add(img);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return images;
    }

    public Map<BufferedImage, List<Parameter>> getImgToParamsMap() {
        return imgToParamsMap;
    }


    /**
     * Runs one of the chunks.
     */
    private class Worker implements Callable<BufferedImage> {

        private MetaImageOp metaOp;

        public Worker(MetaImageOp metaOp) {
            // need to make a copy or other parallel thread may step on our internal data.
            this.metaOp = metaOp.copy();
        }

        public BufferedImage call() {
            BufferedImage img = metaOp.getRandomInstance(variance).filter( imageToBreed, null  );
            // remember the parameters that were used to create this instance;
            imgToParamsMap.put(img, metaOp.getLastUsedParameters());
            return img;
        }
    }
}