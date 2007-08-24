/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.widget.DomWidget");
dojo.require("dojo.event.*");
dojo.require("dojo.widget.Widget");
dojo.require("dojo.dom");
dojo.require("dojo.html.style");
dojo.require("dojo.xml.Parse");
dojo.require("dojo.uri.*");
dojo.require("dojo.lang.func");
dojo.require("dojo.lang.extras");
dojo.widget._cssFiles = {};
dojo.widget._cssStrings = {};
dojo.widget._templateCache = {};
dojo.widget.defaultStrings = {dojoRoot:dojo.hostenv.getBaseScriptUri(), dojoWidgetModuleUri:dojo.uri.moduleUri("dojo.widget"), baseScriptUri:dojo.hostenv.getBaseScriptUri()};
dojo.widget.fillFromTemplateCache = function (obj, templatePath, templateString, avoidCache) {
	var tpath = templatePath || obj.templatePath;
	var tmplts = dojo.widget._templateCache;
	if (!tpath && !obj["widgetType"]) {
		do {
			var dummyName = "__dummyTemplate__" + dojo.widget._templateCache.dummyCount++;
		} while (tmplts[dummyName]);
		obj.widgetType = dummyName;
	}
	var wt = tpath ? tpath.toString() : obj.widgetType;
	var ts = tmplts[wt];
	if (!ts) {
		tmplts[wt] = {"string":null, "node":null};
		if (avoidCache) {
			ts = {};
		} else {
			ts = tmplts[wt];
		}
	}
	if ((!obj.templateString) && (!avoidCache)) {
		obj.templateString = templateString || ts["string"];
	}
	if (obj.templateString) {
		obj.templateString = this._sanitizeTemplateString(obj.templateString);
	}
	if ((!obj.templateNode) && (!avoidCache)) {
		obj.templateNode = ts["node"];
	}
	if ((!obj.templateNode) && (!obj.templateString) && (tpath)) {
		var tstring = this._sanitizeTemplateString(dojo.hostenv.getText(tpath));
		obj.templateString = tstring;
		if (!avoidCache) {
			tmplts[wt]["string"] = tstring;
		}
	}
	if ((!ts["string"]) && (!avoidCache)) {
		ts.string = obj.templateString;
	}
};
dojo.widget._sanitizeTemplateString = function (tString) {
	if (tString) {
		tString = tString.replace(/^\s*<\?xml(\s)+version=[\'\"](\d)*.(\d)*[\'\"](\s)*\?>/im, "");
		var matches = tString.match(/<body[^>]*>\s*([\s\S]+)\s*<\/body>/im);
		if (matches) {
			tString = matches[1];
		}
	} else {
		tString = "";
	}
	return tString;
};
dojo.widget._templateCache.dummyCount = 0;
dojo.widget.attachProperties = ["dojoAttachPoint", "id"];
dojo.widget.eventAttachProperty = "dojoAttachEvent";
dojo.widget.onBuildProperty = "dojoOnBuild";
dojo.widget.waiNames = ["waiRole", "waiState"];
dojo.widget.wai = {waiRole:{name:"waiRole", "namespace":"http://www.w3.org/TR/xhtml2", alias:"x2", prefix:"wairole:"}, waiState:{name:"waiState", "namespace":"http://www.w3.org/2005/07/aaa", alias:"aaa", prefix:""}, setAttr:function (node, ns, attr, value) {
	if (dojo.render.html.ie) {
		node.setAttribute(this[ns].alias + ":" + attr, this[ns].prefix + value);
	} else {
		node.setAttributeNS(this[ns]["namespace"], attr, this[ns].prefix + value);
	}
}, getAttr:function (node, ns, attr) {
	if (dojo.render.html.ie) {
		return node.getAttribute(this[ns].alias + ":" + attr);
	} else {
		return node.getAttributeNS(this[ns]["namespace"], attr);
	}
}, removeAttr:function (node, ns, attr) {
	var success = true;
	if (dojo.render.html.ie) {
		success = node.removeAttribute(this[ns].alias + ":" + attr);
	} else {
		node.removeAttributeNS(this[ns]["namespace"], attr);
	}
	return success;
}};
dojo.widget.attachTemplateNodes = function (rootNode, targetObj, events) {
	var elementNodeType = dojo.dom.ELEMENT_NODE;
	function trim(str) {
		return str.replace(/^\s+|\s+$/g, "");
	}
	if (!rootNode) {
		rootNode = targetObj.domNode;
	}
	if (rootNode.nodeType != elementNodeType) {
		return;
	}
	var nodes = rootNode.all || rootNode.getElementsByTagName("*");
	var _this = targetObj;
	for (var x = -1; x < nodes.length; x++) {
		var baseNode = (x == -1) ? rootNode : nodes[x];
		var attachPoint = [];
		if (!targetObj.widgetsInTemplate || !baseNode.getAttribute("dojoType")) {
			for (var y = 0; y < this.attachProperties.length; y++) {
				var tmpAttachPoint = baseNode.getAttribute(this.attachProperties[y]);
				if (tmpAttachPoint) {
					attachPoint = tmpAttachPoint.split(";");
					for (var z = 0; z < attachPoint.length; z++) {
						if (dojo.lang.isArray(targetObj[attachPoint[z]])) {
							targetObj[attachPoint[z]].push(baseNode);
						} else {
							targetObj[attachPoint[z]] = baseNode;
						}
					}
					break;
				}
			}
			var attachEvent = baseNode.getAttribute(this.eventAttachProperty);
			if (attachEvent) {
				var evts = attachEvent.split(";");
				for (var y = 0; y < evts.length; y++) {
					if ((!evts[y]) || (!evts[y].length)) {
						continue;
					}
					var thisFunc = null;
					var tevt = trim(evts[y]);
					if (evts[y].indexOf(":") >= 0) {
						var funcNameArr = tevt.split(":");
						tevt = trim(funcNameArr[0]);
						thisFunc = trim(funcNameArr[1]);
					}
					if (!thisFunc) {
						thisFunc = tevt;
					}
					var tf = function () {
						var ntf = new String(thisFunc);
						return function (evt) {
							if (_this[ntf]) {
								_this[ntf](dojo.event.browser.fixEvent(evt, this));
							}
						};
					}();
					dojo.event.browser.addListener(baseNode, tevt, tf, false, true);
				}
			}
			for (var y = 0; y < events.length; y++) {
				var evtVal = baseNode.getAttribute(events[y]);
				if ((evtVal) && (evtVal.length)) {
					var thisFunc = null;
					var domEvt = events[y].substr(4);
					thisFunc = trim(evtVal);
					var funcs = [thisFunc];
					if (thisFunc.indexOf(";") >= 0) {
						funcs = dojo.lang.map(thisFunc.split(";"), trim);
					}
					for (var z = 0; z < funcs.length; z++) {
						if (!funcs[z].length) {
							continue;
						}
						var tf = function () {
							var ntf = new String(funcs[z]);
							return function (evt) {
								if (_this[ntf]) {
									_this[ntf](dojo.event.browser.fixEvent(evt, this));
								}
							};
						}();
						dojo.event.browser.addListener(baseNode, domEvt, tf, false, true);
					}
				}
			}
		}
		var tmpltPoint = baseNode.getAttribute(this.templateProperty);
		if (tmpltPoint) {
			targetObj[tmpltPoint] = baseNode;
		}
		dojo.lang.forEach(dojo.widget.waiNames, function (name) {
			var wai = dojo.widget.wai[name];
			var val = baseNode.getAttribute(wai.name);
			if (val) {
				if (val.indexOf("-") == -1) {
					dojo.widget.wai.setAttr(baseNode, wai.name, "role", val);
				} else {
					var statePair = val.split("-");
					dojo.widget.wai.setAttr(baseNode, wai.name, statePair[0], statePair[1]);
				}
			}
		}, this);
		var onBuild = baseNode.getAttribute(this.onBuildProperty);
		if (onBuild) {
			eval("var node = baseNode; var widget = targetObj; " + onBuild);
		}
	}
};
dojo.widget.getDojoEventsFromStr = function (str) {
	var re = /(dojoOn([a-z]+)(\s?))=/gi;
	var evts = str ? str.match(re) || [] : [];
	var ret = [];
	var lem = {};
	for (var x = 0; x < evts.length; x++) {
		if (evts[x].length < 1) {
			continue;
		}
		var cm = evts[x].replace(/\s/, "");
		cm = (cm.slice(0, cm.length - 1));
		if (!lem[cm]) {
			lem[cm] = true;
			ret.push(cm);
		}
	}
	return ret;
};
dojo.declare("dojo.widget.DomWidget", dojo.widget.Widget, function () {
	if ((arguments.length > 0) && (typeof arguments[0] == "object")) {
		this.create(arguments[0]);
	}
}, {templateNode:null, templateString:null, templateCssString:null, preventClobber:false, domNode:null, containerNode:null, widgetsInTemplate:false, addChild:function (widget, overrideContainerNode, pos, ref, insertIndex) {
	if (!this.isContainer) {
		dojo.debug("dojo.widget.DomWidget.addChild() attempted on non-container widget");
		return null;
	} else {
		if (insertIndex == undefined) {
			insertIndex = this.children.length;
		}
		this.addWidgetAsDirectChild(widget, overrideContainerNode, pos, ref, insertIndex);
		this.registerChild(widget, insertIndex);
	}
	return widget;
}, addWidgetAsDirectChild:function (widget, overrideContainerNode, pos, ref, insertIndex) {
	if ((!this.containerNode) && (!overrideContainerNode)) {
		this.containerNode = this.domNode;
	}
	var cn = (overrideContainerNode) ? overrideContainerNode : this.containerNode;
	if (!pos) {
		pos = "after";
	}
	if (!ref) {
		if (!cn) {
			cn = dojo.body();
		}
		ref = cn.lastChild;
	}
	if (!insertIndex) {
		insertIndex = 0;
	}
	widget.domNode.setAttribute("dojoinsertionindex", insertIndex);
	if (!ref) {
		cn.appendChild(widget.domNode);
	} else {
		if (pos == "insertAtIndex") {
			dojo.dom.insertAtIndex(widget.domNode, ref.parentNode, insertIndex);
		} else {
			if ((pos == "after") && (ref === cn.lastChild)) {
				cn.appendChild(widget.domNode);
			} else {
				dojo.dom.insertAtPosition(widget.domNode, cn, pos);
			}
		}
	}
}, registerChild:function (widget, insertionIndex) {
	widget.dojoInsertionIndex = insertionIndex;
	var idx = -1;
	for (var i = 0; i < this.children.length; i++) {
		if (this.children[i].dojoInsertionIndex <= insertionIndex) {
			idx = i;
		}
	}
	this.children.splice(idx + 1, 0, widget);
	widget.parent = this;
	widget.addedTo(this, idx + 1);
	delete dojo.widget.manager.topWidgets[widget.widgetId];
}, removeChild:function (widget) {
	dojo.dom.removeNode(widget.domNode);
	return dojo.widget.DomWidget.superclass.removeChild.call(this, widget);
}, getFragNodeRef:function (frag) {
	if (!frag) {
		return null;
	}
	if (!frag[this.getNamespacedType()]) {
		dojo.raise("Error: no frag for widget type " + this.getNamespacedType() + ", id " + this.widgetId + " (maybe a widget has set it's type incorrectly)");
	}
	return frag[this.getNamespacedType()]["nodeRef"];
}, postInitialize:function (args, frag, parentComp) {
	var sourceNodeRef = this.getFragNodeRef(frag);
	if (parentComp && (parentComp.snarfChildDomOutput || !sourceNodeRef)) {
		parentComp.addWidgetAsDirectChild(this, "", "insertAtIndex", "", args["dojoinsertionindex"], sourceNodeRef);
	} else {
		if (sourceNodeRef) {
			if (this.domNode && (this.domNode !== sourceNodeRef)) {
				this._sourceNodeRef = dojo.dom.replaceNode(sourceNodeRef, this.domNode);
			}
		}
	}
	if (parentComp) {
		parentComp.registerChild(this, args.dojoinsertionindex);
	} else {
		dojo.widget.manager.topWidgets[this.widgetId] = this;
	}
	if (this.widgetsInTemplate) {
		var parser = new dojo.xml.Parse();
		var subContainerNode;
		var subnodes = this.domNode.getElementsByTagName("*");
		for (var i = 0; i < subnodes.length; i++) {
			if (subnodes[i].getAttribute("dojoAttachPoint") == "subContainerWidget") {
				subContainerNode = subnodes[i];
			}
			if (subnodes[i].getAttribute("dojoType")) {
				subnodes[i].setAttribute("isSubWidget", true);
			}
		}
		if (this.isContainer && !this.containerNode) {
			if (subContainerNode) {
				var src = this.getFragNodeRef(frag);
				if (src) {
					dojo.dom.moveChildren(src, subContainerNode);
					frag["dojoDontFollow"] = true;
				}
			} else {
				dojo.debug("No subContainerWidget node can be found in template file for widget " + this);
			}
		}
		var templatefrag = parser.parseElement(this.domNode, null, true);
		dojo.widget.getParser().createSubComponents(templatefrag, this);
		var subwidgets = [];
		var stack = [this];
		var w;
		while ((w = stack.pop())) {
			for (var i = 0; i < w.children.length; i++) {
				var cwidget = w.children[i];
				if (cwidget._processedSubWidgets || !cwidget.extraArgs["issubwidget"]) {
					continue;
				}
				subwidgets.push(cwidget);
				if (cwidget.isContainer) {
					stack.push(cwidget);
				}
			}
		}
		for (var i = 0; i < subwidgets.length; i++) {
			var widget = subwidgets[i];
			if (widget._processedSubWidgets) {
				dojo.debug("This should not happen: widget._processedSubWidgets is already true!");
				return;
			}
			widget._processedSubWidgets = true;
			if (widget.extraArgs["dojoattachevent"]) {
				var evts = widget.extraArgs["dojoattachevent"].split(";");
				for (var j = 0; j < evts.length; j++) {
					var thisFunc = null;
					var tevt = dojo.string.trim(evts[j]);
					if (tevt.indexOf(":") >= 0) {
						var funcNameArr = tevt.split(":");
						tevt = dojo.string.trim(funcNameArr[0]);
						thisFunc = dojo.string.trim(funcNameArr[1]);
					}
					if (!thisFunc) {
						thisFunc = tevt;
					}
					if (dojo.lang.isFunction(widget[tevt])) {
						dojo.event.kwConnect({srcObj:widget, srcFunc:tevt, targetObj:this, targetFunc:thisFunc});
					} else {
						alert(tevt + " is not a function in widget " + widget);
					}
				}
			}
			if (widget.extraArgs["dojoattachpoint"]) {
				this[widget.extraArgs["dojoattachpoint"]] = widget;
			}
		}
	}
	if (this.isContainer && !frag["dojoDontFollow"]) {
		dojo.widget.getParser().createSubComponents(frag, this);
	}
}, buildRendering:function (args, frag) {
	var ts = dojo.widget._templateCache[this.widgetType];
	if (args["templatecsspath"]) {
		args["templateCssPath"] = args["templatecsspath"];
	}
	var cpath = args["templateCssPath"] || this.templateCssPath;
	if (cpath && !dojo.widget._cssFiles[cpath.toString()]) {
		if ((!this.templateCssString) && (cpath)) {
			this.templateCssString = dojo.hostenv.getText(cpath);
			this.templateCssPath = null;
		}
		dojo.widget._cssFiles[cpath.toString()] = true;
	}
	if ((this["templateCssString"]) && (!dojo.widget._cssStrings[this.templateCssString])) {
		dojo.html.insertCssText(this.templateCssString, null, cpath);
		dojo.widget._cssStrings[this.templateCssString] = true;
	}
	if ((!this.preventClobber) && ((this.templatePath) || (this.templateNode) || ((this["templateString"]) && (this.templateString.length)) || ((typeof ts != "undefined") && ((ts["string"]) || (ts["node"]))))) {
		this.buildFromTemplate(args, frag);
	} else {
		this.domNode = this.getFragNodeRef(frag);
	}
	this.fillInTemplate(args, frag);
}, buildFromTemplate:function (args, frag) {
	var avoidCache = false;
	if (args["templatepath"]) {
		args["templatePath"] = args["templatepath"];
	}
	dojo.widget.fillFromTemplateCache(this, args["templatePath"], null, avoidCache);
	var ts = dojo.widget._templateCache[this.templatePath ? this.templatePath.toString() : this.widgetType];
	if ((ts) && (!avoidCache)) {
		if (!this.templateString.length) {
			this.templateString = ts["string"];
		}
		if (!this.templateNode) {
			this.templateNode = ts["node"];
		}
	}
	var matches = false;
	var node = null;
	var tstr = this.templateString;
	if ((!this.templateNode) && (this.templateString)) {
		matches = this.templateString.match(/\$\{([^\}]+)\}/g);
		if (matches) {
			var hash = this.strings || {};
			for (var key in dojo.widget.defaultStrings) {
				if (dojo.lang.isUndefined(hash[key])) {
					hash[key] = dojo.widget.defaultStrings[key];
				}
			}
			for (var i = 0; i < matches.length; i++) {
				var key = matches[i];
				key = key.substring(2, key.length - 1);
				var kval = (key.substring(0, 5) == "this.") ? dojo.lang.getObjPathValue(key.substring(5), this) : hash[key];
				var value;
				if ((kval) || (dojo.lang.isString(kval))) {
					value = new String((dojo.lang.isFunction(kval)) ? kval.call(this, key, this.templateString) : kval);
					while (value.indexOf("\"") > -1) {
						value = value.replace("\"", "&quot;");
					}
					tstr = tstr.replace(matches[i], value);
				}
			}
		} else {
			this.templateNode = this.createNodesFromText(this.templateString, true)[0];
			if (!avoidCache) {
				ts.node = this.templateNode;
			}
		}
	}
	if ((!this.templateNode) && (!matches)) {
		dojo.debug("DomWidget.buildFromTemplate: could not create template");
		return false;
	} else {
		if (!matches) {
			node = this.templateNode.cloneNode(true);
			if (!node) {
				return false;
			}
		} else {
			node = this.createNodesFromText(tstr, true)[0];
		}
	}
	this.domNode = node;
	this.attachTemplateNodes();
	if (this.isContainer && this.containerNode) {
		var src = this.getFragNodeRef(frag);
		if (src) {
			dojo.dom.moveChildren(src, this.containerNode);
		}
	}
}, attachTemplateNodes:function (baseNode, targetObj) {
	if (!baseNode) {
		baseNode = this.domNode;
	}
	if (!targetObj) {
		targetObj = this;
	}
	return dojo.widget.attachTemplateNodes(baseNode, targetObj, dojo.widget.getDojoEventsFromStr(this.templateString));
}, fillInTemplate:function () {
}, destroyRendering:function () {
	try {
		dojo.dom.destroyNode(this.domNode);
		delete this.domNode;
	}
	catch (e) {
	}
	if (this._sourceNodeRef) {
		try {
			dojo.dom.destroyNode(this._sourceNodeRef);
		}
		catch (e) {
		}
	}
}, createNodesFromText:function () {
	dojo.unimplemented("dojo.widget.DomWidget.createNodesFromText");
}});

