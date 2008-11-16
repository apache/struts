/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.widget.Repeater");
dojo.require("dojo.widget.HtmlWidget");
dojo.require("dojo.string");
dojo.require("dojo.event.*");
dojo.require("dojo.experimental");
dojo.experimental("dojo.widget.Repeater");

dojo.widget.defineWidget("dojo.widget.Repeater", dojo.widget.HtmlWidget,
	{
		name: "",
		rowTemplate: "",
		myObject: null,
		pattern: "",
		useDnd: false,
		isContainer: true,

		initialize: function(args,frag) {
			var node = this.getFragNodeRef(frag);
			node.removeAttribute("dojotype");
			this.setRow(dojo.string.trim(node.innerHTML), {});
			node.innerHTML="";
			frag=null;
		},

		postCreate: function(args,frag){
			if (this.useDnd) {
				dojo.require("dojo.dnd.*");
				var dnd = new dojo.dnd.HtmlDropTarget(this.domNode, [this.widgetId]);
			}
		},

		reIndexRows: function() {
			for(var i=0,len=this.domNode.childNodes.length; i<len;i++) {
				var elems = ["INPUT", "SELECT", "TEXTAREA"];
				for (var k=0; k < elems.length; k++) {
					var list = this.domNode.childNodes[i].getElementsByTagName(elems[k]);
					for (var j=0,len2=list.length; j<len2; j++) {
						var name = list[j].name;
						var index=dojo.string.escape("regexp", this.pattern);
						index = index.replace(/%\\{index\\}/g,"%{index}");
						var nameRegexp = dojo.string.substituteParams(index, {"index": "[0-9]*"});
						var newName= dojo.string.substituteParams(this.pattern, {"index": "" + i});
						var re=new RegExp(nameRegexp,"g");
						list[j].name = name.replace(re,newName);
					}
				}
			}
		},

		onDeleteRow: function(e) {
			var index=dojo.string.escape("regexp", this.pattern);
			index = index.replace(/%\\{index\\}/g,"%{index}");
			var nameRegexp = dojo.string.substituteParams(index, {"index": "([0-9]*)"});
			var re=new RegExp(nameRegexp,"g");
			this.deleteRow(re.exec(e.target.name)[1]);
		},
		hasRows: function() {
			if (this.domNode.childNodes.length > 0) {
				return true;
			}
			return false;
		},

		getRowCount: function() {
			return this.domNode.childNodes.length;
		},

		deleteRow: function(idx) {
			this.domNode.removeChild(this.domNode.childNodes[idx]);
			this.reIndexRows();
		},

		changeRowPosition: function(e) {
			if (e.dragStatus == "dropFailure") {
				this.domNode.removeChild(e["dragSource"].domNode);
			} else if (e.dragStatus == "dropSuccess") {
				//  nothing to do
			} // else-if
			this.reIndexRows();
		},
		setRow: function(template, myObject) {
			template = dojo.string.substituteParams(template, {"index": "0"});
			this.rowTemplate=template;
			this.myObject = myObject;
		},
		getRow: function() {
			return this.rowTemplate;
		},
		onAddRow: function(e) {
		},
		addRow: function() {
			var node = document.createElement('span');
			node.innerHTML=this.getRow();
			if (node.childNodes.length == 1) {
				node=node.childNodes[0];
			}
			this.domNode.appendChild(node);
			var parser = new dojo.xml.Parse();
			var frag = parser.parseElement(node, null, true);
			dojo.widget.getParser().createSubComponents(frag, this);
			var elems = ["INPUT", "SELECT", "IMG"];
			for (var k=0; k < elems.length; k++) {
				var list = node.getElementsByTagName(elems[k]);
				for(var i=0, len=list.length; i<len; i++) {
					var child = list[i];
					if(child.nodeType != 1) {continue};
					if (child.getAttribute("rowFunction") != null) {
						if(typeof(this.myObject[child.getAttribute("rowFunction")]) == "undefined") {
							dojo.debug("Function " + child.getAttribute("rowFunction") + " not found");
						} else { 
							this.myObject[child.getAttribute("rowFunction")](child);
						}
					} else if (child.getAttribute("rowAction") != null) {
						if(child.getAttribute("rowAction") == "delete") {
							child.name=dojo.string.substituteParams(this.pattern, {"index": "0"});
							dojo.event.connect(child, "onclick", this, "onDeleteRow");
						} // if
					} // else-if
				} // for
			} // for
			this.reIndexRows();
			if (this.useDnd) { // bind to DND
				node=new dojo.dnd.HtmlDragSource(node, this.widgetId);
				dojo.event.connect(node, "onDragEnd", this, "changeRowPosition");
			}
			this.onAddRow();
		}
});


