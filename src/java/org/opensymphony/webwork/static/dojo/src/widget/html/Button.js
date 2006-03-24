/*
	Copyright (c) 2004-2005, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.widget.html.Button");
dojo.require("dojo.widget.Button");

dojo.widget.html.Button = function(){
	// mix in the button properties
	dojo.widget.Button.call(this);
	dojo.widget.HtmlWidget.call(this);
}
dojo.inherits(dojo.widget.html.Button, dojo.widget.HtmlWidget);
dojo.lang.extend(dojo.widget.html.Button, {

	templatePath: dojo.uri.dojoUri("src/widget/templates/HtmlButtonTemplate.html"),
	templateCssPath: dojo.uri.dojoUri("src/widget/templates/HtmlButtonTemplate.css"),

	label: "",
	labelNode: null,
	containerNode: null,

	postCreate: function(args, frag){
		this.labelNode = this.containerNode;
		/*
		if(this.label != "undefined"){
			this.domNode.appendChild(document.createTextNode(this.label));
		}
		*/
	},
	
	onMouseOver: function(e){
		dojo.html.addClass(this.domNode, "dojoButtonHover");
		dojo.html.removeClass(this.domNode, "dojoButtonNoHover");
	},
	
	onMouseOut: function(e){
		dojo.html.removeClass(this.domNode, "dojoButtonHover");
		dojo.html.addClass(this.domNode, "dojoButtonNoHover");
	},

	// By default, when I am clicked, click the item (link) inside of me.
	// By default, a button is a disguised link.
	// Todo: support actual submit and reset buttons.
	onClick: function (e) {
		var child = dojo.dom.getFirstChildElement(this.domNode);
		if(child){
			if(child.click){
				child.click();
			}else if(child.href){
				location.href = child.href;
			}
		}
	}
});
