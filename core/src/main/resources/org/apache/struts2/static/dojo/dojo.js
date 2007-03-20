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
dojo.version={major:0,minor:4,patch:0,flag:"",revision:Number("$Rev: 6258 $".match(/[0-9]+/)[0]),toString:function(){
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
if(typeof setTimeout=="object"){
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
return _6e?_6e.toLowerCase():dojo.locale;
};
dojo.hostenv.searchLocalePath=function(_6f,_70,_71){
_6f=dojo.hostenv.normalizeLocale(_6f);
var _72=_6f.split("-");
var _73=[];
for(var i=_72.length;i>0;i--){
_73.push(_72.slice(0,i).join("-"));
}
_73.push(false);
if(_70){
_73.reverse();
}
for(var j=_73.length-1;j>=0;j--){
var loc=_73[j]||"ROOT";
var _77=_71(loc);
if(_77){
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
function preload(_78){
_78=dojo.hostenv.normalizeLocale(_78);
dojo.hostenv.searchLocalePath(_78,true,function(loc){
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
var _7b=djConfig.extraLocale||[];
for(var i=0;i<_7b.length;i++){
preload(_7b[i]);
}
}
dojo.hostenv.preloadLocalizations=function(){
};
};
dojo.requireLocalization=function(_7d,_7e,_7f){
dojo.hostenv.preloadLocalizations();
var _80=[_7d,"nls",_7e].join(".");
var _81=dojo.hostenv.findModule(_80);
if(_81){
if(djConfig.localizationComplete&&_81._built){
return;
}
var _82=dojo.hostenv.normalizeLocale(_7f).replace("-","_");
var _83=_80+"."+_82;
if(dojo.hostenv.findModule(_83)){
return;
}
}
_81=dojo.hostenv.startPackage(_80);
var _84=dojo.hostenv.getModuleSymbols(_7d);
var _85=_84.concat("nls").join("/");
var _86;
dojo.hostenv.searchLocalePath(_7f,false,function(loc){
var _88=loc.replace("-","_");
var _89=_80+"."+_88;
var _8a=false;
if(!dojo.hostenv.findModule(_89)){
dojo.hostenv.startPackage(_89);
var _8b=[_85];
if(loc!="ROOT"){
_8b.push(loc);
}
_8b.push(_7e);
var _8c=_8b.join("/")+".js";
_8a=dojo.hostenv.loadPath(_8c,null,function(_8d){
var _8e=function(){
};
_8e.prototype=_86;
_81[_88]=new _8e();
for(var j in _8d){
_81[_88][j]=_8d[j];
}
});
}else{
_8a=true;
}
if(_8a&&_81[_88]){
_86=_81[_88];
}else{
_81[_88]=_86;
}
});
};
(function(){
var _90=djConfig.extraLocale;
if(_90){
if(!_90 instanceof Array){
_90=[_90];
}
var req=dojo.requireLocalization;
dojo.requireLocalization=function(m,b,_94){
req(m,b,_94);
if(_94){
return;
}
for(var i=0;i<_90.length;i++){
req(m,b,_90[i]);
}
};
}
})();
}
if(typeof window!="undefined"){
(function(){
if(djConfig.allowQueryConfig){
var _96=document.location.toString();
var _97=_96.split("?",2);
if(_97.length>1){
var _98=_97[1];
var _99=_98.split("&");
for(var x in _99){
var sp=_99[x].split("=");
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
var _9d=document.getElementsByTagName("script");
var _9e=/(__package__|dojo|bootstrap1)\.js([\?\.]|$)/i;
for(var i=0;i<_9d.length;i++){
var src=_9d[i].getAttribute("src");
if(!src){
continue;
}
var m=src.match(_9e);
if(m){
var _a2=src.substring(0,m.index);
if(src.indexOf("bootstrap1")>-1){
_a2+="../";
}
if(!this["djConfig"]){
djConfig={};
}
if(djConfig["baseScriptUri"]==""){
djConfig["baseScriptUri"]=_a2;
}
if(djConfig["baseRelativePath"]==""){
djConfig["baseRelativePath"]=_a2;
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
var _aa=dua.indexOf("Gecko");
drh.mozilla=drh.moz=(_aa>=0)&&(!drh.khtml);
if(drh.mozilla){
drh.geckoVersion=dua.substring(_aa+6,_aa+14);
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
var _ac=window["document"];
var tdi=_ac["implementation"];
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
}
})();
dojo.hostenv.startPackage("dojo.hostenv");
dojo.render.name=dojo.hostenv.name_="browser";
dojo.hostenv.searchIds=[];
dojo.hostenv._XMLHTTP_PROGIDS=["Msxml2.XMLHTTP","Microsoft.XMLHTTP","Msxml2.XMLHTTP.4.0"];
dojo.hostenv.getXmlhttpObject=function(){
var _b0=null;
var _b1=null;
try{
_b0=new XMLHttpRequest();
}
catch(e){
}
if(!_b0){
for(var i=0;i<3;++i){
var _b3=dojo.hostenv._XMLHTTP_PROGIDS[i];
try{
_b0=new ActiveXObject(_b3);
}
catch(e){
_b1=e;
}
if(_b0){
dojo.hostenv._XMLHTTP_PROGIDS=[_b3];
break;
}
}
}
if(!_b0){
return dojo.raise("XMLHTTP not available",_b1);
}
return _b0;
};
dojo.hostenv._blockAsync=false;
dojo.hostenv.getText=function(uri,_b5,_b6){
if(!_b5){
this._blockAsync=true;
}
var _b7=this.getXmlhttpObject();
function isDocumentOk(_b8){
var _b9=_b8["status"];
return Boolean((!_b9)||((200<=_b9)&&(300>_b9))||(_b9==304));
}
if(_b5){
var _ba=this,_bb=null,gbl=dojo.global();
var xhr=dojo.evalObjPath("dojo.io.XMLHTTPTransport");
_b7.onreadystatechange=function(){
if(_bb){
gbl.clearTimeout(_bb);
_bb=null;
}
if(_ba._blockAsync||(xhr&&xhr._blockAsync)){
_bb=gbl.setTimeout(function(){
_b7.onreadystatechange.apply(this);
},10);
}else{
if(4==_b7.readyState){
if(isDocumentOk(_b7)){
_b5(_b7.responseText);
}
}
}
};
}
_b7.open("GET",uri,_b5?true:false);
try{
_b7.send(null);
if(_b5){
return null;
}
if(!isDocumentOk(_b7)){
var err=Error("Unable to load "+uri+" status:"+_b7.status);
err.status=_b7.status;
err.responseText=_b7.responseText;
throw err;
}
}
catch(e){
this._blockAsync=false;
if((_b6)&&(!_b5)){
return null;
}else{
throw e;
}
}
this._blockAsync=false;
return _b7.responseText;
};
dojo.hostenv.defaultDebugContainerId="dojoDebug";
dojo.hostenv._println_buffer=[];
dojo.hostenv._println_safe=false;
dojo.hostenv.println=function(_bf){
if(!dojo.hostenv._println_safe){
dojo.hostenv._println_buffer.push(_bf);
}else{
try{
var _c0=document.getElementById(djConfig.debugContainerId?djConfig.debugContainerId:dojo.hostenv.defaultDebugContainerId);
if(!_c0){
_c0=dojo.body();
}
var div=document.createElement("div");
div.appendChild(document.createTextNode(_bf));
_c0.appendChild(div);
}
catch(e){
try{
document.write("<div>"+_bf+"</div>");
}
catch(e2){
window.status=_bf;
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
function dj_addNodeEvtHdlr(_c2,_c3,fp,_c5){
var _c6=_c2["on"+_c3]||function(){
};
_c2["on"+_c3]=function(){
fp.apply(_c2,arguments);
_c6.apply(_c2,arguments);
};
return true;
}
function dj_load_init(e){
var _c8=(e&&e.type)?e.type.toLowerCase():"load";
if(arguments.callee.initialized||(_c8!="domcontentloaded"&&_c8!="load")){
return;
}
arguments.callee.initialized=true;
if(typeof (_timer)!="undefined"){
clearInterval(_timer);
delete _timer;
}
var _c9=function(){
if(dojo.render.html.ie){
dojo.hostenv.makeWidgets();
}
};
if(dojo.hostenv.inFlightCount==0){
_c9();
dojo.hostenv.modulesLoaded();
}else{
dojo.addOnLoad(_c9);
}
}
if(document.addEventListener){
if(dojo.render.html.opera||(dojo.render.html.moz&&!djConfig.delayMozLoadingFix)){
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
var _cb=[];
if(djConfig.searchIds&&djConfig.searchIds.length>0){
_cb=_cb.concat(djConfig.searchIds);
}
if(dojo.hostenv.searchIds&&dojo.hostenv.searchIds.length>0){
_cb=_cb.concat(dojo.hostenv.searchIds);
}
if((djConfig.parseWidgets)||(_cb.length>0)){
if(dojo.evalObjPath("dojo.widget.Parse")){
var _cc=new dojo.xml.Parse();
if(_cb.length>0){
for(var x=0;x<_cb.length;x++){
var _ce=document.getElementById(_cb[x]);
if(!_ce){
continue;
}
var _cf=_cc.parseElement(_ce,null,true);
dojo.widget.getParser().createComponents(_cf);
}
}else{
if(djConfig.parseWidgets){
var _cf=_cc.parseElement(dojo.body(),null,true);
dojo.widget.getParser().createComponents(_cf);
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
dojo.setContext=function(_d4,_d5){
dj_currentContext=_d4;
dj_currentDocument=_d5;
};
dojo._fireCallback=function(_d6,_d7,_d8){
if((_d7)&&((typeof _d6=="string")||(_d6 instanceof String))){
_d6=_d7[_d6];
}
return (_d7?_d6.apply(_d7,_d8||[]):_d6());
};
dojo.withGlobal=function(_d9,_da,_db,_dc){
var _dd;
var _de=dj_currentContext;
var _df=dj_currentDocument;
try{
dojo.setContext(_d9,_d9.document);
_dd=dojo._fireCallback(_da,_db,_dc);
}
finally{
dojo.setContext(_de,_df);
}
return _dd;
};
dojo.withDoc=function(_e0,_e1,_e2,_e3){
var _e4;
var _e5=dj_currentDocument;
try{
dj_currentDocument=_e0;
_e4=dojo._fireCallback(_e1,_e2,_e3);
}
finally{
dj_currentDocument=_e5;
}
return _e4;
};
}
(function(){
if(typeof dj_usingBootstrap!="undefined"){
return;
}
var _e6=false;
var _e7=false;
var _e8=false;
if((typeof this["load"]=="function")&&((typeof this["Packages"]=="function")||(typeof this["Packages"]=="object"))){
_e6=true;
}else{
if(typeof this["load"]=="function"){
_e7=true;
}else{
if(window.widget){
_e8=true;
}
}
}
var _e9=[];
if((this["djConfig"])&&((djConfig["isDebug"])||(djConfig["debugAtAllCosts"]))){
_e9.push("debug.js");
}
if((this["djConfig"])&&(djConfig["debugAtAllCosts"])&&(!_e6)&&(!_e8)){
_e9.push("browser_debug.js");
}
var _ea=djConfig["baseScriptUri"];
if((this["djConfig"])&&(djConfig["baseLoaderUri"])){
_ea=djConfig["baseLoaderUri"];
}
for(var x=0;x<_e9.length;x++){
var _ec=_ea+"src/"+_e9[x];
if(_e6||_e7){
load(_ec);
}else{
try{
document.write("<scr"+"ipt type='text/javascript' src='"+_ec+"'></scr"+"ipt>");
}
catch(e){
var _ed=document.createElement("script");
_ed.src=_ec;
document.getElementsByTagName("head")[0].appendChild(_ed);
}
}
}
})();
dojo.provide("dojo.lang.common");
dojo.lang.inherits=function(_ee,_ef){
if(typeof _ef!="function"){
dojo.raise("dojo.inherits: superclass argument ["+_ef+"] must be a function (subclass: ["+_ee+"']");
}
_ee.prototype=new _ef();
_ee.prototype.constructor=_ee;
_ee.superclass=_ef.prototype;
_ee["super"]=_ef.prototype;
};
dojo.lang._mixin=function(obj,_f1){
var _f2={};
for(var x in _f1){
if((typeof _f2[x]=="undefined")||(_f2[x]!=_f1[x])){
obj[x]=_f1[x];
}
}
if(dojo.render.html.ie&&(typeof (_f1["toString"])=="function")&&(_f1["toString"]!=obj["toString"])&&(_f1["toString"]!=_f2["toString"])){
obj.toString=_f1.toString;
}
return obj;
};
dojo.lang.mixin=function(obj,_f5){
for(var i=1,l=arguments.length;i<l;i++){
dojo.lang._mixin(obj,arguments[i]);
}
return obj;
};
dojo.lang.extend=function(_f8,_f9){
for(var i=1,l=arguments.length;i<l;i++){
dojo.lang._mixin(_f8.prototype,arguments[i]);
}
return _f8;
};
dojo.inherits=dojo.lang.inherits;
dojo.mixin=dojo.lang.mixin;
dojo.extend=dojo.lang.extend;
dojo.lang.find=function(_fc,_fd,_fe,_ff){
if(!dojo.lang.isArrayLike(_fc)&&dojo.lang.isArrayLike(_fd)){
dojo.deprecated("dojo.lang.find(value, array)","use dojo.lang.find(array, value) instead","0.5");
var temp=_fc;
_fc=_fd;
_fd=temp;
}
var _101=dojo.lang.isString(_fc);
if(_101){
_fc=_fc.split("");
}
if(_ff){
var step=-1;
var i=_fc.length-1;
var end=-1;
}else{
var step=1;
var i=0;
var end=_fc.length;
}
if(_fe){
while(i!=end){
if(_fc[i]===_fd){
return i;
}
i+=step;
}
}else{
while(i!=end){
if(_fc[i]==_fd){
return i;
}
i+=step;
}
}
return -1;
};
dojo.lang.indexOf=dojo.lang.find;
dojo.lang.findLast=function(_105,_106,_107){
return dojo.lang.find(_105,_106,_107,true);
};
dojo.lang.lastIndexOf=dojo.lang.findLast;
dojo.lang.inArray=function(_108,_109){
return dojo.lang.find(_108,_109)>-1;
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
if(!it){
return false;
}
if((typeof (it)=="function")&&(it=="[object NodeList]")){
return false;
}
return (it instanceof Function||typeof it=="function");
};
dojo.lang.isString=function(it){
return (typeof it=="string"||it instanceof String);
};
dojo.lang.isAlien=function(it){
if(!it){
return false;
}
return !dojo.lang.isFunction()&&/\{\s*\[native code\]\s*\}/.test(String(it));
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
dojo.provide("dojo.lang");
dojo.deprecated("dojo.lang","replaced by dojo.lang.common","0.5");
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
catch(E){
}
}else{
return wh&&!isNaN(wh.nodeType);
}
};
dojo.dom.getUniqueId=function(){
var _114=dojo.doc();
do{
var id="dj_unique_"+(++arguments.callee._idIncrement);
}while(_114.getElementById(id));
return id;
};
dojo.dom.getUniqueId._idIncrement=0;
dojo.dom.firstElement=dojo.dom.getFirstChildElement=function(_116,_117){
var node=_116.firstChild;
while(node&&node.nodeType!=dojo.dom.ELEMENT_NODE){
node=node.nextSibling;
}
if(_117&&node&&node.tagName&&node.tagName.toLowerCase()!=_117.toLowerCase()){
node=dojo.dom.nextElement(node,_117);
}
return node;
};
dojo.dom.lastElement=dojo.dom.getLastChildElement=function(_119,_11a){
var node=_119.lastChild;
while(node&&node.nodeType!=dojo.dom.ELEMENT_NODE){
node=node.previousSibling;
}
if(_11a&&node&&node.tagName&&node.tagName.toLowerCase()!=_11a.toLowerCase()){
node=dojo.dom.prevElement(node,_11a);
}
return node;
};
dojo.dom.nextElement=dojo.dom.getNextSiblingElement=function(node,_11d){
if(!node){
return null;
}
do{
node=node.nextSibling;
}while(node&&node.nodeType!=dojo.dom.ELEMENT_NODE);
if(node&&_11d&&_11d.toLowerCase()!=node.tagName.toLowerCase()){
return dojo.dom.nextElement(node,_11d);
}
return node;
};
dojo.dom.prevElement=dojo.dom.getPreviousSiblingElement=function(node,_11f){
if(!node){
return null;
}
if(_11f){
_11f=_11f.toLowerCase();
}
do{
node=node.previousSibling;
}while(node&&node.nodeType!=dojo.dom.ELEMENT_NODE);
if(node&&_11f&&_11f.toLowerCase()!=node.tagName.toLowerCase()){
return dojo.dom.prevElement(node,_11f);
}
return node;
};
dojo.dom.moveChildren=function(_120,_121,trim){
var _123=0;
if(trim){
while(_120.hasChildNodes()&&_120.firstChild.nodeType==dojo.dom.TEXT_NODE){
_120.removeChild(_120.firstChild);
}
while(_120.hasChildNodes()&&_120.lastChild.nodeType==dojo.dom.TEXT_NODE){
_120.removeChild(_120.lastChild);
}
}
while(_120.hasChildNodes()){
_121.appendChild(_120.firstChild);
_123++;
}
return _123;
};
dojo.dom.copyChildren=function(_124,_125,trim){
var _127=_124.cloneNode(true);
return this.moveChildren(_127,_125,trim);
};
dojo.dom.removeChildren=function(node){
var _129=node.childNodes.length;
while(node.hasChildNodes()){
node.removeChild(node.firstChild);
}
return _129;
};
dojo.dom.replaceChildren=function(node,_12b){
dojo.dom.removeChildren(node);
node.appendChild(_12b);
};
dojo.dom.removeNode=function(node){
if(node&&node.parentNode){
return node.parentNode.removeChild(node);
}
};
dojo.dom.getAncestors=function(node,_12e,_12f){
var _130=[];
var _131=(_12e&&(_12e instanceof Function||typeof _12e=="function"));
while(node){
if(!_131||_12e(node)){
_130.push(node);
}
if(_12f&&_130.length>0){
return _130[0];
}
node=node.parentNode;
}
if(_12f){
return null;
}
return _130;
};
dojo.dom.getAncestorsByTag=function(node,tag,_134){
tag=tag.toLowerCase();
return dojo.dom.getAncestors(node,function(el){
return ((el.tagName)&&(el.tagName.toLowerCase()==tag));
},_134);
};
dojo.dom.getFirstAncestorByTag=function(node,tag){
return dojo.dom.getAncestorsByTag(node,tag,true);
};
dojo.dom.isDescendantOf=function(node,_139,_13a){
if(_13a&&node){
node=node.parentNode;
}
while(node){
if(node==_139){
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
var _13d=dojo.doc();
if(!dj_undef("ActiveXObject")){
var _13e=["MSXML2","Microsoft","MSXML","MSXML3"];
for(var i=0;i<_13e.length;i++){
try{
doc=new ActiveXObject(_13e[i]+".XMLDOM");
}
catch(e){
}
if(doc){
break;
}
}
}else{
if((_13d.implementation)&&(_13d.implementation.createDocument)){
doc=_13d.implementation.createDocument("","",null);
}
}
return doc;
};
dojo.dom.createDocumentFromText=function(str,_141){
if(!_141){
_141="text/xml";
}
if(!dj_undef("DOMParser")){
var _142=new DOMParser();
return _142.parseFromString(str,_141);
}else{
if(!dj_undef("ActiveXObject")){
var _143=dojo.dom.createDocument();
if(_143){
_143.async=false;
_143.loadXML(str);
return _143;
}else{
dojo.debug("toXml didn't work?");
}
}else{
var _144=dojo.doc();
if(_144.createElement){
var tmp=_144.createElement("xml");
tmp.innerHTML=str;
if(_144.implementation&&_144.implementation.createDocument){
var _146=_144.implementation.createDocument("foo","",null);
for(var i=0;i<tmp.childNodes.length;i++){
_146.importNode(tmp.childNodes.item(i),true);
}
return _146;
}
return ((tmp.document)&&(tmp.document.firstChild?tmp.document.firstChild:tmp));
}
}
}
return null;
};
dojo.dom.prependChild=function(node,_149){
if(_149.firstChild){
_149.insertBefore(node,_149.firstChild);
}else{
_149.appendChild(node);
}
return true;
};
dojo.dom.insertBefore=function(node,ref,_14c){
if(_14c!=true&&(node===ref||node.nextSibling===ref)){
return false;
}
var _14d=ref.parentNode;
_14d.insertBefore(node,ref);
return true;
};
dojo.dom.insertAfter=function(node,ref,_150){
var pn=ref.parentNode;
if(ref==pn.lastChild){
if((_150!=true)&&(node===ref)){
return false;
}
pn.appendChild(node);
}else{
return this.insertBefore(node,ref.nextSibling,_150);
}
return true;
};
dojo.dom.insertAtPosition=function(node,ref,_154){
if((!node)||(!ref)||(!_154)){
return false;
}
switch(_154.toLowerCase()){
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
dojo.dom.insertAtIndex=function(node,_156,_157){
var _158=_156.childNodes;
if(!_158.length){
_156.appendChild(node);
return true;
}
var _159=null;
for(var i=0;i<_158.length;i++){
var _15b=_158.item(i)["getAttribute"]?parseInt(_158.item(i).getAttribute("dojoinsertionindex")):-1;
if(_15b<_157){
_159=_158.item(i);
}
}
if(_159){
return dojo.dom.insertAfter(node,_159);
}else{
return dojo.dom.insertBefore(node,_158.item(0));
}
};
dojo.dom.textContent=function(node,text){
if(arguments.length>1){
var _15e=dojo.doc();
dojo.dom.replaceChildren(node,_15e.createTextNode(text));
return text;
}else{
if(node.textContent!=undefined){
return node.textContent;
}
var _15f="";
if(node==null){
return _15f;
}
for(var i=0;i<node.childNodes.length;i++){
switch(node.childNodes[i].nodeType){
case 1:
case 5:
_15f+=dojo.dom.textContent(node.childNodes[i]);
break;
case 3:
case 2:
case 4:
_15f+=node.childNodes[i].nodeValue;
break;
default:
break;
}
}
return _15f;
}
};
dojo.dom.hasParent=function(node){
return node&&node.parentNode&&dojo.dom.isNode(node.parentNode);
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
dojo.dom.setAttributeNS=function(elem,_165,_166,_167){
if(elem==null||((elem==undefined)&&(typeof elem=="undefined"))){
dojo.raise("No element given to dojo.dom.setAttributeNS");
}
if(!((elem.setAttributeNS==undefined)&&(typeof elem.setAttributeNS=="undefined"))){
elem.setAttributeNS(_165,_166,_167);
}else{
var _168=elem.ownerDocument;
var _169=_168.createNode(2,_166,_165);
_169.nodeValue=_167;
elem.setAttributeNode(_169);
}
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
var _16c=dojo.global();
var _16d=dojo.doc();
var w=0;
var h=0;
if(dojo.render.html.mozilla){
w=_16d.documentElement.clientWidth;
h=_16c.innerHeight;
}else{
if(!dojo.render.html.opera&&_16c.innerWidth){
w=_16c.innerWidth;
h=_16c.innerHeight;
}else{
if(!dojo.render.html.opera&&dojo.exists(_16d,"documentElement.clientWidth")){
var w2=_16d.documentElement.clientWidth;
if(!w||w2&&w2<w){
w=w2;
}
h=_16d.documentElement.clientHeight;
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
var _171=dojo.global();
var _172=dojo.doc();
var top=_171.pageYOffset||_172.documentElement.scrollTop||dojo.body().scrollTop||0;
var left=_171.pageXOffset||_172.documentElement.scrollLeft||dojo.body().scrollLeft||0;
return {top:top,left:left,offset:{x:left,y:top}};
};
dojo.html.getParentByType=function(node,type){
var _177=dojo.doc();
var _178=dojo.byId(node);
type=type.toLowerCase();
while((_178)&&(_178.nodeName.toLowerCase()!=type)){
if(_178==(_177["body"]||_177["documentElement"])){
return null;
}
_178=_178.parentNode;
}
return _178;
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
var _180={x:0,y:0};
if(e.pageX||e.pageY){
_180.x=e.pageX;
_180.y=e.pageY;
}else{
var de=dojo.doc().documentElement;
var db=dojo.body();
_180.x=e.clientX+((de||db)["scrollLeft"])-((de||db)["clientLeft"]);
_180.y=e.clientY+((de||db)["scrollTop"])-((de||db)["clientTop"]);
}
return _180;
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
var _185=dojo.doc().createElement("script");
_185.src="javascript:'dojo.html.createExternalElement=function(doc, tag){ return doc.createElement(tag); }'";
dojo.doc().getElementsByTagName("head")[0].appendChild(_185);
})();
}
}else{
dojo.html.createExternalElement=function(doc,tag){
return doc.createElement(tag);
};
}
dojo.html._callDeprecated=function(_188,_189,args,_18b,_18c){
dojo.deprecated("dojo.html."+_188,"replaced by dojo.html."+_189+"("+(_18b?"node, {"+_18b+": "+_18b+"}":"")+")"+(_18c?"."+_18c:""),"0.5");
var _18d=[];
if(_18b){
var _18e={};
_18e[_18b]=args[1];
_18d.push(args[0]);
_18d.push(_18e);
}else{
_18d=args;
}
var ret=dojo.html[_189].apply(dojo.html,args);
if(_18c){
return ret[_18c];
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
this.moduleUri=function(_191,uri){
var loc=dojo.hostenv.getModulePrefix(_191);
if(!loc){
return null;
}
if(loc.lastIndexOf("/")!=loc.length-1){
loc+="/";
}
return new dojo.uri.Uri(dojo.hostenv.getBaseScriptUri()+loc,uri);
};
this.Uri=function(){
var uri=arguments[0];
for(var i=1;i<arguments.length;i++){
if(!arguments[i]){
continue;
}
var _196=new dojo.uri.Uri(arguments[i].toString());
var _197=new dojo.uri.Uri(uri.toString());
if((_196.path=="")&&(_196.scheme==null)&&(_196.authority==null)&&(_196.query==null)){
if(_196.fragment!=null){
_197.fragment=_196.fragment;
}
_196=_197;
}else{
if(_196.scheme==null){
_196.scheme=_197.scheme;
if(_196.authority==null){
_196.authority=_197.authority;
if(_196.path.charAt(0)!="/"){
var path=_197.path.substring(0,_197.path.lastIndexOf("/")+1)+_196.path;
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
_196.path=segs.join("/");
}
}
}
}
uri="";
if(_196.scheme!=null){
uri+=_196.scheme+":";
}
if(_196.authority!=null){
uri+="//"+_196.authority;
}
uri+=_196.path;
if(_196.query!=null){
uri+="?"+_196.query;
}
if(_196.fragment!=null){
uri+="#"+_196.fragment;
}
}
this.uri=uri.toString();
var _19b="^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\\?([^#]*))?(#(.*))?$";
var r=this.uri.match(new RegExp(_19b));
this.scheme=r[2]||(r[1]?"":null);
this.authority=r[4]||(r[3]?"":null);
this.path=r[5];
this.query=r[7]||(r[6]?"":null);
this.fragment=r[9]||(r[8]?"":null);
if(this.authority!=null){
_19b="^((([^:]+:)?([^@]+))@)?([^:]*)(:([0-9]+))?$";
r=this.authority.match(new RegExp(_19b));
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
dojo.html.hasClass=function(node,_1a2){
return (new RegExp("(^|\\s+)"+_1a2+"(\\s+|$)")).test(dojo.html.getClass(node));
};
dojo.html.prependClass=function(node,_1a4){
_1a4+=" "+dojo.html.getClass(node);
return dojo.html.setClass(node,_1a4);
};
dojo.html.addClass=function(node,_1a6){
if(dojo.html.hasClass(node,_1a6)){
return false;
}
_1a6=(dojo.html.getClass(node)+" "+_1a6).replace(/^\s+|\s+$/g,"");
return dojo.html.setClass(node,_1a6);
};
dojo.html.setClass=function(node,_1a8){
node=dojo.byId(node);
var cs=new String(_1a8);
try{
if(typeof node.className=="string"){
node.className=cs;
}else{
if(node.setAttribute){
node.setAttribute("class",_1a8);
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
dojo.html.removeClass=function(node,_1ab,_1ac){
try{
if(!_1ac){
var _1ad=dojo.html.getClass(node).replace(new RegExp("(^|\\s+)"+_1ab+"(\\s+|$)"),"$1$2");
}else{
var _1ad=dojo.html.getClass(node).replace(_1ab,"");
}
dojo.html.setClass(node,_1ad);
}
catch(e){
dojo.debug("dojo.html.removeClass() failed",e);
}
return true;
};
dojo.html.replaceClass=function(node,_1af,_1b0){
dojo.html.removeClass(node,_1b0);
dojo.html.addClass(node,_1af);
};
dojo.html.classMatchType={ContainsAll:0,ContainsAny:1,IsOnly:2};
dojo.html.getElementsByClass=function(_1b1,_1b2,_1b3,_1b4,_1b5){
_1b5=false;
var _1b6=dojo.doc();
_1b2=dojo.byId(_1b2)||_1b6;
var _1b7=_1b1.split(/\s+/g);
var _1b8=[];
if(_1b4!=1&&_1b4!=2){
_1b4=0;
}
var _1b9=new RegExp("(\\s|^)(("+_1b7.join(")|(")+"))(\\s|$)");
var _1ba=_1b7.join(" ").length;
var _1bb=[];
if(!_1b5&&_1b6.evaluate){
var _1bc=".//"+(_1b3||"*")+"[contains(";
if(_1b4!=dojo.html.classMatchType.ContainsAny){
_1bc+="concat(' ',@class,' '), ' "+_1b7.join(" ') and contains(concat(' ',@class,' '), ' ")+" ')";
if(_1b4==2){
_1bc+=" and string-length(@class)="+_1ba+"]";
}else{
_1bc+="]";
}
}else{
_1bc+="concat(' ',@class,' '), ' "+_1b7.join(" ') or contains(concat(' ',@class,' '), ' ")+" ')]";
}
var _1bd=_1b6.evaluate(_1bc,_1b2,null,XPathResult.ANY_TYPE,null);
var _1be=_1bd.iterateNext();
while(_1be){
try{
_1bb.push(_1be);
_1be=_1bd.iterateNext();
}
catch(e){
break;
}
}
return _1bb;
}else{
if(!_1b3){
_1b3="*";
}
_1bb=_1b2.getElementsByTagName(_1b3);
var node,i=0;
outer:
while(node=_1bb[i++]){
var _1c1=dojo.html.getClasses(node);
if(_1c1.length==0){
continue outer;
}
var _1c2=0;
for(var j=0;j<_1c1.length;j++){
if(_1b9.test(_1c1[j])){
if(_1b4==dojo.html.classMatchType.ContainsAny){
_1b8.push(node);
continue outer;
}else{
_1c2++;
}
}else{
if(_1b4==dojo.html.classMatchType.IsOnly){
continue outer;
}
}
}
if(_1c2==_1b7.length){
if((_1b4==dojo.html.classMatchType.IsOnly)&&(_1c2==_1c1.length)){
_1b8.push(node);
}else{
if(_1b4==dojo.html.classMatchType.ContainsAll){
_1b8.push(node);
}
}
}
}
return _1b8;
}
};
dojo.html.getElementsByClassName=dojo.html.getElementsByClass;
dojo.html.toCamelCase=function(_1c4){
var arr=_1c4.split("-"),cc=arr[0];
for(var i=1;i<arr.length;i++){
cc+=arr[i].charAt(0).toUpperCase()+arr[i].substring(1);
}
return cc;
};
dojo.html.toSelectorCase=function(_1c8){
return _1c8.replace(/([A-Z])/g,"-$1").toLowerCase();
};
dojo.html.getComputedStyle=function(node,_1ca,_1cb){
node=dojo.byId(node);
var _1ca=dojo.html.toSelectorCase(_1ca);
var _1cc=dojo.html.toCamelCase(_1ca);
if(!node||!node.style){
return _1cb;
}else{
if(document.defaultView&&dojo.html.isDescendantOf(node,node.ownerDocument)){
try{
var cs=document.defaultView.getComputedStyle(node,"");
if(cs){
return cs.getPropertyValue(_1ca);
}
}
catch(e){
if(node.style.getPropertyValue){
return node.style.getPropertyValue(_1ca);
}else{
return _1cb;
}
}
}else{
if(node.currentStyle){
return node.currentStyle[_1cc];
}
}
}
if(node.style.getPropertyValue){
return node.style.getPropertyValue(_1ca);
}else{
return _1cb;
}
};
dojo.html.getStyleProperty=function(node,_1cf){
node=dojo.byId(node);
return (node&&node.style?node.style[dojo.html.toCamelCase(_1cf)]:undefined);
};
dojo.html.getStyle=function(node,_1d1){
var _1d2=dojo.html.getStyleProperty(node,_1d1);
return (_1d2?_1d2:dojo.html.getComputedStyle(node,_1d1));
};
dojo.html.setStyle=function(node,_1d4,_1d5){
node=dojo.byId(node);
if(node&&node.style){
var _1d6=dojo.html.toCamelCase(_1d4);
node.style[_1d6]=_1d5;
}
};
dojo.html.setStyleText=function(_1d7,text){
try{
_1d7.style.cssText=text;
}
catch(e){
_1d7.setAttribute("style",text);
}
};
dojo.html.copyStyle=function(_1d9,_1da){
if(!_1da.style.cssText){
_1d9.setAttribute("style",_1da.getAttribute("style"));
}else{
_1d9.style.cssText=_1da.style.cssText;
}
dojo.html.addClass(_1d9,dojo.html.getClass(_1da));
};
dojo.html.getUnitValue=function(node,_1dc,_1dd){
var s=dojo.html.getComputedStyle(node,_1dc);
if((!s)||((s=="auto")&&(_1dd))){
return {value:0,units:"px"};
}
var _1df=s.match(/(\-?[\d.]+)([a-z%]*)/i);
if(!_1df){
return dojo.html.getUnitValue.bad;
}
return {value:Number(_1df[1]),units:_1df[2].toLowerCase()};
};
dojo.html.getUnitValue.bad={value:NaN,units:""};
dojo.html.getPixelValue=function(node,_1e1,_1e2){
var _1e3=dojo.html.getUnitValue(node,_1e1,_1e2);
if(isNaN(_1e3.value)){
return 0;
}
if((_1e3.value)&&(_1e3.units!="px")){
return NaN;
}
return _1e3.value;
};
dojo.html.setPositivePixelValue=function(node,_1e5,_1e6){
if(isNaN(_1e6)){
return false;
}
node.style[_1e5]=Math.max(0,_1e6)+"px";
return true;
};
dojo.html.styleSheet=null;
dojo.html.insertCssRule=function(_1e7,_1e8,_1e9){
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
_1e9=dojo.html.styleSheet.cssRules.length;
}else{
if(dojo.html.styleSheet.rules){
_1e9=dojo.html.styleSheet.rules.length;
}else{
return null;
}
}
}
if(dojo.html.styleSheet.insertRule){
var rule=_1e7+" { "+_1e8+" }";
return dojo.html.styleSheet.insertRule(rule,_1e9);
}else{
if(dojo.html.styleSheet.addRule){
return dojo.html.styleSheet.addRule(_1e7,_1e8,_1e9);
}else{
return null;
}
}
};
dojo.html.removeCssRule=function(_1eb){
if(!dojo.html.styleSheet){
dojo.debug("no stylesheet defined for removing rules");
return false;
}
if(dojo.render.html.ie){
if(!_1eb){
_1eb=dojo.html.styleSheet.rules.length;
dojo.html.styleSheet.removeRule(_1eb);
}
}else{
if(document.styleSheets[0]){
if(!_1eb){
_1eb=dojo.html.styleSheet.cssRules.length;
}
dojo.html.styleSheet.deleteRule(_1eb);
}
}
return true;
};
dojo.html._insertedCssFiles=[];
dojo.html.insertCssFile=function(URI,doc,_1ee,_1ef){
if(!URI){
return;
}
if(!doc){
doc=document;
}
var _1f0=dojo.hostenv.getText(URI,false,_1ef);
if(_1f0===null){
return;
}
_1f0=dojo.html.fixPathsInCssText(_1f0,URI);
if(_1ee){
var idx=-1,node,ent=dojo.html._insertedCssFiles;
for(var i=0;i<ent.length;i++){
if((ent[i].doc==doc)&&(ent[i].cssText==_1f0)){
idx=i;
node=ent[i].nodeRef;
break;
}
}
if(node){
var _1f5=doc.getElementsByTagName("style");
for(var i=0;i<_1f5.length;i++){
if(_1f5[i]==node){
return;
}
}
dojo.html._insertedCssFiles.shift(idx,1);
}
}
var _1f6=dojo.html.insertCssText(_1f0);
dojo.html._insertedCssFiles.push({"doc":doc,"cssText":_1f0,"nodeRef":_1f6});
if(_1f6&&djConfig.isDebug){
_1f6.setAttribute("dbgHref",URI);
}
return _1f6;
};
dojo.html.insertCssText=function(_1f7,doc,URI){
if(!_1f7){
return;
}
if(!doc){
doc=document;
}
if(URI){
_1f7=dojo.html.fixPathsInCssText(_1f7,URI);
}
var _1fa=doc.createElement("style");
_1fa.setAttribute("type","text/css");
var head=doc.getElementsByTagName("head")[0];
if(!head){
dojo.debug("No head tag in document, aborting styles");
return;
}else{
head.appendChild(_1fa);
}
if(_1fa.styleSheet){
_1fa.styleSheet.cssText=_1f7;
}else{
var _1fc=doc.createTextNode(_1f7);
_1fa.appendChild(_1fc);
}
return _1fa;
};
dojo.html.fixPathsInCssText=function(_1fd,URI){
function iefixPathsInCssText(){
var _1ff=/AlphaImageLoader\(src\=['"]([\t\s\w()\/.\\'"-:#=&?~]*)['"]/;
while(_200=_1ff.exec(_1fd)){
url=_200[1].replace(_202,"$2");
if(!_203.exec(url)){
url=(new dojo.uri.Uri(URI,url).toString());
}
str+=_1fd.substring(0,_200.index)+"AlphaImageLoader(src='"+url+"'";
_1fd=_1fd.substr(_200.index+_200[0].length);
}
return str+_1fd;
}
if(!_1fd||!URI){
return;
}
var _200,str="",url="";
var _205=/url\(\s*([\t\s\w()\/.\\'"-:#=&?]+)\s*\)/;
var _203=/(file|https?|ftps?):\/\//;
var _202=/^[\s]*(['"]?)([\w()\/.\\'"-:#=&?]*)\1[\s]*?$/;
if(dojo.render.html.ie55||dojo.render.html.ie60){
_1fd=iefixPathsInCssText();
}
while(_200=_205.exec(_1fd)){
url=_200[1].replace(_202,"$2");
if(!_203.exec(url)){
url=(new dojo.uri.Uri(URI,url).toString());
}
str+=_1fd.substring(0,_200.index)+"url("+url+")";
_1fd=_1fd.substr(_200.index+_200[0].length);
}
return str+_1fd;
};
dojo.html.setActiveStyleSheet=function(_206){
var i=0,a,els=dojo.doc().getElementsByTagName("link");
while(a=els[i++]){
if(a.getAttribute("rel").indexOf("style")!=-1&&a.getAttribute("title")){
a.disabled=true;
if(a.getAttribute("title")==_206){
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
var _212={dj_ie:drh.ie,dj_ie55:drh.ie55,dj_ie6:drh.ie60,dj_ie7:drh.ie70,dj_iequirks:drh.ie&&drh.quirks,dj_opera:drh.opera,dj_opera8:drh.opera&&(Math.floor(dojo.render.version)==8),dj_opera9:drh.opera&&(Math.floor(dojo.render.version)==9),dj_khtml:drh.khtml,dj_safari:drh.safari,dj_gecko:drh.mozilla};
for(var p in _212){
if(_212[p]){
dojo.html.addClass(node,p);
}
}
};
dojo.provide("dojo.html.*");
dojo.provide("dojo.html.display");
dojo.html._toggle=function(node,_215,_216){
node=dojo.byId(node);
_216(node,!_215(node));
return _215(node);
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
dojo.html.setShowing=function(node,_21b){
dojo.html[(_21b?"show":"hide")](node);
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
dojo.html.setDisplay=function(node,_221){
dojo.html.setStyle(node,"display",((_221 instanceof String||typeof _221=="string")?_221:(_221?dojo.html.suggestDisplayByTagName(node):"none")));
};
dojo.html.isDisplayed=function(node){
return (dojo.html.getComputedStyle(node,"display")!="none");
};
dojo.html.toggleDisplay=function(node){
return dojo.html._toggle(node,dojo.html.isDisplayed,dojo.html.setDisplay);
};
dojo.html.setVisibility=function(node,_225){
dojo.html.setStyle(node,"visibility",((_225 instanceof String||typeof _225=="string")?_225:(_225?"visible":"hidden")));
};
dojo.html.isVisible=function(node){
return (dojo.html.getComputedStyle(node,"visibility")!="hidden");
};
dojo.html.toggleVisibility=function(node){
return dojo.html._toggle(node,dojo.html.isVisible,dojo.html.setVisibility);
};
dojo.html.setOpacity=function(node,_229,_22a){
node=dojo.byId(node);
var h=dojo.render.html;
if(!_22a){
if(_229>=1){
if(h.ie){
dojo.html.clearOpacity(node);
return;
}else{
_229=0.999999;
}
}else{
if(_229<0){
_229=0;
}
}
}
if(h.ie){
if(node.nodeName.toLowerCase()=="tr"){
var tds=node.getElementsByTagName("td");
for(var x=0;x<tds.length;x++){
tds[x].style.filter="Alpha(Opacity="+_229*100+")";
}
}
node.style.filter="Alpha(Opacity="+_229*100+")";
}else{
if(h.moz){
node.style.opacity=_229;
node.style.MozOpacity=_229;
}else{
if(h.safari){
node.style.opacity=_229;
node.style.KhtmlOpacity=_229;
}else{
node.style.opacity=_229;
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
dojo.provide("dojo.html.layout");
dojo.html.sumAncestorProperties=function(node,prop){
node=dojo.byId(node);
if(!node){
return 0;
}
var _236=0;
while(node){
if(dojo.html.getComputedStyle(node,"position")=="fixed"){
return 0;
}
var val=node[prop];
if(val){
_236+=val-0;
if(node==dojo.body()){
break;
}
}
node=node.parentNode;
}
return _236;
};
dojo.html.setStyleAttributes=function(node,_239){
node=dojo.byId(node);
var _23a=_239.replace(/(;)?\s*$/,"").split(";");
for(var i=0;i<_23a.length;i++){
var _23c=_23a[i].split(":");
var name=_23c[0].replace(/\s*$/,"").replace(/^\s*/,"").toLowerCase();
var _23e=_23c[1].replace(/\s*$/,"").replace(/^\s*/,"");
switch(name){
case "opacity":
dojo.html.setOpacity(node,_23e);
break;
case "content-height":
dojo.html.setContentBox(node,{height:_23e});
break;
case "content-width":
dojo.html.setContentBox(node,{width:_23e});
break;
case "outer-height":
dojo.html.setMarginBox(node,{height:_23e});
break;
case "outer-width":
dojo.html.setMarginBox(node,{width:_23e});
break;
default:
node.style[dojo.html.toCamelCase(name)]=_23e;
}
}
};
dojo.html.boxSizing={MARGIN_BOX:"margin-box",BORDER_BOX:"border-box",PADDING_BOX:"padding-box",CONTENT_BOX:"content-box"};
dojo.html.getAbsolutePosition=dojo.html.abs=function(node,_240,_241){
node=dojo.byId(node,node.ownerDocument);
var ret={x:0,y:0};
var bs=dojo.html.boxSizing;
if(!_241){
_241=bs.CONTENT_BOX;
}
var _244=2;
var _245;
switch(_241){
case bs.MARGIN_BOX:
_245=3;
break;
case bs.BORDER_BOX:
_245=2;
break;
case bs.PADDING_BOX:
default:
_245=1;
break;
case bs.CONTENT_BOX:
_245=0;
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
_244=1;
try{
var bo=document.getBoxObjectFor(node);
ret.x=bo.x-dojo.html.sumAncestorProperties(node,"scrollLeft");
ret.y=bo.y-dojo.html.sumAncestorProperties(node,"scrollTop");
}
catch(e){
}
}else{
if(node["offsetParent"]){
var _249;
if((h.safari)&&(node.style.getPropertyValue("position")=="absolute")&&(node.parentNode==db)){
_249=db;
}else{
_249=db.parentNode;
}
if(node.parentNode!=db){
var nd=node;
if(dojo.render.html.opera){
nd=db;
}
ret.x-=dojo.html.sumAncestorProperties(nd,"scrollLeft");
ret.y-=dojo.html.sumAncestorProperties(nd,"scrollTop");
}
var _24b=node;
do{
var n=_24b["offsetLeft"];
if(!h.opera||n>0){
ret.x+=isNaN(n)?0:n;
}
var m=_24b["offsetTop"];
ret.y+=isNaN(m)?0:m;
_24b=_24b.offsetParent;
}while((_24b!=_249)&&(_24b!=null));
}else{
if(node["x"]&&node["y"]){
ret.x+=isNaN(node.x)?0:node.x;
ret.y+=isNaN(node.y)?0:node.y;
}
}
}
}
if(_240){
var _24e=dojo.html.getScroll();
ret.y+=_24e.top;
ret.x+=_24e.left;
}
var _24f=[dojo.html.getPaddingExtent,dojo.html.getBorderExtent,dojo.html.getMarginExtent];
if(_244>_245){
for(var i=_245;i<_244;++i){
ret.y+=_24f[i](node,"top");
ret.x+=_24f[i](node,"left");
}
}else{
if(_244<_245){
for(var i=_245;i>_244;--i){
ret.y-=_24f[i-1](node,"top");
ret.x-=_24f[i-1](node,"left");
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
dojo.html._sumPixelValues=function(node,_253,_254){
var _255=0;
for(var x=0;x<_253.length;x++){
_255+=dojo.html.getPixelValue(node,_253[x],_254);
}
return _255;
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
var _262=dojo.html.getBorder(node);
return {width:pad.width+_262.width,height:pad.height+_262.height};
};
dojo.html.getBoxSizing=function(node){
var h=dojo.render.html;
var bs=dojo.html.boxSizing;
if((h.ie)||(h.opera)){
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
var _267=dojo.html.getStyle(node,"-moz-box-sizing");
if(!_267){
_267=dojo.html.getStyle(node,"box-sizing");
}
return (_267?_267:bs.CONTENT_BOX);
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
var _26c=dojo.html.getBorder(node);
return {width:box.width-_26c.width,height:box.height-_26c.height};
};
dojo.html.getContentBox=function(node){
node=dojo.byId(node);
var _26e=dojo.html.getPadBorder(node);
return {width:node.offsetWidth-_26e.width,height:node.offsetHeight-_26e.height};
};
dojo.html.setContentBox=function(node,args){
node=dojo.byId(node);
var _271=0;
var _272=0;
var isbb=dojo.html.isBorderBox(node);
var _274=(isbb?dojo.html.getPadBorder(node):{width:0,height:0});
var ret={};
if(typeof args.width!="undefined"){
_271=args.width+_274.width;
ret.width=dojo.html.setPositivePixelValue(node,"width",_271);
}
if(typeof args.height!="undefined"){
_272=args.height+_274.height;
ret.height=dojo.html.setPositivePixelValue(node,"height",_272);
}
return ret;
};
dojo.html.getMarginBox=function(node){
var _277=dojo.html.getBorderBox(node);
var _278=dojo.html.getMargin(node);
return {width:_277.width+_278.width,height:_277.height+_278.height};
};
dojo.html.setMarginBox=function(node,args){
node=dojo.byId(node);
var _27b=0;
var _27c=0;
var isbb=dojo.html.isBorderBox(node);
var _27e=(!isbb?dojo.html.getPadBorder(node):{width:0,height:0});
var _27f=dojo.html.getMargin(node);
var ret={};
if(typeof args.width!="undefined"){
_27b=args.width-_27e.width;
_27b-=_27f.width;
ret.width=dojo.html.setPositivePixelValue(node,"width",_27b);
}
if(typeof args.height!="undefined"){
_27c=args.height-_27e.height;
_27c-=_27f.height;
ret.height=dojo.html.setPositivePixelValue(node,"height",_27c);
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
dojo.html.toCoordinateObject=dojo.html.toCoordinateArray=function(_284,_285,_286){
if(_284 instanceof Array||typeof _284=="array"){
dojo.deprecated("dojo.html.toCoordinateArray","use dojo.html.toCoordinateObject({left: , top: , width: , height: }) instead","0.5");
while(_284.length<4){
_284.push(0);
}
while(_284.length>4){
_284.pop();
}
var ret={left:_284[0],top:_284[1],width:_284[2],height:_284[3]};
}else{
if(!_284.nodeType&&!(_284 instanceof String||typeof _284=="string")&&("width" in _284||"height" in _284||"left" in _284||"x" in _284||"top" in _284||"y" in _284)){
var ret={left:_284.left||_284.x||0,top:_284.top||_284.y||0,width:_284.width||0,height:_284.height||0};
}else{
var node=dojo.byId(_284);
var pos=dojo.html.abs(node,_285,_286);
var _28a=dojo.html.getMarginBox(node);
var ret={left:pos.left,top:pos.top,width:_28a.width,height:_28a.height};
}
}
ret.x=ret.left;
ret.y=ret.top;
return ret;
};
dojo.html.setMarginBoxWidth=dojo.html.setOuterWidth=function(node,_28c){
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
dojo.html.getTotalOffset=function(node,type,_28f){
return dojo.html._callDeprecated("getTotalOffset","getAbsolutePosition",arguments,null,type);
};
dojo.html.getAbsoluteX=function(node,_291){
return dojo.html._callDeprecated("getAbsoluteX","getAbsolutePosition",arguments,null,"x");
};
dojo.html.getAbsoluteY=function(node,_293){
return dojo.html._callDeprecated("getAbsoluteY","getAbsolutePosition",arguments,null,"y");
};
dojo.html.totalOffsetLeft=function(node,_295){
return dojo.html._callDeprecated("totalOffsetLeft","getAbsolutePosition",arguments,null,"left");
};
dojo.html.totalOffsetTop=function(node,_297){
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
dojo.html.setContentBoxWidth=dojo.html.setContentWidth=function(node,_2a1){
return dojo.html._callDeprecated("setContentBoxWidth","setContentBox",arguments,"width");
};
dojo.html.setContentBoxHeight=dojo.html.setContentHeight=function(node,_2a3){
return dojo.html._callDeprecated("setContentBoxHeight","setContentBox",arguments,"height");
};
dojo.provide("dojo.html.util");
dojo.html.getElementWindow=function(_2a4){
return dojo.html.getDocumentWindow(_2a4.ownerDocument);
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
var _2ac=dojo.html.getCursorPosition(e);
with(dojo.html){
var _2ad=getAbsolutePosition(node,true);
var bb=getBorderBox(node);
var _2af=_2ad.x+(bb.width/2);
var _2b0=_2ad.y+(bb.height/2);
}
with(dojo.html.gravity){
return ((_2ac.x<_2af?WEST:EAST)|(_2ac.y<_2b0?NORTH:SOUTH));
}
};
dojo.html.gravity.NORTH=1;
dojo.html.gravity.SOUTH=1<<1;
dojo.html.gravity.EAST=1<<2;
dojo.html.gravity.WEST=1<<3;
dojo.html.overElement=function(_2b1,e){
_2b1=dojo.byId(_2b1);
var _2b3=dojo.html.getCursorPosition(e);
var bb=dojo.html.getBorderBox(_2b1);
var _2b5=dojo.html.getAbsolutePosition(_2b1,true,dojo.html.boxSizing.BORDER_BOX);
var top=_2b5.y;
var _2b7=top+bb.height;
var left=_2b5.x;
var _2b9=left+bb.width;
return (_2b3.x>=left&&_2b3.x<=_2b9&&_2b3.y>=top&&_2b3.y<=_2b7);
};
dojo.html.renderedTextContent=function(node){
node=dojo.byId(node);
var _2bb="";
if(node==null){
return _2bb;
}
for(var i=0;i<node.childNodes.length;i++){
switch(node.childNodes[i].nodeType){
case 1:
case 5:
var _2bd="unknown";
try{
_2bd=dojo.html.getStyle(node.childNodes[i],"display");
}
catch(E){
}
switch(_2bd){
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
_2bb+="\n";
_2bb+=dojo.html.renderedTextContent(node.childNodes[i]);
_2bb+="\n";
break;
case "none":
break;
default:
if(node.childNodes[i].tagName&&node.childNodes[i].tagName.toLowerCase()=="br"){
_2bb+="\n";
}else{
_2bb+=dojo.html.renderedTextContent(node.childNodes[i]);
}
break;
}
break;
case 3:
case 2:
case 4:
var text=node.childNodes[i].nodeValue;
var _2bf="unknown";
try{
_2bf=dojo.html.getStyle(node,"text-transform");
}
catch(E){
}
switch(_2bf){
case "capitalize":
var _2c0=text.split(" ");
for(var i=0;i<_2c0.length;i++){
_2c0[i]=_2c0[i].charAt(0).toUpperCase()+_2c0[i].substring(1);
}
text=_2c0.join(" ");
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
switch(_2bf){
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
if(/\s$/.test(_2bb)){
text.replace(/^\s/,"");
}
break;
}
_2bb+=text;
break;
default:
break;
}
}
return _2bb;
};
dojo.html.createNodesFromText=function(txt,trim){
if(trim){
txt=txt.replace(/^\s+|\s+$/g,"");
}
var tn=dojo.doc().createElement("div");
tn.style.visibility="hidden";
dojo.body().appendChild(tn);
var _2c4="none";
if((/^<t[dh][\s\r\n>]/i).test(txt.replace(/^\s+/))){
txt="<table><tbody><tr>"+txt+"</tr></tbody></table>";
_2c4="cell";
}else{
if((/^<tr[\s\r\n>]/i).test(txt.replace(/^\s+/))){
txt="<table><tbody>"+txt+"</tbody></table>";
_2c4="row";
}else{
if((/^<(thead|tbody|tfoot)[\s\r\n>]/i).test(txt.replace(/^\s+/))){
txt="<table>"+txt+"</table>";
_2c4="section";
}
}
}
tn.innerHTML=txt;
if(tn["normalize"]){
tn.normalize();
}
var _2c5=null;
switch(_2c4){
case "cell":
_2c5=tn.getElementsByTagName("tr")[0];
break;
case "row":
_2c5=tn.getElementsByTagName("tbody")[0];
break;
case "section":
_2c5=tn.getElementsByTagName("table")[0];
break;
default:
_2c5=tn;
break;
}
var _2c6=[];
for(var x=0;x<_2c5.childNodes.length;x++){
_2c6.push(_2c5.childNodes[x].cloneNode(true));
}
tn.style.display="none";
dojo.body().removeChild(tn);
return _2c6;
};
dojo.html.placeOnScreen=function(node,_2c9,_2ca,_2cb,_2cc,_2cd,_2ce){
if(_2c9 instanceof Array||typeof _2c9=="array"){
_2ce=_2cd;
_2cd=_2cc;
_2cc=_2cb;
_2cb=_2ca;
_2ca=_2c9[1];
_2c9=_2c9[0];
}
if(_2cd instanceof String||typeof _2cd=="string"){
_2cd=_2cd.split(",");
}
if(!isNaN(_2cb)){
_2cb=[Number(_2cb),Number(_2cb)];
}else{
if(!(_2cb instanceof Array||typeof _2cb=="array")){
_2cb=[0,0];
}
}
var _2cf=dojo.html.getScroll().offset;
var view=dojo.html.getViewport();
node=dojo.byId(node);
var _2d1=node.style.display;
node.style.display="";
var bb=dojo.html.getBorderBox(node);
var w=bb.width;
var h=bb.height;
node.style.display=_2d1;
if(!(_2cd instanceof Array||typeof _2cd=="array")){
_2cd=["TL"];
}
var _2d5,_2d6,_2d7=Infinity,_2d8;
for(var _2d9=0;_2d9<_2cd.length;++_2d9){
var _2da=_2cd[_2d9];
var _2db=true;
var tryX=_2c9-(_2da.charAt(1)=="L"?0:w)+_2cb[0]*(_2da.charAt(1)=="L"?1:-1);
var tryY=_2ca-(_2da.charAt(0)=="T"?0:h)+_2cb[1]*(_2da.charAt(0)=="T"?1:-1);
if(_2cc){
tryX-=_2cf.x;
tryY-=_2cf.y;
}
if(tryX<0){
tryX=0;
_2db=false;
}
if(tryY<0){
tryY=0;
_2db=false;
}
var x=tryX+w;
if(x>view.width){
x=view.width-w;
_2db=false;
}else{
x=tryX;
}
x=Math.max(_2cb[0],x)+_2cf.x;
var y=tryY+h;
if(y>view.height){
y=view.height-h;
_2db=false;
}else{
y=tryY;
}
y=Math.max(_2cb[1],y)+_2cf.y;
if(_2db){
_2d5=x;
_2d6=y;
_2d7=0;
_2d8=_2da;
break;
}else{
var dist=Math.pow(x-tryX-_2cf.x,2)+Math.pow(y-tryY-_2cf.y,2);
if(_2d7>dist){
_2d7=dist;
_2d5=x;
_2d6=y;
_2d8=_2da;
}
}
}
if(!_2ce){
node.style.left=_2d5+"px";
node.style.top=_2d6+"px";
}
return {left:_2d5,top:_2d6,x:_2d5,y:_2d6,dist:_2d7,corner:_2d8};
};
dojo.html.placeOnScreenPoint=function(node,_2e2,_2e3,_2e4,_2e5){
dojo.deprecated("dojo.html.placeOnScreenPoint","use dojo.html.placeOnScreen() instead","0.5");
return dojo.html.placeOnScreen(node,_2e2,_2e3,_2e4,_2e5,["TL","TR","BL","BR"]);
};
dojo.html.placeOnScreenAroundElement=function(node,_2e7,_2e8,_2e9,_2ea,_2eb){
var best,_2ed=Infinity;
_2e7=dojo.byId(_2e7);
var _2ee=_2e7.style.display;
_2e7.style.display="";
var mb=dojo.html.getElementBox(_2e7,_2e9);
var _2f0=mb.width;
var _2f1=mb.height;
var _2f2=dojo.html.getAbsolutePosition(_2e7,true,_2e9);
_2e7.style.display=_2ee;
for(var _2f3 in _2ea){
var pos,_2f5,_2f6;
var _2f7=_2ea[_2f3];
_2f5=_2f2.x+(_2f3.charAt(1)=="L"?0:_2f0);
_2f6=_2f2.y+(_2f3.charAt(0)=="T"?0:_2f1);
pos=dojo.html.placeOnScreen(node,_2f5,_2f6,_2e8,true,_2f7,true);
if(pos.dist==0){
best=pos;
break;
}else{
if(_2ed>pos.dist){
_2ed=pos.dist;
best=pos;
}
}
}
if(!_2eb){
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
if(dojo.html.getBorderBox(node.parentNode).height<node.parentNode.scrollHeight){
node.scrollIntoView(false);
}
}else{
if(dojo.render.html.mozilla){
node.scrollIntoView(false);
}else{
var _2f9=node.parentNode;
var _2fa=_2f9.scrollTop+dojo.html.getBorderBox(_2f9).height;
var _2fb=node.offsetTop+dojo.html.getMarginBox(node).height;
if(_2fa<_2fb){
_2f9.scrollTop+=(_2fb-_2fa);
}else{
if(_2f9.scrollTop>node.offsetTop){
_2f9.scrollTop-=(_2f9.scrollTop-node.offsetTop);
}
}
}
}
};
dojo.provide("dojo.lang.array");
dojo.lang.has=function(obj,name){
try{
return typeof obj[name]!="undefined";
}
catch(e){
return false;
}
};
dojo.lang.isEmpty=function(obj){
if(dojo.lang.isObject(obj)){
var tmp={};
var _300=0;
for(var x in obj){
if(obj[x]&&(!tmp[x])){
_300++;
break;
}
}
return _300==0;
}else{
if(dojo.lang.isArrayLike(obj)||dojo.lang.isString(obj)){
return obj.length==0;
}
}
};
dojo.lang.map=function(arr,obj,_304){
var _305=dojo.lang.isString(arr);
if(_305){
arr=arr.split("");
}
if(dojo.lang.isFunction(obj)&&(!_304)){
_304=obj;
obj=dj_global;
}else{
if(dojo.lang.isFunction(obj)&&_304){
var _306=obj;
obj=_304;
_304=_306;
}
}
if(Array.map){
var _307=Array.map(arr,_304,obj);
}else{
var _307=[];
for(var i=0;i<arr.length;++i){
_307.push(_304.call(obj,arr[i]));
}
}
if(_305){
return _307.join("");
}else{
return _307;
}
};
dojo.lang.reduce=function(arr,_30a,obj,_30c){
var _30d=_30a;
var ob=obj?obj:dj_global;
dojo.lang.map(arr,function(val){
_30d=_30c.call(ob,_30d,val);
});
return _30d;
};
dojo.lang.forEach=function(_310,_311,_312){
if(dojo.lang.isString(_310)){
_310=_310.split("");
}
if(Array.forEach){
Array.forEach(_310,_311,_312);
}else{
if(!_312){
_312=dj_global;
}
for(var i=0,l=_310.length;i<l;i++){
_311.call(_312,_310[i],i,_310);
}
}
};
dojo.lang._everyOrSome=function(_315,arr,_317,_318){
if(dojo.lang.isString(arr)){
arr=arr.split("");
}
if(Array.every){
return Array[_315?"every":"some"](arr,_317,_318);
}else{
if(!_318){
_318=dj_global;
}
for(var i=0,l=arr.length;i<l;i++){
var _31b=_317.call(_318,arr[i],i,arr);
if(_315&&!_31b){
return false;
}else{
if((!_315)&&(_31b)){
return true;
}
}
}
return Boolean(_315);
}
};
dojo.lang.every=function(arr,_31d,_31e){
return this._everyOrSome(true,arr,_31d,_31e);
};
dojo.lang.some=function(arr,_320,_321){
return this._everyOrSome(false,arr,_320,_321);
};
dojo.lang.filter=function(arr,_323,_324){
var _325=dojo.lang.isString(arr);
if(_325){
arr=arr.split("");
}
var _326;
if(Array.filter){
_326=Array.filter(arr,_323,_324);
}else{
if(!_324){
if(arguments.length>=3){
dojo.raise("thisObject doesn't exist!");
}
_324=dj_global;
}
_326=[];
for(var i=0;i<arr.length;i++){
if(_323.call(_324,arr[i],i,arr)){
_326.push(arr[i]);
}
}
}
if(_325){
return _326.join("");
}else{
return _326;
}
};
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
dojo.lang.toArray=function(_32b,_32c){
var _32d=[];
for(var i=_32c||0;i<_32b.length;i++){
_32d.push(_32b[i]);
}
return _32d;
};
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
dojo.extend(dojo.gfx.color.Color,{toRgb:function(_335){
if(_335){
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
},blend:function(_336,_337){
var rgb=null;
if(dojo.lang.isArray(_336)){
rgb=_336;
}else{
if(_336 instanceof dojo.gfx.color.Color){
rgb=_336.toRgb();
}else{
rgb=new dojo.gfx.color.Color(_336).toRgb();
}
}
return dojo.gfx.color.blend(this.toRgb(),rgb,_337);
}});
dojo.gfx.color.named={white:[255,255,255],black:[0,0,0],red:[255,0,0],green:[0,255,0],lime:[0,255,0],blue:[0,0,255],navy:[0,0,128],gray:[128,128,128],silver:[192,192,192]};
dojo.gfx.color.blend=function(a,b,_33b){
if(typeof a=="string"){
return dojo.gfx.color.blendHex(a,b,_33b);
}
if(!_33b){
_33b=0;
}
_33b=Math.min(Math.max(-1,_33b),1);
_33b=((_33b+1)/2);
var c=[];
for(var x=0;x<3;x++){
c[x]=parseInt(b[x]+((a[x]-b[x])*_33b));
}
return c;
};
dojo.gfx.color.blendHex=function(a,b,_340){
return dojo.gfx.color.rgb2hex(dojo.gfx.color.blend(dojo.gfx.color.hex2rgb(a),dojo.gfx.color.hex2rgb(b),_340));
};
dojo.gfx.color.extractRGB=function(_341){
var hex="0123456789abcdef";
_341=_341.toLowerCase();
if(_341.indexOf("rgb")==0){
var _343=_341.match(/rgba*\((\d+), *(\d+), *(\d+)/i);
var ret=_343.splice(1,3);
return ret;
}else{
var _345=dojo.gfx.color.hex2rgb(_341);
if(_345){
return _345;
}else{
return dojo.gfx.color.named[_341]||[255,255,255];
}
}
};
dojo.gfx.color.hex2rgb=function(hex){
var _347="0123456789ABCDEF";
var rgb=new Array(3);
if(hex.indexOf("#")==0){
hex=hex.substring(1);
}
hex=hex.toUpperCase();
if(hex.replace(new RegExp("["+_347+"]","g"),"")!=""){
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
rgb[i]=_347.indexOf(rgb[i].charAt(0))*16+_347.indexOf(rgb[i].charAt(1));
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
dojo.provide("dojo.lang.func");
dojo.lang.hitch=function(_350,_351){
var fcn=(dojo.lang.isString(_351)?_350[_351]:_351)||function(){
};
return function(){
return fcn.apply(_350,arguments);
};
};
dojo.lang.anonCtr=0;
dojo.lang.anon={};
dojo.lang.nameAnonFunc=function(_353,_354,_355){
var nso=(_354||dojo.lang.anon);
if((_355)||((dj_global["djConfig"])&&(djConfig["slowAnonFuncLookups"]==true))){
for(var x in nso){
try{
if(nso[x]===_353){
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
nso[ret]=_353;
return ret;
};
dojo.lang.forward=function(_359){
return function(){
return this[_359].apply(this,arguments);
};
};
dojo.lang.curry=function(ns,func){
var _35c=[];
ns=ns||dj_global;
if(dojo.lang.isString(func)){
func=ns[func];
}
for(var x=2;x<arguments.length;x++){
_35c.push(arguments[x]);
}
var _35e=(func["__preJoinArity"]||func.length)-_35c.length;
function gather(_35f,_360,_361){
var _362=_361;
var _363=_360.slice(0);
for(var x=0;x<_35f.length;x++){
_363.push(_35f[x]);
}
_361=_361-_35f.length;
if(_361<=0){
var res=func.apply(ns,_363);
_361=_362;
return res;
}else{
return function(){
return gather(arguments,_363,_361);
};
}
}
return gather([],_35c,_35e);
};
dojo.lang.curryArguments=function(ns,func,args,_369){
var _36a=[];
var x=_369||0;
for(x=_369;x<args.length;x++){
_36a.push(args[x]);
}
return dojo.lang.curry.apply(dojo.lang,[ns,func].concat(_36a));
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
dojo.lang.delayThese=function(farr,cb,_370,_371){
if(!farr.length){
if(typeof _371=="function"){
_371();
}
return;
}
if((typeof _370=="undefined")&&(typeof cb=="number")){
_370=cb;
cb=function(){
};
}else{
if(!cb){
cb=function(){
};
if(!_370){
_370=0;
}
}
}
setTimeout(function(){
(farr.shift())();
cb();
dojo.lang.delayThese(farr,cb,_370,_371);
},_370);
};
dojo.provide("dojo.lfx.Animation");
dojo.lfx.Line=function(_372,end){
this.start=_372;
this.end=end;
if(dojo.lang.isArray(_372)){
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
var diff=end-_372;
this.getValue=function(n){
return (diff*n)+this.start;
};
}
};
dojo.lfx.easeDefault=function(n){
if(dojo.render.html.khtml){
return (parseFloat("0.5")+((Math.sin((n+parseFloat("1.5"))*Math.PI))/2));
}else{
return (0.5+((Math.sin((n+1.5)*Math.PI))/2));
}
};
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
dojo.lang.extend(dojo.lfx.IAnimation,{curve:null,duration:1000,easing:null,repeatCount:0,rate:25,handler:null,beforeBegin:null,onBegin:null,onAnimate:null,onEnd:null,onPlay:null,onPause:null,onStop:null,play:null,pause:null,stop:null,connect:function(evt,_381,_382){
if(!_382){
_382=_381;
_381=this;
}
_382=dojo.lang.hitch(_381,_382);
var _383=this[evt]||function(){
};
this[evt]=function(){
var ret=_383.apply(this,arguments);
_382.apply(this,arguments);
return ret;
};
return this;
},fire:function(evt,args){
if(this[evt]){
this[evt].apply(this,(args||[]));
}
return this;
},repeat:function(_387){
this.repeatCount=_387;
return this;
},_active:false,_paused:false});
dojo.lfx.Animation=function(_388,_389,_38a,_38b,_38c,rate){
dojo.lfx.IAnimation.call(this);
if(dojo.lang.isNumber(_388)||(!_388&&_389.getValue)){
rate=_38c;
_38c=_38b;
_38b=_38a;
_38a=_389;
_389=_388;
_388=null;
}else{
if(_388.getValue||dojo.lang.isArray(_388)){
rate=_38b;
_38c=_38a;
_38b=_389;
_38a=_388;
_389=null;
_388=null;
}
}
if(dojo.lang.isArray(_38a)){
this.curve=new dojo.lfx.Line(_38a[0],_38a[1]);
}else{
this.curve=_38a;
}
if(_389!=null&&_389>0){
this.duration=_389;
}
if(_38c){
this.repeatCount=_38c;
}
if(rate){
this.rate=rate;
}
if(_388){
dojo.lang.forEach(["handler","beforeBegin","onBegin","onEnd","onPlay","onStop","onAnimate"],function(item){
if(_388[item]){
this.connect(item,_388[item]);
}
},this);
}
if(_38b&&dojo.lang.isFunction(_38b)){
this.easing=_38b;
}
};
dojo.inherits(dojo.lfx.Animation,dojo.lfx.IAnimation);
dojo.lang.extend(dojo.lfx.Animation,{_startTime:null,_endTime:null,_timer:null,_percent:0,_startRepeatCount:0,play:function(_38f,_390){
if(_390){
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
if(_38f>0){
setTimeout(dojo.lang.hitch(this,function(){
this.play(null,_390);
}),_38f);
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
var _392=this.curve.getValue(step);
if(this._percent==0){
if(!this._startRepeatCount){
this._startRepeatCount=this.repeatCount;
}
this.fire("handler",["begin",_392]);
this.fire("onBegin",[_392]);
}
this.fire("handler",["play",_392]);
this.fire("onPlay",[_392]);
this._cycle();
return this;
},pause:function(){
clearTimeout(this._timer);
if(!this._active){
return this;
}
this._paused=true;
var _393=this.curve.getValue(this._percent/100);
this.fire("handler",["pause",_393]);
this.fire("onPause",[_393]);
return this;
},gotoPercent:function(pct,_395){
clearTimeout(this._timer);
this._active=true;
this._paused=true;
this._percent=pct;
if(_395){
this.play();
}
return this;
},stop:function(_396){
clearTimeout(this._timer);
var step=this._percent/100;
if(_396){
step=1;
}
var _398=this.curve.getValue(step);
this.fire("handler",["stop",_398]);
this.fire("onStop",[_398]);
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
var _39b=this.curve.getValue(step);
this.fire("handler",["animate",_39b]);
this.fire("onAnimate",[_39b]);
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
dojo.lfx.Combine=function(_39c){
dojo.lfx.IAnimation.call(this);
this._anims=[];
this._animsEnded=0;
var _39d=arguments;
if(_39d.length==1&&(dojo.lang.isArray(_39d[0])||dojo.lang.isArrayLike(_39d[0]))){
_39d=_39d[0];
}
dojo.lang.forEach(_39d,function(anim){
this._anims.push(anim);
anim.connect("onEnd",dojo.lang.hitch(this,"_onAnimsEnded"));
},this);
};
dojo.inherits(dojo.lfx.Combine,dojo.lfx.IAnimation);
dojo.lang.extend(dojo.lfx.Combine,{_animsEnded:0,play:function(_39f,_3a0){
if(!this._anims.length){
return this;
}
this.fire("beforeBegin");
if(_39f>0){
setTimeout(dojo.lang.hitch(this,function(){
this.play(null,_3a0);
}),_39f);
return this;
}
if(_3a0||this._anims[0].percent==0){
this.fire("onBegin");
}
this.fire("onPlay");
this._animsCall("play",null,_3a0);
return this;
},pause:function(){
this.fire("onPause");
this._animsCall("pause");
return this;
},stop:function(_3a1){
this.fire("onStop");
this._animsCall("stop",_3a1);
return this;
},_onAnimsEnded:function(){
this._animsEnded++;
if(this._animsEnded>=this._anims.length){
this.fire("onEnd");
}
return this;
},_animsCall:function(_3a2){
var args=[];
if(arguments.length>1){
for(var i=1;i<arguments.length;i++){
args.push(arguments[i]);
}
}
var _3a5=this;
dojo.lang.forEach(this._anims,function(anim){
anim[_3a2](args);
},_3a5);
return this;
}});
dojo.lfx.Chain=function(_3a7){
dojo.lfx.IAnimation.call(this);
this._anims=[];
this._currAnim=-1;
var _3a8=arguments;
if(_3a8.length==1&&(dojo.lang.isArray(_3a8[0])||dojo.lang.isArrayLike(_3a8[0]))){
_3a8=_3a8[0];
}
var _3a9=this;
dojo.lang.forEach(_3a8,function(anim,i,_3ac){
this._anims.push(anim);
if(i<_3ac.length-1){
anim.connect("onEnd",dojo.lang.hitch(this,"_playNext"));
}else{
anim.connect("onEnd",dojo.lang.hitch(this,function(){
this.fire("onEnd");
}));
}
},this);
};
dojo.inherits(dojo.lfx.Chain,dojo.lfx.IAnimation);
dojo.lang.extend(dojo.lfx.Chain,{_currAnim:-1,play:function(_3ad,_3ae){
if(!this._anims.length){
return this;
}
if(_3ae||!this._anims[this._currAnim]){
this._currAnim=0;
}
var _3af=this._anims[this._currAnim];
this.fire("beforeBegin");
if(_3ad>0){
setTimeout(dojo.lang.hitch(this,function(){
this.play(null,_3ae);
}),_3ad);
return this;
}
if(_3af){
if(this._currAnim==0){
this.fire("handler",["begin",this._currAnim]);
this.fire("onBegin",[this._currAnim]);
}
this.fire("onPlay",[this._currAnim]);
_3af.play(null,_3ae);
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
var _3b0=this._anims[this._currAnim];
if(_3b0){
if(!_3b0._active||_3b0._paused){
this.play();
}else{
this.pause();
}
}
return this;
},stop:function(){
var _3b1=this._anims[this._currAnim];
if(_3b1){
_3b1.stop();
this.fire("onStop",[this._currAnim]);
}
return _3b1;
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
dojo.lfx.combine=function(_3b2){
var _3b3=arguments;
if(dojo.lang.isArray(arguments[0])){
_3b3=arguments[0];
}
if(_3b3.length==1){
return _3b3[0];
}
return new dojo.lfx.Combine(_3b3);
};
dojo.lfx.chain=function(_3b4){
var _3b5=arguments;
if(dojo.lang.isArray(arguments[0])){
_3b5=arguments[0];
}
if(_3b5.length==1){
return _3b5[0];
}
return new dojo.lfx.Chain(_3b5);
};
dojo.provide("dojo.html.color");
dojo.html.getBackgroundColor=function(node){
node=dojo.byId(node);
var _3b7;
do{
_3b7=dojo.html.getStyle(node,"background-color");
if(_3b7.toLowerCase()=="rgba(0, 0, 0, 0)"){
_3b7="transparent";
}
if(node==document.getElementsByTagName("body")[0]){
node=null;
break;
}
node=node.parentNode;
}while(node&&dojo.lang.inArray(["transparent",""],_3b7));
if(_3b7=="transparent"){
_3b7=[255,255,255,0];
}else{
_3b7=dojo.gfx.color.extractRGB(_3b7);
}
return _3b7;
};
dojo.provide("dojo.lfx.html");
dojo.lfx.html._byId=function(_3b8){
if(!_3b8){
return [];
}
if(dojo.lang.isArrayLike(_3b8)){
if(!_3b8.alreadyChecked){
var n=[];
dojo.lang.forEach(_3b8,function(node){
n.push(dojo.byId(node));
});
n.alreadyChecked=true;
return n;
}else{
return _3b8;
}
}else{
var n=[];
n.push(dojo.byId(_3b8));
n.alreadyChecked=true;
return n;
}
};
dojo.lfx.html.propertyAnimation=function(_3bb,_3bc,_3bd,_3be,_3bf){
_3bb=dojo.lfx.html._byId(_3bb);
var _3c0={"propertyMap":_3bc,"nodes":_3bb,"duration":_3bd,"easing":_3be||dojo.lfx.easeDefault};
var _3c1=function(args){
if(args.nodes.length==1){
var pm=args.propertyMap;
if(!dojo.lang.isArray(args.propertyMap)){
var parr=[];
for(var _3c5 in pm){
pm[_3c5].property=_3c5;
parr.push(pm[_3c5]);
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
var _3c7=function(_3c8){
var _3c9=[];
dojo.lang.forEach(_3c8,function(c){
_3c9.push(Math.round(c));
});
return _3c9;
};
var _3cb=function(n,_3cd){
n=dojo.byId(n);
if(!n||!n.style){
return;
}
for(var s in _3cd){
if(s=="opacity"){
dojo.html.setOpacity(n,_3cd[s]);
}else{
n.style[s]=_3cd[s];
}
}
};
var _3cf=function(_3d0){
this._properties=_3d0;
this.diffs=new Array(_3d0.length);
dojo.lang.forEach(_3d0,function(prop,i){
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
var _3d7=null;
if(dojo.lang.isArray(prop.start)){
}else{
if(prop.start instanceof dojo.gfx.color.Color){
_3d7=(prop.units||"rgb")+"(";
for(var j=0;j<prop.startRgb.length;j++){
_3d7+=Math.round(((prop.endRgb[j]-prop.startRgb[j])*n)+prop.startRgb[j])+(j<prop.startRgb.length-1?",":"");
}
_3d7+=")";
}else{
_3d7=((this.diffs[i])*n)+prop.start+(prop.property!="opacity"?prop.units||"px":"");
}
}
ret[dojo.html.toCamelCase(prop.property)]=_3d7;
},this);
return ret;
};
};
var anim=new dojo.lfx.Animation({beforeBegin:function(){
_3c1(_3c0);
anim.curve=new _3cf(_3c0.propertyMap);
},onAnimate:function(_3da){
dojo.lang.forEach(_3c0.nodes,function(node){
_3cb(node,_3da);
});
}},_3c0.duration,null,_3c0.easing);
if(_3bf){
for(var x in _3bf){
if(dojo.lang.isFunction(_3bf[x])){
anim.connect(x,anim,_3bf[x]);
}
}
}
return anim;
};
dojo.lfx.html._makeFadeable=function(_3dd){
var _3de=function(node){
if(dojo.render.html.ie){
if((node.style.zoom.length==0)&&(dojo.html.getStyle(node,"zoom")=="normal")){
node.style.zoom="1";
}
if((node.style.width.length==0)&&(dojo.html.getStyle(node,"width")=="auto")){
node.style.width="auto";
}
}
};
if(dojo.lang.isArrayLike(_3dd)){
dojo.lang.forEach(_3dd,_3de);
}else{
_3de(_3dd);
}
};
dojo.lfx.html.fade=function(_3e0,_3e1,_3e2,_3e3,_3e4){
_3e0=dojo.lfx.html._byId(_3e0);
var _3e5={property:"opacity"};
if(!dj_undef("start",_3e1)){
_3e5.start=_3e1.start;
}else{
_3e5.start=function(){
return dojo.html.getOpacity(_3e0[0]);
};
}
if(!dj_undef("end",_3e1)){
_3e5.end=_3e1.end;
}else{
dojo.raise("dojo.lfx.html.fade needs an end value");
}
var anim=dojo.lfx.propertyAnimation(_3e0,[_3e5],_3e2,_3e3);
anim.connect("beforeBegin",function(){
dojo.lfx.html._makeFadeable(_3e0);
});
if(_3e4){
anim.connect("onEnd",function(){
_3e4(_3e0,anim);
});
}
return anim;
};
dojo.lfx.html.fadeIn=function(_3e7,_3e8,_3e9,_3ea){
return dojo.lfx.html.fade(_3e7,{end:1},_3e8,_3e9,_3ea);
};
dojo.lfx.html.fadeOut=function(_3eb,_3ec,_3ed,_3ee){
return dojo.lfx.html.fade(_3eb,{end:0},_3ec,_3ed,_3ee);
};
dojo.lfx.html.fadeShow=function(_3ef,_3f0,_3f1,_3f2){
_3ef=dojo.lfx.html._byId(_3ef);
dojo.lang.forEach(_3ef,function(node){
dojo.html.setOpacity(node,0);
});
var anim=dojo.lfx.html.fadeIn(_3ef,_3f0,_3f1,_3f2);
anim.connect("beforeBegin",function(){
if(dojo.lang.isArrayLike(_3ef)){
dojo.lang.forEach(_3ef,dojo.html.show);
}else{
dojo.html.show(_3ef);
}
});
return anim;
};
dojo.lfx.html.fadeHide=function(_3f5,_3f6,_3f7,_3f8){
var anim=dojo.lfx.html.fadeOut(_3f5,_3f6,_3f7,function(){
if(dojo.lang.isArrayLike(_3f5)){
dojo.lang.forEach(_3f5,dojo.html.hide);
}else{
dojo.html.hide(_3f5);
}
if(_3f8){
_3f8(_3f5,anim);
}
});
return anim;
};
dojo.lfx.html.wipeIn=function(_3fa,_3fb,_3fc,_3fd){
_3fa=dojo.lfx.html._byId(_3fa);
var _3fe=[];
dojo.lang.forEach(_3fa,function(node){
var _400={};
dojo.html.show(node);
var _401=dojo.html.getBorderBox(node).height;
dojo.html.hide(node);
var anim=dojo.lfx.propertyAnimation(node,{"height":{start:1,end:function(){
return _401;
}}},_3fb,_3fc);
anim.connect("beforeBegin",function(){
_400.overflow=node.style.overflow;
_400.height=node.style.height;
with(node.style){
overflow="hidden";
_401="1px";
}
dojo.html.show(node);
});
anim.connect("onEnd",function(){
with(node.style){
overflow=_400.overflow;
_401=_400.height;
}
if(_3fd){
_3fd(node,anim);
}
});
_3fe.push(anim);
});
return dojo.lfx.combine(_3fe);
};
dojo.lfx.html.wipeOut=function(_403,_404,_405,_406){
_403=dojo.lfx.html._byId(_403);
var _407=[];
dojo.lang.forEach(_403,function(node){
var _409={};
var anim=dojo.lfx.propertyAnimation(node,{"height":{start:function(){
return dojo.html.getContentBox(node).height;
},end:1}},_404,_405,{"beforeBegin":function(){
_409.overflow=node.style.overflow;
_409.height=node.style.height;
with(node.style){
overflow="hidden";
}
dojo.html.show(node);
},"onEnd":function(){
dojo.html.hide(node);
with(node.style){
overflow=_409.overflow;
height=_409.height;
}
if(_406){
_406(node,anim);
}
}});
_407.push(anim);
});
return dojo.lfx.combine(_407);
};
dojo.lfx.html.slideTo=function(_40b,_40c,_40d,_40e,_40f){
_40b=dojo.lfx.html._byId(_40b);
var _410=[];
var _411=dojo.html.getComputedStyle;
if(dojo.lang.isArray(_40c)){
dojo.deprecated("dojo.lfx.html.slideTo(node, array)","use dojo.lfx.html.slideTo(node, {top: value, left: value});","0.5");
_40c={top:_40c[0],left:_40c[1]};
}
dojo.lang.forEach(_40b,function(node){
var top=null;
var left=null;
var init=(function(){
var _416=node;
return function(){
var pos=_411(_416,"position");
top=(pos=="absolute"?node.offsetTop:parseInt(_411(node,"top"))||0);
left=(pos=="absolute"?node.offsetLeft:parseInt(_411(node,"left"))||0);
if(!dojo.lang.inArray(["absolute","relative"],pos)){
var ret=dojo.html.abs(_416,true);
dojo.html.setStyleAttributes(_416,"position:absolute;top:"+ret.y+"px;left:"+ret.x+"px;");
top=ret.y;
left=ret.x;
}
};
})();
init();
var anim=dojo.lfx.propertyAnimation(node,{"top":{start:top,end:(_40c.top||0)},"left":{start:left,end:(_40c.left||0)}},_40d,_40e,{"beforeBegin":init});
if(_40f){
anim.connect("onEnd",function(){
_40f(_40b,anim);
});
}
_410.push(anim);
});
return dojo.lfx.combine(_410);
};
dojo.lfx.html.slideBy=function(_41a,_41b,_41c,_41d,_41e){
_41a=dojo.lfx.html._byId(_41a);
var _41f=[];
var _420=dojo.html.getComputedStyle;
if(dojo.lang.isArray(_41b)){
dojo.deprecated("dojo.lfx.html.slideBy(node, array)","use dojo.lfx.html.slideBy(node, {top: value, left: value});","0.5");
_41b={top:_41b[0],left:_41b[1]};
}
dojo.lang.forEach(_41a,function(node){
var top=null;
var left=null;
var init=(function(){
var _425=node;
return function(){
var pos=_420(_425,"position");
top=(pos=="absolute"?node.offsetTop:parseInt(_420(node,"top"))||0);
left=(pos=="absolute"?node.offsetLeft:parseInt(_420(node,"left"))||0);
if(!dojo.lang.inArray(["absolute","relative"],pos)){
var ret=dojo.html.abs(_425,true);
dojo.html.setStyleAttributes(_425,"position:absolute;top:"+ret.y+"px;left:"+ret.x+"px;");
top=ret.y;
left=ret.x;
}
};
})();
init();
var anim=dojo.lfx.propertyAnimation(node,{"top":{start:top,end:top+(_41b.top||0)},"left":{start:left,end:left+(_41b.left||0)}},_41c,_41d).connect("beforeBegin",init);
if(_41e){
anim.connect("onEnd",function(){
_41e(_41a,anim);
});
}
_41f.push(anim);
});
return dojo.lfx.combine(_41f);
};
dojo.lfx.html.explode=function(_429,_42a,_42b,_42c,_42d){
var h=dojo.html;
_429=dojo.byId(_429);
_42a=dojo.byId(_42a);
var _42f=h.toCoordinateObject(_429,true);
var _430=document.createElement("div");
h.copyStyle(_430,_42a);
if(_42a.explodeClassName){
_430.className=_42a.explodeClassName;
}
with(_430.style){
position="absolute";
display="none";
}
dojo.body().appendChild(_430);
with(_42a.style){
visibility="hidden";
display="block";
}
var _431=h.toCoordinateObject(_42a,true);
with(_42a.style){
display="none";
visibility="visible";
}
var _432={opacity:{start:0.5,end:1}};
dojo.lang.forEach(["height","width","top","left"],function(type){
_432[type]={start:_42f[type],end:_431[type]};
});
var anim=new dojo.lfx.propertyAnimation(_430,_432,_42b,_42c,{"beforeBegin":function(){
h.setDisplay(_430,"block");
},"onEnd":function(){
h.setDisplay(_42a,"block");
_430.parentNode.removeChild(_430);
}});
if(_42d){
anim.connect("onEnd",function(){
_42d(_42a,anim);
});
}
return anim;
};
dojo.lfx.html.implode=function(_435,end,_437,_438,_439){
var h=dojo.html;
_435=dojo.byId(_435);
end=dojo.byId(end);
var _43b=dojo.html.toCoordinateObject(_435,true);
var _43c=dojo.html.toCoordinateObject(end,true);
var _43d=document.createElement("div");
dojo.html.copyStyle(_43d,_435);
if(_435.explodeClassName){
_43d.className=_435.explodeClassName;
}
dojo.html.setOpacity(_43d,0.3);
with(_43d.style){
position="absolute";
display="none";
backgroundColor=h.getStyle(_435,"background-color").toLowerCase();
}
dojo.body().appendChild(_43d);
var _43e={opacity:{start:1,end:0.5}};
dojo.lang.forEach(["height","width","top","left"],function(type){
_43e[type]={start:_43b[type],end:_43c[type]};
});
var anim=new dojo.lfx.propertyAnimation(_43d,_43e,_437,_438,{"beforeBegin":function(){
dojo.html.hide(_435);
dojo.html.show(_43d);
},"onEnd":function(){
_43d.parentNode.removeChild(_43d);
}});
if(_439){
anim.connect("onEnd",function(){
_439(_435,anim);
});
}
return anim;
};
dojo.lfx.html.highlight=function(_441,_442,_443,_444,_445){
_441=dojo.lfx.html._byId(_441);
var _446=[];
dojo.lang.forEach(_441,function(node){
var _448=dojo.html.getBackgroundColor(node);
var bg=dojo.html.getStyle(node,"background-color").toLowerCase();
var _44a=dojo.html.getStyle(node,"background-image");
var _44b=(bg=="transparent"||bg=="rgba(0, 0, 0, 0)");
while(_448.length>3){
_448.pop();
}
var rgb=new dojo.gfx.color.Color(_442);
var _44d=new dojo.gfx.color.Color(_448);
var anim=dojo.lfx.propertyAnimation(node,{"background-color":{start:rgb,end:_44d}},_443,_444,{"beforeBegin":function(){
if(_44a){
node.style.backgroundImage="none";
}
node.style.backgroundColor="rgb("+rgb.toRgb().join(",")+")";
},"onEnd":function(){
if(_44a){
node.style.backgroundImage=_44a;
}
if(_44b){
node.style.backgroundColor="transparent";
}
if(_445){
_445(node,anim);
}
}});
_446.push(anim);
});
return dojo.lfx.combine(_446);
};
dojo.lfx.html.unhighlight=function(_44f,_450,_451,_452,_453){
_44f=dojo.lfx.html._byId(_44f);
var _454=[];
dojo.lang.forEach(_44f,function(node){
var _456=new dojo.gfx.color.Color(dojo.html.getBackgroundColor(node));
var rgb=new dojo.gfx.color.Color(_450);
var _458=dojo.html.getStyle(node,"background-image");
var anim=dojo.lfx.propertyAnimation(node,{"background-color":{start:_456,end:rgb}},_451,_452,{"beforeBegin":function(){
if(_458){
node.style.backgroundImage="none";
}
node.style.backgroundColor="rgb("+_456.toRgb().join(",")+")";
},"onEnd":function(){
if(_453){
_453(node,anim);
}
}});
_454.push(anim);
});
return dojo.lfx.combine(_454);
};
dojo.lang.mixin(dojo.lfx,dojo.lfx.html);
dojo.provide("dojo.lfx.*");
dojo.provide("dojo.lang.extras");
dojo.lang.setTimeout=function(func,_45b){
var _45c=window,_45d=2;
if(!dojo.lang.isFunction(func)){
_45c=func;
func=_45b;
_45b=arguments[2];
_45d++;
}
if(dojo.lang.isString(func)){
func=_45c[func];
}
var args=[];
for(var i=_45d;i<arguments.length;i++){
args.push(arguments[i]);
}
return dojo.global().setTimeout(function(){
func.apply(_45c,args);
},_45b);
};
dojo.lang.clearTimeout=function(_460){
dojo.global().clearTimeout(_460);
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
dojo.lang.getObjPathValue=function(_469,_46a,_46b){
with(dojo.parseObjPath(_469,_46a,_46b)){
return dojo.evalProp(prop,obj,_46b);
}
};
dojo.lang.setObjPathValue=function(_46c,_46d,_46e,_46f){
if(arguments.length<4){
_46f=true;
}
with(dojo.parseObjPath(_46c,_46e,_46f)){
if(obj&&(_46f||(prop in obj))){
obj[prop]=_46d;
}
}
};
dojo.provide("dojo.event.common");
dojo.event=new function(){
this._canTimeout=dojo.lang.isFunction(dj_global["setTimeout"])||dojo.lang.isAlien(dj_global["setTimeout"]);
function interpolateArgs(args,_471){
var dl=dojo.lang;
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
var _474=dl.nameAnonFunc(args[2],ao.adviceObj,_471);
ao.adviceFunc=_474;
}else{
if((dl.isFunction(args[0]))&&(dl.isObject(args[1]))&&(dl.isString(args[2]))){
ao.adviceType="after";
ao.srcObj=dj_global;
var _474=dl.nameAnonFunc(args[0],ao.srcObj,_471);
ao.srcFunc=_474;
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
var _474=dl.nameAnonFunc(args[1],dj_global,_471);
ao.srcFunc=_474;
ao.adviceObj=args[2];
ao.adviceFunc=args[3];
}else{
if((dl.isString(args[0]))&&(dl.isObject(args[1]))&&(dl.isString(args[2]))&&(dl.isFunction(args[3]))){
ao.srcObj=args[1];
ao.srcFunc=args[2];
var _474=dl.nameAnonFunc(args[3],dj_global,_471);
ao.adviceObj=dj_global;
ao.adviceFunc=_474;
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
break;
}
if(dl.isFunction(ao.aroundFunc)){
var _474=dl.nameAnonFunc(ao.aroundFunc,ao.aroundObj,_471);
ao.aroundFunc=_474;
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
var _476={};
for(var x in ao){
_476[x]=ao[x];
}
var mjps=[];
dojo.lang.forEach(ao.srcObj,function(src){
if((dojo.render.html.capable)&&(dojo.lang.isString(src))){
src=dojo.byId(src);
}
_476.srcObj=src;
mjps.push(dojo.event.connect.call(dojo.event,_476));
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
var _47e;
if((arguments.length==1)&&(typeof a1=="object")){
_47e=a1;
}else{
_47e={srcObj:a1,srcFunc:a2};
}
_47e.adviceFunc=function(){
var _47f=[];
for(var x=0;x<arguments.length;x++){
_47f.push(arguments[x]);
}
dojo.debug("("+_47e.srcObj+")."+_47e.srcFunc,":",_47f.join(", "));
};
this.kwConnect(_47e);
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
this._kwConnectImpl=function(_486,_487){
var fn=(_487)?"disconnect":"connect";
if(typeof _486["srcFunc"]=="function"){
_486.srcObj=_486["srcObj"]||dj_global;
var _489=dojo.lang.nameAnonFunc(_486.srcFunc,_486.srcObj,true);
_486.srcFunc=_489;
}
if(typeof _486["adviceFunc"]=="function"){
_486.adviceObj=_486["adviceObj"]||dj_global;
var _489=dojo.lang.nameAnonFunc(_486.adviceFunc,_486.adviceObj,true);
_486.adviceFunc=_489;
}
_486.srcObj=_486["srcObj"]||dj_global;
_486.adviceObj=_486["adviceObj"]||_486["targetObj"]||dj_global;
_486.adviceFunc=_486["adviceFunc"]||_486["targetFunc"];
return dojo.event[fn](_486);
};
this.kwConnect=function(_48a){
return this._kwConnectImpl(_48a,false);
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
var mjp=dojo.event.MethodJoinPoint.getForMethod(ao.srcObj,ao.srcFunc);
return mjp.removeAdvice(ao.adviceObj,ao.adviceFunc,ao.adviceType,ao.once);
};
this.kwDisconnect=function(_48d){
return this._kwConnectImpl(_48d,true);
};
};
dojo.event.MethodInvocation=function(_48e,obj,args){
this.jp_=_48e;
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
dojo.event.MethodJoinPoint=function(obj,_496){
this.object=obj||dj_global;
this.methodname=_496;
this.methodfunc=this.object[_496];
this.squelch=false;
};
dojo.event.MethodJoinPoint.getForMethod=function(obj,_498){
if(!obj){
obj=dj_global;
}
if(!obj[_498]){
obj[_498]=function(){
};
if(!obj[_498]){
dojo.raise("Cannot set do-nothing method on that object "+_498);
}
}else{
if((!dojo.lang.isFunction(obj[_498]))&&(!dojo.lang.isAlien(obj[_498]))){
return null;
}
}
var _499=_498+"$joinpoint";
var _49a=_498+"$joinpoint$method";
var _49b=obj[_499];
if(!_49b){
var _49c=false;
if(dojo.event["browser"]){
if((obj["attachEvent"])||(obj["nodeType"])||(obj["addEventListener"])){
_49c=true;
dojo.event.browser.addClobberNodeAttrs(obj,[_499,_49a,_498]);
}
}
var _49d=obj[_498].length;
obj[_49a]=obj[_498];
_49b=obj[_499]=new dojo.event.MethodJoinPoint(obj,_49a);
obj[_498]=function(){
var args=[];
if((_49c)&&(!arguments.length)){
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
if((x==0)&&(_49c)&&(dojo.event.browser.isEvent(arguments[x]))){
args.push(dojo.event.browser.fixEvent(arguments[x],this));
}else{
args.push(arguments[x]);
}
}
}
return _49b.run.apply(_49b,args);
};
obj[_498].__preJoinArity=_49d;
}
return _49b;
};
dojo.lang.extend(dojo.event.MethodJoinPoint,{unintercept:function(){
this.object[this.methodname]=this.methodfunc;
this.before=[];
this.after=[];
this.around=[];
},disconnect:dojo.lang.forward("unintercept"),run:function(){
var obj=this.object||dj_global;
var args=arguments;
var _4a3=[];
for(var x=0;x<args.length;x++){
_4a3[x]=args[x];
}
var _4a5=function(marr){
if(!marr){
dojo.debug("Null argument to unrollAdvice()");
return;
}
var _4a7=marr[0]||dj_global;
var _4a8=marr[1];
if(!_4a7[_4a8]){
dojo.raise("function \""+_4a8+"\" does not exist on \""+_4a7+"\"");
}
var _4a9=marr[2]||dj_global;
var _4aa=marr[3];
var msg=marr[6];
var _4ac;
var to={args:[],jp_:this,object:obj,proceed:function(){
return _4a7[_4a8].apply(_4a7,to.args);
}};
to.args=_4a3;
var _4ae=parseInt(marr[4]);
var _4af=((!isNaN(_4ae))&&(marr[4]!==null)&&(typeof marr[4]!="undefined"));
if(marr[5]){
var rate=parseInt(marr[5]);
var cur=new Date();
var _4b2=false;
if((marr["last"])&&((cur-marr.last)<=rate)){
if(dojo.event._canTimeout){
if(marr["delayTimer"]){
clearTimeout(marr.delayTimer);
}
var tod=parseInt(rate*2);
var mcpy=dojo.lang.shallowCopy(marr);
marr.delayTimer=setTimeout(function(){
mcpy[5]=0;
_4a5(mcpy);
},tod);
}
return;
}else{
marr.last=cur;
}
}
if(_4aa){
_4a9[_4aa].call(_4a9,to);
}else{
if((_4af)&&((dojo.render.html)||(dojo.render.svg))){
dj_global["setTimeout"](function(){
if(msg){
_4a7[_4a8].call(_4a7,to);
}else{
_4a7[_4a8].apply(_4a7,args);
}
},_4ae);
}else{
if(msg){
_4a7[_4a8].call(_4a7,to);
}else{
_4a7[_4a8].apply(_4a7,args);
}
}
}
};
var _4b5=function(){
if(this.squelch){
try{
return _4a5.apply(this,arguments);
}
catch(e){
dojo.debug(e);
}
}else{
return _4a5.apply(this,arguments);
}
};
if((this["before"])&&(this.before.length>0)){
dojo.lang.forEach(this.before.concat(new Array()),_4b5);
}
var _4b6;
try{
if((this["around"])&&(this.around.length>0)){
var mi=new dojo.event.MethodInvocation(this,obj,args);
_4b6=mi.proceed();
}else{
if(this.methodfunc){
_4b6=this.object[this.methodname].apply(this.object,args);
}
}
}
catch(e){
if(!this.squelch){
dojo.raise(e);
}
}
if((this["after"])&&(this.after.length>0)){
dojo.lang.forEach(this.after.concat(new Array()),_4b5);
}
return (this.methodfunc)?_4b6:null;
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
this.addAdvice(args["adviceObj"],args["adviceFunc"],args["aroundObj"],args["aroundFunc"],args["adviceType"],args["precedence"],args["once"],args["delay"],args["rate"],args["adviceMsg"]);
},addAdvice:function(_4bb,_4bc,_4bd,_4be,_4bf,_4c0,once,_4c2,rate,_4c4){
var arr=this.getArr(_4bf);
if(!arr){
dojo.raise("bad this: "+this);
}
var ao=[_4bb,_4bc,_4bd,_4be,_4c2,rate,_4c4];
if(once){
if(this.hasAdvice(_4bb,_4bc,_4bf,arr)>=0){
return;
}
}
if(_4c0=="first"){
arr.unshift(ao);
}else{
arr.push(ao);
}
},hasAdvice:function(_4c7,_4c8,_4c9,arr){
if(!arr){
arr=this.getArr(_4c9);
}
var ind=-1;
for(var x=0;x<arr.length;x++){
var aao=(typeof _4c8=="object")?(new String(_4c8)).toString():_4c8;
var a1o=(typeof arr[x][1]=="object")?(new String(arr[x][1])).toString():arr[x][1];
if((arr[x][0]==_4c7)&&(a1o==aao)){
ind=x;
}
}
return ind;
},removeAdvice:function(_4cf,_4d0,_4d1,once){
var arr=this.getArr(_4d1);
var ind=this.hasAdvice(_4cf,_4d0,_4d1,arr);
if(ind==-1){
return false;
}
while(ind!=-1){
arr.splice(ind,1);
if(once){
break;
}
ind=this.hasAdvice(_4cf,_4d0,_4d1,arr);
}
return true;
}});
dojo.provide("dojo.event.topic");
dojo.event.topic=new function(){
this.topics={};
this.getTopic=function(_4d5){
if(!this.topics[_4d5]){
this.topics[_4d5]=new this.TopicImpl(_4d5);
}
return this.topics[_4d5];
};
this.registerPublisher=function(_4d6,obj,_4d8){
var _4d6=this.getTopic(_4d6);
_4d6.registerPublisher(obj,_4d8);
};
this.subscribe=function(_4d9,obj,_4db){
var _4d9=this.getTopic(_4d9);
_4d9.subscribe(obj,_4db);
};
this.unsubscribe=function(_4dc,obj,_4de){
var _4dc=this.getTopic(_4dc);
_4dc.unsubscribe(obj,_4de);
};
this.destroy=function(_4df){
this.getTopic(_4df).destroy();
delete this.topics[_4df];
};
this.publishApply=function(_4e0,args){
var _4e0=this.getTopic(_4e0);
_4e0.sendMessage.apply(_4e0,args);
};
this.publish=function(_4e2,_4e3){
var _4e2=this.getTopic(_4e2);
var args=[];
for(var x=1;x<arguments.length;x++){
args.push(arguments[x]);
}
_4e2.sendMessage.apply(_4e2,args);
};
};
dojo.event.topic.TopicImpl=function(_4e6){
this.topicName=_4e6;
this.subscribe=function(_4e7,_4e8){
var tf=_4e8||_4e7;
var to=(!_4e8)?dj_global:_4e7;
return dojo.event.kwConnect({srcObj:this,srcFunc:"sendMessage",adviceObj:to,adviceFunc:tf});
};
this.unsubscribe=function(_4eb,_4ec){
var tf=(!_4ec)?_4eb:_4ec;
var to=(!_4ec)?null:_4eb;
return dojo.event.kwDisconnect({srcObj:this,srcFunc:"sendMessage",adviceObj:to,adviceFunc:tf});
};
this._getJoinPoint=function(){
return dojo.event.MethodJoinPoint.getForMethod(this,"sendMessage");
};
this.setSquelch=function(_4ef){
this._getJoinPoint().squelch=_4ef;
};
this.destroy=function(){
this._getJoinPoint().disconnect();
};
this.registerPublisher=function(_4f0,_4f1){
dojo.event.connect(_4f0,_4f1,this,"sendMessage");
};
this.sendMessage=function(_4f2){
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
this.clobber=function(_4f5){
var na;
var tna;
if(_4f5){
tna=_4f5.all||_4f5.getElementsByTagName("*");
na=[_4f5];
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
var _4f9={};
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
var _4fd=0;
this.normalizedEventName=function(_4fe){
switch(_4fe){
case "CheckboxStateChange":
case "DOMAttrModified":
case "DOMMenuItemActive":
case "DOMMenuItemInactive":
case "DOMMouseScroll":
case "DOMNodeInserted":
case "DOMNodeRemoved":
case "RadioStateChange":
return _4fe;
break;
default:
return _4fe.toLowerCase();
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
this.addClobberNodeAttrs=function(node,_502){
if(!dojo.render.html.ie){
return;
}
this.addClobberNode(node);
for(var x=0;x<_502.length;x++){
node.__clobberAttrs__.push(_502[x]);
}
};
this.removeListener=function(node,_505,fp,_507){
if(!_507){
var _507=false;
}
_505=dojo.event.browser.normalizedEventName(_505);
if((_505=="onkey")||(_505=="key")){
if(dojo.render.html.ie){
this.removeListener(node,"onkeydown",fp,_507);
}
_505="onkeypress";
}
if(_505.substr(0,2)=="on"){
_505=_505.substr(2);
}
if(node.removeEventListener){
node.removeEventListener(_505,fp,_507);
}
};
this.addListener=function(node,_509,fp,_50b,_50c){
if(!node){
return;
}
if(!_50b){
var _50b=false;
}
_509=dojo.event.browser.normalizedEventName(_509);
if((_509=="onkey")||(_509=="key")){
if(dojo.render.html.ie){
this.addListener(node,"onkeydown",fp,_50b,_50c);
}
_509="onkeypress";
}
if(_509.substr(0,2)!="on"){
_509="on"+_509;
}
if(!_50c){
var _50d=function(evt){
if(!evt){
evt=window.event;
}
var ret=fp(dojo.event.browser.fixEvent(evt,this));
if(_50b){
dojo.event.browser.stopEvent(evt);
}
return ret;
};
}else{
_50d=fp;
}
if(node.addEventListener){
node.addEventListener(_509.substr(2),_50d,_50b);
return _50d;
}else{
if(typeof node[_509]=="function"){
var _510=node[_509];
node[_509]=function(e){
_510(e);
return _50d(e);
};
}else{
node[_509]=_50d;
}
if(dojo.render.html.ie){
this.addClobberNodeAttrs(node,[_509]);
}
return _50d;
}
};
this.isEvent=function(obj){
return (typeof obj!="undefined")&&(typeof Event!="undefined")&&(obj.eventPhase);
};
this.currentEvent=null;
this.callListener=function(_513,_514){
if(typeof _513!="function"){
dojo.raise("listener not a function: "+_513);
}
dojo.event.browser.currentEvent.currentTarget=_514;
return _513.call(_514,dojo.event.browser.currentEvent);
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
this.fixEvent=function(evt,_517){
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
var _519=evt.keyCode;
if(_519>=65&&_519<=90&&evt.shiftKey==false){
_519+=32;
}
if(_519>=1&&_519<=26&&evt.ctrlKey){
_519+=96;
}
evt.key=String.fromCharCode(_519);
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
var _519=evt.which;
if((evt.ctrlKey||evt.altKey||evt.metaKey)&&(evt.which>=65&&evt.which<=90&&evt.shiftKey==false)){
_519+=32;
}
evt.key=String.fromCharCode(_519);
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
default:
evt.key=evt.charCode>0?String.fromCharCode(evt.charCode):evt.keyCode;
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
evt.currentTarget=(_517?_517:evt.srcElement);
}
if(!evt.layerX){
evt.layerX=evt.offsetX;
}
if(!evt.layerY){
evt.layerY=evt.offsetY;
}
var doc=(evt.srcElement&&evt.srcElement.ownerDocument)?evt.srcElement.ownerDocument:document;
var _51b=((dojo.render.html.ie55)||(doc["compatMode"]=="BackCompat"))?doc.body:doc.documentElement;
if(!evt.pageX){
evt.pageX=evt.clientX+(_51b.scrollLeft||0);
}
if(!evt.pageY){
evt.pageY=evt.clientY+(_51b.scrollTop||0);
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
evt.returnValue=false;
evt.cancelBubble=true;
}else{
evt.preventDefault();
evt.stopPropagation();
}
};
};
dojo.provide("dojo.event.*");
dojo.provide("dojo.logging.Logger");
dojo.logging.Record=function(lvl,msg){
this.level=lvl;
this.message="";
this.msgArgs=[];
this.time=new Date();
if(dojo.lang.isArray(msg)){
if(msg.length>0&&dojo.lang.isString(msg[0])){
this.message=msg.shift();
}
this.msgArgs=msg;
}else{
this.message=msg;
}
};
dojo.logging.LogFilter=function(_51f){
this.passChain=_51f||"";
this.filter=function(_520){
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
dojo.extend(dojo.logging.Logger,{argsToArr:function(args){
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
},removeFilterByIndex:function(_527){
if(this.filters[_527]){
delete this.filters[_527];
return true;
}
return false;
},removeFilter:function(_528){
for(var x=0;x<this.filters.length;x++){
if(this.filters[x]===_528){
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
},exception:function(msg,e,_538){
if(e){
var _539=[e.name,(e.description||e.message)];
if(e.fileName){
_539.push(e.fileName);
_539.push("line "+e.lineNumber);
}
msg+=" "+_539.join(" : ");
}
this.logType("ERROR",msg);
if(!_538){
throw e;
}
},logType:function(type,args){
return this.log.apply(this,[dojo.logging.log.getLevel(type),args]);
},warn:function(){
this.warning.apply(this,arguments);
},err:function(){
this.error.apply(this,arguments);
},crit:function(){
this.critical.apply(this,arguments);
}});
dojo.logging.LogHandler=function(_53c){
this.cutOffLevel=(_53c)?_53c:0;
this.formatter=null;
this.data=[];
this.filters=[];
};
dojo.lang.extend(dojo.logging.LogHandler,{setFormatter:function(_53d){
dojo.unimplemented("setFormatter");
},flush:function(){
},close:function(){
},handleError:function(){
},handle:function(_53e){
if((this.filter(_53e))&&(_53e.level>=this.cutOffLevel)){
this.emit(_53e);
}
},emit:function(_53f){
dojo.unimplemented("emit");
}});
void (function(){
var _540=["setLevel","addFilter","removeFilterByIndex","removeFilter","removeAllFilters","filter"];
var tgt=dojo.logging.LogHandler.prototype;
var src=dojo.logging.Logger.prototype;
for(var x=0;x<_540.length;x++){
tgt[_540[x]]=src[_540[x]];
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
dojo.logging.MemoryLogHandler=function(_54b,_54c,_54d,_54e){
dojo.logging.LogHandler.call(this,_54b);
this.numRecords=(typeof djConfig["loggingNumRecords"]!="undefined")?djConfig["loggingNumRecords"]:((_54c)?_54c:-1);
this.postType=(typeof djConfig["loggingPostType"]!="undefined")?djConfig["loggingPostType"]:(_54d||-1);
this.postInterval=(typeof djConfig["loggingPostInterval"]!="undefined")?djConfig["loggingPostInterval"]:(_54d||-1);
};
dojo.lang.inherits(dojo.logging.MemoryLogHandler,dojo.logging.LogHandler);
dojo.lang.extend(dojo.logging.MemoryLogHandler,{emit:function(_54f){
if(!djConfig.isDebug){
return;
}
var _550=String(dojo.log.getLevelName(_54f.level)+": "+_54f.time.toLocaleTimeString())+": "+_54f.message;
if(!dj_undef("println",dojo.hostenv)){
dojo.hostenv.println(_550);
}
this.data.push(_54f);
if(this.numRecords!=-1){
while(this.data.length>this.numRecords){
this.data.shift();
}
}
}});
dojo.logging.logQueueHandler=new dojo.logging.MemoryLogHandler(0,50,0,10000);
dojo.logging.log.addHandler(dojo.logging.logQueueHandler);
dojo.log=dojo.logging.log;
dojo.provide("dojo.logging.*");
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
dojo.string.repeat=function(str,_557,_558){
var out="";
for(var i=0;i<_557;i++){
out+=str;
if(_558&&i<_557-1){
out+=_558;
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
dojo.provide("dojo.io.common");
dojo.io.transports=[];
dojo.io.hdlrFuncNames=["load","error","timeout"];
dojo.io.Request=function(url,_567,_568,_569){
if((arguments.length==1)&&(arguments[0].constructor==Object)){
this.fromKwArgs(arguments[0]);
}else{
this.url=url;
if(_567){
this.mimetype=_567;
}
if(_568){
this.transport=_568;
}
if(arguments.length>=4){
this.changeUrl=_569;
}
}
};
dojo.lang.extend(dojo.io.Request,{url:"",mimetype:"text/plain",method:"GET",content:undefined,transport:undefined,changeUrl:undefined,formNode:undefined,sync:false,bindSuccess:false,useCache:false,preventCache:false,load:function(type,data,_56c,_56d){
},error:function(type,_56f,_570,_571){
},timeout:function(type,_573,_574,_575){
},handle:function(type,data,_578,_579){
},timeoutSeconds:0,abort:function(){
},fromKwArgs:function(_57a){
if(_57a["url"]){
_57a.url=_57a.url.toString();
}
if(_57a["formNode"]){
_57a.formNode=dojo.byId(_57a.formNode);
}
if(!_57a["method"]&&_57a["formNode"]&&_57a["formNode"].method){
_57a.method=_57a["formNode"].method;
}
if(!_57a["handle"]&&_57a["handler"]){
_57a.handle=_57a.handler;
}
if(!_57a["load"]&&_57a["loaded"]){
_57a.load=_57a.loaded;
}
if(!_57a["changeUrl"]&&_57a["changeURL"]){
_57a.changeUrl=_57a.changeURL;
}
_57a.encoding=dojo.lang.firstValued(_57a["encoding"],djConfig["bindEncoding"],"");
_57a.sendTransport=dojo.lang.firstValued(_57a["sendTransport"],djConfig["ioSendTransport"],false);
var _57b=dojo.lang.isFunction;
for(var x=0;x<dojo.io.hdlrFuncNames.length;x++){
var fn=dojo.io.hdlrFuncNames[x];
if(_57a[fn]&&_57b(_57a[fn])){
continue;
}
if(_57a["handle"]&&_57b(_57a["handle"])){
_57a[fn]=_57a.handle;
}
}
dojo.lang.mixin(this,_57a);
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
dojo.io.bind=function(_582){
if(!(_582 instanceof dojo.io.Request)){
try{
_582=new dojo.io.Request(_582);
}
catch(e){
dojo.debug(e);
}
}
var _583="";
if(_582["transport"]){
_583=_582["transport"];
if(!this[_583]){
dojo.io.sendBindError(_582,"No dojo.io.bind() transport with name '"+_582["transport"]+"'.");
return _582;
}
if(!this[_583].canHandle(_582)){
dojo.io.sendBindError(_582,"dojo.io.bind() transport with name '"+_582["transport"]+"' cannot handle this type of request.");
return _582;
}
}else{
for(var x=0;x<dojo.io.transports.length;x++){
var tmp=dojo.io.transports[x];
if((this[tmp])&&(this[tmp].canHandle(_582))){
_583=tmp;
break;
}
}
if(_583==""){
dojo.io.sendBindError(_582,"None of the loaded transports for dojo.io.bind()"+" can handle the request.");
return _582;
}
}
this[_583].bind(_582);
_582.bindSuccess=true;
return _582;
};
dojo.io.sendBindError=function(_586,_587){
if((typeof _586.error=="function"||typeof _586.handle=="function")&&(typeof setTimeout=="function"||typeof setTimeout=="object")){
var _588=new dojo.io.Error(_587);
setTimeout(function(){
_586[(typeof _586.error=="function")?"error":"handle"]("error",_588,null,_586);
},50);
}else{
dojo.raise(_587);
}
};
dojo.io.queueBind=function(_589){
if(!(_589 instanceof dojo.io.Request)){
try{
_589=new dojo.io.Request(_589);
}
catch(e){
dojo.debug(e);
}
}
var _58a=_589.load;
_589.load=function(){
dojo.io._queueBindInFlight=false;
var ret=_58a.apply(this,arguments);
dojo.io._dispatchNextQueueBind();
return ret;
};
var _58c=_589.error;
_589.error=function(){
dojo.io._queueBindInFlight=false;
var ret=_58c.apply(this,arguments);
dojo.io._dispatchNextQueueBind();
return ret;
};
dojo.io._bindQueue.push(_589);
dojo.io._dispatchNextQueueBind();
return _589;
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
dojo.io.argsFromMap=function(map,_58f,last){
var enc=/utf/i.test(_58f||"")?encodeURIComponent:dojo.string.encodeAscii;
var _592=[];
var _593=new Object();
for(var name in map){
var _595=function(elt){
var val=enc(name)+"="+enc(elt);
_592[(last==name)?"push":"unshift"](val);
};
if(!_593[name]){
var _598=map[name];
if(dojo.lang.isArray(_598)){
dojo.lang.forEach(_598,_595);
}else{
_595(_598);
}
}
}
return _592.join("&");
};
dojo.io.setIFrameSrc=function(_599,src,_59b){
try{
var r=dojo.render.html;
if(!_59b){
if(r.safari){
_599.location=src;
}else{
frames[_599.name].location=src;
}
}else{
var idoc;
if(r.ie){
idoc=_599.contentWindow.document;
}else{
if(r.safari){
idoc=_599.document;
}else{
idoc=_599.contentWindow;
}
}
if(!idoc){
_599.location=src;
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
dojo.provide("dojo.string.extras");
dojo.string.substituteParams=function(_59e,hash){
var map=(typeof hash=="object")?hash:dojo.lang.toArray(arguments,1);
return _59e.replace(/\%\{(\w+)\}/g,function(_5a1,key){
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
var _5a4=str.split(" ");
for(var i=0;i<_5a4.length;i++){
_5a4[i]=_5a4[i].charAt(0).toUpperCase()+_5a4[i].substring(1);
}
return _5a4.join(" ");
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
var _5a9=escape(str);
var _5aa,re=/%u([0-9A-F]{4})/i;
while((_5aa=_5a9.match(re))){
var num=Number("0x"+_5aa[1]);
var _5ad=escape("&#"+num+";");
ret+=_5a9.substring(0,_5aa.index)+_5ad;
_5a9=_5a9.substring(_5aa.index+_5aa[0].length);
}
ret+=_5a9.replace(/\+/g,"%2B");
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
dojo.string.escapeXml=function(str,_5b2){
str=str.replace(/&/gm,"&amp;").replace(/</gm,"&lt;").replace(/>/gm,"&gt;").replace(/"/gm,"&quot;");
if(!_5b2){
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
dojo.string.endsWith=function(str,end,_5bb){
if(_5bb){
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
dojo.string.startsWith=function(str,_5bf,_5c0){
if(_5c0){
str=str.toLowerCase();
_5bf=_5bf.toLowerCase();
}
return str.indexOf(_5bf)==0;
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
dojo.string.normalizeNewlines=function(text,_5c6){
if(_5c6=="\n"){
text=text.replace(/\r\n/g,"\n");
text=text.replace(/\r/g,"\n");
}else{
if(_5c6=="\r"){
text=text.replace(/\r\n/g,"\r");
text=text.replace(/\n/g,"\r");
}else{
text=text.replace(/([^\r])\n/g,"$1\r\n").replace(/\r([^\n])/g,"\r\n$1");
}
}
return text;
};
dojo.string.splitEscaped=function(str,_5c8){
var _5c9=[];
for(var i=0,_5cb=0;i<str.length;i++){
if(str.charAt(i)=="\\"){
i++;
continue;
}
if(str.charAt(i)==_5c8){
_5c9.push(str.substring(_5cb,i));
_5cb=i+1;
}
}
_5c9.push(str.substr(_5cb));
return _5c9;
};
dojo.provide("dojo.undo.browser");
try{
if((!djConfig["preventBackButtonFix"])&&(!dojo.hostenv.post_load_)){
document.write("<iframe style='border: 0px; width: 1px; height: 1px; position: absolute; bottom: 0px; right: 0px; visibility: visible;' name='djhistory' id='djhistory' src='"+(dojo.hostenv.getBaseScriptUri()+"iframe_history.html")+"'></iframe>");
}
}
catch(e){
}
if(dojo.render.html.opera){
dojo.debug("Opera is not supported with dojo.undo.browser, so back/forward detection will not work.");
}
dojo.undo.browser={initialHref:window.location.href,initialHash:window.location.hash,moveForward:false,historyStack:[],forwardStack:[],historyIframe:null,bookmarkAnchor:null,locationTimer:null,setInitialState:function(args){
this.initialState=this._createState(this.initialHref,args,this.initialHash);
},addToHistory:function(args){
this.forwardStack=[];
var hash=null;
var url=null;
if(!this.historyIframe){
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
var _5d0=args["back"]||args["backButton"]||args["handle"];
var tcb=function(_5d2){
if(window.location.hash!=""){
setTimeout("window.location.href = '"+hash+"';",1);
}
_5d0.apply(this,[_5d2]);
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
var _5d3=args["forward"]||args["forwardButton"]||args["handle"];
var tfw=function(_5d5){
if(window.location.hash!=""){
window.location.href=hash;
}
if(_5d3){
_5d3.apply(this,[_5d5]);
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
},iframeLoaded:function(evt,_5d8){
if(!dojo.render.html.opera){
var _5d9=this._getUrlQuery(_5d8.href);
if(_5d9==null){
if(this.historyStack.length==1){
this.handleBackButton();
}
return;
}
if(this.moveForward){
this.moveForward=false;
return;
}
if(this.historyStack.length>=2&&_5d9==this._getUrlQuery(this.historyStack[this.historyStack.length-2].url)){
this.handleBackButton();
}else{
if(this.forwardStack.length>0&&_5d9==this._getUrlQuery(this.forwardStack[this.forwardStack.length-1].url)){
this.handleForwardButton();
}
}
}
},handleBackButton:function(){
var _5da=this.historyStack.pop();
if(!_5da){
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
this.forwardStack.push(_5da);
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
var _5e1=url.split("?");
if(_5e1.length<2){
return null;
}else{
return _5e1[1];
}
},_loadIframeHistory:function(){
var url=dojo.hostenv.getBaseScriptUri()+"iframe_history.html?"+(new Date()).getTime();
this.moveForward=true;
dojo.io.setIFrameSrc(this.historyIframe,url,false);
return url;
}};
dojo.provide("dojo.io.BrowserIO");
dojo.io.checkChildrenForFile=function(node){
var _5e4=false;
var _5e5=node.getElementsByTagName("input");
dojo.lang.forEach(_5e5,function(_5e6){
if(_5e4){
return;
}
if(_5e6.getAttribute("type")=="file"){
_5e4=true;
}
});
return _5e4;
};
dojo.io.formHasFile=function(_5e7){
return dojo.io.checkChildrenForFile(_5e7);
};
dojo.io.updateNode=function(node,_5e9){
node=dojo.byId(node);
var args=_5e9;
if(dojo.lang.isString(_5e9)){
args={url:_5e9};
}
args.mimetype="text/html";
args.load=function(t,d,e){
while(node.firstChild){
if(dojo["event"]){
try{
dojo.event.browser.clean(node.firstChild);
}
catch(e){
}
}
node.removeChild(node.firstChild);
}
node.innerHTML=d;
};
dojo.io.bind(args);
};
dojo.io.formFilter=function(node){
var type=(node.type||"").toLowerCase();
return !node.disabled&&node.name&&!dojo.lang.inArray(["file","submit","image","reset","button"],type);
};
dojo.io.encodeForm=function(_5f0,_5f1,_5f2){
if((!_5f0)||(!_5f0.tagName)||(!_5f0.tagName.toLowerCase()=="form")){
dojo.raise("Attempted to encode a non-form element.");
}
if(!_5f2){
_5f2=dojo.io.formFilter;
}
var enc=/utf/i.test(_5f1||"")?encodeURIComponent:dojo.string.encodeAscii;
var _5f4=[];
for(var i=0;i<_5f0.elements.length;i++){
var elm=_5f0.elements[i];
if(!elm||elm.tagName.toLowerCase()=="fieldset"||!_5f2(elm)){
continue;
}
var name=enc(elm.name);
var type=elm.type.toLowerCase();
if(type=="select-multiple"){
for(var j=0;j<elm.options.length;j++){
if(elm.options[j].selected){
_5f4.push(name+"="+enc(elm.options[j].value));
}
}
}else{
if(dojo.lang.inArray(["radio","checkbox"],type)){
if(elm.checked){
_5f4.push(name+"="+enc(elm.value));
}
}else{
_5f4.push(name+"="+enc(elm.value));
}
}
}
var _5fa=_5f0.getElementsByTagName("input");
for(var i=0;i<_5fa.length;i++){
var _5fb=_5fa[i];
if(_5fb.type.toLowerCase()=="image"&&_5fb.form==_5f0&&_5f2(_5fb)){
var name=enc(_5fb.name);
_5f4.push(name+"="+enc(_5fb.value));
_5f4.push(name+".x=0");
_5f4.push(name+".y=0");
}
}
return _5f4.join("&")+"&";
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
var _601=form.getElementsByTagName("input");
for(var i=0;i<_601.length;i++){
var _602=_601[i];
if(_602.type.toLowerCase()=="image"&&_602.form==form){
this.connect(_602,"onclick","click");
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
var _609=false;
if(node.disabled||!node.name){
_609=false;
}else{
if(dojo.lang.inArray(["submit","button","image"],type)){
if(!this.clickedButton){
this.clickedButton=node;
}
_609=node==this.clickedButton;
}else{
_609=!dojo.lang.inArray(["file","submit","reset","button"],type);
}
}
return _609;
},connect:function(_60a,_60b,_60c){
if(dojo.evalObjPath("dojo.event.connect")){
dojo.event.connect(_60a,_60b,this,_60c);
}else{
var fcn=dojo.lang.hitch(this,_60c);
_60a[_60b]=function(e){
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
var _60f=this;
var _610={};
this.useCache=false;
this.preventCache=false;
function getCacheKey(url,_612,_613){
return url+"|"+_612+"|"+_613.toLowerCase();
}
function addToCache(url,_615,_616,http){
_610[getCacheKey(url,_615,_616)]=http;
}
function getFromCache(url,_619,_61a){
return _610[getCacheKey(url,_619,_61a)];
}
this.clearCache=function(){
_610={};
};
function doLoad(_61b,http,url,_61e,_61f){
if(((http.status>=200)&&(http.status<300))||(http.status==304)||(location.protocol=="file:"&&(http.status==0||http.status==undefined))||(location.protocol=="chrome:"&&(http.status==0||http.status==undefined))){
var ret;
if(_61b.method.toLowerCase()=="head"){
var _621=http.getAllResponseHeaders();
ret={};
ret.toString=function(){
return _621;
};
var _622=_621.split(/[\r\n]+/g);
for(var i=0;i<_622.length;i++){
var pair=_622[i].match(/^([^:]+)\s*:\s*(.+)$/i);
if(pair){
ret[pair[1]]=pair[2];
}
}
}else{
if(_61b.mimetype=="text/javascript"){
try{
ret=dj_eval(http.responseText);
}
catch(e){
dojo.debug(e);
dojo.debug(http.responseText);
ret=null;
}
}else{
if(_61b.mimetype=="text/json"||_61b.mimetype=="application/json"){
try{
ret=dj_eval("("+http.responseText+")");
}
catch(e){
dojo.debug(e);
dojo.debug(http.responseText);
ret=false;
}
}else{
if((_61b.mimetype=="application/xml")||(_61b.mimetype=="text/xml")){
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
if(_61f){
addToCache(url,_61e,_61b.method,http);
}
_61b[(typeof _61b.load=="function")?"load":"handle"]("load",ret,http,_61b);
}else{
var _625=new dojo.io.Error("XMLHttpTransport Error: "+http.status+" "+http.statusText);
_61b[(typeof _61b.error=="function")?"error":"handle"]("error",_625,http,_61b);
}
}
function setHeaders(http,_627){
if(_627["headers"]){
for(var _628 in _627["headers"]){
if(_628.toLowerCase()=="content-type"&&!_627["contentType"]){
_627["contentType"]=_627["headers"][_628];
}else{
http.setRequestHeader(_628,_627["headers"][_628]);
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
if(!dojo.hostenv._blockAsync&&!_60f._blockAsync){
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
var _62c=new dojo.io.Error("XMLHttpTransport.watchInFlight Error: "+e);
tif.req[(typeof tif.req.error=="function")?"error":"handle"]("error",_62c,tif.http,tif.req);
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
var _62d=dojo.hostenv.getXmlhttpObject()?true:false;
this.canHandle=function(_62e){
return _62d&&dojo.lang.inArray(["text/plain","text/html","application/xml","text/xml","text/javascript","text/json","application/json"],(_62e["mimetype"].toLowerCase()||""))&&!(_62e["formNode"]&&dojo.io.formHasFile(_62e["formNode"]));
};
this.multipartBoundary="45309FFF-BD65-4d50-99C9-36986896A96F";
this.bind=function(_62f){
if(!_62f["url"]){
if(!_62f["formNode"]&&(_62f["backButton"]||_62f["back"]||_62f["changeUrl"]||_62f["watchForURL"])&&(!djConfig.preventBackButtonFix)){
dojo.deprecated("Using dojo.io.XMLHTTPTransport.bind() to add to browser history without doing an IO request","Use dojo.undo.browser.addToHistory() instead.","0.4");
dojo.undo.browser.addToHistory(_62f);
return true;
}
}
var url=_62f.url;
var _631="";
if(_62f["formNode"]){
var ta=_62f.formNode.getAttribute("action");
if((ta)&&(!_62f["url"])){
url=ta;
}
var tp=_62f.formNode.getAttribute("method");
if((tp)&&(!_62f["method"])){
_62f.method=tp;
}
_631+=dojo.io.encodeForm(_62f.formNode,_62f.encoding,_62f["formFilter"]);
}
if(url.indexOf("#")>-1){
dojo.debug("Warning: dojo.io.bind: stripping hash values from url:",url);
url=url.split("#")[0];
}
if(_62f["file"]){
_62f.method="post";
}
if(!_62f["method"]){
_62f.method="get";
}
if(_62f.method.toLowerCase()=="get"){
_62f.multipart=false;
}else{
if(_62f["file"]){
_62f.multipart=true;
}else{
if(!_62f["multipart"]){
_62f.multipart=false;
}
}
}
if(_62f["backButton"]||_62f["back"]||_62f["changeUrl"]){
dojo.undo.browser.addToHistory(_62f);
}
var _634=_62f["content"]||{};
if(_62f.sendTransport){
_634["dojo.transport"]="xmlhttp";
}
do{
if(_62f.postContent){
_631=_62f.postContent;
break;
}
if(_634){
_631+=dojo.io.argsFromMap(_634,_62f.encoding);
}
if(_62f.method.toLowerCase()=="get"||!_62f.multipart){
break;
}
var t=[];
if(_631.length){
var q=_631.split("&");
for(var i=0;i<q.length;++i){
if(q[i].length){
var p=q[i].split("=");
t.push("--"+this.multipartBoundary,"Content-Disposition: form-data; name=\""+p[0]+"\"","",p[1]);
}
}
}
if(_62f.file){
if(dojo.lang.isArray(_62f.file)){
for(var i=0;i<_62f.file.length;++i){
var o=_62f.file[i];
t.push("--"+this.multipartBoundary,"Content-Disposition: form-data; name=\""+o.name+"\"; filename=\""+("fileName" in o?o.fileName:o.name)+"\"","Content-Type: "+("contentType" in o?o.contentType:"application/octet-stream"),"",o.content);
}
}else{
var o=_62f.file;
t.push("--"+this.multipartBoundary,"Content-Disposition: form-data; name=\""+o.name+"\"; filename=\""+("fileName" in o?o.fileName:o.name)+"\"","Content-Type: "+("contentType" in o?o.contentType:"application/octet-stream"),"",o.content);
}
}
if(t.length){
t.push("--"+this.multipartBoundary+"--","");
_631=t.join("\r\n");
}
}while(false);
var _63a=_62f["sync"]?false:true;
var _63b=_62f["preventCache"]||(this.preventCache==true&&_62f["preventCache"]!=false);
var _63c=_62f["useCache"]==true||(this.useCache==true&&_62f["useCache"]!=false);
if(!_63b&&_63c){
var _63d=getFromCache(url,_631,_62f.method);
if(_63d){
doLoad(_62f,_63d,url,_631,false);
return;
}
}
var http=dojo.hostenv.getXmlhttpObject(_62f);
var _63f=false;
if(_63a){
var _640=this.inFlight.push({"req":_62f,"http":http,"url":url,"query":_631,"useCache":_63c,"startTime":_62f.timeoutSeconds?(new Date()).getTime():0});
this.startWatchingInFlight();
}else{
_60f._blockAsync=true;
}
if(_62f.method.toLowerCase()=="post"){
if(!_62f.user){
http.open("POST",url,_63a);
}else{
http.open("POST",url,_63a,_62f.user,_62f.password);
}
setHeaders(http,_62f);
http.setRequestHeader("Content-Type",_62f.multipart?("multipart/form-data; boundary="+this.multipartBoundary):(_62f.contentType||"application/x-www-form-urlencoded"));
try{
http.send(_631);
}
catch(e){
if(typeof http.abort=="function"){
http.abort();
}
doLoad(_62f,{status:404},url,_631,_63c);
}
}else{
var _641=url;
if(_631!=""){
_641+=(_641.indexOf("?")>-1?"&":"?")+_631;
}
if(_63b){
_641+=(dojo.string.endsWithAny(_641,"?","&")?"":(_641.indexOf("?")>-1?"&":"?"))+"dojo.preventCache="+new Date().valueOf();
}
if(!_62f.user){
http.open(_62f.method.toUpperCase(),_641,_63a);
}else{
http.open(_62f.method.toUpperCase(),_641,_63a,_62f.user,_62f.password);
}
setHeaders(http,_62f);
try{
http.send(null);
}
catch(e){
if(typeof http.abort=="function"){
http.abort();
}
doLoad(_62f,{status:404},url,_631,_63c);
}
}
if(!_63a){
doLoad(_62f,http,url,_631,_63c);
_60f._blockAsync=false;
}
_62f.abort=function(){
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
dojo.provide("dojo.io.cookie");
dojo.io.cookie.setCookie=function(name,_643,days,path,_646,_647){
var _648=-1;
if(typeof days=="number"&&days>=0){
var d=new Date();
d.setTime(d.getTime()+(days*24*60*60*1000));
_648=d.toGMTString();
}
_643=escape(_643);
document.cookie=name+"="+_643+";"+(_648!=-1?" expires="+_648+";":"")+(path?"path="+path:"")+(_646?"; domain="+_646:"")+(_647?"; secure":"");
};
dojo.io.cookie.set=dojo.io.cookie.setCookie;
dojo.io.cookie.getCookie=function(name){
var idx=document.cookie.lastIndexOf(name+"=");
if(idx==-1){
return null;
}
var _64c=document.cookie.substring(idx+name.length+1);
var end=_64c.indexOf(";");
if(end==-1){
end=_64c.length;
}
_64c=_64c.substring(0,end);
_64c=unescape(_64c);
return _64c;
};
dojo.io.cookie.get=dojo.io.cookie.getCookie;
dojo.io.cookie.deleteCookie=function(name){
dojo.io.cookie.setCookie(name,"-",0);
};
dojo.io.cookie.setObjectCookie=function(name,obj,days,path,_653,_654,_655){
if(arguments.length==5){
_655=_653;
_653=null;
_654=null;
}
var _656=[],_657,_658="";
if(!_655){
_657=dojo.io.cookie.getObjectCookie(name);
}
if(days>=0){
if(!_657){
_657={};
}
for(var prop in obj){
if(prop==null){
delete _657[prop];
}else{
if(typeof obj[prop]=="string"||typeof obj[prop]=="number"){
_657[prop]=obj[prop];
}
}
}
prop=null;
for(var prop in _657){
_656.push(escape(prop)+"="+escape(_657[prop]));
}
_658=_656.join("&");
}
dojo.io.cookie.setCookie(name,_658,days,path,_653,_654);
};
dojo.io.cookie.getObjectCookie=function(name){
var _65b=null,_65c=dojo.io.cookie.getCookie(name);
if(_65c){
_65b={};
var _65d=_65c.split("&");
for(var i=0;i<_65d.length;i++){
var pair=_65d[i].split("=");
var _660=pair[1];
if(isNaN(_660)){
_660=unescape(pair[1]);
}
_65b[unescape(pair[0])]=_660;
}
}
return _65b;
};
dojo.io.cookie.isSupported=function(){
if(typeof navigator.cookieEnabled!="boolean"){
dojo.io.cookie.setCookie("__TestingYourBrowserForCookieSupport__","CookiesAllowed",90,null);
var _661=dojo.io.cookie.getCookie("__TestingYourBrowserForCookieSupport__");
navigator.cookieEnabled=(_661=="CookiesAllowed");
if(navigator.cookieEnabled){
this.deleteCookie("__TestingYourBrowserForCookieSupport__");
}
}
return navigator.cookieEnabled;
};
if(!dojo.io.cookies){
dojo.io.cookies=dojo.io.cookie;
}
dojo.provide("dojo.io.*");
dojo.provide("dojo.uri.*");
dojo.provide("dojo.io.IframeIO");
dojo.io.createIFrame=function(_662,_663,uri){
if(window[_662]){
return window[_662];
}
if(window.frames[_662]){
return window.frames[_662];
}
var r=dojo.render.html;
var _666=null;
var turi=uri||dojo.uri.dojoUri("iframe_history.html?noInit=true");
var _668=((r.ie)&&(dojo.render.os.win))?"<iframe name=\""+_662+"\" src=\""+turi+"\" onload=\""+_663+"\">":"iframe";
_666=document.createElement(_668);
with(_666){
name=_662;
setAttribute("name",_662);
id=_662;
}
dojo.body().appendChild(_666);
window[_662]=_666;
with(_666.style){
if(!r.safari){
position="absolute";
}
left=top="0px";
height=width="1px";
visibility="hidden";
}
if(!r.ie){
dojo.io.setIFrameSrc(_666,turi,true);
_666.onload=new Function(_663);
}
return _666;
};
dojo.io.IframeTransport=new function(){
var _669=this;
this.currentRequest=null;
this.requestQueue=[];
this.iframeName="dojoIoIframe";
this.fireNextRequest=function(){
try{
if((this.currentRequest)||(this.requestQueue.length==0)){
return;
}
var cr=this.currentRequest=this.requestQueue.shift();
cr._contentToClean=[];
var fn=cr["formNode"];
var _66c=cr["content"]||{};
if(cr.sendTransport){
_66c["dojo.transport"]="iframe";
}
if(fn){
if(_66c){
for(var x in _66c){
if(!fn[x]){
var tn;
if(dojo.render.html.ie){
tn=document.createElement("<input type='hidden' name='"+x+"' value='"+_66c[x]+"'>");
fn.appendChild(tn);
}else{
tn=document.createElement("input");
fn.appendChild(tn);
tn.type="hidden";
tn.name=x;
tn.value=_66c[x];
}
cr._contentToClean.push(x);
}else{
fn[x].value=_66c[x];
}
}
}
if(cr["url"]){
cr._originalAction=fn.getAttribute("action");
fn.setAttribute("action",cr.url);
}
if(!fn.getAttribute("method")){
fn.setAttribute("method",(cr["method"])?cr["method"]:"post");
}
cr._originalTarget=fn.getAttribute("target");
fn.setAttribute("target",this.iframeName);
fn.target=this.iframeName;
fn.submit();
}else{
var _66f=dojo.io.argsFromMap(this.currentRequest.content);
var _670=cr.url+(cr.url.indexOf("?")>-1?"&":"?")+_66f;
dojo.io.setIFrameSrc(this.iframe,_670,true);
}
}
catch(e){
this.iframeOnload(e);
}
};
this.canHandle=function(_671){
return ((dojo.lang.inArray(["text/plain","text/html","text/javascript","text/json","application/json"],_671["mimetype"]))&&(dojo.lang.inArray(["post","get"],_671["method"].toLowerCase()))&&(!((_671["sync"])&&(_671["sync"]==true))));
};
this.bind=function(_672){
if(!this["iframe"]){
this.setUpIframe();
}
this.requestQueue.push(_672);
this.fireNextRequest();
return;
};
this.setUpIframe=function(){
this.iframe=dojo.io.createIFrame(this.iframeName,"dojo.io.IframeTransport.iframeOnload();");
};
this.iframeOnload=function(_673){
if(!_669.currentRequest){
_669.fireNextRequest();
return;
}
var req=_669.currentRequest;
if(req.formNode){
var _675=req._contentToClean;
for(var i=0;i<_675.length;i++){
var key=_675[i];
if(dojo.render.html.safari){
var _678=req.formNode;
for(var j=0;j<_678.childNodes.length;j++){
var _67a=_678.childNodes[j];
if(_67a.name==key){
var _67b=_67a.parentNode;
_67b.removeChild(_67a);
break;
}
}
}else{
var _67c=req.formNode[key];
req.formNode.removeChild(_67c);
req.formNode[key]=null;
}
}
if(req["_originalAction"]){
req.formNode.setAttribute("action",req._originalAction);
}
if(req["_originalTarget"]){
req.formNode.setAttribute("target",req._originalTarget);
req.formNode.target=req._originalTarget;
}
}
var _67d=function(_67e){
var doc=_67e.contentDocument||((_67e.contentWindow)&&(_67e.contentWindow.document))||((_67e.name)&&(document.frames[_67e.name])&&(document.frames[_67e.name].document))||null;
return doc;
};
var _680;
var _681=false;
if(_673){
this._callError(req,"IframeTransport Request Error: "+_673);
}else{
var ifd=_67d(_669.iframe);
try{
var cmt=req.mimetype;
if((cmt=="text/javascript")||(cmt=="text/json")||(cmt=="application/json")){
var js=ifd.getElementsByTagName("textarea")[0].value;
if(cmt=="text/json"||cmt=="application/json"){
js="("+js+")";
}
_680=dj_eval(js);
}else{
if(cmt=="text/html"){
_680=ifd;
}else{
_680=ifd.getElementsByTagName("textarea")[0].value;
}
}
_681=true;
}
catch(e){
this._callError(req,"IframeTransport Error: "+e);
}
}
try{
if(_681&&dojo.lang.isFunction(req["load"])){
req.load("load",_680,req);
}
}
catch(e){
throw e;
}
finally{
_669.currentRequest=null;
_669.fireNextRequest();
}
};
this._callError=function(req,_686){
var _687=new dojo.io.Error(_686);
if(dojo.lang.isFunction(req["error"])){
req.error("error",_687,req);
}
};
dojo.io.transports.addTransport("IframeTransport");
};
dojo.provide("dojo.date");
dojo.deprecated("dojo.date","use one of the modules in dojo.date.* instead","0.5");
dojo.provide("dojo.string.Builder");
dojo.string.Builder=function(str){
this.arrConcat=(dojo.render.html.capable&&dojo.render.html["ie"]);
var a=[];
var b="";
var _68b=this.length=b.length;
if(this.arrConcat){
if(b.length>0){
a.push(b);
}
b="";
}
this.toString=this.valueOf=function(){
return (this.arrConcat)?a.join(""):b;
};
this.append=function(){
for(var x=0;x<arguments.length;x++){
var s=arguments[x];
if(dojo.lang.isArrayLike(s)){
this.append.apply(this,s);
}else{
if(this.arrConcat){
a.push(s);
}else{
b+=s;
}
_68b+=s.length;
this.length=_68b;
}
}
return this;
};
this.clear=function(){
a=[];
b="";
_68b=this.length=0;
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
_68b=this.length=b.length;
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
_68b=this.length=b.length;
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
_68b=this.length=b.length;
if(this.arrConcat){
a.push(b);
b="";
}
return this;
};
this.append.apply(this,arguments);
};
dojo.provide("dojo.string.*");
if(!this["dojo"]){
alert("\"dojo/__package__.js\" is now located at \"dojo/dojo.js\". Please update your includes accordingly");
}
dojo.provide("dojo.AdapterRegistry");
dojo.AdapterRegistry=function(_696){
this.pairs=[];
this.returnWrappers=_696||false;
};
dojo.lang.extend(dojo.AdapterRegistry,{register:function(name,_698,wrap,_69a,_69b){
var type=(_69b)?"unshift":"push";
this.pairs[type]([name,_698,wrap,_69a]);
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
dojo.provide("dojo.json");
dojo.json={jsonRegistry:new dojo.AdapterRegistry(),register:function(name,_6a3,wrap,_6a5){
dojo.json.jsonRegistry.register(name,_6a3,wrap,_6a5);
},evalJson:function(json){
try{
return eval("("+json+")");
}
catch(e){
dojo.debug(e);
return json;
}
},serialize:function(o){
var _6a8=typeof (o);
if(_6a8=="undefined"){
return "undefined";
}else{
if((_6a8=="number")||(_6a8=="boolean")){
return o+"";
}else{
if(o===null){
return "null";
}
}
}
if(_6a8=="string"){
return dojo.string.escapeString(o);
}
var me=arguments.callee;
var _6aa;
if(typeof (o.__json__)=="function"){
_6aa=o.__json__();
if(o!==_6aa){
return me(_6aa);
}
}
if(typeof (o.json)=="function"){
_6aa=o.json();
if(o!==_6aa){
return me(_6aa);
}
}
if(_6a8!="function"&&typeof (o.length)=="number"){
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
window.o=o;
_6aa=dojo.json.jsonRegistry.match(o);
return me(_6aa);
}
catch(e){
}
if(_6a8=="function"){
return null;
}
res=[];
for(var k in o){
var _6af;
if(typeof (k)=="number"){
_6af="\""+k+"\"";
}else{
if(typeof (k)=="string"){
_6af=dojo.string.escapeString(k);
}else{
continue;
}
}
val=me(o[k]);
if(typeof (val)!="string"){
continue;
}
res.push(_6af+":"+val);
}
return "{"+res.join(",")+"}";
}};
dojo.provide("dojo.Deferred");
dojo.Deferred=function(_6b0){
this.chain=[];
this.id=this._nextId();
this.fired=-1;
this.paused=0;
this.results=[null,null];
this.canceller=_6b0;
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
var _6b2=new dojo.Deferred();
_6b2.callback();
return _6b2;
},repr:function(){
var _6b3;
if(this.fired==-1){
_6b3="unfired";
}else{
if(this.fired==0){
_6b3="success";
}else{
_6b3="error";
}
}
return "Deferred("+this.id+", "+_6b3+")";
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
var _6bb=this.getFunctionFromArgs(cb,cbfn);
if(arguments.length>2){
_6bb=dojo.lang.curryArguments(null,_6bb,arguments,2);
}
return this.addCallbacks(_6bb,_6bb);
},addCallback:function(cb,cbfn){
var _6be=this.getFunctionFromArgs(cb,cbfn);
if(arguments.length>2){
_6be=dojo.lang.curryArguments(null,_6be,arguments,2);
}
return this.addCallbacks(_6be,null);
},addErrback:function(cb,cbfn){
var _6c1=this.getFunctionFromArgs(cb,cbfn);
if(arguments.length>2){
_6c1=dojo.lang.curryArguments(null,_6c1,arguments,2);
}
return this.addCallbacks(null,_6c1);
return this.addCallbacks(null,cbfn);
},addCallbacks:function(cb,eb){
this.chain.push([cb,eb]);
if(this.fired>=0){
this._fire();
}
return this;
},_fire:function(){
var _6c4=this.chain;
var _6c5=this.fired;
var res=this.results[_6c5];
var self=this;
var cb=null;
while(_6c4.length>0&&this.paused==0){
var pair=_6c4.shift();
var f=pair[_6c5];
if(f==null){
continue;
}
try{
res=f(res);
_6c5=((res instanceof Error)?1:0);
if(res instanceof dojo.Deferred){
cb=function(res){
self._continue(res);
};
this._pause();
}
}
catch(err){
_6c5=1;
res=err;
}
}
this.fired=_6c5;
this.results[_6c5]=res;
if((cb)&&(this.paused)){
res.addBoth(cb);
}
}});
dojo.provide("dojo.rpc.RpcService");
dojo.rpc.RpcService=function(url){
if(url){
this.connect(url);
}
};
dojo.lang.extend(dojo.rpc.RpcService,{strictArgChecks:true,serviceUrl:"",parseResults:function(obj){
return obj;
},errorCallback:function(_6ce){
return function(type,e){
_6ce.errback(new Error(e.message));
};
},resultCallback:function(_6d1){
var tf=dojo.lang.hitch(this,function(type,obj,e){
if(obj["error"]!=null){
var err=new Error(obj.error);
err.id=obj.id;
_6d1.errback(err);
}else{
var _6d7=this.parseResults(obj);
_6d1.callback(_6d7);
}
});
return tf;
},generateMethod:function(_6d8,_6d9,url){
return dojo.lang.hitch(this,function(){
var _6db=new dojo.Deferred();
if((this.strictArgChecks)&&(_6d9!=null)&&(arguments.length!=_6d9.length)){
dojo.raise("Invalid number of parameters for remote method.");
}else{
this.bind(_6d8,arguments,_6db,url);
}
return _6db;
});
},processSmd:function(_6dc){
dojo.debug("RpcService: Processing returned SMD.");
if(_6dc.methods){
dojo.lang.forEach(_6dc.methods,function(m){
if(m&&m["name"]){
dojo.debug("RpcService: Creating Method: this.",m.name,"()");
this[m.name]=this.generateMethod(m.name,m.parameters,m["url"]||m["serviceUrl"]||m["serviceURL"]);
if(dojo.lang.isFunction(this[m.name])){
dojo.debug("RpcService: Successfully created",m.name,"()");
}else{
dojo.debug("RpcService: Failed to create",m.name,"()");
}
}
},this);
}
this.serviceUrl=_6dc.serviceUrl||_6dc.serviceURL;
dojo.debug("RpcService: Dojo RpcService is ready for use.");
},connect:function(_6de){
dojo.debug("RpcService: Attempting to load SMD document from:",_6de);
dojo.io.bind({url:_6de,mimetype:"text/json",load:dojo.lang.hitch(this,function(type,_6e0,e){
return this.processSmd(_6e0);
}),sync:true});
}});
dojo.provide("dojo.rpc.JsonService");
dojo.rpc.JsonService=function(args){
if(args){
if(dojo.lang.isString(args)){
this.connect(args);
}else{
if(args["smdUrl"]){
this.connect(args.smdUrl);
}
if(args["smdStr"]){
this.processSmd(dj_eval("("+args.smdStr+")"));
}
if(args["smdObj"]){
this.processSmd(args.smdObj);
}
if(args["serviceUrl"]){
this.serviceUrl=args.serviceUrl;
}
if(typeof args["strictArgChecks"]!="undefined"){
this.strictArgChecks=args.strictArgChecks;
}
}
}
};
dojo.inherits(dojo.rpc.JsonService,dojo.rpc.RpcService);
dojo.extend(dojo.rpc.JsonService,{bustCache:false,contentType:"application/json-rpc",lastSubmissionId:0,callRemote:function(_6e3,_6e4){
var _6e5=new dojo.Deferred();
this.bind(_6e3,_6e4,_6e5);
return _6e5;
},bind:function(_6e6,_6e7,_6e8,url){
dojo.io.bind({url:url||this.serviceUrl,postContent:this.createRequest(_6e6,_6e7),method:"POST",contentType:this.contentType,mimetype:"text/json",load:this.resultCallback(_6e8),error:this.errorCallback(_6e8),preventCache:this.bustCache});
},createRequest:function(_6ea,_6eb){
var req={"params":_6eb,"method":_6ea,"id":++this.lastSubmissionId};
var data=dojo.json.serialize(req);
dojo.debug("JsonService: JSON-RPC Request: "+data);
return data;
},parseResults:function(obj){
if(!obj){
return;
}
if(obj["Result"]!=null){
return obj["Result"];
}else{
if(obj["result"]!=null){
return obj["result"];
}else{
if(obj["ResultSet"]){
return obj["ResultSet"];
}else{
return obj;
}
}
}
}});
dojo.provide("dojo.rpc.*");
dojo.provide("dojo.xml.Parse");
dojo.xml.Parse=function(){
function getTagName(node){
return ((node)&&(node.tagName)?node.tagName.toLowerCase():"");
}
function getDojoTagName(node){
var _6f1=getTagName(node);
if(!_6f1){
return "";
}
if((dojo.widget)&&(dojo.widget.tags[_6f1])){
return _6f1;
}
var p=_6f1.indexOf(":");
if(p>=0){
return _6f1;
}
if(_6f1.substr(0,5)=="dojo:"){
return _6f1;
}
if(dojo.render.html.capable&&dojo.render.html.ie&&node.scopeName!="HTML"){
return node.scopeName.toLowerCase()+":"+_6f1;
}
if(_6f1.substr(0,4)=="dojo"){
return "dojo:"+_6f1.substring(4);
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
if((!dj_global["djConfig"])||(djConfig["ignoreClassNames"])){
var _6f4=node.className||node.getAttribute("class");
if((_6f4)&&(_6f4.indexOf)&&(_6f4.indexOf("dojo-")!=-1)){
var _6f5=_6f4.split(" ");
for(var x=0,c=_6f5.length;x<c;x++){
if(_6f5[x].slice(0,5)=="dojo-"){
return "dojo:"+_6f5[x].substr(5).toLowerCase();
}
}
}
}
return "";
}
this.parseElement=function(node,_6f9,_6fa,_6fb){
var _6fc={};
var _6fd=getTagName(node);
if((_6fd)&&(_6fd.indexOf("/")==0)){
return null;
}
var _6fe=true;
if(_6fa){
var _6ff=getDojoTagName(node);
_6fd=_6ff||_6fd;
_6fe=Boolean(_6ff);
}
if(node&&node.getAttribute&&node.getAttribute("parseWidgets")&&node.getAttribute("parseWidgets")=="false"){
return {};
}
_6fc[_6fd]=[];
var pos=_6fd.indexOf(":");
if(pos>0){
var ns=_6fd.substring(0,pos);
_6fc["ns"]=ns;
if((dojo.ns)&&(!dojo.ns.allow(ns))){
_6fe=false;
}
}
if(_6fe){
var _702=this.parseAttributes(node);
for(var attr in _702){
if((!_6fc[_6fd][attr])||(typeof _6fc[_6fd][attr]!="array")){
_6fc[_6fd][attr]=[];
}
_6fc[_6fd][attr].push(_702[attr]);
}
_6fc[_6fd].nodeRef=node;
_6fc.tagName=_6fd;
_6fc.index=_6fb||0;
}
var _704=0;
for(var i=0;i<node.childNodes.length;i++){
var tcn=node.childNodes.item(i);
switch(tcn.nodeType){
case dojo.dom.ELEMENT_NODE:
_704++;
var ctn=getDojoTagName(tcn)||getTagName(tcn);
if(!_6fc[ctn]){
_6fc[ctn]=[];
}
_6fc[ctn].push(this.parseElement(tcn,true,_6fa,_704));
if((tcn.childNodes.length==1)&&(tcn.childNodes.item(0).nodeType==dojo.dom.TEXT_NODE)){
_6fc[ctn][_6fc[ctn].length-1].value=tcn.childNodes.item(0).nodeValue;
}
break;
case dojo.dom.TEXT_NODE:
if(node.childNodes.length==1){
_6fc[_6fd].push({value:node.childNodes.item(0).nodeValue});
}
break;
default:
break;
}
}
return _6fc;
};
this.parseAttributes=function(node){
var _709={};
var atts=node.attributes;
var _70b,i=0;
while((_70b=atts[i++])){
if((dojo.render.html.capable)&&(dojo.render.html.ie)){
if(!_70b){
continue;
}
if((typeof _70b=="object")&&(typeof _70b.nodeValue=="undefined")||(_70b.nodeValue==null)||(_70b.nodeValue=="")){
continue;
}
}
var nn=_70b.nodeName.split(":");
nn=(nn.length==2)?nn[1]:_70b.nodeName;
_709[nn]={value:_70b.nodeValue};
}
return _709;
};
};
dojo.provide("dojo.xml.*");
dojo.provide("dojo.undo.Manager");
dojo.undo.Manager=function(_70e){
this.clear();
this._parent=_70e;
};
dojo.extend(dojo.undo.Manager,{_parent:null,_undoStack:null,_redoStack:null,_currentManager:null,canUndo:false,canRedo:false,isUndoing:false,isRedoing:false,onUndo:function(_70f,item){
},onRedo:function(_711,item){
},onUndoAny:function(_713,item){
},onRedoAny:function(_715,item){
},_updateStatus:function(){
this.canUndo=this._undoStack.length>0;
this.canRedo=this._redoStack.length>0;
},clear:function(){
this._undoStack=[];
this._redoStack=[];
this._currentManager=this;
this.isUndoing=false;
this.isRedoing=false;
this._updateStatus();
},undo:function(){
if(!this.canUndo){
return false;
}
this.endAllTransactions();
this.isUndoing=true;
var top=this._undoStack.pop();
if(top instanceof dojo.undo.Manager){
top.undoAll();
}else{
top.undo();
}
if(top.redo){
this._redoStack.push(top);
}
this.isUndoing=false;
this._updateStatus();
this.onUndo(this,top);
if(!(top instanceof dojo.undo.Manager)){
this.getTop().onUndoAny(this,top);
}
return true;
},redo:function(){
if(!this.canRedo){
return false;
}
this.isRedoing=true;
var top=this._redoStack.pop();
if(top instanceof dojo.undo.Manager){
top.redoAll();
}else{
top.redo();
}
this._undoStack.push(top);
this.isRedoing=false;
this._updateStatus();
this.onRedo(this,top);
if(!(top instanceof dojo.undo.Manager)){
this.getTop().onRedoAny(this,top);
}
return true;
},undoAll:function(){
while(this._undoStack.length>0){
this.undo();
}
},redoAll:function(){
while(this._redoStack.length>0){
this.redo();
}
},push:function(undo,redo,_71b){
if(!undo){
return;
}
if(this._currentManager==this){
this._undoStack.push({undo:undo,redo:redo,description:_71b});
}else{
this._currentManager.push.apply(this._currentManager,arguments);
}
this._redoStack=[];
this._updateStatus();
},concat:function(_71c){
if(!_71c){
return;
}
if(this._currentManager==this){
for(var x=0;x<_71c._undoStack.length;x++){
this._undoStack.push(_71c._undoStack[x]);
}
if(_71c._undoStack.length>0){
this._redoStack=[];
}
this._updateStatus();
}else{
this._currentManager.concat.apply(this._currentManager,arguments);
}
},beginTransaction:function(_71e){
if(this._currentManager==this){
var mgr=new dojo.undo.Manager(this);
mgr.description=_71e?_71e:"";
this._undoStack.push(mgr);
this._currentManager=mgr;
return mgr;
}else{
this._currentManager=this._currentManager.beginTransaction.apply(this._currentManager,arguments);
}
},endTransaction:function(_720){
if(this._currentManager==this){
if(this._parent){
this._parent._currentManager=this._parent;
if(this._undoStack.length==0||_720){
var idx=dojo.lang.find(this._parent._undoStack,this);
if(idx>=0){
this._parent._undoStack.splice(idx,1);
if(_720){
for(var x=0;x<this._undoStack.length;x++){
this._parent._undoStack.splice(idx++,0,this._undoStack[x]);
}
this._updateStatus();
}
}
}
return this._parent;
}
}else{
this._currentManager=this._currentManager.endTransaction.apply(this._currentManager,arguments);
}
},endAllTransactions:function(){
while(this._currentManager!=this){
this.endTransaction();
}
},getTop:function(){
if(this._parent){
return this._parent.getTop();
}else{
return this;
}
}});
dojo.provide("dojo.undo.*");
dojo.provide("dojo.crypto");
dojo.crypto.cipherModes={ECB:0,CBC:1,PCBC:2,CFB:3,OFB:4,CTR:5};
dojo.crypto.outputTypes={Base64:0,Hex:1,String:2,Raw:3};
dojo.provide("dojo.crypto.MD5");
dojo.crypto.MD5=new function(){
var _723=8;
var mask=(1<<_723)-1;
function toWord(s){
var wa=[];
for(var i=0;i<s.length*_723;i+=_723){
wa[i>>5]|=(s.charCodeAt(i/_723)&mask)<<(i%32);
}
return wa;
}
function toString(wa){
var s=[];
for(var i=0;i<wa.length*32;i+=_723){
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
wa=core(wa,key.length*_723);
}
var l=[],r=[];
for(var i=0;i<16;i++){
l[i]=wa[i]^909522486;
r[i]=wa[i]^1549556828;
}
var h=core(l.concat(toWord(data)),512+data.length*_723);
return core(r.concat(h),640);
}
this.compute=function(data,_771){
var out=_771||dojo.crypto.outputTypes.Base64;
switch(out){
case dojo.crypto.outputTypes.Hex:
return toHex(core(toWord(data),data.length*_723));
case dojo.crypto.outputTypes.String:
return toString(core(toWord(data),data.length*_723));
default:
return toBase64(core(toWord(data),data.length*_723));
}
};
this.getHMAC=function(data,key,_775){
var out=_775||dojo.crypto.outputTypes.Base64;
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
dojo.provide("dojo.crypto.*");
dojo.provide("dojo.collections.Collections");
dojo.collections.DictionaryEntry=function(k,v){
this.key=k;
this.value=v;
this.valueOf=function(){
return this.value;
};
this.toString=function(){
return String(this.value);
};
};
dojo.collections.Iterator=function(arr){
var a=arr;
var _77b=0;
this.element=a[_77b]||null;
this.atEnd=function(){
return (_77b>=a.length);
};
this.get=function(){
if(this.atEnd()){
return null;
}
this.element=a[_77b++];
return this.element;
};
this.map=function(fn,_77d){
var s=_77d||dj_global;
if(Array.map){
return Array.map(a,fn,s);
}else{
var arr=[];
for(var i=0;i<a.length;i++){
arr.push(fn.call(s,a[i]));
}
return arr;
}
};
this.reset=function(){
_77b=0;
this.element=a[_77b];
};
};
dojo.collections.DictionaryIterator=function(obj){
var a=[];
var _783={};
for(var p in obj){
if(!_783[p]){
a.push(obj[p]);
}
}
var _785=0;
this.element=a[_785]||null;
this.atEnd=function(){
return (_785>=a.length);
};
this.get=function(){
if(this.atEnd()){
return null;
}
this.element=a[_785++];
return this.element;
};
this.map=function(fn,_787){
var s=_787||dj_global;
if(Array.map){
return Array.map(a,fn,s);
}else{
var arr=[];
for(var i=0;i<a.length;i++){
arr.push(fn.call(s,a[i]));
}
return arr;
}
};
this.reset=function(){
_785=0;
this.element=a[_785];
};
};
dojo.provide("dojo.collections.ArrayList");
dojo.collections.ArrayList=function(arr){
var _78c=[];
if(arr){
_78c=_78c.concat(arr);
}
this.count=_78c.length;
this.add=function(obj){
_78c.push(obj);
this.count=_78c.length;
};
this.addRange=function(a){
if(a.getIterator){
var e=a.getIterator();
while(!e.atEnd()){
this.add(e.get());
}
this.count=_78c.length;
}else{
for(var i=0;i<a.length;i++){
_78c.push(a[i]);
}
this.count=_78c.length;
}
};
this.clear=function(){
_78c.splice(0,_78c.length);
this.count=0;
};
this.clone=function(){
return new dojo.collections.ArrayList(_78c);
};
this.contains=function(obj){
for(var i=0;i<_78c.length;i++){
if(_78c[i]==obj){
return true;
}
}
return false;
};
this.forEach=function(fn,_794){
var s=_794||dj_global;
if(Array.forEach){
Array.forEach(_78c,fn,s);
}else{
for(var i=0;i<_78c.length;i++){
fn.call(s,_78c[i],i,_78c);
}
}
};
this.getIterator=function(){
return new dojo.collections.Iterator(_78c);
};
this.indexOf=function(obj){
for(var i=0;i<_78c.length;i++){
if(_78c[i]==obj){
return i;
}
}
return -1;
};
this.insert=function(i,obj){
_78c.splice(i,0,obj);
this.count=_78c.length;
};
this.item=function(i){
return _78c[i];
};
this.remove=function(obj){
var i=this.indexOf(obj);
if(i>=0){
_78c.splice(i,1);
}
this.count=_78c.length;
};
this.removeAt=function(i){
_78c.splice(i,1);
this.count=_78c.length;
};
this.reverse=function(){
_78c.reverse();
};
this.sort=function(fn){
if(fn){
_78c.sort(fn);
}else{
_78c.sort();
}
};
this.setByIndex=function(i,obj){
_78c[i]=obj;
this.count=_78c.length;
};
this.toArray=function(){
return [].concat(_78c);
};
this.toString=function(_7a2){
return _78c.join((_7a2||","));
};
};
dojo.provide("dojo.collections.Queue");
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
this.forEach=function(fn,_7ac){
var s=_7ac||dj_global;
if(Array.forEach){
Array.forEach(q,fn,s);
}else{
for(var i=0;i<q.length;i++){
fn.call(s,q[i],i,q);
}
}
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
this.forEach=function(fn,_7b6){
var s=_7b6||dj_global;
if(Array.forEach){
Array.forEach(q,fn,s);
}else{
for(var i=0;i<q.length;i++){
fn.call(s,q[i],i,q);
}
}
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
dojo.provide("dojo.lang.declare");
dojo.lang.declare=function(_7bb,_7bc,init,_7be){
if((dojo.lang.isFunction(_7be))||((!_7be)&&(!dojo.lang.isFunction(init)))){
var temp=_7be;
_7be=init;
init=temp;
}
var _7c0=[];
if(dojo.lang.isArray(_7bc)){
_7c0=_7bc;
_7bc=_7c0.shift();
}
if(!init){
init=dojo.evalObjPath(_7bb,false);
if((init)&&(!dojo.lang.isFunction(init))){
init=null;
}
}
var ctor=dojo.lang.declare._makeConstructor();
var scp=(_7bc?_7bc.prototype:null);
if(scp){
scp.prototyping=true;
ctor.prototype=new _7bc();
scp.prototyping=false;
}
ctor.superclass=scp;
ctor.mixins=_7c0;
for(var i=0,l=_7c0.length;i<l;i++){
dojo.lang.extend(ctor,_7c0[i].prototype);
}
ctor.prototype.initializer=null;
ctor.prototype.declaredClass=_7bb;
if(dojo.lang.isArray(_7be)){
dojo.lang.extend.apply(dojo.lang,[ctor].concat(_7be));
}else{
dojo.lang.extend(ctor,(_7be)||{});
}
dojo.lang.extend(ctor,dojo.lang.declare._common);
ctor.prototype.constructor=ctor;
ctor.prototype.initializer=(ctor.prototype.initializer)||(init)||(function(){
});
dojo.lang.setObjPathValue(_7bb,ctor,null,true);
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
},_contextMethod:function(_7ca,_7cb,args){
var _7cd,_7ce=this.___proto;
this.___proto=_7ca;
try{
_7cd=_7ca[_7cb].apply(this,(args||[]));
}
catch(e){
throw e;
}
finally{
this.___proto=_7ce;
}
return _7cd;
},_inherited:function(prop,args){
var p=this._getPropContext();
do{
if((!p.constructor)||(!p.constructor.superclass)){
return;
}
p=p.constructor.superclass;
}while(!(prop in p));
return (dojo.lang.isFunction(p[prop])?this._contextMethod(p,prop,args):p[prop]);
}};
dojo.declare=dojo.lang.declare;
dojo.provide("dojo.dnd.DragAndDrop");
dojo.declare("dojo.dnd.DragSource",null,{type:"",onDragEnd:function(){
},onDragStart:function(){
},onSelected:function(){
},unregister:function(){
dojo.dnd.dragManager.unregisterDragSource(this);
},reregister:function(){
dojo.dnd.dragManager.registerDragSource(this);
}},function(){
var dm=dojo.dnd.dragManager;
if(dm["registerDragSource"]){
dm.registerDragSource(this);
}
});
dojo.declare("dojo.dnd.DragObject",null,{type:"",onDragStart:function(){
},onDragMove:function(){
},onDragOver:function(){
},onDragOut:function(){
},onDragEnd:function(){
},onDragLeave:this.onDragOut,onDragEnter:this.onDragOver,ondragout:this.onDragOut,ondragover:this.onDragOver},function(){
var dm=dojo.dnd.dragManager;
if(dm["registerDragObject"]){
dm.registerDragObject(this);
}
});
dojo.declare("dojo.dnd.DropTarget",null,{acceptsType:function(type){
if(!dojo.lang.inArray(this.acceptedTypes,"*")){
if(!dojo.lang.inArray(this.acceptedTypes,type)){
return false;
}
}
return true;
},accepts:function(_7d5){
if(!dojo.lang.inArray(this.acceptedTypes,"*")){
for(var i=0;i<_7d5.length;i++){
if(!dojo.lang.inArray(this.acceptedTypes,_7d5[i].type)){
return false;
}
}
}
return true;
},unregister:function(){
dojo.dnd.dragManager.unregisterDropTarget(this);
},onDragOver:function(){
},onDragOut:function(){
},onDragMove:function(){
},onDropStart:function(){
},onDrop:function(){
},onDropEnd:function(){
}},function(){
if(this.constructor==dojo.dnd.DropTarget){
return;
}
this.acceptedTypes=[];
dojo.dnd.dragManager.registerDropTarget(this);
});
dojo.dnd.DragEvent=function(){
this.dragSource=null;
this.dragObject=null;
this.target=null;
this.eventStatus="success";
};
dojo.declare("dojo.dnd.DragManager",null,{selectedSources:[],dragObjects:[],dragSources:[],registerDragSource:function(){
},dropTargets:[],registerDropTarget:function(){
},lastDragTarget:null,currentDragTarget:null,onKeyDown:function(){
},onMouseOut:function(){
},onMouseMove:function(){
},onMouseUp:function(){
}});
dojo.provide("dojo.dnd.HtmlDragManager");
dojo.declare("dojo.dnd.HtmlDragManager",dojo.dnd.DragManager,{disabled:false,nestedTargets:false,mouseDownTimer:null,dsCounter:0,dsPrefix:"dojoDragSource",dropTargetDimensions:[],currentDropTarget:null,previousDropTarget:null,_dragTriggered:false,selectedSources:[],dragObjects:[],currentX:null,currentY:null,lastX:null,lastY:null,mouseDownX:null,mouseDownY:null,threshold:7,dropAcceptable:false,cancelEvent:function(e){
e.stopPropagation();
e.preventDefault();
},registerDragSource:function(ds){
if(ds["domNode"]){
var dp=this.dsPrefix;
var _7da=dp+"Idx_"+(this.dsCounter++);
ds.dragSourceId=_7da;
this.dragSources[_7da]=ds;
ds.domNode.setAttribute(dp,_7da);
if(dojo.render.html.ie){
dojo.event.browser.addListener(ds.domNode,"ondragstart",this.cancelEvent);
}
}
},unregisterDragSource:function(ds){
if(ds["domNode"]){
var dp=this.dsPrefix;
var _7dd=ds.dragSourceId;
delete ds.dragSourceId;
delete this.dragSources[_7dd];
ds.domNode.setAttribute(dp,null);
if(dojo.render.html.ie){
dojo.event.browser.removeListener(ds.domNode,"ondragstart",this.cancelEvent);
}
}
},registerDropTarget:function(dt){
this.dropTargets.push(dt);
},unregisterDropTarget:function(dt){
var _7e0=dojo.lang.find(this.dropTargets,dt,true);
if(_7e0>=0){
this.dropTargets.splice(_7e0,1);
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
var _7e6=e.target.nodeType==dojo.html.TEXT_NODE?e.target.parentNode:e.target;
if(dojo.html.isTag(_7e6,"button","textarea","input","select","option")){
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
},onMouseUp:function(e,_7e9){
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
dojo.lang.forEach(this.dragObjects,function(_7ea){
var ret=null;
if(!_7ea){
return;
}
if(this.currentDropTarget){
e.dragObject=_7ea;
var ce=this.currentDropTarget.domNode.childNodes;
if(ce.length>0){
e.dropTarget=ce[0];
while(e.dropTarget==_7ea.domNode){
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
_7ea.dragSource.onDragEnd(e);
}
catch(err){
var _7ed={};
for(var i in e){
if(i=="type"){
_7ed.type="mouseup";
continue;
}
_7ed[i]=e[i];
}
_7ea.dragSource.onDragEnd(_7ed);
}
},function(){
_7ea.onDragEnd(e);
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
dojo.lang.forEach(this.dropTargets,function(_7f6){
var tn=_7f6.domNode;
if(!tn||dojo.lang.find(_7f6.acceptedTypes,this.dragSource.type)<0){
return;
}
var abs=dojo.html.getAbsolutePosition(tn,true);
var bb=dojo.html.getBorderBox(tn);
this.dropTargetDimensions.push([[abs.x,abs.y],[abs.x+bb.width,abs.y+bb.height],_7f6]);
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
dojo.lang.forEach(this.selectedSources,function(_7fd){
if(!_7fd){
return;
}
var tdo=_7fd.onDragStart(e);
if(tdo){
tdo.onDragStart(e);
tdo.dragOffset.y+=dy;
tdo.dragOffset.x+=dx;
tdo.dragSource=_7fd;
this.dragObjects.push(tdo);
}
},this);
this.previousDropTarget=null;
this.cacheTargetLocations();
}
dojo.lang.forEach(this.dragObjects,function(_7ff){
if(_7ff){
_7ff.onDragMove(e);
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
var _802=this.findBestTarget(e);
if(_802.target===null){
if(this.currentDropTarget){
this.currentDropTarget.onDragOut(e);
this.previousDropTarget=this.currentDropTarget;
this.currentDropTarget=null;
}
this.dropAcceptable=false;
return;
}
if(this.currentDropTarget!==_802.target){
if(this.currentDropTarget){
this.previousDropTarget=this.currentDropTarget;
this.currentDropTarget.onDragOut(e);
}
this.currentDropTarget=_802.target;
e.dragObjects=this.dragObjects;
this.dropAcceptable=this.currentDropTarget.onDragOver(e);
}else{
if(this.dropAcceptable){
this.currentDropTarget.onDragMove(e,this.dragObjects);
}
}
}
},findBestTarget:function(e){
var _804=this;
var _805=new Object();
_805.target=null;
_805.points=null;
dojo.lang.every(this.dropTargetDimensions,function(_806){
if(!_804.isInsideBox(e,_806)){
return true;
}
_805.target=_806[2];
_805.points=_806;
return Boolean(_804.nestedTargets);
});
return _805;
},isInsideBox:function(e,_808){
if((e.pageX>_808[0][0])&&(e.pageX<_808[1][0])&&(e.pageY>_808[0][1])&&(e.pageY<_808[1][1])){
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
dojo.provide("dojo.html.selection");
dojo.html.selectionType={NONE:0,TEXT:1,CONTROL:2};
dojo.html.clearSelection=function(){
var _80d=dojo.global();
var _80e=dojo.doc();
try{
if(_80d["getSelection"]){
if(dojo.render.html.safari){
_80d.getSelection().collapse();
}else{
_80d.getSelection().removeAllRanges();
}
}else{
if(_80e.selection){
if(_80e.selection.empty){
_80e.selection.empty();
}else{
if(_80e.selection.clear){
_80e.selection.clear();
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
dojo.html.disableSelection=function(_80f){
_80f=dojo.byId(_80f)||dojo.body();
var h=dojo.render.html;
if(h.mozilla){
_80f.style.MozUserSelect="none";
}else{
if(h.safari){
_80f.style.KhtmlUserSelect="none";
}else{
if(h.ie){
_80f.unselectable="on";
}else{
return false;
}
}
}
return true;
};
dojo.html.enableSelection=function(_811){
_811=dojo.byId(_811)||dojo.body();
var h=dojo.render.html;
if(h.mozilla){
_811.style.MozUserSelect="";
}else{
if(h.safari){
_811.style.KhtmlUserSelect="";
}else{
if(h.ie){
_811.unselectable="off";
}else{
return false;
}
}
}
return true;
};
dojo.html.selectElement=function(_813){
dojo.deprecated("dojo.html.selectElement","replaced by dojo.html.selection.selectElementChildren",0.5);
};
dojo.html.selectInputText=function(_814){
var _815=dojo.global();
var _816=dojo.doc();
_814=dojo.byId(_814);
if(_816["selection"]&&dojo.body()["createTextRange"]){
var _817=_814.createTextRange();
_817.moveStart("character",0);
_817.moveEnd("character",_814.value.length);
_817.select();
}else{
if(_815["getSelection"]){
var _818=_815.getSelection();
_814.setSelectionRange(0,_814.value.length);
}
}
_814.focus();
};
dojo.html.isSelectionCollapsed=function(){
dojo.deprecated("dojo.html.isSelectionCollapsed","replaced by dojo.html.selection.isCollapsed",0.5);
return dojo.html.selection.isCollapsed();
};
dojo.lang.mixin(dojo.html.selection,{getType:function(){
if(dojo.doc()["selection"]){
return dojo.html.selectionType[dojo.doc().selection.type.toUpperCase()];
}else{
var _819=dojo.html.selectionType.TEXT;
var oSel;
try{
oSel=dojo.global().getSelection();
}
catch(e){
}
if(oSel&&oSel.rangeCount==1){
var _81b=oSel.getRangeAt(0);
if(_81b.startContainer==_81b.endContainer&&(_81b.endOffset-_81b.startOffset)==1&&_81b.startContainer.nodeType!=dojo.dom.TEXT_NODE){
_819=dojo.html.selectionType.CONTROL;
}
}
return _819;
}
},isCollapsed:function(){
var _81c=dojo.global();
var _81d=dojo.doc();
if(_81d["selection"]){
return _81d.selection.createRange().text=="";
}else{
if(_81c["getSelection"]){
var _81e=_81c.getSelection();
if(dojo.lang.isString(_81e)){
return _81e=="";
}else{
return _81e.isCollapsed||_81e.toString()=="";
}
}
}
},getSelectedElement:function(){
if(dojo.html.selection.getType()==dojo.html.selectionType.CONTROL){
if(dojo.doc()["selection"]){
var _81f=dojo.doc().selection.createRange();
if(_81f&&_81f.item){
return dojo.doc().selection.createRange().item(0);
}
}else{
var _820=dojo.global().getSelection();
return _820.anchorNode.childNodes[_820.anchorOffset];
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
var _822=dojo.global().getSelection();
if(_822){
var node=_822.anchorNode;
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
var _824=dojo.global().getSelection();
if(_824){
return _824.toString();
}
}
},getSelectedHtml:function(){
if(dojo.doc()["selection"]){
if(dojo.html.selection.getType()==dojo.html.selectionType.CONTROL){
return null;
}
return dojo.doc().selection.createRange().htmlText;
}else{
var _825=dojo.global().getSelection();
if(_825&&_825.rangeCount){
var frag=_825.getRangeAt(0).cloneContents();
var div=document.createElement("div");
div.appendChild(frag);
return div.innerHTML;
}
return null;
}
},hasAncestorElement:function(_828){
return (dojo.html.selection.getAncestorElement.apply(this,arguments)!=null);
},getAncestorElement:function(_829){
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
},selectElement:function(_82e){
var _82f=dojo.global();
var _830=dojo.doc();
_82e=dojo.byId(_82e);
if(_830.selection&&dojo.body().createTextRange){
try{
var _831=dojo.body().createControlRange();
_831.addElement(_82e);
_831.select();
}
catch(e){
dojo.html.selection.selectElementChildren(_82e);
}
}else{
if(_82f["getSelection"]){
var _832=_82f.getSelection();
if(_832["removeAllRanges"]){
var _831=_830.createRange();
_831.selectNode(_82e);
_832.removeAllRanges();
_832.addRange(_831);
}
}
}
},selectElementChildren:function(_833){
var _834=dojo.global();
var _835=dojo.doc();
_833=dojo.byId(_833);
if(_835.selection&&dojo.body().createTextRange){
var _836=dojo.body().createTextRange();
_836.moveToElementText(_833);
_836.select();
}else{
if(_834["getSelection"]){
var _837=_834.getSelection();
if(_837["setBaseAndExtent"]){
_837.setBaseAndExtent(_833,0,_833,_833.innerText.length-1);
}else{
if(_837["selectAllChildren"]){
_837.selectAllChildren(_833);
}
}
}
}
},getBookmark:function(){
var _838;
var _839=dojo.doc();
if(_839["selection"]){
var _83a=_839.selection.createRange();
_838=_83a.getBookmark();
}else{
var _83b;
try{
_83b=dojo.global().getSelection();
}
catch(e){
}
if(_83b){
var _83a=_83b.getRangeAt(0);
_838=_83a.cloneRange();
}else{
dojo.debug("No idea how to store the current selection for this browser!");
}
}
return _838;
},moveToBookmark:function(_83c){
var _83d=dojo.doc();
if(_83d["selection"]){
var _83e=_83d.selection.createRange();
_83e.moveToBookmark(_83c);
_83e.select();
}else{
var _83f;
try{
_83f=dojo.global().getSelection();
}
catch(e){
}
if(_83f&&_83f["removeAllRanges"]){
_83f.removeAllRanges();
_83f.addRange(_83c);
}else{
dojo.debug("No idea how to restore selection for this browser!");
}
}
},collapse:function(_840){
if(dojo.global()["getSelection"]){
var _841=dojo.global().getSelection();
if(_841.removeAllRanges){
if(_840){
_841.collapseToStart();
}else{
_841.collapseToEnd();
}
}else{
dojo.global().getSelection().collapse(_840);
}
}else{
if(dojo.doc().selection){
var _842=dojo.doc().selection.createRange();
_842.collapse(_840);
_842.select();
}
}
},remove:function(){
if(dojo.doc().selection){
var _843=dojo.doc().selection;
if(_843.type.toUpperCase()!="NONE"){
_843.clear();
}
return _843;
}else{
var _843=dojo.global().getSelection();
for(var i=0;i<_843.rangeCount;i++){
_843.getRangeAt(i).deleteContents();
}
return _843;
}
}});
dojo.provide("dojo.html.iframe");
dojo.html.iframeContentWindow=function(_845){
var win=dojo.html.getDocumentWindow(dojo.html.iframeContentDocument(_845))||dojo.html.iframeContentDocument(_845).__parent__||(_845.name&&document.frames[_845.name])||null;
return win;
};
dojo.html.iframeContentDocument=function(_847){
var doc=_847.contentDocument||((_847.contentWindow)&&(_847.contentWindow.document))||((_847.name)&&(document.frames[_847.name])&&(document.frames[_847.name].document))||null;
return doc;
};
dojo.html.BackgroundIframe=function(node){
if(dojo.render.html.ie55||dojo.render.html.ie60){
var html="<iframe src='javascript:false'"+"' style='position: absolute; left: 0px; top: 0px; width: 100%; height: 100%;"+"z-index: -1; filter:Alpha(Opacity=\"0\");' "+">";
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
var _84b=dojo.html.getMarginBox(this.domNode);
if(_84b.width==0||_84b.height==0){
dojo.lang.setTimeout(this,this.onResized,100);
return;
}
this.iframe.style.width=_84b.width+"px";
this.iframe.style.height=_84b.height+"px";
}
},size:function(node){
if(!this.iframe){
return;
}
var _84d=dojo.html.toCoordinateObject(node,true,dojo.html.boxSizing.BORDER_BOX);
this.iframe.style.width=_84d.width+"px";
this.iframe.style.height=_84d.height+"px";
this.iframe.style.left=_84d.left+"px";
this.iframe.style.top=_84d.top+"px";
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
if(!this.iframe){
return;
}
this.iframe.style.display="block";
},hide:function(){
if(!this.iframe){
return;
}
this.iframe.style.display="none";
},remove:function(){
dojo.html.removeNode(this.iframe);
}});
dojo.provide("dojo.dnd.HtmlDragAndDrop");
dojo.declare("dojo.dnd.HtmlDragSource",dojo.dnd.DragSource,{dragClass:"",onDragStart:function(){
var _84f=new dojo.dnd.HtmlDragObject(this.dragObject,this.type);
if(this.dragClass){
_84f.dragClass=this.dragClass;
}
if(this.constrainToContainer){
_84f.constrainTo(this.constrainingContainer||this.domNode.parentNode);
}
return _84f;
},setDragHandle:function(node){
node=dojo.byId(node);
dojo.dnd.dragManager.unregisterDragSource(this);
this.domNode=node;
dojo.dnd.dragManager.registerDragSource(this);
},setDragTarget:function(node){
this.dragObject=node;
},constrainTo:function(_852){
this.constrainToContainer=true;
if(_852){
this.constrainingContainer=_852;
}
},onSelected:function(){
for(var i=0;i<this.dragObjects.length;i++){
dojo.dnd.dragManager.selectedSources.push(new dojo.dnd.HtmlDragSource(this.dragObjects[i]));
}
},addDragObjects:function(el){
for(var i=0;i<arguments.length;i++){
this.dragObjects.push(arguments[i]);
}
}},function(node,type){
node=dojo.byId(node);
this.dragObjects=[];
this.constrainToContainer=false;
if(node){
this.domNode=node;
this.dragObject=node;
dojo.dnd.DragSource.call(this);
this.type=(type)||(this.domNode.nodeName.toLowerCase());
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
if(node.tagName.toLowerCase()=="tr"){
var doc=this.domNode.ownerDocument;
var _85a=doc.createElement("table");
var _85b=doc.createElement("tbody");
_85a.appendChild(_85b);
_85b.appendChild(node);
var _85c=this.domNode.childNodes;
var _85d=node.childNodes;
for(var i=0;i<_85c.length;i++){
if((_85d[i])&&(_85d[i].style)){
_85d[i].style.width=dojo.html.getContentBox(_85c[i]).width+"px";
}
}
node=_85a;
}
if((dojo.render.html.ie55||dojo.render.html.ie60)&&this.createIframe){
with(node.style){
top="0px";
left="0px";
}
var _85f=document.createElement("div");
_85f.appendChild(node);
this.bgIframe=new dojo.html.BackgroundIframe(_85f);
_85f.appendChild(this.bgIframe.iframe);
node=_85f;
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
dojo.event.connect(this.domNode,"onclick",this,"squelchOnClick");
dojo.event.topic.publish("dragStart",{source:this});
},getConstraints:function(){
if(this.constrainingContainer.nodeName.toLowerCase()=="body"){
var _861=dojo.html.getViewport();
var _862=_861.width;
var _863=_861.height;
var x=0;
var y=0;
}else{
var _866=dojo.html.getContentBox(this.constrainingContainer);
_862=_866.width;
_863=_866.height;
x=this.containingBlockPosition.x+dojo.html.getPixelValue(this.constrainingContainer,"padding-left",true)+dojo.html.getBorderExtent(this.constrainingContainer,"left");
y=this.containingBlockPosition.y+dojo.html.getPixelValue(this.constrainingContainer,"padding-top",true)+dojo.html.getBorderExtent(this.constrainingContainer,"top");
}
var mb=dojo.html.getMarginBox(this.domNode);
return {minX:x,minY:y,maxX:x+_862-mb.width,maxY:y+_863-mb.height};
},updateDragOffset:function(){
var _868=dojo.html.getScroll().offset;
if(_868.y!=this.scrollOffset.y){
var diff=_868.y-this.scrollOffset.y;
this.dragOffset.y+=diff;
this.scrollOffset.y=_868.y;
}
if(_868.x!=this.scrollOffset.x){
var diff=_868.x-this.scrollOffset.x;
this.dragOffset.x+=diff;
this.scrollOffset.x=_868.x;
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
var _870=dojo.html.getAbsolutePosition(this.dragClone,true);
var _871={left:this.dragStartPosition.x+1,top:this.dragStartPosition.y+1};
var anim=dojo.lfx.slideTo(this.dragClone,_871,500,dojo.lfx.easeOut);
var _873=this;
dojo.event.connect(anim,"onEnd",function(e){
dojo.lang.setTimeout(function(){
dojo.html.removeNode(_873.dragClone);
_873.dragClone=null;
},200);
});
anim.play();
break;
}
dojo.event.topic.publish("dragEnd",{source:this});
},squelchOnClick:function(e){
dojo.event.browser.stopEvent(e);
dojo.lang.setTimeout(function(){
dojo.event.disconnect(this.domNode,"onclick",this,"squelchOnClick");
},50);
},constrainTo:function(_876){
this.constrainToContainer=true;
if(_876){
this.constrainingContainer=_876;
}else{
this.constrainingContainer=this.domNode.parentNode;
}
}},function(node,type){
this.domNode=dojo.byId(node);
this.type=type;
this.constrainToContainer=false;
this.dragSource=null;
});
dojo.declare("dojo.dnd.HtmlDropTarget",dojo.dnd.DropTarget,{vertical:false,onDragOver:function(e){
if(!this.accepts(e.dragObjects)){
return false;
}
this.childBoxes=[];
for(var i=0,_87b;i<this.domNode.childNodes.length;i++){
_87b=this.domNode.childNodes[i];
if(_87b.nodeType!=dojo.html.ELEMENT_NODE){
continue;
}
var pos=dojo.html.getAbsolutePosition(_87b,true);
var _87d=dojo.html.getBorderBox(_87b);
this.childBoxes.push({top:pos.y,bottom:pos.y+_87d.height,left:pos.x,right:pos.x+_87d.width,height:_87d.height,width:_87d.width,node:_87b});
}
return true;
},_getNodeUnderMouse:function(e){
for(var i=0,_880;i<this.childBoxes.length;i++){
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
},onDragMove:function(e,_882){
var i=this._getNodeUnderMouse(e);
if(!this.dropIndicator){
this.createDropIndicator();
}
var _884=this.vertical?dojo.html.gravity.WEST:dojo.html.gravity.NORTH;
var hide=false;
if(i<0){
if(this.childBoxes.length){
var _886=(dojo.html.gravity(this.childBoxes[0].node,e)&_884);
if(_886){
hide=true;
}
}else{
var _886=true;
}
}else{
var _887=this.childBoxes[i];
var _886=(dojo.html.gravity(_887.node,e)&_884);
if(_887.node===_882[0].dragSource.domNode){
hide=true;
}else{
var _888=_886?(i>0?this.childBoxes[i-1]:_887):(i<this.childBoxes.length-1?this.childBoxes[i+1]:_887);
if(_888.node===_882[0].dragSource.domNode){
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
this.placeIndicator(e,_882,i,_886);
if(!dojo.html.hasParent(this.dropIndicator)){
dojo.body().appendChild(this.dropIndicator);
}
},placeIndicator:function(e,_88a,_88b,_88c){
var _88d=this.vertical?"left":"top";
var _88e;
if(_88b<0){
if(this.childBoxes.length){
_88e=_88c?this.childBoxes[0]:this.childBoxes[this.childBoxes.length-1];
}else{
this.dropIndicator.style[_88d]=dojo.html.getAbsolutePosition(this.domNode,true)[this.vertical?"x":"y"]+"px";
}
}else{
_88e=this.childBoxes[_88b];
}
if(_88e){
this.dropIndicator.style[_88d]=(_88c?_88e[_88d]:_88e[this.vertical?"right":"bottom"])+"px";
if(this.vertical){
this.dropIndicator.style.height=_88e.height+"px";
this.dropIndicator.style.top=_88e.top+"px";
}else{
this.dropIndicator.style.width=_88e.width+"px";
this.dropIndicator.style.left=_88e.left+"px";
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
var _892=this.vertical?dojo.html.gravity.WEST:dojo.html.gravity.NORTH;
if(i<0){
if(this.childBoxes.length){
if(dojo.html.gravity(this.childBoxes[0].node,e)&_892){
return this.insert(e,this.childBoxes[0].node,"before");
}else{
return this.insert(e,this.childBoxes[this.childBoxes.length-1].node,"after");
}
}
return this.insert(e,this.domNode,"append");
}
var _893=this.childBoxes[i];
if(dojo.html.gravity(_893.node,e)&_892){
return this.insert(e,_893.node,"before");
}else{
return this.insert(e,_893.node,"after");
}
},insert:function(e,_895,_896){
var node=e.dragObject.domNode;
if(_896=="before"){
return dojo.html.insertBefore(node,_895);
}else{
if(_896=="after"){
return dojo.html.insertAfter(node,_895);
}else{
if(_896=="append"){
_895.appendChild(node);
return true;
}
}
}
return false;
}},function(node,_899){
if(arguments.length==0){
return;
}
this.domNode=dojo.byId(node);
dojo.dnd.DropTarget.call(this);
if(_899&&dojo.lang.isString(_899)){
_899=[_899];
}
this.acceptedTypes=_899||[];
});
dojo.provide("dojo.dnd.*");
dojo.provide("dojo.ns");
dojo.ns={namespaces:{},failed:{},loading:{},loaded:{},register:function(name,_89b,_89c,_89d){
if(!_89d||!this.namespaces[name]){
this.namespaces[name]=new dojo.ns.Ns(name,_89b,_89c);
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
dojo.ns.Ns=function(name,_8a4,_8a5){
this.name=name;
this.module=_8a4;
this.resolver=_8a5;
this._loaded=[];
this._failed=[];
};
dojo.ns.Ns.prototype.resolve=function(name,_8a7,_8a8){
if(!this.resolver||djConfig["skipAutoRequire"]){
return false;
}
var _8a9=this.resolver(name,_8a7);
if((_8a9)&&(!this._loaded[_8a9])&&(!this._failed[_8a9])){
var req=dojo.require;
req(_8a9,false,true);
if(dojo.hostenv.findModule(_8a9,false)){
this._loaded[_8a9]=true;
}else{
if(!_8a8){
dojo.raise("dojo.ns.Ns.resolve: module '"+_8a9+"' not found after loading via namespace '"+this.name+"'");
}
this._failed[_8a9]=true;
}
}
return Boolean(this._loaded[_8a9]);
};
dojo.registerNamespace=function(name,_8ac,_8ad){
dojo.ns.register.apply(dojo.ns,arguments);
};
dojo.registerNamespaceResolver=function(name,_8af){
var n=dojo.ns.namespaces[name];
if(n){
n.resolver=_8af;
}
};
dojo.registerNamespaceManifest=function(_8b1,path,name,_8b4,_8b5){
dojo.registerModulePath(name,path);
dojo.registerNamespace(name,_8b4,_8b5);
};
dojo.registerNamespace("dojo","dojo.widget");
dojo.provide("dojo.widget.Manager");
dojo.widget.manager=new function(){
this.widgets=[];
this.widgetIds=[];
this.topWidgets={};
var _8b6={};
var _8b7=[];
this.getUniqueId=function(_8b8){
var _8b9;
do{
_8b9=_8b8+"_"+(_8b6[_8b8]!=undefined?++_8b6[_8b8]:_8b6[_8b8]=0);
}while(this.getWidgetById(_8b9));
return _8b9;
};
this.add=function(_8ba){
this.widgets.push(_8ba);
if(!_8ba.extraArgs["id"]){
_8ba.extraArgs["id"]=_8ba.extraArgs["ID"];
}
if(_8ba.widgetId==""){
if(_8ba["id"]){
_8ba.widgetId=_8ba["id"];
}else{
if(_8ba.extraArgs["id"]){
_8ba.widgetId=_8ba.extraArgs["id"];
}else{
_8ba.widgetId=this.getUniqueId(_8ba.widgetType);
}
}
}
if(this.widgetIds[_8ba.widgetId]){
dojo.debug("widget ID collision on ID: "+_8ba.widgetId);
}
this.widgetIds[_8ba.widgetId]=_8ba;
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
this.remove=function(_8bc){
if(dojo.lang.isNumber(_8bc)){
var tw=this.widgets[_8bc].widgetId;
delete this.widgetIds[tw];
this.widgets.splice(_8bc,1);
}else{
this.removeById(_8bc);
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
var _8c3=(type.indexOf(":")<0?function(x){
return x.widgetType.toLowerCase();
}:function(x){
return x.getNamespacedType();
});
var ret=[];
dojo.lang.forEach(this.widgets,function(x){
if(_8c3(x)==lt){
ret.push(x);
}
});
return ret;
};
this.getWidgetsByFilter=function(_8c8,_8c9){
var ret=[];
dojo.lang.every(this.widgets,function(x){
if(_8c8(x)){
ret.push(x);
if(_8c9){
return false;
}
}
return true;
});
return (_8c9?ret[0]:ret);
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
var _8cf={};
var _8d0=["dojo.widget"];
for(var i=0;i<_8d0.length;i++){
_8d0[_8d0[i]]=true;
}
this.registerWidgetPackage=function(_8d2){
if(!_8d0[_8d2]){
_8d0[_8d2]=true;
_8d0.push(_8d2);
}
};
this.getWidgetPackageList=function(){
return dojo.lang.map(_8d0,function(elt){
return (elt!==true?elt:undefined);
});
};
this.getImplementation=function(_8d4,_8d5,_8d6,ns){
var impl=this.getImplementationName(_8d4,ns);
if(impl){
var ret=_8d5?new impl(_8d5):new impl();
return ret;
}
};
function buildPrefixCache(){
for(var _8da in dojo.render){
if(dojo.render[_8da]["capable"]===true){
var _8db=dojo.render[_8da].prefixes;
for(var i=0;i<_8db.length;i++){
_8b7.push(_8db[i].toLowerCase());
}
}
}
}
var _8dd=function(_8de,_8df){
if(!_8df){
return null;
}
for(var i=0,l=_8b7.length,_8e2;i<=l;i++){
_8e2=(i<l?_8df[_8b7[i]]:_8df);
if(!_8e2){
continue;
}
for(var name in _8e2){
if(name.toLowerCase()==_8de){
return _8e2[name];
}
}
}
return null;
};
var _8e4=function(_8e5,_8e6){
var _8e7=dojo.evalObjPath(_8e6,false);
return (_8e7?_8dd(_8e5,_8e7):null);
};
this.getImplementationName=function(_8e8,ns){
var _8ea=_8e8.toLowerCase();
ns=ns||"dojo";
var imps=_8cf[ns]||(_8cf[ns]={});
var impl=imps[_8ea];
if(impl){
return impl;
}
if(!_8b7.length){
buildPrefixCache();
}
var _8ed=dojo.ns.get(ns);
if(!_8ed){
dojo.ns.register(ns,ns+".widget");
_8ed=dojo.ns.get(ns);
}
if(_8ed){
_8ed.resolve(_8e8);
}
impl=_8e4(_8ea,_8ed.module);
if(impl){
return (imps[_8ea]=impl);
}
_8ed=dojo.ns.require(ns);
if((_8ed)&&(_8ed.resolver)){
_8ed.resolve(_8e8);
impl=_8e4(_8ea,_8ed.module);
if(impl){
return (imps[_8ea]=impl);
}
}
dojo.deprecated("dojo.widget.Manager.getImplementationName","Could not locate widget implementation for \""+_8e8+"\" in \""+_8ed.module+"\" registered to namespace \""+_8ed.name+"\". "+"Developers must specify correct namespaces for all non-Dojo widgets","0.5");
for(var i=0;i<_8d0.length;i++){
impl=_8e4(_8ea,_8d0[i]);
if(impl){
return (imps[_8ea]=impl);
}
}
throw new Error("Could not locate widget implementation for \""+_8e8+"\" in \""+_8ed.module+"\" registered to namespace \""+_8ed.name+"\"");
};
this.resizing=false;
this.onWindowResized=function(){
if(this.resizing){
return;
}
try{
this.resizing=true;
for(var id in this.topWidgets){
var _8f0=this.topWidgets[id];
if(_8f0.checkSize){
_8f0.checkSize();
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
var g=function(_8f5,_8f6){
dw[(_8f6||_8f5)]=h(_8f5);
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
var _8f8=dwm.getAllWidgets.apply(dwm,arguments);
if(arguments.length>0){
return _8f8[n];
}
return _8f8;
};
g("registerWidgetPackage");
g("getImplementation","getWidgetImplementation");
g("getImplementationName","getWidgetImplementationName");
dw.widgets=dwm.widgets;
dw.widgetIds=dwm.widgetIds;
dw.root=dwm.root;
})();
dojo.provide("dojo.a11y");
dojo.a11y={imgPath:dojo.uri.dojoUri("src/widget/templates/images"),doAccessibleCheck:true,accessible:null,checkAccessible:function(){
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
var _8fa=null;
if(window.getComputedStyle){
var _8fb=getComputedStyle(div,"");
_8fa=_8fb.getPropertyValue("background-image");
}else{
_8fa=div.currentStyle.backgroundImage;
}
var _8fc=false;
if(_8fa!=null&&(_8fa=="none"||_8fa=="url(invalid-url:)")){
this.accessible=true;
}
dojo.body().removeChild(div);
}
return this.accessible;
},setCheckAccessible:function(_8fd){
this.doAccessibleCheck=_8fd;
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
},{parent:null,children:[],extraArgs:{},isTopLevel:false,isModal:false,isEnabled:true,isHidden:false,isContainer:false,widgetId:"",widgetType:"Widget",ns:"dojo",getNamespacedType:function(){
return (this.ns?this.ns+":"+this.widgetType:this.widgetType).toLowerCase();
},toString:function(){
return "[Widget "+this.getNamespacedType()+", "+(this.widgetId||"NO ID")+"]";
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
},onResized:function(){
this.notifyChildrenOfResize();
},notifyChildrenOfResize:function(){
for(var i=0;i<this.children.length;i++){
var _8ff=this.children[i];
if(_8ff.onResized){
_8ff.onResized();
}
}
},create:function(args,_901,_902,ns){
if(ns){
this.ns=ns;
}
this.satisfyPropertySets(args,_901,_902);
this.mixInProperties(args,_901,_902);
this.postMixInProperties(args,_901,_902);
dojo.widget.manager.add(this);
this.buildRendering(args,_901,_902);
this.initialize(args,_901,_902);
this.postInitialize(args,_901,_902);
this.postCreate(args,_901,_902);
return this;
},destroy:function(_904){
this.destroyChildren();
this.uninitialize();
this.destroyRendering(_904);
dojo.widget.manager.removeById(this.widgetId);
},destroyChildren:function(){
var _905;
var i=0;
while(this.children.length>i){
_905=this.children[i];
if(_905 instanceof dojo.widget.Widget){
this.removeChild(_905);
_905.destroy();
continue;
}
i++;
}
},getChildrenOfType:function(type,_908){
var ret=[];
var _90a=dojo.lang.isFunction(type);
if(!_90a){
type=type.toLowerCase();
}
for(var x=0;x<this.children.length;x++){
if(_90a){
if(this.children[x] instanceof type){
ret.push(this.children[x]);
}
}else{
if(this.children[x].widgetType.toLowerCase()==type){
ret.push(this.children[x]);
}
}
if(_908){
ret=ret.concat(this.children[x].getChildrenOfType(type,_908));
}
}
return ret;
},getDescendants:function(){
var _90c=[];
var _90d=[this];
var elem;
while((elem=_90d.pop())){
_90c.push(elem);
if(elem.children){
dojo.lang.forEach(elem.children,function(elem){
_90d.push(elem);
});
}
}
return _90c;
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
var _914;
var _915=dojo.widget.lcArgsCache[this.widgetType];
if(_915==null){
_915={};
for(var y in this){
_915[((new String(y)).toLowerCase())]=y;
}
dojo.widget.lcArgsCache[this.widgetType]=_915;
}
var _917={};
for(var x in args){
if(!this[x]){
var y=_915[(new String(x)).toLowerCase()];
if(y){
args[y]=args[x];
x=y;
}
}
if(_917[x]){
continue;
}
_917[x]=true;
if((typeof this[x])!=(typeof _914)){
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
this[x]=args[x];
}else{
var _919=args[x].split(";");
for(var y=0;y<_919.length;y++){
var si=_919[y].indexOf(":");
if((si!=-1)&&(_919[y].length>si)){
this[x][_919[y].substr(0,si).replace(/^\s+|\s+$/g,"")]=_919[y].substr(si+1);
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
},postMixInProperties:function(args,frag,_91d){
},initialize:function(args,frag,_920){
return false;
},postInitialize:function(args,frag,_923){
return false;
},postCreate:function(args,frag,_926){
return false;
},uninitialize:function(){
return false;
},buildRendering:function(args,frag,_929){
dojo.unimplemented("dojo.widget.Widget.buildRendering, on "+this.toString()+", ");
return false;
},destroyRendering:function(){
dojo.unimplemented("dojo.widget.Widget.destroyRendering");
return false;
},cleanUp:function(){
dojo.unimplemented("dojo.widget.Widget.cleanUp");
return false;
},addedTo:function(_92a){
},addChild:function(_92b){
dojo.unimplemented("dojo.widget.Widget.addChild");
return false;
},removeChild:function(_92c){
for(var x=0;x<this.children.length;x++){
if(this.children[x]===_92c){
this.children.splice(x,1);
break;
}
}
return _92c;
},resize:function(_92e,_92f){
this.setWidth(_92e);
this.setHeight(_92f);
},setWidth:function(_930){
if((typeof _930=="string")&&(_930.substr(-1)=="%")){
this.setPercentageWidth(_930);
}else{
this.setNativeWidth(_930);
}
},setHeight:function(_931){
if((typeof _931=="string")&&(_931.substr(-1)=="%")){
this.setPercentageHeight(_931);
}else{
this.setNativeHeight(_931);
}
},setPercentageHeight:function(_932){
return false;
},setNativeHeight:function(_933){
return false;
},setPercentageWidth:function(_934){
return false;
},setNativeWidth:function(_935){
return false;
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
dojo.widget.tags["dojo:propertyset"]=function(_939,_93a,_93b){
var _93c=_93a.parseProperties(_939["dojo:propertyset"]);
};
dojo.widget.tags["dojo:connect"]=function(_93d,_93e,_93f){
var _940=_93e.parseProperties(_93d["dojo:connect"]);
};
dojo.widget.buildWidgetFromParseTree=function(type,frag,_943,_944,_945,_946){
dojo.a11y.setAccessibleMode();
var _947=type.split(":");
_947=(_947.length==2)?_947[1]:type;
var _948=_946||_943.parseProperties(frag[frag["ns"]+":"+_947]);
var _949=dojo.widget.manager.getImplementation(_947,null,null,frag["ns"]);
if(!_949){
throw new Error("cannot find \""+type+"\" widget");
}else{
if(!_949.create){
throw new Error("\""+type+"\" widget object has no \"create\" method and does not appear to implement *Widget");
}
}
_948["dojoinsertionindex"]=_945;
var ret=_949.create(_948,frag,_944,frag["ns"]);
return ret;
};
dojo.widget.defineWidget=function(_94b,_94c,_94d,init,_94f){
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
dojo.widget._defineWidget=function(_952,_953,_954,init,_956){
var _957=_952.split(".");
var type=_957.pop();
var regx="\\.("+(_953?_953+"|":"")+dojo.widget.defineWidget.renderers+")\\.";
var r=_952.search(new RegExp(regx));
_957=(r<0?_957.join("."):_952.substr(0,r));
dojo.widget.manager.registerWidgetPackage(_957);
var pos=_957.indexOf(".");
var _95c=(pos>-1)?_957.substring(0,pos):_957;
_956=(_956)||{};
_956.widgetType=type;
if((!init)&&(_956["classConstructor"])){
init=_956.classConstructor;
delete _956.classConstructor;
}
dojo.declare(_952,_954,init,_956);
};
dojo.provide("dojo.widget.Parse");
dojo.widget.Parse=function(_95d){
this.propertySetsList=[];
this.fragment=_95d;
this.createComponents=function(frag,_95f){
var _960=[];
var _961=false;
try{
if((frag)&&(frag["tagName"])&&(frag!=frag["nodeRef"])){
var _962=dojo.widget.tags;
var tna=String(frag["tagName"]).split(";");
for(var x=0;x<tna.length;x++){
var ltn=(tna[x].replace(/^\s+|\s+$/g,"")).toLowerCase();
frag.tagName=ltn;
if(_962[ltn]){
_961=true;
var ret=_962[ltn](frag,this,_95f,frag["index"]);
_960.push(ret);
}else{
if(ltn.indexOf(":")==-1){
ltn="dojo:"+ltn;
}
var ret=dojo.widget.buildWidgetFromParseTree(ltn,frag,this,_95f,frag["index"]);
if(ret){
_961=true;
_960.push(ret);
}
}
}
}
}
catch(e){
dojo.debug("dojo.widget.Parse: error:"+e);
}
if(!_961){
_960=_960.concat(this.createSubComponents(frag,_95f));
}
return _960;
};
this.createSubComponents=function(_967,_968){
var frag,_96a=[];
for(var item in _967){
frag=_967[item];
if((frag)&&(typeof frag=="object")&&(frag!=_967.nodeRef)&&(frag!=_967["tagName"])){
_96a=_96a.concat(this.createComponents(frag,_968));
}
}
return _96a;
};
this.parsePropertySets=function(_96c){
return [];
};
this.parseProperties=function(_96d){
var _96e={};
for(var item in _96d){
if((_96d[item]==_96d["tagName"])||(_96d[item]==_96d.nodeRef)){
}else{
if((_96d[item]["tagName"])&&(dojo.widget.tags[_96d[item].tagName.toLowerCase()])){
}else{
if((_96d[item][0])&&(_96d[item][0].value!="")&&(_96d[item][0].value!=null)){
try{
if(item.toLowerCase()=="dataprovider"){
var _970=this;
this.getDataProvider(_970,_96d[item][0].value);
_96e.dataProvider=this.dataProvider;
}
_96e[item]=_96d[item][0].value;
var _971=this.parseProperties(_96d[item]);
for(var _972 in _971){
_96e[_972]=_971[_972];
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
if(typeof _96e[item]!="boolean"){
_96e[item]=true;
}
break;
}
}
}
return _96e;
};
this.getDataProvider=function(_973,_974){
dojo.io.bind({url:_974,load:function(type,_976){
if(type=="load"){
_973.dataProvider=_976;
}
},mimetype:"text/javascript",sync:true});
};
this.getPropertySetById=function(_977){
for(var x=0;x<this.propertySetsList.length;x++){
if(_977==this.propertySetsList[x]["id"][0].value){
return this.propertySetsList[x];
}
}
return "";
};
this.getPropertySetsByType=function(_979){
var _97a=[];
for(var x=0;x<this.propertySetsList.length;x++){
var cpl=this.propertySetsList[x];
var cpcc=cpl["componentClass"]||cpl["componentType"]||null;
var _97e=this.propertySetsList[x]["id"][0].value;
if((cpcc)&&(_97e==cpcc[0].value)){
_97a.push(cpl);
}
}
return _97a;
};
this.getPropertySets=function(_97f){
var ppl="dojo:propertyproviderlist";
var _981=[];
var _982=_97f["tagName"];
if(_97f[ppl]){
var _983=_97f[ppl].value.split(" ");
for(var _984 in _983){
if((_984.indexOf("..")==-1)&&(_984.indexOf("://")==-1)){
var _985=this.getPropertySetById(_984);
if(_985!=""){
_981.push(_985);
}
}else{
}
}
}
return (this.getPropertySetsByType(_982)).concat(_981);
};
this.createComponentFromScript=function(_986,_987,_988,ns){
_988.fastMixIn=true;
var ltn=(ns||"dojo")+":"+_987.toLowerCase();
if(dojo.widget.tags[ltn]){
return [dojo.widget.tags[ltn](_988,this,null,null,_988)];
}
return [dojo.widget.buildWidgetFromParseTree(ltn,_988,this,null,null,_988)];
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
dojo.widget.createWidget=function(name,_98d,_98e,_98f){
var _990=false;
var _991=(typeof name=="string");
if(_991){
var pos=name.indexOf(":");
var ns=(pos>-1)?name.substring(0,pos):"dojo";
if(pos>-1){
name=name.substring(pos+1);
}
var _994=name.toLowerCase();
var _995=ns+":"+_994;
_990=(dojo.byId(name)&&(!dojo.widget.tags[_995]));
}
if((arguments.length==1)&&((_990)||(!_991))){
var xp=new dojo.xml.Parse();
var tn=(_990)?dojo.byId(name):name;
return dojo.widget.getParser().createComponents(xp.parseElement(tn,null,true))[0];
}
function fromScript(_998,name,_99a,ns){
_99a[_995]={dojotype:[{value:_994}],nodeRef:_998,fastMixIn:true};
_99a.ns=ns;
return dojo.widget.getParser().createComponentFromScript(_998,name,_99a,ns);
}
_98d=_98d||{};
var _99c=false;
var tn=null;
var h=dojo.render.html.capable;
if(h){
tn=document.createElement("span");
}
if(!_98e){
_99c=true;
_98e=tn;
if(h){
dojo.body().appendChild(_98e);
}
}else{
if(_98f){
dojo.dom.insertAtPosition(tn,_98e,_98f);
}else{
tn=_98e;
}
}
var _99e=fromScript(tn,name.toLowerCase(),_98d,ns);
if((!_99e)||(!_99e[0])||(typeof _99e[0].widgetType=="undefined")){
throw new Error("createWidget: Creation of \""+name+"\" widget failed.");
}
try{
if(_99c){
if(_99e[0].domNode.parentNode){
_99e[0].domNode.parentNode.removeChild(_99e[0].domNode);
}
}
}
catch(e){
dojo.debug(e);
}
return _99e[0];
};
dojo.provide("dojo.widget.DomWidget");
dojo.widget._cssFiles={};
dojo.widget._cssStrings={};
dojo.widget._templateCache={};
dojo.widget.defaultStrings={dojoRoot:dojo.hostenv.getBaseScriptUri(),baseScriptUri:dojo.hostenv.getBaseScriptUri()};
dojo.widget.fillFromTemplateCache=function(obj,_9a0,_9a1,_9a2){
var _9a3=_9a0||obj.templatePath;
var _9a4=dojo.widget._templateCache;
if(!obj["widgetType"]){
do{
var _9a5="__dummyTemplate__"+dojo.widget._templateCache.dummyCount++;
}while(_9a4[_9a5]);
obj.widgetType=_9a5;
}
var wt=obj.widgetType;
var ts=_9a4[wt];
if(!ts){
_9a4[wt]={"string":null,"node":null};
if(_9a2){
ts={};
}else{
ts=_9a4[wt];
}
}
if((!obj.templateString)&&(!_9a2)){
obj.templateString=_9a1||ts["string"];
}
if((!obj.templateNode)&&(!_9a2)){
obj.templateNode=ts["node"];
}
if((!obj.templateNode)&&(!obj.templateString)&&(_9a3)){
var _9a8=dojo.hostenv.getText(_9a3);
if(_9a8){
_9a8=_9a8.replace(/^\s*<\?xml(\s)+version=[\'\"](\d)*.(\d)*[\'\"](\s)*\?>/im,"");
var _9a9=_9a8.match(/<body[^>]*>\s*([\s\S]+)\s*<\/body>/im);
if(_9a9){
_9a8=_9a9[1];
}
}else{
_9a8="";
}
obj.templateString=_9a8;
if(!_9a2){
_9a4[wt]["string"]=_9a8;
}
}
if((!ts["string"])&&(!_9a2)){
ts.string=obj.templateString;
}
};
dojo.widget._templateCache.dummyCount=0;
dojo.widget.attachProperties=["dojoAttachPoint","id"];
dojo.widget.eventAttachProperty="dojoAttachEvent";
dojo.widget.onBuildProperty="dojoOnBuild";
dojo.widget.waiNames=["waiRole","waiState"];
dojo.widget.wai={waiRole:{name:"waiRole","namespace":"http://www.w3.org/TR/xhtml2",alias:"x2",prefix:"wairole:"},waiState:{name:"waiState","namespace":"http://www.w3.org/2005/07/aaa",alias:"aaa",prefix:""},setAttr:function(node,ns,attr,_9ad){
if(dojo.render.html.ie){
node.setAttribute(this[ns].alias+":"+attr,this[ns].prefix+_9ad);
}else{
node.setAttributeNS(this[ns]["namespace"],attr,this[ns].prefix+_9ad);
}
},getAttr:function(node,ns,attr){
if(dojo.render.html.ie){
return node.getAttribute(this[ns].alias+":"+attr);
}else{
return node.getAttributeNS(this[ns]["namespace"],attr);
}
},removeAttr:function(node,ns,attr){
var _9b4=true;
if(dojo.render.html.ie){
_9b4=node.removeAttribute(this[ns].alias+":"+attr);
}else{
node.removeAttributeNS(this[ns]["namespace"],attr);
}
return _9b4;
}};
dojo.widget.attachTemplateNodes=function(_9b5,_9b6,_9b7){
var _9b8=dojo.dom.ELEMENT_NODE;
function trim(str){
return str.replace(/^\s+|\s+$/g,"");
}
if(!_9b5){
_9b5=_9b6.domNode;
}
if(_9b5.nodeType!=_9b8){
return;
}
var _9ba=_9b5.all||_9b5.getElementsByTagName("*");
var _9bb=_9b6;
for(var x=-1;x<_9ba.length;x++){
var _9bd=(x==-1)?_9b5:_9ba[x];
var _9be=[];
if(!_9b6.widgetsInTemplate||!_9bd.getAttribute("dojoType")){
for(var y=0;y<this.attachProperties.length;y++){
var _9c0=_9bd.getAttribute(this.attachProperties[y]);
if(_9c0){
_9be=_9c0.split(";");
for(var z=0;z<_9be.length;z++){
if(dojo.lang.isArray(_9b6[_9be[z]])){
_9b6[_9be[z]].push(_9bd);
}else{
_9b6[_9be[z]]=_9bd;
}
}
break;
}
}
var _9c2=_9bd.getAttribute(this.eventAttachProperty);
if(_9c2){
var evts=_9c2.split(";");
for(var y=0;y<evts.length;y++){
if((!evts[y])||(!evts[y].length)){
continue;
}
var _9c4=null;
var tevt=trim(evts[y]);
if(evts[y].indexOf(":")>=0){
var _9c6=tevt.split(":");
tevt=trim(_9c6[0]);
_9c4=trim(_9c6[1]);
}
if(!_9c4){
_9c4=tevt;
}
var tf=function(){
var ntf=new String(_9c4);
return function(evt){
if(_9bb[ntf]){
_9bb[ntf](dojo.event.browser.fixEvent(evt,this));
}
};
}();
dojo.event.browser.addListener(_9bd,tevt,tf,false,true);
}
}
for(var y=0;y<_9b7.length;y++){
var _9ca=_9bd.getAttribute(_9b7[y]);
if((_9ca)&&(_9ca.length)){
var _9c4=null;
var _9cb=_9b7[y].substr(4);
_9c4=trim(_9ca);
var _9cc=[_9c4];
if(_9c4.indexOf(";")>=0){
_9cc=dojo.lang.map(_9c4.split(";"),trim);
}
for(var z=0;z<_9cc.length;z++){
if(!_9cc[z].length){
continue;
}
var tf=function(){
var ntf=new String(_9cc[z]);
return function(evt){
if(_9bb[ntf]){
_9bb[ntf](dojo.event.browser.fixEvent(evt,this));
}
};
}();
dojo.event.browser.addListener(_9bd,_9cb,tf,false,true);
}
}
}
}
var _9cf=_9bd.getAttribute(this.templateProperty);
if(_9cf){
_9b6[_9cf]=_9bd;
}
dojo.lang.forEach(dojo.widget.waiNames,function(name){
var wai=dojo.widget.wai[name];
var val=_9bd.getAttribute(wai.name);
if(val){
if(val.indexOf("-")==-1){
dojo.widget.wai.setAttr(_9bd,wai.name,"role",val);
}else{
var _9d3=val.split("-");
dojo.widget.wai.setAttr(_9bd,wai.name,_9d3[0],_9d3[1]);
}
}
},this);
var _9d4=_9bd.getAttribute(this.onBuildProperty);
if(_9d4){
eval("var node = baseNode; var widget = targetObj; "+_9d4);
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
},{templateNode:null,templateString:null,templateCssString:null,preventClobber:false,domNode:null,containerNode:null,widgetsInTemplate:false,addChild:function(_9dc,_9dd,pos,ref,_9e0){
if(!this.isContainer){
dojo.debug("dojo.widget.DomWidget.addChild() attempted on non-container widget");
return null;
}else{
if(_9e0==undefined){
_9e0=this.children.length;
}
this.addWidgetAsDirectChild(_9dc,_9dd,pos,ref,_9e0);
this.registerChild(_9dc,_9e0);
}
return _9dc;
},addWidgetAsDirectChild:function(_9e1,_9e2,pos,ref,_9e5){
if((!this.containerNode)&&(!_9e2)){
this.containerNode=this.domNode;
}
var cn=(_9e2)?_9e2:this.containerNode;
if(!pos){
pos="after";
}
if(!ref){
if(!cn){
cn=dojo.body();
}
ref=cn.lastChild;
}
if(!_9e5){
_9e5=0;
}
_9e1.domNode.setAttribute("dojoinsertionindex",_9e5);
if(!ref){
cn.appendChild(_9e1.domNode);
}else{
if(pos=="insertAtIndex"){
dojo.dom.insertAtIndex(_9e1.domNode,ref.parentNode,_9e5);
}else{
if((pos=="after")&&(ref===cn.lastChild)){
cn.appendChild(_9e1.domNode);
}else{
dojo.dom.insertAtPosition(_9e1.domNode,cn,pos);
}
}
}
},registerChild:function(_9e7,_9e8){
_9e7.dojoInsertionIndex=_9e8;
var idx=-1;
for(var i=0;i<this.children.length;i++){
if(this.children[i].dojoInsertionIndex<=_9e8){
idx=i;
}
}
this.children.splice(idx+1,0,_9e7);
_9e7.parent=this;
_9e7.addedTo(this,idx+1);
delete dojo.widget.manager.topWidgets[_9e7.widgetId];
},removeChild:function(_9eb){
dojo.dom.removeNode(_9eb.domNode);
return dojo.widget.DomWidget.superclass.removeChild.call(this,_9eb);
},getFragNodeRef:function(frag){
if(!frag){
return null;
}
if(!frag[this.getNamespacedType()]){
dojo.raise("Error: no frag for widget type "+this.getNamespacedType()+", id "+this.widgetId+" (maybe a widget has set it's type incorrectly)");
}
return frag[this.getNamespacedType()]["nodeRef"];
},postInitialize:function(args,frag,_9ef){
var _9f0=this.getFragNodeRef(frag);
if(_9ef&&(_9ef.snarfChildDomOutput||!_9f0)){
_9ef.addWidgetAsDirectChild(this,"","insertAtIndex","",args["dojoinsertionindex"],_9f0);
}else{
if(_9f0){
if(this.domNode&&(this.domNode!==_9f0)){
var _9f1=_9f0.parentNode.replaceChild(this.domNode,_9f0);
}
}
}
if(_9ef){
_9ef.registerChild(this,args.dojoinsertionindex);
}else{
dojo.widget.manager.topWidgets[this.widgetId]=this;
}
if(this.widgetsInTemplate){
var _9f2=new dojo.xml.Parse();
var _9f3;
var _9f4=this.domNode.getElementsByTagName("*");
for(var i=0;i<_9f4.length;i++){
if(_9f4[i].getAttribute("dojoAttachPoint")=="subContainerWidget"){
_9f3=_9f4[i];
}
if(_9f4[i].getAttribute("dojoType")){
_9f4[i].setAttribute("_isSubWidget",true);
}
}
if(this.isContainer&&!this.containerNode){
if(_9f3){
var src=this.getFragNodeRef(frag);
if(src){
dojo.dom.moveChildren(src,_9f3);
frag["dojoDontFollow"]=true;
}
}else{
dojo.debug("No subContainerWidget node can be found in template file for widget "+this);
}
}
var _9f7=_9f2.parseElement(this.domNode,null,true);
dojo.widget.getParser().createSubComponents(_9f7,this);
var _9f8=[];
var _9f9=[this];
var w;
while((w=_9f9.pop())){
for(var i=0;i<w.children.length;i++){
var _9fb=w.children[i];
if(_9fb._processedSubWidgets||!_9fb.extraArgs["_issubwidget"]){
continue;
}
_9f8.push(_9fb);
if(_9fb.isContainer){
_9f9.push(_9fb);
}
}
}
for(var i=0;i<_9f8.length;i++){
var _9fc=_9f8[i];
if(_9fc._processedSubWidgets){
dojo.debug("This should not happen: widget._processedSubWidgets is already true!");
return;
}
_9fc._processedSubWidgets=true;
if(_9fc.extraArgs["dojoattachevent"]){
var evts=_9fc.extraArgs["dojoattachevent"].split(";");
for(var j=0;j<evts.length;j++){
var _9ff=null;
var tevt=dojo.string.trim(evts[j]);
if(tevt.indexOf(":")>=0){
var _a01=tevt.split(":");
tevt=dojo.string.trim(_a01[0]);
_9ff=dojo.string.trim(_a01[1]);
}
if(!_9ff){
_9ff=tevt;
}
if(dojo.lang.isFunction(_9fc[tevt])){
dojo.event.kwConnect({srcObj:_9fc,srcFunc:tevt,targetObj:this,targetFunc:_9ff});
}else{
alert(tevt+" is not a function in widget "+_9fc);
}
}
}
if(_9fc.extraArgs["dojoattachpoint"]){
this[_9fc.extraArgs["dojoattachpoint"]]=_9fc;
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
var _a05=args["templateCssPath"]||this.templateCssPath;
if(_a05&&!dojo.widget._cssFiles[_a05.toString()]){
if((!this.templateCssString)&&(_a05)){
this.templateCssString=dojo.hostenv.getText(_a05);
this.templateCssPath=null;
}
dojo.widget._cssFiles[_a05.toString()]=true;
}
if((this["templateCssString"])&&(!this.templateCssString["loaded"])){
dojo.html.insertCssText(this.templateCssString,null,_a05);
if(!this.templateCssString){
this.templateCssString="";
}
this.templateCssString.loaded=true;
}
if((!this.preventClobber)&&((this.templatePath)||(this.templateNode)||((this["templateString"])&&(this.templateString.length))||((typeof ts!="undefined")&&((ts["string"])||(ts["node"]))))){
this.buildFromTemplate(args,frag);
}else{
this.domNode=this.getFragNodeRef(frag);
}
this.fillInTemplate(args,frag);
},buildFromTemplate:function(args,frag){
var _a08=false;
if(args["templatepath"]){
_a08=true;
args["templatePath"]=args["templatepath"];
}
dojo.widget.fillFromTemplateCache(this,args["templatePath"],null,_a08);
var ts=dojo.widget._templateCache[this.widgetType];
if((ts)&&(!_a08)){
if(!this.templateString.length){
this.templateString=ts["string"];
}
if(!this.templateNode){
this.templateNode=ts["node"];
}
}
var _a0a=false;
var node=null;
var tstr=this.templateString;
if((!this.templateNode)&&(this.templateString)){
_a0a=this.templateString.match(/\$\{([^\}]+)\}/g);
if(_a0a){
var hash=this.strings||{};
for(var key in dojo.widget.defaultStrings){
if(dojo.lang.isUndefined(hash[key])){
hash[key]=dojo.widget.defaultStrings[key];
}
}
for(var i=0;i<_a0a.length;i++){
var key=_a0a[i];
key=key.substring(2,key.length-1);
var kval=(key.substring(0,5)=="this.")?dojo.lang.getObjPathValue(key.substring(5),this):hash[key];
var _a11;
if((kval)||(dojo.lang.isString(kval))){
_a11=new String((dojo.lang.isFunction(kval))?kval.call(this,key,this.templateString):kval);
while(_a11.indexOf("\"")>-1){
_a11=_a11.replace("\"","&quot;");
}
tstr=tstr.replace(_a0a[i],_a11);
}
}
}else{
this.templateNode=this.createNodesFromText(this.templateString,true)[0];
if(!_a08){
ts.node=this.templateNode;
}
}
}
if((!this.templateNode)&&(!_a0a)){
dojo.debug("DomWidget.buildFromTemplate: could not create template");
return false;
}else{
if(!_a0a){
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
},attachTemplateNodes:function(_a13,_a14){
if(!_a13){
_a13=this.domNode;
}
if(!_a14){
_a14=this;
}
return dojo.widget.attachTemplateNodes(_a13,_a14,dojo.widget.getDojoEventsFromStr(this.templateString));
},fillInTemplate:function(){
},destroyRendering:function(){
try{
delete this.domNode;
}
catch(e){
}
},cleanUp:function(){
},getContainerHeight:function(){
dojo.unimplemented("dojo.widget.DomWidget.getContainerHeight");
},getContainerWidth:function(){
dojo.unimplemented("dojo.widget.DomWidget.getContainerWidth");
},createNodesFromText:function(){
dojo.unimplemented("dojo.widget.DomWidget.createNodesFromText");
}});
dojo.provide("dojo.lfx.toggle");
dojo.lfx.toggle.plain={show:function(node,_a16,_a17,_a18){
dojo.html.show(node);
if(dojo.lang.isFunction(_a18)){
_a18();
}
},hide:function(node,_a1a,_a1b,_a1c){
dojo.html.hide(node);
if(dojo.lang.isFunction(_a1c)){
_a1c();
}
}};
dojo.lfx.toggle.fade={show:function(node,_a1e,_a1f,_a20){
dojo.lfx.fadeShow(node,_a1e,_a1f,_a20).play();
},hide:function(node,_a22,_a23,_a24){
dojo.lfx.fadeHide(node,_a22,_a23,_a24).play();
}};
dojo.lfx.toggle.wipe={show:function(node,_a26,_a27,_a28){
dojo.lfx.wipeIn(node,_a26,_a27,_a28).play();
},hide:function(node,_a2a,_a2b,_a2c){
dojo.lfx.wipeOut(node,_a2a,_a2b,_a2c).play();
}};
dojo.lfx.toggle.explode={show:function(node,_a2e,_a2f,_a30,_a31){
dojo.lfx.explode(_a31||{x:0,y:0,width:0,height:0},node,_a2e,_a2f,_a30).play();
},hide:function(node,_a33,_a34,_a35,_a36){
dojo.lfx.implode(node,_a36||{x:0,y:0,width:0,height:0},_a33,_a34,_a35).play();
}};
dojo.provide("dojo.widget.HtmlWidget");
dojo.declare("dojo.widget.HtmlWidget",dojo.widget.DomWidget,{widgetType:"HtmlWidget",templateCssPath:null,templatePath:null,lang:"",toggle:"plain",toggleDuration:150,animationInProgress:false,initialize:function(args,frag){
},postMixInProperties:function(args,frag){
if(this.lang===""){
this.lang=null;
}
this.toggleObj=dojo.lfx.toggle[this.toggle.toLowerCase()]||dojo.lfx.toggle.plain;
},getContainerHeight:function(){
dojo.unimplemented("dojo.widget.HtmlWidget.getContainerHeight");
},getContainerWidth:function(){
return this.parent.domNode.offsetWidth;
},setNativeHeight:function(_a3b){
var ch=this.getContainerHeight();
},createNodesFromText:function(txt,wrap){
return dojo.html.createNodesFromText(txt,wrap);
},destroyRendering:function(_a3f){
try{
if(!_a3f&&this.domNode){
dojo.event.browser.clean(this.domNode);
}
this.domNode.parentNode.removeChild(this.domNode);
delete this.domNode;
}
catch(e){
}
},isShowing:function(){
return dojo.html.isShowing(this.domNode);
},toggleShowing:function(){
if(this.isHidden){
this.show();
}else{
this.hide();
}
},show:function(){
this.animationInProgress=true;
this.isHidden=false;
this.toggleObj.show(this.domNode,this.toggleDuration,null,dojo.lang.hitch(this,this.onShow),this.explodeSrc);
},onShow:function(){
this.animationInProgress=false;
this.checkSize();
},hide:function(){
this.animationInProgress=true;
this.isHidden=true;
this.toggleObj.hide(this.domNode,this.toggleDuration,null,dojo.lang.hitch(this,this.onHide),this.explodeSrc);
},onHide:function(){
this.animationInProgress=false;
},_isResized:function(w,h){
if(!this.isShowing()){
return false;
}
var wh=dojo.html.getMarginBox(this.domNode);
var _a43=w||wh.width;
var _a44=h||wh.height;
if(this.width==_a43&&this.height==_a44){
return false;
}
this.width=_a43;
this.height=_a44;
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
dojo.lang.forEach(this.children,function(_a47){
if(_a47.checkSize){
_a47.checkSize();
}
});
}});
dojo.provide("dojo.widget.*");
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
var _a4b=1;
for(var i=1;i<=n;i++){
_a4b*=i;
}
return _a4b;
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
var _a57=dojo.lang.isArray(arguments[0])?arguments[0]:arguments;
var mean=0;
for(var i=0;i<_a57.length;i++){
mean+=_a57[i];
}
return mean/_a57.length;
};
dojo.math.round=function(_a5a,_a5b){
if(!_a5b){
var _a5c=1;
}else{
var _a5c=Math.pow(10,_a5b);
}
return Math.round(_a5a*_a5c)/_a5c;
};
dojo.math.sd=dojo.math.standardDeviation=function(){
var _a5d=dojo.lang.isArray(arguments[0])?arguments[0]:arguments;
return Math.sqrt(dojo.math.variance(_a5d));
};
dojo.math.variance=function(){
var _a5e=dojo.lang.isArray(arguments[0])?arguments[0]:arguments;
var mean=0,_a60=0;
for(var i=0;i<_a5e.length;i++){
mean+=_a5e[i];
_a60+=Math.pow(_a5e[i],2);
}
return (_a60/_a5e.length)-Math.pow(mean/_a5e.length,2);
};
dojo.math.range=function(a,b,step){
if(arguments.length<2){
b=a;
a=0;
}
if(arguments.length<3){
step=1;
}
var _a65=[];
if(step>0){
for(var i=a;i<b;i+=step){
_a65.push(i);
}
}else{
if(step<0){
for(var i=a;i>b;i+=step){
_a65.push(i);
}
}else{
throw new Error("dojo.math.range: step must be non-zero");
}
}
return _a65;
};
dojo.provide("dojo.math.curves");
dojo.math.curves={Line:function(_a67,end){
this.start=_a67;
this.end=end;
this.dimensions=_a67.length;
for(var i=0;i<_a67.length;i++){
_a67[i]=Number(_a67[i]);
}
for(var i=0;i<end.length;i++){
end[i]=Number(end[i]);
}
this.getValue=function(n){
var _a6b=new Array(this.dimensions);
for(var i=0;i<this.dimensions;i++){
_a6b[i]=((this.end[i]-this.start[i])*n)+this.start[i];
}
return _a6b;
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
var _a6f=new Array(this.p[0].length);
for(var k=0;j<this.p[0].length;k++){
_a6f[k]=0;
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
_a6f[j]=C/D;
}
return _a6f;
};
this.p=pnts;
return this;
},CatmullRom:function(pnts,c){
this.getValue=function(step){
var _a79=step*(this.p.length-1);
var node=Math.floor(_a79);
var _a7b=_a79-node;
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
var u=_a7b;
var u2=_a7b*_a7b;
var u3=_a7b*_a7b*_a7b;
var _a83=new Array(this.p[0].length);
for(var k=0;k<this.p[0].length;k++){
var x1=(-this.c*this.p[i0][k])+((2-this.c)*this.p[i][k])+((this.c-2)*this.p[i1][k])+(this.c*this.p[i2][k]);
var x2=(2*this.c*this.p[i0][k])+((this.c-3)*this.p[i][k])+((3-2*this.c)*this.p[i1][k])+(-this.c*this.p[i2][k]);
var x3=(-this.c*this.p[i0][k])+(this.c*this.p[i1][k]);
var x4=this.p[i][k];
_a83[k]=x1*u3+x2*u2+x3*u+x4;
}
return _a83;
};
if(!c){
this.c=0.7;
}else{
this.c=c;
}
this.p=pnts;
return this;
},Arc:function(_a89,end,ccw){
var _a8c=dojo.math.points.midpoint(_a89,end);
var _a8d=dojo.math.points.translate(dojo.math.points.invert(_a8c),_a89);
var rad=Math.sqrt(Math.pow(_a8d[0],2)+Math.pow(_a8d[1],2));
var _a8f=dojo.math.radToDeg(Math.atan(_a8d[1]/_a8d[0]));
if(_a8d[0]<0){
_a8f-=90;
}else{
_a8f+=90;
}
dojo.math.curves.CenteredArc.call(this,_a8c,rad,_a8f,_a8f+(ccw?-180:180));
},CenteredArc:function(_a90,_a91,_a92,end){
this.center=_a90;
this.radius=_a91;
this.start=_a92||0;
this.end=end;
this.getValue=function(n){
var _a95=new Array(2);
var _a96=dojo.math.degToRad(this.start+((this.end-this.start)*n));
_a95[0]=this.center[0]+this.radius*Math.sin(_a96);
_a95[1]=this.center[1]-this.radius*Math.cos(_a96);
return _a95;
};
return this;
},Circle:function(_a97,_a98){
dojo.math.curves.CenteredArc.call(this,_a97,_a98,0,360);
return this;
},Path:function(){
var _a99=[];
var _a9a=[];
var _a9b=[];
var _a9c=0;
this.add=function(_a9d,_a9e){
if(_a9e<0){
dojo.raise("dojo.math.curves.Path.add: weight cannot be less than 0");
}
_a99.push(_a9d);
_a9a.push(_a9e);
_a9c+=_a9e;
computeRanges();
};
this.remove=function(_a9f){
for(var i=0;i<_a99.length;i++){
if(_a99[i]==_a9f){
_a99.splice(i,1);
_a9c-=_a9a.splice(i,1)[0];
break;
}
}
computeRanges();
};
this.removeAll=function(){
_a99=[];
_a9a=[];
_a9c=0;
};
this.getValue=function(n){
var _aa2=false,_aa3=0;
for(var i=0;i<_a9b.length;i++){
var r=_a9b[i];
if(n>=r[0]&&n<r[1]){
var subN=(n-r[0])/r[2];
_aa3=_a99[i].getValue(subN);
_aa2=true;
break;
}
}
if(!_aa2){
_aa3=_a99[_a99.length-1].getValue(1);
}
for(var j=0;j<i;j++){
_aa3=dojo.math.points.translate(_aa3,_a99[j].getValue(1));
}
return _aa3;
};
function computeRanges(){
var _aa8=0;
for(var i=0;i<_a9a.length;i++){
var end=_aa8+_a9a[i]/_a9c;
var len=end-_aa8;
_a9b[i]=[_aa8,end,len];
_aa8=end;
}
}
return this;
}};
dojo.provide("dojo.math.points");
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
dojo.provide("dojo.math.*");

