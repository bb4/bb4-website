// Copyright by Barry G. Becker, 2014. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.apps.aikido.generation;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Represents a xml element (i.e. a step in the technique) in the document object model.
 * @author Barry Becker
 */
class NodeInfo {

    private static final String IMG_SUFFIX = ".jpg";

    private String id;
    private String image;
    private String label;
    private String description;

    NodeInfo(String imagePath, Node node) {

        NamedNodeMap attributeMap = node.getAttributes();
        if (attributeMap == null || !"node".equals(node.getNodeName())) {
            id = null;
            image = null;
            label = null;
        } else {
            for (int i = 0; i < attributeMap.getLength(); i++) {
                Node attr = attributeMap.item(i);
                String name = attr.getNodeName();

                switch (name) {
                    case "id" :
                        id = attr.getNodeValue();
                        // the id gets reused for the image name
                        image = imagePath + attr.getNodeValue() + IMG_SUFFIX;
                        break;
                    case "label" :
                        label = attr.getNodeValue();
                        break;
                    case "description" :
                        description = attr.getNodeValue();
                        break;
                    default:
                        throw new IllegalArgumentException("Unexpected attribute '" + name + "' on element.");
                }
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

    /** @return the description, or label if no description was provided. */
    String getDescription() {
        return (description != null) ? description : label;
    }
}
