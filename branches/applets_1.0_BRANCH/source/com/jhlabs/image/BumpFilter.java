/*
Copyright 2006 Jerry Huxtable

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package com.jhlabs.image;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;

/**
 * A simple embossing filter. 
 */
public class BumpFilter extends ConvolveFilter {
	

    public BumpFilter() {
        super(getEmbossMatrix(0.8f));
    }

	public BumpFilter(float bumpHeight) {
		super(getEmbossMatrix(bumpHeight));
	}
    
    
    /**
     * @param height Height of the simulated bumps. 0-2.0.
     */
	public void setHeight(float height) {
		Kernel k = new Kernel(3, 3, getEmbossMatrix(height));
        this.setKernel(k);
	}
       
    
    private static final float[] getEmbossMatrix(float height)
    {
        float[] embossMatrix = {
            -height, -height,  0.0f,
            -height,  height,  height,
             0.0f,  height,  height
	    };
        return embossMatrix;
    }
    


	public String toString() {
		return "Blur/Emboss Edges";
	}
}
