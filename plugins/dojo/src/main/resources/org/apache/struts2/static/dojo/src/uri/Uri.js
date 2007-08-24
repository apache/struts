/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.uri.Uri");
dojo.uri = new function () {
	this.dojoUri = function (uri) {
		return new dojo.uri.Uri(dojo.hostenv.getBaseScriptUri(), uri);
	};
	this.moduleUri = function (module, uri) {
		var loc = dojo.hostenv.getModuleSymbols(module).join("/");
		if (!loc) {
			return null;
		}
		if (loc.lastIndexOf("/") != loc.length - 1) {
			loc += "/";
		}
		var colonIndex = loc.indexOf(":");
		var slashIndex = loc.indexOf("/");
		if (loc.charAt(0) != "/" && (colonIndex == -1 || colonIndex > slashIndex)) {
			loc = dojo.hostenv.getBaseScriptUri() + loc;
		}
		return new dojo.uri.Uri(loc, uri);
	};
	this.Uri = function () {
		var uri = arguments[0];
		for (var i = 1; i < arguments.length; i++) {
			if (!arguments[i]) {
				continue;
			}
			var relobj = new dojo.uri.Uri(arguments[i].toString());
			var uriobj = new dojo.uri.Uri(uri.toString());
			if ((relobj.path == "") && (relobj.scheme == null) && (relobj.authority == null) && (relobj.query == null)) {
				if (relobj.fragment != null) {
					uriobj.fragment = relobj.fragment;
				}
				relobj = uriobj;
			} else {
				if (relobj.scheme == null) {
					relobj.scheme = uriobj.scheme;
					if (relobj.authority == null) {
						relobj.authority = uriobj.authority;
						if (relobj.path.charAt(0) != "/") {
							var path = uriobj.path.substring(0, uriobj.path.lastIndexOf("/") + 1) + relobj.path;
							var segs = path.split("/");
							for (var j = 0; j < segs.length; j++) {
								if (segs[j] == ".") {
									if (j == segs.length - 1) {
										segs[j] = "";
									} else {
										segs.splice(j, 1);
										j--;
									}
								} else {
									if (j > 0 && !(j == 1 && segs[0] == "") && segs[j] == ".." && segs[j - 1] != "..") {
										if (j == segs.length - 1) {
											segs.splice(j, 1);
											segs[j - 1] = "";
										} else {
											segs.splice(j - 1, 2);
											j -= 2;
										}
									}
								}
							}
							relobj.path = segs.join("/");
						}
					}
				}
			}
			uri = "";
			if (relobj.scheme != null) {
				uri += relobj.scheme + ":";
			}
			if (relobj.authority != null) {
				uri += "//" + relobj.authority;
			}
			uri += relobj.path;
			if (relobj.query != null) {
				uri += "?" + relobj.query;
			}
			if (relobj.fragment != null) {
				uri += "#" + relobj.fragment;
			}
		}
		this.uri = uri.toString();
		var regexp = "^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\\?([^#]*))?(#(.*))?$";
		var r = this.uri.match(new RegExp(regexp));
		this.scheme = r[2] || (r[1] ? "" : null);
		this.authority = r[4] || (r[3] ? "" : null);
		this.path = r[5];
		this.query = r[7] || (r[6] ? "" : null);
		this.fragment = r[9] || (r[8] ? "" : null);
		if (this.authority != null) {
			regexp = "^((([^:]+:)?([^@]+))@)?([^:]*)(:([0-9]+))?$";
			r = this.authority.match(new RegExp(regexp));
			this.user = r[3] || null;
			this.password = r[4] || null;
			this.host = r[5];
			this.port = r[7] || null;
		}
		this.toString = function () {
			return this.uri;
		};
	};
};

