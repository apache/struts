/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/


dojo.provide("dojo.widget.TreeDndControllerV3");

dojo.require("dojo.dnd.TreeDragAndDropV3");
	
dojo.widget.defineWidget(
	"dojo.widget.TreeDndControllerV3",
	[dojo.widget.HtmlWidget, dojo.widget.TreeCommon],
	function() {
		this.dragSources = {};
		this.dropTargets = {};
		this.listenedTrees = {};
	},
{
	listenTreeEvents: ["afterChangeTree","beforeTreeDestroy", "afterAddChild"],
	listenNodeFilter: function(elem) { return elem instanceof dojo.widget.Widget}, 
	
	initialize: function(args) {
		this.treeController = dojo.lang.isString(args.controller) ? dojo.widget.byId(args.controller) : args.controller;
		
		if (!this.treeController) {
			dojo.raise("treeController must be declared");
		}
		
	},

	onBeforeTreeDestroy: function(message) {
		this.unlistenTree(message.source);
	},
	
	// first Dnd registration happens in addChild
	// because I have information about parent on this stage and can use it
	// to check locking or other things
	onAfterAddChild: function(message) {
		//dojo.debug("Dnd addChild "+message.child);
		this.listenNode(message.child);		
	},


	onAfterChangeTree: function(message) {
		/* catch new nodes on afterAddChild, because I need parent */		
		if (!message.oldTree) return;
		
		//dojo.debug("HERE");
		
		if (!message.newTree || !this.listenedTrees[message.newTree.widgetId]) {			
			this.processDescendants(message.node, this.listenNodeFilter, this.unlistenNode);
		}		
		
		if (!this.listenedTrees[message.oldTree.widgetId]) {
			// we have new node
			this.processDescendants(message.node, this.listenNodeFilter, this.listenNode);	
		}
		//dojo.profile.end("onTreeChange");
	},
	
	
	/**
	 * Controller(node model) creates DndNodes because it passes itself to node for synchroneous drops processing
	 * I can't process DnD with events cause an event can't return result success/false
	*/
	listenNode: function(node) {

		//dojo.debug("listen dnd "+node);
		//dojo.debug((new Error()).stack)
		//dojo.profile.start("Dnd listenNode "+node);		
		if (!node.tree.DndMode) return;
		if (this.dragSources[node.widgetId] || this.dropTargets[node.widgetId]) return;

	
		/* I drag label, not domNode, because large domNodes are very slow to copy and large to drag */

		var source = null;
		var target = null;

	
		if (!node.actionIsDisabled(node.actions.MOVE)) {
			//dojo.debug("reg source")
			
			//dojo.profile.start("Dnd source "+node);		
			var source = this.makeDragSource(node);
			//dojo.profile.end("Dnd source "+node);		

			this.dragSources[node.widgetId] = source;
		}

		//dojo.profile.start("Dnd target "+node);		
		//dojo.debug("reg target");
		var target = this.makeDropTarget(node);
		//dojo.profile.end("Dnd target "+node);		

		this.dropTargets[node.widgetId] = target;

		//dojo.profile.end("Dnd listenNode "+node);		


	},
	
	/**
	 * Factory method, override it to create special source
	 */
	makeDragSource: function(node) {
		return new dojo.dnd.TreeDragSourceV3(node.contentNode, this, node.tree.widgetId, node);
	},


	/**
	 * Factory method, override it to create special target
	 */
	makeDropTarget: function(node) {
		 return new dojo.dnd.TreeDropTargetV3(node.contentNode, this.treeController, node.tree.DndAcceptTypes, node);
	},

	unlistenNode: function(node) {

		if (this.dragSources[node.widgetId]) {
			dojo.dnd.dragManager.unregisterDragSource(this.dragSources[node.widgetId]);
			delete this.dragSources[node.widgetId];
		}

		if (this.dropTargets[node.widgetId]) {
			dojo.dnd.dragManager.unregisterDropTarget(this.dropTargets[node.widgetId]);
			delete this.dropTargets[node.widgetId];
		}
	}

});
