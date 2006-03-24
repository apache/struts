/*
	Copyright (c) 2004-2005, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.widget.Tree");
dojo.provide("dojo.widget.HtmlTree");
dojo.provide("dojo.widget.TreeNode");
dojo.provide("dojo.widget.HtmlTreeNode");

dojo.require("dojo.event.*");
dojo.require("dojo.fx.html");
dojo.require("dojo.widget.LayoutPane");

// make it a tag
dojo.widget.tags.addParseTreeHandler("dojo:Tree");
dojo.widget.tags.addParseTreeHandler("dojo:TreeNode");

dojo.widget.HtmlTree = function() {
	dojo.widget.html.LayoutPane.call(this);
}
dojo.inherits(dojo.widget.HtmlTree, dojo.widget.html.LayoutPane);

dojo.lang.extend(dojo.widget.HtmlTree, {
	widgetType: "Tree",
	isContainer: true,

	domNode: null,

	templateCssPath: dojo.uri.dojoUri("src/widget/templates/Tree.css"),
	templateString: '<div class="dojoTree"></div>',

	selectedNode: null,
	toggler: null,


	//
	// these icons control the grid and expando buttons for the whole tree
	//

	blankIconSrc: dojo.uri.dojoUri("src/widget/templates/images/treenode_blank.gif").toString(),

	gridIconSrcT: dojo.uri.dojoUri("src/widget/templates/images/treenode_grid_t.gif").toString(), // for non-last child grid
	gridIconSrcL: dojo.uri.dojoUri("src/widget/templates/images/treenode_grid_l.gif").toString(), // for last child grid
	gridIconSrcV: dojo.uri.dojoUri("src/widget/templates/images/treenode_grid_v.gif").toString(), // vertical line
	gridIconSrcP: dojo.uri.dojoUri("src/widget/templates/images/treenode_grid_p.gif").toString(), // for under parent item child icons
	gridIconSrcC: dojo.uri.dojoUri("src/widget/templates/images/treenode_grid_c.gif").toString(), // for under child item child icons
	gridIconSrcX: dojo.uri.dojoUri("src/widget/templates/images/treenode_grid_x.gif").toString(), // grid for sole root item
	gridIconSrcY: dojo.uri.dojoUri("src/widget/templates/images/treenode_grid_y.gif").toString(), // grid for last rrot item
	gridIconSrcZ: dojo.uri.dojoUri("src/widget/templates/images/treenode_grid_z.gif").toString(), // for under root parent item child icon

	expandIconSrcPlus: dojo.uri.dojoUri("src/widget/templates/images/treenode_expand_plus.gif").toString(),
	expandIconSrcMinus: dojo.uri.dojoUri("src/widget/templates/images/treenode_expand_minus.gif").toString(),

	iconWidth: 18,
	iconHeight: 18,


	//
	// tree options
	//

	showGrid: true,
	showRootGrid: true,

	toggle: "default",
	toggleDuration: 150,


	//
	// subscribable events
	//

	publishSelectionTopic: "",
	publishExpandedTopic: "",
	publishCollapsedTopic: "",


	initialize: function(args, frag){
		switch (this.toggle) {
			case "fade": this.toggler = new dojo.widget.Tree.FadeToggle(); break;
			case "wipe": this.toggler = new dojo.widget.Tree.WipeToggle(); break;
			default    : this.toggler = new dojo.widget.Tree.DefaultToggle();
		}
	},

	postCreate: function(){
		this.buildTree();
	},

	buildTree: function(){

		dojo.html.disableSelection(this.domNode);

		for(var i=0; i<this.children.length; i++){

			this.children[i].isFirstNode = (i == 0) ? true : false;
			this.children[i].isLastNode = (i == this.children.length-1) ? true : false;

			var node = this.children[i].buildNode(this, 0);

			this.domNode.appendChild(node);
		}


		//
		// when we don't show root toggles, we need to auto-expand root nodes
		//

		if (!this.showRootGrid){
			for(var i=0; i<this.children.length; i++){
				this.children[i].expand();
			}
		}

		for(var i=0; i<this.children.length; i++){
			this.children[i].startMe();
		}
	},

	addChild: function(child){

		//
		// this function gets called to add nodes to both trees and nodes, so it's a little confusing :)
		//

		if (child.widgetType != 'TreeNode'){
			dojo.raise("You can only add TreeNode widgets to a "+this.widgetType+" widget!");
			return;
		}

		if (this.children.length){

			var lastChild = this.children[this.children.length-1];
			lastChild.isLastNode = false;
			lastChild.updateIconTree();
		}else{

			if (this.widgetType == 'TreeNode'){
				this.isParent = true;
				this.isExpanded = false;
				this.updateIcons();
			}

			child.isFirstNode = true;
		}



		if (this.widgetType == 'TreeNode'){

			var childDepth = this.depth+1;
			var childTree = this.tree;

			child.parentNode = this;
			child.isLastNode = true;

		}else{
			var childDepth = 0;
			var childTree = this;

			child.isLastNode = true;
		}

		this.children.push(child);
		var node = child.buildNode(childTree, childDepth);

		if (this.widgetType == 'Tree'){
			this.domNode.appendChild(node);
		}else{
			this.containerNode.appendChild(node);
		}

		child.startMe();
	}
});


dojo.widget.HtmlTreeNode = function() {
	dojo.widget.HtmlWidget.call(this);
}

dojo.inherits(dojo.widget.HtmlTreeNode, dojo.widget.HtmlWidget);

dojo.lang.extend(dojo.widget.HtmlTreeNode, {
	widgetType: "TreeNode",
	isContainer: true,
	messWithMyChildren: true,

	domNode: null,
	continerNode: null,

	templateString: '<div class="dojoTreeNode"><div dojoAttachPoint="containerNode"></div></div>',

	childIconSrc: '',

	childIcon: null,
	underChildIcon: null,
	expandIcon: null,

	title: "",

	labelNode: null, // the item label
	imgs: null, // an array of icons imgs
	rowNode: null, // the tr

	tree: null,
	parentNode: null,
	depth: 0,

	isFirstNode: false,
	isLastNode: false,
	isExpanded: false,
	isParent: false,
	booted: false,

	buildNode: function(tree, depth){

		this.tree = tree;
		this.depth = depth;


		//
		// add the tree icons
		//

		this.imgs = [];

		for(var i=0; i<this.depth+2; i++){

			var img = document.createElement('img');

			img.style.width = this.tree.iconWidth + 'px';
			img.style.height = this.tree.iconHeight + 'px';
			img.src = this.tree.blankIconSrc;
			img.style.verticalAlign = 'middle';

			this.domNode.insertBefore(img, this.containerNode);

			this.imgs.push(img);
		}

		this.expandIcon = this.imgs[this.imgs.length-2];
		this.childIcon = this.imgs[this.imgs.length-1];


		//
		// add the cell label
		//


		this.labelNode = document.createElement('span');

		this.labelNode.appendChild(document.createTextNode(this.title));

		this.domNode.insertBefore(this.labelNode, this.containerNode);

		dojo.html.addClass(this.labelNode, 'dojoTreeNodeLabel');


		dojo.event.connect(this.expandIcon, 'onclick', this, 'onTreeClick');
		dojo.event.connect(this.childIcon, 'onclick', this, 'onIconClick');
		dojo.event.connect(this.labelNode, 'onclick', this, 'onLabelClick');


		//
		// create the child rows
		//

		for(var i=0; i<this.children.length; i++){

			this.children[i].isFirstNode = (i == 0) ? true : false;
			this.children[i].isLastNode = (i == this.children.length-1) ? true : false;
			this.children[i].parentNode = this;
			var node = this.children[i].buildNode(this.tree, this.depth+1);

			this.containerNode.appendChild(node);
		}

		this.isParent = (this.children.length > 0) ? true : false;

		this.collapse();

		return this.domNode;
	},

	onTreeClick: function(e){

		if (this.isExpanded){
			this.collapse();
		}else{
			this.expand();
		}
	},

	onIconClick: function(){
		this.onLabelClick();
	},

	onLabelClick: function(){

		if (this.tree.selectedNode == this){

			//this.editInline();
			dojo.debug('TODO: start inline edit here!');
			return;
		}

		if (this.tree.selectedNode){ this.tree.selectedNode.deselect(); }

		this.tree.selectedNode = this;
		this.tree.selectedNode.select();
	},

	select: function(){

		dojo.html.addClass(this.labelNode, 'dojoTreeNodeLabelSelected');

		dojo.event.topic.publish(this.tree.publishSelectionTopic, this.widgetId);
	},

	deselect: function(){

		dojo.html.removeClass(this.labelNode, 'dojoTreeNodeLabelSelected');
	},

	updateIcons: function(){

		this.imgs[0].style.display = this.tree.showRootGrid ? 'inline' : 'none';


		//
		// set the expand icon
		//

		if (this.isParent){
			this.expandIcon.src = this.isExpanded ? this.tree.expandIconSrcMinus : this.tree.expandIconSrcPlus;
		}else{
			this.expandIcon.src = this.tree.blankIconSrc;
		}


		//
		// set the grid under the expand icon
		//

		if (this.tree.showGrid){
			if (this.depth){

				this.setGridImage(-2, this.isLastNode ? this.tree.gridIconSrcL : this.tree.gridIconSrcT);
			}else{
				if (this.isFirstNode){
					this.setGridImage(-2, this.isLastNode ? this.tree.gridIconSrcX : this.tree.gridIconSrcY);
				}else{
					this.setGridImage(-2, this.isLastNode ? this.tree.gridIconSrcL : this.tree.gridIconSrcT);
				}
			}
		}else{
			this.setGridImage(-2, this.tree.blankIconSrc);
		}


		//
		// set the child icon
		//

		if (this.childIconSrc){
			this.childIcon.style.display = 'inline';
			this.childIcon.src = this.childIconSrc;
		}else{
			this.childIcon.style.display = 'none';
		}


		//
		// set the grid under the child icon
		//

		if ((this.depth || this.tree.showRootGrid) && this.tree.showGrid){

			this.setGridImage(-1, (this.isParent && this.isExpanded) ? this.tree.gridIconSrcP : this.tree.gridIconSrcC);
		}else{
			if (this.tree.showGrid && !this.tree.showRootGrid){

				this.setGridImage(-1, (this.isParent && this.isExpanded) ? this.tree.gridIconSrcZ : this.tree.blankIconSrc);
			}else{
				this.setGridImage(-1, this.tree.blankIconSrc);
			}
		}


		//
		// set the vertical grid icons
		//

		var parent = this.parentNode;

		for(var i=0; i<this.depth; i++){

			var idx = this.imgs.length-(3+i);

			this.setGridImage(idx, (this.tree.showGrid && !parent.isLastNode) ? this.tree.gridIconSrcV : this.tree.blankIconSrc);

			parent = parent.parentNode;
		}

	},

	setGridImage: function(idx, src){

		if (idx < 0){
			idx = this.imgs.length + idx;
		}

		this.imgs[idx].style.backgroundImage = 'url(' + src + ')';
	},

	updateIconTree: function(){

		this.updateIcons();

		for(var i=0; i<this.children.length; i++){
			this.children[i].updateIconTree();
		}
	},

	expand: function(){
		this.showChildren();
		this.isExpanded = true;
		this.updateIcons();
	},

	collapse: function(){
		this.hideChildren();
		this.isExpanded = false;
		this.updateIcons();
	},

	hideChildren: function(){

		if (this.booted){
			this.tree.toggler.hide(this.containerNode);
		}else{
			this.containerNode.style.display = 'none';
		}
		dojo.event.topic.publish(this.tree.publishCollapsedTopic, this.widgetId);
	},

	showChildren: function(){

		if (this.booted){
			this.tree.toggler.show(this.containerNode);
		}else{
			this.containerNode.style.display = 'block';
		}
		dojo.event.topic.publish(this.tree.publishExpandedTopic, this.widgetId);
	},

	startMe: function(){

		this.booted = true;
		for(var i=0; i<this.children.length; i++){
			this.children[i].startMe();
		}
	},

	addChild: function(child){

		this.tree.addChild.call(this, child);
	}

});

dojo.widget.Tree.DefaultToggle = function(){

	this.show = function(node){
		node.style.display = 'block';
	}

	this.hide = function(node){
		node.style.display = 'none';
	}
}

dojo.widget.Tree.FadeToggle = function(duration){
	this.toggleDuration = duration ? duration : 150;

	this.show = function(node){
		node.style.display = 'block';
		dojo.fx.html.fade(node, this.toggleDuration, 0, 1);
	}

	this.hide = function(node){
		dojo.fx.html.fadeOut(node, this.toggleDuration, function(node){ node.style.display = 'none'; });
	}
}

dojo.widget.Tree.WipeToggle = function(duration){
	this.toggleDuration = duration ? duration : 150;

	this.show = function(node){
		node.style.display = 'block';
		dojo.fx.html.wipeIn(node, this.toggleDuration);
	}

	this.hide = function(node){
		dojo.fx.html.wipeOut(node, this.toggleDuration, function(node){ node.style.display = 'none'; });
	}
}


