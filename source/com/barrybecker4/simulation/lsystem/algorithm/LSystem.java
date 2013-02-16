// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.lsystem.algorithm;

/**
 *
 */
public class LSystem {

	/** Default set of available characters. */
	protected char [] alphabetTokens = {'F', '+', '-', '[', ']'};

	/** Set of available characters. */
	protected char [] alphabet;

	/** Default initial configuration. */
	protected String defAxiom = "F";

	/** Initial configuration of L-system. */
	protected String axiom;

	/** Default production rule. */
	protected String [] defRule = {"F[+F]F[-F]F", "+", "-", "[", "]"};

	/**
	 * Production rule of each character of the alphabetTokens. There
	 * must exist one rule per character.
	 */
	protected	String [] rule;

	/** Container to hold the current state of the L-system. */
	protected	String tree;


	/** Constructor. */
	public LSystem() {
		alphabet = alphabetTokens;
		axiom = defAxiom;
		int numLetters = defRule.length;
		rule = new String[numLetters];
		for (int i=0; i<numLetters; i++) {
			rule[i] = defRule[i];
        }
		tree = "";
	}

	/**
	 * Generate the tree by applying the rules until the
	 * description has a given length.
	 * @param maxLength maximal length of tree until we continue to iterate.
	 */
	public String generateTree(int maxLength) {
		tree = axiom;
		int [] ruleLen = new int[alphabet.length];

		for (int j=0; j< alphabet.length; j++) {
			ruleLen[j] = rule[j].length();
        }

		for (int num=0; num < maxLength; num++) {
			int len = tree.length();
			int newLen = 0;
			for (int i=0; i<len; i++) {
				char c = tree.charAt(i);
				for (int j=0; j< alphabet.length; j++) {
					if (c == alphabet[j]) {
						newLen += ruleLen[j];
						break;
					}
				}
			}

			StringBuilder newTree = new StringBuilder(newLen);
			for (int i=0; i < len; i++) {
				char c = tree.charAt(i);
				for (int j=0; j< alphabet.length; j++) {
					if (c == alphabet[j]) {
						newTree.append(rule[j]);
						break;
					}
				}
			}
			tree = newTree.toString();
		}
        return tree;
	}
}