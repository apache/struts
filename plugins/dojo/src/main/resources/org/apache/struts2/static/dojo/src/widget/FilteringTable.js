/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.widget.FilteringTable");
dojo.require("dojo.date.format");
dojo.require("dojo.math");
dojo.require("dojo.collections.Store");
dojo.require("dojo.html.*");
dojo.require("dojo.html.util");
dojo.require("dojo.html.style");
dojo.require("dojo.html.selection");
dojo.require("dojo.event.*");
dojo.require("dojo.widget.*");
dojo.require("dojo.widget.HtmlWidget");
dojo.widget.defineWidget("dojo.widget.FilteringTable", dojo.widget.HtmlWidget, function () {
	this.store = new dojo.collections.Store();
	this.valueField = "Id";
	this.multiple = false;
	this.maxSelect = 0;
	this.maxSortable = 1;
	this.minRows = 0;
	this.defaultDateFormat = "%D";
	this.isInitialized = false;
	this.alternateRows = false;
	this.columns = [];
	this.sortInformation = [{index:0, direction:0}];
	this.headClass = "";
	this.tbodyClass = "";
	this.headerClass = "";
	this.headerUpClass = "selectedUp";
	this.headerDownClass = "selectedDown";
	this.rowClass = "";
	this.rowAlternateClass = "alt";
	this.rowSelectedClass = "selected";
	this.columnSelected = "sorted-column";
}, {isContainer:false, templatePath:null, templateCssPath:null, getTypeFromString:function (s) {
	var parts = s.split("."), i = 0, obj = dj_global;
	do {
		obj = obj[parts[i++]];
	} while (i < parts.length && obj);
	return (obj != dj_global) ? obj : null;
}, getByRow:function (row) {
	return this.store.getByKey(dojo.html.getAttribute(row, "value"));
}, getDataByRow:function (row) {
	return this.store.getDataByKey(dojo.html.getAttribute(row, "value"));
}, getRow:function (obj) {
	var rows = this.domNode.tBodies[0].rows;
	for (var i = 0; i < rows.length; i++) {
		if (this.store.getDataByKey(dojo.html.getAttribute(rows[i], "value")) == obj) {
			return rows[i];
		}
	}
	return null;
}, getColumnIndex:function (fieldPath) {
	for (var i = 0; i < this.columns.length; i++) {
		if (this.columns[i].getField() == fieldPath) {
			return i;
		}
	}
	return -1;
}, getSelectedData:function () {
	var data = this.store.get();
	var a = [];
	for (var i = 0; i < data.length; i++) {
		if (data[i].isSelected) {
			a.push(data[i].src);
		}
	}
	if (this.multiple) {
		return a;
	} else {
		return a[0];
	}
}, isSelected:function (obj) {
	var data = this.store.get();
	for (var i = 0; i < data.length; i++) {
		if (data[i].src == obj) {
			return true;
		}
	}
	return false;
}, isValueSelected:function (val) {
	var v = this.store.getByKey(val);
	if (v) {
		return v.isSelected;
	}
	return false;
}, isIndexSelected:function (idx) {
	var v = this.store.getByIndex(idx);
	if (v) {
		return v.isSelected;
	}
	return false;
}, isRowSelected:function (row) {
	var v = this.getByRow(row);
	if (v) {
		return v.isSelected;
	}
	return false;
}, reset:function () {
	this.store.clearData();
	this.columns = [];
	this.sortInformation = [{index:0, direction:0}];
	this.resetSelections();
	this.isInitialized = false;
	this.onReset();
}, resetSelections:function () {
	this.store.forEach(function (element) {
		element.isSelected = false;
	});
}, onReset:function () {
}, select:function (obj) {
	var data = this.store.get();
	for (var i = 0; i < data.length; i++) {
		if (data[i].src == obj) {
			data[i].isSelected = true;
			break;
		}
	}
	this.onDataSelect(obj);
}, selectByValue:function (val) {
	this.select(this.store.getDataByKey(val));
}, selectByIndex:function (idx) {
	this.select(this.store.getDataByIndex(idx));
}, selectByRow:function (row) {
	this.select(this.getDataByRow(row));
}, selectAll:function () {
	this.store.forEach(function (element) {
		element.isSelected = true;
	});
}, onDataSelect:function (obj) {
}, toggleSelection:function (obj) {
	var data = this.store.get();
	for (var i = 0; i < data.length; i++) {
		if (data[i].src == obj) {
			data[i].isSelected = !data[i].isSelected;
			break;
		}
	}
	this.onDataToggle(obj);
}, toggleSelectionByValue:function (val) {
	this.toggleSelection(this.store.getDataByKey(val));
}, toggleSelectionByIndex:function (idx) {
	this.toggleSelection(this.store.getDataByIndex(idx));
}, toggleSelectionByRow:function (row) {
	this.toggleSelection(this.getDataByRow(row));
}, toggleAll:function () {
	this.store.forEach(function (element) {
		element.isSelected = !element.isSelected;
	});
}, onDataToggle:function (obj) {
}, _meta:{field:null, format:null, filterer:null, noSort:false, sortType:"String", dataType:String, sortFunction:null, filterFunction:null, label:null, align:"left", valign:"middle", getField:function () {
	return this.field || this.label;
}, getType:function () {
	return this.dataType;
}}, createMetaData:function (obj) {
	for (var p in this._meta) {
		if (!obj[p]) {
			obj[p] = this._meta[p];
		}
	}
	if (!obj.label) {
		obj.label = obj.field;
	}
	if (!obj.filterFunction) {
		obj.filterFunction = this._defaultFilter;
	}
	return obj;
}, parseMetadata:function (head) {
	this.columns = [];
	this.sortInformation = [];
	var row = head.getElementsByTagName("tr")[0];
	var cells = row.getElementsByTagName("td");
	if (cells.length == 0) {
		cells = row.getElementsByTagName("th");
	}
	for (var i = 0; i < cells.length; i++) {
		var o = this.createMetaData({});
		if (dojo.html.hasAttribute(cells[i], "align")) {
			o.align = dojo.html.getAttribute(cells[i], "align");
		}
		if (dojo.html.hasAttribute(cells[i], "valign")) {
			o.valign = dojo.html.getAttribute(cells[i], "valign");
		}
		if (dojo.html.hasAttribute(cells[i], "nosort")) {
			o.noSort = (dojo.html.getAttribute(cells[i], "nosort") == "true");
		}
		if (dojo.html.hasAttribute(cells[i], "sortusing")) {
			var trans = dojo.html.getAttribute(cells[i], "sortusing");
			var f = this.getTypeFromString(trans);
			if (f != null && f != window && typeof (f) == "function") {
				o.sortFunction = f;
			}
		}
		o.label = dojo.html.renderedTextContent(cells[i]);
		if (dojo.html.hasAttribute(cells[i], "field")) {
			o.field = dojo.html.getAttribute(cells[i], "field");
		} else {
			if (o.label.length > 0) {
				o.field = o.label;
			} else {
				o.field = "field" + i;
			}
		}
		if (dojo.html.hasAttribute(cells[i], "format")) {
			o.format = dojo.html.getAttribute(cells[i], "format");
		}
		if (dojo.html.hasAttribute(cells[i], "dataType")) {
			var sortType = dojo.html.getAttribute(cells[i], "dataType");
			if (sortType.toLowerCase() == "html" || sortType.toLowerCase() == "markup") {
				o.sortType = "__markup__";
			} else {
				var type = this.getTypeFromString(sortType);
				if (type) {
					o.sortType = sortType;
					o.dataType = type;
				}
			}
		}
		if (dojo.html.hasAttribute(cells[i], "filterusing")) {
			var trans = dojo.html.getAttribute(cells[i], "filterusing");
			var f = this.getTypeFromString(trans);
			if (f != null && f != window && typeof (f) == "function") {
				o.filterFunction = f;
			}
		}
		this.columns.push(o);
		if (dojo.html.hasAttribute(cells[i], "sort")) {
			var info = {index:i, direction:0};
			var dir = dojo.html.getAttribute(cells[i], "sort");
			if (!isNaN(parseInt(dir))) {
				dir = parseInt(dir);
				info.direction = (dir != 0) ? 1 : 0;
			} else {
				info.direction = (dir.toLowerCase() == "desc") ? 1 : 0;
			}
			this.sortInformation.push(info);
		}
	}
	if (this.sortInformation.length == 0) {
		this.sortInformation.push({index:0, direction:0});
	} else {
		if (this.sortInformation.length > this.maxSortable) {
			this.sortInformation.length = this.maxSortable;
		}
	}
}, parseData:function (body) {
	if (body.rows.length == 0 && this.columns.length == 0) {
		return;
	}
	var self = this;
	this["__selected__"] = [];
	var arr = this.store.getFromHtml(this.columns, body, function (obj, row) {
		if (typeof (obj[self.valueField]) == "undefined" || obj[self.valueField] == null) {
			obj[self.valueField] = dojo.html.getAttribute(row, "value");
		}
		if (dojo.html.getAttribute(row, "selected") == "true") {
			self["__selected__"].push(obj);
		}
	});
	this.store.setData(arr, true);
	this.render();
	for (var i = 0; i < this["__selected__"].length; i++) {
		this.select(this["__selected__"][i]);
	}
	this.renderSelections();
	delete this["__selected__"];
	this.isInitialized = true;
}, onSelect:function (e) {
	var row = dojo.html.getParentByType(e.target, "tr");
	if (dojo.html.hasAttribute(row, "emptyRow")) {
		return;
	}
	var body = dojo.html.getParentByType(row, "tbody");
	if (this.multiple) {
		if (e.shiftKey) {
			var startRow;
			var rows = body.rows;
			for (var i = 0; i < rows.length; i++) {
				if (rows[i] == row) {
					break;
				}
				if (this.isRowSelected(rows[i])) {
					startRow = rows[i];
				}
			}
			if (!startRow) {
				startRow = row;
				for (; i < rows.length; i++) {
					if (this.isRowSelected(rows[i])) {
						row = rows[i];
						break;
					}
				}
			}
			this.resetSelections();
			if (startRow == row) {
				this.toggleSelectionByRow(row);
			} else {
				var doSelect = false;
				for (var i = 0; i < rows.length; i++) {
					if (rows[i] == startRow) {
						doSelect = true;
					}
					if (doSelect) {
						this.selectByRow(rows[i]);
					}
					if (rows[i] == row) {
						doSelect = false;
					}
				}
			}
		} else {
			this.toggleSelectionByRow(row);
		}
	} else {
		this.resetSelections();
		this.toggleSelectionByRow(row);
	}
	this.renderSelections();
}, onSort:function (e) {
	var oldIndex = this.sortIndex;
	var oldDirection = this.sortDirection;
	var source = e.target;
	var row = dojo.html.getParentByType(source, "tr");
	var cellTag = "td";
	if (row.getElementsByTagName(cellTag).length == 0) {
		cellTag = "th";
	}
	var headers = row.getElementsByTagName(cellTag);
	var header = dojo.html.getParentByType(source, cellTag);
	for (var i = 0; i < headers.length; i++) {
		dojo.html.setClass(headers[i], this.headerClass);
		if (headers[i] == header) {
			if (this.sortInformation[0].index != i) {
				this.sortInformation.unshift({index:i, direction:0});
			} else {
				this.sortInformation[0] = {index:i, direction:(~this.sortInformation[0].direction) & 1};
			}
		}
	}
	this.sortInformation.length = Math.min(this.sortInformation.length, this.maxSortable);
	for (var i = 0; i < this.sortInformation.length; i++) {
		var idx = this.sortInformation[i].index;
		var dir = (~this.sortInformation[i].direction) & 1;
		dojo.html.setClass(headers[idx], dir == 0 ? this.headerDownClass : this.headerUpClass);
	}
	this.render();
}, onFilter:function () {
}, _defaultFilter:function (obj) {
	return true;
}, setFilter:function (field, fn) {
	for (var i = 0; i < this.columns.length; i++) {
		if (this.columns[i].getField() == field) {
			this.columns[i].filterFunction = fn;
			break;
		}
	}
	this.applyFilters();
}, setFilterByIndex:function (idx, fn) {
	this.columns[idx].filterFunction = fn;
	this.applyFilters();
}, clearFilter:function (field) {
	for (var i = 0; i < this.columns.length; i++) {
		if (this.columns[i].getField() == field) {
			this.columns[i].filterFunction = this._defaultFilter;
			break;
		}
	}
	this.applyFilters();
}, clearFilterByIndex:function (idx) {
	this.columns[idx].filterFunction = this._defaultFilter;
	this.applyFilters();
}, clearFilters:function () {
	for (var i = 0; i < this.columns.length; i++) {
		this.columns[i].filterFunction = this._defaultFilter;
	}
	var rows = this.domNode.tBodies[0].rows;
	for (var i = 0; i < rows.length; i++) {
		rows[i].style.display = "";
		if (this.alternateRows) {
			dojo.html[((i % 2 == 1) ? "addClass" : "removeClass")](rows[i], this.rowAlternateClass);
		}
	}
	this.onFilter();
}, applyFilters:function () {
	var alt = 0;
	var rows = this.domNode.tBodies[0].rows;
	for (var i = 0; i < rows.length; i++) {
		var b = true;
		var row = rows[i];
		for (var j = 0; j < this.columns.length; j++) {
			var value = this.store.getField(this.getDataByRow(row), this.columns[j].getField());
			if (this.columns[j].getType() == Date && value != null && !value.getYear) {
				value = new Date(value);
			}
			if (!this.columns[j].filterFunction(value)) {
				b = false;
				break;
			}
		}
		row.style.display = (b ? "" : "none");
		if (b && this.alternateRows) {
			dojo.html[((alt++ % 2 == 1) ? "addClass" : "removeClass")](row, this.rowAlternateClass);
		}
	}
	this.onFilter();
}, createSorter:function (info) {
	var self = this;
	var sortFunctions = [];
	function createSortFunction(fieldIndex, dir) {
		var meta = self.columns[fieldIndex];
		var field = meta.getField();
		return function (rowA, rowB) {
			if (dojo.html.hasAttribute(rowA, "emptyRow")) {
				return 1;
			}
			if (dojo.html.hasAttribute(rowB, "emptyRow")) {
				return -1;
			}
			var a = self.store.getField(self.getDataByRow(rowA), field);
			var b = self.store.getField(self.getDataByRow(rowB), field);
			var ret = 0;
			if (a > b) {
				ret = 1;
			}
			if (a < b) {
				ret = -1;
			}
			return dir * ret;
		};
	}
	var current = 0;
	var max = Math.min(info.length, this.maxSortable, this.columns.length);
	while (current < max) {
		var direction = (info[current].direction == 0) ? 1 : -1;
		sortFunctions.push(createSortFunction(info[current].index, direction));
		current++;
	}
	return function (rowA, rowB) {
		var idx = 0;
		while (idx < sortFunctions.length) {
			var ret = sortFunctions[idx++](rowA, rowB);
			if (ret != 0) {
				return ret;
			}
		}
		return 0;
	};
}, createRow:function (obj) {
	var row = document.createElement("tr");
	dojo.html.disableSelection(row);
	if (obj.key != null) {
		row.setAttribute("value", obj.key);
	}
	for (var j = 0; j < this.columns.length; j++) {
		var cell = document.createElement("td");
		cell.setAttribute("align", this.columns[j].align);
		cell.setAttribute("valign", this.columns[j].valign);
		dojo.html.disableSelection(cell);
		var val = this.store.getField(obj.src, this.columns[j].getField());
		if (typeof (val) == "undefined") {
			val = "";
		}
		this.fillCell(cell, this.columns[j], val);
		row.appendChild(cell);
	}
	return row;
}, fillCell:function (cell, meta, val) {
	if (meta.sortType == "__markup__") {
		cell.innerHTML = val;
	} else {
		if (meta.getType() == Date) {
			val = new Date(val);
			if (!isNaN(val)) {
				var format = this.defaultDateFormat;
				if (meta.format) {
					format = meta.format;
				}
				cell.innerHTML = dojo.date.strftime(val, format);
			} else {
				cell.innerHTML = val;
			}
		} else {
			if ("Number number int Integer float Float".indexOf(meta.getType()) > -1) {
				if (val.length == 0) {
					val = "0";
				}
				var n = parseFloat(val, 10) + "";
				if (n.indexOf(".") > -1) {
					n = dojo.math.round(parseFloat(val, 10), 2);
				}
				cell.innerHTML = n;
			} else {
				cell.innerHTML = val;
			}
		}
	}
}, prefill:function () {
	this.isInitialized = false;
	var body = this.domNode.tBodies[0];
	while (body.childNodes.length > 0) {
		body.removeChild(body.childNodes[0]);
	}
	if (this.minRows > 0) {
		for (var i = 0; i < this.minRows; i++) {
			var row = document.createElement("tr");
			if (this.alternateRows) {
				dojo.html[((i % 2 == 1) ? "addClass" : "removeClass")](row, this.rowAlternateClass);
			}
			row.setAttribute("emptyRow", "true");
			for (var j = 0; j < this.columns.length; j++) {
				var cell = document.createElement("td");
				cell.innerHTML = "&nbsp;";
				row.appendChild(cell);
			}
			body.appendChild(row);
		}
	}
}, init:function () {
	this.isInitialized = false;
	var head = this.domNode.getElementsByTagName("thead")[0];
	if (head.getElementsByTagName("tr").length == 0) {
		var row = document.createElement("tr");
		for (var i = 0; i < this.columns.length; i++) {
			var cell = document.createElement("td");
			cell.setAttribute("align", this.columns[i].align);
			cell.setAttribute("valign", this.columns[i].valign);
			dojo.html.disableSelection(cell);
			cell.innerHTML = this.columns[i].label;
			row.appendChild(cell);
			if (!this.columns[i].noSort) {
				dojo.event.connect(cell, "onclick", this, "onSort");
			}
		}
		dojo.html.prependChild(row, head);
	}
	if (this.store.get().length == 0) {
		return false;
	}
	var idx = this.domNode.tBodies[0].rows.length;
	if (!idx || idx == 0 || this.domNode.tBodies[0].rows[0].getAttribute("emptyRow") == "true") {
		idx = 0;
		var body = this.domNode.tBodies[0];
		while (body.childNodes.length > 0) {
			body.removeChild(body.childNodes[0]);
		}
		var data = this.store.get();
		for (var i = 0; i < data.length; i++) {
			var row = this.createRow(data[i]);
			body.appendChild(row);
			idx++;
		}
	}
	if (this.minRows > 0 && idx < this.minRows) {
		idx = this.minRows - idx;
		for (var i = 0; i < idx; i++) {
			row = document.createElement("tr");
			row.setAttribute("emptyRow", "true");
			for (var j = 0; j < this.columns.length; j++) {
				cell = document.createElement("td");
				cell.innerHTML = "&nbsp;";
				row.appendChild(cell);
			}
			body.appendChild(row);
		}
	}
	var row = this.domNode.getElementsByTagName("thead")[0].rows[0];
	var cellTag = "td";
	if (row.getElementsByTagName(cellTag).length == 0) {
		cellTag = "th";
	}
	var headers = row.getElementsByTagName(cellTag);
	for (var i = 0; i < headers.length; i++) {
		dojo.html.setClass(headers[i], this.headerClass);
	}
	for (var i = 0; i < this.sortInformation.length; i++) {
		var idx = this.sortInformation[i].index;
		var dir = (~this.sortInformation[i].direction) & 1;
		dojo.html.setClass(headers[idx], dir == 0 ? this.headerDownClass : this.headerUpClass);
	}
	this.isInitialized = true;
	return this.isInitialized;
}, render:function () {
	if (!this.isInitialized) {
		var b = this.init();
		if (!b) {
			this.prefill();
			return;
		}
	}
	var rows = [];
	var body = this.domNode.tBodies[0];
	var emptyRowIdx = -1;
	for (var i = 0; i < body.rows.length; i++) {
		rows.push(body.rows[i]);
	}
	var sortFunction = this.createSorter(this.sortInformation);
	if (sortFunction) {
		rows.sort(sortFunction);
	}
	for (var i = 0; i < rows.length; i++) {
		if (this.alternateRows) {
			dojo.html[((i % 2 == 1) ? "addClass" : "removeClass")](rows[i], this.rowAlternateClass);
		}
		dojo.html[(this.isRowSelected(body.rows[i]) ? "addClass" : "removeClass")](body.rows[i], this.rowSelectedClass);
		body.appendChild(rows[i]);
	}
}, renderSelections:function () {
	var body = this.domNode.tBodies[0];
	for (var i = 0; i < body.rows.length; i++) {
		dojo.html[(this.isRowSelected(body.rows[i]) ? "addClass" : "removeClass")](body.rows[i], this.rowSelectedClass);
	}
}, initialize:function () {
	var self = this;
	dojo.event.connect(this.store, "onSetData", function () {
		self.store.forEach(function (element) {
			element.isSelected = false;
		});
		self.isInitialized = false;
		var body = self.domNode.tBodies[0];
		if (body) {
			while (body.childNodes.length > 0) {
				body.removeChild(body.childNodes[0]);
			}
		}
		self.render();
	});
	dojo.event.connect(this.store, "onClearData", function () {
		self.isInitialized = false;
		self.render();
	});
	dojo.event.connect(this.store, "onAddData", function (addedObject) {
		var row = self.createRow(addedObject);
		self.domNode.tBodies[0].appendChild(row);
		self.render();
	});
	dojo.event.connect(this.store, "onAddDataRange", function (arr) {
		for (var i = 0; i < arr.length; i++) {
			arr[i].isSelected = false;
			var row = self.createRow(arr[i]);
			self.domNode.tBodies[0].appendChild(row);
		}
		self.render();
	});
	dojo.event.connect(this.store, "onRemoveData", function (removedObject) {
		var rows = self.domNode.tBodies[0].rows;
		for (var i = 0; i < rows.length; i++) {
			if (self.getDataByRow(rows[i]) == removedObject.src) {
				rows[i].parentNode.removeChild(rows[i]);
				break;
			}
		}
		self.render();
	});
	dojo.event.connect(this.store, "onUpdateField", function (obj, fieldPath, val) {
		var row = self.getRow(obj);
		var idx = self.getColumnIndex(fieldPath);
		if (row && row.cells[idx] && self.columns[idx]) {
			self.fillCell(row.cells[idx], self.columns[idx], val);
		}
	});
}, postCreate:function () {
	this.store.keyField = this.valueField;
	if (this.domNode) {
		if (this.domNode.nodeName.toLowerCase() != "table") {
		}
		if (this.domNode.getElementsByTagName("thead")[0]) {
			var head = this.domNode.getElementsByTagName("thead")[0];
			if (this.headClass.length > 0) {
				head.className = this.headClass;
			}
			dojo.html.disableSelection(this.domNode);
			this.parseMetadata(head);
			var header = "td";
			if (head.getElementsByTagName(header).length == 0) {
				header = "th";
			}
			var headers = head.getElementsByTagName(header);
			for (var i = 0; i < headers.length; i++) {
				if (!this.columns[i].noSort) {
					dojo.event.connect(headers[i], "onclick", this, "onSort");
				}
			}
		} else {
			this.domNode.appendChild(document.createElement("thead"));
		}
		if (this.domNode.tBodies.length < 1) {
			var body = document.createElement("tbody");
			this.domNode.appendChild(body);
		} else {
			var body = this.domNode.tBodies[0];
		}
		if (this.tbodyClass.length > 0) {
			body.className = this.tbodyClass;
		}
		dojo.event.connect(body, "onclick", this, "onSelect");
		this.parseData(body);
	}
}});

