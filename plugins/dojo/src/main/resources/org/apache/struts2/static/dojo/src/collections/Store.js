/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.collections.Store");
dojo.require("dojo.lang.common");
dojo.collections.Store = function (jsonArray) {
	var data = [];
	var items = {};
	this.keyField = "Id";
	this.get = function () {
		return data;
	};
	this.getByKey = function (key) {
		return items[key];
	};
	this.getByIndex = function (idx) {
		return data[idx];
	};
	this.getIndexOf = function (key) {
		for (var i = 0; i < data.length; i++) {
			if (data[i].key == key) {
				return i;
			}
		}
		return -1;
	};
	this.getData = function () {
		var arr = [];
		for (var i = 0; i < data.length; i++) {
			arr.push(data[i].src);
		}
		return arr;
	};
	this.getDataByKey = function (key) {
		if (items[key] != null) {
			return items[key].src;
		}
		return null;
	};
	this.getIndexOfData = function (obj) {
		for (var i = 0; i < data.length; i++) {
			if (data[i].src == obj) {
				return i;
			}
		}
		return -1;
	};
	this.getDataByIndex = function (idx) {
		if (data[idx]) {
			return data[idx].src;
		}
		return null;
	};
	this.update = function (obj, fieldPath, val, bDontFire) {
		var parts = fieldPath.split("."), i = 0, o = obj, field;
		if (parts.length > 1) {
			field = parts.pop();
			do {
				if (parts[i].indexOf("()") > -1) {
					var temp = parts[i++].split("()")[0];
					if (!o[temp]) {
						dojo.raise("dojo.collections.Store.getField(obj, '" + field + "'): '" + temp + "' is not a property of the passed object.");
					} else {
						o = o[temp]();
					}
				} else {
					o = o[parts[i++]];
				}
			} while (i < parts.length && o != null);
		} else {
			field = parts[0];
		}
		obj[field] = val;
		if (!bDontFire) {
			this.onUpdateField(obj, fieldPath, val);
		}
	};
	this.forEach = function (fn) {
		if (Array.forEach) {
			Array.forEach(data, fn, this);
		} else {
			for (var i = 0; i < data.length; i++) {
				fn.call(this, data[i]);
			}
		}
	};
	this.forEachData = function (fn) {
		if (Array.forEach) {
			Array.forEach(this.getData(), fn, this);
		} else {
			var a = this.getData();
			for (var i = 0; i < a.length; i++) {
				fn.call(this, a[i]);
			}
		}
	};
	this.setData = function (arr, bDontFire) {
		data = [];
		for (var i = 0; i < arr.length; i++) {
			var o = {key:arr[i][this.keyField], src:arr[i]};
			data.push(o);
			items[o.key] = o;
		}
		if (!bDontFire) {
			this.onSetData();
		}
	};
	this.clearData = function (bDontFire) {
		data = [];
		items = {};
		if (!bDontFire) {
			this.onClearData();
		}
	};
	this.addData = function (obj, key, bDontFire) {
		var k = key || obj[this.keyField];
		if (items[k] != null) {
			var o = items[k];
			o.src = obj;
		} else {
			var o = {key:k, src:obj};
			data.push(o);
			items[o.key] = o;
		}
		if (!bDontFire) {
			this.onAddData(o);
		}
	};
	this.addDataRange = function (arr, bDontFire) {
		var objects = [];
		for (var i = 0; i < arr.length; i++) {
			var k = arr[i][this.keyField];
			if (items[k] != null) {
				var o = items[k];
				o.src = arr[i];
			} else {
				var o = {key:k, src:arr[i]};
				data.push(o);
				items[k] = o;
			}
			objects.push(o);
		}
		if (!bDontFire) {
			this.onAddDataRange(objects);
		}
	};
	this.addDataByIndex = function (obj, idx, key, bDontFire) {
		var k = key || obj[this.keyField];
		if (items[k] != null) {
			var i = this.getIndexOf(k);
			var o = data.splice(i, 1);
			o.src = obj;
		} else {
			var o = {key:k, src:obj};
			items[k] = o;
		}
		data.splice(idx, 0, o);
		if (!bDontFire) {
			this.onAddData(o);
		}
	};
	this.addDataRangeByIndex = function (arr, idx, bDontFire) {
		var objects = [];
		for (var i = 0; i < arr.length; i++) {
			var k = arr[i][this.keyField];
			if (items[k] != null) {
				var j = this.getIndexOf(k);
				var o = data.splice(j, 1);
				o.src = arr[i];
			} else {
				var o = {key:k, src:arr[i]};
				items[k] = o;
			}
			objects.push(o);
		}
		data.splice(idx, 0, objects);
		if (!bDontFire) {
			this.onAddDataRange(objects);
		}
	};
	this.removeData = function (obj, bDontFire) {
		var idx = -1;
		var o = null;
		for (var i = 0; i < data.length; i++) {
			if (data[i].src == obj) {
				idx = i;
				o = data[i];
				break;
			}
		}
		if (!bDontFire) {
			this.onRemoveData(o);
		}
		if (idx > -1) {
			data.splice(idx, 1);
			delete items[o.key];
		}
	};
	this.removeDataRange = function (idx, range, bDontFire) {
		var ret = data.splice(idx, range);
		for (var i = 0; i < ret.length; i++) {
			delete items[ret[i].key];
		}
		if (!bDontFire) {
			this.onRemoveDataRange(ret);
		}
		return ret;
	};
	this.removeDataByKey = function (key, bDontFire) {
		this.removeData(this.getDataByKey(key), bDontFire);
	};
	this.removeDataByIndex = function (idx, bDontFire) {
		this.removeData(this.getDataByIndex(idx), bDontFire);
	};
	if (jsonArray && jsonArray.length && jsonArray[0]) {
		this.setData(jsonArray, true);
	}
};
dojo.extend(dojo.collections.Store, {getField:function (obj, field) {
	var parts = field.split("."), i = 0, o = obj;
	do {
		if (parts[i].indexOf("()") > -1) {
			var temp = parts[i++].split("()")[0];
			if (!o[temp]) {
				dojo.raise("dojo.collections.Store.getField(obj, '" + field + "'): '" + temp + "' is not a property of the passed object.");
			} else {
				o = o[temp]();
			}
		} else {
			o = o[parts[i++]];
		}
	} while (i < parts.length && o != null);
	if (i < parts.length) {
		dojo.raise("dojo.collections.Store.getField(obj, '" + field + "'): '" + field + "' is not a property of the passed object.");
	}
	return o;
}, getFromHtml:function (meta, body, fnMod) {
	var rows = body.rows;
	var ctor = function (row) {
		var obj = {};
		for (var i = 0; i < meta.length; i++) {
			var o = obj;
			var data = row.cells[i].innerHTML;
			var p = meta[i].getField();
			if (p.indexOf(".") > -1) {
				p = p.split(".");
				while (p.length > 1) {
					var pr = p.shift();
					o[pr] = {};
					o = o[pr];
				}
				p = p[0];
			}
			var type = meta[i].getType();
			if (type == String) {
				o[p] = data;
			} else {
				if (data) {
					o[p] = new type(data);
				} else {
					o[p] = new type();
				}
			}
		}
		return obj;
	};
	var arr = [];
	for (var i = 0; i < rows.length; i++) {
		var o = ctor(rows[i]);
		if (fnMod) {
			fnMod(o, rows[i]);
		}
		arr.push(o);
	}
	return arr;
}, onSetData:function () {
}, onClearData:function () {
}, onAddData:function (obj) {
}, onAddDataRange:function (arr) {
}, onRemoveData:function (obj) {
}, onRemoveDataRange:function (arr) {
}, onUpdateField:function (obj, field, val) {
}});

