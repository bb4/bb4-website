// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.common.expression.mathexpression;

import com.barrybecker4.common.expression.OperatorsDefinition;
import com.barrybecker4.common.expression.TreeNode;
import junit.framework.TestCase;

/**
 * @author Barry Becker
 */
public class MathExpressionParserTest extends TestCase {

    /** instance under test */
    private MathExpressionParser parser;



    /** used to verify parsed tree */
    private TreeSerializer serializer;

    /**
     * common initialization for all go test cases.
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        OperatorsDefinition opDef = new MathOperatorsDefinition();
        parser = new MathExpressionParser(opDef);
        serializer = new TreeSerializer();
    }

    public void testXOnlyExp() {
        verifyParse("x");
    }

    public void testNegXExp() {
        verifyParse("x");
    }

    public void testConstantIntOnlyExp() {
        verifyParse("5", "5.0");
    }

    public void testNegativeConstantIntExp() {
        verifyParse("-5", "-5.0");
    }

    public void testNegativeConstantDecimalExp() {
        verifyParse("-5.3");
    }

    public void testScaledXExp() {
        verifyParse("2.3x", "2.3 * x");
    }

    public void testScaledXExpWithSpaces() {
        verifyParse("2.3x", "2.3 * x");
    }

    public void testXsquaredExp() {
        verifyParse("x*x", "x * x");
    }

    public void testXtimesNegXExp() {
        verifyParse("x*-x", "x * -1 * x");
    }

    /*
            "x^x-3x",
            "x^-x-3x",
            "-x*x",
            "x*x*x",
            "x^2",
            "x^3 - x^3",
            "x-2",
            "2-x",
            "5x",
            "1/6x",
            "3x - 1",
            "(2x + 1) - 3",
            "3(6x - 2)",
            "(3 + x) - (x - 2)",
            "3x - 2x^-2",
            "-3x^2 - 1",
            "4 --4",
            "2(x + 1)(x-1)",
            "-3x + (4x^2 - 5) / (x^-3 + x^2 - (1/x + 4)) (x + 1)",
            "(3 + 2(x + 3x^(5+x))/ 2x) - 4x(3+1/x)^(2x(8-x))",
            "-1 - -2(4 + x)",
            "(1/6x)^(2(x + 3(x^2/3) -1))",
            "((x + 4) / (x^2 - 1)) + 1",
            "1 + ((x + 4) / (x^2 - 1))",
            "2 + (x^2 - (x + (x  + (x + (x - (3x -(x + (x + 1))))))))/ 2",
            "(1 + ((x + 4) / (x^2 - 1)) / ((2x + 4) / (x^2 - 1))   +   (x + ((x + 4) / (x^2 - 1)) / ((2x + 4) / (x^2 - 1)))) *(1 + ((x + 4) / (x^2 - 1)) / ((2x + 4) / (x^2 - 1))   +   (x + ((x + 4) / (x^2 - 1)) / ((2x + 4) / (x^2 - 1))))"
                  */

    /**
     * @param exp the expression to parse
     */
    private void verifyParse(String exp) {
        verifyParse(exp, exp);
    }

    /**
     * @param exp the expression to parse
     */
    private void verifyParse(String exp, String expSerializedStr) {
        TreeNode root = parser.parse(exp);
        String serialized = serializer.serialize(root);
        assertEquals("Unexpected parsed expression tree.",
                expSerializedStr, serialized);
    }

}
