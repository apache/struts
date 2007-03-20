/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.collections.Store");
dojo.require("dojo.lang.common");

/*	Store
 *	Designed to be a simple store of data with access methods...
 *	specifically to be mixed into other objects (such as widgets).
 */
dojo.collections.Store = function(/* array? */jsonArray){
	//	summary
	//	Data Store with accessor methods.
	var data = [];
	this.keyField = "Id";

	this.get = function(){
		//	summary
		//	Get the internal data array, should not be used.
		return data;	//	array
	};
	this.getByKey = function(/* string */key){
		//	summary
		//	Find the internal data object by key.
		for(var i=0; i<data.length; i++){
			if(data[i].key==key){
				return data[i];	// object
			}
		}
		return null;	// null
	};
	this.getByIndex = function(/*number*/idx){ 
		//	summary
		//	Get the internal data object by index.
		return data[idx]; 	// object
	};
	
	this.getData = function(){
		//	summary
		//	Get an array of source objects.
		var arr = [];
		for(var i=0; i<data.length; i++){
			arr.push(data[i].src);
		}
		return arr;	//	array
	};
	this.getDataByKey = function(/*string*/key){
		//	summary
		//	Get the source object by key.
		for(var i=0; i<data.length; i++){
			if(data[i].key==key){
				return data[i].src; //	object
			}
		}
		return null;	//	null
	};
	this.getDataByIndex = function(/*number*/idx){ 
		//	summary
		//	Get the source object at index idx.
		return data[idx].src; 	//	object
	};

	this.update = function(/* Object */obj, /* string */fieldPath, /* Object */val){
		var parts=fieldPath.split("."), i=0, o=obj, field;
		if(parts.length>1) {
			field = parts.pop();
			do{ 
				if(parts[i].indexOf("()")>-1){
					var temp=parts[i++].split("()")[0];
					if(!o[temp]){
						dojo.raise("dojo.collections.Store.getField(obj, '" + field + "'): '" + temp + "' is not a property of the passed object.");
					} else {
						//	this *will* throw an error if the method in question can't be invoked without arguments.
						o = o[temp]();
					}
				} else {
					o = o[parts[i++]];
				}
			} while (i<parts.length && o != null);
		} else {
			field = parts[0];
		}

		obj[field] = val;
		this.onUpdateField(obj, fieldPath, val);
	};

	this.forEach = function(/* function */fn){
		//	summary
		//	Functional iteration directly on the internal data array.
		if(Array.forEach){
			Array.forEach(data, fn, this);
		}else{
			for(var i=0; i<data.length; i++){
				fn.call(this, data[i]);
			}
		}
	};
	this.forEachData = function(/* function */fn){
		//	summary
		//	Functional iteration on source objects in internal data array.
		if(Array.forEach){
			Array.forEach(this.getData(), fn, this);
		}else{
			var a=this.getData();
			for(var i=0; i<a.length; i++){
				fn.call(this, a[i]);
			}
		}
	};

	this.setData = function(/*array*/arr){
		//	summary
		//	Set up the internal data.
		data = []; 	//	don't fire onClearData
		for(var i=0; i<arr.length; i++){
			data.push({ 
				key:arr[i][this.keyField], 
				src:arr[i]
			});
		}
		this.onSetData();
	};
	
	this.clearData = function(){
		//	summary
		//	Clears the internal data array.
		data = [];
		this.onClearData();
	};

	this.addData = function(/*obj*/obj,/*string?*/key){ 
		//	summary
		//	Add an object with optional key to the internal data array.
		var k = key || obj[this.keyField];
		if(this.getByKey(k)){
			var o = this.getByKey(k);
			o.src = obj;
		} else {
			var o={ key:k, src:obj };
			data.push(o);
		}
		this.onAddData(o);
	};
	this.addDataRange = function(/*array*/arr){
		//	summary
		//	Add a range of objects to the internal data array.
		var objects=[];
		for(var i=0; i<arr.length; i++){
			var k = arr[i][this.keyField];
			if(this.getByKey(k)){
				var o = this.getByKey(k);
				o.src = obj;
			} else {
				var o = { key:k, src:arr[i] };
				data.push(o);
			}
			objects.push(o);
		}
		this.onAddDataRange(objects);
	};
	
	this.removeData = function(/*obj*/obj){
		//	summary
		//	remove the passed object from the internal data array.
		var idx=-1;
		var o=null;
		for(var i=0; i<data.length; i++){
			if(data[i].src==obj){
				idx=i;
				o=data[i];
				break;
			}
		}
		this.onRemoveData(o);
		if(idx>-1){
			data.splice(idx,1);
		}
	};
	this.removeDataByKey = function(/*string*/key){
		//	summary
		//	remove the object at key from the internal data array.
		this.removeData(this.getDataByKey(key));
	};
	this.removeDataByIndex = function(/*number*/idx){
		//	summary
		//	remove the object at idx from the internal data array.
		this.removeData(this.getDataByIndex(idx));
	};

	if(jsonArray && jsonArray.length && jsonArray[0]){
		this.setData(jsonArray);
	}
};

dojo.extend(dojo.collections.Store, {
	getField:function(/*object*/obj, /*string*/field){
		//	helper to get the nested value if needed.
		var parts=field.split("."), i=0, o=obj;
		do{ 
			if(parts[i].indexOf("()")>-1){
				var temp=parts[i++].split("()")[0];
				if(!o[temp]){
					dojo.raise("dojo.collections.Store.getField(obj, '" + field + "'): '" + temp + "' is not a property of the passed object.");
				} else {
					//	this *will* throw an error if the method in question can't be invoked without arguments.
					o = o[temp]();
				}
			} else {
				o = o[parts[i++]];
			}
		} while (i<parts.length && o != null);
		
		if(i < parts.length){
			dojo.raise("dojo.collections.Store.getField(obj, '" + field + "'): '" + field + "' is not a property of the passed object.");
		}
		return o; // object
	},
	getFromHtml:function(/* array */meta, /* HTMLTableBody */body, /* function? */fnMod){
		//	summary
		//	Parse HTML data into native JSON structure for the store.
		var rows = body.rows;

		//	create a data constructor.
		var ctor=function(row){
			var obj = {};
			for(var i=0; i<meta.length; i++){
				var o = obj;
				var data = row.cells[i].innerHTML;
				var p = meta[i].getField();
				if(p.indexOf(".") > -1){
					p = p.split(".");
					while(p.length>1){
						var pr = p.shift();
						o[pr] = {};
						o = o[pr];
					}
					p = p[0];
				}

				var type = meta[i].getType();
				if(type == String){
					o[p] = data;
				} else {
					if(data){
						o[p] = new type(data);
					} else {
						o[p] = new type();
					}
				}
			}
			return obj;
		};

		//	we have initialization data, let's parse it.
		var arr=[];
		for(var i=0; i<rows.length; i++){
			var o = ctor(rows[i]);
			if(fnMod){
				fnMod(o, rows[i]);	//	apply any modifiers.
			}
			arr.push(o);
		}
		return arr;	//	array
	},
	onSetData:function(){ },
	onClearData:function(){ },
	onAddData:function(obj){ },
	onAddDataRange:function(arr){ },
	onRemoveData:function(obj){ },
	onUpdateField:function(obj, field, val){ }
});
