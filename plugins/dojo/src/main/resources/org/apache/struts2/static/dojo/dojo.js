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
dojo.version={major:0,minor:4,patch:2,flag:"",revision:Number("$Rev: 7616 $".match(/[0-9]+/)[0]),toString:function(){
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
function dj_load_init(e){
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
dojo.provide("dojo.lang.common");
dojo.lang.inherits=function(_ee,_ef){
if(!dojo.lang.isFunction(_ef)){
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
var _118=0;
for(var x in obj){
if(obj[x]&&(!tmp[x])){
_118++;
break;
}
}
return _118==0;
}else{
if(dojo.lang.isArrayLike(obj)||dojo.lang.isString(obj)){
return obj.length==0;
}
}
},map:function(arr,obj,_11c){
var _11d=dojo.lang.isString(arr);
if(_11d){
arr=arr.split("");
}
if(dojo.lang.isFunction(obj)&&(!_11c)){
_11c=obj;
obj=dj_global;
}else{
if(dojo.lang.isFunction(obj)&&_11c){
var _11e=obj;
obj=_11c;
_11c=_11e;
}
}
if(Array.map){
var _11f=Array.map(arr,_11c,obj);
}else{
var _11f=[];
for(var i=0;i<arr.length;++i){
_11f.push(_11c.call(obj,arr[i]));
}
}
if(_11d){
return _11f.join("");
}else{
return _11f;
}
},reduce:function(arr,_122,obj,_124){
var _125=_122;
if(arguments.length==2){
_124=_122;
_125=arr[0];
arr=arr.slice(1);
}else{
if(arguments.length==3){
if(dojo.lang.isFunction(obj)){
_124=obj;
obj=null;
}
}else{
if(dojo.lang.isFunction(obj)){
var tmp=_124;
_124=obj;
obj=tmp;
}
}
}
var ob=obj||dj_global;
dojo.lang.map(arr,function(val){
_125=_124.call(ob,_125,val);
});
return _125;
},forEach:function(_129,_12a,_12b){
if(dojo.lang.isString(_129)){
_129=_129.split("");
}
if(Array.forEach){
Array.forEach(_129,_12a,_12b);
}else{
if(!_12b){
_12b=dj_global;
}
for(var i=0,l=_129.length;i<l;i++){
_12a.call(_12b,_129[i],i,_129);
}
}
},_everyOrSome:function(_12e,arr,_130,_131){
if(dojo.lang.isString(arr)){
arr=arr.split("");
}
if(Array.every){
return Array[_12e?"every":"some"](arr,_130,_131);
}else{
if(!_131){
_131=dj_global;
}
for(var i=0,l=arr.length;i<l;i++){
var _134=_130.call(_131,arr[i],i,arr);
if(_12e&&!_134){
return false;
}else{
if((!_12e)&&(_134)){
return true;
}
}
}
return Boolean(_12e);
}
},every:function(arr,_136,_137){
return this._everyOrSome(true,arr,_136,_137);
},some:function(arr,_139,_13a){
return this._everyOrSome(false,arr,_139,_13a);
},filter:function(arr,_13c,_13d){
var _13e=dojo.lang.isString(arr);
if(_13e){
arr=arr.split("");
}
var _13f;
if(Array.filter){
_13f=Array.filter(arr,_13c,_13d);
}else{
if(!_13d){
if(arguments.length>=3){
dojo.raise("thisObject doesn't exist!");
}
_13d=dj_global;
}
_13f=[];
for(var i=0;i<arr.length;i++){
if(_13c.call(_13d,arr[i],i,arr)){
_13f.push(arr[i]);
}
}
}
if(_13e){
return _13f.join("");
}else{
return _13f;
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
},toArray:function(_144,_145){
var _146=[];
for(var i=_145||0;i<_144.length;i++){
_146.push(_144[i]);
}
return _146;
}});
dojo.provide("dojo.lang.extras");
dojo.lang.setTimeout=function(func,_149){
var _14a=window,_14b=2;
if(!dojo.lang.isFunction(func)){
_14a=func;
func=_149;
_149=arguments[2];
_14b++;
}
if(dojo.lang.isString(func)){
func=_14a[func];
}
var args=[];
for(var i=_14b;i<arguments.length;i++){
args.push(arguments[i]);
}
return dojo.global().setTimeout(function(){
func.apply(_14a,args);
},_149);
};
dojo.lang.clearTimeout=function(_14e){
dojo.global().clearTimeout(_14e);
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
dojo.lang.getObjPathValue=function(_157,_158,_159){
with(dojo.parseObjPath(_157,_158,_159)){
return dojo.evalProp(prop,obj,_159);
}
};
dojo.lang.setObjPathValue=function(_15a,_15b,_15c,_15d){
dojo.deprecated("dojo.lang.setObjPathValue","use dojo.parseObjPath and the '=' operator","0.6");
if(arguments.length<4){
_15d=true;
}
with(dojo.parseObjPath(_15a,_15c,_15d)){
if(obj&&(_15d||(prop in obj))){
obj[prop]=_15b;
}
}
};
dojo.provide("dojo.lang.declare");
dojo.lang.declare=function(_15e,_15f,init,_161){
if((dojo.lang.isFunction(_161))||((!_161)&&(!dojo.lang.isFunction(init)))){
var temp=_161;
_161=init;
init=temp;
}
var _163=[];
if(dojo.lang.isArray(_15f)){
_163=_15f;
_15f=_163.shift();
}
if(!init){
init=dojo.evalObjPath(_15e,false);
if((init)&&(!dojo.lang.isFunction(init))){
init=null;
}
}
var ctor=dojo.lang.declare._makeConstructor();
var scp=(_15f?_15f.prototype:null);
if(scp){
scp.prototyping=true;
ctor.prototype=new _15f();
scp.prototyping=false;
}
ctor.superclass=scp;
ctor.mixins=_163;
for(var i=0,l=_163.length;i<l;i++){
dojo.lang.extend(ctor,_163[i].prototype);
}
ctor.prototype.initializer=null;
ctor.prototype.declaredClass=_15e;
if(dojo.lang.isArray(_161)){
dojo.lang.extend.apply(dojo.lang,[ctor].concat(_161));
}else{
dojo.lang.extend(ctor,(_161)||{});
}
dojo.lang.extend(ctor,dojo.lang.declare._common);
ctor.prototype.constructor=ctor;
ctor.prototype.initializer=(ctor.prototype.initializer)||(init)||(function(){
});
var _168=dojo.parseObjPath(_15e,null,true);
_168.obj[_168.prop]=ctor;
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
},_contextMethod:function(_16e,_16f,args){
var _171,_172=this.___proto;
this.___proto=_16e;
try{
_171=_16e[_16f].apply(this,(args||[]));
}
catch(e){
throw e;
}
finally{
this.___proto=_172;
}
return _171;
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
dojo.provide("dojo.lang.func");
dojo.lang.hitch=function(_178,_179){
var fcn=(dojo.lang.isString(_179)?_178[_179]:_179)||function(){
};
return function(){
return fcn.apply(_178,arguments);
};
};
dojo.lang.anonCtr=0;
dojo.lang.anon={};
dojo.lang.nameAnonFunc=function(_17b,_17c,_17d){
var nso=(_17c||dojo.lang.anon);
if((_17d)||((dj_global["djConfig"])&&(djConfig["slowAnonFuncLookups"]==true))){
for(var x in nso){
try{
if(nso[x]===_17b){
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
nso[ret]=_17b;
return ret;
};
dojo.lang.forward=function(_181){
return function(){
return this[_181].apply(this,arguments);
};
};
dojo.lang.curry=function(_182,func){
var _184=[];
_182=_182||dj_global;
if(dojo.lang.isString(func)){
func=_182[func];
}
for(var x=2;x<arguments.length;x++){
_184.push(arguments[x]);
}
var _186=(func["__preJoinArity"]||func.length)-_184.length;
function gather(_187,_188,_189){
var _18a=_189;
var _18b=_188.slice(0);
for(var x=0;x<_187.length;x++){
_18b.push(_187[x]);
}
_189=_189-_187.length;
if(_189<=0){
var res=func.apply(_182,_18b);
_189=_18a;
return res;
}else{
return function(){
return gather(arguments,_18b,_189);
};
}
}
return gather([],_184,_186);
};
dojo.lang.curryArguments=function(_18e,func,args,_191){
var _192=[];
var x=_191||0;
for(x=_191;x<args.length;x++){
_192.push(args[x]);
}
return dojo.lang.curry.apply(dojo.lang,[_18e,func].concat(_192));
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
dojo.lang.delayThese=function(farr,cb,_198,_199){
if(!farr.length){
if(typeof _199=="function"){
_199();
}
return;
}
if((typeof _198=="undefined")&&(typeof cb=="number")){
_198=cb;
cb=function(){
};
}else{
if(!cb){
cb=function(){
};
if(!_198){
_198=0;
}
}
}
setTimeout(function(){
(farr.shift())();
cb();
dojo.lang.delayThese(farr,cb,_198,_199);
},_198);
};
dojo.provide("dojo.event.common");
dojo.event=new function(){
this._canTimeout=dojo.lang.isFunction(dj_global["setTimeout"])||dojo.lang.isAlien(dj_global["setTimeout"]);
function interpolateArgs(args,_19b){
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
var _19e=dl.nameAnonFunc(args[2],ao.adviceObj,_19b);
ao.adviceFunc=_19e;
}else{
if((dl.isFunction(args[0]))&&(dl.isObject(args[1]))&&(dl.isString(args[2]))){
ao.adviceType="after";
ao.srcObj=dj_global;
var _19e=dl.nameAnonFunc(args[0],ao.srcObj,_19b);
ao.srcFunc=_19e;
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
var _19e=dl.nameAnonFunc(args[1],dj_global,_19b);
ao.srcFunc=_19e;
ao.adviceObj=args[2];
ao.adviceFunc=args[3];
}else{
if((dl.isString(args[0]))&&(dl.isObject(args[1]))&&(dl.isString(args[2]))&&(dl.isFunction(args[3]))){
ao.srcObj=args[1];
ao.srcFunc=args[2];
var _19e=dl.nameAnonFunc(args[3],dj_global,_19b);
ao.adviceObj=dj_global;
ao.adviceFunc=_19e;
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
var _19e=dl.nameAnonFunc(ao.aroundFunc,ao.aroundObj,_19b);
ao.aroundFunc=_19e;
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
if(dojo.lang.isArray(ao.srcObj)&&ao.srcObj!=""){
var _1a0={};
for(var x in ao){
_1a0[x]=ao[x];
}
var mjps=[];
dojo.lang.forEach(ao.srcObj,function(src){
if((dojo.render.html.capable)&&(dojo.lang.isString(src))){
src=dojo.byId(src);
}
_1a0.srcObj=src;
mjps.push(dojo.event.connect.call(dojo.event,_1a0));
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
var _1a8;
if((arguments.length==1)&&(typeof a1=="object")){
_1a8=a1;
}else{
_1a8={srcObj:a1,srcFunc:a2};
}
_1a8.adviceFunc=function(){
var _1a9=[];
for(var x=0;x<arguments.length;x++){
_1a9.push(arguments[x]);
}
dojo.debug("("+_1a8.srcObj+")."+_1a8.srcFunc,":",_1a9.join(", "));
};
this.kwConnect(_1a8);
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
this._kwConnectImpl=function(_1b1,_1b2){
var fn=(_1b2)?"disconnect":"connect";
if(typeof _1b1["srcFunc"]=="function"){
_1b1.srcObj=_1b1["srcObj"]||dj_global;
var _1b4=dojo.lang.nameAnonFunc(_1b1.srcFunc,_1b1.srcObj,true);
_1b1.srcFunc=_1b4;
}
if(typeof _1b1["adviceFunc"]=="function"){
_1b1.adviceObj=_1b1["adviceObj"]||dj_global;
var _1b4=dojo.lang.nameAnonFunc(_1b1.adviceFunc,_1b1.adviceObj,true);
_1b1.adviceFunc=_1b4;
}
_1b1.srcObj=_1b1["srcObj"]||dj_global;
_1b1.adviceObj=_1b1["adviceObj"]||_1b1["targetObj"]||dj_global;
_1b1.adviceFunc=_1b1["adviceFunc"]||_1b1["targetFunc"];
return dojo.event[fn](_1b1);
};
this.kwConnect=function(_1b5){
return this._kwConnectImpl(_1b5,false);
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
this.kwDisconnect=function(_1b8){
return this._kwConnectImpl(_1b8,true);
};
};
dojo.event.MethodInvocation=function(_1b9,obj,args){
this.jp_=_1b9;
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
dojo.event.MethodJoinPoint=function(obj,_1c1){
this.object=obj||dj_global;
this.methodname=_1c1;
this.methodfunc=this.object[_1c1];
this.squelch=false;
};
dojo.event.MethodJoinPoint.getForMethod=function(obj,_1c3){
if(!obj){
obj=dj_global;
}
var ofn=obj[_1c3];
if(!ofn){
ofn=obj[_1c3]=function(){
};
if(!obj[_1c3]){
dojo.raise("Cannot set do-nothing method on that object "+_1c3);
}
}else{
if((typeof ofn!="function")&&(!dojo.lang.isFunction(ofn))&&(!dojo.lang.isAlien(ofn))){
return null;
}
}
var _1c5=_1c3+"$joinpoint";
var _1c6=_1c3+"$joinpoint$method";
var _1c7=obj[_1c5];
if(!_1c7){
var _1c8=false;
if(dojo.event["browser"]){
if((obj["attachEvent"])||(obj["nodeType"])||(obj["addEventListener"])){
_1c8=true;
dojo.event.browser.addClobberNodeAttrs(obj,[_1c5,_1c6,_1c3]);
}
}
var _1c9=ofn.length;
obj[_1c6]=ofn;
_1c7=obj[_1c5]=new dojo.event.MethodJoinPoint(obj,_1c6);
if(!_1c8){
obj[_1c3]=function(){
return _1c7.run.apply(_1c7,arguments);
};
}else{
obj[_1c3]=function(){
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
return _1c7.run.apply(_1c7,args);
};
}
obj[_1c3].__preJoinArity=_1c9;
}
return _1c7;
};
dojo.lang.extend(dojo.event.MethodJoinPoint,{squelch:false,unintercept:function(){
this.object[this.methodname]=this.methodfunc;
this.before=[];
this.after=[];
this.around=[];
},disconnect:dojo.lang.forward("unintercept"),run:function(){
var obj=this.object||dj_global;
var args=arguments;
var _1cf=[];
for(var x=0;x<args.length;x++){
_1cf[x]=args[x];
}
var _1d1=function(marr){
if(!marr){
dojo.debug("Null argument to unrollAdvice()");
return;
}
var _1d3=marr[0]||dj_global;
var _1d4=marr[1];
if(!_1d3[_1d4]){
dojo.raise("function \""+_1d4+"\" does not exist on \""+_1d3+"\"");
}
var _1d5=marr[2]||dj_global;
var _1d6=marr[3];
var msg=marr[6];
var _1d8=marr[7];
if(_1d8>-1){
if(_1d8==0){
return;
}
marr[7]--;
}
var _1d9;
var to={args:[],jp_:this,object:obj,proceed:function(){
return _1d3[_1d4].apply(_1d3,to.args);
}};
to.args=_1cf;
var _1db=parseInt(marr[4]);
var _1dc=((!isNaN(_1db))&&(marr[4]!==null)&&(typeof marr[4]!="undefined"));
if(marr[5]){
var rate=parseInt(marr[5]);
var cur=new Date();
var _1df=false;
if((marr["last"])&&((cur-marr.last)<=rate)){
if(dojo.event._canTimeout){
if(marr["delayTimer"]){
clearTimeout(marr.delayTimer);
}
var tod=parseInt(rate*2);
var mcpy=dojo.lang.shallowCopy(marr);
marr.delayTimer=setTimeout(function(){
mcpy[5]=0;
_1d1(mcpy);
},tod);
}
return;
}else{
marr.last=cur;
}
}
if(_1d6){
_1d5[_1d6].call(_1d5,to);
}else{
if((_1dc)&&((dojo.render.html)||(dojo.render.svg))){
dj_global["setTimeout"](function(){
if(msg){
_1d3[_1d4].call(_1d3,to);
}else{
_1d3[_1d4].apply(_1d3,args);
}
},_1db);
}else{
if(msg){
_1d3[_1d4].call(_1d3,to);
}else{
_1d3[_1d4].apply(_1d3,args);
}
}
}
};
var _1e2=function(){
if(this.squelch){
try{
return _1d1.apply(this,arguments);
}
catch(e){
dojo.debug(e);
}
}else{
return _1d1.apply(this,arguments);
}
};
if((this["before"])&&(this.before.length>0)){
dojo.lang.forEach(this.before.concat(new Array()),_1e2);
}
var _1e3;
try{
if((this["around"])&&(this.around.length>0)){
var mi=new dojo.event.MethodInvocation(this,obj,args);
_1e3=mi.proceed();
}else{
if(this.methodfunc){
_1e3=this.object[this.methodname].apply(this.object,args);
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
dojo.lang.forEach(this.after.concat(new Array()),_1e2);
}
return (this.methodfunc)?_1e3:null;
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
},addAdvice:function(_1e8,_1e9,_1ea,_1eb,_1ec,_1ed,once,_1ef,rate,_1f1,_1f2){
var arr=this.getArr(_1ec);
if(!arr){
dojo.raise("bad this: "+this);
}
var ao=[_1e8,_1e9,_1ea,_1eb,_1ef,rate,_1f1,_1f2];
if(once){
if(this.hasAdvice(_1e8,_1e9,_1ec,arr)>=0){
return;
}
}
if(_1ed=="first"){
arr.unshift(ao);
}else{
arr.push(ao);
}
},hasAdvice:function(_1f5,_1f6,_1f7,arr){
if(!arr){
arr=this.getArr(_1f7);
}
var ind=-1;
for(var x=0;x<arr.length;x++){
var aao=(typeof _1f6=="object")?(new String(_1f6)).toString():_1f6;
var a1o=(typeof arr[x][1]=="object")?(new String(arr[x][1])).toString():arr[x][1];
if((arr[x][0]==_1f5)&&(a1o==aao)){
ind=x;
}
}
return ind;
},removeAdvice:function(_1fd,_1fe,_1ff,once){
var arr=this.getArr(_1ff);
var ind=this.hasAdvice(_1fd,_1fe,_1ff,arr);
if(ind==-1){
return false;
}
while(ind!=-1){
arr.splice(ind,1);
if(once){
break;
}
ind=this.hasAdvice(_1fd,_1fe,_1ff,arr);
}
return true;
}});
dojo.provide("dojo.event.topic");
dojo.event.topic=new function(){
this.topics={};
this.getTopic=function(_203){
if(!this.topics[_203]){
this.topics[_203]=new this.TopicImpl(_203);
}
return this.topics[_203];
};
this.registerPublisher=function(_204,obj,_206){
var _204=this.getTopic(_204);
_204.registerPublisher(obj,_206);
};
this.subscribe=function(_207,obj,_209){
var _207=this.getTopic(_207);
_207.subscribe(obj,_209);
};
this.unsubscribe=function(_20a,obj,_20c){
var _20a=this.getTopic(_20a);
_20a.unsubscribe(obj,_20c);
};
this.destroy=function(_20d){
this.getTopic(_20d).destroy();
delete this.topics[_20d];
};
this.publishApply=function(_20e,args){
var _20e=this.getTopic(_20e);
_20e.sendMessage.apply(_20e,args);
};
this.publish=function(_210,_211){
var _210=this.getTopic(_210);
var args=[];
for(var x=1;x<arguments.length;x++){
args.push(arguments[x]);
}
_210.sendMessage.apply(_210,args);
};
};
dojo.event.topic.TopicImpl=function(_214){
this.topicName=_214;
this.subscribe=function(_215,_216){
var tf=_216||_215;
var to=(!_216)?dj_global:_215;
return dojo.event.kwConnect({srcObj:this,srcFunc:"sendMessage",adviceObj:to,adviceFunc:tf});
};
this.unsubscribe=function(_219,_21a){
var tf=(!_21a)?_219:_21a;
var to=(!_21a)?null:_219;
return dojo.event.kwDisconnect({srcObj:this,srcFunc:"sendMessage",adviceObj:to,adviceFunc:tf});
};
this._getJoinPoint=function(){
return dojo.event.MethodJoinPoint.getForMethod(this,"sendMessage");
};
this.setSquelch=function(_21d){
this._getJoinPoint().squelch=_21d;
};
this.destroy=function(){
this._getJoinPoint().disconnect();
};
this.registerPublisher=function(_21e,_21f){
dojo.event.connect(_21e,_21f,this,"sendMessage");
};
this.sendMessage=function(_220){
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
this.clobber=function(_223){
var na;
var tna;
if(_223){
tna=_223.all||_223.getElementsByTagName("*");
na=[_223];
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
var _227={};
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
var _22c=0;
this.normalizedEventName=function(_22d){
switch(_22d){
case "CheckboxStateChange":
case "DOMAttrModified":
case "DOMMenuItemActive":
case "DOMMenuItemInactive":
case "DOMMouseScroll":
case "DOMNodeInserted":
case "DOMNodeRemoved":
case "RadioStateChange":
return _22d;
break;
default:
var lcn=_22d.toLowerCase();
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
this.addClobberNodeAttrs=function(node,_232){
if(!dojo.render.html.ie){
return;
}
this.addClobberNode(node);
for(var x=0;x<_232.length;x++){
node.__clobberAttrs__.push(_232[x]);
}
};
this.removeListener=function(node,_235,fp,_237){
if(!_237){
var _237=false;
}
_235=dojo.event.browser.normalizedEventName(_235);
if(_235=="key"){
if(dojo.render.html.ie){
this.removeListener(node,"onkeydown",fp,_237);
}
_235="keypress";
}
if(node.removeEventListener){
node.removeEventListener(_235,fp,_237);
}
};
this.addListener=function(node,_239,fp,_23b,_23c){
if(!node){
return;
}
if(!_23b){
var _23b=false;
}
_239=dojo.event.browser.normalizedEventName(_239);
if(_239=="key"){
if(dojo.render.html.ie){
this.addListener(node,"onkeydown",fp,_23b,_23c);
}
_239="keypress";
}
if(!_23c){
var _23d=function(evt){
if(!evt){
evt=window.event;
}
var ret=fp(dojo.event.browser.fixEvent(evt,this));
if(_23b){
dojo.event.browser.stopEvent(evt);
}
return ret;
};
}else{
_23d=fp;
}
if(node.addEventListener){
node.addEventListener(_239,_23d,_23b);
return _23d;
}else{
_239="on"+_239;
if(typeof node[_239]=="function"){
var _240=node[_239];
node[_239]=function(e){
_240(e);
return _23d(e);
};
}else{
node[_239]=_23d;
}
if(dojo.render.html.ie){
this.addClobberNodeAttrs(node,[_239]);
}
return _23d;
}
};
this.isEvent=function(obj){
return (typeof obj!="undefined")&&(obj)&&(typeof Event!="undefined")&&(obj.eventPhase);
};
this.currentEvent=null;
this.callListener=function(_243,_244){
if(typeof _243!="function"){
dojo.raise("listener not a function: "+_243);
}
dojo.event.browser.currentEvent.currentTarget=_244;
return _243.call(_244,dojo.event.browser.currentEvent);
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
this.fixEvent=function(evt,_247){
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
var _249=evt.keyCode;
if(_249>=65&&_249<=90&&evt.shiftKey==false){
_249+=32;
}
if(_249>=1&&_249<=26&&evt.ctrlKey){
_249+=96;
}
evt.key=String.fromCharCode(_249);
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
var _249=evt.which;
if((evt.ctrlKey||evt.altKey||evt.metaKey)&&(evt.which>=65&&evt.which<=90&&evt.shiftKey==false)){
_249+=32;
}
evt.key=String.fromCharCode(_249);
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
evt.currentTarget=(_247?_247:evt.srcElement);
}
if(!evt.layerX){
evt.layerX=evt.offsetX;
}
if(!evt.layerY){
evt.layerY=evt.offsetY;
}
var doc=(evt.srcElement&&evt.srcElement.ownerDocument)?evt.srcElement.ownerDocument:document;
var _24b=((dojo.render.html.ie55)||(doc["compatMode"]=="BackCompat"))?doc.body:doc.documentElement;
if(!evt.pageX){
evt.pageX=evt.clientX+(_24b.scrollLeft||0);
}
if(!evt.pageY){
evt.pageY=evt.clientY+(_24b.scrollTop||0);
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
dojo.provide("dojo.event");
dojo.deprecated("dojo.event","replaced by dojo.event.*","0.5");
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
dojo.string.repeat=function(str,_253,_254){
var out="";
for(var i=0;i<_253;i++){
out+=str;
if(_254&&i<_253-1){
out+=_254;
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
dojo.provide("dojo.string.extras");
dojo.string.substituteParams=function(_262,hash){
var map=(typeof hash=="object")?hash:dojo.lang.toArray(arguments,1);
return _262.replace(/\%\{(\w+)\}/g,function(_265,key){
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
var _268=str.split(" ");
for(var i=0;i<_268.length;i++){
_268[i]=_268[i].charAt(0).toUpperCase()+_268[i].substring(1);
}
return _268.join(" ");
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
var _26d=escape(str);
var _26e,re=/%u([0-9A-F]{4})/i;
while((_26e=_26d.match(re))){
var num=Number("0x"+_26e[1]);
var _271=escape("&#"+num+";");
ret+=_26d.substring(0,_26e.index)+_271;
_26d=_26d.substring(_26e.index+_26e[0].length);
}
ret+=_26d.replace(/\+/g,"%2B");
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
dojo.string.escapeXml=function(str,_276){
str=str.replace(/&/gm,"&amp;").replace(/</gm,"&lt;").replace(/>/gm,"&gt;").replace(/"/gm,"&quot;");
if(!_276){
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
dojo.string.endsWith=function(str,end,_27f){
if(_27f){
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
dojo.string.startsWith=function(str,_283,_284){
if(_284){
str=str.toLowerCase();
_283=_283.toLowerCase();
}
return str.indexOf(_283)==0;
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
dojo.string.normalizeNewlines=function(text,_28a){
if(_28a=="\n"){
text=text.replace(/\r\n/g,"\n");
text=text.replace(/\r/g,"\n");
}else{
if(_28a=="\r"){
text=text.replace(/\r\n/g,"\r");
text=text.replace(/\n/g,"\r");
}else{
text=text.replace(/([^\r])\n/g,"$1\r\n").replace(/\r([^\n])/g,"\r\n$1");
}
}
return text;
};
dojo.string.splitEscaped=function(str,_28c){
var _28d=[];
for(var i=0,_28f=0;i<str.length;i++){
if(str.charAt(i)=="\\"){
i++;
continue;
}
if(str.charAt(i)==_28c){
_28d.push(str.substring(_28f,i));
_28f=i+1;
}
}
_28d.push(str.substr(_28f));
return _28d;
};
dojo.provide("dojo.string");
dojo.provide("dojo.io.common");
dojo.io.transports=[];
dojo.io.hdlrFuncNames=["load","error","timeout"];
dojo.io.Request=function(url,_291,_292,_293){
if((arguments.length==1)&&(arguments[0].constructor==Object)){
this.fromKwArgs(arguments[0]);
}else{
this.url=url;
if(_291){
this.mimetype=_291;
}
if(_292){
this.transport=_292;
}
if(arguments.length>=4){
this.changeUrl=_293;
}
}
};
dojo.lang.extend(dojo.io.Request,{url:"",mimetype:"text/plain",method:"GET",content:undefined,transport:undefined,changeUrl:undefined,formNode:undefined,sync:false,bindSuccess:false,useCache:false,preventCache:false,load:function(type,data,_296,_297){
},error:function(type,_299,_29a,_29b){
},timeout:function(type,_29d,_29e,_29f){
},handle:function(type,data,_2a2,_2a3){
},timeoutSeconds:0,abort:function(){
},fromKwArgs:function(_2a4){
if(_2a4["url"]){
_2a4.url=_2a4.url.toString();
}
if(_2a4["formNode"]){
_2a4.formNode=dojo.byId(_2a4.formNode);
}
if(!_2a4["method"]&&_2a4["formNode"]&&_2a4["formNode"].method){
_2a4.method=_2a4["formNode"].method;
}
if(!_2a4["handle"]&&_2a4["handler"]){
_2a4.handle=_2a4.handler;
}
if(!_2a4["load"]&&_2a4["loaded"]){
_2a4.load=_2a4.loaded;
}
if(!_2a4["changeUrl"]&&_2a4["changeURL"]){
_2a4.changeUrl=_2a4.changeURL;
}
_2a4.encoding=dojo.lang.firstValued(_2a4["encoding"],djConfig["bindEncoding"],"");
_2a4.sendTransport=dojo.lang.firstValued(_2a4["sendTransport"],djConfig["ioSendTransport"],false);
var _2a5=dojo.lang.isFunction;
for(var x=0;x<dojo.io.hdlrFuncNames.length;x++){
var fn=dojo.io.hdlrFuncNames[x];
if(_2a4[fn]&&_2a5(_2a4[fn])){
continue;
}
if(_2a4["handle"]&&_2a5(_2a4["handle"])){
_2a4[fn]=_2a4.handle;
}
}
dojo.lang.mixin(this,_2a4);
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
dojo.io.bind=function(_2ac){
if(!(_2ac instanceof dojo.io.Request)){
try{
_2ac=new dojo.io.Request(_2ac);
}
catch(e){
dojo.debug(e);
}
}
var _2ad="";
if(_2ac["transport"]){
_2ad=_2ac["transport"];
if(!this[_2ad]){
dojo.io.sendBindError(_2ac,"No dojo.io.bind() transport with name '"+_2ac["transport"]+"'.");
return _2ac;
}
if(!this[_2ad].canHandle(_2ac)){
dojo.io.sendBindError(_2ac,"dojo.io.bind() transport with name '"+_2ac["transport"]+"' cannot handle this type of request.");
return _2ac;
}
}else{
for(var x=0;x<dojo.io.transports.length;x++){
var tmp=dojo.io.transports[x];
if((this[tmp])&&(this[tmp].canHandle(_2ac))){
_2ad=tmp;
break;
}
}
if(_2ad==""){
dojo.io.sendBindError(_2ac,"None of the loaded transports for dojo.io.bind()"+" can handle the request.");
return _2ac;
}
}
this[_2ad].bind(_2ac);
_2ac.bindSuccess=true;
return _2ac;
};
dojo.io.sendBindError=function(_2b0,_2b1){
if((typeof _2b0.error=="function"||typeof _2b0.handle=="function")&&(typeof setTimeout=="function"||typeof setTimeout=="object")){
var _2b2=new dojo.io.Error(_2b1);
setTimeout(function(){
_2b0[(typeof _2b0.error=="function")?"error":"handle"]("error",_2b2,null,_2b0);
},50);
}else{
dojo.raise(_2b1);
}
};
dojo.io.queueBind=function(_2b3){
if(!(_2b3 instanceof dojo.io.Request)){
try{
_2b3=new dojo.io.Request(_2b3);
}
catch(e){
dojo.debug(e);
}
}
var _2b4=_2b3.load;
_2b3.load=function(){
dojo.io._queueBindInFlight=false;
var ret=_2b4.apply(this,arguments);
dojo.io._dispatchNextQueueBind();
return ret;
};
var _2b6=_2b3.error;
_2b3.error=function(){
dojo.io._queueBindInFlight=false;
var ret=_2b6.apply(this,arguments);
dojo.io._dispatchNextQueueBind();
return ret;
};
dojo.io._bindQueue.push(_2b3);
dojo.io._dispatchNextQueueBind();
return _2b3;
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
dojo.io.argsFromMap=function(map,_2b9,last){
var enc=/utf/i.test(_2b9||"")?encodeURIComponent:dojo.string.encodeAscii;
var _2bc=[];
var _2bd=new Object();
for(var name in map){
var _2bf=function(elt){
var val=enc(name)+"="+enc(elt);
_2bc[(last==name)?"push":"unshift"](val);
};
if(!_2bd[name]){
var _2c2=map[name];
if(dojo.lang.isArray(_2c2)){
dojo.lang.forEach(_2c2,_2bf);
}else{
_2bf(_2c2);
}
}
}
return _2bc.join("&");
};
dojo.io.setIFrameSrc=function(_2c3,src,_2c5){
try{
var r=dojo.render.html;
if(!_2c5){
if(r.safari){
_2c3.location=src;
}else{
frames[_2c3.name].location=src;
}
}else{
var idoc;
if(r.ie){
idoc=_2c3.contentWindow.document;
}else{
if(r.safari){
idoc=_2c3.document;
}else{
idoc=_2c3.contentWindow;
}
}
if(!idoc){
_2c3.location=src;
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
var _2c9=dojo.doc();
do{
var id="dj_unique_"+(++arguments.callee._idIncrement);
}while(_2c9.getElementById(id));
return id;
};
dojo.dom.getUniqueId._idIncrement=0;
dojo.dom.firstElement=dojo.dom.getFirstChildElement=function(_2cb,_2cc){
var node=_2cb.firstChild;
while(node&&node.nodeType!=dojo.dom.ELEMENT_NODE){
node=node.nextSibling;
}
if(_2cc&&node&&node.tagName&&node.tagName.toLowerCase()!=_2cc.toLowerCase()){
node=dojo.dom.nextElement(node,_2cc);
}
return node;
};
dojo.dom.lastElement=dojo.dom.getLastChildElement=function(_2ce,_2cf){
var node=_2ce.lastChild;
while(node&&node.nodeType!=dojo.dom.ELEMENT_NODE){
node=node.previousSibling;
}
if(_2cf&&node&&node.tagName&&node.tagName.toLowerCase()!=_2cf.toLowerCase()){
node=dojo.dom.prevElement(node,_2cf);
}
return node;
};
dojo.dom.nextElement=dojo.dom.getNextSiblingElement=function(node,_2d2){
if(!node){
return null;
}
do{
node=node.nextSibling;
}while(node&&node.nodeType!=dojo.dom.ELEMENT_NODE);
if(node&&_2d2&&_2d2.toLowerCase()!=node.tagName.toLowerCase()){
return dojo.dom.nextElement(node,_2d2);
}
return node;
};
dojo.dom.prevElement=dojo.dom.getPreviousSiblingElement=function(node,_2d4){
if(!node){
return null;
}
if(_2d4){
_2d4=_2d4.toLowerCase();
}
do{
node=node.previousSibling;
}while(node&&node.nodeType!=dojo.dom.ELEMENT_NODE);
if(node&&_2d4&&_2d4.toLowerCase()!=node.tagName.toLowerCase()){
return dojo.dom.prevElement(node,_2d4);
}
return node;
};
dojo.dom.moveChildren=function(_2d5,_2d6,trim){
var _2d8=0;
if(trim){
while(_2d5.hasChildNodes()&&_2d5.firstChild.nodeType==dojo.dom.TEXT_NODE){
_2d5.removeChild(_2d5.firstChild);
}
while(_2d5.hasChildNodes()&&_2d5.lastChild.nodeType==dojo.dom.TEXT_NODE){
_2d5.removeChild(_2d5.lastChild);
}
}
while(_2d5.hasChildNodes()){
_2d6.appendChild(_2d5.firstChild);
_2d8++;
}
return _2d8;
};
dojo.dom.copyChildren=function(_2d9,_2da,trim){
var _2dc=_2d9.cloneNode(true);
return this.moveChildren(_2dc,_2da,trim);
};
dojo.dom.replaceChildren=function(node,_2de){
var _2df=[];
if(dojo.render.html.ie){
for(var i=0;i<node.childNodes.length;i++){
_2df.push(node.childNodes[i]);
}
}
dojo.dom.removeChildren(node);
node.appendChild(_2de);
for(var i=0;i<_2df.length;i++){
dojo.dom.destroyNode(_2df[i]);
}
};
dojo.dom.removeChildren=function(node){
var _2e2=node.childNodes.length;
while(node.hasChildNodes()){
dojo.dom.removeNode(node.firstChild);
}
return _2e2;
};
dojo.dom.replaceNode=function(node,_2e4){
return node.parentNode.replaceChild(_2e4,node);
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
dojo.dom.getAncestors=function(node,_2e8,_2e9){
var _2ea=[];
var _2eb=(_2e8&&(_2e8 instanceof Function||typeof _2e8=="function"));
while(node){
if(!_2eb||_2e8(node)){
_2ea.push(node);
}
if(_2e9&&_2ea.length>0){
return _2ea[0];
}
node=node.parentNode;
}
if(_2e9){
return null;
}
return _2ea;
};
dojo.dom.getAncestorsByTag=function(node,tag,_2ee){
tag=tag.toLowerCase();
return dojo.dom.getAncestors(node,function(el){
return ((el.tagName)&&(el.tagName.toLowerCase()==tag));
},_2ee);
};
dojo.dom.getFirstAncestorByTag=function(node,tag){
return dojo.dom.getAncestorsByTag(node,tag,true);
};
dojo.dom.isDescendantOf=function(node,_2f3,_2f4){
if(_2f4&&node){
node=node.parentNode;
}
while(node){
if(node==_2f3){
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
var _2f7=dojo.doc();
if(!dj_undef("ActiveXObject")){
var _2f8=["MSXML2","Microsoft","MSXML","MSXML3"];
for(var i=0;i<_2f8.length;i++){
try{
doc=new ActiveXObject(_2f8[i]+".XMLDOM");
}
catch(e){
}
if(doc){
break;
}
}
}else{
if((_2f7.implementation)&&(_2f7.implementation.createDocument)){
doc=_2f7.implementation.createDocument("","",null);
}
}
return doc;
};
dojo.dom.createDocumentFromText=function(str,_2fb){
if(!_2fb){
_2fb="text/xml";
}
if(!dj_undef("DOMParser")){
var _2fc=new DOMParser();
return _2fc.parseFromString(str,_2fb);
}else{
if(!dj_undef("ActiveXObject")){
var _2fd=dojo.dom.createDocument();
if(_2fd){
_2fd.async=false;
_2fd.loadXML(str);
return _2fd;
}else{
dojo.debug("toXml didn't work?");
}
}else{
var _2fe=dojo.doc();
if(_2fe.createElement){
var tmp=_2fe.createElement("xml");
tmp.innerHTML=str;
if(_2fe.implementation&&_2fe.implementation.createDocument){
var _300=_2fe.implementation.createDocument("foo","",null);
for(var i=0;i<tmp.childNodes.length;i++){
_300.importNode(tmp.childNodes.item(i),true);
}
return _300;
}
return ((tmp.document)&&(tmp.document.firstChild?tmp.document.firstChild:tmp));
}
}
}
return null;
};
dojo.dom.prependChild=function(node,_303){
if(_303.firstChild){
_303.insertBefore(node,_303.firstChild);
}else{
_303.appendChild(node);
}
return true;
};
dojo.dom.insertBefore=function(node,ref,_306){
if((_306!=true)&&(node===ref||node.nextSibling===ref)){
return false;
}
var _307=ref.parentNode;
_307.insertBefore(node,ref);
return true;
};
dojo.dom.insertAfter=function(node,ref,_30a){
var pn=ref.parentNode;
if(ref==pn.lastChild){
if((_30a!=true)&&(node===ref)){
return false;
}
pn.appendChild(node);
}else{
return this.insertBefore(node,ref.nextSibling,_30a);
}
return true;
};
dojo.dom.insertAtPosition=function(node,ref,_30e){
if((!node)||(!ref)||(!_30e)){
return false;
}
switch(_30e.toLowerCase()){
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
dojo.dom.insertAtIndex=function(node,_310,_311){
var _312=_310.childNodes;
if(!_312.length||_312.length==_311){
_310.appendChild(node);
return true;
}
if(_311==0){
return dojo.dom.prependChild(node,_310);
}
return dojo.dom.insertAfter(node,_312[_311-1]);
};
dojo.dom.textContent=function(node,text){
if(arguments.length>1){
var _315=dojo.doc();
dojo.dom.replaceChildren(node,_315.createTextNode(text));
return text;
}else{
if(node.textContent!=undefined){
return node.textContent;
}
var _316="";
if(node==null){
return _316;
}
for(var i=0;i<node.childNodes.length;i++){
switch(node.childNodes[i].nodeType){
case 1:
case 5:
_316+=dojo.dom.textContent(node.childNodes[i]);
break;
case 3:
case 2:
case 4:
_316+=node.childNodes[i].nodeValue;
break;
default:
break;
}
}
return _316;
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
dojo.dom.setAttributeNS=function(elem,_31c,_31d,_31e){
if(elem==null||((elem==undefined)&&(typeof elem=="undefined"))){
dojo.raise("No element given to dojo.dom.setAttributeNS");
}
if(!((elem.setAttributeNS==undefined)&&(typeof elem.setAttributeNS=="undefined"))){
elem.setAttributeNS(_31c,_31d,_31e);
}else{
var _31f=elem.ownerDocument;
var _320=_31f.createNode(2,_31d,_31c);
_320.nodeValue=_31e;
elem.setAttributeNode(_320);
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
var _325=args["back"]||args["backButton"]||args["handle"];
var tcb=function(_327){
if(window.location.hash!=""){
setTimeout("window.location.href = '"+hash+"';",1);
}
_325.apply(this,[_327]);
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
var _328=args["forward"]||args["forwardButton"]||args["handle"];
var tfw=function(_32a){
if(window.location.hash!=""){
window.location.href=hash;
}
if(_328){
_328.apply(this,[_32a]);
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
},iframeLoaded:function(evt,_32d){
if(!dojo.render.html.opera){
var _32e=this._getUrlQuery(_32d.href);
if(_32e==null){
if(this.historyStack.length==1){
this.handleBackButton();
}
return;
}
if(this.moveForward){
this.moveForward=false;
return;
}
if(this.historyStack.length>=2&&_32e==this._getUrlQuery(this.historyStack[this.historyStack.length-2].url)){
this.handleBackButton();
}else{
if(this.forwardStack.length>0&&_32e==this._getUrlQuery(this.forwardStack[this.forwardStack.length-1].url)){
this.handleForwardButton();
}
}
}
},handleBackButton:function(){
var _32f=this.historyStack.pop();
if(!_32f){
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
this.forwardStack.push(_32f);
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
var _336=url.split("?");
if(_336.length<2){
return null;
}else{
return _336[1];
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
var _339=false;
var _33a=node.getElementsByTagName("input");
dojo.lang.forEach(_33a,function(_33b){
if(_339){
return;
}
if(_33b.getAttribute("type")=="file"){
_339=true;
}
});
return _339;
};
dojo.io.formHasFile=function(_33c){
return dojo.io.checkChildrenForFile(_33c);
};
dojo.io.updateNode=function(node,_33e){
node=dojo.byId(node);
var args=_33e;
if(dojo.lang.isString(_33e)){
args={url:_33e};
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
dojo.io.encodeForm=function(_345,_346,_347){
if((!_345)||(!_345.tagName)||(!_345.tagName.toLowerCase()=="form")){
dojo.raise("Attempted to encode a non-form element.");
}
if(!_347){
_347=dojo.io.formFilter;
}
var enc=/utf/i.test(_346||"")?encodeURIComponent:dojo.string.encodeAscii;
var _349=[];
for(var i=0;i<_345.elements.length;i++){
var elm=_345.elements[i];
if(!elm||elm.tagName.toLowerCase()=="fieldset"||!_347(elm)){
continue;
}
var name=enc(elm.name);
var type=elm.type.toLowerCase();
if(type=="select-multiple"){
for(var j=0;j<elm.options.length;j++){
if(elm.options[j].selected){
_349.push(name+"="+enc(elm.options[j].value));
}
}
}else{
if(dojo.lang.inArray(["radio","checkbox"],type)){
if(elm.checked){
_349.push(name+"="+enc(elm.value));
}
}else{
_349.push(name+"="+enc(elm.value));
}
}
}
var _34f=_345.getElementsByTagName("input");
for(var i=0;i<_34f.length;i++){
var _350=_34f[i];
if(_350.type.toLowerCase()=="image"&&_350.form==_345&&_347(_350)){
var name=enc(_350.name);
_349.push(name+"="+enc(_350.value));
_349.push(name+".x=0");
_349.push(name+".y=0");
}
}
return _349.join("&")+"&";
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
var _356=form.getElementsByTagName("input");
for(var i=0;i<_356.length;i++){
var _357=_356[i];
if(_357.type.toLowerCase()=="image"&&_357.form==form){
this.connect(_357,"onclick","click");
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
var _35e=false;
if(node.disabled||!node.name){
_35e=false;
}else{
if(dojo.lang.inArray(["submit","button","image"],type)){
if(!this.clickedButton){
this.clickedButton=node;
}
_35e=node==this.clickedButton;
}else{
_35e=!dojo.lang.inArray(["file","submit","reset","button"],type);
}
}
return _35e;
},connect:function(_35f,_360,_361){
if(dojo.evalObjPath("dojo.event.connect")){
dojo.event.connect(_35f,_360,this,_361);
}else{
var fcn=dojo.lang.hitch(this,_361);
_35f[_360]=function(e){
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
var _364=this;
var _365={};
this.useCache=false;
this.preventCache=false;
function getCacheKey(url,_367,_368){
return url+"|"+_367+"|"+_368.toLowerCase();
}
function addToCache(url,_36a,_36b,http){
_365[getCacheKey(url,_36a,_36b)]=http;
}
function getFromCache(url,_36e,_36f){
return _365[getCacheKey(url,_36e,_36f)];
}
this.clearCache=function(){
_365={};
};
function doLoad(_370,http,url,_373,_374){
if(((http.status>=200)&&(http.status<300))||(http.status==304)||(location.protocol=="file:"&&(http.status==0||http.status==undefined))||(location.protocol=="chrome:"&&(http.status==0||http.status==undefined))){
var ret;
if(_370.method.toLowerCase()=="head"){
var _376=http.getAllResponseHeaders();
ret={};
ret.toString=function(){
return _376;
};
var _377=_376.split(/[\r\n]+/g);
for(var i=0;i<_377.length;i++){
var pair=_377[i].match(/^([^:]+)\s*:\s*(.+)$/i);
if(pair){
ret[pair[1]]=pair[2];
}
}
}else{
if(_370.mimetype=="text/javascript"){
try{
ret=dj_eval(http.responseText);
}
catch(e){
dojo.debug(e);
dojo.debug(http.responseText);
ret=null;
}
}else{
if(_370.mimetype=="text/json"||_370.mimetype=="application/json"){
try{
ret=dj_eval("("+http.responseText+")");
}
catch(e){
dojo.debug(e);
dojo.debug(http.responseText);
ret=false;
}
}else{
if((_370.mimetype=="application/xml")||(_370.mimetype=="text/xml")){
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
if(_374){
addToCache(url,_373,_370.method,http);
}
_370[(typeof _370.load=="function")?"load":"handle"]("load",ret,http,_370);
}else{
var _37a=new dojo.io.Error("XMLHttpTransport Error: "+http.status+" "+http.statusText);
_370[(typeof _370.error=="function")?"error":"handle"]("error",_37a,http,_370);
}
}
function setHeaders(http,_37c){
if(_37c["headers"]){
for(var _37d in _37c["headers"]){
if(_37d.toLowerCase()=="content-type"&&!_37c["contentType"]){
_37c["contentType"]=_37c["headers"][_37d];
}else{
http.setRequestHeader(_37d,_37c["headers"][_37d]);
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
if(!dojo.hostenv._blockAsync&&!_364._blockAsync){
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
var _381=new dojo.io.Error("XMLHttpTransport.watchInFlight Error: "+e);
tif.req[(typeof tif.req.error=="function")?"error":"handle"]("error",_381,tif.http,tif.req);
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
var _382=dojo.hostenv.getXmlhttpObject()?true:false;
this.canHandle=function(_383){
return _382&&dojo.lang.inArray(["text/plain","text/html","application/xml","text/xml","text/javascript","text/json","application/json"],(_383["mimetype"].toLowerCase()||""))&&!(_383["formNode"]&&dojo.io.formHasFile(_383["formNode"]));
};
this.multipartBoundary="45309FFF-BD65-4d50-99C9-36986896A96F";
this.bind=function(_384){
if(!_384["url"]){
if(!_384["formNode"]&&(_384["backButton"]||_384["back"]||_384["changeUrl"]||_384["watchForURL"])&&(!djConfig.preventBackButtonFix)){
dojo.deprecated("Using dojo.io.XMLHTTPTransport.bind() to add to browser history without doing an IO request","Use dojo.undo.browser.addToHistory() instead.","0.4");
dojo.undo.browser.addToHistory(_384);
return true;
}
}
var url=_384.url;
var _386="";
if(_384["formNode"]){
var ta=_384.formNode.getAttribute("action");
if((ta)&&(!_384["url"])){
url=ta;
}
var tp=_384.formNode.getAttribute("method");
if((tp)&&(!_384["method"])){
_384.method=tp;
}
_386+=dojo.io.encodeForm(_384.formNode,_384.encoding,_384["formFilter"]);
}
if(url.indexOf("#")>-1){
dojo.debug("Warning: dojo.io.bind: stripping hash values from url:",url);
url=url.split("#")[0];
}
if(_384["file"]){
_384.method="post";
}
if(!_384["method"]){
_384.method="get";
}
if(_384.method.toLowerCase()=="get"){
_384.multipart=false;
}else{
if(_384["file"]){
_384.multipart=true;
}else{
if(!_384["multipart"]){
_384.multipart=false;
}
}
}
if(_384["backButton"]||_384["back"]||_384["changeUrl"]){
dojo.undo.browser.addToHistory(_384);
}
var _389=_384["content"]||{};
if(_384.sendTransport){
_389["dojo.transport"]="xmlhttp";
}
do{
if(_384.postContent){
_386=_384.postContent;
break;
}
if(_389){
_386+=dojo.io.argsFromMap(_389,_384.encoding);
}
if(_384.method.toLowerCase()=="get"||!_384.multipart){
break;
}
var t=[];
if(_386.length){
var q=_386.split("&");
for(var i=0;i<q.length;++i){
if(q[i].length){
var p=q[i].split("=");
t.push("--"+this.multipartBoundary,"Content-Disposition: form-data; name=\""+p[0]+"\"","",p[1]);
}
}
}
if(_384.file){
if(dojo.lang.isArray(_384.file)){
for(var i=0;i<_384.file.length;++i){
var o=_384.file[i];
t.push("--"+this.multipartBoundary,"Content-Disposition: form-data; name=\""+o.name+"\"; filename=\""+("fileName" in o?o.fileName:o.name)+"\"","Content-Type: "+("contentType" in o?o.contentType:"application/octet-stream"),"",o.content);
}
}else{
var o=_384.file;
t.push("--"+this.multipartBoundary,"Content-Disposition: form-data; name=\""+o.name+"\"; filename=\""+("fileName" in o?o.fileName:o.name)+"\"","Content-Type: "+("contentType" in o?o.contentType:"application/octet-stream"),"",o.content);
}
}
if(t.length){
t.push("--"+this.multipartBoundary+"--","");
_386=t.join("\r\n");
}
}while(false);
var _38f=_384["sync"]?false:true;
var _390=_384["preventCache"]||(this.preventCache==true&&_384["preventCache"]!=false);
var _391=_384["useCache"]==true||(this.useCache==true&&_384["useCache"]!=false);
if(!_390&&_391){
var _392=getFromCache(url,_386,_384.method);
if(_392){
doLoad(_384,_392,url,_386,false);
return;
}
}
var http=dojo.hostenv.getXmlhttpObject(_384);
var _394=false;
if(_38f){
var _395=this.inFlight.push({"req":_384,"http":http,"url":url,"query":_386,"useCache":_391,"startTime":_384.timeoutSeconds?(new Date()).getTime():0});
this.startWatchingInFlight();
}else{
_364._blockAsync=true;
}
if(_384.method.toLowerCase()=="post"){
if(!_384.user){
http.open("POST",url,_38f);
}else{
http.open("POST",url,_38f,_384.user,_384.password);
}
setHeaders(http,_384);
http.setRequestHeader("Content-Type",_384.multipart?("multipart/form-data; boundary="+this.multipartBoundary):(_384.contentType||"application/x-www-form-urlencoded"));
try{
http.send(_386);
}
catch(e){
if(typeof http.abort=="function"){
http.abort();
}
doLoad(_384,{status:404},url,_386,_391);
}
}else{
var _396=url;
if(_386!=""){
_396+=(_396.indexOf("?")>-1?"&":"?")+_386;
}
if(_390){
_396+=(dojo.string.endsWithAny(_396,"?","&")?"":(_396.indexOf("?")>-1?"&":"?"))+"dojo.preventCache="+new Date().valueOf();
}
if(!_384.user){
http.open(_384.method.toUpperCase(),_396,_38f);
}else{
http.open(_384.method.toUpperCase(),_396,_38f,_384.user,_384.password);
}
setHeaders(http,_384);
try{
http.send(null);
}
catch(e){
if(typeof http.abort=="function"){
http.abort();
}
doLoad(_384,{status:404},url,_386,_391);
}
}
if(!_38f){
doLoad(_384,http,url,_386,_391);
_364._blockAsync=false;
}
_384.abort=function(){
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
dojo.io.cookie.setCookie=function(name,_398,days,path,_39b,_39c){
var _39d=-1;
if((typeof days=="number")&&(days>=0)){
var d=new Date();
d.setTime(d.getTime()+(days*24*60*60*1000));
_39d=d.toGMTString();
}
_398=escape(_398);
document.cookie=name+"="+_398+";"+(_39d!=-1?" expires="+_39d+";":"")+(path?"path="+path:"")+(_39b?"; domain="+_39b:"")+(_39c?"; secure":"");
};
dojo.io.cookie.set=dojo.io.cookie.setCookie;
dojo.io.cookie.getCookie=function(name){
var idx=document.cookie.lastIndexOf(name+"=");
if(idx==-1){
return null;
}
var _3a1=document.cookie.substring(idx+name.length+1);
var end=_3a1.indexOf(";");
if(end==-1){
end=_3a1.length;
}
_3a1=_3a1.substring(0,end);
_3a1=unescape(_3a1);
return _3a1;
};
dojo.io.cookie.get=dojo.io.cookie.getCookie;
dojo.io.cookie.deleteCookie=function(name){
dojo.io.cookie.setCookie(name,"-",0);
};
dojo.io.cookie.setObjectCookie=function(name,obj,days,path,_3a8,_3a9,_3aa){
if(arguments.length==5){
_3aa=_3a8;
_3a8=null;
_3a9=null;
}
var _3ab=[],_3ac,_3ad="";
if(!_3aa){
_3ac=dojo.io.cookie.getObjectCookie(name);
}
if(days>=0){
if(!_3ac){
_3ac={};
}
for(var prop in obj){
if(obj[prop]==null){
delete _3ac[prop];
}else{
if((typeof obj[prop]=="string")||(typeof obj[prop]=="number")){
_3ac[prop]=obj[prop];
}
}
}
prop=null;
for(var prop in _3ac){
_3ab.push(escape(prop)+"="+escape(_3ac[prop]));
}
_3ad=_3ab.join("&");
}
dojo.io.cookie.setCookie(name,_3ad,days,path,_3a8,_3a9);
};
dojo.io.cookie.getObjectCookie=function(name){
var _3b0=null,_3b1=dojo.io.cookie.getCookie(name);
if(_3b1){
_3b0={};
var _3b2=_3b1.split("&");
for(var i=0;i<_3b2.length;i++){
var pair=_3b2[i].split("=");
var _3b5=pair[1];
if(isNaN(_3b5)){
_3b5=unescape(pair[1]);
}
_3b0[unescape(pair[0])]=_3b5;
}
}
return _3b0;
};
dojo.io.cookie.isSupported=function(){
if(typeof navigator.cookieEnabled!="boolean"){
dojo.io.cookie.setCookie("__TestingYourBrowserForCookieSupport__","CookiesAllowed",90,null);
var _3b6=dojo.io.cookie.getCookie("__TestingYourBrowserForCookieSupport__");
navigator.cookieEnabled=(_3b6=="CookiesAllowed");
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
dojo.provide("dojo.io");
dojo.deprecated("dojo.io","replaced by dojo.io.*","0.5");
dojo.provide("dojo.AdapterRegistry");
dojo.AdapterRegistry=function(_3b7){
this.pairs=[];
this.returnWrappers=_3b7||false;
};
dojo.lang.extend(dojo.AdapterRegistry,{register:function(name,_3b9,wrap,_3bb,_3bc){
var type=(_3bc)?"unshift":"push";
this.pairs[type]([name,_3b9,wrap,_3bb]);
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
dojo.json={jsonRegistry:new dojo.AdapterRegistry(),register:function(name,_3c4,wrap,_3c6){
dojo.json.jsonRegistry.register(name,_3c4,wrap,_3c6);
},evalJson:function(json){
try{
return eval("("+json+")");
}
catch(e){
dojo.debug(e);
return json;
}
},serialize:function(o){
var _3c9=typeof (o);
if(_3c9=="undefined"){
return "undefined";
}else{
if((_3c9=="number")||(_3c9=="boolean")){
return o+"";
}else{
if(o===null){
return "null";
}
}
}
if(_3c9=="string"){
return dojo.string.escapeString(o);
}
var me=arguments.callee;
var _3cb;
if(typeof (o.__json__)=="function"){
_3cb=o.__json__();
if(o!==_3cb){
return me(_3cb);
}
}
if(typeof (o.json)=="function"){
_3cb=o.json();
if(o!==_3cb){
return me(_3cb);
}
}
if(_3c9!="function"&&typeof (o.length)=="number"){
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
_3cb=dojo.json.jsonRegistry.match(o);
return me(_3cb);
}
catch(e){
}
if(_3c9=="function"){
return null;
}
res=[];
for(var k in o){
var _3d0;
if(typeof (k)=="number"){
_3d0="\""+k+"\"";
}else{
if(typeof (k)=="string"){
_3d0=dojo.string.escapeString(k);
}else{
continue;
}
}
val=me(o[k]);
if(typeof (val)!="string"){
continue;
}
res.push(_3d0+":"+val);
}
return "{"+res.join(",")+"}";
}};
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
var _3d3=dojo.global();
var _3d4=dojo.doc();
var w=0;
var h=0;
if(dojo.render.html.mozilla){
w=_3d4.documentElement.clientWidth;
h=_3d3.innerHeight;
}else{
if(!dojo.render.html.opera&&_3d3.innerWidth){
w=_3d3.innerWidth;
h=_3d3.innerHeight;
}else{
if(!dojo.render.html.opera&&dojo.exists(_3d4,"documentElement.clientWidth")){
var w2=_3d4.documentElement.clientWidth;
if(!w||w2&&w2<w){
w=w2;
}
h=_3d4.documentElement.clientHeight;
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
var _3d8=dojo.global();
var _3d9=dojo.doc();
var top=_3d8.pageYOffset||_3d9.documentElement.scrollTop||dojo.body().scrollTop||0;
var left=_3d8.pageXOffset||_3d9.documentElement.scrollLeft||dojo.body().scrollLeft||0;
return {top:top,left:left,offset:{x:left,y:top}};
};
dojo.html.getParentByType=function(node,type){
var _3de=dojo.doc();
var _3df=dojo.byId(node);
type=type.toLowerCase();
while((_3df)&&(_3df.nodeName.toLowerCase()!=type)){
if(_3df==(_3de["body"]||_3de["documentElement"])){
return null;
}
_3df=_3df.parentNode;
}
return _3df;
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
var _3e7={x:0,y:0};
if(e.pageX||e.pageY){
_3e7.x=e.pageX;
_3e7.y=e.pageY;
}else{
var de=dojo.doc().documentElement;
var db=dojo.body();
_3e7.x=e.clientX+((de||db)["scrollLeft"])-((de||db)["clientLeft"]);
_3e7.y=e.clientY+((de||db)["scrollTop"])-((de||db)["clientTop"]);
}
return _3e7;
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
var _3ec=dojo.doc().createElement("script");
_3ec.src="javascript:'dojo.html.createExternalElement=function(doc, tag){ return doc.createElement(tag); }'";
dojo.doc().getElementsByTagName("head")[0].appendChild(_3ec);
})();
}
}else{
dojo.html.createExternalElement=function(doc,tag){
return doc.createElement(tag);
};
}
dojo.html._callDeprecated=function(_3ef,_3f0,args,_3f2,_3f3){
dojo.deprecated("dojo.html."+_3ef,"replaced by dojo.html."+_3f0+"("+(_3f2?"node, {"+_3f2+": "+_3f2+"}":"")+")"+(_3f3?"."+_3f3:""),"0.5");
var _3f4=[];
if(_3f2){
var _3f5={};
_3f5[_3f2]=args[1];
_3f4.push(args[0]);
_3f4.push(_3f5);
}else{
_3f4=args;
}
var ret=dojo.html[_3f0].apply(dojo.html,args);
if(_3f3){
return ret[_3f3];
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
this.moduleUri=function(_3f8,uri){
var loc=dojo.hostenv.getModuleSymbols(_3f8).join("/");
if(!loc){
return null;
}
if(loc.lastIndexOf("/")!=loc.length-1){
loc+="/";
}
var _3fb=loc.indexOf(":");
var _3fc=loc.indexOf("/");
if(loc.charAt(0)!="/"&&(_3fb==-1||_3fb>_3fc)){
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
var _3ff=new dojo.uri.Uri(arguments[i].toString());
var _400=new dojo.uri.Uri(uri.toString());
if((_3ff.path=="")&&(_3ff.scheme==null)&&(_3ff.authority==null)&&(_3ff.query==null)){
if(_3ff.fragment!=null){
_400.fragment=_3ff.fragment;
}
_3ff=_400;
}else{
if(_3ff.scheme==null){
_3ff.scheme=_400.scheme;
if(_3ff.authority==null){
_3ff.authority=_400.authority;
if(_3ff.path.charAt(0)!="/"){
var path=_400.path.substring(0,_400.path.lastIndexOf("/")+1)+_3ff.path;
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
_3ff.path=segs.join("/");
}
}
}
}
uri="";
if(_3ff.scheme!=null){
uri+=_3ff.scheme+":";
}
if(_3ff.authority!=null){
uri+="//"+_3ff.authority;
}
uri+=_3ff.path;
if(_3ff.query!=null){
uri+="?"+_3ff.query;
}
if(_3ff.fragment!=null){
uri+="#"+_3ff.fragment;
}
}
this.uri=uri.toString();
var _404="^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\\?([^#]*))?(#(.*))?$";
var r=this.uri.match(new RegExp(_404));
this.scheme=r[2]||(r[1]?"":null);
this.authority=r[4]||(r[3]?"":null);
this.path=r[5];
this.query=r[7]||(r[6]?"":null);
this.fragment=r[9]||(r[8]?"":null);
if(this.authority!=null){
_404="^((([^:]+:)?([^@]+))@)?([^:]*)(:([0-9]+))?$";
r=this.authority.match(new RegExp(_404));
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
dojo.html.hasClass=function(node,_40b){
return (new RegExp("(^|\\s+)"+_40b+"(\\s+|$)")).test(dojo.html.getClass(node));
};
dojo.html.prependClass=function(node,_40d){
_40d+=" "+dojo.html.getClass(node);
return dojo.html.setClass(node,_40d);
};
dojo.html.addClass=function(node,_40f){
if(dojo.html.hasClass(node,_40f)){
return false;
}
_40f=(dojo.html.getClass(node)+" "+_40f).replace(/^\s+|\s+$/g,"");
return dojo.html.setClass(node,_40f);
};
dojo.html.setClass=function(node,_411){
node=dojo.byId(node);
var cs=new String(_411);
try{
if(typeof node.className=="string"){
node.className=cs;
}else{
if(node.setAttribute){
node.setAttribute("class",_411);
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
dojo.html.removeClass=function(node,_414,_415){
try{
if(!_415){
var _416=dojo.html.getClass(node).replace(new RegExp("(^|\\s+)"+_414+"(\\s+|$)"),"$1$2");
}else{
var _416=dojo.html.getClass(node).replace(_414,"");
}
dojo.html.setClass(node,_416);
}
catch(e){
dojo.debug("dojo.html.removeClass() failed",e);
}
return true;
};
dojo.html.replaceClass=function(node,_418,_419){
dojo.html.removeClass(node,_419);
dojo.html.addClass(node,_418);
};
dojo.html.classMatchType={ContainsAll:0,ContainsAny:1,IsOnly:2};
dojo.html.getElementsByClass=function(_41a,_41b,_41c,_41d,_41e){
_41e=false;
var _41f=dojo.doc();
_41b=dojo.byId(_41b)||_41f;
var _420=_41a.split(/\s+/g);
var _421=[];
if(_41d!=1&&_41d!=2){
_41d=0;
}
var _422=new RegExp("(\\s|^)(("+_420.join(")|(")+"))(\\s|$)");
var _423=_420.join(" ").length;
var _424=[];
if(!_41e&&_41f.evaluate){
var _425=".//"+(_41c||"*")+"[contains(";
if(_41d!=dojo.html.classMatchType.ContainsAny){
_425+="concat(' ',@class,' '), ' "+_420.join(" ') and contains(concat(' ',@class,' '), ' ")+" ')";
if(_41d==2){
_425+=" and string-length(@class)="+_423+"]";
}else{
_425+="]";
}
}else{
_425+="concat(' ',@class,' '), ' "+_420.join(" ') or contains(concat(' ',@class,' '), ' ")+" ')]";
}
var _426=_41f.evaluate(_425,_41b,null,XPathResult.ANY_TYPE,null);
var _427=_426.iterateNext();
while(_427){
try{
_424.push(_427);
_427=_426.iterateNext();
}
catch(e){
break;
}
}
return _424;
}else{
if(!_41c){
_41c="*";
}
_424=_41b.getElementsByTagName(_41c);
var node,i=0;
outer:
while(node=_424[i++]){
var _42a=dojo.html.getClasses(node);
if(_42a.length==0){
continue outer;
}
var _42b=0;
for(var j=0;j<_42a.length;j++){
if(_422.test(_42a[j])){
if(_41d==dojo.html.classMatchType.ContainsAny){
_421.push(node);
continue outer;
}else{
_42b++;
}
}else{
if(_41d==dojo.html.classMatchType.IsOnly){
continue outer;
}
}
}
if(_42b==_420.length){
if((_41d==dojo.html.classMatchType.IsOnly)&&(_42b==_42a.length)){
_421.push(node);
}else{
if(_41d==dojo.html.classMatchType.ContainsAll){
_421.push(node);
}
}
}
}
return _421;
}
};
dojo.html.getElementsByClassName=dojo.html.getElementsByClass;
dojo.html.toCamelCase=function(_42d){
var arr=_42d.split("-"),cc=arr[0];
for(var i=1;i<arr.length;i++){
cc+=arr[i].charAt(0).toUpperCase()+arr[i].substring(1);
}
return cc;
};
dojo.html.toSelectorCase=function(_431){
return _431.replace(/([A-Z])/g,"-$1").toLowerCase();
};
if(dojo.render.html.ie){
dojo.html.getComputedStyle=function(node,_433,_434){
node=dojo.byId(node);
if(!node||!node.style){
return _434;
}
return node.currentStyle[dojo.html.toCamelCase(_433)];
};
dojo.html.getComputedStyles=function(node){
return node.currentStyle;
};
}else{
dojo.html.getComputedStyle=function(node,_437,_438){
node=dojo.byId(node);
if(!node||!node.style){
return _438;
}
var s=document.defaultView.getComputedStyle(node,null);
return (s&&s[dojo.html.toCamelCase(_437)])||"";
};
dojo.html.getComputedStyles=function(node){
return document.defaultView.getComputedStyle(node,null);
};
}
dojo.html.getStyleProperty=function(node,_43c){
node=dojo.byId(node);
return (node&&node.style?node.style[dojo.html.toCamelCase(_43c)]:undefined);
};
dojo.html.getStyle=function(node,_43e){
var _43f=dojo.html.getStyleProperty(node,_43e);
return (_43f?_43f:dojo.html.getComputedStyle(node,_43e));
};
dojo.html.setStyle=function(node,_441,_442){
node=dojo.byId(node);
if(node&&node.style){
var _443=dojo.html.toCamelCase(_441);
node.style[_443]=_442;
}
};
dojo.html.setStyleText=function(_444,text){
try{
_444.style.cssText=text;
}
catch(e){
_444.setAttribute("style",text);
}
};
dojo.html.copyStyle=function(_446,_447){
if(!_447.style.cssText){
_446.setAttribute("style",_447.getAttribute("style"));
}else{
_446.style.cssText=_447.style.cssText;
}
dojo.html.addClass(_446,dojo.html.getClass(_447));
};
dojo.html.getUnitValue=function(node,_449,_44a){
var s=dojo.html.getComputedStyle(node,_449);
if((!s)||((s=="auto")&&(_44a))){
return {value:0,units:"px"};
}
var _44c=s.match(/(\-?[\d.]+)([a-z%]*)/i);
if(!_44c){
return dojo.html.getUnitValue.bad;
}
return {value:Number(_44c[1]),units:_44c[2].toLowerCase()};
};
dojo.html.getUnitValue.bad={value:NaN,units:""};
if(dojo.render.html.ie){
dojo.html.toPixelValue=function(_44d,_44e){
if(!_44e){
return 0;
}
if(_44e.slice(-2)=="px"){
return parseFloat(_44e);
}
var _44f=0;
with(_44d){
var _450=style.left;
var _451=runtimeStyle.left;
runtimeStyle.left=currentStyle.left;
try{
style.left=_44e||0;
_44f=style.pixelLeft;
style.left=_450;
runtimeStyle.left=_451;
}
catch(e){
}
}
return _44f;
};
}else{
dojo.html.toPixelValue=function(_452,_453){
return (_453&&(_453.slice(-2)=="px")?parseFloat(_453):0);
};
}
dojo.html.getPixelValue=function(node,_455,_456){
return dojo.html.toPixelValue(node,dojo.html.getComputedStyle(node,_455));
};
dojo.html.setPositivePixelValue=function(node,_458,_459){
if(isNaN(_459)){
return false;
}
node.style[_458]=Math.max(0,_459)+"px";
return true;
};
dojo.html.styleSheet=null;
dojo.html.insertCssRule=function(_45a,_45b,_45c){
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
_45c=dojo.html.styleSheet.cssRules.length;
}else{
if(dojo.html.styleSheet.rules){
_45c=dojo.html.styleSheet.rules.length;
}else{
return null;
}
}
}
if(dojo.html.styleSheet.insertRule){
var rule=_45a+" { "+_45b+" }";
return dojo.html.styleSheet.insertRule(rule,_45c);
}else{
if(dojo.html.styleSheet.addRule){
return dojo.html.styleSheet.addRule(_45a,_45b,_45c);
}else{
return null;
}
}
};
dojo.html.removeCssRule=function(_45e){
if(!dojo.html.styleSheet){
dojo.debug("no stylesheet defined for removing rules");
return false;
}
if(dojo.render.html.ie){
if(!_45e){
_45e=dojo.html.styleSheet.rules.length;
dojo.html.styleSheet.removeRule(_45e);
}
}else{
if(document.styleSheets[0]){
if(!_45e){
_45e=dojo.html.styleSheet.cssRules.length;
}
dojo.html.styleSheet.deleteRule(_45e);
}
}
return true;
};
dojo.html._insertedCssFiles=[];
dojo.html.insertCssFile=function(URI,doc,_461,_462){
if(!URI){
return;
}
if(!doc){
doc=document;
}
var _463=dojo.hostenv.getText(URI,false,_462);
if(_463===null){
return;
}
_463=dojo.html.fixPathsInCssText(_463,URI);
if(_461){
var idx=-1,node,ent=dojo.html._insertedCssFiles;
for(var i=0;i<ent.length;i++){
if((ent[i].doc==doc)&&(ent[i].cssText==_463)){
idx=i;
node=ent[i].nodeRef;
break;
}
}
if(node){
var _468=doc.getElementsByTagName("style");
for(var i=0;i<_468.length;i++){
if(_468[i]==node){
return;
}
}
dojo.html._insertedCssFiles.shift(idx,1);
}
}
var _469=dojo.html.insertCssText(_463,doc);
dojo.html._insertedCssFiles.push({"doc":doc,"cssText":_463,"nodeRef":_469});
if(_469&&djConfig.isDebug){
_469.setAttribute("dbgHref",URI);
}
return _469;
};
dojo.html.insertCssText=function(_46a,doc,URI){
if(!_46a){
return;
}
if(!doc){
doc=document;
}
if(URI){
_46a=dojo.html.fixPathsInCssText(_46a,URI);
}
var _46d=doc.createElement("style");
_46d.setAttribute("type","text/css");
var head=doc.getElementsByTagName("head")[0];
if(!head){
dojo.debug("No head tag in document, aborting styles");
return;
}else{
head.appendChild(_46d);
}
if(_46d.styleSheet){
var _46f=function(){
try{
_46d.styleSheet.cssText=_46a;
}
catch(e){
dojo.debug(e);
}
};
if(_46d.styleSheet.disabled){
setTimeout(_46f,10);
}else{
_46f();
}
}else{
var _470=doc.createTextNode(_46a);
_46d.appendChild(_470);
}
return _46d;
};
dojo.html.fixPathsInCssText=function(_471,URI){
if(!_471||!URI){
return;
}
var _473,str="",url="",_476="[\\t\\s\\w\\(\\)\\/\\.\\\\'\"-:#=&?~]+";
var _477=new RegExp("url\\(\\s*("+_476+")\\s*\\)");
var _478=/(file|https?|ftps?):\/\//;
regexTrim=new RegExp("^[\\s]*(['\"]?)("+_476+")\\1[\\s]*?$");
if(dojo.render.html.ie55||dojo.render.html.ie60){
var _479=new RegExp("AlphaImageLoader\\((.*)src=['\"]("+_476+")['\"]");
while(_473=_479.exec(_471)){
url=_473[2].replace(regexTrim,"$2");
if(!_478.exec(url)){
url=(new dojo.uri.Uri(URI,url).toString());
}
str+=_471.substring(0,_473.index)+"AlphaImageLoader("+_473[1]+"src='"+url+"'";
_471=_471.substr(_473.index+_473[0].length);
}
_471=str+_471;
str="";
}
while(_473=_477.exec(_471)){
url=_473[1].replace(regexTrim,"$2");
if(!_478.exec(url)){
url=(new dojo.uri.Uri(URI,url).toString());
}
str+=_471.substring(0,_473.index)+"url("+url+")";
_471=_471.substr(_473.index+_473[0].length);
}
return str+_471;
};
dojo.html.setActiveStyleSheet=function(_47a){
var i=0,a,els=dojo.doc().getElementsByTagName("link");
while(a=els[i++]){
if(a.getAttribute("rel").indexOf("style")!=-1&&a.getAttribute("title")){
a.disabled=true;
if(a.getAttribute("title")==_47a){
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
var _486={dj_ie:drh.ie,dj_ie55:drh.ie55,dj_ie6:drh.ie60,dj_ie7:drh.ie70,dj_iequirks:drh.ie&&drh.quirks,dj_opera:drh.opera,dj_opera8:drh.opera&&(Math.floor(dojo.render.version)==8),dj_opera9:drh.opera&&(Math.floor(dojo.render.version)==9),dj_khtml:drh.khtml,dj_safari:drh.safari,dj_gecko:drh.mozilla};
for(var p in _486){
if(_486[p]){
dojo.html.addClass(node,p);
}
}
};
dojo.kwCompoundRequire({common:["dojo.html.common","dojo.html.style"]});
dojo.provide("dojo.html.*");
dojo.provide("dojo.html.display");
dojo.html._toggle=function(node,_489,_48a){
node=dojo.byId(node);
_48a(node,!_489(node));
return _489(node);
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
dojo.html.setShowing=function(node,_48f){
dojo.html[(_48f?"show":"hide")](node);
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
dojo.html.setDisplay=function(node,_495){
dojo.html.setStyle(node,"display",((_495 instanceof String||typeof _495=="string")?_495:(_495?dojo.html.suggestDisplayByTagName(node):"none")));
};
dojo.html.isDisplayed=function(node){
return (dojo.html.getComputedStyle(node,"display")!="none");
};
dojo.html.toggleDisplay=function(node){
return dojo.html._toggle(node,dojo.html.isDisplayed,dojo.html.setDisplay);
};
dojo.html.setVisibility=function(node,_499){
dojo.html.setStyle(node,"visibility",((_499 instanceof String||typeof _499=="string")?_499:(_499?"visible":"hidden")));
};
dojo.html.isVisible=function(node){
return (dojo.html.getComputedStyle(node,"visibility")!="hidden");
};
dojo.html.toggleVisibility=function(node){
return dojo.html._toggle(node,dojo.html.isVisible,dojo.html.setVisibility);
};
dojo.html.setOpacity=function(node,_49d,_49e){
node=dojo.byId(node);
var h=dojo.render.html;
if(!_49e){
if(_49d>=1){
if(h.ie){
dojo.html.clearOpacity(node);
return;
}else{
_49d=0.999999;
}
}else{
if(_49d<0){
_49d=0;
}
}
}
if(h.ie){
if(node.nodeName.toLowerCase()=="tr"){
var tds=node.getElementsByTagName("td");
for(var x=0;x<tds.length;x++){
tds[x].style.filter="Alpha(Opacity="+_49d*100+")";
}
}
node.style.filter="Alpha(Opacity="+_49d*100+")";
}else{
if(h.moz){
node.style.opacity=_49d;
node.style.MozOpacity=_49d;
}else{
if(h.safari){
node.style.opacity=_49d;
node.style.KhtmlOpacity=_49d;
}else{
node.style.opacity=_49d;
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
var _4aa=0;
while(node){
if(dojo.html.getComputedStyle(node,"position")=="fixed"){
return 0;
}
var val=node[prop];
if(val){
_4aa+=val-0;
if(node==dojo.body()){
break;
}
}
node=node.parentNode;
}
return _4aa;
};
dojo.html.setStyleAttributes=function(node,_4ad){
node=dojo.byId(node);
var _4ae=_4ad.replace(/(;)?\s*$/,"").split(";");
for(var i=0;i<_4ae.length;i++){
var _4b0=_4ae[i].split(":");
var name=_4b0[0].replace(/\s*$/,"").replace(/^\s*/,"").toLowerCase();
var _4b2=_4b0[1].replace(/\s*$/,"").replace(/^\s*/,"");
switch(name){
case "opacity":
dojo.html.setOpacity(node,_4b2);
break;
case "content-height":
dojo.html.setContentBox(node,{height:_4b2});
break;
case "content-width":
dojo.html.setContentBox(node,{width:_4b2});
break;
case "outer-height":
dojo.html.setMarginBox(node,{height:_4b2});
break;
case "outer-width":
dojo.html.setMarginBox(node,{width:_4b2});
break;
default:
node.style[dojo.html.toCamelCase(name)]=_4b2;
}
}
};
dojo.html.boxSizing={MARGIN_BOX:"margin-box",BORDER_BOX:"border-box",PADDING_BOX:"padding-box",CONTENT_BOX:"content-box"};
dojo.html.getAbsolutePosition=dojo.html.abs=function(node,_4b4,_4b5){
node=dojo.byId(node,node.ownerDocument);
var ret={x:0,y:0};
var bs=dojo.html.boxSizing;
if(!_4b5){
_4b5=bs.CONTENT_BOX;
}
var _4b8=2;
var _4b9;
switch(_4b5){
case bs.MARGIN_BOX:
_4b9=3;
break;
case bs.BORDER_BOX:
_4b9=2;
break;
case bs.PADDING_BOX:
default:
_4b9=1;
break;
case bs.CONTENT_BOX:
_4b9=0;
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
_4b8=1;
try{
var bo=document.getBoxObjectFor(node);
ret.x=bo.x-dojo.html.sumAncestorProperties(node,"scrollLeft");
ret.y=bo.y-dojo.html.sumAncestorProperties(node,"scrollTop");
}
catch(e){
}
}else{
if(node["offsetParent"]){
var _4bd;
if((h.safari)&&(node.style.getPropertyValue("position")=="absolute")&&(node.parentNode==db)){
_4bd=db;
}else{
_4bd=db.parentNode;
}
if(node.parentNode!=db){
var nd=node;
if(dojo.render.html.opera){
nd=db;
}
ret.x-=dojo.html.sumAncestorProperties(nd,"scrollLeft");
ret.y-=dojo.html.sumAncestorProperties(nd,"scrollTop");
}
var _4bf=node;
do{
var n=_4bf["offsetLeft"];
if(!h.opera||n>0){
ret.x+=isNaN(n)?0:n;
}
var m=_4bf["offsetTop"];
ret.y+=isNaN(m)?0:m;
_4bf=_4bf.offsetParent;
}while((_4bf!=_4bd)&&(_4bf!=null));
}else{
if(node["x"]&&node["y"]){
ret.x+=isNaN(node.x)?0:node.x;
ret.y+=isNaN(node.y)?0:node.y;
}
}
}
}
if(_4b4){
var _4c2=dojo.html.getScroll();
ret.y+=_4c2.top;
ret.x+=_4c2.left;
}
var _4c3=[dojo.html.getPaddingExtent,dojo.html.getBorderExtent,dojo.html.getMarginExtent];
if(_4b8>_4b9){
for(var i=_4b9;i<_4b8;++i){
ret.y+=_4c3[i](node,"top");
ret.x+=_4c3[i](node,"left");
}
}else{
if(_4b8<_4b9){
for(var i=_4b9;i>_4b8;--i){
ret.y-=_4c3[i-1](node,"top");
ret.x-=_4c3[i-1](node,"left");
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
dojo.html._sumPixelValues=function(node,_4c7,_4c8){
var _4c9=0;
for(var x=0;x<_4c7.length;x++){
_4c9+=dojo.html.getPixelValue(node,_4c7[x],_4c8);
}
return _4c9;
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
var _4d6=dojo.html.getBorder(node);
return {width:pad.width+_4d6.width,height:pad.height+_4d6.height};
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
var _4db;
if(!h.ie){
_4db=dojo.html.getStyle(node,"-moz-box-sizing");
if(!_4db){
_4db=dojo.html.getStyle(node,"box-sizing");
}
}
return (_4db?_4db:bs.CONTENT_BOX);
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
var _4e0=dojo.html.getBorder(node);
return {width:box.width-_4e0.width,height:box.height-_4e0.height};
};
dojo.html.getContentBox=function(node){
node=dojo.byId(node);
var _4e2=dojo.html.getPadBorder(node);
return {width:node.offsetWidth-_4e2.width,height:node.offsetHeight-_4e2.height};
};
dojo.html.setContentBox=function(node,args){
node=dojo.byId(node);
var _4e5=0;
var _4e6=0;
var isbb=dojo.html.isBorderBox(node);
var _4e8=(isbb?dojo.html.getPadBorder(node):{width:0,height:0});
var ret={};
if(typeof args.width!="undefined"){
_4e5=args.width+_4e8.width;
ret.width=dojo.html.setPositivePixelValue(node,"width",_4e5);
}
if(typeof args.height!="undefined"){
_4e6=args.height+_4e8.height;
ret.height=dojo.html.setPositivePixelValue(node,"height",_4e6);
}
return ret;
};
dojo.html.getMarginBox=function(node){
var _4eb=dojo.html.getBorderBox(node);
var _4ec=dojo.html.getMargin(node);
return {width:_4eb.width+_4ec.width,height:_4eb.height+_4ec.height};
};
dojo.html.setMarginBox=function(node,args){
node=dojo.byId(node);
var _4ef=0;
var _4f0=0;
var isbb=dojo.html.isBorderBox(node);
var _4f2=(!isbb?dojo.html.getPadBorder(node):{width:0,height:0});
var _4f3=dojo.html.getMargin(node);
var ret={};
if(typeof args.width!="undefined"){
_4ef=args.width-_4f2.width;
_4ef-=_4f3.width;
ret.width=dojo.html.setPositivePixelValue(node,"width",_4ef);
}
if(typeof args.height!="undefined"){
_4f0=args.height-_4f2.height;
_4f0-=_4f3.height;
ret.height=dojo.html.setPositivePixelValue(node,"height",_4f0);
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
dojo.html.toCoordinateObject=dojo.html.toCoordinateArray=function(_4f8,_4f9,_4fa){
if(_4f8 instanceof Array||typeof _4f8=="array"){
dojo.deprecated("dojo.html.toCoordinateArray","use dojo.html.toCoordinateObject({left: , top: , width: , height: }) instead","0.5");
while(_4f8.length<4){
_4f8.push(0);
}
while(_4f8.length>4){
_4f8.pop();
}
var ret={left:_4f8[0],top:_4f8[1],width:_4f8[2],height:_4f8[3]};
}else{
if(!_4f8.nodeType&&!(_4f8 instanceof String||typeof _4f8=="string")&&("width" in _4f8||"height" in _4f8||"left" in _4f8||"x" in _4f8||"top" in _4f8||"y" in _4f8)){
var ret={left:_4f8.left||_4f8.x||0,top:_4f8.top||_4f8.y||0,width:_4f8.width||0,height:_4f8.height||0};
}else{
var node=dojo.byId(_4f8);
var pos=dojo.html.abs(node,_4f9,_4fa);
var _4fe=dojo.html.getMarginBox(node);
var ret={left:pos.left,top:pos.top,width:_4fe.width,height:_4fe.height};
}
}
ret.x=ret.left;
ret.y=ret.top;
return ret;
};
dojo.html.setMarginBoxWidth=dojo.html.setOuterWidth=function(node,_500){
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
dojo.html.getTotalOffset=function(node,type,_503){
return dojo.html._callDeprecated("getTotalOffset","getAbsolutePosition",arguments,null,type);
};
dojo.html.getAbsoluteX=function(node,_505){
return dojo.html._callDeprecated("getAbsoluteX","getAbsolutePosition",arguments,null,"x");
};
dojo.html.getAbsoluteY=function(node,_507){
return dojo.html._callDeprecated("getAbsoluteY","getAbsolutePosition",arguments,null,"y");
};
dojo.html.totalOffsetLeft=function(node,_509){
return dojo.html._callDeprecated("totalOffsetLeft","getAbsolutePosition",arguments,null,"left");
};
dojo.html.totalOffsetTop=function(node,_50b){
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
dojo.html.setContentBoxWidth=dojo.html.setContentWidth=function(node,_515){
return dojo.html._callDeprecated("setContentBoxWidth","setContentBox",arguments,"width");
};
dojo.html.setContentBoxHeight=dojo.html.setContentHeight=function(node,_517){
return dojo.html._callDeprecated("setContentBoxHeight","setContentBox",arguments,"height");
};
dojo.provide("dojo.html.util");
dojo.html.getElementWindow=function(_518){
return dojo.html.getDocumentWindow(_518.ownerDocument);
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
var _520=dojo.html.getCursorPosition(e);
with(dojo.html){
var _521=getAbsolutePosition(node,true);
var bb=getBorderBox(node);
var _523=_521.x+(bb.width/2);
var _524=_521.y+(bb.height/2);
}
with(dojo.html.gravity){
return ((_520.x<_523?WEST:EAST)|(_520.y<_524?NORTH:SOUTH));
}
};
dojo.html.gravity.NORTH=1;
dojo.html.gravity.SOUTH=1<<1;
dojo.html.gravity.EAST=1<<2;
dojo.html.gravity.WEST=1<<3;
dojo.html.overElement=function(_525,e){
_525=dojo.byId(_525);
var _527=dojo.html.getCursorPosition(e);
var bb=dojo.html.getBorderBox(_525);
var _529=dojo.html.getAbsolutePosition(_525,true,dojo.html.boxSizing.BORDER_BOX);
var top=_529.y;
var _52b=top+bb.height;
var left=_529.x;
var _52d=left+bb.width;
return (_527.x>=left&&_527.x<=_52d&&_527.y>=top&&_527.y<=_52b);
};
dojo.html.renderedTextContent=function(node){
node=dojo.byId(node);
var _52f="";
if(node==null){
return _52f;
}
for(var i=0;i<node.childNodes.length;i++){
switch(node.childNodes[i].nodeType){
case 1:
case 5:
var _531="unknown";
try{
_531=dojo.html.getStyle(node.childNodes[i],"display");
}
catch(E){
}
switch(_531){
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
_52f+="\n";
_52f+=dojo.html.renderedTextContent(node.childNodes[i]);
_52f+="\n";
break;
case "none":
break;
default:
if(node.childNodes[i].tagName&&node.childNodes[i].tagName.toLowerCase()=="br"){
_52f+="\n";
}else{
_52f+=dojo.html.renderedTextContent(node.childNodes[i]);
}
break;
}
break;
case 3:
case 2:
case 4:
var text=node.childNodes[i].nodeValue;
var _533="unknown";
try{
_533=dojo.html.getStyle(node,"text-transform");
}
catch(E){
}
switch(_533){
case "capitalize":
var _534=text.split(" ");
for(var i=0;i<_534.length;i++){
_534[i]=_534[i].charAt(0).toUpperCase()+_534[i].substring(1);
}
text=_534.join(" ");
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
switch(_533){
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
if(/\s$/.test(_52f)){
text.replace(/^\s/,"");
}
break;
}
_52f+=text;
break;
default:
break;
}
}
return _52f;
};
dojo.html.createNodesFromText=function(txt,trim){
if(trim){
txt=txt.replace(/^\s+|\s+$/g,"");
}
var tn=dojo.doc().createElement("div");
tn.style.visibility="hidden";
dojo.body().appendChild(tn);
var _538="none";
if((/^<t[dh][\s\r\n>]/i).test(txt.replace(/^\s+/))){
txt="<table><tbody><tr>"+txt+"</tr></tbody></table>";
_538="cell";
}else{
if((/^<tr[\s\r\n>]/i).test(txt.replace(/^\s+/))){
txt="<table><tbody>"+txt+"</tbody></table>";
_538="row";
}else{
if((/^<(thead|tbody|tfoot)[\s\r\n>]/i).test(txt.replace(/^\s+/))){
txt="<table>"+txt+"</table>";
_538="section";
}
}
}
tn.innerHTML=txt;
if(tn["normalize"]){
tn.normalize();
}
var _539=null;
switch(_538){
case "cell":
_539=tn.getElementsByTagName("tr")[0];
break;
case "row":
_539=tn.getElementsByTagName("tbody")[0];
break;
case "section":
_539=tn.getElementsByTagName("table")[0];
break;
default:
_539=tn;
break;
}
var _53a=[];
for(var x=0;x<_539.childNodes.length;x++){
_53a.push(_539.childNodes[x].cloneNode(true));
}
tn.style.display="none";
dojo.html.destroyNode(tn);
return _53a;
};
dojo.html.placeOnScreen=function(node,_53d,_53e,_53f,_540,_541,_542){
if(_53d instanceof Array||typeof _53d=="array"){
_542=_541;
_541=_540;
_540=_53f;
_53f=_53e;
_53e=_53d[1];
_53d=_53d[0];
}
if(_541 instanceof String||typeof _541=="string"){
_541=_541.split(",");
}
if(!isNaN(_53f)){
_53f=[Number(_53f),Number(_53f)];
}else{
if(!(_53f instanceof Array||typeof _53f=="array")){
_53f=[0,0];
}
}
var _543=dojo.html.getScroll().offset;
var view=dojo.html.getViewport();
node=dojo.byId(node);
var _545=node.style.display;
node.style.display="";
var bb=dojo.html.getBorderBox(node);
var w=bb.width;
var h=bb.height;
node.style.display=_545;
if(!(_541 instanceof Array||typeof _541=="array")){
_541=["TL"];
}
var _549,_54a,_54b=Infinity,_54c;
for(var _54d=0;_54d<_541.length;++_54d){
var _54e=_541[_54d];
var _54f=true;
var tryX=_53d-(_54e.charAt(1)=="L"?0:w)+_53f[0]*(_54e.charAt(1)=="L"?1:-1);
var tryY=_53e-(_54e.charAt(0)=="T"?0:h)+_53f[1]*(_54e.charAt(0)=="T"?1:-1);
if(_540){
tryX-=_543.x;
tryY-=_543.y;
}
if(tryX<0){
tryX=0;
_54f=false;
}
if(tryY<0){
tryY=0;
_54f=false;
}
var x=tryX+w;
if(x>view.width){
x=view.width-w;
_54f=false;
}else{
x=tryX;
}
x=Math.max(_53f[0],x)+_543.x;
var y=tryY+h;
if(y>view.height){
y=view.height-h;
_54f=false;
}else{
y=tryY;
}
y=Math.max(_53f[1],y)+_543.y;
if(_54f){
_549=x;
_54a=y;
_54b=0;
_54c=_54e;
break;
}else{
var dist=Math.pow(x-tryX-_543.x,2)+Math.pow(y-tryY-_543.y,2);
if(_54b>dist){
_54b=dist;
_549=x;
_54a=y;
_54c=_54e;
}
}
}
if(!_542){
node.style.left=_549+"px";
node.style.top=_54a+"px";
}
return {left:_549,top:_54a,x:_549,y:_54a,dist:_54b,corner:_54c};
};
dojo.html.placeOnScreenPoint=function(node,_556,_557,_558,_559){
dojo.deprecated("dojo.html.placeOnScreenPoint","use dojo.html.placeOnScreen() instead","0.5");
return dojo.html.placeOnScreen(node,_556,_557,_558,_559,["TL","TR","BL","BR"]);
};
dojo.html.placeOnScreenAroundElement=function(node,_55b,_55c,_55d,_55e,_55f){
var best,_561=Infinity;
_55b=dojo.byId(_55b);
var _562=_55b.style.display;
_55b.style.display="";
var mb=dojo.html.getElementBox(_55b,_55d);
var _564=mb.width;
var _565=mb.height;
var _566=dojo.html.getAbsolutePosition(_55b,true,_55d);
_55b.style.display=_562;
for(var _567 in _55e){
var pos,_569,_56a;
var _56b=_55e[_567];
_569=_566.x+(_567.charAt(1)=="L"?0:_564);
_56a=_566.y+(_567.charAt(0)=="T"?0:_565);
pos=dojo.html.placeOnScreen(node,_569,_56a,_55c,true,_56b,true);
if(pos.dist==0){
best=pos;
break;
}else{
if(_561>pos.dist){
_561=pos.dist;
best=pos;
}
}
}
if(!_55f){
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
var _56d=node.parentNode;
var _56e=_56d.scrollTop+dojo.html.getBorderBox(_56d).height;
var _56f=node.offsetTop+dojo.html.getMarginBox(node).height;
if(_56e<_56f){
_56d.scrollTop+=(_56f-_56e);
}else{
if(_56d.scrollTop>node.offsetTop){
_56d.scrollTop-=(_56d.scrollTop-node.offsetTop);
}
}
}
}
};

