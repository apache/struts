/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.widget.Manager");
dojo.require("dojo.lang.array");
dojo.require("dojo.lang.func");
dojo.require("dojo.event.*");
dojo.widget.manager = new function () {
	this.widgets = [];
	this.widgetIds = [];
	this.topWidgets = {};
	var widgetTypeCtr = {};
	var renderPrefixCache = [];
	this.getUniqueId = function (widgetType) {
		var widgetId;
		do {
			widgetId = widgetType + "_" + (widgetTypeCtr[widgetType] != undefined ? ++widgetTypeCtr[widgetType] : widgetTypeCtr[widgetType] = 0);
		} while (this.getWidgetById(widgetId));
		return widgetId;
	};
	this.add = function (widget) {
		this.widgets.push(widget);
		if (!widget.extraArgs["id"]) {
			widget.extraArgs["id"] = widget.extraArgs["ID"];
		}
		if (widget.widgetId == "") {
			if (widget["id"]) {
				widget.widgetId = widget["id"];
			} else {
				if (widget.extraArgs["id"]) {
					widget.widgetId = widget.extraArgs["id"];
				} else {
					widget.widgetId = this.getUniqueId(widget.ns + "_" + widget.widgetType);
				}
			}
		}
		if (this.widgetIds[widget.widgetId]) {
			dojo.debug("widget ID collision on ID: " + widget.widgetId);
		}
		this.widgetIds[widget.widgetId] = widget;
	};
	this.destroyAll = function () {
		for (var x = this.widgets.length - 1; x >= 0; x--) {
			try {
				this.widgets[x].destroy(true);
				delete this.widgets[x];
			}
			catch (e) {
			}
		}
	};
	this.remove = function (widgetIndex) {
		if (dojo.lang.isNumber(widgetIndex)) {
			var tw = this.widgets[widgetIndex].widgetId;
			delete this.topWidgets[tw];
			delete this.widgetIds[tw];
			this.widgets.splice(widgetIndex, 1);
		} else {
			this.removeById(widgetIndex);
		}
	};
	this.removeById = function (id) {
		if (!dojo.lang.isString(id)) {
			id = id["widgetId"];
			if (!id) {
				dojo.debug("invalid widget or id passed to removeById");
				return;
			}
		}
		for (var i = 0; i < this.widgets.length; i++) {
			if (this.widgets[i].widgetId == id) {
				this.remove(i);
				break;
			}
		}
	};
	this.getWidgetById = function (id) {
		if (dojo.lang.isString(id)) {
			return this.widgetIds[id];
		}
		return id;
	};
	this.getWidgetsByType = function (type) {
		var lt = type.toLowerCase();
		var getType = (type.indexOf(":") < 0 ? function (x) {
			return x.widgetType.toLowerCase();
		} : function (x) {
			return x.getNamespacedType();
		});
		var ret = [];
		dojo.lang.forEach(this.widgets, function (x) {
			if (getType(x) == lt) {
				ret.push(x);
			}
		});
		return ret;
	};
	this.getWidgetsByFilter = function (unaryFunc, onlyOne) {
		var ret = [];
		dojo.lang.every(this.widgets, function (x) {
			if (unaryFunc(x)) {
				ret.push(x);
				if (onlyOne) {
					return false;
				}
			}
			return true;
		});
		return (onlyOne ? ret[0] : ret);
	};
	this.getAllWidgets = function () {
		return this.widgets.concat();
	};
	this.getWidgetByNode = function (node) {
		var w = this.getAllWidgets();
		node = dojo.byId(node);
		for (var i = 0; i < w.length; i++) {
			if (w[i].domNode == node) {
				return w[i];
			}
		}
		return null;
	};
	this.byId = this.getWidgetById;
	this.byType = this.getWidgetsByType;
	this.byFilter = this.getWidgetsByFilter;
	this.byNode = this.getWidgetByNode;
	var knownWidgetImplementations = {};
	var widgetPackages = ["dojo.widget"];
	for (var i = 0; i < widgetPackages.length; i++) {
		widgetPackages[widgetPackages[i]] = true;
	}
	this.registerWidgetPackage = function (pname) {
		if (!widgetPackages[pname]) {
			widgetPackages[pname] = true;
			widgetPackages.push(pname);
		}
	};
	this.getWidgetPackageList = function () {
		return dojo.lang.map(widgetPackages, function (elt) {
			return (elt !== true ? elt : undefined);
		});
	};
	this.getImplementation = function (widgetName, ctorObject, mixins, ns) {
		var impl = this.getImplementationName(widgetName, ns);
		if (impl) {
			var ret = ctorObject ? new impl(ctorObject) : new impl();
			return ret;
		}
	};
	function buildPrefixCache() {
		for (var renderer in dojo.render) {
			if (dojo.render[renderer]["capable"] === true) {
				var prefixes = dojo.render[renderer].prefixes;
				for (var i = 0; i < prefixes.length; i++) {
					renderPrefixCache.push(prefixes[i].toLowerCase());
				}
			}
		}
	}
	var findImplementationInModule = function (lowerCaseWidgetName, module) {
		if (!module) {
			return null;
		}
		for (var i = 0, l = renderPrefixCache.length, widgetModule; i <= l; i++) {
			widgetModule = (i < l ? module[renderPrefixCache[i]] : module);
			if (!widgetModule) {
				continue;
			}
			for (var name in widgetModule) {
				if (name.toLowerCase() == lowerCaseWidgetName) {
					return widgetModule[name];
				}
			}
		}
		return null;
	};
	var findImplementation = function (lowerCaseWidgetName, moduleName) {
		var module = dojo.evalObjPath(moduleName, false);
		return (module ? findImplementationInModule(lowerCaseWidgetName, module) : null);
	};
	this.getImplementationName = function (widgetName, ns) {
		var lowerCaseWidgetName = widgetName.toLowerCase();
		ns = ns || "dojo";
		var imps = knownWidgetImplementations[ns] || (knownWidgetImplementations[ns] = {});
		var impl = imps[lowerCaseWidgetName];
		if (impl) {
			return impl;
		}
		if (!renderPrefixCache.length) {
			buildPrefixCache();
		}
		var nsObj = dojo.ns.get(ns);
		if (!nsObj) {
			dojo.ns.register(ns, ns + ".widget");
			nsObj = dojo.ns.get(ns);
		}
		if (nsObj) {
			nsObj.resolve(widgetName);
		}
		impl = findImplementation(lowerCaseWidgetName, nsObj.module);
		if (impl) {
			return (imps[lowerCaseWidgetName] = impl);
		}
		nsObj = dojo.ns.require(ns);
		if ((nsObj) && (nsObj.resolver)) {
			nsObj.resolve(widgetName);
			impl = findImplementation(lowerCaseWidgetName, nsObj.module);
			if (impl) {
				return (imps[lowerCaseWidgetName] = impl);
			}
		}
		dojo.deprecated("dojo.widget.Manager.getImplementationName", "Could not locate widget implementation for \"" + widgetName + "\" in \"" + nsObj.module + "\" registered to namespace \"" + nsObj.name + "\". " + "Developers must specify correct namespaces for all non-Dojo widgets", "0.5");
		for (var i = 0; i < widgetPackages.length; i++) {
			impl = findImplementation(lowerCaseWidgetName, widgetPackages[i]);
			if (impl) {
				return (imps[lowerCaseWidgetName] = impl);
			}
		}
		throw new Error("Could not locate widget implementation for \"" + widgetName + "\" in \"" + nsObj.module + "\" registered to namespace \"" + nsObj.name + "\"");
	};
	this.resizing = false;
	this.onWindowResized = function () {
		if (this.resizing) {
			return;
		}
		try {
			this.resizing = true;
			for (var id in this.topWidgets) {
				var child = this.topWidgets[id];
				if (child.checkSize) {
					child.checkSize();
				}
			}
		}
		catch (e) {
		}
		finally {
			this.resizing = false;
		}
	};
	if (typeof window != "undefined") {
		dojo.addOnLoad(this, "onWindowResized");
		dojo.event.connect(window, "onresize", this, "onWindowResized");
	}
};
(function () {
	var dw = dojo.widget;
	var dwm = dw.manager;
	var h = dojo.lang.curry(dojo.lang, "hitch", dwm);
	var g = function (oldName, newName) {
		dw[(newName || oldName)] = h(oldName);
	};
	g("add", "addWidget");
	g("destroyAll", "destroyAllWidgets");
	g("remove", "removeWidget");
	g("removeById", "removeWidgetById");
	g("getWidgetById");
	g("getWidgetById", "byId");
	g("getWidgetsByType");
	g("getWidgetsByFilter");
	g("getWidgetsByType", "byType");
	g("getWidgetsByFilter", "byFilter");
	g("getWidgetByNode", "byNode");
	dw.all = function (n) {
		var widgets = dwm.getAllWidgets.apply(dwm, arguments);
		if (arguments.length > 0) {
			return widgets[n];
		}
		return widgets;
	};
	g("registerWidgetPackage");
	g("getImplementation", "getWidgetImplementation");
	g("getImplementationName", "getWidgetImplementationName");
	dw.widgets = dwm.widgets;
	dw.widgetIds = dwm.widgetIds;
	dw.root = dwm.root;
})();

