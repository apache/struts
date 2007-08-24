/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.data.core.RemoteStore");
dojo.require("dojo.data.core.Read");
dojo.require("dojo.data.core.Write");
dojo.require("dojo.data.core.Result");
dojo.require("dojo.experimental");
dojo.require("dojo.Deferred");
dojo.require("dojo.lang.declare");
dojo.require("dojo.json");
dojo.require("dojo.io.*");
dojo.experimental("dojo.data.core.RemoteStore");
dojo.lang.declare("dojo.data.core.RemoteStore", [dojo.data.core.Read, dojo.data.core.Write], {_datatypeMap:{}, _jsonRegistry:dojo.json.jsonRegistry, initializer:function (kwArgs) {
	if (!kwArgs) {
		kwArgs = {};
	}
	this._serverQueryUrl = kwArgs.queryUrl || "";
	this._serverSaveUrl = kwArgs.saveUrl || "";
	this._deleted = {};
	this._changed = {};
	this._added = {};
	this._results = {};
	this._data = {};
	this._numItems = 0;
}, _setupQueryRequest:function (result, requestKw) {
	result.query = result.query || "";
	requestKw.url = this._serverQueryUrl + encodeURIComponent(result.query);
	requestKw.method = "get";
	requestKw.mimetype = "text/json";
}, _resultToQueryMetadata:function (serverResponseData) {
	return serverResponseData;
}, _resultToQueryData:function (serverResponseData) {
	return serverResponseData.data;
}, _remoteToLocalValues:function (attributes) {
	for (var key in attributes) {
		var values = attributes[key];
		for (var i = 0; i < values.length; i++) {
			var value = values[i];
			var type = value.datatype || value.type;
			if (type) {
				var localValue = value.value;
				if (this._datatypeMap[type]) {
					localValue = this._datatypeMap[type](value);
				}
				values[i] = localValue;
			}
		}
	}
	return attributes;
}, _queryToQueryKey:function (query) {
	if (typeof query == "string") {
		return query;
	} else {
		return dojo.json.serialize(query);
	}
}, _assertIsItem:function (item) {
	if (!this.isItem(item)) {
		throw new Error("dojo.data.RemoteStore: a function was passed an item argument that was not an item");
	}
}, get:function (item, attribute, defaultValue) {
	var valueArray = this.getValues(item, attribute);
	if (valueArray.length == 0) {
		return defaultValue;
	}
	return valueArray[0];
}, getValues:function (item, attribute) {
	var itemIdentity = this.getIdentity(item);
	this._assertIsItem(itemIdentity);
	var changes = this._changed[itemIdentity];
	if (changes) {
		var newvalues = changes[attribute];
		if (newvalues !== undefined) {
			return newvalues;
		} else {
			return [];
		}
	}
	return this._data[itemIdentity][0][attribute];
}, getAttributes:function (item) {
	var itemIdentity = this.getIdentity(item);
	if (!itemIdentity) {
		return undefined;
	}
	var atts = [];
	var attrDict = this._data[itemIdentity][0];
	for (var att in attrDict) {
		atts.push(att);
	}
	return atts;
}, hasAttribute:function (item, attribute) {
	var valueArray = this.getValues(item, attribute);
	return valueArray.length ? true : false;
}, containsValue:function (item, attribute, value) {
	var valueArray = this.getValues(item, attribute);
	for (var i = 0; i < valueArray.length; i++) {
		if (valueArray[i] == value) {
			return true;
		}
	}
	return false;
}, isItem:function (something) {
	if (!something) {
		return false;
	}
	var itemIdentity = something;
	if (this._deleted[itemIdentity]) {
		return false;
	}
	if (this._data[itemIdentity]) {
		return true;
	}
	if (this._added[itemIdentity]) {
		return true;
	}
	return false;
}, find:function (keywordArgs) {
	var result = null;
	if (keywordArgs instanceof dojo.data.core.Result) {
		result = keywordArgs;
		result.store = this;
	} else {
		result = new dojo.data.core.Result(keywordArgs, this);
	}
	var query = result.query;
	var self = this;
	var bindfunc = function (type, data, evt) {
		var scope = result.scope || dj_global;
		if (type == "load") {
			result.resultMetadata = self._resultToQueryMetadata(data);
			var dataDict = self._resultToQueryData(data);
			if (result.onbegin) {
				result.onbegin.call(scope, result);
			}
			var count = 0;
			var resultData = [];
			var newItemCount = 0;
			for (var key in dataDict) {
				if (result._aborted) {
					break;
				}
				if (!self._deleted[key]) {
					var values = dataDict[key];
					var attributeDict = self._remoteToLocalValues(values);
					var existingValue = self._data[key];
					var refCount = 1;
					if (existingValue) {
						refCount = ++existingValue[1];
					} else {
						newItemCount++;
					}
					self._data[key] = [attributeDict, refCount];
					resultData.push(key);
					count++;
					if (result.onnext) {
						result.onnext.call(scope, key, result);
					}
				}
			}
			self._results[self._queryToQueryKey(query)] = resultData;
			self._numItems += newItemCount;
			result.length = count;
			if (result.saveResult) {
				result.items = resultData;
			}
			if (!result._aborted && result.oncompleted) {
				result.oncompleted.call(scope, result);
			}
		} else {
			if (type == "error" || type == "timeout") {
				dojo.debug("find error: " + dojo.json.serialize(data));
				if (result.onerror) {
					result.onerror.call(scope, data);
				}
			}
		}
	};
	var bindKw = keywordArgs.bindArgs || {};
	bindKw.sync = result.sync;
	bindKw.handle = bindfunc;
	this._setupQueryRequest(result, bindKw);
	var request = dojo.io.bind(bindKw);
	result._abortFunc = request.abort;
	return result;
}, getIdentity:function (item) {
	if (!this.isItem(item)) {
		return null;
	}
	return (item.id ? item.id : item);
}, newItem:function (attributes, keywordArgs) {
	var itemIdentity = keywordArgs["identity"];
	if (this._deleted[itemIdentity]) {
		delete this._deleted[itemIdentity];
	} else {
		this._added[itemIdentity] = 1;
	}
	if (attributes) {
		for (var attribute in attributes) {
			var valueOrArrayOfValues = attributes[attribute];
			if (dojo.lang.isArray(valueOrArrayOfValues)) {
				this.setValues(itemIdentity, attribute, valueOrArrayOfValues);
			} else {
				this.set(itemIdentity, attribute, valueOrArrayOfValues);
			}
		}
	}
	return {id:itemIdentity};
}, deleteItem:function (item) {
	var identity = this.getIdentity(item);
	if (!identity) {
		return false;
	}
	if (this._added[identity]) {
		delete this._added[identity];
	} else {
		this._deleted[identity] = 1;
	}
	if (this._changed[identity]) {
		delete this._changed[identity];
	}
	return true;
}, setValues:function (item, attribute, values) {
	var identity = this.getIdentity(item);
	if (!identity) {
		return undefined;
	}
	var changes = this._changed[identity];
	if (!changes) {
		changes = {};
		this._changed[identity] = changes;
	}
	changes[attribute] = values;
	return true;
}, set:function (item, attribute, value) {
	return this.setValues(item, attribute, [value]);
}, unsetAttribute:function (item, attribute) {
	return this.setValues(item, attribute, []);
}, _initChanges:function () {
	this._deleted = {};
	this._changed = {};
	this._added = {};
}, _setupSaveRequest:function (saveKeywordArgs, requestKw) {
	requestKw.url = this._serverSaveUrl;
	requestKw.method = "post";
	requestKw.mimetype = "text/plain";
	var deleted = [];
	for (var key in this._deleted) {
		deleted.push(key);
	}
	var saveStruct = {"changed":this._changed, "deleted":deleted};
	var oldRegistry = dojo.json.jsonRegistry;
	dojo.json.jsonRegistry = this._jsonRegistry;
	var jsonString = dojo.json.serialize(saveStruct);
	dojo.json.jsonRegistry = oldRegistry;
	requestKw.postContent = jsonString;
}, save:function (keywordArgs) {
	keywordArgs = keywordArgs || {};
	var result = new dojo.Deferred();
	var self = this;
	var bindfunc = function (type, data, evt) {
		if (type == "load") {
			if (result.fired == 1) {
				return;
			}
			var key = null;
			for (key in self._added) {
				if (!self._data[key]) {
					self._data[key] = [{}, 1];
				}
			}
			for (key in self._changed) {
				var existing = self._data[key];
				var changes = self._changed[key];
				if (existing) {
					existing[0] = changes;
				} else {
					self._data[key] = [changes, 1];
				}
			}
			for (key in self._deleted) {
				if (self._data[key]) {
					delete self._data[key];
				}
			}
			self._initChanges();
			result.callback(true);
		} else {
			if (type == "error" || type == "timeout") {
				result.errback(data);
			}
		}
	};
	var bindKw = {sync:keywordArgs["sync"], handle:bindfunc};
	this._setupSaveRequest(keywordArgs, bindKw);
	var request = dojo.io.bind(bindKw);
	result.canceller = function (deferred) {
		request.abort();
	};
	return result;
}, revert:function () {
	this._initChanges();
	return true;
}, isDirty:function (item) {
	if (item) {
		var identity = item.id || item;
		return this._deleted[identity] || this._changed[identity];
	} else {
		var key = null;
		for (key in this._changed) {
			return true;
		}
		for (key in this._deleted) {
			return true;
		}
		for (key in this._added) {
			return true;
		}
		return false;
	}
}, createReference:function (idstring) {
	return {id:idstring};
}, getSize:function () {
	return this._numItems;
}, forgetResults:function (query) {
	var queryKey = this._queryToQueryKey(query);
	var results = this._results[queryKey];
	if (!results) {
		return false;
	}
	var removed = 0;
	for (var i = 0; i < results.length; i++) {
		var key = results[i];
		var existingValue = this._data[key];
		if (existingValue[1] <= 1) {
			delete this._data[key];
			removed++;
		} else {
			existingValue[1] = --existingValue[1];
		}
	}
	delete this._results[queryKey];
	this._numItems -= removed;
	return true;
}});

