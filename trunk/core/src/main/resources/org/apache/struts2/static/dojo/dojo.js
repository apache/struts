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
dojo.version={major:0,minor:2,patch:2,flag:"",revision:Number("$Rev: 2836 $".match(/[0-9]+/)[0]),toString:function(){
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
uri+="?"+String(djConfig.cacheBust).replace(/\W+/g,"");
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
if(this.loadedUris[uri]){
return;
}
var _38=this.getText(uri,null,true);
if(_38==null){
return 0;
}
this.loadedUris[uri]=true;
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
if(!_44){
return;
}
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
var lmn=(new String(_52)).toLowerCase();
if(this.loaded_modules_[lmn]){
return this.loaded_modules_[lmn];
}
var _55=dojo.evalObjPath(_52);
if((_52)&&(typeof _55!="undefined")&&(_55)){
this.loaded_modules_[lmn]=_55;
return _55;
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
var _56=document.location.toString();
var _57=_56.split("?",2);
if(_57.length>1){
var _58=_57[1];
var _59=_58.split("&");
for(var x in _59){
var sp=_59[x].split("=");
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
var _5d=document.getElementsByTagName("script");
var _5e=/(__package__|dojo)\.js([\?\.]|$)/i;
for(var i=0;i<_5d.length;i++){
var src=_5d[i].getAttribute("src");
if(!src){
continue;
}
var m=src.match(_5e);
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
var _68=dua.indexOf("Gecko");
drh.mozilla=drh.moz=(_68>=0)&&(!drh.khtml);
if(drh.mozilla){
drh.geckoVersion=dua.substring(_68+6,_68+14);
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
var _69=navigator.mimeTypes["image/svg+xml"]||navigator.mimeTypes["image/svg"]||navigator.mimeTypes["image/svg-xml"];
if(_69){
dr.svg.adobe=_69&&_69.enabledPlugin&&_69.enabledPlugin.description&&(_69.enabledPlugin.description.indexOf("Adobe")>-1);
if(dr.svg.adobe){
dr.svg.capable=t;
dr.svg.support.plugin=t;
}
}
}else{
if(drh.ie&&dr.os.win){
var _69=f;
try{
var _6a=new ActiveXObject("Adobe.SVGCtl");
_69=t;
}
catch(e){
}
if(_69){
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
var _6b=null;
var _6c=null;
try{
_6b=new XMLHttpRequest();
}
catch(e){
}
if(!_6b){
for(var i=0;i<3;++i){
var _6e=DJ_XMLHTTP_PROGIDS[i];
try{
_6b=new ActiveXObject(_6e);
}
catch(e){
_6c=e;
}
if(_6b){
DJ_XMLHTTP_PROGIDS=[_6e];
break;
}
}
}
if(!_6b){
return dojo.raise("XMLHTTP not available",_6c);
}
return _6b;
};
dojo.hostenv.getText=function(uri,_70,_71){
var _72=this.getXmlhttpObject();
if(_70){
_72.onreadystatechange=function(){
if((4==_72.readyState)&&(_72["status"])){
if(_72.status==200){
_70(_72.responseText);
}
}
};
}
_72.open("GET",uri,_70?true:false);
_72.send(null);
if(_70){
return null;
}
return _72.responseText;
};
dojo.hostenv.defaultDebugContainerId="dojoDebug";
dojo.hostenv._println_buffer=[];
dojo.hostenv._println_safe=false;
dojo.hostenv.println=function(_73){
if(!dojo.hostenv._println_safe){
dojo.hostenv._println_buffer.push(_73);
}else{
try{
var _74=document.getElementById(djConfig.debugContainerId?djConfig.debugContainerId:dojo.hostenv.defaultDebugContainerId);
if(!_74){
_74=document.getElementsByTagName("body")[0]||document.body;
}
var div=document.createElement("div");
div.appendChild(document.createTextNode(_73));
_74.appendChild(div);
}
catch(e){
try{
document.write("<div>"+_73+"</div>");
}
catch(e2){
window.status=_73;
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
function dj_addNodeEvtHdlr(_76,_77,fp,_79){
var _7a=_76["on"+_77]||function(){
};
_76["on"+_77]=function(){
fp.apply(_76,arguments);
_7a.apply(_76,arguments);
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
var _7b=[];
if(djConfig.searchIds&&djConfig.searchIds.length>0){
_7b=_7b.concat(djConfig.searchIds);
}
if(dojo.hostenv.searchIds&&dojo.hostenv.searchIds.length>0){
_7b=_7b.concat(dojo.hostenv.searchIds);
}
if((djConfig.parseWidgets)||(_7b.length>0)){
if(dojo.evalObjPath("dojo.widget.Parse")){
try{
var _7c=new dojo.xml.Parse();
if(_7b.length>0){
for(var x=0;x<_7b.length;x++){
var _7e=document.getElementById(_7b[x]);
if(!_7e){
continue;
}
var _7f=_7c.parseElement(_7e,null,true);
dojo.widget.getParser().createComponents(_7f);
}
}else{
if(djConfig.parseWidgets){
var _7f=_7c.parseElement(document.getElementsByTagName("body")[0]||document.body,null,true);
dojo.widget.getParser().createComponents(_7f);
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
dojo.hostenv.conditionalLoadModule=function(_85){
var _86=_85["common"]||[];
var _87=(_85[dojo.hostenv.name_])?_86.concat(_85[dojo.hostenv.name_]||[]):_86.concat(_85["default"]||[]);
for(var x=0;x<_87.length;x++){
var _89=_87[x];
if(_89.constructor==Array){
dojo.hostenv.loadModule.apply(dojo.hostenv,_89);
}else{
dojo.hostenv.loadModule(_89);
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
var _8a=[];
for(var i=1;i<arguments.length;i++){
_8a.push(arguments[i]);
}
dojo.require.apply(dojo,_8a);
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
dojo.setModulePrefix=function(_8c,_8d){
return dojo.hostenv.setModulePrefix(_8c,_8d);
};
dojo.profile={start:function(){
},end:function(){
},dump:function(){
}};
dojo.exists=function(obj,_8f){
var p=_8f.split(".");
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
dojo.lang.mixin=function(obj,_93){
var _94={};
for(var x in _93){
if(typeof _94[x]=="undefined"||_94[x]!=_93[x]){
obj[x]=_93[x];
}
}
if(dojo.render.html.ie&&dojo.lang.isFunction(_93["toString"])&&_93["toString"]!=obj["toString"]){
obj.toString=_93.toString;
}
return obj;
};
dojo.lang.extend=function(_96,_97){
this.mixin(_96.prototype,_97);
};
dojo.lang.extendPrototype=function(obj,_99){
this.extend(obj.constructor,_99);
};
dojo.lang.anonCtr=0;
dojo.lang.anon={};
dojo.lang.nameAnonFunc=function(_9a,_9b){
var nso=(_9b||dojo.lang.anon);
if((dj_global["djConfig"])&&(djConfig["slowAnonFuncLookups"]==true)){
for(var x in nso){
if(nso[x]===_9a){
return x;
}
}
}
var ret="__"+dojo.lang.anonCtr++;
while(typeof nso[ret]!="undefined"){
ret="__"+dojo.lang.anonCtr++;
}
nso[ret]=_9a;
return ret;
};
dojo.lang.hitch=function(_9f,_a0){
if(dojo.lang.isString(_a0)){
var fcn=_9f[_a0];
}else{
var fcn=_a0;
}
return function(){
return fcn.apply(_9f,arguments);
};
};
dojo.lang.forward=function(_a2){
return function(){
return this[_a2].apply(this,arguments);
};
};
dojo.lang.curry=function(ns,_a4){
var _a5=[];
ns=ns||dj_global;
if(dojo.lang.isString(_a4)){
_a4=ns[_a4];
}
for(var x=2;x<arguments.length;x++){
_a5.push(arguments[x]);
}
var _a7=_a4.length-_a5.length;
function gather(_a8,_a9,_aa){
var _ab=_aa;
var _ac=_a9.slice(0);
for(var x=0;x<_a8.length;x++){
_ac.push(_a8[x]);
}
_aa=_aa-_a8.length;
if(_aa<=0){
var res=_a4.apply(ns,_ac);
_aa=_ab;
return res;
}else{
return function(){
return gather(arguments,_ac,_aa);
};
}
}
return gather([],_a5,_a7);
};
dojo.lang.curryArguments=function(ns,_b0,_b1,_b2){
var _b3=[];
var x=_b2||0;
for(x=_b2;x<_b1.length;x++){
_b3.push(_b1[x]);
}
return dojo.lang.curry.apply(dojo.lang,[ns,_b0].concat(_b3));
};
dojo.lang.setTimeout=function(_b5,_b6){
var _b7=window,argsStart=2;
if(!dojo.lang.isFunction(_b5)){
_b7=_b5;
_b5=_b6;
_b6=arguments[2];
argsStart++;
}
if(dojo.lang.isString(_b5)){
_b5=_b7[_b5];
}
var _b8=[];
for(var i=argsStart;i<arguments.length;i++){
_b8.push(arguments[i]);
}
return setTimeout(function(){
_b5.apply(_b7,_b8);
},_b6);
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
if(typeof wh!="undefined"&&wh&&dojo.lang.isNumber(wh.length)&&isFinite(wh.length)){
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
for(var _c4 in dojo.lang.whatAmI.custom){
if(dojo.lang.whatAmI.custom[_c4](wh)){
return _c4;
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
dojo.lang.find=function(arr,val,_c7){
if(!dojo.lang.isArrayLike(arr)&&dojo.lang.isArrayLike(val)){
var a=arr;
arr=val;
val=a;
}
var _c9=dojo.lang.isString(arr);
if(_c9){
arr=arr.split("");
}
if(_c7){
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
dojo.lang.findLast=function(arr,val,_cd){
if(!dojo.lang.isArrayLike(arr)&&dojo.lang.isArrayLike(val)){
var a=arr;
arr=val;
val=a;
}
var _cf=dojo.lang.isString(arr);
if(_cf){
arr=arr.split("");
}
if(_cd){
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
dojo.lang.getNameInObj=function(ns,_d4){
if(!ns){
ns=dj_global;
}
for(var x in ns){
if(ns[x]===_d4){
return new String(x);
}
}
return null;
};
dojo.lang.has=function(obj,_d7){
return (typeof obj[_d7]!=="undefined");
};
dojo.lang.isEmpty=function(obj){
if(dojo.lang.isObject(obj)){
var tmp={};
var _da=0;
for(var x in obj){
if(obj[x]&&(!tmp[x])){
_da++;
break;
}
}
return (_da==0);
}else{
if(dojo.lang.isArrayLike(obj)||dojo.lang.isString(obj)){
return obj.length==0;
}
}
};
dojo.lang.forEach=function(arr,_dd,_de){
var _df=dojo.lang.isString(arr);
if(_df){
arr=arr.split("");
}
var il=arr.length;
for(var i=0;i<((_de)?il:arr.length);i++){
if(_dd(arr[i],i,arr)=="break"){
break;
}
}
};
dojo.lang.map=function(arr,obj,_e4){
var _e5=dojo.lang.isString(arr);
if(_e5){
arr=arr.split("");
}
if(dojo.lang.isFunction(obj)&&(!_e4)){
_e4=obj;
obj=dj_global;
}else{
if(dojo.lang.isFunction(obj)&&_e4){
var _e6=obj;
obj=_e4;
_e4=_e6;
}
}
if(Array.map){
var _e7=Array.map(arr,_e4,obj);
}else{
var _e7=[];
for(var i=0;i<arr.length;++i){
_e7.push(_e4.call(obj,arr[i]));
}
}
if(_e5){
return _e7.join("");
}else{
return _e7;
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
dojo.lang.delayThese=function(_eb,cb,_ed,_ee){
if(!_eb.length){
if(typeof _ee=="function"){
_ee();
}
return;
}
if((typeof _ed=="undefined")&&(typeof cb=="number")){
_ed=cb;
cb=function(){
};
}else{
if(!cb){
cb=function(){
};
if(!_ed){
_ed=0;
}
}
}
setTimeout(function(){
(_eb.shift())();
cb();
dojo.lang.delayThese(_eb,cb,_ed,_ee);
},_ed);
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
dojo.lang.every=function(arr,_f2,_f3){
var _f4=dojo.lang.isString(arr);
if(_f4){
arr=arr.split("");
}
if(Array.every){
return Array.every(arr,_f2,_f3);
}else{
if(!_f3){
if(arguments.length>=3){
dojo.raise("thisObject doesn't exist!");
}
_f3=dj_global;
}
for(var i=0;i<arr.length;i++){
if(!_f2.call(_f3,arr[i],i,arr)){
return false;
}
}
return true;
}
};
dojo.lang.some=function(arr,_f7,_f8){
var _f9=dojo.lang.isString(arr);
if(_f9){
arr=arr.split("");
}
if(Array.some){
return Array.some(arr,_f7,_f8);
}else{
if(!_f8){
if(arguments.length>=3){
dojo.raise("thisObject doesn't exist!");
}
_f8=dj_global;
}
for(var i=0;i<arr.length;i++){
if(_f7.call(_f8,arr[i],i,arr)){
return true;
}
}
return false;
}
};
dojo.lang.filter=function(arr,_fc,_fd){
var _fe=dojo.lang.isString(arr);
if(_fe){
arr=arr.split("");
}
if(Array.filter){
var _ff=Array.filter(arr,_fc,_fd);
}else{
if(!_fd){
if(arguments.length>=3){
dojo.raise("thisObject doesn't exist!");
}
_fd=dj_global;
}
var _ff=[];
for(var i=0;i<arr.length;i++){
if(_fc.call(_fd,arr[i],i,arr)){
_ff.push(arr[i]);
}
}
}
if(_fe){
return _ff.join("");
}else{
return _ff;
}
};
dojo.AdapterRegistry=function(){
this.pairs=[];
};
dojo.lang.extend(dojo.AdapterRegistry,{register:function(name,_102,wrap,_104){
if(_104){
this.pairs.unshift([name,_102,wrap]);
}else{
this.pairs.push([name,_102,wrap]);
}
},match:function(){
for(var i=0;i<this.pairs.length;i++){
var pair=this.pairs[i];
if(pair[1].apply(this,arguments)){
return pair[2].apply(this,arguments);
}
}
throw new Error("No match found");
},unregister:function(name){
for(var i=0;i<this.pairs.length;i++){
var pair=this.pairs[i];
if(pair[0]==name){
this.pairs.splice(i,1);
return true;
}
}
return false;
}});
dojo.lang.reprRegistry=new dojo.AdapterRegistry();
dojo.lang.registerRepr=function(name,_10b,wrap,_10d){
dojo.lang.reprRegistry.register(name,_10b,wrap,_10d);
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
dojo.lang.firstValued=function(){
for(var i=0;i<arguments.length;i++){
if(typeof arguments[i]!="undefined"){
return arguments[i];
}
}
return undefined;
};
dojo.lang.toArray=function(_119,_11a){
var _11b=[];
for(var i=_11a||0;i<_119.length;i++){
_11b.push(_119[i]);
}
return _11b;
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
dojo.string.paramString=function(str,_122,_123){
for(var name in _122){
var re=new RegExp("\\%\\{"+name+"\\}","g");
str=str.replace(re,_122[name]);
}
if(_123){
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
var _127=str.split(" ");
var _128="";
var len=_127.length;
for(var i=0;i<len;i++){
var word=_127[i];
word=word.charAt(0).toUpperCase()+word.substring(1,word.length);
_128+=word;
if(i<len-1){
_128+=" ";
}
}
return new String(_128);
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
var _12f=escape(str);
var _130,re=/%u([0-9A-F]{4})/i;
while((_130=_12f.match(re))){
var num=Number("0x"+_130[1]);
var _132=escape("&#"+num+";");
ret+=_12f.substring(0,_130.index)+_132;
_12f=_12f.substring(_130.index+_130[0].length);
}
ret+=_12f.replace(/\+/g,"%2B");
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
var args=[];
for(var i=1;i<arguments.length;i++){
args.push(arguments[i]);
}
switch(type.toLowerCase()){
case "xml":
case "html":
case "xhtml":
return dojo.string.escapeXml.apply(this,args);
case "sql":
return dojo.string.escapeSql.apply(this,args);
case "regexp":
case "regex":
return dojo.string.escapeRegExp.apply(this,args);
case "javascript":
case "jscript":
case "js":
return dojo.string.escapeJavaScript.apply(this,args);
case "ascii":
return dojo.string.encodeAscii.apply(this,args);
default:
return str;
}
};
dojo.string.escapeXml=function(str,_13a){
str=str.replace(/&/gm,"&amp;").replace(/</gm,"&lt;").replace(/>/gm,"&gt;").replace(/"/gm,"&quot;");
if(!_13a){
str=str.replace(/'/gm,"&#39;");
}
return str;
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
dojo.string.repeat=function(str,_13f,_140){
var out="";
for(var i=0;i<_13f;i++){
out+=str;
if(_140&&i<_13f-1){
out+=_140;
}
}
return out;
};
dojo.string.endsWith=function(str,end,_145){
if(_145){
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
dojo.string.startsWith=function(str,_149,_14a){
if(_14a){
str=str.toLowerCase();
_149=_149.toLowerCase();
}
return str.indexOf(_149)==0;
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
dojo.string.normalizeNewlines=function(text,_15b){
if(_15b=="\n"){
text=text.replace(/\r\n/g,"\n");
text=text.replace(/\r/g,"\n");
}else{
if(_15b=="\r"){
text=text.replace(/\r\n/g,"\r");
text=text.replace(/\n/g,"\r");
}else{
text=text.replace(/([^\r])\n/g,"$1\r\n");
text=text.replace(/\r([^\n])/g,"\r\n$1");
}
}
return text;
};
dojo.string.splitEscaped=function(str,_15d){
var _15e=[];
for(var i=0,prevcomma=0;i<str.length;i++){
if(str.charAt(i)=="\\"){
i++;
continue;
}
if(str.charAt(i)==_15d){
_15e.push(str.substring(prevcomma,i));
prevcomma=i+1;
}
}
_15e.push(str.substr(prevcomma));
return _15e;
};
dojo.string.addToPrototype=function(){
for(var _160 in dojo.string){
if(dojo.lang.isFunction(dojo.string[_160])){
var func=(function(){
var meth=_160;
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
String.prototype[_160]=func;
}
}
}
};
dojo.provide("dojo.io.IO");
dojo.require("dojo.string");
dojo.io.transports=[];
dojo.io.hdlrFuncNames=["load","error"];
dojo.io.Request=function(url,_167,_168,_169){
if((arguments.length==1)&&(arguments[0].constructor==Object)){
this.fromKwArgs(arguments[0]);
}else{
this.url=url;
if(_167){
this.mimetype=_167;
}
if(_168){
this.transport=_168;
}
if(arguments.length>=4){
this.changeUrl=_169;
}
}
};
dojo.lang.extend(dojo.io.Request,{url:"",mimetype:"text/plain",method:"GET",content:undefined,transport:undefined,changeUrl:undefined,formNode:undefined,sync:false,bindSuccess:false,useCache:false,preventCache:false,load:function(type,data,evt){
},error:function(type,_16e){
},handle:function(){
},abort:function(){
},fromKwArgs:function(_16f){
if(_16f["url"]){
_16f.url=_16f.url.toString();
}
if(!_16f["method"]&&_16f["formNode"]&&_16f["formNode"].method){
_16f.method=_16f["formNode"].method;
}
if(!_16f["handle"]&&_16f["handler"]){
_16f.handle=_16f.handler;
}
if(!_16f["load"]&&_16f["loaded"]){
_16f.load=_16f.loaded;
}
if(!_16f["changeUrl"]&&_16f["changeURL"]){
_16f.changeUrl=_16f.changeURL;
}
_16f.encoding=dojo.lang.firstValued(_16f["encoding"],djConfig["bindEncoding"],"");
_16f.sendTransport=dojo.lang.firstValued(_16f["sendTransport"],djConfig["ioSendTransport"],true);
var _170=dojo.lang.isFunction;
for(var x=0;x<dojo.io.hdlrFuncNames.length;x++){
var fn=dojo.io.hdlrFuncNames[x];
if(_170(_16f[fn])){
continue;
}
if(_170(_16f["handle"])){
_16f[fn]=_16f.handle;
}
}
dojo.lang.mixin(this,_16f);
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
dojo.io.bind=function(_177){
if(!(_177 instanceof dojo.io.Request)){
try{
_177=new dojo.io.Request(_177);
}
catch(e){
dojo.debug(e);
}
}
var _178="";
if(_177["transport"]){
_178=_177["transport"];
if(!this[_178]){
return _177;
}
}else{
for(var x=0;x<dojo.io.transports.length;x++){
var tmp=dojo.io.transports[x];
if((this[tmp])&&(this[tmp].canHandle(_177))){
_178=tmp;
}
}
if(_178==""){
return _177;
}
}
this[_178].bind(_177);
_177.bindSuccess=true;
return _177;
};
dojo.io.queueBind=function(_17b){
if(!(_17b instanceof dojo.io.Request)){
try{
_17b=new dojo.io.Request(_17b);
}
catch(e){
dojo.debug(e);
}
}
var _17c=_17b.load;
_17b.load=function(){
dojo.io._queueBindInFlight=false;
var ret=_17c.apply(this,arguments);
dojo.io._dispatchNextQueueBind();
return ret;
};
var _17e=_17b.error;
_17b.error=function(){
dojo.io._queueBindInFlight=false;
var ret=_17e.apply(this,arguments);
dojo.io._dispatchNextQueueBind();
return ret;
};
dojo.io._bindQueue.push(_17b);
dojo.io._dispatchNextQueueBind();
return _17b;
};
dojo.io._dispatchNextQueueBind=function(){
if(!dojo.io._queueBindInFlight){
dojo.io._queueBindInFlight=true;
dojo.io.bind(dojo.io._bindQueue.shift());
}
};
dojo.io._bindQueue=[];
dojo.io._queueBindInFlight=false;
dojo.io.argsFromMap=function(map,_181){
var _182=new Object();
var _183="";
var enc=/utf/i.test(_181||"")?encodeURIComponent:dojo.string.encodeAscii;
for(var x in map){
if(!_182[x]){
_183+=enc(x)+"="+enc(map[x])+"&";
}
}
return _183;
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
var _188=node.tagName;
if(_188.substr(0,5).toLowerCase()!="dojo:"){
if(_188.substr(0,4).toLowerCase()=="dojo"){
return "dojo:"+_188.substring(4).toLowerCase();
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
var _18a=node.className||node.getAttribute("class");
if((_18a)&&(_18a.indexOf)&&(_18a.indexOf("dojo-")!=-1)){
var _18b=_18a.split(" ");
for(var x=0;x<_18b.length;x++){
if((_18b[x].length>5)&&(_18b[x].indexOf("dojo-")>=0)){
return "dojo:"+_18b[x].substr(5).toLowerCase();
}
}
}
}
}
return _188.toLowerCase();
};
dojo.dom.getUniqueId=function(){
do{
var id="dj_unique_"+(++arguments.callee._idIncrement);
}while(document.getElementById(id));
return id;
};
dojo.dom.getUniqueId._idIncrement=0;
dojo.dom.firstElement=dojo.dom.getFirstChildElement=function(_18e,_18f){
var node=_18e.firstChild;
while(node&&node.nodeType!=dojo.dom.ELEMENT_NODE){
node=node.nextSibling;
}
if(_18f&&node&&node.tagName&&node.tagName.toLowerCase()!=_18f.toLowerCase()){
node=dojo.dom.nextElement(node,_18f);
}
return node;
};
dojo.dom.lastElement=dojo.dom.getLastChildElement=function(_191,_192){
var node=_191.lastChild;
while(node&&node.nodeType!=dojo.dom.ELEMENT_NODE){
node=node.previousSibling;
}
if(_192&&node&&node.tagName&&node.tagName.toLowerCase()!=_192.toLowerCase()){
node=dojo.dom.prevElement(node,_192);
}
return node;
};
dojo.dom.nextElement=dojo.dom.getNextSiblingElement=function(node,_195){
if(!node){
return null;
}
do{
node=node.nextSibling;
}while(node&&node.nodeType!=dojo.dom.ELEMENT_NODE);
if(node&&_195&&_195.toLowerCase()!=node.tagName.toLowerCase()){
return dojo.dom.nextElement(node,_195);
}
return node;
};
dojo.dom.prevElement=dojo.dom.getPreviousSiblingElement=function(node,_197){
if(!node){
return null;
}
if(_197){
_197=_197.toLowerCase();
}
do{
node=node.previousSibling;
}while(node&&node.nodeType!=dojo.dom.ELEMENT_NODE);
if(node&&_197&&_197.toLowerCase()!=node.tagName.toLowerCase()){
return dojo.dom.prevElement(node,_197);
}
return node;
};
dojo.dom.moveChildren=function(_198,_199,trim){
var _19b=0;
if(trim){
while(_198.hasChildNodes()&&_198.firstChild.nodeType==dojo.dom.TEXT_NODE){
_198.removeChild(_198.firstChild);
}
while(_198.hasChildNodes()&&_198.lastChild.nodeType==dojo.dom.TEXT_NODE){
_198.removeChild(_198.lastChild);
}
}
while(_198.hasChildNodes()){
_199.appendChild(_198.firstChild);
_19b++;
}
return _19b;
};
dojo.dom.copyChildren=function(_19c,_19d,trim){
var _19f=_19c.cloneNode(true);
return this.moveChildren(_19f,_19d,trim);
};
dojo.dom.removeChildren=function(node){
var _1a1=node.childNodes.length;
while(node.hasChildNodes()){
node.removeChild(node.firstChild);
}
return _1a1;
};
dojo.dom.replaceChildren=function(node,_1a3){
dojo.dom.removeChildren(node);
node.appendChild(_1a3);
};
dojo.dom.removeNode=function(node){
if(node&&node.parentNode){
return node.parentNode.removeChild(node);
}
};
dojo.dom.getAncestors=function(node,_1a6,_1a7){
var _1a8=[];
var _1a9=dojo.lang.isFunction(_1a6);
while(node){
if(!_1a9||_1a6(node)){
_1a8.push(node);
}
if(_1a7&&_1a8.length>0){
return _1a8[0];
}
node=node.parentNode;
}
if(_1a7){
return null;
}
return _1a8;
};
dojo.dom.getAncestorsByTag=function(node,tag,_1ac){
tag=tag.toLowerCase();
return dojo.dom.getAncestors(node,function(el){
return ((el.tagName)&&(el.tagName.toLowerCase()==tag));
},_1ac);
};
dojo.dom.getFirstAncestorByTag=function(node,tag){
return dojo.dom.getAncestorsByTag(node,tag,true);
};
dojo.dom.isDescendantOf=function(node,_1b1,_1b2){
if(_1b2&&node){
node=node.parentNode;
}
while(node){
if(node==_1b1){
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
dojo.dom.createDocumentFromText=function(str,_1b5){
if(!_1b5){
_1b5="text/xml";
}
if(typeof DOMParser!="undefined"){
var _1b6=new DOMParser();
return _1b6.parseFromString(str,_1b5);
}else{
if(typeof ActiveXObject!="undefined"){
var _1b7=new ActiveXObject("Microsoft.XMLDOM");
if(_1b7){
_1b7.async=false;
_1b7.loadXML(str);
return _1b7;
}else{
dojo.debug("toXml didn't work?");
}
}else{
if(document.createElement){
var tmp=document.createElement("xml");
tmp.innerHTML=str;
if(document.implementation&&document.implementation.createDocument){
var _1b9=document.implementation.createDocument("foo","",null);
for(var i=0;i<tmp.childNodes.length;i++){
_1b9.importNode(tmp.childNodes.item(i),true);
}
return _1b9;
}
return tmp.document&&tmp.document.firstChild?tmp.document.firstChild:tmp;
}
}
}
return null;
};
dojo.dom.prependChild=function(node,_1bc){
if(_1bc.firstChild){
_1bc.insertBefore(node,_1bc.firstChild);
}else{
_1bc.appendChild(node);
}
return true;
};
dojo.dom.insertBefore=function(node,ref,_1bf){
if(_1bf!=true&&(node===ref||node.nextSibling===ref)){
return false;
}
var _1c0=ref.parentNode;
_1c0.insertBefore(node,ref);
return true;
};
dojo.dom.insertAfter=function(node,ref,_1c3){
var pn=ref.parentNode;
if(ref==pn.lastChild){
if((_1c3!=true)&&(node===ref)){
return false;
}
pn.appendChild(node);
}else{
return this.insertBefore(node,ref.nextSibling,_1c3);
}
return true;
};
dojo.dom.insertAtPosition=function(node,ref,_1c7){
if((!node)||(!ref)||(!_1c7)){
return false;
}
switch(_1c7.toLowerCase()){
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
dojo.dom.insertAtIndex=function(node,_1c9,_1ca){
var _1cb=_1c9.childNodes;
if(!_1cb.length){
_1c9.appendChild(node);
return true;
}
var _1cc=null;
for(var i=0;i<_1cb.length;i++){
var _1ce=_1cb.item(i)["getAttribute"]?parseInt(_1cb.item(i).getAttribute("dojoinsertionindex")):-1;
if(_1ce<_1ca){
_1cc=_1cb.item(i);
}
}
if(_1cc){
return dojo.dom.insertAfter(node,_1cc);
}else{
return dojo.dom.insertBefore(node,_1cb.item(0));
}
};
dojo.dom.textContent=function(node,text){
if(text){
dojo.dom.replaceChildren(node,document.createTextNode(text));
return text;
}else{
var _1d1="";
if(node==null){
return _1d1;
}
for(var i=0;i<node.childNodes.length;i++){
switch(node.childNodes[i].nodeType){
case 1:
case 5:
_1d1+=dojo.dom.textContent(node.childNodes[i]);
break;
case 3:
case 2:
case 4:
_1d1+=node.childNodes[i].nodeValue;
break;
default:
break;
}
}
return _1d1;
}
};
dojo.dom.collectionToArray=function(_1d3){
dojo.deprecated("dojo.dom.collectionToArray","use dojo.lang.toArray instead");
return dojo.lang.toArray(_1d3);
};
dojo.dom.hasParent=function(node){
if(!node||!node.parentNode||(node.parentNode&&!node.parentNode.tagName)){
return false;
}
return true;
};
dojo.dom.isTag=function(node){
if(node&&node.tagName){
var arr=dojo.lang.toArray(arguments,1);
return arr[dojo.lang.find(node.tagName,arr)]||"";
}
return "";
};
dojo.provide("dojo.io.BrowserIO");
dojo.require("dojo.io");
dojo.require("dojo.lang");
dojo.require("dojo.dom");
try{
if((!djConfig["preventBackButtonFix"])&&(!dojo.hostenv.post_load_)){
document.write("<iframe style='border: 0px; width: 1px; height: 1px; position: absolute; bottom: 0px; right: 0px; visibility: visible;' name='djhistory' id='djhistory' src='"+(dojo.hostenv.getBaseScriptUri()+"iframe_history.html")+"'></iframe>");
}
}
catch(e){
}
dojo.io.checkChildrenForFile=function(node){
var _1d8=false;
var _1d9=node.getElementsByTagName("input");
dojo.lang.forEach(_1d9,function(_1da){
if(_1d8){
return;
}
if(_1da.getAttribute("type")=="file"){
_1d8=true;
}
});
return _1d8;
};
dojo.io.formHasFile=function(_1db){
return dojo.io.checkChildrenForFile(_1db);
};
dojo.io.encodeForm=function(_1dc,_1dd){
if((!_1dc)||(!_1dc.tagName)||(!_1dc.tagName.toLowerCase()=="form")){
dojo.raise("Attempted to encode a non-form element.");
}
var enc=/utf/i.test(_1dd||"")?encodeURIComponent:dojo.string.encodeAscii;
var _1df=[];
for(var i=0;i<_1dc.elements.length;i++){
var elm=_1dc.elements[i];
if(elm.disabled||elm.tagName.toLowerCase()=="fieldset"||!elm.name){
continue;
}
var name=enc(elm.name);
var type=elm.type.toLowerCase();
if(type=="select-multiple"){
for(var j=0;j<elm.options.length;j++){
if(elm.options[j].selected){
_1df.push(name+"="+enc(elm.options[j].value));
}
}
}else{
if(dojo.lang.inArray(type,["radio","checkbox"])){
if(elm.checked){
_1df.push(name+"="+enc(elm.value));
}
}else{
if(!dojo.lang.inArray(type,["file","submit","reset","button"])){
_1df.push(name+"="+enc(elm.value));
}
}
}
}
var _1e5=_1dc.getElementsByTagName("input");
for(var i=0;i<_1e5.length;i++){
var _1e6=_1e5[i];
if(_1e6.type.toLowerCase()=="image"&&_1e6.form==_1dc){
var name=enc(_1e6.name);
_1df.push(name+"="+enc(_1e6.value));
_1df.push(name+".x=0");
_1df.push(name+".y=0");
}
}
return _1df.join("&")+"&";
};
dojo.io.setIFrameSrc=function(_1e7,src,_1e9){
try{
var r=dojo.render.html;
if(!_1e9){
if(r.safari){
_1e7.location=src;
}else{
frames[_1e7.name].location=src;
}
}else{
var idoc;
if(r.ie){
idoc=_1e7.contentWindow.document;
}else{
if(r.moz){
idoc=_1e7.contentWindow;
}else{
if(r.safari){
idoc=_1e7.document;
}
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
var _1ec=this;
this.initialHref=window.location.href;
this.initialHash=window.location.hash;
this.moveForward=false;
var _1ed={};
this.useCache=false;
this.preventCache=false;
this.historyStack=[];
this.forwardStack=[];
this.historyIframe=null;
this.bookmarkAnchor=null;
this.locationTimer=null;
function getCacheKey(url,_1ef,_1f0){
return url+"|"+_1ef+"|"+_1f0.toLowerCase();
}
function addToCache(url,_1f2,_1f3,http){
_1ed[getCacheKey(url,_1f2,_1f3)]=http;
}
function getFromCache(url,_1f6,_1f7){
return _1ed[getCacheKey(url,_1f6,_1f7)];
}
this.clearCache=function(){
_1ed={};
};
function doLoad(_1f8,http,url,_1fb,_1fc){
if((http.status==200)||(location.protocol=="file:"&&http.status==0)){
var ret;
if(_1f8.method.toLowerCase()=="head"){
var _1fe=http.getAllResponseHeaders();
ret={};
ret.toString=function(){
return _1fe;
};
var _1ff=_1fe.split(/[\r\n]+/g);
for(var i=0;i<_1ff.length;i++){
var pair=_1ff[i].match(/^([^:]+)\s*:\s*(.+)$/i);
if(pair){
ret[pair[1]]=pair[2];
}
}
}else{
if(_1f8.mimetype=="text/javascript"){
try{
ret=dj_eval(http.responseText);
}
catch(e){
dojo.debug(e);
dojo.debug(http.responseText);
ret=null;
}
}else{
if(_1f8.mimetype=="text/json"){
try{
ret=dj_eval("("+http.responseText+")");
}
catch(e){
dojo.debug(e);
dojo.debug(http.responseText);
ret=false;
}
}else{
if((_1f8.mimetype=="application/xml")||(_1f8.mimetype=="text/xml")){
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
if(_1fc){
addToCache(url,_1fb,_1f8.method,http);
}
_1f8[(typeof _1f8.load=="function")?"load":"handle"]("load",ret,http);
}else{
var _202=new dojo.io.Error("XMLHttpTransport Error: "+http.status+" "+http.statusText);
_1f8[(typeof _1f8.error=="function")?"error":"handle"]("error",_202,http);
}
}
function setHeaders(http,_204){
if(_204["headers"]){
for(var _205 in _204["headers"]){
if(_205.toLowerCase()=="content-type"&&!_204["contentType"]){
_204["contentType"]=_204["headers"][_205];
}else{
http.setRequestHeader(_205,_204["headers"][_205]);
}
}
}
}
this.addToHistory=function(args){
var _207=args["back"]||args["backButton"]||args["handle"];
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
var _20a=_207;
var lh=null;
var hsl=this.historyStack.length-1;
if(hsl>=0){
while(!this.historyStack[hsl]["urlHash"]){
hsl--;
}
lh=this.historyStack[hsl]["urlHash"];
}
if(lh){
_207=function(){
if(window.location.hash!=""){
setTimeout("window.location.href = '"+lh+"';",1);
}
_20a();
};
}
this.forwardStack=[];
var _20d=args["forward"]||args["forwardButton"];
var tfw=function(){
if(window.location.hash!=""){
window.location.href=hash;
}
if(_20d){
_20d();
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
this.historyStack.push({"url":url,"callback":_207,"kwArgs":args,"urlHash":hash});
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
this.iframeLoaded=function(evt,_211){
var isp=_211.href.split("?");
if(isp.length<2){
if(this.historyStack.length==1){
this.handleBackButton();
}
return;
}
var _213=isp[1];
if(this.moveForward){
this.moveForward=false;
return;
}
var last=this.historyStack.pop();
if(!last){
if(this.forwardStack.length>0){
var next=this.forwardStack[this.forwardStack.length-1];
if(_213==next.url.split("?")[1]){
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
var _21a=dojo.hostenv.getXmlhttpObject()?true:false;
this.canHandle=function(_21b){
return _21a&&dojo.lang.inArray((_21b["mimetype"]||"".toLowerCase()),["text/plain","text/html","application/xml","text/xml","text/javascript","text/json"])&&dojo.lang.inArray(_21b["method"].toLowerCase(),["post","get","head"])&&!(_21b["formNode"]&&dojo.io.formHasFile(_21b["formNode"]));
};
this.multipartBoundary="45309FFF-BD65-4d50-99C9-36986896A96F";
this.bind=function(_21c){
if(!_21c["url"]){
if(!_21c["formNode"]&&(_21c["backButton"]||_21c["back"]||_21c["changeUrl"]||_21c["watchForURL"])&&(!djConfig.preventBackButtonFix)){
this.addToHistory(_21c);
return true;
}
}
var url=_21c.url;
var _21e="";
if(_21c["formNode"]){
var ta=_21c.formNode.getAttribute("action");
if((ta)&&(!_21c["url"])){
url=ta;
}
var tp=_21c.formNode.getAttribute("method");
if((tp)&&(!_21c["method"])){
_21c.method=tp;
}
_21e+=dojo.io.encodeForm(_21c.formNode,_21c.encoding);
}
if(url.indexOf("#")>-1){
dojo.debug("Warning: dojo.io.bind: stripping hash values from url:",url);
url=url.split("#")[0];
}
if(_21c["file"]){
_21c.method="post";
}
if(!_21c["method"]){
_21c.method="get";
}
if(_21c.method.toLowerCase()=="get"){
_21c.multipart=false;
}else{
if(_21c["file"]){
_21c.multipart=true;
}else{
if(!_21c["multipart"]){
_21c.multipart=false;
}
}
}
if(_21c["backButton"]||_21c["back"]||_21c["changeUrl"]){
this.addToHistory(_21c);
}
var _221=_21c["content"]||{};
if(_21c.sendTransport){
_221["dojo.transport"]="xmlhttp";
}
do{
if(_21c.postContent){
_21e=_21c.postContent;
break;
}
if(_221){
_21e+=dojo.io.argsFromMap(_221,_21c.encoding);
}
if(_21c.method.toLowerCase()=="get"||!_21c.multipart){
break;
}
var t=[];
if(_21e.length){
var q=_21e.split("&");
for(var i=0;i<q.length;++i){
if(q[i].length){
var p=q[i].split("=");
t.push("--"+this.multipartBoundary,"Content-Disposition: form-data; name=\""+p[0]+"\"","",p[1]);
}
}
}
if(_21c.file){
if(dojo.lang.isArray(_21c.file)){
for(var i=0;i<_21c.file.length;++i){
var o=_21c.file[i];
t.push("--"+this.multipartBoundary,"Content-Disposition: form-data; name=\""+o.name+"\"; filename=\""+("fileName" in o?o.fileName:o.name)+"\"","Content-Type: "+("contentType" in o?o.contentType:"application/octet-stream"),"",o.content);
}
}else{
var o=_21c.file;
t.push("--"+this.multipartBoundary,"Content-Disposition: form-data; name=\""+o.name+"\"; filename=\""+("fileName" in o?o.fileName:o.name)+"\"","Content-Type: "+("contentType" in o?o.contentType:"application/octet-stream"),"",o.content);
}
}
if(t.length){
t.push("--"+this.multipartBoundary+"--","");
_21e=t.join("\r\n");
}
}while(false);
var _227=_21c["sync"]?false:true;
var _228=_21c["preventCache"]||(this.preventCache==true&&_21c["preventCache"]!=false);
var _229=_21c["useCache"]==true||(this.useCache==true&&_21c["useCache"]!=false);
if(!_228&&_229){
var _22a=getFromCache(url,_21e,_21c.method);
if(_22a){
doLoad(_21c,_22a,url,_21e,false);
return;
}
}
var http=dojo.hostenv.getXmlhttpObject();
var _22c=false;
if(_227){
this.inFlight.push({"req":_21c,"http":http,"url":url,"query":_21e,"useCache":_229});
this.startWatchingInFlight();
}
if(_21c.method.toLowerCase()=="post"){
http.open("POST",url,_227);
setHeaders(http,_21c);
http.setRequestHeader("Content-Type",_21c.multipart?("multipart/form-data; boundary="+this.multipartBoundary):(_21c.contentType||"application/x-www-form-urlencoded"));
http.send(_21e);
}else{
var _22d=url;
if(_21e!=""){
_22d+=(_22d.indexOf("?")>-1?"&":"?")+_21e;
}
if(_228){
_22d+=(dojo.string.endsWithAny(_22d,"?","&")?"":(_22d.indexOf("?")>-1?"&":"?"))+"dojo.preventCache="+new Date().valueOf();
}
http.open(_21c.method.toUpperCase(),_22d,_227);
setHeaders(http,_21c);
http.send(null);
}
if(!_227){
doLoad(_21c,http,url,_21e,_229);
}
_21c.abort=function(){
return http.abort();
};
return;
};
dojo.io.transports.addTransport("XMLHTTPTransport");
};

