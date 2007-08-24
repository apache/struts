/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.selection.Selection");
dojo.require("dojo.lang.array");
dojo.require("dojo.lang.func");
dojo.require("dojo.lang.common");
dojo.require("dojo.math");
dojo.declare("dojo.selection.Selection", null, {initializer:function (items, isCollection) {
	this.items = [];
	this.selection = [];
	this._pivotItems = [];
	this.clearItems();
	if (items) {
		if (isCollection) {
			this.setItemsCollection(items);
		} else {
			this.setItems(items);
		}
	}
}, items:null, selection:null, lastSelected:null, allowImplicit:true, length:0, isGrowable:true, _pivotItems:null, _pivotItem:null, onSelect:function (item) {
}, onDeselect:function (item) {
}, onSelectChange:function (item, selected) {
}, _find:function (item, inSelection) {
	if (inSelection) {
		return dojo.lang.find(this.selection, item);
	} else {
		return dojo.lang.find(this.items, item);
	}
}, isSelectable:function (item) {
	return true;
}, setItems:function () {
	this.clearItems();
	this.addItems.call(this, arguments);
}, setItemsCollection:function (collection) {
	this.items = collection;
}, addItems:function () {
	var args = dojo.lang.unnest(arguments);
	for (var i = 0; i < args.length; i++) {
		this.items.push(args[i]);
	}
}, addItemsAt:function (item, before) {
	if (this.items.length == 0) {
		return this.addItems(dojo.lang.toArray(arguments, 2));
	}
	if (!this.isItem(item)) {
		item = this.items[item];
	}
	if (!item) {
		throw new Error("addItemsAt: item doesn't exist");
	}
	var idx = this._find(item);
	if (idx > 0 && before) {
		idx--;
	}
	for (var i = 2; i < arguments.length; i++) {
		if (!this.isItem(arguments[i])) {
			this.items.splice(idx++, 0, arguments[i]);
		}
	}
}, removeItem:function (item) {
	var idx = this._find(item);
	if (idx > -1) {
		this.items.splice(idx, 1);
	}
	idx = this._find(item, true);
	if (idx > -1) {
		this.selection.splice(idx, 1);
	}
}, clearItems:function () {
	this.items = [];
	this.deselectAll();
}, isItem:function (item) {
	return this._find(item) > -1;
}, isSelected:function (item) {
	return this._find(item, true) > -1;
}, selectFilter:function (item, selection, add, grow) {
	return true;
}, update:function (item, add, grow, noToggle) {
	if (!this.isItem(item)) {
		return false;
	}
	if (this.isGrowable && grow) {
		if ((!this.isSelected(item)) && this.selectFilter(item, this.selection, false, true)) {
			this.grow(item);
			this.lastSelected = item;
		}
	} else {
		if (add) {
			if (this.selectFilter(item, this.selection, true, false)) {
				if (noToggle) {
					if (this.select(item)) {
						this.lastSelected = item;
					}
				} else {
					if (this.toggleSelected(item)) {
						this.lastSelected = item;
					}
				}
			}
		} else {
			this.deselectAll();
			this.select(item);
		}
	}
	this.length = this.selection.length;
	return true;
}, grow:function (toItem, fromItem) {
	if (!this.isGrowable) {
		return;
	}
	if (arguments.length == 1) {
		fromItem = this._pivotItem;
		if (!fromItem && this.allowImplicit) {
			fromItem = this.items[0];
		}
	}
	if (!toItem || !fromItem) {
		return false;
	}
	var fromIdx = this._find(fromItem);
	var toDeselect = {};
	var lastIdx = -1;
	if (this.lastSelected) {
		lastIdx = this._find(this.lastSelected);
		var step = fromIdx < lastIdx ? -1 : 1;
		var range = dojo.math.range(lastIdx, fromIdx, step);
		for (var i = 0; i < range.length; i++) {
			toDeselect[range[i]] = true;
		}
	}
	var toIdx = this._find(toItem);
	var step = fromIdx < toIdx ? -1 : 1;
	var shrink = lastIdx >= 0 && step == 1 ? lastIdx < toIdx : lastIdx > toIdx;
	var range = dojo.math.range(toIdx, fromIdx, step);
	if (range.length) {
		for (var i = range.length - 1; i >= 0; i--) {
			var item = this.items[range[i]];
			if (this.selectFilter(item, this.selection, false, true)) {
				if (this.select(item, true) || shrink) {
					this.lastSelected = item;
				}
				if (range[i] in toDeselect) {
					delete toDeselect[range[i]];
				}
			}
		}
	} else {
		this.lastSelected = fromItem;
	}
	for (var i in toDeselect) {
		if (this.items[i] == this.lastSelected) {
		}
		this.deselect(this.items[i]);
	}
	this._updatePivot();
}, growUp:function () {
	if (!this.isGrowable) {
		return;
	}
	var idx = this._find(this.lastSelected) - 1;
	while (idx >= 0) {
		if (this.selectFilter(this.items[idx], this.selection, false, true)) {
			this.grow(this.items[idx]);
			break;
		}
		idx--;
	}
}, growDown:function () {
	if (!this.isGrowable) {
		return;
	}
	var idx = this._find(this.lastSelected);
	if (idx < 0 && this.allowImplicit) {
		this.select(this.items[0]);
		idx = 0;
	}
	idx++;
	while (idx > 0 && idx < this.items.length) {
		if (this.selectFilter(this.items[idx], this.selection, false, true)) {
			this.grow(this.items[idx]);
			break;
		}
		idx++;
	}
}, toggleSelected:function (item, noPivot) {
	if (this.isItem(item)) {
		if (this.select(item, noPivot)) {
			return 1;
		}
		if (this.deselect(item)) {
			return -1;
		}
	}
	return 0;
}, select:function (item, noPivot) {
	if (this.isItem(item) && !this.isSelected(item) && this.isSelectable(item)) {
		this.selection.push(item);
		this.lastSelected = item;
		this.onSelect(item);
		this.onSelectChange(item, true);
		if (!noPivot) {
			this._addPivot(item);
		}
		this.length = this.selection.length;
		return true;
	}
	return false;
}, deselect:function (item) {
	var idx = this._find(item, true);
	if (idx > -1) {
		this.selection.splice(idx, 1);
		this.onDeselect(item);
		this.onSelectChange(item, false);
		if (item == this.lastSelected) {
			this.lastSelected = null;
		}
		this._removePivot(item);
		this.length = this.selection.length;
		return true;
	}
	return false;
}, selectAll:function () {
	for (var i = 0; i < this.items.length; i++) {
		this.select(this.items[i]);
	}
}, deselectAll:function () {
	while (this.selection && this.selection.length) {
		this.deselect(this.selection[0]);
	}
}, selectNext:function () {
	var idx = this._find(this.lastSelected);
	while (idx > -1 && ++idx < this.items.length) {
		if (this.isSelectable(this.items[idx])) {
			this.deselectAll();
			this.select(this.items[idx]);
			return true;
		}
	}
	return false;
}, selectPrevious:function () {
	var idx = this._find(this.lastSelected);
	while (idx-- > 0) {
		if (this.isSelectable(this.items[idx])) {
			this.deselectAll();
			this.select(this.items[idx]);
			return true;
		}
	}
	return false;
}, selectFirst:function () {
	this.deselectAll();
	var idx = 0;
	while (this.items[idx] && !this.select(this.items[idx])) {
		idx++;
	}
	return this.items[idx] ? true : false;
}, selectLast:function () {
	this.deselectAll();
	var idx = this.items.length - 1;
	while (this.items[idx] && !this.select(this.items[idx])) {
		idx--;
	}
	return this.items[idx] ? true : false;
}, _addPivot:function (item, andClear) {
	this._pivotItem = item;
	if (andClear) {
		this._pivotItems = [item];
	} else {
		this._pivotItems.push(item);
	}
}, _removePivot:function (item) {
	var i = dojo.lang.find(this._pivotItems, item);
	if (i > -1) {
		this._pivotItems.splice(i, 1);
		this._pivotItem = this._pivotItems[this._pivotItems.length - 1];
	}
	this._updatePivot();
}, _updatePivot:function () {
	if (this._pivotItems.length == 0) {
		if (this.lastSelected) {
			this._addPivot(this.lastSelected);
		}
	}
}, sorted:function () {
	return dojo.lang.toArray(this.selection).sort(dojo.lang.hitch(this, function (a, b) {
		var A = this._find(a), B = this._find(b);
		if (A > B) {
			return 1;
		} else {
			if (A < B) {
				return -1;
			} else {
				return 0;
			}
		}
	}));
}, updateSelected:function () {
	for (var i = 0; i < this.selection.length; i++) {
		if (this._find(this.selection[i]) < 0) {
			var removed = this.selection.splice(i, 1);
			this._removePivot(removed[0]);
		}
	}
	this.length = this.selection.length;
}});

