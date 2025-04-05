//=================================================================
// JavaScript Tree
// Version 1.0
// by Nicholas C. Zakas, nicholas@nczonline.net
// Copyright (c) 2002 Nicholas C. Zakas.  All Rights Reserved.
//-----------------------------------------------------------------
// Browsers Supported:
//  * Netscape 6.1+
//  * Internet Explorer 5.0+
//=================================================================
// History
//-----------------------------------------------------------------
// January 27, 2002 (Version 1.0)
//  - Works in Netscape 6.1+ and IE 5.0+  
//  - Modified by Barry Becker: March 2003
//=================================================================
// Software License
// Copyright (c) 2002 Nicholas C. Zakas.  All Rights Reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions
// are met:
//
// 1. Redistributions of source code must retain the above copyright
//    notice, this list of conditions and the following disclaimer. 
//
// 2. Redistributions in binary form must reproduce the above copyright
//    notice, this list of conditions and the following disclaimer in
//    the documentation and/or other materials provided with the
//    distribution.
//
// 3. The end-user documentation included with the redistribution,
//    if any, must include the following acknowledgment:  
//       "This product includes software developed by the
//        Nicholas C. Zakas (http://www.nczonline.net/)."
//    Alternately, this acknowledgment may appear in the software itself,
//    if and wherever such third-party acknowledgments normally appear.
//
// 4. Redistributions of any form are free for use in non-commercial
//    ventures. If intent is to use in a commercial product, contact
//    Nicholas C. Zakas at nicholas@nczonline.net for purchasing of
//    a commercial license.
//
// THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESSED OR IMPLIED
// WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
// OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED.  IN NO EVENT SHALL NICHOLAS C. ZAKAS  BE LIABLE FOR ANY 
// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
// DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
// GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
// IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED 
// OF THE POSSIBILITY OF SUCH DAMAGE.
//-----------------------------------------------------------------
// Any questions, comments, or suggestions should be e-mailed to 
// nicholas@nczonline.net.  For more information, please visit
// http://www.nczonline.net/. 
//=================================================================


var DIR_IMAGES = "images/";
var IMG_PLUS = DIR_IMAGES + "btnPlus.gif";
var IMG_MINUS = DIR_IMAGES + "btnMinus.gif";

var imgPlus = new Image();
imgPlus.src = IMG_PLUS;
var imgMinus = new Image();
imgMinus.src = IMG_MINUS;

var objLocalTree = null;
var INDENT_WIDTH = 18;

//-----------------------------------------------------------------
// Class jsTree
//
// Description
//  The jsTreeNode class encapsulates the functionality of a tree.
//
// Parameters
//  (none)
//-----------------------------------------------------------------
function jsTree() {

    this.root = null;           //the root node of the tree
    this.nodes = new Array;     //array for all nodes in the tree
   
    //Constructor
    objLocalTree = this;
}

//-----------------------------------------------------------------
// Method jsTree.createRoot()
//
// Description
//  This method creates the root of the tree.
//
// Parameters
//  strIcon (string) - the icon to display for the root.
//  strText (string) - the text to display for the root.
//  strURL (string) - the URL to navigate to when the root is clicked.
//  strTarget (string) - the target for the URL (optional).
//
// Returns
//  The jsTreeNode that was created.
//-----------------------------------------------------------------
jsTree.prototype.createRoot = function(strIcon, strText, strURL, strTarget) {

    this.root = new jsTreeNode(strIcon, strText, strURL, strTarget);
    
    //assign an ID for internal tracking.
    this.root.id = "root";
    this.nodes["root"] = this.root;
    this.root.expanded = true;
  
    return this.root;
}

//-----------------------------------------------------------------
// Method jsTree.buildDOM()
//
// Description
//  This method creates the HTML for the tree.
//
// Parameters
//  (none)
//
// Returns
//  (nothing)
//-----------------------------------------------------------------
jsTree.prototype.buildDOM = function() {

    //call method to add root to document, which will recursively
    //add all other nodes
    this.root.addToDOM(document.body);
}

//-----------------------------------------------------------------
// Method jsTree.toggleExpand()
//
// Description
//  This toggles the expansion of a node identified by an ID.
//
// Parameters
//  strNodeID (string) - the ID of the node that is being expanded/collapsed.
//
// Returns
//  (nothing)
//-----------------------------------------------------------------
jsTree.prototype.toggleExpand = function(strNodeID) {

    //get the node
    var objNode = this.nodes[strNodeID];
    
    //determine whether to expand or collapse
    if (objNode.expanded)
        objNode.collapse();
    else
        objNode.expand();
}

//-----------------------------------------------------------------
// Class jsTreeNode
//
// Description
//  The jsTreeNode class encapsulates the basic information for a node
//  in the tree.
//
// Parameters
//  strIcon (string) - the icon to display for this node.
//  strText (string) - the text to display for this node.
//  strURL (string) - the URL to navigate to when this node is clicked.
//  strTarget (string) - the target for the URL (optional).
//-----------------------------------------------------------------
function jsTreeNode(strIcon, strText, strURL, strTarget) {

    //Public Properties
    this.icon = strIcon;            //the icon to display
    this.text = strText;            //the text to display
    this.url = strURL;              //the URL to link to
    this.target = strTarget;        //the target for the URL
    
    //Private Properties
    this.indent = 0;                //the indent for the node
    
    //Public States 
    this.expanded = false;          //is this node expanded?
 
    //Public Collections 
    this.childNodes = new Array;    //the collection of child nodes
}

//-----------------------------------------------------------------
// Method jsTreeNode.addChild()
//
// Description
//  This method adds a child node to the current node.
//
// Parameters
//  strIcon (string) - the icon to display for this node.
//  strText (string) - the text to display for this node.
//  strURL (string) - the URL to navigate to when this node is clicked.
//  strTarget (string) - the target for the URL (optional).
//
// Returns
//  The jsTreeNode that was created.
//-----------------------------------------------------------------
jsTreeNode.prototype.addChild = function (strIcon, strText, strURL, strTarget) {

    var objNode = new jsTreeNode(strIcon, strText, strURL, strTarget);
    
    //assign an ID for internal tracking
    objNode.id = this.id + "_" + this.childNodes.length;
    
    //assign the indent for this node
    objNode.indent = this.indent + 1;
    
    //add into the array of child nodes
    this.childNodes[this.childNodes.length] = objNode;
    objLocalTree.nodes[objNode.id] = objNode;
    
    return objNode;
}

//-----------------------------------------------------------------
// Method jsTreeNode.addToDOM()
//
// Description
//  This method adds DOM elements to a parent DOM element.
//
// Parameters
//  objDOMParent (HTMLElement) - the parent DOM element to add to.
//
// Returns
//  (nothing)
//-----------------------------------------------------------------
jsTreeNode.prototype.addToDOM = function (objDOMParent) {

    var strHTMLLink = "<a href=\"" + this.url + "\"";
    if (this.target)strHTMLLink += " target=\"" + this.target + "\">";
    
    //create the layer for the node
    var objNodeDiv = document.createElement("div");
    objDOMParent.appendChild(objNodeDiv);
    
    var d = new jsDocument;
 
    d.writeln("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\"><tr>");
    
    //no indent needed for root or level under root
    if (this.indent > 1) {
        d.write("<td width=\"");
        d.write(this.indent * INDENT_WIDTH);
        d.write("\">&nbsp;</td>");
    }
    
    //there is no plus/minus image for the root
    if (this.indent > 0) {
    
        d.write("<td width=\"18\" align=\"center\">");
        
        //if there are children, then add a plus/minus image
        if (this.childNodes.length > 0) {
            d.write("<a href=\"javascript:objLocalTree.toggleExpand('");
            d.write(this.id);
            d.write("')\"><img src=\"");
            d.write(this.expanded ? imgMinus.src : imgPlus.src);
            d.write("\" border=\"0\" hspace=\"1\" id=\"");
            d.write("imgPM_" + this.id);
            d.write("\" /></a>");
        }
        
        d.write("</td>");
    }
    
    //finish by drawing the icon and text 
     d.write("<td width=\"22\">" +strHTMLLink + "<img hspace=\"1\" src=\"" + this.icon + "\" border=\"0\" align=\"absmiddle\" /></a></td>");
    // d.write("<td width=\"22\">" +strHTMLLink + "<img hspace=\"1\"  border=\"0\" align=\"absmiddle\" /></a></td>");

    d.write("<td nowrap=\"nowrap\">" + strHTMLLink + this.text + "</a></td>");
    d.writeln("</tr></table>");
        
    objNodeDiv.innerHTML = d;
    
    //create the layer for the children 
    var objChildNodesLayer = document.createElement("div");
    objChildNodesLayer.setAttribute("id", "divChildren_" + this.id);
    objChildNodesLayer.style.position = "relative";
    objChildNodesLayer.style.display = (this.expanded ? "block" : "none");
    objNodeDiv.appendChild(objChildNodesLayer);
    
    //call for all children
    for (var i=0; i < this.childNodes.length; i++)
        this.childNodes[i].addToDOM(objChildNodesLayer);
}

//-----------------------------------------------------------------
// Method jsTreeNode.collapse()
//
// Description
//  This method expands the jsTreeNode's children to be hidden.
//
// Parameters
//  (none)
//
// Returns
//  (nothing)
//-----------------------------------------------------------------
jsTreeNode.prototype.collapse = function () {

    if (!this.expanded) {
        throw "Node is already collapsed"
    } else {
    
        this.expanded = false;
        document.images["imgPM_" + this.id].src = imgPlus.src;
        
        document.getElementById("divChildren_" + this.id).style.display = "none";
    }
}


//-----------------------------------------------------------------
// Method jsTreeNode.expand()
//
// Description
//  This method expands the jsTreeNode's children to be displayed.
//
// Parameters
//  (none)
//
// Returns
//  (nothing)
//-----------------------------------------------------------------
jsTreeNode.prototype.expand = function () {

    if (this.expanded) {
        throw "Node is already expanded"
    
    } else {
    
        this.expanded = true;
        document.images["imgPM_" + this.id].src = imgMinus.src;
        
        document.getElementById("divChildren_" + this.id).style.display = "block";
    }
}


//-----------------------------------------------------------------
// Object jsDocument
//
// jsDocument helps to speed up printing to the screen from Javascripts.
// Tests have shown that String concatenation (using +=) takes up some
// considerable time.  Many Javascripts create an output string by concatenating
// throughout the run, which eats up the clock.  In order to alleviate that,
// jsDocument stores data in an array, and then joins all array elements with one
// join command, making for much speedier processing.
//-----------------------------------------------------------------
function jsDocument() {
	this.text = new Array();		//array to store the string
	this.write = function (str) { this.text[this.text.length] = str; }
	this.writeln = function (str) { this.text[this.text.length] = str + "\n"; }
	this.toString = function () { return this.text.join(""); }
	this.clear = function () { delete this.text; this.text = null; this.text = new Array; }
}

/*-------------------------------------------------------------------------
 * Utility function for preloading a single named image (so it will be fast)
 * e.g. usage: <BODY onLoad="preload('fooImg', 'foo.gif')">
 */
function preloadImage(imgObj, imgSrc) {
  if (document.images) {
	eval(imgObj+' = new Image()');
	eval(imgObj+'.src = "'+imgSrc+'"');
  }
}
