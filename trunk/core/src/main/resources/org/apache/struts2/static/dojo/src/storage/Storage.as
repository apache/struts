import flash.external.ExternalInterface;

class Storage {
	static var app : Storage;
	var store: SharedObject;
	static var started: Boolean = false;
	
	public function Storage(){
		ExternalInterface.addCallback("set", null, set);
		ExternalInterface.addCallback("get", null, get);
		ExternalInterface.addCallback("free", null, free);
	}

	public function set(key, value, namespace){
		var primeForReHide = false;
		store = SharedObject.getLocal(namespace);
		store.onStatus = function(status){
			// ExternalInterface.call("alert", status.code == "SharedObject.Flush.Failed");
			// ExternalInterface.call("alert", status.code == "SharedObject.Flush.Success");
			if(primeForReHide){
				primeForReHide = false;
				ExternalInterface.call("dojo.storage.provider.hideStore");
			}
		}
		store.data[key] = value;
		var ret = store.flush();
		if(typeof ret == "string"){
			ExternalInterface.call("dojo.storage.provider.unHideStore");
			primeForReHide = true;
		}
		return store.getSize(namespace);
	}

	public function get(key, namespace){
		store = SharedObject.getLocal(namespace);
		return store.data[key];
	}

	public function free(namespace){
		return SharedObject.getDiskUsage(namespace);
	}

	static function main(mc){
		app = new Storage();
		if(!started){
			ExternalInterface.call("dojo.storage.provider.storageOnLoad");
			started = true;
		}
	}
}

