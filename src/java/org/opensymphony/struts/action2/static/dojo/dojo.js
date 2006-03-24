/*
	Copyright (c) 2004-2005, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

/*
	This is a compiled version of Dojo, built for deployment and not for
	development. To get an editable version, please visit:

		http://dojotoolkit.org

	for documentation and information on getting the source.
*/

var dj_global=this;
function dj_undef(_1,_2){
if(!_2){
_2=dj_global;
}
return (typeof _2[_1]=="undefined");
}
if(dj_undef("djConfig")){
var djConfig={};
}
var dojo;
if(dj_undef("dojo")){
dojo={};
}
dojo.version={major:0,minor:2,patch:1,flag:"",revision:Number("$Rev: 2555 $".match(/[0-9]+/)[0]),toString:function(){
with(dojo.version){
return major+"."+minor+"."+patch+flag+" ("+revision+")";
}
}};
dojo.evalObjPath=function(_3,_4){
if(typeof _3!="string"){
return dj_global;
}
if(_3.indexOf(".")==-1){
if((dj_undef(_3,dj_global))&&(_4)){
dj_global[_3]={};
}
return dj_global[_3];
}
var _5=_3.split(/\./);
var _6=dj_global;
for(var i=0;i<_5.length;++i){
if(!_4){
_6=_6[_5[i]];
if((typeof _6=="undefined")||(!_6)){
return _6;
}
}else{
if(dj_undef(_5[i],_6)){
_6[_5[i]]={};
}
_6=_6[_5[i]];
}
}
return _6;
};
dojo.errorToString=function(_8){
return ((!dj_undef("message",_8))?_8.message:(dj_undef("description",_8)?_8:_8.description));
};
dojo.raise=function(_9,_a){
if(_a){
_9=_9+": "+dojo.errorToString(_a);
}
var he=dojo.hostenv;
if((!dj_undef("hostenv",dojo))&&(!dj_undef("println",dojo.hostenv))){
dojo.hostenv.println("FATAL: "+_9);
}
throw Error(_9);
};
dj_throw=dj_rethrow=function(m,e){
dojo.deprecated("dj_throw and dj_rethrow deprecated, use dojo.raise instead");
dojo.raise(m,e);
};
dojo.debug=function(){
if(!djConfig.isDebug){
return;
}
var _e=arguments;
if(dj_undef("println",dojo.hostenv)){
dojo.raise("dojo.debug not available (yet?)");
}
var _f=dj_global["jum"]&&!dj_global["jum"].isBrowser;
var s=[(_f?"":"DEBUG: ")];
for(var i=0;i<_e.length;++i){
if(!false&&_e[i] instanceof Error){
var msg="["+_e[i].name+": "+dojo.errorToString(_e[i])+(_e[i].fileName?", file: "+_e[i].fileName:"")+(_e[i].lineNumber?", line: "+_e[i].lineNumber:"")+"]";
}else{
try{
var msg=String(_e[i]);
}
catch(e){
if(dojo.render.html.ie){
var msg="[ActiveXObject]";
}else{
var msg="[unknown]";
}
}
}
s.push(msg);
}
if(_f){
jum.debug(s.join(" "));
}else{
dojo.hostenv.println(s.join(" "));
}
};
dojo.debugShallow=function(obj){
if(!djConfig.isDebug){
return;
}
dojo.debug("------------------------------------------------------------");
dojo.debug("Object: "+obj);
for(i in obj){
dojo.debug(i+": "+obj[i]);
}
dojo.debug("------------------------------------------------------------");
};
var dj_debug=dojo.debug;
function dj_eval(s){
return dj_global.eval?dj_global.eval(s):eval(s);
}
dj_unimplemented=dojo.unimplemented=function(_15,_16){
var _17="'"+_15+"' not implemented";
if((!dj_undef(_16))&&(_16)){
_17+=" "+_16;
}
dojo.raise(_17);
};
dj_deprecated=dojo.deprecated=function(_18,_19,_1a){
var _1b="DEPRECATED: "+_18;
if(_19){
_1b+=" "+_19;
}
if(_1a){
_1b+=" -- will be removed in version: "+_1a;
}
dojo.debug(_1b);
};
dojo.inherits=function(_1c,_1d){
if(typeof _1d!="function"){
dojo.raise("superclass: "+_1d+" borken");
}
_1c.prototype=new _1d();
_1c.prototype.constructor=_1c;
_1c.superclass=_1d.prototype;
_1c["super"]=_1d.prototype;
};
dj_inherits=function(_1e,_1f){
dojo.deprecated("dj_inherits deprecated, use dojo.inherits instead");
dojo.inherits(_1e,_1f);
};
dojo.render=(function(){
function vscaffold(_20,_21){
var tmp={capable:false,support:{builtin:false,plugin:false},prefixes:_20};
for(var x in _21){
tmp[x]=false;
}
return tmp;
}
return {name:"",ver:dojo.version,os:{win:false,linux:false,osx:false},html:vscaffold(["html"],["ie","opera","khtml","safari","moz"]),svg:vscaffold(["svg"],["corel","adobe","batik"]),vml:vscaffold(["vml"],["ie"]),swf:vscaffold(["Swf","Flash","Mm"],["mm"]),swt:vscaffold(["Swt"],["ibm"])};
})();
dojo.hostenv=(function(){
var _24={isDebug:false,allowQueryConfig:false,baseScriptUri:"",baseRelativePath:"",libraryScriptUri:"",iePreventClobber:false,ieClobberMinimal:true,preventBackButtonFix:true,searchIds:[],parseWidgets:true};
if(typeof djConfig=="undefined"){
djConfig=_24;
}else{
for(var _25 in _24){
if(typeof djConfig[_25]=="undefined"){
djConfig[_25]=_24[_25];
}
}
}
var djc=djConfig;
function _def(obj,_28,def){
return (dj_undef(_28,obj)?def:obj[_28]);
}
return {name_:"(unset)",version_:"(unset)",pkgFileName:"__package__",loading_modules_:{},loaded_modules_:{},addedToLoadingCount:[],removedFromLoadingCount:[],inFlightCount:0,modulePrefixes_:{dojo:{name:"dojo",value:"src"}},setModulePrefix:function(_2a,_2b){
this.modulePrefixes_[_2a]={name:_2a,value:_2b};
},getModulePrefix:function(_2c){
var mp=this.modulePrefixes_;
if((mp[_2c])&&(mp[_2c]["name"])){
return mp[_2c].value;
}
return _2c;
},getTextStack:[],loadUriStack:[],loadedUris:[],post_load_:false,modulesLoadedListeners:[],getName:function(){
return this.name_;
},getVersion:function(){
return this.version_;
},getText:function(uri){
dojo.unimplemented("getText","uri="+uri);
},getLibraryScriptUri:function(){
dojo.unimplemented("getLibraryScriptUri","");
}};
})();
dojo.hostenv.getBaseScriptUri=function(){
if(djConfig.baseScriptUri.length){
return djConfig.baseScriptUri;
}
var uri=new String(djConfig.libraryScriptUri||djConfig.baseRelativePath);
if(!uri){
dojo.raise("Nothing returned by getLibraryScriptUri(): "+uri);
}
var _30=uri.lastIndexOf("/");
djConfig.baseScriptUri=djConfig.baseRelativePath;
return djConfig.baseScriptUri;
};
dojo.hostenv.setBaseScriptUri=function(uri){
djConfig.baseScriptUri=uri;
};
dojo.hostenv.loadPath=function(_32,_33,cb){
if((_32.charAt(0)=="/")||(_32.match(/^\w+:/))){
dojo.raise("relpath '"+_32+"'; must be relative");
}
var uri=this.getBaseScriptUri()+_32;
if(djConfig.cacheBust&&dojo.render.html.capable){
uri+="?"+djConfig.cacheBust.replace(/\W+/g,"");
}
try{
return ((!_33)?this.loadUri(uri,cb):this.loadUriAndCheck(uri,_33,cb));
}
catch(e){
dojo.debug(e);
return false;
}
};
dojo.hostenv.loadUri=function(uri,cb){
if(dojo.hostenv.loadedUris[uri]){
return;
}
var _38=this.getText(uri,null,true);
if(_38==null){
return 0;
}
var _39=dj_eval(_38);
return 1;
};
dojo.hostenv.loadUriAndCheck=function(uri,_3b,cb){
var ok=true;
try{
ok=this.loadUri(uri,cb);
}
catch(e){
dojo.debug("failed loading ",uri," with error: ",e);
}
return ((ok)&&(this.findModule(_3b,false)))?true:false;
};
dojo.loaded=function(){
};
dojo.hostenv.loaded=function(){
this.post_load_=true;
var mll=this.modulesLoadedListeners;
for(var x=0;x<mll.length;x++){
mll[x]();
}
dojo.loaded();
};
dojo.addOnLoad=function(obj,_41){
if(arguments.length==1){
dojo.hostenv.modulesLoadedListeners.push(obj);
}else{
if(arguments.length>1){
dojo.hostenv.modulesLoadedListeners.push(function(){
obj[_41]();
});
}
}
};
dojo.hostenv.modulesLoaded=function(){
if(this.post_load_){
return;
}
if((this.loadUriStack.length==0)&&(this.getTextStack.length==0)){
if(this.inFlightCount>0){
dojo.debug("files still in flight!");
return;
}
if(typeof setTimeout=="object"){
setTimeout("dojo.hostenv.loaded();",0);
}else{
dojo.hostenv.loaded();
}
}
};
dojo.hostenv.moduleLoaded=function(_42){
var _43=dojo.evalObjPath((_42.split(".").slice(0,-1)).join("."));
this.loaded_modules_[(new String(_42)).toLowerCase()]=_43;
};
dojo.hostenv._global_omit_module_check=false;
dojo.hostenv.loadModule=function(_44,_45,_46){
_46=this._global_omit_module_check||_46;
var _47=this.findModule(_44,false);
if(_47){
return _47;
}
if(dj_undef(_44,this.loading_modules_)){
this.addedToLoadingCount.push(_44);
}
this.loading_modules_[_44]=1;
var _48=_44.replace(/\./g,"/")+".js";
var _49=_44.split(".");
var _4a=_44.split(".");
for(var i=_49.length-1;i>0;i--){
var _4c=_49.slice(0,i).join(".");
var _4d=this.getModulePrefix(_4c);
if(_4d!=_4c){
_49.splice(0,i,_4d);
break;
}
}
var _4e=_49[_49.length-1];
if(_4e=="*"){
_44=(_4a.slice(0,-1)).join(".");
while(_49.length){
_49.pop();
_49.push(this.pkgFileName);
_48=_49.join("/")+".js";
if(_48.charAt(0)=="/"){
_48=_48.slice(1);
}
ok=this.loadPath(_48,((!_46)?_44:null));
if(ok){
break;
}
_49.pop();
}
}else{
_48=_49.join("/")+".js";
_44=_4a.join(".");
var ok=this.loadPath(_48,((!_46)?_44:null));
if((!ok)&&(!_45)){
_49.pop();
while(_49.length){
_48=_49.join("/")+".js";
ok=this.loadPath(_48,((!_46)?_44:null));
if(ok){
break;
}
_49.pop();
_48=_49.join("/")+"/"+this.pkgFileName+".js";
if(_48.charAt(0)=="/"){
_48=_48.slice(1);
}
ok=this.loadPath(_48,((!_46)?_44:null));
if(ok){
break;
}
}
}
if((!ok)&&(!_46)){
dojo.raise("Could not load '"+_44+"'; last tried '"+_48+"'");
}
}
if(!_46){
_47=this.findModule(_44,false);
if(!_47){
dojo.raise("symbol '"+_44+"' is not defined after loading '"+_48+"'");
}
}
return _47;
};
dojo.hostenv.startPackage=function(_50){
var _51=_50.split(/\./);
if(_51[_51.length-1]=="*"){
_51.pop();
}
return dojo.evalObjPath(_51.join("."),true);
};
dojo.hostenv.findModule=function(_52,_53){
if(this.loaded_modules_[(new String(_52)).toLowerCase()]){
return this.loaded_modules_[_52];
}
var _54=dojo.evalObjPath(_52);
if((typeof _54!=="undefined")&&(_54)){
return _54;
}
if(_53){
dojo.raise("no loaded module named '"+_52+"'");
}
return null;
};
if(typeof window=="undefined"){
dojo.raise("no window object");
}
(function(){
if(djConfig.allowQueryConfig){
var _55=document.location.toString();
var _56=_55.split("?",2);
if(_56.length>1){
var _57=_56[1];
var _58=_57.split("&");
for(var x in _58){
var sp=_58[x].split("=");
if((sp[0].length>9)&&(sp[0].substr(0,9)=="djConfig.")){
var opt=sp[0].substr(9);
try{
djConfig[opt]=eval(sp[1]);
}
catch(e){
djConfig[opt]=sp[1];
}
}
}
}
}
if(((djConfig["baseScriptUri"]=="")||(djConfig["baseRelativePath"]==""))&&(document&&document.getElementsByTagName)){
var _5c=document.getElementsByTagName("script");
var _5d=/(__package__|dojo)\.js(\?|$)/i;
for(var i=0;i<_5c.length;i++){
var src=_5c[i].getAttribute("src");
if(!src){
continue;
}
var m=src.match(_5d);
if(m){
root=src.substring(0,m.index);
if(!this["djConfig"]){
djConfig={};
}
if(djConfig["baseScriptUri"]==""){
djConfig["baseScriptUri"]=root;
}
if(djConfig["baseRelativePath"]==""){
djConfig["baseRelativePath"]=root;
}
break;
}
}
}
var dr=dojo.render;
var drh=dojo.render.html;
var dua=drh.UA=navigator.userAgent;
var dav=drh.AV=navigator.appVersion;
var t=true;
var f=false;
drh.capable=t;
drh.support.builtin=t;
dr.ver=parseFloat(drh.AV);
dr.os.mac=dav.indexOf("Macintosh")>=0;
dr.os.win=dav.indexOf("Windows")>=0;
dr.os.linux=dav.indexOf("X11")>=0;
drh.opera=dua.indexOf("Opera")>=0;
drh.khtml=(dav.indexOf("Konqueror")>=0)||(dav.indexOf("Safari")>=0);
drh.safari=dav.indexOf("Safari")>=0;
var _67=dua.indexOf("Gecko");
drh.mozilla=drh.moz=(_67>=0)&&(!drh.khtml);
if(drh.mozilla){
drh.geckoVersion=dua.substring(_67+6,_67+14);
}
drh.ie=(document.all)&&(!drh.opera);
drh.ie50=drh.ie&&dav.indexOf("MSIE 5.0")>=0;
drh.ie55=drh.ie&&dav.indexOf("MSIE 5.5")>=0;
drh.ie60=drh.ie&&dav.indexOf("MSIE 6.0")>=0;
dr.vml.capable=drh.ie;
dr.svg.capable=f;
dr.svg.support.plugin=f;
dr.svg.support.builtin=f;
dr.svg.adobe=f;
if(document.implementation&&document.implementation.hasFeature&&document.implementation.hasFeature("org.w3c.dom.svg","1.0")){
dr.svg.capable=t;
dr.svg.support.builtin=t;
dr.svg.support.plugin=f;
dr.svg.adobe=f;
}else{
if(navigator.mimeTypes&&navigator.mimeTypes.length>0){
var _68=navigator.mimeTypes["image/svg+xml"]||navigator.mimeTypes["image/svg"]||navigator.mimeTypes["image/svg-xml"];
if(_68){
dr.svg.adobe=_68&&_68.enabledPlugin&&_68.enabledPlugin.description&&(_68.enabledPlugin.description.indexOf("Adobe")>-1);
if(dr.svg.adobe){
dr.svg.capable=t;
dr.svg.support.plugin=t;
}
}
}else{
if(drh.ie&&dr.os.win){
var _68=f;
try{
var _69=new ActiveXObject("Adobe.SVGCtl");
_68=t;
}
catch(e){
}
if(_68){
dr.svg.capable=t;
dr.svg.support.plugin=t;
dr.svg.adobe=t;
}
}else{
dr.svg.capable=f;
dr.svg.support.plugin=f;
dr.svg.adobe=f;
}
}
}
})();
dojo.hostenv.startPackage("dojo.hostenv");
dojo.hostenv.name_="browser";
dojo.hostenv.searchIds=[];
var DJ_XMLHTTP_PROGIDS=["Msxml2.XMLHTTP","Microsoft.XMLHTTP","Msxml2.XMLHTTP.4.0"];
dojo.hostenv.getXmlhttpObject=function(){
var _6a=null;
var _6b=null;
try{
_6a=new XMLHttpRequest();
}
catch(e){
}
if(!_6a){
for(var i=0;i<3;++i){
var _6d=DJ_XMLHTTP_PROGIDS[i];
try{
_6a=new ActiveXObject(_6d);
}
catch(e){
_6b=e;
}
if(_6a){
DJ_XMLHTTP_PROGIDS=[_6d];
break;
}
}
}
if(!_6a){
return dojo.raise("XMLHTTP not available",_6b);
}
return _6a;
};
dojo.hostenv.getText=function(uri,_6f,_70){
var _71=this.getXmlhttpObject();
if(_6f){
_71.onreadystatechange=function(){
if((4==_71.readyState)&&(_71["status"])){
if(_71.status==200){
dojo.debug("LOADED URI: "+uri);
_6f(_71.responseText);
}
}
};
}
_71.open("GET",uri,_6f?true:false);
_71.send(null);
if(_6f){
return null;
}
return _71.responseText;
};
dojo.hostenv.defaultDebugContainerId="dojoDebug";
dojo.hostenv._println_buffer=[];
dojo.hostenv._println_safe=false;
dojo.hostenv.println=function(_72){
if(!dojo.hostenv._println_safe){
dojo.hostenv._println_buffer.push(_72);
}else{
try{
var _73=document.getElementById(djConfig.debugContainerId?djConfig.debugContainerId:dojo.hostenv.defaultDebugContainerId);
if(!_73){
_73=document.getElementsByTagName("body")[0]||document.body;
}
var div=document.createElement("div");
div.appendChild(document.createTextNode(_72));
_73.appendChild(div);
}
catch(e){
try{
document.write("<div>"+_72+"</div>");
}
catch(e2){
window.status=_72;
}
}
}
};
dojo.addOnLoad(function(){
dojo.hostenv._println_safe=true;
while(dojo.hostenv._println_buffer.length>0){
dojo.hostenv.println(dojo.hostenv._println_buffer.shift());
}
});
function dj_addNodeEvtHdlr(_75,_76,fp,_78){
var _79=_75["on"+_76]||function(){
};
_75["on"+_76]=function(){
fp.apply(_75,arguments);
_79.apply(_75,arguments);
};
return true;
}
dj_addNodeEvtHdlr(window,"load",function(){
if(dojo.render.html.ie){
dojo.hostenv.makeWidgets();
}
dojo.hostenv.modulesLoaded();
});
dojo.hostenv.makeWidgets=function(){
var _7a=[];
if(djConfig.searchIds&&djConfig.searchIds.length>0){
_7a=_7a.concat(djConfig.searchIds);
}
if(dojo.hostenv.searchIds&&dojo.hostenv.searchIds.length>0){
_7a=_7a.concat(dojo.hostenv.searchIds);
}
if((djConfig.parseWidgets)||(_7a.length>0)){
if(dojo.evalObjPath("dojo.widget.Parse")){
try{
var _7b=new dojo.xml.Parse();
if(_7a.length>0){
for(var x=0;x<_7a.length;x++){
var _7d=document.getElementById(_7a[x]);
if(!_7d){
continue;
}
var _7e=_7b.parseElement(_7d,null,true);
dojo.widget.getParser().createComponents(_7e);
}
}else{
if(djConfig.parseWidgets){
var _7e=_7b.parseElement(document.getElementsByTagName("body")[0]||document.body,null,true);
dojo.widget.getParser().createComponents(_7e);
}
}
}
catch(e){
dojo.debug("auto-build-widgets error:",e);
}
}
}
};
dojo.hostenv.modulesLoadedListeners.push(function(){
if(!dojo.render.html.ie){
dojo.hostenv.makeWidgets();
}
});
try{
if(!window["djConfig"]||!window.djConfig["preventBackButtonFix"]){
document.write("<iframe style='border: 0px; width: 1px; height: 1px; position: absolute; bottom: 0px; right: 0px; visibility: visible;' name='djhistory' id='djhistory' src='"+(dojo.hostenv.getBaseScriptUri()+"iframe_history.html")+"'></iframe>");
}
if(dojo.render.html.ie){
document.write("<style>v:*{ behavior:url(#default#VML); }</style>");
document.write("<xml:namespace ns=\"urn:schemas-microsoft-com:vml\" prefix=\"v\"/>");
}
}
catch(e){
}
dojo.hostenv.writeIncludes=function(){
};
dojo.hostenv.byId=dojo.byId=function(id,doc){
if(typeof id=="string"||id instanceof String){
if(!doc){
doc=document;
}
return doc.getElementById(id);
}
return id;
};
dojo.hostenv.byIdArray=dojo.byIdArray=function(){
var ids=[];
for(var i=0;i<arguments.length;i++){
if((arguments[i] instanceof Array)||(typeof arguments[i]=="array")){
for(var j=0;j<arguments[i].length;j++){
ids=ids.concat(dojo.hostenv.byIdArray(arguments[i][j]));
}
}else{
ids.push(dojo.hostenv.byId(arguments[i]));
}
}
return ids;
};
dojo.hostenv.conditionalLoadModule=function(_84){
var _85=_84["common"]||[];
var _86=(_84[dojo.hostenv.name_])?_85.concat(_84[dojo.hostenv.name_]||[]):_85.concat(_84["default"]||[]);
for(var x=0;x<_86.length;x++){
var _88=_86[x];
if(_88.constructor==Array){
dojo.hostenv.loadModule.apply(dojo.hostenv,_88);
}else{
dojo.hostenv.loadModule(_88);
}
}
};
dojo.hostenv.require=dojo.hostenv.loadModule;
dojo.require=function(){
dojo.hostenv.loadModule.apply(dojo.hostenv,arguments);
};
dojo.requireAfter=dojo.require;
dojo.requireIf=function(){
if((arguments[0]===true)||(arguments[0]=="common")||(dojo.render[arguments[0]].capable)){
var _89=[];
for(var i=1;i<arguments.length;i++){
_89.push(arguments[i]);
}
dojo.require.apply(dojo,_89);
}
};
dojo.requireAfterIf=dojo.requireIf;
dojo.conditionalRequire=dojo.requireIf;
dojo.kwCompoundRequire=function(){
dojo.hostenv.conditionalLoadModule.apply(dojo.hostenv,arguments);
};
dojo.hostenv.provide=dojo.hostenv.startPackage;
dojo.provide=function(){
return dojo.hostenv.startPackage.apply(dojo.hostenv,arguments);
};
dojo.setModulePrefix=function(_8b,_8c){
return dojo.hostenv.setModulePrefix(_8b,_8c);
};
dojo.profile={start:function(){
},end:function(){
},dump:function(){
}};
dojo.exists=function(obj,_8e){
var p=_8e.split(".");
for(var i=0;i<p.length;i++){
if(!(obj[p[i]])){
return false;
}
obj=obj[p[i]];
}
return true;
};
dojo.provide("dojo.lang");
dojo.provide("dojo.AdapterRegistry");
dojo.provide("dojo.lang.Lang");
dojo.lang.mixin=function(obj,_92,_93){
if(typeof _93!="object"){
_93={};
}
for(var x in _92){
if(typeof _93[x]=="undefined"||_93[x]!=_92[x]){
obj[x]=_92[x];
}
}
return obj;
};
dojo.lang.extend=function(_95,_96){
this.mixin(_95.prototype,_96);
};
dojo.lang.extendPrototype=function(obj,_98){
this.extend(obj.constructor,_98);
};
dojo.lang.anonCtr=0;
dojo.lang.anon={};
dojo.lang.nameAnonFunc=function(_99,_9a){
var nso=(_9a||dojo.lang.anon);
if((dj_global["djConfig"])&&(djConfig["slowAnonFuncLookups"]==true)){
for(var x in nso){
if(nso[x]===_99){
return x;
}
}
}
var ret="__"+dojo.lang.anonCtr++;
while(typeof nso[ret]!="undefined"){
ret="__"+dojo.lang.anonCtr++;
}
nso[ret]=_99;
return ret;
};
dojo.lang.hitch=function(_9e,_9f){
if(dojo.lang.isString(_9f)){
var fcn=_9e[_9f];
}else{
var fcn=_9f;
}
return function(){
return fcn.apply(_9e,arguments);
};
};
dojo.lang.setTimeout=function(_a1,_a2){
var _a3=window,argsStart=2;
if(!dojo.lang.isFunction(_a1)){
_a3=_a1;
_a1=_a2;
_a2=arguments[2];
argsStart++;
}
if(dojo.lang.isString(_a1)){
_a1=_a3[_a1];
}
var _a4=[];
for(var i=argsStart;i<arguments.length;i++){
_a4.push(arguments[i]);
}
return setTimeout(function(){
_a1.apply(_a3,_a4);
},_a2);
};
dojo.lang.isObject=function(wh){
return typeof wh=="object"||dojo.lang.isArray(wh)||dojo.lang.isFunction(wh);
};
dojo.lang.isArray=function(wh){
return (wh instanceof Array||typeof wh=="array");
};
dojo.lang.isArrayLike=function(wh){
if(dojo.lang.isString(wh)){
return false;
}
if(dojo.lang.isArray(wh)){
return true;
}
if(dojo.lang.isNumber(wh.length)&&isFinite(wh)){
return true;
}
return false;
};
dojo.lang.isFunction=function(wh){
return (wh instanceof Function||typeof wh=="function");
};
dojo.lang.isString=function(wh){
return (wh instanceof String||typeof wh=="string");
};
dojo.lang.isAlien=function(wh){
return !dojo.lang.isFunction()&&/\{\s*\[native code\]\s*\}/.test(String(wh));
};
dojo.lang.isBoolean=function(wh){
return (wh instanceof Boolean||typeof wh=="boolean");
};
dojo.lang.isNumber=function(wh){
return (wh instanceof Number||typeof wh=="number");
};
dojo.lang.isUndefined=function(wh){
return ((wh==undefined)&&(typeof wh=="undefined"));
};
dojo.lang.whatAmI=function(wh){
try{
if(dojo.lang.isArray(wh)){
return "array";
}
if(dojo.lang.isFunction(wh)){
return "function";
}
if(dojo.lang.isString(wh)){
return "string";
}
if(dojo.lang.isNumber(wh)){
return "number";
}
if(dojo.lang.isBoolean(wh)){
return "boolean";
}
if(dojo.lang.isAlien(wh)){
return "alien";
}
if(dojo.lang.isUndefined(wh)){
return "undefined";
}
for(var _b0 in dojo.lang.whatAmI.custom){
if(dojo.lang.whatAmI.custom[_b0](wh)){
return _b0;
}
}
if(dojo.lang.isObject(wh)){
return "object";
}
}
catch(E){
}
return "unknown";
};
dojo.lang.whatAmI.custom={};
dojo.lang.find=function(arr,val,_b3){
if(!dojo.lang.isArray(arr)&&dojo.lang.isArray(val)){
var a=arr;
arr=val;
val=a;
}
var _b5=dojo.lang.isString(arr);
if(_b5){
arr=arr.split("");
}
if(_b3){
for(var i=0;i<arr.length;++i){
if(arr[i]===val){
return i;
}
}
}else{
for(var i=0;i<arr.length;++i){
if(arr[i]==val){
return i;
}
}
}
return -1;
};
dojo.lang.indexOf=dojo.lang.find;
dojo.lang.findLast=function(arr,val,_b9){
if(!dojo.lang.isArray(arr)&&dojo.lang.isArray(val)){
var a=arr;
arr=val;
val=a;
}
var _bb=dojo.lang.isString(arr);
if(_bb){
arr=arr.split("");
}
if(_b9){
for(var i=arr.length-1;i>=0;i--){
if(arr[i]===val){
return i;
}
}
}else{
for(var i=arr.length-1;i>=0;i--){
if(arr[i]==val){
return i;
}
}
}
return -1;
};
dojo.lang.lastIndexOf=dojo.lang.findLast;
dojo.lang.inArray=function(arr,val){
return dojo.lang.find(arr,val)>-1;
};
dojo.lang.getNameInObj=function(ns,_c0){
if(!ns){
ns=dj_global;
}
for(var x in ns){
if(ns[x]===_c0){
return new String(x);
}
}
return null;
};
dojo.lang.has=function(obj,_c3){
return (typeof obj[_c3]!=="undefined");
};
dojo.lang.isEmpty=function(obj){
if(dojo.lang.isObject(obj)){
var tmp={};
var _c6=0;
for(var x in obj){
if(obj[x]&&(!tmp[x])){
_c6++;
break;
}
}
return (_c6==0);
}else{
if(dojo.lang.isArray(obj)||dojo.lang.isString(obj)){
return obj.length==0;
}
}
};
dojo.lang.forEach=function(arr,_c9,_ca){
var _cb=dojo.lang.isString(arr);
if(_cb){
arr=arr.split("");
}
var il=arr.length;
for(var i=0;i<((_ca)?il:arr.length);i++){
if(_c9(arr[i],i,arr)=="break"){
break;
}
}
};
dojo.lang.map=function(arr,obj,_d0){
var _d1=dojo.lang.isString(arr);
if(_d1){
arr=arr.split("");
}
if(dojo.lang.isFunction(obj)&&(!_d0)){
_d0=obj;
obj=dj_global;
}else{
if(dojo.lang.isFunction(obj)&&_d0){
var _d2=obj;
obj=_d0;
_d0=_d2;
}
}
if(Array.map){
var _d3=Array.map(arr,_d0,obj);
}else{
var _d3=[];
for(var i=0;i<arr.length;++i){
_d3.push(_d0.call(obj,arr[i]));
}
}
if(_d1){
return _d3.join("");
}else{
return _d3;
}
};
dojo.lang.tryThese=function(){
for(var x=0;x<arguments.length;x++){
try{
if(typeof arguments[x]=="function"){
var ret=(arguments[x]());
if(ret){
return ret;
}
}
}
catch(e){
dojo.debug(e);
}
}
};
dojo.lang.delayThese=function(_d7,cb,_d9,_da){
if(!_d7.length){
if(typeof _da=="function"){
_da();
}
return;
}
if((typeof _d9=="undefined")&&(typeof cb=="number")){
_d9=cb;
cb=function(){
};
}else{
if(!cb){
cb=function(){
};
if(!_d9){
_d9=0;
}
}
}
setTimeout(function(){
(_d7.shift())();
cb();
dojo.lang.delayThese(_d7,cb,_d9,_da);
},_d9);
};
dojo.lang.shallowCopy=function(obj){
var ret={},key;
for(key in obj){
if(dojo.lang.isUndefined(ret[key])){
ret[key]=obj[key];
}
}
return ret;
};
dojo.lang.every=function(arr,_de,_df){
var _e0=dojo.lang.isString(arr);
if(_e0){
arr=arr.split("");
}
if(Array.every){
return Array.every(arr,_de,_df);
}else{
if(!_df){
if(arguments.length>=3){
dojo.raise("thisObject doesn't exist!");
}
_df=dj_global;
}
for(var i=0;i<arr.length;i++){
if(!_de.call(_df,arr[i],i,arr)){
return false;
}
}
return true;
}
};
dojo.lang.some=function(arr,_e3,_e4){
var _e5=dojo.lang.isString(arr);
if(_e5){
arr=arr.split("");
}
if(Array.some){
return Array.some(arr,_e3,_e4);
}else{
if(!_e4){
if(arguments.length>=3){
dojo.raise("thisObject doesn't exist!");
}
_e4=dj_global;
}
for(var i=0;i<arr.length;i++){
if(_e3.call(_e4,arr[i],i,arr)){
return true;
}
}
return false;
}
};
dojo.lang.filter=function(arr,_e8,_e9){
var _ea=dojo.lang.isString(arr);
if(_ea){
arr=arr.split("");
}
if(Array.filter){
var _eb=Array.filter(arr,_e8,_e9);
}else{
if(!_e9){
if(arguments.length>=3){
dojo.raise("thisObject doesn't exist!");
}
_e9=dj_global;
}
var _eb=[];
for(var i=0;i<arr.length;i++){
if(_e8.call(_e9,arr[i],i,arr)){
_eb.push(arr[i]);
}
}
}
if(_ea){
return _eb.join("");
}else{
return _eb;
}
};
dojo.AdapterRegistry=function(){
this.pairs=[];
};
dojo.lang.extend(dojo.AdapterRegistry,{register:function(_ed,_ee,_ef,_f0){
if(_f0){
this.pairs.unshift([_ed,_ee,_ef]);
}else{
this.pairs.push([_ed,_ee,_ef]);
}
},match:function(){
for(var i=0;i<this.pairs.length;i++){
var _f2=this.pairs[i];
if(_f2[1].apply(this,arguments)){
return _f2[2].apply(this,arguments);
}
}
dojo.raise("No match found");
},unregister:function(_f3){
for(var i=0;i<this.pairs.length;i++){
var _f5=this.pairs[i];
if(_f5[0]==_f3){
this.pairs.splice(i,1);
return true;
}
}
return false;
}});
dojo.lang.reprRegistry=new dojo.AdapterRegistry();
dojo.lang.registerRepr=function(_f6,_f7,_f8,_f9){
dojo.lang.reprRegistry.register(_f6,_f7,_f8,_f9);
};
dojo.lang.repr=function(obj){
if(typeof (obj)=="undefined"){
return "undefined";
}else{
if(obj===null){
return "null";
}
}
try{
if(typeof (obj["__repr__"])=="function"){
return obj["__repr__"]();
}else{
if((typeof (obj["repr"])=="function")&&(obj.repr!=arguments.callee)){
return obj["repr"]();
}
}
return dojo.lang.reprRegistry.match(obj);
}
catch(e){
if(typeof (obj.NAME)=="string"&&(obj.toString==Function.prototype.toString||obj.toString==Object.prototype.toString)){
return o.NAME;
}
}
if(typeof (obj)=="function"){
obj=(obj+"").replace(/^\s+/,"");
var idx=obj.indexOf("{");
if(idx!=-1){
obj=obj.substr(0,idx)+"{...}";
}
}
return obj+"";
};
dojo.lang.reprArrayLike=function(arr){
try{
var na=dojo.lang.map(arr,dojo.lang.repr);
return "["+na.join(", ")+"]";
}
catch(e){
}
};
dojo.lang.reprString=function(str){
return ("\""+str.replace(/(["\\])/g,"\\$1")+"\"").replace(/[\f]/g,"\\f").replace(/[\b]/g,"\\b").replace(/[\n]/g,"\\n").replace(/[\t]/g,"\\t").replace(/[\r]/g,"\\r");
};
dojo.lang.reprNumber=function(num){
return num+"";
};
(function(){
var m=dojo.lang;
m.registerRepr("arrayLike",m.isArrayLike,m.reprArrayLike);
m.registerRepr("string",m.isString,m.reprString);
m.registerRepr("numbers",m.isNumber,m.reprNumber);
m.registerRepr("boolean",m.isBoolean,m.reprNumber);
})();
dojo.lang.unnest=function(){
var out=[];
for(var i=0;i<arguments.length;i++){
if(dojo.lang.isArrayLike(arguments[i])){
var add=dojo.lang.unnest.apply(this,arguments[i]);
out=out.concat(add);
}else{
out.push(arguments[i]);
}
}
return out;
};
dojo.provide("dojo.dom");
dojo.require("dojo.lang");
dojo.dom.ELEMENT_NODE=1;
dojo.dom.ATTRIBUTE_NODE=2;
dojo.dom.TEXT_NODE=3;
dojo.dom.CDATA_SECTION_NODE=4;
dojo.dom.ENTITY_REFERENCE_NODE=5;
dojo.dom.ENTITY_NODE=6;
dojo.dom.PROCESSING_INSTRUCTION_NODE=7;
dojo.dom.COMMENT_NODE=8;
dojo.dom.DOCUMENT_NODE=9;
dojo.dom.DOCUMENT_TYPE_NODE=10;
dojo.dom.DOCUMENT_FRAGMENT_NODE=11;
dojo.dom.NOTATION_NODE=12;
dojo.dom.dojoml="http://www.dojotoolkit.org/2004/dojoml";
dojo.dom.xmlns={svg:"http://www.w3.org/2000/svg",smil:"http://www.w3.org/2001/SMIL20/",mml:"http://www.w3.org/1998/Math/MathML",cml:"http://www.xml-cml.org",xlink:"http://www.w3.org/1999/xlink",xhtml:"http://www.w3.org/1999/xhtml",xul:"http://www.mozilla.org/keymaster/gatekeeper/there.is.only.xul",xbl:"http://www.mozilla.org/xbl",fo:"http://www.w3.org/1999/XSL/Format",xsl:"http://www.w3.org/1999/XSL/Transform",xslt:"http://www.w3.org/1999/XSL/Transform",xi:"http://www.w3.org/2001/XInclude",xforms:"http://www.w3.org/2002/01/xforms",saxon:"http://icl.com/saxon",xalan:"http://xml.apache.org/xslt",xsd:"http://www.w3.org/2001/XMLSchema",dt:"http://www.w3.org/2001/XMLSchema-datatypes",xsi:"http://www.w3.org/2001/XMLSchema-instance",rdf:"http://www.w3.org/1999/02/22-rdf-syntax-ns#",rdfs:"http://www.w3.org/2000/01/rdf-schema#",dc:"http://purl.org/dc/elements/1.1/",dcq:"http://purl.org/dc/qualifiers/1.0","soap-env":"http://schemas.xmlsoap.org/soap/envelope/",wsdl:"http://schemas.xmlsoap.org/wsdl/",AdobeExtensions:"http://ns.adobe.com/AdobeSVGViewerExtensions/3.0/"};
dojo.dom.isNode=dojo.lang.isDomNode=function(wh){
if(typeof Element=="object"){
try{
return wh instanceof Element;
}
catch(E){
}
}else{
return wh&&!isNaN(wh.nodeType);
}
};
dojo.lang.whatAmI.custom["node"]=dojo.dom.isNode;
dojo.dom.getTagName=function(node){
var _106=node.tagName;
if(_106.substr(0,5).toLowerCase()!="dojo:"){
if(_106.substr(0,4).toLowerCase()=="dojo"){
return "dojo:"+_106.substring(4).toLowerCase();
}
var djt=node.getAttribute("dojoType")||node.getAttribute("dojotype");
if(djt){
return "dojo:"+djt.toLowerCase();
}
if((node.getAttributeNS)&&(node.getAttributeNS(this.dojoml,"type"))){
return "dojo:"+node.getAttributeNS(this.dojoml,"type").toLowerCase();
}
try{
djt=node.getAttribute("dojo:type");
}
catch(e){
}
if(djt){
return "dojo:"+djt.toLowerCase();
}
if((!dj_global["djConfig"])||(!djConfig["ignoreClassNames"])){
var _108=node.className||node.getAttribute("class");
if((_108)&&(_108.indexOf("dojo-")!=-1)){
var _109=_108.split(" ");
for(var x=0;x<_109.length;x++){
if((_109[x].length>5)&&(_109[x].indexOf("dojo-")>=0)){
return "dojo:"+_109[x].substr(5).toLowerCase();
}
}
}
}
}
return _106.toLowerCase();
};
dojo.dom.getUniqueId=function(){
do{
var id="dj_unique_"+(++arguments.callee._idIncrement);
}while(document.getElementById(id));
return id;
};
dojo.dom.getUniqueId._idIncrement=0;
dojo.dom.firstElement=dojo.dom.getFirstChildElement=function(_10c,_10d){
var node=_10c.firstChild;
while(node&&node.nodeType!=dojo.dom.ELEMENT_NODE){
node=node.nextSibling;
}
if(_10d&&node&&node.tagName&&node.tagName.toLowerCase()!=_10d.toLowerCase()){
node=dojo.dom.nextElement(node,_10d);
}
return node;
};
dojo.dom.lastElement=dojo.dom.getLastChildElement=function(_10f,_110){
var node=_10f.lastChild;
while(node&&node.nodeType!=dojo.dom.ELEMENT_NODE){
node=node.previousSibling;
}
if(_110&&node&&node.tagName&&node.tagName.toLowerCase()!=_110.toLowerCase()){
node=dojo.dom.prevElement(node,_110);
}
return node;
};
dojo.dom.nextElement=dojo.dom.getNextSiblingElement=function(node,_113){
if(!node){
return null;
}
do{
node=node.nextSibling;
}while(node&&node.nodeType!=dojo.dom.ELEMENT_NODE);
if(node&&_113&&_113.toLowerCase()!=node.tagName.toLowerCase()){
return dojo.dom.nextElement(node,_113);
}
return node;
};
dojo.dom.prevElement=dojo.dom.getPreviousSiblingElement=function(node,_115){
if(!node){
return null;
}
if(_115){
_115=_115.toLowerCase();
}
do{
node=node.previousSibling;
}while(node&&node.nodeType!=dojo.dom.ELEMENT_NODE);
if(node&&_115&&_115.toLowerCase()!=node.tagName.toLowerCase()){
return dojo.dom.prevElement(node,_115);
}
return node;
};
dojo.dom.moveChildren=function(_116,_117,trim){
var _119=0;
if(trim){
while(_116.hasChildNodes()&&_116.firstChild.nodeType==dojo.dom.TEXT_NODE){
_116.removeChild(_116.firstChild);
}
while(_116.hasChildNodes()&&_116.lastChild.nodeType==dojo.dom.TEXT_NODE){
_116.removeChild(_116.lastChild);
}
}
while(_116.hasChildNodes()){
_117.appendChild(_116.firstChild);
_119++;
}
return _119;
};
dojo.dom.copyChildren=function(_11a,_11b,trim){
var _11d=_11a.cloneNode(true);
return this.moveChildren(_11d,_11b,trim);
};
dojo.dom.removeChildren=function(node){
var _11f=node.childNodes.length;
while(node.hasChildNodes()){
node.removeChild(node.firstChild);
}
return _11f;
};
dojo.dom.replaceChildren=function(node,_121){
dojo.dom.removeChildren(node);
node.appendChild(_121);
};
dojo.dom.removeNode=function(node){
if(node&&node.parentNode){
return node.parentNode.removeChild(node);
}
};
dojo.dom.getAncestors=function(node,_124,_125){
var _126=[];
var _127=dojo.lang.isFunction(_124);
while(node){
if(!_127||_124(node)){
_126.push(node);
}
if(_125&&_126.length>0){
return _126[0];
}
node=node.parentNode;
}
if(_125){
return null;
}
return _126;
};
dojo.dom.getAncestorsByTag=function(node,tag,_12a){
tag=tag.toLowerCase();
return dojo.dom.getAncestors(node,function(el){
return ((el.tagName)&&(el.tagName.toLowerCase()==tag));
},_12a);
};
dojo.dom.getFirstAncestorByTag=function(node,tag){
return dojo.dom.getAncestorsByTag(node,tag,true);
};
dojo.dom.isDescendantOf=function(node,_12f,_130){
if(_130&&node){
node=node.parentNode;
}
while(node){
if(node==_12f){
return true;
}
node=node.parentNode;
}
return false;
};
dojo.dom.innerXML=function(node){
if(node.innerXML){
return node.innerXML;
}else{
if(typeof XMLSerializer!="undefined"){
return (new XMLSerializer()).serializeToString(node);
}
}
};
dojo.dom.createDocumentFromText=function(str,_133){
if(!_133){
_133="text/xml";
}
if(typeof DOMParser!="undefined"){
var _134=new DOMParser();
return _134.parseFromString(str,_133);
}else{
if(typeof ActiveXObject!="undefined"){
var _135=new ActiveXObject("Microsoft.XMLDOM");
if(_135){
_135.async=false;
_135.loadXML(str);
return _135;
}else{
dojo.debug("toXml didn't work?");
}
}else{
if(document.createElement){
var tmp=document.createElement("xml");
tmp.innerHTML=str;
if(document.implementation&&document.implementation.createDocument){
var _137=document.implementation.createDocument("foo","",null);
for(var i=0;i<tmp.childNodes.length;i++){
_137.importNode(tmp.childNodes.item(i),true);
}
return _137;
}
return tmp.document&&tmp.document.firstChild?tmp.document.firstChild:tmp;
}
}
}
return null;
};
dojo.dom.insertBefore=function(node,ref,_13b){
if(_13b!=true&&(node===ref||node.nextSibling===ref)){
return false;
}
var _13c=ref.parentNode;
_13c.insertBefore(node,ref);
return true;
};
dojo.dom.insertAfter=function(node,ref,_13f){
var pn=ref.parentNode;
if(ref==pn.lastChild){
if((_13f!=true)&&(node===ref)){
return false;
}
pn.appendChild(node);
}else{
return this.insertBefore(node,ref.nextSibling,_13f);
}
return true;
};
dojo.dom.insertAtPosition=function(node,ref,_143){
if((!node)||(!ref)||(!_143)){
return false;
}
switch(_143.toLowerCase()){
case "before":
return dojo.dom.insertBefore(node,ref);
case "after":
return dojo.dom.insertAfter(node,ref);
case "first":
if(ref.firstChild){
return dojo.dom.insertBefore(node,ref.firstChild);
}else{
ref.appendChild(node);
return true;
}
break;
default:
ref.appendChild(node);
return true;
}
};
dojo.dom.insertAtIndex=function(node,_145,_146){
var _147=_145.childNodes;
if(!_147.length){
_145.appendChild(node);
return true;
}
var _148=null;
for(var i=0;i<_147.length;i++){
var _14a=_147.item(i)["getAttribute"]?parseInt(_147.item(i).getAttribute("dojoinsertionindex")):-1;
if(_14a<_146){
_148=_147.item(i);
}
}
if(_148){
return dojo.dom.insertAfter(node,_148);
}else{
return dojo.dom.insertBefore(node,_147.item(0));
}
};
dojo.dom.textContent=function(node,text){
if(text){
dojo.dom.replaceChildren(node,document.createTextNode(text));
return text;
}else{
var _14d="";
if(node==null){
return _14d;
}
for(var i=0;i<node.childNodes.length;i++){
switch(node.childNodes[i].nodeType){
case 1:
case 5:
_14d+=dojo.dom.textContent(node.childNodes[i]);
break;
case 3:
case 2:
case 4:
_14d+=node.childNodes[i].nodeValue;
break;
default:
break;
}
}
return _14d;
}
};
dojo.dom.collectionToArray=function(_14f){
var _150=new Array(_14f.length);
for(var i=0;i<_14f.length;i++){
_150[i]=_14f[i];
}
return _150;
};
dojo.provide("dojo.uri.Uri");
dojo.uri=new function(){
this.joinPath=function(){
var arr=[];
for(var i=0;i<arguments.length;i++){
arr.push(arguments[i]);
}
return arr.join("/").replace(/\/{2,}/g,"/").replace(/((https*|ftps*):)/i,"$1/");
};
this.dojoUri=function(uri){
return new dojo.uri.Uri(dojo.hostenv.getBaseScriptUri(),uri);
};
this.Uri=function(){
var uri=arguments[0];
for(var i=1;i<arguments.length;i++){
if(!arguments[i]){
continue;
}
var _157=new dojo.uri.Uri(arguments[i].toString());
var _158=new dojo.uri.Uri(uri.toString());
if(_157.path==""&&_157.scheme==null&&_157.authority==null&&_157.query==null){
if(_157.fragment!=null){
_158.fragment=_157.fragment;
}
_157=_158;
}else{
if(_157.scheme==null){
_157.scheme=_158.scheme;
if(_157.authority==null){
_157.authority=_158.authority;
if(_157.path.charAt(0)!="/"){
var path=_158.path.substring(0,_158.path.lastIndexOf("/")+1)+_157.path;
var segs=path.split("/");
for(var j=0;j<segs.length;j++){
if(segs[j]=="."){
if(j==segs.length-1){
segs[j]="";
}else{
segs.splice(j,1);
j--;
}
}else{
if(j>0&&!(j==1&&segs[0]=="")&&segs[j]==".."&&segs[j-1]!=".."){
if(j==segs.length-1){
segs.splice(j,1);
segs[j-1]="";
}else{
segs.splice(j-1,2);
j-=2;
}
}
}
}
_157.path=segs.join("/");
}
}
}
}
uri="";
if(_157.scheme!=null){
uri+=_157.scheme+":";
}
if(_157.authority!=null){
uri+="//"+_157.authority;
}
uri+=_157.path;
if(_157.query!=null){
uri+="?"+_157.query;
}
if(_157.fragment!=null){
uri+="#"+_157.fragment;
}
}
this.uri=uri.toString();
var _15c="^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\\?([^#]*))?(#(.*))?$";
var r=this.uri.match(new RegExp(_15c));
this.scheme=r[2]||(r[1]?"":null);
this.authority=r[4]||(r[3]?"":null);
this.path=r[5];
this.query=r[7]||(r[6]?"":null);
this.fragment=r[9]||(r[8]?"":null);
if(this.authority!=null){
_15c="^((([^:]+:)?([^@]+))@)?([^:]*)(:([0-9]+))?$";
r=this.authority.match(new RegExp(_15c));
this.user=r[3]||null;
this.password=r[4]||null;
this.host=r[5];
this.port=r[7]||null;
}
this.toString=function(){
return this.uri;
};
};
};
dojo.provide("dojo.string");
dojo.require("dojo.lang");
dojo.string.trim=function(str,wh){
if(!dojo.lang.isString(str)){
return str;
}
if(!str.length){
return str;
}
if(wh>0){
return str.replace(/^\s+/,"");
}else{
if(wh<0){
return str.replace(/\s+$/,"");
}else{
return str.replace(/^\s+|\s+$/g,"");
}
}
};
dojo.string.trimStart=function(str){
return dojo.string.trim(str,1);
};
dojo.string.trimEnd=function(str){
return dojo.string.trim(str,-1);
};
dojo.string.paramString=function(str,_163,_164){
for(var name in _163){
var re=new RegExp("\\%\\{"+name+"\\}","g");
str=str.replace(re,_163[name]);
}
if(_164){
str=str.replace(/%\{([^\}\s]+)\}/g,"");
}
return str;
};
dojo.string.capitalize=function(str){
if(!dojo.lang.isString(str)){
return "";
}
if(arguments.length==0){
str=this;
}
var _168=str.split(" ");
var _169="";
var len=_168.length;
for(var i=0;i<len;i++){
var word=_168[i];
word=word.charAt(0).toUpperCase()+word.substring(1,word.length);
_169+=word;
if(i<len-1){
_169+=" ";
}
}
return new String(_169);
};
dojo.string.isBlank=function(str){
if(!dojo.lang.isString(str)){
return true;
}
return (dojo.string.trim(str).length==0);
};
dojo.string.encodeAscii=function(str){
if(!dojo.lang.isString(str)){
return str;
}
var ret="";
var _170=escape(str);
var _171,re=/%u([0-9A-F]{4})/i;
while((_171=_170.match(re))){
var num=Number("0x"+_171[1]);
var _173=escape("&#"+num+";");
ret+=_170.substring(0,_171.index)+_173;
_170=_170.substring(_171.index+_171[0].length);
}
ret+=_170.replace(/\+/g,"%2B");
return ret;
};
dojo.string.summary=function(str,len){
if(!len||str.length<=len){
return str;
}else{
return str.substring(0,len).replace(/\.+$/,"")+"...";
}
};
dojo.string.escape=function(type,str){
switch(type.toLowerCase()){
case "xml":
case "html":
case "xhtml":
return dojo.string.escapeXml(str);
case "sql":
return dojo.string.escapeSql(str);
case "regexp":
case "regex":
return dojo.string.escapeRegExp(str);
case "javascript":
case "jscript":
case "js":
return dojo.string.escapeJavaScript(str);
case "ascii":
return dojo.string.encodeAscii(str);
default:
return str;
}
};
dojo.string.escapeXml=function(str){
return str.replace(/&/gm,"&amp;").replace(/</gm,"&lt;").replace(/>/gm,"&gt;").replace(/"/gm,"&quot;").replace(/'/gm,"&#39;");
};
dojo.string.escapeSql=function(str){
return str.replace(/'/gm,"''");
};
dojo.string.escapeRegExp=function(str){
return str.replace(/\\/gm,"\\\\").replace(/([\f\b\n\t\r])/gm,"\\$1");
};
dojo.string.escapeJavaScript=function(str){
return str.replace(/(["'\f\b\n\t\r])/gm,"\\$1");
};
dojo.string.repeat=function(str,_17d,_17e){
var out="";
for(var i=0;i<_17d;i++){
out+=str;
if(_17e&&i<_17d-1){
out+=_17e;
}
}
return out;
};
dojo.string.endsWith=function(str,end,_183){
if(_183){
str=str.toLowerCase();
end=end.toLowerCase();
}
return str.lastIndexOf(end)==str.length-end.length;
};
dojo.string.endsWithAny=function(str){
for(var i=1;i<arguments.length;i++){
if(dojo.string.endsWith(str,arguments[i])){
return true;
}
}
return false;
};
dojo.string.startsWith=function(str,_187,_188){
if(_188){
str=str.toLowerCase();
_187=_187.toLowerCase();
}
return str.indexOf(_187)==0;
};
dojo.string.startsWithAny=function(str){
for(var i=1;i<arguments.length;i++){
if(dojo.string.startsWith(str,arguments[i])){
return true;
}
}
return false;
};
dojo.string.has=function(str){
for(var i=1;i<arguments.length;i++){
if(str.indexOf(arguments[i]>-1)){
return true;
}
}
return false;
};
dojo.string.pad=function(str,len,c,dir){
var out=String(str);
if(!c){
c="0";
}
if(!dir){
dir=1;
}
while(out.length<len){
if(dir>0){
out=c+out;
}else{
out+=c;
}
}
return out;
};
dojo.string.padLeft=function(str,len,c){
return dojo.string.pad(str,len,c,1);
};
dojo.string.padRight=function(str,len,c){
return dojo.string.pad(str,len,c,-1);
};
dojo.string.addToPrototype=function(){
for(var _198 in dojo.string){
if(dojo.lang.isFunction(dojo.string[_198])){
var func=(function(){
var meth=_198;
switch(meth){
case "addToPrototype":
return null;
break;
case "escape":
return function(type){
return dojo.string.escape(type,this);
};
break;
default:
return function(){
var args=[this];
for(var i=0;i<arguments.length;i++){
args.push(arguments[i]);
}
dojo.debug(args);
return dojo.string[meth].apply(dojo.string,args);
};
}
})();
if(func){
String.prototype[_198]=func;
}
}
}
};
dojo.provide("dojo.math");
dojo.math.degToRad=function(x){
return (x*Math.PI)/180;
};
dojo.math.radToDeg=function(x){
return (x*180)/Math.PI;
};
dojo.math.factorial=function(n){
if(n<1){
return 0;
}
var _1a1=1;
for(var i=1;i<=n;i++){
_1a1*=i;
}
return _1a1;
};
dojo.math.permutations=function(n,k){
if(n==0||k==0){
return 1;
}
return (dojo.math.factorial(n)/dojo.math.factorial(n-k));
};
dojo.math.combinations=function(n,r){
if(n==0||r==0){
return 1;
}
return (dojo.math.factorial(n)/(dojo.math.factorial(n-r)*dojo.math.factorial(r)));
};
dojo.math.bernstein=function(t,n,i){
return (dojo.math.combinations(n,i)*Math.pow(t,i)*Math.pow(1-t,n-i));
};
dojo.math.gaussianRandom=function(){
var k=2;
do{
var i=2*Math.random()-1;
var j=2*Math.random()-1;
k=i*i+j*j;
}while(k>=1);
k=Math.sqrt((-2*Math.log(k))/k);
return i*k;
};
dojo.math.mean=function(){
var _1ad=dojo.lang.isArray(arguments[0])?arguments[0]:arguments;
var mean=0;
for(var i=0;i<_1ad.length;i++){
mean+=_1ad[i];
}
return mean/_1ad.length;
};
dojo.math.round=function(_1b0,_1b1){
if(!_1b1){
var _1b2=1;
}else{
var _1b2=Math.pow(10,_1b1);
}
return Math.round(_1b0*_1b2)/_1b2;
};
dojo.math.sd=function(){
var _1b3=dojo.lang.isArray(arguments[0])?arguments[0]:arguments;
return Math.sqrt(dojo.math.variance(_1b3));
};
dojo.math.variance=function(){
var _1b4=dojo.lang.isArray(arguments[0])?arguments[0]:arguments;
var mean=0,squares=0;
for(var i=0;i<_1b4.length;i++){
mean+=_1b4[i];
squares+=Math.pow(_1b4[i],2);
}
return (squares/_1b4.length)-Math.pow(mean/_1b4.length,2);
};
dojo.provide("dojo.graphics.color");
dojo.require("dojo.lang");
dojo.require("dojo.string");
dojo.require("dojo.math");
dojo.graphics.color.Color=function(r,g,b,a){
if(dojo.lang.isArray(r)){
this.r=r[0];
this.g=r[1];
this.b=r[2];
this.a=r[3]||1;
}else{
if(dojo.lang.isString(r)){
var rgb=dojo.graphics.color.extractRGB(r);
this.r=rgb[0];
this.g=rgb[1];
this.b=rgb[2];
this.a=g||1;
}else{
if(r instanceof dojo.graphics.color.Color){
this.r=r.r;
this.b=r.b;
this.g=r.g;
this.a=r.a;
}else{
this.r=r;
this.g=g;
this.b=b;
this.a=a;
}
}
}
};
dojo.lang.extend(dojo.graphics.color.Color,{toRgb:function(_1bc){
if(_1bc){
return this.toRgba();
}else{
return [this.r,this.g,this.b];
}
},toRgba:function(){
return [this.r,this.g,this.b,this.a];
},toHex:function(){
return dojo.graphics.color.rgb2hex(this.toRgb());
},toCss:function(){
return "rgb("+this.toRgb().join()+")";
},toString:function(){
return this.toHex();
},toHsv:function(){
return dojo.graphics.color.rgb2hsv(this.toRgb());
},toHsl:function(){
return dojo.graphics.color.rgb2hsl(this.toRgb());
},blend:function(_1bd,_1be){
return dojo.graphics.color.blend(this.toRgb(),new Color(_1bd).toRgb(),_1be);
}});
dojo.graphics.color.named={white:[255,255,255],black:[0,0,0],red:[255,0,0],green:[0,255,0],blue:[0,0,255],navy:[0,0,128],gray:[128,128,128],silver:[192,192,192]};
dojo.graphics.color.blend=function(a,b,_1c1){
if(typeof a=="string"){
return dojo.graphics.color.blendHex(a,b,_1c1);
}
if(!_1c1){
_1c1=0;
}else{
if(_1c1>1){
_1c1=1;
}else{
if(_1c1<-1){
_1c1=-1;
}
}
}
var c=new Array(3);
for(var i=0;i<3;i++){
var half=Math.abs(a[i]-b[i])/2;
c[i]=Math.floor(Math.min(a[i],b[i])+half+(half*_1c1));
}
return c;
};
dojo.graphics.color.blendHex=function(a,b,_1c7){
return dojo.graphics.color.rgb2hex(dojo.graphics.color.blend(dojo.graphics.color.hex2rgb(a),dojo.graphics.color.hex2rgb(b),_1c7));
};
dojo.graphics.color.extractRGB=function(_1c8){
var hex="0123456789abcdef";
_1c8=_1c8.toLowerCase();
if(_1c8.indexOf("rgb")==0){
var _1ca=_1c8.match(/rgba*\((\d+), *(\d+), *(\d+)/i);
var ret=_1ca.splice(1,3);
return ret;
}else{
var _1cc=dojo.graphics.color.hex2rgb(_1c8);
if(_1cc){
return _1cc;
}else{
return dojo.graphics.color.named[_1c8]||[255,255,255];
}
}
};
dojo.graphics.color.hex2rgb=function(hex){
var _1ce="0123456789ABCDEF";
var rgb=new Array(3);
if(hex.indexOf("#")==0){
hex=hex.substring(1);
}
hex=hex.toUpperCase();
if(hex.replace(new RegExp("["+_1ce+"]","g"),"")!=""){
return null;
}
if(hex.length==3){
rgb[0]=hex.charAt(0)+hex.charAt(0);
rgb[1]=hex.charAt(1)+hex.charAt(1);
rgb[2]=hex.charAt(2)+hex.charAt(2);
}else{
rgb[0]=hex.substring(0,2);
rgb[1]=hex.substring(2,4);
rgb[2]=hex.substring(4);
}
for(var i=0;i<rgb.length;i++){
rgb[i]=_1ce.indexOf(rgb[i].charAt(0))*16+_1ce.indexOf(rgb[i].charAt(1));
}
return rgb;
};
dojo.graphics.color.rgb2hex=function(r,g,b){
if(dojo.lang.isArray(r)){
g=r[1]||0;
b=r[2]||0;
r=r[0]||0;
}
return ["#",dojo.string.pad(r.toString(16),2),dojo.string.pad(g.toString(16),2),dojo.string.pad(b.toString(16),2)].join("");
};
dojo.graphics.color.rgb2hsv=function(r,g,b){
if(dojo.lang.isArray(r)){
b=r[2]||0;
g=r[1]||0;
r=r[0]||0;
}
var h=null;
var s=null;
var v=null;
var min=Math.min(r,g,b);
v=Math.max(r,g,b);
var _1db=v-min;
s=(v==0)?0:_1db/v;
if(s==0){
h=0;
}else{
if(r==v){
h=60*(g-b)/_1db;
}else{
if(g==v){
h=120+60*(b-r)/_1db;
}else{
if(b==v){
h=240+60*(r-g)/_1db;
}
}
}
if(h<0){
h+=360;
}
}
h=(h==0)?360:Math.ceil((h/360)*255);
s=Math.ceil(s*255);
return [h,s,v];
};
dojo.graphics.color.hsv2rgb=function(h,s,v){
if(dojo.lang.isArray(h)){
v=h[2]||0;
s=h[1]||0;
h=h[0]||0;
}
h=(h/255)*360;
if(h==360){
h=0;
}
s=s/255;
v=v/255;
var r=null;
var g=null;
var b=null;
if(s==0){
r=v;
g=v;
b=v;
}else{
var _1e2=h/60;
var i=Math.floor(_1e2);
var f=_1e2-i;
var p=v*(1-s);
var q=v*(1-(s*f));
var t=v*(1-(s*(1-f)));
switch(i){
case 0:
r=v;
g=t;
b=p;
break;
case 1:
r=q;
g=v;
b=p;
break;
case 2:
r=p;
g=v;
b=t;
break;
case 3:
r=p;
g=q;
b=v;
break;
case 4:
r=t;
g=p;
b=v;
break;
case 5:
r=v;
g=p;
b=q;
break;
}
}
r=Math.ceil(r*255);
g=Math.ceil(g*255);
b=Math.ceil(b*255);
return [r,g,b];
};
dojo.graphics.color.rgb2hsl=function(r,g,b){
if(dojo.lang.isArray(r)){
b=r[2]||0;
g=r[1]||0;
r=r[0]||0;
}
r/=255;
g/=255;
b/=255;
var h=null;
var s=null;
var l=null;
var min=Math.min(r,g,b);
var max=Math.max(r,g,b);
var _1f0=max-min;
l=(min+max)/2;
s=0;
if((l>0)&&(l<1)){
s=_1f0/((l<0.5)?(2*l):(2-2*l));
}
h=0;
if(_1f0>0){
if((max==r)&&(max!=g)){
h+=(g-b)/_1f0;
}
if((max==g)&&(max!=b)){
h+=(2+(b-r)/_1f0);
}
if((max==b)&&(max!=r)){
h+=(4+(r-g)/_1f0);
}
h*=60;
}
h=(h==0)?360:Math.ceil((h/360)*255);
s=Math.ceil(s*255);
l=Math.ceil(l*255);
return [h,s,l];
};
dojo.graphics.color.hsl2rgb=function(h,s,l){
if(dojo.lang.isArray(h)){
l=h[2]||0;
s=h[1]||0;
h=h[0]||0;
}
h=(h/255)*360;
if(h==360){
h=0;
}
s=s/255;
l=l/255;
while(h<0){
h+=360;
}
while(h>360){
h-=360;
}
if(h<120){
r=(120-h)/60;
g=h/60;
b=0;
}else{
if(h<240){
r=0;
g=(240-h)/60;
b=(h-120)/60;
}else{
r=(h-240)/60;
g=0;
b=(360-h)/60;
}
}
r=Math.min(r,1);
g=Math.min(g,1);
b=Math.min(b,1);
r=2*s*r+(1-s);
g=2*s*g+(1-s);
b=2*s*b+(1-s);
if(l<0.5){
r=l*r;
g=l*g;
b=l*b;
}else{
r=(1-l)*r+2*l-1;
g=(1-l)*g+2*l-1;
b=(1-l)*b+2*l-1;
}
r=Math.ceil(r*255);
g=Math.ceil(g*255);
b=Math.ceil(b*255);
return [r,g,b];
};
dojo.provide("dojo.style");
dojo.require("dojo.dom");
dojo.require("dojo.uri.Uri");
dojo.require("dojo.graphics.color");
dojo.style.boxSizing={marginBox:"margin-box",borderBox:"border-box",paddingBox:"padding-box",contentBox:"content-box"};
dojo.style.getBoxSizing=function(node){
if(dojo.render.html.ie||dojo.render.html.opera){
var cm=document["compatMode"];
if(cm=="BackCompat"||cm=="QuirksMode"){
return dojo.style.boxSizing.borderBox;
}else{
return dojo.style.boxSizing.contentBox;
}
}else{
if(arguments.length==0){
node=document.documentElement;
}
var _1f6=dojo.style.getStyle(node,"-moz-box-sizing");
if(!_1f6){
_1f6=dojo.style.getStyle(node,"box-sizing");
}
return (_1f6?_1f6:dojo.style.boxSizing.contentBox);
}
};
dojo.style.isBorderBox=function(node){
return (dojo.style.getBoxSizing(node)==dojo.style.boxSizing.borderBox);
};
dojo.style.getUnitValue=function(_1f8,_1f9,_1fa){
var _1fb={value:0,units:"px"};
var s=dojo.style.getComputedStyle(_1f8,_1f9);
if(s==""||(s=="auto"&&_1fa)){
return _1fb;
}
if(dojo.lang.isUndefined(s)){
_1fb.value=NaN;
}else{
var _1fd=s.match(/([\d.]+)([a-z%]*)/i);
if(!_1fd){
_1fb.value=NaN;
}else{
_1fb.value=Number(_1fd[1]);
_1fb.units=_1fd[2].toLowerCase();
}
}
return _1fb;
};
dojo.style.getPixelValue=function(_1fe,_1ff,_200){
var _201=dojo.style.getUnitValue(_1fe,_1ff,_200);
if(isNaN(_201.value)||(_201.value&&_201.units!="px")){
return NaN;
}
return _201.value;
};
dojo.style.getNumericStyle=dojo.style.getPixelValue;
dojo.style.isPositionAbsolute=function(node){
return (dojo.style.getComputedStyle(node,"position")=="absolute");
};
dojo.style.getMarginWidth=function(node){
var _204=dojo.style.isPositionAbsolute(node);
var left=dojo.style.getPixelValue(node,"margin-left",_204);
var _206=dojo.style.getPixelValue(node,"margin-right",_204);
return left+_206;
};
dojo.style.getBorderWidth=function(node){
var left=(dojo.style.getStyle(node,"border-left-style")=="none"?0:dojo.style.getPixelValue(node,"border-left-width"));
var _209=(dojo.style.getStyle(node,"border-right-style")=="none"?0:dojo.style.getPixelValue(node,"border-right-width"));
return left+_209;
};
dojo.style.getPaddingWidth=function(node){
var left=dojo.style.getPixelValue(node,"padding-left",true);
var _20c=dojo.style.getPixelValue(node,"padding-right",true);
return left+_20c;
};
dojo.style.getContentWidth=function(node){
return node.offsetWidth-dojo.style.getPaddingWidth(node)-dojo.style.getBorderWidth(node);
};
dojo.style.getInnerWidth=function(node){
return node.offsetWidth;
};
dojo.style.getOuterWidth=function(node){
return dojo.style.getInnerWidth(node)+dojo.style.getMarginWidth(node);
};
dojo.style.setOuterWidth=function(node,_211){
if(!dojo.style.isBorderBox(node)){
_211-=dojo.style.getPaddingWidth(node)+dojo.style.getBorderWidth(node);
}
_211-=dojo.style.getMarginWidth(node);
if(!isNaN(_211)&&_211>0){
node.style.width=_211+"px";
return true;
}else{
return false;
}
};
dojo.style.getContentBoxWidth=dojo.style.getContentWidth;
dojo.style.getBorderBoxWidth=dojo.style.getInnerWidth;
dojo.style.getMarginBoxWidth=dojo.style.getOuterWidth;
dojo.style.setMarginBoxWidth=dojo.style.setOuterWidth;
dojo.style.getMarginHeight=function(node){
var _213=dojo.style.isPositionAbsolute(node);
var top=dojo.style.getPixelValue(node,"margin-top",_213);
var _215=dojo.style.getPixelValue(node,"margin-bottom",_213);
return top+_215;
};
dojo.style.getBorderHeight=function(node){
var top=(dojo.style.getStyle(node,"border-top-style")=="none"?0:dojo.style.getPixelValue(node,"border-top-width"));
var _218=(dojo.style.getStyle(node,"border-bottom-style")=="none"?0:dojo.style.getPixelValue(node,"border-bottom-width"));
return top+_218;
};
dojo.style.getPaddingHeight=function(node){
var top=dojo.style.getPixelValue(node,"padding-top",true);
var _21b=dojo.style.getPixelValue(node,"padding-bottom",true);
return top+_21b;
};
dojo.style.getContentHeight=function(node){
return node.offsetHeight-dojo.style.getPaddingHeight(node)-dojo.style.getBorderHeight(node);
};
dojo.style.getInnerHeight=function(node){
return node.offsetHeight;
};
dojo.style.getOuterHeight=function(node){
return dojo.style.getInnerHeight(node)+dojo.style.getMarginHeight(node);
};
dojo.style.setOuterHeight=function(node,_220){
if(!dojo.style.isBorderBox(node)){
_220-=dojo.style.getPaddingHeight(node)+dojo.style.getBorderHeight(node);
}
_220-=dojo.style.getMarginHeight(node);
if(!isNaN(_220)&&_220>0){
node.style.height=_220+"px";
return true;
}else{
return false;
}
};
dojo.style.setContentWidth=function(node,_222){
if(dojo.style.isBorderBox(node)){
_222+=dojo.style.getPaddingWidth(node)+dojo.style.getBorderWidth(node);
}
if(!isNaN(_222)&&_222>0){
node.style.width=_222+"px";
return true;
}else{
return false;
}
};
dojo.style.setContentHeight=function(node,_224){
if(dojo.style.isBorderBox(node)){
_224+=dojo.style.getPaddingHeight(node)+dojo.style.getBorderHeight(node);
}
if(!isNaN(_224)&&_224>0){
node.style.height=_224+"px";
return true;
}else{
return false;
}
};
dojo.style.getContentBoxHeight=dojo.style.getContentHeight;
dojo.style.getBorderBoxHeight=dojo.style.getInnerHeight;
dojo.style.getMarginBoxHeight=dojo.style.getOuterHeight;
dojo.style.setMarginBoxHeight=dojo.style.setOuterHeight;
dojo.style.getTotalOffset=function(node,type,_227){
var _228=(type=="top")?"offsetTop":"offsetLeft";
var _229=(type=="top")?"scrollTop":"scrollLeft";
var alt=(type=="top")?"y":"x";
var ret=0;
if(node["offsetParent"]){
if(_227&&node.parentNode!=document.body){
ret-=dojo.style.sumAncestorProperties(node,_229);
}
do{
ret+=node[_228];
node=node.offsetParent;
}while(node!=document.getElementsByTagName("body")[0].parentNode&&node!=null);
}else{
if(node[alt]){
ret+=node[alt];
}
}
return ret;
};
dojo.style.sumAncestorProperties=function(node,prop){
if(!node){
return 0;
}
var _22e=0;
while(node){
var val=node[prop];
if(val){
_22e+=val-0;
}
node=node.parentNode;
}
return _22e;
};
dojo.style.totalOffsetLeft=function(node,_231){
return dojo.style.getTotalOffset(node,"left",_231);
};
dojo.style.getAbsoluteX=dojo.style.totalOffsetLeft;
dojo.style.totalOffsetTop=function(node,_233){
return dojo.style.getTotalOffset(node,"top",_233);
};
dojo.style.getAbsoluteY=dojo.style.totalOffsetTop;
dojo.style.getAbsolutePosition=function(node,_235){
var _236=[dojo.style.getAbsoluteX(node,_235),dojo.style.getAbsoluteY(node,_235)];
_236.x=_236[0];
_236.y=_236[1];
return _236;
};
dojo.style.styleSheet=null;
dojo.style.insertCssRule=function(_237,_238,_239){
if(!dojo.style.styleSheet){
if(document.createStyleSheet){
dojo.style.styleSheet=document.createStyleSheet();
}else{
if(document.styleSheets[0]){
dojo.style.styleSheet=document.styleSheets[0];
}else{
return null;
}
}
}
if(arguments.length<3){
if(dojo.style.styleSheet.cssRules){
_239=dojo.style.styleSheet.cssRules.length;
}else{
if(dojo.style.styleSheet.rules){
_239=dojo.style.styleSheet.rules.length;
}else{
return null;
}
}
}
if(dojo.style.styleSheet.insertRule){
var rule=_237+" { "+_238+" }";
return dojo.style.styleSheet.insertRule(rule,_239);
}else{
if(dojo.style.styleSheet.addRule){
return dojo.style.styleSheet.addRule(_237,_238,_239);
}else{
return null;
}
}
};
dojo.style.removeCssRule=function(_23b){
if(!dojo.style.styleSheet){
dojo.debug("no stylesheet defined for removing rules");
return false;
}
if(dojo.render.html.ie){
if(!_23b){
_23b=dojo.style.styleSheet.rules.length;
dojo.style.styleSheet.removeRule(_23b);
}
}else{
if(document.styleSheets[0]){
if(!_23b){
_23b=dojo.style.styleSheet.cssRules.length;
}
dojo.style.styleSheet.deleteRule(_23b);
}
}
return true;
};
dojo.style.insertCssFile=function(URI,doc,_23e){
if(!URI){
return;
}
if(!doc){
doc=document;
}
if(doc.baseURI){
URI=new dojo.uri.Uri(doc.baseURI,URI);
}
if(_23e&&doc.styleSheets){
var loc=location.href.split("#")[0].substring(0,location.href.indexOf(location.pathname));
for(var i=0;i<doc.styleSheets.length;i++){
if(doc.styleSheets[i].href&&URI.toString()==new dojo.uri.Uri(doc.styleSheets[i].href.toString())){
return;
}
}
}
var file=doc.createElement("link");
file.setAttribute("type","text/css");
file.setAttribute("rel","stylesheet");
file.setAttribute("href",URI);
var head=doc.getElementsByTagName("head")[0];
if(head){
head.appendChild(file);
}
};
dojo.style.getBackgroundColor=function(node){
var _244;
do{
_244=dojo.style.getStyle(node,"background-color");
if(_244.toLowerCase()=="rgba(0, 0, 0, 0)"){
_244="transparent";
}
if(node==document.getElementsByTagName("body")[0]){
node=null;
break;
}
node=node.parentNode;
}while(node&&dojo.lang.inArray(_244,["transparent",""]));
if(_244=="transparent"){
_244=[255,255,255,0];
}else{
_244=dojo.graphics.color.extractRGB(_244);
}
return _244;
};
dojo.style.getComputedStyle=function(_245,_246,_247){
var _248=_247;
if(_245.style.getPropertyValue){
_248=_245.style.getPropertyValue(_246);
}
if(!_248){
if(document.defaultView){
_248=document.defaultView.getComputedStyle(_245,"").getPropertyValue(_246);
}else{
if(_245.currentStyle){
_248=_245.currentStyle[dojo.style.toCamelCase(_246)];
}
}
}
return _248;
};
dojo.style.getStyle=function(_249,_24a){
var _24b=dojo.style.toCamelCase(_24a);
var _24c=_249.style[_24b];
return (_24c?_24c:dojo.style.getComputedStyle(_249,_24a,_24c));
};
dojo.style.toCamelCase=function(_24d){
var arr=_24d.split("-"),cc=arr[0];
for(var i=1;i<arr.length;i++){
cc+=arr[i].charAt(0).toUpperCase()+arr[i].substring(1);
}
return cc;
};
dojo.style.toSelectorCase=function(_250){
return _250.replace(/([A-Z])/g,"-$1").toLowerCase();
};
dojo.style.setOpacity=function setOpacity(node,_252,_253){
node=dojo.byId(node);
var h=dojo.render.html;
if(!_253){
if(_252>=1){
if(h.ie){
dojo.style.clearOpacity(node);
return;
}else{
_252=0.999999;
}
}else{
if(_252<0){
_252=0;
}
}
}
if(h.ie){
if(node.nodeName.toLowerCase()=="tr"){
var tds=node.getElementsByTagName("td");
for(var x=0;x<tds.length;x++){
tds[x].style.filter="Alpha(Opacity="+_252*100+")";
}
}
node.style.filter="Alpha(Opacity="+_252*100+")";
}else{
if(h.moz){
node.style.opacity=_252;
node.style.MozOpacity=_252;
}else{
if(h.safari){
node.style.opacity=_252;
node.style.KhtmlOpacity=_252;
}else{
node.style.opacity=_252;
}
}
}
};
dojo.style.getOpacity=function getOpacity(node){
if(dojo.render.html.ie){
var opac=(node.filters&&node.filters.alpha&&typeof node.filters.alpha.opacity=="number"?node.filters.alpha.opacity:100)/100;
}else{
var opac=node.style.opacity||node.style.MozOpacity||node.style.KhtmlOpacity||1;
}
return opac>=0.999999?1:Number(opac);
};
dojo.style.clearOpacity=function clearOpacity(node){
var h=dojo.render.html;
if(h.ie){
if(node.filters&&node.filters.alpha){
node.style.filter="";
}
}else{
if(h.moz){
node.style.opacity=1;
node.style.MozOpacity=1;
}else{
if(h.safari){
node.style.opacity=1;
node.style.KhtmlOpacity=1;
}else{
node.style.opacity=1;
}
}
}
};
dojo.provide("dojo.html");
dojo.require("dojo.dom");
dojo.require("dojo.style");
dojo.require("dojo.string");
dojo.lang.mixin(dojo.html,dojo.dom);
dojo.lang.mixin(dojo.html,dojo.style);
dojo.html.clearSelection=function(){
try{
if(window["getSelection"]){
if(dojo.render.html.safari){
window.getSelection().collapse();
}else{
window.getSelection().removeAllRanges();
}
}else{
if((document.selection)&&(document.selection.clear)){
document.selection.clear();
}
}
return true;
}
catch(e){
dojo.debug(e);
return false;
}
};
dojo.html.disableSelection=function(_25b){
_25b=_25b||dojo.html.body();
var h=dojo.render.html;
if(h.mozilla){
_25b.style.MozUserSelect="none";
}else{
if(h.safari){
_25b.style.KhtmlUserSelect="none";
}else{
if(h.ie){
_25b.unselectable="on";
}else{
return false;
}
}
}
return true;
};
dojo.html.enableSelection=function(_25d){
_25d=_25d||dojo.html.body();
var h=dojo.render.html;
if(h.mozilla){
_25d.style.MozUserSelect="";
}else{
if(h.safari){
_25d.style.KhtmlUserSelect="";
}else{
if(h.ie){
_25d.unselectable="off";
}else{
return false;
}
}
}
return true;
};
dojo.html.selectElement=function(_25f){
if(document.selection&&dojo.html.body().createTextRange){
var _260=dojo.html.body().createTextRange();
_260.moveToElementText(_25f);
_260.select();
}else{
if(window["getSelection"]){
var _261=window.getSelection();
if(_261["selectAllChildren"]){
_261.selectAllChildren(_25f);
}
}
}
};
dojo.html.isSelectionCollapsed=function(){
if(document["selection"]){
return document.selection.createRange().text=="";
}else{
if(window["getSelection"]){
var _262=window.getSelection();
if(dojo.lang.isString(_262)){
return _262=="";
}else{
return _262.isCollapsed;
}
}
}
};
dojo.html.getEventTarget=function(evt){
if(!evt){
evt=window.event||{};
}
if(evt.srcElement){
return evt.srcElement;
}else{
if(evt.target){
return evt.target;
}
}
return null;
};
dojo.html.getScrollTop=function(){
return document.documentElement.scrollTop||dojo.html.body().scrollTop||0;
};
dojo.html.getScrollLeft=function(){
return document.documentElement.scrollLeft||dojo.html.body().scrollLeft||0;
};
dojo.html.getDocumentWidth=function(){
dojo.deprecated("dojo.html.getDocument* has been deprecated in favor of dojo.html.getViewport*");
return dojo.html.getViewportWidth();
};
dojo.html.getDocumentHeight=function(){
dojo.deprecated("dojo.html.getDocument* has been deprecated in favor of dojo.html.getViewport*");
return dojo.html.getViewportHeight();
};
dojo.html.getDocumentSize=function(){
dojo.deprecated("dojo.html.getDocument* has been deprecated in favor of dojo.html.getViewport*");
return dojo.html.getViewportSize();
};
dojo.html.getViewportWidth=function(){
var w=0;
if(window.innerWidth){
w=window.innerWidth;
}
if(dojo.exists(document,"documentElement.clientWidth")){
var w2=document.documentElement.clientWidth;
if(!w||w2&&w2<w){
w=w2;
}
return w;
}
if(document.body){
return document.body.clientWidth;
}
return 0;
};
dojo.html.getViewportHeight=function(){
if(window.innerHeight){
return window.innerHeight;
}
if(dojo.exists(document,"documentElement.clientHeight")){
return document.documentElement.clientHeight;
}
if(document.body){
return document.body.clientHeight;
}
return 0;
};
dojo.html.getViewportSize=function(){
var ret=[dojo.html.getViewportWidth(),dojo.html.getViewportHeight()];
ret.w=ret[0];
ret.h=ret[1];
return ret;
};
dojo.html.getScrollOffset=function(){
var ret=[0,0];
if(window.pageYOffset){
ret=[window.pageXOffset,window.pageYOffset];
}else{
if(dojo.exists(document,"documentElement.scrollTop")){
ret=[document.documentElement.scrollLeft,document.documentElement.scrollTop];
}else{
if(document.body){
ret=[document.body.scrollLeft,document.body.scrollTop];
}
}
}
ret.x=ret[0];
ret.y=ret[1];
return ret;
};
dojo.html.getParentOfType=function(node,type){
dojo.deprecated("dojo.html.getParentOfType has been deprecated in favor of dojo.html.getParentByType*");
return dojo.html.getParentByType(node,type);
};
dojo.html.getParentByType=function(node,type){
var _26c=node;
type=type.toLowerCase();
while((_26c)&&(_26c.nodeName.toLowerCase()!=type)){
if(_26c==(document["body"]||document["documentElement"])){
return null;
}
_26c=_26c.parentNode;
}
return _26c;
};
dojo.html.getAttribute=function(node,attr){
if((!node)||(!node.getAttribute)){
return null;
}
var ta=typeof attr=="string"?attr:new String(attr);
var v=node.getAttribute(ta.toUpperCase());
if((v)&&(typeof v=="string")&&(v!="")){
return v;
}
if(v&&v.value){
return v.value;
}
if((node.getAttributeNode)&&(node.getAttributeNode(ta))){
return (node.getAttributeNode(ta)).value;
}else{
if(node.getAttribute(ta)){
return node.getAttribute(ta);
}else{
if(node.getAttribute(ta.toLowerCase())){
return node.getAttribute(ta.toLowerCase());
}
}
}
return null;
};
dojo.html.hasAttribute=function(node,attr){
return dojo.html.getAttribute(node,attr)?true:false;
};
dojo.html.getClass=function(node){
if(!node){
return "";
}
var cs="";
if(node.className){
cs=node.className;
}else{
if(dojo.html.hasAttribute(node,"class")){
cs=dojo.html.getAttribute(node,"class");
}
}
return dojo.string.trim(cs);
};
dojo.html.getClasses=function(node){
var c=dojo.html.getClass(node);
return (c=="")?[]:c.split(/\s+/g);
};
dojo.html.hasClass=function(node,_278){
return dojo.lang.inArray(dojo.html.getClasses(node),_278);
};
dojo.html.prependClass=function(node,_27a){
if(!node){
return false;
}
_27a+=" "+dojo.html.getClass(node);
return dojo.html.setClass(node,_27a);
};
dojo.html.addClass=function(node,_27c){
if(!node){
return false;
}
if(dojo.html.hasClass(node,_27c)){
return false;
}
_27c=dojo.string.trim(dojo.html.getClass(node)+" "+_27c);
return dojo.html.setClass(node,_27c);
};
dojo.html.setClass=function(node,_27e){
if(!node){
return false;
}
var cs=new String(_27e);
try{
if(typeof node.className=="string"){
node.className=cs;
}else{
if(node.setAttribute){
node.setAttribute("class",_27e);
node.className=cs;
}else{
return false;
}
}
}
catch(e){
dojo.debug("dojo.html.setClass() failed",e);
}
return true;
};
dojo.html.removeClass=function(node,_281,_282){
if(!node){
return false;
}
var _281=dojo.string.trim(new String(_281));
try{
var cs=dojo.html.getClasses(node);
var nca=[];
if(_282){
for(var i=0;i<cs.length;i++){
if(cs[i].indexOf(_281)==-1){
nca.push(cs[i]);
}
}
}else{
for(var i=0;i<cs.length;i++){
if(cs[i]!=_281){
nca.push(cs[i]);
}
}
}
dojo.html.setClass(node,nca.join(" "));
}
catch(e){
dojo.debug("dojo.html.removeClass() failed",e);
}
return true;
};
dojo.html.replaceClass=function(node,_287,_288){
dojo.html.removeClass(node,_288);
dojo.html.addClass(node,_287);
};
dojo.html.classMatchType={ContainsAll:0,ContainsAny:1,IsOnly:2};
dojo.html.getElementsByClass=function(_289,_28a,_28b,_28c){
if(!_28a){
_28a=document;
}
var _28d=_289.split(/\s+/g);
var _28e=[];
if(_28c!=1&&_28c!=2){
_28c=0;
}
var _28f=new RegExp("(\\s|^)(("+_28d.join(")|(")+"))(\\s|$)");
if(!_28b){
_28b="*";
}
var _290=_28a.getElementsByTagName(_28b);
outer:
for(var i=0;i<_290.length;i++){
var node=_290[i];
var _293=dojo.html.getClasses(node);
if(_293.length==0){
continue outer;
}
var _294=0;
for(var j=0;j<_293.length;j++){
if(_28f.test(_293[j])){
if(_28c==dojo.html.classMatchType.ContainsAny){
_28e.push(node);
continue outer;
}else{
_294++;
}
}else{
if(_28c==dojo.html.classMatchType.IsOnly){
continue outer;
}
}
}
if(_294==_28d.length){
if(_28c==dojo.html.classMatchType.IsOnly&&_294==_293.length){
_28e.push(node);
}else{
if(_28c==dojo.html.classMatchType.ContainsAll){
_28e.push(node);
}
}
}
}
return _28e;
};
dojo.html.getElementsByClassName=dojo.html.getElementsByClass;
dojo.html.gravity=function(node,e){
var _298=e.pageX||e.clientX+dojo.html.body().scrollLeft;
var _299=e.pageY||e.clientY+dojo.html.body().scrollTop;
with(dojo.html){
var _29a=getAbsoluteX(node)+(getInnerWidth(node)/2);
var _29b=getAbsoluteY(node)+(getInnerHeight(node)/2);
}
with(dojo.html.gravity){
return ((_298<_29a?WEST:EAST)|(_299<_29b?NORTH:SOUTH));
}
};
dojo.html.gravity.NORTH=1;
dojo.html.gravity.SOUTH=1<<1;
dojo.html.gravity.EAST=1<<2;
dojo.html.gravity.WEST=1<<3;
dojo.html.overElement=function(_29c,e){
var _29e=e.pageX||e.clientX+dojo.html.body().scrollLeft;
var _29f=e.pageY||e.clientY+dojo.html.body().scrollTop;
with(dojo.html){
var top=getAbsoluteY(_29c);
var _2a1=top+getInnerHeight(_29c);
var left=getAbsoluteX(_29c);
var _2a3=left+getInnerWidth(_29c);
}
return (_29e>=left&&_29e<=_2a3&&_29f>=top&&_29f<=_2a1);
};
dojo.html.renderedTextContent=function(node){
var _2a5="";
if(node==null){
return _2a5;
}
for(var i=0;i<node.childNodes.length;i++){
switch(node.childNodes[i].nodeType){
case 1:
case 5:
var _2a7="unknown";
try{
_2a7=dojo.style.getStyle(node.childNodes[i],"display");
}
catch(E){
}
switch(_2a7){
case "block":
case "list-item":
case "run-in":
case "table":
case "table-row-group":
case "table-header-group":
case "table-footer-group":
case "table-row":
case "table-column-group":
case "table-column":
case "table-cell":
case "table-caption":
_2a5+="\n";
_2a5+=dojo.html.renderedTextContent(node.childNodes[i]);
_2a5+="\n";
break;
case "none":
break;
default:
if(node.childNodes[i].tagName&&node.childNodes[i].tagName.toLowerCase()=="br"){
_2a5+="\n";
}else{
_2a5+=dojo.html.renderedTextContent(node.childNodes[i]);
}
break;
}
break;
case 3:
case 2:
case 4:
var text=node.childNodes[i].nodeValue;
var _2a9="unknown";
try{
_2a9=dojo.style.getStyle(node,"text-transform");
}
catch(E){
}
switch(_2a9){
case "capitalize":
text=dojo.string.capitalize(text);
break;
case "uppercase":
text=text.toUpperCase();
break;
case "lowercase":
text=text.toLowerCase();
break;
default:
break;
}
switch(_2a9){
case "nowrap":
break;
case "pre-wrap":
break;
case "pre-line":
break;
case "pre":
break;
default:
text=text.replace(/\s+/," ");
if(/\s$/.test(_2a5)){
text.replace(/^\s/,"");
}
break;
}
_2a5+=text;
break;
default:
break;
}
}
return _2a5;
};
dojo.html.setActiveStyleSheet=function(_2aa){
var i,a,main;
for(i=0;(a=document.getElementsByTagName("link")[i]);i++){
if(a.getAttribute("rel").indexOf("style")!=-1&&a.getAttribute("title")){
a.disabled=true;
if(a.getAttribute("title")==_2aa){
a.disabled=false;
}
}
}
};
dojo.html.getActiveStyleSheet=function(){
var i,a;
for(i=0;(a=document.getElementsByTagName("link")[i]);i++){
if(a.getAttribute("rel").indexOf("style")!=-1&&a.getAttribute("title")&&!a.disabled){
return a.getAttribute("title");
}
}
return null;
};
dojo.html.getPreferredStyleSheet=function(){
var i,a;
for(i=0;(a=document.getElementsByTagName("link")[i]);i++){
if(a.getAttribute("rel").indexOf("style")!=-1&&a.getAttribute("rel").indexOf("alt")==-1&&a.getAttribute("title")){
return a.getAttribute("title");
}
}
return null;
};
dojo.html.body=function(){
return document.body||document.getElementsByTagName("body")[0];
};
dojo.html.createNodesFromText=function(txt,wrap){
var tn=document.createElement("div");
tn.style.visibility="hidden";
document.body.appendChild(tn);
tn.innerHTML=txt;
tn.normalize();
if(wrap){
var ret=[];
var fc=tn.firstChild;
ret[0]=((fc.nodeValue==" ")||(fc.nodeValue=="\t"))?fc.nextSibling:fc;
document.body.removeChild(tn);
return ret;
}
var _2b3=[];
for(var x=0;x<tn.childNodes.length;x++){
_2b3.push(tn.childNodes[x].cloneNode(true));
}
tn.style.display="none";
document.body.removeChild(tn);
return _2b3;
};
if(!dojo.evalObjPath("dojo.dom.createNodesFromText")){
dojo.dom.createNodesFromText=function(){
dojo.deprecated("dojo.dom.createNodesFromText","use dojo.html.createNodesFromText instead");
return dojo.html.createNodesFromText.apply(dojo.html,arguments);
};
}
dojo.html.isVisible=function(node){
return dojo.style.getComputedStyle(node||this.domNode,"display")!="none";
};
dojo.html.show=function(node){
if(node.style){
node.style.display=dojo.lang.inArray(["tr","td","th"],node.tagName.toLowerCase())?"":"block";
}
};
dojo.html.hide=function(node){
if(node.style){
node.style.display="none";
}
};
dojo.html.toCoordinateArray=function(_2b8,_2b9){
if(dojo.lang.isArray(_2b8)){
while(_2b8.length<4){
_2b8.push(0);
}
while(_2b8.length>4){
_2b8.pop();
}
var ret=_2b8;
}else{
var node=dojo.byId(_2b8);
var ret=[dojo.html.getAbsoluteX(node,_2b9),dojo.html.getAbsoluteY(node,_2b9),dojo.html.getInnerWidth(node),dojo.html.getInnerHeight(node)];
}
ret.x=ret[0];
ret.y=ret[1];
ret.w=ret[2];
ret.h=ret[3];
return ret;
};
dojo.html.placeOnScreen=function(node,_2bd,_2be,_2bf,_2c0){
if(dojo.lang.isArray(_2bd)){
_2c0=_2bf;
_2bf=_2be;
_2be=_2bd[1];
_2bd=_2bd[0];
}
if(!isNaN(_2bf)){
_2bf=[Number(_2bf),Number(_2bf)];
}else{
if(!dojo.lang.isArray(_2bf)){
_2bf=[0,0];
}
}
var _2c1=dojo.html.getScrollOffset();
var view=dojo.html.getViewportSize();
node=dojo.byId(node);
var w=node.offsetWidth+_2bf[0];
var h=node.offsetHeight+_2bf[1];
if(_2c0){
_2bd-=_2c1.x;
_2be-=_2c1.y;
}
var x=_2bd+w;
if(x>view.w){
x=view.w-w;
}else{
x=_2bd;
}
x=Math.max(_2bf[0],x)+_2c1.x;
var y=_2be+h;
if(y>view.h){
y=view.h-h;
}else{
y=_2be;
}
y=Math.max(_2bf[1],y)+_2c1.y;
node.style.left=x+"px";
node.style.top=y+"px";
var ret=[x,y];
ret.x=x;
ret.y=y;
return ret;
};
dojo.html.placeOnScreenPoint=function(node,_2c9,_2ca,_2cb,_2cc){
if(dojo.lang.isArray(_2c9)){
_2cc=_2cb;
_2cb=_2ca;
_2ca=_2c9[1];
_2c9=_2c9[0];
}
var _2cd=dojo.html.getScrollOffset();
var view=dojo.html.getViewportSize();
node=dojo.byId(node);
var w=node.offsetWidth;
var h=node.offsetHeight;
if(_2cc){
_2c9-=_2cd.x;
_2ca-=_2cd.y;
}
var x=-1,y=-1;
if(_2c9+w<=view.w&&_2ca+h<=view.h){
x=_2c9;
y=_2ca;
}
if((x<0||y<0)&&_2c9<=view.w&&_2ca+h<=view.h){
x=_2c9-w;
y=_2ca;
}
if((x<0||y<0)&&_2c9+w<=view.w&&_2ca<=view.h){
x=_2c9;
y=_2ca-h;
}
if((x<0||y<0)&&_2c9<=view.w&&_2ca<=view.h){
x=_2c9-w;
y=_2ca-h;
}
if(x<0||y<0||(x+w>view.w)||(y+h>view.h)){
return dojo.html.placeOnScreen(node,_2c9,_2ca,_2cb,_2cc);
}
x+=_2cd.x;
y+=_2cd.y;
node.style.left=x+"px";
node.style.top=y+"px";
var ret=[x,y];
ret.x=x;
ret.y=y;
return ret;
};
dojo.html.BackgroundIframe=function(){
if(this.ie){
this.iframe=document.createElement("<iframe frameborder='0' src='about:blank'>");
var s=this.iframe.style;
s.position="absolute";
s.left=s.top="0px";
s.zIndex=2;
s.display="none";
dojo.style.setOpacity(this.iframe,0);
dojo.html.body().appendChild(this.iframe);
}else{
this.enabled=false;
}
};
dojo.lang.extend(dojo.html.BackgroundIframe,{ie:dojo.render.html.ie,enabled:true,visibile:false,iframe:null,sizeNode:null,sizeCoords:null,size:function(node){
if(!this.ie||!this.enabled){
return;
}
if(dojo.dom.isNode(node)){
this.sizeNode=node;
}else{
if(arguments.length>0){
this.sizeNode=null;
this.sizeCoords=node;
}
}
this.update();
},update:function(){
if(!this.ie||!this.enabled){
return;
}
if(this.sizeNode){
this.sizeCoords=dojo.html.toCoordinateArray(this.sizeNode,true);
}else{
if(this.sizeCoords){
this.sizeCoords=dojo.html.toCoordinateArray(this.sizeCoords,true);
}else{
return;
}
}
var s=this.iframe.style;
var dims=this.sizeCoords;
s.width=dims.w+"px";
s.height=dims.h+"px";
s.left=dims.x+"px";
s.top=dims.y+"px";
},setZIndex:function(node){
if(!this.ie||!this.enabled){
return;
}
if(dojo.dom.isNode(node)){
this.iframe.zIndex=dojo.html.getStyle(node,"z-index")-1;
}else{
if(!isNaN(node)){
this.iframe.zIndex=node;
}
}
},show:function(node){
if(!this.ie||!this.enabled){
return;
}
this.size(node);
this.iframe.style.display="block";
},hide:function(){
if(!this.ie){
return;
}
var s=this.iframe.style;
s.display="none";
s.width=s.height="1px";
},remove:function(){
dojo.dom.removeNode(this.iframe);
}});
dojo.provide("dojo.math.curves");
dojo.require("dojo.math");
dojo.math.curves={Line:function(_2da,end){
this.start=_2da;
this.end=end;
this.dimensions=_2da.length;
for(var i=0;i<_2da.length;i++){
_2da[i]=Number(_2da[i]);
}
for(var i=0;i<end.length;i++){
end[i]=Number(end[i]);
}
this.getValue=function(n){
var _2de=new Array(this.dimensions);
for(var i=0;i<this.dimensions;i++){
_2de[i]=((this.end[i]-this.start[i])*n)+this.start[i];
}
return _2de;
};
return this;
},Bezier:function(pnts){
this.getValue=function(step){
if(step>=1){
return this.p[this.p.length-1];
}
if(step<=0){
return this.p[0];
}
var _2e2=new Array(this.p[0].length);
for(var k=0;j<this.p[0].length;k++){
_2e2[k]=0;
}
for(var j=0;j<this.p[0].length;j++){
var C=0;
var D=0;
for(var i=0;i<this.p.length;i++){
C+=this.p[i][j]*this.p[this.p.length-1][0]*dojo.math.bernstein(step,this.p.length,i);
}
for(var l=0;l<this.p.length;l++){
D+=this.p[this.p.length-1][0]*dojo.math.bernstein(step,this.p.length,l);
}
_2e2[j]=C/D;
}
return _2e2;
};
this.p=pnts;
return this;
},CatmullRom:function(pnts,c){
this.getValue=function(step){
var _2ec=step*(this.p.length-1);
var node=Math.floor(_2ec);
var _2ee=_2ec-node;
var i0=node-1;
if(i0<0){
i0=0;
}
var i=node;
var i1=node+1;
if(i1>=this.p.length){
i1=this.p.length-1;
}
var i2=node+2;
if(i2>=this.p.length){
i2=this.p.length-1;
}
var u=_2ee;
var u2=_2ee*_2ee;
var u3=_2ee*_2ee*_2ee;
var _2f6=new Array(this.p[0].length);
for(var k=0;k<this.p[0].length;k++){
var x1=(-this.c*this.p[i0][k])+((2-this.c)*this.p[i][k])+((this.c-2)*this.p[i1][k])+(this.c*this.p[i2][k]);
var x2=(2*this.c*this.p[i0][k])+((this.c-3)*this.p[i][k])+((3-2*this.c)*this.p[i1][k])+(-this.c*this.p[i2][k]);
var x3=(-this.c*this.p[i0][k])+(this.c*this.p[i1][k]);
var x4=this.p[i][k];
_2f6[k]=x1*u3+x2*u2+x3*u+x4;
}
return _2f6;
};
if(!c){
this.c=0.7;
}else{
this.c=c;
}
this.p=pnts;
return this;
},Arc:function(_2fc,end,ccw){
var _2ff=dojo.math.points.midpoint(_2fc,end);
var _300=dojo.math.points.translate(dojo.math.points.invert(_2ff),_2fc);
var rad=Math.sqrt(Math.pow(_300[0],2)+Math.pow(_300[1],2));
var _302=dojo.math.radToDeg(Math.atan(_300[1]/_300[0]));
if(_300[0]<0){
_302-=90;
}else{
_302+=90;
}
dojo.math.curves.CenteredArc.call(this,_2ff,rad,_302,_302+(ccw?-180:180));
},CenteredArc:function(_303,_304,_305,end){
this.center=_303;
this.radius=_304;
this.start=_305||0;
this.end=end;
this.getValue=function(n){
var _308=new Array(2);
var _309=dojo.math.degToRad(this.start+((this.end-this.start)*n));
_308[0]=this.center[0]+this.radius*Math.sin(_309);
_308[1]=this.center[1]-this.radius*Math.cos(_309);
return _308;
};
return this;
},Circle:function(_30a,_30b){
dojo.math.curves.CenteredArc.call(this,_30a,_30b,0,360);
return this;
},Path:function(){
var _30c=[];
var _30d=[];
var _30e=[];
var _30f=0;
this.add=function(_310,_311){
if(_311<0){
dojo.raise("dojo.math.curves.Path.add: weight cannot be less than 0");
}
_30c.push(_310);
_30d.push(_311);
_30f+=_311;
computeRanges();
};
this.remove=function(_312){
for(var i=0;i<_30c.length;i++){
if(_30c[i]==_312){
_30c.splice(i,1);
_30f-=_30d.splice(i,1)[0];
break;
}
}
computeRanges();
};
this.removeAll=function(){
_30c=[];
_30d=[];
_30f=0;
};
this.getValue=function(n){
var _315=false,value=0;
for(var i=0;i<_30e.length;i++){
var r=_30e[i];
if(n>=r[0]&&n<r[1]){
var subN=(n-r[0])/r[2];
value=_30c[i].getValue(subN);
_315=true;
break;
}
}
if(!_315){
value=_30c[_30c.length-1].getValue(1);
}
for(j=0;j<i;j++){
value=dojo.math.points.translate(value,_30c[j].getValue(1));
}
return value;
};
function computeRanges(){
var _319=0;
for(var i=0;i<_30d.length;i++){
var end=_319+_30d[i]/_30f;
var len=end-_319;
_30e[i]=[_319,end,len];
_319=end;
}
}
return this;
}};
dojo.provide("dojo.animation");
dojo.provide("dojo.animation.Animation");
dojo.require("dojo.lang");
dojo.require("dojo.math");
dojo.require("dojo.math.curves");
dojo.animation.Animation=function(_31d,_31e,_31f,_320,rate){
this.curve=_31d;
this.duration=_31e;
this.repeatCount=_320||0;
this.rate=rate||10;
if(_31f){
if(dojo.lang.isFunction(_31f.getValue)){
this.accel=_31f;
}else{
var i=0.35*_31f+0.5;
this.accel=new dojo.math.curves.CatmullRom([[0],[i],[1]],0.45);
}
}
};
dojo.lang.extend(dojo.animation.Animation,{curve:null,duration:0,repeatCount:0,accel:null,onBegin:null,onAnimate:null,onEnd:null,onPlay:null,onPause:null,onStop:null,handler:null,_animSequence:null,_startTime:null,_endTime:null,_lastFrame:null,_timer:null,_percent:0,_active:false,_paused:false,_startRepeatCount:0,play:function(_323){
if(_323){
clearTimeout(this._timer);
this._active=false;
this._paused=false;
this._percent=0;
}else{
if(this._active&&!this._paused){
return;
}
}
this._startTime=new Date().valueOf();
if(this._paused){
this._startTime-=(this.duration*this._percent/100);
}
this._endTime=this._startTime+this.duration;
this._lastFrame=this._startTime;
var e=new dojo.animation.AnimationEvent(this,null,this.curve.getValue(this._percent),this._startTime,this._startTime,this._endTime,this.duration,this._percent,0);
this._active=true;
this._paused=false;
if(this._percent==0){
if(!this._startRepeatCount){
this._startRepeatCount=this.repeatCount;
}
e.type="begin";
if(typeof this.handler=="function"){
this.handler(e);
}
if(typeof this.onBegin=="function"){
this.onBegin(e);
}
}
e.type="play";
if(typeof this.handler=="function"){
this.handler(e);
}
if(typeof this.onPlay=="function"){
this.onPlay(e);
}
if(this._animSequence){
this._animSequence._setCurrent(this);
}
this._cycle();
},pause:function(){
clearTimeout(this._timer);
if(!this._active){
return;
}
this._paused=true;
var e=new dojo.animation.AnimationEvent(this,"pause",this.curve.getValue(this._percent),this._startTime,new Date().valueOf(),this._endTime,this.duration,this._percent,0);
if(typeof this.handler=="function"){
this.handler(e);
}
if(typeof this.onPause=="function"){
this.onPause(e);
}
},playPause:function(){
if(!this._active||this._paused){
this.play();
}else{
this.pause();
}
},gotoPercent:function(pct,_327){
clearTimeout(this._timer);
this._active=true;
this._paused=true;
this._percent=pct;
if(_327){
this.play();
}
},stop:function(_328){
clearTimeout(this._timer);
var step=this._percent/100;
if(_328){
step=1;
}
var e=new dojo.animation.AnimationEvent(this,"stop",this.curve.getValue(step),this._startTime,new Date().valueOf(),this._endTime,this.duration,this._percent,Math.round(fps));
if(typeof this.handler=="function"){
this.handler(e);
}
if(typeof this.onStop=="function"){
this.onStop(e);
}
this._active=false;
this._paused=false;
},status:function(){
if(this._active){
return this._paused?"paused":"playing";
}else{
return "stopped";
}
},_cycle:function(){
clearTimeout(this._timer);
if(this._active){
var curr=new Date().valueOf();
var step=(curr-this._startTime)/(this._endTime-this._startTime);
fps=1000/(curr-this._lastFrame);
this._lastFrame=curr;
if(step>=1){
step=1;
this._percent=100;
}else{
this._percent=step*100;
}
if(this.accel&&this.accel.getValue){
step=this.accel.getValue(step);
}
var e=new dojo.animation.AnimationEvent(this,"animate",this.curve.getValue(step),this._startTime,curr,this._endTime,this.duration,this._percent,Math.round(fps));
if(typeof this.handler=="function"){
this.handler(e);
}
if(typeof this.onAnimate=="function"){
this.onAnimate(e);
}
if(step<1){
this._timer=setTimeout(dojo.lang.hitch(this,"_cycle"),this.rate);
}else{
e.type="end";
this._active=false;
if(typeof this.handler=="function"){
this.handler(e);
}
if(typeof this.onEnd=="function"){
this.onEnd(e);
}
if(this.repeatCount>0){
this.repeatCount--;
this.play(true);
}else{
if(this.repeatCount==-1){
this.play(true);
}else{
if(this._startRepeatCount){
this.repeatCount=this._startRepeatCount;
this._startRepeatCount=0;
}
if(this._animSequence){
this._animSequence._playNext();
}
}
}
}
}
}});
dojo.animation.AnimationEvent=function(anim,type,_330,_331,_332,_333,dur,pct,fps){
this.type=type;
this.animation=anim;
this.coords=_330;
this.x=_330[0];
this.y=_330[1];
this.z=_330[2];
this.startTime=_331;
this.currentTime=_332;
this.endTime=_333;
this.duration=dur;
this.percent=pct;
this.fps=fps;
};
dojo.lang.extend(dojo.animation.AnimationEvent,{coordsAsInts:function(){
var _337=new Array(this.coords.length);
for(var i=0;i<this.coords.length;i++){
_337[i]=Math.round(this.coords[i]);
}
return _337;
}});
dojo.animation.AnimationSequence=function(_339){
this.repeatCount=_339||0;
};
dojo.lang.extend(dojo.animation.AnimationSequence,{repeateCount:0,_anims:[],_currAnim:-1,onBegin:null,onEnd:null,onNext:null,handler:null,add:function(){
for(var i=0;i<arguments.length;i++){
this._anims.push(arguments[i]);
arguments[i]._animSequence=this;
}
},remove:function(anim){
for(var i=0;i<this._anims.length;i++){
if(this._anims[i]==anim){
this._anims[i]._animSequence=null;
this._anims.splice(i,1);
break;
}
}
},removeAll:function(){
for(var i=0;i<this._anims.length;i++){
this._anims[i]._animSequence=null;
}
this._anims=[];
this._currAnim=-1;
},clear:function(){
this.removeAll();
},play:function(_33e){
if(this._anims.length==0){
return;
}
if(_33e||!this._anims[this._currAnim]){
this._currAnim=0;
}
if(this._anims[this._currAnim]){
if(this._currAnim==0){
var e={type:"begin",animation:this._anims[this._currAnim]};
if(typeof this.handler=="function"){
this.handler(e);
}
if(typeof this.onBegin=="function"){
this.onBegin(e);
}
}
this._anims[this._currAnim].play(_33e);
}
},pause:function(){
if(this._anims[this._currAnim]){
this._anims[this._currAnim].pause();
}
},playPause:function(){
if(this._anims.length==0){
return;
}
if(this._currAnim==-1){
this._currAnim=0;
}
if(this._anims[this._currAnim]){
this._anims[this._currAnim].playPause();
}
},stop:function(){
if(this._anims[this._currAnim]){
this._anims[this._currAnim].stop();
}
},status:function(){
if(this._anims[this._currAnim]){
return this._anims[this._currAnim].status();
}else{
return "stopped";
}
},_setCurrent:function(anim){
for(var i=0;i<this._anims.length;i++){
if(this._anims[i]==anim){
this._currAnim=i;
break;
}
}
},_playNext:function(){
if(this._currAnim==-1||this._anims.length==0){
return;
}
this._currAnim++;
if(this._anims[this._currAnim]){
var e={type:"next",animation:this._anims[this._currAnim]};
if(typeof this.handler=="function"){
this.handler(e);
}
if(typeof this.onNext=="function"){
this.onNext(e);
}
this._anims[this._currAnim].play(true);
}else{
var e={type:"end",animation:this._anims[this._anims.length-1]};
if(typeof this.handler=="function"){
this.handler(e);
}
if(typeof this.onEnd=="function"){
this.onEnd(e);
}
if(this.repeatCount>0){
this._currAnim=0;
this.repeatCount--;
this._anims[this._currAnim].play(true);
}else{
if(this.repeatCount==-1){
this._currAnim=0;
this._anims[this._currAnim].play(true);
}else{
this._currAnim=-1;
}
}
}
}});
dojo.hostenv.conditionalLoadModule({common:["dojo.animation.Animation",false,false]});
dojo.hostenv.moduleLoaded("dojo.animation.*");
dojo.require("dojo.lang");
dojo.provide("dojo.event");
dojo.event=new function(){
this.canTimeout=dojo.lang.isFunction(dj_global["setTimeout"])||dojo.lang.isAlien(dj_global["setTimeout"]);
this.createFunctionPair=function(obj,cb){
var ret=[];
if(typeof obj=="function"){
ret[1]=dojo.lang.nameAnonFunc(obj,dj_global);
ret[0]=dj_global;
return ret;
}else{
if((typeof obj=="object")&&(typeof cb=="string")){
return [obj,cb];
}else{
if((typeof obj=="object")&&(typeof cb=="function")){
ret[1]=dojo.lang.nameAnonFunc(cb,obj);
ret[0]=obj;
return ret;
}
}
}
return null;
};
function interpolateArgs(args){
var ao={srcObj:dj_global,srcFunc:null,adviceObj:dj_global,adviceFunc:null,aroundObj:null,aroundFunc:null,adviceType:(args.length>2)?args[0]:"after",precedence:"last",once:false,delay:null,rate:0,adviceMsg:false};
switch(args.length){
case 0:
return;
case 1:
return;
case 2:
ao.srcFunc=args[0];
ao.adviceFunc=args[1];
break;
case 3:
if((typeof args[0]=="object")&&(typeof args[1]=="string")&&(typeof args[2]=="string")){
ao.adviceType="after";
ao.srcObj=args[0];
ao.srcFunc=args[1];
ao.adviceFunc=args[2];
}else{
if((typeof args[1]=="string")&&(typeof args[2]=="string")){
ao.srcFunc=args[1];
ao.adviceFunc=args[2];
}else{
if((typeof args[0]=="object")&&(typeof args[1]=="string")&&(typeof args[2]=="function")){
ao.adviceType="after";
ao.srcObj=args[0];
ao.srcFunc=args[1];
var _348=dojo.lang.nameAnonFunc(args[2],ao.adviceObj);
ao.adviceObj[_348]=args[2];
ao.adviceFunc=_348;
}else{
if((typeof args[0]=="function")&&(typeof args[1]=="object")&&(typeof args[2]=="string")){
ao.adviceType="after";
ao.srcObj=dj_global;
var _348=dojo.lang.nameAnonFunc(args[0],ao.srcObj);
ao.srcObj[_348]=args[0];
ao.srcFunc=_348;
ao.adviceObj=args[1];
ao.adviceFunc=args[2];
}
}
}
}
break;
case 4:
if((typeof args[0]=="object")&&(typeof args[2]=="object")){
ao.adviceType="after";
ao.srcObj=args[0];
ao.srcFunc=args[1];
ao.adviceObj=args[2];
ao.adviceFunc=args[3];
}else{
if((typeof args[1]).toLowerCase()=="object"){
ao.srcObj=args[1];
ao.srcFunc=args[2];
ao.adviceObj=dj_global;
ao.adviceFunc=args[3];
}else{
if((typeof args[2]).toLowerCase()=="object"){
ao.srcObj=dj_global;
ao.srcFunc=args[1];
ao.adviceObj=args[2];
ao.adviceFunc=args[3];
}else{
ao.srcObj=ao.adviceObj=ao.aroundObj=dj_global;
ao.srcFunc=args[1];
ao.adviceFunc=args[2];
ao.aroundFunc=args[3];
}
}
}
break;
case 6:
ao.srcObj=args[1];
ao.srcFunc=args[2];
ao.adviceObj=args[3];
ao.adviceFunc=args[4];
ao.aroundFunc=args[5];
ao.aroundObj=dj_global;
break;
default:
ao.srcObj=args[1];
ao.srcFunc=args[2];
ao.adviceObj=args[3];
ao.adviceFunc=args[4];
ao.aroundObj=args[5];
ao.aroundFunc=args[6];
ao.once=args[7];
ao.delay=args[8];
ao.rate=args[9];
ao.adviceMsg=args[10];
break;
}
if((typeof ao.srcFunc).toLowerCase()!="string"){
ao.srcFunc=dojo.lang.getNameInObj(ao.srcObj,ao.srcFunc);
}
if((typeof ao.adviceFunc).toLowerCase()!="string"){
ao.adviceFunc=dojo.lang.getNameInObj(ao.adviceObj,ao.adviceFunc);
}
if((ao.aroundObj)&&((typeof ao.aroundFunc).toLowerCase()!="string")){
ao.aroundFunc=dojo.lang.getNameInObj(ao.aroundObj,ao.aroundFunc);
}
if(!ao.srcObj){
dojo.raise("bad srcObj for srcFunc: "+ao.srcFunc);
}
if(!ao.adviceObj){
dojo.raise("bad adviceObj for adviceFunc: "+ao.adviceFunc);
}
return ao;
}
this.connect=function(){
var ao=interpolateArgs(arguments);
var mjp=dojo.event.MethodJoinPoint.getForMethod(ao.srcObj,ao.srcFunc);
if(ao.adviceFunc){
var mjp2=dojo.event.MethodJoinPoint.getForMethod(ao.adviceObj,ao.adviceFunc);
}
mjp.kwAddAdvice(ao);
return mjp;
};
this.connectBefore=function(){
var args=["before"];
for(var i=0;i<arguments.length;i++){
args.push(arguments[i]);
}
return this.connect.apply(this,args);
};
this.connectAround=function(){
var args=["around"];
for(var i=0;i<arguments.length;i++){
args.push(arguments[i]);
}
return this.connect.apply(this,args);
};
this._kwConnectImpl=function(_350,_351){
var fn=(_351)?"disconnect":"connect";
if(typeof _350["srcFunc"]=="function"){
_350.srcObj=_350["srcObj"]||dj_global;
var _353=dojo.lang.nameAnonFunc(_350.srcFunc,_350.srcObj);
_350.srcFunc=_353;
}
if(typeof _350["adviceFunc"]=="function"){
_350.adviceObj=_350["adviceObj"]||dj_global;
var _353=dojo.lang.nameAnonFunc(_350.adviceFunc,_350.adviceObj);
_350.adviceFunc=_353;
}
return dojo.event[fn]((_350["type"]||_350["adviceType"]||"after"),_350["srcObj"]||dj_global,_350["srcFunc"],_350["adviceObj"]||_350["targetObj"]||dj_global,_350["adviceFunc"]||_350["targetFunc"],_350["aroundObj"],_350["aroundFunc"],_350["once"],_350["delay"],_350["rate"],_350["adviceMsg"]||false);
};
this.kwConnect=function(_354){
return this._kwConnectImpl(_354,false);
};
this.disconnect=function(){
var ao=interpolateArgs(arguments);
if(!ao.adviceFunc){
return;
}
var mjp=dojo.event.MethodJoinPoint.getForMethod(ao.srcObj,ao.srcFunc);
return mjp.removeAdvice(ao.adviceObj,ao.adviceFunc,ao.adviceType,ao.once);
};
this.kwDisconnect=function(_357){
return this._kwConnectImpl(_357,true);
};
};
dojo.event.MethodInvocation=function(_358,obj,args){
this.jp_=_358;
this.object=obj;
this.args=[];
for(var x=0;x<args.length;x++){
this.args[x]=args[x];
}
this.around_index=-1;
};
dojo.event.MethodInvocation.prototype.proceed=function(){
this.around_index++;
if(this.around_index>=this.jp_.around.length){
return this.jp_.object[this.jp_.methodname].apply(this.jp_.object,this.args);
}else{
var ti=this.jp_.around[this.around_index];
var mobj=ti[0]||dj_global;
var meth=ti[1];
return mobj[meth].call(mobj,this);
}
};
dojo.event.MethodJoinPoint=function(obj,_360){
this.object=obj||dj_global;
this.methodname=_360;
this.methodfunc=this.object[_360];
this.before=[];
this.after=[];
this.around=[];
};
dojo.event.MethodJoinPoint.getForMethod=function(obj,_362){
if(!obj){
obj=dj_global;
}
if(!obj[_362]){
obj[_362]=function(){
};
}else{
if((!dojo.lang.isFunction(obj[_362]))&&(!dojo.lang.isAlien(obj[_362]))){
return null;
}
}
var _363=_362+"$joinpoint";
var _364=_362+"$joinpoint$method";
var _365=obj[_363];
if(!_365){
var _366=false;
if(dojo.event["browser"]){
if((obj["attachEvent"])||(obj["nodeType"])||(obj["addEventListener"])){
_366=true;
dojo.event.browser.addClobberNodeAttrs(obj,[_363,_364,_362]);
}
}
obj[_364]=obj[_362];
_365=obj[_363]=new dojo.event.MethodJoinPoint(obj,_364);
obj[_362]=function(){
var args=[];
if((_366)&&(!arguments.length)&&(window.event)){
args.push(dojo.event.browser.fixEvent(window.event));
}else{
for(var x=0;x<arguments.length;x++){
if((x==0)&&(_366)&&(dojo.event.browser.isEvent(arguments[x]))){
args.push(dojo.event.browser.fixEvent(arguments[x]));
}else{
args.push(arguments[x]);
}
}
}
return _365.run.apply(_365,args);
};
}
return _365;
};
dojo.lang.extend(dojo.event.MethodJoinPoint,{unintercept:function(){
this.object[this.methodname]=this.methodfunc;
},run:function(){
var obj=this.object||dj_global;
var args=arguments;
var _36b=[];
for(var x=0;x<args.length;x++){
_36b[x]=args[x];
}
var _36d=function(marr){
if(!marr){
dojo.debug("Null argument to unrollAdvice()");
return;
}
var _36f=marr[0]||dj_global;
var _370=marr[1];
if(!_36f[_370]){
dojo.raise("function \""+_370+"\" does not exist on \""+_36f+"\"");
}
var _371=marr[2]||dj_global;
var _372=marr[3];
var msg=marr[6];
var _374;
var to={args:[],jp_:this,object:obj,proceed:function(){
return _36f[_370].apply(_36f,to.args);
}};
to.args=_36b;
var _376=parseInt(marr[4]);
var _377=((!isNaN(_376))&&(marr[4]!==null)&&(typeof marr[4]!="undefined"));
if(marr[5]){
var rate=parseInt(marr[5]);
var cur=new Date();
var _37a=false;
if((marr["last"])&&((cur-marr.last)<=rate)){
if(dojo.event.canTimeout){
if(marr["delayTimer"]){
clearTimeout(marr.delayTimer);
}
var tod=parseInt(rate*2);
var mcpy=dojo.lang.shallowCopy(marr);
marr.delayTimer=setTimeout(function(){
mcpy[5]=0;
_36d(mcpy);
},tod);
}
return;
}else{
marr.last=cur;
}
}
if(_372){
_371[_372].call(_371,to);
}else{
if((_377)&&((dojo.render.html)||(dojo.render.svg))){
dj_global["setTimeout"](function(){
if(msg){
_36f[_370].call(_36f,to);
}else{
_36f[_370].apply(_36f,args);
}
},_376);
}else{
if(msg){
_36f[_370].call(_36f,to);
}else{
_36f[_370].apply(_36f,args);
}
}
}
};
if(this.before.length>0){
dojo.lang.forEach(this.before,_36d,true);
}
var _37d;
if(this.around.length>0){
var mi=new dojo.event.MethodInvocation(this,obj,args);
_37d=mi.proceed();
}else{
if(this.methodfunc){
_37d=this.object[this.methodname].apply(this.object,args);
}
}
if(this.after.length>0){
dojo.lang.forEach(this.after,_36d,true);
}
return (this.methodfunc)?_37d:null;
},getArr:function(kind){
var arr=this.after;
if((typeof kind=="string")&&(kind.indexOf("before")!=-1)){
arr=this.before;
}else{
if(kind=="around"){
arr=this.around;
}
}
return arr;
},kwAddAdvice:function(args){
this.addAdvice(args["adviceObj"],args["adviceFunc"],args["aroundObj"],args["aroundFunc"],args["adviceType"],args["precedence"],args["once"],args["delay"],args["rate"],args["adviceMsg"]);
},addAdvice:function(_382,_383,_384,_385,_386,_387,once,_389,rate,_38b){
var arr=this.getArr(_386);
if(!arr){
dojo.raise("bad this: "+this);
}
var ao=[_382,_383,_384,_385,_389,rate,_38b];
if(once){
if(this.hasAdvice(_382,_383,_386,arr)>=0){
return;
}
}
if(_387=="first"){
arr.unshift(ao);
}else{
arr.push(ao);
}
},hasAdvice:function(_38e,_38f,_390,arr){
if(!arr){
arr=this.getArr(_390);
}
var ind=-1;
for(var x=0;x<arr.length;x++){
if((arr[x][0]==_38e)&&(arr[x][1]==_38f)){
ind=x;
}
}
return ind;
},removeAdvice:function(_394,_395,_396,once){
var arr=this.getArr(_396);
var ind=this.hasAdvice(_394,_395,_396,arr);
if(ind==-1){
return false;
}
while(ind!=-1){
arr.splice(ind,1);
if(once){
break;
}
ind=this.hasAdvice(_394,_395,_396,arr);
}
return true;
}});
dojo.require("dojo.event");
dojo.provide("dojo.event.topic");
dojo.event.topic=new function(){
this.topics={};
this.getTopic=function(_39a){
if(!this.topics[_39a]){
this.topics[_39a]=new this.TopicImpl(_39a);
}
return this.topics[_39a];
};
this.registerPublisher=function(_39b,obj,_39d){
var _39b=this.getTopic(_39b);
_39b.registerPublisher(obj,_39d);
};
this.subscribe=function(_39e,obj,_3a0){
var _39e=this.getTopic(_39e);
_39e.subscribe(obj,_3a0);
};
this.unsubscribe=function(_3a1,obj,_3a3){
var _3a1=this.getTopic(_3a1);
_3a1.unsubscribe(obj,_3a3);
};
this.publish=function(_3a4,_3a5){
var _3a4=this.getTopic(_3a4);
var args=[];
if((arguments.length==2)&&(_3a5.length)&&(typeof _3a5!="string")){
args=_3a5;
}else{
var args=[];
for(var x=1;x<arguments.length;x++){
args.push(arguments[x]);
}
}
_3a4.sendMessage.apply(_3a4,args);
};
};
dojo.event.topic.TopicImpl=function(_3a8){
this.topicName=_3a8;
var self=this;
self.subscribe=function(_3aa,_3ab){
dojo.event.connect("before",self,"sendMessage",_3aa,_3ab);
};
self.unsubscribe=function(_3ac,_3ad){
dojo.event.disconnect("before",self,"sendMessage",_3ac,_3ad);
};
self.registerPublisher=function(_3ae,_3af){
dojo.event.connect(_3ae,_3af,self,"sendMessage");
};
self.sendMessage=function(_3b0){
};
};
dojo.provide("dojo.event.browser");
dojo.require("dojo.event");
dojo_ie_clobber=new function(){
this.clobberNodes=[];
function nukeProp(node,prop){
try{
node[prop]=null;
}
catch(e){
}
try{
delete node[prop];
}
catch(e){
}
try{
node.removeAttribute(prop);
}
catch(e){
}
}
this.clobber=function(_3b3){
var na;
var tna;
if(_3b3){
tna=_3b3.getElementsByTagName("*");
na=[_3b3];
for(var x=0;x<tna.length;x++){
if(tna[x]["__doClobber__"]){
na.push(tna[x]);
}
}
}else{
try{
window.onload=null;
}
catch(e){
}
na=(this.clobberNodes.length)?this.clobberNodes:document.all;
}
tna=null;
var _3b7={};
for(var i=na.length-1;i>=0;i=i-1){
var el=na[i];
if(el["__clobberAttrs__"]){
for(var j=0;j<el.__clobberAttrs__.length;j++){
nukeProp(el,el.__clobberAttrs__[j]);
}
nukeProp(el,"__clobberAttrs__");
nukeProp(el,"__doClobber__");
}
}
na=null;
};
};
if(dojo.render.html.ie){
window.onunload=function(){
dojo_ie_clobber.clobber();
try{
if((dojo["widget"])&&(dojo.widget["manager"])){
dojo.widget.manager.destroyAll();
}
}
catch(e){
}
try{
window.onload=null;
}
catch(e){
}
try{
window.onunload=null;
}
catch(e){
}
dojo_ie_clobber.clobberNodes=[];
};
}
dojo.event.browser=new function(){
var _3bb=0;
this.clean=function(node){
if(dojo.render.html.ie){
dojo_ie_clobber.clobber(node);
}
};
this.addClobberNode=function(node){
if(!node["__doClobber__"]){
node.__doClobber__=true;
dojo_ie_clobber.clobberNodes.push(node);
node.__clobberAttrs__=[];
}
};
this.addClobberNodeAttrs=function(node,_3bf){
this.addClobberNode(node);
for(var x=0;x<_3bf.length;x++){
node.__clobberAttrs__.push(_3bf[x]);
}
};
this.removeListener=function(node,_3c2,fp,_3c4){
if(!_3c4){
var _3c4=false;
}
_3c2=_3c2.toLowerCase();
if(_3c2.substr(0,2)=="on"){
_3c2=_3c2.substr(2);
}
if(node.removeEventListener){
node.removeEventListener(_3c2,fp,_3c4);
}
};
this.addListener=function(node,_3c6,fp,_3c8,_3c9){
if(!node){
return;
}
if(!_3c8){
var _3c8=false;
}
_3c6=_3c6.toLowerCase();
if(_3c6.substr(0,2)!="on"){
_3c6="on"+_3c6;
}
if(!_3c9){
var _3ca=function(evt){
if(!evt){
evt=window.event;
}
var ret=fp(dojo.event.browser.fixEvent(evt));
if(_3c8){
dojo.event.browser.stopEvent(evt);
}
return ret;
};
}else{
_3ca=fp;
}
if(node.addEventListener){
node.addEventListener(_3c6.substr(2),_3ca,_3c8);
return _3ca;
}else{
if(typeof node[_3c6]=="function"){
var _3cd=node[_3c6];
node[_3c6]=function(e){
_3cd(e);
return _3ca(e);
};
}else{
node[_3c6]=_3ca;
}
if(dojo.render.html.ie){
this.addClobberNodeAttrs(node,[_3c6]);
}
return _3ca;
}
};
this.isEvent=function(obj){
return (typeof Event!="undefined")&&(obj.eventPhase);
};
this.currentEvent=null;
this.callListener=function(_3d0,_3d1){
if(typeof _3d0!="function"){
dojo.raise("listener not a function: "+_3d0);
}
dojo.event.browser.currentEvent.currentTarget=_3d1;
return _3d0.call(_3d1,dojo.event.browser.currentEvent);
};
this.stopPropagation=function(){
dojo.event.browser.currentEvent.cancelBubble=true;
};
this.preventDefault=function(){
dojo.event.browser.currentEvent.returnValue=false;
};
this.keys={KEY_BACKSPACE:8,KEY_TAB:9,KEY_ENTER:13,KEY_SHIFT:16,KEY_CTRL:17,KEY_ALT:18,KEY_PAUSE:19,KEY_CAPS_LOCK:20,KEY_ESCAPE:27,KEY_SPACE:32,KEY_PAGE_UP:33,KEY_PAGE_DOWN:34,KEY_END:35,KEY_HOME:36,KEY_LEFT_ARROW:37,KEY_UP_ARROW:38,KEY_RIGHT_ARROW:39,KEY_DOWN_ARROW:40,KEY_INSERT:45,KEY_DELETE:46,KEY_LEFT_WINDOW:91,KEY_RIGHT_WINDOW:92,KEY_SELECT:93,KEY_F1:112,KEY_F2:113,KEY_F3:114,KEY_F4:115,KEY_F5:116,KEY_F6:117,KEY_F7:118,KEY_F8:119,KEY_F9:120,KEY_F10:121,KEY_F11:122,KEY_F12:123,KEY_NUM_LOCK:144,KEY_SCROLL_LOCK:145};
this.revKeys=[];
for(var key in this.keys){
this.revKeys[this.keys[key]]=key;
}
this.fixEvent=function(evt){
if((!evt)&&(window["event"])){
var evt=window.event;
}
if((evt["type"])&&(evt["type"].indexOf("key")==0)){
evt.keys=this.revKeys;
for(var key in this.keys){
evt[key]=this.keys[key];
}
if((dojo.render.html.ie)&&(evt["type"]=="keypress")){
evt.charCode=evt.keyCode;
}
}
if(dojo.render.html.ie){
if(!evt.target){
evt.target=evt.srcElement;
}
if(!evt.currentTarget){
evt.currentTarget=evt.srcElement;
}
if(!evt.layerX){
evt.layerX=evt.offsetX;
}
if(!evt.layerY){
evt.layerY=evt.offsetY;
}
if(evt.fromElement){
evt.relatedTarget=evt.fromElement;
}
if(evt.toElement){
evt.relatedTarget=evt.toElement;
}
this.currentEvent=evt;
evt.callListener=this.callListener;
evt.stopPropagation=this.stopPropagation;
evt.preventDefault=this.preventDefault;
}
return evt;
};
this.stopEvent=function(ev){
if(window.event){
ev.returnValue=false;
ev.cancelBubble=true;
}else{
ev.preventDefault();
ev.stopPropagation();
}
};
};
dojo.hostenv.conditionalLoadModule({common:["dojo.event","dojo.event.topic"],browser:["dojo.event.browser"]});
dojo.hostenv.moduleLoaded("dojo.event.*");
dojo.provide("dojo.fx.html");
dojo.require("dojo.html");
dojo.require("dojo.style");
dojo.require("dojo.lang");
dojo.require("dojo.animation.*");
dojo.require("dojo.event.*");
dojo.require("dojo.graphics.color");
dojo.fx.html._makeFadeable=function(node){
if(dojo.render.html.ie){
if((node.style.zoom.length==0)&&(dojo.style.getStyle(node,"zoom")=="normal")){
node.style.zoom="1";
}
if((node.style.width.length==0)&&(dojo.style.getStyle(node,"width")=="auto")){
node.style.width="auto";
}
}
};
dojo.fx.html.fadeOut=function(node,_3d8,_3d9,_3da){
return dojo.fx.html.fade(node,_3d8,dojo.style.getOpacity(node),0,_3d9,_3da);
};
dojo.fx.html.fadeIn=function(node,_3dc,_3dd,_3de){
return dojo.fx.html.fade(node,_3dc,dojo.style.getOpacity(node),1,_3dd,_3de);
};
dojo.fx.html.fadeHide=function(node,_3e0,_3e1,_3e2){
node=dojo.byId(node);
if(!_3e0){
_3e0=150;
}
return dojo.fx.html.fadeOut(node,_3e0,function(node){
node.style.display="none";
if(typeof _3e1=="function"){
_3e1(node);
}
});
};
dojo.fx.html.fadeShow=function(node,_3e5,_3e6,_3e7){
node=dojo.byId(node);
if(!_3e5){
_3e5=150;
}
node.style.display="block";
return dojo.fx.html.fade(node,_3e5,0,1,_3e6,_3e7);
};
dojo.fx.html.fade=function(node,_3e9,_3ea,_3eb,_3ec,_3ed){
node=dojo.byId(node);
dojo.fx.html._makeFadeable(node);
var anim=new dojo.animation.Animation(new dojo.math.curves.Line([_3ea],[_3eb]),_3e9,0);
dojo.event.connect(anim,"onAnimate",function(e){
dojo.style.setOpacity(node,e.x);
});
if(_3ec){
dojo.event.connect(anim,"onEnd",function(e){
_3ec(node,anim);
});
}
if(!_3ed){
anim.play(true);
}
return anim;
};
dojo.fx.html.slideTo=function(node,_3f2,_3f3,_3f4,_3f5){
if(!dojo.lang.isNumber(_3f2)){
var tmp=_3f2;
_3f2=_3f3;
_3f3=tmp;
}
node=dojo.byId(node);
var top=node.offsetTop;
var left=node.offsetLeft;
var pos=dojo.style.getComputedStyle(node,"position");
if(pos=="relative"||pos=="static"){
top=parseInt(dojo.style.getComputedStyle(node,"top"))||0;
left=parseInt(dojo.style.getComputedStyle(node,"left"))||0;
}
return dojo.fx.html.slide(node,_3f2,[left,top],_3f3,_3f4,_3f5);
};
dojo.fx.html.slideBy=function(node,_3fb,_3fc,_3fd,_3fe){
if(!dojo.lang.isNumber(_3fb)){
var tmp=_3fb;
_3fb=_3fc;
_3fc=tmp;
}
node=dojo.byId(node);
var top=node.offsetTop;
var left=node.offsetLeft;
var pos=dojo.style.getComputedStyle(node,"position");
if(pos=="relative"||pos=="static"){
top=parseInt(dojo.style.getComputedStyle(node,"top"))||0;
left=parseInt(dojo.style.getComputedStyle(node,"left"))||0;
}
return dojo.fx.html.slideTo(node,_3fb,[left+_3fc[0],top+_3fc[1]],_3fd,_3fe);
};
dojo.fx.html.slide=function(node,_404,_405,_406,_407,_408){
if(!dojo.lang.isNumber(_404)){
var tmp=_404;
_404=_406;
_406=_405;
_405=tmp;
}
node=dojo.byId(node);
if(dojo.style.getComputedStyle(node,"position")=="static"){
node.style.position="relative";
}
var anim=new dojo.animation.Animation(new dojo.math.curves.Line(_405,_406),_404,0);
dojo.event.connect(anim,"onAnimate",function(e){
with(node.style){
left=e.x+"px";
top=e.y+"px";
}
});
if(_407){
dojo.event.connect(anim,"onEnd",function(e){
_407(node,anim);
});
}
if(!_408){
anim.play(true);
}
return anim;
};
dojo.fx.html.colorFadeIn=function(node,_40e,_40f,_410,_411,_412){
if(!dojo.lang.isNumber(_40e)){
var tmp=_40e;
_40e=_40f;
_40f=tmp;
}
node=dojo.byId(node);
var _414=dojo.html.getBackgroundColor(node);
var bg=dojo.style.getStyle(node,"background-color").toLowerCase();
var _416=bg=="transparent"||bg=="rgba(0, 0, 0, 0)";
while(_414.length>3){
_414.pop();
}
var rgb=new dojo.graphics.color.Color(_40f).toRgb();
var anim=dojo.fx.html.colorFade(node,_40e,_40f,_414,_411,true);
dojo.event.connect(anim,"onEnd",function(e){
if(_416){
node.style.backgroundColor="transparent";
}
});
if(_410>0){
node.style.backgroundColor="rgb("+rgb.join(",")+")";
if(!_412){
setTimeout(function(){
anim.play(true);
},_410);
}
}else{
if(!_412){
anim.play(true);
}
}
return anim;
};
dojo.fx.html.highlight=dojo.fx.html.colorFadeIn;
dojo.fx.html.colorFadeFrom=dojo.fx.html.colorFadeIn;
dojo.fx.html.colorFadeOut=function(node,_41b,_41c,_41d,_41e,_41f){
if(!dojo.lang.isNumber(_41b)){
var tmp=_41b;
_41b=_41c;
_41c=tmp;
}
node=dojo.byId(node);
var _421=new dojo.graphics.color.Color(dojo.html.getBackgroundColor(node)).toRgb();
var rgb=new dojo.graphics.color.Color(_41c).toRgb();
var anim=dojo.fx.html.colorFade(node,_41b,_421,rgb,_41e,_41d>0||_41f);
if(_41d>0){
node.style.backgroundColor="rgb("+_421.join(",")+")";
if(!_41f){
setTimeout(function(){
anim.play(true);
},_41d);
}
}
return anim;
};
dojo.fx.html.unhighlight=dojo.fx.html.colorFadeOut;
dojo.fx.html.colorFadeTo=dojo.fx.html.colorFadeOut;
dojo.fx.html.colorFade=function(node,_425,_426,_427,_428,_429){
if(!dojo.lang.isNumber(_425)){
var tmp=_425;
_425=_427;
_427=_426;
_426=tmp;
}
node=dojo.byId(node);
var _42b=new dojo.graphics.color.Color(_426).toRgb();
var _42c=new dojo.graphics.color.Color(_427).toRgb();
var anim=new dojo.animation.Animation(new dojo.math.curves.Line(_42b,_42c),_425,0);
dojo.event.connect(anim,"onAnimate",function(e){
node.style.backgroundColor="rgb("+e.coordsAsInts().join(",")+")";
});
if(_428){
dojo.event.connect(anim,"onEnd",function(e){
_428(node,anim);
});
}
if(!_429){
anim.play(true);
}
return anim;
};
dojo.fx.html.wipeIn=function(node,_431,_432,_433){
node=dojo.byId(node);
var _434=dojo.html.getStyle(node,"height");
var _435=dojo.lang.inArray(node.tagName.toLowerCase(),["tr","td","th"])?"":"block";
node.style.display=_435;
var _436=node.offsetHeight;
var anim=dojo.fx.html.wipeInToHeight(node,_431,_436,function(e){
node.style.height=_434||"auto";
if(_432){
_432(node,anim);
}
},_433);
};
dojo.fx.html.wipeInToHeight=function(node,_43a,_43b,_43c,_43d){
node=dojo.byId(node);
var _43e=dojo.html.getStyle(node,"overflow");
node.style.height="0px";
node.style.display="none";
if(_43e=="visible"){
node.style.overflow="hidden";
}
var _43f=dojo.lang.inArray(node.tagName.toLowerCase(),["tr","td","th"])?"":"block";
node.style.display=_43f;
var anim=new dojo.animation.Animation(new dojo.math.curves.Line([0],[_43b]),_43a,0);
dojo.event.connect(anim,"onAnimate",function(e){
node.style.height=Math.round(e.x)+"px";
});
dojo.event.connect(anim,"onEnd",function(e){
if(_43e!="visible"){
node.style.overflow=_43e;
}
if(_43c){
_43c(node,anim);
}
});
if(!_43d){
anim.play(true);
}
return anim;
};
dojo.fx.html.wipeOut=function(node,_444,_445,_446){
node=dojo.byId(node);
var _447=dojo.html.getStyle(node,"overflow");
var _448=dojo.html.getStyle(node,"height");
var _449=node.offsetHeight;
node.style.overflow="hidden";
var anim=new dojo.animation.Animation(new dojo.math.curves.Line([_449],[0]),_444,0);
dojo.event.connect(anim,"onAnimate",function(e){
node.style.height=Math.round(e.x)+"px";
});
dojo.event.connect(anim,"onEnd",function(e){
node.style.display="none";
node.style.overflow=_447;
node.style.height=_448||"auto";
if(_445){
_445(node,anim);
}
});
if(!_446){
anim.play(true);
}
return anim;
};
dojo.fx.html.explode=function(_44d,_44e,_44f,_450,_451){
var _452=dojo.html.toCoordinateArray(_44d);
var _453=document.createElement("div");
with(_453.style){
position="absolute";
border="1px solid black";
display="none";
}
dojo.html.body().appendChild(_453);
_44e=dojo.byId(_44e);
with(_44e.style){
visibility="hidden";
display="block";
}
var _454=dojo.html.toCoordinateArray(_44e);
with(_44e.style){
display="none";
visibility="visible";
}
var anim=new dojo.animation.Animation(new dojo.math.curves.Line(_452,_454),_44f,0);
dojo.event.connect(anim,"onBegin",function(e){
_453.style.display="block";
});
dojo.event.connect(anim,"onAnimate",function(e){
with(_453.style){
left=e.x+"px";
top=e.y+"px";
width=e.coords[2]+"px";
height=e.coords[3]+"px";
}
});
dojo.event.connect(anim,"onEnd",function(){
_44e.style.display="block";
_453.parentNode.removeChild(_453);
if(_450){
_450(_44e,anim);
}
});
if(!_451){
anim.play();
}
return anim;
};
dojo.fx.html.implode=function(_458,end,_45a,_45b,_45c){
var _45d=dojo.html.toCoordinateArray(_458);
var _45e=dojo.html.toCoordinateArray(end);
_458=dojo.byId(_458);
var _45f=document.createElement("div");
with(_45f.style){
position="absolute";
border="1px solid black";
display="none";
}
dojo.html.body().appendChild(_45f);
var anim=new dojo.animation.Animation(new dojo.math.curves.Line(_45d,_45e),_45a,0);
dojo.event.connect(anim,"onBegin",function(e){
_458.style.display="none";
_45f.style.display="block";
});
dojo.event.connect(anim,"onAnimate",function(e){
with(_45f.style){
left=e.x+"px";
top=e.y+"px";
width=e.coords[2]+"px";
height=e.coords[3]+"px";
}
});
dojo.event.connect(anim,"onEnd",function(){
_45f.parentNode.removeChild(_45f);
if(_45b){
_45b(_458,anim);
}
});
if(!_45c){
anim.play();
}
return anim;
};
dojo.fx.html.Exploder=function(_463,_464){
_463=dojo.byId(_463);
_464=dojo.byId(_464);
var _465=this;
this.waitToHide=500;
this.timeToShow=100;
this.waitToShow=200;
this.timeToHide=70;
this.autoShow=false;
this.autoHide=false;
var _466=null;
var _467=null;
var _468=null;
var _469=null;
var _46a=null;
var _46b=null;
this.showing=false;
this.onBeforeExplode=null;
this.onAfterExplode=null;
this.onBeforeImplode=null;
this.onAfterImplode=null;
this.onExploding=null;
this.onImploding=null;
this.timeShow=function(){
clearTimeout(_468);
_468=setTimeout(_465.show,_465.waitToShow);
};
this.show=function(){
clearTimeout(_468);
clearTimeout(_469);
if((_467&&_467.status()=="playing")||(_466&&_466.status()=="playing")||_465.showing){
return;
}
if(typeof _465.onBeforeExplode=="function"){
_465.onBeforeExplode(_463,_464);
}
_466=dojo.fx.html.explode(_463,_464,_465.timeToShow,function(e){
_465.showing=true;
if(typeof _465.onAfterExplode=="function"){
_465.onAfterExplode(_463,_464);
}
});
if(typeof _465.onExploding=="function"){
dojo.event.connect(_466,"onAnimate",this,"onExploding");
}
};
this.timeHide=function(){
clearTimeout(_468);
clearTimeout(_469);
if(_465.showing){
_469=setTimeout(_465.hide,_465.waitToHide);
}
};
this.hide=function(){
clearTimeout(_468);
clearTimeout(_469);
if(_466&&_466.status()=="playing"){
return;
}
_465.showing=false;
if(typeof _465.onBeforeImplode=="function"){
_465.onBeforeImplode(_463,_464);
}
_467=dojo.fx.html.implode(_464,_463,_465.timeToHide,function(e){
if(typeof _465.onAfterImplode=="function"){
_465.onAfterImplode(_463,_464);
}
});
if(typeof _465.onImploding=="function"){
dojo.event.connect(_467,"onAnimate",this,"onImploding");
}
};
dojo.event.connect(_463,"onclick",function(e){
if(_465.showing){
_465.hide();
}else{
_465.show();
}
});
dojo.event.connect(_463,"onmouseover",function(e){
if(_465.autoShow){
_465.timeShow();
}
});
dojo.event.connect(_463,"onmouseout",function(e){
if(_465.autoHide){
_465.timeHide();
}
});
dojo.event.connect(_464,"onmouseover",function(e){
clearTimeout(_469);
});
dojo.event.connect(_464,"onmouseout",function(e){
if(_465.autoHide){
_465.timeHide();
}
});
dojo.event.connect(document.documentElement||dojo.html.body(),"onclick",function(e){
if(_465.autoHide&&_465.showing&&!dojo.dom.isDescendantOf(e.target,_464)&&!dojo.dom.isDescendantOf(e.target,_463)){
_465.hide();
}
});
return this;
};
dojo.lang.mixin(dojo.fx,dojo.fx.html);
dojo.hostenv.conditionalLoadModule({browser:["dojo.fx.html"]});
dojo.hostenv.moduleLoaded("dojo.fx.*");
dojo.provide("dojo.logging.Logger");
dojo.provide("dojo.log");
dojo.require("dojo.lang");
dojo.logging.Record=function(lvl,msg){
this.level=lvl;
this.message=msg;
this.time=new Date();
};
dojo.logging.LogFilter=function(_476){
this.passChain=_476||"";
this.filter=function(_477){
return true;
};
};
dojo.logging.Logger=function(){
this.cutOffLevel=0;
this.propagate=true;
this.parent=null;
this.data=[];
this.filters=[];
this.handlers=[];
};
dojo.lang.extend(dojo.logging.Logger,{argsToArr:function(args){
var ret=[];
for(var x=0;x<args.length;x++){
ret.push(args[x]);
}
return ret;
},setLevel:function(lvl){
this.cutOffLevel=parseInt(lvl);
},isEnabledFor:function(lvl){
return parseInt(lvl)>=this.cutOffLevel;
},getEffectiveLevel:function(){
if((this.cutOffLevel==0)&&(this.parent)){
return this.parent.getEffectiveLevel();
}
return this.cutOffLevel;
},addFilter:function(flt){
this.filters.push(flt);
return this.filters.length-1;
},removeFilterByIndex:function(_47e){
if(this.filters[_47e]){
delete this.filters[_47e];
return true;
}
return false;
},removeFilter:function(_47f){
for(var x=0;x<this.filters.length;x++){
if(this.filters[x]===_47f){
delete this.filters[x];
return true;
}
}
return false;
},removeAllFilters:function(){
this.filters=[];
},filter:function(rec){
for(var x=0;x<this.filters.length;x++){
if((this.filters[x]["filter"])&&(!this.filters[x].filter(rec))||(rec.level<this.cutOffLevel)){
return false;
}
}
return true;
},addHandler:function(hdlr){
this.handlers.push(hdlr);
return this.handlers.length-1;
},handle:function(rec){
if((!this.filter(rec))||(rec.level<this.cutOffLevel)){
return false;
}
for(var x=0;x<this.handlers.length;x++){
if(this.handlers[x]["handle"]){
this.handlers[x].handle(rec);
}
}
return true;
},log:function(lvl,msg){
if((this.propagate)&&(this.parent)&&(this.parent.rec.level>=this.cutOffLevel)){
this.parent.log(lvl,msg);
return false;
}
this.handle(new dojo.logging.Record(lvl,msg));
return true;
},debug:function(msg){
return this.logType("DEBUG",this.argsToArr(arguments));
},info:function(msg){
return this.logType("INFO",this.argsToArr(arguments));
},warning:function(msg){
return this.logType("WARNING",this.argsToArr(arguments));
},error:function(msg){
return this.logType("ERROR",this.argsToArr(arguments));
},critical:function(msg){
return this.logType("CRITICAL",this.argsToArr(arguments));
},exception:function(msg,e,_48f){
if(e){
var _490=[e.name,(e.description||e.message)];
if(e.fileName){
_490.push(e.fileName);
_490.push("line "+e.lineNumber);
}
msg+=" "+_490.join(" : ");
}
this.logType("ERROR",msg);
if(!_48f){
throw e;
}
},logType:function(type,args){
var na=[dojo.logging.log.getLevel(type)];
if(typeof args=="array"){
na=na.concat(args);
}else{
if((typeof args=="object")&&(args["length"])){
na=na.concat(this.argsToArr(args));
}else{
na=na.concat(this.argsToArr(arguments).slice(1));
}
}
return this.log.apply(this,na);
}});
void (function(){
var _494=dojo.logging.Logger.prototype;
_494.warn=_494.warning;
_494.err=_494.error;
_494.crit=_494.critical;
})();
dojo.logging.LogHandler=function(_495){
this.cutOffLevel=(_495)?_495:0;
this.formatter=null;
this.data=[];
this.filters=[];
};
dojo.logging.LogHandler.prototype.setFormatter=function(fmtr){
dj_unimplemented("setFormatter");
};
dojo.logging.LogHandler.prototype.flush=function(){
dj_unimplemented("flush");
};
dojo.logging.LogHandler.prototype.close=function(){
dj_unimplemented("close");
};
dojo.logging.LogHandler.prototype.handleError=function(){
dj_unimplemented("handleError");
};
dojo.logging.LogHandler.prototype.handle=function(_497){
if((this.filter(_497))&&(_497.level>=this.cutOffLevel)){
this.emit(_497);
}
};
dojo.logging.LogHandler.prototype.emit=function(_498){
dj_unimplemented("emit");
};
void (function(){
var _499=["setLevel","addFilter","removeFilterByIndex","removeFilter","removeAllFilters","filter"];
var tgt=dojo.logging.LogHandler.prototype;
var src=dojo.logging.Logger.prototype;
for(var x=0;x<_499.length;x++){
tgt[_499[x]]=src[_499[x]];
}
})();
dojo.logging.log=new dojo.logging.Logger();
dojo.logging.log.levels=[{"name":"DEBUG","level":1},{"name":"INFO","level":2},{"name":"WARNING","level":3},{"name":"ERROR","level":4},{"name":"CRITICAL","level":5}];
dojo.logging.log.loggers={};
dojo.logging.log.getLogger=function(name){
if(!this.loggers[name]){
this.loggers[name]=new dojo.logging.Logger();
this.loggers[name].parent=this;
}
return this.loggers[name];
};
dojo.logging.log.getLevelName=function(lvl){
for(var x=0;x<this.levels.length;x++){
if(this.levels[x].level==lvl){
return this.levels[x].name;
}
}
return null;
};
dojo.logging.log.addLevelName=function(name,lvl){
if(this.getLevelName(name)){
this.err("could not add log level "+name+" because a level with that name already exists");
return false;
}
this.levels.append({"name":name,"level":parseInt(lvl)});
return true;
};
dojo.logging.log.getLevel=function(name){
for(var x=0;x<this.levels.length;x++){
if(this.levels[x].name.toUpperCase()==name.toUpperCase()){
return this.levels[x].level;
}
}
return null;
};
dojo.logging.MemoryLogHandler=function(_4a4,_4a5,_4a6,_4a7){
dojo.logging.LogHandler.call(this,_4a4);
this.numRecords=(typeof djConfig["loggingNumRecords"]!="undefined")?djConfig["loggingNumRecords"]:((_4a5)?_4a5:-1);
this.postType=(typeof djConfig["loggingPostType"]!="undefined")?djConfig["loggingPostType"]:(_4a6||-1);
this.postInterval=(typeof djConfig["loggingPostInterval"]!="undefined")?djConfig["loggingPostInterval"]:(_4a6||-1);
};
dojo.logging.MemoryLogHandler.prototype=new dojo.logging.LogHandler();
dojo.logging.MemoryLogHandler.prototype.emit=function(_4a8){
this.data.push(_4a8);
if(this.numRecords!=-1){
while(this.data.length>this.numRecords){
this.data.shift();
}
}
};
dojo.logging.logQueueHandler=new dojo.logging.MemoryLogHandler(0,50,0,10000);
dojo.logging.logQueueHandler.emit=function(_4a9){
var _4aa=String(dojo.log.getLevelName(_4a9.level)+": "+_4a9.time.toLocaleTimeString())+": "+_4a9.message;
if(!dj_undef("debug",dj_global)){
dojo.debug(_4aa);
}else{
if((typeof dj_global["print"]=="function")&&(!dojo.render.html.capable)){
print(_4aa);
}
}
this.data.push(_4a9);
if(this.numRecords!=-1){
while(this.data.length>this.numRecords){
this.data.shift();
}
}
};
dojo.logging.log.addHandler(dojo.logging.logQueueHandler);
dojo.log=dojo.logging.log;
dojo.hostenv.conditionalLoadModule({common:["dojo.logging.Logger",false,false],rhino:["dojo.logging.RhinoLogger"]});
dojo.hostenv.moduleLoaded("dojo.logging.*");
dojo.provide("dojo.io.IO");
dojo.require("dojo.string");
dojo.io.transports=[];
dojo.io.hdlrFuncNames=["load","error"];
dojo.io.Request=function(url,_4ac,_4ad,_4ae){
if((arguments.length==1)&&(arguments[0].constructor==Object)){
this.fromKwArgs(arguments[0]);
}else{
this.url=url;
if(_4ac){
this.mimetype=_4ac;
}
if(_4ad){
this.transport=_4ad;
}
if(arguments.length>=4){
this.changeUrl=_4ae;
}
}
};
dojo.lang.extend(dojo.io.Request,{url:"",mimetype:"text/plain",method:"GET",content:undefined,transport:undefined,changeUrl:undefined,formNode:undefined,sync:false,bindSuccess:false,useCache:false,preventCache:false,load:function(type,data,evt){
},error:function(type,_4b3){
},handle:function(){
},abort:function(){
},fromKwArgs:function(_4b4){
if(_4b4["url"]){
_4b4.url=_4b4.url.toString();
}
if(!_4b4["method"]&&_4b4["formNode"]&&_4b4["formNode"].method){
_4b4.method=_4b4["formNode"].method;
}
if(!_4b4["handle"]&&_4b4["handler"]){
_4b4.handle=_4b4.handler;
}
if(!_4b4["load"]&&_4b4["loaded"]){
_4b4.load=_4b4.loaded;
}
if(!_4b4["changeUrl"]&&_4b4["changeURL"]){
_4b4.changeUrl=_4b4.changeURL;
}
if(!_4b4["encoding"]){
if(!dojo.lang.isUndefined(djConfig["bindEncoding"])){
_4b4.encoding=djConfig.bindEncoding;
}else{
_4b4.encoding="";
}
}
var _4b5=dojo.lang.isFunction;
for(var x=0;x<dojo.io.hdlrFuncNames.length;x++){
var fn=dojo.io.hdlrFuncNames[x];
if(_4b5(_4b4[fn])){
continue;
}
if(_4b5(_4b4["handle"])){
_4b4[fn]=_4b4.handle;
}
}
dojo.lang.mixin(this,_4b4);
}});
dojo.io.Error=function(msg,type,num){
this.message=msg;
this.type=type||"unknown";
this.number=num||0;
};
dojo.io.transports.addTransport=function(name){
this.push(name);
this[name]=dojo.io[name];
};
dojo.io.bind=function(_4bc){
if(!(_4bc instanceof dojo.io.Request)){
try{
_4bc=new dojo.io.Request(_4bc);
}
catch(e){
dojo.debug(e);
}
}
var _4bd="";
if(_4bc["transport"]){
_4bd=_4bc["transport"];
if(!this[_4bd]){
return _4bc;
}
}else{
for(var x=0;x<dojo.io.transports.length;x++){
var tmp=dojo.io.transports[x];
if((this[tmp])&&(this[tmp].canHandle(_4bc))){
_4bd=tmp;
}
}
if(_4bd==""){
return _4bc;
}
}
this[_4bd].bind(_4bc);
_4bc.bindSuccess=true;
return _4bc;
};
dojo.io.queueBind=function(_4c0){
if(!(_4c0 instanceof dojo.io.Request)){
try{
_4c0=new dojo.io.Request(_4c0);
}
catch(e){
dojo.debug(e);
}
}
var _4c1=_4c0.load;
_4c0.load=function(){
dojo.io._queueBindInFlight=false;
var ret=_4c1.apply(this,arguments);
dojo.io._dispatchNextQueueBind();
return ret;
};
var _4c3=_4c0.error;
_4c0.error=function(){
dojo.io._queueBindInFlight=false;
var ret=_4c3.apply(this,arguments);
dojo.io._dispatchNextQueueBind();
return ret;
};
dojo.io._bindQueue.push(_4c0);
dojo.io._dispatchNextQueueBind();
return _4c0;
};
dojo.io._dispatchNextQueueBind=function(){
if(!dojo.io._queueBindInFlight){
dojo.io._queueBindInFlight=true;
dojo.io.bind(dojo.io._bindQueue.shift());
}
};
dojo.io._bindQueue=[];
dojo.io._queueBindInFlight=false;
dojo.io.argsFromMap=function(map,_4c6){
var _4c7=new Object();
var _4c8="";
var enc=/utf/i.test(_4c6||"")?encodeURIComponent:dojo.string.encodeAscii;
for(var x in map){
if(!_4c7[x]){
_4c8+=enc(x)+"="+enc(map[x])+"&";
}
}
return _4c8;
};
dojo.provide("dojo.io.BrowserIO");
dojo.require("dojo.io");
dojo.require("dojo.lang");
dojo.require("dojo.dom");
try{
if((!djConfig.preventBackButtonFix)&&(!dojo.hostenv.post_load_)){
document.write("<iframe style='border: 0px; width: 1px; height: 1px; position: absolute; bottom: 0px; right: 0px; visibility: visible;' name='djhistory' id='djhistory' src='"+(dojo.hostenv.getBaseScriptUri()+"iframe_history.html")+"'></iframe>");
}
}
catch(e){
}
dojo.io.checkChildrenForFile=function(node){
var _4cc=false;
var _4cd=node.getElementsByTagName("input");
dojo.lang.forEach(_4cd,function(_4ce){
if(_4cc){
return;
}
if(_4ce.getAttribute("type")=="file"){
_4cc=true;
}
});
return _4cc;
};
dojo.io.formHasFile=function(_4cf){
return dojo.io.checkChildrenForFile(_4cf);
};
dojo.io.encodeForm=function(_4d0,_4d1){
if((!_4d0)||(!_4d0.tagName)||(!_4d0.tagName.toLowerCase()=="form")){
dojo.raise("Attempted to encode a non-form element.");
}
var enc=/utf/i.test(_4d1||"")?encodeURIComponent:dojo.string.encodeAscii;
var _4d3=[];
for(var i=0;i<_4d0.elements.length;i++){
var elm=_4d0.elements[i];
if(elm.disabled||elm.tagName.toLowerCase()=="fieldset"||!elm.name){
continue;
}
var name=enc(elm.name);
var type=elm.type.toLowerCase();
if(type=="select-multiple"){
for(var j=0;j<elm.options.length;j++){
if(elm.options[j].selected){
_4d3.push(name+"="+enc(elm.options[j].value));
}
}
}else{
if(dojo.lang.inArray(type,["radio","checkbox"])){
if(elm.checked){
_4d3.push(name+"="+enc(elm.value));
}
}else{
if(!dojo.lang.inArray(type,["file","submit","reset","button"])){
_4d3.push(name+"="+enc(elm.value));
}
}
}
}
var _4d9=_4d0.getElementsByTagName("input");
for(var i=0;i<_4d9.length;i++){
var _4da=_4d9[i];
if(_4da.type.toLowerCase()=="image"&&_4da.form==_4d0){
var name=enc(_4da.name);
_4d3.push(name+"="+enc(_4da.value));
_4d3.push(name+".x=0");
_4d3.push(name+".y=0");
}
}
return _4d3.join("&")+"&";
};
dojo.io.setIFrameSrc=function(_4db,src,_4dd){
try{
var r=dojo.render.html;
if(!_4dd){
if(r.safari){
_4db.location=src;
}else{
frames[_4db.name].location=src;
}
}else{
var idoc;
if(r.ie){
idoc=_4db.contentWindow.document;
}else{
if(r.moz){
idoc=_4db.contentWindow;
}
}
idoc.location.replace(src);
}
}
catch(e){
dojo.debug(e);
dojo.debug("setIFrameSrc: "+e);
}
};
dojo.io.XMLHTTPTransport=new function(){
var _4e0=this;
this.initialHref=window.location.href;
this.initialHash=window.location.hash;
this.moveForward=false;
var _4e1={};
this.useCache=false;
this.preventCache=false;
this.historyStack=[];
this.forwardStack=[];
this.historyIframe=null;
this.bookmarkAnchor=null;
this.locationTimer=null;
function getCacheKey(url,_4e3,_4e4){
return url+"|"+_4e3+"|"+_4e4.toLowerCase();
}
function addToCache(url,_4e6,_4e7,http){
_4e1[getCacheKey(url,_4e6,_4e7)]=http;
}
function getFromCache(url,_4ea,_4eb){
return _4e1[getCacheKey(url,_4ea,_4eb)];
}
this.clearCache=function(){
_4e1={};
};
function doLoad(_4ec,http,url,_4ef,_4f0){
if((http.status==200)||(location.protocol=="file:"&&http.status==0)){
var ret;
if(_4ec.method.toLowerCase()=="head"){
var _4f2=http.getAllResponseHeaders();
ret={};
ret.toString=function(){
return _4f2;
};
var _4f3=_4f2.split(/[\r\n]+/g);
for(var i=0;i<_4f3.length;i++){
var pair=_4f3[i].match(/^([^:]+)\s*:\s*(.+)$/i);
if(pair){
ret[pair[1]]=pair[2];
}
}
}else{
if(_4ec.mimetype=="text/javascript"){
try{
ret=dj_eval(http.responseText);
}
catch(e){
dojo.debug(e);
dojo.debug(http.responseText);
ret=null;
}
}else{
if(_4ec.mimetype=="text/json"){
try{
ret=dj_eval("("+http.responseText+")");
}
catch(e){
dojo.debug(e);
dojo.debug(http.responseText);
ret=false;
}
}else{
if((_4ec.mimetype=="application/xml")||(_4ec.mimetype=="text/xml")){
ret=http.responseXML;
if(!ret||typeof ret=="string"){
ret=dojo.dom.createDocumentFromText(http.responseText);
}
}else{
ret=http.responseText;
}
}
}
}
if(_4f0){
addToCache(url,_4ef,_4ec.method,http);
}
_4ec[(typeof _4ec.load=="function")?"load":"handle"]("load",ret,http);
}else{
var _4f6=new dojo.io.Error("XMLHttpTransport Error: "+http.status+" "+http.statusText);
_4ec[(typeof _4ec.error=="function")?"error":"handle"]("error",_4f6,http);
}
}
function setHeaders(http,_4f8){
if(_4f8["headers"]){
for(var _4f9 in _4f8["headers"]){
if(_4f9.toLowerCase()=="content-type"&&!_4f8["contentType"]){
_4f8["contentType"]=_4f8["headers"][_4f9];
}else{
http.setRequestHeader(_4f9,_4f8["headers"][_4f9]);
}
}
}
}
this.addToHistory=function(args){
var _4fb=args["back"]||args["backButton"]||args["handle"];
var hash=null;
if(!this.historyIframe){
this.historyIframe=window.frames["djhistory"];
}
if(!this.bookmarkAnchor){
this.bookmarkAnchor=document.createElement("a");
(document.body||document.getElementsByTagName("body")[0]).appendChild(this.bookmarkAnchor);
this.bookmarkAnchor.style.display="none";
}
if((!args["changeUrl"])||(dojo.render.html.ie)){
var url=dojo.hostenv.getBaseScriptUri()+"iframe_history.html?"+(new Date()).getTime();
this.moveForward=true;
dojo.io.setIFrameSrc(this.historyIframe,url,false);
}
if(args["changeUrl"]){
hash="#"+((args["changeUrl"]!==true)?args["changeUrl"]:(new Date()).getTime());
setTimeout("window.location.href = '"+hash+"';",1);
this.bookmarkAnchor.href=hash;
if(dojo.render.html.ie){
var _4fe=_4fb;
var lh=null;
var hsl=this.historyStack.length-1;
if(hsl>=0){
while(!this.historyStack[hsl]["urlHash"]){
hsl--;
}
lh=this.historyStack[hsl]["urlHash"];
}
if(lh){
_4fb=function(){
if(window.location.hash!=""){
setTimeout("window.location.href = '"+lh+"';",1);
}
_4fe();
};
}
this.forwardStack=[];
var _501=args["forward"]||args["forwardButton"];
var tfw=function(){
if(window.location.hash!=""){
window.location.href=hash;
}
if(_501){
_501();
}
};
if(args["forward"]){
args.forward=tfw;
}else{
if(args["forwardButton"]){
args.forwardButton=tfw;
}
}
}else{
if(dojo.render.html.moz){
if(!this.locationTimer){
this.locationTimer=setInterval("dojo.io.XMLHTTPTransport.checkLocation();",200);
}
}
}
}
this.historyStack.push({"url":url,"callback":_4fb,"kwArgs":args,"urlHash":hash});
};
this.checkLocation=function(){
var hsl=this.historyStack.length;
if((window.location.hash==this.initialHash)||(window.location.href==this.initialHref)&&(hsl==1)){
this.handleBackButton();
return;
}
if(this.forwardStack.length>0){
if(this.forwardStack[this.forwardStack.length-1].urlHash==window.location.hash){
this.handleForwardButton();
return;
}
}
if((hsl>=2)&&(this.historyStack[hsl-2])){
if(this.historyStack[hsl-2].urlHash==window.location.hash){
this.handleBackButton();
return;
}
}
};
this.iframeLoaded=function(evt,_505){
var isp=_505.href.split("?");
if(isp.length<2){
if(this.historyStack.length==1){
this.handleBackButton();
}
return;
}
var _507=isp[1];
if(this.moveForward){
this.moveForward=false;
return;
}
var last=this.historyStack.pop();
if(!last){
if(this.forwardStack.length>0){
var next=this.forwardStack[this.forwardStack.length-1];
if(_507==next.url.split("?")[1]){
this.handleForwardButton();
}
}
return;
}
this.historyStack.push(last);
if(this.historyStack.length>=2){
if(isp[1]==this.historyStack[this.historyStack.length-2].url.split("?")[1]){
this.handleBackButton();
}
}else{
this.handleBackButton();
}
};
this.handleBackButton=function(){
var last=this.historyStack.pop();
if(!last){
return;
}
if(last["callback"]){
last.callback();
}else{
if(last.kwArgs["backButton"]){
last.kwArgs["backButton"]();
}else{
if(last.kwArgs["back"]){
last.kwArgs["back"]();
}else{
if(last.kwArgs["handle"]){
last.kwArgs.handle("back");
}
}
}
}
this.forwardStack.push(last);
};
this.handleForwardButton=function(){
var last=this.forwardStack.pop();
if(!last){
return;
}
if(last.kwArgs["forward"]){
last.kwArgs.forward();
}else{
if(last.kwArgs["forwardButton"]){
last.kwArgs.forwardButton();
}else{
if(last.kwArgs["handle"]){
last.kwArgs.handle("forward");
}
}
}
this.historyStack.push(last);
};
this.inFlight=[];
this.inFlightTimer=null;
this.startWatchingInFlight=function(){
if(!this.inFlightTimer){
this.inFlightTimer=setInterval("dojo.io.XMLHTTPTransport.watchInFlight();",10);
}
};
this.watchInFlight=function(){
for(var x=this.inFlight.length-1;x>=0;x--){
var tif=this.inFlight[x];
if(!tif){
this.inFlight.splice(x,1);
continue;
}
if(4==tif.http.readyState){
this.inFlight.splice(x,1);
doLoad(tif.req,tif.http,tif.url,tif.query,tif.useCache);
if(this.inFlight.length==0){
clearInterval(this.inFlightTimer);
this.inFlightTimer=null;
}
}
}
};
var _50e=dojo.hostenv.getXmlhttpObject()?true:false;
this.canHandle=function(_50f){
return _50e&&dojo.lang.inArray((_50f["mimetype"]||"".toLowerCase()),["text/plain","text/html","application/xml","text/xml","text/javascript","text/json"])&&dojo.lang.inArray(_50f["method"].toLowerCase(),["post","get","head"])&&!(_50f["formNode"]&&dojo.io.formHasFile(_50f["formNode"]));
};
this.multipartBoundary="45309FFF-BD65-4d50-99C9-36986896A96F";
this.bind=function(_510){
if(!_510["url"]){
if(!_510["formNode"]&&(_510["backButton"]||_510["back"]||_510["changeUrl"]||_510["watchForURL"])&&(!djConfig.preventBackButtonFix)){
this.addToHistory(_510);
return true;
}
}
var url=_510.url;
var _512="";
if(_510["formNode"]){
var ta=_510.formNode.getAttribute("action");
if((ta)&&(!_510["url"])){
url=ta;
}
var tp=_510.formNode.getAttribute("method");
if((tp)&&(!_510["method"])){
_510.method=tp;
}
_512+=dojo.io.encodeForm(_510.formNode,_510.encoding);
}
if(url.indexOf("#")>-1){
dojo.debug("Warning: dojo.io.bind: stripping hash values from url:",url);
url=url.split("#")[0];
}
if(_510["file"]){
_510.method="post";
}
if(!_510["method"]){
_510.method="get";
}
if(_510.method.toLowerCase()=="get"){
_510.multipart=false;
}else{
if(_510["file"]){
_510.multipart=true;
}else{
if(!_510["multipart"]){
_510.multipart=false;
}
}
}
if(_510["backButton"]||_510["back"]||_510["changeUrl"]){
this.addToHistory(_510);
}
do{
if(_510.postContent){
_512=_510.postContent;
break;
}
if(_510["content"]){
_512+=dojo.io.argsFromMap(_510.content,_510.encoding);
}
if(_510.method.toLowerCase()=="get"||!_510.multipart){
break;
}
var t=[];
if(_512.length){
var q=_512.split("&");
for(var i=0;i<q.length;++i){
if(q[i].length){
var p=q[i].split("=");
t.push("--"+this.multipartBoundary,"Content-Disposition: form-data; name=\""+p[0]+"\"","",p[1]);
}
}
}
if(_510.file){
if(dojo.lang.isArray(_510.file)){
for(var i=0;i<_510.file.length;++i){
var o=_510.file[i];
t.push("--"+this.multipartBoundary,"Content-Disposition: form-data; name=\""+o.name+"\"; filename=\""+("fileName" in o?o.fileName:o.name)+"\"","Content-Type: "+("contentType" in o?o.contentType:"application/octet-stream"),"",o.content);
}
}else{
var o=_510.file;
t.push("--"+this.multipartBoundary,"Content-Disposition: form-data; name=\""+o.name+"\"; filename=\""+("fileName" in o?o.fileName:o.name)+"\"","Content-Type: "+("contentType" in o?o.contentType:"application/octet-stream"),"",o.content);
}
}
if(t.length){
t.push("--"+this.multipartBoundary+"--","");
_512=t.join("\r\n");
}
}while(false);
var _51a=_510["sync"]?false:true;
var _51b=_510["preventCache"]||(this.preventCache==true&&_510["preventCache"]!=false);
var _51c=_510["useCache"]==true||(this.useCache==true&&_510["useCache"]!=false);
if(!_51b&&_51c){
var _51d=getFromCache(url,_512,_510.method);
if(_51d){
doLoad(_510,_51d,url,_512,false);
return;
}
}
var http=dojo.hostenv.getXmlhttpObject();
var _51f=false;
if(_51a){
this.inFlight.push({"req":_510,"http":http,"url":url,"query":_512,"useCache":_51c});
this.startWatchingInFlight();
}
if(_510.method.toLowerCase()=="post"){
http.open("POST",url,_51a);
setHeaders(http,_510);
http.setRequestHeader("Content-Type",_510.multipart?("multipart/form-data; boundary="+this.multipartBoundary):(_510.contentType||"application/x-www-form-urlencoded"));
http.send(_512);
}else{
var _520=url;
if(_512!=""){
_520+=(_520.indexOf("?")>-1?"&":"?")+_512;
}
if(_51b){
_520+=(dojo.string.endsWithAny(_520,"?","&")?"":(_520.indexOf("?")>-1?"&":"?"))+"dojo.preventCache="+new Date().valueOf();
}
http.open(_510.method.toUpperCase(),_520,_51a);
setHeaders(http,_510);
http.send(null);
}
if(!_51a){
doLoad(_510,http,url,_512,_51c);
}
_510.abort=function(){
return http.abort();
};
return;
};
dojo.io.transports.addTransport("XMLHTTPTransport");
};
dojo.provide("dojo.io.cookie");
dojo.io.cookie.setCookie=function(name,_522,days,path,_525,_526){
var _527=-1;
if(typeof days=="number"&&days>=0){
var d=new Date();
d.setTime(d.getTime()+(days*24*60*60*1000));
_527=d.toGMTString();
}
_522=escape(_522);
document.cookie=name+"="+_522+";"+(_527!=-1?" expires="+_527+";":"")+(path?"path="+path:"")+(_525?"; domain="+_525:"")+(_526?"; secure":"");
};
dojo.io.cookie.set=dojo.io.cookie.setCookie;
dojo.io.cookie.getCookie=function(name){
var idx=document.cookie.indexOf(name+"=");
if(idx==-1){
return null;
}
value=document.cookie.substring(idx+name.length+1);
var end=value.indexOf(";");
if(end==-1){
end=value.length;
}
value=value.substring(0,end);
value=unescape(value);
return value;
};
dojo.io.cookie.get=dojo.io.cookie.getCookie;
dojo.io.cookie.deleteCookie=function(name){
dojo.io.cookie.setCookie(name,"-",0);
};
dojo.io.cookie.setObjectCookie=function(name,obj,days,path,_531,_532,_533){
if(arguments.length==5){
_533=_531;
_531=null;
_532=null;
}
var _534=[],cookie,value="";
if(!_533){
cookie=dojo.io.cookie.getObjectCookie(name);
}
if(days>=0){
if(!cookie){
cookie={};
}
for(var prop in obj){
if(prop==null){
delete cookie[prop];
}else{
if(typeof obj[prop]=="string"||typeof obj[prop]=="number"){
cookie[prop]=obj[prop];
}
}
}
prop=null;
for(var prop in cookie){
_534.push(escape(prop)+"="+escape(cookie[prop]));
}
value=_534.join("&");
}
dojo.io.cookie.setCookie(name,value,days,path,_531,_532);
};
dojo.io.cookie.getObjectCookie=function(name){
var _537=null,cookie=dojo.io.cookie.getCookie(name);
if(cookie){
_537={};
var _538=cookie.split("&");
for(var i=0;i<_538.length;i++){
var pair=_538[i].split("=");
var _53b=pair[1];
if(isNaN(_53b)){
_53b=unescape(pair[1]);
}
_537[unescape(pair[0])]=_53b;
}
}
return _537;
};
dojo.io.cookie.isSupported=function(){
if(typeof navigator.cookieEnabled!="boolean"){
dojo.io.cookie.setCookie("__TestingYourBrowserForCookieSupport__","CookiesAllowed",90,null);
var _53c=dojo.io.cookie.getCookie("__TestingYourBrowserForCookieSupport__");
navigator.cookieEnabled=(_53c=="CookiesAllowed");
if(navigator.cookieEnabled){
this.deleteCookie("__TestingYourBrowserForCookieSupport__");
}
}
return navigator.cookieEnabled;
};
if(!dojo.io.cookies){
dojo.io.cookies=dojo.io.cookie;
}
dojo.hostenv.conditionalLoadModule({common:["dojo.io",false,false],rhino:["dojo.io.RhinoIO",false,false],browser:[["dojo.io.BrowserIO",false,false],["dojo.io.cookie",false,false]]});
dojo.hostenv.moduleLoaded("dojo.io.*");
dojo.hostenv.conditionalLoadModule({common:["dojo.uri.Uri",false,false]});
dojo.hostenv.moduleLoaded("dojo.uri.*");
dojo.provide("dojo.io.IframeIO");
dojo.require("dojo.io.BrowserIO");
dojo.require("dojo.uri.*");
dojo.io.createIFrame=function(_53d,_53e){
if(window[_53d]){
return window[_53d];
}
if(window.frames[_53d]){
return window.frames[_53d];
}
var r=dojo.render.html;
var _540=null;
var turi=dojo.uri.dojoUri("iframe_history.html?noInit=true");
var _542=((r.ie)&&(dojo.render.os.win))?"<iframe name='"+_53d+"' src='"+turi+"' onload='"+_53e+"'>":"iframe";
_540=document.createElement(_542);
with(_540){
name=_53d;
setAttribute("name",_53d);
id=_53d;
}
(document.body||document.getElementsByTagName("body")[0]).appendChild(_540);
window[_53d]=_540;
with(_540.style){
position="absolute";
left=top="0px";
height=width="1px";
visibility="hidden";
}
if(!r.ie){
dojo.io.setIFrameSrc(_540,turi,true);
_540.onload=new Function(_53e);
}
return _540;
};
dojo.io.iframeContentWindow=function(_543){
var win=_543.contentWindow||dojo.io.iframeContentDocument(_543).defaultView||dojo.io.iframeContentDocument(_543).__parent__||(_543.name&&document.frames[_543.name])||null;
return win;
};
dojo.io.iframeContentDocument=function(_545){
var doc=_545.contentDocument||((_545.contentWindow)&&(_545.contentWindow.document))||((_545.name)&&(document.frames[_545.name])&&(document.frames[_545.name].document))||null;
return doc;
};
dojo.io.IframeTransport=new function(){
var _547=this;
this.currentRequest=null;
this.requestQueue=[];
this.iframeName="dojoIoIframe";
this.fireNextRequest=function(){
if((this.currentRequest)||(this.requestQueue.length==0)){
return;
}
var cr=this.currentRequest=this.requestQueue.shift();
var fn=cr["formNode"];
if(fn){
if(cr["content"]){
for(var x in cr.content){
if(!fn[x]){
var tn;
if(dojo.render.html.ie){
tn=document.createElement("<input type='hidden' name='"+x+"' value='"+cr.content[x]+"'>");
fn.appendChild(tn);
}else{
tn=document.createElement("input");
fn.appendChild(tn);
tn.type="hidden";
tn.name=x;
tn.value=cr.content[x];
}
}else{
fn[x].value=cr.content[x];
}
}
}
if(cr["url"]){
fn.setAttribute("action",cr.url);
}
if(!fn.getAttribute("method")){
fn.setAttribute("method",(cr["method"])?cr["method"]:"post");
}
fn.setAttribute("target",this.iframeName);
fn.target=this.iframeName;
fn.submit();
}else{
var _54c=dojo.io.argsFromMap(this.currentRequest.content);
var _54d=(cr.url.indexOf("?")>-1?"&":"?")+_54c;
dojo.io.setIFrameSrc(this.iframe,_54d,true);
}
};
this.canHandle=function(_54e){
return ((dojo.lang.inArray(_54e["mimetype"],["text/plain","text/html","application/xml","text/xml","text/javascript","text/json"]))&&((_54e["formNode"])&&(dojo.io.checkChildrenForFile(_54e["formNode"])))&&(dojo.lang.inArray(_54e["method"].toLowerCase(),["post","get"]))&&(!((_54e["sync"])&&(_54e["sync"]==true))));
};
this.bind=function(_54f){
this.requestQueue.push(_54f);
this.fireNextRequest();
return;
};
this.setUpIframe=function(){
this.iframe=dojo.io.createIFrame(this.iframeName,"dojo.io.IframeTransport.iframeOnload();");
};
this.iframeOnload=function(){
if(!_547.currentRequest){
_547.fireNextRequest();
return;
}
var ifr=_547.iframe;
var ifw=dojo.io.iframeContentWindow(ifr);
var _552;
try{
var cmt=_547.currentRequest.mimetype;
if((cmt=="text/javascript")||(cmt=="text/json")){
var cd=dojo.io.iframeContentDocument(_547.iframe);
var js=cd.getElementsByTagName("textarea")[0].value;
if(cmt=="text/json"){
js="("+js+")";
}
_552=dj_eval(js);
}else{
if((cmt=="application/xml")||(cmt=="text/xml")){
_552=dojo.io.iframeContentDocument(_547.iframe);
}else{
_552=ifw.innerHTML;
}
}
if(typeof _547.currentRequest.load=="function"){
_547.currentRequest.load("load",_552,_547.currentRequest);
}
}
catch(e){
var _556=new dojo.io.Error("IframeTransport Error");
if(typeof _547.currentRequest["error"]=="function"){
_547.currentRequest.error("error",_556,_547.currentRequest);
}
}
_547.currentRequest=null;
_547.fireNextRequest();
};
dojo.io.transports.addTransport("IframeTransport");
};
dojo.addOnLoad(function(){
dojo.io.IframeTransport.setUpIframe();
});
dojo.provide("dojo.date");
dojo.date.setIso8601=function(_557,_558){
var _559=_558.split("T");
dojo.date.setIso8601Date(_557,_559[0]);
if(_559.length==2){
dojo.date.setIso8601Time(_557,_559[1]);
}
return _557;
};
dojo.date.fromIso8601=function(_55a){
return dojo.date.setIso8601(new Date(0),_55a);
};
dojo.date.setIso8601Date=function(_55b,_55c){
var _55d="^([0-9]{4})((-?([0-9]{2})(-?([0-9]{2}))?)|"+"(-?([0-9]{3}))|(-?W([0-9]{2})(-?([1-7]))?))?$";
var d=_55c.match(new RegExp(_55d));
var year=d[1];
var _560=d[4];
var date=d[6];
var _562=d[8];
var week=d[10];
var _564=(d[12])?d[12]:1;
_55b.setYear(year);
if(_562){
dojo.date.setDayOfYear(_55b,Number(_562));
}else{
if(week){
_55b.setMonth(0);
_55b.setDate(1);
var gd=_55b.getDay();
var day=(gd)?gd:7;
var _567=Number(_564)+(7*Number(week));
if(day<=4){
_55b.setDate(_567+1-day);
}else{
_55b.setDate(_567+8-day);
}
}else{
if(_560){
_55b.setMonth(_560-1);
}
if(date){
_55b.setDate(date);
}
}
}
return _55b;
};
dojo.date.fromIso8601Date=function(_568){
return dojo.date.setIso8601Date(new Date(0),_568);
};
dojo.date.setIso8601Time=function(_569,_56a){
var _56b="Z|(([-+])([0-9]{2})(:?([0-9]{2}))?)$";
var d=_56a.match(new RegExp(_56b));
var _56d=0;
if(d){
if(d[0]!="Z"){
_56d=(Number(d[3])*60)+Number(d[5]);
_56d*=((d[2]=="-")?1:-1);
}
_56d-=_569.getTimezoneOffset();
_56a=_56a.substr(0,_56a.length-d[0].length);
}
var _56e="^([0-9]{2})(:?([0-9]{2})(:?([0-9]{2})(.([0-9]+))?)?)?$";
var d=_56a.match(new RegExp(_56e));
var _56f=d[1];
var mins=Number((d[3])?d[3]:0)+_56d;
var secs=(d[5])?d[5]:0;
var ms=d[7]?(Number("0."+d[7])*1000):0;
_569.setHours(_56f);
_569.setMinutes(mins);
_569.setSeconds(secs);
_569.setMilliseconds(ms);
return _569;
};
dojo.date.fromIso8601Time=function(_573){
return dojo.date.setIso8601Time(new Date(0),_573);
};
dojo.date.setDayOfYear=function(_574,_575){
_574.setMonth(0);
_574.setDate(_575);
};
dojo.date.getDayOfYear=function(_576){
var _577=new Date(0);
_577.setMonth(_576.getMonth());
_577.setDate(_576.getDate());
return Number(_577)/86400000;
};
dojo.date.daysInMonth=function(_578,year){
var days=[31,28,31,30,31,30,31,31,30,31,30,31];
if(_578==1&&year){
if((!(year%4)&&(year%100))||(!(year%4)&&!(year%100)&&!(year%400))){
return 29;
}else{
return 28;
}
}else{
return days[_578];
}
};
dojo.date.months=["January","February","March","April","May","June","July","August","September","October","November","December"];
dojo.date.shortMonths=["Jan","Feb","Mar","Apr","May","June","July","Aug","Sep","Oct","Nov","Dec"];
dojo.date.days=["Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"];
dojo.date.shortDays=["Sun","Mon","Tues","Wed","Thur","Fri","Sat"];
dojo.date.toLongDateString=function(date){
return dojo.date.months[date.getMonth()]+" "+date.getDate()+", "+date.getFullYear();
};
dojo.date.toShortDateString=function(date){
return dojo.date.shortMonths[date.getMonth()]+" "+date.getDate()+", "+date.getFullYear();
};
dojo.date.toMilitaryTimeString=function(date){
var h="00"+date.getHours();
var m="00"+date.getMinutes();
var s="00"+date.getSeconds();
return h.substr(h.length-2,2)+":"+m.substr(m.length-2,2)+":"+s.substr(s.length-2,2);
};
dojo.date.toRelativeString=function(date){
var now=new Date();
var diff=(now-date)/1000;
var end=" ago";
var _585=false;
if(diff<0){
_585=true;
end=" from now";
diff=-diff;
}
if(diff<60){
diff=Math.round(diff);
return diff+" second"+(diff==1?"":"s")+end;
}else{
if(diff<3600){
diff=Math.round(diff/60);
return diff+" minute"+(diff==1?"":"s")+end;
}else{
if(diff<3600*24&&date.getDay()==now.getDay()){
diff=Math.round(diff/3600);
return diff+" hour"+(diff==1?"":"s")+end;
}else{
if(diff<3600*24*7){
diff=Math.round(diff/(3600*24));
if(diff==1){
return _585?"Tomorrow":"Yesterday";
}else{
return diff+" days"+end;
}
}else{
return dojo.date.toShortDateString(date);
}
}
}
}
};
dojo.provide("dojo.string.Builder");
dojo.require("dojo.string");
dojo.string.Builder=function(str){
this.arrConcat=(dojo.render.html.capable&&dojo.render.html["ie"]);
var a=[];
var b=str||"";
var _589=this.length=b.length;
if(this.arrConcat){
if(b.length>0){
a.push(b);
}
b="";
}
this.toString=this.valueOf=function(){
return (this.arrConcat)?a.join(""):b;
};
this.append=function(s){
if(this.arrConcat){
a.push(s);
}else{
b+=s;
}
_589+=s.length;
this.length=_589;
return this;
};
this.clear=function(){
a=[];
b="";
_589=this.length=0;
return this;
};
this.remove=function(f,l){
var s="";
if(this.arrConcat){
b=a.join("");
}
a=[];
if(f>0){
s=b.substring(0,(f-1));
}
b=s+b.substring(f+l);
_589=this.length=b.length;
if(this.arrConcat){
a.push(b);
b="";
}
return this;
};
this.replace=function(o,n){
if(this.arrConcat){
b=a.join("");
}
a=[];
b=b.replace(o,n);
_589=this.length=b.length;
if(this.arrConcat){
a.push(b);
b="";
}
return this;
};
this.insert=function(idx,s){
if(this.arrConcat){
b=a.join("");
}
a=[];
if(idx==0){
b=s+b;
}else{
var t=b.split("");
t.splice(idx,0,s);
b=t.join("");
}
_589=this.length=b.length;
if(this.arrConcat){
a.push(b);
b="";
}
return this;
};
};
dojo.hostenv.conditionalLoadModule({common:["dojo.string","dojo.string.Builder"]});
dojo.hostenv.moduleLoaded("dojo.string.*");
if(!this["dojo"]){
alert("\"dojo/__package__.js\" is now located at \"dojo/dojo.js\". Please update your includes accordingly");
}
dojo.provide("dojo.json");
dojo.require("dojo.lang");
dojo.json={jsonRegistry:new dojo.AdapterRegistry(),register:function(name,_594,wrap,_596){
dojo.json.jsonRegistry.register(name,_594,wrap,_596);
},evalJSON:function(){
return eval("("+arguments[0]+")");
},serialize:function(o){
var _598=typeof (o);
if(_598=="undefined"){
return "undefined";
}else{
if((_598=="number")||(_598=="boolean")){
return o+"";
}else{
if(o===null){
return "null";
}
}
}
var m=dojo.lang;
if(_598=="string"){
return m.reprString(o);
}
var me=arguments.callee;
var _59b;
if(typeof (o.__json__)=="function"){
_59b=o.__json__();
if(o!==_59b){
return me(_59b);
}
}
if(typeof (o.json)=="function"){
_59b=o.json();
if(o!==_59b){
return me(_59b);
}
}
if(_598!="function"&&typeof (o.length)=="number"){
var res=[];
for(var i=0;i<o.length;i++){
var val=me(o[i]);
if(typeof (val)!="string"){
val="undefined";
}
res.push(val);
}
return "["+res.join(",")+"]";
}
try{
_59b=dojo.json.jsonRegistry.match(o);
return me(_59b);
}
catch(e){
dojo.debug(e);
}
if(_598=="function"){
return null;
}
res=[];
for(var k in o){
var _5a0;
if(typeof (k)=="number"){
_5a0="\""+k+"\"";
}else{
if(typeof (k)=="string"){
_5a0=m.reprString(k);
}else{
continue;
}
}
val=me(o[k]);
if(typeof (val)!="string"){
continue;
}
res.push(_5a0+":"+val);
}
return "{"+res.join(",")+"}";
}};
dojo.provide("dojo.rpc.JsonService");
dojo.require("dojo.io.*");
dojo.require("dojo.json");
dojo.require("dojo.lang");
dojo.rpc.JsonService=function(url){
if(url){
this.connect(url);
}
};
dojo.lang.extend(dojo.rpc.JsonService,{status:"LOADING",lastSubmissionId:0,createJsonRpcRequest:function(_5a2,_5a3,id){
dojo.debug("JsonService: Create JSON-RPC Request.");
var req={"params":_5a2,"method":_5a3,"id":id};
data=dojo.json.serialize(req);
dojo.debug("JsonService: JSON-RPC Request: "+data);
return data;
},JsonRpcCallback:function(_5a6,_5a7){
return function(type,_5a9,e){
this.error=function(e){
dojo.debug("JsonService: Error in Callback: "+e);
};
if(dojo.lang.isFunction(_5a6)){
this.results=_5a6;
}else{
dojo.raise("JsonService: First argument to JsonRpcCallback must be the resultCallbackFunction");
}
if(dojo.lang.isFunction(_5a7)){
this.error=_5a7;
}
if(_5a9.e!=null){
if(dojo.lang.isFunction(this.error)){
this.error(_5a9.error);
}else{
}
}else{
if(dojo.lang.isFunction(this.results)){
this.results(_5a9.result,_5a9.id);
}else{
dojo.debug("JsonService: Results received but no callback method was specified.");
}
}
};
},createRemoteJsonRpcMethod:function(_5ac,_5ad,_5ae){
return function(){
dojo.debug("JsonService: Executing Remote Method");
if(_5ae){
var _5af=_5ae.length;
}else{
var _5af=0;
}
if(arguments.length<_5af){
dojo.raise("Invalid number of parameters for remote method.");
}else{
if(arguments.length>_5af){
if(dojo.lang.isFunction(arguments[arguments.length-1])){
if(arguments.length-1==_5af){
var p=[];
for(var n=0;n<_5af;n++){
p[n]=arguments[n];
}
dojo.io.bind({url:_5ac,postContent:this.createJsonRpcRequest(p,_5ad,this.lastSubmissionId++),method:"POST",mimetype:"text/json",load:this.JsonRpcCallback(arguments[arguments.length-1])});
return this.lastSubmissionId-1;
}else{
dojo.raise("Too many parameters supplied for remote method.");
}
}else{
dojo.raise("More parameters than require and/or the extra parameter isn't a callback function");
}
}else{
dojo.raise("No Callback function supplied and synchronous rpc calls haven't been implemented");
}
}
};
},processJSDL:function(type,_5b3,e){
dojo.debug("JsonService: Processing returned JSDL.");
dojo.debug("JsonService: Creating "+_5b3.className+" object.");
for(var n=0;n<_5b3.methods.length;n++){
dojo.debug("JsonService: Creating Method: this."+_5b3.methods[n].name+"()");
this[_5b3.methods[n].name]=this.createRemoteJsonRpcMethod(_5b3.serviceURL,_5b3.methods[n].name,_5b3.methods[n].parameters);
}
this.status="READY";
dojo.debug("JsonService: Dojo RPC Object is ready for use.");
},viewJSDL:function(type,_5b7,e){
dojo.debug(_5b7);
},connect:function(_5b9){
dojo.debug("JsonService: Attempting to load jsdl document from "+_5b9);
dojo.io.bind({url:_5b9,mimetype:"text/json",load:dojo.lang.hitch(this,function(type,_5bb,e){
return this.processJSDL(type,_5bb,e);
})});
}});
dojo.hostenv.conditionalLoadModule({common:["dojo.rpc.JsonService",false,false]});
dojo.hostenv.moduleLoaded("dojo.rpc.*");
dojo.provide("dojo.xml.Parse");
dojo.require("dojo.dom");
dojo.xml.Parse=function(){
this.parseFragment=function(_5bd){
var _5be={};
var _5bf=dojo.dom.getTagName(_5bd);
_5be[_5bf]=new Array(_5bd.tagName);
var _5c0=this.parseAttributes(_5bd);
for(var attr in _5c0){
if(!_5be[attr]){
_5be[attr]=[];
}
_5be[attr][_5be[attr].length]=_5c0[attr];
}
var _5c2=_5bd.childNodes;
for(var _5c3 in _5c2){
switch(_5c2[_5c3].nodeType){
case dojo.dom.ELEMENT_NODE:
_5be[_5bf].push(this.parseElement(_5c2[_5c3]));
break;
case dojo.dom.TEXT_NODE:
if(_5c2.length==1){
if(!_5be[_5bd.tagName]){
_5be[_5bf]=[];
}
_5be[_5bf].push({value:_5c2[0].nodeValue});
}
break;
}
}
return _5be;
};
this.parseElement=function(node,_5c5,_5c6,_5c7){
var _5c8={};
var _5c9=dojo.dom.getTagName(node);
_5c8[_5c9]=[];
if((!_5c6)||(_5c9.substr(0,4).toLowerCase()=="dojo")){
var _5ca=this.parseAttributes(node);
for(var attr in _5ca){
if((!_5c8[_5c9][attr])||(typeof _5c8[_5c9][attr]!="array")){
_5c8[_5c9][attr]=[];
}
_5c8[_5c9][attr].push(_5ca[attr]);
}
_5c8[_5c9].nodeRef=node;
_5c8.tagName=_5c9;
_5c8.index=_5c7||0;
}
var _5cc=0;
for(var i=0;i<node.childNodes.length;i++){
var tcn=node.childNodes.item(i);
switch(tcn.nodeType){
case dojo.dom.ELEMENT_NODE:
_5cc++;
var ctn=dojo.dom.getTagName(tcn);
if(!_5c8[ctn]){
_5c8[ctn]=[];
}
_5c8[ctn].push(this.parseElement(tcn,true,_5c6,_5cc));
if((tcn.childNodes.length==1)&&(tcn.childNodes.item(0).nodeType==dojo.dom.TEXT_NODE)){
_5c8[ctn][_5c8[ctn].length-1].value=tcn.childNodes.item(0).nodeValue;
}
break;
case dojo.dom.TEXT_NODE:
if(node.childNodes.length==1){
_5c8[_5c9].push({value:node.childNodes.item(0).nodeValue});
}
break;
default:
break;
}
}
return _5c8;
};
this.parseAttributes=function(node){
var _5d1={};
var atts=node.attributes;
for(var i=0;i<atts.length;i++){
var _5d4=atts.item(i);
if((dojo.render.html.capable)&&(dojo.render.html.ie)){
if(!_5d4){
continue;
}
if((typeof _5d4=="object")&&(typeof _5d4.nodeValue=="undefined")||(_5d4.nodeValue==null)||(_5d4.nodeValue=="")){
continue;
}
}
var nn=(_5d4.nodeName.indexOf("dojo:")==-1)?_5d4.nodeName:_5d4.nodeName.split("dojo:")[1];
_5d1[nn]={value:_5d4.nodeValue};
}
return _5d1;
};
};
dojo.provide("dojo.xml.domUtil");
dojo.require("dojo.graphics.color");
dojo.require("dojo.dom");
dojo.require("dojo.style");
dj_deprecated("dojo.xml.domUtil is deprecated, use dojo.dom instead");
dojo.xml.domUtil=new function(){
this.nodeTypes={ELEMENT_NODE:1,ATTRIBUTE_NODE:2,TEXT_NODE:3,CDATA_SECTION_NODE:4,ENTITY_REFERENCE_NODE:5,ENTITY_NODE:6,PROCESSING_INSTRUCTION_NODE:7,COMMENT_NODE:8,DOCUMENT_NODE:9,DOCUMENT_TYPE_NODE:10,DOCUMENT_FRAGMENT_NODE:11,NOTATION_NODE:12};
this.dojoml="http://www.dojotoolkit.org/2004/dojoml";
this.idIncrement=0;
this.getTagName=function(){
return dojo.dom.getTagName.apply(dojo.dom,arguments);
};
this.getUniqueId=function(){
return dojo.dom.getUniqueId.apply(dojo.dom,arguments);
};
this.getFirstChildTag=function(){
return dojo.dom.getFirstChildElement.apply(dojo.dom,arguments);
};
this.getLastChildTag=function(){
return dojo.dom.getLastChildElement.apply(dojo.dom,arguments);
};
this.getNextSiblingTag=function(){
return dojo.dom.getNextSiblingElement.apply(dojo.dom,arguments);
};
this.getPreviousSiblingTag=function(){
return dojo.dom.getPreviousSiblingElement.apply(dojo.dom,arguments);
};
this.forEachChildTag=function(node,_5d7){
var _5d8=this.getFirstChildTag(node);
while(_5d8){
if(_5d7(_5d8)=="break"){
break;
}
_5d8=this.getNextSiblingTag(_5d8);
}
};
this.moveChildren=function(){
return dojo.dom.moveChildren.apply(dojo.dom,arguments);
};
this.copyChildren=function(){
return dojo.dom.copyChildren.apply(dojo.dom,arguments);
};
this.clearChildren=function(){
return dojo.dom.removeChildren.apply(dojo.dom,arguments);
};
this.replaceChildren=function(){
return dojo.dom.replaceChildren.apply(dojo.dom,arguments);
};
this.getStyle=function(){
return dojo.style.getStyle.apply(dojo.style,arguments);
};
this.toCamelCase=function(){
return dojo.style.toCamelCase.apply(dojo.style,arguments);
};
this.toSelectorCase=function(){
return dojo.style.toSelectorCase.apply(dojo.style,arguments);
};
this.getAncestors=function(){
return dojo.dom.getAncestors.apply(dojo.dom,arguments);
};
this.isChildOf=function(){
return dojo.dom.isDescendantOf.apply(dojo.dom,arguments);
};
this.createDocumentFromText=function(){
return dojo.dom.createDocumentFromText.apply(dojo.dom,arguments);
};
if(dojo.render.html.capable||dojo.render.svg.capable){
this.createNodesFromText=function(txt,wrap){
return dojo.dom.createNodesFromText.apply(dojo.dom,arguments);
};
}
this.extractRGB=function(_5db){
return dojo.graphics.color.extractRGB(_5db);
};
this.hex2rgb=function(hex){
return dojo.graphics.color.hex2rgb(hex);
};
this.rgb2hex=function(r,g,b){
return dojo.graphics.color.rgb2hex(r,g,b);
};
this.insertBefore=function(){
return dojo.dom.insertBefore.apply(dojo.dom,arguments);
};
this.before=this.insertBefore;
this.insertAfter=function(){
return dojo.dom.insertAfter.apply(dojo.dom,arguments);
};
this.after=this.insertAfter;
this.insert=function(){
return dojo.dom.insertAtPosition.apply(dojo.dom,arguments);
};
this.insertAtIndex=function(){
return dojo.dom.insertAtIndex.apply(dojo.dom,arguments);
};
this.textContent=function(){
return dojo.dom.textContent.apply(dojo.dom,arguments);
};
this.renderedTextContent=function(){
return dojo.dom.renderedTextContent.apply(dojo.dom,arguments);
};
this.remove=function(node){
return dojo.dom.removeNode.apply(dojo.dom,arguments);
};
};
dojo.provide("dojo.xml.htmlUtil");
dojo.require("dojo.html");
dojo.require("dojo.style");
dojo.require("dojo.dom");
dj_deprecated("dojo.xml.htmlUtil is deprecated, use dojo.html instead");
dojo.xml.htmlUtil=new function(){
this.styleSheet=dojo.style.styleSheet;
this._clobberSelection=function(){
return dojo.html.clearSelection.apply(dojo.html,arguments);
};
this.disableSelect=function(){
return dojo.html.disableSelection.apply(dojo.html,arguments);
};
this.enableSelect=function(){
return dojo.html.enableSelection.apply(dojo.html,arguments);
};
this.getInnerWidth=function(){
return dojo.style.getInnerWidth.apply(dojo.style,arguments);
};
this.getOuterWidth=function(node){
dj_unimplemented("dojo.xml.htmlUtil.getOuterWidth");
};
this.getInnerHeight=function(){
return dojo.style.getInnerHeight.apply(dojo.style,arguments);
};
this.getOuterHeight=function(node){
dj_unimplemented("dojo.xml.htmlUtil.getOuterHeight");
};
this.getTotalOffset=function(){
return dojo.style.getTotalOffset.apply(dojo.style,arguments);
};
this.totalOffsetLeft=function(){
return dojo.style.totalOffsetLeft.apply(dojo.style,arguments);
};
this.getAbsoluteX=this.totalOffsetLeft;
this.totalOffsetTop=function(){
return dojo.style.totalOffsetTop.apply(dojo.style,arguments);
};
this.getAbsoluteY=this.totalOffsetTop;
this.getEventTarget=function(){
return dojo.html.getEventTarget.apply(dojo.html,arguments);
};
this.getScrollTop=function(){
return dojo.html.getScrollTop.apply(dojo.html,arguments);
};
this.getScrollLeft=function(){
return dojo.html.getScrollLeft.apply(dojo.html,arguments);
};
this.evtTgt=this.getEventTarget;
this.getParentOfType=function(){
return dojo.html.getParentOfType.apply(dojo.html,arguments);
};
this.getAttribute=function(){
return dojo.html.getAttribute.apply(dojo.html,arguments);
};
this.getAttr=function(node,attr){
dj_deprecated("dojo.xml.htmlUtil.getAttr is deprecated, use dojo.xml.htmlUtil.getAttribute instead");
return dojo.xml.htmlUtil.getAttribute(node,attr);
};
this.hasAttribute=function(){
return dojo.html.hasAttribute.apply(dojo.html,arguments);
};
this.hasAttr=function(node,attr){
dj_deprecated("dojo.xml.htmlUtil.hasAttr is deprecated, use dojo.xml.htmlUtil.hasAttribute instead");
return dojo.xml.htmlUtil.hasAttribute(node,attr);
};
this.getClass=function(){
return dojo.html.getClass.apply(dojo.html,arguments);
};
this.hasClass=function(){
return dojo.html.hasClass.apply(dojo.html,arguments);
};
this.prependClass=function(){
return dojo.html.prependClass.apply(dojo.html,arguments);
};
this.addClass=function(){
return dojo.html.addClass.apply(dojo.html,arguments);
};
this.setClass=function(){
return dojo.html.setClass.apply(dojo.html,arguments);
};
this.removeClass=function(){
return dojo.html.removeClass.apply(dojo.html,arguments);
};
this.classMatchType={ContainsAll:0,ContainsAny:1,IsOnly:2};
this.getElementsByClass=function(){
return dojo.html.getElementsByClass.apply(dojo.html,arguments);
};
this.getElementsByClassName=this.getElementsByClass;
this.setOpacity=function(){
return dojo.style.setOpacity.apply(dojo.style,arguments);
};
this.getOpacity=function(){
return dojo.style.getOpacity.apply(dojo.style,arguments);
};
this.clearOpacity=function(){
return dojo.style.clearOpacity.apply(dojo.style,arguments);
};
this.gravity=function(){
return dojo.html.gravity.apply(dojo.html,arguments);
};
this.gravity.NORTH=1;
this.gravity.SOUTH=1<<1;
this.gravity.EAST=1<<2;
this.gravity.WEST=1<<3;
this.overElement=function(){
return dojo.html.overElement.apply(dojo.html,arguments);
};
this.insertCssRule=function(){
return dojo.style.insertCssRule.apply(dojo.style,arguments);
};
this.insertCSSRule=function(_5e7,_5e8,_5e9){
dj_deprecated("dojo.xml.htmlUtil.insertCSSRule is deprecated, use dojo.xml.htmlUtil.insertCssRule instead");
return dojo.xml.htmlUtil.insertCssRule(_5e7,_5e8,_5e9);
};
this.removeCssRule=function(){
return dojo.style.removeCssRule.apply(dojo.style,arguments);
};
this.removeCSSRule=function(_5ea){
dj_deprecated("dojo.xml.htmlUtil.removeCSSRule is deprecated, use dojo.xml.htmlUtil.removeCssRule instead");
return dojo.xml.htmlUtil.removeCssRule(_5ea);
};
this.insertCssFile=function(){
return dojo.style.insertCssFile.apply(dojo.style,arguments);
};
this.insertCSSFile=function(URI,doc,_5ed){
dj_deprecated("dojo.xml.htmlUtil.insertCSSFile is deprecated, use dojo.xml.htmlUtil.insertCssFile instead");
return dojo.xml.htmlUtil.insertCssFile(URI,doc,_5ed);
};
this.getBackgroundColor=function(){
return dojo.style.getBackgroundColor.apply(dojo.style,arguments);
};
this.getUniqueId=function(){
return dojo.dom.getUniqueId();
};
this.getStyle=function(){
return dojo.style.getStyle.apply(dojo.style,arguments);
};
};
dojo.require("dojo.xml.Parse");
dojo.hostenv.conditionalLoadModule({common:["dojo.xml.domUtil"],browser:["dojo.xml.htmlUtil"],svg:["dojo.xml.svgUtil"]});
dojo.hostenv.moduleLoaded("dojo.xml.*");
dojo.hostenv.conditionalLoadModule({common:["dojo.lang"]});
dojo.hostenv.moduleLoaded("dojo.lang.*");
dojo.require("dojo.lang.*");
dojo.provide("dojo.storage");
dojo.provide("dojo.storage.StorageProvider");
dojo.storage=new function(){
this.provider=null;
this.setProvider=function(obj){
this.provider=obj;
};
this.set=function(key,_5f0,_5f1){
if(!this.provider){
return false;
}
return this.provider.set(key,_5f0,_5f1);
};
this.get=function(key,_5f3){
if(!this.provider){
return false;
}
return this.provider.get(key,_5f3);
};
this.remove=function(key,_5f5){
return this.provider.remove(key,_5f5);
};
};
dojo.storage.StorageProvider=function(){
};
dojo.lang.extend(dojo.storage.StorageProvider,{namespace:"*",initialized:false,free:function(){
dojo.unimplemented("dojo.storage.StorageProvider.free");
return 0;
},freeK:function(){
return dojo.math.round(this.free()/1024,0);
},set:function(key,_5f7,_5f8){
dojo.unimplemented("dojo.storage.StorageProvider.set");
},get:function(key,_5fa){
dojo.unimplemented("dojo.storage.StorageProvider.get");
},remove:function(key,_5fc,_5fd){
dojo.unimplemented("dojo.storage.StorageProvider.set");
}});
dojo.provide("dojo.storage.browser");
dojo.require("dojo.storage");
dojo.require("dojo.uri.*");
dojo.storage.browser.StorageProvider=function(){
this.initialized=false;
this.flash=null;
this.backlog=[];
};
dojo.inherits(dojo.storage.browser.StorageProvider,dojo.storage.StorageProvider);
dojo.lang.extend(dojo.storage.browser.StorageProvider,{storageOnLoad:function(){
this.initialized=true;
this.hideStore();
while(this.backlog.length){
this.set.apply(this,this.backlog.shift());
}
},unHideStore:function(){
var _5fe=dojo.byId("dojo-storeContainer");
with(_5fe.style){
position="absolute";
overflow="visible";
width="215px";
height="138px";
left="30px";
top="30px";
visiblity="visible";
zIndex="20";
border="1px solid black";
}
},hideStore:function(_5ff){
var _600=dojo.byId("dojo-storeContainer");
with(_600.style){
left="-300px";
top="-300px";
}
},set:function(key,_602,ns){
if(!this.initialized){
this.backlog.push([key,_602,ns]);
return "pending";
}
return this.flash.set(key,_602,ns||this.namespace);
},get:function(key,ns){
return this.flash.get(key,ns||this.namespace);
},writeStorage:function(){
var _606=dojo.uri.dojoUri("src/storage/Storage.swf").toString();
var _607=["<div id=\"dojo-storeContainer\"","style=\"position: absolute; left: -300px; top: -300px;\">"];
if(dojo.render.html.ie){
_607.push("<object");
_607.push("\tstyle=\"border: 1px solid black;\"");
_607.push("\tclassid=\"clsid:D27CDB6E-AE6D-11cf-96B8-444553540000\"");
_607.push("\tcodebase=\"http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,0,0\"");
_607.push("\twidth=\"215\" height=\"138\" id=\"dojoStorage\">");
_607.push("\t<param name=\"movie\" value=\""+_606+"\">");
_607.push("\t<param name=\"quality\" value=\"high\">");
_607.push("</object>");
}else{
_607.push("<embed src=\""+_606+"\" width=\"215\" height=\"138\" ");
_607.push("\tquality=\"high\" ");
_607.push("\tpluginspage=\"http://www.macromedia.com/go/getflashplayer\" ");
_607.push("\ttype=\"application/x-shockwave-flash\" ");
_607.push("\tname=\"dojoStorage\">");
_607.push("</embed>");
}
_607.push("</div>");
document.write(_607.join(""));
}});
dojo.storage.setProvider(new dojo.storage.browser.StorageProvider());
dojo.storage.provider.writeStorage();
dojo.addOnLoad(function(){
dojo.storage.provider.flash=(dojo.render.html.ie)?window["dojoStorage"]:document["dojoStorage"];
});
dojo.hostenv.conditionalLoadModule({common:["dojo.storage"],browser:["dojo.storage.browser"]});
dojo.hostenv.moduleLoaded("dojo.storage.*");
dojo.provide("dojo.crypto");
dojo.crypto.cipherModes={ECB:0,CBC:1,PCBC:2,CFB:3,OFB:4,CTR:5};
dojo.crypto.outputTypes={Base64:0,Hex:1,String:2,Raw:3};
dojo.require("dojo.crypto");
dojo.provide("dojo.crypto.MD5");
dojo.crypto.MD5=new function(){
var _608=8;
var mask=(1<<_608)-1;
function toWord(s){
var wa=[];
for(var i=0;i<s.length*_608;i+=_608){
wa[i>>5]|=(s.charCodeAt(i/_608)&mask)<<(i%32);
}
return wa;
}
function toString(wa){
var s=[];
for(var i=0;i<wa.length*32;i+=_608){
s.push(String.fromCharCode((wa[i>>5]>>>(i%32))&mask));
}
return s.join("");
}
function toHex(wa){
var h="0123456789abcdef";
var s=[];
for(var i=0;i<wa.length*4;i++){
s.push(h.charAt((wa[i>>2]>>((i%4)*8+4))&15)+h.charAt((wa[i>>2]>>((i%4)*8))&15));
}
return s.join("");
}
function toBase64(wa){
var p="=";
var tab="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
var s=[];
for(var i=0;i<wa.length*4;i+=3){
var t=(((wa[i>>2]>>8*(i%4))&255)<<16)|(((wa[i+1>>2]>>8*((i+1)%4))&255)<<8)|((wa[i+2>>2]>>8*((i+2)%4))&255);
for(var j=0;j<4;j++){
if(i*8+j*6>wa.length*32){
s.push(p);
}else{
s.push(tab.charAt((t>>6*(3-j))&63));
}
}
}
return s.join("");
}
function add(x,y){
var l=(x&65535)+(y&65535);
var m=(x>>16)+(y>>16)+(l>>16);
return (m<<16)|(l&65535);
}
function R(n,c){
return (n<<c)|(n>>>(32-c));
}
function C(q,a,b,x,s,t){
return add(R(add(add(a,q),add(x,t)),s),b);
}
function FF(a,b,c,d,x,s,t){
return C((b&c)|((~b)&d),a,b,x,s,t);
}
function GG(a,b,c,d,x,s,t){
return C((b&d)|(c&(~d)),a,b,x,s,t);
}
function HH(a,b,c,d,x,s,t){
return C(b^c^d,a,b,x,s,t);
}
function II(a,b,c,d,x,s,t){
return C(c^(b|(~d)),a,b,x,s,t);
}
function core(x,len){
x[len>>5]|=128<<((len)%32);
x[(((len+64)>>>9)<<4)+14]=len;
var a=1732584193;
var b=-271733879;
var c=-1732584194;
var d=271733878;
for(var i=0;i<x.length;i+=16){
var olda=a;
var oldb=b;
var oldc=c;
var oldd=d;
a=FF(a,b,c,d,x[i+0],7,-680876936);
d=FF(d,a,b,c,x[i+1],12,-389564586);
c=FF(c,d,a,b,x[i+2],17,606105819);
b=FF(b,c,d,a,x[i+3],22,-1044525330);
a=FF(a,b,c,d,x[i+4],7,-176418897);
d=FF(d,a,b,c,x[i+5],12,1200080426);
c=FF(c,d,a,b,x[i+6],17,-1473231341);
b=FF(b,c,d,a,x[i+7],22,-45705983);
a=FF(a,b,c,d,x[i+8],7,1770035416);
d=FF(d,a,b,c,x[i+9],12,-1958414417);
c=FF(c,d,a,b,x[i+10],17,-42063);
b=FF(b,c,d,a,x[i+11],22,-1990404162);
a=FF(a,b,c,d,x[i+12],7,1804603682);
d=FF(d,a,b,c,x[i+13],12,-40341101);
c=FF(c,d,a,b,x[i+14],17,-1502002290);
b=FF(b,c,d,a,x[i+15],22,1236535329);
a=GG(a,b,c,d,x[i+1],5,-165796510);
d=GG(d,a,b,c,x[i+6],9,-1069501632);
c=GG(c,d,a,b,x[i+11],14,643717713);
b=GG(b,c,d,a,x[i+0],20,-373897302);
a=GG(a,b,c,d,x[i+5],5,-701558691);
d=GG(d,a,b,c,x[i+10],9,38016083);
c=GG(c,d,a,b,x[i+15],14,-660478335);
b=GG(b,c,d,a,x[i+4],20,-405537848);
a=GG(a,b,c,d,x[i+9],5,568446438);
d=GG(d,a,b,c,x[i+14],9,-1019803690);
c=GG(c,d,a,b,x[i+3],14,-187363961);
b=GG(b,c,d,a,x[i+8],20,1163531501);
a=GG(a,b,c,d,x[i+13],5,-1444681467);
d=GG(d,a,b,c,x[i+2],9,-51403784);
c=GG(c,d,a,b,x[i+7],14,1735328473);
b=GG(b,c,d,a,x[i+12],20,-1926607734);
a=HH(a,b,c,d,x[i+5],4,-378558);
d=HH(d,a,b,c,x[i+8],11,-2022574463);
c=HH(c,d,a,b,x[i+11],16,1839030562);
b=HH(b,c,d,a,x[i+14],23,-35309556);
a=HH(a,b,c,d,x[i+1],4,-1530992060);
d=HH(d,a,b,c,x[i+4],11,1272893353);
c=HH(c,d,a,b,x[i+7],16,-155497632);
b=HH(b,c,d,a,x[i+10],23,-1094730640);
a=HH(a,b,c,d,x[i+13],4,681279174);
d=HH(d,a,b,c,x[i+0],11,-358537222);
c=HH(c,d,a,b,x[i+3],16,-722521979);
b=HH(b,c,d,a,x[i+6],23,76029189);
a=HH(a,b,c,d,x[i+9],4,-640364487);
d=HH(d,a,b,c,x[i+12],11,-421815835);
c=HH(c,d,a,b,x[i+15],16,530742520);
b=HH(b,c,d,a,x[i+2],23,-995338651);
a=II(a,b,c,d,x[i+0],6,-198630844);
d=II(d,a,b,c,x[i+7],10,1126891415);
c=II(c,d,a,b,x[i+14],15,-1416354905);
b=II(b,c,d,a,x[i+5],21,-57434055);
a=II(a,b,c,d,x[i+12],6,1700485571);
d=II(d,a,b,c,x[i+3],10,-1894986606);
c=II(c,d,a,b,x[i+10],15,-1051523);
b=II(b,c,d,a,x[i+1],21,-2054922799);
a=II(a,b,c,d,x[i+8],6,1873313359);
d=II(d,a,b,c,x[i+15],10,-30611744);
c=II(c,d,a,b,x[i+6],15,-1560198380);
b=II(b,c,d,a,x[i+13],21,1309151649);
a=II(a,b,c,d,x[i+4],6,-145523070);
d=II(d,a,b,c,x[i+11],10,-1120210379);
c=II(c,d,a,b,x[i+2],15,718787259);
b=II(b,c,d,a,x[i+9],21,-343485551);
a=add(a,olda);
b=add(b,oldb);
c=add(c,oldc);
d=add(d,oldd);
}
return [a,b,c,d];
}
function hmac(data,key){
var wa=toWord(key);
if(wa.length>16){
wa=core(wa,key.length*_608);
}
var l=[],r=[];
for(var i=0;i<16;i++){
l[i]=wa[i]^909522486;
r[i]=wa[i]^1549556828;
}
var h=core(l.concat(toWord(data)),512+data.length*_608);
return core(r.concat(h),640);
}
this.compute=function(data,_655){
var out=_655||dojo.crypto.outputTypes.Base64;
switch(out){
case dojo.crypto.outputTypes.Hex:
return toHex(core(toWord(data),data.length*_608));
case dojo.crypto.outputTypes.String:
return toString(core(toWord(data),data.length*_608));
default:
return toBase64(core(toWord(data),data.length*_608));
}
};
this.getHMAC=function(data,key,_659){
var out=_659||dojo.crypto.outputTypes.Base64;
switch(out){
case dojo.crypto.outputTypes.Hex:
return toHex(hmac(data,key));
case dojo.crypto.outputTypes.String:
return toString(hmac(data,key));
default:
return toBase64(hmac(data,key));
}
};
}();
dojo.hostenv.conditionalLoadModule({common:["dojo.crypto","dojo.crypto.MD5"]});
dojo.hostenv.moduleLoaded("dojo.crypto.*");
dojo.provide("dojo.collections.Collections");
dojo.collections={Collections:true};
dojo.collections.DictionaryEntry=function(k,v){
this.key=k;
this.value=v;
this.valueOf=function(){
return this.value;
};
this.toString=function(){
return this.value;
};
};
dojo.collections.Iterator=function(a){
var obj=a;
var _65f=0;
this.atEnd=(_65f>=obj.length-1);
this.current=obj[_65f];
this.moveNext=function(){
if(++_65f>=obj.length){
this.atEnd=true;
}
if(this.atEnd){
return false;
}
this.current=obj[_65f];
return true;
};
this.reset=function(){
_65f=0;
this.atEnd=false;
this.current=obj[_65f];
};
};
dojo.collections.DictionaryIterator=function(obj){
var arr=[];
for(var p in obj){
arr.push(obj[p]);
}
var _663=0;
this.atEnd=(_663>=arr.length-1);
this.current=arr[_663]||null;
this.entry=this.current||null;
this.key=(this.entry)?this.entry.key:null;
this.value=(this.entry)?this.entry.value:null;
this.moveNext=function(){
if(++_663>=arr.length){
this.atEnd=true;
}
if(this.atEnd){
return false;
}
this.entry=this.current=arr[_663];
if(this.entry){
this.key=this.entry.key;
this.value=this.entry.value;
}
return true;
};
this.reset=function(){
_663=0;
this.atEnd=false;
this.current=arr[_663]||null;
this.entry=this.current||null;
this.key=(this.entry)?this.entry.key:null;
this.value=(this.entry)?this.entry.value:null;
};
};
dojo.provide("dojo.collections.ArrayList");
dojo.require("dojo.collections.Collections");
dojo.collections.ArrayList=function(arr){
var _665=[];
if(arr){
_665=_665.concat(arr);
}
this.count=_665.length;
this.add=function(obj){
_665.push(obj);
this.count=_665.length;
};
this.addRange=function(a){
if(a.getIterator){
var e=a.getIterator();
while(!e.atEnd){
this.add(e.current);
e.moveNext();
}
this.count=_665.length;
}else{
for(var i=0;i<a.length;i++){
_665.push(a[i]);
}
this.count=_665.length;
}
};
this.clear=function(){
_665.splice(0,_665.length);
this.count=0;
};
this.clone=function(){
return new dojo.collections.ArrayList(_665);
};
this.contains=function(obj){
for(var i=0;i<_665.length;i++){
if(_665[i]==obj){
return true;
}
}
return false;
};
this.getIterator=function(){
return new dojo.collections.Iterator(_665);
};
this.indexOf=function(obj){
for(var i=0;i<_665.length;i++){
if(_665[i]==obj){
return i;
}
}
return -1;
};
this.insert=function(i,obj){
_665.splice(i,0,obj);
this.count=_665.length;
};
this.item=function(k){
return _665[k];
};
this.remove=function(obj){
var i=this.indexOf(obj);
if(i>=0){
_665.splice(i,1);
}
this.count=_665.length;
};
this.removeAt=function(i){
_665.splice(i,1);
this.count=_665.length;
};
this.reverse=function(){
_665.reverse();
};
this.sort=function(fn){
if(fn){
_665.sort(fn);
}else{
_665.sort();
}
};
this.toArray=function(){
return [].concat(_665);
};
this.toString=function(){
return _665.join(",");
};
};
dojo.provide("dojo.collections.Queue");
dojo.require("dojo.collections.Collections");
dojo.collections.Queue=function(arr){
var q=[];
if(arr){
q=q.concat(arr);
}
this.count=q.length;
this.clear=function(){
q=[];
this.count=q.length;
};
this.clone=function(){
return new dojo.collections.Queue(q);
};
this.contains=function(o){
for(var i=0;i<q.length;i++){
if(q[i]==o){
return true;
}
}
return false;
};
this.copyTo=function(arr,i){
arr.splice(i,0,q);
};
this.dequeue=function(){
var r=q.shift();
this.count=q.length;
return r;
};
this.enqueue=function(o){
this.count=q.push(o);
};
this.getIterator=function(){
return new dojo.collections.Iterator(q);
};
this.peek=function(){
return q[0];
};
this.toArray=function(){
return [].concat(q);
};
};
dojo.provide("dojo.collections.Stack");
dojo.require("dojo.collections.Collections");
dojo.collections.Stack=function(arr){
var q=[];
if(arr){
q=q.concat(arr);
}
this.count=q.length;
this.clear=function(){
q=[];
this.count=q.length;
};
this.clone=function(){
return new dojo.collections.Stack(q);
};
this.contains=function(o){
for(var i=0;i<q.length;i++){
if(q[i]==o){
return true;
}
}
return false;
};
this.copyTo=function(arr,i){
arr.splice(i,0,q);
};
this.getIterator=function(){
return new dojo.collections.Iterator(q);
};
this.peek=function(){
return q[(q.length-1)];
};
this.pop=function(){
var r=q.pop();
this.count=q.length;
return r;
};
this.push=function(o){
this.count=q.push(o);
};
this.toArray=function(){
return [].concat(q);
};
};
dojo.provide("dojo.graphics.htmlEffects");
dojo.require("dojo.fx.*");
dj_deprecated("dojo.graphics.htmlEffects is deprecated, use dojo.fx.html instead");
dojo.graphics.htmlEffects=dojo.fx.html;
dojo.hostenv.conditionalLoadModule({browser:["dojo.graphics.htmlEffects"]});
dojo.hostenv.moduleLoaded("dojo.graphics.*");
dojo.require("dojo.lang");
dojo.provide("dojo.dnd.DragSource");
dojo.provide("dojo.dnd.DropTarget");
dojo.provide("dojo.dnd.DragObject");
dojo.provide("dojo.dnd.DragManager");
dojo.provide("dojo.dnd.DragAndDrop");
dojo.dnd.DragSource=function(){
dojo.dnd.dragManager.registerDragSource(this);
};
dojo.lang.extend(dojo.dnd.DragSource,{type:"",onDragEnd:function(){
},onDragStart:function(){
},unregister:function(){
dojo.dnd.dragManager.unregisterDragSource(this);
},reregister:function(){
dojo.dnd.dragManager.registerDragSource(this);
}});
dojo.dnd.DragObject=function(){
dojo.dnd.dragManager.registerDragObject(this);
};
dojo.lang.extend(dojo.dnd.DragObject,{type:"",onDragStart:function(){
},onDragMove:function(){
},onDragOver:function(){
},onDragOut:function(){
},onDragEnd:function(){
},onDragLeave:this.onDragOut,onDragEnter:this.onDragOver,ondragout:this.onDragOut,ondragover:this.onDragOver});
dojo.dnd.DropTarget=function(){
if(this.constructor==dojo.dnd.DropTarget){
return;
}
dojo.dnd.dragManager.registerDropTarget(this);
};
dojo.lang.extend(dojo.dnd.DropTarget,{acceptedTypes:[],onDragOver:function(){
},onDragOut:function(){
},onDragMove:function(){
},onDrop:function(){
}});
dojo.dnd.DragEvent=function(){
this.dragSource=null;
this.dragObject=null;
this.target=null;
this.eventStatus="success";
};
dojo.dnd.DragManager=function(){
};
dojo.lang.extend(dojo.dnd.DragManager,{selectedSources:[],dragObjects:[],dragSources:[],registerDragSource:function(){
},dropTargets:[],registerDropTarget:function(){
},lastDragTarget:null,currentDragTarget:null,onKeyDown:function(){
},onMouseOut:function(){
},onMouseMove:function(){
},onMouseUp:function(){
}});
dojo.dnd.dragManager=null;
dojo.provide("dojo.dnd.HtmlDragManager");
dojo.require("dojo.event.*");
dojo.require("dojo.lang");
dojo.require("dojo.html");
dojo.require("dojo.style");
dojo.dnd.HtmlDragManager=function(){
};
dojo.inherits(dojo.dnd.HtmlDragManager,dojo.dnd.DragManager);
dojo.lang.extend(dojo.dnd.HtmlDragManager,{disabled:false,nestedTargets:false,mouseDownTimer:null,dsCounter:0,dsPrefix:"dojoDragSource",dropTargetDimensions:[],currentDropTarget:null,currentDropTargetPoints:null,previousDropTarget:null,selectedSources:[],dragObjects:[],currentX:null,currentY:null,lastX:null,lastY:null,mouseDownX:null,mouseDownY:null,dropAcceptable:false,registerDragSource:function(ds){
if(ds["domNode"]){
var dp=this.dsPrefix;
var _687=dp+"Idx_"+(this.dsCounter++);
ds.dragSourceId=_687;
this.dragSources[_687]=ds;
ds.domNode.setAttribute(dp,_687);
}
},unregisterDragSource:function(ds){
if(ds["domNode"]){
var dp=this.dsPrefix;
var _68a=ds.dragSourceId;
delete ds.dragSourceId;
delete this.dragSources[_68a];
ds.domNode.setAttribute(dp,null);
}
},registerDropTarget:function(dt){
this.dropTargets.push(dt);
},getDragSource:function(e){
var tn=e.target;
if(tn===dojo.html.body()){
return;
}
var ta=dojo.html.getAttribute(tn,this.dsPrefix);
while((!ta)&&(tn)){
tn=tn.parentNode;
if((!tn)||(tn===dojo.html.body())){
return;
}
ta=dojo.html.getAttribute(tn,this.dsPrefix);
}
return this.dragSources[ta];
},onKeyDown:function(e){
},onMouseDown:function(e){
if(this.disabled){
return;
}
switch(e.target.tagName.toLowerCase()){
case "a":
case "button":
case "textarea":
case "input":
return;
}
var ds=this.getDragSource(e);
if(!ds){
return;
}
if(!dojo.lang.inArray(this.selectedSources,ds)){
this.selectedSources.push(ds);
}
e.preventDefault();
dojo.event.connect(document,"onmousemove",this,"onMouseMove");
},onMouseUp:function(e){
var _693=this;
e.dragSource=this.dragSource;
if((!e.shiftKey)&&(!e.ctrlKey)){
dojo.lang.forEach(this.dragObjects,function(_694){
var ret=null;
if(!_694){
return;
}
if(_693.currentDropTarget){
e.dragObject=_694;
var ce=_693.currentDropTarget.domNode.childNodes;
if(ce.length>0){
e.dropTarget=ce[0];
while(e.dropTarget==_694.domNode){
e.dropTarget=e.dropTarget.nextSibling;
}
}else{
e.dropTarget=_693.currentDropTarget.domNode;
}
if(_693.dropAcceptable){
ret=_693.currentDropTarget.onDrop(e);
}else{
_693.currentDropTarget.onDragOut(e);
}
}
e.dragStatus=_693.dropAcceptable&&ret?"dropSuccess":"dropFailure";
_694.onDragEnd(e);
});
this.selectedSources=[];
this.dragObjects=[];
this.dragSource=null;
}
dojo.event.disconnect(document,"onmousemove",this,"onMouseMove");
this.currentDropTarget=null;
this.currentDropTargetPoints=null;
},scrollBy:function(x,y){
for(var i=0;i<this.dragObjects.length;i++){
if(this.dragObjects[i].updateDragOffset){
this.dragObjects[i].updateDragOffset();
}
}
},onMouseMove:function(e){
var _69b=this;
if((this.selectedSources.length)&&(!this.dragObjects.length)){
if(this.selectedSources.length==1){
this.dragSource=this.selectedSources[0];
}
dojo.lang.forEach(this.selectedSources,function(_69c){
if(!_69c){
return;
}
var tdo=_69c.onDragStart(e);
if(tdo){
tdo.onDragStart(e);
_69b.dragObjects.push(tdo);
}
});
this.dropTargetDimensions=[];
dojo.lang.forEach(this.dropTargets,function(_69e){
var tn=_69e.domNode;
if(!tn){
return;
}
var ttx=dojo.style.getAbsoluteX(tn,true);
var tty=dojo.style.getAbsoluteY(tn,true);
_69b.dropTargetDimensions.push([[ttx,tty],[ttx+dojo.style.getInnerWidth(tn),tty+dojo.style.getInnerHeight(tn)],_69e]);
});
}
for(var i=0;i<this.dragObjects.length;i++){
if(this.dragObjects[i]){
this.dragObjects[i].onDragMove(e);
}
}
var dtp=this.currentDropTargetPoints;
if((!this.nestedTargets)&&(dtp)&&(this.isInsideBox(e,dtp))){
if(this.dropAcceptable){
this.currentDropTarget.onDragMove(e,this.dragObjects);
}
}else{
var _6a4=this.findBestTarget(e);
if(_6a4.target==null){
if(this.currentDropTarget){
this.currentDropTarget.onDragOut(e);
this.currentDropTarget=null;
this.currentDropTargetPoints=null;
}
this.dropAcceptable=false;
return;
}
if(this.currentDropTarget!=_6a4.target){
if(this.currentDropTarget){
this.currentDropTarget.onDragOut(e);
}
this.currentDropTarget=_6a4.target;
this.currentDropTargetPoints=_6a4.points;
e.dragObjects=this.dragObjects;
this.dropAcceptable=this.currentDropTarget.onDragOver(e);
}else{
if(this.dropAcceptable){
this.currentDropTarget.onDragMove(e,this.dragObjects);
}
}
}
},findBestTarget:function(e){
var _6a6=this;
var _6a7=new Object();
_6a7.target=null;
_6a7.points=null;
dojo.lang.forEach(this.dropTargetDimensions,function(_6a8){
if(_6a6.isInsideBox(e,_6a8)){
_6a7.target=_6a8[2];
_6a7.points=_6a8;
if(!_6a6.nestedTargets){
return "break";
}
}
});
return _6a7;
},isInsideBox:function(e,_6aa){
if((e.clientX>_6aa[0][0])&&(e.clientX<_6aa[1][0])&&(e.clientY>_6aa[0][1])&&(e.clientY<_6aa[1][1])){
return true;
}
return false;
},onMouseOver:function(e){
},onMouseOut:function(e){
}});
dojo.dnd.dragManager=new dojo.dnd.HtmlDragManager();
(function(){
var d=document;
var dm=dojo.dnd.dragManager;
dojo.event.connect(d,"onkeydown",dm,"onKeyDown");
dojo.event.connect(d,"onmouseover",dm,"onMouseOver");
dojo.event.connect(d,"onmouseout",dm,"onMouseOut");
dojo.event.connect(d,"onmousedown",dm,"onMouseDown");
dojo.event.connect(d,"onmouseup",dm,"onMouseUp");
dojo.event.connect(window,"scrollBy",dm,"scrollBy");
})();
dojo.provide("dojo.dnd.HtmlDragAndDrop");
dojo.provide("dojo.dnd.HtmlDragSource");
dojo.provide("dojo.dnd.HtmlDropTarget");
dojo.provide("dojo.dnd.HtmlDragObject");
dojo.require("dojo.dnd.HtmlDragManager");
dojo.require("dojo.animation.*");
dojo.require("dojo.dom");
dojo.require("dojo.style");
dojo.require("dojo.html");
dojo.require("dojo.lang");
dojo.dnd.HtmlDragSource=function(node,type){
node=dojo.byId(node);
if(node){
this.domNode=node;
this.dragObject=node;
dojo.dnd.DragSource.call(this);
this.type=type||this.domNode.nodeName.toLowerCase();
}
};
dojo.lang.extend(dojo.dnd.HtmlDragSource,{onDragStart:function(){
return new dojo.dnd.HtmlDragObject(this.dragObject,this.type);
},setDragHandle:function(node){
node=dojo.byId(node);
dojo.dnd.dragManager.unregisterDragSource(this);
this.domNode=node;
dojo.dnd.dragManager.registerDragSource(this);
},setDragTarget:function(node){
this.dragObject=node;
}});
dojo.dnd.HtmlDragObject=function(node,type){
node=dojo.byId(node);
this.type=type;
this.domNode=node;
};
dojo.lang.extend(dojo.dnd.HtmlDragObject,{onDragStart:function(e){
dojo.html.clearSelection();
this.scrollOffset={top:dojo.html.getScrollTop(),left:dojo.html.getScrollLeft()};
this.dragStartPosition={top:dojo.style.getAbsoluteY(this.domNode,true)+this.scrollOffset.top,left:dojo.style.getAbsoluteX(this.domNode,true)+this.scrollOffset.left};
this.dragOffset={top:this.dragStartPosition.top-e.clientY,left:this.dragStartPosition.left-e.clientX};
this.dragClone=this.domNode.cloneNode(true);
with(this.dragClone.style){
position="absolute";
top=this.dragOffset.top+e.clientY+"px";
left=this.dragOffset.left+e.clientX+"px";
}
dojo.style.setOpacity(this.dragClone,0.5);
dojo.html.body().appendChild(this.dragClone);
},updateDragOffset:function(){
var sTop=dojo.html.getScrollTop();
var _6b7=dojo.html.getScrollLeft();
if(sTop!=this.scrollOffset.top){
var diff=sTop-this.scrollOffset.top;
this.dragOffset.top+=diff;
this.scrollOffset.top=sTop;
}
},onDragMove:function(e){
this.dragClone.style.top=this.dragOffset.top+e.clientY+"px";
this.dragClone.style.left=this.dragOffset.left+e.clientX+"px";
},onDragEnd:function(e){
switch(e.dragStatus){
case "dropSuccess":
dojo.dom.removeNode(this.dragClone);
this.dragClone=null;
break;
case "dropFailure":
var _6bb=[dojo.style.getAbsoluteX(this.dragClone),dojo.style.getAbsoluteY(this.dragClone)];
var _6bc=[this.dragStartPosition.left+1,this.dragStartPosition.top+1];
var line=new dojo.math.curves.Line(_6bb,_6bc);
var anim=new dojo.animation.Animation(line,300,0,0);
var _6bf=this;
dojo.event.connect(anim,"onAnimate",function(e){
_6bf.dragClone.style.left=e.x+"px";
_6bf.dragClone.style.top=e.y+"px";
});
dojo.event.connect(anim,"onEnd",function(e){
dojo.lang.setTimeout(dojo.dom.removeNode,200,_6bf.dragClone);
});
anim.play();
break;
}
}});
dojo.dnd.HtmlDropTarget=function(node,_6c3){
if(arguments.length==0){
return;
}
node=dojo.byId(node);
this.domNode=node;
dojo.dnd.DropTarget.call(this);
this.acceptedTypes=_6c3||[];
};
dojo.inherits(dojo.dnd.HtmlDropTarget,dojo.dnd.DropTarget);
dojo.lang.extend(dojo.dnd.HtmlDropTarget,{onDragOver:function(e){
if(!dojo.lang.inArray(this.acceptedTypes,"*")){
for(var i=0;i<e.dragObjects.length;i++){
if(!dojo.lang.inArray(this.acceptedTypes,e.dragObjects[i].type)){
return false;
}
}
}
this.childBoxes=[];
for(var i=0,child;i<this.domNode.childNodes.length;i++){
child=this.domNode.childNodes[i];
if(child.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var top=dojo.style.getAbsoluteY(child);
var _6c7=top+dojo.style.getInnerHeight(child);
var left=dojo.style.getAbsoluteX(child);
var _6c9=left+dojo.style.getInnerWidth(child);
this.childBoxes.push({top:top,bottom:_6c7,left:left,right:_6c9,node:child});
}
return true;
},_getNodeUnderMouse:function(e){
var _6cb=e.pageX||e.clientX+dojo.html.body().scrollLeft;
var _6cc=e.pageY||e.clientY+dojo.html.body().scrollTop;
for(var i=0,child;i<this.childBoxes.length;i++){
with(this.childBoxes[i]){
if(_6cb>=left&&_6cb<=right&&_6cc>=top&&_6cc<=bottom){
return i;
}
}
}
return -1;
},onDragMove:function(e){
var i=this._getNodeUnderMouse(e);
if(!this.dropIndicator){
this.dropIndicator=document.createElement("div");
with(this.dropIndicator.style){
position="absolute";
zIndex=1;
borderTopWidth="1px";
borderTopColor="black";
borderTopStyle="solid";
width=dojo.style.getInnerWidth(this.domNode)+"px";
left=dojo.style.getAbsoluteX(this.domNode)+"px";
}
}
with(this.dropIndicator.style){
if(i<0){
if(this.childBoxes.length){
top=((dojo.html.gravity(this.childBoxes[0].node,e)&dojo.html.gravity.NORTH)?this.childBoxes[0].top:this.childBoxes[this.childBoxes.length-1].bottom)+"px";
}else{
top=dojo.style.getAbsoluteY(this.domNode)+"px";
}
}else{
var _6d0=this.childBoxes[i];
top=((dojo.html.gravity(_6d0.node,e)&dojo.html.gravity.NORTH)?_6d0.top:_6d0.bottom)+"px";
}
}
if(!this.dropIndicator.parentNode){
dojo.html.body().appendChild(this.dropIndicator);
}
},onDragOut:function(e){
dojo.dom.removeNode(this.dropIndicator);
delete this.dropIndicator;
},onDrop:function(e){
this.onDragOut(e);
var i=this._getNodeUnderMouse(e);
if(i<0){
if(this.childBoxes.length){
if(dojo.html.gravity(this.childBoxes[0].node,e)&dojo.html.gravity.NORTH){
return dojo.dom.insertBefore(e.dragObject.domNode,this.childBoxes[0].node);
}else{
return dojo.dom.insertAfter(e.dragObject.domNode,this.childBoxes[this.childBoxes.length-1].node);
}
}
this.domNode.appendChild(e.dragObject.domNode);
return true;
}
var _6d4=this.childBoxes[i];
if(dojo.html.gravity(_6d4.node,e)&dojo.html.gravity.NORTH){
return dojo.dom.insertBefore(e.dragObject.domNode,_6d4.node);
}else{
return dojo.dom.insertAfter(e.dragObject.domNode,_6d4.node);
}
}});
dojo.hostenv.conditionalLoadModule({common:["dojo.dnd.DragAndDrop"],browser:["dojo.dnd.HtmlDragAndDrop"]});
dojo.hostenv.moduleLoaded("dojo.dnd.*");
dojo.provide("dojo.widget.Manager");
dojo.require("dojo.lang");
dojo.require("dojo.event.*");
dojo.widget.manager=new function(){
this.widgets=[];
this.widgetIds=[];
this.topWidgets={};
var _6d5={};
var _6d6=[];
this.getUniqueId=function(_6d7){
return _6d7+"_"+(_6d5[_6d7]!=undefined?++_6d5[_6d7]:_6d5[_6d7]=0);
};
this.add=function(_6d8){
dojo.profile.start("dojo.widget.manager.add");
this.widgets.push(_6d8);
if(_6d8.widgetId==""){
if(_6d8["id"]){
_6d8.widgetId=_6d8["id"];
}else{
if(_6d8.extraArgs["id"]){
_6d8.widgetId=_6d8.extraArgs["id"];
}else{
_6d8.widgetId=this.getUniqueId(_6d8.widgetType);
}
}
}
if(this.widgetIds[_6d8.widgetId]){
dojo.debug("widget ID collision on ID: "+_6d8.widgetId);
}
this.widgetIds[_6d8.widgetId]=_6d8;
dojo.profile.end("dojo.widget.manager.add");
};
this.destroyAll=function(){
for(var x=this.widgets.length-1;x>=0;x--){
try{
this.widgets[x].destroy(true);
delete this.widgets[x];
}
catch(e){
}
}
};
this.remove=function(_6da){
var tw=this.widgets[_6da].widgetId;
delete this.widgetIds[tw];
this.widgets.splice(_6da,1);
};
this.removeById=function(id){
for(var i=0;i<this.widgets.length;i++){
if(this.widgets[i].widgetId==id){
this.remove(i);
break;
}
}
};
this.getWidgetById=function(id){
return this.widgetIds[id];
};
this.getWidgetsByType=function(type){
var lt=type.toLowerCase();
var ret=[];
dojo.lang.forEach(this.widgets,function(x){
if(x.widgetType.toLowerCase()==lt){
ret.push(x);
}
});
return ret;
};
this.getWidgetsOfType=function(id){
dj_deprecated("getWidgetsOfType is depecrecated, use getWidgetsByType");
return dojo.widget.manager.getWidgetsByType(id);
};
this.getWidgetsByFilter=function(_6e4){
var ret=[];
dojo.lang.forEach(this.widgets,function(x){
if(_6e4(x)){
ret.push(x);
}
});
return ret;
};
this.getAllWidgets=function(){
return this.widgets.concat();
};
this.byId=this.getWidgetById;
this.byType=this.getWidgetsByType;
this.byFilter=this.getWidgetsByFilter;
var _6e7={};
var _6e8=["dojo.widget","dojo.webui.widgets"];
for(var i=0;i<_6e8.length;i++){
_6e8[_6e8[i]]=true;
}
this.registerWidgetPackage=function(_6ea){
if(!_6e8[_6ea]){
_6e8[_6ea]=true;
_6e8.push(_6ea);
}
};
this.getWidgetPackageList=function(){
return dojo.lang.map(_6e8,function(elt){
return (elt!==true?elt:undefined);
});
};
this.getImplementation=function(_6ec,_6ed,_6ee){
var impl=this.getImplementationName(_6ec);
if(impl){
var ret=new impl(_6ed);
return ret;
}
};
this.getImplementationName=function(_6f1){
var _6f2=_6f1.toLowerCase();
var impl=_6e7[_6f2];
if(impl){
return impl;
}
if(!_6d6.length){
for(var _6f4 in dojo.render){
if(dojo.render[_6f4]["capable"]===true){
var _6f5=dojo.render[_6f4].prefixes;
for(var i=0;i<_6f5.length;i++){
_6d6.push(_6f5[i].toLowerCase());
}
}
}
_6d6.push("");
}
for(var i=0;i<_6e8.length;i++){
var _6f7=dojo.evalObjPath(_6e8[i]);
if(!_6f7){
continue;
}
for(var j=0;j<_6d6.length;j++){
if(!_6f7[_6d6[j]]){
continue;
}
for(var _6f9 in _6f7[_6d6[j]]){
if(_6f9.toLowerCase()!=_6f2){
continue;
}
_6e7[_6f2]=_6f7[_6d6[j]][_6f9];
return _6e7[_6f2];
}
}
for(var j=0;j<_6d6.length;j++){
for(var _6f9 in _6f7){
if(_6f9.toLowerCase()!=(_6d6[j]+_6f2)){
continue;
}
_6e7[_6f2]=_6f7[_6f9];
return _6e7[_6f2];
}
}
}
throw new Error("Could not locate \""+_6f1+"\" class");
};
this.onResized=function(){
for(var id in this.topWidgets){
var _6fb=this.topWidgets[id];
if(_6fb.onResized){
_6fb.onResized();
}
}
};
if(typeof window!="undefined"){
dojo.addOnLoad(this,"onResized");
dojo.event.connect(window,"onresize",this,"onResized");
}
};
dojo.widget.getUniqueId=function(){
return dojo.widget.manager.getUniqueId.apply(dojo.widget.manager,arguments);
};
dojo.widget.addWidget=function(){
return dojo.widget.manager.add.apply(dojo.widget.manager,arguments);
};
dojo.widget.destroyAllWidgets=function(){
return dojo.widget.manager.destroyAll.apply(dojo.widget.manager,arguments);
};
dojo.widget.removeWidget=function(){
return dojo.widget.manager.remove.apply(dojo.widget.manager,arguments);
};
dojo.widget.removeWidgetById=function(){
return dojo.widget.manager.removeById.apply(dojo.widget.manager,arguments);
};
dojo.widget.getWidgetById=function(){
return dojo.widget.manager.getWidgetById.apply(dojo.widget.manager,arguments);
};
dojo.widget.getWidgetsByType=function(){
return dojo.widget.manager.getWidgetsByType.apply(dojo.widget.manager,arguments);
};
dojo.widget.getWidgetsByFilter=function(){
return dojo.widget.manager.getWidgetsByFilter.apply(dojo.widget.manager,arguments);
};
dojo.widget.byId=function(){
return dojo.widget.manager.getWidgetById.apply(dojo.widget.manager,arguments);
};
dojo.widget.byType=function(){
return dojo.widget.manager.getWidgetsByType.apply(dojo.widget.manager,arguments);
};
dojo.widget.byFilter=function(){
return dojo.widget.manager.getWidgetsByFilter.apply(dojo.widget.manager,arguments);
};
dojo.widget.all=function(){
return dojo.widget.manager.getAllWidgets.apply(dojo.widget.manager,arguments);
};
dojo.widget.registerWidgetPackage=function(){
return dojo.widget.manager.registerWidgetPackage.apply(dojo.widget.manager,arguments);
};
dojo.widget.getWidgetImplementation=function(){
return dojo.widget.manager.getImplementation.apply(dojo.widget.manager,arguments);
};
dojo.widget.getWidgetImplementationName=function(){
return dojo.widget.manager.getImplementationName.apply(dojo.widget.manager,arguments);
};
dojo.widget.widgets=dojo.widget.manager.widgets;
dojo.widget.widgetIds=dojo.widget.manager.widgetIds;
dojo.widget.root=dojo.widget.manager.root;
dojo.provide("dojo.widget.Widget");
dojo.provide("dojo.widget.tags");
dojo.require("dojo.lang");
dojo.require("dojo.widget.Manager");
dojo.require("dojo.event.*");
dojo.require("dojo.string");
dojo.widget.Widget=function(){
this.children=[];
this.extraArgs={};
};
dojo.lang.extend(dojo.widget.Widget,{parent:null,isTopLevel:false,isModal:false,isEnabled:true,isHidden:false,isContainer:false,widgetId:"",widgetType:"Widget",toString:function(){
return "[Widget "+this.widgetType+", "+(this.widgetId||"NO ID")+"]";
},repr:function(){
return this.toString();
},enable:function(){
this.isEnabled=true;
},disable:function(){
this.isEnabled=false;
},hide:function(){
this.isHidden=true;
},show:function(){
this.isHidden=false;
},create:function(args,_6fd,_6fe){
this.satisfyPropertySets(args,_6fd,_6fe);
this.mixInProperties(args,_6fd,_6fe);
this.postMixInProperties(args,_6fd,_6fe);
dojo.widget.manager.add(this);
this.buildRendering(args,_6fd,_6fe);
this.initialize(args,_6fd,_6fe);
this.postInitialize(args,_6fd,_6fe);
this.postCreate(args,_6fd,_6fe);
return this;
},destroy:function(_6ff){
this.uninitialize();
this.destroyRendering(_6ff);
dojo.widget.manager.removeById(this.widgetId);
},destroyChildren:function(_700){
_700=(!_700)?function(){
return true;
}:_700;
for(var x=0;x<this.children.length;x++){
var tc=this.children[x];
if((tc)&&(_700(tc))){
tc.destroy();
}
}
},destroyChildrenOfType:function(type){
type=type.toLowerCase();
this.destroyChildren(function(item){
if(item.widgetType.toLowerCase()==type){
return true;
}else{
return false;
}
});
},getChildrenOfType:function(type,_706){
var ret=[];
type=type.toLowerCase();
for(var x=0;x<this.children.length;x++){
if(this.children[x].widgetType.toLowerCase()==type){
ret.push(this.children[x]);
}
if(_706){
ret=ret.concat(this.children[x].getChildrenOfType(type,_706));
}
}
return ret;
},satisfyPropertySets:function(args){
return args;
},mixInProperties:function(args,frag){
if((args["fastMixIn"])||(frag["fastMixIn"])){
for(var x in args){
this[x]=args[x];
}
return;
}
var _70d;
var _70e=dojo.widget.lcArgsCache[this.widgetType];
if(_70e==null){
_70e={};
for(var y in this){
_70e[((new String(y)).toLowerCase())]=y;
}
dojo.widget.lcArgsCache[this.widgetType]=_70e;
}
var _710={};
for(var x in args){
if(!this[x]){
var y=_70e[(new String(x)).toLowerCase()];
if(y){
args[y]=args[x];
x=y;
}
}
if(_710[x]){
continue;
}
_710[x]=true;
if((typeof this[x])!=(typeof _70d)){
if(typeof args[x]!="string"){
this[x]=args[x];
}else{
if(dojo.lang.isString(this[x])){
this[x]=args[x];
}else{
if(dojo.lang.isNumber(this[x])){
this[x]=new Number(args[x]);
}else{
if(dojo.lang.isBoolean(this[x])){
this[x]=(args[x].toLowerCase()=="false")?false:true;
}else{
if(dojo.lang.isFunction(this[x])){
var tn=dojo.lang.nameAnonFunc(new Function(args[x]),this);
dojo.event.connect(this,x,this,tn);
}else{
if(dojo.lang.isArray(this[x])){
this[x]=args[x].split(";");
}else{
if(this[x] instanceof Date){
this[x]=new Date(Number(args[x]));
}else{
if(typeof this[x]=="object"){
var _712=args[x].split(";");
for(var y=0;y<_712.length;y++){
var si=_712[y].indexOf(":");
if((si!=-1)&&(_712[y].length>si)){
this[x][dojo.string.trim(_712[y].substr(0,si))]=_712[y].substr(si+1);
}
}
}else{
this[x]=args[x];
}
}
}
}
}
}
}
}
}else{
this.extraArgs[x]=args[x];
}
}
},postMixInProperties:function(){
},initialize:function(args,frag){
return false;
},postInitialize:function(args,frag){
return false;
},postCreate:function(args,frag){
return false;
},uninitialize:function(){
return false;
},buildRendering:function(){
dj_unimplemented("dojo.widget.Widget.buildRendering, on "+this.toString()+", ");
return false;
},destroyRendering:function(){
dj_unimplemented("dojo.widget.Widget.destroyRendering");
return false;
},cleanUp:function(){
dj_unimplemented("dojo.widget.Widget.cleanUp");
return false;
},addedTo:function(_71a){
},addChild:function(_71b){
dj_unimplemented("dojo.widget.Widget.addChild");
return false;
},addChildAtIndex:function(_71c,_71d){
dj_unimplemented("dojo.widget.Widget.addChildAtIndex");
return false;
},removeChild:function(_71e){
dj_unimplemented("dojo.widget.Widget.removeChild");
return false;
},removeChildAtIndex:function(_71f){
dj_unimplemented("dojo.widget.Widget.removeChildAtIndex");
return false;
},resize:function(_720,_721){
this.setWidth(_720);
this.setHeight(_721);
},setWidth:function(_722){
if((typeof _722=="string")&&(_722.substr(-1)=="%")){
this.setPercentageWidth(_722);
}else{
this.setNativeWidth(_722);
}
},setHeight:function(_723){
if((typeof _723=="string")&&(_723.substr(-1)=="%")){
this.setPercentageHeight(_723);
}else{
this.setNativeHeight(_723);
}
},setPercentageHeight:function(_724){
return false;
},setNativeHeight:function(_725){
return false;
},setPercentageWidth:function(_726){
return false;
},setNativeWidth:function(_727){
return false;
}});
dojo.widget.lcArgsCache={};
dojo.widget.tags={};
dojo.widget.tags.addParseTreeHandler=function(type){
var _729=type.toLowerCase();
this[_729]=function(_72a,_72b,_72c,_72d){
return dojo.widget.buildWidgetFromParseTree(_729,_72a,_72b,_72c,_72d);
};
};
dojo.widget.tags.addParseTreeHandler("dojo:widget");
dojo.widget.tags["dojo:propertyset"]=function(_72e,_72f,_730){
var _731=_72f.parseProperties(_72e["dojo:propertyset"]);
};
dojo.widget.tags["dojo:connect"]=function(_732,_733,_734){
var _735=_733.parseProperties(_732["dojo:connect"]);
};
dojo.widget.buildWidgetFromParseTree=function(type,frag,_738,_739,_73a){
var _73b={};
var _73c=type.split(":");
_73c=(_73c.length==2)?_73c[1]:type;
var _73b=_738.parseProperties(frag["dojo:"+_73c]);
var _73d=dojo.widget.manager.getImplementation(_73c);
if(!_73d){
throw new Error("cannot find \""+_73c+"\" widget");
}else{
if(!_73d.create){
throw new Error("\""+_73c+"\" widget object does not appear to implement *Widget");
}
}
_73b["dojoinsertionindex"]=_73a;
var ret=_73d.create(_73b,frag,_739);
return ret;
};
dojo.provide("dojo.widget.Parse");
dojo.require("dojo.widget.Manager");
dojo.require("dojo.string");
dojo.require("dojo.dom");
dojo.widget.Parse=function(_73f){
this.propertySetsList=[];
this.fragment=_73f;
this.createComponents=function(_740,_741){
var _742=dojo.widget.tags;
var _743=[];
for(var item in _740){
var _745=false;
try{
if(_740[item]&&(_740[item]["tagName"])&&(_740[item]!=_740["nodeRef"])){
var tn=new String(_740[item]["tagName"]);
var tna=tn.split(";");
for(var x=0;x<tna.length;x++){
var ltn=dojo.string.trim(tna[x]).toLowerCase();
if(_742[ltn]){
_745=true;
_740[item].tagName=ltn;
_743.push(_742[ltn](_740[item],this,_741,_740[item]["index"]));
}else{
if(ltn.substr(0,5)=="dojo:"){
dojo.debug("no tag handler registed for type: ",ltn);
}
}
}
}
}
catch(e){
dojo.debug(e);
}
if((!_745)&&(typeof _740[item]=="object")&&(_740[item]!=_740.nodeRef)&&(_740[item]!=_740["tagName"])){
_743.push(this.createComponents(_740[item],_741));
}
}
return _743;
};
this.parsePropertySets=function(_74a){
return [];
var _74b=[];
for(var item in _74a){
if((_74a[item]["tagName"]=="dojo:propertyset")){
_74b.push(_74a[item]);
}
}
this.propertySetsList.push(_74b);
return _74b;
};
this.parseProperties=function(_74d){
var _74e={};
for(var item in _74d){
if((_74d[item]==_74d["tagName"])||(_74d[item]==_74d.nodeRef)){
}else{
if((_74d[item]["tagName"])&&(dojo.widget.tags[_74d[item].tagName.toLowerCase()])){
}else{
if((_74d[item][0])&&(_74d[item][0].value!="")){
try{
if(item.toLowerCase()=="dataprovider"){
var _750=this;
this.getDataProvider(_750,_74d[item][0].value);
_74e.dataProvider=this.dataProvider;
}
_74e[item]=_74d[item][0].value;
var _751=this.parseProperties(_74d[item]);
for(var _752 in _751){
_74e[_752]=_751[_752];
}
}
catch(e){
dj_debug(e);
}
}
}
}
}
return _74e;
};
this.getDataProvider=function(_753,_754){
dojo.io.bind({url:_754,load:function(type,_756){
if(type=="load"){
_753.dataProvider=_756;
}
},mimetype:"text/javascript",sync:true});
};
this.getPropertySetById=function(_757){
for(var x=0;x<this.propertySetsList.length;x++){
if(_757==this.propertySetsList[x]["id"][0].value){
return this.propertySetsList[x];
}
}
return "";
};
this.getPropertySetsByType=function(_759){
var _75a=[];
for(var x=0;x<this.propertySetsList.length;x++){
var cpl=this.propertySetsList[x];
var cpcc=cpl["componentClass"]||cpl["componentType"]||null;
if((cpcc)&&(propertySetId==cpcc[0].value)){
_75a.push(cpl);
}
}
return _75a;
};
this.getPropertySets=function(_75e){
var ppl="dojo:propertyproviderlist";
var _760=[];
var _761=_75e["tagName"];
if(_75e[ppl]){
var _762=_75e[ppl].value.split(" ");
for(propertySetId in _762){
if((propertySetId.indexOf("..")==-1)&&(propertySetId.indexOf("://")==-1)){
var _763=this.getPropertySetById(propertySetId);
if(_763!=""){
_760.push(_763);
}
}else{
}
}
}
return (this.getPropertySetsByType(_761)).concat(_760);
};
this.createComponentFromScript=function(_764,_765,_766,_767){
var frag={};
var _769="dojo:"+_765.toLowerCase();
frag[_769]={};
var bo={};
_766.dojotype=_765;
for(var prop in _766){
if(typeof bo[prop]=="undefined"){
frag[_769][prop]=[{value:_766[prop]}];
}
}
frag[_769].nodeRef=_764;
frag.tagName=_769;
var _76c=[frag];
if(_767){
_76c[0].fastMixIn=true;
}
return this.createComponents(_76c);
};
};
dojo.widget._parser_collection={"dojo":new dojo.widget.Parse()};
dojo.widget.getParser=function(name){
if(!name){
name="dojo";
}
if(!this._parser_collection[name]){
this._parser_collection[name]=new dojo.widget.Parse();
}
return this._parser_collection[name];
};
dojo.widget.fromScript=function(name,_76f,_770,_771){
if((typeof name!="string")&&(typeof _76f=="string")){
return dojo.widget._oldFromScript(name,_76f,_770);
}
_76f=_76f||{};
var _772=false;
var tn=null;
var h=dojo.render.html.capable;
if(h){
tn=document.createElement("span");
}
if(!_770){
_772=true;
_770=tn;
if(h){
dojo.html.body().appendChild(_770);
}
}else{
if(_771){
dojo.dom.insertAtPosition(tn,_770,_771);
}else{
tn=_770;
}
}
var _775=dojo.widget._oldFromScript(tn,name,_76f);
if(!_775[0]||typeof _775[0].widgetType=="undefined"){
throw new Error("Creation of \""+name+"\" widget fromScript failed.");
}
if(_772){
if(_775[0].domNode.parentNode){
_775[0].domNode.parentNode.removeChild(_775[0].domNode);
}
}
return _775[0];
};
dojo.widget._oldFromScript=function(_776,name,_778){
var ln=name.toLowerCase();
var tn="dojo:"+ln;
_778[tn]={dojotype:[{value:ln}],nodeRef:_776,fastMixIn:true};
var ret=dojo.widget.getParser().createComponentFromScript(_776,name,_778,true);
return ret;
};
dojo.provide("dojo.widget.DomWidget");
dojo.require("dojo.event.*");
dojo.require("dojo.string");
dojo.require("dojo.widget.Widget");
dojo.require("dojo.dom");
dojo.require("dojo.xml.Parse");
dojo.require("dojo.uri.*");
dojo.widget._cssFiles={};
dojo.widget.defaultStrings={dojoRoot:dojo.hostenv.getBaseScriptUri(),baseScriptUri:dojo.hostenv.getBaseScriptUri()};
dojo.widget.buildFromTemplate=function(obj,_77d,_77e,_77f){
var _780=_77d||obj.templatePath;
var _781=_77e||obj.templateCssPath;
if(!_781&&obj.templateCSSPath){
obj.templateCssPath=_781=obj.templateCSSPath;
obj.templateCSSPath=null;
dj_deprecated("templateCSSPath is deprecated, use templateCssPath");
}
if(_780&&!(_780 instanceof dojo.uri.Uri)){
_780=dojo.uri.dojoUri(_780);
dj_deprecated("templatePath should be of type dojo.uri.Uri");
}
if(_781&&!(_781 instanceof dojo.uri.Uri)){
_781=dojo.uri.dojoUri(_781);
dj_deprecated("templateCssPath should be of type dojo.uri.Uri");
}
var _782=dojo.widget.DomWidget.templates;
if(!obj["widgetType"]){
do{
var _783="__dummyTemplate__"+dojo.widget.buildFromTemplate.dummyCount++;
}while(_782[_783]);
obj.widgetType=_783;
}
if((_781)&&(!dojo.widget._cssFiles[_781])){
dojo.html.insertCssFile(_781);
obj.templateCssPath=null;
dojo.widget._cssFiles[_781]=true;
}
var ts=_782[obj.widgetType];
if(!ts){
_782[obj.widgetType]={};
ts=_782[obj.widgetType];
}
if(!obj.templateString){
obj.templateString=_77f||ts["string"];
}
if(!obj.templateNode){
obj.templateNode=ts["node"];
}
if((!obj.templateNode)&&(!obj.templateString)&&(_780)){
var _785=dojo.hostenv.getText(_780);
if(_785){
var _786=_785.match(/<body[^>]*>\s*([\s\S]+)\s*<\/body>/im);
if(_786){
_785=_786[1];
}
}else{
_785="";
}
obj.templateString=_785;
ts.string=_785;
}
if(!ts["string"]){
ts.string=obj.templateString;
}
};
dojo.widget.buildFromTemplate.dummyCount=0;
dojo.widget.attachProperties=["dojoAttachPoint","id"];
dojo.widget.eventAttachProperty="dojoAttachEvent";
dojo.widget.onBuildProperty="dojoOnBuild";
dojo.widget.attachTemplateNodes=function(_787,_788,_789){
var _78a=dojo.dom.ELEMENT_NODE;
if(!_787){
_787=_788.domNode;
}
if(_787.nodeType!=_78a){
return;
}
var _78b=_787.getElementsByTagName("*");
var _78c=_788;
for(var x=-1;x<_78b.length;x++){
var _78e=(x==-1)?_787:_78b[x];
var _78f=[];
for(var y=0;y<this.attachProperties.length;y++){
var _791=_78e.getAttribute(this.attachProperties[y]);
if(_791){
_78f=_791.split(";");
for(var z=0;z<this.attachProperties.length;z++){
if((_788[_78f[z]])&&(dojo.lang.isArray(_788[_78f[z]]))){
_788[_78f[z]].push(_78e);
}else{
_788[_78f[z]]=_78e;
}
}
break;
}
}
var _793=_78e.getAttribute(this.templateProperty);
if(_793){
_788[_793]=_78e;
}
var _794=_78e.getAttribute(this.eventAttachProperty);
if(_794){
var evts=_794.split(";");
for(var y=0;y<evts.length;y++){
if((!evts[y])||(!evts[y].length)){
continue;
}
var _796=null;
var tevt=dojo.string.trim(evts[y]);
if(evts[y].indexOf(":")>=0){
var _798=tevt.split(":");
tevt=dojo.string.trim(_798[0]);
_796=dojo.string.trim(_798[1]);
}
if(!_796){
_796=tevt;
}
var tf=function(){
var ntf=new String(_796);
return function(evt){
if(_78c[ntf]){
_78c[ntf](dojo.event.browser.fixEvent(evt));
}
};
}();
dojo.event.browser.addListener(_78e,tevt,tf,false,true);
}
}
for(var y=0;y<_789.length;y++){
var _79c=_78e.getAttribute(_789[y]);
if((_79c)&&(_79c.length)){
var _796=null;
var _79d=_789[y].substr(4);
_796=dojo.string.trim(_79c);
var tf=function(){
var ntf=new String(_796);
return function(evt){
if(_78c[ntf]){
_78c[ntf](dojo.event.browser.fixEvent(evt));
}
};
}();
dojo.event.browser.addListener(_78e,_79d,tf,false,true);
}
}
var _7a0=_78e.getAttribute(this.onBuildProperty);
if(_7a0){
eval("var node = baseNode; var widget = targetObj; "+_7a0);
}
_78e.id="";
}
};
dojo.widget.getDojoEventsFromStr=function(str){
var re=/(dojoOn([a-z]+)(\s?))=/gi;
var evts=str?str.match(re)||[]:[];
var ret=[];
var lem={};
for(var x=0;x<evts.length;x++){
if(evts[x].legth<1){
continue;
}
var cm=evts[x].replace(/\s/,"");
cm=(cm.slice(0,cm.length-1));
if(!lem[cm]){
lem[cm]=true;
ret.push(cm);
}
}
return ret;
};
dojo.widget.buildAndAttachTemplate=function(obj,_7a9,_7aa,_7ab,_7ac){
this.buildFromTemplate(obj,_7a9,_7aa,_7ab);
var node=dojo.dom.createNodesFromText(obj.templateString,true)[0];
this.attachTemplateNodes(node,_7ac||obj,dojo.widget.getDojoEventsFromStr(_7ab));
return node;
};
dojo.widget.DomWidget=function(){
dojo.widget.Widget.call(this);
if((arguments.length>0)&&(typeof arguments[0]=="object")){
this.create(arguments[0]);
}
};
dojo.inherits(dojo.widget.DomWidget,dojo.widget.Widget);
dojo.lang.extend(dojo.widget.DomWidget,{templateNode:null,templateString:null,preventClobber:false,domNode:null,containerNode:null,addChild:function(_7ae,_7af,pos,ref,_7b2){
if(!this.isContainer){
dojo.debug("dojo.widget.DomWidget.addChild() attempted on non-container widget");
return null;
}else{
this.addWidgetAsDirectChild(_7ae,_7af,pos,ref,_7b2);
this.registerChild(_7ae);
}
return _7ae;
},addWidgetAsDirectChild:function(_7b3,_7b4,pos,ref,_7b7){
if((!this.containerNode)&&(!_7b4)){
this.containerNode=this.domNode;
}
var cn=(_7b4)?_7b4:this.containerNode;
if(!pos){
pos="after";
}
if(!ref){
ref=cn.lastChild;
}
if(!_7b7){
_7b7=0;
}
_7b3.domNode.setAttribute("dojoinsertionindex",_7b7);
if(!ref){
cn.appendChild(_7b3.domNode);
}else{
if(pos=="insertAtIndex"){
dojo.dom.insertAtIndex(_7b3.domNode,ref.parentNode,_7b7);
}else{
if((pos=="after")&&(ref===cn.lastChild)){
cn.appendChild(_7b3.domNode);
}else{
dojo.dom.insertAtPosition(_7b3.domNode,cn,pos);
}
}
}
},registerChild:function(_7b9,_7ba){
_7b9.dojoInsertionIndex=_7ba;
var idx=-1;
for(var i=0;i<this.children.length;i++){
if(this.children[i].dojoInsertionIndex<_7ba){
idx=i;
}
}
this.children.splice(idx+1,0,_7b9);
_7b9.parent=this;
_7b9.addedTo(this);
delete dojo.widget.manager.topWidgets[_7b9.widgetId];
},removeChild:function(_7bd){
for(var x=0;x<this.children.length;x++){
if(this.children[x]===_7bd){
this.children.splice(x,1);
break;
}
}
return _7bd;
},getFragNodeRef:function(frag){
if(!frag["dojo:"+this.widgetType.toLowerCase()]){
dojo.raise("Error: no frag for widget type "+this.widgetType+", id "+this.widgetId+" (maybe a widget has set it's type incorrectly)");
}
return (frag?frag["dojo:"+this.widgetType.toLowerCase()]["nodeRef"]:null);
},postInitialize:function(args,frag,_7c2){
var _7c3=this.getFragNodeRef(frag);
if(_7c2&&(_7c2.snarfChildDomOutput||!_7c3)){
_7c2.addWidgetAsDirectChild(this,"","insertAtIndex","",args["dojoinsertionindex"],_7c3);
}else{
if(_7c3){
if(this.domNode&&(this.domNode!==_7c3)){
var _7c4=_7c3.parentNode.replaceChild(this.domNode,_7c3);
}
}
}
if(_7c2){
_7c2.registerChild(this,args.dojoinsertionindex);
}else{
dojo.widget.manager.topWidgets[this.widgetId]=this;
}
if(this.isContainer){
var _7c5=dojo.widget.getParser();
_7c5.createComponents(frag,this);
}
},startResize:function(_7c6){
dj_unimplemented("dojo.widget.DomWidget.startResize");
},updateResize:function(_7c7){
dj_unimplemented("dojo.widget.DomWidget.updateResize");
},endResize:function(_7c8){
dj_unimplemented("dojo.widget.DomWidget.endResize");
},buildRendering:function(args,frag){
var ts=dojo.widget.DomWidget.templates[this.widgetType];
if((!this.preventClobber)&&((this.templatePath)||(this.templateNode)||((this["templateString"])&&(this.templateString.length))||((typeof ts!="undefined")&&((ts["string"])||(ts["node"]))))){
this.buildFromTemplate(args,frag);
}else{
this.domNode=this.getFragNodeRef(frag);
}
this.fillInTemplate(args,frag);
},buildFromTemplate:function(args,frag){
var ts=dojo.widget.DomWidget.templates[this.widgetType];
if(ts){
if(!this.templateString.length){
this.templateString=ts["string"];
}
if(!this.templateNode){
this.templateNode=ts["node"];
}
}
var _7cf=false;
var node=null;
var tstr=new String(this.templateString);
if((!this.templateNode)&&(this.templateString)){
_7cf=this.templateString.match(/\$\{([^\}]+)\}/g);
if(_7cf){
var hash=this.strings||{};
for(var key in dojo.widget.defaultStrings){
if(dojo.lang.isUndefined(hash[key])){
hash[key]=dojo.widget.defaultStrings[key];
}
}
for(var i=0;i<_7cf.length;i++){
var key=_7cf[i];
key=key.substring(2,key.length-1);
var kval=(key.substring(0,5)=="this.")?this[key.substring(5)]:hash[key];
var _7d6;
if((kval)||(dojo.lang.isString(kval))){
_7d6=(dojo.lang.isFunction(kval))?kval.call(this,key,this.templateString):kval;
tstr=tstr.replace(_7cf[i],_7d6);
}
}
}else{
this.templateNode=this.createNodesFromText(this.templateString,true)[0];
ts.node=this.templateNode;
}
}
if((!this.templateNode)&&(!_7cf)){
dojo.debug("weren't able to create template!");
return false;
}else{
if(!_7cf){
node=this.templateNode.cloneNode(true);
if(!node){
return false;
}
}else{
node=this.createNodesFromText(tstr,true)[0];
}
}
this.domNode=node;
this.attachTemplateNodes(this.domNode,this);
if(this.isContainer&&this.containerNode){
var src=this.getFragNodeRef(frag);
if(src){
dojo.dom.moveChildren(src,this.containerNode);
}
}
},attachTemplateNodes:function(_7d8,_7d9){
if(!_7d9){
_7d9=this;
}
return dojo.widget.attachTemplateNodes(_7d8,_7d9,dojo.widget.getDojoEventsFromStr(this.templateString));
},fillInTemplate:function(){
},destroyRendering:function(){
try{
var _7da=this.domNode.parentNode.removeChild(this.domNode);
delete _7da;
}
catch(e){
}
},cleanUp:function(){
},getContainerHeight:function(){
return dojo.html.getInnerHeight(this.domNode.parentNode);
},getContainerWidth:function(){
return dojo.html.getInnerWidth(this.domNode.parentNode);
},createNodesFromText:function(){
dj_unimplemented("dojo.widget.DomWidget.createNodesFromText");
}});
dojo.widget.DomWidget.templates={};
dojo.provide("dojo.widget.HtmlWidget");
dojo.require("dojo.widget.DomWidget");
dojo.require("dojo.html");
dojo.require("dojo.string");
dojo.widget.HtmlWidget=function(args){
dojo.widget.DomWidget.call(this);
};
dojo.inherits(dojo.widget.HtmlWidget,dojo.widget.DomWidget);
dojo.lang.extend(dojo.widget.HtmlWidget,{widgetType:"HtmlWidget",templateCssPath:null,templatePath:null,allowResizeX:true,allowResizeY:true,resizeGhost:null,initialResizeCoords:null,toggle:"plain",toggleDuration:150,initialize:function(args,frag){
},postMixInProperties:function(args,frag){
dojo.lang.mixin(this,dojo.widget.HtmlWidget.Toggle[dojo.string.capitalize(this.toggle)]||dojo.widget.HtmlWidget.Toggle.Plain);
},getContainerHeight:function(){
dj_unimplemented("dojo.widget.HtmlWidget.getContainerHeight");
},getContainerWidth:function(){
return this.parent.domNode.offsetWidth;
},setNativeHeight:function(_7e0){
var ch=this.getContainerHeight();
},startResize:function(_7e2){
_7e2.offsetLeft=dojo.html.totalOffsetLeft(this.domNode);
_7e2.offsetTop=dojo.html.totalOffsetTop(this.domNode);
_7e2.innerWidth=dojo.html.getInnerWidth(this.domNode);
_7e2.innerHeight=dojo.html.getInnerHeight(this.domNode);
if(!this.resizeGhost){
this.resizeGhost=document.createElement("div");
var rg=this.resizeGhost;
rg.style.position="absolute";
rg.style.backgroundColor="white";
rg.style.border="1px solid black";
dojo.html.setOpacity(rg,0.3);
dojo.html.body().appendChild(rg);
}
with(this.resizeGhost.style){
left=_7e2.offsetLeft+"px";
top=_7e2.offsetTop+"px";
}
this.initialResizeCoords=_7e2;
this.resizeGhost.style.display="";
this.updateResize(_7e2,true);
},updateResize:function(_7e4,_7e5){
var dx=_7e4.x-this.initialResizeCoords.x;
var dy=_7e4.y-this.initialResizeCoords.y;
with(this.resizeGhost.style){
if((this.allowResizeX)||(_7e5)){
width=this.initialResizeCoords.innerWidth+dx+"px";
}
if((this.allowResizeY)||(_7e5)){
height=this.initialResizeCoords.innerHeight+dy+"px";
}
}
},endResize:function(_7e8){
var dx=_7e8.x-this.initialResizeCoords.x;
var dy=_7e8.y-this.initialResizeCoords.y;
with(this.domNode.style){
if(this.allowResizeX){
width=this.initialResizeCoords.innerWidth+dx+"px";
}
if(this.allowResizeY){
height=this.initialResizeCoords.innerHeight+dy+"px";
}
}
this.resizeGhost.style.display="none";
},createNodesFromText:function(txt,wrap){
return dojo.html.createNodesFromText(txt,wrap);
},_old_buildFromTemplate:dojo.widget.DomWidget.prototype.buildFromTemplate,buildFromTemplate:function(args,frag){
if(dojo.widget.DomWidget.templates[this.widgetType]){
var ot=dojo.widget.DomWidget.templates[this.widgetType];
dojo.widget.DomWidget.templates[this.widgetType]={};
}
dojo.widget.buildFromTemplate(this,args["templatePath"],args["templateCssPath"]);
this._old_buildFromTemplate(args,frag);
dojo.widget.DomWidget.templates[this.widgetType]=ot;
},destroyRendering:function(_7f0){
try{
var _7f1=this.domNode.parentNode.removeChild(this.domNode);
if(!_7f0){
dojo.event.browser.clean(_7f1);
}
delete _7f1;
}
catch(e){
}
},isVisible:function(){
return dojo.html.isVisible(this.domNode);
},doToggle:function(){
this.isVisible()?this.hide():this.show();
},show:function(){
this.showMe();
},onShow:function(){
},hide:function(){
this.hideMe();
},onHide:function(){
}});
dojo.widget.HtmlWidget.Toggle={};
dojo.widget.HtmlWidget.Toggle.Plain={showMe:function(){
dojo.html.show(this.domNode);
if(dojo.lang.isFunction(this.onShow)){
this.onShow();
}
},hideMe:function(){
dojo.html.hide(this.domNode);
if(dojo.lang.isFunction(this.onHide)){
this.onHide();
}
}};
dojo.widget.HtmlWidget.Toggle.Fade={showMe:function(){
dojo.fx.html.fadeShow(this.domNode,this.toggleDuration,dojo.lang.hitch(this,this.onShow));
},hideMe:function(){
dojo.fx.html.fadeHide(this.domNode,this.toggleDuration,dojo.lang.hitch(this,this.onHide));
}};
dojo.widget.HtmlWidget.Toggle.Wipe={showMe:function(){
dojo.fx.html.wipeIn(this.domNode,this.toggleDuration,dojo.lang.hitch(this,this.onShow));
},hideMe:function(){
dojo.fx.html.wipeOut(this.domNode,this.toggleDuration,dojo.lang.hitch(this,this.onHide));
}};
dojo.widget.HtmlWidget.Toggle.Explode={showMe:function(){
dojo.fx.html.explode(this.explodeSrc,this.domNode,this.toggleDuration,dojo.lang.hitch(this,this.onShow));
},hideMe:function(){
dojo.fx.html.implode(this.domNode,this.explodeSrc,this.toggleDuration,dojo.lang.hitch(this,this.onHide));
}};
dojo.hostenv.conditionalLoadModule({common:["dojo.xml.Parse","dojo.widget.Widget","dojo.widget.Parse","dojo.widget.Manager"],browser:["dojo.widget.DomWidget","dojo.widget.HtmlWidget"],svg:["dojo.widget.SvgWidget"]});
dojo.hostenv.moduleLoaded("dojo.widget.*");
dojo.provide("dojo.math.points");
dojo.require("dojo.math");
dojo.math.points={translate:function(a,b){
if(a.length!=b.length){
dojo.raise("dojo.math.translate: points not same size (a:["+a+"], b:["+b+"])");
}
var c=new Array(a.length);
for(var i=0;i<a.length;i++){
c[i]=a[i]+b[i];
}
return c;
},midpoint:function(a,b){
if(a.length!=b.length){
dojo.raise("dojo.math.midpoint: points not same size (a:["+a+"], b:["+b+"])");
}
var c=new Array(a.length);
for(var i=0;i<a.length;i++){
c[i]=(a[i]+b[i])/2;
}
return c;
},invert:function(a){
var b=new Array(a.length);
for(var i=0;i<a.length;i++){
b[i]=-a[i];
}
return b;
},distance:function(a,b){
return Math.sqrt(Math.pow(b[0]-a[0],2)+Math.pow(b[1]-a[1],2));
}};
dojo.hostenv.conditionalLoadModule({common:[["dojo.math",false,false],["dojo.math.curves",false,false],["dojo.math.points",false,false]]});
dojo.hostenv.moduleLoaded("dojo.math.*");

