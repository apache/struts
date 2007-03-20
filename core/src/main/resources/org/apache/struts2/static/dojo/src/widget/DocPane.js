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

dojo.widget.DocPane = function(){
	dojo.event.topic.subscribe("/docs/function/results", this, "onDocResults");
	dojo.event.topic.subscribe("/docs/package/results", this, "onPkgResults");
	dojo.event.topic.subscribe("/docs/function/detail", this, "onDocSelectFunction");
}

dojo.widget.defineWidget(
	"dojo.widget.DocPane",
	dojo.widget.HtmlWidget,
	{
		// Template parameters
		dialog: null,
		dialogBg: null,
		dialogFg: null,
		logIn: null,
		edit: null,
		save: null,
		cancel: null,
		detail: null,
		result: null,
		packag: null,
		fn: null,
		fnLink: null,
		count: null,
		row: null,
		summary: null,
		description: null,
		variables: null,
		vRow: null,
		vLink: null,
		vDesc: null,
		methods: null,
		mRow: null,
		mLink: null,
		mDesc: null,
		requires: null,
		rRow: null,
		rRow2: null,
		rH3: null,
		rLink: null,
		parameters: null,
		pRow: null,
		pLink: null,
		pDesc: null,
		pOpt: null,
		pType: null,
		sType: null,
		sName: null,
		sParams: null,
		sPType: null,
		sPTypeSave: null,
		sPName: null,
		sPNameSave: null,
		pkgDescription: null,

		// Fields and methods
		_appends: [],
		templatePath: dojo.uri.dojoUri("src/widget/templates/DocPane.html"),
		templateCssPath: dojo.uri.dojoUri("src/widget/templates/DocPane.css"),
		isContainer: true,
		fillInTemplate: function(){
			this.requires = dojo.html.removeNode(this.requires);
			this.rRow.style.display = "none";
			this.rRow2.style.display = "none";
			
			this.methods = dojo.html.removeNode(this.methods);
			this.mRow.style.display = "none";
			
			this.dialog = dojo.widget.createWidget("dialog", {}, this.dialog);
			this.dialog.setCloseControl(this.cancel);
			dojo.html.setOpacity(this.dialogBg, 0.8);
			dojo.html.setOpacity(this.dialogFg, 1);

			dojo.event.connect(this.edit, "onclick", dojo.lang.hitch(this, function(){
				if(!this._isLoggedIn){
					this.dialog.show();
				}
			}));
			dojo.event.connect(this.logIn, "onclick", this, "_logIn");
			dojo.event.connect(this.save, "onclick", this, "_save");
			dojo.event.connect(dojo.docs, "logInSuccess", this, "_loggedIn");
			
			/*
			this.pkgDescription = dojo.widget.createWidget("editor2", {
				toolbarAlwaysVisible: true
			}, this.pkgDescription);
			*/
			
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
		},

		_logIn: function(){
			dojo.docs.setUserName(this.userName.value);
			dojo.docs.setPassword(this.password.value);
		},

		_loggedIn: function(){
			this._isLoggedIn = true;
			this.dialog.hide();
			this.pkgEditor = dojo.widget.createWidget("editor2", {
				toolbarAlwaysVisible: true
			}, this.pkgDescription);
		},

		_save: function(){
			if(this.pkgEditor){
				dojo.docs.savePackage(this._pkgPath, {
					description: this.pkgEditor.getEditorContent()
				});
			}
		},

		onDocSelectFunction: function(message){
			dojo.debug("onDocSelectFunction()");
			for(var key in message){
				dojo.debug(key + ": " + dojo.json.serialize(message[key]));
			}
			var meta = message.meta;
			if(meta){
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
			if(variables){
				all = variables;
			}
			if(this_variables){
				all = all.concat(this_variables);
			}
			if(child_variables){
				all = all.concat(child_variables);
			}
			if(!all.length){
				this.variables.style.display = "none";
			}else{
				for(var i = 0, one; one = all[i]; i++){
					this.vLink.innerHTML = one;
					this.vDesc.parentNode.style.display = "none";
					appends.push(this.vParent.appendChild(this.vSave.cloneNode(true)));
				}
			}

			this.sParams.innerHTML = "";
			var first = true;
			for(var param in parameters){
				var paramType = parameters[param].type;
				var paramSummary = parameters[param].summary;
				var paramName = param;
				this.parameters.style.display = "block";		
				this.pLink.innerHTML = paramName;
				this.pOpt.style.display = "none";
				if(parameters[param].opt){
					this.pOpt.style.display = "inline";				
				}
				this.pType.parentNode.style.display = "none";
				if(parameters[param][0]){
					this.pType.parentNode.style.display = "inline";
					this.pType.innerHTML = paramType;
				}
				this.pDesc.parentNode.style.display = "none";
				if(paramSummary){
					this.pDesc.parentNode.style.display = "inline";
					this.pDesc.innerHTML = paramSummary;
				}
				appends.push(this.pParent.appendChild(this.pSave.cloneNode(true)));

				if(!first) {
					this.sParams.appendChild(document.createTextNode(", "));
				}
				first = false;
				if(paramType){
					dojo.debug(this.sPTypeSave);
					this.sPTypeSave.innerHTML = paramType;
					this.sParams.appendChild(this.sPTypeSave.cloneNode(true));
					this.sParams.appendChild(document.createTextNode(" "));
				}
				dojo.debug(this.sPNameSave);
				this.sPNameSave.innerHTML = paramName;
				this.sParams.appendChild(this.sPNameSave.cloneNode(true))
			}

			if(message.returns){
				this.sType.innerHTML = message.returns;
			}else{
				this.sType.innerHTML = "void";
			}

			this.sName.innerHTML = message.name;

			this.domNode.appendChild(this.navSave);
			this.domNode.appendChild(this.detailSave.cloneNode(true));

			for(var i = 0, append; append = appends[i]; i++){
				dojo.html.removeNode(append);
			}
		},

		onPkgResult: function(/*Object*/ results){
			if(this.pkgEditor){
				this.pkgEditor.close(true);
				dojo.debug(this.pkgDescription);
			}
			var methods = results.methods;
			var requires = results.requires;
			var description = results.description;
			this._pkgPath = results.path;
			var requireLinks = [];
			var appends = this._appends;
			while(appends.length){
				dojo.html.removeNode(appends.shift());
			}

			dojo.html.removeChildren(this.domNode);
			
			this.pkg.innerHTML = results.pkg;
			
			var hasRequires = false;
			for(var env in requires){
				hasRequires = true;

				this.rH3.style.display = "none";
				if(env != "common"){
					this.rH3.style.display = "";
					this.rH3.innerHTML = env;
				}

				for(var i = 0, require; require = requires[env][i]; i++){
					requireLinks.push({
						name: require
					});
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
			
			if(hasRequires){
				appends.push(this.packageSave.appendChild(this.requires.cloneNode(true)));
			}

			if(results.size){
				for(var i = 0, method; method = methods[i]; i++){
					this.mLink.innerHTML = method.name;
					this.mLink.href = "#" + method.name;
					this.mDesc.parentNode.style.display = "none";
					if(method.summary){
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
			
			/*
			dojo.debug(description);
			function fillContent(){
				this.pkgDescription.replaceEditorContent(description);
				this.pkgDescription._updateHeight();
			}
			if(this.pkgDescription.isLoaded){
				fillContent();
			}else{
				dojo.event.connect(this.pkgDescription, "onLoad", dojo.lang.hitch(this, fillContent));
			}
			*/
			this.pkgDescription.innerHTML = description;
			
			function makeSelect(fOrP, x){
				return function(e) {
					dojo.event.topic.publish("/docs/" + fOrP + "/select", x);
				}
			}

			var as = this.domNode.getElementsByTagName("a");
			for(var i = 0, a; a = as[i]; i++){
				if(a.className == "docMLink"){
					dojo.event.connect(a, "onclick", makeSelect("function", methods[i]));
				}else if(a.className == "docRLink"){
					dojo.event.connect(a, "onclick", makeSelect("package", requireLinks[i]));
				}
			}
		},

		onDocResults: function(fns){
			dojo.debug("onDocResults(): called");

			if(fns.length == 1){
				dojo.event.topic.publish("/docs/function/select", fns[0]);
				return;
			}

			dojo.html.removeChildren(this.domNode);

			this.count.innerHTML = fns.length;
			var appends = [];
			for(var i = 0, fn; fn = fns[i]; i++){
				this.fnLink.innerHTML = fn.name;
				this.fnLink.href = "#" + fn.name;
				if(fn.id){
					this.fnLink.href = this.fnLink.href + "," + fn.id;	
				}
				this.summary.parentNode.style.display = "none";
				if(fn.summary){
					this.summary.parentNode.style.display = "inline";				
					this.summary.innerHTML = fn.summary;
				}
				appends.push(this.rowParent.appendChild(this.rowSave.cloneNode(true)));
			}

			function makeSelect(x){
				return function(e) {
					dojo.event.topic.publish("/docs/function/select", x);
				}
			}

			this.domNode.appendChild(this.resultSave.cloneNode(true));
			var as = this.domNode.getElementsByTagName("a");
			for(var i = 0, a; a = as[i]; i++){
				dojo.event.connect(a, "onclick", makeSelect(fns[i]));
			}

			for(var i = 0, append; append = appends[i]; i++){
				this.rowParent.removeChild(append);
			}
		}
	}
);
