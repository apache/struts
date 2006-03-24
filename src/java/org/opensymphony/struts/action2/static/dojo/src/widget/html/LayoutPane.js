/*
	Copyright (c) 2004-2005, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.widget.LayoutPane");
dojo.provide("dojo.widget.html.LayoutPane");

//
// this widget provides Delphi-style panel layout semantics
// this is a good place to stash layout logic, then derive components from it
//
// TODO: allow more edge priority orders (e.g. t,r,l,b)
// TODO: allow percentage sizing stuff
//

dojo.require("dojo.widget.LayoutPane");
dojo.require("dojo.widget.*");
dojo.require("dojo.event.*");
dojo.require("dojo.io.*");
dojo.require("dojo.widget.Container");
dojo.require("dojo.html");
dojo.require("dojo.style");
dojo.require("dojo.dom");
dojo.require("dojo.string");


dojo.widget.html.LayoutPane = function(){
	dojo.widget.html.Container.call(this);
}

dojo.inherits(dojo.widget.html.LayoutPane, dojo.widget.html.Container);

dojo.lang.extend(dojo.widget.html.LayoutPane, {
	widgetType: "LayoutPane",

	isChild: false,

	clientLeft: 0,
	clientTop: 0,
	clientRect: {'left':0, 'right':0, 'top':0, 'bottom':0},
	clientWidth: 0,
	clientHeight: 0,

	layoutAlign: 'none',
	layoutChildPriority: 'top-bottom',

	cssPath: dojo.uri.dojoUri("src/widget/templates/HtmlLayoutPane.css"),

	// If this pane's content is external then set the url here	
	url: "inline",
	extractContent: true,
	parseContent: true,
	
	// To generate pane content from a java function
	handler: "none",

	minWidth: 0,
	minHeight: 0,

	fillInTemplate: function(){
		this.filterAllowed('layoutAlign',         ['none', 'left', 'top', 'right', 'bottom', 'client', 'flood']);
		this.filterAllowed('layoutChildPriority', ['left-right', 'top-bottom']);

		// Need to include CSS manually because there is no template file/string
		dojo.style.insertCssFile(this.cssPath, null, true);

		this.domNode.style.position = 'relative';
		dojo.html.addClass(this.domNode, "dojoLayoutPane");
		dojo.html.addClass(this.domNode, "dojoAlign" + dojo.string.capitalize(this.layoutAlign));		
	},

	postCreate: function(args, fragment, parentComp){

		for(var i=0; i<this.children.length; i++){
			if (this.hasLayoutAlign(this.children[i])){
				this.children[i].domNode.style.position = 'absolute';
				this.children[i].isChild = true;	
			}
		}

		if ( this.handler != "none" ){
			this.setHandler(this.handler);
		}
		if ( this.isVisible() ){
			this.loadContents();
		}
	},

	// If the pane contents are external then load them
	loadContents: function() {
		if ( this.isLoaded ){
			return;
		}
		if ( dojo.lang.isFunction(this.handler)) {
			this._runHandler();
		} else if ( this.url != "inline" ) {
			this._downloadExternalContent(this.url, true);
		}
		this.isLoaded=true;
	},

	// Reset the (external defined) content of this pane
	setUrl: function(url) {
		this.url = url;
		this.isLoaded = false;
		if ( this.isVisible() ){
			this.loadContents();
		}
	},

	_downloadExternalContent: function(url, useCache) {
		//dojo.debug(this.widgetId + " downloading " + url);
		var node = this.containerNode || this.domNode;
		node.innerHTML = "Loading...";

		var extract = this.extractContent;
		var parse = this.parseContent;
		var self = this;

		dojo.io.bind({
			url: url,
			useCache: useCache,
			mimetype: "text/html",
			handler: function(type, data, e) {
				if(type == "load") {
					if(extract) {
						var matches = data.match(/<body[^>]*>\s*([\s\S]+)\s*<\/body>/im);
						if(matches) { data = matches[1]; }
					}
					node.innerHTML = data;
					if(parse) {
						var parser = new dojo.xml.Parse();
						var frag = parser.parseElement(node, null, true);
						dojo.widget.getParser().createComponents(frag);
					}
					self.onResized();
				} else {
					node.innerHTML = "Error loading '" + url + "' (" + e.status + " " + e.statusText + ")";
				}
			}
		});
	},

	// Generate pane content from given java function
	setHandler: function(handler) {
		var fcn = dojo.lang.isFunction(handler) ? handler : window[handler];
		if(!dojo.lang.isFunction(fcn)) {
			throw new Error("Unable to set handler, '" + handler + "' not a function.");
			return;
		}
		this.handler = function() {
			return fcn.apply(this, arguments);
		}
	},

	_runHandler: function() {
		if(dojo.lang.isFunction(this.handler)) {
			this.handler(this, this.domNode);
			return false;
		}
		return true;
		/*
		// in case we want to honor the return value?
		var ret = true;
		if(dojo.lang.isFunction(this.handler) {
			var val = this.handler(this, panel);
			if(!dojo.lang.isUndefined(val)) {
				ret = val;
			}
		}
		return ret;
		*/
	},

	filterAllowed: function(param, values){
		if ( !dojo.lang.inArray(values, this[param]) ) {
			this[param] = values[0];
		}
	},

	layoutChildren: function(){
		// find the children to arrange

		var kids = {'left':[], 'right':[], 'top':[], 'bottom':[], 'client':[], 'flood':[]};
		var hits = 0;

		for(var i=0; i<this.children.length; i++){
			if (this.hasLayoutAlign(this.children[i])){
				kids[this.children[i].layoutAlign].push(this.children[i]);
				hits++;
			}
		}

		if (!hits){
			return;
		}

		var container = this.containerNode || this.domNode;

		// calc layout space

		this.clientWidth  = dojo.style.getContentWidth(container);
		this.clientHeight = dojo.style.getContentHeight(container);

		this.clientRect['left']   = dojo.style.getPixelValue(container, "padding-left", true);
		this.clientRect['right']  = dojo.style.getPixelValue(container, "padding-right", true);
		this.clientRect['top']    = dojo.style.getPixelValue(container, "padding-top", true);
		this.clientRect['bottom'] = dojo.style.getPixelValue(container, "padding-bottom", true);

		// arrange them in order
		this.layoutCenter(kids, "flood");
		if (this.layoutChildPriority == 'top-bottom'){
			this.layoutFloat(kids, "top");
			this.layoutFloat(kids, "bottom");
			this.layoutFloat(kids, "left");
			this.layoutFloat(kids, "right");
		}else{
			this.layoutFloat(kids, "left");
			this.layoutFloat(kids, "right");
			this.layoutFloat(kids, "top");
			this.layoutFloat(kids, "bottom");
		}
		this.layoutCenter(kids, "client");
	},

	// Position the left/right/top/bottom aligned elements
	layoutFloat: function(kids, position){
		var ary = kids[position];
		
		// figure out which two of the left/right/top/bottom properties to set
		var lr = (position=="right")?"right":"left";
		var tb = (position=="bottom")?"bottom":"top";

		for(var i=0; i<ary.length; i++){
			var elm=ary[i];
			
			// set two of left/right/top/bottom properties
			elm.domNode.style[lr]=this.clientRect[lr] + "px";
			elm.domNode.style[tb]=this.clientRect[tb] + "px";
			
			// adjust record of remaining space
			if ( (position=="top")||(position=="bottom") ) {
				dojo.style.setOuterWidth(elm.domNode, this.clientWidth);
				var height = dojo.style.getOuterHeight(elm.domNode);
				this.clientHeight -= height;
				this.clientRect[position] += height;
			} else {
				dojo.style.setOuterHeight(elm.domNode, this.clientHeight);
				var width = dojo.style.getOuterWidth(elm.domNode);
				this.clientWidth -= width;
				this.clientRect[position] += width;
			}
		}
	},

	// Position elements into the remaining space (in the center)
	// If multiple elements are present they overlap each other
	layoutCenter: function(kids, position){
		var ary = kids[position];
		for(var i=0; i<ary.length; i++){
			var elm=ary[i];
			elm.domNode.style.left=this.clientRect.left + "px";
			elm.domNode.style.top=this.clientRect.top + "px";
			dojo.style.setOuterWidth(elm.domNode, this.clientWidth);		
			dojo.style.setOuterHeight(elm.domNode, this.clientHeight);
		}

	},

	hasLayoutAlign: function(child){
		return dojo.lang.inArray(['left','right','top','bottom','client', 'flood'], child.layoutAlign);
	},

	addPane: function(pane){

		pane.domNode.style.position = 'absolute';
		pane.isChild = true;

		this.addChild(pane);

		this.resizeSoon();
	},

	removePane: function(pane){

		this.removeChild(pane);

		dojo.dom.removeNode(pane.domNode);

		this.resizeSoon();
	},
	
	resizeSoon: function(){
		if ( this.isVisible() ) {
			dojo.lang.setTimeout(this, this.onResized, 0);
		}
	},

	onResized: function(){
		if ( !this.isVisible() ) {
			return;
		}

		//dojo.debug(this.widgetId + ": resized");

		// set position/size for my children
		this.layoutChildren();

		// notify children that they have been moved/resized
		this.notifyChildrenOfResize();
	},

	resizeTo: function(w, h){

		w = Math.max(w, this.getMinWidth());
		h = Math.max(h, this.getMinHeight());

		dojo.style.setOuterWidth(this.domNode, w);
		dojo.style.setOuterHeight(this.domNode, h);
		this.onResized();
	},

	show: function(){
		// If this is the first time we are displaying this object,
		// and the contents are external, then download them.
		this.loadContents();

		// If this node was created while display=="none" then it
		// hasn't been laid out yet.  Do that now.
		this.domNode.style.display="";
		this.onResized();
		this.domNode.style.display="none";
		this.domNode.style.visibility="";

		dojo.widget.html.LayoutPane.superclass.show.call(this);
	},

	getMinWidth: function(){

		//
		// we need to first get the cumulative width
		//

		var w = this.minWidth;

		if ((this.layoutAlign == 'left') || (this.layoutAlign == 'right')){

			w = dojo.style.getOuterWidth(this.domNode);
		}

		for(var i=0; i<this.children.length; i++){
			var ch = this.children[i];
			var a = ch.layoutAlign;

			if ((a == 'left') || (a == 'right') || (a == 'client')){

				if (dojo.lang.isFunction(ch.getMinWidth)){
					w += ch.getMinWidth();
				}
			}
		}

		//
		// but then we need to check to see if the top/bottom kids are larger
		//

		for(var i=0; i<this.children.length; i++){
			var ch = this.children[i];
			var a = ch.layoutAlign;

			if ((a == 'top') || (a == 'bottom')){

				if (dojo.lang.isFunction(ch.getMinWidth)){
					w = Math.max(w, ch.getMinWidth());
				}
			}
		}

		return w;
	},

	getMinHeight: function(){

		//
		// we need to first get the cumulative height
		//

		var h = this.minHeight;

		if ((this.layoutAlign == 'top') || (this.layoutAlign == 'bottom')){

			h = dojo.style.getOuterHeight(this.domNode);
		}

		for(var i=0; i<this.children.length; i++){
			var ch = this.children[i];
			var a = ch.layoutAlign;

			if ((a == 'top') || (a == 'bottom') || (a == 'client')){

				if (dojo.lang.isFunction(ch.getMinHeight)){
					h += ch.getMinHeight();
				}
			}
		}

		//
		// but then we need to check to see if the left/right kids are larger
		//

		for(var i=0; i<this.children.length; i++){
			var ch = this.children[i];
			var a = ch.layoutAlign;

			if ((a == 'left') || (a == 'right')){

				if (dojo.lang.isFunction(ch.getMinHeight)){
					h = Math.max(h, ch.getMinHeight());
				}
			}
		}

		return h;
	}
});


