/*
	Copyright (c) 2004-2005, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.widget.html.ContentPane");

dojo.require("dojo.widget.*");
dojo.require("dojo.io.*");
dojo.require("dojo.widget.Container");
dojo.require("dojo.widget.ContentPane");

dojo.widget.html.ContentPane = function(){
	dojo.widget.html.Container.call(this);
}
dojo.inherits(dojo.widget.html.ContentPane, dojo.widget.html.Container);

dojo.lang.extend(dojo.widget.html.ContentPane, {
	widgetType: "ContentPane",

	href: "",
	extractContent: true,
	parseContent: true,
	cacheContent: true,
	
	// To generate pane content from a java function
	handler: "",

	postCreate: function(args, frag, parentComp){
		if ( this.handler != "" ){
			this.setHandler(this.handler);
		}
	},

	onResized: function(){
		if(this.isVisible()){
			this.loadContents();
		}
		dojo.widget.html.ContentPane.superclass.onResized.call(this);
	},

	show: function(){
		this.loadContents();
		dojo.widget.html.ContentPane.superclass.show.call(this);
	},

	loadContents: function() {
		if ( this.isLoaded ){
			return;
		}
		this.isLoaded=true;
		if ( dojo.lang.isFunction(this.handler)) {
			this._runHandler();
		} else if ( this.href != "" ) {
			this._downloadExternalContent(this.href, this.cacheContent);
		}
	},

	// Reset the (external defined) content of this pane
	setUrl: function(url) {
		this.href = url;
		this.isLoaded = false;
		if ( this.isVisible() ){
			this.loadContents();
		}
	},

	_downloadExternalContent: function(url, useCache) {
		this.setContent("Loading...");

		var self = this;
		dojo.io.bind({
			url: url,
			useCache: useCache,
			mimetype: "text/html",
			handler: function(type, data, e) {
				if(type == "load") {
					if(self.extractContent) {
						var matches = data.match(/<body[^>]*>\s*([\s\S]+)\s*<\/body>/im);
						if(matches) { data = matches[1]; }
					}
					self.setContent.call(self, data);
				} else {
					self.setContent.call(self, "Error loading '" + url + "' (" + e.status + " " + e.statusText + ")");
				}
			}
		});
	},

	setContent: function(data){
		var node = this.containerNode || this.domNode;
		node.innerHTML = data;
		if(this.parseContent) {
			var parser = new dojo.xml.Parse();
			var frag = parser.parseElement(node, null, true);
			dojo.widget.getParser().createComponents(frag);
			this.onResized();
		}
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
	}
});

dojo.widget.tags.addParseTreeHandler("dojo:ContentPane");
