/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

/*TODO:
 * Add a command to toggle DnD support for a toolbar
 * Save/restore order of toolbar/item
 */
dojo.provide("dojo.widget.Editor2Plugin.ToolbarDndSupport");
dojo.require("dojo.dnd.*");

dojo.event.topic.subscribe("dojo.widget.Editor2::preLoadingToolbar", function(editor){
	dojo.dnd.dragManager.nestedTargets = true;
	var p = new dojo.widget.Editor2Plugin.ToolbarDndSupport(editor);
});

dojo.declare("dojo.widget.Editor2Plugin.ToolbarDndSupport", null,{
	lookForClass: "dojoEditorToolbarDnd TB_ToolbarSet TB_Toolbar",
	initializer: function(editor){
		this.editor = editor;
		dojo.event.connect(this.editor, "toolbarLoaded", this, "setup");
		this.editor.registerLoadedPlugin(this);
	},

	setup: function(){
		dojo.event.disconnect(this.editor, "toolbarLoaded", this, "setup");
		var tbw = this.editor.toolbarWidget;
		dojo.event.connect("before", tbw, "destroy", this, "destroy");

		var nodes = dojo.html.getElementsByClass(this.lookForClass, tbw.domNode, null, dojo.html.classMatchType.ContainsAny);
		if(!nodes){
			dojo.debug("dojo.widget.Editor2Plugin.ToolbarDndSupport: No dom node with class in "+this.lookForClass);
			return;
		}
		for(var i=0; i<nodes.length; i++){
			var node = nodes[i];
			var droptarget = node.getAttribute("dojoETDropTarget");
			if(droptarget){
				(new dojo.dnd.HtmlDropTarget(node, [droptarget+tbw.widgetId])).vertical = true;
			}
			var dragsource = node.getAttribute("dojoETDragSource");
			if(dragsource){
				new dojo.dnd.HtmlDragSource(node, dragsource+tbw.widgetId);
			}
		}
	},

	destroy: function(){
		this.editor.unregisterLoadedPlugin(this);
	}
});

//let's have a command to enable DnD
/*dojo.declare("dojo.widget.Editor2Plugin.ToolbarDndCommand", dojo.widget.Editor2Command,{
	execute: function(text, option){
		var curInst = dojo.widget.Editor2Manager.getCurrentInstance();
		if(curInst){
		}
	},
	getState: function(){	
	}
});*/