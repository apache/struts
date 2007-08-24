/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.ns");
dojo.ns = {namespaces:{}, failed:{}, loading:{}, loaded:{}, register:function (name, module, resolver, noOverride) {
	if (!noOverride || !this.namespaces[name]) {
		this.namespaces[name] = new dojo.ns.Ns(name, module, resolver);
	}
}, allow:function (name) {
	if (this.failed[name]) {
		return false;
	}
	if ((djConfig.excludeNamespace) && (dojo.lang.inArray(djConfig.excludeNamespace, name))) {
		return false;
	}
	return ((name == this.dojo) || (!djConfig.includeNamespace) || (dojo.lang.inArray(djConfig.includeNamespace, name)));
}, get:function (name) {
	return this.namespaces[name];
}, require:function (name) {
	var ns = this.namespaces[name];
	if ((ns) && (this.loaded[name])) {
		return ns;
	}
	if (!this.allow(name)) {
		return false;
	}
	if (this.loading[name]) {
		dojo.debug("dojo.namespace.require: re-entrant request to load namespace \"" + name + "\" must fail.");
		return false;
	}
	var req = dojo.require;
	this.loading[name] = true;
	try {
		if (name == "dojo") {
			req("dojo.namespaces.dojo");
		} else {
			if (!dojo.hostenv.moduleHasPrefix(name)) {
				dojo.registerModulePath(name, "../" + name);
			}
			req([name, "manifest"].join("."), false, true);
		}
		if (!this.namespaces[name]) {
			this.failed[name] = true;
		}
	}
	finally {
		this.loading[name] = false;
	}
	return this.namespaces[name];
}};
dojo.ns.Ns = function (name, module, resolver) {
	this.name = name;
	this.module = module;
	this.resolver = resolver;
	this._loaded = [];
	this._failed = [];
};
dojo.ns.Ns.prototype.resolve = function (name, domain, omitModuleCheck) {
	if (!this.resolver || djConfig["skipAutoRequire"]) {
		return false;
	}
	var fullName = this.resolver(name, domain);
	if ((fullName) && (!this._loaded[fullName]) && (!this._failed[fullName])) {
		var req = dojo.require;
		req(fullName, false, true);
		if (dojo.hostenv.findModule(fullName, false)) {
			this._loaded[fullName] = true;
		} else {
			if (!omitModuleCheck) {
				dojo.raise("dojo.ns.Ns.resolve: module '" + fullName + "' not found after loading via namespace '" + this.name + "'");
			}
			this._failed[fullName] = true;
		}
	}
	return Boolean(this._loaded[fullName]);
};
dojo.registerNamespace = function (name, module, resolver) {
	dojo.ns.register.apply(dojo.ns, arguments);
};
dojo.registerNamespaceResolver = function (name, resolver) {
	var n = dojo.ns.namespaces[name];
	if (n) {
		n.resolver = resolver;
	}
};
dojo.registerNamespaceManifest = function (module, path, name, widgetModule, resolver) {
	dojo.registerModulePath(name, path);
	dojo.registerNamespace(name, widgetModule, resolver);
};
dojo.registerNamespace("dojo", "dojo.widget");

