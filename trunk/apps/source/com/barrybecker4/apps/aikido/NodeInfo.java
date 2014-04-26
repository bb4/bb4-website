// Copyright by Barry G. Becker, 2014. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.apps.aikido;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 *
 * @author Barry Becker
 */
class NodeInfo {

    private static final String IMG_SUFFIX = "_s.jpg";

    private String id;
    private String image;
    private String label;

    NodeInfo(String imagePath, NamedNodeMap attributebMap) {
        if (attributebMap == null) {
            id = null;
            image = null;
            label = null;
        } else {
            for (int i = 0; i < attributebMap.getLength(); i++) {
                Node attr = attributebMap.item(i);
                if ("id".equals(attr.getNodeName())) {
                    id = attr.getNodeValue();
                    // the id gets reused for the image name
                    image = imagePath + attr.getNodeValue() + IMG_SUFFIX;
                } else if ("label".equals(attr.getNodeName()))
                    label = attr.getNodeValue();
            }
        }
    }

    String getId() {
        return id;
    }

    String getImage() {
        return image;
    }

    String getLabel() {
        return label;
    }
}
