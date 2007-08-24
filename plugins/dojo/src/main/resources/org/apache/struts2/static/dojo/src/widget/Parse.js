/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.widget.Parse");
dojo.require("dojo.widget.Manager");
dojo.require("dojo.dom");
dojo.widget.Parse = function (fragment) {
	this.propertySetsList = [];
	this.fragment = fragment;
	this.createComponents = function (frag, parentComp) {
		var comps = [];
		var built = false;
		try {
			if (frag && frag.tagName && (frag != frag.nodeRef)) {
				var djTags = dojo.widget.tags;
				var tna = String(frag.tagName).split(";");
				for (var x = 0; x < tna.length; x++) {
					var ltn = tna[x].replace(/^\s+|\s+$/g, "").toLowerCase();
					frag.tagName = ltn;
					var ret;
					if (djTags[ltn]) {
						built = true;
						ret = djTags[ltn](frag, this, parentComp, frag.index);
						comps.push(ret);
					} else {
						if (ltn.indexOf(":") == -1) {
							ltn = "dojo:" + ltn;
						}
						ret = dojo.widget.buildWidgetFromParseTree(ltn, frag, this, parentComp, frag.index);
						if (ret) {
							built = true;
							comps.push(ret);
						}
					}
				}
			}
		}
		catch (e) {
			dojo.debug("dojo.widget.Parse: error:", e);
		}
		if (!built) {
			comps = comps.concat(this.createSubComponents(frag, parentComp));
		}
		return comps;
	};
	this.createSubComponents = function (fragment, parentComp) {
		var frag, comps = [];
		for (var item in fragment) {
			frag = fragment[item];
			if (frag && typeof frag == "object" && (frag != fragment.nodeRef) && (frag != fragment.tagName) && (!dojo.dom.isNode(frag))) {
				comps = comps.concat(this.createComponents(frag, parentComp));
			}
		}
		return comps;
	};
	this.parsePropertySets = function (fragment) {
		return [];
	};
	this.parseProperties = function (fragment) {
		var properties = {};
		for (var item in fragment) {
			if ((fragment[item] == fragment.tagName) || (fragment[item] == fragment.nodeRef)) {
			} else {
				var frag = fragment[item];
				if (frag.tagName && dojo.widget.tags[frag.tagName.toLowerCase()]) {
				} else {
					if (frag[0] && frag[0].value != "" && frag[0].value != null) {
						try {
							if (item.toLowerCase() == "dataprovider") {
								var _this = this;
								this.getDataProvider(_this, frag[0].value);
								properties.dataProvider = this.dataProvider;
							}
							properties[item] = frag[0].value;
							var nestedProperties = this.parseProperties(frag);
							for (var property in nestedProperties) {
								properties[property] = nestedProperties[property];
							}
						}
						catch (e) {
							dojo.debug(e);
						}
					}
				}
				switch (item.toLowerCase()) {
				  case "checked":
				  case "disabled":
					if (typeof properties[item] != "boolean") {
						properties[item] = true;
					}
					break;
				}
			}
		}
		return properties;
	};
	this.getDataProvider = function (objRef, dataUrl) {
		dojo.io.bind({url:dataUrl, load:function (type, evaldObj) {
			if (type == "load") {
				objRef.dataProvider = evaldObj;
			}
		}, mimetype:"text/javascript", sync:true});
	};
	this.getPropertySetById = function (propertySetId) {
		for (var x = 0; x < this.propertySetsList.length; x++) {
			if (propertySetId == this.propertySetsList[x]["id"][0].value) {
				return this.propertySetsList[x];
			}
		}
		return "";
	};
	this.getPropertySetsByType = function (componentType) {
		var propertySets = [];
		for (var x = 0; x < this.propertySetsList.length; x++) {
			var cpl = this.propertySetsList[x];
			var cpcc = cpl.componentClass || cpl.componentType || null;
			var propertySetId = this.propertySetsList[x]["id"][0].value;
			if (cpcc && (propertySetId == cpcc[0].value)) {
				propertySets.push(cpl);
			}
		}
		return propertySets;
	};
	this.getPropertySets = function (fragment) {
		var ppl = "dojo:propertyproviderlist";
		var propertySets = [];
		var tagname = fragment.tagName;
		if (fragment[ppl]) {
			var propertyProviderIds = fragment[ppl].value.split(" ");
			for (var propertySetId in propertyProviderIds) {
				if ((propertySetId.indexOf("..") == -1) && (propertySetId.indexOf("://") == -1)) {
					var propertySet = this.getPropertySetById(propertySetId);
					if (propertySet != "") {
						propertySets.push(propertySet);
					}
				} else {
				}
			}
		}
		return this.getPropertySetsByType(tagname).concat(propertySets);
	};
	this.createComponentFromScript = function (nodeRef, componentName, properties, ns) {
		properties.fastMixIn = true;
		var ltn = (ns || "dojo") + ":" + componentName.toLowerCase();
		if (dojo.widget.tags[ltn]) {
			return [dojo.widget.tags[ltn](properties, this, null, null, properties)];
		}
		return [dojo.widget.buildWidgetFromParseTree(ltn, properties, this, null, null, properties)];
	};
};
dojo.widget._parser_collection = {"dojo":new dojo.widget.Parse()};
dojo.widget.getParser = function (name) {
	if (!name) {
		name = "dojo";
	}
	if (!this._parser_collection[name]) {
		this._parser_collection[name] = new dojo.widget.Parse();
	}
	return this._parser_collection[name];
};
dojo.widget.createWidget = function (name, props, refNode, position) {
	var isNode = false;
	var isNameStr = (typeof name == "string");
	if (isNameStr) {
		var pos = name.indexOf(":");
		var ns = (pos > -1) ? name.substring(0, pos) : "dojo";
		if (pos > -1) {
			name = name.substring(pos + 1);
		}
		var lowerCaseName = name.toLowerCase();
		var namespacedName = ns + ":" + lowerCaseName;
		isNode = (dojo.byId(name) && !dojo.widget.tags[namespacedName]);
	}
	if ((arguments.length == 1) && (isNode || !isNameStr)) {
		var xp = new dojo.xml.Parse();
		var tn = isNode ? dojo.byId(name) : name;
		return dojo.widget.getParser().createComponents(xp.parseElement(tn, null, true))[0];
	}
	function fromScript(placeKeeperNode, name, props, ns) {
		props[namespacedName] = {dojotype:[{value:lowerCaseName}], nodeRef:placeKeeperNode, fastMixIn:true};
		props.ns = ns;
		return dojo.widget.getParser().createComponentFromScript(placeKeeperNode, name, props, ns);
	}
	props = props || {};
	var notRef = false;
	var tn = null;
	var h = dojo.render.html.capable;
	if (h) {
		tn = document.createElement("span");
	}
	if (!refNode) {
		notRef = true;
		refNode = tn;
		if (h) {
			dojo.body().appendChild(refNode);
		}
	} else {
		if (position) {
			dojo.dom.insertAtPosition(tn, refNode, position);
		} else {
			tn = refNode;
		}
	}
	var widgetArray = fromScript(tn, name.toLowerCase(), props, ns);
	if ((!widgetArray) || (!widgetArray[0]) || (typeof widgetArray[0].widgetType == "undefined")) {
		throw new Error("createWidget: Creation of \"" + name + "\" widget failed.");
	}
	try {
		if (notRef && widgetArray[0].domNode.parentNode) {
			widgetArray[0].domNode.parentNode.removeChild(widgetArray[0].domNode);
		}
	}
	catch (e) {
		dojo.debug(e);
	}
	return widgetArray[0];
};

