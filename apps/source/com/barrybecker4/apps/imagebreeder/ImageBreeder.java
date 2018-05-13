/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.apps.imagebreeder;

import com.barrybecker4.common.concurrency.CallableParallelizer;
import com.barrybecker4.common.concurrency.DoneHandler;
import com.barrybecker4.java2d.imageproc.MetaImageOp;
import com.barrybecker4.optimization.parameter.types.Parameter;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
//import java.util.concurrent.Future;

/**
 * Create a set of images from a single MetaImageOp
 */
public class ImageBreeder {

    private MetaImageOp metaOp;

    private float variance;

    private BufferedImage imageToBreed;

    private CallableParallelizer<BufferedImage> parallelizer =
            new CallableParallelizer<>(Runtime.getRuntime().availableProcessors());

    private Map<BufferedImage, List<Parameter>> imgToParamsMap;

    /**
     * Create permutations from the input image based on the metaOp passed in.
     * @param image image to breed
     * @param op metaOp to breed based on.
     * @param variance amount of variation to have in bred images.
     */
    public ImageBreeder(BufferedImage image,  MetaImageOp op, float variance) {
        metaOp = op;
        imageToBreed = image;
        this.variance = variance;
        imgToParamsMap = new HashMap<> ();
    }

    /**
     *
     * @param numChildImages number of child images
     * @return list of bred images
     */
    public List<BufferedImage> breedImages(int numChildImages)  {

        final List<BufferedImage> images =
                Collections.synchronizedList(new ArrayList<BufferedImage>(numChildImages));

        imgToParamsMap.clear();

        List<Callable<BufferedImage>> filterTasks = new ArrayList<Callable<BufferedImage>>(numChildImages);
        for (int i=0; i<numChildImages; i++) {
            filterTasks.add(new Worker(metaOp));
        }

        parallelizer.invokeAllWithCallback(filterTasks, new DoneHandler<BufferedImage>() {
            @Override
            public void done(BufferedImage img) {
                images.add(img);
            }
        });

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

        @Override
        public BufferedImage call() {

            BufferedImageOp randOp = metaOp.getRandomInstance(variance);
            BufferedImage img = randOp.filter(imageToBreed, null );
            // remember the parameters that were used to create this instance;
            imgToParamsMap.put(img, metaOp.getLastUsedParameters());
            return img;
        }
    }
}