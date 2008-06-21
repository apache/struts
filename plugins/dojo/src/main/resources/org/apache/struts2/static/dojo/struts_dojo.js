/*
	Copyright (c) 2004-2006, The Dojo Foundation
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

if(typeof dojo=="undefined"){
var dj_global=this;
var dj_currentContext=this;
function dj_undef(_1,_2){
return (typeof (_2||dj_currentContext)[_1]=="undefined");
}
if(dj_undef("djConfig",this)){
var djConfig={};
}
if(dj_undef("dojo",this)){
var dojo={};
}
dojo.global=function(){
return dj_currentContext;
};
dojo.locale=djConfig.locale;
dojo.version={major:0,minor:4,patch:3,flag:"",revision:Number("$Rev$".match(/[0-9]+/)[0]),toString:function(){
with(dojo.version){
return major+"."+minor+"."+patch+flag+" ("+revision+")";
}
}};
dojo.evalProp=function(_3,_4,_5){
if((!_4)||(!_3)){
return undefined;
}
if(!dj_undef(_3,_4)){
return _4[_3];
}
return (_5?(_4[_3]={}):undefined);
};
dojo.parseObjPath=function(_6,_7,_8){
var _9=(_7||dojo.global());
var _a=_6.split(".");
var _b=_a.pop();
for(var i=0,l=_a.length;i<l&&_9;i++){
_9=dojo.evalProp(_a[i],_9,_8);
}
return {obj:_9,prop:_b};
};
dojo.evalObjPath=function(_e,_f){
if(typeof _e!="string"){
return dojo.global();
}
if(_e.indexOf(".")==-1){
return dojo.evalProp(_e,dojo.global(),_f);
}
var ref=dojo.parseObjPath(_e,dojo.global(),_f);
if(ref){
return dojo.evalProp(ref.prop,ref.obj,_f);
}
return null;
};
dojo.errorToString=function(_11){
if(!dj_undef("message",_11)){
return _11.message;
}else{
if(!dj_undef("description",_11)){
return _11.description;
}else{
return _11;
}
}
};
dojo.raise=function(_12,_13){
if(_13){
_12=_12+": "+dojo.errorToString(_13);
}else{
_12=dojo.errorToString(_12);
}
try{
if(djConfig.isDebug){
dojo.hostenv.println("FATAL exception raised: "+_12);
}
}
catch(e){
}
throw _13||Error(_12);
};
dojo.debug=function(){
};
dojo.debugShallow=function(obj){
};
dojo.profile={start:function(){
},end:function(){
},stop:function(){
},dump:function(){
}};
function dj_eval(_15){
return dj_global.eval?dj_global.eval(_15):eval(_15);
}
dojo.unimplemented=function(_16,_17){
var _18="'"+_16+"' not implemented";
if(_17!=null){
_18+=" "+_17;
}
dojo.raise(_18);
};
dojo.deprecated=function(_19,_1a,_1b){
var _1c="DEPRECATED: "+_19;
if(_1a){
_1c+=" "+_1a;
}
if(_1b){
_1c+=" -- will be removed in version: "+_1b;
}
dojo.debug(_1c);
};
dojo.render=(function(){
function vscaffold(_1d,_1e){
var tmp={capable:false,support:{builtin:false,plugin:false},prefixes:_1d};
for(var i=0;i<_1e.length;i++){
tmp[_1e[i]]=false;
}
return tmp;
}
return {name:"",ver:dojo.version,os:{win:false,linux:false,osx:false},html:vscaffold(["html"],["ie","opera","khtml","safari","moz"]),svg:vscaffold(["svg"],["corel","adobe","batik"]),vml:vscaffold(["vml"],["ie"]),swf:vscaffold(["Swf","Flash","Mm"],["mm"]),swt:vscaffold(["Swt"],["ibm"])};
})();
dojo.hostenv=(function(){
var _21={isDebug:false,allowQueryConfig:false,baseScriptUri:"",baseRelativePath:"",libraryScriptUri:"",iePreventClobber:false,ieClobberMinimal:true,preventBackButtonFix:true,delayMozLoadingFix:false,searchIds:[],parseWidgets:true};
if(typeof djConfig=="undefined"){
djConfig=_21;
}else{
for(var _22 in _21){
if(typeof djConfig[_22]=="undefined"){
djConfig[_22]=_21[_22];
}
}
}
return {name_:"(unset)",version_:"(unset)",getName:function(){
return this.name_;
},getVersion:function(){
return this.version_;
},getText:function(uri){
dojo.unimplemented("getText","uri="+uri);
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
var _25=uri.lastIndexOf("/");
djConfig.baseScriptUri=djConfig.baseRelativePath;
return djConfig.baseScriptUri;
};
(function(){
var _26={pkgFileName:"__package__",loading_modules_:{},loaded_modules_:{},addedToLoadingCount:[],removedFromLoadingCount:[],inFlightCount:0,modulePrefixes_:{dojo:{name:"dojo",value:"src"}},setModulePrefix:function(_27,_28){
this.modulePrefixes_[_27]={name:_27,value:_28};
},moduleHasPrefix:function(_29){
var mp=this.modulePrefixes_;
return Boolean(mp[_29]&&mp[_29].value);
},getModulePrefix:function(_2b){
if(this.moduleHasPrefix(_2b)){
return this.modulePrefixes_[_2b].value;
}
return _2b;
},getTextStack:[],loadUriStack:[],loadedUris:[],post_load_:false,modulesLoadedListeners:[],unloadListeners:[],loadNotifying:false};
for(var _2c in _26){
dojo.hostenv[_2c]=_26[_2c];
}
})();
dojo.hostenv.loadPath=function(_2d,_2e,cb){
var uri;
if(_2d.charAt(0)=="/"||_2d.match(/^\w+:/)){
uri=_2d;
}else{
uri=this.getBaseScriptUri()+_2d;
}
if(djConfig.cacheBust&&dojo.render.html.capable){
uri+="?"+String(djConfig.cacheBust).replace(/\W+/g,"");
}
try{
return !_2e?this.loadUri(uri,cb):this.loadUriAndCheck(uri,_2e,cb);
}
catch(e){
dojo.debug(e);
return false;
}
};
dojo.hostenv.loadUri=function(uri,cb){
if(this.loadedUris[uri]){
return true;
}
var _33=this.getText(uri,null,true);
if(!_33){
return false;
}
this.loadedUris[uri]=true;
if(cb){
_33="("+_33+")";
}
var _34=dj_eval(_33);
if(cb){
cb(_34);
}
return true;
};
dojo.hostenv.loadUriAndCheck=function(uri,_36,cb){
var ok=true;
try{
ok=this.loadUri(uri,cb);
}
catch(e){
dojo.debug("failed loading ",uri," with error: ",e);
}
return Boolean(ok&&this.findModule(_36,false));
};
dojo.loaded=function(){
};
dojo.unloaded=function(){
};
dojo.hostenv.loaded=function(){
this.loadNotifying=true;
this.post_load_=true;
var mll=this.modulesLoadedListeners;
for(var x=0;x<mll.length;x++){
mll[x]();
}
this.modulesLoadedListeners=[];
this.loadNotifying=false;
dojo.loaded();
};
dojo.hostenv.unloaded=function(){
var mll=this.unloadListeners;
while(mll.length){
(mll.pop())();
}
dojo.unloaded();
};
dojo.addOnLoad=function(obj,_3d){
var dh=dojo.hostenv;
if(arguments.length==1){
dh.modulesLoadedListeners.push(obj);
}else{
if(arguments.length>1){
dh.modulesLoadedListeners.push(function(){
obj[_3d]();
});
}
}
if(dh.post_load_&&dh.inFlightCount==0&&!dh.loadNotifying){
dh.callLoaded();
}
};
dojo.addOnUnload=function(obj,_40){
var dh=dojo.hostenv;
if(arguments.length==1){
dh.unloadListeners.push(obj);
}else{
if(arguments.length>1){
dh.unloadListeners.push(function(){
obj[_40]();
});
}
}
};
dojo.hostenv.modulesLoaded=function(){
if(this.post_load_){
return;
}
if(this.loadUriStack.length==0&&this.getTextStack.length==0){
if(this.inFlightCount>0){
dojo.debug("files still in flight!");
return;
}
dojo.hostenv.callLoaded();
}
};
dojo.hostenv.callLoaded=function(){
if(typeof setTimeout=="object"||(djConfig["useXDomain"]&&dojo.render.html.opera)){
setTimeout("dojo.hostenv.loaded();",0);
}else{
dojo.hostenv.loaded();
}
};
dojo.hostenv.getModuleSymbols=function(_42){
var _43=_42.split(".");
for(var i=_43.length;i>0;i--){
var _45=_43.slice(0,i).join(".");
if((i==1)&&!this.moduleHasPrefix(_45)){
_43[0]="../"+_43[0];
}else{
var _46=this.getModulePrefix(_45);
if(_46!=_45){
_43.splice(0,i,_46);
break;
}
}
}
return _43;
};
dojo.hostenv._global_omit_module_check=false;
dojo.hostenv.loadModule=function(_47,_48,_49){
if(!_47){
return;
}
_49=this._global_omit_module_check||_49;
var _4a=this.findModule(_47,false);
if(_4a){
return _4a;
}
if(dj_undef(_47,this.loading_modules_)){
this.addedToLoadingCount.push(_47);
}
this.loading_modules_[_47]=1;
var _4b=_47.replace(/\./g,"/")+".js";
var _4c=_47.split(".");
var _4d=this.getModuleSymbols(_47);
var _4e=((_4d[0].charAt(0)!="/")&&!_4d[0].match(/^\w+:/));
var _4f=_4d[_4d.length-1];
var ok;
if(_4f=="*"){
_47=_4c.slice(0,-1).join(".");
while(_4d.length){
_4d.pop();
_4d.push(this.pkgFileName);
_4b=_4d.join("/")+".js";
if(_4e&&_4b.charAt(0)=="/"){
_4b=_4b.slice(1);
}
ok=this.loadPath(_4b,!_49?_47:null);
if(ok){
break;
}
_4d.pop();
}
}else{
_4b=_4d.join("/")+".js";
_47=_4c.join(".");
var _51=!_49?_47:null;
ok=this.loadPath(_4b,_51);
if(!ok&&!_48){
_4d.pop();
while(_4d.length){
_4b=_4d.join("/")+".js";
ok=this.loadPath(_4b,_51);
if(ok){
break;
}
_4d.pop();
_4b=_4d.join("/")+"/"+this.pkgFileName+".js";
if(_4e&&_4b.charAt(0)=="/"){
_4b=_4b.slice(1);
}
ok=this.loadPath(_4b,_51);
if(ok){
break;
}
}
}
if(!ok&&!_49){
dojo.raise("Could not load '"+_47+"'; last tried '"+_4b+"'");
}
}
if(!_49&&!this["isXDomain"]){
_4a=this.findModule(_47,false);
if(!_4a){
dojo.raise("symbol '"+_47+"' is not defined after loading '"+_4b+"'");
}
}
return _4a;
};
dojo.hostenv.startPackage=function(_52){
var _53=String(_52);
var _54=_53;
var _55=_52.split(/\./);
if(_55[_55.length-1]=="*"){
_55.pop();
_54=_55.join(".");
}
var _56=dojo.evalObjPath(_54,true);
this.loaded_modules_[_53]=_56;
this.loaded_modules_[_54]=_56;
return _56;
};
dojo.hostenv.findModule=function(_57,_58){
var lmn=String(_57);
if(this.loaded_modules_[lmn]){
return this.loaded_modules_[lmn];
}
if(_58){
dojo.raise("no loaded module named '"+_57+"'");
}
return null;
};
dojo.kwCompoundRequire=function(_5a){
var _5b=_5a["common"]||[];
var _5c=_5a[dojo.hostenv.name_]?_5b.concat(_5a[dojo.hostenv.name_]||[]):_5b.concat(_5a["default"]||[]);
for(var x=0;x<_5c.length;x++){
var _5e=_5c[x];
if(_5e.constructor==Array){
dojo.hostenv.loadModule.apply(dojo.hostenv,_5e);
}else{
dojo.hostenv.loadModule(_5e);
}
}
};
dojo.require=function(_5f){
dojo.hostenv.loadModule.apply(dojo.hostenv,arguments);
};
dojo.requireIf=function(_60,_61){
var _62=arguments[0];
if((_62===true)||(_62=="common")||(_62&&dojo.render[_62].capable)){
var _63=[];
for(var i=1;i<arguments.length;i++){
_63.push(arguments[i]);
}
dojo.require.apply(dojo,_63);
}
};
dojo.requireAfterIf=dojo.requireIf;
dojo.provide=function(_65){
return dojo.hostenv.startPackage.apply(dojo.hostenv,arguments);
};
dojo.registerModulePath=function(_66,_67){
return dojo.hostenv.setModulePrefix(_66,_67);
};
if(djConfig["modulePaths"]){
for(var param in djConfig["modulePaths"]){
dojo.registerModulePath(param,djConfig["modulePaths"][param]);
}
}
dojo.setModulePrefix=function(_68,_69){
dojo.deprecated("dojo.setModulePrefix(\""+_68+"\", \""+_69+"\")","replaced by dojo.registerModulePath","0.5");
return dojo.registerModulePath(_68,_69);
};
dojo.exists=function(obj,_6b){
var p=_6b.split(".");
for(var i=0;i<p.length;i++){
if(!obj[p[i]]){
return false;
}
obj=obj[p[i]];
}
return true;
};
dojo.hostenv.normalizeLocale=function(_6e){
var _6f=_6e?_6e.toLowerCase():dojo.locale;
if(_6f=="root"){
_6f="ROOT";
}
return _6f;
};
dojo.hostenv.searchLocalePath=function(_70,_71,_72){
_70=dojo.hostenv.normalizeLocale(_70);
var _73=_70.split("-");
var _74=[];
for(var i=_73.length;i>0;i--){
_74.push(_73.slice(0,i).join("-"));
}
_74.push(false);
if(_71){
_74.reverse();
}
for(var j=_74.length-1;j>=0;j--){
var loc=_74[j]||"ROOT";
var _78=_72(loc);
if(_78){
break;
}
}
};
dojo.hostenv.localesGenerated;
dojo.hostenv.registerNlsPrefix=function(){
dojo.registerModulePath("nls","nls");
};
dojo.hostenv.preloadLocalizations=function(){
if(dojo.hostenv.localesGenerated){
dojo.hostenv.registerNlsPrefix();
function preload(_79){
_79=dojo.hostenv.normalizeLocale(_79);
dojo.hostenv.searchLocalePath(_79,true,function(loc){
for(var i=0;i<dojo.hostenv.localesGenerated.length;i++){
if(dojo.hostenv.localesGenerated[i]==loc){
dojo["require"]("nls.dojo_"+loc);
return true;
}
}
return false;
});
}
preload();
var _7c=djConfig.extraLocale||[];
for(var i=0;i<_7c.length;i++){
preload(_7c[i]);
}
}
dojo.hostenv.preloadLocalizations=function(){
};
};
dojo.requireLocalization=function(_7e,_7f,_80,_81){
dojo.hostenv.preloadLocalizations();
var _82=dojo.hostenv.normalizeLocale(_80);
var _83=[_7e,"nls",_7f].join(".");
var _84="";
if(_81){
var _85=_81.split(",");
for(var i=0;i<_85.length;i++){
if(_82.indexOf(_85[i])==0){
if(_85[i].length>_84.length){
_84=_85[i];
}
}
}
if(!_84){
_84="ROOT";
}
}
var _87=_81?_84:_82;
var _88=dojo.hostenv.findModule(_83);
var _89=null;
if(_88){
if(djConfig.localizationComplete&&_88._built){
return;
}
var _8a=_87.replace("-","_");
var _8b=_83+"."+_8a;
_89=dojo.hostenv.findModule(_8b);
}
if(!_89){
_88=dojo.hostenv.startPackage(_83);
var _8c=dojo.hostenv.getModuleSymbols(_7e);
var _8d=_8c.concat("nls").join("/");
var _8e;
dojo.hostenv.searchLocalePath(_87,_81,function(loc){
var _90=loc.replace("-","_");
var _91=_83+"."+_90;
var _92=false;
if(!dojo.hostenv.findModule(_91)){
dojo.hostenv.startPackage(_91);
var _93=[_8d];
if(loc!="ROOT"){
_93.push(loc);
}
_93.push(_7f);
var _94=_93.join("/")+".js";
_92=dojo.hostenv.loadPath(_94,null,function(_95){
var _96=function(){
};
_96.prototype=_8e;
_88[_90]=new _96();
for(var j in _95){
_88[_90][j]=_95[j];
}
});
}else{
_92=true;
}
if(_92&&_88[_90]){
_8e=_88[_90];
}else{
_88[_90]=_8e;
}
if(_81){
return true;
}
});
}
if(_81&&_82!=_84){
_88[_82.replace("-","_")]=_88[_84.replace("-","_")];
}
};
(function(){
var _98=djConfig.extraLocale;
if(_98){
if(!_98 instanceof Array){
_98=[_98];
}
var req=dojo.requireLocalization;
dojo.requireLocalization=function(m,b,_9c,_9d){
req(m,b,_9c,_9d);
if(_9c){
return;
}
for(var i=0;i<_98.length;i++){
req(m,b,_98[i],_9d);
}
};
}
})();
}
if(typeof window!="undefined"){
(function(){
if(djConfig.allowQueryConfig){
var _9f=document.location.toString();
var _a0=_9f.split("?",2);
if(_a0.length>1){
var _a1=_a0[1];
var _a2=_a1.split("&");
for(var x in _a2){
var sp=_a2[x].split("=");
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
var _a6=document.getElementsByTagName("script");
var _a7=/(__package__|dojo|bootstrap1)\.js([\?\.]|$)/i;
for(var i=0;i<_a6.length;i++){
var src=_a6[i].getAttribute("src");
if(!src){
continue;
}
var m=src.match(_a7);
if(m){
var _ab=src.substring(0,m.index);
if(src.indexOf("bootstrap1")>-1){
_ab+="../";
}
if(!this["djConfig"]){
djConfig={};
}
if(djConfig["baseScriptUri"]==""){
djConfig["baseScriptUri"]=_ab;
}
if(djConfig["baseRelativePath"]==""){
djConfig["baseRelativePath"]=_ab;
}
break;
}
}
}
var dr=dojo.render;
var drh=dojo.render.html;
var drs=dojo.render.svg;
var dua=(drh.UA=navigator.userAgent);
var dav=(drh.AV=navigator.appVersion);
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
var _b3=dua.indexOf("Gecko");
drh.mozilla=drh.moz=(_b3>=0)&&(!drh.khtml);
if(drh.mozilla){
drh.geckoVersion=dua.substring(_b3+6,_b3+14);
}
drh.ie=(document.all)&&(!drh.opera);
drh.ie50=drh.ie&&dav.indexOf("MSIE 5.0")>=0;
drh.ie55=drh.ie&&dav.indexOf("MSIE 5.5")>=0;
drh.ie60=drh.ie&&dav.indexOf("MSIE 6.0")>=0;
drh.ie70=drh.ie&&dav.indexOf("MSIE 7.0")>=0;
var cm=document["compatMode"];
drh.quirks=(cm=="BackCompat")||(cm=="QuirksMode")||drh.ie55||drh.ie50;
dojo.locale=dojo.locale||(drh.ie?navigator.userLanguage:navigator.language).toLowerCase();
dr.vml.capable=drh.ie;
drs.capable=f;
drs.support.plugin=f;
drs.support.builtin=f;
var _b5=window["document"];
var tdi=_b5["implementation"];
if((tdi)&&(tdi["hasFeature"])&&(tdi.hasFeature("org.w3c.dom.svg","1.0"))){
drs.capable=t;
drs.support.builtin=t;
drs.support.plugin=f;
}
if(drh.safari){
var tmp=dua.split("AppleWebKit/")[1];
var ver=parseFloat(tmp.split(" ")[0]);
if(ver>=420){
drs.capable=t;
drs.support.builtin=t;
drs.support.plugin=f;
}
}else{
}
})();
dojo.hostenv.startPackage("dojo.hostenv");
dojo.render.name=dojo.hostenv.name_="browser";
dojo.hostenv.searchIds=[];
dojo.hostenv._XMLHTTP_PROGIDS=["Msxml2.XMLHTTP","Microsoft.XMLHTTP","Msxml2.XMLHTTP.4.0"];
dojo.hostenv.getXmlhttpObject=function(){
var _b9=null;
var _ba=null;
try{
_b9=new XMLHttpRequest();
}
catch(e){
}
if(!_b9){
for(var i=0;i<3;++i){
var _bc=dojo.hostenv._XMLHTTP_PROGIDS[i];
try{
_b9=new ActiveXObject(_bc);
}
catch(e){
_ba=e;
}
if(_b9){
dojo.hostenv._XMLHTTP_PROGIDS=[_bc];
break;
}
}
}
if(!_b9){
return dojo.raise("XMLHTTP not available",_ba);
}
return _b9;
};
dojo.hostenv._blockAsync=false;
dojo.hostenv.getText=function(uri,_be,_bf){
if(!_be){
this._blockAsync=true;
}
var _c0=this.getXmlhttpObject();
function isDocumentOk(_c1){
var _c2=_c1["status"];
return Boolean((!_c2)||((200<=_c2)&&(300>_c2))||(_c2==304));
}
if(_be){
var _c3=this,_c4=null,gbl=dojo.global();
var xhr=dojo.evalObjPath("dojo.io.XMLHTTPTransport");
_c0.onreadystatechange=function(){
if(_c4){
gbl.clearTimeout(_c4);
_c4=null;
}
if(_c3._blockAsync||(xhr&&xhr._blockAsync)){
_c4=gbl.setTimeout(function(){
_c0.onreadystatechange.apply(this);
},10);
}else{
if(4==_c0.readyState){
if(isDocumentOk(_c0)){
_be(_c0.responseText);
}
}
}
};
}
_c0.open("GET",uri,_be?true:false);
try{
_c0.send(null);
if(_be){
return null;
}
if(!isDocumentOk(_c0)){
var err=Error("Unable to load "+uri+" status:"+_c0.status);
err.status=_c0.status;
err.responseText=_c0.responseText;
throw err;
}
}
catch(e){
this._blockAsync=false;
if((_bf)&&(!_be)){
return null;
}else{
throw e;
}
}
this._blockAsync=false;
return _c0.responseText;
};
dojo.hostenv.defaultDebugContainerId="dojoDebug";
dojo.hostenv._println_buffer=[];
dojo.hostenv._println_safe=false;
dojo.hostenv.println=function(_c8){
if(!dojo.hostenv._println_safe){
dojo.hostenv._println_buffer.push(_c8);
}else{
try{
var _c9=document.getElementById(djConfig.debugContainerId?djConfig.debugContainerId:dojo.hostenv.defaultDebugContainerId);
if(!_c9){
_c9=dojo.body();
}
var div=document.createElement("div");
div.appendChild(document.createTextNode(_c8));
_c9.appendChild(div);
}
catch(e){
try{
document.write("<div>"+_c8+"</div>");
}
catch(e2){
window.status=_c8;
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
function dj_addNodeEvtHdlr(_cb,_cc,fp){
var _ce=_cb["on"+_cc]||function(){
};
_cb["on"+_cc]=function(){
fp.apply(_cb,arguments);
_ce.apply(_cb,arguments);
};
return true;
}
dojo.hostenv._djInitFired=false;
function dj_load_init(e){
dojo.hostenv._djInitFired=true;
var _d0=(e&&e.type)?e.type.toLowerCase():"load";
if(arguments.callee.initialized||(_d0!="domcontentloaded"&&_d0!="load")){
return;
}
arguments.callee.initialized=true;
if(typeof (_timer)!="undefined"){
clearInterval(_timer);
delete _timer;
}
var _d1=function(){
if(dojo.render.html.ie){
dojo.hostenv.makeWidgets();
}
};
if(dojo.hostenv.inFlightCount==0){
_d1();
dojo.hostenv.modulesLoaded();
}else{
dojo.hostenv.modulesLoadedListeners.unshift(_d1);
}
}
if(document.addEventListener){
if(dojo.render.html.opera||(dojo.render.html.moz&&(djConfig["enableMozDomContentLoaded"]===true))){
document.addEventListener("DOMContentLoaded",dj_load_init,null);
}
window.addEventListener("load",dj_load_init,null);
}
if(dojo.render.html.ie&&dojo.render.os.win){
document.attachEvent("onreadystatechange",function(e){
if(document.readyState=="complete"){
dj_load_init();
}
});
}
if(/(WebKit|khtml)/i.test(navigator.userAgent)){
var _timer=setInterval(function(){
if(/loaded|complete/.test(document.readyState)){
dj_load_init();
}
},10);
}
if(dojo.render.html.ie){
dj_addNodeEvtHdlr(window,"beforeunload",function(){
dojo.hostenv._unloading=true;
window.setTimeout(function(){
dojo.hostenv._unloading=false;
},0);
});
}
dj_addNodeEvtHdlr(window,"unload",function(){
dojo.hostenv.unloaded();
if((!dojo.render.html.ie)||(dojo.render.html.ie&&dojo.hostenv._unloading)){
dojo.hostenv.unloaded();
}
});
dojo.hostenv.makeWidgets=function(){
var _d3=[];
if(djConfig.searchIds&&djConfig.searchIds.length>0){
_d3=_d3.concat(djConfig.searchIds);
}
if(dojo.hostenv.searchIds&&dojo.hostenv.searchIds.length>0){
_d3=_d3.concat(dojo.hostenv.searchIds);
}
if((djConfig.parseWidgets)||(_d3.length>0)){
if(dojo.evalObjPath("dojo.widget.Parse")){
var _d4=new dojo.xml.Parse();
if(_d3.length>0){
for(var x=0;x<_d3.length;x++){
var _d6=document.getElementById(_d3[x]);
if(!_d6){
continue;
}
var _d7=_d4.parseElement(_d6,null,true);
dojo.widget.getParser().createComponents(_d7);
}
}else{
if(djConfig.parseWidgets){
var _d7=_d4.parseElement(dojo.body(),null,true);
dojo.widget.getParser().createComponents(_d7);
}
}
}
}
};
dojo.addOnLoad(function(){
if(!dojo.render.html.ie){
dojo.hostenv.makeWidgets();
}
});
try{
if(dojo.render.html.ie){
document.namespaces.add("v","urn:schemas-microsoft-com:vml");
document.createStyleSheet().addRule("v\\:*","behavior:url(#default#VML)");
}
}
catch(e){
}
dojo.hostenv.writeIncludes=function(){
};
if(!dj_undef("document",this)){
dj_currentDocument=this.document;
}
dojo.doc=function(){
return dj_currentDocument;
};
dojo.body=function(){
return dojo.doc().body||dojo.doc().getElementsByTagName("body")[0];
};
dojo.byId=function(id,doc){
if((id)&&((typeof id=="string")||(id instanceof String))){
if(!doc){
doc=dj_currentDocument;
}
var ele=doc.getElementById(id);
if(ele&&(ele.id!=id)&&doc.all){
ele=null;
eles=doc.all[id];
if(eles){
if(eles.length){
for(var i=0;i<eles.length;i++){
if(eles[i].id==id){
ele=eles[i];
break;
}
}
}else{
ele=eles;
}
}
}
return ele;
}
return id;
};
dojo.setContext=function(_dc,_dd){
dj_currentContext=_dc;
dj_currentDocument=_dd;
};
dojo._fireCallback=function(_de,_df,_e0){
if((_df)&&((typeof _de=="string")||(_de instanceof String))){
_de=_df[_de];
}
return (_df?_de.apply(_df,_e0||[]):_de());
};
dojo.withGlobal=function(_e1,_e2,_e3,_e4){
var _e5;
var _e6=dj_currentContext;
var _e7=dj_currentDocument;
try{
dojo.setContext(_e1,_e1.document);
_e5=dojo._fireCallback(_e2,_e3,_e4);
}
finally{
dojo.setContext(_e6,_e7);
}
return _e5;
};
dojo.withDoc=function(_e8,_e9,_ea,_eb){
var _ec;
var _ed=dj_currentDocument;
try{
dj_currentDocument=_e8;
_ec=dojo._fireCallback(_e9,_ea,_eb);
}
finally{
dj_currentDocument=_ed;
}
return _ec;
};
}
dojo.requireIf((djConfig["isDebug"]||djConfig["debugAtAllCosts"]),"dojo.debug");
dojo.requireIf(djConfig["debugAtAllCosts"]&&!window.widget&&!djConfig["useXDomain"],"dojo.browser_debug");
dojo.requireIf(djConfig["debugAtAllCosts"]&&!window.widget&&djConfig["useXDomain"],"dojo.browser_debug_xd");
dojo.provide("dojo.string.common");
dojo.string.trim=function(str,wh){
if(!str.replace){
return str;
}
if(!str.length){
return str;
}
var re=(wh>0)?(/^\s+/):(wh<0)?(/\s+$/):(/^\s+|\s+$/g);
return str.replace(re,"");
};
dojo.string.trimStart=function(str){
return dojo.string.trim(str,1);
};
dojo.string.trimEnd=function(str){
return dojo.string.trim(str,-1);
};
dojo.string.repeat=function(str,_f4,_f5){
var out="";
for(var i=0;i<_f4;i++){
out+=str;
if(_f5&&i<_f4-1){
out+=_f5;
}
}
return out;
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
dojo.provide("dojo.string");
dojo.provide("dojo.lang.common");
dojo.lang.inherits=function(_103,_104){
if(!dojo.lang.isFunction(_104)){
dojo.raise("dojo.inherits: superclass argument ["+_104+"] must be a function (subclass: ["+_103+"']");
}
_103.prototype=new _104();
_103.prototype.constructor=_103;
_103.superclass=_104.prototype;
_103["super"]=_104.prototype;
};
dojo.lang._mixin=function(obj,_106){
var tobj={};
for(var x in _106){
if((typeof tobj[x]=="undefined")||(tobj[x]!=_106[x])){
obj[x]=_106[x];
}
}
if(dojo.render.html.ie&&(typeof (_106["toString"])=="function")&&(_106["toString"]!=obj["toString"])&&(_106["toString"]!=tobj["toString"])){
obj.toString=_106.toString;
}
return obj;
};
dojo.lang.mixin=function(obj,_10a){
for(var i=1,l=arguments.length;i<l;i++){
dojo.lang._mixin(obj,arguments[i]);
}
return obj;
};
dojo.lang.extend=function(_10d,_10e){
for(var i=1,l=arguments.length;i<l;i++){
dojo.lang._mixin(_10d.prototype,arguments[i]);
}
return _10d;
};
dojo.inherits=dojo.lang.inherits;
dojo.mixin=dojo.lang.mixin;
dojo.extend=dojo.lang.extend;
dojo.lang.find=function(_111,_112,_113,_114){
if(!dojo.lang.isArrayLike(_111)&&dojo.lang.isArrayLike(_112)){
dojo.deprecated("dojo.lang.find(value, array)","use dojo.lang.find(array, value) instead","0.5");
var temp=_111;
_111=_112;
_112=temp;
}
var _116=dojo.lang.isString(_111);
if(_116){
_111=_111.split("");
}
if(_114){
var step=-1;
var i=_111.length-1;
var end=-1;
}else{
var step=1;
var i=0;
var end=_111.length;
}
if(_113){
while(i!=end){
if(_111[i]===_112){
return i;
}
i+=step;
}
}else{
while(i!=end){
if(_111[i]==_112){
return i;
}
i+=step;
}
}
return -1;
};
dojo.lang.indexOf=dojo.lang.find;
dojo.lang.findLast=function(_11a,_11b,_11c){
return dojo.lang.find(_11a,_11b,_11c,true);
};
dojo.lang.lastIndexOf=dojo.lang.findLast;
dojo.lang.inArray=function(_11d,_11e){
return dojo.lang.find(_11d,_11e)>-1;
};
dojo.lang.isObject=function(it){
if(typeof it=="undefined"){
return false;
}
return (typeof it=="object"||it===null||dojo.lang.isArray(it)||dojo.lang.isFunction(it));
};
dojo.lang.isArray=function(it){
return (it&&it instanceof Array||typeof it=="array");
};
dojo.lang.isArrayLike=function(it){
if((!it)||(dojo.lang.isUndefined(it))){
return false;
}
if(dojo.lang.isString(it)){
return false;
}
if(dojo.lang.isFunction(it)){
return false;
}
if(dojo.lang.isArray(it)){
return true;
}
if((it.tagName)&&(it.tagName.toLowerCase()=="form")){
return false;
}
if(dojo.lang.isNumber(it.length)&&isFinite(it.length)){
return true;
}
return false;
};
dojo.lang.isFunction=function(it){
return (it instanceof Function||typeof it=="function");
};
(function(){
if((dojo.render.html.capable)&&(dojo.render.html["safari"])){
dojo.lang.isFunction=function(it){
if((typeof (it)=="function")&&(it=="[object NodeList]")){
return false;
}
return (it instanceof Function||typeof it=="function");
};
}
})();
dojo.lang.isString=function(it){
return (typeof it=="string"||it instanceof String);
};
dojo.lang.isAlien=function(it){
if(!it){
return false;
}
return !dojo.lang.isFunction(it)&&/\{\s*\[native code\]\s*\}/.test(String(it));
};
dojo.lang.isBoolean=function(it){
return (it instanceof Boolean||typeof it=="boolean");
};
dojo.lang.isNumber=function(it){
return (it instanceof Number||typeof it=="number");
};
dojo.lang.isUndefined=function(it){
return ((typeof (it)=="undefined")&&(it==undefined));
};
dojo.provide("dojo.lang.extras");
dojo.lang.setTimeout=function(func,_12a){
var _12b=window,_12c=2;
if(!dojo.lang.isFunction(func)){
_12b=func;
func=_12a;
_12a=arguments[2];
_12c++;
}
if(dojo.lang.isString(func)){
func=_12b[func];
}
var args=[];
for(var i=_12c;i<arguments.length;i++){
args.push(arguments[i]);
}
return dojo.global().setTimeout(function(){
func.apply(_12b,args);
},_12a);
};
dojo.lang.clearTimeout=function(_12f){
dojo.global().clearTimeout(_12f);
};
dojo.lang.getNameInObj=function(ns,item){
if(!ns){
ns=dj_global;
}
for(var x in ns){
if(ns[x]===item){
return new String(x);
}
}
return null;
};
dojo.lang.shallowCopy=function(obj,deep){
var i,ret;
if(obj===null){
return null;
}
if(dojo.lang.isObject(obj)){
ret=new obj.constructor();
for(i in obj){
if(dojo.lang.isUndefined(ret[i])){
ret[i]=deep?dojo.lang.shallowCopy(obj[i],deep):obj[i];
}
}
}else{
if(dojo.lang.isArray(obj)){
ret=[];
for(i=0;i<obj.length;i++){
ret[i]=deep?dojo.lang.shallowCopy(obj[i],deep):obj[i];
}
}else{
ret=obj;
}
}
return ret;
};
dojo.lang.firstValued=function(){
for(var i=0;i<arguments.length;i++){
if(typeof arguments[i]!="undefined"){
return arguments[i];
}
}
return undefined;
};
dojo.lang.getObjPathValue=function(_138,_139,_13a){
with(dojo.parseObjPath(_138,_139,_13a)){
return dojo.evalProp(prop,obj,_13a);
}
};
dojo.lang.setObjPathValue=function(_13b,_13c,_13d,_13e){
dojo.deprecated("dojo.lang.setObjPathValue","use dojo.parseObjPath and the '=' operator","0.6");
if(arguments.length<4){
_13e=true;
}
with(dojo.parseObjPath(_13b,_13d,_13e)){
if(obj&&(_13e||(prop in obj))){
obj[prop]=_13c;
}
}
};
dojo.provide("dojo.io.common");
dojo.io.transports=[];
dojo.io.hdlrFuncNames=["load","error","timeout"];
dojo.io.Request=function(url,_140,_141,_142){
if((arguments.length==1)&&(arguments[0].constructor==Object)){
this.fromKwArgs(arguments[0]);
}else{
this.url=url;
if(_140){
this.mimetype=_140;
}
if(_141){
this.transport=_141;
}
if(arguments.length>=4){
this.changeUrl=_142;
}
}
};
dojo.lang.extend(dojo.io.Request,{url:"",mimetype:"text/plain",method:"GET",content:undefined,transport:undefined,changeUrl:undefined,formNode:undefined,sync:false,bindSuccess:false,useCache:false,preventCache:false,jsonFilter:function(_143){
if((this.mimetype=="text/json-comment-filtered")||(this.mimetype=="application/json-comment-filtered")){
var _144=_143.indexOf("/*");
var _145=_143.lastIndexOf("*/");
if((_144==-1)||(_145==-1)){
dojo.debug("your JSON wasn't comment filtered!");
return "";
}
return _143.substring(_144+2,_145);
}
dojo.debug("please consider using a mimetype of text/json-comment-filtered to avoid potential security issues with JSON endpoints");
return _143;
},load:function(type,data,_148,_149){
},error:function(type,_14b,_14c,_14d){
},timeout:function(type,_14f,_150,_151){
},handle:function(type,data,_154,_155){
},timeoutSeconds:0,abort:function(){
},fromKwArgs:function(_156){
if(_156["url"]){
_156.url=_156.url.toString();
}
if(_156["formNode"]){
_156.formNode=dojo.byId(_156.formNode);
}
if(!_156["method"]&&_156["formNode"]&&_156["formNode"].method){
_156.method=_156["formNode"].method;
}
if(!_156["handle"]&&_156["handler"]){
_156.handle=_156.handler;
}
if(!_156["load"]&&_156["loaded"]){
_156.load=_156.loaded;
}
if(!_156["changeUrl"]&&_156["changeURL"]){
_156.changeUrl=_156.changeURL;
}
_156.encoding=dojo.lang.firstValued(_156["encoding"],djConfig["bindEncoding"],"");
_156.sendTransport=dojo.lang.firstValued(_156["sendTransport"],djConfig["ioSendTransport"],false);
var _157=dojo.lang.isFunction;
for(var x=0;x<dojo.io.hdlrFuncNames.length;x++){
var fn=dojo.io.hdlrFuncNames[x];
if(_156[fn]&&_157(_156[fn])){
continue;
}
if(_156["handle"]&&_157(_156["handle"])){
_156[fn]=_156.handle;
}
}
dojo.lang.mixin(this,_156);
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
dojo.io.bind=function(_15e){
if(!(_15e instanceof dojo.io.Request)){
try{
_15e=new dojo.io.Request(_15e);
}
catch(e){
dojo.debug(e);
}
}
var _15f="";
if(_15e["transport"]){
_15f=_15e["transport"];
if(!this[_15f]){
dojo.io.sendBindError(_15e,"No dojo.io.bind() transport with name '"+_15e["transport"]+"'.");
return _15e;
}
if(!this[_15f].canHandle(_15e)){
dojo.io.sendBindError(_15e,"dojo.io.bind() transport with name '"+_15e["transport"]+"' cannot handle this type of request.");
return _15e;
}
}else{
for(var x=0;x<dojo.io.transports.length;x++){
var tmp=dojo.io.transports[x];
if((this[tmp])&&(this[tmp].canHandle(_15e))){
_15f=tmp;
break;
}
}
if(_15f==""){
dojo.io.sendBindError(_15e,"None of the loaded transports for dojo.io.bind()"+" can handle the request.");
return _15e;
}
}
this[_15f].bind(_15e);
_15e.bindSuccess=true;
return _15e;
};
dojo.io.sendBindError=function(_162,_163){
if((typeof _162.error=="function"||typeof _162.handle=="function")&&(typeof setTimeout=="function"||typeof setTimeout=="object")){
var _164=new dojo.io.Error(_163);
setTimeout(function(){
_162[(typeof _162.error=="function")?"error":"handle"]("error",_164,null,_162);
},50);
}else{
dojo.raise(_163);
}
};
dojo.io.queueBind=function(_165){
if(!(_165 instanceof dojo.io.Request)){
try{
_165=new dojo.io.Request(_165);
}
catch(e){
dojo.debug(e);
}
}
var _166=_165.load;
_165.load=function(){
dojo.io._queueBindInFlight=false;
var ret=_166.apply(this,arguments);
dojo.io._dispatchNextQueueBind();
return ret;
};
var _168=_165.error;
_165.error=function(){
dojo.io._queueBindInFlight=false;
var ret=_168.apply(this,arguments);
dojo.io._dispatchNextQueueBind();
return ret;
};
dojo.io._bindQueue.push(_165);
dojo.io._dispatchNextQueueBind();
return _165;
};
dojo.io._dispatchNextQueueBind=function(){
if(!dojo.io._queueBindInFlight){
dojo.io._queueBindInFlight=true;
if(dojo.io._bindQueue.length>0){
dojo.io.bind(dojo.io._bindQueue.shift());
}else{
dojo.io._queueBindInFlight=false;
}
}
};
dojo.io._bindQueue=[];
dojo.io._queueBindInFlight=false;
dojo.io.argsFromMap=function(map,_16b,last){
var enc=/utf/i.test(_16b||"")?encodeURIComponent:dojo.string.encodeAscii;
var _16e=[];
var _16f=new Object();
for(var name in map){
var _171=function(elt){
var val=enc(name)+"="+enc(elt);
_16e[(last==name)?"push":"unshift"](val);
};
if(!_16f[name]){
var _174=map[name];
if(dojo.lang.isArray(_174)){
dojo.lang.forEach(_174,_171);
}else{
_171(_174);
}
}
}
return _16e.join("&");
};
dojo.io.setIFrameSrc=function(_175,src,_177){
try{
var r=dojo.render.html;
if(!_177){
if(r.safari){
_175.location=src;
}else{
frames[_175.name].location=src;
}
}else{
var idoc;
if(r.ie){
idoc=_175.contentWindow.document;
}else{
if(r.safari){
idoc=_175.document;
}else{
idoc=_175.contentWindow;
}
}
if(!idoc){
_175.location=src;
return;
}else{
idoc.location.replace(src);
}
}
}
catch(e){
dojo.debug(e);
dojo.debug("setIFrameSrc: "+e);
}
};
dojo.provide("dojo.lang.array");
dojo.lang.mixin(dojo.lang,{has:function(obj,name){
try{
return typeof obj[name]!="undefined";
}
catch(e){
return false;
}
},isEmpty:function(obj){
if(dojo.lang.isObject(obj)){
var tmp={};
var _17e=0;
for(var x in obj){
if(obj[x]&&(!tmp[x])){
_17e++;
break;
}
}
return _17e==0;
}else{
if(dojo.lang.isArrayLike(obj)||dojo.lang.isString(obj)){
return obj.length==0;
}
}
},map:function(arr,obj,_182){
var _183=dojo.lang.isString(arr);
if(_183){
arr=arr.split("");
}
if(dojo.lang.isFunction(obj)&&(!_182)){
_182=obj;
obj=dj_global;
}else{
if(dojo.lang.isFunction(obj)&&_182){
var _184=obj;
obj=_182;
_182=_184;
}
}
if(Array.map){
var _185=Array.map(arr,_182,obj);
}else{
var _185=[];
for(var i=0;i<arr.length;++i){
_185.push(_182.call(obj,arr[i]));
}
}
if(_183){
return _185.join("");
}else{
return _185;
}
},reduce:function(arr,_188,obj,_18a){
var _18b=_188;
if(arguments.length==2){
_18a=_188;
_18b=arr[0];
arr=arr.slice(1);
}else{
if(arguments.length==3){
if(dojo.lang.isFunction(obj)){
_18a=obj;
obj=null;
}
}else{
if(dojo.lang.isFunction(obj)){
var tmp=_18a;
_18a=obj;
obj=tmp;
}
}
}
var ob=obj||dj_global;
dojo.lang.map(arr,function(val){
_18b=_18a.call(ob,_18b,val);
});
return _18b;
},forEach:function(_18f,_190,_191){
if(dojo.lang.isString(_18f)){
_18f=_18f.split("");
}
if(Array.forEach){
Array.forEach(_18f,_190,_191);
}else{
if(!_191){
_191=dj_global;
}
for(var i=0,l=_18f.length;i<l;i++){
_190.call(_191,_18f[i],i,_18f);
}
}
},_everyOrSome:function(_194,arr,_196,_197){
if(dojo.lang.isString(arr)){
arr=arr.split("");
}
if(Array.every){
return Array[_194?"every":"some"](arr,_196,_197);
}else{
if(!_197){
_197=dj_global;
}
for(var i=0,l=arr.length;i<l;i++){
var _19a=_196.call(_197,arr[i],i,arr);
if(_194&&!_19a){
return false;
}else{
if((!_194)&&(_19a)){
return true;
}
}
}
return Boolean(_194);
}
},every:function(arr,_19c,_19d){
return this._everyOrSome(true,arr,_19c,_19d);
},some:function(arr,_19f,_1a0){
return this._everyOrSome(false,arr,_19f,_1a0);
},filter:function(arr,_1a2,_1a3){
var _1a4=dojo.lang.isString(arr);
if(_1a4){
arr=arr.split("");
}
var _1a5;
if(Array.filter){
_1a5=Array.filter(arr,_1a2,_1a3);
}else{
if(!_1a3){
if(arguments.length>=3){
dojo.raise("thisObject doesn't exist!");
}
_1a3=dj_global;
}
_1a5=[];
for(var i=0;i<arr.length;i++){
if(_1a2.call(_1a3,arr[i],i,arr)){
_1a5.push(arr[i]);
}
}
}
if(_1a4){
return _1a5.join("");
}else{
return _1a5;
}
},unnest:function(){
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
},toArray:function(_1aa,_1ab){
var _1ac=[];
for(var i=_1ab||0;i<_1aa.length;i++){
_1ac.push(_1aa[i]);
}
return _1ac;
}});
dojo.provide("dojo.lang.func");
dojo.lang.hitch=function(_1ae,_1af){
var args=[];
for(var x=2;x<arguments.length;x++){
args.push(arguments[x]);
}
var fcn=(dojo.lang.isString(_1af)?_1ae[_1af]:_1af)||function(){
};
return function(){
var ta=args.concat([]);
for(var x=0;x<arguments.length;x++){
ta.push(arguments[x]);
}
return fcn.apply(_1ae,ta);
};
};
dojo.lang.anonCtr=0;
dojo.lang.anon={};
dojo.lang.nameAnonFunc=function(_1b5,_1b6,_1b7){
var nso=(_1b6||dojo.lang.anon);
if((_1b7)||((dj_global["djConfig"])&&(djConfig["slowAnonFuncLookups"]==true))){
for(var x in nso){
try{
if(nso[x]===_1b5){
return x;
}
}
catch(e){
}
}
}
var ret="__"+dojo.lang.anonCtr++;
while(typeof nso[ret]!="undefined"){
ret="__"+dojo.lang.anonCtr++;
}
nso[ret]=_1b5;
return ret;
};
dojo.lang.forward=function(_1bb){
return function(){
return this[_1bb].apply(this,arguments);
};
};
dojo.lang.curry=function(_1bc,func){
var _1be=[];
_1bc=_1bc||dj_global;
if(dojo.lang.isString(func)){
func=_1bc[func];
}
for(var x=2;x<arguments.length;x++){
_1be.push(arguments[x]);
}
var _1c0=(func["__preJoinArity"]||func.length)-_1be.length;
function gather(_1c1,_1c2,_1c3){
var _1c4=_1c3;
var _1c5=_1c2.slice(0);
for(var x=0;x<_1c1.length;x++){
_1c5.push(_1c1[x]);
}
_1c3=_1c3-_1c1.length;
if(_1c3<=0){
var res=func.apply(_1bc,_1c5);
_1c3=_1c4;
return res;
}else{
return function(){
return gather(arguments,_1c5,_1c3);
};
}
}
return gather([],_1be,_1c0);
};
dojo.lang.curryArguments=function(_1c8,func,args,_1cb){
var _1cc=[];
var x=_1cb||0;
for(x=_1cb;x<args.length;x++){
_1cc.push(args[x]);
}
return dojo.lang.curry.apply(dojo.lang,[_1c8,func].concat(_1cc));
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
dojo.lang.delayThese=function(farr,cb,_1d2,_1d3){
if(!farr.length){
if(typeof _1d3=="function"){
_1d3();
}
return;
}
if((typeof _1d2=="undefined")&&(typeof cb=="number")){
_1d2=cb;
cb=function(){
};
}else{
if(!cb){
cb=function(){
};
if(!_1d2){
_1d2=0;
}
}
}
setTimeout(function(){
(farr.shift())();
cb();
dojo.lang.delayThese(farr,cb,_1d2,_1d3);
},_1d2);
};
dojo.provide("dojo.string.extras");
dojo.string.substituteParams=function(_1d4,hash){
var map=(typeof hash=="object")?hash:dojo.lang.toArray(arguments,1);
return _1d4.replace(/\%\{(\w+)\}/g,function(_1d7,key){
if(typeof (map[key])!="undefined"&&map[key]!=null){
return map[key];
}
dojo.raise("Substitution not found: "+key);
});
};
dojo.string.capitalize=function(str){
if(!dojo.lang.isString(str)){
return "";
}
if(arguments.length==0){
str=this;
}
var _1da=str.split(" ");
for(var i=0;i<_1da.length;i++){
_1da[i]=_1da[i].charAt(0).toUpperCase()+_1da[i].substring(1);
}
return _1da.join(" ");
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
var _1df=escape(str);
var _1e0,re=/%u([0-9A-F]{4})/i;
while((_1e0=_1df.match(re))){
var num=Number("0x"+_1e0[1]);
var _1e3=escape("&#"+num+";");
ret+=_1df.substring(0,_1e0.index)+_1e3;
_1df=_1df.substring(_1e0.index+_1e0[0].length);
}
ret+=_1df.replace(/\+/g,"%2B");
return ret;
};
dojo.string.escape=function(type,str){
var args=dojo.lang.toArray(arguments,1);
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
dojo.string.escapeXml=function(str,_1e8){
str=str.replace(/&/gm,"&amp;").replace(/</gm,"&lt;").replace(/>/gm,"&gt;").replace(/"/gm,"&quot;");
if(!_1e8){
str=str.replace(/'/gm,"&#39;");
}
return str;
};
dojo.string.escapeSql=function(str){
return str.replace(/'/gm,"''");
};
dojo.string.escapeRegExp=function(str){
return str.replace(/\\/gm,"\\\\").replace(/([\f\b\n\t\r[\^$|?*+(){}])/gm,"\\$1");
};
dojo.string.escapeJavaScript=function(str){
return str.replace(/(["'\f\b\n\t\r])/gm,"\\$1");
};
dojo.string.escapeString=function(str){
return ("\""+str.replace(/(["\\])/g,"\\$1")+"\"").replace(/[\f]/g,"\\f").replace(/[\b]/g,"\\b").replace(/[\n]/g,"\\n").replace(/[\t]/g,"\\t").replace(/[\r]/g,"\\r");
};
dojo.string.summary=function(str,len){
if(!len||str.length<=len){
return str;
}
return str.substring(0,len).replace(/\.+$/,"")+"...";
};
dojo.string.endsWith=function(str,end,_1f1){
if(_1f1){
str=str.toLowerCase();
end=end.toLowerCase();
}
if((str.length-end.length)<0){
return false;
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
dojo.string.startsWith=function(str,_1f5,_1f6){
if(_1f6){
str=str.toLowerCase();
_1f5=_1f5.toLowerCase();
}
return str.indexOf(_1f5)==0;
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
if(str.indexOf(arguments[i])>-1){
return true;
}
}
return false;
};
dojo.string.normalizeNewlines=function(text,_1fc){
if(_1fc=="\n"){
text=text.replace(/\r\n/g,"\n");
text=text.replace(/\r/g,"\n");
}else{
if(_1fc=="\r"){
text=text.replace(/\r\n/g,"\r");
text=text.replace(/\n/g,"\r");
}else{
text=text.replace(/([^\r])\n/g,"$1\r\n").replace(/\r([^\n])/g,"\r\n$1");
}
}
return text;
};
dojo.string.splitEscaped=function(str,_1fe){
var _1ff=[];
for(var i=0,_201=0;i<str.length;i++){
if(str.charAt(i)=="\\"){
i++;
continue;
}
if(str.charAt(i)==_1fe){
_1ff.push(str.substring(_201,i));
_201=i+1;
}
}
_1ff.push(str.substr(_201));
return _1ff;
};
dojo.provide("dojo.dom");
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
dojo.dom.isNode=function(wh){
if(typeof Element=="function"){
try{
return wh instanceof Element;
}
catch(e){
}
}else{
return wh&&!isNaN(wh.nodeType);
}
};
dojo.dom.getUniqueId=function(){
var _203=dojo.doc();
do{
var id="dj_unique_"+(++arguments.callee._idIncrement);
}while(_203.getElementById(id));
return id;
};
dojo.dom.getUniqueId._idIncrement=0;
dojo.dom.firstElement=dojo.dom.getFirstChildElement=function(_205,_206){
var node=_205.firstChild;
while(node&&node.nodeType!=dojo.dom.ELEMENT_NODE){
node=node.nextSibling;
}
if(_206&&node&&node.tagName&&node.tagName.toLowerCase()!=_206.toLowerCase()){
node=dojo.dom.nextElement(node,_206);
}
return node;
};
dojo.dom.lastElement=dojo.dom.getLastChildElement=function(_208,_209){
var node=_208.lastChild;
while(node&&node.nodeType!=dojo.dom.ELEMENT_NODE){
node=node.previousSibling;
}
if(_209&&node&&node.tagName&&node.tagName.toLowerCase()!=_209.toLowerCase()){
node=dojo.dom.prevElement(node,_209);
}
return node;
};
dojo.dom.nextElement=dojo.dom.getNextSiblingElement=function(node,_20c){
if(!node){
return null;
}
do{
node=node.nextSibling;
}while(node&&node.nodeType!=dojo.dom.ELEMENT_NODE);
if(node&&_20c&&_20c.toLowerCase()!=node.tagName.toLowerCase()){
return dojo.dom.nextElement(node,_20c);
}
return node;
};
dojo.dom.prevElement=dojo.dom.getPreviousSiblingElement=function(node,_20e){
if(!node){
return null;
}
if(_20e){
_20e=_20e.toLowerCase();
}
do{
node=node.previousSibling;
}while(node&&node.nodeType!=dojo.dom.ELEMENT_NODE);
if(node&&_20e&&_20e.toLowerCase()!=node.tagName.toLowerCase()){
return dojo.dom.prevElement(node,_20e);
}
return node;
};
dojo.dom.moveChildren=function(_20f,_210,trim){
var _212=0;
if(trim){
while(_20f.hasChildNodes()&&_20f.firstChild.nodeType==dojo.dom.TEXT_NODE){
_20f.removeChild(_20f.firstChild);
}
while(_20f.hasChildNodes()&&_20f.lastChild.nodeType==dojo.dom.TEXT_NODE){
_20f.removeChild(_20f.lastChild);
}
}
while(_20f.hasChildNodes()){
_210.appendChild(_20f.firstChild);
_212++;
}
return _212;
};
dojo.dom.copyChildren=function(_213,_214,trim){
var _216=_213.cloneNode(true);
return this.moveChildren(_216,_214,trim);
};
dojo.dom.replaceChildren=function(node,_218){
var _219=[];
if(dojo.render.html.ie){
for(var i=0;i<node.childNodes.length;i++){
_219.push(node.childNodes[i]);
}
}
dojo.dom.removeChildren(node);
node.appendChild(_218);
for(var i=0;i<_219.length;i++){
dojo.dom.destroyNode(_219[i]);
}
};
dojo.dom.removeChildren=function(node){
var _21c=node.childNodes.length;
while(node.hasChildNodes()){
dojo.dom.removeNode(node.firstChild);
}
return _21c;
};
dojo.dom.replaceNode=function(node,_21e){
return node.parentNode.replaceChild(_21e,node);
};
dojo.dom.destroyNode=function(node){
if(node.parentNode){
node=dojo.dom.removeNode(node);
}
if(node.nodeType!=3){
if(dojo.evalObjPath("dojo.event.browser.clean",false)){
dojo.event.browser.clean(node);
}
if(dojo.render.html.ie){
node.outerHTML="";
}
}
};
dojo.dom.removeNode=function(node){
if(node&&node.parentNode){
return node.parentNode.removeChild(node);
}
};
dojo.dom.getAncestors=function(node,_222,_223){
var _224=[];
var _225=(_222&&(_222 instanceof Function||typeof _222=="function"));
while(node){
if(!_225||_222(node)){
_224.push(node);
}
if(_223&&_224.length>0){
return _224[0];
}
node=node.parentNode;
}
if(_223){
return null;
}
return _224;
};
dojo.dom.getAncestorsByTag=function(node,tag,_228){
tag=tag.toLowerCase();
return dojo.dom.getAncestors(node,function(el){
return ((el.tagName)&&(el.tagName.toLowerCase()==tag));
},_228);
};
dojo.dom.getFirstAncestorByTag=function(node,tag){
return dojo.dom.getAncestorsByTag(node,tag,true);
};
dojo.dom.isDescendantOf=function(node,_22d,_22e){
if(_22e&&node){
node=node.parentNode;
}
while(node){
if(node==_22d){
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
if(node.xml){
return node.xml;
}else{
if(typeof XMLSerializer!="undefined"){
return (new XMLSerializer()).serializeToString(node);
}
}
}
};
dojo.dom.createDocument=function(){
var doc=null;
var _231=dojo.doc();
if(!dj_undef("ActiveXObject")){
var _232=["MSXML2","Microsoft","MSXML","MSXML3"];
for(var i=0;i<_232.length;i++){
try{
doc=new ActiveXObject(_232[i]+".XMLDOM");
}
catch(e){
}
if(doc){
break;
}
}
}else{
if((_231.implementation)&&(_231.implementation.createDocument)){
doc=_231.implementation.createDocument("","",null);
}
}
return doc;
};
dojo.dom.createDocumentFromText=function(str,_235){
if(!_235){
_235="text/xml";
}
if(!dj_undef("DOMParser")){
var _236=new DOMParser();
return _236.parseFromString(str,_235);
}else{
if(!dj_undef("ActiveXObject")){
var _237=dojo.dom.createDocument();
if(_237){
_237.async=false;
_237.loadXML(str);
return _237;
}else{
dojo.debug("toXml didn't work?");
}
}else{
var _238=dojo.doc();
if(_238.createElement){
var tmp=_238.createElement("xml");
tmp.innerHTML=str;
if(_238.implementation&&_238.implementation.createDocument){
var _23a=_238.implementation.createDocument("foo","",null);
for(var i=0;i<tmp.childNodes.length;i++){
_23a.importNode(tmp.childNodes.item(i),true);
}
return _23a;
}
return ((tmp.document)&&(tmp.document.firstChild?tmp.document.firstChild:tmp));
}
}
}
return null;
};
dojo.dom.prependChild=function(node,_23d){
if(_23d.firstChild){
_23d.insertBefore(node,_23d.firstChild);
}else{
_23d.appendChild(node);
}
return true;
};
dojo.dom.insertBefore=function(node,ref,_240){
if((_240!=true)&&(node===ref||node.nextSibling===ref)){
return false;
}
var _241=ref.parentNode;
_241.insertBefore(node,ref);
return true;
};
dojo.dom.insertAfter=function(node,ref,_244){
var pn=ref.parentNode;
if(ref==pn.lastChild){
if((_244!=true)&&(node===ref)){
return false;
}
pn.appendChild(node);
}else{
return this.insertBefore(node,ref.nextSibling,_244);
}
return true;
};
dojo.dom.insertAtPosition=function(node,ref,_248){
if((!node)||(!ref)||(!_248)){
return false;
}
switch(_248.toLowerCase()){
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
dojo.dom.insertAtIndex=function(node,_24a,_24b){
var _24c=_24a.childNodes;
if(!_24c.length||_24c.length==_24b){
_24a.appendChild(node);
return true;
}
if(_24b==0){
return dojo.dom.prependChild(node,_24a);
}
return dojo.dom.insertAfter(node,_24c[_24b-1]);
};
dojo.dom.textContent=function(node,text){
if(arguments.length>1){
var _24f=dojo.doc();
dojo.dom.replaceChildren(node,_24f.createTextNode(text));
return text;
}else{
if(node.textContent!=undefined){
return node.textContent;
}
var _250="";
if(node==null){
return _250;
}
for(var i=0;i<node.childNodes.length;i++){
switch(node.childNodes[i].nodeType){
case 1:
case 5:
_250+=dojo.dom.textContent(node.childNodes[i]);
break;
case 3:
case 2:
case 4:
_250+=node.childNodes[i].nodeValue;
break;
default:
break;
}
}
return _250;
}
};
dojo.dom.hasParent=function(node){
return Boolean(node&&node.parentNode&&dojo.dom.isNode(node.parentNode));
};
dojo.dom.isTag=function(node){
if(node&&node.tagName){
for(var i=1;i<arguments.length;i++){
if(node.tagName==String(arguments[i])){
return String(arguments[i]);
}
}
}
return "";
};
dojo.dom.setAttributeNS=function(elem,_256,_257,_258){
if(elem==null||((elem==undefined)&&(typeof elem=="undefined"))){
dojo.raise("No element given to dojo.dom.setAttributeNS");
}
if(!((elem.setAttributeNS==undefined)&&(typeof elem.setAttributeNS=="undefined"))){
elem.setAttributeNS(_256,_257,_258);
}else{
var _259=elem.ownerDocument;
var _25a=_259.createNode(2,_257,_256);
_25a.nodeValue=_258;
elem.setAttributeNode(_25a);
}
};
dojo.provide("dojo.undo.browser");
try{
if((!djConfig["preventBackButtonFix"])&&(!dojo.hostenv.post_load_)){
document.write("<iframe style='border: 0px; width: 1px; height: 1px; position: absolute; bottom: 0px; right: 0px; visibility: visible;' name='djhistory' id='djhistory' src='"+(djConfig["dojoIframeHistoryUrl"]||dojo.hostenv.getBaseScriptUri()+"iframe_history.html")+"'></iframe>");
}
}
catch(e){
}
if(dojo.render.html.opera){
dojo.debug("Opera is not supported with dojo.undo.browser, so back/forward detection will not work.");
}
dojo.undo.browser={initialHref:(!dj_undef("window"))?window.location.href:"",initialHash:(!dj_undef("window"))?window.location.hash:"",moveForward:false,historyStack:[],forwardStack:[],historyIframe:null,bookmarkAnchor:null,locationTimer:null,setInitialState:function(args){
this.initialState=this._createState(this.initialHref,args,this.initialHash);
},addToHistory:function(args){
this.forwardStack=[];
var hash=null;
var url=null;
if(!this.historyIframe){
if(djConfig["useXDomain"]&&!djConfig["dojoIframeHistoryUrl"]){
dojo.debug("dojo.undo.browser: When using cross-domain Dojo builds,"+" please save iframe_history.html to your domain and set djConfig.dojoIframeHistoryUrl"+" to the path on your domain to iframe_history.html");
}
this.historyIframe=window.frames["djhistory"];
}
if(!this.bookmarkAnchor){
this.bookmarkAnchor=document.createElement("a");
dojo.body().appendChild(this.bookmarkAnchor);
this.bookmarkAnchor.style.display="none";
}
if(args["changeUrl"]){
hash="#"+((args["changeUrl"]!==true)?args["changeUrl"]:(new Date()).getTime());
if(this.historyStack.length==0&&this.initialState.urlHash==hash){
this.initialState=this._createState(url,args,hash);
return;
}else{
if(this.historyStack.length>0&&this.historyStack[this.historyStack.length-1].urlHash==hash){
this.historyStack[this.historyStack.length-1]=this._createState(url,args,hash);
return;
}
}
this.changingUrl=true;
setTimeout("window.location.href = '"+hash+"'; dojo.undo.browser.changingUrl = false;",1);
this.bookmarkAnchor.href=hash;
if(dojo.render.html.ie){
url=this._loadIframeHistory();
var _25f=args["back"]||args["backButton"]||args["handle"];
var tcb=function(_261){
if(window.location.hash!=""){
setTimeout("window.location.href = '"+hash+"';",1);
}
_25f.apply(this,[_261]);
};
if(args["back"]){
args.back=tcb;
}else{
if(args["backButton"]){
args.backButton=tcb;
}else{
if(args["handle"]){
args.handle=tcb;
}
}
}
var _262=args["forward"]||args["forwardButton"]||args["handle"];
var tfw=function(_264){
if(window.location.hash!=""){
window.location.href=hash;
}
if(_262){
_262.apply(this,[_264]);
}
};
if(args["forward"]){
args.forward=tfw;
}else{
if(args["forwardButton"]){
args.forwardButton=tfw;
}else{
if(args["handle"]){
args.handle=tfw;
}
}
}
}else{
if(dojo.render.html.moz){
if(!this.locationTimer){
this.locationTimer=setInterval("dojo.undo.browser.checkLocation();",200);
}
}
}
}else{
url=this._loadIframeHistory();
}
this.historyStack.push(this._createState(url,args,hash));
},checkLocation:function(){
if(!this.changingUrl){
var hsl=this.historyStack.length;
if((window.location.hash==this.initialHash||window.location.href==this.initialHref)&&(hsl==1)){
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
}
},iframeLoaded:function(evt,_267){
if(!dojo.render.html.opera){
var _268=this._getUrlQuery(_267.href);
if(_268==null){
if(this.historyStack.length==1){
this.handleBackButton();
}
return;
}
if(this.moveForward){
this.moveForward=false;
return;
}
if(this.historyStack.length>=2&&_268==this._getUrlQuery(this.historyStack[this.historyStack.length-2].url)){
this.handleBackButton();
}else{
if(this.forwardStack.length>0&&_268==this._getUrlQuery(this.forwardStack[this.forwardStack.length-1].url)){
this.handleForwardButton();
}
}
}
},handleBackButton:function(){
var _269=this.historyStack.pop();
if(!_269){
return;
}
var last=this.historyStack[this.historyStack.length-1];
if(!last&&this.historyStack.length==0){
last=this.initialState;
}
if(last){
if(last.kwArgs["back"]){
last.kwArgs["back"]();
}else{
if(last.kwArgs["backButton"]){
last.kwArgs["backButton"]();
}else{
if(last.kwArgs["handle"]){
last.kwArgs.handle("back");
}
}
}
}
this.forwardStack.push(_269);
},handleForwardButton:function(){
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
},_createState:function(url,args,hash){
return {"url":url,"kwArgs":args,"urlHash":hash};
},_getUrlQuery:function(url){
var _270=url.split("?");
if(_270.length<2){
return null;
}else{
return _270[1];
}
},_loadIframeHistory:function(){
var url=(djConfig["dojoIframeHistoryUrl"]||dojo.hostenv.getBaseScriptUri()+"iframe_history.html")+"?"+(new Date()).getTime();
this.moveForward=true;
dojo.io.setIFrameSrc(this.historyIframe,url,false);
return url;
}};
dojo.provide("dojo.io.BrowserIO");
if(!dj_undef("window")){
dojo.io.checkChildrenForFile=function(node){
var _273=false;
var _274=node.getElementsByTagName("input");
dojo.lang.forEach(_274,function(_275){
if(_273){
return;
}
if(_275.getAttribute("type")=="file"){
_273=true;
}
});
return _273;
};
dojo.io.formHasFile=function(_276){
return dojo.io.checkChildrenForFile(_276);
};
dojo.io.updateNode=function(node,_278){
node=dojo.byId(node);
var args=_278;
if(dojo.lang.isString(_278)){
args={url:_278};
}
args.mimetype="text/html";
args.load=function(t,d,e){
while(node.firstChild){
dojo.dom.destroyNode(node.firstChild);
}
node.innerHTML=d;
};
dojo.io.bind(args);
};
dojo.io.formFilter=function(node){
var type=(node.type||"").toLowerCase();
return !node.disabled&&node.name&&!dojo.lang.inArray(["file","submit","image","reset","button"],type);
};
dojo.io.encodeForm=function(_27f,_280,_281){
if((!_27f)||(!_27f.tagName)||(!_27f.tagName.toLowerCase()=="form")){
dojo.raise("Attempted to encode a non-form element.");
}
if(!_281){
_281=dojo.io.formFilter;
}
var enc=/utf/i.test(_280||"")?encodeURIComponent:dojo.string.encodeAscii;
var _283=[];
for(var i=0;i<_27f.elements.length;i++){
var elm=_27f.elements[i];
if(!elm||elm.tagName.toLowerCase()=="fieldset"||!_281(elm)){
continue;
}
var name=enc(elm.name);
var type=elm.type.toLowerCase();
if(type=="select-multiple"){
for(var j=0;j<elm.options.length;j++){
if(elm.options[j].selected){
_283.push(name+"="+enc(elm.options[j].value));
}
}
}else{
if(dojo.lang.inArray(["radio","checkbox"],type)){
if(elm.checked){
_283.push(name+"="+enc(elm.value));
}
}else{
_283.push(name+"="+enc(elm.value));
}
}
}
var _289=_27f.getElementsByTagName("input");
for(var i=0;i<_289.length;i++){
var _28a=_289[i];
if(_28a.type.toLowerCase()=="image"&&_28a.form==_27f&&_281(_28a)){
var name=enc(_28a.name);
_283.push(name+"="+enc(_28a.value));
_283.push(name+".x=0");
_283.push(name+".y=0");
}
}
return _283.join("&")+"&";
};
dojo.io.FormBind=function(args){
this.bindArgs={};
if(args&&args.formNode){
this.init(args);
}else{
if(args){
this.init({formNode:args});
}
}
};
dojo.lang.extend(dojo.io.FormBind,{form:null,bindArgs:null,clickedButton:null,init:function(args){
var form=dojo.byId(args.formNode);
if(!form||!form.tagName||form.tagName.toLowerCase()!="form"){
throw new Error("FormBind: Couldn't apply, invalid form");
}else{
if(this.form==form){
return;
}else{
if(this.form){
throw new Error("FormBind: Already applied to a form");
}
}
}
dojo.lang.mixin(this.bindArgs,args);
this.form=form;
this.connect(form,"onsubmit","submit");
for(var i=0;i<form.elements.length;i++){
var node=form.elements[i];
if(node&&node.type&&dojo.lang.inArray(["submit","button"],node.type.toLowerCase())){
this.connect(node,"onclick","click");
}
}
var _290=form.getElementsByTagName("input");
for(var i=0;i<_290.length;i++){
var _291=_290[i];
if(_291.type.toLowerCase()=="image"&&_291.form==form){
this.connect(_291,"onclick","click");
}
}
},onSubmit:function(form){
return true;
},submit:function(e){
e.preventDefault();
if(this.onSubmit(this.form)){
dojo.io.bind(dojo.lang.mixin(this.bindArgs,{formFilter:dojo.lang.hitch(this,"formFilter")}));
}
},click:function(e){
var node=e.currentTarget;
if(node.disabled){
return;
}
this.clickedButton=node;
},formFilter:function(node){
var type=(node.type||"").toLowerCase();
var _298=false;
if(node.disabled||!node.name){
_298=false;
}else{
if(dojo.lang.inArray(["submit","button","image"],type)){
if(!this.clickedButton){
this.clickedButton=node;
}
_298=node==this.clickedButton;
}else{
_298=!dojo.lang.inArray(["file","submit","reset","button"],type);
}
}
return _298;
},connect:function(_299,_29a,_29b){
if(dojo.evalObjPath("dojo.event.connect")){
dojo.event.connect(_299,_29a,this,_29b);
}else{
var fcn=dojo.lang.hitch(this,_29b);
_299[_29a]=function(e){
if(!e){
e=window.event;
}
if(!e.currentTarget){
e.currentTarget=e.srcElement;
}
if(!e.preventDefault){
e.preventDefault=function(){
window.event.returnValue=false;
};
}
fcn(e);
};
}
}});
dojo.io.XMLHTTPTransport=new function(){
var _29e=this;
var _29f={};
this.useCache=false;
this.preventCache=false;
function getCacheKey(url,_2a1,_2a2){
return url+"|"+_2a1+"|"+_2a2.toLowerCase();
}
function addToCache(url,_2a4,_2a5,http){
_29f[getCacheKey(url,_2a4,_2a5)]=http;
}
function getFromCache(url,_2a8,_2a9){
return _29f[getCacheKey(url,_2a8,_2a9)];
}
this.clearCache=function(){
_29f={};
};
function doLoad(_2aa,http,url,_2ad,_2ae){
if(((http.status>=200)&&(http.status<300))||(http.status==304)||(http.status==1223)||(location.protocol=="file:"&&(http.status==0||http.status==undefined))||(location.protocol=="chrome:"&&(http.status==0||http.status==undefined))){
var ret;
if(_2aa.method.toLowerCase()=="head"){
var _2b0=http.getAllResponseHeaders();
ret={};
ret.toString=function(){
return _2b0;
};
var _2b1=_2b0.split(/[\r\n]+/g);
for(var i=0;i<_2b1.length;i++){
var pair=_2b1[i].match(/^([^:]+)\s*:\s*(.+)$/i);
if(pair){
ret[pair[1]]=pair[2];
}
}
}else{
if(_2aa.mimetype=="text/javascript"){
try{
ret=dj_eval(http.responseText);
}
catch(e){
dojo.debug(e);
dojo.debug(http.responseText);
ret=null;
}
}else{
if(_2aa.mimetype.substr(0,9)=="text/json"||_2aa.mimetype.substr(0,16)=="application/json"){
try{
ret=dj_eval("("+_2aa.jsonFilter(http.responseText)+")");
}
catch(e){
dojo.debug(e);
dojo.debug(http.responseText);
ret=false;
}
}else{
if((_2aa.mimetype=="application/xml")||(_2aa.mimetype=="text/xml")){
ret=http.responseXML;
if(!ret||typeof ret=="string"||!http.getResponseHeader("Content-Type")){
ret=dojo.dom.createDocumentFromText(http.responseText);
}
}else{
ret=http.responseText;
}
}
}
}
if(_2ae){
addToCache(url,_2ad,_2aa.method,http);
}
_2aa[(typeof _2aa.load=="function")?"load":"handle"]("load",ret,http,_2aa);
}else{
var _2b4=new dojo.io.Error("XMLHttpTransport Error: "+http.status+" "+http.statusText);
_2aa[(typeof _2aa.error=="function")?"error":"handle"]("error",_2b4,http,_2aa);
}
}
function setHeaders(http,_2b6){
if(_2b6["headers"]){
for(var _2b7 in _2b6["headers"]){
if(_2b7.toLowerCase()=="content-type"&&!_2b6["contentType"]){
_2b6["contentType"]=_2b6["headers"][_2b7];
}else{
http.setRequestHeader(_2b7,_2b6["headers"][_2b7]);
}
}
}
}
this.inFlight=[];
this.inFlightTimer=null;
this.startWatchingInFlight=function(){
if(!this.inFlightTimer){
this.inFlightTimer=setTimeout("dojo.io.XMLHTTPTransport.watchInFlight();",10);
}
};
this.watchInFlight=function(){
var now=null;
if(!dojo.hostenv._blockAsync&&!_29e._blockAsync){
for(var x=this.inFlight.length-1;x>=0;x--){
try{
var tif=this.inFlight[x];
if(!tif||tif.http._aborted||!tif.http.readyState){
this.inFlight.splice(x,1);
continue;
}
if(4==tif.http.readyState){
this.inFlight.splice(x,1);
doLoad(tif.req,tif.http,tif.url,tif.query,tif.useCache);
}else{
if(tif.startTime){
if(!now){
now=(new Date()).getTime();
}
if(tif.startTime+(tif.req.timeoutSeconds*1000)<now){
if(typeof tif.http.abort=="function"){
tif.http.abort();
}
this.inFlight.splice(x,1);
tif.req[(typeof tif.req.timeout=="function")?"timeout":"handle"]("timeout",null,tif.http,tif.req);
}
}
}
}
catch(e){
try{
var _2bb=new dojo.io.Error("XMLHttpTransport.watchInFlight Error: "+e);
tif.req[(typeof tif.req.error=="function")?"error":"handle"]("error",_2bb,tif.http,tif.req);
}
catch(e2){
dojo.debug("XMLHttpTransport error callback failed: "+e2);
}
}
}
}
clearTimeout(this.inFlightTimer);
if(this.inFlight.length==0){
this.inFlightTimer=null;
return;
}
this.inFlightTimer=setTimeout("dojo.io.XMLHTTPTransport.watchInFlight();",10);
};
var _2bc=dojo.hostenv.getXmlhttpObject()?true:false;
this.canHandle=function(_2bd){
var mlc=_2bd["mimetype"].toLowerCase()||"";
return _2bc&&((dojo.lang.inArray(["text/plain","text/html","application/xml","text/xml","text/javascript"],mlc))||(mlc.substr(0,9)=="text/json"||mlc.substr(0,16)=="application/json"))&&!(_2bd["formNode"]&&dojo.io.formHasFile(_2bd["formNode"]));
};
this.multipartBoundary="45309FFF-BD65-4d50-99C9-36986896A96F";
this.bind=function(_2bf){
if(!_2bf["url"]){
if(!_2bf["formNode"]&&(_2bf["backButton"]||_2bf["back"]||_2bf["changeUrl"]||_2bf["watchForURL"])&&(!djConfig.preventBackButtonFix)){
dojo.deprecated("Using dojo.io.XMLHTTPTransport.bind() to add to browser history without doing an IO request","Use dojo.undo.browser.addToHistory() instead.","0.4");
dojo.undo.browser.addToHistory(_2bf);
return true;
}
}
var url=_2bf.url;
var _2c1="";
if(_2bf["formNode"]){
var ta=_2bf.formNode.getAttribute("action");
if((ta)&&(!_2bf["url"])){
url=ta;
}
var tp=_2bf.formNode.getAttribute("method");
if((tp)&&(!_2bf["method"])){
_2bf.method=tp;
}
_2c1+=dojo.io.encodeForm(_2bf.formNode,_2bf.encoding,_2bf["formFilter"]);
}
if(url.indexOf("#")>-1){
dojo.debug("Warning: dojo.io.bind: stripping hash values from url:",url);
url=url.split("#")[0];
}
if(_2bf["file"]){
_2bf.method="post";
}
if(!_2bf["method"]){
_2bf.method="get";
}
if(_2bf.method.toLowerCase()=="get"){
_2bf.multipart=false;
}else{
if(_2bf["file"]){
_2bf.multipart=true;
}else{
if(!_2bf["multipart"]){
_2bf.multipart=false;
}
}
}
if(_2bf["backButton"]||_2bf["back"]||_2bf["changeUrl"]){
dojo.undo.browser.addToHistory(_2bf);
}
var _2c4=_2bf["content"]||{};
if(_2bf.sendTransport){
_2c4["dojo.transport"]="xmlhttp";
}
do{
if(_2bf.postContent){
_2c1=_2bf.postContent;
break;
}
if(_2c4){
_2c1+=dojo.io.argsFromMap(_2c4,_2bf.encoding);
}
if(_2bf.method.toLowerCase()=="get"||!_2bf.multipart){
break;
}
var t=[];
if(_2c1.length){
var q=_2c1.split("&");
for(var i=0;i<q.length;++i){
if(q[i].length){
var p=q[i].split("=");
t.push("--"+this.multipartBoundary,"Content-Disposition: form-data; name=\""+p[0]+"\"","",p[1]);
}
}
}
if(_2bf.file){
if(dojo.lang.isArray(_2bf.file)){
for(var i=0;i<_2bf.file.length;++i){
var o=_2bf.file[i];
t.push("--"+this.multipartBoundary,"Content-Disposition: form-data; name=\""+o.name+"\"; filename=\""+("fileName" in o?o.fileName:o.name)+"\"","Content-Type: "+("contentType" in o?o.contentType:"application/octet-stream"),"",o.content);
}
}else{
var o=_2bf.file;
t.push("--"+this.multipartBoundary,"Content-Disposition: form-data; name=\""+o.name+"\"; filename=\""+("fileName" in o?o.fileName:o.name)+"\"","Content-Type: "+("contentType" in o?o.contentType:"application/octet-stream"),"",o.content);
}
}
if(t.length){
t.push("--"+this.multipartBoundary+"--","");
_2c1=t.join("\r\n");
}
}while(false);
var _2ca=_2bf["sync"]?false:true;
var _2cb=_2bf["preventCache"]||(this.preventCache==true&&_2bf["preventCache"]!=false);
var _2cc=_2bf["useCache"]==true||(this.useCache==true&&_2bf["useCache"]!=false);
if(!_2cb&&_2cc){
var _2cd=getFromCache(url,_2c1,_2bf.method);
if(_2cd){
doLoad(_2bf,_2cd,url,_2c1,false);
return;
}
}
var http=dojo.hostenv.getXmlhttpObject(_2bf);
var _2cf=false;
if(_2ca){
var _2d0=this.inFlight.push({"req":_2bf,"http":http,"url":url,"query":_2c1,"useCache":_2cc,"startTime":_2bf.timeoutSeconds?(new Date()).getTime():0});
this.startWatchingInFlight();
}else{
_29e._blockAsync=true;
}
if(_2bf.method.toLowerCase()=="post"){
if(!_2bf.user){
http.open("POST",url,_2ca);
}else{
http.open("POST",url,_2ca,_2bf.user,_2bf.password);
}
setHeaders(http,_2bf);
http.setRequestHeader("Content-Type",_2bf.multipart?("multipart/form-data; boundary="+this.multipartBoundary):(_2bf.contentType||"application/x-www-form-urlencoded"));
try{
http.send(_2c1);
}
catch(e){
if(typeof http.abort=="function"){
http.abort();
}
doLoad(_2bf,{status:404},url,_2c1,_2cc);
}
}else{
var _2d1=url;
if(_2c1!=""){
_2d1+=(_2d1.indexOf("?")>-1?"&":"?")+_2c1;
}
if(_2cb){
_2d1+=(dojo.string.endsWithAny(_2d1,"?","&")?"":(_2d1.indexOf("?")>-1?"&":"?"))+"dojo.preventCache="+new Date().valueOf();
}
if(!_2bf.user){
http.open(_2bf.method.toUpperCase(),_2d1,_2ca);
}else{
http.open(_2bf.method.toUpperCase(),_2d1,_2ca,_2bf.user,_2bf.password);
}
setHeaders(http,_2bf);
try{
http.send(null);
}
catch(e){
if(typeof http.abort=="function"){
http.abort();
}
doLoad(_2bf,{status:404},url,_2c1,_2cc);
}
}
if(!_2ca){
doLoad(_2bf,http,url,_2c1,_2cc);
_29e._blockAsync=false;
}
_2bf.abort=function(){
try{
http._aborted=true;
}
catch(e){
}
return http.abort();
};
return;
};
dojo.io.transports.addTransport("XMLHTTPTransport");
};
}
dojo.provide("dojo.io.cookie");
dojo.io.cookie.setCookie=function(name,_2d3,days,path,_2d6,_2d7){
var _2d8=-1;
if((typeof days=="number")&&(days>=0)){
var d=new Date();
d.setTime(d.getTime()+(days*24*60*60*1000));
_2d8=d.toGMTString();
}
_2d3=escape(_2d3);
document.cookie=name+"="+_2d3+";"+(_2d8!=-1?" expires="+_2d8+";":"")+(path?"path="+path:"")+(_2d6?"; domain="+_2d6:"")+(_2d7?"; secure":"");
};
dojo.io.cookie.set=dojo.io.cookie.setCookie;
dojo.io.cookie.getCookie=function(name){
var idx=document.cookie.lastIndexOf(name+"=");
if(idx==-1){
return null;
}
var _2dc=document.cookie.substring(idx+name.length+1);
var end=_2dc.indexOf(";");
if(end==-1){
end=_2dc.length;
}
_2dc=_2dc.substring(0,end);
_2dc=unescape(_2dc);
return _2dc;
};
dojo.io.cookie.get=dojo.io.cookie.getCookie;
dojo.io.cookie.deleteCookie=function(name){
dojo.io.cookie.setCookie(name,"-",0);
};
dojo.io.cookie.setObjectCookie=function(name,obj,days,path,_2e3,_2e4,_2e5){
if(arguments.length==5){
_2e5=_2e3;
_2e3=null;
_2e4=null;
}
var _2e6=[],_2e7,_2e8="";
if(!_2e5){
_2e7=dojo.io.cookie.getObjectCookie(name);
}
if(days>=0){
if(!_2e7){
_2e7={};
}
for(var prop in obj){
if(obj[prop]==null){
delete _2e7[prop];
}else{
if((typeof obj[prop]=="string")||(typeof obj[prop]=="number")){
_2e7[prop]=obj[prop];
}
}
}
prop=null;
for(var prop in _2e7){
_2e6.push(escape(prop)+"="+escape(_2e7[prop]));
}
_2e8=_2e6.join("&");
}
dojo.io.cookie.setCookie(name,_2e8,days,path,_2e3,_2e4);
};
dojo.io.cookie.getObjectCookie=function(name){
var _2eb=null,_2ec=dojo.io.cookie.getCookie(name);
if(_2ec){
_2eb={};
var _2ed=_2ec.split("&");
for(var i=0;i<_2ed.length;i++){
var pair=_2ed[i].split("=");
var _2f0=pair[1];
if(isNaN(_2f0)){
_2f0=unescape(pair[1]);
}
_2eb[unescape(pair[0])]=_2f0;
}
}
return _2eb;
};
dojo.io.cookie.isSupported=function(){
if(typeof navigator.cookieEnabled!="boolean"){
dojo.io.cookie.setCookie("__TestingYourBrowserForCookieSupport__","CookiesAllowed",90,null);
var _2f1=dojo.io.cookie.getCookie("__TestingYourBrowserForCookieSupport__");
navigator.cookieEnabled=(_2f1=="CookiesAllowed");
if(navigator.cookieEnabled){
this.deleteCookie("__TestingYourBrowserForCookieSupport__");
}
}
return navigator.cookieEnabled;
};
if(!dojo.io.cookies){
dojo.io.cookies=dojo.io.cookie;
}
dojo.kwCompoundRequire({common:["dojo.io.common"],rhino:["dojo.io.RhinoIO"],browser:["dojo.io.BrowserIO","dojo.io.cookie"],dashboard:["dojo.io.BrowserIO","dojo.io.cookie"]});
dojo.provide("dojo.io.*");
dojo.provide("dojo.event.common");
dojo.event=new function(){
this._canTimeout=dojo.lang.isFunction(dj_global["setTimeout"])||dojo.lang.isAlien(dj_global["setTimeout"]);
function interpolateArgs(args,_2f3){
var dl=dojo.lang;
var ao={srcObj:dj_global,srcFunc:null,adviceObj:dj_global,adviceFunc:null,aroundObj:null,aroundFunc:null,adviceType:(args.length>2)?args[0]:"after",precedence:"last",once:false,delay:null,rate:0,adviceMsg:false,maxCalls:-1};
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
if((dl.isObject(args[0]))&&(dl.isString(args[1]))&&(dl.isString(args[2]))){
ao.adviceType="after";
ao.srcObj=args[0];
ao.srcFunc=args[1];
ao.adviceFunc=args[2];
}else{
if((dl.isString(args[1]))&&(dl.isString(args[2]))){
ao.srcFunc=args[1];
ao.adviceFunc=args[2];
}else{
if((dl.isObject(args[0]))&&(dl.isString(args[1]))&&(dl.isFunction(args[2]))){
ao.adviceType="after";
ao.srcObj=args[0];
ao.srcFunc=args[1];
var _2f6=dl.nameAnonFunc(args[2],ao.adviceObj,_2f3);
ao.adviceFunc=_2f6;
}else{
if((dl.isFunction(args[0]))&&(dl.isObject(args[1]))&&(dl.isString(args[2]))){
ao.adviceType="after";
ao.srcObj=dj_global;
var _2f6=dl.nameAnonFunc(args[0],ao.srcObj,_2f3);
ao.srcFunc=_2f6;
ao.adviceObj=args[1];
ao.adviceFunc=args[2];
}
}
}
}
break;
case 4:
if((dl.isObject(args[0]))&&(dl.isObject(args[2]))){
ao.adviceType="after";
ao.srcObj=args[0];
ao.srcFunc=args[1];
ao.adviceObj=args[2];
ao.adviceFunc=args[3];
}else{
if((dl.isString(args[0]))&&(dl.isString(args[1]))&&(dl.isObject(args[2]))){
ao.adviceType=args[0];
ao.srcObj=dj_global;
ao.srcFunc=args[1];
ao.adviceObj=args[2];
ao.adviceFunc=args[3];
}else{
if((dl.isString(args[0]))&&(dl.isFunction(args[1]))&&(dl.isObject(args[2]))){
ao.adviceType=args[0];
ao.srcObj=dj_global;
var _2f6=dl.nameAnonFunc(args[1],dj_global,_2f3);
ao.srcFunc=_2f6;
ao.adviceObj=args[2];
ao.adviceFunc=args[3];
}else{
if((dl.isString(args[0]))&&(dl.isObject(args[1]))&&(dl.isString(args[2]))&&(dl.isFunction(args[3]))){
ao.srcObj=args[1];
ao.srcFunc=args[2];
var _2f6=dl.nameAnonFunc(args[3],dj_global,_2f3);
ao.adviceObj=dj_global;
ao.adviceFunc=_2f6;
}else{
if(dl.isObject(args[1])){
ao.srcObj=args[1];
ao.srcFunc=args[2];
ao.adviceObj=dj_global;
ao.adviceFunc=args[3];
}else{
if(dl.isObject(args[2])){
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
ao.maxCalls=(!isNaN(parseInt(args[11])))?args[11]:-1;
break;
}
if(dl.isFunction(ao.aroundFunc)){
var _2f6=dl.nameAnonFunc(ao.aroundFunc,ao.aroundObj,_2f3);
ao.aroundFunc=_2f6;
}
if(dl.isFunction(ao.srcFunc)){
ao.srcFunc=dl.getNameInObj(ao.srcObj,ao.srcFunc);
}
if(dl.isFunction(ao.adviceFunc)){
ao.adviceFunc=dl.getNameInObj(ao.adviceObj,ao.adviceFunc);
}
if((ao.aroundObj)&&(dl.isFunction(ao.aroundFunc))){
ao.aroundFunc=dl.getNameInObj(ao.aroundObj,ao.aroundFunc);
}
if(!ao.srcObj){
dojo.raise("bad srcObj for srcFunc: "+ao.srcFunc);
}
if(!ao.adviceObj){
dojo.raise("bad adviceObj for adviceFunc: "+ao.adviceFunc);
}
if(!ao.adviceFunc){
dojo.debug("bad adviceFunc for srcFunc: "+ao.srcFunc);
dojo.debugShallow(ao);
}
return ao;
}
this.connect=function(){
if(arguments.length==1){
var ao=arguments[0];
}else{
var ao=interpolateArgs(arguments,true);
}
if(dojo.lang.isString(ao.srcFunc)&&(ao.srcFunc.toLowerCase()=="onkey")){
if(dojo.render.html.ie){
ao.srcFunc="onkeydown";
this.connect(ao);
}
ao.srcFunc="onkeypress";
}
if(dojo.lang.isArray(ao.srcObj)&&ao.srcObj!=""){
var _2f8={};
for(var x in ao){
_2f8[x]=ao[x];
}
var mjps=[];
dojo.lang.forEach(ao.srcObj,function(src){
if((dojo.render.html.capable)&&(dojo.lang.isString(src))){
src=dojo.byId(src);
}
_2f8.srcObj=src;
mjps.push(dojo.event.connect.call(dojo.event,_2f8));
});
return mjps;
}
var mjp=dojo.event.MethodJoinPoint.getForMethod(ao.srcObj,ao.srcFunc);
if(ao.adviceFunc){
var mjp2=dojo.event.MethodJoinPoint.getForMethod(ao.adviceObj,ao.adviceFunc);
}
mjp.kwAddAdvice(ao);
return mjp;
};
this.log=function(a1,a2){
var _300;
if((arguments.length==1)&&(typeof a1=="object")){
_300=a1;
}else{
_300={srcObj:a1,srcFunc:a2};
}
_300.adviceFunc=function(){
var _301=[];
for(var x=0;x<arguments.length;x++){
_301.push(arguments[x]);
}
dojo.debug("("+_300.srcObj+")."+_300.srcFunc,":",_301.join(", "));
};
this.kwConnect(_300);
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
this.connectOnce=function(){
var ao=interpolateArgs(arguments,true);
ao.once=true;
return this.connect(ao);
};
this.connectRunOnce=function(){
var ao=interpolateArgs(arguments,true);
ao.maxCalls=1;
return this.connect(ao);
};
this._kwConnectImpl=function(_309,_30a){
var fn=(_30a)?"disconnect":"connect";
if(typeof _309["srcFunc"]=="function"){
_309.srcObj=_309["srcObj"]||dj_global;
var _30c=dojo.lang.nameAnonFunc(_309.srcFunc,_309.srcObj,true);
_309.srcFunc=_30c;
}
if(typeof _309["adviceFunc"]=="function"){
_309.adviceObj=_309["adviceObj"]||dj_global;
var _30c=dojo.lang.nameAnonFunc(_309.adviceFunc,_309.adviceObj,true);
_309.adviceFunc=_30c;
}
_309.srcObj=_309["srcObj"]||dj_global;
_309.adviceObj=_309["adviceObj"]||_309["targetObj"]||dj_global;
_309.adviceFunc=_309["adviceFunc"]||_309["targetFunc"];
return dojo.event[fn](_309);
};
this.kwConnect=function(_30d){
return this._kwConnectImpl(_30d,false);
};
this.disconnect=function(){
if(arguments.length==1){
var ao=arguments[0];
}else{
var ao=interpolateArgs(arguments,true);
}
if(!ao.adviceFunc){
return;
}
if(dojo.lang.isString(ao.srcFunc)&&(ao.srcFunc.toLowerCase()=="onkey")){
if(dojo.render.html.ie){
ao.srcFunc="onkeydown";
this.disconnect(ao);
}
ao.srcFunc="onkeypress";
}
if(!ao.srcObj[ao.srcFunc]){
return null;
}
var mjp=dojo.event.MethodJoinPoint.getForMethod(ao.srcObj,ao.srcFunc,true);
mjp.removeAdvice(ao.adviceObj,ao.adviceFunc,ao.adviceType,ao.once);
return mjp;
};
this.kwDisconnect=function(_310){
return this._kwConnectImpl(_310,true);
};
};
dojo.event.MethodInvocation=function(_311,obj,args){
this.jp_=_311;
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
dojo.event.MethodJoinPoint=function(obj,_319){
this.object=obj||dj_global;
this.methodname=_319;
this.methodfunc=this.object[_319];
this.squelch=false;
};
dojo.event.MethodJoinPoint.getForMethod=function(obj,_31b){
if(!obj){
obj=dj_global;
}
var ofn=obj[_31b];
if(!ofn){
ofn=obj[_31b]=function(){
};
if(!obj[_31b]){
dojo.raise("Cannot set do-nothing method on that object "+_31b);
}
}else{
if((typeof ofn!="function")&&(!dojo.lang.isFunction(ofn))&&(!dojo.lang.isAlien(ofn))){
return null;
}
}
var _31d=_31b+"$joinpoint";
var _31e=_31b+"$joinpoint$method";
var _31f=obj[_31d];
if(!_31f){
var _320=false;
if(dojo.event["browser"]){
if((obj["attachEvent"])||(obj["nodeType"])||(obj["addEventListener"])){
_320=true;
dojo.event.browser.addClobberNodeAttrs(obj,[_31d,_31e,_31b]);
}
}
var _321=ofn.length;
obj[_31e]=ofn;
_31f=obj[_31d]=new dojo.event.MethodJoinPoint(obj,_31e);
if(!_320){
obj[_31b]=function(){
return _31f.run.apply(_31f,arguments);
};
}else{
obj[_31b]=function(){
var args=[];
if(!arguments.length){
var evt=null;
try{
if(obj.ownerDocument){
evt=obj.ownerDocument.parentWindow.event;
}else{
if(obj.documentElement){
evt=obj.documentElement.ownerDocument.parentWindow.event;
}else{
if(obj.event){
evt=obj.event;
}else{
evt=window.event;
}
}
}
}
catch(e){
evt=window.event;
}
if(evt){
args.push(dojo.event.browser.fixEvent(evt,this));
}
}else{
for(var x=0;x<arguments.length;x++){
if((x==0)&&(dojo.event.browser.isEvent(arguments[x]))){
args.push(dojo.event.browser.fixEvent(arguments[x],this));
}else{
args.push(arguments[x]);
}
}
}
return _31f.run.apply(_31f,args);
};
}
obj[_31b].__preJoinArity=_321;
}
return _31f;
};
dojo.lang.extend(dojo.event.MethodJoinPoint,{squelch:false,unintercept:function(){
this.object[this.methodname]=this.methodfunc;
this.before=[];
this.after=[];
this.around=[];
},disconnect:dojo.lang.forward("unintercept"),run:function(){
var obj=this.object||dj_global;
var args=arguments;
var _327=[];
for(var x=0;x<args.length;x++){
_327[x]=args[x];
}
var _329=function(marr){
if(!marr){
dojo.debug("Null argument to unrollAdvice()");
return;
}
var _32b=marr[0]||dj_global;
var _32c=marr[1];
if(!_32b[_32c]){
dojo.raise("function \""+_32c+"\" does not exist on \""+_32b+"\"");
}
var _32d=marr[2]||dj_global;
var _32e=marr[3];
var msg=marr[6];
var _330=marr[7];
if(_330>-1){
if(_330==0){
return;
}
marr[7]--;
}
var _331;
var to={args:[],jp_:this,object:obj,proceed:function(){
return _32b[_32c].apply(_32b,to.args);
}};
to.args=_327;
var _333=parseInt(marr[4]);
var _334=((!isNaN(_333))&&(marr[4]!==null)&&(typeof marr[4]!="undefined"));
if(marr[5]){
var rate=parseInt(marr[5]);
var cur=new Date();
var _337=false;
if((marr["last"])&&((cur-marr.last)<=rate)){
if(dojo.event._canTimeout){
if(marr["delayTimer"]){
clearTimeout(marr.delayTimer);
}
var tod=parseInt(rate*2);
var mcpy=dojo.lang.shallowCopy(marr);
marr.delayTimer=setTimeout(function(){
mcpy[5]=0;
_329(mcpy);
},tod);
}
return;
}else{
marr.last=cur;
}
}
if(_32e){
_32d[_32e].call(_32d,to);
}else{
if((_334)&&((dojo.render.html)||(dojo.render.svg))){
dj_global["setTimeout"](function(){
if(msg){
_32b[_32c].call(_32b,to);
}else{
_32b[_32c].apply(_32b,args);
}
},_333);
}else{
if(msg){
_32b[_32c].call(_32b,to);
}else{
_32b[_32c].apply(_32b,args);
}
}
}
};
var _33a=function(){
if(this.squelch){
try{
return _329.apply(this,arguments);
}
catch(e){
dojo.debug(e);
}
}else{
return _329.apply(this,arguments);
}
};
if((this["before"])&&(this.before.length>0)){
dojo.lang.forEach(this.before.concat(new Array()),_33a);
}
var _33b;
try{
if((this["around"])&&(this.around.length>0)){
var mi=new dojo.event.MethodInvocation(this,obj,args);
_33b=mi.proceed();
}else{
if(this.methodfunc){
_33b=this.object[this.methodname].apply(this.object,args);
}
}
}
catch(e){
if(!this.squelch){
dojo.debug(e,"when calling",this.methodname,"on",this.object,"with arguments",args);
dojo.raise(e);
}
}
if((this["after"])&&(this.after.length>0)){
dojo.lang.forEach(this.after.concat(new Array()),_33a);
}
return (this.methodfunc)?_33b:null;
},getArr:function(kind){
var type="after";
if((typeof kind=="string")&&(kind.indexOf("before")!=-1)){
type="before";
}else{
if(kind=="around"){
type="around";
}
}
if(!this[type]){
this[type]=[];
}
return this[type];
},kwAddAdvice:function(args){
this.addAdvice(args["adviceObj"],args["adviceFunc"],args["aroundObj"],args["aroundFunc"],args["adviceType"],args["precedence"],args["once"],args["delay"],args["rate"],args["adviceMsg"],args["maxCalls"]);
},addAdvice:function(_340,_341,_342,_343,_344,_345,once,_347,rate,_349,_34a){
var arr=this.getArr(_344);
if(!arr){
dojo.raise("bad this: "+this);
}
var ao=[_340,_341,_342,_343,_347,rate,_349,_34a];
if(once){
if(this.hasAdvice(_340,_341,_344,arr)>=0){
return;
}
}
if(_345=="first"){
arr.unshift(ao);
}else{
arr.push(ao);
}
},hasAdvice:function(_34d,_34e,_34f,arr){
if(!arr){
arr=this.getArr(_34f);
}
var ind=-1;
for(var x=0;x<arr.length;x++){
var aao=(typeof _34e=="object")?(new String(_34e)).toString():_34e;
var a1o=(typeof arr[x][1]=="object")?(new String(arr[x][1])).toString():arr[x][1];
if((arr[x][0]==_34d)&&(a1o==aao)){
ind=x;
}
}
return ind;
},removeAdvice:function(_355,_356,_357,once){
var arr=this.getArr(_357);
var ind=this.hasAdvice(_355,_356,_357,arr);
if(ind==-1){
return false;
}
while(ind!=-1){
arr.splice(ind,1);
if(once){
break;
}
ind=this.hasAdvice(_355,_356,_357,arr);
}
return true;
}});
dojo.provide("dojo.event.topic");
dojo.event.topic=new function(){
this.topics={};
this.getTopic=function(_35b){
if(!this.topics[_35b]){
this.topics[_35b]=new this.TopicImpl(_35b);
}
return this.topics[_35b];
};
this.registerPublisher=function(_35c,obj,_35e){
var _35c=this.getTopic(_35c);
_35c.registerPublisher(obj,_35e);
};
this.subscribe=function(_35f,obj,_361){
var _35f=this.getTopic(_35f);
_35f.subscribe(obj,_361);
};
this.unsubscribe=function(_362,obj,_364){
var _362=this.getTopic(_362);
_362.unsubscribe(obj,_364);
};
this.destroy=function(_365){
this.getTopic(_365).destroy();
delete this.topics[_365];
};
this.publishApply=function(_366,args){
var _366=this.getTopic(_366);
_366.sendMessage.apply(_366,args);
};
this.publish=function(_368,_369){
var _368=this.getTopic(_368);
var args=[];
for(var x=1;x<arguments.length;x++){
args.push(arguments[x]);
}
_368.sendMessage.apply(_368,args);
};
};
dojo.event.topic.TopicImpl=function(_36c){
this.topicName=_36c;
this.subscribe=function(_36d,_36e){
var tf=_36e||_36d;
var to=(!_36e)?dj_global:_36d;
return dojo.event.kwConnect({srcObj:this,srcFunc:"sendMessage",adviceObj:to,adviceFunc:tf});
};
this.unsubscribe=function(_371,_372){
var tf=(!_372)?_371:_372;
var to=(!_372)?null:_371;
return dojo.event.kwDisconnect({srcObj:this,srcFunc:"sendMessage",adviceObj:to,adviceFunc:tf});
};
this._getJoinPoint=function(){
return dojo.event.MethodJoinPoint.getForMethod(this,"sendMessage");
};
this.setSquelch=function(_375){
this._getJoinPoint().squelch=_375;
};
this.destroy=function(){
this._getJoinPoint().disconnect();
};
this.registerPublisher=function(_376,_377){
dojo.event.connect(_376,_377,this,"sendMessage");
};
this.sendMessage=function(_378){
};
};
dojo.provide("dojo.event.browser");
dojo._ie_clobber=new function(){
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
this.clobber=function(_37b){
var na;
var tna;
if(_37b){
tna=_37b.all||_37b.getElementsByTagName("*");
na=[_37b];
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
var _37f={};
for(var i=na.length-1;i>=0;i=i-1){
var el=na[i];
try{
if(el&&el["__clobberAttrs__"]){
for(var j=0;j<el.__clobberAttrs__.length;j++){
nukeProp(el,el.__clobberAttrs__[j]);
}
nukeProp(el,"__clobberAttrs__");
nukeProp(el,"__doClobber__");
}
}
catch(e){
}
}
na=null;
};
};
if(dojo.render.html.ie){
dojo.addOnUnload(function(){
dojo._ie_clobber.clobber();
try{
if((dojo["widget"])&&(dojo.widget["manager"])){
dojo.widget.manager.destroyAll();
}
}
catch(e){
}
if(dojo.widget){
for(var name in dojo.widget._templateCache){
if(dojo.widget._templateCache[name].node){
dojo.dom.destroyNode(dojo.widget._templateCache[name].node);
dojo.widget._templateCache[name].node=null;
delete dojo.widget._templateCache[name].node;
}
}
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
dojo._ie_clobber.clobberNodes=[];
});
}
dojo.event.browser=new function(){
var _384=0;
this.normalizedEventName=function(_385){
switch(_385){
case "CheckboxStateChange":
case "DOMAttrModified":
case "DOMMenuItemActive":
case "DOMMenuItemInactive":
case "DOMMouseScroll":
case "DOMNodeInserted":
case "DOMNodeRemoved":
case "RadioStateChange":
return _385;
break;
default:
var lcn=_385.toLowerCase();
return (lcn.indexOf("on")==0)?lcn.substr(2):lcn;
break;
}
};
this.clean=function(node){
if(dojo.render.html.ie){
dojo._ie_clobber.clobber(node);
}
};
this.addClobberNode=function(node){
if(!dojo.render.html.ie){
return;
}
if(!node["__doClobber__"]){
node.__doClobber__=true;
dojo._ie_clobber.clobberNodes.push(node);
node.__clobberAttrs__=[];
}
};
this.addClobberNodeAttrs=function(node,_38a){
if(!dojo.render.html.ie){
return;
}
this.addClobberNode(node);
for(var x=0;x<_38a.length;x++){
node.__clobberAttrs__.push(_38a[x]);
}
};
this.removeListener=function(node,_38d,fp,_38f){
if(!_38f){
var _38f=false;
}
_38d=dojo.event.browser.normalizedEventName(_38d);
if(_38d=="key"){
if(dojo.render.html.ie){
this.removeListener(node,"onkeydown",fp,_38f);
}
_38d="keypress";
}
if(node.removeEventListener){
node.removeEventListener(_38d,fp,_38f);
}
};
this.addListener=function(node,_391,fp,_393,_394){
if(!node){
return;
}
if(!_393){
var _393=false;
}
_391=dojo.event.browser.normalizedEventName(_391);
if(_391=="key"){
if(dojo.render.html.ie){
this.addListener(node,"onkeydown",fp,_393,_394);
}
_391="keypress";
}
if(!_394){
var _395=function(evt){
if(!evt){
evt=window.event;
}
var ret=fp(dojo.event.browser.fixEvent(evt,this));
if(_393){
dojo.event.browser.stopEvent(evt);
}
return ret;
};
}else{
_395=fp;
}
if(node.addEventListener){
node.addEventListener(_391,_395,_393);
return _395;
}else{
_391="on"+_391;
if(typeof node[_391]=="function"){
var _398=node[_391];
node[_391]=function(e){
_398(e);
return _395(e);
};
}else{
node[_391]=_395;
}
if(dojo.render.html.ie){
this.addClobberNodeAttrs(node,[_391]);
}
return _395;
}
};
this.isEvent=function(obj){
return (typeof obj!="undefined")&&(obj)&&(typeof Event!="undefined")&&(obj.eventPhase);
};
this.currentEvent=null;
this.callListener=function(_39b,_39c){
if(typeof _39b!="function"){
dojo.raise("listener not a function: "+_39b);
}
dojo.event.browser.currentEvent.currentTarget=_39c;
return _39b.call(_39c,dojo.event.browser.currentEvent);
};
this._stopPropagation=function(){
dojo.event.browser.currentEvent.cancelBubble=true;
};
this._preventDefault=function(){
dojo.event.browser.currentEvent.returnValue=false;
};
this.keys={KEY_BACKSPACE:8,KEY_TAB:9,KEY_CLEAR:12,KEY_ENTER:13,KEY_SHIFT:16,KEY_CTRL:17,KEY_ALT:18,KEY_PAUSE:19,KEY_CAPS_LOCK:20,KEY_ESCAPE:27,KEY_SPACE:32,KEY_PAGE_UP:33,KEY_PAGE_DOWN:34,KEY_END:35,KEY_HOME:36,KEY_LEFT_ARROW:37,KEY_UP_ARROW:38,KEY_RIGHT_ARROW:39,KEY_DOWN_ARROW:40,KEY_INSERT:45,KEY_DELETE:46,KEY_HELP:47,KEY_LEFT_WINDOW:91,KEY_RIGHT_WINDOW:92,KEY_SELECT:93,KEY_NUMPAD_0:96,KEY_NUMPAD_1:97,KEY_NUMPAD_2:98,KEY_NUMPAD_3:99,KEY_NUMPAD_4:100,KEY_NUMPAD_5:101,KEY_NUMPAD_6:102,KEY_NUMPAD_7:103,KEY_NUMPAD_8:104,KEY_NUMPAD_9:105,KEY_NUMPAD_MULTIPLY:106,KEY_NUMPAD_PLUS:107,KEY_NUMPAD_ENTER:108,KEY_NUMPAD_MINUS:109,KEY_NUMPAD_PERIOD:110,KEY_NUMPAD_DIVIDE:111,KEY_F1:112,KEY_F2:113,KEY_F3:114,KEY_F4:115,KEY_F5:116,KEY_F6:117,KEY_F7:118,KEY_F8:119,KEY_F9:120,KEY_F10:121,KEY_F11:122,KEY_F12:123,KEY_F13:124,KEY_F14:125,KEY_F15:126,KEY_NUM_LOCK:144,KEY_SCROLL_LOCK:145};
this.revKeys=[];
for(var key in this.keys){
this.revKeys[this.keys[key]]=key;
}
this.fixEvent=function(evt,_39f){
if(!evt){
if(window["event"]){
evt=window.event;
}
}
if((evt["type"])&&(evt["type"].indexOf("key")==0)){
evt.keys=this.revKeys;
for(var key in this.keys){
evt[key]=this.keys[key];
}
if(evt["type"]=="keydown"&&dojo.render.html.ie){
switch(evt.keyCode){
case evt.KEY_SHIFT:
case evt.KEY_CTRL:
case evt.KEY_ALT:
case evt.KEY_CAPS_LOCK:
case evt.KEY_LEFT_WINDOW:
case evt.KEY_RIGHT_WINDOW:
case evt.KEY_SELECT:
case evt.KEY_NUM_LOCK:
case evt.KEY_SCROLL_LOCK:
case evt.KEY_NUMPAD_0:
case evt.KEY_NUMPAD_1:
case evt.KEY_NUMPAD_2:
case evt.KEY_NUMPAD_3:
case evt.KEY_NUMPAD_4:
case evt.KEY_NUMPAD_5:
case evt.KEY_NUMPAD_6:
case evt.KEY_NUMPAD_7:
case evt.KEY_NUMPAD_8:
case evt.KEY_NUMPAD_9:
case evt.KEY_NUMPAD_PERIOD:
break;
case evt.KEY_NUMPAD_MULTIPLY:
case evt.KEY_NUMPAD_PLUS:
case evt.KEY_NUMPAD_ENTER:
case evt.KEY_NUMPAD_MINUS:
case evt.KEY_NUMPAD_DIVIDE:
break;
case evt.KEY_PAUSE:
case evt.KEY_TAB:
case evt.KEY_BACKSPACE:
case evt.KEY_ENTER:
case evt.KEY_ESCAPE:
case evt.KEY_PAGE_UP:
case evt.KEY_PAGE_DOWN:
case evt.KEY_END:
case evt.KEY_HOME:
case evt.KEY_LEFT_ARROW:
case evt.KEY_UP_ARROW:
case evt.KEY_RIGHT_ARROW:
case evt.KEY_DOWN_ARROW:
case evt.KEY_INSERT:
case evt.KEY_DELETE:
case evt.KEY_F1:
case evt.KEY_F2:
case evt.KEY_F3:
case evt.KEY_F4:
case evt.KEY_F5:
case evt.KEY_F6:
case evt.KEY_F7:
case evt.KEY_F8:
case evt.KEY_F9:
case evt.KEY_F10:
case evt.KEY_F11:
case evt.KEY_F12:
case evt.KEY_F12:
case evt.KEY_F13:
case evt.KEY_F14:
case evt.KEY_F15:
case evt.KEY_CLEAR:
case evt.KEY_HELP:
evt.key=evt.keyCode;
break;
default:
if(evt.ctrlKey||evt.altKey){
var _3a1=evt.keyCode;
if(_3a1>=65&&_3a1<=90&&evt.shiftKey==false){
_3a1+=32;
}
if(_3a1>=1&&_3a1<=26&&evt.ctrlKey){
_3a1+=96;
}
evt.key=String.fromCharCode(_3a1);
}
}
}else{
if(evt["type"]=="keypress"){
if(dojo.render.html.opera){
if(evt.which==0){
evt.key=evt.keyCode;
}else{
if(evt.which>0){
switch(evt.which){
case evt.KEY_SHIFT:
case evt.KEY_CTRL:
case evt.KEY_ALT:
case evt.KEY_CAPS_LOCK:
case evt.KEY_NUM_LOCK:
case evt.KEY_SCROLL_LOCK:
break;
case evt.KEY_PAUSE:
case evt.KEY_TAB:
case evt.KEY_BACKSPACE:
case evt.KEY_ENTER:
case evt.KEY_ESCAPE:
evt.key=evt.which;
break;
default:
var _3a1=evt.which;
if((evt.ctrlKey||evt.altKey||evt.metaKey)&&(evt.which>=65&&evt.which<=90&&evt.shiftKey==false)){
_3a1+=32;
}
evt.key=String.fromCharCode(_3a1);
}
}
}
}else{
if(dojo.render.html.ie){
if(!evt.ctrlKey&&!evt.altKey&&evt.keyCode>=evt.KEY_SPACE){
evt.key=String.fromCharCode(evt.keyCode);
}
}else{
if(dojo.render.html.safari){
switch(evt.keyCode){
case 25:
evt.key=evt.KEY_TAB;
evt.shift=true;
break;
case 63232:
evt.key=evt.KEY_UP_ARROW;
break;
case 63233:
evt.key=evt.KEY_DOWN_ARROW;
break;
case 63234:
evt.key=evt.KEY_LEFT_ARROW;
break;
case 63235:
evt.key=evt.KEY_RIGHT_ARROW;
break;
case 63236:
evt.key=evt.KEY_F1;
break;
case 63237:
evt.key=evt.KEY_F2;
break;
case 63238:
evt.key=evt.KEY_F3;
break;
case 63239:
evt.key=evt.KEY_F4;
break;
case 63240:
evt.key=evt.KEY_F5;
break;
case 63241:
evt.key=evt.KEY_F6;
break;
case 63242:
evt.key=evt.KEY_F7;
break;
case 63243:
evt.key=evt.KEY_F8;
break;
case 63244:
evt.key=evt.KEY_F9;
break;
case 63245:
evt.key=evt.KEY_F10;
break;
case 63246:
evt.key=evt.KEY_F11;
break;
case 63247:
evt.key=evt.KEY_F12;
break;
case 63250:
evt.key=evt.KEY_PAUSE;
break;
case 63272:
evt.key=evt.KEY_DELETE;
break;
case 63273:
evt.key=evt.KEY_HOME;
break;
case 63275:
evt.key=evt.KEY_END;
break;
case 63276:
evt.key=evt.KEY_PAGE_UP;
break;
case 63277:
evt.key=evt.KEY_PAGE_DOWN;
break;
case 63302:
evt.key=evt.KEY_INSERT;
break;
case 63248:
case 63249:
case 63289:
break;
default:
evt.key=evt.charCode>=evt.KEY_SPACE?String.fromCharCode(evt.charCode):evt.keyCode;
}
}else{
evt.key=evt.charCode>0?String.fromCharCode(evt.charCode):evt.keyCode;
}
}
}
}
}
}
if(dojo.render.html.ie){
if(!evt.target){
evt.target=evt.srcElement;
}
if(!evt.currentTarget){
evt.currentTarget=(_39f?_39f:evt.srcElement);
}
if(!evt.layerX){
evt.layerX=evt.offsetX;
}
if(!evt.layerY){
evt.layerY=evt.offsetY;
}
var doc=(evt.srcElement&&evt.srcElement.ownerDocument)?evt.srcElement.ownerDocument:document;
var _3a3=((dojo.render.html.ie55)||(doc["compatMode"]=="BackCompat"))?doc.body:doc.documentElement;
if(!evt.pageX){
evt.pageX=evt.clientX+(_3a3.scrollLeft||0);
}
if(!evt.pageY){
evt.pageY=evt.clientY+(_3a3.scrollTop||0);
}
if(evt.type=="mouseover"){
evt.relatedTarget=evt.fromElement;
}
if(evt.type=="mouseout"){
evt.relatedTarget=evt.toElement;
}
this.currentEvent=evt;
evt.callListener=this.callListener;
evt.stopPropagation=this._stopPropagation;
evt.preventDefault=this._preventDefault;
}
return evt;
};
this.stopEvent=function(evt){
if(window.event){
evt.cancelBubble=true;
evt.returnValue=false;
}else{
evt.preventDefault();
evt.stopPropagation();
}
};
};
dojo.kwCompoundRequire({common:["dojo.event.common","dojo.event.topic"],browser:["dojo.event.browser"],dashboard:["dojo.event.browser"]});
dojo.provide("dojo.event.*");
dojo.provide("dojo.gfx.color");
dojo.gfx.color.Color=function(r,g,b,a){
if(dojo.lang.isArray(r)){
this.r=r[0];
this.g=r[1];
this.b=r[2];
this.a=r[3]||1;
}else{
if(dojo.lang.isString(r)){
var rgb=dojo.gfx.color.extractRGB(r);
this.r=rgb[0];
this.g=rgb[1];
this.b=rgb[2];
this.a=g||1;
}else{
if(r instanceof dojo.gfx.color.Color){
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
dojo.gfx.color.Color.fromArray=function(arr){
return new dojo.gfx.color.Color(arr[0],arr[1],arr[2],arr[3]);
};
dojo.extend(dojo.gfx.color.Color,{toRgb:function(_3ab){
if(_3ab){
return this.toRgba();
}else{
return [this.r,this.g,this.b];
}
},toRgba:function(){
return [this.r,this.g,this.b,this.a];
},toHex:function(){
return dojo.gfx.color.rgb2hex(this.toRgb());
},toCss:function(){
return "rgb("+this.toRgb().join()+")";
},toString:function(){
return this.toHex();
},blend:function(_3ac,_3ad){
var rgb=null;
if(dojo.lang.isArray(_3ac)){
rgb=_3ac;
}else{
if(_3ac instanceof dojo.gfx.color.Color){
rgb=_3ac.toRgb();
}else{
rgb=new dojo.gfx.color.Color(_3ac).toRgb();
}
}
return dojo.gfx.color.blend(this.toRgb(),rgb,_3ad);
}});
dojo.gfx.color.named={white:[255,255,255],black:[0,0,0],red:[255,0,0],green:[0,255,0],lime:[0,255,0],blue:[0,0,255],navy:[0,0,128],gray:[128,128,128],silver:[192,192,192]};
dojo.gfx.color.blend=function(a,b,_3b1){
if(typeof a=="string"){
return dojo.gfx.color.blendHex(a,b,_3b1);
}
if(!_3b1){
_3b1=0;
}
_3b1=Math.min(Math.max(-1,_3b1),1);
_3b1=((_3b1+1)/2);
var c=[];
for(var x=0;x<3;x++){
c[x]=parseInt(b[x]+((a[x]-b[x])*_3b1));
}
return c;
};
dojo.gfx.color.blendHex=function(a,b,_3b6){
return dojo.gfx.color.rgb2hex(dojo.gfx.color.blend(dojo.gfx.color.hex2rgb(a),dojo.gfx.color.hex2rgb(b),_3b6));
};
dojo.gfx.color.extractRGB=function(_3b7){
var hex="0123456789abcdef";
_3b7=_3b7.toLowerCase();
if(_3b7.indexOf("rgb")==0){
var _3b9=_3b7.match(/rgba*\((\d+), *(\d+), *(\d+)/i);
var ret=_3b9.splice(1,3);
return ret;
}else{
var _3bb=dojo.gfx.color.hex2rgb(_3b7);
if(_3bb){
return _3bb;
}else{
return dojo.gfx.color.named[_3b7]||[255,255,255];
}
}
};
dojo.gfx.color.hex2rgb=function(hex){
var _3bd="0123456789ABCDEF";
var rgb=new Array(3);
if(hex.indexOf("#")==0){
hex=hex.substring(1);
}
hex=hex.toUpperCase();
if(hex.replace(new RegExp("["+_3bd+"]","g"),"")!=""){
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
rgb[i]=_3bd.indexOf(rgb[i].charAt(0))*16+_3bd.indexOf(rgb[i].charAt(1));
}
return rgb;
};
dojo.gfx.color.rgb2hex=function(r,g,b){
if(dojo.lang.isArray(r)){
g=r[1]||0;
b=r[2]||0;
r=r[0]||0;
}
var ret=dojo.lang.map([r,g,b],function(x){
x=new Number(x);
var s=x.toString(16);
while(s.length<2){
s="0"+s;
}
return s;
});
ret.unshift("#");
return ret.join("");
};
dojo.provide("dojo.lfx.Animation");
dojo.lfx.Line=function(_3c6,end){
this.start=_3c6;
this.end=end;
if(dojo.lang.isArray(_3c6)){
var diff=[];
dojo.lang.forEach(this.start,function(s,i){
diff[i]=this.end[i]-s;
},this);
this.getValue=function(n){
var res=[];
dojo.lang.forEach(this.start,function(s,i){
res[i]=(diff[i]*n)+s;
},this);
return res;
};
}else{
var diff=end-_3c6;
this.getValue=function(n){
return (diff*n)+this.start;
};
}
};
if((dojo.render.html.khtml)&&(!dojo.render.html.safari)){
dojo.lfx.easeDefault=function(n){
return (parseFloat("0.5")+((Math.sin((n+parseFloat("1.5"))*Math.PI))/2));
};
}else{
dojo.lfx.easeDefault=function(n){
return (0.5+((Math.sin((n+1.5)*Math.PI))/2));
};
}
dojo.lfx.easeIn=function(n){
return Math.pow(n,3);
};
dojo.lfx.easeOut=function(n){
return (1-Math.pow(1-n,3));
};
dojo.lfx.easeInOut=function(n){
return ((3*Math.pow(n,2))-(2*Math.pow(n,3)));
};
dojo.lfx.IAnimation=function(){
};
dojo.lang.extend(dojo.lfx.IAnimation,{curve:null,duration:1000,easing:null,repeatCount:0,rate:10,handler:null,beforeBegin:null,onBegin:null,onAnimate:null,onEnd:null,onPlay:null,onPause:null,onStop:null,play:null,pause:null,stop:null,connect:function(evt,_3d6,_3d7){
if(!_3d7){
_3d7=_3d6;
_3d6=this;
}
_3d7=dojo.lang.hitch(_3d6,_3d7);
var _3d8=this[evt]||function(){
};
this[evt]=function(){
var ret=_3d8.apply(this,arguments);
_3d7.apply(this,arguments);
return ret;
};
return this;
},fire:function(evt,args){
if(this[evt]){
this[evt].apply(this,(args||[]));
}
return this;
},repeat:function(_3dc){
this.repeatCount=_3dc;
return this;
},_active:false,_paused:false});
dojo.lfx.Animation=function(_3dd,_3de,_3df,_3e0,_3e1,rate){
dojo.lfx.IAnimation.call(this);
if(dojo.lang.isNumber(_3dd)||(!_3dd&&_3de.getValue)){
rate=_3e1;
_3e1=_3e0;
_3e0=_3df;
_3df=_3de;
_3de=_3dd;
_3dd=null;
}else{
if(_3dd.getValue||dojo.lang.isArray(_3dd)){
rate=_3e0;
_3e1=_3df;
_3e0=_3de;
_3df=_3dd;
_3de=null;
_3dd=null;
}
}
if(dojo.lang.isArray(_3df)){
this.curve=new dojo.lfx.Line(_3df[0],_3df[1]);
}else{
this.curve=_3df;
}
if(_3de!=null&&_3de>0){
this.duration=_3de;
}
if(_3e1){
this.repeatCount=_3e1;
}
if(rate){
this.rate=rate;
}
if(_3dd){
dojo.lang.forEach(["handler","beforeBegin","onBegin","onEnd","onPlay","onStop","onAnimate"],function(item){
if(_3dd[item]){
this.connect(item,_3dd[item]);
}
},this);
}
if(_3e0&&dojo.lang.isFunction(_3e0)){
this.easing=_3e0;
}
};
dojo.inherits(dojo.lfx.Animation,dojo.lfx.IAnimation);
dojo.lang.extend(dojo.lfx.Animation,{_startTime:null,_endTime:null,_timer:null,_percent:0,_startRepeatCount:0,play:function(_3e4,_3e5){
if(_3e5){
clearTimeout(this._timer);
this._active=false;
this._paused=false;
this._percent=0;
}else{
if(this._active&&!this._paused){
return this;
}
}
this.fire("handler",["beforeBegin"]);
this.fire("beforeBegin");
if(_3e4>0){
setTimeout(dojo.lang.hitch(this,function(){
this.play(null,_3e5);
}),_3e4);
return this;
}
this._startTime=new Date().valueOf();
if(this._paused){
this._startTime-=(this.duration*this._percent/100);
}
this._endTime=this._startTime+this.duration;
this._active=true;
this._paused=false;
var step=this._percent/100;
var _3e7=this.curve.getValue(step);
if(this._percent==0){
if(!this._startRepeatCount){
this._startRepeatCount=this.repeatCount;
}
this.fire("handler",["begin",_3e7]);
this.fire("onBegin",[_3e7]);
}
this.fire("handler",["play",_3e7]);
this.fire("onPlay",[_3e7]);
this._cycle();
return this;
},pause:function(){
clearTimeout(this._timer);
if(!this._active){
return this;
}
this._paused=true;
var _3e8=this.curve.getValue(this._percent/100);
this.fire("handler",["pause",_3e8]);
this.fire("onPause",[_3e8]);
return this;
},gotoPercent:function(pct,_3ea){
clearTimeout(this._timer);
this._active=true;
this._paused=true;
this._percent=pct;
if(_3ea){
this.play();
}
return this;
},stop:function(_3eb){
clearTimeout(this._timer);
var step=this._percent/100;
if(_3eb){
step=1;
}
var _3ed=this.curve.getValue(step);
this.fire("handler",["stop",_3ed]);
this.fire("onStop",[_3ed]);
this._active=false;
this._paused=false;
return this;
},status:function(){
if(this._active){
return this._paused?"paused":"playing";
}else{
return "stopped";
}
return this;
},_cycle:function(){
clearTimeout(this._timer);
if(this._active){
var curr=new Date().valueOf();
var step=(curr-this._startTime)/(this._endTime-this._startTime);
if(step>=1){
step=1;
this._percent=100;
}else{
this._percent=step*100;
}
if((this.easing)&&(dojo.lang.isFunction(this.easing))){
step=this.easing(step);
}
var _3f0=this.curve.getValue(step);
this.fire("handler",["animate",_3f0]);
this.fire("onAnimate",[_3f0]);
if(step<1){
this._timer=setTimeout(dojo.lang.hitch(this,"_cycle"),this.rate);
}else{
this._active=false;
this.fire("handler",["end"]);
this.fire("onEnd");
if(this.repeatCount>0){
this.repeatCount--;
this.play(null,true);
}else{
if(this.repeatCount==-1){
this.play(null,true);
}else{
if(this._startRepeatCount){
this.repeatCount=this._startRepeatCount;
this._startRepeatCount=0;
}
}
}
}
}
return this;
}});
dojo.lfx.Combine=function(_3f1){
dojo.lfx.IAnimation.call(this);
this._anims=[];
this._animsEnded=0;
var _3f2=arguments;
if(_3f2.length==1&&(dojo.lang.isArray(_3f2[0])||dojo.lang.isArrayLike(_3f2[0]))){
_3f2=_3f2[0];
}
dojo.lang.forEach(_3f2,function(anim){
this._anims.push(anim);
anim.connect("onEnd",dojo.lang.hitch(this,"_onAnimsEnded"));
},this);
};
dojo.inherits(dojo.lfx.Combine,dojo.lfx.IAnimation);
dojo.lang.extend(dojo.lfx.Combine,{_animsEnded:0,play:function(_3f4,_3f5){
if(!this._anims.length){
return this;
}
this.fire("beforeBegin");
if(_3f4>0){
setTimeout(dojo.lang.hitch(this,function(){
this.play(null,_3f5);
}),_3f4);
return this;
}
if(_3f5||this._anims[0].percent==0){
this.fire("onBegin");
}
this.fire("onPlay");
this._animsCall("play",null,_3f5);
return this;
},pause:function(){
this.fire("onPause");
this._animsCall("pause");
return this;
},stop:function(_3f6){
this.fire("onStop");
this._animsCall("stop",_3f6);
return this;
},_onAnimsEnded:function(){
this._animsEnded++;
if(this._animsEnded>=this._anims.length){
this.fire("onEnd");
}
return this;
},_animsCall:function(_3f7){
var args=[];
if(arguments.length>1){
for(var i=1;i<arguments.length;i++){
args.push(arguments[i]);
}
}
var _3fa=this;
dojo.lang.forEach(this._anims,function(anim){
anim[_3f7](args);
},_3fa);
return this;
}});
dojo.lfx.Chain=function(_3fc){
dojo.lfx.IAnimation.call(this);
this._anims=[];
this._currAnim=-1;
var _3fd=arguments;
if(_3fd.length==1&&(dojo.lang.isArray(_3fd[0])||dojo.lang.isArrayLike(_3fd[0]))){
_3fd=_3fd[0];
}
var _3fe=this;
dojo.lang.forEach(_3fd,function(anim,i,_401){
this._anims.push(anim);
if(i<_401.length-1){
anim.connect("onEnd",dojo.lang.hitch(this,"_playNext"));
}else{
anim.connect("onEnd",dojo.lang.hitch(this,function(){
this.fire("onEnd");
}));
}
},this);
};
dojo.inherits(dojo.lfx.Chain,dojo.lfx.IAnimation);
dojo.lang.extend(dojo.lfx.Chain,{_currAnim:-1,play:function(_402,_403){
if(!this._anims.length){
return this;
}
if(_403||!this._anims[this._currAnim]){
this._currAnim=0;
}
var _404=this._anims[this._currAnim];
this.fire("beforeBegin");
if(_402>0){
setTimeout(dojo.lang.hitch(this,function(){
this.play(null,_403);
}),_402);
return this;
}
if(_404){
if(this._currAnim==0){
this.fire("handler",["begin",this._currAnim]);
this.fire("onBegin",[this._currAnim]);
}
this.fire("onPlay",[this._currAnim]);
_404.play(null,_403);
}
return this;
},pause:function(){
if(this._anims[this._currAnim]){
this._anims[this._currAnim].pause();
this.fire("onPause",[this._currAnim]);
}
return this;
},playPause:function(){
if(this._anims.length==0){
return this;
}
if(this._currAnim==-1){
this._currAnim=0;
}
var _405=this._anims[this._currAnim];
if(_405){
if(!_405._active||_405._paused){
this.play();
}else{
this.pause();
}
}
return this;
},stop:function(){
var _406=this._anims[this._currAnim];
if(_406){
_406.stop();
this.fire("onStop",[this._currAnim]);
}
return _406;
},_playNext:function(){
if(this._currAnim==-1||this._anims.length==0){
return this;
}
this._currAnim++;
if(this._anims[this._currAnim]){
this._anims[this._currAnim].play(null,true);
}
return this;
}});
dojo.lfx.combine=function(_407){
var _408=arguments;
if(dojo.lang.isArray(arguments[0])){
_408=arguments[0];
}
if(_408.length==1){
return _408[0];
}
return new dojo.lfx.Combine(_408);
};
dojo.lfx.chain=function(_409){
var _40a=arguments;
if(dojo.lang.isArray(arguments[0])){
_40a=arguments[0];
}
if(_40a.length==1){
return _40a[0];
}
return new dojo.lfx.Chain(_40a);
};
dojo.provide("dojo.html.common");
dojo.lang.mixin(dojo.html,dojo.dom);
dojo.html.body=function(){
dojo.deprecated("dojo.html.body() moved to dojo.body()","0.5");
return dojo.body();
};
dojo.html.getEventTarget=function(evt){
if(!evt){
evt=dojo.global().event||{};
}
var t=(evt.srcElement?evt.srcElement:(evt.target?evt.target:null));
while((t)&&(t.nodeType!=1)){
t=t.parentNode;
}
return t;
};
dojo.html.getViewport=function(){
var _40d=dojo.global();
var _40e=dojo.doc();
var w=0;
var h=0;
if(dojo.render.html.mozilla){
w=_40e.documentElement.clientWidth;
h=_40d.innerHeight;
}else{
if(!dojo.render.html.opera&&_40d.innerWidth){
w=_40d.innerWidth;
h=_40d.innerHeight;
}else{
if(!dojo.render.html.opera&&dojo.exists(_40e,"documentElement.clientWidth")){
var w2=_40e.documentElement.clientWidth;
if(!w||w2&&w2<w){
w=w2;
}
h=_40e.documentElement.clientHeight;
}else{
if(dojo.body().clientWidth){
w=dojo.body().clientWidth;
h=dojo.body().clientHeight;
}
}
}
}
return {width:w,height:h};
};
dojo.html.getScroll=function(){
var _412=dojo.global();
var _413=dojo.doc();
var top=_412.pageYOffset||_413.documentElement.scrollTop||dojo.body().scrollTop||0;
var left=_412.pageXOffset||_413.documentElement.scrollLeft||dojo.body().scrollLeft||0;
return {top:top,left:left,offset:{x:left,y:top}};
};
dojo.html.getParentByType=function(node,type){
var _418=dojo.doc();
var _419=dojo.byId(node);
type=type.toLowerCase();
while((_419)&&(_419.nodeName.toLowerCase()!=type)){
if(_419==(_418["body"]||_418["documentElement"])){
return null;
}
_419=_419.parentNode;
}
return _419;
};
dojo.html.getAttribute=function(node,attr){
node=dojo.byId(node);
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
return dojo.html.getAttribute(dojo.byId(node),attr)?true:false;
};
dojo.html.getCursorPosition=function(e){
e=e||dojo.global().event;
var _421={x:0,y:0};
if(e.pageX||e.pageY){
_421.x=e.pageX;
_421.y=e.pageY;
}else{
var de=dojo.doc().documentElement;
var db=dojo.body();
_421.x=e.clientX+((de||db)["scrollLeft"])-((de||db)["clientLeft"]);
_421.y=e.clientY+((de||db)["scrollTop"])-((de||db)["clientTop"]);
}
return _421;
};
dojo.html.isTag=function(node){
node=dojo.byId(node);
if(node&&node.tagName){
for(var i=1;i<arguments.length;i++){
if(node.tagName.toLowerCase()==String(arguments[i]).toLowerCase()){
return String(arguments[i]).toLowerCase();
}
}
}
return "";
};
if(dojo.render.html.ie&&!dojo.render.html.ie70){
if(window.location.href.substr(0,6).toLowerCase()!="https:"){
(function(){
var _426=dojo.doc().createElement("script");
_426.src="javascript:'dojo.html.createExternalElement=function(doc, tag){ return doc.createElement(tag); }'";
dojo.doc().getElementsByTagName("head")[0].appendChild(_426);
})();
}
}else{
dojo.html.createExternalElement=function(doc,tag){
return doc.createElement(tag);
};
}
dojo.html._callDeprecated=function(_429,_42a,args,_42c,_42d){
dojo.deprecated("dojo.html."+_429,"replaced by dojo.html."+_42a+"("+(_42c?"node, {"+_42c+": "+_42c+"}":"")+")"+(_42d?"."+_42d:""),"0.5");
var _42e=[];
if(_42c){
var _42f={};
_42f[_42c]=args[1];
_42e.push(args[0]);
_42e.push(_42f);
}else{
_42e=args;
}
var ret=dojo.html[_42a].apply(dojo.html,args);
if(_42d){
return ret[_42d];
}else{
return ret;
}
};
dojo.html.getViewportWidth=function(){
return dojo.html._callDeprecated("getViewportWidth","getViewport",arguments,null,"width");
};
dojo.html.getViewportHeight=function(){
return dojo.html._callDeprecated("getViewportHeight","getViewport",arguments,null,"height");
};
dojo.html.getViewportSize=function(){
return dojo.html._callDeprecated("getViewportSize","getViewport",arguments);
};
dojo.html.getScrollTop=function(){
return dojo.html._callDeprecated("getScrollTop","getScroll",arguments,null,"top");
};
dojo.html.getScrollLeft=function(){
return dojo.html._callDeprecated("getScrollLeft","getScroll",arguments,null,"left");
};
dojo.html.getScrollOffset=function(){
return dojo.html._callDeprecated("getScrollOffset","getScroll",arguments,null,"offset");
};
dojo.provide("dojo.uri.Uri");
dojo.uri=new function(){
this.dojoUri=function(uri){
return new dojo.uri.Uri(dojo.hostenv.getBaseScriptUri(),uri);
};
this.moduleUri=function(_432,uri){
var loc=dojo.hostenv.getModuleSymbols(_432).join("/");
if(!loc){
return null;
}
if(loc.lastIndexOf("/")!=loc.length-1){
loc+="/";
}
var _435=loc.indexOf(":");
var _436=loc.indexOf("/");
if(loc.charAt(0)!="/"&&(_435==-1||_435>_436)){
loc=dojo.hostenv.getBaseScriptUri()+loc;
}
return new dojo.uri.Uri(loc,uri);
};
this.Uri=function(){
var uri=arguments[0];
for(var i=1;i<arguments.length;i++){
if(!arguments[i]){
continue;
}
var _439=new dojo.uri.Uri(arguments[i].toString());
var _43a=new dojo.uri.Uri(uri.toString());
if((_439.path=="")&&(_439.scheme==null)&&(_439.authority==null)&&(_439.query==null)){
if(_439.fragment!=null){
_43a.fragment=_439.fragment;
}
_439=_43a;
}else{
if(_439.scheme==null){
_439.scheme=_43a.scheme;
if(_439.authority==null){
_439.authority=_43a.authority;
if(_439.path.charAt(0)!="/"){
var path=_43a.path.substring(0,_43a.path.lastIndexOf("/")+1)+_439.path;
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
_439.path=segs.join("/");
}
}
}
}
uri="";
if(_439.scheme!=null){
uri+=_439.scheme+":";
}
if(_439.authority!=null){
uri+="//"+_439.authority;
}
uri+=_439.path;
if(_439.query!=null){
uri+="?"+_439.query;
}
if(_439.fragment!=null){
uri+="#"+_439.fragment;
}
}
this.uri=uri.toString();
var _43e="^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\\?([^#]*))?(#(.*))?$";
var r=this.uri.match(new RegExp(_43e));
this.scheme=r[2]||(r[1]?"":null);
this.authority=r[4]||(r[3]?"":null);
this.path=r[5];
this.query=r[7]||(r[6]?"":null);
this.fragment=r[9]||(r[8]?"":null);
if(this.authority!=null){
_43e="^((([^:]+:)?([^@]+))@)?([^:]*)(:([0-9]+))?$";
r=this.authority.match(new RegExp(_43e));
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
dojo.provide("dojo.html.style");
dojo.html.getClass=function(node){
node=dojo.byId(node);
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
return cs.replace(/^\s+|\s+$/g,"");
};
dojo.html.getClasses=function(node){
var c=dojo.html.getClass(node);
return (c=="")?[]:c.split(/\s+/g);
};
dojo.html.hasClass=function(node,_445){
return (new RegExp("(^|\\s+)"+_445+"(\\s+|$)")).test(dojo.html.getClass(node));
};
dojo.html.prependClass=function(node,_447){
_447+=" "+dojo.html.getClass(node);
return dojo.html.setClass(node,_447);
};
dojo.html.addClass=function(node,_449){
if(dojo.html.hasClass(node,_449)){
return false;
}
_449=(dojo.html.getClass(node)+" "+_449).replace(/^\s+|\s+$/g,"");
return dojo.html.setClass(node,_449);
};
dojo.html.setClass=function(node,_44b){
node=dojo.byId(node);
var cs=new String(_44b);
try{
if(typeof node.className=="string"){
node.className=cs;
}else{
if(node.setAttribute){
node.setAttribute("class",_44b);
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
dojo.html.removeClass=function(node,_44e,_44f){
try{
if(!_44f){
var _450=dojo.html.getClass(node).replace(new RegExp("(^|\\s+)"+_44e+"(\\s+|$)"),"$1$2");
}else{
var _450=dojo.html.getClass(node).replace(_44e,"");
}
dojo.html.setClass(node,_450);
}
catch(e){
dojo.debug("dojo.html.removeClass() failed",e);
}
return true;
};
dojo.html.replaceClass=function(node,_452,_453){
dojo.html.removeClass(node,_453);
dojo.html.addClass(node,_452);
};
dojo.html.classMatchType={ContainsAll:0,ContainsAny:1,IsOnly:2};
dojo.html.getElementsByClass=function(_454,_455,_456,_457,_458){
_458=false;
var _459=dojo.doc();
_455=dojo.byId(_455)||_459;
var _45a=_454.split(/\s+/g);
var _45b=[];
if(_457!=1&&_457!=2){
_457=0;
}
var _45c=new RegExp("(\\s|^)(("+_45a.join(")|(")+"))(\\s|$)");
var _45d=_45a.join(" ").length;
var _45e=[];
if(!_458&&_459.evaluate){
var _45f=".//"+(_456||"*")+"[contains(";
if(_457!=dojo.html.classMatchType.ContainsAny){
_45f+="concat(' ',@class,' '), ' "+_45a.join(" ') and contains(concat(' ',@class,' '), ' ")+" ')";
if(_457==2){
_45f+=" and string-length(@class)="+_45d+"]";
}else{
_45f+="]";
}
}else{
_45f+="concat(' ',@class,' '), ' "+_45a.join(" ') or contains(concat(' ',@class,' '), ' ")+" ')]";
}
var _460=_459.evaluate(_45f,_455,null,XPathResult.ANY_TYPE,null);
var _461=_460.iterateNext();
while(_461){
try{
_45e.push(_461);
_461=_460.iterateNext();
}
catch(e){
break;
}
}
return _45e;
}else{
if(!_456){
_456="*";
}
_45e=_455.getElementsByTagName(_456);
var node,i=0;
outer:
while(node=_45e[i++]){
var _464=dojo.html.getClasses(node);
if(_464.length==0){
continue outer;
}
var _465=0;
for(var j=0;j<_464.length;j++){
if(_45c.test(_464[j])){
if(_457==dojo.html.classMatchType.ContainsAny){
_45b.push(node);
continue outer;
}else{
_465++;
}
}else{
if(_457==dojo.html.classMatchType.IsOnly){
continue outer;
}
}
}
if(_465==_45a.length){
if((_457==dojo.html.classMatchType.IsOnly)&&(_465==_464.length)){
_45b.push(node);
}else{
if(_457==dojo.html.classMatchType.ContainsAll){
_45b.push(node);
}
}
}
}
return _45b;
}
};
dojo.html.getElementsByClassName=dojo.html.getElementsByClass;
dojo.html.toCamelCase=function(_467){
var arr=_467.split("-"),cc=arr[0];
for(var i=1;i<arr.length;i++){
cc+=arr[i].charAt(0).toUpperCase()+arr[i].substring(1);
}
return cc;
};
dojo.html.toSelectorCase=function(_46b){
return _46b.replace(/([A-Z])/g,"-$1").toLowerCase();
};
if(dojo.render.html.ie){
dojo.html.getComputedStyle=function(node,_46d,_46e){
node=dojo.byId(node);
if(!node||!node.currentStyle){
return _46e;
}
return node.currentStyle[dojo.html.toCamelCase(_46d)];
};
dojo.html.getComputedStyles=function(node){
return node.currentStyle;
};
}else{
dojo.html.getComputedStyle=function(node,_471,_472){
node=dojo.byId(node);
if(!node||!node.style){
return _472;
}
var s=document.defaultView.getComputedStyle(node,null);
return (s&&s[dojo.html.toCamelCase(_471)])||"";
};
dojo.html.getComputedStyles=function(node){
return document.defaultView.getComputedStyle(node,null);
};
}
dojo.html.getStyleProperty=function(node,_476){
node=dojo.byId(node);
return (node&&node.style?node.style[dojo.html.toCamelCase(_476)]:undefined);
};
dojo.html.getStyle=function(node,_478){
var _479=dojo.html.getStyleProperty(node,_478);
return (_479?_479:dojo.html.getComputedStyle(node,_478));
};
dojo.html.setStyle=function(node,_47b,_47c){
node=dojo.byId(node);
if(node&&node.style){
var _47d=dojo.html.toCamelCase(_47b);
node.style[_47d]=_47c;
}
};
dojo.html.setStyleText=function(_47e,text){
try{
_47e.style.cssText=text;
}
catch(e){
_47e.setAttribute("style",text);
}
};
dojo.html.copyStyle=function(_480,_481){
if(!_481.style.cssText){
_480.setAttribute("style",_481.getAttribute("style"));
}else{
_480.style.cssText=_481.style.cssText;
}
dojo.html.addClass(_480,dojo.html.getClass(_481));
};
dojo.html.getUnitValue=function(node,_483,_484){
var s=dojo.html.getComputedStyle(node,_483);
if((!s)||((s=="auto")&&(_484))){
return {value:0,units:"px"};
}
var _486=s.match(/(\-?[\d.]+)([a-z%]*)/i);
if(!_486){
return dojo.html.getUnitValue.bad;
}
return {value:Number(_486[1]),units:_486[2].toLowerCase()};
};
dojo.html.getUnitValue.bad={value:NaN,units:""};
if(dojo.render.html.ie){
dojo.html.toPixelValue=function(_487,_488){
if(!_488){
return 0;
}
if(_488.slice(-2)=="px"){
return parseFloat(_488);
}
var _489=0;
with(_487){
var _48a=style.left;
var _48b=runtimeStyle.left;
runtimeStyle.left=currentStyle.left;
try{
style.left=_488||0;
_489=style.pixelLeft;
style.left=_48a;
runtimeStyle.left=_48b;
}
catch(e){
}
}
return _489;
};
}else{
dojo.html.toPixelValue=function(_48c,_48d){
return (_48d&&(_48d.slice(-2)=="px")?parseFloat(_48d):0);
};
}
dojo.html.getPixelValue=function(node,_48f,_490){
return dojo.html.toPixelValue(node,dojo.html.getComputedStyle(node,_48f));
};
dojo.html.setPositivePixelValue=function(node,_492,_493){
if(isNaN(_493)){
return false;
}
node.style[_492]=Math.max(0,_493)+"px";
return true;
};
dojo.html.styleSheet=null;
dojo.html.insertCssRule=function(_494,_495,_496){
if(!dojo.html.styleSheet){
if(document.createStyleSheet){
dojo.html.styleSheet=document.createStyleSheet();
}else{
if(document.styleSheets[0]){
dojo.html.styleSheet=document.styleSheets[0];
}else{
return null;
}
}
}
if(arguments.length<3){
if(dojo.html.styleSheet.cssRules){
_496=dojo.html.styleSheet.cssRules.length;
}else{
if(dojo.html.styleSheet.rules){
_496=dojo.html.styleSheet.rules.length;
}else{
return null;
}
}
}
if(dojo.html.styleSheet.insertRule){
var rule=_494+" { "+_495+" }";
return dojo.html.styleSheet.insertRule(rule,_496);
}else{
if(dojo.html.styleSheet.addRule){
return dojo.html.styleSheet.addRule(_494,_495,_496);
}else{
return null;
}
}
};
dojo.html.removeCssRule=function(_498){
if(!dojo.html.styleSheet){
dojo.debug("no stylesheet defined for removing rules");
return false;
}
if(dojo.render.html.ie){
if(!_498){
_498=dojo.html.styleSheet.rules.length;
dojo.html.styleSheet.removeRule(_498);
}
}else{
if(document.styleSheets[0]){
if(!_498){
_498=dojo.html.styleSheet.cssRules.length;
}
dojo.html.styleSheet.deleteRule(_498);
}
}
return true;
};
dojo.html._insertedCssFiles=[];
dojo.html.insertCssFile=function(URI,doc,_49b,_49c){
if(!URI){
return;
}
if(!doc){
doc=document;
}
var _49d=dojo.hostenv.getText(URI,false,_49c);
if(_49d===null){
return;
}
_49d=dojo.html.fixPathsInCssText(_49d,URI);
if(_49b){
var idx=-1,node,ent=dojo.html._insertedCssFiles;
for(var i=0;i<ent.length;i++){
if((ent[i].doc==doc)&&(ent[i].cssText==_49d)){
idx=i;
node=ent[i].nodeRef;
break;
}
}
if(node){
var _4a2=doc.getElementsByTagName("style");
for(var i=0;i<_4a2.length;i++){
if(_4a2[i]==node){
return;
}
}
dojo.html._insertedCssFiles.shift(idx,1);
}
}
var _4a3=dojo.html.insertCssText(_49d,doc);
dojo.html._insertedCssFiles.push({"doc":doc,"cssText":_49d,"nodeRef":_4a3});
if(_4a3&&djConfig.isDebug){
_4a3.setAttribute("dbgHref",URI);
}
return _4a3;
};
dojo.html.insertCssText=function(_4a4,doc,URI){
if(!_4a4){
return;
}
if(!doc){
doc=document;
}
if(URI){
_4a4=dojo.html.fixPathsInCssText(_4a4,URI);
}
var _4a7=doc.createElement("style");
_4a7.setAttribute("type","text/css");
var head=doc.getElementsByTagName("head")[0];
if(!head){
dojo.debug("No head tag in document, aborting styles");
return;
}else{
head.appendChild(_4a7);
}
if(_4a7.styleSheet){
var _4a9=function(){
try{
_4a7.styleSheet.cssText=_4a4;
}
catch(e){
dojo.debug(e);
}
};
if(_4a7.styleSheet.disabled){
setTimeout(_4a9,10);
}else{
_4a9();
}
}else{
var _4aa=doc.createTextNode(_4a4);
_4a7.appendChild(_4aa);
}
return _4a7;
};
dojo.html.fixPathsInCssText=function(_4ab,URI){
if(!_4ab||!URI){
return;
}
var _4ad,str="",url="",_4b0="[\\t\\s\\w\\(\\)\\/\\.\\\\'\"-:#=&?~]+";
var _4b1=new RegExp("url\\(\\s*("+_4b0+")\\s*\\)");
var _4b2=/(file|https?|ftps?):\/\//;
regexTrim=new RegExp("^[\\s]*(['\"]?)("+_4b0+")\\1[\\s]*?$");
if(dojo.render.html.ie55||dojo.render.html.ie60){
var _4b3=new RegExp("AlphaImageLoader\\((.*)src=['\"]("+_4b0+")['\"]");
while(_4ad=_4b3.exec(_4ab)){
url=_4ad[2].replace(regexTrim,"$2");
if(!_4b2.exec(url)){
url=(new dojo.uri.Uri(URI,url).toString());
}
str+=_4ab.substring(0,_4ad.index)+"AlphaImageLoader("+_4ad[1]+"src='"+url+"'";
_4ab=_4ab.substr(_4ad.index+_4ad[0].length);
}
_4ab=str+_4ab;
str="";
}
while(_4ad=_4b1.exec(_4ab)){
url=_4ad[1].replace(regexTrim,"$2");
if(!_4b2.exec(url)){
url=(new dojo.uri.Uri(URI,url).toString());
}
str+=_4ab.substring(0,_4ad.index)+"url("+url+")";
_4ab=_4ab.substr(_4ad.index+_4ad[0].length);
}
return str+_4ab;
};
dojo.html.setActiveStyleSheet=function(_4b4){
var i=0,a,els=dojo.doc().getElementsByTagName("link");
while(a=els[i++]){
if(a.getAttribute("rel").indexOf("style")!=-1&&a.getAttribute("title")){
a.disabled=true;
if(a.getAttribute("title")==_4b4){
a.disabled=false;
}
}
}
};
dojo.html.getActiveStyleSheet=function(){
var i=0,a,els=dojo.doc().getElementsByTagName("link");
while(a=els[i++]){
if(a.getAttribute("rel").indexOf("style")!=-1&&a.getAttribute("title")&&!a.disabled){
return a.getAttribute("title");
}
}
return null;
};
dojo.html.getPreferredStyleSheet=function(){
var i=0,a,els=dojo.doc().getElementsByTagName("link");
while(a=els[i++]){
if(a.getAttribute("rel").indexOf("style")!=-1&&a.getAttribute("rel").indexOf("alt")==-1&&a.getAttribute("title")){
return a.getAttribute("title");
}
}
return null;
};
dojo.html.applyBrowserClass=function(node){
var drh=dojo.render.html;
var _4c0={dj_ie:drh.ie,dj_ie55:drh.ie55,dj_ie6:drh.ie60,dj_ie7:drh.ie70,dj_iequirks:drh.ie&&drh.quirks,dj_opera:drh.opera,dj_opera8:drh.opera&&(Math.floor(dojo.render.version)==8),dj_opera9:drh.opera&&(Math.floor(dojo.render.version)==9),dj_khtml:drh.khtml,dj_safari:drh.safari,dj_gecko:drh.mozilla};
for(var p in _4c0){
if(_4c0[p]){
dojo.html.addClass(node,p);
}
}
};
dojo.provide("dojo.html.display");
dojo.html._toggle=function(node,_4c3,_4c4){
node=dojo.byId(node);
_4c4(node,!_4c3(node));
return _4c3(node);
};
dojo.html.show=function(node){
node=dojo.byId(node);
if(dojo.html.getStyleProperty(node,"display")=="none"){
dojo.html.setStyle(node,"display",(node.dojoDisplayCache||""));
node.dojoDisplayCache=undefined;
}
};
dojo.html.hide=function(node){
node=dojo.byId(node);
if(typeof node["dojoDisplayCache"]=="undefined"){
var d=dojo.html.getStyleProperty(node,"display");
if(d!="none"){
node.dojoDisplayCache=d;
}
}
dojo.html.setStyle(node,"display","none");
};
dojo.html.setShowing=function(node,_4c9){
dojo.html[(_4c9?"show":"hide")](node);
};
dojo.html.isShowing=function(node){
return (dojo.html.getStyleProperty(node,"display")!="none");
};
dojo.html.toggleShowing=function(node){
return dojo.html._toggle(node,dojo.html.isShowing,dojo.html.setShowing);
};
dojo.html.displayMap={tr:"",td:"",th:"",img:"inline",span:"inline",input:"inline",button:"inline"};
dojo.html.suggestDisplayByTagName=function(node){
node=dojo.byId(node);
if(node&&node.tagName){
var tag=node.tagName.toLowerCase();
return (tag in dojo.html.displayMap?dojo.html.displayMap[tag]:"block");
}
};
dojo.html.setDisplay=function(node,_4cf){
dojo.html.setStyle(node,"display",((_4cf instanceof String||typeof _4cf=="string")?_4cf:(_4cf?dojo.html.suggestDisplayByTagName(node):"none")));
};
dojo.html.isDisplayed=function(node){
return (dojo.html.getComputedStyle(node,"display")!="none");
};
dojo.html.toggleDisplay=function(node){
return dojo.html._toggle(node,dojo.html.isDisplayed,dojo.html.setDisplay);
};
dojo.html.setVisibility=function(node,_4d3){
dojo.html.setStyle(node,"visibility",((_4d3 instanceof String||typeof _4d3=="string")?_4d3:(_4d3?"visible":"hidden")));
};
dojo.html.isVisible=function(node){
return (dojo.html.getComputedStyle(node,"visibility")!="hidden");
};
dojo.html.toggleVisibility=function(node){
return dojo.html._toggle(node,dojo.html.isVisible,dojo.html.setVisibility);
};
dojo.html.setOpacity=function(node,_4d7,_4d8){
node=dojo.byId(node);
var h=dojo.render.html;
if(!_4d8){
if(_4d7>=1){
if(h.ie){
dojo.html.clearOpacity(node);
return;
}else{
_4d7=0.999999;
}
}else{
if(_4d7<0){
_4d7=0;
}
}
}
if(h.ie){
if(node.nodeName.toLowerCase()=="tr"){
var tds=node.getElementsByTagName("td");
for(var x=0;x<tds.length;x++){
tds[x].style.filter="Alpha(Opacity="+_4d7*100+")";
}
}
node.style.filter="Alpha(Opacity="+_4d7*100+")";
}else{
if(h.moz){
node.style.opacity=_4d7;
node.style.MozOpacity=_4d7;
}else{
if(h.safari){
node.style.opacity=_4d7;
node.style.KhtmlOpacity=_4d7;
}else{
node.style.opacity=_4d7;
}
}
}
};
dojo.html.clearOpacity=function(node){
node=dojo.byId(node);
var ns=node.style;
var h=dojo.render.html;
if(h.ie){
try{
if(node.filters&&node.filters.alpha){
ns.filter="";
}
}
catch(e){
}
}else{
if(h.moz){
ns.opacity=1;
ns.MozOpacity=1;
}else{
if(h.safari){
ns.opacity=1;
ns.KhtmlOpacity=1;
}else{
ns.opacity=1;
}
}
}
};
dojo.html.getOpacity=function(node){
node=dojo.byId(node);
var h=dojo.render.html;
if(h.ie){
var opac=(node.filters&&node.filters.alpha&&typeof node.filters.alpha.opacity=="number"?node.filters.alpha.opacity:100)/100;
}else{
var opac=node.style.opacity||node.style.MozOpacity||node.style.KhtmlOpacity||1;
}
return opac>=0.999999?1:Number(opac);
};
dojo.provide("dojo.html.color");
dojo.html.getBackgroundColor=function(node){
node=dojo.byId(node);
var _4e3;
do{
_4e3=dojo.html.getStyle(node,"background-color");
if(_4e3.toLowerCase()=="rgba(0, 0, 0, 0)"){
_4e3="transparent";
}
if(node==document.getElementsByTagName("body")[0]){
node=null;
break;
}
node=node.parentNode;
}while(node&&dojo.lang.inArray(["transparent",""],_4e3));
if(_4e3=="transparent"){
_4e3=[255,255,255,0];
}else{
_4e3=dojo.gfx.color.extractRGB(_4e3);
}
return _4e3;
};
dojo.provide("dojo.html.layout");
dojo.html.sumAncestorProperties=function(node,prop){
node=dojo.byId(node);
if(!node){
return 0;
}
var _4e6=0;
while(node){
if(dojo.html.getComputedStyle(node,"position")=="fixed"){
return 0;
}
var val=node[prop];
if(val){
_4e6+=val-0;
if(node==dojo.body()){
break;
}
}
node=node.parentNode;
}
return _4e6;
};
dojo.html.setStyleAttributes=function(node,_4e9){
node=dojo.byId(node);
var _4ea=_4e9.replace(/(;)?\s*$/,"").split(";");
for(var i=0;i<_4ea.length;i++){
var _4ec=_4ea[i].split(":");
var name=_4ec[0].replace(/\s*$/,"").replace(/^\s*/,"").toLowerCase();
var _4ee=_4ec[1].replace(/\s*$/,"").replace(/^\s*/,"");
switch(name){
case "opacity":
dojo.html.setOpacity(node,_4ee);
break;
case "content-height":
dojo.html.setContentBox(node,{height:_4ee});
break;
case "content-width":
dojo.html.setContentBox(node,{width:_4ee});
break;
case "outer-height":
dojo.html.setMarginBox(node,{height:_4ee});
break;
case "outer-width":
dojo.html.setMarginBox(node,{width:_4ee});
break;
default:
node.style[dojo.html.toCamelCase(name)]=_4ee;
}
}
};
dojo.html.boxSizing={MARGIN_BOX:"margin-box",BORDER_BOX:"border-box",PADDING_BOX:"padding-box",CONTENT_BOX:"content-box"};
dojo.html.getAbsolutePosition=dojo.html.abs=function(node,_4f0,_4f1){
node=dojo.byId(node,node.ownerDocument);
var ret={x:0,y:0};
var bs=dojo.html.boxSizing;
if(!_4f1){
_4f1=bs.CONTENT_BOX;
}
var _4f4=2;
var _4f5;
switch(_4f1){
case bs.MARGIN_BOX:
_4f5=3;
break;
case bs.BORDER_BOX:
_4f5=2;
break;
case bs.PADDING_BOX:
default:
_4f5=1;
break;
case bs.CONTENT_BOX:
_4f5=0;
break;
}
var h=dojo.render.html;
var db=document["body"]||document["documentElement"];
if(h.ie){
with(node.getBoundingClientRect()){
ret.x=left-2;
ret.y=top-2;
}
}else{
if(document.getBoxObjectFor){
_4f4=1;
try{
var bo=document.getBoxObjectFor(node);
ret.x=bo.x-dojo.html.sumAncestorProperties(node,"scrollLeft");
ret.y=bo.y-dojo.html.sumAncestorProperties(node,"scrollTop");
}
catch(e){
}
}else{
if(node["offsetParent"]){
var _4f9;
if((h.safari)&&(node.style.getPropertyValue("position")=="absolute")&&(node.parentNode==db)){
_4f9=db;
}else{
_4f9=db.parentNode;
}
if(node.parentNode!=db){
var nd=node;
if(dojo.render.html.opera){
nd=db;
}
ret.x-=dojo.html.sumAncestorProperties(nd,"scrollLeft");
ret.y-=dojo.html.sumAncestorProperties(nd,"scrollTop");
}
var _4fb=node;
do{
var n=_4fb["offsetLeft"];
if(!h.opera||n>0){
ret.x+=isNaN(n)?0:n;
}
var m=_4fb["offsetTop"];
ret.y+=isNaN(m)?0:m;
_4fb=_4fb.offsetParent;
}while((_4fb!=_4f9)&&(_4fb!=null));
}else{
if(node["x"]&&node["y"]){
ret.x+=isNaN(node.x)?0:node.x;
ret.y+=isNaN(node.y)?0:node.y;
}
}
}
}
if(_4f0){
var _4fe=dojo.html.getScroll();
ret.y+=_4fe.top;
ret.x+=_4fe.left;
}
var _4ff=[dojo.html.getPaddingExtent,dojo.html.getBorderExtent,dojo.html.getMarginExtent];
if(_4f4>_4f5){
for(var i=_4f5;i<_4f4;++i){
ret.y+=_4ff[i](node,"top");
ret.x+=_4ff[i](node,"left");
}
}else{
if(_4f4<_4f5){
for(var i=_4f5;i>_4f4;--i){
ret.y-=_4ff[i-1](node,"top");
ret.x-=_4ff[i-1](node,"left");
}
}
}
ret.top=ret.y;
ret.left=ret.x;
return ret;
};
dojo.html.isPositionAbsolute=function(node){
return (dojo.html.getComputedStyle(node,"position")=="absolute");
};
dojo.html._sumPixelValues=function(node,_503,_504){
var _505=0;
for(var x=0;x<_503.length;x++){
_505+=dojo.html.getPixelValue(node,_503[x],_504);
}
return _505;
};
dojo.html.getMargin=function(node){
return {width:dojo.html._sumPixelValues(node,["margin-left","margin-right"],(dojo.html.getComputedStyle(node,"position")=="absolute")),height:dojo.html._sumPixelValues(node,["margin-top","margin-bottom"],(dojo.html.getComputedStyle(node,"position")=="absolute"))};
};
dojo.html.getBorder=function(node){
return {width:dojo.html.getBorderExtent(node,"left")+dojo.html.getBorderExtent(node,"right"),height:dojo.html.getBorderExtent(node,"top")+dojo.html.getBorderExtent(node,"bottom")};
};
dojo.html.getBorderExtent=function(node,side){
return (dojo.html.getStyle(node,"border-"+side+"-style")=="none"?0:dojo.html.getPixelValue(node,"border-"+side+"-width"));
};
dojo.html.getMarginExtent=function(node,side){
return dojo.html._sumPixelValues(node,["margin-"+side],dojo.html.isPositionAbsolute(node));
};
dojo.html.getPaddingExtent=function(node,side){
return dojo.html._sumPixelValues(node,["padding-"+side],true);
};
dojo.html.getPadding=function(node){
return {width:dojo.html._sumPixelValues(node,["padding-left","padding-right"],true),height:dojo.html._sumPixelValues(node,["padding-top","padding-bottom"],true)};
};
dojo.html.getPadBorder=function(node){
var pad=dojo.html.getPadding(node);
var _512=dojo.html.getBorder(node);
return {width:pad.width+_512.width,height:pad.height+_512.height};
};
dojo.html.getBoxSizing=function(node){
var h=dojo.render.html;
var bs=dojo.html.boxSizing;
if(((h.ie)||(h.opera))&&node.nodeName.toLowerCase()!="img"){
var cm=document["compatMode"];
if((cm=="BackCompat")||(cm=="QuirksMode")){
return bs.BORDER_BOX;
}else{
return bs.CONTENT_BOX;
}
}else{
if(arguments.length==0){
node=document.documentElement;
}
var _517;
if(!h.ie){
_517=dojo.html.getStyle(node,"-moz-box-sizing");
if(!_517){
_517=dojo.html.getStyle(node,"box-sizing");
}
}
return (_517?_517:bs.CONTENT_BOX);
}
};
dojo.html.isBorderBox=function(node){
return (dojo.html.getBoxSizing(node)==dojo.html.boxSizing.BORDER_BOX);
};
dojo.html.getBorderBox=function(node){
node=dojo.byId(node);
return {width:node.offsetWidth,height:node.offsetHeight};
};
dojo.html.getPaddingBox=function(node){
var box=dojo.html.getBorderBox(node);
var _51c=dojo.html.getBorder(node);
return {width:box.width-_51c.width,height:box.height-_51c.height};
};
dojo.html.getContentBox=function(node){
node=dojo.byId(node);
var _51e=dojo.html.getPadBorder(node);
return {width:node.offsetWidth-_51e.width,height:node.offsetHeight-_51e.height};
};
dojo.html.setContentBox=function(node,args){
node=dojo.byId(node);
var _521=0;
var _522=0;
var isbb=dojo.html.isBorderBox(node);
var _524=(isbb?dojo.html.getPadBorder(node):{width:0,height:0});
var ret={};
if(typeof args.width!="undefined"){
_521=args.width+_524.width;
ret.width=dojo.html.setPositivePixelValue(node,"width",_521);
}
if(typeof args.height!="undefined"){
_522=args.height+_524.height;
ret.height=dojo.html.setPositivePixelValue(node,"height",_522);
}
return ret;
};
dojo.html.getMarginBox=function(node){
var _527=dojo.html.getBorderBox(node);
var _528=dojo.html.getMargin(node);
return {width:_527.width+_528.width,height:_527.height+_528.height};
};
dojo.html.setMarginBox=function(node,args){
node=dojo.byId(node);
var _52b=0;
var _52c=0;
var isbb=dojo.html.isBorderBox(node);
var _52e=(!isbb?dojo.html.getPadBorder(node):{width:0,height:0});
var _52f=dojo.html.getMargin(node);
var ret={};
if(typeof args.width!="undefined"){
_52b=args.width-_52e.width;
_52b-=_52f.width;
ret.width=dojo.html.setPositivePixelValue(node,"width",_52b);
}
if(typeof args.height!="undefined"){
_52c=args.height-_52e.height;
_52c-=_52f.height;
ret.height=dojo.html.setPositivePixelValue(node,"height",_52c);
}
return ret;
};
dojo.html.getElementBox=function(node,type){
var bs=dojo.html.boxSizing;
switch(type){
case bs.MARGIN_BOX:
return dojo.html.getMarginBox(node);
case bs.BORDER_BOX:
return dojo.html.getBorderBox(node);
case bs.PADDING_BOX:
return dojo.html.getPaddingBox(node);
case bs.CONTENT_BOX:
default:
return dojo.html.getContentBox(node);
}
};
dojo.html.toCoordinateObject=dojo.html.toCoordinateArray=function(_534,_535,_536){
if(_534 instanceof Array||typeof _534=="array"){
dojo.deprecated("dojo.html.toCoordinateArray","use dojo.html.toCoordinateObject({left: , top: , width: , height: }) instead","0.5");
while(_534.length<4){
_534.push(0);
}
while(_534.length>4){
_534.pop();
}
var ret={left:_534[0],top:_534[1],width:_534[2],height:_534[3]};
}else{
if(!_534.nodeType&&!(_534 instanceof String||typeof _534=="string")&&("width" in _534||"height" in _534||"left" in _534||"x" in _534||"top" in _534||"y" in _534)){
var ret={left:_534.left||_534.x||0,top:_534.top||_534.y||0,width:_534.width||0,height:_534.height||0};
}else{
var node=dojo.byId(_534);
var pos=dojo.html.abs(node,_535,_536);
var _53a=dojo.html.getMarginBox(node);
var ret={left:pos.left,top:pos.top,width:_53a.width,height:_53a.height};
}
}
ret.x=ret.left;
ret.y=ret.top;
return ret;
};
dojo.html.setMarginBoxWidth=dojo.html.setOuterWidth=function(node,_53c){
return dojo.html._callDeprecated("setMarginBoxWidth","setMarginBox",arguments,"width");
};
dojo.html.setMarginBoxHeight=dojo.html.setOuterHeight=function(){
return dojo.html._callDeprecated("setMarginBoxHeight","setMarginBox",arguments,"height");
};
dojo.html.getMarginBoxWidth=dojo.html.getOuterWidth=function(){
return dojo.html._callDeprecated("getMarginBoxWidth","getMarginBox",arguments,null,"width");
};
dojo.html.getMarginBoxHeight=dojo.html.getOuterHeight=function(){
return dojo.html._callDeprecated("getMarginBoxHeight","getMarginBox",arguments,null,"height");
};
dojo.html.getTotalOffset=function(node,type,_53f){
return dojo.html._callDeprecated("getTotalOffset","getAbsolutePosition",arguments,null,type);
};
dojo.html.getAbsoluteX=function(node,_541){
return dojo.html._callDeprecated("getAbsoluteX","getAbsolutePosition",arguments,null,"x");
};
dojo.html.getAbsoluteY=function(node,_543){
return dojo.html._callDeprecated("getAbsoluteY","getAbsolutePosition",arguments,null,"y");
};
dojo.html.totalOffsetLeft=function(node,_545){
return dojo.html._callDeprecated("totalOffsetLeft","getAbsolutePosition",arguments,null,"left");
};
dojo.html.totalOffsetTop=function(node,_547){
return dojo.html._callDeprecated("totalOffsetTop","getAbsolutePosition",arguments,null,"top");
};
dojo.html.getMarginWidth=function(node){
return dojo.html._callDeprecated("getMarginWidth","getMargin",arguments,null,"width");
};
dojo.html.getMarginHeight=function(node){
return dojo.html._callDeprecated("getMarginHeight","getMargin",arguments,null,"height");
};
dojo.html.getBorderWidth=function(node){
return dojo.html._callDeprecated("getBorderWidth","getBorder",arguments,null,"width");
};
dojo.html.getBorderHeight=function(node){
return dojo.html._callDeprecated("getBorderHeight","getBorder",arguments,null,"height");
};
dojo.html.getPaddingWidth=function(node){
return dojo.html._callDeprecated("getPaddingWidth","getPadding",arguments,null,"width");
};
dojo.html.getPaddingHeight=function(node){
return dojo.html._callDeprecated("getPaddingHeight","getPadding",arguments,null,"height");
};
dojo.html.getPadBorderWidth=function(node){
return dojo.html._callDeprecated("getPadBorderWidth","getPadBorder",arguments,null,"width");
};
dojo.html.getPadBorderHeight=function(node){
return dojo.html._callDeprecated("getPadBorderHeight","getPadBorder",arguments,null,"height");
};
dojo.html.getBorderBoxWidth=dojo.html.getInnerWidth=function(){
return dojo.html._callDeprecated("getBorderBoxWidth","getBorderBox",arguments,null,"width");
};
dojo.html.getBorderBoxHeight=dojo.html.getInnerHeight=function(){
return dojo.html._callDeprecated("getBorderBoxHeight","getBorderBox",arguments,null,"height");
};
dojo.html.getContentBoxWidth=dojo.html.getContentWidth=function(){
return dojo.html._callDeprecated("getContentBoxWidth","getContentBox",arguments,null,"width");
};
dojo.html.getContentBoxHeight=dojo.html.getContentHeight=function(){
return dojo.html._callDeprecated("getContentBoxHeight","getContentBox",arguments,null,"height");
};
dojo.html.setContentBoxWidth=dojo.html.setContentWidth=function(node,_551){
return dojo.html._callDeprecated("setContentBoxWidth","setContentBox",arguments,"width");
};
dojo.html.setContentBoxHeight=dojo.html.setContentHeight=function(node,_553){
return dojo.html._callDeprecated("setContentBoxHeight","setContentBox",arguments,"height");
};
dojo.provide("dojo.lfx.html");
dojo.lfx.html._byId=function(_554){
if(!_554){
return [];
}
if(dojo.lang.isArrayLike(_554)){
if(!_554.alreadyChecked){
var n=[];
dojo.lang.forEach(_554,function(node){
n.push(dojo.byId(node));
});
n.alreadyChecked=true;
return n;
}else{
return _554;
}
}else{
var n=[];
n.push(dojo.byId(_554));
n.alreadyChecked=true;
return n;
}
};
dojo.lfx.html.propertyAnimation=function(_557,_558,_559,_55a,_55b){
_557=dojo.lfx.html._byId(_557);
var _55c={"propertyMap":_558,"nodes":_557,"duration":_559,"easing":_55a||dojo.lfx.easeDefault};
var _55d=function(args){
if(args.nodes.length==1){
var pm=args.propertyMap;
if(!dojo.lang.isArray(args.propertyMap)){
var parr=[];
for(var _561 in pm){
pm[_561].property=_561;
parr.push(pm[_561]);
}
pm=args.propertyMap=parr;
}
dojo.lang.forEach(pm,function(prop){
if(dj_undef("start",prop)){
if(prop.property!="opacity"){
prop.start=parseInt(dojo.html.getComputedStyle(args.nodes[0],prop.property));
}else{
prop.start=dojo.html.getOpacity(args.nodes[0]);
}
}
});
}
};
var _563=function(_564){
var _565=[];
dojo.lang.forEach(_564,function(c){
_565.push(Math.round(c));
});
return _565;
};
var _567=function(n,_569){
n=dojo.byId(n);
if(!n||!n.style){
return;
}
for(var s in _569){
try{
if(s=="opacity"){
dojo.html.setOpacity(n,_569[s]);
}else{
n.style[s]=_569[s];
}
}
catch(e){
dojo.debug(e);
}
}
};
var _56b=function(_56c){
this._properties=_56c;
this.diffs=new Array(_56c.length);
dojo.lang.forEach(_56c,function(prop,i){
if(dojo.lang.isFunction(prop.start)){
prop.start=prop.start(prop,i);
}
if(dojo.lang.isFunction(prop.end)){
prop.end=prop.end(prop,i);
}
if(dojo.lang.isArray(prop.start)){
this.diffs[i]=null;
}else{
if(prop.start instanceof dojo.gfx.color.Color){
prop.startRgb=prop.start.toRgb();
prop.endRgb=prop.end.toRgb();
}else{
this.diffs[i]=prop.end-prop.start;
}
}
},this);
this.getValue=function(n){
var ret={};
dojo.lang.forEach(this._properties,function(prop,i){
var _573=null;
if(dojo.lang.isArray(prop.start)){
}else{
if(prop.start instanceof dojo.gfx.color.Color){
_573=(prop.units||"rgb")+"(";
for(var j=0;j<prop.startRgb.length;j++){
_573+=Math.round(((prop.endRgb[j]-prop.startRgb[j])*n)+prop.startRgb[j])+(j<prop.startRgb.length-1?",":"");
}
_573+=")";
}else{
_573=((this.diffs[i])*n)+prop.start+(prop.property!="opacity"?prop.units||"px":"");
}
}
ret[dojo.html.toCamelCase(prop.property)]=_573;
},this);
return ret;
};
};
var anim=new dojo.lfx.Animation({beforeBegin:function(){
_55d(_55c);
anim.curve=new _56b(_55c.propertyMap);
},onAnimate:function(_576){
dojo.lang.forEach(_55c.nodes,function(node){
_567(node,_576);
});
}},_55c.duration,null,_55c.easing);
if(_55b){
for(var x in _55b){
if(dojo.lang.isFunction(_55b[x])){
anim.connect(x,anim,_55b[x]);
}
}
}
return anim;
};
dojo.lfx.html._makeFadeable=function(_579){
var _57a=function(node){
if(dojo.render.html.ie){
if((node.style.zoom.length==0)&&(dojo.html.getStyle(node,"zoom")=="normal")){
node.style.zoom="1";
}
if((node.style.width.length==0)&&(dojo.html.getStyle(node,"width")=="auto")){
node.style.width="auto";
}
}
};
if(dojo.lang.isArrayLike(_579)){
dojo.lang.forEach(_579,_57a);
}else{
_57a(_579);
}
};
dojo.lfx.html.fade=function(_57c,_57d,_57e,_57f,_580){
_57c=dojo.lfx.html._byId(_57c);
var _581={property:"opacity"};
if(!dj_undef("start",_57d)){
_581.start=_57d.start;
}else{
_581.start=function(){
return dojo.html.getOpacity(_57c[0]);
};
}
if(!dj_undef("end",_57d)){
_581.end=_57d.end;
}else{
dojo.raise("dojo.lfx.html.fade needs an end value");
}
var anim=dojo.lfx.propertyAnimation(_57c,[_581],_57e,_57f);
anim.connect("beforeBegin",function(){
dojo.lfx.html._makeFadeable(_57c);
});
if(_580){
anim.connect("onEnd",function(){
_580(_57c,anim);
});
}
return anim;
};
dojo.lfx.html.fadeIn=function(_583,_584,_585,_586){
return dojo.lfx.html.fade(_583,{end:1},_584,_585,_586);
};
dojo.lfx.html.fadeOut=function(_587,_588,_589,_58a){
return dojo.lfx.html.fade(_587,{end:0},_588,_589,_58a);
};
dojo.lfx.html.fadeShow=function(_58b,_58c,_58d,_58e){
_58b=dojo.lfx.html._byId(_58b);
dojo.lang.forEach(_58b,function(node){
dojo.html.setOpacity(node,0);
});
var anim=dojo.lfx.html.fadeIn(_58b,_58c,_58d,_58e);
anim.connect("beforeBegin",function(){
if(dojo.lang.isArrayLike(_58b)){
dojo.lang.forEach(_58b,dojo.html.show);
}else{
dojo.html.show(_58b);
}
});
return anim;
};
dojo.lfx.html.fadeHide=function(_591,_592,_593,_594){
var anim=dojo.lfx.html.fadeOut(_591,_592,_593,function(){
if(dojo.lang.isArrayLike(_591)){
dojo.lang.forEach(_591,dojo.html.hide);
}else{
dojo.html.hide(_591);
}
if(_594){
_594(_591,anim);
}
});
return anim;
};
dojo.lfx.html.wipeIn=function(_596,_597,_598,_599){
_596=dojo.lfx.html._byId(_596);
var _59a=[];
dojo.lang.forEach(_596,function(node){
var _59c={};
var _59d,_59e,_59f;
with(node.style){
_59d=top;
_59e=left;
_59f=position;
top="-9999px";
left="-9999px";
position="absolute";
display="";
}
var _5a0=dojo.html.getBorderBox(node).height;
with(node.style){
top=_59d;
left=_59e;
position=_59f;
display="none";
}
var anim=dojo.lfx.propertyAnimation(node,{"height":{start:1,end:function(){
return _5a0;
}}},_597,_598);
anim.connect("beforeBegin",function(){
_59c.overflow=node.style.overflow;
_59c.height=node.style.height;
with(node.style){
overflow="hidden";
height="1px";
}
dojo.html.show(node);
});
anim.connect("onEnd",function(){
with(node.style){
overflow=_59c.overflow;
height=_59c.height;
}
if(_599){
_599(node,anim);
}
});
_59a.push(anim);
});
return dojo.lfx.combine(_59a);
};
dojo.lfx.html.wipeOut=function(_5a2,_5a3,_5a4,_5a5){
_5a2=dojo.lfx.html._byId(_5a2);
var _5a6=[];
dojo.lang.forEach(_5a2,function(node){
var _5a8={};
var anim=dojo.lfx.propertyAnimation(node,{"height":{start:function(){
return dojo.html.getContentBox(node).height;
},end:1}},_5a3,_5a4,{"beforeBegin":function(){
_5a8.overflow=node.style.overflow;
_5a8.height=node.style.height;
with(node.style){
overflow="hidden";
}
dojo.html.show(node);
},"onEnd":function(){
dojo.html.hide(node);
with(node.style){
overflow=_5a8.overflow;
height=_5a8.height;
}
if(_5a5){
_5a5(node,anim);
}
}});
_5a6.push(anim);
});
return dojo.lfx.combine(_5a6);
};
dojo.lfx.html.slideTo=function(_5aa,_5ab,_5ac,_5ad,_5ae){
_5aa=dojo.lfx.html._byId(_5aa);
var _5af=[];
var _5b0=dojo.html.getComputedStyle;
if(dojo.lang.isArray(_5ab)){
dojo.deprecated("dojo.lfx.html.slideTo(node, array)","use dojo.lfx.html.slideTo(node, {top: value, left: value});","0.5");
_5ab={top:_5ab[0],left:_5ab[1]};
}
dojo.lang.forEach(_5aa,function(node){
var top=null;
var left=null;
var init=(function(){
var _5b5=node;
return function(){
var pos=_5b0(_5b5,"position");
top=(pos=="absolute"?node.offsetTop:parseInt(_5b0(node,"top"))||0);
left=(pos=="absolute"?node.offsetLeft:parseInt(_5b0(node,"left"))||0);
if(!dojo.lang.inArray(["absolute","relative"],pos)){
var ret=dojo.html.abs(_5b5,true);
dojo.html.setStyleAttributes(_5b5,"position:absolute;top:"+ret.y+"px;left:"+ret.x+"px;");
top=ret.y;
left=ret.x;
}
};
})();
init();
var anim=dojo.lfx.propertyAnimation(node,{"top":{start:top,end:(_5ab.top||0)},"left":{start:left,end:(_5ab.left||0)}},_5ac,_5ad,{"beforeBegin":init});
if(_5ae){
anim.connect("onEnd",function(){
_5ae(_5aa,anim);
});
}
_5af.push(anim);
});
return dojo.lfx.combine(_5af);
};
dojo.lfx.html.slideBy=function(_5b9,_5ba,_5bb,_5bc,_5bd){
_5b9=dojo.lfx.html._byId(_5b9);
var _5be=[];
var _5bf=dojo.html.getComputedStyle;
if(dojo.lang.isArray(_5ba)){
dojo.deprecated("dojo.lfx.html.slideBy(node, array)","use dojo.lfx.html.slideBy(node, {top: value, left: value});","0.5");
_5ba={top:_5ba[0],left:_5ba[1]};
}
dojo.lang.forEach(_5b9,function(node){
var top=null;
var left=null;
var init=(function(){
var _5c4=node;
return function(){
var pos=_5bf(_5c4,"position");
top=(pos=="absolute"?node.offsetTop:parseInt(_5bf(node,"top"))||0);
left=(pos=="absolute"?node.offsetLeft:parseInt(_5bf(node,"left"))||0);
if(!dojo.lang.inArray(["absolute","relative"],pos)){
var ret=dojo.html.abs(_5c4,true);
dojo.html.setStyleAttributes(_5c4,"position:absolute;top:"+ret.y+"px;left:"+ret.x+"px;");
top=ret.y;
left=ret.x;
}
};
})();
init();
var anim=dojo.lfx.propertyAnimation(node,{"top":{start:top,end:top+(_5ba.top||0)},"left":{start:left,end:left+(_5ba.left||0)}},_5bb,_5bc).connect("beforeBegin",init);
if(_5bd){
anim.connect("onEnd",function(){
_5bd(_5b9,anim);
});
}
_5be.push(anim);
});
return dojo.lfx.combine(_5be);
};
dojo.lfx.html.explode=function(_5c8,_5c9,_5ca,_5cb,_5cc){
var h=dojo.html;
_5c8=dojo.byId(_5c8);
_5c9=dojo.byId(_5c9);
var _5ce=h.toCoordinateObject(_5c8,true);
var _5cf=document.createElement("div");
h.copyStyle(_5cf,_5c9);
if(_5c9.explodeClassName){
_5cf.className=_5c9.explodeClassName;
}
with(_5cf.style){
position="absolute";
display="none";
var _5d0=h.getStyle(_5c8,"background-color");
backgroundColor=_5d0?_5d0.toLowerCase():"transparent";
backgroundColor=(backgroundColor=="transparent")?"rgb(221, 221, 221)":backgroundColor;
}
dojo.body().appendChild(_5cf);
with(_5c9.style){
visibility="hidden";
display="block";
}
var _5d1=h.toCoordinateObject(_5c9,true);
with(_5c9.style){
display="none";
visibility="visible";
}
var _5d2={opacity:{start:0.5,end:1}};
dojo.lang.forEach(["height","width","top","left"],function(type){
_5d2[type]={start:_5ce[type],end:_5d1[type]};
});
var anim=new dojo.lfx.propertyAnimation(_5cf,_5d2,_5ca,_5cb,{"beforeBegin":function(){
h.setDisplay(_5cf,"block");
},"onEnd":function(){
h.setDisplay(_5c9,"block");
_5cf.parentNode.removeChild(_5cf);
}});
if(_5cc){
anim.connect("onEnd",function(){
_5cc(_5c9,anim);
});
}
return anim;
};
dojo.lfx.html.implode=function(_5d5,end,_5d7,_5d8,_5d9){
var h=dojo.html;
_5d5=dojo.byId(_5d5);
end=dojo.byId(end);
var _5db=dojo.html.toCoordinateObject(_5d5,true);
var _5dc=dojo.html.toCoordinateObject(end,true);
var _5dd=document.createElement("div");
dojo.html.copyStyle(_5dd,_5d5);
if(_5d5.explodeClassName){
_5dd.className=_5d5.explodeClassName;
}
dojo.html.setOpacity(_5dd,0.3);
with(_5dd.style){
position="absolute";
display="none";
backgroundColor=h.getStyle(_5d5,"background-color").toLowerCase();
}
dojo.body().appendChild(_5dd);
var _5de={opacity:{start:1,end:0.5}};
dojo.lang.forEach(["height","width","top","left"],function(type){
_5de[type]={start:_5db[type],end:_5dc[type]};
});
var anim=new dojo.lfx.propertyAnimation(_5dd,_5de,_5d7,_5d8,{"beforeBegin":function(){
dojo.html.hide(_5d5);
dojo.html.show(_5dd);
},"onEnd":function(){
_5dd.parentNode.removeChild(_5dd);
}});
if(_5d9){
anim.connect("onEnd",function(){
_5d9(_5d5,anim);
});
}
return anim;
};
dojo.lfx.html.highlight=function(_5e1,_5e2,_5e3,_5e4,_5e5){
_5e1=dojo.lfx.html._byId(_5e1);
var _5e6=[];
dojo.lang.forEach(_5e1,function(node){
var _5e8=dojo.html.getBackgroundColor(node);
var bg=dojo.html.getStyle(node,"background-color").toLowerCase();
var _5ea=dojo.html.getStyle(node,"background-image");
var _5eb=(bg=="transparent"||bg=="rgba(0, 0, 0, 0)");
while(_5e8.length>3){
_5e8.pop();
}
var rgb=new dojo.gfx.color.Color(_5e2);
var _5ed=new dojo.gfx.color.Color(_5e8);
var anim=dojo.lfx.propertyAnimation(node,{"background-color":{start:rgb,end:_5ed}},_5e3,_5e4,{"beforeBegin":function(){
if(_5ea){
node.style.backgroundImage="none";
}
node.style.backgroundColor="rgb("+rgb.toRgb().join(",")+")";
},"onEnd":function(){
if(_5ea){
node.style.backgroundImage=_5ea;
}
if(_5eb){
node.style.backgroundColor="transparent";
}
if(_5e5){
_5e5(node,anim);
}
}});
_5e6.push(anim);
});
return dojo.lfx.combine(_5e6);
};
dojo.lfx.html.unhighlight=function(_5ef,_5f0,_5f1,_5f2,_5f3){
_5ef=dojo.lfx.html._byId(_5ef);
var _5f4=[];
dojo.lang.forEach(_5ef,function(node){
var _5f6=new dojo.gfx.color.Color(dojo.html.getBackgroundColor(node));
var rgb=new dojo.gfx.color.Color(_5f0);
var _5f8=dojo.html.getStyle(node,"background-image");
var anim=dojo.lfx.propertyAnimation(node,{"background-color":{start:_5f6,end:rgb}},_5f1,_5f2,{"beforeBegin":function(){
if(_5f8){
node.style.backgroundImage="none";
}
node.style.backgroundColor="rgb("+_5f6.toRgb().join(",")+")";
},"onEnd":function(){
if(_5f3){
_5f3(node,anim);
}
}});
_5f4.push(anim);
});
return dojo.lfx.combine(_5f4);
};
dojo.lang.mixin(dojo.lfx,dojo.lfx.html);
dojo.kwCompoundRequire({browser:["dojo.lfx.html"],dashboard:["dojo.lfx.html"]});
dojo.provide("dojo.lfx.*");
if(!this["dojo"]){
alert("\"dojo/__package__.js\" is now located at \"dojo/dojo.js\". Please update your includes accordingly");
}
dojo.provide("dojo.xml.Parse");
dojo.xml.Parse=function(){
var isIE=((dojo.render.html.capable)&&(dojo.render.html.ie));
function getTagName(node){
try{
return node.tagName.toLowerCase();
}
catch(e){
return "";
}
}
function getDojoTagName(node){
var _5fd=getTagName(node);
if(!_5fd){
return "";
}
if((dojo.widget)&&(dojo.widget.tags[_5fd])){
return _5fd;
}
var p=_5fd.indexOf(":");
if(p>=0){
return _5fd;
}
if(_5fd.substr(0,5)=="dojo:"){
return _5fd;
}
if(dojo.render.html.capable&&dojo.render.html.ie&&node.scopeName!="HTML"){
return node.scopeName.toLowerCase()+":"+_5fd;
}
if(_5fd.substr(0,4)=="dojo"){
return "dojo:"+_5fd.substring(4);
}
var djt=node.getAttribute("dojoType")||node.getAttribute("dojotype");
if(djt){
if(djt.indexOf(":")<0){
djt="dojo:"+djt;
}
return djt.toLowerCase();
}
djt=node.getAttributeNS&&node.getAttributeNS(dojo.dom.dojoml,"type");
if(djt){
return "dojo:"+djt.toLowerCase();
}
try{
djt=node.getAttribute("dojo:type");
}
catch(e){
}
if(djt){
return "dojo:"+djt.toLowerCase();
}
if((dj_global["djConfig"])&&(!djConfig["ignoreClassNames"])){
var _600=node.className||node.getAttribute("class");
if((_600)&&(_600.indexOf)&&(_600.indexOf("dojo-")!=-1)){
var _601=_600.split(" ");
for(var x=0,c=_601.length;x<c;x++){
if(_601[x].slice(0,5)=="dojo-"){
return "dojo:"+_601[x].substr(5).toLowerCase();
}
}
}
}
return "";
}
this.parseElement=function(node,_605,_606,_607){
var _608=getTagName(node);
if(isIE&&_608.indexOf("/")==0){
return null;
}
try{
var attr=node.getAttribute("parseWidgets");
if(attr&&attr.toLowerCase()=="false"){
return {};
}
}
catch(e){
}
var _60a=true;
if(_606){
var _60b=getDojoTagName(node);
_608=_60b||_608;
_60a=Boolean(_60b);
}
var _60c={};
_60c[_608]=[];
var pos=_608.indexOf(":");
if(pos>0){
var ns=_608.substring(0,pos);
_60c["ns"]=ns;
if((dojo.ns)&&(!dojo.ns.allow(ns))){
_60a=false;
}
}
if(_60a){
var _60f=this.parseAttributes(node);
for(var attr in _60f){
if((!_60c[_608][attr])||(typeof _60c[_608][attr]!="array")){
_60c[_608][attr]=[];
}
_60c[_608][attr].push(_60f[attr]);
}
_60c[_608].nodeRef=node;
_60c.tagName=_608;
_60c.index=_607||0;
}
var _610=0;
for(var i=0;i<node.childNodes.length;i++){
var tcn=node.childNodes.item(i);
switch(tcn.nodeType){
case dojo.dom.ELEMENT_NODE:
var ctn=getDojoTagName(tcn)||getTagName(tcn);
if(!_60c[ctn]){
_60c[ctn]=[];
}
_60c[ctn].push(this.parseElement(tcn,true,_606,_610));
if((tcn.childNodes.length==1)&&(tcn.childNodes.item(0).nodeType==dojo.dom.TEXT_NODE)){
_60c[ctn][_60c[ctn].length-1].value=tcn.childNodes.item(0).nodeValue;
}
_610++;
break;
case dojo.dom.TEXT_NODE:
if(node.childNodes.length==1){
_60c[_608].push({value:node.childNodes.item(0).nodeValue});
}
break;
default:
break;
}
}
return _60c;
};
this.parseAttributes=function(node){
var _615={};
var atts=node.attributes;
var _617,i=0;
while((_617=atts[i++])){
if(isIE){
if(!_617){
continue;
}
if((typeof _617=="object")&&(typeof _617.nodeValue=="undefined")||(_617.nodeValue==null)||(_617.nodeValue=="")){
continue;
}
}
var nn=_617.nodeName.split(":");
nn=(nn.length==2)?nn[1]:_617.nodeName;
_615[nn]={value:_617.nodeValue};
}
return _615;
};
};
dojo.provide("dojo.lang.declare");
dojo.lang.declare=function(_61a,_61b,init,_61d){
if((dojo.lang.isFunction(_61d))||((!_61d)&&(!dojo.lang.isFunction(init)))){
var temp=_61d;
_61d=init;
init=temp;
}
var _61f=[];
if(dojo.lang.isArray(_61b)){
_61f=_61b;
_61b=_61f.shift();
}
if(!init){
init=dojo.evalObjPath(_61a,false);
if((init)&&(!dojo.lang.isFunction(init))){
init=null;
}
}
var ctor=dojo.lang.declare._makeConstructor();
var scp=(_61b?_61b.prototype:null);
if(scp){
scp.prototyping=true;
ctor.prototype=new _61b();
scp.prototyping=false;
}
ctor.superclass=scp;
ctor.mixins=_61f;
for(var i=0,l=_61f.length;i<l;i++){
dojo.lang.extend(ctor,_61f[i].prototype);
}
ctor.prototype.initializer=null;
ctor.prototype.declaredClass=_61a;
if(dojo.lang.isArray(_61d)){
dojo.lang.extend.apply(dojo.lang,[ctor].concat(_61d));
}else{
dojo.lang.extend(ctor,(_61d)||{});
}
dojo.lang.extend(ctor,dojo.lang.declare._common);
ctor.prototype.constructor=ctor;
ctor.prototype.initializer=(ctor.prototype.initializer)||(init)||(function(){
});
var _624=dojo.parseObjPath(_61a,null,true);
_624.obj[_624.prop]=ctor;
return ctor;
};
dojo.lang.declare._makeConstructor=function(){
return function(){
var self=this._getPropContext();
var s=self.constructor.superclass;
if((s)&&(s.constructor)){
if(s.constructor==arguments.callee){
this._inherited("constructor",arguments);
}else{
this._contextMethod(s,"constructor",arguments);
}
}
var ms=(self.constructor.mixins)||([]);
for(var i=0,m;(m=ms[i]);i++){
(((m.prototype)&&(m.prototype.initializer))||(m)).apply(this,arguments);
}
if((!this.prototyping)&&(self.initializer)){
self.initializer.apply(this,arguments);
}
};
};
dojo.lang.declare._common={_getPropContext:function(){
return (this.___proto||this);
},_contextMethod:function(_62a,_62b,args){
var _62d,_62e=this.___proto;
this.___proto=_62a;
try{
_62d=_62a[_62b].apply(this,(args||[]));
}
catch(e){
throw e;
}
finally{
this.___proto=_62e;
}
return _62d;
},_inherited:function(prop,args){
var p=this._getPropContext();
do{
if((!p.constructor)||(!p.constructor.superclass)){
return;
}
p=p.constructor.superclass;
}while(!(prop in p));
return (dojo.lang.isFunction(p[prop])?this._contextMethod(p,prop,args):p[prop]);
},inherited:function(prop,args){
dojo.deprecated("'inherited' method is dangerous, do not up-call! 'inherited' is slated for removal in 0.5; name your super class (or use superclass property) instead.","0.5");
this._inherited(prop,args);
}};
dojo.declare=dojo.lang.declare;
dojo.provide("dojo.ns");
dojo.ns={namespaces:{},failed:{},loading:{},loaded:{},register:function(name,_635,_636,_637){
if(!_637||!this.namespaces[name]){
this.namespaces[name]=new dojo.ns.Ns(name,_635,_636);
}
},allow:function(name){
if(this.failed[name]){
return false;
}
if((djConfig.excludeNamespace)&&(dojo.lang.inArray(djConfig.excludeNamespace,name))){
return false;
}
return ((name==this.dojo)||(!djConfig.includeNamespace)||(dojo.lang.inArray(djConfig.includeNamespace,name)));
},get:function(name){
return this.namespaces[name];
},require:function(name){
var ns=this.namespaces[name];
if((ns)&&(this.loaded[name])){
return ns;
}
if(!this.allow(name)){
return false;
}
if(this.loading[name]){
dojo.debug("dojo.namespace.require: re-entrant request to load namespace \""+name+"\" must fail.");
return false;
}
var req=dojo.require;
this.loading[name]=true;
try{
if(name=="dojo"){
req("dojo.namespaces.dojo");
}else{
if(!dojo.hostenv.moduleHasPrefix(name)){
dojo.registerModulePath(name,"../"+name);
}
req([name,"manifest"].join("."),false,true);
}
if(!this.namespaces[name]){
this.failed[name]=true;
}
}
finally{
this.loading[name]=false;
}
return this.namespaces[name];
}};
dojo.ns.Ns=function(name,_63e,_63f){
this.name=name;
this.module=_63e;
this.resolver=_63f;
this._loaded=[];
this._failed=[];
};
dojo.ns.Ns.prototype.resolve=function(name,_641,_642){
if(!this.resolver||djConfig["skipAutoRequire"]){
return false;
}
var _643=this.resolver(name,_641);
if((_643)&&(!this._loaded[_643])&&(!this._failed[_643])){
var req=dojo.require;
req(_643,false,true);
if(dojo.hostenv.findModule(_643,false)){
this._loaded[_643]=true;
}else{
if(!_642){
dojo.raise("dojo.ns.Ns.resolve: module '"+_643+"' not found after loading via namespace '"+this.name+"'");
}
this._failed[_643]=true;
}
}
return Boolean(this._loaded[_643]);
};
dojo.registerNamespace=function(name,_646,_647){
dojo.ns.register.apply(dojo.ns,arguments);
};
dojo.registerNamespaceResolver=function(name,_649){
var n=dojo.ns.namespaces[name];
if(n){
n.resolver=_649;
}
};
dojo.registerNamespaceManifest=function(_64b,path,name,_64e,_64f){
dojo.registerModulePath(name,path);
dojo.registerNamespace(name,_64e,_64f);
};
dojo.registerNamespace("dojo","dojo.widget");
dojo.provide("dojo.widget.Manager");
dojo.widget.manager=new function(){
this.widgets=[];
this.widgetIds=[];
this.topWidgets={};
var _650={};
var _651=[];
this.getUniqueId=function(_652){
var _653;
do{
_653=_652+"_"+(_650[_652]!=undefined?++_650[_652]:_650[_652]=0);
}while(this.getWidgetById(_653));
return _653;
};
this.add=function(_654){
this.widgets.push(_654);
if(!_654.extraArgs["id"]){
_654.extraArgs["id"]=_654.extraArgs["ID"];
}
if(_654.widgetId==""){
if(_654["id"]){
_654.widgetId=_654["id"];
}else{
if(_654.extraArgs["id"]){
_654.widgetId=_654.extraArgs["id"];
}else{
_654.widgetId=this.getUniqueId(_654.ns+"_"+_654.widgetType);
}
}
}
if(this.widgetIds[_654.widgetId]){
dojo.debug("widget ID collision on ID: "+_654.widgetId);
}
this.widgetIds[_654.widgetId]=_654;
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
this.remove=function(_656){
if(dojo.lang.isNumber(_656)){
var tw=this.widgets[_656].widgetId;
delete this.topWidgets[tw];
delete this.widgetIds[tw];
this.widgets.splice(_656,1);
}else{
this.removeById(_656);
}
};
this.removeById=function(id){
if(!dojo.lang.isString(id)){
id=id["widgetId"];
if(!id){
dojo.debug("invalid widget or id passed to removeById");
return;
}
}
for(var i=0;i<this.widgets.length;i++){
if(this.widgets[i].widgetId==id){
this.remove(i);
break;
}
}
};
this.getWidgetById=function(id){
if(dojo.lang.isString(id)){
return this.widgetIds[id];
}
return id;
};
this.getWidgetsByType=function(type){
var lt=type.toLowerCase();
var _65d=(type.indexOf(":")<0?function(x){
return x.widgetType.toLowerCase();
}:function(x){
return x.getNamespacedType();
});
var ret=[];
dojo.lang.forEach(this.widgets,function(x){
if(_65d(x)==lt){
ret.push(x);
}
});
return ret;
};
this.getWidgetsByFilter=function(_662,_663){
var ret=[];
dojo.lang.every(this.widgets,function(x){
if(_662(x)){
ret.push(x);
if(_663){
return false;
}
}
return true;
});
return (_663?ret[0]:ret);
};
this.getAllWidgets=function(){
return this.widgets.concat();
};
this.getWidgetByNode=function(node){
var w=this.getAllWidgets();
node=dojo.byId(node);
for(var i=0;i<w.length;i++){
if(w[i].domNode==node){
return w[i];
}
}
return null;
};
this.byId=this.getWidgetById;
this.byType=this.getWidgetsByType;
this.byFilter=this.getWidgetsByFilter;
this.byNode=this.getWidgetByNode;
var _669={};
var _66a=["dojo.widget"];
for(var i=0;i<_66a.length;i++){
_66a[_66a[i]]=true;
}
this.registerWidgetPackage=function(_66c){
if(!_66a[_66c]){
_66a[_66c]=true;
_66a.push(_66c);
}
};
this.getWidgetPackageList=function(){
return dojo.lang.map(_66a,function(elt){
return (elt!==true?elt:undefined);
});
};
this.getImplementation=function(_66e,_66f,_670,ns){
var impl=this.getImplementationName(_66e,ns);
if(impl){
var ret=_66f?new impl(_66f):new impl();
return ret;
}
};
function buildPrefixCache(){
for(var _674 in dojo.render){
if(dojo.render[_674]["capable"]===true){
var _675=dojo.render[_674].prefixes;
for(var i=0;i<_675.length;i++){
_651.push(_675[i].toLowerCase());
}
}
}
}
var _677=function(_678,_679){
if(!_679){
return null;
}
for(var i=0,l=_651.length,_67c;i<=l;i++){
_67c=(i<l?_679[_651[i]]:_679);
if(!_67c){
continue;
}
for(var name in _67c){
if(name.toLowerCase()==_678){
return _67c[name];
}
}
}
return null;
};
var _67e=function(_67f,_680){
var _681=dojo.evalObjPath(_680,false);
return (_681?_677(_67f,_681):null);
};
this.getImplementationName=function(_682,ns){
var _684=_682.toLowerCase();
ns=ns||"dojo";
var imps=_669[ns]||(_669[ns]={});
var impl=imps[_684];
if(impl){
return impl;
}
if(!_651.length){
buildPrefixCache();
}
var _687=dojo.ns.get(ns);
if(!_687){
dojo.ns.register(ns,ns+".widget");
_687=dojo.ns.get(ns);
}
if(_687){
_687.resolve(_682);
}
impl=_67e(_684,_687.module);
if(impl){
return (imps[_684]=impl);
}
_687=dojo.ns.require(ns);
if((_687)&&(_687.resolver)){
_687.resolve(_682);
impl=_67e(_684,_687.module);
if(impl){
return (imps[_684]=impl);
}
}
dojo.deprecated("dojo.widget.Manager.getImplementationName","Could not locate widget implementation for \""+_682+"\" in \""+_687.module+"\" registered to namespace \""+_687.name+"\". "+"Developers must specify correct namespaces for all non-Dojo widgets","0.5");
for(var i=0;i<_66a.length;i++){
impl=_67e(_684,_66a[i]);
if(impl){
return (imps[_684]=impl);
}
}
throw new Error("Could not locate widget implementation for \""+_682+"\" in \""+_687.module+"\" registered to namespace \""+_687.name+"\"");
};
this.resizing=false;
this.onWindowResized=function(){
if(this.resizing){
return;
}
try{
this.resizing=true;
for(var id in this.topWidgets){
var _68a=this.topWidgets[id];
if(_68a.checkSize){
_68a.checkSize();
}
}
}
catch(e){
}
finally{
this.resizing=false;
}
};
if(typeof window!="undefined"){
dojo.addOnLoad(this,"onWindowResized");
dojo.event.connect(window,"onresize",this,"onWindowResized");
}
};
(function(){
var dw=dojo.widget;
var dwm=dw.manager;
var h=dojo.lang.curry(dojo.lang,"hitch",dwm);
var g=function(_68f,_690){
dw[(_690||_68f)]=h(_68f);
};
g("add","addWidget");
g("destroyAll","destroyAllWidgets");
g("remove","removeWidget");
g("removeById","removeWidgetById");
g("getWidgetById");
g("getWidgetById","byId");
g("getWidgetsByType");
g("getWidgetsByFilter");
g("getWidgetsByType","byType");
g("getWidgetsByFilter","byFilter");
g("getWidgetByNode","byNode");
dw.all=function(n){
var _692=dwm.getAllWidgets.apply(dwm,arguments);
if(arguments.length>0){
return _692[n];
}
return _692;
};
g("registerWidgetPackage");
g("getImplementation","getWidgetImplementation");
g("getImplementationName","getWidgetImplementationName");
dw.widgets=dwm.widgets;
dw.widgetIds=dwm.widgetIds;
dw.root=dwm.root;
})();
dojo.kwCompoundRequire({common:[["dojo.uri.Uri",false,false]]});
dojo.provide("dojo.uri.*");
dojo.provide("dojo.a11y");
dojo.a11y={imgPath:dojo.uri.moduleUri("dojo.widget","templates/images"),doAccessibleCheck:true,accessible:null,checkAccessible:function(){
if(this.accessible===null){
this.accessible=false;
if(this.doAccessibleCheck==true){
this.accessible=this.testAccessible();
}
}
return this.accessible;
},testAccessible:function(){
this.accessible=false;
if(dojo.render.html.ie||dojo.render.html.mozilla){
var div=document.createElement("div");
div.style.backgroundImage="url(\""+this.imgPath+"/tab_close.gif\")";
dojo.body().appendChild(div);
var _694=null;
if(window.getComputedStyle){
var _695=getComputedStyle(div,"");
_694=_695.getPropertyValue("background-image");
}else{
_694=div.currentStyle.backgroundImage;
}
var _696=false;
if(_694!=null&&(_694=="none"||_694=="url(invalid-url:)")){
this.accessible=true;
}
dojo.body().removeChild(div);
}
return this.accessible;
},setCheckAccessible:function(_697){
this.doAccessibleCheck=_697;
},setAccessibleMode:function(){
if(this.accessible===null){
if(this.checkAccessible()){
dojo.render.html.prefixes.unshift("a11y");
}
}
return this.accessible;
}};
dojo.provide("dojo.widget.Widget");
dojo.declare("dojo.widget.Widget",null,function(){
this.children=[];
this.extraArgs={};
},{parent:null,isTopLevel:false,disabled:false,isContainer:false,widgetId:"",widgetType:"Widget",ns:"dojo",getNamespacedType:function(){
return (this.ns?this.ns+":"+this.widgetType:this.widgetType).toLowerCase();
},toString:function(){
return "[Widget "+this.getNamespacedType()+", "+(this.widgetId||"NO ID")+"]";
},repr:function(){
return this.toString();
},enable:function(){
this.disabled=false;
},disable:function(){
this.disabled=true;
},onResized:function(){
this.notifyChildrenOfResize();
},notifyChildrenOfResize:function(){
for(var i=0;i<this.children.length;i++){
var _699=this.children[i];
if(_699.onResized){
_699.onResized();
}
}
},create:function(args,_69b,_69c,ns){
if(ns){
this.ns=ns;
}
this.satisfyPropertySets(args,_69b,_69c);
this.mixInProperties(args,_69b,_69c);
this.postMixInProperties(args,_69b,_69c);
dojo.widget.manager.add(this);
this.buildRendering(args,_69b,_69c);
this.initialize(args,_69b,_69c);
this.postInitialize(args,_69b,_69c);
this.postCreate(args,_69b,_69c);
return this;
},destroy:function(_69e){
if(this.parent){
this.parent.removeChild(this);
}
this.destroyChildren();
this.uninitialize();
this.destroyRendering(_69e);
dojo.widget.manager.removeById(this.widgetId);
},destroyChildren:function(){
var _69f;
var i=0;
while(this.children.length>i){
_69f=this.children[i];
if(_69f instanceof dojo.widget.Widget){
this.removeChild(_69f);
_69f.destroy();
continue;
}
i++;
}
},getChildrenOfType:function(type,_6a2){
var ret=[];
var _6a4=dojo.lang.isFunction(type);
if(!_6a4){
type=type.toLowerCase();
}
for(var x=0;x<this.children.length;x++){
if(_6a4){
if(this.children[x] instanceof type){
ret.push(this.children[x]);
}
}else{
if(this.children[x].widgetType.toLowerCase()==type){
ret.push(this.children[x]);
}
}
if(_6a2){
ret=ret.concat(this.children[x].getChildrenOfType(type,_6a2));
}
}
return ret;
},getDescendants:function(){
var _6a6=[];
var _6a7=[this];
var elem;
while((elem=_6a7.pop())){
_6a6.push(elem);
if(elem.children){
dojo.lang.forEach(elem.children,function(elem){
_6a7.push(elem);
});
}
}
return _6a6;
},isFirstChild:function(){
return this===this.parent.children[0];
},isLastChild:function(){
return this===this.parent.children[this.parent.children.length-1];
},satisfyPropertySets:function(args){
return args;
},mixInProperties:function(args,frag){
if((args["fastMixIn"])||(frag["fastMixIn"])){
for(var x in args){
this[x]=args[x];
}
return;
}
var _6ae;
var _6af=dojo.widget.lcArgsCache[this.widgetType];
if(_6af==null){
_6af={};
for(var y in this){
_6af[((new String(y)).toLowerCase())]=y;
}
dojo.widget.lcArgsCache[this.widgetType]=_6af;
}
var _6b1={};
for(var x in args){
if(!this[x]){
var y=_6af[(new String(x)).toLowerCase()];
if(y){
args[y]=args[x];
x=y;
}
}
if(_6b1[x]){
continue;
}
_6b1[x]=true;
if((typeof this[x])!=(typeof _6ae)){
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
if(args[x].search(/[^\w\.]+/i)==-1){
this[x]=dojo.evalObjPath(args[x],false);
}else{
var tn=dojo.lang.nameAnonFunc(new Function(args[x]),this);
dojo.event.kwConnect({srcObj:this,srcFunc:x,adviceObj:this,adviceFunc:tn});
}
}else{
if(dojo.lang.isArray(this[x])){
this[x]=args[x].split(";");
}else{
if(this[x] instanceof Date){
this[x]=new Date(Number(args[x]));
}else{
if(typeof this[x]=="object"){
if(this[x] instanceof dojo.uri.Uri){
this[x]=dojo.uri.dojoUri(args[x]);
}else{
var _6b3=args[x].split(";");
for(var y=0;y<_6b3.length;y++){
var si=_6b3[y].indexOf(":");
if((si!=-1)&&(_6b3[y].length>si)){
this[x][_6b3[y].substr(0,si).replace(/^\s+|\s+$/g,"")]=_6b3[y].substr(si+1);
}
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
this.extraArgs[x.toLowerCase()]=args[x];
}
}
},postMixInProperties:function(args,frag,_6b7){
},initialize:function(args,frag,_6ba){
return false;
},postInitialize:function(args,frag,_6bd){
return false;
},postCreate:function(args,frag,_6c0){
return false;
},uninitialize:function(){
return false;
},buildRendering:function(args,frag,_6c3){
dojo.unimplemented("dojo.widget.Widget.buildRendering, on "+this.toString()+", ");
return false;
},destroyRendering:function(){
dojo.unimplemented("dojo.widget.Widget.destroyRendering");
return false;
},addedTo:function(_6c4){
},addChild:function(_6c5){
dojo.unimplemented("dojo.widget.Widget.addChild");
return false;
},removeChild:function(_6c6){
for(var x=0;x<this.children.length;x++){
if(this.children[x]===_6c6){
this.children.splice(x,1);
_6c6.parent=null;
break;
}
}
return _6c6;
},getPreviousSibling:function(){
var idx=this.getParentIndex();
if(idx<=0){
return null;
}
return this.parent.children[idx-1];
},getSiblings:function(){
return this.parent.children;
},getParentIndex:function(){
return dojo.lang.indexOf(this.parent.children,this,true);
},getNextSibling:function(){
var idx=this.getParentIndex();
if(idx==this.parent.children.length-1){
return null;
}
if(idx<0){
return null;
}
return this.parent.children[idx+1];
}});
dojo.widget.lcArgsCache={};
dojo.widget.tags={};
dojo.widget.tags.addParseTreeHandler=function(type){
dojo.deprecated("addParseTreeHandler",". ParseTreeHandlers are now reserved for components. Any unfiltered DojoML tag without a ParseTreeHandler is assumed to be a widget","0.5");
};
dojo.widget.tags["dojo:propertyset"]=function(_6cb,_6cc,_6cd){
var _6ce=_6cc.parseProperties(_6cb["dojo:propertyset"]);
};
dojo.widget.tags["dojo:connect"]=function(_6cf,_6d0,_6d1){
var _6d2=_6d0.parseProperties(_6cf["dojo:connect"]);
};
dojo.widget.buildWidgetFromParseTree=function(type,frag,_6d5,_6d6,_6d7,_6d8){
dojo.a11y.setAccessibleMode();
var _6d9=type.split(":");
_6d9=(_6d9.length==2)?_6d9[1]:type;
var _6da=_6d8||_6d5.parseProperties(frag[frag["ns"]+":"+_6d9]);
var _6db=dojo.widget.manager.getImplementation(_6d9,null,null,frag["ns"]);
if(!_6db){
throw new Error("cannot find \""+type+"\" widget");
}else{
if(!_6db.create){
throw new Error("\""+type+"\" widget object has no \"create\" method and does not appear to implement *Widget");
}
}
_6da["dojoinsertionindex"]=_6d7;
var ret=_6db.create(_6da,frag,_6d6,frag["ns"]);
return ret;
};
dojo.widget.defineWidget=function(_6dd,_6de,_6df,init,_6e1){
if(dojo.lang.isString(arguments[3])){
dojo.widget._defineWidget(arguments[0],arguments[3],arguments[1],arguments[4],arguments[2]);
}else{
var args=[arguments[0]],p=3;
if(dojo.lang.isString(arguments[1])){
args.push(arguments[1],arguments[2]);
}else{
args.push("",arguments[1]);
p=2;
}
if(dojo.lang.isFunction(arguments[p])){
args.push(arguments[p],arguments[p+1]);
}else{
args.push(null,arguments[p]);
}
dojo.widget._defineWidget.apply(this,args);
}
};
dojo.widget.defineWidget.renderers="html|svg|vml";
dojo.widget._defineWidget=function(_6e4,_6e5,_6e6,init,_6e8){
var _6e9=_6e4.split(".");
var type=_6e9.pop();
var regx="\\.("+(_6e5?_6e5+"|":"")+dojo.widget.defineWidget.renderers+")\\.";
var r=_6e4.search(new RegExp(regx));
_6e9=(r<0?_6e9.join("."):_6e4.substr(0,r));
dojo.widget.manager.registerWidgetPackage(_6e9);
var pos=_6e9.indexOf(".");
var _6ee=(pos>-1)?_6e9.substring(0,pos):_6e9;
_6e8=(_6e8)||{};
_6e8.widgetType=type;
if((!init)&&(_6e8["classConstructor"])){
init=_6e8.classConstructor;
delete _6e8.classConstructor;
}
dojo.declare(_6e4,_6e6,init,_6e8);
};
dojo.provide("dojo.widget.Parse");
dojo.widget.Parse=function(_6ef){
this.propertySetsList=[];
this.fragment=_6ef;
this.createComponents=function(frag,_6f1){
var _6f2=[];
var _6f3=false;
try{
if(frag&&frag.tagName&&(frag!=frag.nodeRef)){
var _6f4=dojo.widget.tags;
var tna=String(frag.tagName).split(";");
for(var x=0;x<tna.length;x++){
var ltn=tna[x].replace(/^\s+|\s+$/g,"").toLowerCase();
frag.tagName=ltn;
var ret;
if(_6f4[ltn]){
_6f3=true;
ret=_6f4[ltn](frag,this,_6f1,frag.index);
_6f2.push(ret);
}else{
if(ltn.indexOf(":")==-1){
ltn="dojo:"+ltn;
}
ret=dojo.widget.buildWidgetFromParseTree(ltn,frag,this,_6f1,frag.index);
if(ret){
_6f3=true;
_6f2.push(ret);
}
}
}
}
}
catch(e){
dojo.debug("dojo.widget.Parse: error:",e);
}
if(!_6f3){
_6f2=_6f2.concat(this.createSubComponents(frag,_6f1));
}
return _6f2;
};
this.createSubComponents=function(_6f9,_6fa){
var frag,_6fc=[];
for(var item in _6f9){
frag=_6f9[item];
if(frag&&typeof frag=="object"&&(frag!=_6f9.nodeRef)&&(frag!=_6f9.tagName)&&(!dojo.dom.isNode(frag))){
_6fc=_6fc.concat(this.createComponents(frag,_6fa));
}
}
return _6fc;
};
this.parsePropertySets=function(_6fe){
return [];
};
this.parseProperties=function(_6ff){
var _700={};
for(var item in _6ff){
if((_6ff[item]==_6ff.tagName)||(_6ff[item]==_6ff.nodeRef)){
}else{
var frag=_6ff[item];
if(frag.tagName&&dojo.widget.tags[frag.tagName.toLowerCase()]){
}else{
if(frag[0]&&frag[0].value!=""&&frag[0].value!=null){
try{
if(item.toLowerCase()=="dataprovider"){
var _703=this;
this.getDataProvider(_703,frag[0].value);
_700.dataProvider=this.dataProvider;
}
_700[item]=frag[0].value;
var _704=this.parseProperties(frag);
for(var _705 in _704){
_700[_705]=_704[_705];
}
}
catch(e){
dojo.debug(e);
}
}
}
switch(item.toLowerCase()){
case "checked":
case "disabled":
if(typeof _700[item]!="boolean"){
_700[item]=true;
}
break;
}
}
}
return _700;
};
this.getDataProvider=function(_706,_707){
dojo.io.bind({url:_707,load:function(type,_709){
if(type=="load"){
_706.dataProvider=_709;
}
},mimetype:"text/javascript",sync:true});
};
this.getPropertySetById=function(_70a){
for(var x=0;x<this.propertySetsList.length;x++){
if(_70a==this.propertySetsList[x]["id"][0].value){
return this.propertySetsList[x];
}
}
return "";
};
this.getPropertySetsByType=function(_70c){
var _70d=[];
for(var x=0;x<this.propertySetsList.length;x++){
var cpl=this.propertySetsList[x];
var cpcc=cpl.componentClass||cpl.componentType||null;
var _711=this.propertySetsList[x]["id"][0].value;
if(cpcc&&(_711==cpcc[0].value)){
_70d.push(cpl);
}
}
return _70d;
};
this.getPropertySets=function(_712){
var ppl="dojo:propertyproviderlist";
var _714=[];
var _715=_712.tagName;
if(_712[ppl]){
var _716=_712[ppl].value.split(" ");
for(var _717 in _716){
if((_717.indexOf("..")==-1)&&(_717.indexOf("://")==-1)){
var _718=this.getPropertySetById(_717);
if(_718!=""){
_714.push(_718);
}
}else{
}
}
}
return this.getPropertySetsByType(_715).concat(_714);
};
this.createComponentFromScript=function(_719,_71a,_71b,ns){
_71b.fastMixIn=true;
var ltn=(ns||"dojo")+":"+_71a.toLowerCase();
if(dojo.widget.tags[ltn]){
return [dojo.widget.tags[ltn](_71b,this,null,null,_71b)];
}
return [dojo.widget.buildWidgetFromParseTree(ltn,_71b,this,null,null,_71b)];
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
dojo.widget.createWidget=function(name,_720,_721,_722){
var _723=false;
var _724=(typeof name=="string");
if(_724){
var pos=name.indexOf(":");
var ns=(pos>-1)?name.substring(0,pos):"dojo";
if(pos>-1){
name=name.substring(pos+1);
}
var _727=name.toLowerCase();
var _728=ns+":"+_727;
_723=(dojo.byId(name)&&!dojo.widget.tags[_728]);
}
if((arguments.length==1)&&(_723||!_724)){
var xp=new dojo.xml.Parse();
var tn=_723?dojo.byId(name):name;
return dojo.widget.getParser().createComponents(xp.parseElement(tn,null,true))[0];
}
function fromScript(_72b,name,_72d,ns){
_72d[_728]={dojotype:[{value:_727}],nodeRef:_72b,fastMixIn:true};
_72d.ns=ns;
return dojo.widget.getParser().createComponentFromScript(_72b,name,_72d,ns);
}
_720=_720||{};
var _72f=false;
var tn=null;
var h=dojo.render.html.capable;
if(h){
tn=document.createElement("span");
}
if(!_721){
_72f=true;
_721=tn;
if(h){
dojo.body().appendChild(_721);
}
}else{
if(_722){
dojo.dom.insertAtPosition(tn,_721,_722);
}else{
tn=_721;
}
}
var _731=fromScript(tn,name.toLowerCase(),_720,ns);
if((!_731)||(!_731[0])||(typeof _731[0].widgetType=="undefined")){
throw new Error("createWidget: Creation of \""+name+"\" widget failed.");
}
try{
if(_72f&&_731[0].domNode.parentNode){
_731[0].domNode.parentNode.removeChild(_731[0].domNode);
}
}
catch(e){
dojo.debug(e);
}
return _731[0];
};
dojo.provide("dojo.widget.DomWidget");
dojo.widget._cssFiles={};
dojo.widget._cssStrings={};
dojo.widget._templateCache={};
dojo.widget.defaultStrings={dojoRoot:dojo.hostenv.getBaseScriptUri(),dojoWidgetModuleUri:dojo.uri.moduleUri("dojo.widget"),baseScriptUri:dojo.hostenv.getBaseScriptUri()};
dojo.widget.fillFromTemplateCache=function(obj,_733,_734,_735){
var _736=_733||obj.templatePath;
var _737=dojo.widget._templateCache;
if(!_736&&!obj["widgetType"]){
do{
var _738="__dummyTemplate__"+dojo.widget._templateCache.dummyCount++;
}while(_737[_738]);
obj.widgetType=_738;
}
var wt=_736?_736.toString():obj.widgetType;
var ts=_737[wt];
if(!ts){
_737[wt]={"string":null,"node":null};
if(_735){
ts={};
}else{
ts=_737[wt];
}
}
if((!obj.templateString)&&(!_735)){
obj.templateString=_734||ts["string"];
}
if(obj.templateString){
obj.templateString=this._sanitizeTemplateString(obj.templateString);
}
if((!obj.templateNode)&&(!_735)){
obj.templateNode=ts["node"];
}
if((!obj.templateNode)&&(!obj.templateString)&&(_736)){
var _73b=this._sanitizeTemplateString(dojo.hostenv.getText(_736));
obj.templateString=_73b;
if(!_735){
_737[wt]["string"]=_73b;
}
}
if((!ts["string"])&&(!_735)){
ts.string=obj.templateString;
}
};
dojo.widget._sanitizeTemplateString=function(_73c){
if(_73c){
_73c=_73c.replace(/^\s*<\?xml(\s)+version=[\'\"](\d)*.(\d)*[\'\"](\s)*\?>/im,"");
var _73d=_73c.match(/<body[^>]*>\s*([\s\S]+)\s*<\/body>/im);
if(_73d){
_73c=_73d[1];
}
}else{
_73c="";
}
return _73c;
};
dojo.widget._templateCache.dummyCount=0;
dojo.widget.attachProperties=["dojoAttachPoint","id"];
dojo.widget.eventAttachProperty="dojoAttachEvent";
dojo.widget.onBuildProperty="dojoOnBuild";
dojo.widget.waiNames=["waiRole","waiState"];
dojo.widget.wai={waiRole:{name:"waiRole","namespace":"http://www.w3.org/TR/xhtml2",alias:"x2",prefix:"wairole:"},waiState:{name:"waiState","namespace":"http://www.w3.org/2005/07/aaa",alias:"aaa",prefix:""},setAttr:function(node,ns,attr,_741){
if(dojo.render.html.ie){
node.setAttribute(this[ns].alias+":"+attr,this[ns].prefix+_741);
}else{
node.setAttributeNS(this[ns]["namespace"],attr,this[ns].prefix+_741);
}
},getAttr:function(node,ns,attr){
if(dojo.render.html.ie){
return node.getAttribute(this[ns].alias+":"+attr);
}else{
return node.getAttributeNS(this[ns]["namespace"],attr);
}
},removeAttr:function(node,ns,attr){
var _748=true;
if(dojo.render.html.ie){
_748=node.removeAttribute(this[ns].alias+":"+attr);
}else{
node.removeAttributeNS(this[ns]["namespace"],attr);
}
return _748;
}};
dojo.widget.attachTemplateNodes=function(_749,_74a,_74b){
var _74c=dojo.dom.ELEMENT_NODE;
function trim(str){
return str.replace(/^\s+|\s+$/g,"");
}
if(!_749){
_749=_74a.domNode;
}
if(_749.nodeType!=_74c){
return;
}
var _74e=_749.all||_749.getElementsByTagName("*");
var _74f=_74a;
for(var x=-1;x<_74e.length;x++){
var _751=(x==-1)?_749:_74e[x];
var _752=[];
if(!_74a.widgetsInTemplate||!_751.getAttribute("dojoType")){
for(var y=0;y<this.attachProperties.length;y++){
var _754=_751.getAttribute(this.attachProperties[y]);
if(_754){
_752=_754.split(";");
for(var z=0;z<_752.length;z++){
if(dojo.lang.isArray(_74a[_752[z]])){
_74a[_752[z]].push(_751);
}else{
_74a[_752[z]]=_751;
}
}
break;
}
}
var _756=_751.getAttribute(this.eventAttachProperty);
if(_756){
var evts=_756.split(";");
for(var y=0;y<evts.length;y++){
if((!evts[y])||(!evts[y].length)){
continue;
}
var _758=null;
var tevt=trim(evts[y]);
if(evts[y].indexOf(":")>=0){
var _75a=tevt.split(":");
tevt=trim(_75a[0]);
_758=trim(_75a[1]);
}
if(!_758){
_758=tevt;
}
var tf=function(){
var ntf=new String(_758);
return function(evt){
if(_74f[ntf]){
_74f[ntf](dojo.event.browser.fixEvent(evt,this));
}
};
}();
dojo.event.browser.addListener(_751,tevt,tf,false,true);
}
}
for(var y=0;y<_74b.length;y++){
var _75e=_751.getAttribute(_74b[y]);
if((_75e)&&(_75e.length)){
var _758=null;
var _75f=_74b[y].substr(4);
_758=trim(_75e);
var _760=[_758];
if(_758.indexOf(";")>=0){
_760=dojo.lang.map(_758.split(";"),trim);
}
for(var z=0;z<_760.length;z++){
if(!_760[z].length){
continue;
}
var tf=function(){
var ntf=new String(_760[z]);
return function(evt){
if(_74f[ntf]){
_74f[ntf](dojo.event.browser.fixEvent(evt,this));
}
};
}();
dojo.event.browser.addListener(_751,_75f,tf,false,true);
}
}
}
}
var _763=_751.getAttribute(this.templateProperty);
if(_763){
_74a[_763]=_751;
}
dojo.lang.forEach(dojo.widget.waiNames,function(name){
var wai=dojo.widget.wai[name];
var val=_751.getAttribute(wai.name);
if(val){
if(val.indexOf("-")==-1){
dojo.widget.wai.setAttr(_751,wai.name,"role",val);
}else{
var _767=val.split("-");
dojo.widget.wai.setAttr(_751,wai.name,_767[0],_767[1]);
}
}
},this);
var _768=_751.getAttribute(this.onBuildProperty);
if(_768){
eval("var node = baseNode; var widget = targetObj; "+_768);
}
}
};
dojo.widget.getDojoEventsFromStr=function(str){
var re=/(dojoOn([a-z]+)(\s?))=/gi;
var evts=str?str.match(re)||[]:[];
var ret=[];
var lem={};
for(var x=0;x<evts.length;x++){
if(evts[x].length<1){
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
dojo.declare("dojo.widget.DomWidget",dojo.widget.Widget,function(){
if((arguments.length>0)&&(typeof arguments[0]=="object")){
this.create(arguments[0]);
}
},{templateNode:null,templateString:null,templateCssString:null,preventClobber:false,domNode:null,containerNode:null,widgetsInTemplate:false,addChild:function(_770,_771,pos,ref,_774){
if(!this.isContainer){
dojo.debug("dojo.widget.DomWidget.addChild() attempted on non-container widget");
return null;
}else{
if(_774==undefined){
_774=this.children.length;
}
this.addWidgetAsDirectChild(_770,_771,pos,ref,_774);
this.registerChild(_770,_774);
}
return _770;
},addWidgetAsDirectChild:function(_775,_776,pos,ref,_779){
if((!this.containerNode)&&(!_776)){
this.containerNode=this.domNode;
}
var cn=(_776)?_776:this.containerNode;
if(!pos){
pos="after";
}
if(!ref){
if(!cn){
cn=dojo.body();
}
ref=cn.lastChild;
}
if(!_779){
_779=0;
}
_775.domNode.setAttribute("dojoinsertionindex",_779);
if(!ref){
cn.appendChild(_775.domNode);
}else{
if(pos=="insertAtIndex"){
dojo.dom.insertAtIndex(_775.domNode,ref.parentNode,_779);
}else{
if((pos=="after")&&(ref===cn.lastChild)){
cn.appendChild(_775.domNode);
}else{
dojo.dom.insertAtPosition(_775.domNode,cn,pos);
}
}
}
},registerChild:function(_77b,_77c){
_77b.dojoInsertionIndex=_77c;
var idx=-1;
for(var i=0;i<this.children.length;i++){
if(this.children[i].dojoInsertionIndex<=_77c){
idx=i;
}
}
this.children.splice(idx+1,0,_77b);
_77b.parent=this;
_77b.addedTo(this,idx+1);
delete dojo.widget.manager.topWidgets[_77b.widgetId];
},removeChild:function(_77f){
dojo.dom.removeNode(_77f.domNode);
return dojo.widget.DomWidget.superclass.removeChild.call(this,_77f);
},getFragNodeRef:function(frag){
if(!frag){
return null;
}
if(!frag[this.getNamespacedType()]){
dojo.raise("Error: no frag for widget type "+this.getNamespacedType()+", id "+this.widgetId+" (maybe a widget has set it's type incorrectly)");
}
return frag[this.getNamespacedType()]["nodeRef"];
},postInitialize:function(args,frag,_783){
var _784=this.getFragNodeRef(frag);
if(_783&&(_783.snarfChildDomOutput||!_784)){
_783.addWidgetAsDirectChild(this,"","insertAtIndex","",args["dojoinsertionindex"],_784);
}else{
if(_784){
if(this.domNode&&(this.domNode!==_784)){
this._sourceNodeRef=dojo.dom.replaceNode(_784,this.domNode);
}
}
}
if(_783){
_783.registerChild(this,args.dojoinsertionindex);
}else{
dojo.widget.manager.topWidgets[this.widgetId]=this;
}
if(this.widgetsInTemplate){
var _785=new dojo.xml.Parse();
var _786;
var _787=this.domNode.getElementsByTagName("*");
for(var i=0;i<_787.length;i++){
if(_787[i].getAttribute("dojoAttachPoint")=="subContainerWidget"){
_786=_787[i];
}
if(_787[i].getAttribute("dojoType")){
_787[i].setAttribute("isSubWidget",true);
}
}
if(this.isContainer&&!this.containerNode){
if(_786){
var src=this.getFragNodeRef(frag);
if(src){
dojo.dom.moveChildren(src,_786);
frag["dojoDontFollow"]=true;
}
}else{
dojo.debug("No subContainerWidget node can be found in template file for widget "+this);
}
}
var _78a=_785.parseElement(this.domNode,null,true);
dojo.widget.getParser().createSubComponents(_78a,this);
var _78b=[];
var _78c=[this];
var w;
while((w=_78c.pop())){
for(var i=0;i<w.children.length;i++){
var _78e=w.children[i];
if(_78e._processedSubWidgets||!_78e.extraArgs["issubwidget"]){
continue;
}
_78b.push(_78e);
if(_78e.isContainer){
_78c.push(_78e);
}
}
}
for(var i=0;i<_78b.length;i++){
var _78f=_78b[i];
if(_78f._processedSubWidgets){
dojo.debug("This should not happen: widget._processedSubWidgets is already true!");
return;
}
_78f._processedSubWidgets=true;
if(_78f.extraArgs["dojoattachevent"]){
var evts=_78f.extraArgs["dojoattachevent"].split(";");
for(var j=0;j<evts.length;j++){
var _792=null;
var tevt=dojo.string.trim(evts[j]);
if(tevt.indexOf(":")>=0){
var _794=tevt.split(":");
tevt=dojo.string.trim(_794[0]);
_792=dojo.string.trim(_794[1]);
}
if(!_792){
_792=tevt;
}
if(dojo.lang.isFunction(_78f[tevt])){
dojo.event.kwConnect({srcObj:_78f,srcFunc:tevt,targetObj:this,targetFunc:_792});
}else{
alert(tevt+" is not a function in widget "+_78f);
}
}
}
if(_78f.extraArgs["dojoattachpoint"]){
this[_78f.extraArgs["dojoattachpoint"]]=_78f;
}
}
}
if(this.isContainer&&!frag["dojoDontFollow"]){
dojo.widget.getParser().createSubComponents(frag,this);
}
},buildRendering:function(args,frag){
var ts=dojo.widget._templateCache[this.widgetType];
if(args["templatecsspath"]){
args["templateCssPath"]=args["templatecsspath"];
}
var _798=args["templateCssPath"]||this.templateCssPath;
if(_798&&!dojo.widget._cssFiles[_798.toString()]){
if((!this.templateCssString)&&(_798)){
this.templateCssString=dojo.hostenv.getText(_798);
this.templateCssPath=null;
}
dojo.widget._cssFiles[_798.toString()]=true;
}
if((this["templateCssString"])&&(!dojo.widget._cssStrings[this.templateCssString])){
dojo.html.insertCssText(this.templateCssString,null,_798);
dojo.widget._cssStrings[this.templateCssString]=true;
}
if((!this.preventClobber)&&((this.templatePath)||(this.templateNode)||((this["templateString"])&&(this.templateString.length))||((typeof ts!="undefined")&&((ts["string"])||(ts["node"]))))){
this.buildFromTemplate(args,frag);
}else{
this.domNode=this.getFragNodeRef(frag);
}
this.fillInTemplate(args,frag);
},buildFromTemplate:function(args,frag){
var _79b=false;
if(args["templatepath"]){
args["templatePath"]=args["templatepath"];
}
dojo.widget.fillFromTemplateCache(this,args["templatePath"],null,_79b);
var ts=dojo.widget._templateCache[this.templatePath?this.templatePath.toString():this.widgetType];
if((ts)&&(!_79b)){
if(!this.templateString.length){
this.templateString=ts["string"];
}
if(!this.templateNode){
this.templateNode=ts["node"];
}
}
var _79d=false;
var node=null;
var tstr=this.templateString;
if((!this.templateNode)&&(this.templateString)){
_79d=this.templateString.match(/\$\{([^\}]+)\}/g);
if(_79d){
var hash=this.strings||{};
for(var key in dojo.widget.defaultStrings){
if(dojo.lang.isUndefined(hash[key])){
hash[key]=dojo.widget.defaultStrings[key];
}
}
for(var i=0;i<_79d.length;i++){
var key=_79d[i];
key=key.substring(2,key.length-1);
var kval=(key.substring(0,5)=="this.")?dojo.lang.getObjPathValue(key.substring(5),this):hash[key];
var _7a4;
if((kval)||(dojo.lang.isString(kval))){
_7a4=new String((dojo.lang.isFunction(kval))?kval.call(this,key,this.templateString):kval);
while(_7a4.indexOf("\"")>-1){
_7a4=_7a4.replace("\"","&quot;");
}
tstr=tstr.replace(_79d[i],_7a4);
}
}
}else{
this.templateNode=this.createNodesFromText(this.templateString,true)[0];
if(!_79b){
ts.node=this.templateNode;
}
}
}
if((!this.templateNode)&&(!_79d)){
dojo.debug("DomWidget.buildFromTemplate: could not create template");
return false;
}else{
if(!_79d){
node=this.templateNode.cloneNode(true);
if(!node){
return false;
}
}else{
node=this.createNodesFromText(tstr,true)[0];
}
}
this.domNode=node;
this.attachTemplateNodes();
if(this.isContainer&&this.containerNode){
var src=this.getFragNodeRef(frag);
if(src){
dojo.dom.moveChildren(src,this.containerNode);
}
}
},attachTemplateNodes:function(_7a6,_7a7){
if(!_7a6){
_7a6=this.domNode;
}
if(!_7a7){
_7a7=this;
}
return dojo.widget.attachTemplateNodes(_7a6,_7a7,dojo.widget.getDojoEventsFromStr(this.templateString));
},fillInTemplate:function(){
},destroyRendering:function(){
try{
dojo.dom.destroyNode(this.domNode);
delete this.domNode;
}
catch(e){
}
if(this._sourceNodeRef){
try{
dojo.dom.destroyNode(this._sourceNodeRef);
}
catch(e){
}
}
},createNodesFromText:function(){
dojo.unimplemented("dojo.widget.DomWidget.createNodesFromText");
}});
dojo.provide("dojo.html.util");
dojo.html.getElementWindow=function(_7a8){
return dojo.html.getDocumentWindow(_7a8.ownerDocument);
};
dojo.html.getDocumentWindow=function(doc){
if(dojo.render.html.safari&&!doc._parentWindow){
var fix=function(win){
win.document._parentWindow=win;
for(var i=0;i<win.frames.length;i++){
fix(win.frames[i]);
}
};
fix(window.top);
}
if(dojo.render.html.ie&&window!==document.parentWindow&&!doc._parentWindow){
doc.parentWindow.execScript("document._parentWindow = window;","Javascript");
var win=doc._parentWindow;
doc._parentWindow=null;
return win;
}
return doc._parentWindow||doc.parentWindow||doc.defaultView;
};
dojo.html.gravity=function(node,e){
node=dojo.byId(node);
var _7b0=dojo.html.getCursorPosition(e);
with(dojo.html){
var _7b1=getAbsolutePosition(node,true);
var bb=getBorderBox(node);
var _7b3=_7b1.x+(bb.width/2);
var _7b4=_7b1.y+(bb.height/2);
}
with(dojo.html.gravity){
return ((_7b0.x<_7b3?WEST:EAST)|(_7b0.y<_7b4?NORTH:SOUTH));
}
};
dojo.html.gravity.NORTH=1;
dojo.html.gravity.SOUTH=1<<1;
dojo.html.gravity.EAST=1<<2;
dojo.html.gravity.WEST=1<<3;
dojo.html.overElement=function(_7b5,e){
_7b5=dojo.byId(_7b5);
var _7b7=dojo.html.getCursorPosition(e);
var bb=dojo.html.getBorderBox(_7b5);
var _7b9=dojo.html.getAbsolutePosition(_7b5,true,dojo.html.boxSizing.BORDER_BOX);
var top=_7b9.y;
var _7bb=top+bb.height;
var left=_7b9.x;
var _7bd=left+bb.width;
return (_7b7.x>=left&&_7b7.x<=_7bd&&_7b7.y>=top&&_7b7.y<=_7bb);
};
dojo.html.renderedTextContent=function(node){
node=dojo.byId(node);
var _7bf="";
if(node==null){
return _7bf;
}
for(var i=0;i<node.childNodes.length;i++){
switch(node.childNodes[i].nodeType){
case 1:
case 5:
var _7c1="unknown";
try{
_7c1=dojo.html.getStyle(node.childNodes[i],"display");
}
catch(E){
}
switch(_7c1){
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
_7bf+="\n";
_7bf+=dojo.html.renderedTextContent(node.childNodes[i]);
_7bf+="\n";
break;
case "none":
break;
default:
if(node.childNodes[i].tagName&&node.childNodes[i].tagName.toLowerCase()=="br"){
_7bf+="\n";
}else{
_7bf+=dojo.html.renderedTextContent(node.childNodes[i]);
}
break;
}
break;
case 3:
case 2:
case 4:
var text=node.childNodes[i].nodeValue;
var _7c3="unknown";
try{
_7c3=dojo.html.getStyle(node,"text-transform");
}
catch(E){
}
switch(_7c3){
case "capitalize":
var _7c4=text.split(" ");
for(var i=0;i<_7c4.length;i++){
_7c4[i]=_7c4[i].charAt(0).toUpperCase()+_7c4[i].substring(1);
}
text=_7c4.join(" ");
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
switch(_7c3){
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
if(/\s$/.test(_7bf)){
text.replace(/^\s/,"");
}
break;
}
_7bf+=text;
break;
default:
break;
}
}
return _7bf;
};
dojo.html.createNodesFromText=function(txt,trim){
if(trim){
txt=txt.replace(/^\s+|\s+$/g,"");
}
var tn=dojo.doc().createElement("div");
tn.style.visibility="hidden";
dojo.body().appendChild(tn);
var _7c8="none";
if((/^<t[dh][\s\r\n>]/i).test(txt.replace(/^\s+/))){
txt="<table><tbody><tr>"+txt+"</tr></tbody></table>";
_7c8="cell";
}else{
if((/^<tr[\s\r\n>]/i).test(txt.replace(/^\s+/))){
txt="<table><tbody>"+txt+"</tbody></table>";
_7c8="row";
}else{
if((/^<(thead|tbody|tfoot)[\s\r\n>]/i).test(txt.replace(/^\s+/))){
txt="<table>"+txt+"</table>";
_7c8="section";
}
}
}
tn.innerHTML=txt;
if(tn["normalize"]){
tn.normalize();
}
var _7c9=null;
switch(_7c8){
case "cell":
_7c9=tn.getElementsByTagName("tr")[0];
break;
case "row":
_7c9=tn.getElementsByTagName("tbody")[0];
break;
case "section":
_7c9=tn.getElementsByTagName("table")[0];
break;
default:
_7c9=tn;
break;
}
var _7ca=[];
for(var x=0;x<_7c9.childNodes.length;x++){
_7ca.push(_7c9.childNodes[x].cloneNode(true));
}
tn.style.display="none";
dojo.html.destroyNode(tn);
return _7ca;
};
dojo.html.placeOnScreen=function(node,_7cd,_7ce,_7cf,_7d0,_7d1,_7d2){
if(_7cd instanceof Array||typeof _7cd=="array"){
_7d2=_7d1;
_7d1=_7d0;
_7d0=_7cf;
_7cf=_7ce;
_7ce=_7cd[1];
_7cd=_7cd[0];
}
if(_7d1 instanceof String||typeof _7d1=="string"){
_7d1=_7d1.split(",");
}
if(!isNaN(_7cf)){
_7cf=[Number(_7cf),Number(_7cf)];
}else{
if(!(_7cf instanceof Array||typeof _7cf=="array")){
_7cf=[0,0];
}
}
var _7d3=dojo.html.getScroll().offset;
var view=dojo.html.getViewport();
node=dojo.byId(node);
var _7d5=node.style.display;
node.style.display="";
var bb=dojo.html.getBorderBox(node);
var w=bb.width;
var h=bb.height;
node.style.display=_7d5;
if(!(_7d1 instanceof Array||typeof _7d1=="array")){
_7d1=["TL"];
}
var _7d9,_7da,_7db=Infinity,_7dc;
for(var _7dd=0;_7dd<_7d1.length;++_7dd){
var _7de=_7d1[_7dd];
var _7df=true;
var tryX=_7cd-(_7de.charAt(1)=="L"?0:w)+_7cf[0]*(_7de.charAt(1)=="L"?1:-1);
var tryY=_7ce-(_7de.charAt(0)=="T"?0:h)+_7cf[1]*(_7de.charAt(0)=="T"?1:-1);
if(_7d0){
tryX-=_7d3.x;
tryY-=_7d3.y;
}
if(tryX<0){
tryX=0;
_7df=false;
}
if(tryY<0){
tryY=0;
_7df=false;
}
var x=tryX+w;
if(x>view.width){
x=view.width-w;
_7df=false;
}else{
x=tryX;
}
x=Math.max(_7cf[0],x)+_7d3.x;
var y=tryY+h;
if(y>view.height){
y=view.height-h;
_7df=false;
}else{
y=tryY;
}
y=Math.max(_7cf[1],y)+_7d3.y;
if(_7df){
_7d9=x;
_7da=y;
_7db=0;
_7dc=_7de;
break;
}else{
var dist=Math.pow(x-tryX-_7d3.x,2)+Math.pow(y-tryY-_7d3.y,2);
if(_7db>dist){
_7db=dist;
_7d9=x;
_7da=y;
_7dc=_7de;
}
}
}
if(!_7d2){
node.style.left=_7d9+"px";
node.style.top=_7da+"px";
}
return {left:_7d9,top:_7da,x:_7d9,y:_7da,dist:_7db,corner:_7dc};
};
dojo.html.placeOnScreenPoint=function(node,_7e6,_7e7,_7e8,_7e9){
dojo.deprecated("dojo.html.placeOnScreenPoint","use dojo.html.placeOnScreen() instead","0.5");
return dojo.html.placeOnScreen(node,_7e6,_7e7,_7e8,_7e9,["TL","TR","BL","BR"]);
};
dojo.html.placeOnScreenAroundElement=function(node,_7eb,_7ec,_7ed,_7ee,_7ef){
var best,_7f1=Infinity;
_7eb=dojo.byId(_7eb);
var _7f2=_7eb.style.display;
_7eb.style.display="";
var mb=dojo.html.getElementBox(_7eb,_7ed);
var _7f4=mb.width;
var _7f5=mb.height;
var _7f6=dojo.html.getAbsolutePosition(_7eb,true,_7ed);
_7eb.style.display=_7f2;
for(var _7f7 in _7ee){
var pos,_7f9,_7fa;
var _7fb=_7ee[_7f7];
_7f9=_7f6.x+(_7f7.charAt(1)=="L"?0:_7f4);
_7fa=_7f6.y+(_7f7.charAt(0)=="T"?0:_7f5);
pos=dojo.html.placeOnScreen(node,_7f9,_7fa,_7ec,true,_7fb,true);
if(pos.dist==0){
best=pos;
break;
}else{
if(_7f1>pos.dist){
_7f1=pos.dist;
best=pos;
}
}
}
if(!_7ef){
node.style.left=best.left+"px";
node.style.top=best.top+"px";
}
return best;
};
dojo.html.scrollIntoView=function(node){
if(!node){
return;
}
if(dojo.render.html.ie){
if(dojo.html.getBorderBox(node.parentNode).height<=node.parentNode.scrollHeight){
node.scrollIntoView(false);
}
}else{
if(dojo.render.html.mozilla){
node.scrollIntoView(false);
}else{
var _7fd=node.parentNode;
var _7fe=_7fd.scrollTop+dojo.html.getBorderBox(_7fd).height;
var _7ff=node.offsetTop+dojo.html.getMarginBox(node).height;
if(_7fe<_7ff){
_7fd.scrollTop+=(_7ff-_7fe);
}else{
if(_7fd.scrollTop>node.offsetTop){
_7fd.scrollTop-=(_7fd.scrollTop-node.offsetTop);
}
}
}
}
};
dojo.provide("dojo.lfx.toggle");
dojo.lfx.toggle.plain={show:function(node,_801,_802,_803){
dojo.html.show(node);
if(dojo.lang.isFunction(_803)){
_803();
}
},hide:function(node,_805,_806,_807){
dojo.html.hide(node);
if(dojo.lang.isFunction(_807)){
_807();
}
}};
dojo.lfx.toggle.fade={show:function(node,_809,_80a,_80b){
dojo.lfx.fadeShow(node,_809,_80a,_80b).play();
},hide:function(node,_80d,_80e,_80f){
dojo.lfx.fadeHide(node,_80d,_80e,_80f).play();
}};
dojo.lfx.toggle.wipe={show:function(node,_811,_812,_813){
dojo.lfx.wipeIn(node,_811,_812,_813).play();
},hide:function(node,_815,_816,_817){
dojo.lfx.wipeOut(node,_815,_816,_817).play();
}};
dojo.lfx.toggle.explode={show:function(node,_819,_81a,_81b,_81c){
dojo.lfx.explode(_81c||{x:0,y:0,width:0,height:0},node,_819,_81a,_81b).play();
},hide:function(node,_81e,_81f,_820,_821){
dojo.lfx.implode(node,_821||{x:0,y:0,width:0,height:0},_81e,_81f,_820).play();
}};
dojo.provide("dojo.widget.HtmlWidget");
dojo.declare("dojo.widget.HtmlWidget",dojo.widget.DomWidget,{templateCssPath:null,templatePath:null,lang:"",toggle:"plain",toggleDuration:150,initialize:function(args,frag){
},postMixInProperties:function(args,frag){
if(this.lang===""){
this.lang=null;
}
this.toggleObj=dojo.lfx.toggle[this.toggle.toLowerCase()]||dojo.lfx.toggle.plain;
},createNodesFromText:function(txt,wrap){
return dojo.html.createNodesFromText(txt,wrap);
},destroyRendering:function(_828){
try{
if(this.bgIframe){
this.bgIframe.remove();
delete this.bgIframe;
}
if(!_828&&this.domNode){
dojo.event.browser.clean(this.domNode);
}
dojo.widget.HtmlWidget.superclass.destroyRendering.call(this);
}
catch(e){
}
},isShowing:function(){
return dojo.html.isShowing(this.domNode);
},toggleShowing:function(){
if(this.isShowing()){
this.hide();
}else{
this.show();
}
},show:function(){
if(this.isShowing()){
return;
}
this.animationInProgress=true;
this.toggleObj.show(this.domNode,this.toggleDuration,null,dojo.lang.hitch(this,this.onShow),this.explodeSrc);
},onShow:function(){
this.animationInProgress=false;
this.checkSize();
},hide:function(){
if(!this.isShowing()){
return;
}
this.animationInProgress=true;
this.toggleObj.hide(this.domNode,this.toggleDuration,null,dojo.lang.hitch(this,this.onHide),this.explodeSrc);
},onHide:function(){
this.animationInProgress=false;
},_isResized:function(w,h){
if(!this.isShowing()){
return false;
}
var wh=dojo.html.getMarginBox(this.domNode);
var _82c=w||wh.width;
var _82d=h||wh.height;
if(this.width==_82c&&this.height==_82d){
return false;
}
this.width=_82c;
this.height=_82d;
return true;
},checkSize:function(){
if(!this._isResized()){
return;
}
this.onResized();
},resizeTo:function(w,h){
dojo.html.setMarginBox(this.domNode,{width:w,height:h});
if(this.isShowing()){
this.onResized();
}
},resizeSoon:function(){
if(this.isShowing()){
dojo.lang.setTimeout(this,this.onResized,0);
}
},onResized:function(){
dojo.lang.forEach(this.children,function(_830){
if(_830.checkSize){
_830.checkSize();
}
});
}});
dojo.kwCompoundRequire({common:["dojo.xml.Parse","dojo.widget.Widget","dojo.widget.Parse","dojo.widget.Manager"],browser:["dojo.widget.DomWidget","dojo.widget.HtmlWidget"],dashboard:["dojo.widget.DomWidget","dojo.widget.HtmlWidget"],svg:["dojo.widget.SvgWidget"],rhino:["dojo.widget.SwtWidget"]});
dojo.provide("dojo.widget.*");
dojo.kwCompoundRequire({common:["dojo.html.common","dojo.html.style"]});
dojo.provide("dojo.html.*");
dojo.provide("dojo.html.selection");
dojo.html.selectionType={NONE:0,TEXT:1,CONTROL:2};
dojo.html.clearSelection=function(){
var _831=dojo.global();
var _832=dojo.doc();
try{
if(_831["getSelection"]){
if(dojo.render.html.safari){
_831.getSelection().collapse();
}else{
_831.getSelection().removeAllRanges();
}
}else{
if(_832.selection){
if(_832.selection.empty){
_832.selection.empty();
}else{
if(_832.selection.clear){
_832.selection.clear();
}
}
}
}
return true;
}
catch(e){
dojo.debug(e);
return false;
}
};
dojo.html.disableSelection=function(_833){
_833=dojo.byId(_833)||dojo.body();
var h=dojo.render.html;
if(h.mozilla){
_833.style.MozUserSelect="none";
}else{
if(h.safari){
_833.style.KhtmlUserSelect="none";
}else{
if(h.ie){
_833.unselectable="on";
}else{
return false;
}
}
}
return true;
};
dojo.html.enableSelection=function(_835){
_835=dojo.byId(_835)||dojo.body();
var h=dojo.render.html;
if(h.mozilla){
_835.style.MozUserSelect="";
}else{
if(h.safari){
_835.style.KhtmlUserSelect="";
}else{
if(h.ie){
_835.unselectable="off";
}else{
return false;
}
}
}
return true;
};
dojo.html.selectElement=function(_837){
dojo.deprecated("dojo.html.selectElement","replaced by dojo.html.selection.selectElementChildren",0.5);
};
dojo.html.selectInputText=function(_838){
var _839=dojo.global();
var _83a=dojo.doc();
_838=dojo.byId(_838);
if(_83a["selection"]&&dojo.body()["createTextRange"]){
var _83b=_838.createTextRange();
_83b.moveStart("character",0);
_83b.moveEnd("character",_838.value.length);
_83b.select();
}else{
if(_839["getSelection"]){
var _83c=_839.getSelection();
_838.setSelectionRange(0,_838.value.length);
}
}
_838.focus();
};
dojo.html.isSelectionCollapsed=function(){
dojo.deprecated("dojo.html.isSelectionCollapsed","replaced by dojo.html.selection.isCollapsed",0.5);
return dojo.html.selection.isCollapsed();
};
dojo.lang.mixin(dojo.html.selection,{getType:function(){
if(dojo.doc()["selection"]){
return dojo.html.selectionType[dojo.doc().selection.type.toUpperCase()];
}else{
var _83d=dojo.html.selectionType.TEXT;
var oSel;
try{
oSel=dojo.global().getSelection();
}
catch(e){
}
if(oSel&&oSel.rangeCount==1){
var _83f=oSel.getRangeAt(0);
if(_83f.startContainer==_83f.endContainer&&(_83f.endOffset-_83f.startOffset)==1&&_83f.startContainer.nodeType!=dojo.dom.TEXT_NODE){
_83d=dojo.html.selectionType.CONTROL;
}
}
return _83d;
}
},isCollapsed:function(){
var _840=dojo.global();
var _841=dojo.doc();
if(_841["selection"]){
return _841.selection.createRange().text=="";
}else{
if(_840["getSelection"]){
var _842=_840.getSelection();
if(dojo.lang.isString(_842)){
return _842=="";
}else{
return _842.isCollapsed||_842.toString()=="";
}
}
}
},getSelectedElement:function(){
if(dojo.html.selection.getType()==dojo.html.selectionType.CONTROL){
if(dojo.doc()["selection"]){
var _843=dojo.doc().selection.createRange();
if(_843&&_843.item){
return dojo.doc().selection.createRange().item(0);
}
}else{
var _844=dojo.global().getSelection();
return _844.anchorNode.childNodes[_844.anchorOffset];
}
}
},getParentElement:function(){
if(dojo.html.selection.getType()==dojo.html.selectionType.CONTROL){
var p=dojo.html.selection.getSelectedElement();
if(p){
return p.parentNode;
}
}else{
if(dojo.doc()["selection"]){
return dojo.doc().selection.createRange().parentElement();
}else{
var _846=dojo.global().getSelection();
if(_846){
var node=_846.anchorNode;
while(node&&node.nodeType!=dojo.dom.ELEMENT_NODE){
node=node.parentNode;
}
return node;
}
}
}
},getSelectedText:function(){
if(dojo.doc()["selection"]){
if(dojo.html.selection.getType()==dojo.html.selectionType.CONTROL){
return null;
}
return dojo.doc().selection.createRange().text;
}else{
var _848=dojo.global().getSelection();
if(_848){
return _848.toString();
}
}
},getSelectedHtml:function(){
if(dojo.doc()["selection"]){
if(dojo.html.selection.getType()==dojo.html.selectionType.CONTROL){
return null;
}
return dojo.doc().selection.createRange().htmlText;
}else{
var _849=dojo.global().getSelection();
if(_849&&_849.rangeCount){
var frag=_849.getRangeAt(0).cloneContents();
var div=document.createElement("div");
div.appendChild(frag);
return div.innerHTML;
}
return null;
}
},hasAncestorElement:function(_84c){
return (dojo.html.selection.getAncestorElement.apply(this,arguments)!=null);
},getAncestorElement:function(_84d){
var node=dojo.html.selection.getSelectedElement()||dojo.html.selection.getParentElement();
while(node){
if(dojo.html.selection.isTag(node,arguments).length>0){
return node;
}
node=node.parentNode;
}
return null;
},isTag:function(node,tags){
if(node&&node.tagName){
for(var i=0;i<tags.length;i++){
if(node.tagName.toLowerCase()==String(tags[i]).toLowerCase()){
return String(tags[i]).toLowerCase();
}
}
}
return "";
},selectElement:function(_852){
var _853=dojo.global();
var _854=dojo.doc();
_852=dojo.byId(_852);
if(_854.selection&&dojo.body().createTextRange){
try{
var _855=dojo.body().createControlRange();
_855.addElement(_852);
_855.select();
}
catch(e){
dojo.html.selection.selectElementChildren(_852);
}
}else{
if(_853["getSelection"]){
var _856=_853.getSelection();
if(_856["removeAllRanges"]){
var _855=_854.createRange();
_855.selectNode(_852);
_856.removeAllRanges();
_856.addRange(_855);
}
}
}
},selectElementChildren:function(_857){
var _858=dojo.global();
var _859=dojo.doc();
_857=dojo.byId(_857);
if(_859.selection&&dojo.body().createTextRange){
var _85a=dojo.body().createTextRange();
_85a.moveToElementText(_857);
_85a.select();
}else{
if(_858["getSelection"]){
var _85b=_858.getSelection();
if(_85b["setBaseAndExtent"]){
_85b.setBaseAndExtent(_857,0,_857,_857.innerText.length-1);
}else{
if(_85b["selectAllChildren"]){
_85b.selectAllChildren(_857);
}
}
}
}
},getBookmark:function(){
var _85c;
var _85d=dojo.doc();
if(_85d["selection"]){
var _85e=_85d.selection.createRange();
_85c=_85e.getBookmark();
}else{
var _85f;
try{
_85f=dojo.global().getSelection();
}
catch(e){
}
if(_85f){
var _85e=_85f.getRangeAt(0);
_85c=_85e.cloneRange();
}else{
dojo.debug("No idea how to store the current selection for this browser!");
}
}
return _85c;
},moveToBookmark:function(_860){
var _861=dojo.doc();
if(_861["selection"]){
var _862=_861.selection.createRange();
_862.moveToBookmark(_860);
_862.select();
}else{
var _863;
try{
_863=dojo.global().getSelection();
}
catch(e){
}
if(_863&&_863["removeAllRanges"]){
_863.removeAllRanges();
_863.addRange(_860);
}else{
dojo.debug("No idea how to restore selection for this browser!");
}
}
},collapse:function(_864){
if(dojo.global()["getSelection"]){
var _865=dojo.global().getSelection();
if(_865.removeAllRanges){
if(_864){
_865.collapseToStart();
}else{
_865.collapseToEnd();
}
}else{
dojo.global().getSelection().collapse(_864);
}
}else{
if(dojo.doc().selection){
var _866=dojo.doc().selection.createRange();
_866.collapse(_864);
_866.select();
}
}
},remove:function(){
if(dojo.doc().selection){
var _867=dojo.doc().selection;
if(_867.type.toUpperCase()!="NONE"){
_867.clear();
}
return _867;
}else{
var _867=dojo.global().getSelection();
for(var i=0;i<_867.rangeCount;i++){
_867.getRangeAt(i).deleteContents();
}
return _867;
}
}});
dojo.provide("dojo.Deferred");
dojo.Deferred=function(_869){
this.chain=[];
this.id=this._nextId();
this.fired=-1;
this.paused=0;
this.results=[null,null];
this.canceller=_869;
this.silentlyCancelled=false;
};
dojo.lang.extend(dojo.Deferred,{getFunctionFromArgs:function(){
var a=arguments;
if((a[0])&&(!a[1])){
if(dojo.lang.isFunction(a[0])){
return a[0];
}else{
if(dojo.lang.isString(a[0])){
return dj_global[a[0]];
}
}
}else{
if((a[0])&&(a[1])){
return dojo.lang.hitch(a[0],a[1]);
}
}
return null;
},makeCalled:function(){
var _86b=new dojo.Deferred();
_86b.callback();
return _86b;
},repr:function(){
var _86c;
if(this.fired==-1){
_86c="unfired";
}else{
if(this.fired==0){
_86c="success";
}else{
_86c="error";
}
}
return "Deferred("+this.id+", "+_86c+")";
},toString:dojo.lang.forward("repr"),_nextId:(function(){
var n=1;
return function(){
return n++;
};
})(),cancel:function(){
if(this.fired==-1){
if(this.canceller){
this.canceller(this);
}else{
this.silentlyCancelled=true;
}
if(this.fired==-1){
this.errback(new Error(this.repr()));
}
}else{
if((this.fired==0)&&(this.results[0] instanceof dojo.Deferred)){
this.results[0].cancel();
}
}
},_pause:function(){
this.paused++;
},_unpause:function(){
this.paused--;
if((this.paused==0)&&(this.fired>=0)){
this._fire();
}
},_continue:function(res){
this._resback(res);
this._unpause();
},_resback:function(res){
this.fired=((res instanceof Error)?1:0);
this.results[this.fired]=res;
this._fire();
},_check:function(){
if(this.fired!=-1){
if(!this.silentlyCancelled){
dojo.raise("already called!");
}
this.silentlyCancelled=false;
return;
}
},callback:function(res){
this._check();
this._resback(res);
},errback:function(res){
this._check();
if(!(res instanceof Error)){
res=new Error(res);
}
this._resback(res);
},addBoth:function(cb,cbfn){
var _874=this.getFunctionFromArgs(cb,cbfn);
if(arguments.length>2){
_874=dojo.lang.curryArguments(null,_874,arguments,2);
}
return this.addCallbacks(_874,_874);
},addCallback:function(cb,cbfn){
var _877=this.getFunctionFromArgs(cb,cbfn);
if(arguments.length>2){
_877=dojo.lang.curryArguments(null,_877,arguments,2);
}
return this.addCallbacks(_877,null);
},addErrback:function(cb,cbfn){
var _87a=this.getFunctionFromArgs(cb,cbfn);
if(arguments.length>2){
_87a=dojo.lang.curryArguments(null,_87a,arguments,2);
}
return this.addCallbacks(null,_87a);
return this.addCallbacks(null,cbfn);
},addCallbacks:function(cb,eb){
this.chain.push([cb,eb]);
if(this.fired>=0){
this._fire();
}
return this;
},_fire:function(){
var _87d=this.chain;
var _87e=this.fired;
var res=this.results[_87e];
var self=this;
var cb=null;
while(_87d.length>0&&this.paused==0){
var pair=_87d.shift();
var f=pair[_87e];
if(f==null){
continue;
}
try{
res=f(res);
_87e=((res instanceof Error)?1:0);
if(res instanceof dojo.Deferred){
cb=function(res){
self._continue(res);
};
this._pause();
}
}
catch(err){
_87e=1;
res=err;
}
}
this.fired=_87e;
this.results[_87e]=res;
if((cb)&&(this.paused)){
res.addBoth(cb);
}
}});
dojo.provide("dojo.widget.RichText");
if(!djConfig["useXDomain"]||djConfig["allowXdRichTextSave"]){
if(dojo.hostenv.post_load_){
(function(){
var _885=dojo.doc().createElement("textarea");
_885.id="dojo.widget.RichText.savedContent";
_885.style="display:none;position:absolute;top:-100px;left:-100px;height:3px;width:3px;overflow:hidden;";
dojo.body().appendChild(_885);
})();
}else{
try{
dojo.doc().write("<textarea id=\"dojo.widget.RichText.savedContent\" "+"style=\"display:none;position:absolute;top:-100px;left:-100px;height:3px;width:3px;overflow:hidden;\"></textarea>");
}
catch(e){
}
}
}
dojo.widget.defineWidget("dojo.widget.RichText",dojo.widget.HtmlWidget,function(){
this.contentPreFilters=[];
this.contentPostFilters=[];
this.contentDomPreFilters=[];
this.contentDomPostFilters=[];
this.editingAreaStyleSheets=[];
if(dojo.render.html.moz){
this.contentPreFilters.push(this._fixContentForMoz);
}
this._keyHandlers={};
if(dojo.Deferred){
this.onLoadDeferred=new dojo.Deferred();
}
},{inheritWidth:false,focusOnLoad:false,saveName:"",styleSheets:"",_content:"",height:"",minHeight:"1em",isClosed:true,isLoaded:false,useActiveX:false,relativeImageUrls:false,_SEPARATOR:"@@**%%__RICHTEXTBOUNDRY__%%**@@",onLoadDeferred:null,fillInTemplate:function(){
dojo.event.topic.publish("dojo.widget.RichText::init",this);
this.open();
dojo.event.connect(this,"onKeyPressed",this,"afterKeyPress");
dojo.event.connect(this,"onKeyPress",this,"keyPress");
dojo.event.connect(this,"onKeyDown",this,"keyDown");
dojo.event.connect(this,"onKeyUp",this,"keyUp");
this.setupDefaultShortcuts();
},setupDefaultShortcuts:function(){
var ctrl=this.KEY_CTRL;
var exec=function(cmd,arg){
return arguments.length==1?function(){
this.execCommand(cmd);
}:function(){
this.execCommand(cmd,arg);
};
};
this.addKeyHandler("b",ctrl,exec("bold"));
this.addKeyHandler("i",ctrl,exec("italic"));
this.addKeyHandler("u",ctrl,exec("underline"));
this.addKeyHandler("a",ctrl,exec("selectall"));
this.addKeyHandler("s",ctrl,function(){
this.save(true);
});
this.addKeyHandler("1",ctrl,exec("formatblock","h1"));
this.addKeyHandler("2",ctrl,exec("formatblock","h2"));
this.addKeyHandler("3",ctrl,exec("formatblock","h3"));
this.addKeyHandler("4",ctrl,exec("formatblock","h4"));
this.addKeyHandler("\\",ctrl,exec("insertunorderedlist"));
if(!dojo.render.html.ie){
this.addKeyHandler("Z",ctrl,exec("redo"));
}
},events:["onBlur","onFocus","onKeyPress","onKeyDown","onKeyUp","onClick"],open:function(_88a){
if(this.onLoadDeferred.fired>=0){
this.onLoadDeferred=new dojo.Deferred();
}
var h=dojo.render.html;
if(!this.isClosed){
this.close();
}
dojo.event.topic.publish("dojo.widget.RichText::open",this);
this._content="";
if((arguments.length==1)&&(_88a["nodeName"])){
this.domNode=_88a;
}
if((this.domNode["nodeName"])&&(this.domNode.nodeName.toLowerCase()=="textarea")){
this.textarea=this.domNode;
var html=this._preFilterContent(this.textarea.value);
this.domNode=dojo.doc().createElement("div");
dojo.html.copyStyle(this.domNode,this.textarea);
var _88d=dojo.lang.hitch(this,function(){
with(this.textarea.style){
display="block";
position="absolute";
left=top="-1000px";
if(h.ie){
this.__overflow=overflow;
overflow="hidden";
}
}
});
if(h.ie){
setTimeout(_88d,10);
}else{
_88d();
}
if(!h.safari){
dojo.html.insertBefore(this.domNode,this.textarea);
}
if(this.textarea.form){
dojo.event.connect("before",this.textarea.form,"onsubmit",dojo.lang.hitch(this,function(){
this.textarea.value=this.getEditorContent();
}));
}
var _88e=this;
dojo.event.connect(this,"postCreate",function(){
dojo.html.insertAfter(_88e.textarea,_88e.domNode);
});
}else{
var html=this._preFilterContent(dojo.string.trim(this.domNode.innerHTML));
}
if(html==""){
html="&nbsp;";
}
var _88f=dojo.html.getContentBox(this.domNode);
this._oldHeight=_88f.height;
this._oldWidth=_88f.width;
this._firstChildContributingMargin=this._getContributingMargin(this.domNode,"top");
this._lastChildContributingMargin=this._getContributingMargin(this.domNode,"bottom");
this.savedContent=html;
this.domNode.innerHTML="";
this.editingArea=dojo.doc().createElement("div");
this.domNode.appendChild(this.editingArea);
if((this.domNode["nodeName"])&&(this.domNode.nodeName=="LI")){
this.domNode.innerHTML=" <br>";
}
if(this.saveName!=""&&(!djConfig["useXDomain"]||djConfig["allowXdRichTextSave"])){
var _890=dojo.doc().getElementById("dojo.widget.RichText.savedContent");
if(_890.value!=""){
var _891=_890.value.split(this._SEPARATOR);
for(var i=0;i<_891.length;i++){
var data=_891[i].split(":");
if(data[0]==this.saveName){
html=data[1];
_891.splice(i,1);
break;
}
}
}
dojo.event.connect("before",window,"onunload",this,"_saveContent");
}
if(h.ie70&&this.useActiveX){
dojo.debug("activeX in ie70 is not currently supported, useActiveX is ignored for now.");
this.useActiveX=false;
}
if(this.useActiveX&&h.ie){
var self=this;
setTimeout(function(){
self._drawObject(html);
},0);
}else{
if(h.ie||this._safariIsLeopard()||h.opera){
this.iframe=dojo.doc().createElement("iframe");
this.iframe.src="javascript:void(0)";
this.editorObject=this.iframe;
with(this.iframe.style){
border="0";
width="100%";
}
this.iframe.frameBorder=0;
this.editingArea.appendChild(this.iframe);
this.window=this.iframe.contentWindow;
this.document=this.window.document;
this.document.open();
this.document.write("<html><head><style>body{margin:0;padding:0;border:0;overflow:hidden;}</style></head><body><div></div></body></html>");
this.document.close();
this.editNode=this.document.body.firstChild;
this.editNode.contentEditable=true;
with(this.iframe.style){
if(h.ie70){
if(this.height){
height=this.height;
}
if(this.minHeight){
minHeight=this.minHeight;
}
}else{
height=this.height?this.height:this.minHeight;
}
}
var _895=["p","pre","address","h1","h2","h3","h4","h5","h6","ol","div","ul"];
var _896="";
for(var i in _895){
if(_895[i].charAt(1)!="l"){
_896+="<"+_895[i]+"><span>content</span></"+_895[i]+">";
}else{
_896+="<"+_895[i]+"><li>content</li></"+_895[i]+">";
}
}
with(this.editNode.style){
position="absolute";
left="-2000px";
top="-2000px";
}
this.editNode.innerHTML=_896;
var node=this.editNode.firstChild;
while(node){
dojo.withGlobal(this.window,"selectElement",dojo.html.selection,[node.firstChild]);
var _898=node.tagName.toLowerCase();
this._local2NativeFormatNames[_898]=this.queryCommandValue("formatblock");
this._native2LocalFormatNames[this._local2NativeFormatNames[_898]]=_898;
node=node.nextSibling;
}
with(this.editNode.style){
position="";
left="";
top="";
}
this.editNode.innerHTML=html;
if(this.height){
this.document.body.style.overflowY="scroll";
}
dojo.lang.forEach(this.events,function(e){
dojo.event.connect(this.editNode,e.toLowerCase(),this,e);
},this);
this.onLoad();
}else{
this._drawIframe(html);
this.editorObject=this.iframe;
}
}
if(this.domNode.nodeName=="LI"){
this.domNode.lastChild.style.marginTop="-1.2em";
}
dojo.html.addClass(this.domNode,"RichTextEditable");
this.isClosed=false;
},_hasCollapseableMargin:function(_89a,side){
if(dojo.html.getPixelValue(_89a,"border-"+side+"-width",false)){
return false;
}else{
if(dojo.html.getPixelValue(_89a,"padding-"+side,false)){
return false;
}else{
return true;
}
}
},_getContributingMargin:function(_89c,_89d){
if(_89d=="top"){
var _89e="previousSibling";
var _89f="nextSibling";
var _8a0="firstChild";
var _8a1="margin-top";
var _8a2="margin-bottom";
}else{
var _89e="nextSibling";
var _89f="previousSibling";
var _8a0="lastChild";
var _8a1="margin-bottom";
var _8a2="margin-top";
}
var _8a3=dojo.html.getPixelValue(_89c,_8a1,false);
function isSignificantNode(_8a4){
return !(_8a4.nodeType==3&&dojo.string.isBlank(_8a4.data))&&dojo.html.getStyle(_8a4,"display")!="none"&&!dojo.html.isPositionAbsolute(_8a4);
}
var _8a5=0;
var _8a6=_89c[_8a0];
while(_8a6){
while((!isSignificantNode(_8a6))&&_8a6[_89f]){
_8a6=_8a6[_89f];
}
_8a5=Math.max(_8a5,dojo.html.getPixelValue(_8a6,_8a1,false));
if(!this._hasCollapseableMargin(_8a6,_89d)){
break;
}
_8a6=_8a6[_8a0];
}
if(!this._hasCollapseableMargin(_89c,_89d)){
return parseInt(_8a5);
}
var _8a7=0;
var _8a8=_89c[_89e];
while(_8a8){
if(isSignificantNode(_8a8)){
_8a7=dojo.html.getPixelValue(_8a8,_8a2,false);
break;
}
_8a8=_8a8[_89e];
}
if(!_8a8){
_8a7=dojo.html.getPixelValue(_89c.parentNode,_8a1,false);
}
if(_8a5>_8a3){
return parseInt(Math.max((_8a5-_8a3)-_8a7,0));
}else{
return 0;
}
},_drawIframe:function(html){
var _8aa=Boolean(dojo.render.html.moz&&(typeof window.XML=="undefined"));
if(!this.iframe){
var _8ab=(new dojo.uri.Uri(dojo.doc().location)).host;
this.iframe=dojo.doc().createElement("iframe");
with(this.iframe){
style.border="none";
style.lineHeight="0";
style.verticalAlign="bottom";
scrolling=this.height?"auto":"no";
}
}
if(djConfig["useXDomain"]&&!djConfig["dojoRichTextFrameUrl"]){
dojo.debug("dojo.widget.RichText: When using cross-domain Dojo builds,"+" please save src/widget/templates/richtextframe.html to your domain and set djConfig.dojoRichTextFrameUrl"+" to the path on your domain to richtextframe.html");
}
this.iframe.src=(djConfig["dojoRichTextFrameUrl"]||dojo.uri.moduleUri("dojo.widget","templates/richtextframe.html"))+((dojo.doc().domain!=_8ab)?("#"+dojo.doc().domain):"");
this.iframe.width=this.inheritWidth?this._oldWidth:"100%";
if(this.height){
this.iframe.style.height=this.height;
}else{
var _8ac=this._oldHeight;
if(this._hasCollapseableMargin(this.domNode,"top")){
_8ac+=this._firstChildContributingMargin;
}
if(this._hasCollapseableMargin(this.domNode,"bottom")){
_8ac+=this._lastChildContributingMargin;
}
this.iframe.height=_8ac;
}
var _8ad=dojo.doc().createElement("div");
_8ad.innerHTML=html;
this.editingArea.appendChild(_8ad);
if(this.relativeImageUrls){
var imgs=_8ad.getElementsByTagName("img");
for(var i=0;i<imgs.length;i++){
imgs[i].src=(new dojo.uri.Uri(dojo.global().location,imgs[i].src)).toString();
}
html=_8ad.innerHTML;
}
var _8b0=dojo.html.firstElement(_8ad);
var _8b1=dojo.html.lastElement(_8ad);
if(_8b0){
_8b0.style.marginTop=this._firstChildContributingMargin+"px";
}
if(_8b1){
_8b1.style.marginBottom=this._lastChildContributingMargin+"px";
}
this.editingArea.appendChild(this.iframe);
if(dojo.render.html.safari){
this.iframe.src=this.iframe.src;
}
var _8b2=false;
var _8b3=dojo.lang.hitch(this,function(){
if(!_8b2){
_8b2=true;
}else{
return;
}
if(!this.editNode){
if(this.iframe.contentWindow){
this.window=this.iframe.contentWindow;
this.document=this.iframe.contentWindow.document;
}else{
if(this.iframe.contentDocument){
this.window=this.iframe.contentDocument.window;
this.document=this.iframe.contentDocument;
}
}
var _8b4=(function(_8b5){
return function(_8b6){
return dojo.html.getStyle(_8b5,_8b6);
};
})(this.domNode);
var font=_8b4("font-weight")+" "+_8b4("font-size")+" "+_8b4("font-family");
var _8b8="1.0";
var _8b9=dojo.html.getUnitValue(this.domNode,"line-height");
if(_8b9.value&&_8b9.units==""){
_8b8=_8b9.value;
}
dojo.html.insertCssText("body,html{background:transparent;padding:0;margin:0;}"+"body{top:0;left:0;right:0;"+(((this.height)||(dojo.render.html.opera))?"":"position:fixed;")+"font:"+font+";"+"min-height:"+this.minHeight+";"+"line-height:"+_8b8+"}"+"p{margin: 1em 0 !important;}"+"body > *:first-child{padding-top:0 !important;margin-top:"+this._firstChildContributingMargin+"px !important;}"+"body > *:last-child{padding-bottom:0 !important;margin-bottom:"+this._lastChildContributingMargin+"px !important;}"+"li > ul:-moz-first-node, li > ol:-moz-first-node{padding-top:1.2em;}\n"+"li{min-height:1.2em;}"+"",this.document);
dojo.html.removeNode(_8ad);
this.document.body.innerHTML=html;
if(_8aa||dojo.render.html.safari){
this.document.designMode="on";
}
this.onLoad();
}else{
dojo.html.removeNode(_8ad);
this.editNode.innerHTML=html;
this.onDisplayChanged();
}
});
if(this.editNode){
_8b3();
}else{
if(dojo.render.html.moz){
this.iframe.onload=function(){
setTimeout(_8b3,250);
};
}else{
this.iframe.onload=_8b3;
}
}
},_applyEditingAreaStyleSheets:function(){
var _8ba=[];
if(this.styleSheets){
_8ba=this.styleSheets.split(";");
this.styleSheets="";
}
_8ba=_8ba.concat(this.editingAreaStyleSheets);
this.editingAreaStyleSheets=[];
if(_8ba.length>0){
for(var i=0;i<_8ba.length;i++){
var url=_8ba[i];
if(url){
this.addStyleSheet(dojo.uri.dojoUri(url));
}
}
}
},addStyleSheet:function(uri){
var url=uri.toString();
if(dojo.lang.find(this.editingAreaStyleSheets,url)>-1){
dojo.debug("dojo.widget.RichText.addStyleSheet: Style sheet "+url+" is already applied to the editing area!");
return;
}
if(url.charAt(0)=="."||(url.charAt(0)!="/"&&!uri.host)){
url=(new dojo.uri.Uri(dojo.global().location,url)).toString();
}
this.editingAreaStyleSheets.push(url);
if(this.document.createStyleSheet){
this.document.createStyleSheet(url);
}else{
var head=this.document.getElementsByTagName("head")[0];
var _8c0=this.document.createElement("link");
with(_8c0){
rel="stylesheet";
type="text/css";
href=url;
}
head.appendChild(_8c0);
}
},removeStyleSheet:function(uri){
var url=uri.toString();
if(url.charAt(0)=="."||(url.charAt(0)!="/"&&!uri.host)){
url=(new dojo.uri.Uri(dojo.global().location,url)).toString();
}
var _8c3=dojo.lang.find(this.editingAreaStyleSheets,url);
if(_8c3==-1){
dojo.debug("dojo.widget.RichText.removeStyleSheet: Style sheet "+url+" is not applied to the editing area so it can not be removed!");
return;
}
delete this.editingAreaStyleSheets[_8c3];
var _8c4=this.document.getElementsByTagName("link");
for(var i=0;i<_8c4.length;i++){
if(_8c4[i].href==url){
if(dojo.render.html.ie){
_8c4[i].href="";
}
dojo.html.removeNode(_8c4[i]);
break;
}
}
},_drawObject:function(html){
this.object=dojo.html.createExternalElement(dojo.doc(),"object");
with(this.object){
classid="clsid:2D360201-FFF5-11D1-8D03-00A0C959BC0A";
width=this.inheritWidth?this._oldWidth:"100%";
style.height=this.height?this.height:(this._oldHeight+"px");
Scrollbars=this.height?true:false;
Appearance=this._activeX.appearance.flat;
}
this.editorObject=this.object;
this.editingArea.appendChild(this.object);
this.object.attachEvent("DocumentComplete",dojo.lang.hitch(this,"onLoad"));
dojo.lang.forEach(this.events,function(e){
this.object.attachEvent(e.toLowerCase(),dojo.lang.hitch(this,e));
},this);
this.object.DocumentHTML="<!doctype HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">"+"<html><title></title>"+"<style type=\"text/css\">"+"    body,html { padding: 0; margin: 0; }"+(this.height?"":"    body,  { overflow: hidden; }")+"</style>"+"<body><div>"+html+"<div></body></html>";
this._cacheLocalBlockFormatNames();
},_local2NativeFormatNames:{},_native2LocalFormatNames:{},_cacheLocalBlockFormatNames:function(){
if(!this._native2LocalFormatNames["p"]){
var obj=this.object;
var _8c9=false;
if(!obj){
try{
obj=dojo.html.createExternalElement(dojo.doc(),"object");
obj.classid="clsid:2D360201-FFF5-11D1-8D03-00A0C959BC0A";
dojo.body().appendChild(obj);
obj.DocumentHTML="<html><head></head><body></body></html>";
}
catch(e){
_8c9=true;
}
}
try{
var _8ca=new ActiveXObject("DEGetBlockFmtNamesParam.DEGetBlockFmtNamesParam");
obj.ExecCommand(this._activeX.command["getblockformatnames"],0,_8ca);
var _8cb=new VBArray(_8ca.Names);
var _8cc=_8cb.toArray();
var _8cd=["p","pre","address","h1","h2","h3","h4","h5","h6","ol","ul","","","","","div"];
for(var i=0;i<_8cd.length;++i){
if(_8cd[i].length>0){
this._local2NativeFormatNames[_8cc[i]]=_8cd[i];
this._native2LocalFormatNames[_8cd[i]]=_8cc[i];
}
}
}
catch(e){
_8c9=true;
}
if(obj&&!this.object){
dojo.body().removeChild(obj);
}
}
return !_8c9;
},_isResized:function(){
return false;
},onLoad:function(e){
this.isLoaded=true;
if(this.object){
this.document=this.object.DOM;
this.window=this.document.parentWindow;
this.editNode=this.document.body.firstChild;
this.editingArea.style.height=this.height?this.height:this.minHeight;
if(!this.height){
this.connect(this,"onDisplayChanged","_updateHeight");
}
this.window._frameElement=this.object;
}else{
if(this.iframe&&!dojo.render.html.ie){
this.editNode=this.document.body;
if(!this.height){
this.connect(this,"onDisplayChanged","_updateHeight");
}
try{
this.document.execCommand("useCSS",false,true);
this.document.execCommand("styleWithCSS",false,false);
}
catch(e2){
}
if(dojo.render.html.safari){
this.connect(this.editNode,"onblur","onBlur");
this.connect(this.editNode,"onfocus","onFocus");
this.connect(this.editNode,"onclick","onFocus");
this.interval=setInterval(dojo.lang.hitch(this,"onDisplayChanged"),750);
}else{
if(dojo.render.html.mozilla||dojo.render.html.opera){
var doc=this.document;
var _8d1=dojo.event.browser.addListener;
var self=this;
dojo.lang.forEach(this.events,function(e){
var l=_8d1(self.document,e.substr(2).toLowerCase(),dojo.lang.hitch(self,e));
if(e=="onBlur"){
var _8d5={unBlur:function(e){
dojo.event.browser.removeListener(doc,"blur",l);
}};
dojo.event.connect("before",self,"close",_8d5,"unBlur");
}
});
}
}
}else{
if(dojo.render.html.ie){
if(!this.height){
this.connect(this,"onDisplayChanged","_updateHeight");
}
this.editNode.style.zoom=1;
}
}
}
this._applyEditingAreaStyleSheets();
if(this.focusOnLoad){
this.focus();
}
this.onDisplayChanged(e);
if(this.onLoadDeferred){
this.onLoadDeferred.callback(true);
}
},onKeyDown:function(e){
if((!e)&&(this.object)){
e=dojo.event.browser.fixEvent(this.window.event);
}
if((dojo.render.html.ie)&&(e.keyCode==e.KEY_TAB)){
e.preventDefault();
e.stopPropagation();
this.execCommand((e.shiftKey?"outdent":"indent"));
}else{
if(dojo.render.html.ie){
if((65<=e.keyCode)&&(e.keyCode<=90)){
e.charCode=e.keyCode;
this.onKeyPress(e);
}
}
}
},onKeyUp:function(e){
return;
},KEY_CTRL:1,onKeyPress:function(e){
if((!e)&&(this.object)){
e=dojo.event.browser.fixEvent(this.window.event);
}
var _8da=e.ctrlKey?this.KEY_CTRL:0;
if(this._keyHandlers[e.key]){
var _8db=this._keyHandlers[e.key],i=0,_8dd;
while(_8dd=_8db[i++]){
if(_8da==_8dd.modifiers){
e.preventDefault();
_8dd.handler.call(this);
break;
}
}
}
dojo.lang.setTimeout(this,this.onKeyPressed,1,e);
},addKeyHandler:function(key,_8df,_8e0){
if(!(this._keyHandlers[key] instanceof Array)){
this._keyHandlers[key]=[];
}
this._keyHandlers[key].push({modifiers:_8df||0,handler:_8e0});
},onKeyPressed:function(e){
this.onDisplayChanged();
},onClick:function(e){
this.onDisplayChanged(e);
},onBlur:function(e){
},_initialFocus:true,onFocus:function(e){
if((dojo.render.html.mozilla)&&(this._initialFocus)){
this._initialFocus=false;
if(dojo.string.trim(this.editNode.innerHTML)=="&nbsp;"){
this.placeCursorAtStart();
}
}
},blur:function(){
if(this.iframe){
this.window.blur();
}else{
if(this.object){
this.document.body.blur();
}else{
if(this.editNode){
this.editNode.blur();
}
}
}
},focus:function(){
if(this.iframe&&!dojo.render.html.ie){
this.window.focus();
}else{
if(this.object){
this.document.focus();
}else{
if(this.editNode&&this.editNode.focus){
this.editNode.focus();
}else{
dojo.debug("Have no idea how to focus into the editor!");
}
}
}
},onDisplayChanged:function(e){
},_activeX:{command:{bold:5000,italic:5023,underline:5048,justifycenter:5024,justifyleft:5025,justifyright:5026,cut:5003,copy:5002,paste:5032,"delete":5004,undo:5049,redo:5033,removeformat:5034,selectall:5035,unlink:5050,indent:5018,outdent:5031,insertorderedlist:5030,insertunorderedlist:5051,inserttable:5022,insertcell:5019,insertcol:5020,insertrow:5021,deletecells:5005,deletecols:5006,deleterows:5007,mergecells:5029,splitcell:5047,setblockformat:5043,getblockformat:5011,getblockformatnames:5012,setfontname:5044,getfontname:5013,setfontsize:5045,getfontsize:5014,setbackcolor:5042,getbackcolor:5010,setforecolor:5046,getforecolor:5015,findtext:5008,font:5009,hyperlink:5016,image:5017,lockelement:5027,makeabsolute:5028,sendbackward:5036,bringforward:5037,sendbelowtext:5038,bringabovetext:5039,sendtoback:5040,bringtofront:5041,properties:5052},ui:{"default":0,prompt:1,noprompt:2},status:{notsupported:0,disabled:1,enabled:3,latched:7,ninched:11},appearance:{flat:0,inset:1},state:{unchecked:0,checked:1,gray:2}},_normalizeCommand:function(cmd){
var drh=dojo.render.html;
var _8e8=cmd.toLowerCase();
if(_8e8=="formatblock"){
if(drh.safari){
_8e8="heading";
}
}else{
if(this.object){
switch(_8e8){
case "createlink":
_8e8="hyperlink";
break;
case "insertimage":
_8e8="image";
break;
}
}else{
if(_8e8=="hilitecolor"&&!drh.mozilla){
_8e8="backcolor";
}
}
}
return _8e8;
},_safariIsLeopard:function(){
var _8e9=false;
if(dojo.render.html.safari){
var tmp=dojo.render.html.UA.split("AppleWebKit/")[1];
var ver=parseFloat(tmp.split(" ")[0]);
if(ver>=420){
_8e9=true;
}
}
return _8e9;
},queryCommandAvailable:function(_8ec){
var ie=1;
var _8ee=1<<1;
var _8ef=1<<2;
var _8f0=1<<3;
var _8f1=1<<4;
var _8f2=this._safariIsLeopard();
function isSupportedBy(_8f3){
return {ie:Boolean(_8f3&ie),mozilla:Boolean(_8f3&_8ee),safari:Boolean(_8f3&_8ef),safari420:Boolean(_8f3&_8f1),opera:Boolean(_8f3&_8f0)};
}
var _8f4=null;
switch(_8ec.toLowerCase()){
case "bold":
case "italic":
case "underline":
case "subscript":
case "superscript":
case "fontname":
case "fontsize":
case "forecolor":
case "hilitecolor":
case "justifycenter":
case "justifyfull":
case "justifyleft":
case "justifyright":
case "delete":
case "selectall":
_8f4=isSupportedBy(_8ee|ie|_8ef|_8f0);
break;
case "createlink":
case "unlink":
case "removeformat":
case "inserthorizontalrule":
case "insertimage":
case "insertorderedlist":
case "insertunorderedlist":
case "indent":
case "outdent":
case "formatblock":
case "inserthtml":
case "undo":
case "redo":
case "strikethrough":
_8f4=isSupportedBy(_8ee|ie|_8f0|_8f1);
break;
case "blockdirltr":
case "blockdirrtl":
case "dirltr":
case "dirrtl":
case "inlinedirltr":
case "inlinedirrtl":
_8f4=isSupportedBy(ie);
break;
case "cut":
case "copy":
case "paste":
_8f4=isSupportedBy(ie|_8ee|_8f1);
break;
case "inserttable":
_8f4=isSupportedBy(_8ee|(this.object?ie:0));
break;
case "insertcell":
case "insertcol":
case "insertrow":
case "deletecells":
case "deletecols":
case "deleterows":
case "mergecells":
case "splitcell":
_8f4=isSupportedBy(this.object?ie:0);
break;
default:
return false;
}
return (dojo.render.html.ie&&_8f4.ie)||(dojo.render.html.mozilla&&_8f4.mozilla)||(dojo.render.html.safari&&_8f4.safari)||(_8f2&&_8f4.safari420)||(dojo.render.html.opera&&_8f4.opera);
},execCommand:function(_8f5,_8f6){
var _8f7;
this.focus();
_8f5=this._normalizeCommand(_8f5);
if(_8f6!=undefined){
if(_8f5=="heading"){
throw new Error("unimplemented");
}else{
if(_8f5=="formatblock"){
if(this.object){
_8f6=this._native2LocalFormatNames[_8f6];
}else{
if(dojo.render.html.ie){
_8f6="<"+_8f6+">";
}
}
}
}
}
if(this.object){
switch(_8f5){
case "hilitecolor":
_8f5="setbackcolor";
break;
case "forecolor":
case "backcolor":
case "fontsize":
case "fontname":
_8f5="set"+_8f5;
break;
case "formatblock":
_8f5="setblockformat";
}
if(_8f5=="strikethrough"){
_8f5="inserthtml";
var _8f8=this.document.selection.createRange();
if(!_8f8.htmlText){
return;
}
_8f6=_8f8.htmlText.strike();
}else{
if(_8f5=="inserthorizontalrule"){
_8f5="inserthtml";
_8f6="<hr>";
}
}
if(_8f5=="inserthtml"){
var _8f8=this.document.selection.createRange();
if(this.document.selection.type.toUpperCase()=="CONTROL"){
for(var i=0;i<_8f8.length;i++){
_8f8.item(i).outerHTML=_8f6;
}
}else{
_8f8.pasteHTML(_8f6);
_8f8.select();
}
_8f7=true;
}else{
if(arguments.length==1){
_8f7=this.object.ExecCommand(this._activeX.command[_8f5],this._activeX.ui.noprompt);
}else{
_8f7=this.object.ExecCommand(this._activeX.command[_8f5],this._activeX.ui.noprompt,_8f6);
}
}
}else{
if(_8f5=="inserthtml"){
if(dojo.render.html.ie){
var _8fa=this.document.selection.createRange();
_8fa.pasteHTML(_8f6);
_8fa.select();
return true;
}else{
return this.document.execCommand(_8f5,false,_8f6);
}
}else{
if((_8f5=="unlink")&&(this.queryCommandEnabled("unlink"))&&(dojo.render.html.mozilla)){
var _8fb=this.window.getSelection();
var _8fc=_8fb.getRangeAt(0);
var _8fd=_8fc.startContainer;
var _8fe=_8fc.startOffset;
var _8ff=_8fc.endContainer;
var _900=_8fc.endOffset;
var a=dojo.withGlobal(this.window,"getAncestorElement",dojo.html.selection,["a"]);
dojo.withGlobal(this.window,"selectElement",dojo.html.selection,[a]);
_8f7=this.document.execCommand("unlink",false,null);
var _8fc=this.document.createRange();
_8fc.setStart(_8fd,_8fe);
_8fc.setEnd(_8ff,_900);
_8fb.removeAllRanges();
_8fb.addRange(_8fc);
return _8f7;
}else{
if((_8f5=="hilitecolor")&&(dojo.render.html.mozilla)){
this.document.execCommand("useCSS",false,false);
_8f7=this.document.execCommand(_8f5,false,_8f6);
this.document.execCommand("useCSS",false,true);
}else{
if((dojo.render.html.ie)&&((_8f5=="backcolor")||(_8f5=="forecolor"))){
_8f6=arguments.length>1?_8f6:null;
_8f7=this.document.execCommand(_8f5,false,_8f6);
}else{
_8f6=arguments.length>1?_8f6:null;
if(_8f6||_8f5!="createlink"){
_8f7=this.document.execCommand(_8f5,false,_8f6);
}
}
}
}
}
}
this.onDisplayChanged();
return _8f7;
},queryCommandEnabled:function(_902){
_902=this._normalizeCommand(_902);
if(this.object){
switch(_902){
case "hilitecolor":
_902="setbackcolor";
break;
case "forecolor":
case "backcolor":
case "fontsize":
case "fontname":
_902="set"+_902;
break;
case "formatblock":
_902="setblockformat";
break;
case "strikethrough":
_902="bold";
break;
case "inserthorizontalrule":
return true;
}
if(typeof this._activeX.command[_902]=="undefined"){
return false;
}
var _903=this.object.QueryStatus(this._activeX.command[_902]);
return ((_903!=this._activeX.status.notsupported)&&(_903!=this._activeX.status.disabled));
}else{
if(dojo.render.html.mozilla){
if(_902=="unlink"){
return dojo.withGlobal(this.window,"hasAncestorElement",dojo.html.selection,["a"]);
}else{
if(_902=="inserttable"){
return true;
}
}
}
var elem=(dojo.render.html.ie)?this.document.selection.createRange():this.document;
return elem.queryCommandEnabled(_902);
}
},queryCommandState:function(_905){
_905=this._normalizeCommand(_905);
if(this.object){
if(_905=="forecolor"){
_905="setforecolor";
}else{
if(_905=="backcolor"){
_905="setbackcolor";
}else{
if(_905=="strikethrough"){
return dojo.withGlobal(this.window,"hasAncestorElement",dojo.html.selection,["strike"]);
}else{
if(_905=="inserthorizontalrule"){
return false;
}
}
}
}
if(typeof this._activeX.command[_905]=="undefined"){
return null;
}
var _906=this.object.QueryStatus(this._activeX.command[_905]);
return ((_906==this._activeX.status.latched)||(_906==this._activeX.status.ninched));
}else{
return this.document.queryCommandState(_905);
}
},queryCommandValue:function(_907){
_907=this._normalizeCommand(_907);
if(this.object){
switch(_907){
case "forecolor":
case "backcolor":
case "fontsize":
case "fontname":
_907="get"+_907;
return this.object.execCommand(this._activeX.command[_907],this._activeX.ui.noprompt);
case "formatblock":
var _908=this.object.execCommand(this._activeX.command["getblockformat"],this._activeX.ui.noprompt);
if(_908){
return this._local2NativeFormatNames[_908];
}
}
}else{
if(dojo.render.html.ie&&_907=="formatblock"){
return this._local2NativeFormatNames[this.document.queryCommandValue(_907)]||this.document.queryCommandValue(_907);
}
return this.document.queryCommandValue(_907);
}
},placeCursorAtStart:function(){
this.focus();
if(dojo.render.html.moz&&this.editNode.firstChild&&this.editNode.firstChild.nodeType!=dojo.dom.TEXT_NODE){
dojo.withGlobal(this.window,"selectElementChildren",dojo.html.selection,[this.editNode.firstChild]);
}else{
dojo.withGlobal(this.window,"selectElementChildren",dojo.html.selection,[this.editNode]);
}
dojo.withGlobal(this.window,"collapse",dojo.html.selection,[true]);
},placeCursorAtEnd:function(){
this.focus();
if(dojo.render.html.moz&&this.editNode.lastChild&&this.editNode.lastChild.nodeType!=dojo.dom.TEXT_NODE){
dojo.withGlobal(this.window,"selectElementChildren",dojo.html.selection,[this.editNode.lastChild]);
}else{
dojo.withGlobal(this.window,"selectElementChildren",dojo.html.selection,[this.editNode]);
}
dojo.withGlobal(this.window,"collapse",dojo.html.selection,[false]);
},replaceEditorContent:function(html){
html=this._preFilterContent(html);
if(this.isClosed){
this.domNode.innerHTML=html;
}else{
if(this.window&&this.window.getSelection&&!dojo.render.html.moz){
this.editNode.innerHTML=html;
}else{
if((this.window&&this.window.getSelection)||(this.document&&this.document.selection)){
this.execCommand("selectall");
if(dojo.render.html.moz&&!html){
html="&nbsp;";
}
this.execCommand("inserthtml",html);
}
}
}
},_preFilterContent:function(html){
var ec=html;
dojo.lang.forEach(this.contentPreFilters,function(ef){
ec=ef(ec);
});
if(this.contentDomPreFilters.length>0){
var dom=dojo.doc().createElement("div");
dom.style.display="none";
dojo.body().appendChild(dom);
dom.innerHTML=ec;
dojo.lang.forEach(this.contentDomPreFilters,function(ef){
dom=ef(dom);
});
ec=dom.innerHTML;
dojo.body().removeChild(dom);
}
return ec;
},_postFilterContent:function(html){
var ec=html;
if(this.contentDomPostFilters.length>0){
var dom=this.document.createElement("div");
dom.innerHTML=ec;
dojo.lang.forEach(this.contentDomPostFilters,function(ef){
dom=ef(dom);
});
ec=dom.innerHTML;
}
dojo.lang.forEach(this.contentPostFilters,function(ef){
ec=ef(ec);
});
return ec;
},_lastHeight:0,_updateHeight:function(){
if(!this.isLoaded){
return;
}
if(this.height){
return;
}
var _914=dojo.html.getBorderBox(this.editNode).height;
if(!_914){
_914=dojo.html.getBorderBox(this.document.body).height;
}
if(_914==0){
dojo.debug("Can not figure out the height of the editing area!");
return;
}
this._lastHeight=_914;
this.editorObject.style.height=this._lastHeight+"px";
this.window.scrollTo(0,0);
},_saveContent:function(e){
var _916=dojo.doc().getElementById("dojo.widget.RichText.savedContent");
_916.value+=this._SEPARATOR+this.saveName+":"+this.getEditorContent();
},getEditorContent:function(){
var ec="";
try{
ec=(this._content.length>0)?this._content:this.editNode.innerHTML;
if(dojo.string.trim(ec)=="&nbsp;"){
ec="";
}
}
catch(e){
}
if(dojo.render.html.ie&&!this.object){
var re=new RegExp("(?:<p>&nbsp;</p>[\n\r]*)+$","i");
ec=ec.replace(re,"");
}
ec=this._postFilterContent(ec);
if(this.relativeImageUrls){
var _919=dojo.global().location.protocol+"//"+dojo.global().location.host;
var _91a=dojo.global().location.pathname;
if(_91a.match(/\/$/)){
}else{
var _91b=_91a.split("/");
if(_91b.length){
_91b.pop();
}
_91a=_91b.join("/")+"/";
}
var _91c=new RegExp("(<img[^>]* src=[\"'])("+_919+"("+_91a+")?)","ig");
ec=ec.replace(_91c,"$1");
}
return ec;
},close:function(save,_91e){
if(this.isClosed){
return false;
}
if(arguments.length==0){
save=true;
}
this._content=this._postFilterContent(this.editNode.innerHTML);
var _91f=(this.savedContent!=this._content);
if(this.interval){
clearInterval(this.interval);
}
if(dojo.render.html.ie&&!this.object){
dojo.event.browser.clean(this.editNode);
}
if(this.iframe){
delete this.iframe;
}
if(this.textarea){
with(this.textarea.style){
position="";
left=top="";
if(dojo.render.html.ie){
overflow=this.__overflow;
this.__overflow=null;
}
}
if(save){
this.textarea.value=this._content;
}else{
this.textarea.value=this.savedContent;
}
dojo.html.removeNode(this.domNode);
this.domNode=this.textarea;
}else{
if(save){
if(dojo.render.html.moz){
var nc=dojo.doc().createElement("span");
this.domNode.appendChild(nc);
nc.innerHTML=this.editNode.innerHTML;
}else{
this.domNode.innerHTML=this._content;
}
}else{
this.domNode.innerHTML=this.savedContent;
}
}
dojo.html.removeClass(this.domNode,"RichTextEditable");
this.isClosed=true;
this.isLoaded=false;
delete this.editNode;
if(this.window._frameElement){
this.window._frameElement=null;
}
this.window=null;
this.document=null;
this.object=null;
this.editingArea=null;
this.editorObject=null;
return _91f;
},destroyRendering:function(){
},destroy:function(){
this.destroyRendering();
if(!this.isClosed){
this.close(false);
}
dojo.widget.RichText.superclass.destroy.call(this);
},connect:function(_921,_922,_923){
dojo.event.connect(_921,_922,this,_923);
},disconnect:function(_924,_925,_926){
dojo.event.disconnect(_924,_925,this,_926);
},disconnectAllWithRoot:function(_927){
dojo.deprecated("disconnectAllWithRoot","is deprecated. No need to disconnect manually","0.5");
},_fixContentForMoz:function(html){
html=html.replace(/<strong([ \>])/gi,"<b$1");
html=html.replace(/<\/strong>/gi,"</b>");
html=html.replace(/<em([ \>])/gi,"<i$1");
html=html.replace(/<\/em>/gi,"</i>");
return html;
}});
dojo.provide("dojo.lang.type");
dojo.lang.whatAmI=function(_929){
dojo.deprecated("dojo.lang.whatAmI","use dojo.lang.getType instead","0.5");
return dojo.lang.getType(_929);
};
dojo.lang.whatAmI.custom={};
dojo.lang.getType=function(_92a){
try{
if(dojo.lang.isArray(_92a)){
return "array";
}
if(dojo.lang.isFunction(_92a)){
return "function";
}
if(dojo.lang.isString(_92a)){
return "string";
}
if(dojo.lang.isNumber(_92a)){
return "number";
}
if(dojo.lang.isBoolean(_92a)){
return "boolean";
}
if(dojo.lang.isAlien(_92a)){
return "alien";
}
if(dojo.lang.isUndefined(_92a)){
return "undefined";
}
for(var name in dojo.lang.whatAmI.custom){
if(dojo.lang.whatAmI.custom[name](_92a)){
return name;
}
}
if(dojo.lang.isObject(_92a)){
return "object";
}
}
catch(e){
}
return "unknown";
};
dojo.lang.isNumeric=function(_92c){
return (!isNaN(_92c)&&isFinite(_92c)&&(_92c!=null)&&!dojo.lang.isBoolean(_92c)&&!dojo.lang.isArray(_92c)&&!/^\s*$/.test(_92c));
};
dojo.lang.isBuiltIn=function(_92d){
return (dojo.lang.isArray(_92d)||dojo.lang.isFunction(_92d)||dojo.lang.isString(_92d)||dojo.lang.isNumber(_92d)||dojo.lang.isBoolean(_92d)||(_92d==null)||(_92d instanceof Error)||(typeof _92d=="error"));
};
dojo.lang.isPureObject=function(_92e){
return ((_92e!=null)&&dojo.lang.isObject(_92e)&&_92e.constructor==Object);
};
dojo.lang.isOfType=function(_92f,type,_931){
var _932=false;
if(_931){
_932=_931["optional"];
}
if(_932&&((_92f===null)||dojo.lang.isUndefined(_92f))){
return true;
}
if(dojo.lang.isArray(type)){
var _933=type;
for(var i in _933){
var _935=_933[i];
if(dojo.lang.isOfType(_92f,_935)){
return true;
}
}
return false;
}else{
if(dojo.lang.isString(type)){
type=type.toLowerCase();
}
switch(type){
case Array:
case "array":
return dojo.lang.isArray(_92f);
case Function:
case "function":
return dojo.lang.isFunction(_92f);
case String:
case "string":
return dojo.lang.isString(_92f);
case Number:
case "number":
return dojo.lang.isNumber(_92f);
case "numeric":
return dojo.lang.isNumeric(_92f);
case Boolean:
case "boolean":
return dojo.lang.isBoolean(_92f);
case Object:
case "object":
return dojo.lang.isObject(_92f);
case "pureobject":
return dojo.lang.isPureObject(_92f);
case "builtin":
return dojo.lang.isBuiltIn(_92f);
case "alien":
return dojo.lang.isAlien(_92f);
case "undefined":
return dojo.lang.isUndefined(_92f);
case null:
case "null":
return (_92f===null);
case "optional":
dojo.deprecated("dojo.lang.isOfType(value, [type, \"optional\"])","use dojo.lang.isOfType(value, type, {optional: true} ) instead","0.5");
return ((_92f===null)||dojo.lang.isUndefined(_92f));
default:
if(dojo.lang.isFunction(type)){
return (_92f instanceof type);
}else{
dojo.raise("dojo.lang.isOfType() was passed an invalid type");
}
}
}
dojo.raise("If we get here, it means a bug was introduced above.");
};
dojo.lang.getObject=function(str){
var _937=str.split("."),i=0,obj=dj_global;
do{
obj=obj[_937[i++]];
}while(i<_937.length&&obj);
return (obj!=dj_global)?obj:null;
};
dojo.lang.doesObjectExist=function(str){
var _93b=str.split("."),i=0,obj=dj_global;
do{
obj=obj[_93b[i++]];
}while(i<_93b.length&&obj);
return (obj&&obj!=dj_global);
};
dojo.provide("dojo.lang.assert");
dojo.lang.assert=function(_93e,_93f){
if(!_93e){
var _940="An assert statement failed.\n"+"The method dojo.lang.assert() was called with a 'false' value.\n";
if(_93f){
_940+="Here's the assert message:\n"+_93f+"\n";
}
throw new Error(_940);
}
};
dojo.lang.assertType=function(_941,type,_943){
if(dojo.lang.isString(_943)){
dojo.deprecated("dojo.lang.assertType(value, type, \"message\")","use dojo.lang.assertType(value, type) instead","0.5");
}
if(!dojo.lang.isOfType(_941,type,_943)){
if(!dojo.lang.assertType._errorMessage){
dojo.lang.assertType._errorMessage="Type mismatch: dojo.lang.assertType() failed.";
}
dojo.lang.assert(false,dojo.lang.assertType._errorMessage);
}
};
dojo.lang.assertValidKeywords=function(_944,_945,_946){
var key;
if(!_946){
if(!dojo.lang.assertValidKeywords._errorMessage){
dojo.lang.assertValidKeywords._errorMessage="In dojo.lang.assertValidKeywords(), found invalid keyword:";
}
_946=dojo.lang.assertValidKeywords._errorMessage;
}
if(dojo.lang.isArray(_945)){
for(key in _944){
if(!dojo.lang.inArray(_945,key)){
dojo.lang.assert(false,_946+" "+key);
}
}
}else{
for(key in _944){
if(!(key in _945)){
dojo.lang.assert(false,_946+" "+key);
}
}
}
};
dojo.provide("dojo.AdapterRegistry");
dojo.AdapterRegistry=function(_948){
this.pairs=[];
this.returnWrappers=_948||false;
};
dojo.lang.extend(dojo.AdapterRegistry,{register:function(name,_94a,wrap,_94c,_94d){
var type=(_94d)?"unshift":"push";
this.pairs[type]([name,_94a,wrap,_94c]);
},match:function(){
for(var i=0;i<this.pairs.length;i++){
var pair=this.pairs[i];
if(pair[1].apply(this,arguments)){
if((pair[3])||(this.returnWrappers)){
return pair[2];
}else{
return pair[2].apply(this,arguments);
}
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
dojo.provide("dojo.lang.repr");
dojo.lang.reprRegistry=new dojo.AdapterRegistry();
dojo.lang.registerRepr=function(name,_955,wrap,_957){
dojo.lang.reprRegistry.register(name,_955,wrap,_957);
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
return obj.NAME;
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
(function(){
var m=dojo.lang;
m.registerRepr("arrayLike",m.isArrayLike,m.reprArrayLike);
m.registerRepr("string",m.isString,m.reprString);
m.registerRepr("numbers",m.isNumber,m.reprNumber);
m.registerRepr("boolean",m.isBoolean,m.reprNumber);
})();
dojo.kwCompoundRequire({common:["dojo.lang.common","dojo.lang.assert","dojo.lang.array","dojo.lang.type","dojo.lang.func","dojo.lang.extras","dojo.lang.repr","dojo.lang.declare"]});
dojo.provide("dojo.lang.*");
dojo.provide("dojo.html.iframe");
dojo.html.iframeContentWindow=function(_95d){
var win=dojo.html.getDocumentWindow(dojo.html.iframeContentDocument(_95d))||dojo.html.iframeContentDocument(_95d).__parent__||(_95d.name&&document.frames[_95d.name])||null;
return win;
};
dojo.html.iframeContentDocument=function(_95f){
var doc=_95f.contentDocument||((_95f.contentWindow)&&(_95f.contentWindow.document))||((_95f.name)&&(document.frames[_95f.name])&&(document.frames[_95f.name].document))||null;
return doc;
};
dojo.html.BackgroundIframe=function(node){
if(dojo.render.html.ie55||dojo.render.html.ie60){
var html="<iframe src='javascript:false'"+" style='position: absolute; left: 0px; top: 0px; width: 100%; height: 100%;"+"z-index: -1; filter:Alpha(Opacity=\"0\");' "+">";
this.iframe=dojo.doc().createElement(html);
this.iframe.tabIndex=-1;
if(node){
node.appendChild(this.iframe);
this.domNode=node;
}else{
dojo.body().appendChild(this.iframe);
this.iframe.style.display="none";
}
}
};
dojo.lang.extend(dojo.html.BackgroundIframe,{iframe:null,onResized:function(){
if(this.iframe&&this.domNode&&this.domNode.parentNode){
var _963=dojo.html.getMarginBox(this.domNode);
if(_963.width==0||_963.height==0){
dojo.lang.setTimeout(this,this.onResized,100);
return;
}
this.iframe.style.width=_963.width+"px";
this.iframe.style.height=_963.height+"px";
}
},size:function(node){
if(!this.iframe){
return;
}
var _965=dojo.html.toCoordinateObject(node,true,dojo.html.boxSizing.BORDER_BOX);
with(this.iframe.style){
width=_965.width+"px";
height=_965.height+"px";
left=_965.left+"px";
top=_965.top+"px";
}
},setZIndex:function(node){
if(!this.iframe){
return;
}
if(dojo.dom.isNode(node)){
this.iframe.style.zIndex=dojo.html.getStyle(node,"z-index")-1;
}else{
if(!isNaN(node)){
this.iframe.style.zIndex=node;
}
}
},show:function(){
if(this.iframe){
this.iframe.style.display="block";
}
},hide:function(){
if(this.iframe){
this.iframe.style.display="none";
}
},remove:function(){
if(this.iframe){
dojo.html.removeNode(this.iframe,true);
delete this.iframe;
this.iframe=null;
}
}});
dojo.provide("dojo.widget.PopupContainer");
dojo.declare("dojo.widget.PopupContainerBase",null,function(){
this.queueOnAnimationFinish=[];
},{isShowingNow:false,currentSubpopup:null,beginZIndex:1000,parentPopup:null,parent:null,popupIndex:0,aroundBox:dojo.html.boxSizing.BORDER_BOX,openedForWindow:null,processKey:function(evt){
return false;
},applyPopupBasicStyle:function(){
with(this.domNode.style){
display="none";
position="absolute";
}
},aboutToShow:function(){
},open:function(x,y,_96a,_96b,_96c,_96d){
if(this.isShowingNow){
return;
}
if(this.animationInProgress){
this.queueOnAnimationFinish.push(this.open,arguments);
return;
}
this.aboutToShow();
var _96e=false,node,_970;
if(typeof x=="object"){
node=x;
_970=_96b;
_96b=_96a;
_96a=y;
_96e=true;
}
this.parent=_96a;
dojo.body().appendChild(this.domNode);
_96b=_96b||_96a["domNode"]||[];
var _971=null;
this.isTopLevel=true;
while(_96a){
if(_96a!==this&&(_96a.setOpenedSubpopup!=undefined&&_96a.applyPopupBasicStyle!=undefined)){
_971=_96a;
this.isTopLevel=false;
_971.setOpenedSubpopup(this);
break;
}
_96a=_96a.parent;
}
this.parentPopup=_971;
this.popupIndex=_971?_971.popupIndex+1:1;
if(this.isTopLevel){
var _972=dojo.html.isNode(_96b)?_96b:null;
dojo.widget.PopupManager.opened(this,_972);
}
if(this.isTopLevel&&!dojo.withGlobal(this.openedForWindow||dojo.global(),dojo.html.selection.isCollapsed)){
this._bookmark=dojo.withGlobal(this.openedForWindow||dojo.global(),dojo.html.selection.getBookmark);
}else{
this._bookmark=null;
}
if(_96b instanceof Array){
_96b={left:_96b[0],top:_96b[1],width:0,height:0};
}
with(this.domNode.style){
display="";
zIndex=this.beginZIndex+this.popupIndex;
}
if(_96e){
this.move(node,_96d,_970);
}else{
this.move(x,y,_96d,_96c);
}
this.domNode.style.display="none";
this.explodeSrc=_96b;
this.show();
this.isShowingNow=true;
},move:function(x,y,_975,_976){
var _977=(typeof x=="object");
if(_977){
var _978=_975;
var node=x;
_975=y;
if(!_978){
_978={"BL":"TL","TL":"BL"};
}
dojo.html.placeOnScreenAroundElement(this.domNode,node,_975,this.aroundBox,_978);
}else{
if(!_976){
_976="TL,TR,BL,BR";
}
dojo.html.placeOnScreen(this.domNode,x,y,_975,true,_976);
}
},close:function(_97a){
if(_97a){
this.domNode.style.display="none";
}
if(this.animationInProgress){
this.queueOnAnimationFinish.push(this.close,[]);
return;
}
this.closeSubpopup(_97a);
this.hide();
if(this.bgIframe){
this.bgIframe.hide();
this.bgIframe.size({left:0,top:0,width:0,height:0});
}
if(this.isTopLevel){
dojo.widget.PopupManager.closed(this);
}
this.isShowingNow=false;
if(this.parent){
setTimeout(dojo.lang.hitch(this,function(){
try{
if(this.parent["focus"]){
this.parent.focus();
}else{
this.parent.domNode.focus();
}
}
catch(e){
dojo.debug("No idea how to focus to parent",e);
}
}),10);
}
if(this._bookmark&&dojo.withGlobal(this.openedForWindow||dojo.global(),dojo.html.selection.isCollapsed)){
if(this.openedForWindow){
this.openedForWindow.focus();
}
try{
dojo.withGlobal(this.openedForWindow||dojo.global(),"moveToBookmark",dojo.html.selection,[this._bookmark]);
}
catch(e){
}
}
this._bookmark=null;
},closeAll:function(_97b){
if(this.parentPopup){
this.parentPopup.closeAll(_97b);
}else{
this.close(_97b);
}
},setOpenedSubpopup:function(_97c){
this.currentSubpopup=_97c;
},closeSubpopup:function(_97d){
if(this.currentSubpopup==null){
return;
}
this.currentSubpopup.close(_97d);
this.currentSubpopup=null;
},onShow:function(){
dojo.widget.PopupContainer.superclass.onShow.apply(this,arguments);
this.openedSize={w:this.domNode.style.width,h:this.domNode.style.height};
if(dojo.render.html.ie){
if(!this.bgIframe){
this.bgIframe=new dojo.html.BackgroundIframe();
this.bgIframe.setZIndex(this.domNode);
}
this.bgIframe.size(this.domNode);
this.bgIframe.show();
}
this.processQueue();
},processQueue:function(){
if(!this.queueOnAnimationFinish.length){
return;
}
var func=this.queueOnAnimationFinish.shift();
var args=this.queueOnAnimationFinish.shift();
func.apply(this,args);
},onHide:function(){
dojo.widget.HtmlWidget.prototype.onHide.call(this);
if(this.openedSize){
with(this.domNode.style){
width=this.openedSize.w;
height=this.openedSize.h;
}
}
this.processQueue();
}});
dojo.widget.defineWidget("dojo.widget.PopupContainer",[dojo.widget.HtmlWidget,dojo.widget.PopupContainerBase],{isContainer:true,fillInTemplate:function(){
this.applyPopupBasicStyle();
dojo.widget.PopupContainer.superclass.fillInTemplate.apply(this,arguments);
}});
dojo.widget.PopupManager=new function(){
this.currentMenu=null;
this.currentButton=null;
this.currentFocusMenu=null;
this.focusNode=null;
this.registeredWindows=[];
this.registerWin=function(win){
if(!win.__PopupManagerRegistered){
dojo.event.connect(win.document,"onmousedown",this,"onClick");
dojo.event.connect(win,"onscroll",this,"onClick");
dojo.event.connect(win.document,"onkey",this,"onKey");
win.__PopupManagerRegistered=true;
this.registeredWindows.push(win);
}
};
this.registerAllWindows=function(_981){
if(!_981){
_981=dojo.html.getDocumentWindow(window.top&&window.top.document||window.document);
}
this.registerWin(_981);
for(var i=0;i<_981.frames.length;i++){
try{
var win=dojo.html.getDocumentWindow(_981.frames[i].document);
if(win){
this.registerAllWindows(win);
}
}
catch(e){
}
}
};
this.unRegisterWin=function(win){
if(win.__PopupManagerRegistered){
dojo.event.disconnect(win.document,"onmousedown",this,"onClick");
dojo.event.disconnect(win,"onscroll",this,"onClick");
dojo.event.disconnect(win.document,"onkey",this,"onKey");
win.__PopupManagerRegistered=false;
}
};
this.unRegisterAllWindows=function(){
for(var i=0;i<this.registeredWindows.length;++i){
this.unRegisterWin(this.registeredWindows[i]);
}
this.registeredWindows=[];
};
dojo.addOnLoad(this,"registerAllWindows");
dojo.addOnUnload(this,"unRegisterAllWindows");
this.closed=function(menu){
if(this.currentMenu==menu){
this.currentMenu=null;
this.currentButton=null;
this.currentFocusMenu=null;
}
};
this.opened=function(menu,_988){
if(menu==this.currentMenu){
return;
}
if(this.currentMenu){
this.currentMenu.close();
}
this.currentMenu=menu;
this.currentFocusMenu=menu;
this.currentButton=_988;
};
this.setFocusedMenu=function(menu){
this.currentFocusMenu=menu;
};
this.onKey=function(e){
if(!e.key){
return;
}
if(!this.currentMenu||!this.currentMenu.isShowingNow){
return;
}
var m=this.currentFocusMenu;
while(m){
if(m.processKey(e)){
e.preventDefault();
e.stopPropagation();
break;
}
m=m.parentPopup||m.parentMenu;
}
},this.onClick=function(e){
if(!this.currentMenu){
return;
}
var _98d=dojo.html.getScroll().offset;
var m=this.currentMenu;
while(m){
if(dojo.html.overElement(m.domNode,e)||dojo.html.isDescendantOf(e.target,m.domNode)){
return;
}
m=m.currentSubpopup;
}
if(this.currentButton&&dojo.html.overElement(this.currentButton,e)){
return;
}
this.currentMenu.closeAll(true);
};
};
dojo.provide("dojo.widget.ColorPalette");
dojo.widget.defineWidget("dojo.widget.ColorPalette",dojo.widget.HtmlWidget,{palette:"7x10",_palettes:{"7x10":[["fff","fcc","fc9","ff9","ffc","9f9","9ff","cff","ccf","fcf"],["ccc","f66","f96","ff6","ff3","6f9","3ff","6ff","99f","f9f"],["c0c0c0","f00","f90","fc6","ff0","3f3","6cc","3cf","66c","c6c"],["999","c00","f60","fc3","fc0","3c0","0cc","36f","63f","c3c"],["666","900","c60","c93","990","090","399","33f","60c","939"],["333","600","930","963","660","060","366","009","339","636"],["000","300","630","633","330","030","033","006","309","303"]],"3x4":[["ffffff","00ff00","008000","0000ff"],["c0c0c0","ffff00","ff00ff","000080"],["808080","ff0000","800080","000000"]]},buildRendering:function(){
this.domNode=document.createElement("table");
dojo.html.disableSelection(this.domNode);
dojo.event.connect(this.domNode,"onmousedown",function(e){
e.preventDefault();
});
with(this.domNode){
cellPadding="0";
cellSpacing="1";
border="1";
style.backgroundColor="white";
}
var _990=this._palettes[this.palette];
for(var i=0;i<_990.length;i++){
var tr=this.domNode.insertRow(-1);
for(var j=0;j<_990[i].length;j++){
if(_990[i][j].length==3){
_990[i][j]=_990[i][j].replace(/(.)(.)(.)/,"$1$1$2$2$3$3");
}
var td=tr.insertCell(-1);
with(td.style){
backgroundColor="#"+_990[i][j];
border="1px solid gray";
width=height="15px";
fontSize="1px";
}
td.color="#"+_990[i][j];
td.onmouseover=function(e){
this.style.borderColor="white";
};
td.onmouseout=function(e){
this.style.borderColor="gray";
};
dojo.event.connect(td,"onmousedown",this,"onClick");
td.innerHTML="&nbsp;";
}
}
},onClick:function(e){
this.onColorSelect(e.currentTarget.color);
e.currentTarget.style.borderColor="gray";
},onColorSelect:function(_998){
}});
dojo.provide("dojo.widget.ContentPane");
dojo.widget.defineWidget("dojo.widget.ContentPane",dojo.widget.HtmlWidget,function(){
this._styleNodes=[];
this._onLoadStack=[];
this._onUnloadStack=[];
this._callOnUnload=false;
this._ioBindObj;
this.scriptScope;
this.bindArgs={};
},{isContainer:true,adjustPaths:true,href:"",extractContent:true,parseContent:true,cacheContent:true,preload:false,refreshOnShow:false,handler:"",executeScripts:false,scriptSeparation:true,loadingMessage:"Loading...",isLoaded:false,postCreate:function(args,frag,_99b){
if(this.handler!==""){
this.setHandler(this.handler);
}
if(this.isShowing()||this.preload){
this.loadContents();
}
},show:function(){
if(this.refreshOnShow){
this.refresh();
}else{
this.loadContents();
}
dojo.widget.ContentPane.superclass.show.call(this);
},refresh:function(){
this.isLoaded=false;
this.loadContents();
},loadContents:function(){
if(this.isLoaded){
return;
}
if(dojo.lang.isFunction(this.handler)){
this._runHandler();
}else{
if(this.href!=""){
this._downloadExternalContent(this.href,this.cacheContent&&!this.refreshOnShow);
}
}
},setUrl:function(url){
this.href=url;
this.isLoaded=false;
if(this.preload||this.isShowing()){
this.loadContents();
}
},abort:function(){
var bind=this._ioBindObj;
if(!bind||!bind.abort){
return;
}
bind.abort();
delete this._ioBindObj;
},_downloadExternalContent:function(url,_99f){
this.abort();
this._handleDefaults(this.loadingMessage,"onDownloadStart");
var self=this;
this._ioBindObj=dojo.io.bind(this._cacheSetting({url:url,mimetype:"text/html",handler:function(type,data,xhr){
delete self._ioBindObj;
if(type=="load"){
self.onDownloadEnd.call(self,url,data);
}else{
var e={responseText:xhr.responseText,status:xhr.status,statusText:xhr.statusText,responseHeaders:xhr.getAllResponseHeaders(),text:"Error loading '"+url+"' ("+xhr.status+" "+xhr.statusText+")"};
self._handleDefaults.call(self,e,"onDownloadError");
self.onLoad();
}
}},_99f));
},_cacheSetting:function(_9a5,_9a6){
for(var x in this.bindArgs){
if(dojo.lang.isUndefined(_9a5[x])){
_9a5[x]=this.bindArgs[x];
}
}
if(dojo.lang.isUndefined(_9a5.useCache)){
_9a5.useCache=_9a6;
}
if(dojo.lang.isUndefined(_9a5.preventCache)){
_9a5.preventCache=!_9a6;
}
if(dojo.lang.isUndefined(_9a5.mimetype)){
_9a5.mimetype="text/html";
}
return _9a5;
},onLoad:function(e){
this._runStack("_onLoadStack");
this.isLoaded=true;
},onUnLoad:function(e){
dojo.deprecated(this.widgetType+".onUnLoad, use .onUnload (lowercased load)",0.5);
},onUnload:function(e){
this._runStack("_onUnloadStack");
delete this.scriptScope;
if(this.onUnLoad!==dojo.widget.ContentPane.prototype.onUnLoad){
this.onUnLoad.apply(this,arguments);
}
},_runStack:function(_9ab){
var st=this[_9ab];
var err="";
var _9ae=this.scriptScope||window;
for(var i=0;i<st.length;i++){
try{
st[i].call(_9ae);
}
catch(e){
err+="\n"+st[i]+" failed: "+e.description;
}
}
this[_9ab]=[];
if(err.length){
var name=(_9ab=="_onLoadStack")?"addOnLoad":"addOnUnLoad";
this._handleDefaults(name+" failure\n "+err,"onExecError","debug");
}
},addOnLoad:function(obj,func){
this._pushOnStack(this._onLoadStack,obj,func);
},addOnUnload:function(obj,func){
this._pushOnStack(this._onUnloadStack,obj,func);
},addOnUnLoad:function(){
dojo.deprecated(this.widgetType+".addOnUnLoad, use addOnUnload instead. (lowercased Load)",0.5);
this.addOnUnload.apply(this,arguments);
},_pushOnStack:function(_9b5,obj,func){
if(typeof func=="undefined"){
_9b5.push(obj);
}else{
_9b5.push(function(){
obj[func]();
});
}
},destroy:function(){
this.onUnload();
dojo.widget.ContentPane.superclass.destroy.call(this);
},onExecError:function(e){
},onContentError:function(e){
},onDownloadError:function(e){
},onDownloadStart:function(e){
},onDownloadEnd:function(url,data){
data=this.splitAndFixPaths(data,url);
this.setContent(data);
},_handleDefaults:function(e,_9bf,_9c0){
if(!_9bf){
_9bf="onContentError";
}
if(dojo.lang.isString(e)){
e={text:e};
}
if(!e.text){
e.text=e.toString();
}
e.toString=function(){
return this.text;
};
if(typeof e.returnValue!="boolean"){
e.returnValue=true;
}
if(typeof e.preventDefault!="function"){
e.preventDefault=function(){
this.returnValue=false;
};
}
this[_9bf](e);
if(e.returnValue){
switch(_9c0){
case true:
case "alert":
alert(e.toString());
break;
case "debug":
dojo.debug(e.toString());
break;
default:
if(this._callOnUnload){
this.onUnload();
}
this._callOnUnload=false;
if(arguments.callee._loopStop){
dojo.debug(e.toString());
}else{
arguments.callee._loopStop=true;
this._setContent(e.toString());
}
}
}
arguments.callee._loopStop=false;
},splitAndFixPaths:function(s,url){
var _9c3=[],_9c4=[],tmp=[];
var _9c6=[],_9c7=[],attr=[],_9c9=[];
var str="",path="",fix="",_9cd="",tag="",_9cf="";
if(!url){
url="./";
}
if(s){
var _9d0=/<title[^>]*>([\s\S]*?)<\/title>/i;
while(_9c6=_9d0.exec(s)){
_9c3.push(_9c6[1]);
s=s.substring(0,_9c6.index)+s.substr(_9c6.index+_9c6[0].length);
}
if(this.adjustPaths){
var _9d1=/<[a-z][a-z0-9]*[^>]*\s(?:(?:src|href|style)=[^>])+[^>]*>/i;
var _9d2=/\s(src|href|style)=(['"]?)([\w()\[\]\/.,\\'"-:;#=&?\s@]+?)\2/i;
var _9d3=/^(?:[#]|(?:(?:https?|ftps?|file|javascript|mailto|news):))/;
while(tag=_9d1.exec(s)){
str+=s.substring(0,tag.index);
s=s.substring((tag.index+tag[0].length),s.length);
tag=tag[0];
_9cd="";
while(attr=_9d2.exec(tag)){
path="";
_9cf=attr[3];
switch(attr[1].toLowerCase()){
case "src":
case "href":
if(_9d3.exec(_9cf)){
path=_9cf;
}else{
path=(new dojo.uri.Uri(url,_9cf).toString());
}
break;
case "style":
path=dojo.html.fixPathsInCssText(_9cf,url);
break;
default:
path=_9cf;
}
fix=" "+attr[1]+"="+attr[2]+path+attr[2];
_9cd+=tag.substring(0,attr.index)+fix;
tag=tag.substring((attr.index+attr[0].length),tag.length);
}
str+=_9cd+tag;
}
s=str+s;
}
_9d0=/(?:<(style)[^>]*>([\s\S]*?)<\/style>|<link ([^>]*rel=['"]?stylesheet['"]?[^>]*)>)/i;
while(_9c6=_9d0.exec(s)){
if(_9c6[1]&&_9c6[1].toLowerCase()=="style"){
_9c9.push(dojo.html.fixPathsInCssText(_9c6[2],url));
}else{
if(attr=_9c6[3].match(/href=(['"]?)([^'">]*)\1/i)){
_9c9.push({path:attr[2]});
}
}
s=s.substring(0,_9c6.index)+s.substr(_9c6.index+_9c6[0].length);
}
var _9d0=/<script([^>]*)>([\s\S]*?)<\/script>/i;
var _9d4=/src=(['"]?)([^"']*)\1/i;
var _9d5=/.*(\bdojo\b\.js(?:\.uncompressed\.js)?)$/;
var _9d6=/(?:var )?\bdjConfig\b(?:[\s]*=[\s]*\{[^}]+\}|\.[\w]*[\s]*=[\s]*[^;\n]*)?;?|dojo\.hostenv\.writeIncludes\(\s*\);?/g;
var _9d7=/dojo\.(?:(?:require(?:After)?(?:If)?)|(?:widget\.(?:manager\.)?registerWidgetPackage)|(?:(?:hostenv\.)?setModulePrefix|registerModulePath)|defineNamespace)\((['"]).*?\1\)\s*;?/;
while(_9c6=_9d0.exec(s)){
if(this.executeScripts&&_9c6[1]){
if(attr=_9d4.exec(_9c6[1])){
if(_9d5.exec(attr[2])){
dojo.debug("Security note! inhibit:"+attr[2]+" from  being loaded again.");
}else{
_9c4.push({path:attr[2]});
}
}
}
if(_9c6[2]){
var sc=_9c6[2].replace(_9d6,"");
if(!sc){
continue;
}
while(tmp=_9d7.exec(sc)){
_9c7.push(tmp[0]);
sc=sc.substring(0,tmp.index)+sc.substr(tmp.index+tmp[0].length);
}
if(this.executeScripts){
_9c4.push(sc);
}
}
s=s.substr(0,_9c6.index)+s.substr(_9c6.index+_9c6[0].length);
}
if(this.extractContent){
_9c6=s.match(/<body[^>]*>\s*([\s\S]+)\s*<\/body>/im);
if(_9c6){
s=_9c6[1];
}
}
if(this.executeScripts&&this.scriptSeparation){
var _9d0=/(<[a-zA-Z][a-zA-Z0-9]*\s[^>]*?\S=)((['"])[^>]*scriptScope[^>]*>)/;
var _9d9=/([\s'";:\(])scriptScope(.*)/;
str="";
while(tag=_9d0.exec(s)){
tmp=((tag[3]=="'")?"\"":"'");
fix="";
str+=s.substring(0,tag.index)+tag[1];
while(attr=_9d9.exec(tag[2])){
tag[2]=tag[2].substring(0,attr.index)+attr[1]+"dojo.widget.byId("+tmp+this.widgetId+tmp+").scriptScope"+attr[2];
}
str+=tag[2];
s=s.substr(tag.index+tag[0].length);
}
s=str+s;
}
}
return {"xml":s,"styles":_9c9,"titles":_9c3,"requires":_9c7,"scripts":_9c4,"url":url};
},_setContent:function(cont){
this.destroyChildren();
for(var i=0;i<this._styleNodes.length;i++){
if(this._styleNodes[i]&&this._styleNodes[i].parentNode){
this._styleNodes[i].parentNode.removeChild(this._styleNodes[i]);
}
}
this._styleNodes=[];
try{
var node=this.containerNode||this.domNode;
while(node.firstChild){
dojo.html.destroyNode(node.firstChild);
}
if(typeof cont!="string"){
node.appendChild(cont);
}else{
node.innerHTML=cont;
}
}
catch(e){
e.text="Couldn't load content:"+e.description;
this._handleDefaults(e,"onContentError");
}
},setContent:function(data){
this.abort();
if(this._callOnUnload){
this.onUnload();
}
this._callOnUnload=true;
if(!data||dojo.html.isNode(data)){
this._setContent(data);
this.onResized();
this.onLoad();
}else{
if(typeof data.xml!="string"){
this.href="";
data=this.splitAndFixPaths(data);
}
this._setContent(data.xml);
for(var i=0;i<data.styles.length;i++){
if(data.styles[i].path){
this._styleNodes.push(dojo.html.insertCssFile(data.styles[i].path,dojo.doc(),false,true));
}else{
this._styleNodes.push(dojo.html.insertCssText(data.styles[i]));
}
}
if(this.parseContent){
for(var i=0;i<data.requires.length;i++){
try{
eval(data.requires[i]);
}
catch(e){
e.text="ContentPane: error in package loading calls, "+(e.description||e);
this._handleDefaults(e,"onContentError","debug");
}
}
}
var _9df=this;
function asyncParse(){
if(_9df.executeScripts){
_9df._executeScripts(data.scripts);
}
if(_9df.parseContent){
var node=_9df.containerNode||_9df.domNode;
var _9e1=new dojo.xml.Parse();
var frag=_9e1.parseElement(node,null,true);
dojo.widget.getParser().createSubComponents(frag,_9df);
}
_9df.onResized();
_9df.onLoad();
}
if(dojo.hostenv.isXDomain&&data.requires.length){
dojo.addOnLoad(asyncParse);
}else{
asyncParse();
}
}
},setHandler:function(_9e3){
var fcn=dojo.lang.isFunction(_9e3)?_9e3:window[_9e3];
if(!dojo.lang.isFunction(fcn)){
this._handleDefaults("Unable to set handler, '"+_9e3+"' not a function.","onExecError",true);
return;
}
this.handler=function(){
return fcn.apply(this,arguments);
};
},_runHandler:function(){
var ret=true;
if(dojo.lang.isFunction(this.handler)){
this.handler(this,this.domNode);
ret=false;
}
this.onLoad();
return ret;
},_executeScripts:function(_9e6){
var self=this;
var tmp="",code="";
for(var i=0;i<_9e6.length;i++){
if(_9e6[i].path){
dojo.io.bind(this._cacheSetting({"url":_9e6[i].path,"load":function(type,_9ec){
dojo.lang.hitch(self,tmp=";"+_9ec);
},"error":function(type,_9ee){
_9ee.text=type+" downloading remote script";
self._handleDefaults.call(self,_9ee,"onExecError","debug");
},"mimetype":"text/plain","sync":true},this.cacheContent));
code+=tmp;
}else{
code+=_9e6[i];
}
}
try{
if(this.scriptSeparation){
delete this.scriptScope;
this.scriptScope=new (new Function("_container_",code+"; return this;"))(self);
}else{
var djg=dojo.global();
if(djg.execScript){
djg.execScript(code);
}else{
var djd=dojo.doc();
var sc=djd.createElement("script");
sc.appendChild(djd.createTextNode(code));
(this.containerNode||this.domNode).appendChild(sc);
}
}
}
catch(e){
e.text="Error running scripts from content:\n"+e.description;
this._handleDefaults(e,"onExecError","debug");
}
}});
dojo.provide("dojo.widget.Editor2Toolbar");
dojo.lang.declare("dojo.widget.HandlerManager",null,function(){
this._registeredHandlers=[];
},{registerHandler:function(obj,func){
if(arguments.length==2){
this._registeredHandlers.push(function(){
return obj[func].apply(obj,arguments);
});
}else{
this._registeredHandlers.push(obj);
}
},removeHandler:function(func){
for(var i=0;i<this._registeredHandlers.length;i++){
if(func===this._registeredHandlers[i]){
delete this._registeredHandlers[i];
return;
}
}
dojo.debug("HandlerManager handler "+func+" is not registered, can not remove.");
},destroy:function(){
for(var i=0;i<this._registeredHandlers.length;i++){
delete this._registeredHandlers[i];
}
}});
dojo.widget.Editor2ToolbarItemManager=new dojo.widget.HandlerManager;
dojo.lang.mixin(dojo.widget.Editor2ToolbarItemManager,{getToolbarItem:function(name){
var item;
name=name.toLowerCase();
for(var i=0;i<this._registeredHandlers.length;i++){
item=this._registeredHandlers[i](name);
if(item){
return item;
}
}
switch(name){
case "bold":
case "copy":
case "cut":
case "delete":
case "indent":
case "inserthorizontalrule":
case "insertorderedlist":
case "insertunorderedlist":
case "italic":
case "justifycenter":
case "justifyfull":
case "justifyleft":
case "justifyright":
case "outdent":
case "paste":
case "redo":
case "removeformat":
case "selectall":
case "strikethrough":
case "subscript":
case "superscript":
case "underline":
case "undo":
case "unlink":
case "createlink":
case "insertimage":
case "htmltoggle":
item=new dojo.widget.Editor2ToolbarButton(name);
break;
case "forecolor":
case "hilitecolor":
item=new dojo.widget.Editor2ToolbarColorPaletteButton(name);
break;
case "plainformatblock":
item=new dojo.widget.Editor2ToolbarFormatBlockPlainSelect("formatblock");
break;
case "formatblock":
item=new dojo.widget.Editor2ToolbarFormatBlockSelect("formatblock");
break;
case "fontsize":
item=new dojo.widget.Editor2ToolbarFontSizeSelect("fontsize");
break;
case "fontname":
item=new dojo.widget.Editor2ToolbarFontNameSelect("fontname");
break;
case "inserttable":
case "insertcell":
case "insertcol":
case "insertrow":
case "deletecells":
case "deletecols":
case "deleterows":
case "mergecells":
case "splitcell":
dojo.debug(name+" is implemented in dojo.widget.Editor2Plugin.TableOperation, please require it first.");
break;
case "inserthtml":
case "blockdirltr":
case "blockdirrtl":
case "dirltr":
case "dirrtl":
case "inlinedirltr":
case "inlinedirrtl":
dojo.debug("Not yet implemented toolbar item: "+name);
break;
default:
dojo.debug("dojo.widget.Editor2ToolbarItemManager.getToolbarItem: Unknown toolbar item: "+name);
}
return item;
}});
dojo.addOnUnload(dojo.widget.Editor2ToolbarItemManager,"destroy");
dojo.declare("dojo.widget.Editor2ToolbarButton",null,function(name){
this._name=name;
},{create:function(node,_9fc,_9fd){
this._domNode=node;
var cmd=_9fc.parent.getCommand(this._name);
if(cmd){
this._domNode.title=cmd.getText();
}
this.disableSelection(this._domNode);
this._parentToolbar=_9fc;
dojo.event.connect(this._domNode,"onclick",this,"onClick");
if(!_9fd){
dojo.event.connect(this._domNode,"onmouseover",this,"onMouseOver");
dojo.event.connect(this._domNode,"onmouseout",this,"onMouseOut");
}
},disableSelection:function(_9ff){
dojo.html.disableSelection(_9ff);
var _a00=_9ff.all||_9ff.getElementsByTagName("*");
for(var x=0;x<_a00.length;x++){
dojo.html.disableSelection(_a00[x]);
}
},onMouseOver:function(){
var _a02=dojo.widget.Editor2Manager.getCurrentInstance();
if(_a02){
var _a03=_a02.getCommand(this._name);
if(_a03&&_a03.getState()!=dojo.widget.Editor2Manager.commandState.Disabled){
this.highlightToolbarItem();
}
}
},onMouseOut:function(){
this.unhighlightToolbarItem();
},destroy:function(){
this._domNode=null;
this._parentToolbar=null;
},onClick:function(e){
if(this._domNode&&!this._domNode.disabled&&this._parentToolbar.checkAvailability()){
e.preventDefault();
e.stopPropagation();
var _a05=dojo.widget.Editor2Manager.getCurrentInstance();
if(_a05){
var _a06=_a05.getCommand(this._name);
if(_a06){
_a06.execute();
}
}
}
},refreshState:function(){
var _a07=dojo.widget.Editor2Manager.getCurrentInstance();
var em=dojo.widget.Editor2Manager;
if(_a07){
var _a09=_a07.getCommand(this._name);
if(_a09){
var _a0a=_a09.getState();
if(_a0a!=this._lastState){
switch(_a0a){
case em.commandState.Latched:
this.latchToolbarItem();
break;
case em.commandState.Enabled:
this.enableToolbarItem();
break;
case em.commandState.Disabled:
default:
this.disableToolbarItem();
}
this._lastState=_a0a;
}
}
}
return em.commandState.Enabled;
},latchToolbarItem:function(){
this._domNode.disabled=false;
this.removeToolbarItemStyle(this._domNode);
dojo.html.addClass(this._domNode,this._parentToolbar.ToolbarLatchedItemStyle);
},enableToolbarItem:function(){
this._domNode.disabled=false;
this.removeToolbarItemStyle(this._domNode);
dojo.html.addClass(this._domNode,this._parentToolbar.ToolbarEnabledItemStyle);
},disableToolbarItem:function(){
this._domNode.disabled=true;
this.removeToolbarItemStyle(this._domNode);
dojo.html.addClass(this._domNode,this._parentToolbar.ToolbarDisabledItemStyle);
},highlightToolbarItem:function(){
dojo.html.addClass(this._domNode,this._parentToolbar.ToolbarHighlightedItemStyle);
},unhighlightToolbarItem:function(){
dojo.html.removeClass(this._domNode,this._parentToolbar.ToolbarHighlightedItemStyle);
},removeToolbarItemStyle:function(){
dojo.html.removeClass(this._domNode,this._parentToolbar.ToolbarEnabledItemStyle);
dojo.html.removeClass(this._domNode,this._parentToolbar.ToolbarLatchedItemStyle);
dojo.html.removeClass(this._domNode,this._parentToolbar.ToolbarDisabledItemStyle);
this.unhighlightToolbarItem();
}});
dojo.declare("dojo.widget.Editor2ToolbarDropDownButton",dojo.widget.Editor2ToolbarButton,{onClick:function(){
if(this._domNode&&!this._domNode.disabled&&this._parentToolbar.checkAvailability()){
if(!this._dropdown){
this._dropdown=dojo.widget.createWidget("PopupContainer",{});
this._domNode.appendChild(this._dropdown.domNode);
}
if(this._dropdown.isShowingNow){
this._dropdown.close();
}else{
this.onDropDownShown();
this._dropdown.open(this._domNode,null,this._domNode);
}
}
},destroy:function(){
this.onDropDownDestroy();
if(this._dropdown){
this._dropdown.destroy();
}
dojo.widget.Editor2ToolbarDropDownButton.superclass.destroy.call(this);
},onDropDownShown:function(){
},onDropDownDestroy:function(){
}});
dojo.declare("dojo.widget.Editor2ToolbarColorPaletteButton",dojo.widget.Editor2ToolbarDropDownButton,{onDropDownShown:function(){
if(!this._colorpalette){
this._colorpalette=dojo.widget.createWidget("ColorPalette",{});
this._dropdown.addChild(this._colorpalette);
this.disableSelection(this._dropdown.domNode);
this.disableSelection(this._colorpalette.domNode);
dojo.event.connect(this._colorpalette,"onColorSelect",this,"setColor");
dojo.event.connect(this._dropdown,"open",this,"latchToolbarItem");
dojo.event.connect(this._dropdown,"close",this,"enableToolbarItem");
}
},setColor:function(_a0b){
this._dropdown.close();
var _a0c=dojo.widget.Editor2Manager.getCurrentInstance();
if(_a0c){
var _a0d=_a0c.getCommand(this._name);
if(_a0d){
_a0d.execute(_a0b);
}
}
}});
dojo.declare("dojo.widget.Editor2ToolbarFormatBlockPlainSelect",dojo.widget.Editor2ToolbarButton,{create:function(node,_a0f){
this._domNode=node;
this._parentToolbar=_a0f;
this._domNode=node;
this.disableSelection(this._domNode);
dojo.event.connect(this._domNode,"onchange",this,"onChange");
},destroy:function(){
this._domNode=null;
},onChange:function(){
if(this._parentToolbar.checkAvailability()){
var sv=this._domNode.value.toLowerCase();
var _a11=dojo.widget.Editor2Manager.getCurrentInstance();
if(_a11){
var _a12=_a11.getCommand(this._name);
if(_a12){
_a12.execute(sv);
}
}
}
},refreshState:function(){
if(this._domNode){
dojo.widget.Editor2ToolbarFormatBlockPlainSelect.superclass.refreshState.call(this);
var _a13=dojo.widget.Editor2Manager.getCurrentInstance();
if(_a13){
var _a14=_a13.getCommand(this._name);
if(_a14){
var _a15=_a14.getValue();
if(!_a15){
_a15="";
}
dojo.lang.forEach(this._domNode.options,function(item){
if(item.value.toLowerCase()==_a15.toLowerCase()){
item.selected=true;
}
});
}
}
}
}});
dojo.declare("dojo.widget.Editor2ToolbarComboItem",dojo.widget.Editor2ToolbarDropDownButton,{href:null,create:function(node,_a18){
dojo.widget.Editor2ToolbarComboItem.superclass.create.apply(this,arguments);
if(!this._contentPane){
this._contentPane=dojo.widget.createWidget("ContentPane",{preload:"true"});
this._contentPane.addOnLoad(this,"setup");
this._contentPane.setUrl(this.href);
}
},onMouseOver:function(e){
if(this._lastState!=dojo.widget.Editor2Manager.commandState.Disabled){
dojo.html.addClass(e.currentTarget,this._parentToolbar.ToolbarHighlightedSelectStyle);
}
},onMouseOut:function(e){
dojo.html.removeClass(e.currentTarget,this._parentToolbar.ToolbarHighlightedSelectStyle);
},onDropDownShown:function(){
if(!this._dropdown.__addedContentPage){
this._dropdown.addChild(this._contentPane);
this._dropdown.__addedContentPage=true;
}
},setup:function(){
},onChange:function(e){
if(this._parentToolbar.checkAvailability()){
var name=e.currentTarget.getAttribute("dropDownItemName");
var _a1d=dojo.widget.Editor2Manager.getCurrentInstance();
if(_a1d){
var _a1e=_a1d.getCommand(this._name);
if(_a1e){
_a1e.execute(name);
}
}
}
this._dropdown.close();
},onMouseOverItem:function(e){
dojo.html.addClass(e.currentTarget,this._parentToolbar.ToolbarHighlightedSelectItemStyle);
},onMouseOutItem:function(e){
dojo.html.removeClass(e.currentTarget,this._parentToolbar.ToolbarHighlightedSelectItemStyle);
}});
dojo.declare("dojo.widget.Editor2ToolbarFormatBlockSelect",dojo.widget.Editor2ToolbarComboItem,{href:dojo.uri.moduleUri("dojo.widget","templates/Editor2/EditorToolbar_FormatBlock.html"),setup:function(){
dojo.widget.Editor2ToolbarFormatBlockSelect.superclass.setup.call(this);
var _a21=this._contentPane.domNode.all||this._contentPane.domNode.getElementsByTagName("*");
this._blockNames={};
this._blockDisplayNames={};
for(var x=0;x<_a21.length;x++){
var node=_a21[x];
dojo.html.disableSelection(node);
var name=node.getAttribute("dropDownItemName");
if(name){
this._blockNames[name]=node;
var _a25=node.getElementsByTagName(name);
this._blockDisplayNames[name]=_a25[_a25.length-1].innerHTML;
}
}
for(var name in this._blockNames){
dojo.event.connect(this._blockNames[name],"onclick",this,"onChange");
dojo.event.connect(this._blockNames[name],"onmouseover",this,"onMouseOverItem");
dojo.event.connect(this._blockNames[name],"onmouseout",this,"onMouseOutItem");
}
},onDropDownDestroy:function(){
if(this._blockNames){
for(var name in this._blockNames){
delete this._blockNames[name];
delete this._blockDisplayNames[name];
}
}
},refreshState:function(){
dojo.widget.Editor2ToolbarFormatBlockSelect.superclass.refreshState.call(this);
if(this._lastState!=dojo.widget.Editor2Manager.commandState.Disabled){
var _a27=dojo.widget.Editor2Manager.getCurrentInstance();
if(_a27){
var _a28=_a27.getCommand(this._name);
if(_a28){
var _a29=_a28.getValue();
if(_a29==this._lastSelectedFormat&&this._blockDisplayNames){
return this._lastState;
}
this._lastSelectedFormat=_a29;
var _a2a=this._domNode.getElementsByTagName("label")[0];
var _a2b=false;
if(this._blockDisplayNames){
for(var name in this._blockDisplayNames){
if(name==_a29){
_a2a.innerHTML=this._blockDisplayNames[name];
_a2b=true;
break;
}
}
if(!_a2b){
_a2a.innerHTML="&nbsp;";
}
}
}
}
}
return this._lastState;
}});
dojo.declare("dojo.widget.Editor2ToolbarFontSizeSelect",dojo.widget.Editor2ToolbarComboItem,{href:dojo.uri.moduleUri("dojo.widget","templates/Editor2/EditorToolbar_FontSize.html"),setup:function(){
dojo.widget.Editor2ToolbarFormatBlockSelect.superclass.setup.call(this);
var _a2d=this._contentPane.domNode.all||this._contentPane.domNode.getElementsByTagName("*");
this._fontsizes={};
this._fontSizeDisplayNames={};
for(var x=0;x<_a2d.length;x++){
var node=_a2d[x];
dojo.html.disableSelection(node);
var name=node.getAttribute("dropDownItemName");
if(name){
this._fontsizes[name]=node;
this._fontSizeDisplayNames[name]=node.getElementsByTagName("font")[0].innerHTML;
}
}
for(var name in this._fontsizes){
dojo.event.connect(this._fontsizes[name],"onclick",this,"onChange");
dojo.event.connect(this._fontsizes[name],"onmouseover",this,"onMouseOverItem");
dojo.event.connect(this._fontsizes[name],"onmouseout",this,"onMouseOutItem");
}
},onDropDownDestroy:function(){
if(this._fontsizes){
for(var name in this._fontsizes){
delete this._fontsizes[name];
delete this._fontSizeDisplayNames[name];
}
}
},refreshState:function(){
dojo.widget.Editor2ToolbarFormatBlockSelect.superclass.refreshState.call(this);
if(this._lastState!=dojo.widget.Editor2Manager.commandState.Disabled){
var _a32=dojo.widget.Editor2Manager.getCurrentInstance();
if(_a32){
var _a33=_a32.getCommand(this._name);
if(_a33){
var size=_a33.getValue();
if(size==this._lastSelectedSize&&this._fontSizeDisplayNames){
return this._lastState;
}
this._lastSelectedSize=size;
var _a35=this._domNode.getElementsByTagName("label")[0];
var _a36=false;
if(this._fontSizeDisplayNames){
for(var name in this._fontSizeDisplayNames){
if(name==size){
_a35.innerHTML=this._fontSizeDisplayNames[name];
_a36=true;
break;
}
}
if(!_a36){
_a35.innerHTML="&nbsp;";
}
}
}
}
}
return this._lastState;
}});
dojo.declare("dojo.widget.Editor2ToolbarFontNameSelect",dojo.widget.Editor2ToolbarFontSizeSelect,{href:dojo.uri.moduleUri("dojo.widget","templates/Editor2/EditorToolbar_FontName.html")});
dojo.widget.defineWidget("dojo.widget.Editor2Toolbar",dojo.widget.HtmlWidget,function(){
dojo.event.connect(this,"fillInTemplate",dojo.lang.hitch(this,function(){
if(dojo.render.html.ie){
this.domNode.style.zoom=1;
}
}));
},{templateString:"<div dojoAttachPoint=\"domNode\" class=\"EditorToolbarDomNode\" unselectable=\"on\">\r\n\t<table cellpadding=\"3\" cellspacing=\"0\" border=\"0\">\r\n\t\t<!--\r\n\t\t\tour toolbar should look something like:\r\n\r\n\t\t\t+=======+=======+=======+=============================================+\r\n\t\t\t| w   w | style | copy  | bo | it | un | le | ce | ri |\r\n\t\t\t| w w w | style |=======|==============|==============|\r\n\t\t\t|  w w  | style | paste |  undo | redo | change style |\r\n\t\t\t+=======+=======+=======+=============================================+\r\n\t\t-->\r\n\t\t<tbody>\r\n\t\t\t<tr valign=\"top\">\r\n\t\t\t\t<td rowspan=\"2\">\r\n\t\t\t\t\t<div class=\"bigIcon\" dojoAttachPoint=\"wikiWordButton\"\r\n\t\t\t\t\t\tdojoOnClick=\"wikiWordClick; buttonClick;\">\r\n\t\t\t\t\t\t<span style=\"font-size: 30px; margin-left: 5px;\">\r\n\t\t\t\t\t\t\tW\r\n\t\t\t\t\t\t</span>\r\n\t\t\t\t\t</div>\r\n\t\t\t\t</td>\r\n\t\t\t\t<td rowspan=\"2\">\r\n\t\t\t\t\t<div class=\"bigIcon\" dojoAttachPoint=\"styleDropdownButton\"\r\n\t\t\t\t\t\tdojoOnClick=\"styleDropdownClick; buttonClick;\">\r\n\t\t\t\t\t\t<span unselectable=\"on\"\r\n\t\t\t\t\t\t\tstyle=\"font-size: 30px; margin-left: 5px;\">\r\n\t\t\t\t\t\t\tS\r\n\t\t\t\t\t\t</span>\r\n\t\t\t\t\t</div>\r\n\t\t\t\t\t<div class=\"StyleDropdownContainer\" style=\"display: none;\"\r\n\t\t\t\t\t\tdojoAttachPoint=\"styleDropdownContainer\">\r\n\t\t\t\t\t\t<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"\r\n\t\t\t\t\t\t\theight=\"100%\" width=\"100%\">\r\n\t\t\t\t\t\t\t<tr valign=\"top\">\r\n\t\t\t\t\t\t\t\t<td rowspan=\"2\">\r\n\t\t\t\t\t\t\t\t\t<div style=\"height: 245px; overflow: auto;\">\r\n\t\t\t\t\t\t\t\t\t\t<div class=\"headingContainer\"\r\n\t\t\t\t\t\t\t\t\t\t\tunselectable=\"on\"\r\n\t\t\t\t\t\t\t\t\t\t\tdojoOnClick=\"normalTextClick\">normal</div>\r\n\t\t\t\t\t\t\t\t\t\t<h1 class=\"headingContainer\"\r\n\t\t\t\t\t\t\t\t\t\t\tunselectable=\"on\"\r\n\t\t\t\t\t\t\t\t\t\t\tdojoOnClick=\"h1TextClick\">Heading 1</h1>\r\n\t\t\t\t\t\t\t\t\t\t<h2 class=\"headingContainer\"\r\n\t\t\t\t\t\t\t\t\t\t\tunselectable=\"on\"\r\n\t\t\t\t\t\t\t\t\t\t\tdojoOnClick=\"h2TextClick\">Heading 2</h2>\r\n\t\t\t\t\t\t\t\t\t\t<h3 class=\"headingContainer\"\r\n\t\t\t\t\t\t\t\t\t\t\tunselectable=\"on\"\r\n\t\t\t\t\t\t\t\t\t\t\tdojoOnClick=\"h3TextClick\">Heading 3</h3>\r\n\t\t\t\t\t\t\t\t\t\t<h4 class=\"headingContainer\"\r\n\t\t\t\t\t\t\t\t\t\t\tunselectable=\"on\"\r\n\t\t\t\t\t\t\t\t\t\t\tdojoOnClick=\"h4TextClick\">Heading 4</h4>\r\n\t\t\t\t\t\t\t\t\t\t<div class=\"headingContainer\"\r\n\t\t\t\t\t\t\t\t\t\t\tunselectable=\"on\"\r\n\t\t\t\t\t\t\t\t\t\t\tdojoOnClick=\"blahTextClick\">blah</div>\r\n\t\t\t\t\t\t\t\t\t\t<div class=\"headingContainer\"\r\n\t\t\t\t\t\t\t\t\t\t\tunselectable=\"on\"\r\n\t\t\t\t\t\t\t\t\t\t\tdojoOnClick=\"blahTextClick\">blah</div>\r\n\t\t\t\t\t\t\t\t\t\t<div class=\"headingContainer\"\r\n\t\t\t\t\t\t\t\t\t\t\tunselectable=\"on\"\r\n\t\t\t\t\t\t\t\t\t\t\tdojoOnClick=\"blahTextClick\">blah</div>\r\n\t\t\t\t\t\t\t\t\t\t<div class=\"headingContainer\">blah</div>\r\n\t\t\t\t\t\t\t\t\t\t<div class=\"headingContainer\">blah</div>\r\n\t\t\t\t\t\t\t\t\t\t<div class=\"headingContainer\">blah</div>\r\n\t\t\t\t\t\t\t\t\t\t<div class=\"headingContainer\">blah</div>\r\n\t\t\t\t\t\t\t\t\t</div>\r\n\t\t\t\t\t\t\t\t</td>\r\n\t\t\t\t\t\t\t\t<!--\r\n\t\t\t\t\t\t\t\t<td>\r\n\t\t\t\t\t\t\t\t\t<span class=\"iconContainer\" dojoOnClick=\"buttonClick;\">\r\n\t\t\t\t\t\t\t\t\t\t<span class=\"icon justifyleft\" \r\n\t\t\t\t\t\t\t\t\t\t\tstyle=\"float: left;\">&nbsp;</span>\r\n\t\t\t\t\t\t\t\t\t</span>\r\n\t\t\t\t\t\t\t\t\t<span class=\"iconContainer\" dojoOnClick=\"buttonClick;\">\r\n\t\t\t\t\t\t\t\t\t\t<span class=\"icon justifycenter\" \r\n\t\t\t\t\t\t\t\t\t\t\tstyle=\"float: left;\">&nbsp;</span>\r\n\t\t\t\t\t\t\t\t\t</span>\r\n\t\t\t\t\t\t\t\t\t<span class=\"iconContainer\" dojoOnClick=\"buttonClick;\">\r\n\t\t\t\t\t\t\t\t\t\t<span class=\"icon justifyright\" \r\n\t\t\t\t\t\t\t\t\t\t\tstyle=\"float: left;\">&nbsp;</span>\r\n\t\t\t\t\t\t\t\t\t</span>\r\n\t\t\t\t\t\t\t\t\t<span class=\"iconContainer\" dojoOnClick=\"buttonClick;\">\r\n\t\t\t\t\t\t\t\t\t\t<span class=\"icon justifyfull\" \r\n\t\t\t\t\t\t\t\t\t\t\tstyle=\"float: left;\">&nbsp;</span>\r\n\t\t\t\t\t\t\t\t\t</span>\r\n\t\t\t\t\t\t\t\t</td>\r\n\t\t\t\t\t\t\t\t-->\r\n\t\t\t\t\t\t\t</tr>\r\n\t\t\t\t\t\t\t<tr valign=\"top\">\r\n\t\t\t\t\t\t\t\t<td>\r\n\t\t\t\t\t\t\t\t\tthud\r\n\t\t\t\t\t\t\t\t</td>\r\n\t\t\t\t\t\t\t</tr>\r\n\t\t\t\t\t\t</table>\r\n\t\t\t\t\t</div>\r\n\t\t\t\t</td>\r\n\t\t\t\t<td>\r\n\t\t\t\t\t<!-- copy -->\r\n\t\t\t\t\t<span class=\"iconContainer\" dojoAttachPoint=\"copyButton\"\r\n\t\t\t\t\t\tunselectable=\"on\"\r\n\t\t\t\t\t\tdojoOnClick=\"copyClick; buttonClick;\">\r\n\t\t\t\t\t\t<span class=\"icon copy\" \r\n\t\t\t\t\t\t\tunselectable=\"on\"\r\n\t\t\t\t\t\t\tstyle=\"float: left;\">&nbsp;</span> copy\r\n\t\t\t\t\t</span>\r\n\t\t\t\t\t<!-- \"droppable\" options -->\r\n\t\t\t\t\t<span class=\"iconContainer\" dojoAttachPoint=\"boldButton\"\r\n\t\t\t\t\t\tunselectable=\"on\"\r\n\t\t\t\t\t\tdojoOnClick=\"boldClick; buttonClick;\">\r\n\t\t\t\t\t\t<span class=\"icon bold\" unselectable=\"on\">&nbsp;</span>\r\n\t\t\t\t\t</span>\r\n\t\t\t\t\t<span class=\"iconContainer\" dojoAttachPoint=\"italicButton\"\r\n\t\t\t\t\t\tdojoOnClick=\"italicClick; buttonClick;\">\r\n\t\t\t\t\t\t<span class=\"icon italic\" unselectable=\"on\">&nbsp;</span>\r\n\t\t\t\t\t</span>\r\n\t\t\t\t\t<span class=\"iconContainer\" dojoAttachPoint=\"underlineButton\"\r\n\t\t\t\t\t\tdojoOnClick=\"underlineClick; buttonClick;\">\r\n\t\t\t\t\t\t<span class=\"icon underline\" unselectable=\"on\">&nbsp;</span>\r\n\t\t\t\t\t</span>\r\n\t\t\t\t\t<span class=\"iconContainer\" dojoAttachPoint=\"leftButton\"\r\n\t\t\t\t\t\tdojoOnClick=\"leftClick; buttonClick;\">\r\n\t\t\t\t\t\t<span class=\"icon justifyleft\" unselectable=\"on\">&nbsp;</span>\r\n\t\t\t\t\t</span>\r\n\t\t\t\t\t<span class=\"iconContainer\" dojoAttachPoint=\"fullButton\"\r\n\t\t\t\t\t\tdojoOnClick=\"fullClick; buttonClick;\">\r\n\t\t\t\t\t\t<span class=\"icon justifyfull\" unselectable=\"on\">&nbsp;</span>\r\n\t\t\t\t\t</span>\r\n\t\t\t\t\t<span class=\"iconContainer\" dojoAttachPoint=\"rightButton\"\r\n\t\t\t\t\t\tdojoOnClick=\"rightClick; buttonClick;\">\r\n\t\t\t\t\t\t<span class=\"icon justifyright\" unselectable=\"on\">&nbsp;</span>\r\n\t\t\t\t\t</span>\r\n\t\t\t\t</td>\r\n\t\t\t</tr>\r\n\t\t\t<tr>\r\n\t\t\t\t<td>\r\n\t\t\t\t\t<!-- paste -->\r\n\t\t\t\t\t<span class=\"iconContainer\" dojoAttachPoint=\"pasteButton\"\r\n\t\t\t\t\t\tdojoOnClick=\"pasteClick; buttonClick;\" unselectable=\"on\">\r\n\t\t\t\t\t\t<span class=\"icon paste\" style=\"float: left;\" unselectable=\"on\">&nbsp;</span> paste\r\n\t\t\t\t\t</span>\r\n\t\t\t\t\t<!-- \"droppable\" options -->\r\n\t\t\t\t\t<span class=\"iconContainer\" dojoAttachPoint=\"undoButton\"\r\n\t\t\t\t\t\tdojoOnClick=\"undoClick; buttonClick;\" unselectable=\"on\">\r\n\t\t\t\t\t\t<span class=\"icon undo\" style=\"float: left;\" unselectable=\"on\">&nbsp;</span> undo\r\n\t\t\t\t\t</span>\r\n\t\t\t\t\t<span class=\"iconContainer\" dojoAttachPoint=\"redoButton\"\r\n\t\t\t\t\t\tdojoOnClick=\"redoClick; buttonClick;\" unselectable=\"on\">\r\n\t\t\t\t\t\t<span class=\"icon redo\" style=\"float: left;\" unselectable=\"on\">&nbsp;</span> redo\r\n\t\t\t\t\t</span>\r\n\t\t\t\t</td>\t\r\n\t\t\t</tr>\r\n\t\t</tbody>\r\n\t</table>\r\n</div>\r\n",templateCssString:".StyleDropdownContainer {\r\n\tposition: absolute;\r\n\tz-index: 1000;\r\n\toverflow: auto;\r\n\tcursor: default;\r\n\twidth: 250px;\r\n\theight: 250px;\r\n\tbackground-color: white;\r\n\tborder: 1px solid black;\r\n}\r\n\r\n.ColorDropdownContainer {\r\n\tposition: absolute;\r\n\tz-index: 1000;\r\n\toverflow: auto;\r\n\tcursor: default;\r\n\twidth: 250px;\r\n\theight: 150px;\r\n\tbackground-color: white;\r\n\tborder: 1px solid black;\r\n}\r\n\r\n.EditorToolbarDomNode {\r\n\tbackground-image: url(buttons/bg-fade.png);\r\n\tbackground-repeat: repeat-x;\r\n\tbackground-position: 0px -50px;\r\n}\r\n\r\n.EditorToolbarSmallBg {\r\n\tbackground-image: url(images/toolbar-bg.gif);\r\n\tbackground-repeat: repeat-x;\r\n\tbackground-position: 0px 0px;\r\n}\r\n\r\n/*\r\nbody {\r\n\tbackground:url(images/blank.gif) fixed;\r\n}*/\r\n\r\n.IEFixedToolbar {\r\n\tposition:absolute;\r\n\t/* top:0; */\r\n\ttop: expression(eval((document.documentElement||document.body).scrollTop));\r\n}\r\n\r\ndiv.bigIcon {\r\n\twidth: 40px;\r\n\theight: 40px; \r\n\t/* background-color: white; */\r\n\t/* border: 1px solid #a6a7a3; */\r\n\tfont-family: Verdana, Trebuchet, Tahoma, Arial;\r\n}\r\n\r\n.iconContainer {\r\n\tfont-family: Verdana, Trebuchet, Tahoma, Arial;\r\n\tfont-size: 13px;\r\n\tfloat: left;\r\n\theight: 18px;\r\n\tdisplay: block;\r\n\t/* background-color: white; */\r\n\tcursor: pointer;\r\n\tpadding: 1px 4px 1px 1px; /* almost the same as a transparent border */\r\n\tborder: 0px;\r\n}\r\n\r\n.dojoE2TBIcon {\r\n\tdisplay: block;\r\n\ttext-align: center;\r\n\tmin-width: 18px;\r\n\twidth: 18px;\r\n\theight: 18px;\r\n\t/* background-color: #a6a7a3; */\r\n\tbackground-repeat: no-repeat;\r\n\tbackground-image: url(buttons/aggregate.gif);\r\n}\r\n\r\n\r\n.dojoE2TBIcon[class~=dojoE2TBIcon] {\r\n}\r\n\r\n.ToolbarButtonLatched {\r\n    border: #316ac5 1px solid; !important;\r\n    padding: 0px 3px 0px 0px; !important; /* make room for border */\r\n    background-color: #c1d2ee;\r\n}\r\n\r\n.ToolbarButtonHighlighted {\r\n    border: #316ac5 1px solid; !important;\r\n    padding: 0px 3px 0px 0px; !important; /* make room for border */\r\n    background-color: #dff1ff;\r\n}\r\n\r\n.ToolbarButtonDisabled{\r\n    filter: gray() alpha(opacity=30); /* IE */\r\n    opacity: 0.30; /* Safari, Opera and Mozilla */\r\n}\r\n\r\n.headingContainer {\r\n\twidth: 150px;\r\n\theight: 30px;\r\n\tmargin: 0px;\r\n\t/* padding-left: 5px; */\r\n\toverflow: hidden;\r\n\tline-height: 25px;\r\n\tborder-bottom: 1px solid black;\r\n\tborder-top: 1px solid white;\r\n}\r\n\r\n.EditorToolbarDomNode select {\r\n\tfont-size: 14px;\r\n}\r\n \r\n.dojoE2TBIcon_Sep { width: 5px; min-width: 5px; max-width: 5px; background-position: 0px 0px}\r\n.dojoE2TBIcon_Backcolor { background-position: -18px 0px}\r\n.dojoE2TBIcon_Bold { background-position: -36px 0px}\r\n.dojoE2TBIcon_Cancel { background-position: -54px 0px}\r\n.dojoE2TBIcon_Copy { background-position: -72px 0px}\r\n.dojoE2TBIcon_Link { background-position: -90px 0px}\r\n.dojoE2TBIcon_Cut { background-position: -108px 0px}\r\n.dojoE2TBIcon_Delete { background-position: -126px 0px}\r\n.dojoE2TBIcon_TextColor { background-position: -144px 0px}\r\n.dojoE2TBIcon_BackgroundColor { background-position: -162px 0px}\r\n.dojoE2TBIcon_Indent { background-position: -180px 0px}\r\n.dojoE2TBIcon_HorizontalLine { background-position: -198px 0px}\r\n.dojoE2TBIcon_Image { background-position: -216px 0px}\r\n.dojoE2TBIcon_NumberedList { background-position: -234px 0px}\r\n.dojoE2TBIcon_Table { background-position: -252px 0px}\r\n.dojoE2TBIcon_BulletedList { background-position: -270px 0px}\r\n.dojoE2TBIcon_Italic { background-position: -288px 0px}\r\n.dojoE2TBIcon_CenterJustify { background-position: -306px 0px}\r\n.dojoE2TBIcon_BlockJustify { background-position: -324px 0px}\r\n.dojoE2TBIcon_LeftJustify { background-position: -342px 0px}\r\n.dojoE2TBIcon_RightJustify { background-position: -360px 0px}\r\n.dojoE2TBIcon_left_to_right { background-position: -378px 0px}\r\n.dojoE2TBIcon_list_bullet_indent { background-position: -396px 0px}\r\n.dojoE2TBIcon_list_bullet_outdent { background-position: -414px 0px}\r\n.dojoE2TBIcon_list_num_indent { background-position: -432px 0px}\r\n.dojoE2TBIcon_list_num_outdent { background-position: -450px 0px}\r\n.dojoE2TBIcon_Outdent { background-position: -468px 0px}\r\n.dojoE2TBIcon_Paste { background-position: -486px 0px}\r\n.dojoE2TBIcon_Redo { background-position: -504px 0px}\r\ndojoE2TBIcon_RemoveFormat { background-position: -522px 0px}\r\n.dojoE2TBIcon_right_to_left { background-position: -540px 0px}\r\n.dojoE2TBIcon_Save { background-position: -558px 0px}\r\n.dojoE2TBIcon_Space { background-position: -576px 0px}\r\n.dojoE2TBIcon_StrikeThrough { background-position: -594px 0px}\r\n.dojoE2TBIcon_Subscript { background-position: -612px 0px}\r\n.dojoE2TBIcon_Superscript { background-position: -630px 0px}\r\n.dojoE2TBIcon_Underline { background-position: -648px 0px}\r\n.dojoE2TBIcon_Undo { background-position: -666px 0px}\r\n.dojoE2TBIcon_WikiWord { background-position: -684px 0px}\r\n\r\n",templateCssPath:dojo.uri.moduleUri("dojo.widget","templates/EditorToolbar.css"),ToolbarLatchedItemStyle:"ToolbarButtonLatched",ToolbarEnabledItemStyle:"ToolbarButtonEnabled",ToolbarDisabledItemStyle:"ToolbarButtonDisabled",ToolbarHighlightedItemStyle:"ToolbarButtonHighlighted",ToolbarHighlightedSelectStyle:"ToolbarSelectHighlighted",ToolbarHighlightedSelectItemStyle:"ToolbarSelectHighlightedItem",postCreate:function(){
var _a38=dojo.html.getElementsByClass("dojoEditorToolbarItem",this.domNode);
this.items={};
for(var x=0;x<_a38.length;x++){
var node=_a38[x];
var _a3b=node.getAttribute("dojoETItemName");
if(_a3b){
var item=dojo.widget.Editor2ToolbarItemManager.getToolbarItem(_a3b);
if(item){
item.create(node,this);
this.items[_a3b.toLowerCase()]=item;
}else{
node.style.display="none";
}
}
}
},update:function(){
for(var cmd in this.items){
this.items[cmd].refreshState();
}
},shareGroup:"",checkAvailability:function(){
if(!this.shareGroup){
this.parent.focus();
return true;
}
var _a3e=dojo.widget.Editor2Manager.getCurrentInstance();
if(this.shareGroup==_a3e.toolbarGroup){
return true;
}
return false;
},destroy:function(){
for(var it in this.items){
this.items[it].destroy();
delete this.items[it];
}
dojo.widget.Editor2Toolbar.superclass.destroy.call(this);
}});
dojo.provide("dojo.uri.cache");
dojo.uri.cache={_cache:{},set:function(uri,_a41){
this._cache[uri.toString()]=_a41;
return uri;
},remove:function(uri){
delete this._cache[uri.toString()];
},get:function(uri){
var key=uri.toString();
var _a45=this._cache[key];
if(!_a45){
_a45=dojo.hostenv.getText(key);
if(_a45){
this._cache[key]=_a45;
}
}
return _a45;
},allow:function(uri){
return uri;
}};
dojo.provide("dojo.lfx.shadow");
dojo.lfx.shadow=function(node){
this.shadowPng=dojo.uri.moduleUri("dojo.html","images/shadow");
this.shadowThickness=8;
this.shadowOffset=15;
this.init(node);
};
dojo.extend(dojo.lfx.shadow,{init:function(node){
this.node=node;
this.pieces={};
var x1=-1*this.shadowThickness;
var y0=this.shadowOffset;
var y1=this.shadowOffset+this.shadowThickness;
this._makePiece("tl","top",y0,"left",x1);
this._makePiece("l","top",y1,"left",x1,"scale");
this._makePiece("tr","top",y0,"left",0);
this._makePiece("r","top",y1,"left",0,"scale");
this._makePiece("bl","top",0,"left",x1);
this._makePiece("b","top",0,"left",0,"crop");
this._makePiece("br","top",0,"left",0);
},_makePiece:function(name,_a4d,_a4e,_a4f,_a50,_a51){
var img;
var url=this.shadowPng+name.toUpperCase()+".png";
if(dojo.render.html.ie55||dojo.render.html.ie60){
img=dojo.doc().createElement("div");
img.style.filter="progid:DXImageTransform.Microsoft.AlphaImageLoader(src='"+url+"'"+(_a51?", sizingMethod='"+_a51+"'":"")+")";
}else{
img=dojo.doc().createElement("img");
img.src=url;
}
img.style.position="absolute";
img.style[_a4d]=_a4e+"px";
img.style[_a4f]=_a50+"px";
img.style.width=this.shadowThickness+"px";
img.style.height=this.shadowThickness+"px";
this.pieces[name]=img;
this.node.appendChild(img);
},size:function(_a54,_a55){
var _a56=_a55-(this.shadowOffset+this.shadowThickness+1);
if(_a56<0){
_a56=0;
}
if(_a55<1){
_a55=1;
}
if(_a54<1){
_a54=1;
}
with(this.pieces){
l.style.height=_a56+"px";
r.style.height=_a56+"px";
b.style.width=(_a54-1)+"px";
bl.style.top=(_a55-1)+"px";
b.style.top=(_a55-1)+"px";
br.style.top=(_a55-1)+"px";
tr.style.left=(_a54-1)+"px";
r.style.left=(_a54-1)+"px";
br.style.left=(_a54-1)+"px";
}
}});
dojo.provide("dojo.widget.html.layout");
dojo.widget.html.layout=function(_a57,_a58,_a59){
dojo.html.addClass(_a57,"dojoLayoutContainer");
_a58=dojo.lang.filter(_a58,function(_a5a,idx){
_a5a.idx=idx;
return dojo.lang.inArray(["top","bottom","left","right","client","flood"],_a5a.layoutAlign);
});
if(_a59&&_a59!="none"){
var rank=function(_a5d){
switch(_a5d.layoutAlign){
case "flood":
return 1;
case "left":
case "right":
return (_a59=="left-right")?2:3;
case "top":
case "bottom":
return (_a59=="left-right")?3:2;
default:
return 4;
}
};
_a58.sort(function(a,b){
return (rank(a)-rank(b))||(a.idx-b.idx);
});
}
var f={top:dojo.html.getPixelValue(_a57,"padding-top",true),left:dojo.html.getPixelValue(_a57,"padding-left",true)};
dojo.lang.mixin(f,dojo.html.getContentBox(_a57));
dojo.lang.forEach(_a58,function(_a61){
var elm=_a61.domNode;
var pos=_a61.layoutAlign;
with(elm.style){
left=f.left+"px";
top=f.top+"px";
bottom="auto";
right="auto";
}
dojo.html.addClass(elm,"dojoAlign"+dojo.string.capitalize(pos));
if((pos=="top")||(pos=="bottom")){
dojo.html.setMarginBox(elm,{width:f.width});
var h=dojo.html.getMarginBox(elm).height;
f.height-=h;
if(pos=="top"){
f.top+=h;
}else{
elm.style.top=f.top+f.height+"px";
}
if(_a61.onResized){
_a61.onResized();
}
}else{
if(pos=="left"||pos=="right"){
var w=dojo.html.getMarginBox(elm).width;
if(_a61.resizeTo){
_a61.resizeTo(w,f.height);
}else{
dojo.html.setMarginBox(elm,{width:w,height:f.height});
}
f.width-=w;
if(pos=="left"){
f.left+=w;
}else{
elm.style.left=f.left+f.width+"px";
}
}else{
if(pos=="flood"||pos=="client"){
if(_a61.resizeTo){
_a61.resizeTo(f.width,f.height);
}else{
dojo.html.setMarginBox(elm,{width:f.width,height:f.height});
}
}
}
}
});
};
dojo.html.insertCssText(".dojoLayoutContainer{ position: relative; display: block; overflow: hidden; }\n"+"body .dojoAlignTop, body .dojoAlignBottom, body .dojoAlignLeft, body .dojoAlignRight { position: absolute; overflow: hidden; }\n"+"body .dojoAlignClient { position: absolute }\n"+".dojoAlignClient { overflow: auto; }\n");
dojo.provide("dojo.dnd.DragAndDrop");
dojo.declare("dojo.dnd.DragSource",null,{type:"",onDragEnd:function(evt){
},onDragStart:function(evt){
},onSelected:function(evt){
},unregister:function(){
dojo.dnd.dragManager.unregisterDragSource(this);
},reregister:function(){
dojo.dnd.dragManager.registerDragSource(this);
}});
dojo.declare("dojo.dnd.DragObject",null,{type:"",register:function(){
var dm=dojo.dnd.dragManager;
if(dm["registerDragObject"]){
dm.registerDragObject(this);
}
},onDragStart:function(evt){
},onDragMove:function(evt){
},onDragOver:function(evt){
},onDragOut:function(evt){
},onDragEnd:function(evt){
},onDragLeave:dojo.lang.forward("onDragOut"),onDragEnter:dojo.lang.forward("onDragOver"),ondragout:dojo.lang.forward("onDragOut"),ondragover:dojo.lang.forward("onDragOver")});
dojo.declare("dojo.dnd.DropTarget",null,{acceptsType:function(type){
if(!dojo.lang.inArray(this.acceptedTypes,"*")){
if(!dojo.lang.inArray(this.acceptedTypes,type)){
return false;
}
}
return true;
},accepts:function(_a70){
if(!dojo.lang.inArray(this.acceptedTypes,"*")){
for(var i=0;i<_a70.length;i++){
if(!dojo.lang.inArray(this.acceptedTypes,_a70[i].type)){
return false;
}
}
}
return true;
},unregister:function(){
dojo.dnd.dragManager.unregisterDropTarget(this);
},onDragOver:function(evt){
},onDragOut:function(evt){
},onDragMove:function(evt){
},onDropStart:function(evt){
},onDrop:function(evt){
},onDropEnd:function(){
}},function(){
this.acceptedTypes=[];
});
dojo.dnd.DragEvent=function(){
this.dragSource=null;
this.dragObject=null;
this.target=null;
this.eventStatus="success";
};
dojo.declare("dojo.dnd.DragManager",null,{selectedSources:[],dragObjects:[],dragSources:[],registerDragSource:function(_a77){
},dropTargets:[],registerDropTarget:function(_a78){
},lastDragTarget:null,currentDragTarget:null,onKeyDown:function(){
},onMouseOut:function(){
},onMouseMove:function(){
},onMouseUp:function(){
}});
dojo.provide("dojo.dnd.HtmlDragManager");
dojo.declare("dojo.dnd.HtmlDragManager",dojo.dnd.DragManager,{disabled:false,nestedTargets:false,mouseDownTimer:null,dsCounter:0,dsPrefix:"dojoDragSource",dropTargetDimensions:[],currentDropTarget:null,previousDropTarget:null,_dragTriggered:false,selectedSources:[],dragObjects:[],dragSources:[],dropTargets:[],currentX:null,currentY:null,lastX:null,lastY:null,mouseDownX:null,mouseDownY:null,threshold:7,dropAcceptable:false,cancelEvent:function(e){
e.stopPropagation();
e.preventDefault();
},registerDragSource:function(ds){
if(ds["domNode"]){
var dp=this.dsPrefix;
var _a7c=dp+"Idx_"+(this.dsCounter++);
ds.dragSourceId=_a7c;
this.dragSources[_a7c]=ds;
ds.domNode.setAttribute(dp,_a7c);
if(dojo.render.html.ie){
dojo.event.browser.addListener(ds.domNode,"ondragstart",this.cancelEvent);
}
}
},unregisterDragSource:function(ds){
if(ds["domNode"]){
var dp=this.dsPrefix;
var _a7f=ds.dragSourceId;
delete ds.dragSourceId;
delete this.dragSources[_a7f];
ds.domNode.setAttribute(dp,null);
if(dojo.render.html.ie){
dojo.event.browser.removeListener(ds.domNode,"ondragstart",this.cancelEvent);
}
}
},registerDropTarget:function(dt){
this.dropTargets.push(dt);
},unregisterDropTarget:function(dt){
var _a82=dojo.lang.find(this.dropTargets,dt,true);
if(_a82>=0){
this.dropTargets.splice(_a82,1);
}
},getDragSource:function(e){
var tn=e.target;
if(tn===dojo.body()){
return;
}
var ta=dojo.html.getAttribute(tn,this.dsPrefix);
while((!ta)&&(tn)){
tn=tn.parentNode;
if((!tn)||(tn===dojo.body())){
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
if(dojo.render.html.ie){
if(e.button!=1){
return;
}
}else{
if(e.which!=1){
return;
}
}
var _a88=e.target.nodeType==dojo.html.TEXT_NODE?e.target.parentNode:e.target;
if(dojo.html.isTag(_a88,"button","textarea","input","select","option")){
return;
}
var ds=this.getDragSource(e);
if(!ds){
return;
}
if(!dojo.lang.inArray(this.selectedSources,ds)){
this.selectedSources.push(ds);
ds.onSelected();
}
this.mouseDownX=e.pageX;
this.mouseDownY=e.pageY;
e.preventDefault();
dojo.event.connect(document,"onmousemove",this,"onMouseMove");
},onMouseUp:function(e,_a8b){
if(this.selectedSources.length==0){
return;
}
this.mouseDownX=null;
this.mouseDownY=null;
this._dragTriggered=false;
e.dragSource=this.dragSource;
if((!e.shiftKey)&&(!e.ctrlKey)){
if(this.currentDropTarget){
this.currentDropTarget.onDropStart();
}
dojo.lang.forEach(this.dragObjects,function(_a8c){
var ret=null;
if(!_a8c){
return;
}
if(this.currentDropTarget){
e.dragObject=_a8c;
var ce=this.currentDropTarget.domNode.childNodes;
if(ce.length>0){
e.dropTarget=ce[0];
while(e.dropTarget==_a8c.domNode){
e.dropTarget=e.dropTarget.nextSibling;
}
}else{
e.dropTarget=this.currentDropTarget.domNode;
}
if(this.dropAcceptable){
ret=this.currentDropTarget.onDrop(e);
}else{
this.currentDropTarget.onDragOut(e);
}
}
e.dragStatus=this.dropAcceptable&&ret?"dropSuccess":"dropFailure";
dojo.lang.delayThese([function(){
try{
_a8c.dragSource.onDragEnd(e);
}
catch(err){
var _a8f={};
for(var i in e){
if(i=="type"){
_a8f.type="mouseup";
continue;
}
_a8f[i]=e[i];
}
_a8c.dragSource.onDragEnd(_a8f);
}
},function(){
_a8c.onDragEnd(e);
}]);
},this);
this.selectedSources=[];
this.dragObjects=[];
this.dragSource=null;
if(this.currentDropTarget){
this.currentDropTarget.onDropEnd();
}
}else{
}
dojo.event.disconnect(document,"onmousemove",this,"onMouseMove");
this.currentDropTarget=null;
},onScroll:function(){
for(var i=0;i<this.dragObjects.length;i++){
if(this.dragObjects[i].updateDragOffset){
this.dragObjects[i].updateDragOffset();
}
}
if(this.dragObjects.length){
this.cacheTargetLocations();
}
},_dragStartDistance:function(x,y){
if((!this.mouseDownX)||(!this.mouseDownX)){
return;
}
var dx=Math.abs(x-this.mouseDownX);
var dx2=dx*dx;
var dy=Math.abs(y-this.mouseDownY);
var dy2=dy*dy;
return parseInt(Math.sqrt(dx2+dy2),10);
},cacheTargetLocations:function(){
dojo.profile.start("cacheTargetLocations");
this.dropTargetDimensions=[];
dojo.lang.forEach(this.dropTargets,function(_a98){
var tn=_a98.domNode;
if(!tn||!_a98.accepts([this.dragSource])){
return;
}
var abs=dojo.html.getAbsolutePosition(tn,true);
var bb=dojo.html.getBorderBox(tn);
this.dropTargetDimensions.push([[abs.x,abs.y],[abs.x+bb.width,abs.y+bb.height],_a98]);
},this);
dojo.profile.end("cacheTargetLocations");
},onMouseMove:function(e){
if((dojo.render.html.ie)&&(e.button!=1)){
this.currentDropTarget=null;
this.onMouseUp(e,true);
return;
}
if((this.selectedSources.length)&&(!this.dragObjects.length)){
var dx;
var dy;
if(!this._dragTriggered){
this._dragTriggered=(this._dragStartDistance(e.pageX,e.pageY)>this.threshold);
if(!this._dragTriggered){
return;
}
dx=e.pageX-this.mouseDownX;
dy=e.pageY-this.mouseDownY;
}
this.dragSource=this.selectedSources[0];
dojo.lang.forEach(this.selectedSources,function(_a9f){
if(!_a9f){
return;
}
var tdo=_a9f.onDragStart(e);
if(tdo){
tdo.onDragStart(e);
tdo.dragOffset.y+=dy;
tdo.dragOffset.x+=dx;
tdo.dragSource=_a9f;
this.dragObjects.push(tdo);
}
},this);
this.previousDropTarget=null;
this.cacheTargetLocations();
}
dojo.lang.forEach(this.dragObjects,function(_aa1){
if(_aa1){
_aa1.onDragMove(e);
}
});
if(this.currentDropTarget){
var c=dojo.html.toCoordinateObject(this.currentDropTarget.domNode,true);
var dtp=[[c.x,c.y],[c.x+c.width,c.y+c.height]];
}
if((!this.nestedTargets)&&(dtp)&&(this.isInsideBox(e,dtp))){
if(this.dropAcceptable){
this.currentDropTarget.onDragMove(e,this.dragObjects);
}
}else{
var _aa4=this.findBestTarget(e);
if(_aa4.target===null){
if(this.currentDropTarget){
this.currentDropTarget.onDragOut(e);
this.previousDropTarget=this.currentDropTarget;
this.currentDropTarget=null;
}
this.dropAcceptable=false;
return;
}
if(this.currentDropTarget!==_aa4.target){
if(this.currentDropTarget){
this.previousDropTarget=this.currentDropTarget;
this.currentDropTarget.onDragOut(e);
}
this.currentDropTarget=_aa4.target;
e.dragObjects=this.dragObjects;
this.dropAcceptable=this.currentDropTarget.onDragOver(e);
}else{
if(this.dropAcceptable){
this.currentDropTarget.onDragMove(e,this.dragObjects);
}
}
}
},findBestTarget:function(e){
var _aa6=this;
var _aa7=new Object();
_aa7.target=null;
_aa7.points=null;
dojo.lang.every(this.dropTargetDimensions,function(_aa8){
if(!_aa6.isInsideBox(e,_aa8)){
return true;
}
_aa7.target=_aa8[2];
_aa7.points=_aa8;
return Boolean(_aa6.nestedTargets);
});
return _aa7;
},isInsideBox:function(e,_aaa){
if((e.pageX>_aaa[0][0])&&(e.pageX<_aaa[1][0])&&(e.pageY>_aaa[0][1])&&(e.pageY<_aaa[1][1])){
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
dojo.event.connect(window,"onscroll",dm,"onScroll");
})();
dojo.provide("dojo.dnd.HtmlDragAndDrop");
dojo.declare("dojo.dnd.HtmlDragSource",dojo.dnd.DragSource,{dragClass:"",onDragStart:function(){
var _aaf=new dojo.dnd.HtmlDragObject(this.dragObject,this.type);
if(this.dragClass){
_aaf.dragClass=this.dragClass;
}
if(this.constrainToContainer){
_aaf.constrainTo(this.constrainingContainer||this.domNode.parentNode);
}
return _aaf;
},setDragHandle:function(node){
node=dojo.byId(node);
dojo.dnd.dragManager.unregisterDragSource(this);
this.domNode=node;
dojo.dnd.dragManager.registerDragSource(this);
},setDragTarget:function(node){
this.dragObject=node;
},constrainTo:function(_ab2){
this.constrainToContainer=true;
if(_ab2){
this.constrainingContainer=_ab2;
}
},onSelected:function(){
for(var i=0;i<this.dragObjects.length;i++){
dojo.dnd.dragManager.selectedSources.push(new dojo.dnd.HtmlDragSource(this.dragObjects[i]));
}
},addDragObjects:function(el){
for(var i=0;i<arguments.length;i++){
this.dragObjects.push(dojo.byId(arguments[i]));
}
}},function(node,type){
node=dojo.byId(node);
this.dragObjects=[];
this.constrainToContainer=false;
if(node){
this.domNode=node;
this.dragObject=node;
this.type=(type)||(this.domNode.nodeName.toLowerCase());
dojo.dnd.DragSource.prototype.reregister.call(this);
}
});
dojo.declare("dojo.dnd.HtmlDragObject",dojo.dnd.DragObject,{dragClass:"",opacity:0.5,createIframe:true,disableX:false,disableY:false,createDragNode:function(){
var node=this.domNode.cloneNode(true);
if(this.dragClass){
dojo.html.addClass(node,this.dragClass);
}
if(this.opacity<1){
dojo.html.setOpacity(node,this.opacity);
}
var ltn=node.tagName.toLowerCase();
var isTr=(ltn=="tr");
if((isTr)||(ltn=="tbody")){
var doc=this.domNode.ownerDocument;
var _abc=doc.createElement("table");
if(isTr){
var _abd=doc.createElement("tbody");
_abc.appendChild(_abd);
_abd.appendChild(node);
}else{
_abc.appendChild(node);
}
var _abe=((isTr)?this.domNode:this.domNode.firstChild);
var _abf=((isTr)?node:node.firstChild);
var _ac0=_abe.childNodes;
var _ac1=_abf.childNodes;
for(var i=0;i<_ac0.length;i++){
if((_ac1[i])&&(_ac1[i].style)){
_ac1[i].style.width=dojo.html.getContentBox(_ac0[i]).width+"px";
}
}
node=_abc;
}
if((dojo.render.html.ie55||dojo.render.html.ie60)&&this.createIframe){
with(node.style){
top="0px";
left="0px";
}
var _ac3=document.createElement("div");
_ac3.appendChild(node);
this.bgIframe=new dojo.html.BackgroundIframe(_ac3);
_ac3.appendChild(this.bgIframe.iframe);
node=_ac3;
}
node.style.zIndex=999;
return node;
},onDragStart:function(e){
dojo.html.clearSelection();
this.scrollOffset=dojo.html.getScroll().offset;
this.dragStartPosition=dojo.html.getAbsolutePosition(this.domNode,true);
this.dragOffset={y:this.dragStartPosition.y-e.pageY,x:this.dragStartPosition.x-e.pageX};
this.dragClone=this.createDragNode();
this.containingBlockPosition=this.domNode.offsetParent?dojo.html.getAbsolutePosition(this.domNode.offsetParent,true):{x:0,y:0};
if(this.constrainToContainer){
this.constraints=this.getConstraints();
}
with(this.dragClone.style){
position="absolute";
top=this.dragOffset.y+e.pageY+"px";
left=this.dragOffset.x+e.pageX+"px";
}
dojo.body().appendChild(this.dragClone);
dojo.event.topic.publish("dragStart",{source:this});
},getConstraints:function(){
if(this.constrainingContainer.nodeName.toLowerCase()=="body"){
var _ac5=dojo.html.getViewport();
var _ac6=_ac5.width;
var _ac7=_ac5.height;
var _ac8=dojo.html.getScroll().offset;
var x=_ac8.x;
var y=_ac8.y;
}else{
var _acb=dojo.html.getContentBox(this.constrainingContainer);
_ac6=_acb.width;
_ac7=_acb.height;
x=this.containingBlockPosition.x+dojo.html.getPixelValue(this.constrainingContainer,"padding-left",true)+dojo.html.getBorderExtent(this.constrainingContainer,"left");
y=this.containingBlockPosition.y+dojo.html.getPixelValue(this.constrainingContainer,"padding-top",true)+dojo.html.getBorderExtent(this.constrainingContainer,"top");
}
var mb=dojo.html.getMarginBox(this.domNode);
return {minX:x,minY:y,maxX:x+_ac6-mb.width,maxY:y+_ac7-mb.height};
},updateDragOffset:function(){
var _acd=dojo.html.getScroll().offset;
if(_acd.y!=this.scrollOffset.y){
var diff=_acd.y-this.scrollOffset.y;
this.dragOffset.y+=diff;
this.scrollOffset.y=_acd.y;
}
if(_acd.x!=this.scrollOffset.x){
var diff=_acd.x-this.scrollOffset.x;
this.dragOffset.x+=diff;
this.scrollOffset.x=_acd.x;
}
},onDragMove:function(e){
this.updateDragOffset();
var x=this.dragOffset.x+e.pageX;
var y=this.dragOffset.y+e.pageY;
if(this.constrainToContainer){
if(x<this.constraints.minX){
x=this.constraints.minX;
}
if(y<this.constraints.minY){
y=this.constraints.minY;
}
if(x>this.constraints.maxX){
x=this.constraints.maxX;
}
if(y>this.constraints.maxY){
y=this.constraints.maxY;
}
}
this.setAbsolutePosition(x,y);
dojo.event.topic.publish("dragMove",{source:this});
},setAbsolutePosition:function(x,y){
if(!this.disableY){
this.dragClone.style.top=y+"px";
}
if(!this.disableX){
this.dragClone.style.left=x+"px";
}
},onDragEnd:function(e){
switch(e.dragStatus){
case "dropSuccess":
dojo.html.removeNode(this.dragClone);
this.dragClone=null;
break;
case "dropFailure":
var _ad5=dojo.html.getAbsolutePosition(this.dragClone,true);
var _ad6={left:this.dragStartPosition.x+1,top:this.dragStartPosition.y+1};
var anim=dojo.lfx.slideTo(this.dragClone,_ad6,300);
var _ad8=this;
dojo.event.connect(anim,"onEnd",function(e){
dojo.html.removeNode(_ad8.dragClone);
_ad8.dragClone=null;
});
anim.play();
break;
}
dojo.event.topic.publish("dragEnd",{source:this});
},constrainTo:function(_ada){
this.constrainToContainer=true;
if(_ada){
this.constrainingContainer=_ada;
}else{
this.constrainingContainer=this.domNode.parentNode;
}
}},function(node,type){
this.domNode=dojo.byId(node);
this.type=type;
this.constrainToContainer=false;
this.dragSource=null;
dojo.dnd.DragObject.prototype.register.call(this);
});
dojo.declare("dojo.dnd.HtmlDropTarget",dojo.dnd.DropTarget,{vertical:false,onDragOver:function(e){
if(!this.accepts(e.dragObjects)){
return false;
}
this.childBoxes=[];
for(var i=0,_adf;i<this.domNode.childNodes.length;i++){
_adf=this.domNode.childNodes[i];
if(_adf.nodeType!=dojo.html.ELEMENT_NODE){
continue;
}
var pos=dojo.html.getAbsolutePosition(_adf,true);
var _ae1=dojo.html.getBorderBox(_adf);
this.childBoxes.push({top:pos.y,bottom:pos.y+_ae1.height,left:pos.x,right:pos.x+_ae1.width,height:_ae1.height,width:_ae1.width,node:_adf});
}
return true;
},_getNodeUnderMouse:function(e){
for(var i=0,_ae4;i<this.childBoxes.length;i++){
with(this.childBoxes[i]){
if(e.pageX>=left&&e.pageX<=right&&e.pageY>=top&&e.pageY<=bottom){
return i;
}
}
}
return -1;
},createDropIndicator:function(){
this.dropIndicator=document.createElement("div");
with(this.dropIndicator.style){
position="absolute";
zIndex=999;
if(this.vertical){
borderLeftWidth="1px";
borderLeftColor="black";
borderLeftStyle="solid";
height=dojo.html.getBorderBox(this.domNode).height+"px";
top=dojo.html.getAbsolutePosition(this.domNode,true).y+"px";
}else{
borderTopWidth="1px";
borderTopColor="black";
borderTopStyle="solid";
width=dojo.html.getBorderBox(this.domNode).width+"px";
left=dojo.html.getAbsolutePosition(this.domNode,true).x+"px";
}
}
},onDragMove:function(e,_ae6){
var i=this._getNodeUnderMouse(e);
if(!this.dropIndicator){
this.createDropIndicator();
}
var _ae8=this.vertical?dojo.html.gravity.WEST:dojo.html.gravity.NORTH;
var hide=false;
if(i<0){
if(this.childBoxes.length){
var _aea=(dojo.html.gravity(this.childBoxes[0].node,e)&_ae8);
if(_aea){
hide=true;
}
}else{
var _aea=true;
}
}else{
var _aeb=this.childBoxes[i];
var _aea=(dojo.html.gravity(_aeb.node,e)&_ae8);
if(_aeb.node===_ae6[0].dragSource.domNode){
hide=true;
}else{
var _aec=_aea?(i>0?this.childBoxes[i-1]:_aeb):(i<this.childBoxes.length-1?this.childBoxes[i+1]:_aeb);
if(_aec.node===_ae6[0].dragSource.domNode){
hide=true;
}
}
}
if(hide){
this.dropIndicator.style.display="none";
return;
}else{
this.dropIndicator.style.display="";
}
this.placeIndicator(e,_ae6,i,_aea);
if(!dojo.html.hasParent(this.dropIndicator)){
dojo.body().appendChild(this.dropIndicator);
}
},placeIndicator:function(e,_aee,_aef,_af0){
var _af1=this.vertical?"left":"top";
var _af2;
if(_aef<0){
if(this.childBoxes.length){
_af2=_af0?this.childBoxes[0]:this.childBoxes[this.childBoxes.length-1];
}else{
this.dropIndicator.style[_af1]=dojo.html.getAbsolutePosition(this.domNode,true)[this.vertical?"x":"y"]+"px";
}
}else{
_af2=this.childBoxes[_aef];
}
if(_af2){
this.dropIndicator.style[_af1]=(_af0?_af2[_af1]:_af2[this.vertical?"right":"bottom"])+"px";
if(this.vertical){
this.dropIndicator.style.height=_af2.height+"px";
this.dropIndicator.style.top=_af2.top+"px";
}else{
this.dropIndicator.style.width=_af2.width+"px";
this.dropIndicator.style.left=_af2.left+"px";
}
}
},onDragOut:function(e){
if(this.dropIndicator){
dojo.html.removeNode(this.dropIndicator);
delete this.dropIndicator;
}
},onDrop:function(e){
this.onDragOut(e);
var i=this._getNodeUnderMouse(e);
var _af6=this.vertical?dojo.html.gravity.WEST:dojo.html.gravity.NORTH;
if(i<0){
if(this.childBoxes.length){
if(dojo.html.gravity(this.childBoxes[0].node,e)&_af6){
return this.insert(e,this.childBoxes[0].node,"before");
}else{
return this.insert(e,this.childBoxes[this.childBoxes.length-1].node,"after");
}
}
return this.insert(e,this.domNode,"append");
}
var _af7=this.childBoxes[i];
if(dojo.html.gravity(_af7.node,e)&_af6){
return this.insert(e,_af7.node,"before");
}else{
return this.insert(e,_af7.node,"after");
}
},insert:function(e,_af9,_afa){
var node=e.dragObject.domNode;
if(_afa=="before"){
return dojo.html.insertBefore(node,_af9);
}else{
if(_afa=="after"){
return dojo.html.insertAfter(node,_af9);
}else{
if(_afa=="append"){
_af9.appendChild(node);
return true;
}
}
}
return false;
}},function(node,_afd){
if(arguments.length==0){
return;
}
this.domNode=dojo.byId(node);
dojo.dnd.DropTarget.call(this);
if(_afd&&dojo.lang.isString(_afd)){
_afd=[_afd];
}
this.acceptedTypes=_afd||[];
dojo.dnd.dragManager.registerDropTarget(this);
});
dojo.kwCompoundRequire({common:["dojo.dnd.DragAndDrop"],browser:["dojo.dnd.HtmlDragAndDrop"],dashboard:["dojo.dnd.HtmlDragAndDrop"]});
dojo.provide("dojo.dnd.*");
dojo.provide("dojo.dnd.HtmlDragMove");
dojo.declare("dojo.dnd.HtmlDragMoveSource",dojo.dnd.HtmlDragSource,{onDragStart:function(){
var _afe=new dojo.dnd.HtmlDragMoveObject(this.dragObject,this.type);
if(this.constrainToContainer){
_afe.constrainTo(this.constrainingContainer);
}
return _afe;
},onSelected:function(){
for(var i=0;i<this.dragObjects.length;i++){
dojo.dnd.dragManager.selectedSources.push(new dojo.dnd.HtmlDragMoveSource(this.dragObjects[i]));
}
}});
dojo.declare("dojo.dnd.HtmlDragMoveObject",dojo.dnd.HtmlDragObject,{onDragStart:function(e){
dojo.html.clearSelection();
this.dragClone=this.domNode;
if(dojo.html.getComputedStyle(this.domNode,"position")!="absolute"){
this.domNode.style.position="relative";
}
var left=parseInt(dojo.html.getComputedStyle(this.domNode,"left"));
var top=parseInt(dojo.html.getComputedStyle(this.domNode,"top"));
this.dragStartPosition={x:isNaN(left)?0:left,y:isNaN(top)?0:top};
this.scrollOffset=dojo.html.getScroll().offset;
this.dragOffset={y:this.dragStartPosition.y-e.pageY,x:this.dragStartPosition.x-e.pageX};
this.containingBlockPosition={x:0,y:0};
if(this.constrainToContainer){
this.constraints=this.getConstraints();
}
dojo.event.connect(this.domNode,"onclick",this,"_squelchOnClick");
},onDragEnd:function(e){
},setAbsolutePosition:function(x,y){
if(!this.disableY){
this.domNode.style.top=y+"px";
}
if(!this.disableX){
this.domNode.style.left=x+"px";
}
},_squelchOnClick:function(e){
dojo.event.browser.stopEvent(e);
dojo.event.disconnect(this.domNode,"onclick",this,"_squelchOnClick");
}});
dojo.provide("dojo.widget.Dialog");
dojo.declare("dojo.widget.ModalDialogBase",null,{isContainer:true,focusElement:"",bgColor:"black",bgOpacity:0.4,followScroll:true,closeOnBackgroundClick:false,trapTabs:function(e){
if(e.target==this.tabStartOuter){
if(this._fromTrap){
this.tabStart.focus();
this._fromTrap=false;
}else{
this._fromTrap=true;
this.tabEnd.focus();
}
}else{
if(e.target==this.tabStart){
if(this._fromTrap){
this._fromTrap=false;
}else{
this._fromTrap=true;
this.tabEnd.focus();
}
}else{
if(e.target==this.tabEndOuter){
if(this._fromTrap){
this.tabEnd.focus();
this._fromTrap=false;
}else{
this._fromTrap=true;
this.tabStart.focus();
}
}else{
if(e.target==this.tabEnd){
if(this._fromTrap){
this._fromTrap=false;
}else{
this._fromTrap=true;
this.tabStart.focus();
}
}
}
}
}
},clearTrap:function(e){
var _b09=this;
setTimeout(function(){
_b09._fromTrap=false;
},100);
},postCreate:function(){
with(this.domNode.style){
position="absolute";
zIndex=999;
display="none";
overflow="visible";
}
var b=dojo.body();
b.appendChild(this.domNode);
this.bg=document.createElement("div");
this.bg.className="dialogUnderlay";
with(this.bg.style){
position="absolute";
left=top="0px";
zIndex=998;
display="none";
}
b.appendChild(this.bg);
this.setBackgroundColor(this.bgColor);
this.bgIframe=new dojo.html.BackgroundIframe();
if(this.bgIframe.iframe){
with(this.bgIframe.iframe.style){
position="absolute";
left=top="0px";
zIndex=90;
display="none";
}
}
if(this.closeOnBackgroundClick){
dojo.event.kwConnect({srcObj:this.bg,srcFunc:"onclick",adviceObj:this,adviceFunc:"onBackgroundClick",once:true});
}
},uninitialize:function(){
this.bgIframe.remove();
dojo.html.removeNode(this.bg,true);
},setBackgroundColor:function(_b0b){
if(arguments.length>=3){
_b0b=new dojo.gfx.color.Color(arguments[0],arguments[1],arguments[2]);
}else{
_b0b=new dojo.gfx.color.Color(_b0b);
}
this.bg.style.backgroundColor=_b0b.toString();
return this.bgColor=_b0b;
},setBackgroundOpacity:function(op){
if(arguments.length==0){
op=this.bgOpacity;
}
dojo.html.setOpacity(this.bg,op);
try{
this.bgOpacity=dojo.html.getOpacity(this.bg);
}
catch(e){
this.bgOpacity=op;
}
return this.bgOpacity;
},_sizeBackground:function(){
if(this.bgOpacity>0){
var _b0d=dojo.html.getViewport();
var h=_b0d.height;
var w=_b0d.width;
with(this.bg.style){
width=w+"px";
height=h+"px";
}
var _b10=dojo.html.getScroll().offset;
this.bg.style.top=_b10.y+"px";
this.bg.style.left=_b10.x+"px";
var _b0d=dojo.html.getViewport();
if(_b0d.width!=w){
this.bg.style.width=_b0d.width+"px";
}
if(_b0d.height!=h){
this.bg.style.height=_b0d.height+"px";
}
}
this.bgIframe.size(this.bg);
},_showBackground:function(){
if(this.bgOpacity>0){
this.bg.style.display="block";
}
if(this.bgIframe.iframe){
this.bgIframe.iframe.style.display="block";
}
},placeModalDialog:function(){
var _b11=dojo.html.getScroll().offset;
var _b12=dojo.html.getViewport();
var mb;
if(this.isShowing()){
mb=dojo.html.getMarginBox(this.domNode);
}else{
dojo.html.setVisibility(this.domNode,false);
dojo.html.show(this.domNode);
mb=dojo.html.getMarginBox(this.domNode);
dojo.html.hide(this.domNode);
dojo.html.setVisibility(this.domNode,true);
}
var x=_b11.x+(_b12.width-mb.width)/2;
var y=_b11.y+(_b12.height-mb.height)/2;
with(this.domNode.style){
left=x+"px";
top=y+"px";
}
},_onKey:function(evt){
if(evt.key){
var node=evt.target;
while(node!=null){
if(node==this.domNode){
return;
}
node=node.parentNode;
}
if(evt.key!=evt.KEY_TAB){
dojo.event.browser.stopEvent(evt);
}else{
if(!dojo.render.html.opera){
try{
this.tabStart.focus();
}
catch(e){
}
}
}
}
},showModalDialog:function(){
if(this.followScroll&&!this._scrollConnected){
this._scrollConnected=true;
dojo.event.connect(window,"onscroll",this,"_onScroll");
}
dojo.event.connect(document.documentElement,"onkey",this,"_onKey");
this.placeModalDialog();
this.setBackgroundOpacity();
this._sizeBackground();
this._showBackground();
this._fromTrap=true;
setTimeout(dojo.lang.hitch(this,function(){
try{
this.tabStart.focus();
}
catch(e){
}
}),50);
},hideModalDialog:function(){
if(this.focusElement){
dojo.byId(this.focusElement).focus();
dojo.byId(this.focusElement).blur();
}
this.bg.style.display="none";
this.bg.style.width=this.bg.style.height="1px";
if(this.bgIframe.iframe){
this.bgIframe.iframe.style.display="none";
}
dojo.event.disconnect(document.documentElement,"onkey",this,"_onKey");
if(this._scrollConnected){
this._scrollConnected=false;
dojo.event.disconnect(window,"onscroll",this,"_onScroll");
}
},_onScroll:function(){
var _b18=dojo.html.getScroll().offset;
this.bg.style.top=_b18.y+"px";
this.bg.style.left=_b18.x+"px";
this.placeModalDialog();
},checkSize:function(){
if(this.isShowing()){
this._sizeBackground();
this.placeModalDialog();
this.onResized();
}
},onBackgroundClick:function(){
if(this.lifetime-this.timeRemaining>=this.blockDuration){
return;
}
this.hide();
}});
dojo.widget.defineWidget("dojo.widget.Dialog",[dojo.widget.ContentPane,dojo.widget.ModalDialogBase],{templateString:"<div id=\"${this.widgetId}\" class=\"dojoDialog\" dojoattachpoint=\"wrapper\">\r\n\t<span dojoattachpoint=\"tabStartOuter\" dojoonfocus=\"trapTabs\" dojoonblur=\"clearTrap\"\ttabindex=\"0\"></span>\r\n\t<span dojoattachpoint=\"tabStart\" dojoonfocus=\"trapTabs\" dojoonblur=\"clearTrap\" tabindex=\"0\"></span>\r\n\t<div dojoattachpoint=\"containerNode\" style=\"position: relative; z-index: 2;\"></div>\r\n\t<span dojoattachpoint=\"tabEnd\" dojoonfocus=\"trapTabs\" dojoonblur=\"clearTrap\" tabindex=\"0\"></span>\r\n\t<span dojoattachpoint=\"tabEndOuter\" dojoonfocus=\"trapTabs\" dojoonblur=\"clearTrap\" tabindex=\"0\"></span>\r\n</div>\r\n",blockDuration:0,lifetime:0,closeNode:"",postMixInProperties:function(){
dojo.widget.Dialog.superclass.postMixInProperties.apply(this,arguments);
if(this.closeNode){
this.setCloseControl(this.closeNode);
}
},postCreate:function(){
dojo.widget.Dialog.superclass.postCreate.apply(this,arguments);
dojo.widget.ModalDialogBase.prototype.postCreate.apply(this,arguments);
},show:function(){
if(this.lifetime){
this.timeRemaining=this.lifetime;
if(this.timerNode){
this.timerNode.innerHTML=Math.ceil(this.timeRemaining/1000);
}
if(this.blockDuration&&this.closeNode){
if(this.lifetime>this.blockDuration){
this.closeNode.style.visibility="hidden";
}else{
this.closeNode.style.display="none";
}
}
if(this.timer){
clearInterval(this.timer);
}
this.timer=setInterval(dojo.lang.hitch(this,"_onTick"),100);
}
this.showModalDialog();
dojo.widget.Dialog.superclass.show.call(this);
},onLoad:function(){
this.placeModalDialog();
dojo.widget.Dialog.superclass.onLoad.call(this);
},fillInTemplate:function(){
},hide:function(){
this.hideModalDialog();
dojo.widget.Dialog.superclass.hide.call(this);
if(this.timer){
clearInterval(this.timer);
}
},setTimerNode:function(node){
this.timerNode=node;
},setCloseControl:function(node){
this.closeNode=dojo.byId(node);
dojo.event.connect(this.closeNode,"onclick",this,"hide");
},setShowControl:function(node){
node=dojo.byId(node);
dojo.event.connect(node,"onclick",this,"show");
},_onTick:function(){
if(this.timer){
this.timeRemaining-=100;
if(this.lifetime-this.timeRemaining>=this.blockDuration){
if(this.closeNode){
this.closeNode.style.visibility="visible";
}
}
if(!this.timeRemaining){
clearInterval(this.timer);
this.hide();
}else{
if(this.timerNode){
this.timerNode.innerHTML=Math.ceil(this.timeRemaining/1000);
}
}
}
}});
dojo.provide("dojo.widget.ResizeHandle");
dojo.widget.defineWidget("dojo.widget.ResizeHandle",dojo.widget.HtmlWidget,{targetElmId:"",templateCssString:".dojoHtmlResizeHandle {\r\n\tfloat: right;\r\n\tposition: absolute;\r\n\tright: 2px;\r\n\tbottom: 2px;\r\n\twidth: 13px;\r\n\theight: 13px;\r\n\tz-index: 20;\r\n\tcursor: nw-resize;\r\n\tbackground-image: url(grabCorner.gif);\r\n\tline-height: 0px;\r\n}\r\n",templateCssPath:dojo.uri.moduleUri("dojo.widget","templates/ResizeHandle.css"),templateString:"<div class=\"dojoHtmlResizeHandle\"><div></div></div>",postCreate:function(){
dojo.event.connect(this.domNode,"onmousedown",this,"_beginSizing");
},_beginSizing:function(e){
if(this._isSizing){
return false;
}
this.targetWidget=dojo.widget.byId(this.targetElmId);
this.targetDomNode=this.targetWidget?this.targetWidget.domNode:dojo.byId(this.targetElmId);
if(!this.targetDomNode){
return;
}
this._isSizing=true;
this.startPoint={"x":e.clientX,"y":e.clientY};
var mb=dojo.html.getMarginBox(this.targetDomNode);
this.startSize={"w":mb.width,"h":mb.height};
dojo.event.kwConnect({srcObj:dojo.body(),srcFunc:"onmousemove",targetObj:this,targetFunc:"_changeSizing",rate:25});
dojo.event.connect(dojo.body(),"onmouseup",this,"_endSizing");
e.preventDefault();
},_changeSizing:function(e){
try{
if(!e.clientX||!e.clientY){
return;
}
}
catch(e){
return;
}
var dx=this.startPoint.x-e.clientX;
var dy=this.startPoint.y-e.clientY;
var newW=this.startSize.w-dx;
var newH=this.startSize.h-dy;
if(this.minSize){
var mb=dojo.html.getMarginBox(this.targetDomNode);
if(newW<this.minSize.w){
newW=mb.width;
}
if(newH<this.minSize.h){
newH=mb.height;
}
}
if(this.targetWidget){
this.targetWidget.resizeTo(newW,newH);
}else{
dojo.html.setMarginBox(this.targetDomNode,{width:newW,height:newH});
}
e.preventDefault();
},_endSizing:function(e){
dojo.event.disconnect(dojo.body(),"onmousemove",this,"_changeSizing");
dojo.event.disconnect(dojo.body(),"onmouseup",this,"_endSizing");
this._isSizing=false;
}});
dojo.provide("dojo.widget.FloatingPane");
dojo.declare("dojo.widget.FloatingPaneBase",null,{title:"",iconSrc:"",hasShadow:false,constrainToContainer:false,taskBarId:"",resizable:true,titleBarDisplay:true,windowState:"normal",displayCloseAction:false,displayMinimizeAction:false,displayMaximizeAction:false,_max_taskBarConnectAttempts:5,_taskBarConnectAttempts:0,templateString:"<div id=\"${this.widgetId}\" dojoAttachEvent=\"onMouseDown\" class=\"dojoFloatingPane\">\r\n\t<div dojoAttachPoint=\"titleBar\" class=\"dojoFloatingPaneTitleBar\"  style=\"display:none\">\r\n\t  \t<img dojoAttachPoint=\"titleBarIcon\"  class=\"dojoFloatingPaneTitleBarIcon\">\r\n\t\t<div dojoAttachPoint=\"closeAction\" dojoAttachEvent=\"onClick:closeWindow\"\r\n   \t  \t\tclass=\"dojoFloatingPaneCloseIcon\"></div>\r\n\t\t<div dojoAttachPoint=\"restoreAction\" dojoAttachEvent=\"onClick:restoreWindow\"\r\n   \t  \t\tclass=\"dojoFloatingPaneRestoreIcon\"></div>\r\n\t\t<div dojoAttachPoint=\"maximizeAction\" dojoAttachEvent=\"onClick:maximizeWindow\"\r\n   \t  \t\tclass=\"dojoFloatingPaneMaximizeIcon\"></div>\r\n\t\t<div dojoAttachPoint=\"minimizeAction\" dojoAttachEvent=\"onClick:minimizeWindow\"\r\n   \t  \t\tclass=\"dojoFloatingPaneMinimizeIcon\"></div>\r\n\t  \t<div dojoAttachPoint=\"titleBarText\" class=\"dojoFloatingPaneTitleText\">${this.title}</div>\r\n\t</div>\r\n\r\n\t<div id=\"${this.widgetId}_container\" dojoAttachPoint=\"containerNode\" class=\"dojoFloatingPaneClient\"></div>\r\n\r\n\t<div dojoAttachPoint=\"resizeBar\" class=\"dojoFloatingPaneResizebar\" style=\"display:none\"></div>\r\n</div>\r\n",templateCssString:"\r\n/********** Outer Window ***************/\r\n\r\n.dojoFloatingPane {\r\n\t/* essential css */\r\n\tposition: absolute;\r\n\toverflow: visible;\t\t/* so drop shadow is displayed */\r\n\tz-index: 10;\r\n\r\n\t/* styling css */\r\n\tborder: 1px solid;\r\n\tborder-color: ThreeDHighlight ThreeDShadow ThreeDShadow ThreeDHighlight;\r\n\tbackground-color: ThreeDFace;\r\n}\r\n\r\n\r\n/********** Title Bar ****************/\r\n\r\n.dojoFloatingPaneTitleBar {\r\n\tvertical-align: top;\r\n\tmargin: 2px 2px 2px 2px;\r\n\tz-index: 10;\r\n\tbackground-color: #7596c6;\r\n\tcursor: default;\r\n\toverflow: hidden;\r\n\tborder-color: ThreeDHighlight ThreeDShadow ThreeDShadow ThreeDHighlight;\r\n\tvertical-align: middle;\r\n}\r\n\r\n.dojoFloatingPaneTitleText {\r\n\tfloat: left;\r\n\tpadding: 2px 4px 2px 2px;\r\n\twhite-space: nowrap;\r\n\tcolor: CaptionText;\r\n\tfont: small-caption;\r\n}\r\n\r\n.dojoTitleBarIcon {\r\n\tfloat: left;\r\n\theight: 22px;\r\n\twidth: 22px;\r\n\tvertical-align: middle;\r\n\tmargin-right: 5px;\r\n\tmargin-left: 5px;\r\n}\r\n\r\n.dojoFloatingPaneActions{\r\n\tfloat: right;\r\n\tposition: absolute;\r\n\tright: 2px;\r\n\ttop: 2px;\r\n\tvertical-align: middle;\r\n}\r\n\r\n\r\n.dojoFloatingPaneActionItem {\r\n\tvertical-align: middle;\r\n\tmargin-right: 1px;\r\n\theight: 22px;\r\n\twidth: 22px;\r\n}\r\n\r\n\r\n.dojoFloatingPaneTitleBarIcon {\r\n\t/* essential css */\r\n\tfloat: left;\r\n\r\n\t/* styling css */\r\n\tmargin-left: 2px;\r\n\tmargin-right: 4px;\r\n\theight: 22px;\r\n}\r\n\r\n/* minimize/maximize icons are specified by CSS only */\r\n.dojoFloatingPaneMinimizeIcon,\r\n.dojoFloatingPaneMaximizeIcon,\r\n.dojoFloatingPaneRestoreIcon,\r\n.dojoFloatingPaneCloseIcon {\r\n\tvertical-align: middle;\r\n\theight: 22px;\r\n\twidth: 22px;\r\n\tfloat: right;\r\n}\r\n.dojoFloatingPaneMinimizeIcon {\r\n\tbackground-image: url(images/floatingPaneMinimize.gif);\r\n}\r\n.dojoFloatingPaneMaximizeIcon {\r\n\tbackground-image: url(images/floatingPaneMaximize.gif);\r\n}\r\n.dojoFloatingPaneRestoreIcon {\r\n\tbackground-image: url(images/floatingPaneRestore.gif);\r\n}\r\n.dojoFloatingPaneCloseIcon {\r\n\tbackground-image: url(images/floatingPaneClose.gif);\r\n}\r\n\r\n/* bar at bottom of window that holds resize handle */\r\n.dojoFloatingPaneResizebar {\r\n\tz-index: 10;\r\n\theight: 13px;\r\n\tbackground-color: ThreeDFace;\r\n}\r\n\r\n/************* Client Area ***************/\r\n\r\n.dojoFloatingPaneClient {\r\n\tposition: relative;\r\n\tz-index: 10;\r\n\tborder: 1px solid;\r\n\tborder-color: ThreeDShadow ThreeDHighlight ThreeDHighlight ThreeDShadow;\r\n\tmargin: 2px;\r\n\tbackground-color: ThreeDFace;\r\n\tpadding: 8px;\r\n\tfont-family: Verdana, Helvetica, Garamond, sans-serif;\r\n\tfont-size: 12px;\r\n\toverflow: auto;\r\n}\r\n\r\n",templateCssPath:dojo.uri.moduleUri("dojo.widget","templates/FloatingPane.css"),fillInFloatingPaneTemplate:function(args,frag){
var _b27=this.getFragNodeRef(frag);
dojo.html.copyStyle(this.domNode,_b27);
dojo.body().appendChild(this.domNode);
if(!this.isShowing()){
this.windowState="minimized";
}
if(this.iconSrc==""){
dojo.html.removeNode(this.titleBarIcon);
}else{
this.titleBarIcon.src=this.iconSrc.toString();
}
if(this.titleBarDisplay){
this.titleBar.style.display="";
dojo.html.disableSelection(this.titleBar);
this.titleBarIcon.style.display=(this.iconSrc==""?"none":"");
this.minimizeAction.style.display=(this.displayMinimizeAction?"":"none");
this.maximizeAction.style.display=(this.displayMaximizeAction&&this.windowState!="maximized"?"":"none");
this.restoreAction.style.display=(this.displayMaximizeAction&&this.windowState=="maximized"?"":"none");
this.closeAction.style.display=(this.displayCloseAction?"":"none");
this.drag=new dojo.dnd.HtmlDragMoveSource(this.domNode);
if(this.constrainToContainer){
this.drag.constrainTo();
}
this.drag.setDragHandle(this.titleBar);
var self=this;
dojo.event.topic.subscribe("dragMove",function(info){
if(info.source.domNode==self.domNode){
dojo.event.topic.publish("floatingPaneMove",{source:self});
}
});
}
if(this.resizable){
this.resizeBar.style.display="";
this.resizeHandle=dojo.widget.createWidget("ResizeHandle",{targetElmId:this.widgetId,id:this.widgetId+"_resize"});
this.resizeBar.appendChild(this.resizeHandle.domNode);
}
if(this.hasShadow){
this.shadow=new dojo.lfx.shadow(this.domNode);
}
this.bgIframe=new dojo.html.BackgroundIframe(this.domNode);
if(this.taskBarId){
this._taskBarSetup();
}
dojo.body().removeChild(this.domNode);
},postCreate:function(){
if(dojo.hostenv.post_load_){
this._setInitialWindowState();
}else{
dojo.addOnLoad(this,"_setInitialWindowState");
}
},maximizeWindow:function(evt){
var mb=dojo.html.getMarginBox(this.domNode);
this.previous={width:mb.width||this.width,height:mb.height||this.height,left:this.domNode.style.left,top:this.domNode.style.top,bottom:this.domNode.style.bottom,right:this.domNode.style.right};
if(this.domNode.parentNode.style.overflow.toLowerCase()!="hidden"){
this.parentPrevious={overflow:this.domNode.parentNode.style.overflow};
dojo.debug(this.domNode.parentNode.style.overflow);
this.domNode.parentNode.style.overflow="hidden";
}
this.domNode.style.left=dojo.html.getPixelValue(this.domNode.parentNode,"padding-left",true)+"px";
this.domNode.style.top=dojo.html.getPixelValue(this.domNode.parentNode,"padding-top",true)+"px";
if((this.domNode.parentNode.nodeName.toLowerCase()=="body")){
var _b2c=dojo.html.getViewport();
var _b2d=dojo.html.getPadding(dojo.body());
this.resizeTo(_b2c.width-_b2d.width,_b2c.height-_b2d.height);
}else{
var _b2e=dojo.html.getContentBox(this.domNode.parentNode);
this.resizeTo(_b2e.width,_b2e.height);
}
this.maximizeAction.style.display="none";
this.restoreAction.style.display="";
if(this.resizeHandle){
this.resizeHandle.domNode.style.display="none";
}
this.drag.setDragHandle(null);
this.windowState="maximized";
},minimizeWindow:function(evt){
this.hide();
for(var attr in this.parentPrevious){
this.domNode.parentNode.style[attr]=this.parentPrevious[attr];
}
this.lastWindowState=this.windowState;
this.windowState="minimized";
},restoreWindow:function(evt){
if(this.windowState=="minimized"){
this.show();
if(this.lastWindowState=="maximized"){
this.domNode.parentNode.style.overflow="hidden";
this.windowState="maximized";
}else{
this.windowState="normal";
}
}else{
if(this.windowState=="maximized"){
for(var attr in this.previous){
this.domNode.style[attr]=this.previous[attr];
}
for(var attr in this.parentPrevious){
this.domNode.parentNode.style[attr]=this.parentPrevious[attr];
}
this.resizeTo(this.previous.width,this.previous.height);
this.previous=null;
this.parentPrevious=null;
this.restoreAction.style.display="none";
this.maximizeAction.style.display=this.displayMaximizeAction?"":"none";
if(this.resizeHandle){
this.resizeHandle.domNode.style.display="";
}
this.drag.setDragHandle(this.titleBar);
this.windowState="normal";
}else{
}
}
},toggleDisplay:function(){
if(this.windowState=="minimized"){
this.restoreWindow();
}else{
this.minimizeWindow();
}
},closeWindow:function(evt){
dojo.html.removeNode(this.domNode);
this.destroy();
},onMouseDown:function(evt){
this.bringToTop();
},bringToTop:function(){
var _b35=dojo.widget.manager.getWidgetsByType(this.widgetType);
var _b36=[];
for(var x=0;x<_b35.length;x++){
if(this.widgetId!=_b35[x].widgetId){
_b36.push(_b35[x]);
}
}
_b36.sort(function(a,b){
return a.domNode.style.zIndex-b.domNode.style.zIndex;
});
_b36.push(this);
var _b3a=100;
for(x=0;x<_b36.length;x++){
_b36[x].domNode.style.zIndex=_b3a+x*2;
}
},_setInitialWindowState:function(){
if(this.isShowing()){
this.width=-1;
var mb=dojo.html.getMarginBox(this.domNode);
this.resizeTo(mb.width,mb.height);
}
if(this.windowState=="maximized"){
this.maximizeWindow();
this.show();
return;
}
if(this.windowState=="normal"){
this.show();
return;
}
if(this.windowState=="minimized"){
this.hide();
return;
}
this.windowState="minimized";
},_taskBarSetup:function(){
var _b3c=dojo.widget.getWidgetById(this.taskBarId);
if(!_b3c){
if(this._taskBarConnectAttempts<this._max_taskBarConnectAttempts){
dojo.lang.setTimeout(this,this._taskBarSetup,50);
this._taskBarConnectAttempts++;
}else{
dojo.debug("Unable to connect to the taskBar");
}
return;
}
_b3c.addChild(this);
},showFloatingPane:function(){
this.bringToTop();
},onFloatingPaneShow:function(){
var mb=dojo.html.getMarginBox(this.domNode);
this.resizeTo(mb.width,mb.height);
},resizeTo:function(_b3e,_b3f){
dojo.html.setMarginBox(this.domNode,{width:_b3e,height:_b3f});
dojo.widget.html.layout(this.domNode,[{domNode:this.titleBar,layoutAlign:"top"},{domNode:this.resizeBar,layoutAlign:"bottom"},{domNode:this.containerNode,layoutAlign:"client"}]);
dojo.widget.html.layout(this.containerNode,this.children,"top-bottom");
this.bgIframe.onResized();
if(this.shadow){
this.shadow.size(_b3e,_b3f);
}
this.onResized();
},checkSize:function(){
},destroyFloatingPane:function(){
if(this.resizeHandle){
this.resizeHandle.destroy();
this.resizeHandle=null;
}
}});
dojo.widget.defineWidget("dojo.widget.FloatingPane",[dojo.widget.ContentPane,dojo.widget.FloatingPaneBase],{fillInTemplate:function(args,frag){
this.fillInFloatingPaneTemplate(args,frag);
dojo.widget.FloatingPane.superclass.fillInTemplate.call(this,args,frag);
},postCreate:function(){
dojo.widget.FloatingPaneBase.prototype.postCreate.apply(this,arguments);
dojo.widget.FloatingPane.superclass.postCreate.apply(this,arguments);
},show:function(){
dojo.widget.FloatingPane.superclass.show.apply(this,arguments);
this.showFloatingPane();
},onShow:function(){
dojo.widget.FloatingPane.superclass.onShow.call(this);
this.onFloatingPaneShow();
},destroy:function(){
this.destroyFloatingPane();
dojo.widget.FloatingPane.superclass.destroy.apply(this,arguments);
}});
dojo.widget.defineWidget("dojo.widget.ModalFloatingPane",[dojo.widget.FloatingPane,dojo.widget.ModalDialogBase],{windowState:"minimized",displayCloseAction:true,postCreate:function(){
dojo.widget.ModalDialogBase.prototype.postCreate.call(this);
dojo.widget.ModalFloatingPane.superclass.postCreate.call(this);
},show:function(){
this.showModalDialog();
dojo.widget.ModalFloatingPane.superclass.show.apply(this,arguments);
this.bg.style.zIndex=this.domNode.style.zIndex-1;
},hide:function(){
this.hideModalDialog();
dojo.widget.ModalFloatingPane.superclass.hide.apply(this,arguments);
},closeWindow:function(){
this.hide();
dojo.widget.ModalFloatingPane.superclass.closeWindow.apply(this,arguments);
}});
dojo.provide("dojo.widget.Editor2Plugin.AlwaysShowToolbar");
dojo.event.topic.subscribe("dojo.widget.Editor2::onLoad",function(_b42){
if(_b42.toolbarAlwaysVisible){
var p=new dojo.widget.Editor2Plugin.AlwaysShowToolbar(_b42);
}
});
dojo.declare("dojo.widget.Editor2Plugin.AlwaysShowToolbar",null,function(_b44){
this.editor=_b44;
this.editor.registerLoadedPlugin(this);
this.setup();
},{_scrollSetUp:false,_fixEnabled:false,_scrollThreshold:false,_handleScroll:true,setup:function(){
var tdn=this.editor.toolbarWidget;
if(!tdn.tbBgIframe){
tdn.tbBgIframe=new dojo.html.BackgroundIframe(tdn.domNode);
tdn.tbBgIframe.onResized();
}
this.scrollInterval=setInterval(dojo.lang.hitch(this,"globalOnScrollHandler"),100);
dojo.event.connect("before",this.editor.toolbarWidget,"destroy",this,"destroy");
},globalOnScrollHandler:function(){
var isIE=dojo.render.html.ie;
if(!this._handleScroll){
return;
}
var dh=dojo.html;
var tdn=this.editor.toolbarWidget.domNode;
var db=dojo.body();
if(!this._scrollSetUp){
this._scrollSetUp=true;
var _b4a=dh.getMarginBox(this.editor.domNode).width;
this._scrollThreshold=dh.abs(tdn,true).y;
if((isIE)&&(db)&&(dh.getStyle(db,"background-image")=="none")){
with(db.style){
backgroundImage="url("+dojo.uri.moduleUri("dojo.widget","templates/images/blank.gif")+")";
backgroundAttachment="fixed";
}
}
}
var _b4b=(window["pageYOffset"])?window["pageYOffset"]:(document["documentElement"]||document["body"]).scrollTop;
if(_b4b>this._scrollThreshold){
if(!this._fixEnabled){
var _b4c=dojo.html.getMarginBox(tdn);
this.editor.editorObject.style.marginTop=_b4c.height+"px";
if(isIE){
tdn.style.left=dojo.html.abs(tdn,dojo.html.boxSizing.MARGIN_BOX).x;
if(tdn.previousSibling){
this._IEOriginalPos=["after",tdn.previousSibling];
}else{
if(tdn.nextSibling){
this._IEOriginalPos=["before",tdn.nextSibling];
}else{
this._IEOriginalPos=["",tdn.parentNode];
}
}
dojo.body().appendChild(tdn);
dojo.html.addClass(tdn,"IEFixedToolbar");
}else{
with(tdn.style){
position="fixed";
top="0px";
}
}
tdn.style.width=_b4c.width+"px";
tdn.style.zIndex=1000;
this._fixEnabled=true;
}
if(!dojo.render.html.safari){
var _b4d=(this.height)?parseInt(this.editor.height):this.editor._lastHeight;
if(_b4b>(this._scrollThreshold+_b4d)){
tdn.style.display="none";
}else{
tdn.style.display="";
}
}
}else{
if(this._fixEnabled){
(this.editor.object||this.editor.iframe).style.marginTop=null;
with(tdn.style){
position="";
top="";
zIndex="";
display="";
}
if(isIE){
tdn.style.left="";
dojo.html.removeClass(tdn,"IEFixedToolbar");
if(this._IEOriginalPos){
dojo.html.insertAtPosition(tdn,this._IEOriginalPos[1],this._IEOriginalPos[0]);
this._IEOriginalPos=null;
}else{
dojo.html.insertBefore(tdn,this.editor.object||this.editor.iframe);
}
}
tdn.style.width="";
this._fixEnabled=false;
}
}
},destroy:function(){
this._IEOriginalPos=null;
this._handleScroll=false;
clearInterval(this.scrollInterval);
this.editor.unregisterLoadedPlugin(this);
if(dojo.render.html.ie){
dojo.html.removeClass(this.editor.toolbarWidget.domNode,"IEFixedToolbar");
}
}});
dojo.provide("dojo.widget.Editor2");
dojo.widget.Editor2Manager=new dojo.widget.HandlerManager;
dojo.lang.mixin(dojo.widget.Editor2Manager,{_currentInstance:null,commandState:{Disabled:0,Latched:1,Enabled:2},getCurrentInstance:function(){
return this._currentInstance;
},setCurrentInstance:function(inst){
this._currentInstance=inst;
},getCommand:function(_b4f,name){
var _b51;
name=name.toLowerCase();
for(var i=0;i<this._registeredHandlers.length;i++){
_b51=this._registeredHandlers[i](_b4f,name);
if(_b51){
return _b51;
}
}
switch(name){
case "htmltoggle":
_b51=new dojo.widget.Editor2BrowserCommand(_b4f,name);
break;
case "formatblock":
_b51=new dojo.widget.Editor2FormatBlockCommand(_b4f,name);
break;
case "anchor":
_b51=new dojo.widget.Editor2Command(_b4f,name);
break;
case "createlink":
_b51=new dojo.widget.Editor2DialogCommand(_b4f,name,{contentFile:"dojo.widget.Editor2Plugin.CreateLinkDialog",contentClass:"Editor2CreateLinkDialog",title:"Insert/Edit Link",width:"300px",height:"200px"});
break;
case "insertimage":
_b51=new dojo.widget.Editor2DialogCommand(_b4f,name,{contentFile:"dojo.widget.Editor2Plugin.InsertImageDialog",contentClass:"Editor2InsertImageDialog",title:"Insert/Edit Image",width:"400px",height:"270px"});
break;
default:
var _b53=this.getCurrentInstance();
if((_b53&&_b53.queryCommandAvailable(name))||(!_b53&&dojo.widget.Editor2.prototype.queryCommandAvailable(name))){
_b51=new dojo.widget.Editor2BrowserCommand(_b4f,name);
}else{
dojo.debug("dojo.widget.Editor2Manager.getCommand: Unknown command "+name);
return;
}
}
return _b51;
},destroy:function(){
this._currentInstance=null;
dojo.widget.HandlerManager.prototype.destroy.call(this);
}});
dojo.addOnUnload(dojo.widget.Editor2Manager,"destroy");
dojo.lang.declare("dojo.widget.Editor2Command",null,function(_b54,name){
this._editor=_b54;
this._updateTime=0;
this._name=name;
},{_text:"Unknown",execute:function(para){
dojo.unimplemented("dojo.widget.Editor2Command.execute");
},getText:function(){
return this._text;
},getState:function(){
return dojo.widget.Editor2Manager.commandState.Enabled;
},destroy:function(){
}});
dojo.widget.Editor2BrowserCommandNames={"bold":"Bold","copy":"Copy","cut":"Cut","Delete":"Delete","indent":"Indent","inserthorizontalrule":"Horizental Rule","insertorderedlist":"Numbered List","insertunorderedlist":"Bullet List","italic":"Italic","justifycenter":"Align Center","justifyfull":"Justify","justifyleft":"Align Left","justifyright":"Align Right","outdent":"Outdent","paste":"Paste","redo":"Redo","removeformat":"Remove Format","selectall":"Select All","strikethrough":"Strikethrough","subscript":"Subscript","superscript":"Superscript","underline":"Underline","undo":"Undo","unlink":"Remove Link","createlink":"Create Link","insertimage":"Insert Image","htmltoggle":"HTML Source","forecolor":"Foreground Color","hilitecolor":"Background Color","plainformatblock":"Paragraph Style","formatblock":"Paragraph Style","fontsize":"Font Size","fontname":"Font Name"};
dojo.lang.declare("dojo.widget.Editor2BrowserCommand",dojo.widget.Editor2Command,function(_b57,name){
var text=dojo.widget.Editor2BrowserCommandNames[name.toLowerCase()];
if(text){
this._text=text;
}
},{execute:function(para){
this._editor.execCommand(this._name,para);
},getState:function(){
if(this._editor._lastStateTimestamp>this._updateTime||this._state==undefined){
this._updateTime=this._editor._lastStateTimestamp;
try{
if(this._editor.queryCommandEnabled(this._name)){
if(this._editor.queryCommandState(this._name)){
this._state=dojo.widget.Editor2Manager.commandState.Latched;
}else{
this._state=dojo.widget.Editor2Manager.commandState.Enabled;
}
}else{
this._state=dojo.widget.Editor2Manager.commandState.Disabled;
}
}
catch(e){
this._state=dojo.widget.Editor2Manager.commandState.Enabled;
}
}
return this._state;
},getValue:function(){
try{
return this._editor.queryCommandValue(this._name);
}
catch(e){
}
}});
dojo.lang.declare("dojo.widget.Editor2FormatBlockCommand",dojo.widget.Editor2BrowserCommand,{});
dojo.widget.defineWidget("dojo.widget.Editor2Dialog",[dojo.widget.HtmlWidget,dojo.widget.FloatingPaneBase,dojo.widget.ModalDialogBase],{templateString:"<div id=\"${this.widgetId}\" class=\"dojoFloatingPane\">\r\n\t<span dojoattachpoint=\"tabStartOuter\" dojoonfocus=\"trapTabs\" dojoonblur=\"clearTrap\"\ttabindex=\"0\"></span>\r\n\t<span dojoattachpoint=\"tabStart\" dojoonfocus=\"trapTabs\" dojoonblur=\"clearTrap\" tabindex=\"0\"></span>\r\n\t<div dojoAttachPoint=\"titleBar\" class=\"dojoFloatingPaneTitleBar\"  style=\"display:none\">\r\n\t  \t<img dojoAttachPoint=\"titleBarIcon\"  class=\"dojoFloatingPaneTitleBarIcon\">\r\n\t\t<div dojoAttachPoint=\"closeAction\" dojoAttachEvent=\"onClick:hide\"\r\n   \t  \t\tclass=\"dojoFloatingPaneCloseIcon\"></div>\r\n\t\t<div dojoAttachPoint=\"restoreAction\" dojoAttachEvent=\"onClick:restoreWindow\"\r\n   \t  \t\tclass=\"dojoFloatingPaneRestoreIcon\"></div>\r\n\t\t<div dojoAttachPoint=\"maximizeAction\" dojoAttachEvent=\"onClick:maximizeWindow\"\r\n   \t  \t\tclass=\"dojoFloatingPaneMaximizeIcon\"></div>\r\n\t\t<div dojoAttachPoint=\"minimizeAction\" dojoAttachEvent=\"onClick:minimizeWindow\"\r\n   \t  \t\tclass=\"dojoFloatingPaneMinimizeIcon\"></div>\r\n\t  \t<div dojoAttachPoint=\"titleBarText\" class=\"dojoFloatingPaneTitleText\">${this.title}</div>\r\n\t</div>\r\n\r\n\t<div id=\"${this.widgetId}_container\" dojoAttachPoint=\"containerNode\" class=\"dojoFloatingPaneClient\"></div>\r\n\t<span dojoattachpoint=\"tabEnd\" dojoonfocus=\"trapTabs\" dojoonblur=\"clearTrap\" tabindex=\"0\"></span>\r\n\t<span dojoattachpoint=\"tabEndOuter\" dojoonfocus=\"trapTabs\" dojoonblur=\"clearTrap\" tabindex=\"0\"></span>\r\n\t<div dojoAttachPoint=\"resizeBar\" class=\"dojoFloatingPaneResizebar\" style=\"display:none\"></div>\r\n</div>\r\n",modal:true,width:"",height:"",windowState:"minimized",displayCloseAction:true,contentFile:"",contentClass:"",fillInTemplate:function(args,frag){
this.fillInFloatingPaneTemplate(args,frag);
dojo.widget.Editor2Dialog.superclass.fillInTemplate.call(this,args,frag);
},postCreate:function(){
if(this.contentFile){
dojo.require(this.contentFile);
}
if(this.modal){
dojo.widget.ModalDialogBase.prototype.postCreate.call(this);
}else{
with(this.domNode.style){
zIndex=999;
display="none";
}
}
dojo.widget.FloatingPaneBase.prototype.postCreate.apply(this,arguments);
dojo.widget.Editor2Dialog.superclass.postCreate.call(this);
if(this.width&&this.height){
with(this.domNode.style){
width=this.width;
height=this.height;
}
}
},createContent:function(){
if(!this.contentWidget&&this.contentClass){
this.contentWidget=dojo.widget.createWidget(this.contentClass);
this.addChild(this.contentWidget);
}
},show:function(){
if(!this.contentWidget){
dojo.widget.Editor2Dialog.superclass.show.apply(this,arguments);
this.createContent();
dojo.widget.Editor2Dialog.superclass.hide.call(this);
}
if(!this.contentWidget||!this.contentWidget.loadContent()){
return;
}
this.showFloatingPane();
dojo.widget.Editor2Dialog.superclass.show.apply(this,arguments);
if(this.modal){
this.showModalDialog();
}
if(this.modal){
this.bg.style.zIndex=this.domNode.style.zIndex-1;
}
},onShow:function(){
dojo.widget.Editor2Dialog.superclass.onShow.call(this);
this.onFloatingPaneShow();
},closeWindow:function(){
this.hide();
dojo.widget.Editor2Dialog.superclass.closeWindow.apply(this,arguments);
},hide:function(){
if(this.modal){
this.hideModalDialog();
}
dojo.widget.Editor2Dialog.superclass.hide.call(this);
},checkSize:function(){
if(this.isShowing()){
if(this.modal){
this._sizeBackground();
}
this.placeModalDialog();
this.onResized();
}
}});
dojo.widget.defineWidget("dojo.widget.Editor2DialogContent",dojo.widget.HtmlWidget,{widgetsInTemplate:true,loadContent:function(){
return true;
},cancel:function(){
this.parent.hide();
}});
dojo.lang.declare("dojo.widget.Editor2DialogCommand",dojo.widget.Editor2BrowserCommand,function(_b5d,name,_b5f){
this.dialogParas=_b5f;
},{execute:function(){
if(!this.dialog){
if(!this.dialogParas.contentFile||!this.dialogParas.contentClass){
alert("contentFile and contentClass should be set for dojo.widget.Editor2DialogCommand.dialogParas!");
return;
}
this.dialog=dojo.widget.createWidget("Editor2Dialog",this.dialogParas);
dojo.body().appendChild(this.dialog.domNode);
dojo.event.connect(this,"destroy",this.dialog,"destroy");
}
this.dialog.show();
},getText:function(){
return this.dialogParas.title||dojo.widget.Editor2DialogCommand.superclass.getText.call(this);
}});
dojo.widget.Editor2ToolbarGroups={};
dojo.widget.defineWidget("dojo.widget.Editor2",dojo.widget.RichText,function(){
this._loadedCommands={};
},{toolbarAlwaysVisible:false,toolbarWidget:null,scrollInterval:null,toolbarTemplatePath:dojo.uri.cache.set(dojo.uri.moduleUri("dojo.widget","templates/EditorToolbarOneline.html"), "<div class=\"EditorToolbarDomNode EditorToolbarSmallBg\">\r\n\t<table cellpadding=\"1\" cellspacing=\"0\" border=\"0\">\r\n\t\t<tbody>\r\n\t\t\t<tr valign=\"top\" align=\"left\">\r\n\t\t\t\t<td>\r\n\t\t\t\t\t<span class=\"iconContainer dojoEditorToolbarItem\" dojoETItemName=\"htmltoggle\">\r\n\t\t\t\t\t\t<span class=\"dojoE2TBIcon\" \r\n\t\t\t\t\t\tstyle=\"background-image: none; width: 30px;\" >&lt;h&gt;</span>\r\n\t\t\t\t\t</span>\r\n\t\t\t\t</td>\r\n\t\t\t\t<td>\r\n\t\t\t\t\t<span class=\"iconContainer dojoEditorToolbarItem\" dojoETItemName=\"copy\">\r\n\t\t\t\t\t\t<span class=\"dojoE2TBIcon dojoE2TBIcon_Copy\">&nbsp;</span>\r\n\t\t\t\t\t</span>\r\n\t\t\t\t</td>\r\n\t\t\t\t<td>\r\n\t\t\t\t\t<span class=\"iconContainer dojoEditorToolbarItem\" dojoETItemName=\"paste\">\r\n\t\t\t\t\t\t<span class=\"dojoE2TBIcon dojoE2TBIcon_Paste\">&nbsp;</span>\r\n\t\t\t\t\t</span>\r\n\t\t\t\t</td>\r\n\t\t\t\t<td>\r\n\t\t\t\t\t<span class=\"iconContainer dojoEditorToolbarItem\" dojoETItemName=\"undo\">\r\n\t\t\t\t\t\t<!-- FIXME: should we have the text \"undo\" here? -->\r\n\t\t\t\t\t\t<span class=\"dojoE2TBIcon dojoE2TBIcon_Undo\">&nbsp;</span>\r\n\t\t\t\t\t</span>\r\n\t\t\t\t</td>\r\n\t\t\t\t<td>\r\n\t\t\t\t\t<span class=\"iconContainer dojoEditorToolbarItem\" dojoETItemName=\"redo\">\r\n\t\t\t\t\t\t<span class=\"dojoE2TBIcon dojoE2TBIcon_Redo\">&nbsp;</span>\r\n\t\t\t\t\t</span>\r\n\t\t\t\t</td>\r\n\t\t\t\t<td isSpacer=\"true\">\r\n\t\t\t\t\t<span class=\"iconContainer\">\r\n\t\t\t\t\t\t<span class=\"dojoE2TBIcon dojoE2TBIcon_Sep\"\tstyle=\"width: 5px; min-width: 5px;\"></span>\r\n\t\t\t\t\t</span>\r\n\t\t\t\t</td>\r\n\t\t\t\t<td>\r\n\t\t\t\t\t<span class=\"iconContainer dojoEditorToolbarItem\" dojoETItemName=\"createlink\">\r\n\t\t\t\t\t\t<span class=\"dojoE2TBIcon dojoE2TBIcon_Link\">&nbsp;</span>\r\n\t\t\t\t\t</span>\r\n\t\t\t\t</td>\r\n\t\t\t\t<td>\r\n\t\t\t\t\t<span class=\"iconContainer dojoEditorToolbarItem\" dojoETItemName=\"insertimage\">\r\n\t\t\t\t\t\t<span class=\"dojoE2TBIcon dojoE2TBIcon_Image\">&nbsp;</span>\r\n\t\t\t\t\t</span>\r\n\t\t\t\t</td>\r\n\t\t\t\t<td>\r\n\t\t\t\t\t<span class=\"iconContainer dojoEditorToolbarItem\" dojoETItemName=\"inserthorizontalrule\">\r\n\t\t\t\t\t\t<span class=\"dojoE2TBIcon dojoE2TBIcon_HorizontalLine \">&nbsp;</span>\r\n\t\t\t\t\t</span>\r\n\t\t\t\t</td>\r\n\t\t\t\t<td>\r\n\t\t\t\t\t<span class=\"iconContainer dojoEditorToolbarItem\" dojoETItemName=\"bold\">\r\n\t\t\t\t\t\t<span class=\"dojoE2TBIcon dojoE2TBIcon_Bold\">&nbsp;</span>\r\n\t\t\t\t\t</span>\r\n\t\t\t\t</td>\r\n\t\t\t\t<td>\r\n\t\t\t\t\t<span class=\"iconContainer dojoEditorToolbarItem\" dojoETItemName=\"italic\">\r\n\t\t\t\t\t\t<span class=\"dojoE2TBIcon dojoE2TBIcon_Italic\">&nbsp;</span>\r\n\t\t\t\t\t</span>\r\n\t\t\t\t</td>\r\n\t\t\t\t<td>\r\n\t\t\t\t\t<span class=\"iconContainer dojoEditorToolbarItem\" dojoETItemName=\"underline\">\r\n\t\t\t\t\t\t<span class=\"dojoE2TBIcon dojoE2TBIcon_Underline\">&nbsp;</span>\r\n\t\t\t\t\t</span>\r\n\t\t\t\t</td>\r\n\t\t\t\t<td>\r\n\t\t\t\t\t<span class=\"iconContainer dojoEditorToolbarItem\" dojoETItemName=\"strikethrough\">\r\n\t\t\t\t\t\t<span \r\n\t\t\t\t\t\t\tclass=\"dojoE2TBIcon dojoE2TBIcon_StrikeThrough\">&nbsp;</span>\r\n\t\t\t\t\t</span>\r\n\t\t\t\t</td>\r\n\t\t\t\t<td isSpacer=\"true\">\r\n\t\t\t\t\t<span class=\"iconContainer\">\r\n\t\t\t\t\t\t<span class=\"dojoE2TBIcon dojoE2TBIcon_Sep\" \r\n\t\t\t\t\t\t\tstyle=\"width: 5px; min-width: 5px;\"></span>\r\n\t\t\t\t\t</span>\r\n\t\t\t\t</td>\r\n\t\t\t\t<td>\r\n\t\t\t\t\t<span class=\"iconContainer dojoEditorToolbarItem\" dojoETItemName=\"insertunorderedlist\">\r\n\t\t\t\t\t\t<span \r\n\t\t\t\t\t\t\tclass=\"dojoE2TBIcon dojoE2TBIcon_BulletedList\">&nbsp;</span>\r\n\t\t\t\t\t</span>\r\n\t\t\t\t</td>\r\n\t\t\t\t<td>\r\n\t\t\t\t\t<span class=\"iconContainer dojoEditorToolbarItem\" dojoETItemName=\"insertorderedlist\">\r\n\t\t\t\t\t\t<span \r\n\t\t\t\t\t\t\tclass=\"dojoE2TBIcon dojoE2TBIcon_NumberedList\">&nbsp;</span>\r\n\t\t\t\t\t</span>\r\n\t\t\t\t</td>\r\n\t\t\t\t<td isSpacer=\"true\">\r\n\t\t\t\t\t<span class=\"iconContainer\">\r\n\t\t\t\t\t\t<span class=\"dojoE2TBIcon dojoE2TBIcon_Sep\" style=\"width: 5px; min-width: 5px;\"></span>\r\n\t\t\t\t\t</span>\r\n\t\t\t\t</td>\r\n\t\t\t\t<td>\r\n\t\t\t\t\t<span class=\"iconContainer dojoEditorToolbarItem\" dojoETItemName=\"indent\">\r\n\t\t\t\t\t\t<span class=\"dojoE2TBIcon dojoE2TBIcon_Indent\" \r\n\t\t\t\t\t\t\tunselectable=\"on\">&nbsp;</span>\r\n\t\t\t\t\t</span>\r\n\t\t\t\t</td>\r\n\t\t\t\t<td>\r\n\t\t\t\t\t<span class=\"iconContainer dojoEditorToolbarItem\" dojoETItemName=\"outdent\">\r\n\t\t\t\t\t\t<span class=\"dojoE2TBIcon dojoE2TBIcon_Outdent\" \r\n\t\t\t\t\t\t\tunselectable=\"on\">&nbsp;</span>\r\n\t\t\t\t\t</span>\r\n\t\t\t\t</td>\r\n\t\t\t\t<td isSpacer=\"true\">\r\n\t\t\t\t\t<span class=\"iconContainer\">\r\n\t\t\t\t\t\t<span class=\"dojoE2TBIcon dojoE2TBIcon_Sep\" style=\"width: 5px; min-width: 5px;\"></span>\r\n\t\t\t\t\t</span>\r\n\t\t\t\t</td>\r\n\t\t\t\t<td>\r\n\t\t\t\t\t<span class=\"iconContainer dojoEditorToolbarItem\" dojoETItemName=\"forecolor\">\r\n\t\t\t\t\t\t<span class=\"dojoE2TBIcon dojoE2TBIcon_TextColor\" \r\n\t\t\t\t\t\t\tunselectable=\"on\">&nbsp;</span>\r\n\t\t\t\t\t</span>\r\n\t\t\t\t</td>\r\n\t\t\t\t<td>\r\n\t\t\t\t\t<span class=\"iconContainer dojoEditorToolbarItem\" dojoETItemName=\"hilitecolor\">\r\n\t\t\t\t\t\t<span class=\"dojoE2TBIcon dojoE2TBIcon_BackgroundColor\" \r\n\t\t\t\t\t\t\tunselectable=\"on\">&nbsp;</span>\r\n\t\t\t\t\t</span>\r\n\t\t\t\t</td>\r\n\t\t\t\t<td isSpacer=\"true\">\r\n\t\t\t\t\t<span class=\"iconContainer\">\r\n\t\t\t\t\t\t<span class=\"dojoE2TBIcon dojoE2TBIcon_Sep\" style=\"width: 5px; min-width: 5px;\"></span>\r\n\t\t\t\t\t</span>\r\n\t\t\t\t</td>\r\n\t\t\t\t<td>\r\n\t\t\t\t\t<span class=\"iconContainer dojoEditorToolbarItem\" dojoETItemName=\"justifyleft\">\r\n\t\t\t\t\t\t<span class=\"dojoE2TBIcon dojoE2TBIcon_LeftJustify\">&nbsp;</span>\r\n\t\t\t\t\t</span>\r\n\t\t\t\t</td>\r\n\t\t\t\t<td>\r\n\t\t\t\t\t<span class=\"iconContainer dojoEditorToolbarItem\" dojoETItemName=\"justifycenter\">\r\n\t\t\t\t\t\t<span class=\"dojoE2TBIcon dojoE2TBIcon_CenterJustify\">&nbsp;</span>\r\n\t\t\t\t\t</span>\r\n\t\t\t\t</td>\r\n\t\t\t\t<td>\r\n\t\t\t\t\t<span class=\"iconContainer dojoEditorToolbarItem\" dojoETItemName=\"justifyright\">\r\n\t\t\t\t\t\t<span class=\"dojoE2TBIcon dojoE2TBIcon_RightJustify\">&nbsp;</span>\r\n\t\t\t\t\t</span>\r\n\t\t\t\t</td>\r\n\t\t\t\t<td>\r\n\t\t\t\t\t<span class=\"iconContainer dojoEditorToolbarItem\" dojoETItemName=\"justifyfull\">\r\n\t\t\t\t\t\t<span class=\"dojoE2TBIcon dojoE2TBIcon_BlockJustify\">&nbsp;</span>\r\n\t\t\t\t\t</span>\r\n\t\t\t\t</td>\t\r\n\t\t\t\t<td>\r\n\t\t\t\t\t<select class=\"dojoEditorToolbarItem\" dojoETItemName=\"plainformatblock\">\r\n\t\t\t\t\t\t<!-- FIXME: using \"p\" here inserts a paragraph in most cases! -->\r\n\t\t\t\t\t\t<option value=\"\">-- format --</option>\r\n\t\t\t\t\t\t<option value=\"p\">Normal</option>\r\n\t\t\t\t\t\t<option value=\"pre\">Fixed Font</option>\r\n\t\t\t\t\t\t<option value=\"h1\">Main Heading</option>\r\n\t\t\t\t\t\t<option value=\"h2\">Section Heading</option>\r\n\t\t\t\t\t\t<option value=\"h3\">Sub-Heading</option>\r\n\t\t\t\t\t\t<!-- <option value=\"blockquote\">Block Quote</option> -->\r\n\t\t\t\t\t</select>\r\n\t\t\t\t</td>\r\n\t\t\t\t<td><!-- uncomment to enable save button -->\r\n\t\t\t\t\t<!-- save -->\r\n\t\t\t\t\t<!--span class=\"iconContainer dojoEditorToolbarItem\" dojoETItemName=\"save\">\r\n\t\t\t\t\t\t<span class=\"dojoE2TBIcon dojoE2TBIcon_Save\">&nbsp;</span>\r\n\t\t\t\t\t</span-->\r\n\t\t\t\t</td>\r\n\t\t\t\t<td width=\"*\">&nbsp;</td>\r\n\t\t\t</tr>\r\n\t\t</tbody>\r\n\t</table>\r\n</div>\r\n"),toolbarTemplateCssPath:null,toolbarPlaceHolder:"",_inSourceMode:false,_htmlEditNode:null,toolbarGroup:"",shareToolbar:false,contextMenuGroupSet:"",editorOnLoad:function(){
dojo.event.topic.publish("dojo.widget.Editor2::preLoadingToolbar",this);
if(this.toolbarAlwaysVisible){
}
if(this.toolbarWidget){
this.toolbarWidget.show();
dojo.html.insertBefore(this.toolbarWidget.domNode,this.domNode.firstChild);
}else{
if(this.shareToolbar){
dojo.deprecated("Editor2:shareToolbar is deprecated in favor of toolbarGroup","0.5");
this.toolbarGroup="defaultDojoToolbarGroup";
}
if(this.toolbarGroup){
if(dojo.widget.Editor2ToolbarGroups[this.toolbarGroup]){
this.toolbarWidget=dojo.widget.Editor2ToolbarGroups[this.toolbarGroup];
}
}
if(!this.toolbarWidget){
var _b60={shareGroup:this.toolbarGroup,parent:this};
_b60.templateString=dojo.uri.cache.get(this.toolbarTemplatePath);
if(this.toolbarTemplateCssPath){
_b60.templateCssPath=this.toolbarTemplateCssPath;
_b60.templateCssString=dojo.uri.cache.get(this.toolbarTemplateCssPath);
}
if(this.toolbarPlaceHolder){
this.toolbarWidget=dojo.widget.createWidget("Editor2Toolbar",_b60,dojo.byId(this.toolbarPlaceHolder),"after");
}else{
this.toolbarWidget=dojo.widget.createWidget("Editor2Toolbar",_b60,this.domNode.firstChild,"before");
}
if(this.toolbarGroup){
dojo.widget.Editor2ToolbarGroups[this.toolbarGroup]=this.toolbarWidget;
}
dojo.event.connect(this,"close",this.toolbarWidget,"hide");
this.toolbarLoaded();
}
}
dojo.event.topic.registerPublisher("Editor2.clobberFocus",this,"clobberFocus");
dojo.event.topic.subscribe("Editor2.clobberFocus",this,"setBlur");
dojo.event.topic.publish("dojo.widget.Editor2::onLoad",this);
},toolbarLoaded:function(){
},registerLoadedPlugin:function(obj){
if(!this.loadedPlugins){
this.loadedPlugins=[];
}
this.loadedPlugins.push(obj);
},unregisterLoadedPlugin:function(obj){
for(var i in this.loadedPlugins){
if(this.loadedPlugins[i]===obj){
delete this.loadedPlugins[i];
return;
}
}
dojo.debug("dojo.widget.Editor2.unregisterLoadedPlugin: unknow plugin object: "+obj);
},execCommand:function(_b64,_b65){
switch(_b64.toLowerCase()){
case "htmltoggle":
this.toggleHtmlEditing();
break;
default:
dojo.widget.Editor2.superclass.execCommand.apply(this,arguments);
}
},queryCommandEnabled:function(_b66,_b67){
switch(_b66.toLowerCase()){
case "htmltoggle":
return true;
default:
if(this._inSourceMode){
return false;
}
return dojo.widget.Editor2.superclass.queryCommandEnabled.apply(this,arguments);
}
},queryCommandState:function(_b68,_b69){
switch(_b68.toLowerCase()){
case "htmltoggle":
return this._inSourceMode;
default:
return dojo.widget.Editor2.superclass.queryCommandState.apply(this,arguments);
}
},onClick:function(e){
dojo.widget.Editor2.superclass.onClick.call(this,e);
if(dojo.widget.PopupManager){
if(!e){
e=this.window.event;
}
dojo.widget.PopupManager.onClick(e);
}
},clobberFocus:function(){
},toggleHtmlEditing:function(){
if(this===dojo.widget.Editor2Manager.getCurrentInstance()){
if(!this._inSourceMode){
var html=this.getEditorContent();
this._inSourceMode=true;
if(!this._htmlEditNode){
this._htmlEditNode=dojo.doc().createElement("textarea");
dojo.html.insertAfter(this._htmlEditNode,this.editorObject);
}
this._htmlEditNode.style.display="";
this._htmlEditNode.style.width="100%";
this._htmlEditNode.style.height=dojo.html.getBorderBox(this.editNode).height+"px";
this._htmlEditNode.value=html;
with(this.editorObject.style){
position="absolute";
left="-2000px";
top="-2000px";
}
}else{
this._inSourceMode=false;
this._htmlEditNode.blur();
with(this.editorObject.style){
position="";
left="";
top="";
}
var html=this._htmlEditNode.value;
dojo.lang.setTimeout(this,"replaceEditorContent",1,html);
this._htmlEditNode.style.display="none";
this.focus();
}
this.onDisplayChanged(null,true);
}
},setFocus:function(){
if(dojo.widget.Editor2Manager.getCurrentInstance()===this){
return;
}
this.clobberFocus();
dojo.widget.Editor2Manager.setCurrentInstance(this);
},setBlur:function(){
},saveSelection:function(){
this._bookmark=null;
this._bookmark=dojo.withGlobal(this.window,dojo.html.selection.getBookmark);
},restoreSelection:function(){
if(this._bookmark){
this.focus();
dojo.withGlobal(this.window,"moveToBookmark",dojo.html.selection,[this._bookmark]);
this._bookmark=null;
}else{
dojo.debug("restoreSelection: no saved selection is found!");
}
},_updateToolbarLastRan:null,_updateToolbarTimer:null,_updateToolbarFrequency:500,updateToolbar:function(_b6c){
if((!this.isLoaded)||(!this.toolbarWidget)){
return;
}
var diff=new Date()-this._updateToolbarLastRan;
if((!_b6c)&&(this._updateToolbarLastRan)&&((diff<this._updateToolbarFrequency))){
clearTimeout(this._updateToolbarTimer);
var _b6e=this;
this._updateToolbarTimer=setTimeout(function(){
_b6e.updateToolbar();
},this._updateToolbarFrequency/2);
return;
}else{
this._updateToolbarLastRan=new Date();
}
if(dojo.widget.Editor2Manager.getCurrentInstance()!==this){
return;
}
this.toolbarWidget.update();
},destroy:function(_b6f){
this._htmlEditNode=null;
dojo.event.disconnect(this,"close",this.toolbarWidget,"hide");
if(!_b6f){
this.toolbarWidget.destroy();
}
dojo.widget.Editor2.superclass.destroy.call(this);
},_lastStateTimestamp:0,onDisplayChanged:function(e,_b71){
this._lastStateTimestamp=(new Date()).getTime();
dojo.widget.Editor2.superclass.onDisplayChanged.call(this,e);
this.updateToolbar(_b71);
},onLoad:function(){
try{
dojo.widget.Editor2.superclass.onLoad.call(this);
}
catch(e){
dojo.debug(e);
}
this.editorOnLoad();
},onFocus:function(){
dojo.widget.Editor2.superclass.onFocus.call(this);
this.setFocus();
},getEditorContent:function(){
if(this._inSourceMode){
return this._htmlEditNode.value;
}
return dojo.widget.Editor2.superclass.getEditorContent.call(this);
},replaceEditorContent:function(html){
if(this._inSourceMode){
this._htmlEditNode.value=html;
return;
}
dojo.widget.Editor2.superclass.replaceEditorContent.apply(this,arguments);
},getCommand:function(name){
if(this._loadedCommands[name]){
return this._loadedCommands[name];
}
var cmd=dojo.widget.Editor2Manager.getCommand(this,name);
this._loadedCommands[name]=cmd;
return cmd;
},shortcuts:[["bold"],["italic"],["underline"],["selectall","a"],["insertunorderedlist","\\"]],setupDefaultShortcuts:function(){
var exec=function(cmd){
return function(){
cmd.execute();
};
};
var self=this;
dojo.lang.forEach(this.shortcuts,function(item){
var cmd=self.getCommand(item[0]);
if(cmd){
self.addKeyHandler(item[1]?item[1]:item[0].charAt(0),item[2]==undefined?self.KEY_CTRL:item[2],exec(cmd));
}
});
}});

