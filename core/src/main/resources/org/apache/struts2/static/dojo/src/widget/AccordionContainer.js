/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.widget.AccordionContainer");

dojo.require("dojo.widget.*");
dojo.require("dojo.html.*");
dojo.require("dojo.lfx.html");
dojo.require("dojo.html.selection");
dojo.require("dojo.widget.html.layout");
dojo.require("dojo.widget.PageContainer");


/**
 *summary
 *	Holds a set of panes where every pane's title is visible, but only one pane's content is visible at a time,
 *	and switching between panes is visualized by sliding the other panes up/down.
 *
 * description
 *	Front view (3 panes, pane #2 open)
 *	------------------------
 *	|:::Pane#1 title:::    |
 * 	|:::Pane#2 title:::    |
 *	|                      |
 *	|    pane#2 contents   |
 *	|                      |
 *	|:::Pane#3 title:::    |
 *	------------------------
 *
 *	Side view (showing implementation):
 *
 *         viewport    pane#3     pane#2     pane#1
 *            =                                
 *            |                                =
 *            |                      =         |
 *	front     |                      |         |
 *            |                      |         =
 *            |                      =
 *            |          =
 *            =          |
 *                       |
 *                       =
 *
 *	Panes are stacked by z-index like a stack of cards, so they can be slid correctly.
 *	The panes on the bottom extend past the bottom of the viewport (but are hidden).
 *
 * usage
 *	<div dojoType="AccordionContainer">
 *		<div dojoType="ContentPane" label="pane 1">...</div>
 *		...
 *	</div>
 *
 * TODO:
 *	* this widget should extend PageContainer
 *	* call child.onShow(), child.onHide() so you can attach to those methods if you want
 */
 dojo.widget.defineWidget(
	"dojo.widget.AccordionContainer",
	dojo.widget.HtmlWidget,
	{
		isContainer: true,
		
		// String
		//	CSS class name for dom node w/the title
		labelNodeClass: "label",
		
		// String
		//	CSS class name for dom node holding the content
		containerNodeClass: "accBody",

		// Integer
		//	Amount of time (in ms) it takes to slide panes
		duration: 250,

		fillInTemplate: function(){
			with(this.domNode.style){
				// position must be either relative or absolute
				if(position!="absolute"){
					position="relative";
				}
				overflow="hidden";
			}
		},

		addChild: function(/*Widget*/ widget){
			var child = this._addChild(widget);
			this._setSizes();
			return child;	// Widget
		},
		
		_addChild: function(/*Widget*/ widget){
			// summary
			//	Internal call to add child, used during postCreate() and by the real addChild() call
			if(widget.open){
				dojo.deprecated("open parameter deprecated, use 'selected=true' instead will be removed in ", "0.5");
				dojo.debug(widget.widgetId + ": open == " + widget.open);
				widget.selected=true;
			}
			if (widget.widgetType != "AccordionPane") {
				var wrapper=dojo.widget.createWidget("AccordionPane",{label: widget.label, selected: widget.selected, labelNodeClass: this.labelNodeClass, containerNodeClass: this.containerNodeClass, allowCollapse: this.allowCollapse });
				wrapper.addChild(widget);
				this.addWidgetAsDirectChild(wrapper);
				this.registerChild(wrapper, this.children.length);
				return wrapper;	// Widget
			} else {
				dojo.html.addClass(widget.containerNode, this.containerNodeClass);
				dojo.html.addClass(widget.labelNode, this.labelNodeClass);
				this.addWidgetAsDirectChild(widget);
				this.registerChild(widget, this.children.length);	
				return widget;	// Widget
			}
		},
	
		postCreate: function() {
			var tmpChildren = this.children;
			this.children=[];
			dojo.html.removeChildren(this.domNode);
			dojo.lang.forEach(tmpChildren, dojo.lang.hitch(this,"_addChild"));
			this._setSizes();
		},
	
		removeChild: function(/*Widget*/ widget) {
			dojo.widget.AccordionContainer.superclass.removeChild.call(this, widget);
			this._setSizes();
		},
		
		onResized: function(){
			this._setSizes();
		},

		_setSizes: function() {
			// summary
			//	Set panes' size/position based on my size, and the current open node.

			// get cumulative height of all the title bars, and figure out which pane is open
			var totalCollapsedHeight = 0;
			var openIdx = 0;
			dojo.lang.forEach(this.children, function(child, idx){
				totalCollapsedHeight += child.getLabelHeight();
				if(child.selected){ openIdx=idx; }
			});

			// size and position each pane
			var mySize=dojo.html.getContentBox(this.domNode);
			var y = 0;
			dojo.lang.forEach(this.children, function(child, idx){
				var childCollapsedHeight = child.getLabelHeight();
				child.resizeTo(mySize.width, mySize.height-totalCollapsedHeight+childCollapsedHeight);
				child.domNode.style.zIndex=idx+1;
				child.domNode.style.position="absolute";
				child.domNode.style.top = y+"px";
				y += (idx==openIdx) ? dojo.html.getBorderBox(child.domNode).height : childCollapsedHeight;
			});
		},

		selectChild: function(/*Widget*/ page){
			// summary
			//	close the current page and select a new one
			dojo.lang.forEach(this.children, function(child){child.setSelected(child==page);});

			// slide each pane that needs to be moved
			var y = 0;
			var anims = [];
			dojo.lang.forEach(this.children, function(child, idx){
				if(child.domNode.style.top != (y+"px")){
					anims.push(dojo.lfx.html.slideTo(child.domNode, {top: y, left: 0}, this.duration));
				}
				y += child.selected ? dojo.html.getBorderBox(child.domNode).height : child.getLabelHeight();
			});
			dojo.lfx.combine(anims).play();
		}
	}
);

/**
 * summary
 *	AccordionPane is a box with a title that contains another widget (often a ContentPane).
 *	It's a widget used internally by AccordionContainer.
 */
dojo.widget.defineWidget(
	"dojo.widget.AccordionPane",
	dojo.widget.HtmlWidget,
{
	// parameters

	// String
	//	label to print on top of AccordionPane
	label: "",

	// String
	//	CSS class name for the AccordionPane's dom node
	"class": "dojoAccordionPane",

	// String
	//	CSS class name for the AccordionPane's label node
	labelNodeClass: "label",

	// String
	//	CSS class name for the AccordionPane's container node
	containerNodeClass: "accBody",
	
	// Boolean
	//	if true, this is the open pane
	selected: false,

	templatePath: dojo.uri.dojoUri("src/widget/templates/AccordionPane.html"),
	templateCssPath: dojo.uri.dojoUri("src/widget/templates/AccordionPane.css"),

	isContainer: true,

    fillInTemplate: function() {
    	dojo.html.addClass(this.domNode, this["class"]);
		dojo.widget.AccordionPane.superclass.fillInTemplate.call(this);
		dojo.html.disableSelection(this.labelNode);
		this.setSelected(this.selected);
	},

	setLabel: function(/*String*/ label) {
		// summary: set the  title of the node
		this.labelNode.innerHTML=label;
	},
	
	resizeTo: function(width, height){
		dojo.html.setMarginBox(this.domNode, {width: width, height: height});
		var children = [
			{domNode: this.labelNode, layoutAlign: "top"},
			{domNode: this.containerNode, layoutAlign: "client"}
		];
		dojo.widget.html.layout(this.domNode, children);
		var childSize = dojo.html.getContentBox(this.containerNode);
		this.children[0].resizeTo(childSize.width, childSize.height);
	},

	getLabelHeight: function() {
		// summary: returns the height of the title dom node
		return dojo.html.getMarginBox(this.labelNode).height;	// Integer
	},

	onLabelClick: function() {
		// summary: callback when someone clicks my label
		this.parent.selectChild(this);
	},
	
	setSelected: function(/*Boolean*/ isSelected){
		this.selected=isSelected;
		(isSelected ? dojo.html.addClass : dojo.html.removeClass)(this.domNode, this["class"]+"-selected");

		// make sure child is showing (lazy load), and also that onShow()/onHide() is called
		var child = this.children[0];
		if(child){
			if(isSelected){
				if(!child.isShowing()){
					child.show();
				}else{
					child.onShow();
				}
			}else{
				child.onHide();
			}
		}
	}
});

// These arguments can be specified for the children of an AccordionContainer
// Since any widget can be specified as a child, mix them
// into the base widget class.  (This is a hack, but it's effective.)
dojo.lang.extend(dojo.widget.Widget, {
	// String
	//	is this the selected child?
	//	DEPRECATED: will be removed in 0.5.  Used "selected" attribute instead.
	open: false
});
