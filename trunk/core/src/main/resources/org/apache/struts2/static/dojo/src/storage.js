/*
	Copyright (c) 2004-2005, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

// FIXME: should we require JSON here?
dojo.require("dojo.lang.*");
dojo.provide("dojo.storage");
dojo.provide("dojo.storage.StorageProvider");

dojo.storage = new function(){
	this.provider = null;

	// similar API as with dojo.io.addTransport()
	this.setProvider = function(obj){
		this.provider = obj;
	}

	this.set = function(key, value, namespace){
		// FIXME: not very expressive, doesn't have a way of indicating queuing
		if(!this.provider){
			return false;
		}
		return this.provider.set(key, value, namespace);
	}

	this.get = function(key, namespace){
		if(!this.provider){
			return false;
		}
		return this.provider.get(key, namespace);
	}

	this.remove = function(key, namespace){
		return this.provider.remove(key, namespace);
	}
}

dojo.storage.StorageProvider = function(){
}

dojo.lang.extend(dojo.storage.StorageProvider, {
	namespace: "*",
	initialized: false,

	free: function(){
		dojo.unimplemented("dojo.storage.StorageProvider.free");
		return 0;
	},

	freeK: function(){
		return dojo.math.round(this.free()/1024, 0);
	},

	set: function(key, value, namespace){
		dojo.unimplemented("dojo.storage.StorageProvider.set");
	},

	get: function(key, namespace){
		dojo.unimplemented("dojo.storage.StorageProvider.get");
	},

	remove: function(key, value, namespace){
		dojo.unimplemented("dojo.storage.StorageProvider.set");
	}

});
