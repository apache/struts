/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.profile");
dojo.profile = {_profiles:{}, _pns:[], start:function (name) {
	if (!this._profiles[name]) {
		this._profiles[name] = {iters:0, total:0};
		this._pns[this._pns.length] = name;
	} else {
		if (this._profiles[name]["start"]) {
			this.end(name);
		}
	}
	this._profiles[name].end = null;
	this._profiles[name].start = new Date();
}, end:function (name) {
	var ed = new Date();
	if ((this._profiles[name]) && (this._profiles[name]["start"])) {
		with (this._profiles[name]) {
			end = ed;
			total += (end - start);
			start = null;
			iters++;
		}
	} else {
		return true;
	}
}, dump:function (appendToDoc) {
	var tbl = document.createElement("table");
	with (tbl.style) {
		border = "1px solid black";
		borderCollapse = "collapse";
	}
	var hdr = tbl.createTHead();
	var hdrtr = hdr.insertRow(0);
	var cols = ["Identifier", "Calls", "Total", "Avg"];
	for (var x = 0; x < cols.length; x++) {
		var ntd = hdrtr.insertCell(x);
		with (ntd.style) {
			backgroundColor = "#225d94";
			color = "white";
			borderBottom = "1px solid black";
			borderRight = "1px solid black";
			fontFamily = "tahoma";
			fontWeight = "bolder";
			paddingLeft = paddingRight = "5px";
		}
		ntd.appendChild(document.createTextNode(cols[x]));
	}
	for (var x = 0; x < this._pns.length; x++) {
		var prf = this._profiles[this._pns[x]];
		this.end(this._pns[x]);
		if (prf.iters > 0) {
			var bdytr = tbl.insertRow(true);
			var vals = [this._pns[x], prf.iters, prf.total, parseInt(prf.total / prf.iters)];
			for (var y = 0; y < vals.length; y++) {
				var cc = bdytr.insertCell(y);
				cc.appendChild(document.createTextNode(vals[y]));
				with (cc.style) {
					borderBottom = "1px solid gray";
					paddingLeft = paddingRight = "5px";
					if (x % 2) {
						backgroundColor = "#e1f1ff";
					}
					if (y > 0) {
						textAlign = "right";
						borderRight = "1px solid gray";
					} else {
						borderRight = "1px solid black";
					}
				}
			}
		}
	}
	if (appendToDoc) {
		var ne = document.createElement("div");
		ne.id = "profileOutputTable";
		with (ne.style) {
			fontFamily = "Courier New, monospace";
			fontSize = "12px";
			lineHeight = "16px";
			borderTop = "1px solid black";
			padding = "10px";
		}
		if (document.getElementById("profileOutputTable")) {
			dojo.body().replaceChild(ne, document.getElementById("profileOutputTable"));
		} else {
			dojo.body().appendChild(ne);
		}
		ne.appendChild(tbl);
	}
	return tbl;
}};
dojo.profile.stop = dojo.profile.end;

