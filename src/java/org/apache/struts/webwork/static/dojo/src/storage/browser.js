/*
	Copyright (c) 2004-2005, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.storage.browser");
dojo.require("dojo.storage");
dojo.require("dojo.uri.*");

dojo.storage.browser.StorageProvider = function(){
	this.initialized = false;
	this.flash = null;
	this.backlog = [];
}

dojo.inherits(	dojo.storage.browser.StorageProvider, 
				dojo.storage.StorageProvider);

dojo.lang.extend(dojo.storage.browser.StorageProvider, {
	storageOnLoad: function(){
		this.initialized = true;
		this.hideStore();
		while(this.backlog.length){
			this.set.apply(this, this.backlog.shift());
		}
	},

	unHideStore: function(){
		var container = dojo.byId("dojo-storeContainer");
		with(container.style){
			position = "absolute";
			overflow = "visible";
			width = "215px";
			height = "138px";
			// FIXME: make these positions dependent on screen size/scrolling!
			left = "30px"; 
			top = "30px";
			visiblity = "visible";
			zIndex = "20";
			border = "1px solid black";
		}
	},

	hideStore: function(status){
		var container = dojo.byId("dojo-storeContainer");
		with(container.style){
			left = "-300px";
			top = "-300px";
		}
	},

	set: function(key, value, ns){
		if(!this.initialized){
			this.backlog.push([key, value, ns]);
			return "pending";
		}
		return this.flash.set(key, value, ns||this.namespace);
	},

	get: function(key, ns){
		return this.flash.get(key, ns||this.namespace);
	},

	writeStorage: function(){
		var swfloc = dojo.uri.dojoUri("src/storage/Storage.swf").toString();
		// alert(swfloc);
		var storeParts = [
			'<div id="dojo-storeContainer"',
				'style="position: absolute; left: -300px; top: -300px;">'];
		if(dojo.render.html.ie){
			storeParts.push('<object');
			storeParts.push('	style="border: 1px solid black;"');
			storeParts.push('	classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000"');
			storeParts.push('	codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,0,0"');
			storeParts.push('	width="215" height="138" id="dojoStorage">');
			storeParts.push('	<param name="movie" value="'+swfloc+'">');
			storeParts.push('	<param name="quality" value="high">');
			storeParts.push('</object>');
		}else{
			storeParts.push('<embed src="'+swfloc+'" width="215" height="138" ');
			storeParts.push('	quality="high" ');
			storeParts.push('	pluginspage="http://www.macromedia.com/go/getflashplayer" ');
			storeParts.push('	type="application/x-shockwave-flash" ');
			storeParts.push('	name="dojoStorage">');
			storeParts.push('</embed>');
		}
		storeParts.push('</div>');
		document.write(storeParts.join(""));
	}
});

dojo.storage.setProvider(new dojo.storage.browser.StorageProvider());
dojo.storage.provider.writeStorage();

dojo.addOnLoad(function(){
	dojo.storage.provider.flash = (dojo.render.html.ie) ? window["dojoStorage"] : document["dojoStorage"];
});
