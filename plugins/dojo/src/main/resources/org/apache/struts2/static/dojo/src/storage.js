/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.storage");
dojo.require("dojo.lang.*");
dojo.require("dojo.event.*");
dojo.storage = new function () {
};
dojo.declare("dojo.storage", null, {SUCCESS:"success", FAILED:"failed", PENDING:"pending", SIZE_NOT_AVAILABLE:"Size not available", SIZE_NO_LIMIT:"No size limit", namespace:"default", onHideSettingsUI:null, initialize:function () {
	dojo.unimplemented("dojo.storage.initialize");
}, isAvailable:function () {
	dojo.unimplemented("dojo.storage.isAvailable");
}, put:function (key, value, resultsHandler) {
	dojo.unimplemented("dojo.storage.put");
}, get:function (key) {
	dojo.unimplemented("dojo.storage.get");
}, hasKey:function (key) {
	return (this.get(key) != null);
}, getKeys:function () {
	dojo.unimplemented("dojo.storage.getKeys");
}, clear:function () {
	dojo.unimplemented("dojo.storage.clear");
}, remove:function (key) {
	dojo.unimplemented("dojo.storage.remove");
}, isPermanent:function () {
	dojo.unimplemented("dojo.storage.isPermanent");
}, getMaximumSize:function () {
	dojo.unimplemented("dojo.storage.getMaximumSize");
}, hasSettingsUI:function () {
	return false;
}, showSettingsUI:function () {
	dojo.unimplemented("dojo.storage.showSettingsUI");
}, hideSettingsUI:function () {
	dojo.unimplemented("dojo.storage.hideSettingsUI");
}, getType:function () {
	dojo.unimplemented("dojo.storage.getType");
}, isValidKey:function (keyName) {
	if ((keyName == null) || (typeof keyName == "undefined")) {
		return false;
	}
	return /^[0-9A-Za-z_]*$/.test(keyName);
}});
dojo.storage.manager = new function () {
	this.currentProvider = null;
	this.available = false;
	this._initialized = false;
	this._providers = [];
	this.namespace = "default";
	this.initialize = function () {
		this.autodetect();
	};
	this.register = function (name, instance) {
		this._providers[this._providers.length] = instance;
		this._providers[name] = instance;
	};
	this.setProvider = function (storageClass) {
	};
	this.autodetect = function () {
		if (this._initialized == true) {
			return;
		}
		var providerToUse = null;
		for (var i = 0; i < this._providers.length; i++) {
			providerToUse = this._providers[i];
			if (dojo.lang.isUndefined(djConfig["forceStorageProvider"]) == false && providerToUse.getType() == djConfig["forceStorageProvider"]) {
				providerToUse.isAvailable();
				break;
			} else {
				if (dojo.lang.isUndefined(djConfig["forceStorageProvider"]) == true && providerToUse.isAvailable()) {
					break;
				}
			}
		}
		if (providerToUse == null) {
			this._initialized = true;
			this.available = false;
			this.currentProvider = null;
			dojo.raise("No storage provider found for this platform");
		}
		this.currentProvider = providerToUse;
		for (var i in providerToUse) {
			dojo.storage[i] = providerToUse[i];
		}
		dojo.storage.manager = this;
		dojo.storage.initialize();
		this._initialized = true;
		this.available = true;
	};
	this.isAvailable = function () {
		return this.available;
	};
	this.isInitialized = function () {
		if (this.currentProvider.getType() == "dojo.storage.browser.FlashStorageProvider" && dojo.flash.ready == false) {
			return false;
		} else {
			return this._initialized;
		}
	};
	this.supportsProvider = function (storageClass) {
		try {
			var provider = eval("new " + storageClass + "()");
			var results = provider.isAvailable();
			if (results == null || typeof results == "undefined") {
				return false;
			}
			return results;
		}
		catch (exception) {
			return false;
		}
	};
	this.getProvider = function () {
		return this.currentProvider;
	};
	this.loaded = function () {
	};
};

