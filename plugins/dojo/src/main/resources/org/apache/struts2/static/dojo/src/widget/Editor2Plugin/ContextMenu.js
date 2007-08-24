/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.widget.Editor2Plugin.ContextMenu");
dojo.require("dojo.widget.Menu2");
dojo.event.topic.subscribe("dojo.widget.Editor2::onLoad", function (editor) {
	dojo.widget.Editor2Plugin.ContextMenuManager.getContextMenu(editor);
});
dojo.widget.Editor2Plugin.ContextMenuManager = {menuGroups:["Generic", "Link", "Anchor", "Image", "List", "Table"], _contextMenuGroupSets:{}, _registeredGroups:{}, _menus:{}, registerGroup:function (name, handler) {
	if (this._registeredGroups[name]) {
		alert("dojo.widget.Editor2Plugin.ContextMenuManager.registerGroup: menu group " + name + "is already registered. Ignored.");
		return;
	}
	this._registeredGroups[name] = handler;
}, removeGroup:function (name) {
	delete this._registeredGroups[name];
}, getGroup:function (name, contextmenuplugin) {
	if (this._registeredGroups[name]) {
		var item = this._registeredGroups[name](name, contextmenuplugin);
		if (item) {
			return item;
		}
	}
	switch (name) {
	  case "Generic":
	  case "Link":
	  case "Image":
		return new dojo.widget.Editor2Plugin[name + "ContextMenuGroup"](contextmenuplugin);
	  case "Anchor":
	  case "List":
	}
}, registerGroupSet:function (name, set) {
	this._contextMenuGroupSets[name] = set;
}, removeGroupSet:function (name) {
	var set = this._contextMenuGroupSets[name];
	delete this._contextMenuGroupSets[name];
	return set;
}, getContextMenu:function (editor) {
	var set = editor.contextMenuGroupSet || "defaultDojoEditor2MenuGroupSet";
	if (this._menus[set]) {
		this._menus[set].bindEditor(editor);
		return this._menus[set];
	}
	var gs = (editor.contextMenuGroupSet && this._contextMenuGroupSets[editor.contextMenuGroupSet]) || this.menuGroups;
	var menu = new dojo.widget.Editor2Plugin.ContextMenu(editor, gs);
	this._menus[set] = menu;
	return menu;
}};
dojo.declare("dojo.widget.Editor2Plugin.ContextMenu", null, function (editor, gs) {
	this.groups = [];
	this.separators = [];
	this.editor = editor;
	this.editor.registerLoadedPlugin(this);
	this.contextMenu = dojo.widget.createWidget("PopupMenu2", {});
	dojo.body().appendChild(this.contextMenu.domNode);
	this.bindEditor(this.editor);
	dojo.event.connect(this.contextMenu, "aboutToShow", this, "aboutToShow");
	dojo.event.connect(this.editor, "destroy", this, "destroy");
	this.setup(gs);
}, {bindEditor:function (editor) {
	this.contextMenu.bindDomNode(editor.document.body);
}, setup:function (gs) {
	for (var i in gs) {
		var g = dojo.widget.Editor2Plugin.ContextMenuManager.getGroup(gs[i], this);
		if (g) {
			this.groups.push(g);
		}
	}
}, aboutToShow:function () {
	var first = true;
	for (var i in this.groups) {
		if (i > 0 && this.separators.length != this.groups.length - 1) {
			this.separators.push(dojo.widget.createWidget("MenuSeparator2", {}));
			this.contextMenu.addChild(this.separators[this.separators.length - 1]);
		}
		if (this.groups[i].refresh()) {
			if (i > 0) {
				if (first) {
					this.separators[i - 1].hide();
				} else {
					this.separators[i - 1].show();
				}
			}
			if (first) {
				first = false;
			}
		} else {
			if (i > 0) {
				this.separators[i - 1].hide();
			}
		}
	}
}, destroy:function () {
	this.editor.unregisterLoadedPlugin(this);
	delete this.groups;
	delete this.separators;
	this.contextMenu.destroy();
	delete this.contextMenu;
}});
dojo.widget.defineWidget("dojo.widget.Editor2ContextMenuItem", dojo.widget.MenuItem2, {command:"", buildRendering:function () {
	var curInst = dojo.widget.Editor2Manager.getCurrentInstance();
	this.caption = curInst.getCommand(this.command).getText();
	dojo.widget.Editor2ContextMenuItem.superclass.buildRendering.apply(this, arguments);
}, onClick:function () {
	var curInst = dojo.widget.Editor2Manager.getCurrentInstance();
	if (curInst) {
		var _command = curInst.getCommand(this.command);
		if (_command) {
			_command.execute();
		}
	}
}, refresh:function () {
	var curInst = dojo.widget.Editor2Manager.getCurrentInstance();
	if (curInst) {
		var _command = curInst.getCommand(this.command);
		if (_command) {
			if (_command.getState() == dojo.widget.Editor2Manager.commandState.Disabled) {
				this.disable();
				return false;
			} else {
				this.enable();
				return true;
			}
		}
	}
}, hide:function () {
	this.domNode.style.display = "none";
}, show:function () {
	this.domNode.style.display = "";
}});
dojo.declare("dojo.widget.Editor2Plugin.SimpleContextMenuGroup", null, function (contextmenuplugin) {
	this.contextMenu = contextmenuplugin.contextMenu;
	this.items = [];
	dojo.event.connect(contextmenuplugin, "destroy", this, "destroy");
}, {refresh:function () {
	if (!this.items.length) {
		this.createItems();
		for (var i in this.items) {
			this.contextMenu.addChild(this.items[i]);
		}
	}
	return this.checkVisibility();
}, destroy:function () {
	this.contextmenu = null;
	delete this.items;
	delete this.contextMenu;
}, createItems:function () {
}, checkVisibility:function () {
	var show = false;
	for (var i in this.items) {
		show = show || this.items[i].refresh();
	}
	var action = show ? "show" : "hide";
	for (var i in this.items) {
		this.items[i][action]();
	}
	return show;
}});
dojo.declare("dojo.widget.Editor2Plugin.GenericContextMenuGroup", dojo.widget.Editor2Plugin.SimpleContextMenuGroup, {createItems:function () {
	this.items.push(dojo.widget.createWidget("Editor2ContextMenuItem", {command:"cut", iconClass:"dojoE2TBIcon dojoE2TBIcon_Cut"}));
	this.items.push(dojo.widget.createWidget("Editor2ContextMenuItem", {command:"copy", iconClass:"dojoE2TBIcon dojoE2TBIcon_Copy"}));
	this.items.push(dojo.widget.createWidget("Editor2ContextMenuItem", {command:"paste", iconClass:"dojoE2TBIcon dojoE2TBIcon_Paste"}));
}});
dojo.declare("dojo.widget.Editor2Plugin.LinkContextMenuGroup", dojo.widget.Editor2Plugin.SimpleContextMenuGroup, {createItems:function () {
	this.items.push(dojo.widget.createWidget("Editor2ContextMenuItem", {command:"createlink", iconClass:"dojoE2TBIcon dojoE2TBIcon_Link"}));
	this.items.push(dojo.widget.createWidget("Editor2ContextMenuItem", {command:"unlink", iconClass:"dojoE2TBIcon dojoE2TBIcon_UnLink"}));
}, checkVisibility:function () {
	var show = this.items[1].refresh();
	if (show) {
		this.items[0].refresh();
		for (var i in this.items) {
			this.items[i].show();
		}
	} else {
		for (var i in this.items) {
			this.items[i].hide();
		}
	}
	return show;
}});
dojo.declare("dojo.widget.Editor2Plugin.ImageContextMenuGroup", dojo.widget.Editor2Plugin.SimpleContextMenuGroup, {createItems:function () {
	this.items.push(dojo.widget.createWidget("Editor2ContextMenuItem", {command:"insertimage", iconClass:"dojoE2TBIcon dojoE2TBIcon_Image"}));
}, checkVisibility:function () {
	var curInst = dojo.widget.Editor2Manager.getCurrentInstance();
	var img = dojo.withGlobal(curInst.window, "getSelectedElement", dojo.html.selection);
	if (img && img.tagName.toLowerCase() == "img") {
		this.items[0].show();
		return true;
	} else {
		this.items[0].hide();
		return false;
	}
}});

