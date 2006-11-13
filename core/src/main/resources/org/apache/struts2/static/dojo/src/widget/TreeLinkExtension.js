/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/


dojo.provide("dojo.widget.TreeLinkExtension");

dojo.require("dojo.widget.HtmlWidget");
dojo.require("dojo.widget.TreeExtension");

dojo.widget.defineWidget(
	"dojo.widget.TreeLinkExtension",
	dojo.widget.TreeExtension,
	function() {
		this.params = {};
	},
{
	/**
	 * can only listen, no unlisten
	 */

	listenTreeEvents: ["afterChangeTree"],	

	listenTree: function(tree) {
		
		dojo.widget.TreeCommon.prototype.listenTree.call(this,tree);
		
		var labelNode = tree.labelNodeTemplate;
		var newLabel = this.makeALabel();
		dojo.html.setClass(newLabel, dojo.html.getClass(labelNode));
		labelNode.parentNode.replaceChild(newLabel, labelNode);		
	},
	
		
	
	makeALabel: function() {		
		var newLabel = document.createElement("a");
		
		for(var key in this.params) {
			if (key in {}) continue;
			newLabel.setAttribute(key, this.params[key]);
		}
		
		return newLabel;
	},
		
	
	onAfterChangeTree: function(message) {
		var _this = this;
		
		
		// only for new nodes
		if (!message.oldTree) {
			this.listenNode(message.node);
		}
		
	},
	
	listenNode: function(node) {
		for(var key in node.object) {
			if (key in {}) continue;
			node.labelNode.setAttribute(key, node.object[key]);
		}
	}


});
