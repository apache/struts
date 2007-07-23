/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/


dojo.provide("dojo.widget.TreeBasicControllerV3");

dojo.require("dojo.event.*");
dojo.require("dojo.json")
dojo.require("dojo.io.*");
dojo.require("dojo.widget.TreeCommon");
dojo.require("dojo.widget.TreeNodeV3");
dojo.require("dojo.widget.TreeV3");

dojo.widget.defineWidget(
	"dojo.widget.TreeBasicControllerV3",
	[dojo.widget.HtmlWidget, dojo.widget.TreeCommon],
	function(){
		this.listenedTrees = {};
	},
{
	// TODO: do something with addChild / setChild, so that RpcController become able
	// to hook on this and report to server
	
	// TODO: make sure keyboard control stuff works when node is moved between trees
	// node should be unfocus()'ed when it its ancestor is moved and tree,lastFocus - cleared

	/**
	 * TreeCommon.listenTree will attach listeners to these events
	 *
	 * The logic behind the naming:
	 * 1. (after|before)
	 * 2. if an event refers to tree, then add "Tree"
	 * 3. add action
	 */
	listenTreeEvents: ["afterSetFolder", "afterTreeCreate", "beforeTreeDestroy"],
	listenNodeFilter: function(elem) { return elem instanceof dojo.widget.Widget},	
		
		
	editor: null,

	
	initialize: function(args) {
		if (args.editor) {
			this.editor = dojo.widget.byId(args.editor);
			this.editor.controller = this;
		}
		
	},
		
	
	getInfo: function(elem) {
		return elem.getInfo();
	},

	onBeforeTreeDestroy: function(message) {
                this.unlistenTree(message.source);
	},

	onAfterSetFolder: function(message) {
		
		//dojo.profile.start("onTreeChange");
        
		if (message.source.expandLevel > 0) {
			this.expandToLevel(message.source, message.source.expandLevel);				
		}
		if (message.source.loadLevel > 0) {
			this.loadToLevel(message.source, message.source.loadLevel);				
		}
			
		
		//dojo.profile.end("onTreeChange");
	},
	

	// down arrow
	_focusNextVisible: function(nodeWidget) {
		
		// if this is an expanded folder, get the first child
		if (nodeWidget.isFolder && nodeWidget.isExpanded && nodeWidget.children.length > 0) {
			returnWidget = nodeWidget.children[0];			
		} else {
			// find a parent node with a sibling
			while (nodeWidget.isTreeNode && nodeWidget.isLastChild()) {
				nodeWidget = nodeWidget.parent;
			}
			
			if (nodeWidget.isTreeNode) {
				var returnWidget = nodeWidget.parent.children[nodeWidget.getParentIndex()+1];				
			}
			
		}
				
		if (returnWidget && returnWidget.isTreeNode) {
			this._focusLabel(returnWidget);
			return returnWidget;
		}
		
	},
	
	// up arrow
	_focusPreviousVisible: function(nodeWidget) {
		var returnWidget = nodeWidget;
		
		// if younger siblings		
		if (!nodeWidget.isFirstChild()) {
			var previousSibling = nodeWidget.parent.children[nodeWidget.getParentIndex()-1]

			nodeWidget = previousSibling;
			// if the previous nodeWidget is expanded, dive in deep
			while (nodeWidget.isFolder && nodeWidget.isExpanded && nodeWidget.children.length > 0) {
				returnWidget = nodeWidget;
				// move to the last child
				nodeWidget = nodeWidget.children[nodeWidget.children.length-1];
			}
		} else {
			// if this is the first child, return the parent
			nodeWidget = nodeWidget.parent;
		}
		
		if (nodeWidget && nodeWidget.isTreeNode) {
			returnWidget = nodeWidget;
		}
		
		if (returnWidget && returnWidget.isTreeNode) {
			this._focusLabel(returnWidget);
			return returnWidget;
		}
		
	},
	
	// right arrow
	_focusZoomIn: function(nodeWidget) {
		var returnWidget = nodeWidget;
		
		// if not expanded, expand, else move to 1st child
		if (nodeWidget.isFolder && !nodeWidget.isExpanded) {
			this.expand(nodeWidget);
		}else if (nodeWidget.children.length > 0) {
			nodeWidget = nodeWidget.children[0];
		}
		
		if (nodeWidget && nodeWidget.isTreeNode) {
			returnWidget = nodeWidget;
		}
		
		if (returnWidget && returnWidget.isTreeNode) {
			this._focusLabel(returnWidget);
			return returnWidget;
		}
		
	},
	
	// left arrow
	_focusZoomOut: function(node) {
		
		var returnWidget = node;
		
		// if not expanded, expand, else move to 1st child
		if (node.isFolder && node.isExpanded) {
			this.collapse(node);
		} else {
			node = node.parent;
		}
		if (node && node.isTreeNode) {
			returnWidget = node;
		}
		
		if (returnWidget && returnWidget.isTreeNode) {
			this._focusLabel(returnWidget);
			return returnWidget;
		}
		
	},
	
	onFocusNode: function(e) {
		var node = this.domElement2TreeNode(e.target);
		
		if (node) {
			node.viewFocus();			
			dojo.event.browser.stopEvent(e);
		}
	},
	
	onBlurNode: function(e) {
		var node = this.domElement2TreeNode(e.target);
		
		if (!node) {
			return;
		}
		
		var labelNode = node.labelNode;
		
		labelNode.setAttribute("tabIndex", "-1");
		node.viewUnfocus();		
		dojo.event.browser.stopEvent(e);
		
		// this could have been set to -1 by the shift+TAB processing
		node.tree.domNode.setAttribute("tabIndex", "0");
		
	},
	
	
	_focusLabel: function(node) {
		//dojo.debug((new Error()).stack)		
		var lastFocused = node.tree.lastFocused;
		var labelNode;
		
		if (lastFocused && lastFocused.labelNode) {
			labelNode = lastFocused.labelNode;
			// help Opera out with blur events
			dojo.event.disconnect(labelNode, "onblur", this, "onBlurNode");
			labelNode.setAttribute("tabIndex", "-1");
			dojo.html.removeClass(labelNode, "TreeLabelFocused");
		}
		
		// set tabIndex so that the tab key can find this node
		labelNode = node.labelNode;
		labelNode.setAttribute("tabIndex", "0");
		node.tree.lastFocused = node;
		
		// add an outline - this helps opera a lot
		dojo.html.addClass(labelNode, "TreeLabelFocused");
		dojo.event.connectOnce(labelNode, "onblur", this, "onBlurNode");
		// prevent the domNode from seeing the focus event
		dojo.event.connectOnce(labelNode, "onfocus", this, "onFocusNode");
		// set focus so that the label wil be voiced using screen readers
		labelNode.focus();
			
	},
	
	onKey: function(e) {
		if (!e.key || e.ctrkKey || e.altKey) { return; }
		// pretend the key was directed toward the current focused node (helps opera out)
		
		var nodeWidget = this.domElement2TreeNode(e.target);
		if (!nodeWidget) {
			return;
		}
		
		var treeWidget = nodeWidget.tree;
		
		if (treeWidget.lastFocused && treeWidget.lastFocused.labelNode) {
			nodeWidget = treeWidget.lastFocused;
		}
		
		switch(e.key) {
			case e.KEY_TAB:
				if (e.shiftKey) {
					// we're moving backwards so don't tab to the domNode
					// it'll be added back in onBlurNode
					treeWidget.domNode.setAttribute("tabIndex", "-1");
				}
				break;
			case e.KEY_RIGHT_ARROW:
				this._focusZoomIn(nodeWidget);
				dojo.event.browser.stopEvent(e);
				break;
			case e.KEY_LEFT_ARROW:
				this._focusZoomOut(nodeWidget);
				dojo.event.browser.stopEvent(e);
				break;
			case e.KEY_UP_ARROW:
				this._focusPreviousVisible(nodeWidget);
				dojo.event.browser.stopEvent(e);
				break;
			case e.KEY_DOWN_ARROW:
				this._focusNextVisible(nodeWidget);
				dojo.event.browser.stopEvent(e);
				break;
		}
	},
	
	
	onFocusTree: function(e) {
		if (!e.currentTarget) { return; }
		try {
			var treeWidget = this.getWidgetByNode(e.currentTarget);
			if (!treeWidget || !treeWidget.isTree) { return; }
			// on first focus, choose the root node
			var nodeWidget = this.getWidgetByNode(treeWidget.domNode.firstChild);
			if (nodeWidget && nodeWidget.isTreeNode) {
				if (treeWidget.lastFocused && treeWidget.lastFocused.isTreeNode) { // onClick could have chosen a non-root node
					nodeWidget = treeWidget.lastFocused;
				}
				this._focusLabel(nodeWidget);
			}
		}
		catch(e) {}
	},

	// perform actions-initializers for tree
	onAfterTreeCreate: function(message) {
		var tree = message.source;
		dojo.event.browser.addListener(tree.domNode, "onKey", dojo.lang.hitch(this, this.onKey));
		dojo.event.browser.addListener(tree.domNode, "onmousedown", dojo.lang.hitch(this, this.onTreeMouseDown));
		dojo.event.browser.addListener(tree.domNode, "onclick", dojo.lang.hitch(this, this.onTreeClick));
		dojo.event.browser.addListener(tree.domNode, "onfocus", dojo.lang.hitch(this, this.onFocusTree));
		tree.domNode.setAttribute("tabIndex", "0");
		
		if (tree.expandLevel) {								
			this.expandToLevel(tree, tree.expandLevel)
		}
		if (tree.loadLevel) {
			this.loadToLevel(tree, tree.loadLevel);
		}
	},

    onTreeMouseDown: function(e) {
    },

	onTreeClick: function(e){
		//dojo.profile.start("onTreeClick");
		
		var domElement = e.target;
		//dojo.debug('click')
		// find node
        var node = this.domElement2TreeNode(domElement);		
		if (!node || !node.isTreeNode) {
			return;
		}
		
		
		var checkExpandClick = function(el) {
			return el === node.expandNode;
		}
		
		if (this.checkPathCondition(domElement, checkExpandClick)) {
			this.processExpandClick(node);			
		}
		
		this._focusLabel(node);
		
		//dojo.profile.end("onTreeClick");
		
	},
	
	processExpandClick: function(node){
		
		//dojo.profile.start("processExpandClick");
		
		if (node.isExpanded){
			this.collapse(node);
		} else {
			this.expand(node);
		}
		
		//dojo.profile.end("processExpandClick");
	},
		
	
	
	/**
	 * time between expand calls for batch operations
	 * @see expandToLevel
	 */
	batchExpandTimeout: 20,
	
	
	expandAll: function(nodeOrTree) {		
		return this.expandToLevel(nodeOrTree, Number.POSITIVE_INFINITY);
		
	},
	
	
	collapseAll: function(nodeOrTree) {
		var _this = this;
		
		var filter = function(elem) {
			return (elem instanceof dojo.widget.Widget) && elem.isFolder && elem.isExpanded;
		}
		
		if (nodeOrTree.isTreeNode) {		
			this.processDescendants(nodeOrTree, filter, this.collapse);
		} else if (nodeOrTree.isTree) {
			dojo.lang.forEach(nodeOrTree.children,function(c) { _this.processDescendants(c, filter, _this.collapse) });
		}
	},
	
	/**
	 * expand tree to specific node
	 */
	expandToNode: function(node) {
		n = node.parent
		s = []
		while (!n.isExpanded) {
			s.push(n)
			n = n.parent
		}
				
		dojo.lang.forEach(s, function(n) { n.expand() })
	},
		
	/**
	 * walk a node in time, forward order, with pauses between expansions
	 */
	expandToLevel: function(nodeOrTree, level) {
		dojo.require("dojo.widget.TreeTimeoutIterator");
		
		var _this = this;
		var filterFunc = function(elem) {
			var res = elem.isFolder || elem.children && elem.children.length;
			//dojo.debug("Filter "+elem+ " result:"+res);
			return res;
		};
		var callFunc = function(node, iterator) {			
			 _this.expand(node, true);
			 iterator.forward();
		}
			
		var iterator = new dojo.widget.TreeTimeoutIterator(nodeOrTree, callFunc, this);
		iterator.setFilter(filterFunc);
		
		
		iterator.timeout = this.batchExpandTimeout;
		
		//dojo.debug("here "+nodeOrTree+" level "+level);
		
		iterator.setMaxLevel(nodeOrTree.isTreeNode ? level-1 : level);
		
		
		return iterator.start(nodeOrTree.isTreeNode);
	},
	

	getWidgetByNode: function(node) {
		var widgetId;
		var newNode = node;
		while (! (widgetId = newNode.widgetId) ) {
			newNode = newNode.parentNode;
			if (newNode == null) { break; }
		}
		if (widgetId) { return dojo.widget.byId(widgetId); }
		else if (node == null) { return null; }
		else{ return dojo.widget.manager.byNode(node); }
	},



	/**
	 * callout activated even if node is expanded already
	 */
	expand: function(node) {
		
		//dojo.profile.start("expand");
		
		//dojo.debug("Expand "+node.isFolder);
		
		if (node.isFolder) {			
			node.expand(); // skip trees or non-folders
		}		
		
		//dojo.profile.end("expand");
				
	},

	/**
	 * safe to call on tree and non-folder
	 */
	collapse: function(node) {
		if (node.isFolder) {
			node.collapse();
		}
	},
	
	
	// -------------------------- TODO: Inline edit node ---------------------
	canEditLabel: function(node) {
		if (node.actionIsDisabledNow(node.actions.EDIT)) return false;

		return true;
	},
	
		
	editLabelStart: function(node) {		
		if (!this.canEditLabel(node)) {
			return false;
		}
		
		if (!this.editor.isClosed()) {
			//dojo.debug("editLabelStart editor open");
			this.editLabelFinish(this.editor.saveOnBlur);			
		}
				
		this.doEditLabelStart(node);
		
	
	},
	
	
	editLabelFinish: function(save) {
		this.doEditLabelFinish(save);		
	},
	
	
	doEditLabelStart: function(node) {
		if (!this.editor) {
			dojo.raise(this.widgetType+": no editor specified");
		}
		
		//dojo.debug("editLabelStart editor open "+node);
		
		this.editor.open(node);		
	},
	
	doEditLabelFinish: function(save, server_data) {
		//dojo.debug("Finish "+save);
		//dojo.debug((new Error()).stack)
		if (!this.editor) {
			dojo.raise(this.widgetType+": no editor specified");
		}

		var node = this.editor.node;	
		var editorTitle = this.editor.getContents();
		
		this.editor.close(save);

		if (save) {
			var data = {title:editorTitle};
			
			if (server_data) { // may be undefined
				dojo.lang.mixin(data, server_data);
			}
			
			
			if (node.isPhantom) {			
				// I can't just set node phantom's title, because widgetId/objectId/widgetName...
				// may be provided by server
				var parent = node.parent;
				var index = node.getParentIndex();				
				node.destroy();
				// new node was added!
				dojo.widget.TreeBasicControllerV3.prototype.doCreateChild.call(this, parent, index, data);
			} else {
				var title = server_data && server_data.title ? server_data.title : editorTitle;
				// use special method to make sure everything updated and event sent
				node.setTitle(title); 
			}
		} else {
			//dojo.debug("Kill phantom on cancel");
			if (node.isPhantom) {
				node.destroy();
			}
		}
	},
	
	
		
	makeDefaultNode: function(parent, index) {
		var data = {title:parent.tree.defaultChildTitle};
		return dojo.widget.TreeBasicControllerV3.prototype.doCreateChild.call(this,parent,index,data);
	},
	
	/**
	 * check that something is possible
	 * run maker to do it
	 * run exposer to expose result to visitor immediatelly
	 *   exposer does not affect result
	 */
	runStages: function(check, prepare, make, finalize, expose, args) {
		
		if (check && !check.apply(this, args)) {
			return false;
		}
		
		if (prepare && !prepare.apply(this, args)) {
			return false;
		}
		
		var result = make.apply(this, args);
		
		
		if (finalize) {
			finalize.apply(this,args);			
		}
			
		if (!result) {
			return result;
		}
		
			
		if (expose) {
			expose.apply(this, args);
		}
		
		return result;
	}
});


// create and edit
dojo.lang.extend(dojo.widget.TreeBasicControllerV3, {
		
	createAndEdit: function(parent, index) {
		var data = {title:parent.tree.defaultChildTitle};
		
		if (!this.canCreateChild(parent, index, data)) {
			return false;
		}
		
		var child = this.doCreateChild(parent, index, data);
		if (!child) return false;
		this.exposeCreateChild(parent, index, data);
		
		child.isPhantom = true;
		
		if (!this.editor.isClosed()) {
			//dojo.debug("editLabelStart editor open");
			this.editLabelFinish(this.editor.saveOnBlur);			
		}
		
		
				
		this.doEditLabelStart(child);		
	
	}
	
});


// =============================== clone ============================
dojo.lang.extend(dojo.widget.TreeBasicControllerV3, {
	
	canClone: function(child, newParent, index, deep){
		return true;
	},
	
	
	clone: function(child, newParent, index, deep) {
		return this.runStages(
			this.canClone, this.prepareClone, this.doClone, this.finalizeClone, this.exposeClone, arguments
		);			
	},

	exposeClone: function(child, newParent) {
		if (newParent.isTreeNode) {
			this.expand(newParent);
		}
	},

	doClone: function(child, newParent, index, deep) {
		//dojo.debug("Clone "+child);
		var cloned = child.clone(deep);
		newParent.addChild(cloned, index);
				
		return cloned;
	}
	

});

// =============================== detach ============================

dojo.lang.extend(dojo.widget.TreeBasicControllerV3, {
	canDetach: function(child) {
		if (child.actionIsDisabledNow(child.actions.DETACH)) {
			return false;
		}

		return true;
	},


	detach: function(node) {
		return this.runStages(
			this.canDetach, this.prepareDetach, this.doDetach, this.finalizeDetach, this.exposeDetach, arguments
		);			
	},


	doDetach: function(node, callObj, callFunc) {
		node.detach();
	}
	
});


// =============================== destroy ============================
dojo.lang.extend(dojo.widget.TreeBasicControllerV3, {

	canDestroyChild: function(child) {
		
		if (child.parent && !this.canDetach(child)) {
			return false;
		}
		return true;
	},


	destroyChild: function(node) {
		return this.runStages(
			this.canDestroyChild, this.prepareDestroyChild, this.doDestroyChild, this.finalizeDestroyChild, this.exposeDestroyChild, arguments
		);			
	},


	doDestroyChild: function(node) {
		node.destroy();
	}
	
});



// =============================== move ============================

dojo.lang.extend(dojo.widget.TreeBasicControllerV3, {

	/**
	 * check for non-treenodes
	 */
	canMoveNotANode: function(child, parent) {
		if (child.treeCanMove) {
			return child.treeCanMove(parent);
		}
		
		return true;
	},

	/**
	 * Checks whether it is ok to change parent of child to newParent
	 * May incur type checks etc
	 *
	 * It should check only hierarchical possibility w/o index, etc
	 * because in onDragOver event for Between Dnd mode we can't calculate index at once on onDragOVer.
	 * index changes as client moves mouse up-down over the node
	 */
	canMove: function(child, newParent){
		if (!child.isTreeNode) {
			return this.canMoveNotANode(child, newParent);
		}
						
		if (child.actionIsDisabledNow(child.actions.MOVE)) {
			return false;
		}

		// if we move under same parent then no matter if ADDCHILD disabled for him
		// but if we move to NEW parent then check if action is disabled for him
		// also covers case for newParent being a non-folder in strict mode etc
		if (child.parent !== newParent && newParent.actionIsDisabledNow(newParent.actions.ADDCHILD)) {
			return false;
		}

		// Can't move parent under child. check whether new parent is child of "child".
		var node = newParent;
		while(node.isTreeNode) {
			//dojo.debugShallow(node.title)
			if (node === child) {
				// parent of newParent is child
				return false;
			}
			node = node.parent;
		}

		return true;
	},


	move: function(child, newParent, index/*,...*/) {
		return this.runStages(this.canMove, this.prepareMove, this.doMove, this.finalizeMove, this.exposeMove, arguments);			
	},

	doMove: function(child, newParent, index) {
		//dojo.debug("MOVE "+child);
		child.tree.move(child, newParent, index);

		return true;
	},
	
	exposeMove: function(child, newParent) {		
		if (newParent.isTreeNode) {
			this.expand(newParent);
		}
	}
		

});

dojo.lang.extend(dojo.widget.TreeBasicControllerV3, {

	// -----------------------------------------------------------------------------
	//                             Create node stuff
	// -----------------------------------------------------------------------------


	canCreateChild: function(parent, index, data) {
		if (parent.actionIsDisabledNow(parent.actions.ADDCHILD)) {
			return false;
		}

		return true;
	},


	/* send data to server and add child from server */
	/* data may contain an almost ready child, or anything else, suggested to server */
	/*in Rpc controllers server responds with child data to be inserted */
	createChild: function(parent, index, data) {
		return this.runStages(this.canCreateChild, this.prepareCreateChild, this.doCreateChild, this.finalizeCreateChild, this.exposeCreateChild, arguments);		
	},


	doCreateChild: function(parent, index, data) {
		//dojo.debug("doCreateChild parent "+parent+" index "+index+" data "+data);
		
		var newChild = parent.tree.createNode(data); 
		//var newChild = dojo.widget.createWidget(widgetType, data);

		parent.addChild(newChild, index);

		return newChild;
	},
	
	exposeCreateChild: function(parent) {
		return this.expand(parent);
	}


});
