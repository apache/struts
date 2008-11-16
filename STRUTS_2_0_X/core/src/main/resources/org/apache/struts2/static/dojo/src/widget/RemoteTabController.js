/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.widget.RemoteTabController");

//Summary
//Remote Tab Controller widget.  Can be located independently of a tab
//container and control the selection of its tabs
dojo.require("dojo.widget.*");
dojo.require("dojo.widget.TabContainer");
dojo.require("dojo.event.*");

dojo.deprecated("dojo.widget.RemoteTabController is slated for removal in 0.5; use PageController or TabController instead.", "0.5");

dojo.widget.defineWidget(
    "dojo.widget.RemoteTabController",
    dojo.widget.TabController,
	{
        templateCssPath: dojo.uri.dojoUri("src/widget/templates/RemoteTabControl.css"),
		templateString: '<div dojoAttachPoint="domNode" wairole="tablist"></div>',

		"class": "dojoRemoteTabController",

		// String
		//	ID of page container that I connect to
		tabContainer: "",
	
		postMixInProperties: function(){
			this.containerId = this.tabContainer;
			dojo.widget.RemoteTabController.superclass.postMixInProperties.apply(this, arguments);
		},
			
		fillInTemplate: function() {
			dojo.html.addClass(this.domNode, this["class"]);  // "class" is a reserved word in JS

			if (this.tabContainer) {
				dojo.addOnLoad(dojo.lang.hitch(this, "setupTabs"));
			}

			dojo.widget.RemoteTabController.superclass.fillInTemplate.apply(this, arguments);
		}
	}
);
