/*
	Copyright (c) 2004-2005, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.widget.html.Tooltip");
dojo.require("dojo.widget.HtmlWidget");
dojo.require("dojo.widget.Tooltip");
dojo.require("dojo.uri");
dojo.require("dojo.widget.*");
dojo.require("dojo.event");
dojo.require("dojo.style");
dojo.require("dojo.html");

dojo.widget.html.Tooltip = function(){
	// mix in the tooltip properties
	dojo.widget.Tooltip.call(this);
	dojo.widget.HtmlWidget.call(this);
}
dojo.inherits(dojo.widget.html.Tooltip, dojo.widget.HtmlWidget);
dojo.lang.extend(dojo.widget.html.Tooltip, {

	// Constructor arguments (should these be in tooltip.js rather than html/tooltip.js???)
	caption: "undefined",
	delay: 500,
	connectId: "",

	templatePath: dojo.uri.dojoUri("src/widget/templates/HtmlTooltipTemplate.html"),
	templateCssPath: dojo.uri.dojoUri("src/widget/templates/HtmlTooltipTemplate.css"),

	containerNode: null,
	connectNode: null,

	hovering: false,
	displayed: false,

	fillInTemplate: function(args, frag){
		if(this.caption != "undefined"){
			this.domNode.appendChild(document.createTextNode(this.caption));
		}
		dojo.html.body().appendChild(this.domNode);
		this.connectNode = dojo.byId(this.connectId);
		
		// IE bug workaround
		this.bgIframe = new dojo.html.BackgroundIframe();
	},
	
	postCreate: function(args, frag){
		var self = this;
		this.timerEvent = function () { self.display.apply(self); };
		dojo.event.connect(this.connectNode, "onmouseover", this, "onMouseOver");
	},
	
	onMouseOver: function(e) {
		if( this.displayed ){ return; }
		this.timerEventId = setTimeout(this.timerEvent, this.delay);
		dojo.event.connect(document.documentElement, "onmousemove", this, "onMouseMove");
	},
	
	onMouseMove: function(e) {
		this.mouseX = e.pageX || e.clientX + dojo.html.body().scrollLeft;
		this.mouseY = e.pageY || e.clientY + dojo.html.body().scrollTop;
		if( !dojo.html.overElement(this.connectNode, e) ){
			// Note: can't use onMouseOut because the "explode" effect causes
			// spurious onMouseOut/onMouseOver events (due to interference from outline)
			this.erase();
		}
	},

	display: function() {
		if ( this.displayed ) { return; }

		this.domNode.style.top = this.mouseY + 15 + "px";
		this.domNode.style.left = this.mouseX + 10 + "px";

		// if rendering using explosion effect, need to set explosion source
		this.explodeSrc = [this.mouseX, this.mouseY];

		this.show();
		this.bgIframe.show(this.domNode);

		this.displayed=true;
	},

	onShow: function() {
		// for explode effect, have to display the iframe after the effect completes
		this.bgIframe.show(this.domNode);
	},

	erase: function() {
		if ( this.timerEventId ) {
			clearTimeout(this.timerEventId);
			delete this.timerEventId;
		}
		if ( this.displayed ) {
			this.hide();
			this.bgIframe.hide();
			this.displayed=false;
		}
		dojo.event.disconnect(document.documentElement, "onmousemove", this, "onMouseMove");
	}
});
