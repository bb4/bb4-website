// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.lsystem.algorithm.expression;

import com.barrybecker4.common.expression.TreeNode;

import static com.barrybecker4.common.expression.Tokens.*;


/**
 * Turns a tree into a string via in order traversal.
 * Implements visitor pattern
 *
 * @author Barry Becker
 */
public class LTreeSerializer {

    public String serialize(TreeNode node) {
        String serialized = "";
        if (node != null) {
            serialized = traverse(node);
        }
        return serialized.length()>0 ? serialized : "Invalid";
    }

    /** processing for inner nodes */
    private String traverse(TreeNode node) {

        String text = "";
        if (node.children.size() > 0) {
            text += (node.hasParens ? LEFT_PAREN.getSymbol() : "");
            for (TreeNode n : node.children) {
                text += traverse(n);
            }
            text += node.hasParens ? RIGHT_PAREN.getSymbol() : "";
        }
        else {
            text += node.getData();
        }
        return text;
    }
}
