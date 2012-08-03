/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.browser_debug");
dojo.hostenv.loadedUris.push("../src/bootstrap1.js");
dojo.hostenv.loadedUris.push("../src/loader.js");
dojo.hostenv.loadedUris.push("../src/hostenv_browser.js");
dojo.hostenv._loadedUrisListStart = dojo.hostenv.loadedUris.length;
function removeComments(contents) {
	contents = new String((!contents) ? "" : contents);
	contents = contents.replace(/^(.*?)\/\/(.*)$/mg, "$1");
	contents = contents.replace(/(\n)/mg, "__DOJONEWLINE");
	contents = contents.replace(/\/\*(.*?)\*\//g, "");
	return contents.replace(/__DOJONEWLINE/mg, "\n");
}
dojo.hostenv.getRequiresAndProvides = function (contents) {
	if (!contents) {
		return [];
	}
	var deps = [];
	var tmp;
	RegExp.lastIndex = 0;
	var testExp = /dojo.(hostenv.loadModule|hostenv.require|require|requireIf|kwCompoundRequire|hostenv.conditionalLoadModule|hostenv.startPackage|provide)\([\w\W]*?\)/mg;
	while ((tmp = testExp.exec(contents)) != null) {
		deps.push(tmp[0]);
	}
	return deps;
};
dojo.hostenv.getDelayRequiresAndProvides = function (contents) {
	if (!contents) {
		return [];
	}
	var deps = [];
	var tmp;
	RegExp.lastIndex = 0;
	var testExp = /dojo.(requireAfterIf)\([\w\W]*?\)/mg;
	while ((tmp = testExp.exec(contents)) != null) {
		deps.push(tmp[0]);
	}
	return deps;
};
dojo.clobberLastObject = function (objpath) {
	if (objpath.indexOf(".") == -1) {
		if (!dj_undef(objpath, dj_global)) {
			delete dj_global[objpath];
		}
		return true;
	}
	var syms = objpath.split(/\./);
	var base = dojo.evalObjPath(syms.slice(0, -1).join("."), false);
	var child = syms[syms.length - 1];
	if (!dj_undef(child, base)) {
		delete base[child];
		return true;
	}
	return false;
};
var removals = [];
function zip(arr) {
	var ret = [];
	var seen = {};
	for (var x = 0; x < arr.length; x++) {
		if (!seen[arr[x]]) {
			ret.push(arr[x]);
			seen[arr[x]] = true;
		}
	}
	return ret;
}
var old_dj_eval = dj_eval;
dj_eval = function () {
	return true;
};
dojo.hostenv.oldLoadUri = dojo.hostenv.loadUri;
dojo.hostenv.loadUri = function (uri, cb) {
	if (dojo.hostenv.loadedUris[uri]) {
		return true;
	}
	try {
		var text = this.getText(uri, null, true);
		if (!text) {
			return false;
		}
		if (cb) {
			var expr = old_dj_eval("(" + text + ")");
			cb(expr);
		} else {
			var requires = dojo.hostenv.getRequiresAndProvides(text);
			eval(requires.join(";"));
			dojo.hostenv.loadedUris.push(uri);
			dojo.hostenv.loadedUris[uri] = true;
			var delayRequires = dojo.hostenv.getDelayRequiresAndProvides(text);
			eval(delayRequires.join(";"));
		}
	}
	catch (e) {
		alert(e);
	}
	return true;
};
dojo.hostenv._writtenIncludes = {};
dojo.hostenv.writeIncludes = function (willCallAgain) {
	for (var x = removals.length - 1; x >= 0; x--) {
		dojo.clobberLastObject(removals[x]);
	}
	var depList = [];
	var seen = dojo.hostenv._writtenIncludes;
	for (var x = 0; x < dojo.hostenv.loadedUris.length; x++) {
		var curi = dojo.hostenv.loadedUris[x];
		if (!seen[curi]) {
			seen[curi] = true;
			depList.push(curi);
		}
	}
	dojo.hostenv._global_omit_module_check = true;
	for (var x = dojo.hostenv._loadedUrisListStart; x < depList.length; x++) {
		document.write("<script type='text/javascript' src='" + depList[x] + "'></script>");
	}
	document.write("<script type='text/javascript'>dojo.hostenv._global_omit_module_check = false;</script>");
	dojo.hostenv._loadedUrisListStart = 0;
	if (!willCallAgain) {
		dj_eval = old_dj_eval;
		dojo.hostenv.loadUri = dojo.hostenv.oldLoadUri;
	}
};

