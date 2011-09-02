/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.storage.browser");
dojo.require("dojo.storage");
dojo.require("dojo.flash");
dojo.require("dojo.json");
dojo.require("dojo.uri.*");
dojo.storage.browser.FileStorageProvider = function () {
};
dojo.inherits(dojo.storage.browser.FileStorageProvider, dojo.storage);
dojo.storage.browser.FileStorageProvider._KEY_INDEX_FILENAME = "__dojoAllKeys";
dojo.storage.browser.FileStorageProvider._APPLET_ID = "__dojoFileJavaObj";
dojo.lang.extend(dojo.storage.browser.FileStorageProvider, {namespace:"default", initialized:false, _available:null, _statusHandler:null, _keyIndex:new Array(), initialize:function () {
	if (djConfig["disableFileStorage"] == true) {
		return;
	}
	this._loadKeyIndex();
	this.initialized = true;
	dojo.storage.manager.loaded();
}, isAvailable:function () {
	this._available = false;
	var protocol = window.location.protocol;
	if (protocol.indexOf("file") != -1 || protocol.indexOf("chrome") != -1) {
		this._available = this._isAvailableXPCOM();
		if (this._available == false) {
			this._available = this._isAvailableActiveX();
		}
	}
	return this._available;
}, put:function (key, value, resultsHandler) {
	if (this.isValidKey(key) == false) {
		dojo.raise("Invalid key given: " + key);
	}
	this._statusHandler = resultsHandler;
	try {
		this._save(key, value);
		resultsHandler.call(null, dojo.storage.SUCCESS, key);
	}
	catch (e) {
		this._statusHandler.call(null, dojo.storage.FAILED, key, e.toString());
	}
}, get:function (key) {
	if (this.isValidKey(key) == false) {
		dojo.raise("Invalid key given: " + key);
	}
	var results = this._load(key);
	return results;
}, getKeys:function () {
	return this._keyIndex;
}, hasKey:function (key) {
	if (this.isValidKey(key) == false) {
		dojo.raise("Invalid key given: " + key);
	}
	this._loadKeyIndex();
	var exists = false;
	for (var i = 0; i < this._keyIndex.length; i++) {
		if (this._keyIndex[i] == key) {
			exists = true;
		}
	}
	return exists;
}, clear:function () {
	this._loadKeyIndex();
	var keyIndex = new Array();
	for (var i = 0; i < this._keyIndex.length; i++) {
		keyIndex[keyIndex.length] = new String(this._keyIndex[i]);
	}
	for (var i = 0; i < keyIndex.length; i++) {
		this.remove(keyIndex[i]);
	}
}, remove:function (key) {
	if (this.isValidKey(key) == false) {
		dojo.raise("Invalid key given: " + key);
	}
	this._loadKeyIndex();
	for (var i = 0; i < this._keyIndex.length; i++) {
		if (this._keyIndex[i] == key) {
			this._keyIndex.splice(i, 1);
			break;
		}
	}
	this._save(dojo.storage.browser.FileStorageProvider._KEY_INDEX_FILENAME, this._keyIndex, false);
	var fullPath = this._getPagePath() + key + ".txt";
	if (this._isAvailableXPCOM()) {
		this._removeXPCOM(fullPath);
	} else {
		if (this._isAvailableActiveX()) {
			this._removeActiveX(fullPath);
		}
	}
}, isPermanent:function () {
	return true;
}, getMaximumSize:function () {
	return dojo.storage.SIZE_NO_LIMIT;
}, hasSettingsUI:function () {
	return false;
}, showSettingsUI:function () {
	dojo.raise(this.getType() + " does not support a storage settings user-interface");
}, hideSettingsUI:function () {
	dojo.raise(this.getType() + " does not support a storage settings user-interface");
}, getType:function () {
	return "dojo.storage.browser.FileStorageProvider";
}, _save:function (key, value, updateKeyIndex) {
	if (typeof updateKeyIndex == "undefined") {
		updateKeyIndex = true;
	}
	if (dojo.lang.isString(value) == false) {
		value = dojo.json.serialize(value);
		value = "/* JavaScript */\n" + value + "\n\n";
	}
	var fullPath = this._getPagePath() + key + ".txt";
	if (this._isAvailableXPCOM()) {
		this._saveFileXPCOM(fullPath, value);
	} else {
		if (this._isAvailableActiveX()) {
			this._saveFileActiveX(fullPath, value);
		}
	}
	if (updateKeyIndex) {
		this._updateKeyIndex(key);
	}
}, _load:function (key) {
	var fullPath = this._getPagePath() + key + ".txt";
	var results = null;
	if (this._isAvailableXPCOM()) {
		results = this._loadFileXPCOM(fullPath);
	} else {
		if (this._isAvailableActiveX()) {
			results = this._loadFileActiveX(fullPath);
		} else {
			if (this._isAvailableJava()) {
				results = this._loadFileJava(fullPath);
			}
		}
	}
	if (results == null) {
		return null;
	}
	if (!dojo.lang.isUndefined(results) && results != null && /^\/\* JavaScript \*\//.test(results)) {
		results = dojo.json.evalJson(results);
	}
	return results;
}, _updateKeyIndex:function (key) {
	this._loadKeyIndex();
	var alreadyAdded = false;
	for (var i = 0; i < this._keyIndex.length; i++) {
		if (this._keyIndex[i] == key) {
			alreadyAdded = true;
			break;
		}
	}
	if (alreadyAdded == false) {
		this._keyIndex[this._keyIndex.length] = key;
	}
	this._save(dojo.storage.browser.FileStorageProvider._KEY_INDEX_FILENAME, this._keyIndex, false);
}, _loadKeyIndex:function () {
	var indexContents = this._load(dojo.storage.browser.FileStorageProvider._KEY_INDEX_FILENAME);
	if (indexContents == null) {
		this._keyIndex = new Array();
	} else {
		this._keyIndex = indexContents;
	}
}, _saveFileXPCOM:function (filename, value) {
	try {
		netscape.security.PrivilegeManager.enablePrivilege("UniversalXPConnect");
		var f = Components.classes["@mozilla.org/file/local;1"].createInstance(Components.interfaces.nsILocalFile);
		f.initWithPath(filename);
		var ouputStream = Components.classes["@mozilla.org/network/file-output-stream;1"].createInstance(Components.interfaces.nsIFileOutputStream);
		ouputStream.init(f, 32 | 4 | 8, 256 + 128, null);
		ouputStream.write(value, value.length);
		ouputStream.close();
	}
	catch (e) {
		var msg = e.toString();
		if (e.name && e.message) {
			msg = e.name + ": " + e.message;
		}
		dojo.raise("dojo.storage.browser.FileStorageProvider._saveFileXPCOM(): " + msg);
	}
}, _loadFileXPCOM:function (filename) {
	try {
		netscape.security.PrivilegeManager.enablePrivilege("UniversalXPConnect");
		var f = Components.classes["@mozilla.org/file/local;1"].createInstance(Components.interfaces.nsILocalFile);
		f.initWithPath(filename);
		if (f.exists() == false) {
			return null;
		}
		var inp = Components.classes["@mozilla.org/network/file-input-stream;1"].createInstance(Components.interfaces.nsIFileInputStream);
		inp.init(f, 1, 4, null);
		var inputStream = Components.classes["@mozilla.org/scriptableinputstream;1"].createInstance(Components.interfaces.nsIScriptableInputStream);
		inputStream.init(inp);
		var results = inputStream.read(inputStream.available());
		return results;
	}
	catch (e) {
		var msg = e.toString();
		if (e.name && e.message) {
			msg = e.name + ": " + e.message;
		}
		dojo.raise("dojo.storage.browser.FileStorageProvider._loadFileXPCOM(): " + msg);
	}
	return null;
}, _saveFileActiveX:function (filename, value) {
	try {
		var fileSystem = new ActiveXObject("Scripting.FileSystemObject");
		var f = fileSystem.OpenTextFile(filename, 2, true);
		f.Write(value);
		f.Close();
	}
	catch (e) {
		var msg = e.toString();
		if (e.name && e.message) {
			msg = e.name + ": " + e.message;
		}
		dojo.raise("dojo.storage.browser.FileStorageProvider._saveFileActiveX(): " + msg);
	}
}, _loadFileActiveX:function (filename) {
	try {
		var fileSystem = new ActiveXObject("Scripting.FileSystemObject");
		if (fileSystem.FileExists(filename) == false) {
			return null;
		}
		var f = fileSystem.OpenTextFile(filename, 1);
		var results = f.ReadAll();
		f.Close();
		return results;
	}
	catch (e) {
		var msg = e.toString();
		if (e.name && e.message) {
			msg = e.name + ": " + e.message;
		}
		dojo.raise("dojo.storage.browser.FileStorageProvider._loadFileActiveX(): " + msg);
	}
}, _saveFileJava:function (filename, value) {
	try {
		var applet = dojo.byId(dojo.storage.browser.FileStorageProvider._APPLET_ID);
		applet.save(filename, value);
	}
	catch (e) {
		var msg = e.toString();
		if (e.name && e.message) {
			msg = e.name + ": " + e.message;
		}
		dojo.raise("dojo.storage.browser.FileStorageProvider._saveFileJava(): " + msg);
	}
}, _loadFileJava:function (filename) {
	try {
		var applet = dojo.byId(dojo.storage.browser.FileStorageProvider._APPLET_ID);
		var results = applet.load(filename);
		return results;
	}
	catch (e) {
		var msg = e.toString();
		if (e.name && e.message) {
			msg = e.name + ": " + e.message;
		}
		dojo.raise("dojo.storage.browser.FileStorageProvider._loadFileJava(): " + msg);
	}
}, _isAvailableActiveX:function () {
	try {
		if (window.ActiveXObject) {
			var fileSystem = new window.ActiveXObject("Scripting.FileSystemObject");
			return true;
		}
	}
	catch (e) {
		dojo.debug(e);
	}
	return false;
}, _isAvailableXPCOM:function () {
	try {
		if (window.Components) {
			netscape.security.PrivilegeManager.enablePrivilege("UniversalXPConnect");
			Components.classes["@mozilla.org/file/local;1"].createInstance(Components.interfaces.nsILocalFile);
			return true;
		}
	}
	catch (e) {
		dojo.debug(e);
	}
	return false;
}, _isAvailableJava:function () {
	try {
		if (dojo.render.html.safari == true || dojo.render.html.opera == true()) {
			if (navigator.javaEnabled() == true) {
				return true;
			}
		}
	}
	catch (e) {
		dojo.debug(e);
	}
	return false;
}, _getPagePath:function () {
	var path = window.location.pathname;
	if (/\.html?$/i.test(path)) {
		path = path.replace(/(?:\/|\\)?[^\.\/\\]*\.html?$/, "");
	}
	if (/^\/?[a-z]+\:/i.test(path)) {
		path = path.replace(/^\/?/, "");
		path = path.replace(/\//g, "\\");
	} else {
		if (/^[\/\\]{2,3}[^\/]/.test(path)) {
			path = path.replace(/^[\/\\]{2,3}/, "");
			path = path.replace(/\//g, "\\");
			path = "\\\\" + path;
		}
	}
	if (/\/$/.test(path) == false && /\\$/.test(path) == false) {
		if (/\//.test(path)) {
			path += "/";
		} else {
			path += "\\";
		}
	}
	path = unescape(path);
	return path;
}, _removeXPCOM:function (filename) {
	try {
		netscape.security.PrivilegeManager.enablePrivilege("UniversalXPConnect");
		var f = Components.classes["@mozilla.org/file/local;1"].createInstance(Components.interfaces.nsILocalFile);
		f.initWithPath(filename);
		if (f.exists() == false || f.isDirectory()) {
			return;
		}
		if (f.isFile()) {
			f.remove(false);
		}
	}
	catch (e) {
		dojo.raise("dojo.storage.browser.FileStorageProvider.remove(): " + e.toString());
	}
}, _removeActiveX:function (filename) {
	try {
		var fileSystem = new ActiveXObject("Scripting.FileSystemObject");
		fileSystem.DeleteFile(filename);
	}
	catch (e) {
		dojo.raise("dojo.storage.browser.FileStorageProvider.remove(): " + e.toString());
	}
}, _removeJava:function (filename) {
	try {
		var applet = dojo.byId(dojo.storage.browser.FileStorageProvider._APPLET_ID);
		applet.remove(filename);
	}
	catch (e) {
		var msg = e.toString();
		if (e.name && e.message) {
			msg = e.name + ": " + e.message;
		}
		dojo.raise("dojo.storage.browser.FileStorageProvider._removeJava(): " + msg);
	}
}, _writeApplet:function () {
	var archive = dojo.uri.moduleUri("dojo", "../DojoFileStorageProvider.jar").toString();
	var tag = "<applet " + "id='" + dojo.storage.browser.FileStorageProvider._APPLET_ID + "' " + "style='position: absolute; top: -500px; left: -500px; width: 1px; height: 1px;' " + "code='DojoFileStorageProvider.class' " + "archive='" + archive + "' " + "width='1' " + "height='1' " + ">" + "</applet>";
	document.writeln(tag);
}});
dojo.storage.browser.WhatWGStorageProvider = function () {
};
dojo.inherits(dojo.storage.browser.WhatWGStorageProvider, dojo.storage);
dojo.lang.extend(dojo.storage.browser.WhatWGStorageProvider, {namespace:"default", initialized:false, _domain:null, _available:null, _statusHandler:null, initialize:function () {
	if (djConfig["disableWhatWGStorage"] == true) {
		return;
	}
	this._domain = location.hostname;
	this.initialized = true;
	dojo.storage.manager.loaded();
}, isAvailable:function () {
	try {
		var myStorage = globalStorage[location.hostname];
	}
	catch (e) {
		this._available = false;
		return this._available;
	}
	this._available = true;
	return this._available;
}, put:function (key, value, resultsHandler) {
	if (this.isValidKey(key) == false) {
		dojo.raise("Invalid key given: " + key);
	}
	this._statusHandler = resultsHandler;
	if (dojo.lang.isString(value)) {
		value = "string:" + value;
	} else {
		value = dojo.json.serialize(value);
	}
	window.addEventListener("storage", function (evt) {
		resultsHandler.call(null, dojo.storage.SUCCESS, key);
	}, false);
	try {
		var myStorage = globalStorage[this._domain];
		myStorage.setItem(key, value);
	}
	catch (e) {
		this._statusHandler.call(null, dojo.storage.FAILED, key, e.toString());
	}
}, get:function (key) {
	if (this.isValidKey(key) == false) {
		dojo.raise("Invalid key given: " + key);
	}
	var myStorage = globalStorage[this._domain];
	var results = myStorage.getItem(key);
	if (results == null) {
		return null;
	}
	results = results.value;
	if (!dojo.lang.isUndefined(results) && results != null && /^string:/.test(results)) {
		results = results.substring("string:".length);
	} else {
		results = dojo.json.evalJson(results);
	}
	return results;
}, getKeys:function () {
	var myStorage = globalStorage[this._domain];
	var keysArray = new Array();
	for (i = 0; i < myStorage.length; i++) {
		keysArray[i] = myStorage.key(i);
	}
	return keysArray;
}, clear:function () {
	var myStorage = globalStorage[this._domain];
	var keys = new Array();
	for (var i = 0; i < myStorage.length; i++) {
		keys[keys.length] = myStorage.key(i);
	}
	for (var i = 0; i < keys.length; i++) {
		myStorage.removeItem(keys[i]);
	}
}, remove:function (key) {
	var myStorage = globalStorage[this._domain];
	myStorage.removeItem(key);
}, isPermanent:function () {
	return true;
}, getMaximumSize:function () {
	return dojo.storage.SIZE_NO_LIMIT;
}, hasSettingsUI:function () {
	return false;
}, showSettingsUI:function () {
	dojo.raise(this.getType() + " does not support a storage settings user-interface");
}, hideSettingsUI:function () {
	dojo.raise(this.getType() + " does not support a storage settings user-interface");
}, getType:function () {
	return "dojo.storage.browser.WhatWGProvider";
}});
dojo.storage.browser.FlashStorageProvider = function () {
};
dojo.inherits(dojo.storage.browser.FlashStorageProvider, dojo.storage);
dojo.lang.extend(dojo.storage.browser.FlashStorageProvider, {namespace:"default", initialized:false, _available:null, _statusHandler:null, initialize:function () {
	if (djConfig["disableFlashStorage"] == true) {
		return;
	}
	var loadedListener = function () {
		dojo.storage._flashLoaded();
	};
	dojo.flash.addLoadedListener(loadedListener);
	var swfloc6 = dojo.uri.moduleUri("dojo", "../Storage_version6.swf").toString();
	var swfloc8 = dojo.uri.moduleUri("dojo", "../Storage_version8.swf").toString();
	dojo.flash.setSwf({flash6:swfloc6, flash8:swfloc8, visible:false});
}, isAvailable:function () {
	if (djConfig["disableFlashStorage"] == true) {
		this._available = false;
	} else {
		this._available = true;
	}
	return this._available;
}, put:function (key, value, resultsHandler) {
	if (this.isValidKey(key) == false) {
		dojo.raise("Invalid key given: " + key);
	}
	this._statusHandler = resultsHandler;
	if (dojo.lang.isString(value)) {
		value = "string:" + value;
	} else {
		value = dojo.json.serialize(value);
	}
	dojo.flash.comm.put(key, value, this.namespace);
}, get:function (key) {
	if (this.isValidKey(key) == false) {
		dojo.raise("Invalid key given: " + key);
	}
	var results = dojo.flash.comm.get(key, this.namespace);
	if (results == "") {
		return null;
	}
	if (!dojo.lang.isUndefined(results) && results != null && /^string:/.test(results)) {
		results = results.substring("string:".length);
	} else {
		results = dojo.json.evalJson(results);
	}
	return results;
}, getKeys:function () {
	var results = dojo.flash.comm.getKeys(this.namespace);
	if (results == "") {
		return [];
	}
	return results.split(",");
}, clear:function () {
	dojo.flash.comm.clear(this.namespace);
}, remove:function (key) {
	dojo.unimplemented("dojo.storage.browser.FlashStorageProvider.remove");
}, isPermanent:function () {
	return true;
}, getMaximumSize:function () {
	return dojo.storage.SIZE_NO_LIMIT;
}, hasSettingsUI:function () {
	return true;
}, showSettingsUI:function () {
	dojo.flash.comm.showSettings();
	dojo.flash.obj.setVisible(true);
	dojo.flash.obj.center();
}, hideSettingsUI:function () {
	dojo.flash.obj.setVisible(false);
	if (dojo.storage.onHideSettingsUI != null && !dojo.lang.isUndefined(dojo.storage.onHideSettingsUI)) {
		dojo.storage.onHideSettingsUI.call(null);
	}
}, getType:function () {
	return "dojo.storage.browser.FlashStorageProvider";
}, _flashLoaded:function () {
	this._initialized = true;
	dojo.storage.manager.loaded();
}, _onStatus:function (statusResult, key) {
	var ds = dojo.storage;
	var dfo = dojo.flash.obj;
	if (statusResult == ds.PENDING) {
		dfo.center();
		dfo.setVisible(true);
	} else {
		dfo.setVisible(false);
	}
	if ((!dj_undef("_statusHandler", ds)) && (ds._statusHandler != null)) {
		ds._statusHandler.call(null, statusResult, key);
	}
}});
dojo.storage.manager.register("dojo.storage.browser.FileStorageProvider", new dojo.storage.browser.FileStorageProvider());
dojo.storage.manager.register("dojo.storage.browser.WhatWGStorageProvider", new dojo.storage.browser.WhatWGStorageProvider());
dojo.storage.manager.register("dojo.storage.browser.FlashStorageProvider", new dojo.storage.browser.FlashStorageProvider());
dojo.storage.manager.initialize();

