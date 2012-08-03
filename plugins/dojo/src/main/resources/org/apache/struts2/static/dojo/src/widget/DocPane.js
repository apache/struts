/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.widget.DocPane");
dojo.require("dojo.widget.*");
dojo.require("dojo.io.*");
dojo.require("dojo.event.*");
dojo.require("dojo.widget.HtmlWidget");
dojo.require("dojo.widget.Editor2");
dojo.require("dojo.widget.Dialog");
dojo.require("dojo.html.common");
dojo.require("dojo.html.display");
dojo.widget.DocPane = function () {
	dojo.event.topic.subscribe("/docs/function/results", this, "onDocResults");
	dojo.event.topic.subscribe("/docs/package/results", this, "onPkgResults");
	dojo.event.topic.subscribe("/docs/function/detail", this, "onDocSelectFunction");
};
dojo.widget.defineWidget("dojo.widget.DocPane", dojo.widget.HtmlWidget, {dialog:null, dialogBg:null, dialogFg:null, logIn:null, edit:null, save:null, cancel:null, detail:null, result:null, packag:null, fn:null, fnLink:null, count:null, row:null, summary:null, description:null, variables:null, vRow:null, vLink:null, vDesc:null, methods:null, mRow:null, mLink:null, mDesc:null, requires:null, rRow:null, rRow2:null, rH3:null, rLink:null, parameters:null, pRow:null, pLink:null, pDesc:null, pOpt:null, pType:null, sType:null, sName:null, sParams:null, sPType:null, sPTypeSave:null, sPName:null, sPNameSave:null, pkgDescription:null, _appends:[], templateString:"<div class=\"dojoDocPane\">\n\t<div dojoAttachPoint=\"containerNode\" class=\"container\"></div>\n\n\t<div dojoAttachPoint=\"dialog\" class=\"dialog\">\n\t\t<div class=\"container\" dojoAttachPoint=\"dialogBg\">\n\t\t\t<div class=\"docDialog\" dojoAttachPoint=\"dialogFg\">\n\t\t\t\t<h2>Log In</h2>\n\t\t\t\t<p><input id=\"dojoDocUserName\" dojoAttachPoint=\"userName\"><label for=\"dojoDocUserName\">User Name:</label></p>\n\t\t\t\t<p><input id=\"dojoDocPassword\" dojoAttachPoint=\"password\" type=\"password\"><label for=\"dojoDocPassword\">Password:</label></p>\n\t\t\t\t<p><input type=\"button\" dojoAttachPoint=\"cancel\" value=\"cancel\"> <input type=\"button\" dojoAttachPoint=\"logIn\" value=\"Log In\"></p>\n\t\t\t\t<p></p>\n\t\t\t</div>\n\t\t</div>\n\t</div>\n\n\t<div dojoAttachPoint=\"nav\" class=\"nav\"><span>Detail</span> | <span>Source</span> | <span>Examples</span> | <span>Walkthrough</span></div>\n\n\t<div dojoAttachPoint=\"detail\" class=\"detail\">\n\t\t<h1>Detail: <span class=\"fn\" dojoAttachPoint=\"fn\">dojo.select</span></h1>\n\t\t<div class=\"description\" dojoAttachPoint=\"description\">Description</div>\n\t\t<div class=\"params\" dojoAttachPoint=\"parameters\">\n\t\t\t<h2>Parameters</h2>\n\t\t\t<div class=\"row\" dojoAttachPoint=\"pRow\">\n\t\t\t\t<span dojoAttachPoint=\"pOpt\"><em>optional</em> </span>\n\t\t\t\t<span><span dojoAttachPoint=\"pType\">type</span> </span>\n\t\t\t\t<a href=\"#\" dojoAttachPoint=\"pLink\">variable</a>\n\t\t\t\t<span> - <span dojoAttachPoint=\"pDesc\"></span></span>\n\t\t\t</div>\n\t\t</div>\n\t\t<div class=\"variables\" dojoAttachPoint=\"variables\">\n\t\t\t<h2>Variables</h2>\n\t\t\t<div class\"row\" dojoAttachPoint=\"vRow\">\n\t\t\t\t<a href=\"#\" dojoAttachPoint=\"vLink\">variable</a><span> - <span dojoAttachPoint=\"vDesc\"></span></span>\n\t\t\t</div>\n\t\t</div>\n\t\t<div class=\"signature\">\n\t\t\t<h2>Signature</h2>\n\t\t\t<div class=\"source\">\n\t\t\t\t<span class=\"return\" dojoAttachPoint=\"sType\">returnType</span> \n\t\t\t\t<span class=\"function\" dojoAttachPoint=\"sName\">foo</span>\n\t\t\t\t(<span class=\"params\" dojoAttachPoint=\"sParams\">\n\t\t\t\t\t<span class=\"type\" dojoAttachPoint=\"sPType\">type </span>\n\t\t\t\t\t<span class=\"name\" dojoAttachPoint=\"sPName\">paramName</span>\n\t\t\t\t</span>)\n\t\t\t</div>\n\t\t</div>\n\t</div>\n\t\n\t<div dojoAttachPoint=\"result\" class=\"result\">\n\t\t<h1>Search Results: <span dojoAttachPoint=\"count\">0</span> matches</h1>\n\t\t<div class=\"row\" dojoAttachPoint=\"row\">\n\t\t\t<a href=\"#\" dojoAttachPoint=\"fnLink\">dojo.fnLink</a>\n\t\t\t<span> - <span class=\"summary\" dojoAttachPoint=\"summary\">summary</span></span>\n\t\t</div>\n\t</div>\n\n\t<div dojoAttachPoint=\"packag\" class=\"package\">\n\t\t<h1>Package: \n\t\t\t<span class=\"pkg\" dojoAttachPoint=\"pkg\">dojo.package</span> \n\t\t\t<span class=\"edit\" dojoAttachPoint=\"edit\">[edit]</span> \n\t\t\t<span class=\"save\" dojoAttachPoint=\"save\">[save]</span>\n\t\t</h1>\n\t\t<div dojoAttachPoint=\"pkgDescription\" class=\"description\">Description</div>\n\t\t<div class=\"methods\" dojoAttachPoint=\"methods\">\n\t\t\t<h2>Methods</h2>\n\t\t\t<div class=\"row\" dojoAttachPoint=\"mRow\">\n\t\t\t\t<a href=\"#\" dojoAttachPoint=\"mLink\">method</a>\n\t\t\t\t<span> - <span class=\"description\" dojoAttachPoint=\"mDesc\"></span></span>\n\t\t\t</div>\n\t\t</div>\n\t\t<div class=\"requires\" dojoAttachPoint=\"requires\">\n\t\t\t<h2>Requires</h2>\n\t\t\t<div class=\"row\" dojoAttachPoint=\"rRow\">\n\t\t\t\t<h3 dojoAttachPoint=\"rH3\">Environment</h3>\n\t\t\t\t<div dojoAttachPoint=\"rRow2\"><a href=\"#\" dojoAttachPoint=\"rLink\" class=\"package\">require</a></div>\n\t\t\t</div>\n\t\t</div>\n\t</div>\n</div>\n", templateCssString:".dojoDocPane { padding:1em; font: 1em Georgia,Times,\"Times New Roman\",serif; }\n\n.dojoDocPane .container{ }\n\n.dojoDocPane .dialog{ }\n.dojoDocPane .dialog .container{ padding: 0.5em; background: #fff; border: 2px solid #333; }\n.dojoDocPane .dialog .docDialog{ background: transparent; width: 20em; }\n.dojoDocPane .dialog .docDialog h2{ margin-top: 0; padding-top: 0; }\n.dojoDocPane .dialog .docDialog input { float: right; margin-right: 1em; }\n.dojoDocPane .dialog .docDialog p{ clear: both; }\n#dojoDocUserName, #dojoDocPassword { width: 10em; }\n\n.dojoDocPane .nav{ }\n.dojoDocPane .nav span{ }\n\n.dojoDocPane .detail{ }\n.dojoDocPane .detail h1{ }\n.dojoDocPane .detail h1 span.fn{ }\n.dojoDocPane .detail .description{ }\n.dojoDocPane .detail .params{ }\n.dojoDocPane .detail .params .row{ }\n.dojoDocPane .detail .params .row span{ }\n.dojoDocPane .detail .variables{ }\n.dojoDocPane .detail .variables .row{ }\n.dojoDocPane .detail .signature{ }\n.dojoDocPane .detail .signature .source{ white-space: pre; font: 0.8em Monaco, Courier, \"Courier New\", monospace; }\n.dojoDocPane .detail .signature .source .return{ color:#369; }\n.dojoDocPane .detail .signature .source .function{ color: #98543F; font-weight: bold; }\n.dojoDocPane .detail .signature .source .params{ }\n.dojoDocPane .detail .signature .source .params .type{ font-style: italic; color: #d17575; }\n.dojoDocPane .detail .signature .source .params .name{ color: #d14040; }\n\n.dojoDocPane .result{ }\n.dojoDocPane .result h1{ }\n.dojoDocPane .result .row{ }\n.dojoDocPane .result .row .summary{ }\n\n.dojoDocPane .package{ }\n.dojoDocPane .package h1{ }\n.dojoDocPane .package .row{ }\n.dojoDocPane .package .row .summary{ }\n.dojoDocPane .package .description{ }\n.dojoDocPane .package .methods{ }\n.dojoDocPane .package .methods h2{ }\n.dojoDocPane .package .methods .row{ }\n.dojoDocPane .package .methods .row .description{ }\n.dojoDocPane .package .requires{ }\n.dojoDocPane .package .requires h2{ }\n.dojoDocPane .package .requires .row{ }\n", templateCssPath:dojo.uri.moduleUri("dojo.widget", "templates/DocPane.css"), isContainer:true, fillInTemplate:function () {
	this.requires = dojo.html.removeNode(this.requires);
	this.rRow.style.display = "none";
	this.rRow2.style.display = "none";
	this.methods = dojo.html.removeNode(this.methods);
	this.mRow.style.display = "none";
	this.dialog = dojo.widget.createWidget("dialog", {}, this.dialog);
	this.dialog.setCloseControl(this.cancel);
	dojo.html.setOpacity(this.dialogBg, 0.8);
	dojo.html.setOpacity(this.dialogFg, 1);
	dojo.event.connect(this.edit, "onclick", dojo.lang.hitch(this, function () {
		if (!this._isLoggedIn) {
			this.dialog.show();
		}
	}));
	dojo.event.connect(this.logIn, "onclick", this, "_logIn");
	dojo.event.connect(this.save, "onclick", this, "_save");
	dojo.event.connect(dojo.docs, "logInSuccess", this, "_loggedIn");
	this.homeSave = this.containerNode.cloneNode(true);
	this.detailSave = dojo.html.removeNode(this.detail);
	this.resultSave = dojo.html.removeNode(this.result);
	this.packageSave = dojo.html.removeNode(this.packag);
	this.results = dojo.html.removeNode(this.results);
	this.rowParent = this.row.parentNode;
	this.rowSave = dojo.html.removeNode(this.row);
	this.vParent = this.vRow.parentNode;
	this.vSave = dojo.html.removeNode(this.vRow);
	this.pParent = this.pRow.parentNode;
	this.pSave = dojo.html.removeNode(this.pRow);
	this.sPTypeSave = dojo.html.removeNode(this.sPType);
	this.sPNameSave = dojo.html.removeNode(this.sPName);
	this.navSave = dojo.html.removeNode(this.nav);
}, _logIn:function () {
	dojo.docs.setUserName(this.userName.value);
	dojo.docs.setPassword(this.password.value);
}, _loggedIn:function () {
	this._isLoggedIn = true;
	this.dialog.hide();
	this.pkgEditor = dojo.widget.createWidget("editor2", {toolbarAlwaysVisible:true}, this.pkgDescription);
}, _save:function () {
	if (this.pkgEditor) {
		dojo.docs.savePackage(this._pkgPath, {description:this.pkgEditor.getEditorContent()});
	}
}, onDocSelectFunction:function (message) {
	dojo.debug("onDocSelectFunction()");
	for (var key in message) {
		dojo.debug(key + ": " + dojo.json.serialize(message[key]));
	}
	var meta = message.meta;
	if (meta) {
		var variables = meta.variables;
		var this_variables = meta.this_variables;
		var child_variables = meta.child_variables;
		var parameters = meta.parameters;
	}
	var doc = message.doc;
	dojo.debug(dojo.json.serialize(doc));
	var appends = this._appends;
	dojo.html.removeChildren(this.domNode);
	this.fn.innerHTML = message.name;
	this.variables.style.display = "block";
	var all = [];
	if (variables) {
		all = variables;
	}
	if (this_variables) {
		all = all.concat(this_variables);
	}
	if (child_variables) {
		all = all.concat(child_variables);
	}
	if (!all.length) {
		this.variables.style.display = "none";
	} else {
		for (var i = 0, one; one = all[i]; i++) {
			this.vLink.innerHTML = one;
			this.vDesc.parentNode.style.display = "none";
			appends.push(this.vParent.appendChild(this.vSave.cloneNode(true)));
		}
	}
	this.sParams.innerHTML = "";
	var first = true;
	for (var param in parameters) {
		var paramType = parameters[param].type;
		var paramSummary = parameters[param].summary;
		var paramName = param;
		this.parameters.style.display = "block";
		this.pLink.innerHTML = paramName;
		this.pOpt.style.display = "none";
		if (parameters[param].opt) {
			this.pOpt.style.display = "inline";
		}
		this.pType.parentNode.style.display = "none";
		if (parameters[param][0]) {
			this.pType.parentNode.style.display = "inline";
			this.pType.innerHTML = paramType;
		}
		this.pDesc.parentNode.style.display = "none";
		if (paramSummary) {
			this.pDesc.parentNode.style.display = "inline";
			this.pDesc.innerHTML = paramSummary;
		}
		appends.push(this.pParent.appendChild(this.pSave.cloneNode(true)));
		if (!first) {
			this.sParams.appendChild(document.createTextNode(", "));
		}
		first = false;
		if (paramType) {
			dojo.debug(this.sPTypeSave);
			this.sPTypeSave.innerHTML = paramType;
			this.sParams.appendChild(this.sPTypeSave.cloneNode(true));
			this.sParams.appendChild(document.createTextNode(" "));
		}
		dojo.debug(this.sPNameSave);
		this.sPNameSave.innerHTML = paramName;
		this.sParams.appendChild(this.sPNameSave.cloneNode(true));
	}
	if (message.returns) {
		this.sType.innerHTML = message.returns;
	} else {
		this.sType.innerHTML = "void";
	}
	this.sName.innerHTML = message.name;
	this.domNode.appendChild(this.navSave);
	this.domNode.appendChild(this.detailSave.cloneNode(true));
	for (var i = 0, append; append = appends[i]; i++) {
		dojo.html.removeNode(append);
	}
}, onPkgResult:function (results) {
	if (this.pkgEditor) {
		this.pkgEditor.close(true);
		dojo.debug(this.pkgDescription);
	}
	var methods = results.methods;
	var requires = results.requires;
	var description = results.description;
	this._pkgPath = results.path;
	var requireLinks = [];
	var appends = this._appends;
	while (appends.length) {
		dojo.html.removeNode(appends.shift());
	}
	dojo.html.removeChildren(this.domNode);
	this.pkg.innerHTML = results.pkg;
	var hasRequires = false;
	for (var env in requires) {
		hasRequires = true;
		this.rH3.style.display = "none";
		if (env != "common") {
			this.rH3.style.display = "";
			this.rH3.innerHTML = env;
		}
		for (var i = 0, require; require = requires[env][i]; i++) {
			requireLinks.push({name:require});
			this.rLink.innerHTML = require;
			this.rLink.href = "#" + require;
			var rRow2 = this.rRow2.parentNode.insertBefore(this.rRow2.cloneNode(true), this.rRow2);
			rRow2.style.display = "";
			appends.push(rRow2);
		}
		var rRow = this.rRow.parentNode.insertBefore(this.rRow.cloneNode(true), this.rRow);
		rRow.style.display = "";
		appends.push(rRow);
	}
	if (hasRequires) {
		appends.push(this.packageSave.appendChild(this.requires.cloneNode(true)));
	}
	if (results.size) {
		for (var i = 0, method; method = methods[i]; i++) {
			this.mLink.innerHTML = method.name;
			this.mLink.href = "#" + method.name;
			this.mDesc.parentNode.style.display = "none";
			if (method.summary) {
				this.mDesc.parentNode.style.display = "inline";
				this.mDesc.innerHTML = method.summary;
			}
			var mRow = this.mRow.parentNode.insertBefore(this.mRow.cloneNode(true), this.mRow);
			mRow.style.display = "";
			appends.push(mRow);
		}
		appends.push(this.packageSave.appendChild(this.methods.cloneNode(true)));
	}
	this.domNode.appendChild(this.packageSave);
	this.pkgDescription.innerHTML = description;
	function makeSelect(fOrP, x) {
		return function (e) {
			dojo.event.topic.publish("/docs/" + fOrP + "/select", x);
		};
	}
	var as = this.domNode.getElementsByTagName("a");
	for (var i = 0, a; a = as[i]; i++) {
		if (a.className == "docMLink") {
			dojo.event.connect(a, "onclick", makeSelect("function", methods[i]));
		} else {
			if (a.className == "docRLink") {
				dojo.event.connect(a, "onclick", makeSelect("package", requireLinks[i]));
			}
		}
	}
}, onDocResults:function (fns) {
	dojo.debug("onDocResults(): called");
	if (fns.length == 1) {
		dojo.event.topic.publish("/docs/function/select", fns[0]);
		return;
	}
	dojo.html.removeChildren(this.domNode);
	this.count.innerHTML = fns.length;
	var appends = [];
	for (var i = 0, fn; fn = fns[i]; i++) {
		this.fnLink.innerHTML = fn.name;
		this.fnLink.href = "#" + fn.name;
		if (fn.id) {
			this.fnLink.href = this.fnLink.href + "," + fn.id;
		}
		this.summary.parentNode.style.display = "none";
		if (fn.summary) {
			this.summary.parentNode.style.display = "inline";
			this.summary.innerHTML = fn.summary;
		}
		appends.push(this.rowParent.appendChild(this.rowSave.cloneNode(true)));
	}
	function makeSelect(x) {
		return function (e) {
			dojo.event.topic.publish("/docs/function/select", x);
		};
	}
	this.domNode.appendChild(this.resultSave.cloneNode(true));
	var as = this.domNode.getElementsByTagName("a");
	for (var i = 0, a; a = as[i]; i++) {
		dojo.event.connect(a, "onclick", makeSelect(fns[i]));
	}
	for (var i = 0, append; append = appends[i]; i++) {
		this.rowParent.removeChild(append);
	}
}});

