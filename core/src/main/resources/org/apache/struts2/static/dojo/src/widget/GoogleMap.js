/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.widget.GoogleMap");
dojo.require("dojo.event.*");
dojo.require("dojo.math");
dojo.require("dojo.widget.*");
dojo.require("dojo.uri.Uri");
dojo.require("dojo.widget.HtmlWidget");

(function(){
	var gkey = djConfig["gMapKey"]||djConfig["googleMapKey"];

	//	the Google API key mechanism sucks.  We're hardcoding here for love and affection but I don't like it.
	var uri=new dojo.uri.Uri(window.location.href);
	if(uri.host=="www.dojotoolkit.org"){
		gkey="ABQIAAAACUNdgv_7FGOmUslbm9l6_hRqjp7ri2mNiOEYqetD3xnFHpt5rBSjszDd1sdufPyQKUTyCf_YxoIxvw";
	}
	else if(uri.host=="blog.dojotoolkit.org"){
		gkey="ABQIAAAACUNdgv_7FGOmUslbm9l6_hSkep6Av1xaMhVn3yCLkorJeXeLARQ6fammI_P3qSGleTJhoI5_1JmP_Q";
	}
	else if(uri.host=="archive.dojotoolkit.org"){
		gkey="ABQIAAAACUNdgv_7FGOmUslbm9l6_hTaQpDt0dyGLIHbXMPTzg1kWeAfwRTwZNyrUfbfxYE9yIvRivEjcXoDTg";
	}
	else if(uri.host=="dojotoolkit.org"){
		gkey="ABQIAAAACUNdgv_7FGOmUslbm9l6_hSaOaO_TgJ5c3mtQFnk5JO2zD5dZBRZk-ieqVs7BORREYNzAERmcJoEjQ";
	}

	if(!dojo.hostenv.post_load_){
		if(!gkey || gkey==""){
			dojo.raise("dojo.widget.GoogleMap: The Google Map widget requires a proper API key in order to be used.");
		}
		var tag = "<scr"+"ipt src='http://maps.google.com/maps?file=api&amp;v=2&amp;key="+gkey+"'></scri"+"pt>";
		if(!dj_global["GMap2"]){
			document.write(tag);
		}
	}else{
		dojo.debug("Cannot initialize Google Map system after the page has been loaded! Please either manually include the script block provided by Google in your page or require() the GoogleMap widget before onload has fired.");
	}
})();

dojo.widget.defineWidget(
	"dojo.widget.GoogleMap",
	dojo.widget.HtmlWidget,
	function(){
		//	summary
		//	initializer/constructor for the simple GoogleMap widget.
		this.map=null;
		this.geocoder=null;
		this.data=[];
		this.datasrc="";
		this.controls=["largemap","scale","maptype"];
	},
{
	templatePath:null,
	templateCssPath:null,
	isContainer: false,

	_defaultPoint:{lat:39.10662, lng: -94.578209},

	setControls:function(){
		//	summary
		//	Set any controls on the map in question.
		var methodmap={
			largemap:GLargeMapControl,
			smallmap:GSmallMapControl,
			smallzoom:GSmallZoomControl,
			scale:GScaleControl,
			maptype:GMapTypeControl,
			overview:GOverviewMapControl
		};
		for(var i=0; i<this.controls.length; i++){
			this.map.addControl(new (methodmap[this.controls[i].toLowerCase()])());
		}
	},
	
	findCenter:function(/* GLatLngBounds */bounds){
		//	summary
		//	Returns the center point given the Bounds object.
		if(this.data.length==1){
			return (new GLatLng(this.data[0].lat, this.data[0].lng));	//	GLatLng
		}
		var clat=(bounds.getNorthEast().lat()+bounds.getSouthWest().lat())/2;
		var clng=(bounds.getNorthEast().lng()+bounds.getSouthWest().lng())/2;
		return (new GLatLng(clat,clng));	//	GLatLng
	},

	createPinpoint:function(/* GLatLng */pt, /* string? */overlay){
		//	summary
		//	Creates a marker at the given point, with an optional overlay HTML string.
		var m=new GMarker(pt);
		if(overlay){
			GEvent.addListener(m,"click",function(){
				m.openInfoWindowHtml("<div>"+overlay+"</div>");
			});
		}
		return m;	//	GMarker
	},
	plot:function(/* object */obj){
		//	summary
		//	Plots a point at given lat/lng coordinate
		var p=new GLatLng(obj.lat,obj.lng);
		var d=obj.description||null;
		var m=this.createPinpoint(p,d);
		this.map.addOverlay(m);
	},
	plotAddress:function(/* string */address){
		//	summary
		//	Calls the Google Geocoder to get a lat/lng coordinate at string address
		var self=this;
		this.geocoder.getLocations(address, function(response){
			if(!response || response.Status.code != 200){
				alert("The address \"" + address + "\" was not found.");
				return;
			}
			var obj={
				lat:response.Placemark[0].Point.coordinates[1],
				lng:response.Placemark[0].Point.coordinates[0],
				description:response.Placemark[0].address
			};
			self.data.push(obj);
			self.render();
		});
	},

	parse:function(/* HTMLTable */table){
		//	summary
		//	Parses the passed table for data to plot on this map.
		this.data=[];

		//	get the column indices
		var h=table.getElementsByTagName("thead")[0];
		if(!h){
			return;
		}

		var a=[];
		var cols=h.getElementsByTagName("td");
		if(cols.length==0){
			cols=h.getElementsByTagName("th");
		}
		for(var i=0; i<cols.length; i++){
			var c=cols[i].innerHTML.toLowerCase();
			if(c=="long") c="lng";
			a.push(c);
		}
		
		//	parse the data
		var b=table.getElementsByTagName("tbody")[0];
		if(!b){
			return;
		}
		for(var i=0; i<b.childNodes.length; i++){
			if(!(b.childNodes[i].nodeName&&b.childNodes[i].nodeName.toLowerCase()=="tr")){
				continue;
			}
			var cells=b.childNodes[i].getElementsByTagName("td");
			var o={};
			for(var j=0; j<a.length; j++){
				var col=a[j];
				if(col=="lat"||col=="lng"){
					o[col]=parseFloat(cells[j].innerHTML);					
				}else{
					o[col]=cells[j].innerHTML;
				}
			}
			this.data.push(o);
		}
	},
	render:function(){
		//	summary
		//	Plots all acutal points in the current data array.
		if(this.data.length==0){
			this.map.setCenter(new GLatLng(this._defaultPoint.lat, this._defaultPoint.lng), 4);
			return;
		}

		//	remove all overlays
		this.map.clearOverlays();

		var bounds=new GLatLngBounds();
		var d=this.data;
		for(var i=0; i<d.length; i++){
			bounds.extend(new GLatLng(d[i].lat,d[i].lng));
		}
		var zoom=Math.min((this.map.getBoundsZoomLevel(bounds)-1),14);
		this.map.setCenter(this.findCenter(bounds), zoom);

		for(var i=0; i<this.data.length; i++){
			this.plot(this.data[i]);
		}
	},

	initialize:function(/* object */args, /* object */frag){
		//	summary
		//	initializes the widget
		if(this.datasrc){
			this.parse(dojo.byId(this.datasrc));
		}
		else if(this.domNode.getElementsByTagName("table")[0]){
			this.parse(this.domNode.getElementsByTagName("table")[0]);
		}
	},
	postCreate:function(){
		//	summary
		//	Sets up and renders the widget.

		//	clean the domNode before creating the map.
		while(this.domNode.childNodes.length>0){
			this.domNode.removeChild(this.domNode.childNodes[0]);
		}
		if(this.domNode.style.position!="absolute"){
			this.domNode.style.position="relative";
		}
		this.map=new GMap2(this.domNode);
		try{
			this.geocoder=new GClientGeocoder();
		}catch(ex){}
		this.render();
		this.setControls();
	}
});
