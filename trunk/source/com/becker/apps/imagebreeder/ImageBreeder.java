/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.apps.imagebreeder;

import com.becker.common.concurrency.Parallelizer;
import com.becker.java2d.imageproc.MetaImageOp;
import com.becker.optimization.parameter.types.Parameter;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * Create a set of images from a single MetaImageOp
 */
public class ImageBreeder {

    private MetaImageOp metaOp;

    private float variance;

    private BufferedImage imageToBreed;

    private Parallelizer<BufferedImage> parallelizer = new Parallelizer<BufferedImage>();

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

        List<Callable<BufferedImage>> filterTasks = new ArrayList<Callable<BufferedImage>>(numChildImages);
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