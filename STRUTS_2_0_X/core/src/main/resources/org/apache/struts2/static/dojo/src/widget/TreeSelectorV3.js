/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/


dojo.provide("dojo.widget.TreeSelectorV3");

dojo.require("dojo.widget.HtmlWidget");
dojo.require("dojo.widget.TreeCommon");

dojo.widget.defineWidget(
	"dojo.widget.TreeSelectorV3",
	[dojo.widget.HtmlWidget, dojo.widget.TreeCommon],
	function() {
		this.eventNames = {};
		this.listenedTrees = {};
		this.selectedNodes = [];		
	},
{
	// TODO: add multiselect

	listenTreeEvents: ["afterTreeCreate","afterCollapse","afterChangeTree", "afterDetach", "beforeTreeDestroy"],
	listenNodeFilter: function(elem) { return elem instanceof dojo.widget.Widget},	
	
	allowedMulti: true,
	
	eventNamesDefault: {
		select : "select",
		deselect : "deselect",
		dblselect: "dblselect" // select already selected node.. Edit or whatever
	},

	onAfterTreeCreate: function(message) {
		var tree = message.source;
		dojo.event.browser.addListener(tree.domNode, "onclick", dojo.lang.hitch(this, this.onTreeClick));
		if (dojo.render.html.ie) {
			dojo.event.browser.addListener(tree.domNode, "ondblclick", dojo.lang.hitch(this, this.onTreeDblClick));
		}
		dojo.event.browser.addListener(tree.domNode, "onKey", dojo.lang.hitch(this, this.onKey));
		
	},
	
	
	onKey: function(e) {
		if (!e.key || e.ctrkKey || e.altKey) { return; }
		
		switch(e.key) {
			case e.KEY_ENTER:
				var node = this.domElement2TreeNode(e.target);
				if (node) {
					this.processNode(node, e);
				}
		
		}
	},
	
	
		
	onAfterChangeTree: function(message) {
		
		if (!message.oldTree && message.node.selected) {
			this.select(message.node);
		}
		
		if (!message.newTree || !this.listenedTrees[message.newTree.widgetId]) {
			// moving from our trfee to new one that we don't listen
			
			if (this.selectedNode && message.node.children) {
				this.deselectIfAncestorMatch(message.node);
			}						
			
		}
		
		
	},
		
		
		
	initialize: function(args) {

		for(name in this.eventNamesDefault) {
			if (dojo.lang.isUndefined(this.eventNames[name])) {
				this.eventNames[name] = this.widgetId+"/"+this.eventNamesDefault[name];
			}
		}
				
	},

	onBeforeTreeDestroy: function(message) {
		this.unlistenTree(message.source);
	},

	// deselect node if ancestor is collapsed
	onAfterCollapse: function(message) {		
		this.deselectIfAncestorMatch(message.source);		
	},

	// IE will throw select -> dblselect. Need to transform to select->select
	onTreeDblClick: function(event) {
		this.onTreeClick(event);			
	},		
		
	checkSpecialEvent: function(event) {		
		return event.shiftKey || event.ctrlKey;
	},
	
	
	onTreeClick: function(event) {
				
		var node = this.domElement2TreeNode(event.target);
		
		if (!node) {
			return;
		}
		
		var checkLabelClick = function(domElement) {
			return domElement === node.labelNode;
		}
		
		if (this.checkPathCondition(event.target, checkLabelClick)) {
			//dojo.debug("condition ok");
			this.processNode(node, event);			
		}
		
		
	},
	
	
	/**
	 * press on selected with ctrl => deselect it
	 * press on selected w/o ctrl => dblselect it and deselect all other
	 *
	 * press on unselected with ctrl => add it to selection
	 *
	 * event may be both mouse & keyboard enter
	 */
	processNode: function(node, event) {
		
		if (node.actionIsDisabled(node.actions.SELECT)) {
			return;
		}
		
		//dojo.debug("click "+node+ "special "+this.checkSpecialEvent(event));
		//dojo.html.setClass(event.target, "TreeLabel TreeNodeEmphased");
		
		if (dojo.lang.inArray(this.selectedNodes, node)) {			
			if(this.checkSpecialEvent(event)){				
				// If the node is currently selected, and they select it again while holding
				// down a meta key, it deselects it
				this.deselect(node);
				return;
			}
			
			var _this = this;
			var i=0;
			var selectedNode;
			while(this.selectedNodes.length > i) {
				selectedNode = this.selectedNodes[i];
				if (selectedNode !== node) {
					//dojo.debug("Deselect "+selectedNode);
					_this.deselect(selectedNode);
					continue;
				}
			
				i++; // skip the doubleclicked node
			}
		
			
			dojo.event.topic.publish(this.eventNames.dblselect, { node: node });
			return;
		}
		
		// if unselected node..
		
		this.deselectIfNoMulti(event);
		
		//dojo.debug("select");

		this.select(node);

	},
	
	// deselect all if no meta key or disallowed
	deselectIfNoMulti: function(event) {
		if (!this.checkSpecialEvent(event) || !this.allowedMulti) {
			//dojo.debug("deselect All");
			this.deselectAll();
		}
	},

	deselectIfAncestorMatch: function(ancestor) {
		/* deselect all nodes with this ancestor */
		var _this = this;
		dojo.lang.forEach(this.selectedNodes, function(node) {
			var selectedNode = node;
			node = node.parent
			while (node && node.isTreeNode) {
				//dojo.debug("ancestor try "+node);
				
				if (node === ancestor) {
					_this.deselect(selectedNode); 
					return;					
				}
				node = node.parent;
			}
		});
	},
	
			


	onAfterDetach: function(message) {
		this.deselectIfAncestorMatch(message.child);		
	},


	select: function(node) {

		var index = dojo.lang.find(this.selectedNodes, node, true);
		
		if (index >=0 ) {
			return; // already selected
		}
				
		//dojo.debug("select "+node);
		this.selectedNodes.push(node);
						
		dojo.event.topic.publish(this.eventNames.select, {node: node} );
	},


	deselect: function(node){
		var index = dojo.lang.find(this.selectedNodes, node, true);
		if (index < 0) {
			//dojo.debug("not selected");
			return; // not selected
		}
		
		//dojo.debug("deselect "+node);
		//dojo.debug((new Error()).stack);
		
		this.selectedNodes.splice(index, 1);
		dojo.event.topic.publish(this.eventNames.deselect, {node: node} );
		//dojo.debug("deselect");

	},
	
	deselectAll: function() {
		//dojo.debug("deselect all "+this.selectedNodes);
		while (this.selectedNodes.length) {
			this.deselect(this.selectedNodes[0]);
		}
	}

});



