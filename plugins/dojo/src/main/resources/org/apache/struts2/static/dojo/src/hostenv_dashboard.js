/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.render.name = dojo.hostenv.name_ = "dashboard";
dojo.hostenv.println = function (message) {
	return alert(message);
};
dojo.hostenv.getXmlhttpObject = function (kwArgs) {
	if (widget.system && kwArgs) {
		if ((kwArgs.contentType && kwArgs.contentType.indexOf("text/") != 0) || (kwArgs.headers && kwArgs.headers["content-type"] && kwArgs.headers["content-type"].indexOf("text/") != 0)) {
			var curl = new dojo.hostenv.CurlRequest;
			curl._save = true;
			return curl;
		} else {
			if (kwArgs.method && kwArgs.method.toUpperCase() == "HEAD") {
				return new dojo.hostenv.CurlRequest;
			} else {
				if (kwArgs.headers && kwArgs.header.referer) {
					return new dojo.hostenv.CurlRequest;
				}
			}
		}
	}
	return new XMLHttpRequest;
};
dojo.hostenv.CurlRequest = function () {
	this.onreadystatechange = null;
	this.readyState = 0;
	this.responseText = "";
	this.responseXML = null;
	this.status = 0;
	this.statusText = "";
	this._method = "";
	this._url = "";
	this._async = true;
	this._referrer = "";
	this._headers = [];
	this._save = false;
	this._responseHeader = "";
	this._responseHeaders = {};
	this._fileName = "";
	this._username = "";
	this._password = "";
};
dojo.hostenv.CurlRequest.prototype.open = function (method, url, async, username, password) {
	this._method = method;
	this._url = url;
	if (async) {
		this._async = async;
	}
	if (username) {
		this._username = username;
	}
	if (password) {
		this._password = password;
	}
};
dojo.hostenv.CurlRequest.prototype.setRequestHeader = function (label, value) {
	switch (label) {
	  case "Referer":
		this._referrer = value;
		break;
	  case "content-type":
		break;
	  default:
		this._headers.push(label + "=" + value);
		break;
	}
};
dojo.hostenv.CurlRequest.prototype.getAllResponseHeaders = function () {
	return this._responseHeader;
};
dojo.hostenv.CurlRequest.prototype.getResponseHeader = function (headerLabel) {
	return this._responseHeaders[headerLabel];
};
dojo.hostenv.CurlRequest.prototype.send = function (content) {
	this.readyState = 1;
	if (this.onreadystatechange) {
		this.onreadystatechange.call(this);
	}
	var query = {sS:""};
	if (this._referrer) {
		query.e = this._referrer;
	}
	if (this._headers.length) {
		query.H = this._headers.join("&");
	}
	if (this._username) {
		if (this._password) {
			query.u = this._username + ":" + this._password;
		} else {
			query.u = this._username;
		}
	}
	if (content) {
		query.d = this.content;
		if (this._method != "POST") {
			query.G = "";
		}
	}
	if (this._method == "HEAD") {
		query.I = "";
	} else {
		if (this._save) {
			query.I = "";
		} else {
			query.i = "";
		}
	}
	var system = widget.system(dojo.hostenv.CurlRequest._formatCall(query, this._url), null);
	this.readyState = 2;
	if (this.onreadystatechange) {
		this.onreadystatechange.call(this);
	}
	if (system.errorString) {
		this.responseText = system.errorString;
		this.status = 0;
	} else {
		if (this._save) {
			this._responseHeader = system.outputString;
		} else {
			var split = system.outputString.replace(/\r/g, "").split("\n\n", 2);
			this._responseHeader = split[0];
			this.responseText = split[1];
		}
		split = this._responseHeader.split("\n");
		this.statusText = split.shift();
		this.status = this.statusText.split(" ")[1];
		for (var i = 0, header; header = split[i]; i++) {
			var header_split = header.split(": ", 2);
			this._responseHeaders[header_split[0]] = header_split[1];
		}
		if (this._save) {
			widget.system("/bin/mkdir cache", null);
			this._fileName = this._url.split("/").pop().replace(/\W/g, "");
			this._fileName += "." + this._responseHeaders["Content-Type"].replace(/[\r\n]/g, "").split("/").pop();
			delete query.I;
			query.o = "cache/" + this._fileName;
			system = widget.system(dojo.hostenv.CurlRequest._formatCall(query, this._url), null);
			if (!system.errorString) {
				this.responseText = "cache/" + this._fileName;
			}
		} else {
			if (this._method == "HEAD") {
				this.responseText = this._responseHeader;
			}
		}
	}
	this.readyState = 4;
	if (this.onreadystatechange) {
		this.onreadystatechange.call(this);
	}
};
dojo.hostenv.CurlRequest._formatCall = function (query, url) {
	var call = ["/usr/bin/curl"];
	for (var key in query) {
		if (query[key] != "") {
			call.push("-" + key + " '" + query[key].replace(/'/g, "'") + "'");
		} else {
			call.push("-" + key);
		}
	}
	call.push("'" + url.replace(/'/g, "'") + "'");
	return call.join(" ");
};
dojo.hostenv.exit = function () {
	if (widget.system) {
		widget.system("/bin/rm -rf cache/*", null);
	}
};

