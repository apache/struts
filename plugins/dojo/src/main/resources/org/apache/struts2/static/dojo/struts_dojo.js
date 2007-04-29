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
dojo.hostenv.localesGenerated=["ROOT","es-es","es","it-it","pt-br","de","fr-fr","zh-cn","pt","en-us","zh","fr","zh-tw","it","en-gb","xx","de-de","ko-kr","ja-jp","ko","en","ja"];
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
dojo.lang.extend(dojo.io.Request,{url:"",mimetype:"text/plain",method:"GET",content:undefined,transport:undefined,changeUrl:undefined,formNode:undefined,sync:false,bindSuccess:false,useCache:false,preventCache:false,load:function(type,data,_145,_146){
},error:function(type,_148,_149,_14a){
},timeout:function(type,_14c,_14d,_14e){
},handle:function(type,data,_151,_152){
},timeoutSeconds:0,abort:function(){
},fromKwArgs:function(_153){
if(_153["url"]){
_153.url=_153.url.toString();
}
if(_153["formNode"]){
_153.formNode=dojo.byId(_153.formNode);
}
if(!_153["method"]&&_153["formNode"]&&_153["formNode"].method){
_153.method=_153["formNode"].method;
}
if(!_153["handle"]&&_153["handler"]){
_153.handle=_153.handler;
}
if(!_153["load"]&&_153["loaded"]){
_153.load=_153.loaded;
}
if(!_153["changeUrl"]&&_153["changeURL"]){
_153.changeUrl=_153.changeURL;
}
_153.encoding=dojo.lang.firstValued(_153["encoding"],djConfig["bindEncoding"],"");
_153.sendTransport=dojo.lang.firstValued(_153["sendTransport"],djConfig["ioSendTransport"],false);
var _154=dojo.lang.isFunction;
for(var x=0;x<dojo.io.hdlrFuncNames.length;x++){
var fn=dojo.io.hdlrFuncNames[x];
if(_153[fn]&&_154(_153[fn])){
continue;
}
if(_153["handle"]&&_154(_153["handle"])){
_153[fn]=_153.handle;
}
}
dojo.lang.mixin(this,_153);
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
dojo.io.bind=function(_15b){
if(!(_15b instanceof dojo.io.Request)){
try{
_15b=new dojo.io.Request(_15b);
}
catch(e){
dojo.debug(e);
}
}
var _15c="";
if(_15b["transport"]){
_15c=_15b["transport"];
if(!this[_15c]){
dojo.io.sendBindError(_15b,"No dojo.io.bind() transport with name '"+_15b["transport"]+"'.");
return _15b;
}
if(!this[_15c].canHandle(_15b)){
dojo.io.sendBindError(_15b,"dojo.io.bind() transport with name '"+_15b["transport"]+"' cannot handle this type of request.");
return _15b;
}
}else{
for(var x=0;x<dojo.io.transports.length;x++){
var tmp=dojo.io.transports[x];
if((this[tmp])&&(this[tmp].canHandle(_15b))){
_15c=tmp;
break;
}
}
if(_15c==""){
dojo.io.sendBindError(_15b,"None of the loaded transports for dojo.io.bind()"+" can handle the request.");
return _15b;
}
}
this[_15c].bind(_15b);
_15b.bindSuccess=true;
return _15b;
};
dojo.io.sendBindError=function(_15f,_160){
if((typeof _15f.error=="function"||typeof _15f.handle=="function")&&(typeof setTimeout=="function"||typeof setTimeout=="object")){
var _161=new dojo.io.Error(_160);
setTimeout(function(){
_15f[(typeof _15f.error=="function")?"error":"handle"]("error",_161,null,_15f);
},50);
}else{
dojo.raise(_160);
}
};
dojo.io.queueBind=function(_162){
if(!(_162 instanceof dojo.io.Request)){
try{
_162=new dojo.io.Request(_162);
}
catch(e){
dojo.debug(e);
}
}
var _163=_162.load;
_162.load=function(){
dojo.io._queueBindInFlight=false;
var ret=_163.apply(this,arguments);
dojo.io._dispatchNextQueueBind();
return ret;
};
var _165=_162.error;
_162.error=function(){
dojo.io._queueBindInFlight=false;
var ret=_165.apply(this,arguments);
dojo.io._dispatchNextQueueBind();
return ret;
};
dojo.io._bindQueue.push(_162);
dojo.io._dispatchNextQueueBind();
return _162;
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
dojo.io.argsFromMap=function(map,_168,last){
var enc=/utf/i.test(_168||"")?encodeURIComponent:dojo.string.encodeAscii;
var _16b=[];
var _16c=new Object();
for(var name in map){
var _16e=function(elt){
var val=enc(name)+"="+enc(elt);
_16b[(last==name)?"push":"unshift"](val);
};
if(!_16c[name]){
var _171=map[name];
if(dojo.lang.isArray(_171)){
dojo.lang.forEach(_171,_16e);
}else{
_16e(_171);
}
}
}
return _16b.join("&");
};
dojo.io.setIFrameSrc=function(_172,src,_174){
try{
var r=dojo.render.html;
if(!_174){
if(r.safari){
_172.location=src;
}else{
frames[_172.name].location=src;
}
}else{
var idoc;
if(r.ie){
idoc=_172.contentWindow.document;
}else{
if(r.safari){
idoc=_172.document;
}else{
idoc=_172.contentWindow;
}
}
if(!idoc){
_172.location=src;
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
var _17b=0;
for(var x in obj){
if(obj[x]&&(!tmp[x])){
_17b++;
break;
}
}
return _17b==0;
}else{
if(dojo.lang.isArrayLike(obj)||dojo.lang.isString(obj)){
return obj.length==0;
}
}
},map:function(arr,obj,_17f){
var _180=dojo.lang.isString(arr);
if(_180){
arr=arr.split("");
}
if(dojo.lang.isFunction(obj)&&(!_17f)){
_17f=obj;
obj=dj_global;
}else{
if(dojo.lang.isFunction(obj)&&_17f){
var _181=obj;
obj=_17f;
_17f=_181;
}
}
if(Array.map){
var _182=Array.map(arr,_17f,obj);
}else{
var _182=[];
for(var i=0;i<arr.length;++i){
_182.push(_17f.call(obj,arr[i]));
}
}
if(_180){
return _182.join("");
}else{
return _182;
}
},reduce:function(arr,_185,obj,_187){
var _188=_185;
if(arguments.length==2){
_187=_185;
_188=arr[0];
arr=arr.slice(1);
}else{
if(arguments.length==3){
if(dojo.lang.isFunction(obj)){
_187=obj;
obj=null;
}
}else{
if(dojo.lang.isFunction(obj)){
var tmp=_187;
_187=obj;
obj=tmp;
}
}
}
var ob=obj||dj_global;
dojo.lang.map(arr,function(val){
_188=_187.call(ob,_188,val);
});
return _188;
},forEach:function(_18c,_18d,_18e){
if(dojo.lang.isString(_18c)){
_18c=_18c.split("");
}
if(Array.forEach){
Array.forEach(_18c,_18d,_18e);
}else{
if(!_18e){
_18e=dj_global;
}
for(var i=0,l=_18c.length;i<l;i++){
_18d.call(_18e,_18c[i],i,_18c);
}
}
},_everyOrSome:function(_191,arr,_193,_194){
if(dojo.lang.isString(arr)){
arr=arr.split("");
}
if(Array.every){
return Array[_191?"every":"some"](arr,_193,_194);
}else{
if(!_194){
_194=dj_global;
}
for(var i=0,l=arr.length;i<l;i++){
var _197=_193.call(_194,arr[i],i,arr);
if(_191&&!_197){
return false;
}else{
if((!_191)&&(_197)){
return true;
}
}
}
return Boolean(_191);
}
},every:function(arr,_199,_19a){
return this._everyOrSome(true,arr,_199,_19a);
},some:function(arr,_19c,_19d){
return this._everyOrSome(false,arr,_19c,_19d);
},filter:function(arr,_19f,_1a0){
var _1a1=dojo.lang.isString(arr);
if(_1a1){
arr=arr.split("");
}
var _1a2;
if(Array.filter){
_1a2=Array.filter(arr,_19f,_1a0);
}else{
if(!_1a0){
if(arguments.length>=3){
dojo.raise("thisObject doesn't exist!");
}
_1a0=dj_global;
}
_1a2=[];
for(var i=0;i<arr.length;i++){
if(_19f.call(_1a0,arr[i],i,arr)){
_1a2.push(arr[i]);
}
}
}
if(_1a1){
return _1a2.join("");
}else{
return _1a2;
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
},toArray:function(_1a7,_1a8){
var _1a9=[];
for(var i=_1a8||0;i<_1a7.length;i++){
_1a9.push(_1a7[i]);
}
return _1a9;
}});
dojo.provide("dojo.lang.func");
dojo.lang.hitch=function(_1ab,_1ac){
var fcn=(dojo.lang.isString(_1ac)?_1ab[_1ac]:_1ac)||function(){
};
return function(){
return fcn.apply(_1ab,arguments);
};
};
dojo.lang.anonCtr=0;
dojo.lang.anon={};
dojo.lang.nameAnonFunc=function(_1ae,_1af,_1b0){
var nso=(_1af||dojo.lang.anon);
if((_1b0)||((dj_global["djConfig"])&&(djConfig["slowAnonFuncLookups"]==true))){
for(var x in nso){
try{
if(nso[x]===_1ae){
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
nso[ret]=_1ae;
return ret;
};
dojo.lang.forward=function(_1b4){
return function(){
return this[_1b4].apply(this,arguments);
};
};
dojo.lang.curry=function(_1b5,func){
var _1b7=[];
_1b5=_1b5||dj_global;
if(dojo.lang.isString(func)){
func=_1b5[func];
}
for(var x=2;x<arguments.length;x++){
_1b7.push(arguments[x]);
}
var _1b9=(func["__preJoinArity"]||func.length)-_1b7.length;
function gather(_1ba,_1bb,_1bc){
var _1bd=_1bc;
var _1be=_1bb.slice(0);
for(var x=0;x<_1ba.length;x++){
_1be.push(_1ba[x]);
}
_1bc=_1bc-_1ba.length;
if(_1bc<=0){
var res=func.apply(_1b5,_1be);
_1bc=_1bd;
return res;
}else{
return function(){
return gather(arguments,_1be,_1bc);
};
}
}
return gather([],_1b7,_1b9);
};
dojo.lang.curryArguments=function(_1c1,func,args,_1c4){
var _1c5=[];
var x=_1c4||0;
for(x=_1c4;x<args.length;x++){
_1c5.push(args[x]);
}
return dojo.lang.curry.apply(dojo.lang,[_1c1,func].concat(_1c5));
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
dojo.lang.delayThese=function(farr,cb,_1cb,_1cc){
if(!farr.length){
if(typeof _1cc=="function"){
_1cc();
}
return;
}
if((typeof _1cb=="undefined")&&(typeof cb=="number")){
_1cb=cb;
cb=function(){
};
}else{
if(!cb){
cb=function(){
};
if(!_1cb){
_1cb=0;
}
}
}
setTimeout(function(){
(farr.shift())();
cb();
dojo.lang.delayThese(farr,cb,_1cb,_1cc);
},_1cb);
};
dojo.provide("dojo.string.extras");
dojo.string.substituteParams=function(_1cd,hash){
var map=(typeof hash=="object")?hash:dojo.lang.toArray(arguments,1);
return _1cd.replace(/\%\{(\w+)\}/g,function(_1d0,key){
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
var _1d3=str.split(" ");
for(var i=0;i<_1d3.length;i++){
_1d3[i]=_1d3[i].charAt(0).toUpperCase()+_1d3[i].substring(1);
}
return _1d3.join(" ");
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
var _1d8=escape(str);
var _1d9,re=/%u([0-9A-F]{4})/i;
while((_1d9=_1d8.match(re))){
var num=Number("0x"+_1d9[1]);
var _1dc=escape("&#"+num+";");
ret+=_1d8.substring(0,_1d9.index)+_1dc;
_1d8=_1d8.substring(_1d9.index+_1d9[0].length);
}
ret+=_1d8.replace(/\+/g,"%2B");
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
dojo.string.escapeXml=function(str,_1e1){
str=str.replace(/&/gm,"&amp;").replace(/</gm,"&lt;").replace(/>/gm,"&gt;").replace(/"/gm,"&quot;");
if(!_1e1){
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
dojo.string.endsWith=function(str,end,_1ea){
if(_1ea){
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
dojo.string.startsWith=function(str,_1ee,_1ef){
if(_1ef){
str=str.toLowerCase();
_1ee=_1ee.toLowerCase();
}
return str.indexOf(_1ee)==0;
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
dojo.string.normalizeNewlines=function(text,_1f5){
if(_1f5=="\n"){
text=text.replace(/\r\n/g,"\n");
text=text.replace(/\r/g,"\n");
}else{
if(_1f5=="\r"){
text=text.replace(/\r\n/g,"\r");
text=text.replace(/\n/g,"\r");
}else{
text=text.replace(/([^\r])\n/g,"$1\r\n").replace(/\r([^\n])/g,"\r\n$1");
}
}
return text;
};
dojo.string.splitEscaped=function(str,_1f7){
var _1f8=[];
for(var i=0,_1fa=0;i<str.length;i++){
if(str.charAt(i)=="\\"){
i++;
continue;
}
if(str.charAt(i)==_1f7){
_1f8.push(str.substring(_1fa,i));
_1fa=i+1;
}
}
_1f8.push(str.substr(_1fa));
return _1f8;
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
var _1fc=dojo.doc();
do{
var id="dj_unique_"+(++arguments.callee._idIncrement);
}while(_1fc.getElementById(id));
return id;
};
dojo.dom.getUniqueId._idIncrement=0;
dojo.dom.firstElement=dojo.dom.getFirstChildElement=function(_1fe,_1ff){
var node=_1fe.firstChild;
while(node&&node.nodeType!=dojo.dom.ELEMENT_NODE){
node=node.nextSibling;
}
if(_1ff&&node&&node.tagName&&node.tagName.toLowerCase()!=_1ff.toLowerCase()){
node=dojo.dom.nextElement(node,_1ff);
}
return node;
};
dojo.dom.lastElement=dojo.dom.getLastChildElement=function(_201,_202){
var node=_201.lastChild;
while(node&&node.nodeType!=dojo.dom.ELEMENT_NODE){
node=node.previousSibling;
}
if(_202&&node&&node.tagName&&node.tagName.toLowerCase()!=_202.toLowerCase()){
node=dojo.dom.prevElement(node,_202);
}
return node;
};
dojo.dom.nextElement=dojo.dom.getNextSiblingElement=function(node,_205){
if(!node){
return null;
}
do{
node=node.nextSibling;
}while(node&&node.nodeType!=dojo.dom.ELEMENT_NODE);
if(node&&_205&&_205.toLowerCase()!=node.tagName.toLowerCase()){
return dojo.dom.nextElement(node,_205);
}
return node;
};
dojo.dom.prevElement=dojo.dom.getPreviousSiblingElement=function(node,_207){
if(!node){
return null;
}
if(_207){
_207=_207.toLowerCase();
}
do{
node=node.previousSibling;
}while(node&&node.nodeType!=dojo.dom.ELEMENT_NODE);
if(node&&_207&&_207.toLowerCase()!=node.tagName.toLowerCase()){
return dojo.dom.prevElement(node,_207);
}
return node;
};
dojo.dom.moveChildren=function(_208,_209,trim){
var _20b=0;
if(trim){
while(_208.hasChildNodes()&&_208.firstChild.nodeType==dojo.dom.TEXT_NODE){
_208.removeChild(_208.firstChild);
}
while(_208.hasChildNodes()&&_208.lastChild.nodeType==dojo.dom.TEXT_NODE){
_208.removeChild(_208.lastChild);
}
}
while(_208.hasChildNodes()){
_209.appendChild(_208.firstChild);
_20b++;
}
return _20b;
};
dojo.dom.copyChildren=function(_20c,_20d,trim){
var _20f=_20c.cloneNode(true);
return this.moveChildren(_20f,_20d,trim);
};
dojo.dom.replaceChildren=function(node,_211){
var _212=[];
if(dojo.render.html.ie){
for(var i=0;i<node.childNodes.length;i++){
_212.push(node.childNodes[i]);
}
}
dojo.dom.removeChildren(node);
node.appendChild(_211);
for(var i=0;i<_212.length;i++){
dojo.dom.destroyNode(_212[i]);
}
};
dojo.dom.removeChildren=function(node){
var _215=node.childNodes.length;
while(node.hasChildNodes()){
dojo.dom.removeNode(node.firstChild);
}
return _215;
};
dojo.dom.replaceNode=function(node,_217){
return node.parentNode.replaceChild(_217,node);
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
dojo.dom.getAncestors=function(node,_21b,_21c){
var _21d=[];
var _21e=(_21b&&(_21b instanceof Function||typeof _21b=="function"));
while(node){
if(!_21e||_21b(node)){
_21d.push(node);
}
if(_21c&&_21d.length>0){
return _21d[0];
}
node=node.parentNode;
}
if(_21c){
return null;
}
return _21d;
};
dojo.dom.getAncestorsByTag=function(node,tag,_221){
tag=tag.toLowerCase();
return dojo.dom.getAncestors(node,function(el){
return ((el.tagName)&&(el.tagName.toLowerCase()==tag));
},_221);
};
dojo.dom.getFirstAncestorByTag=function(node,tag){
return dojo.dom.getAncestorsByTag(node,tag,true);
};
dojo.dom.isDescendantOf=function(node,_226,_227){
if(_227&&node){
node=node.parentNode;
}
while(node){
if(node==_226){
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
var _22a=dojo.doc();
if(!dj_undef("ActiveXObject")){
var _22b=["MSXML2","Microsoft","MSXML","MSXML3"];
for(var i=0;i<_22b.length;i++){
try{
doc=new ActiveXObject(_22b[i]+".XMLDOM");
}
catch(e){
}
if(doc){
break;
}
}
}else{
if((_22a.implementation)&&(_22a.implementation.createDocument)){
doc=_22a.implementation.createDocument("","",null);
}
}
return doc;
};
dojo.dom.createDocumentFromText=function(str,_22e){
if(!_22e){
_22e="text/xml";
}
if(!dj_undef("DOMParser")){
var _22f=new DOMParser();
return _22f.parseFromString(str,_22e);
}else{
if(!dj_undef("ActiveXObject")){
var _230=dojo.dom.createDocument();
if(_230){
_230.async=false;
_230.loadXML(str);
return _230;
}else{
dojo.debug("toXml didn't work?");
}
}else{
var _231=dojo.doc();
if(_231.createElement){
var tmp=_231.createElement("xml");
tmp.innerHTML=str;
if(_231.implementation&&_231.implementation.createDocument){
var _233=_231.implementation.createDocument("foo","",null);
for(var i=0;i<tmp.childNodes.length;i++){
_233.importNode(tmp.childNodes.item(i),true);
}
return _233;
}
return ((tmp.document)&&(tmp.document.firstChild?tmp.document.firstChild:tmp));
}
}
}
return null;
};
dojo.dom.prependChild=function(node,_236){
if(_236.firstChild){
_236.insertBefore(node,_236.firstChild);
}else{
_236.appendChild(node);
}
return true;
};
dojo.dom.insertBefore=function(node,ref,_239){
if((_239!=true)&&(node===ref||node.nextSibling===ref)){
return false;
}
var _23a=ref.parentNode;
_23a.insertBefore(node,ref);
return true;
};
dojo.dom.insertAfter=function(node,ref,_23d){
var pn=ref.parentNode;
if(ref==pn.lastChild){
if((_23d!=true)&&(node===ref)){
return false;
}
pn.appendChild(node);
}else{
return this.insertBefore(node,ref.nextSibling,_23d);
}
return true;
};
dojo.dom.insertAtPosition=function(node,ref,_241){
if((!node)||(!ref)||(!_241)){
return false;
}
switch(_241.toLowerCase()){
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
dojo.dom.insertAtIndex=function(node,_243,_244){
var _245=_243.childNodes;
if(!_245.length||_245.length==_244){
_243.appendChild(node);
return true;
}
if(_244==0){
return dojo.dom.prependChild(node,_243);
}
return dojo.dom.insertAfter(node,_245[_244-1]);
};
dojo.dom.textContent=function(node,text){
if(arguments.length>1){
var _248=dojo.doc();
dojo.dom.replaceChildren(node,_248.createTextNode(text));
return text;
}else{
if(node.textContent!=undefined){
return node.textContent;
}
var _249="";
if(node==null){
return _249;
}
for(var i=0;i<node.childNodes.length;i++){
switch(node.childNodes[i].nodeType){
case 1:
case 5:
_249+=dojo.dom.textContent(node.childNodes[i]);
break;
case 3:
case 2:
case 4:
_249+=node.childNodes[i].nodeValue;
break;
default:
break;
}
}
return _249;
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
dojo.dom.setAttributeNS=function(elem,_24f,_250,_251){
if(elem==null||((elem==undefined)&&(typeof elem=="undefined"))){
dojo.raise("No element given to dojo.dom.setAttributeNS");
}
if(!((elem.setAttributeNS==undefined)&&(typeof elem.setAttributeNS=="undefined"))){
elem.setAttributeNS(_24f,_250,_251);
}else{
var _252=elem.ownerDocument;
var _253=_252.createNode(2,_250,_24f);
_253.nodeValue=_251;
elem.setAttributeNode(_253);
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
var _258=args["back"]||args["backButton"]||args["handle"];
var tcb=function(_25a){
if(window.location.hash!=""){
setTimeout("window.location.href = '"+hash+"';",1);
}
_258.apply(this,[_25a]);
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
var _25b=args["forward"]||args["forwardButton"]||args["handle"];
var tfw=function(_25d){
if(window.location.hash!=""){
window.location.href=hash;
}
if(_25b){
_25b.apply(this,[_25d]);
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
},iframeLoaded:function(evt,_260){
if(!dojo.render.html.opera){
var _261=this._getUrlQuery(_260.href);
if(_261==null){
if(this.historyStack.length==1){
this.handleBackButton();
}
return;
}
if(this.moveForward){
this.moveForward=false;
return;
}
if(this.historyStack.length>=2&&_261==this._getUrlQuery(this.historyStack[this.historyStack.length-2].url)){
this.handleBackButton();
}else{
if(this.forwardStack.length>0&&_261==this._getUrlQuery(this.forwardStack[this.forwardStack.length-1].url)){
this.handleForwardButton();
}
}
}
},handleBackButton:function(){
var _262=this.historyStack.pop();
if(!_262){
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
this.forwardStack.push(_262);
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
var _269=url.split("?");
if(_269.length<2){
return null;
}else{
return _269[1];
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
var _26c=false;
var _26d=node.getElementsByTagName("input");
dojo.lang.forEach(_26d,function(_26e){
if(_26c){
return;
}
if(_26e.getAttribute("type")=="file"){
_26c=true;
}
});
return _26c;
};
dojo.io.formHasFile=function(_26f){
return dojo.io.checkChildrenForFile(_26f);
};
dojo.io.updateNode=function(node,_271){
node=dojo.byId(node);
var args=_271;
if(dojo.lang.isString(_271)){
args={url:_271};
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
dojo.io.encodeForm=function(_278,_279,_27a){
if((!_278)||(!_278.tagName)||(!_278.tagName.toLowerCase()=="form")){
dojo.raise("Attempted to encode a non-form element.");
}
if(!_27a){
_27a=dojo.io.formFilter;
}
var enc=/utf/i.test(_279||"")?encodeURIComponent:dojo.string.encodeAscii;
var _27c=[];
for(var i=0;i<_278.elements.length;i++){
var elm=_278.elements[i];
if(!elm||elm.tagName.toLowerCase()=="fieldset"||!_27a(elm)){
continue;
}
var name=enc(elm.name);
var type=elm.type.toLowerCase();
if(type=="select-multiple"){
for(var j=0;j<elm.options.length;j++){
if(elm.options[j].selected){
_27c.push(name+"="+enc(elm.options[j].value));
}
}
}else{
if(dojo.lang.inArray(["radio","checkbox"],type)){
if(elm.checked){
_27c.push(name+"="+enc(elm.value));
}
}else{
_27c.push(name+"="+enc(elm.value));
}
}
}
var _282=_278.getElementsByTagName("input");
for(var i=0;i<_282.length;i++){
var _283=_282[i];
if(_283.type.toLowerCase()=="image"&&_283.form==_278&&_27a(_283)){
var name=enc(_283.name);
_27c.push(name+"="+enc(_283.value));
_27c.push(name+".x=0");
_27c.push(name+".y=0");
}
}
return _27c.join("&")+"&";
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
var _289=form.getElementsByTagName("input");
for(var i=0;i<_289.length;i++){
var _28a=_289[i];
if(_28a.type.toLowerCase()=="image"&&_28a.form==form){
this.connect(_28a,"onclick","click");
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
var _291=false;
if(node.disabled||!node.name){
_291=false;
}else{
if(dojo.lang.inArray(["submit","button","image"],type)){
if(!this.clickedButton){
this.clickedButton=node;
}
_291=node==this.clickedButton;
}else{
_291=!dojo.lang.inArray(["file","submit","reset","button"],type);
}
}
return _291;
},connect:function(_292,_293,_294){
if(dojo.evalObjPath("dojo.event.connect")){
dojo.event.connect(_292,_293,this,_294);
}else{
var fcn=dojo.lang.hitch(this,_294);
_292[_293]=function(e){
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
var _297=this;
var _298={};
this.useCache=false;
this.preventCache=false;
function getCacheKey(url,_29a,_29b){
return url+"|"+_29a+"|"+_29b.toLowerCase();
}
function addToCache(url,_29d,_29e,http){
_298[getCacheKey(url,_29d,_29e)]=http;
}
function getFromCache(url,_2a1,_2a2){
return _298[getCacheKey(url,_2a1,_2a2)];
}
this.clearCache=function(){
_298={};
};
function doLoad(_2a3,http,url,_2a6,_2a7){
if(((http.status>=200)&&(http.status<300))||(http.status==304)||(location.protocol=="file:"&&(http.status==0||http.status==undefined))||(location.protocol=="chrome:"&&(http.status==0||http.status==undefined))){
var ret;
if(_2a3.method.toLowerCase()=="head"){
var _2a9=http.getAllResponseHeaders();
ret={};
ret.toString=function(){
return _2a9;
};
var _2aa=_2a9.split(/[\r\n]+/g);
for(var i=0;i<_2aa.length;i++){
var pair=_2aa[i].match(/^([^:]+)\s*:\s*(.+)$/i);
if(pair){
ret[pair[1]]=pair[2];
}
}
}else{
if(_2a3.mimetype=="text/javascript"){
try{
ret=dj_eval(http.responseText);
}
catch(e){
dojo.debug(e);
dojo.debug(http.responseText);
ret=null;
}
}else{
if(_2a3.mimetype=="text/json"||_2a3.mimetype=="application/json"){
try{
ret=dj_eval("("+http.responseText+")");
}
catch(e){
dojo.debug(e);
dojo.debug(http.responseText);
ret=false;
}
}else{
if((_2a3.mimetype=="application/xml")||(_2a3.mimetype=="text/xml")){
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
if(_2a7){
addToCache(url,_2a6,_2a3.method,http);
}
_2a3[(typeof _2a3.load=="function")?"load":"handle"]("load",ret,http,_2a3);
}else{
var _2ad=new dojo.io.Error("XMLHttpTransport Error: "+http.status+" "+http.statusText);
_2a3[(typeof _2a3.error=="function")?"error":"handle"]("error",_2ad,http,_2a3);
}
}
function setHeaders(http,_2af){
if(_2af["headers"]){
for(var _2b0 in _2af["headers"]){
if(_2b0.toLowerCase()=="content-type"&&!_2af["contentType"]){
_2af["contentType"]=_2af["headers"][_2b0];
}else{
http.setRequestHeader(_2b0,_2af["headers"][_2b0]);
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
if(!dojo.hostenv._blockAsync&&!_297._blockAsync){
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
var _2b4=new dojo.io.Error("XMLHttpTransport.watchInFlight Error: "+e);
tif.req[(typeof tif.req.error=="function")?"error":"handle"]("error",_2b4,tif.http,tif.req);
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
var _2b5=dojo.hostenv.getXmlhttpObject()?true:false;
this.canHandle=function(_2b6){
return _2b5&&dojo.lang.inArray(["text/plain","text/html","application/xml","text/xml","text/javascript","text/json","application/json"],(_2b6["mimetype"].toLowerCase()||""))&&!(_2b6["formNode"]&&dojo.io.formHasFile(_2b6["formNode"]));
};
this.multipartBoundary="45309FFF-BD65-4d50-99C9-36986896A96F";
this.bind=function(_2b7){
if(!_2b7["url"]){
if(!_2b7["formNode"]&&(_2b7["backButton"]||_2b7["back"]||_2b7["changeUrl"]||_2b7["watchForURL"])&&(!djConfig.preventBackButtonFix)){
dojo.deprecated("Using dojo.io.XMLHTTPTransport.bind() to add to browser history without doing an IO request","Use dojo.undo.browser.addToHistory() instead.","0.4");
dojo.undo.browser.addToHistory(_2b7);
return true;
}
}
var url=_2b7.url;
var _2b9="";
if(_2b7["formNode"]){
var ta=_2b7.formNode.getAttribute("action");
if((ta)&&(!_2b7["url"])){
url=ta;
}
var tp=_2b7.formNode.getAttribute("method");
if((tp)&&(!_2b7["method"])){
_2b7.method=tp;
}
_2b9+=dojo.io.encodeForm(_2b7.formNode,_2b7.encoding,_2b7["formFilter"]);
}
if(url.indexOf("#")>-1){
dojo.debug("Warning: dojo.io.bind: stripping hash values from url:",url);
url=url.split("#")[0];
}
if(_2b7["file"]){
_2b7.method="post";
}
if(!_2b7["method"]){
_2b7.method="get";
}
if(_2b7.method.toLowerCase()=="get"){
_2b7.multipart=false;
}else{
if(_2b7["file"]){
_2b7.multipart=true;
}else{
if(!_2b7["multipart"]){
_2b7.multipart=false;
}
}
}
if(_2b7["backButton"]||_2b7["back"]||_2b7["changeUrl"]){
dojo.undo.browser.addToHistory(_2b7);
}
var _2bc=_2b7["content"]||{};
if(_2b7.sendTransport){
_2bc["dojo.transport"]="xmlhttp";
}
do{
if(_2b7.postContent){
_2b9=_2b7.postContent;
break;
}
if(_2bc){
_2b9+=dojo.io.argsFromMap(_2bc,_2b7.encoding);
}
if(_2b7.method.toLowerCase()=="get"||!_2b7.multipart){
break;
}
var t=[];
if(_2b9.length){
var q=_2b9.split("&");
for(var i=0;i<q.length;++i){
if(q[i].length){
var p=q[i].split("=");
t.push("--"+this.multipartBoundary,"Content-Disposition: form-data; name=\""+p[0]+"\"","",p[1]);
}
}
}
if(_2b7.file){
if(dojo.lang.isArray(_2b7.file)){
for(var i=0;i<_2b7.file.length;++i){
var o=_2b7.file[i];
t.push("--"+this.multipartBoundary,"Content-Disposition: form-data; name=\""+o.name+"\"; filename=\""+("fileName" in o?o.fileName:o.name)+"\"","Content-Type: "+("contentType" in o?o.contentType:"application/octet-stream"),"",o.content);
}
}else{
var o=_2b7.file;
t.push("--"+this.multipartBoundary,"Content-Disposition: form-data; name=\""+o.name+"\"; filename=\""+("fileName" in o?o.fileName:o.name)+"\"","Content-Type: "+("contentType" in o?o.contentType:"application/octet-stream"),"",o.content);
}
}
if(t.length){
t.push("--"+this.multipartBoundary+"--","");
_2b9=t.join("\r\n");
}
}while(false);
var _2c2=_2b7["sync"]?false:true;
var _2c3=_2b7["preventCache"]||(this.preventCache==true&&_2b7["preventCache"]!=false);
var _2c4=_2b7["useCache"]==true||(this.useCache==true&&_2b7["useCache"]!=false);
if(!_2c3&&_2c4){
var _2c5=getFromCache(url,_2b9,_2b7.method);
if(_2c5){
doLoad(_2b7,_2c5,url,_2b9,false);
return;
}
}
var http=dojo.hostenv.getXmlhttpObject(_2b7);
var _2c7=false;
if(_2c2){
var _2c8=this.inFlight.push({"req":_2b7,"http":http,"url":url,"query":_2b9,"useCache":_2c4,"startTime":_2b7.timeoutSeconds?(new Date()).getTime():0});
this.startWatchingInFlight();
}else{
_297._blockAsync=true;
}
if(_2b7.method.toLowerCase()=="post"){
if(!_2b7.user){
http.open("POST",url,_2c2);
}else{
http.open("POST",url,_2c2,_2b7.user,_2b7.password);
}
setHeaders(http,_2b7);
http.setRequestHeader("Content-Type",_2b7.multipart?("multipart/form-data; boundary="+this.multipartBoundary):(_2b7.contentType||"application/x-www-form-urlencoded"));
try{
http.send(_2b9);
}
catch(e){
if(typeof http.abort=="function"){
http.abort();
}
doLoad(_2b7,{status:404},url,_2b9,_2c4);
}
}else{
var _2c9=url;
if(_2b9!=""){
_2c9+=(_2c9.indexOf("?")>-1?"&":"?")+_2b9;
}
if(_2c3){
_2c9+=(dojo.string.endsWithAny(_2c9,"?","&")?"":(_2c9.indexOf("?")>-1?"&":"?"))+"dojo.preventCache="+new Date().valueOf();
}
if(!_2b7.user){
http.open(_2b7.method.toUpperCase(),_2c9,_2c2);
}else{
http.open(_2b7.method.toUpperCase(),_2c9,_2c2,_2b7.user,_2b7.password);
}
setHeaders(http,_2b7);
try{
http.send(null);
}
catch(e){
if(typeof http.abort=="function"){
http.abort();
}
doLoad(_2b7,{status:404},url,_2b9,_2c4);
}
}
if(!_2c2){
doLoad(_2b7,http,url,_2b9,_2c4);
_297._blockAsync=false;
}
_2b7.abort=function(){
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
dojo.io.cookie.setCookie=function(name,_2cb,days,path,_2ce,_2cf){
var _2d0=-1;
if((typeof days=="number")&&(days>=0)){
var d=new Date();
d.setTime(d.getTime()+(days*24*60*60*1000));
_2d0=d.toGMTString();
}
_2cb=escape(_2cb);
document.cookie=name+"="+_2cb+";"+(_2d0!=-1?" expires="+_2d0+";":"")+(path?"path="+path:"")+(_2ce?"; domain="+_2ce:"")+(_2cf?"; secure":"");
};
dojo.io.cookie.set=dojo.io.cookie.setCookie;
dojo.io.cookie.getCookie=function(name){
var idx=document.cookie.lastIndexOf(name+"=");
if(idx==-1){
return null;
}
var _2d4=document.cookie.substring(idx+name.length+1);
var end=_2d4.indexOf(";");
if(end==-1){
end=_2d4.length;
}
_2d4=_2d4.substring(0,end);
_2d4=unescape(_2d4);
return _2d4;
};
dojo.io.cookie.get=dojo.io.cookie.getCookie;
dojo.io.cookie.deleteCookie=function(name){
dojo.io.cookie.setCookie(name,"-",0);
};
dojo.io.cookie.setObjectCookie=function(name,obj,days,path,_2db,_2dc,_2dd){
if(arguments.length==5){
_2dd=_2db;
_2db=null;
_2dc=null;
}
var _2de=[],_2df,_2e0="";
if(!_2dd){
_2df=dojo.io.cookie.getObjectCookie(name);
}
if(days>=0){
if(!_2df){
_2df={};
}
for(var prop in obj){
if(obj[prop]==null){
delete _2df[prop];
}else{
if((typeof obj[prop]=="string")||(typeof obj[prop]=="number")){
_2df[prop]=obj[prop];
}
}
}
prop=null;
for(var prop in _2df){
_2de.push(escape(prop)+"="+escape(_2df[prop]));
}
_2e0=_2de.join("&");
}
dojo.io.cookie.setCookie(name,_2e0,days,path,_2db,_2dc);
};
dojo.io.cookie.getObjectCookie=function(name){
var _2e3=null,_2e4=dojo.io.cookie.getCookie(name);
if(_2e4){
_2e3={};
var _2e5=_2e4.split("&");
for(var i=0;i<_2e5.length;i++){
var pair=_2e5[i].split("=");
var _2e8=pair[1];
if(isNaN(_2e8)){
_2e8=unescape(pair[1]);
}
_2e3[unescape(pair[0])]=_2e8;
}
}
return _2e3;
};
dojo.io.cookie.isSupported=function(){
if(typeof navigator.cookieEnabled!="boolean"){
dojo.io.cookie.setCookie("__TestingYourBrowserForCookieSupport__","CookiesAllowed",90,null);
var _2e9=dojo.io.cookie.getCookie("__TestingYourBrowserForCookieSupport__");
navigator.cookieEnabled=(_2e9=="CookiesAllowed");
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
function interpolateArgs(args,_2eb){
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
var _2ee=dl.nameAnonFunc(args[2],ao.adviceObj,_2eb);
ao.adviceFunc=_2ee;
}else{
if((dl.isFunction(args[0]))&&(dl.isObject(args[1]))&&(dl.isString(args[2]))){
ao.adviceType="after";
ao.srcObj=dj_global;
var _2ee=dl.nameAnonFunc(args[0],ao.srcObj,_2eb);
ao.srcFunc=_2ee;
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
var _2ee=dl.nameAnonFunc(args[1],dj_global,_2eb);
ao.srcFunc=_2ee;
ao.adviceObj=args[2];
ao.adviceFunc=args[3];
}else{
if((dl.isString(args[0]))&&(dl.isObject(args[1]))&&(dl.isString(args[2]))&&(dl.isFunction(args[3]))){
ao.srcObj=args[1];
ao.srcFunc=args[2];
var _2ee=dl.nameAnonFunc(args[3],dj_global,_2eb);
ao.adviceObj=dj_global;
ao.adviceFunc=_2ee;
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
var _2ee=dl.nameAnonFunc(ao.aroundFunc,ao.aroundObj,_2eb);
ao.aroundFunc=_2ee;
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
var _2f0={};
for(var x in ao){
_2f0[x]=ao[x];
}
var mjps=[];
dojo.lang.forEach(ao.srcObj,function(src){
if((dojo.render.html.capable)&&(dojo.lang.isString(src))){
src=dojo.byId(src);
}
_2f0.srcObj=src;
mjps.push(dojo.event.connect.call(dojo.event,_2f0));
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
var _2f8;
if((arguments.length==1)&&(typeof a1=="object")){
_2f8=a1;
}else{
_2f8={srcObj:a1,srcFunc:a2};
}
_2f8.adviceFunc=function(){
var _2f9=[];
for(var x=0;x<arguments.length;x++){
_2f9.push(arguments[x]);
}
dojo.debug("("+_2f8.srcObj+")."+_2f8.srcFunc,":",_2f9.join(", "));
};
this.kwConnect(_2f8);
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
this._kwConnectImpl=function(_301,_302){
var fn=(_302)?"disconnect":"connect";
if(typeof _301["srcFunc"]=="function"){
_301.srcObj=_301["srcObj"]||dj_global;
var _304=dojo.lang.nameAnonFunc(_301.srcFunc,_301.srcObj,true);
_301.srcFunc=_304;
}
if(typeof _301["adviceFunc"]=="function"){
_301.adviceObj=_301["adviceObj"]||dj_global;
var _304=dojo.lang.nameAnonFunc(_301.adviceFunc,_301.adviceObj,true);
_301.adviceFunc=_304;
}
_301.srcObj=_301["srcObj"]||dj_global;
_301.adviceObj=_301["adviceObj"]||_301["targetObj"]||dj_global;
_301.adviceFunc=_301["adviceFunc"]||_301["targetFunc"];
return dojo.event[fn](_301);
};
this.kwConnect=function(_305){
return this._kwConnectImpl(_305,false);
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
this.kwDisconnect=function(_308){
return this._kwConnectImpl(_308,true);
};
};
dojo.event.MethodInvocation=function(_309,obj,args){
this.jp_=_309;
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
dojo.event.MethodJoinPoint=function(obj,_311){
this.object=obj||dj_global;
this.methodname=_311;
this.methodfunc=this.object[_311];
this.squelch=false;
};
dojo.event.MethodJoinPoint.getForMethod=function(obj,_313){
if(!obj){
obj=dj_global;
}
var ofn=obj[_313];
if(!ofn){
ofn=obj[_313]=function(){
};
if(!obj[_313]){
dojo.raise("Cannot set do-nothing method on that object "+_313);
}
}else{
if((typeof ofn!="function")&&(!dojo.lang.isFunction(ofn))&&(!dojo.lang.isAlien(ofn))){
return null;
}
}
var _315=_313+"$joinpoint";
var _316=_313+"$joinpoint$method";
var _317=obj[_315];
if(!_317){
var _318=false;
if(dojo.event["browser"]){
if((obj["attachEvent"])||(obj["nodeType"])||(obj["addEventListener"])){
_318=true;
dojo.event.browser.addClobberNodeAttrs(obj,[_315,_316,_313]);
}
}
var _319=ofn.length;
obj[_316]=ofn;
_317=obj[_315]=new dojo.event.MethodJoinPoint(obj,_316);
if(!_318){
obj[_313]=function(){
return _317.run.apply(_317,arguments);
};
}else{
obj[_313]=function(){
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
return _317.run.apply(_317,args);
};
}
obj[_313].__preJoinArity=_319;
}
return _317;
};
dojo.lang.extend(dojo.event.MethodJoinPoint,{squelch:false,unintercept:function(){
this.object[this.methodname]=this.methodfunc;
this.before=[];
this.after=[];
this.around=[];
},disconnect:dojo.lang.forward("unintercept"),run:function(){
var obj=this.object||dj_global;
var args=arguments;
var _31f=[];
for(var x=0;x<args.length;x++){
_31f[x]=args[x];
}
var _321=function(marr){
if(!marr){
dojo.debug("Null argument to unrollAdvice()");
return;
}
var _323=marr[0]||dj_global;
var _324=marr[1];
if(!_323[_324]){
dojo.raise("function \""+_324+"\" does not exist on \""+_323+"\"");
}
var _325=marr[2]||dj_global;
var _326=marr[3];
var msg=marr[6];
var _328=marr[7];
if(_328>-1){
if(_328==0){
return;
}
marr[7]--;
}
var _329;
var to={args:[],jp_:this,object:obj,proceed:function(){
return _323[_324].apply(_323,to.args);
}};
to.args=_31f;
var _32b=parseInt(marr[4]);
var _32c=((!isNaN(_32b))&&(marr[4]!==null)&&(typeof marr[4]!="undefined"));
if(marr[5]){
var rate=parseInt(marr[5]);
var cur=new Date();
var _32f=false;
if((marr["last"])&&((cur-marr.last)<=rate)){
if(dojo.event._canTimeout){
if(marr["delayTimer"]){
clearTimeout(marr.delayTimer);
}
var tod=parseInt(rate*2);
var mcpy=dojo.lang.shallowCopy(marr);
marr.delayTimer=setTimeout(function(){
mcpy[5]=0;
_321(mcpy);
},tod);
}
return;
}else{
marr.last=cur;
}
}
if(_326){
_325[_326].call(_325,to);
}else{
if((_32c)&&((dojo.render.html)||(dojo.render.svg))){
dj_global["setTimeout"](function(){
if(msg){
_323[_324].call(_323,to);
}else{
_323[_324].apply(_323,args);
}
},_32b);
}else{
if(msg){
_323[_324].call(_323,to);
}else{
_323[_324].apply(_323,args);
}
}
}
};
var _332=function(){
if(this.squelch){
try{
return _321.apply(this,arguments);
}
catch(e){
dojo.debug(e);
}
}else{
return _321.apply(this,arguments);
}
};
if((this["before"])&&(this.before.length>0)){
dojo.lang.forEach(this.before.concat(new Array()),_332);
}
var _333;
try{
if((this["around"])&&(this.around.length>0)){
var mi=new dojo.event.MethodInvocation(this,obj,args);
_333=mi.proceed();
}else{
if(this.methodfunc){
_333=this.object[this.methodname].apply(this.object,args);
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
dojo.lang.forEach(this.after.concat(new Array()),_332);
}
return (this.methodfunc)?_333:null;
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
},addAdvice:function(_338,_339,_33a,_33b,_33c,_33d,once,_33f,rate,_341,_342){
var arr=this.getArr(_33c);
if(!arr){
dojo.raise("bad this: "+this);
}
var ao=[_338,_339,_33a,_33b,_33f,rate,_341,_342];
if(once){
if(this.hasAdvice(_338,_339,_33c,arr)>=0){
return;
}
}
if(_33d=="first"){
arr.unshift(ao);
}else{
arr.push(ao);
}
},hasAdvice:function(_345,_346,_347,arr){
if(!arr){
arr=this.getArr(_347);
}
var ind=-1;
for(var x=0;x<arr.length;x++){
var aao=(typeof _346=="object")?(new String(_346)).toString():_346;
var a1o=(typeof arr[x][1]=="object")?(new String(arr[x][1])).toString():arr[x][1];
if((arr[x][0]==_345)&&(a1o==aao)){
ind=x;
}
}
return ind;
},removeAdvice:function(_34d,_34e,_34f,once){
var arr=this.getArr(_34f);
var ind=this.hasAdvice(_34d,_34e,_34f,arr);
if(ind==-1){
return false;
}
while(ind!=-1){
arr.splice(ind,1);
if(once){
break;
}
ind=this.hasAdvice(_34d,_34e,_34f,arr);
}
return true;
}});
dojo.provide("dojo.event.topic");
dojo.event.topic=new function(){
this.topics={};
this.getTopic=function(_353){
if(!this.topics[_353]){
this.topics[_353]=new this.TopicImpl(_353);
}
return this.topics[_353];
};
this.registerPublisher=function(_354,obj,_356){
var _354=this.getTopic(_354);
_354.registerPublisher(obj,_356);
};
this.subscribe=function(_357,obj,_359){
var _357=this.getTopic(_357);
_357.subscribe(obj,_359);
};
this.unsubscribe=function(_35a,obj,_35c){
var _35a=this.getTopic(_35a);
_35a.unsubscribe(obj,_35c);
};
this.destroy=function(_35d){
this.getTopic(_35d).destroy();
delete this.topics[_35d];
};
this.publishApply=function(_35e,args){
var _35e=this.getTopic(_35e);
_35e.sendMessage.apply(_35e,args);
};
this.publish=function(_360,_361){
var _360=this.getTopic(_360);
var args=[];
for(var x=1;x<arguments.length;x++){
args.push(arguments[x]);
}
_360.sendMessage.apply(_360,args);
};
};
dojo.event.topic.TopicImpl=function(_364){
this.topicName=_364;
this.subscribe=function(_365,_366){
var tf=_366||_365;
var to=(!_366)?dj_global:_365;
return dojo.event.kwConnect({srcObj:this,srcFunc:"sendMessage",adviceObj:to,adviceFunc:tf});
};
this.unsubscribe=function(_369,_36a){
var tf=(!_36a)?_369:_36a;
var to=(!_36a)?null:_369;
return dojo.event.kwDisconnect({srcObj:this,srcFunc:"sendMessage",adviceObj:to,adviceFunc:tf});
};
this._getJoinPoint=function(){
return dojo.event.MethodJoinPoint.getForMethod(this,"sendMessage");
};
this.setSquelch=function(_36d){
this._getJoinPoint().squelch=_36d;
};
this.destroy=function(){
this._getJoinPoint().disconnect();
};
this.registerPublisher=function(_36e,_36f){
dojo.event.connect(_36e,_36f,this,"sendMessage");
};
this.sendMessage=function(_370){
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
this.clobber=function(_373){
var na;
var tna;
if(_373){
tna=_373.all||_373.getElementsByTagName("*");
na=[_373];
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
var _377={};
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
var _37c=0;
this.normalizedEventName=function(_37d){
switch(_37d){
case "CheckboxStateChange":
case "DOMAttrModified":
case "DOMMenuItemActive":
case "DOMMenuItemInactive":
case "DOMMouseScroll":
case "DOMNodeInserted":
case "DOMNodeRemoved":
case "RadioStateChange":
return _37d;
break;
default:
var lcn=_37d.toLowerCase();
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
this.addClobberNodeAttrs=function(node,_382){
if(!dojo.render.html.ie){
return;
}
this.addClobberNode(node);
for(var x=0;x<_382.length;x++){
node.__clobberAttrs__.push(_382[x]);
}
};
this.removeListener=function(node,_385,fp,_387){
if(!_387){
var _387=false;
}
_385=dojo.event.browser.normalizedEventName(_385);
if(_385=="key"){
if(dojo.render.html.ie){
this.removeListener(node,"onkeydown",fp,_387);
}
_385="keypress";
}
if(node.removeEventListener){
node.removeEventListener(_385,fp,_387);
}
};
this.addListener=function(node,_389,fp,_38b,_38c){
if(!node){
return;
}
if(!_38b){
var _38b=false;
}
_389=dojo.event.browser.normalizedEventName(_389);
if(_389=="key"){
if(dojo.render.html.ie){
this.addListener(node,"onkeydown",fp,_38b,_38c);
}
_389="keypress";
}
if(!_38c){
var _38d=function(evt){
if(!evt){
evt=window.event;
}
var ret=fp(dojo.event.browser.fixEvent(evt,this));
if(_38b){
dojo.event.browser.stopEvent(evt);
}
return ret;
};
}else{
_38d=fp;
}
if(node.addEventListener){
node.addEventListener(_389,_38d,_38b);
return _38d;
}else{
_389="on"+_389;
if(typeof node[_389]=="function"){
var _390=node[_389];
node[_389]=function(e){
_390(e);
return _38d(e);
};
}else{
node[_389]=_38d;
}
if(dojo.render.html.ie){
this.addClobberNodeAttrs(node,[_389]);
}
return _38d;
}
};
this.isEvent=function(obj){
return (typeof obj!="undefined")&&(obj)&&(typeof Event!="undefined")&&(obj.eventPhase);
};
this.currentEvent=null;
this.callListener=function(_393,_394){
if(typeof _393!="function"){
dojo.raise("listener not a function: "+_393);
}
dojo.event.browser.currentEvent.currentTarget=_394;
return _393.call(_394,dojo.event.browser.currentEvent);
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
this.fixEvent=function(evt,_397){
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
var _399=evt.keyCode;
if(_399>=65&&_399<=90&&evt.shiftKey==false){
_399+=32;
}
if(_399>=1&&_399<=26&&evt.ctrlKey){
_399+=96;
}
evt.key=String.fromCharCode(_399);
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
var _399=evt.which;
if((evt.ctrlKey||evt.altKey||evt.metaKey)&&(evt.which>=65&&evt.which<=90&&evt.shiftKey==false)){
_399+=32;
}
evt.key=String.fromCharCode(_399);
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
evt.currentTarget=(_397?_397:evt.srcElement);
}
if(!evt.layerX){
evt.layerX=evt.offsetX;
}
if(!evt.layerY){
evt.layerY=evt.offsetY;
}
var doc=(evt.srcElement&&evt.srcElement.ownerDocument)?evt.srcElement.ownerDocument:document;
var _39b=((dojo.render.html.ie55)||(doc["compatMode"]=="BackCompat"))?doc.body:doc.documentElement;
if(!evt.pageX){
evt.pageX=evt.clientX+(_39b.scrollLeft||0);
}
if(!evt.pageY){
evt.pageY=evt.clientY+(_39b.scrollTop||0);
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
dojo.extend(dojo.gfx.color.Color,{toRgb:function(_3a3){
if(_3a3){
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
},blend:function(_3a4,_3a5){
var rgb=null;
if(dojo.lang.isArray(_3a4)){
rgb=_3a4;
}else{
if(_3a4 instanceof dojo.gfx.color.Color){
rgb=_3a4.toRgb();
}else{
rgb=new dojo.gfx.color.Color(_3a4).toRgb();
}
}
return dojo.gfx.color.blend(this.toRgb(),rgb,_3a5);
}});
dojo.gfx.color.named={white:[255,255,255],black:[0,0,0],red:[255,0,0],green:[0,255,0],lime:[0,255,0],blue:[0,0,255],navy:[0,0,128],gray:[128,128,128],silver:[192,192,192]};
dojo.gfx.color.blend=function(a,b,_3a9){
if(typeof a=="string"){
return dojo.gfx.color.blendHex(a,b,_3a9);
}
if(!_3a9){
_3a9=0;
}
_3a9=Math.min(Math.max(-1,_3a9),1);
_3a9=((_3a9+1)/2);
var c=[];
for(var x=0;x<3;x++){
c[x]=parseInt(b[x]+((a[x]-b[x])*_3a9));
}
return c;
};
dojo.gfx.color.blendHex=function(a,b,_3ae){
return dojo.gfx.color.rgb2hex(dojo.gfx.color.blend(dojo.gfx.color.hex2rgb(a),dojo.gfx.color.hex2rgb(b),_3ae));
};
dojo.gfx.color.extractRGB=function(_3af){
var hex="0123456789abcdef";
_3af=_3af.toLowerCase();
if(_3af.indexOf("rgb")==0){
var _3b1=_3af.match(/rgba*\((\d+), *(\d+), *(\d+)/i);
var ret=_3b1.splice(1,3);
return ret;
}else{
var _3b3=dojo.gfx.color.hex2rgb(_3af);
if(_3b3){
return _3b3;
}else{
return dojo.gfx.color.named[_3af]||[255,255,255];
}
}
};
dojo.gfx.color.hex2rgb=function(hex){
var _3b5="0123456789ABCDEF";
var rgb=new Array(3);
if(hex.indexOf("#")==0){
hex=hex.substring(1);
}
hex=hex.toUpperCase();
if(hex.replace(new RegExp("["+_3b5+"]","g"),"")!=""){
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
rgb[i]=_3b5.indexOf(rgb[i].charAt(0))*16+_3b5.indexOf(rgb[i].charAt(1));
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
dojo.lfx.Line=function(_3be,end){
this.start=_3be;
this.end=end;
if(dojo.lang.isArray(_3be)){
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
var diff=end-_3be;
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
dojo.lang.extend(dojo.lfx.IAnimation,{curve:null,duration:1000,easing:null,repeatCount:0,rate:10,handler:null,beforeBegin:null,onBegin:null,onAnimate:null,onEnd:null,onPlay:null,onPause:null,onStop:null,play:null,pause:null,stop:null,connect:function(evt,_3ce,_3cf){
if(!_3cf){
_3cf=_3ce;
_3ce=this;
}
_3cf=dojo.lang.hitch(_3ce,_3cf);
var _3d0=this[evt]||function(){
};
this[evt]=function(){
var ret=_3d0.apply(this,arguments);
_3cf.apply(this,arguments);
return ret;
};
return this;
},fire:function(evt,args){
if(this[evt]){
this[evt].apply(this,(args||[]));
}
return this;
},repeat:function(_3d4){
this.repeatCount=_3d4;
return this;
},_active:false,_paused:false});
dojo.lfx.Animation=function(_3d5,_3d6,_3d7,_3d8,_3d9,rate){
dojo.lfx.IAnimation.call(this);
if(dojo.lang.isNumber(_3d5)||(!_3d5&&_3d6.getValue)){
rate=_3d9;
_3d9=_3d8;
_3d8=_3d7;
_3d7=_3d6;
_3d6=_3d5;
_3d5=null;
}else{
if(_3d5.getValue||dojo.lang.isArray(_3d5)){
rate=_3d8;
_3d9=_3d7;
_3d8=_3d6;
_3d7=_3d5;
_3d6=null;
_3d5=null;
}
}
if(dojo.lang.isArray(_3d7)){
this.curve=new dojo.lfx.Line(_3d7[0],_3d7[1]);
}else{
this.curve=_3d7;
}
if(_3d6!=null&&_3d6>0){
this.duration=_3d6;
}
if(_3d9){
this.repeatCount=_3d9;
}
if(rate){
this.rate=rate;
}
if(_3d5){
dojo.lang.forEach(["handler","beforeBegin","onBegin","onEnd","onPlay","onStop","onAnimate"],function(item){
if(_3d5[item]){
this.connect(item,_3d5[item]);
}
},this);
}
if(_3d8&&dojo.lang.isFunction(_3d8)){
this.easing=_3d8;
}
};
dojo.inherits(dojo.lfx.Animation,dojo.lfx.IAnimation);
dojo.lang.extend(dojo.lfx.Animation,{_startTime:null,_endTime:null,_timer:null,_percent:0,_startRepeatCount:0,play:function(_3dc,_3dd){
if(_3dd){
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
if(_3dc>0){
setTimeout(dojo.lang.hitch(this,function(){
this.play(null,_3dd);
}),_3dc);
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
var _3df=this.curve.getValue(step);
if(this._percent==0){
if(!this._startRepeatCount){
this._startRepeatCount=this.repeatCount;
}
this.fire("handler",["begin",_3df]);
this.fire("onBegin",[_3df]);
}
this.fire("handler",["play",_3df]);
this.fire("onPlay",[_3df]);
this._cycle();
return this;
},pause:function(){
clearTimeout(this._timer);
if(!this._active){
return this;
}
this._paused=true;
var _3e0=this.curve.getValue(this._percent/100);
this.fire("handler",["pause",_3e0]);
this.fire("onPause",[_3e0]);
return this;
},gotoPercent:function(pct,_3e2){
clearTimeout(this._timer);
this._active=true;
this._paused=true;
this._percent=pct;
if(_3e2){
this.play();
}
return this;
},stop:function(_3e3){
clearTimeout(this._timer);
var step=this._percent/100;
if(_3e3){
step=1;
}
var _3e5=this.curve.getValue(step);
this.fire("handler",["stop",_3e5]);
this.fire("onStop",[_3e5]);
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
var _3e8=this.curve.getValue(step);
this.fire("handler",["animate",_3e8]);
this.fire("onAnimate",[_3e8]);
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
dojo.lfx.Combine=function(_3e9){
dojo.lfx.IAnimation.call(this);
this._anims=[];
this._animsEnded=0;
var _3ea=arguments;
if(_3ea.length==1&&(dojo.lang.isArray(_3ea[0])||dojo.lang.isArrayLike(_3ea[0]))){
_3ea=_3ea[0];
}
dojo.lang.forEach(_3ea,function(anim){
this._anims.push(anim);
anim.connect("onEnd",dojo.lang.hitch(this,"_onAnimsEnded"));
},this);
};
dojo.inherits(dojo.lfx.Combine,dojo.lfx.IAnimation);
dojo.lang.extend(dojo.lfx.Combine,{_animsEnded:0,play:function(_3ec,_3ed){
if(!this._anims.length){
return this;
}
this.fire("beforeBegin");
if(_3ec>0){
setTimeout(dojo.lang.hitch(this,function(){
this.play(null,_3ed);
}),_3ec);
return this;
}
if(_3ed||this._anims[0].percent==0){
this.fire("onBegin");
}
this.fire("onPlay");
this._animsCall("play",null,_3ed);
return this;
},pause:function(){
this.fire("onPause");
this._animsCall("pause");
return this;
},stop:function(_3ee){
this.fire("onStop");
this._animsCall("stop",_3ee);
return this;
},_onAnimsEnded:function(){
this._animsEnded++;
if(this._animsEnded>=this._anims.length){
this.fire("onEnd");
}
return this;
},_animsCall:function(_3ef){
var args=[];
if(arguments.length>1){
for(var i=1;i<arguments.length;i++){
args.push(arguments[i]);
}
}
var _3f2=this;
dojo.lang.forEach(this._anims,function(anim){
anim[_3ef](args);
},_3f2);
return this;
}});
dojo.lfx.Chain=function(_3f4){
dojo.lfx.IAnimation.call(this);
this._anims=[];
this._currAnim=-1;
var _3f5=arguments;
if(_3f5.length==1&&(dojo.lang.isArray(_3f5[0])||dojo.lang.isArrayLike(_3f5[0]))){
_3f5=_3f5[0];
}
var _3f6=this;
dojo.lang.forEach(_3f5,function(anim,i,_3f9){
this._anims.push(anim);
if(i<_3f9.length-1){
anim.connect("onEnd",dojo.lang.hitch(this,"_playNext"));
}else{
anim.connect("onEnd",dojo.lang.hitch(this,function(){
this.fire("onEnd");
}));
}
},this);
};
dojo.inherits(dojo.lfx.Chain,dojo.lfx.IAnimation);
dojo.lang.extend(dojo.lfx.Chain,{_currAnim:-1,play:function(_3fa,_3fb){
if(!this._anims.length){
return this;
}
if(_3fb||!this._anims[this._currAnim]){
this._currAnim=0;
}
var _3fc=this._anims[this._currAnim];
this.fire("beforeBegin");
if(_3fa>0){
setTimeout(dojo.lang.hitch(this,function(){
this.play(null,_3fb);
}),_3fa);
return this;
}
if(_3fc){
if(this._currAnim==0){
this.fire("handler",["begin",this._currAnim]);
this.fire("onBegin",[this._currAnim]);
}
this.fire("onPlay",[this._currAnim]);
_3fc.play(null,_3fb);
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
var _3fd=this._anims[this._currAnim];
if(_3fd){
if(!_3fd._active||_3fd._paused){
this.play();
}else{
this.pause();
}
}
return this;
},stop:function(){
var _3fe=this._anims[this._currAnim];
if(_3fe){
_3fe.stop();
this.fire("onStop",[this._currAnim]);
}
return _3fe;
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
dojo.lfx.combine=function(_3ff){
var _400=arguments;
if(dojo.lang.isArray(arguments[0])){
_400=arguments[0];
}
if(_400.length==1){
return _400[0];
}
return new dojo.lfx.Combine(_400);
};
dojo.lfx.chain=function(_401){
var _402=arguments;
if(dojo.lang.isArray(arguments[0])){
_402=arguments[0];
}
if(_402.length==1){
return _402[0];
}
return new dojo.lfx.Chain(_402);
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
var _405=dojo.global();
var _406=dojo.doc();
var w=0;
var h=0;
if(dojo.render.html.mozilla){
w=_406.documentElement.clientWidth;
h=_405.innerHeight;
}else{
if(!dojo.render.html.opera&&_405.innerWidth){
w=_405.innerWidth;
h=_405.innerHeight;
}else{
if(!dojo.render.html.opera&&dojo.exists(_406,"documentElement.clientWidth")){
var w2=_406.documentElement.clientWidth;
if(!w||w2&&w2<w){
w=w2;
}
h=_406.documentElement.clientHeight;
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
var _40a=dojo.global();
var _40b=dojo.doc();
var top=_40a.pageYOffset||_40b.documentElement.scrollTop||dojo.body().scrollTop||0;
var left=_40a.pageXOffset||_40b.documentElement.scrollLeft||dojo.body().scrollLeft||0;
return {top:top,left:left,offset:{x:left,y:top}};
};
dojo.html.getParentByType=function(node,type){
var _410=dojo.doc();
var _411=dojo.byId(node);
type=type.toLowerCase();
while((_411)&&(_411.nodeName.toLowerCase()!=type)){
if(_411==(_410["body"]||_410["documentElement"])){
return null;
}
_411=_411.parentNode;
}
return _411;
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
var _419={x:0,y:0};
if(e.pageX||e.pageY){
_419.x=e.pageX;
_419.y=e.pageY;
}else{
var de=dojo.doc().documentElement;
var db=dojo.body();
_419.x=e.clientX+((de||db)["scrollLeft"])-((de||db)["clientLeft"]);
_419.y=e.clientY+((de||db)["scrollTop"])-((de||db)["clientTop"]);
}
return _419;
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
var _41e=dojo.doc().createElement("script");
_41e.src="javascript:'dojo.html.createExternalElement=function(doc, tag){ return doc.createElement(tag); }'";
dojo.doc().getElementsByTagName("head")[0].appendChild(_41e);
})();
}
}else{
dojo.html.createExternalElement=function(doc,tag){
return doc.createElement(tag);
};
}
dojo.html._callDeprecated=function(_421,_422,args,_424,_425){
dojo.deprecated("dojo.html."+_421,"replaced by dojo.html."+_422+"("+(_424?"node, {"+_424+": "+_424+"}":"")+")"+(_425?"."+_425:""),"0.5");
var _426=[];
if(_424){
var _427={};
_427[_424]=args[1];
_426.push(args[0]);
_426.push(_427);
}else{
_426=args;
}
var ret=dojo.html[_422].apply(dojo.html,args);
if(_425){
return ret[_425];
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
this.moduleUri=function(_42a,uri){
var loc=dojo.hostenv.getModuleSymbols(_42a).join("/");
if(!loc){
return null;
}
if(loc.lastIndexOf("/")!=loc.length-1){
loc+="/";
}
var _42d=loc.indexOf(":");
var _42e=loc.indexOf("/");
if(loc.charAt(0)!="/"&&(_42d==-1||_42d>_42e)){
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
var _431=new dojo.uri.Uri(arguments[i].toString());
var _432=new dojo.uri.Uri(uri.toString());
if((_431.path=="")&&(_431.scheme==null)&&(_431.authority==null)&&(_431.query==null)){
if(_431.fragment!=null){
_432.fragment=_431.fragment;
}
_431=_432;
}else{
if(_431.scheme==null){
_431.scheme=_432.scheme;
if(_431.authority==null){
_431.authority=_432.authority;
if(_431.path.charAt(0)!="/"){
var path=_432.path.substring(0,_432.path.lastIndexOf("/")+1)+_431.path;
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
_431.path=segs.join("/");
}
}
}
}
uri="";
if(_431.scheme!=null){
uri+=_431.scheme+":";
}
if(_431.authority!=null){
uri+="//"+_431.authority;
}
uri+=_431.path;
if(_431.query!=null){
uri+="?"+_431.query;
}
if(_431.fragment!=null){
uri+="#"+_431.fragment;
}
}
this.uri=uri.toString();
var _436="^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\\?([^#]*))?(#(.*))?$";
var r=this.uri.match(new RegExp(_436));
this.scheme=r[2]||(r[1]?"":null);
this.authority=r[4]||(r[3]?"":null);
this.path=r[5];
this.query=r[7]||(r[6]?"":null);
this.fragment=r[9]||(r[8]?"":null);
if(this.authority!=null){
_436="^((([^:]+:)?([^@]+))@)?([^:]*)(:([0-9]+))?$";
r=this.authority.match(new RegExp(_436));
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
dojo.html.hasClass=function(node,_43d){
return (new RegExp("(^|\\s+)"+_43d+"(\\s+|$)")).test(dojo.html.getClass(node));
};
dojo.html.prependClass=function(node,_43f){
_43f+=" "+dojo.html.getClass(node);
return dojo.html.setClass(node,_43f);
};
dojo.html.addClass=function(node,_441){
if(dojo.html.hasClass(node,_441)){
return false;
}
_441=(dojo.html.getClass(node)+" "+_441).replace(/^\s+|\s+$/g,"");
return dojo.html.setClass(node,_441);
};
dojo.html.setClass=function(node,_443){
node=dojo.byId(node);
var cs=new String(_443);
try{
if(typeof node.className=="string"){
node.className=cs;
}else{
if(node.setAttribute){
node.setAttribute("class",_443);
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
dojo.html.removeClass=function(node,_446,_447){
try{
if(!_447){
var _448=dojo.html.getClass(node).replace(new RegExp("(^|\\s+)"+_446+"(\\s+|$)"),"$1$2");
}else{
var _448=dojo.html.getClass(node).replace(_446,"");
}
dojo.html.setClass(node,_448);
}
catch(e){
dojo.debug("dojo.html.removeClass() failed",e);
}
return true;
};
dojo.html.replaceClass=function(node,_44a,_44b){
dojo.html.removeClass(node,_44b);
dojo.html.addClass(node,_44a);
};
dojo.html.classMatchType={ContainsAll:0,ContainsAny:1,IsOnly:2};
dojo.html.getElementsByClass=function(_44c,_44d,_44e,_44f,_450){
_450=false;
var _451=dojo.doc();
_44d=dojo.byId(_44d)||_451;
var _452=_44c.split(/\s+/g);
var _453=[];
if(_44f!=1&&_44f!=2){
_44f=0;
}
var _454=new RegExp("(\\s|^)(("+_452.join(")|(")+"))(\\s|$)");
var _455=_452.join(" ").length;
var _456=[];
if(!_450&&_451.evaluate){
var _457=".//"+(_44e||"*")+"[contains(";
if(_44f!=dojo.html.classMatchType.ContainsAny){
_457+="concat(' ',@class,' '), ' "+_452.join(" ') and contains(concat(' ',@class,' '), ' ")+" ')";
if(_44f==2){
_457+=" and string-length(@class)="+_455+"]";
}else{
_457+="]";
}
}else{
_457+="concat(' ',@class,' '), ' "+_452.join(" ') or contains(concat(' ',@class,' '), ' ")+" ')]";
}
var _458=_451.evaluate(_457,_44d,null,XPathResult.ANY_TYPE,null);
var _459=_458.iterateNext();
while(_459){
try{
_456.push(_459);
_459=_458.iterateNext();
}
catch(e){
break;
}
}
return _456;
}else{
if(!_44e){
_44e="*";
}
_456=_44d.getElementsByTagName(_44e);
var node,i=0;
outer:
while(node=_456[i++]){
var _45c=dojo.html.getClasses(node);
if(_45c.length==0){
continue outer;
}
var _45d=0;
for(var j=0;j<_45c.length;j++){
if(_454.test(_45c[j])){
if(_44f==dojo.html.classMatchType.ContainsAny){
_453.push(node);
continue outer;
}else{
_45d++;
}
}else{
if(_44f==dojo.html.classMatchType.IsOnly){
continue outer;
}
}
}
if(_45d==_452.length){
if((_44f==dojo.html.classMatchType.IsOnly)&&(_45d==_45c.length)){
_453.push(node);
}else{
if(_44f==dojo.html.classMatchType.ContainsAll){
_453.push(node);
}
}
}
}
return _453;
}
};
dojo.html.getElementsByClassName=dojo.html.getElementsByClass;
dojo.html.toCamelCase=function(_45f){
var arr=_45f.split("-"),cc=arr[0];
for(var i=1;i<arr.length;i++){
cc+=arr[i].charAt(0).toUpperCase()+arr[i].substring(1);
}
return cc;
};
dojo.html.toSelectorCase=function(_463){
return _463.replace(/([A-Z])/g,"-$1").toLowerCase();
};
if(dojo.render.html.ie){
dojo.html.getComputedStyle=function(node,_465,_466){
node=dojo.byId(node);
if(!node||!node.style){
return _466;
}
return node.currentStyle[dojo.html.toCamelCase(_465)];
};
dojo.html.getComputedStyles=function(node){
return node.currentStyle;
};
}else{
dojo.html.getComputedStyle=function(node,_469,_46a){
node=dojo.byId(node);
if(!node||!node.style){
return _46a;
}
var s=document.defaultView.getComputedStyle(node,null);
return (s&&s[dojo.html.toCamelCase(_469)])||"";
};
dojo.html.getComputedStyles=function(node){
return document.defaultView.getComputedStyle(node,null);
};
}
dojo.html.getStyleProperty=function(node,_46e){
node=dojo.byId(node);
return (node&&node.style?node.style[dojo.html.toCamelCase(_46e)]:undefined);
};
dojo.html.getStyle=function(node,_470){
var _471=dojo.html.getStyleProperty(node,_470);
return (_471?_471:dojo.html.getComputedStyle(node,_470));
};
dojo.html.setStyle=function(node,_473,_474){
node=dojo.byId(node);
if(node&&node.style){
var _475=dojo.html.toCamelCase(_473);
node.style[_475]=_474;
}
};
dojo.html.setStyleText=function(_476,text){
try{
_476.style.cssText=text;
}
catch(e){
_476.setAttribute("style",text);
}
};
dojo.html.copyStyle=function(_478,_479){
if(!_479.style.cssText){
_478.setAttribute("style",_479.getAttribute("style"));
}else{
_478.style.cssText=_479.style.cssText;
}
dojo.html.addClass(_478,dojo.html.getClass(_479));
};
dojo.html.getUnitValue=function(node,_47b,_47c){
var s=dojo.html.getComputedStyle(node,_47b);
if((!s)||((s=="auto")&&(_47c))){
return {value:0,units:"px"};
}
var _47e=s.match(/(\-?[\d.]+)([a-z%]*)/i);
if(!_47e){
return dojo.html.getUnitValue.bad;
}
return {value:Number(_47e[1]),units:_47e[2].toLowerCase()};
};
dojo.html.getUnitValue.bad={value:NaN,units:""};
if(dojo.render.html.ie){
dojo.html.toPixelValue=function(_47f,_480){
if(!_480){
return 0;
}
if(_480.slice(-2)=="px"){
return parseFloat(_480);
}
var _481=0;
with(_47f){
var _482=style.left;
var _483=runtimeStyle.left;
runtimeStyle.left=currentStyle.left;
try{
style.left=_480||0;
_481=style.pixelLeft;
style.left=_482;
runtimeStyle.left=_483;
}
catch(e){
}
}
return _481;
};
}else{
dojo.html.toPixelValue=function(_484,_485){
return (_485&&(_485.slice(-2)=="px")?parseFloat(_485):0);
};
}
dojo.html.getPixelValue=function(node,_487,_488){
return dojo.html.toPixelValue(node,dojo.html.getComputedStyle(node,_487));
};
dojo.html.setPositivePixelValue=function(node,_48a,_48b){
if(isNaN(_48b)){
return false;
}
node.style[_48a]=Math.max(0,_48b)+"px";
return true;
};
dojo.html.styleSheet=null;
dojo.html.insertCssRule=function(_48c,_48d,_48e){
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
_48e=dojo.html.styleSheet.cssRules.length;
}else{
if(dojo.html.styleSheet.rules){
_48e=dojo.html.styleSheet.rules.length;
}else{
return null;
}
}
}
if(dojo.html.styleSheet.insertRule){
var rule=_48c+" { "+_48d+" }";
return dojo.html.styleSheet.insertRule(rule,_48e);
}else{
if(dojo.html.styleSheet.addRule){
return dojo.html.styleSheet.addRule(_48c,_48d,_48e);
}else{
return null;
}
}
};
dojo.html.removeCssRule=function(_490){
if(!dojo.html.styleSheet){
dojo.debug("no stylesheet defined for removing rules");
return false;
}
if(dojo.render.html.ie){
if(!_490){
_490=dojo.html.styleSheet.rules.length;
dojo.html.styleSheet.removeRule(_490);
}
}else{
if(document.styleSheets[0]){
if(!_490){
_490=dojo.html.styleSheet.cssRules.length;
}
dojo.html.styleSheet.deleteRule(_490);
}
}
return true;
};
dojo.html._insertedCssFiles=[];
dojo.html.insertCssFile=function(URI,doc,_493,_494){
if(!URI){
return;
}
if(!doc){
doc=document;
}
var _495=dojo.hostenv.getText(URI,false,_494);
if(_495===null){
return;
}
_495=dojo.html.fixPathsInCssText(_495,URI);
if(_493){
var idx=-1,node,ent=dojo.html._insertedCssFiles;
for(var i=0;i<ent.length;i++){
if((ent[i].doc==doc)&&(ent[i].cssText==_495)){
idx=i;
node=ent[i].nodeRef;
break;
}
}
if(node){
var _49a=doc.getElementsByTagName("style");
for(var i=0;i<_49a.length;i++){
if(_49a[i]==node){
return;
}
}
dojo.html._insertedCssFiles.shift(idx,1);
}
}
var _49b=dojo.html.insertCssText(_495,doc);
dojo.html._insertedCssFiles.push({"doc":doc,"cssText":_495,"nodeRef":_49b});
if(_49b&&djConfig.isDebug){
_49b.setAttribute("dbgHref",URI);
}
return _49b;
};
dojo.html.insertCssText=function(_49c,doc,URI){
if(!_49c){
return;
}
if(!doc){
doc=document;
}
if(URI){
_49c=dojo.html.fixPathsInCssText(_49c,URI);
}
var _49f=doc.createElement("style");
_49f.setAttribute("type","text/css");
var head=doc.getElementsByTagName("head")[0];
if(!head){
dojo.debug("No head tag in document, aborting styles");
return;
}else{
head.appendChild(_49f);
}
if(_49f.styleSheet){
var _4a1=function(){
try{
_49f.styleSheet.cssText=_49c;
}
catch(e){
dojo.debug(e);
}
};
if(_49f.styleSheet.disabled){
setTimeout(_4a1,10);
}else{
_4a1();
}
}else{
var _4a2=doc.createTextNode(_49c);
_49f.appendChild(_4a2);
}
return _49f;
};
dojo.html.fixPathsInCssText=function(_4a3,URI){
if(!_4a3||!URI){
return;
}
var _4a5,str="",url="",_4a8="[\\t\\s\\w\\(\\)\\/\\.\\\\'\"-:#=&?~]+";
var _4a9=new RegExp("url\\(\\s*("+_4a8+")\\s*\\)");
var _4aa=/(file|https?|ftps?):\/\//;
regexTrim=new RegExp("^[\\s]*(['\"]?)("+_4a8+")\\1[\\s]*?$");
if(dojo.render.html.ie55||dojo.render.html.ie60){
var _4ab=new RegExp("AlphaImageLoader\\((.*)src=['\"]("+_4a8+")['\"]");
while(_4a5=_4ab.exec(_4a3)){
url=_4a5[2].replace(regexTrim,"$2");
if(!_4aa.exec(url)){
url=(new dojo.uri.Uri(URI,url).toString());
}
str+=_4a3.substring(0,_4a5.index)+"AlphaImageLoader("+_4a5[1]+"src='"+url+"'";
_4a3=_4a3.substr(_4a5.index+_4a5[0].length);
}
_4a3=str+_4a3;
str="";
}
while(_4a5=_4a9.exec(_4a3)){
url=_4a5[1].replace(regexTrim,"$2");
if(!_4aa.exec(url)){
url=(new dojo.uri.Uri(URI,url).toString());
}
str+=_4a3.substring(0,_4a5.index)+"url("+url+")";
_4a3=_4a3.substr(_4a5.index+_4a5[0].length);
}
return str+_4a3;
};
dojo.html.setActiveStyleSheet=function(_4ac){
var i=0,a,els=dojo.doc().getElementsByTagName("link");
while(a=els[i++]){
if(a.getAttribute("rel").indexOf("style")!=-1&&a.getAttribute("title")){
a.disabled=true;
if(a.getAttribute("title")==_4ac){
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
var _4b8={dj_ie:drh.ie,dj_ie55:drh.ie55,dj_ie6:drh.ie60,dj_ie7:drh.ie70,dj_iequirks:drh.ie&&drh.quirks,dj_opera:drh.opera,dj_opera8:drh.opera&&(Math.floor(dojo.render.version)==8),dj_opera9:drh.opera&&(Math.floor(dojo.render.version)==9),dj_khtml:drh.khtml,dj_safari:drh.safari,dj_gecko:drh.mozilla};
for(var p in _4b8){
if(_4b8[p]){
dojo.html.addClass(node,p);
}
}
};
dojo.provide("dojo.html.display");
dojo.html._toggle=function(node,_4bb,_4bc){
node=dojo.byId(node);
_4bc(node,!_4bb(node));
return _4bb(node);
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
dojo.html.setShowing=function(node,_4c1){
dojo.html[(_4c1?"show":"hide")](node);
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
dojo.html.setDisplay=function(node,_4c7){
dojo.html.setStyle(node,"display",((_4c7 instanceof String||typeof _4c7=="string")?_4c7:(_4c7?dojo.html.suggestDisplayByTagName(node):"none")));
};
dojo.html.isDisplayed=function(node){
return (dojo.html.getComputedStyle(node,"display")!="none");
};
dojo.html.toggleDisplay=function(node){
return dojo.html._toggle(node,dojo.html.isDisplayed,dojo.html.setDisplay);
};
dojo.html.setVisibility=function(node,_4cb){
dojo.html.setStyle(node,"visibility",((_4cb instanceof String||typeof _4cb=="string")?_4cb:(_4cb?"visible":"hidden")));
};
dojo.html.isVisible=function(node){
return (dojo.html.getComputedStyle(node,"visibility")!="hidden");
};
dojo.html.toggleVisibility=function(node){
return dojo.html._toggle(node,dojo.html.isVisible,dojo.html.setVisibility);
};
dojo.html.setOpacity=function(node,_4cf,_4d0){
node=dojo.byId(node);
var h=dojo.render.html;
if(!_4d0){
if(_4cf>=1){
if(h.ie){
dojo.html.clearOpacity(node);
return;
}else{
_4cf=0.999999;
}
}else{
if(_4cf<0){
_4cf=0;
}
}
}
if(h.ie){
if(node.nodeName.toLowerCase()=="tr"){
var tds=node.getElementsByTagName("td");
for(var x=0;x<tds.length;x++){
tds[x].style.filter="Alpha(Opacity="+_4cf*100+")";
}
}
node.style.filter="Alpha(Opacity="+_4cf*100+")";
}else{
if(h.moz){
node.style.opacity=_4cf;
node.style.MozOpacity=_4cf;
}else{
if(h.safari){
node.style.opacity=_4cf;
node.style.KhtmlOpacity=_4cf;
}else{
node.style.opacity=_4cf;
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
var _4db;
do{
_4db=dojo.html.getStyle(node,"background-color");
if(_4db.toLowerCase()=="rgba(0, 0, 0, 0)"){
_4db="transparent";
}
if(node==document.getElementsByTagName("body")[0]){
node=null;
break;
}
node=node.parentNode;
}while(node&&dojo.lang.inArray(["transparent",""],_4db));
if(_4db=="transparent"){
_4db=[255,255,255,0];
}else{
_4db=dojo.gfx.color.extractRGB(_4db);
}
return _4db;
};
dojo.provide("dojo.html.layout");
dojo.html.sumAncestorProperties=function(node,prop){
node=dojo.byId(node);
if(!node){
return 0;
}
var _4de=0;
while(node){
if(dojo.html.getComputedStyle(node,"position")=="fixed"){
return 0;
}
var val=node[prop];
if(val){
_4de+=val-0;
if(node==dojo.body()){
break;
}
}
node=node.parentNode;
}
return _4de;
};
dojo.html.setStyleAttributes=function(node,_4e1){
node=dojo.byId(node);
var _4e2=_4e1.replace(/(;)?\s*$/,"").split(";");
for(var i=0;i<_4e2.length;i++){
var _4e4=_4e2[i].split(":");
var name=_4e4[0].replace(/\s*$/,"").replace(/^\s*/,"").toLowerCase();
var _4e6=_4e4[1].replace(/\s*$/,"").replace(/^\s*/,"");
switch(name){
case "opacity":
dojo.html.setOpacity(node,_4e6);
break;
case "content-height":
dojo.html.setContentBox(node,{height:_4e6});
break;
case "content-width":
dojo.html.setContentBox(node,{width:_4e6});
break;
case "outer-height":
dojo.html.setMarginBox(node,{height:_4e6});
break;
case "outer-width":
dojo.html.setMarginBox(node,{width:_4e6});
break;
default:
node.style[dojo.html.toCamelCase(name)]=_4e6;
}
}
};
dojo.html.boxSizing={MARGIN_BOX:"margin-box",BORDER_BOX:"border-box",PADDING_BOX:"padding-box",CONTENT_BOX:"content-box"};
dojo.html.getAbsolutePosition=dojo.html.abs=function(node,_4e8,_4e9){
node=dojo.byId(node,node.ownerDocument);
var ret={x:0,y:0};
var bs=dojo.html.boxSizing;
if(!_4e9){
_4e9=bs.CONTENT_BOX;
}
var _4ec=2;
var _4ed;
switch(_4e9){
case bs.MARGIN_BOX:
_4ed=3;
break;
case bs.BORDER_BOX:
_4ed=2;
break;
case bs.PADDING_BOX:
default:
_4ed=1;
break;
case bs.CONTENT_BOX:
_4ed=0;
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
_4ec=1;
try{
var bo=document.getBoxObjectFor(node);
ret.x=bo.x-dojo.html.sumAncestorProperties(node,"scrollLeft");
ret.y=bo.y-dojo.html.sumAncestorProperties(node,"scrollTop");
}
catch(e){
}
}else{
if(node["offsetParent"]){
var _4f1;
if((h.safari)&&(node.style.getPropertyValue("position")=="absolute")&&(node.parentNode==db)){
_4f1=db;
}else{
_4f1=db.parentNode;
}
if(node.parentNode!=db){
var nd=node;
if(dojo.render.html.opera){
nd=db;
}
ret.x-=dojo.html.sumAncestorProperties(nd,"scrollLeft");
ret.y-=dojo.html.sumAncestorProperties(nd,"scrollTop");
}
var _4f3=node;
do{
var n=_4f3["offsetLeft"];
if(!h.opera||n>0){
ret.x+=isNaN(n)?0:n;
}
var m=_4f3["offsetTop"];
ret.y+=isNaN(m)?0:m;
_4f3=_4f3.offsetParent;
}while((_4f3!=_4f1)&&(_4f3!=null));
}else{
if(node["x"]&&node["y"]){
ret.x+=isNaN(node.x)?0:node.x;
ret.y+=isNaN(node.y)?0:node.y;
}
}
}
}
if(_4e8){
var _4f6=dojo.html.getScroll();
ret.y+=_4f6.top;
ret.x+=_4f6.left;
}
var _4f7=[dojo.html.getPaddingExtent,dojo.html.getBorderExtent,dojo.html.getMarginExtent];
if(_4ec>_4ed){
for(var i=_4ed;i<_4ec;++i){
ret.y+=_4f7[i](node,"top");
ret.x+=_4f7[i](node,"left");
}
}else{
if(_4ec<_4ed){
for(var i=_4ed;i>_4ec;--i){
ret.y-=_4f7[i-1](node,"top");
ret.x-=_4f7[i-1](node,"left");
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
dojo.html._sumPixelValues=function(node,_4fb,_4fc){
var _4fd=0;
for(var x=0;x<_4fb.length;x++){
_4fd+=dojo.html.getPixelValue(node,_4fb[x],_4fc);
}
return _4fd;
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
var _50a=dojo.html.getBorder(node);
return {width:pad.width+_50a.width,height:pad.height+_50a.height};
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
var _50f;
if(!h.ie){
_50f=dojo.html.getStyle(node,"-moz-box-sizing");
if(!_50f){
_50f=dojo.html.getStyle(node,"box-sizing");
}
}
return (_50f?_50f:bs.CONTENT_BOX);
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
var _514=dojo.html.getBorder(node);
return {width:box.width-_514.width,height:box.height-_514.height};
};
dojo.html.getContentBox=function(node){
node=dojo.byId(node);
var _516=dojo.html.getPadBorder(node);
return {width:node.offsetWidth-_516.width,height:node.offsetHeight-_516.height};
};
dojo.html.setContentBox=function(node,args){
node=dojo.byId(node);
var _519=0;
var _51a=0;
var isbb=dojo.html.isBorderBox(node);
var _51c=(isbb?dojo.html.getPadBorder(node):{width:0,height:0});
var ret={};
if(typeof args.width!="undefined"){
_519=args.width+_51c.width;
ret.width=dojo.html.setPositivePixelValue(node,"width",_519);
}
if(typeof args.height!="undefined"){
_51a=args.height+_51c.height;
ret.height=dojo.html.setPositivePixelValue(node,"height",_51a);
}
return ret;
};
dojo.html.getMarginBox=function(node){
var _51f=dojo.html.getBorderBox(node);
var _520=dojo.html.getMargin(node);
return {width:_51f.width+_520.width,height:_51f.height+_520.height};
};
dojo.html.setMarginBox=function(node,args){
node=dojo.byId(node);
var _523=0;
var _524=0;
var isbb=dojo.html.isBorderBox(node);
var _526=(!isbb?dojo.html.getPadBorder(node):{width:0,height:0});
var _527=dojo.html.getMargin(node);
var ret={};
if(typeof args.width!="undefined"){
_523=args.width-_526.width;
_523-=_527.width;
ret.width=dojo.html.setPositivePixelValue(node,"width",_523);
}
if(typeof args.height!="undefined"){
_524=args.height-_526.height;
_524-=_527.height;
ret.height=dojo.html.setPositivePixelValue(node,"height",_524);
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
dojo.html.toCoordinateObject=dojo.html.toCoordinateArray=function(_52c,_52d,_52e){
if(_52c instanceof Array||typeof _52c=="array"){
dojo.deprecated("dojo.html.toCoordinateArray","use dojo.html.toCoordinateObject({left: , top: , width: , height: }) instead","0.5");
while(_52c.length<4){
_52c.push(0);
}
while(_52c.length>4){
_52c.pop();
}
var ret={left:_52c[0],top:_52c[1],width:_52c[2],height:_52c[3]};
}else{
if(!_52c.nodeType&&!(_52c instanceof String||typeof _52c=="string")&&("width" in _52c||"height" in _52c||"left" in _52c||"x" in _52c||"top" in _52c||"y" in _52c)){
var ret={left:_52c.left||_52c.x||0,top:_52c.top||_52c.y||0,width:_52c.width||0,height:_52c.height||0};
}else{
var node=dojo.byId(_52c);
var pos=dojo.html.abs(node,_52d,_52e);
var _532=dojo.html.getMarginBox(node);
var ret={left:pos.left,top:pos.top,width:_532.width,height:_532.height};
}
}
ret.x=ret.left;
ret.y=ret.top;
return ret;
};
dojo.html.setMarginBoxWidth=dojo.html.setOuterWidth=function(node,_534){
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
dojo.html.getTotalOffset=function(node,type,_537){
return dojo.html._callDeprecated("getTotalOffset","getAbsolutePosition",arguments,null,type);
};
dojo.html.getAbsoluteX=function(node,_539){
return dojo.html._callDeprecated("getAbsoluteX","getAbsolutePosition",arguments,null,"x");
};
dojo.html.getAbsoluteY=function(node,_53b){
return dojo.html._callDeprecated("getAbsoluteY","getAbsolutePosition",arguments,null,"y");
};
dojo.html.totalOffsetLeft=function(node,_53d){
return dojo.html._callDeprecated("totalOffsetLeft","getAbsolutePosition",arguments,null,"left");
};
dojo.html.totalOffsetTop=function(node,_53f){
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
dojo.html.setContentBoxWidth=dojo.html.setContentWidth=function(node,_549){
return dojo.html._callDeprecated("setContentBoxWidth","setContentBox",arguments,"width");
};
dojo.html.setContentBoxHeight=dojo.html.setContentHeight=function(node,_54b){
return dojo.html._callDeprecated("setContentBoxHeight","setContentBox",arguments,"height");
};
dojo.provide("dojo.lfx.html");
dojo.lfx.html._byId=function(_54c){
if(!_54c){
return [];
}
if(dojo.lang.isArrayLike(_54c)){
if(!_54c.alreadyChecked){
var n=[];
dojo.lang.forEach(_54c,function(node){
n.push(dojo.byId(node));
});
n.alreadyChecked=true;
return n;
}else{
return _54c;
}
}else{
var n=[];
n.push(dojo.byId(_54c));
n.alreadyChecked=true;
return n;
}
};
dojo.lfx.html.propertyAnimation=function(_54f,_550,_551,_552,_553){
_54f=dojo.lfx.html._byId(_54f);
var _554={"propertyMap":_550,"nodes":_54f,"duration":_551,"easing":_552||dojo.lfx.easeDefault};
var _555=function(args){
if(args.nodes.length==1){
var pm=args.propertyMap;
if(!dojo.lang.isArray(args.propertyMap)){
var parr=[];
for(var _559 in pm){
pm[_559].property=_559;
parr.push(pm[_559]);
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
var _55b=function(_55c){
var _55d=[];
dojo.lang.forEach(_55c,function(c){
_55d.push(Math.round(c));
});
return _55d;
};
var _55f=function(n,_561){
n=dojo.byId(n);
if(!n||!n.style){
return;
}
for(var s in _561){
try{
if(s=="opacity"){
dojo.html.setOpacity(n,_561[s]);
}else{
n.style[s]=_561[s];
}
}
catch(e){
dojo.debug(e);
}
}
};
var _563=function(_564){
this._properties=_564;
this.diffs=new Array(_564.length);
dojo.lang.forEach(_564,function(prop,i){
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
var _56b=null;
if(dojo.lang.isArray(prop.start)){
}else{
if(prop.start instanceof dojo.gfx.color.Color){
_56b=(prop.units||"rgb")+"(";
for(var j=0;j<prop.startRgb.length;j++){
_56b+=Math.round(((prop.endRgb[j]-prop.startRgb[j])*n)+prop.startRgb[j])+(j<prop.startRgb.length-1?",":"");
}
_56b+=")";
}else{
_56b=((this.diffs[i])*n)+prop.start+(prop.property!="opacity"?prop.units||"px":"");
}
}
ret[dojo.html.toCamelCase(prop.property)]=_56b;
},this);
return ret;
};
};
var anim=new dojo.lfx.Animation({beforeBegin:function(){
_555(_554);
anim.curve=new _563(_554.propertyMap);
},onAnimate:function(_56e){
dojo.lang.forEach(_554.nodes,function(node){
_55f(node,_56e);
});
}},_554.duration,null,_554.easing);
if(_553){
for(var x in _553){
if(dojo.lang.isFunction(_553[x])){
anim.connect(x,anim,_553[x]);
}
}
}
return anim;
};
dojo.lfx.html._makeFadeable=function(_571){
var _572=function(node){
if(dojo.render.html.ie){
if((node.style.zoom.length==0)&&(dojo.html.getStyle(node,"zoom")=="normal")){
node.style.zoom="1";
}
if((node.style.width.length==0)&&(dojo.html.getStyle(node,"width")=="auto")){
node.style.width="auto";
}
}
};
if(dojo.lang.isArrayLike(_571)){
dojo.lang.forEach(_571,_572);
}else{
_572(_571);
}
};
dojo.lfx.html.fade=function(_574,_575,_576,_577,_578){
_574=dojo.lfx.html._byId(_574);
var _579={property:"opacity"};
if(!dj_undef("start",_575)){
_579.start=_575.start;
}else{
_579.start=function(){
return dojo.html.getOpacity(_574[0]);
};
}
if(!dj_undef("end",_575)){
_579.end=_575.end;
}else{
dojo.raise("dojo.lfx.html.fade needs an end value");
}
var anim=dojo.lfx.propertyAnimation(_574,[_579],_576,_577);
anim.connect("beforeBegin",function(){
dojo.lfx.html._makeFadeable(_574);
});
if(_578){
anim.connect("onEnd",function(){
_578(_574,anim);
});
}
return anim;
};
dojo.lfx.html.fadeIn=function(_57b,_57c,_57d,_57e){
return dojo.lfx.html.fade(_57b,{end:1},_57c,_57d,_57e);
};
dojo.lfx.html.fadeOut=function(_57f,_580,_581,_582){
return dojo.lfx.html.fade(_57f,{end:0},_580,_581,_582);
};
dojo.lfx.html.fadeShow=function(_583,_584,_585,_586){
_583=dojo.lfx.html._byId(_583);
dojo.lang.forEach(_583,function(node){
dojo.html.setOpacity(node,0);
});
var anim=dojo.lfx.html.fadeIn(_583,_584,_585,_586);
anim.connect("beforeBegin",function(){
if(dojo.lang.isArrayLike(_583)){
dojo.lang.forEach(_583,dojo.html.show);
}else{
dojo.html.show(_583);
}
});
return anim;
};
dojo.lfx.html.fadeHide=function(_589,_58a,_58b,_58c){
var anim=dojo.lfx.html.fadeOut(_589,_58a,_58b,function(){
if(dojo.lang.isArrayLike(_589)){
dojo.lang.forEach(_589,dojo.html.hide);
}else{
dojo.html.hide(_589);
}
if(_58c){
_58c(_589,anim);
}
});
return anim;
};
dojo.lfx.html.wipeIn=function(_58e,_58f,_590,_591){
_58e=dojo.lfx.html._byId(_58e);
var _592=[];
dojo.lang.forEach(_58e,function(node){
var _594={};
var _595,_596,_597;
with(node.style){
_595=top;
_596=left;
_597=position;
top="-9999px";
left="-9999px";
position="absolute";
display="";
}
var _598=dojo.html.getBorderBox(node).height;
with(node.style){
top=_595;
left=_596;
position=_597;
display="none";
}
var anim=dojo.lfx.propertyAnimation(node,{"height":{start:1,end:function(){
return _598;
}}},_58f,_590);
anim.connect("beforeBegin",function(){
_594.overflow=node.style.overflow;
_594.height=node.style.height;
with(node.style){
overflow="hidden";
height="1px";
}
dojo.html.show(node);
});
anim.connect("onEnd",function(){
with(node.style){
overflow=_594.overflow;
height=_594.height;
}
if(_591){
_591(node,anim);
}
});
_592.push(anim);
});
return dojo.lfx.combine(_592);
};
dojo.lfx.html.wipeOut=function(_59a,_59b,_59c,_59d){
_59a=dojo.lfx.html._byId(_59a);
var _59e=[];
dojo.lang.forEach(_59a,function(node){
var _5a0={};
var anim=dojo.lfx.propertyAnimation(node,{"height":{start:function(){
return dojo.html.getContentBox(node).height;
},end:1}},_59b,_59c,{"beforeBegin":function(){
_5a0.overflow=node.style.overflow;
_5a0.height=node.style.height;
with(node.style){
overflow="hidden";
}
dojo.html.show(node);
},"onEnd":function(){
dojo.html.hide(node);
with(node.style){
overflow=_5a0.overflow;
height=_5a0.height;
}
if(_59d){
_59d(node,anim);
}
}});
_59e.push(anim);
});
return dojo.lfx.combine(_59e);
};
dojo.lfx.html.slideTo=function(_5a2,_5a3,_5a4,_5a5,_5a6){
_5a2=dojo.lfx.html._byId(_5a2);
var _5a7=[];
var _5a8=dojo.html.getComputedStyle;
if(dojo.lang.isArray(_5a3)){
dojo.deprecated("dojo.lfx.html.slideTo(node, array)","use dojo.lfx.html.slideTo(node, {top: value, left: value});","0.5");
_5a3={top:_5a3[0],left:_5a3[1]};
}
dojo.lang.forEach(_5a2,function(node){
var top=null;
var left=null;
var init=(function(){
var _5ad=node;
return function(){
var pos=_5a8(_5ad,"position");
top=(pos=="absolute"?node.offsetTop:parseInt(_5a8(node,"top"))||0);
left=(pos=="absolute"?node.offsetLeft:parseInt(_5a8(node,"left"))||0);
if(!dojo.lang.inArray(["absolute","relative"],pos)){
var ret=dojo.html.abs(_5ad,true);
dojo.html.setStyleAttributes(_5ad,"position:absolute;top:"+ret.y+"px;left:"+ret.x+"px;");
top=ret.y;
left=ret.x;
}
};
})();
init();
var anim=dojo.lfx.propertyAnimation(node,{"top":{start:top,end:(_5a3.top||0)},"left":{start:left,end:(_5a3.left||0)}},_5a4,_5a5,{"beforeBegin":init});
if(_5a6){
anim.connect("onEnd",function(){
_5a6(_5a2,anim);
});
}
_5a7.push(anim);
});
return dojo.lfx.combine(_5a7);
};
dojo.lfx.html.slideBy=function(_5b1,_5b2,_5b3,_5b4,_5b5){
_5b1=dojo.lfx.html._byId(_5b1);
var _5b6=[];
var _5b7=dojo.html.getComputedStyle;
if(dojo.lang.isArray(_5b2)){
dojo.deprecated("dojo.lfx.html.slideBy(node, array)","use dojo.lfx.html.slideBy(node, {top: value, left: value});","0.5");
_5b2={top:_5b2[0],left:_5b2[1]};
}
dojo.lang.forEach(_5b1,function(node){
var top=null;
var left=null;
var init=(function(){
var _5bc=node;
return function(){
var pos=_5b7(_5bc,"position");
top=(pos=="absolute"?node.offsetTop:parseInt(_5b7(node,"top"))||0);
left=(pos=="absolute"?node.offsetLeft:parseInt(_5b7(node,"left"))||0);
if(!dojo.lang.inArray(["absolute","relative"],pos)){
var ret=dojo.html.abs(_5bc,true);
dojo.html.setStyleAttributes(_5bc,"position:absolute;top:"+ret.y+"px;left:"+ret.x+"px;");
top=ret.y;
left=ret.x;
}
};
})();
init();
var anim=dojo.lfx.propertyAnimation(node,{"top":{start:top,end:top+(_5b2.top||0)},"left":{start:left,end:left+(_5b2.left||0)}},_5b3,_5b4).connect("beforeBegin",init);
if(_5b5){
anim.connect("onEnd",function(){
_5b5(_5b1,anim);
});
}
_5b6.push(anim);
});
return dojo.lfx.combine(_5b6);
};
dojo.lfx.html.explode=function(_5c0,_5c1,_5c2,_5c3,_5c4){
var h=dojo.html;
_5c0=dojo.byId(_5c0);
_5c1=dojo.byId(_5c1);
var _5c6=h.toCoordinateObject(_5c0,true);
var _5c7=document.createElement("div");
h.copyStyle(_5c7,_5c1);
if(_5c1.explodeClassName){
_5c7.className=_5c1.explodeClassName;
}
with(_5c7.style){
position="absolute";
display="none";
var _5c8=h.getStyle(_5c0,"background-color");
backgroundColor=_5c8?_5c8.toLowerCase():"transparent";
backgroundColor=(backgroundColor=="transparent")?"rgb(221, 221, 221)":backgroundColor;
}
dojo.body().appendChild(_5c7);
with(_5c1.style){
visibility="hidden";
display="block";
}
var _5c9=h.toCoordinateObject(_5c1,true);
with(_5c1.style){
display="none";
visibility="visible";
}
var _5ca={opacity:{start:0.5,end:1}};
dojo.lang.forEach(["height","width","top","left"],function(type){
_5ca[type]={start:_5c6[type],end:_5c9[type]};
});
var anim=new dojo.lfx.propertyAnimation(_5c7,_5ca,_5c2,_5c3,{"beforeBegin":function(){
h.setDisplay(_5c7,"block");
},"onEnd":function(){
h.setDisplay(_5c1,"block");
_5c7.parentNode.removeChild(_5c7);
}});
if(_5c4){
anim.connect("onEnd",function(){
_5c4(_5c1,anim);
});
}
return anim;
};
dojo.lfx.html.implode=function(_5cd,end,_5cf,_5d0,_5d1){
var h=dojo.html;
_5cd=dojo.byId(_5cd);
end=dojo.byId(end);
var _5d3=dojo.html.toCoordinateObject(_5cd,true);
var _5d4=dojo.html.toCoordinateObject(end,true);
var _5d5=document.createElement("div");
dojo.html.copyStyle(_5d5,_5cd);
if(_5cd.explodeClassName){
_5d5.className=_5cd.explodeClassName;
}
dojo.html.setOpacity(_5d5,0.3);
with(_5d5.style){
position="absolute";
display="none";
backgroundColor=h.getStyle(_5cd,"background-color").toLowerCase();
}
dojo.body().appendChild(_5d5);
var _5d6={opacity:{start:1,end:0.5}};
dojo.lang.forEach(["height","width","top","left"],function(type){
_5d6[type]={start:_5d3[type],end:_5d4[type]};
});
var anim=new dojo.lfx.propertyAnimation(_5d5,_5d6,_5cf,_5d0,{"beforeBegin":function(){
dojo.html.hide(_5cd);
dojo.html.show(_5d5);
},"onEnd":function(){
_5d5.parentNode.removeChild(_5d5);
}});
if(_5d1){
anim.connect("onEnd",function(){
_5d1(_5cd,anim);
});
}
return anim;
};
dojo.lfx.html.highlight=function(_5d9,_5da,_5db,_5dc,_5dd){
_5d9=dojo.lfx.html._byId(_5d9);
var _5de=[];
dojo.lang.forEach(_5d9,function(node){
var _5e0=dojo.html.getBackgroundColor(node);
var bg=dojo.html.getStyle(node,"background-color").toLowerCase();
var _5e2=dojo.html.getStyle(node,"background-image");
var _5e3=(bg=="transparent"||bg=="rgba(0, 0, 0, 0)");
while(_5e0.length>3){
_5e0.pop();
}
var rgb=new dojo.gfx.color.Color(_5da);
var _5e5=new dojo.gfx.color.Color(_5e0);
var anim=dojo.lfx.propertyAnimation(node,{"background-color":{start:rgb,end:_5e5}},_5db,_5dc,{"beforeBegin":function(){
if(_5e2){
node.style.backgroundImage="none";
}
node.style.backgroundColor="rgb("+rgb.toRgb().join(",")+")";
},"onEnd":function(){
if(_5e2){
node.style.backgroundImage=_5e2;
}
if(_5e3){
node.style.backgroundColor="transparent";
}
if(_5dd){
_5dd(node,anim);
}
}});
_5de.push(anim);
});
return dojo.lfx.combine(_5de);
};
dojo.lfx.html.unhighlight=function(_5e7,_5e8,_5e9,_5ea,_5eb){
_5e7=dojo.lfx.html._byId(_5e7);
var _5ec=[];
dojo.lang.forEach(_5e7,function(node){
var _5ee=new dojo.gfx.color.Color(dojo.html.getBackgroundColor(node));
var rgb=new dojo.gfx.color.Color(_5e8);
var _5f0=dojo.html.getStyle(node,"background-image");
var anim=dojo.lfx.propertyAnimation(node,{"background-color":{start:_5ee,end:rgb}},_5e9,_5ea,{"beforeBegin":function(){
if(_5f0){
node.style.backgroundImage="none";
}
node.style.backgroundColor="rgb("+_5ee.toRgb().join(",")+")";
},"onEnd":function(){
if(_5eb){
_5eb(node,anim);
}
}});
_5ec.push(anim);
});
return dojo.lfx.combine(_5ec);
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
var _5f5=getTagName(node);
if(!_5f5){
return "";
}
if((dojo.widget)&&(dojo.widget.tags[_5f5])){
return _5f5;
}
var p=_5f5.indexOf(":");
if(p>=0){
return _5f5;
}
if(_5f5.substr(0,5)=="dojo:"){
return _5f5;
}
if(dojo.render.html.capable&&dojo.render.html.ie&&node.scopeName!="HTML"){
return node.scopeName.toLowerCase()+":"+_5f5;
}
if(_5f5.substr(0,4)=="dojo"){
return "dojo:"+_5f5.substring(4);
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
var _5f8=node.className||node.getAttribute("class");
if((_5f8)&&(_5f8.indexOf)&&(_5f8.indexOf("dojo-")!=-1)){
var _5f9=_5f8.split(" ");
for(var x=0,c=_5f9.length;x<c;x++){
if(_5f9[x].slice(0,5)=="dojo-"){
return "dojo:"+_5f9[x].substr(5).toLowerCase();
}
}
}
}
return "";
}
this.parseElement=function(node,_5fd,_5fe,_5ff){
var _600=getTagName(node);
if(isIE&&_600.indexOf("/")==0){
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
var _602=true;
if(_5fe){
var _603=getDojoTagName(node);
_600=_603||_600;
_602=Boolean(_603);
}
var _604={};
_604[_600]=[];
var pos=_600.indexOf(":");
if(pos>0){
var ns=_600.substring(0,pos);
_604["ns"]=ns;
if((dojo.ns)&&(!dojo.ns.allow(ns))){
_602=false;
}
}
if(_602){
var _607=this.parseAttributes(node);
for(var attr in _607){
if((!_604[_600][attr])||(typeof _604[_600][attr]!="array")){
_604[_600][attr]=[];
}
_604[_600][attr].push(_607[attr]);
}
_604[_600].nodeRef=node;
_604.tagName=_600;
_604.index=_5ff||0;
}
var _608=0;
for(var i=0;i<node.childNodes.length;i++){
var tcn=node.childNodes.item(i);
switch(tcn.nodeType){
case dojo.dom.ELEMENT_NODE:
var ctn=getDojoTagName(tcn)||getTagName(tcn);
if(!_604[ctn]){
_604[ctn]=[];
}
_604[ctn].push(this.parseElement(tcn,true,_5fe,_608));
if((tcn.childNodes.length==1)&&(tcn.childNodes.item(0).nodeType==dojo.dom.TEXT_NODE)){
_604[ctn][_604[ctn].length-1].value=tcn.childNodes.item(0).nodeValue;
}
_608++;
break;
case dojo.dom.TEXT_NODE:
if(node.childNodes.length==1){
_604[_600].push({value:node.childNodes.item(0).nodeValue});
}
break;
default:
break;
}
}
return _604;
};
this.parseAttributes=function(node){
var _60d={};
var atts=node.attributes;
var _60f,i=0;
while((_60f=atts[i++])){
if(isIE){
if(!_60f){
continue;
}
if((typeof _60f=="object")&&(typeof _60f.nodeValue=="undefined")||(_60f.nodeValue==null)||(_60f.nodeValue=="")){
continue;
}
}
var nn=_60f.nodeName.split(":");
nn=(nn.length==2)?nn[1]:_60f.nodeName;
_60d[nn]={value:_60f.nodeValue};
}
return _60d;
};
};
dojo.provide("dojo.lang.declare");
dojo.lang.declare=function(_612,_613,init,_615){
if((dojo.lang.isFunction(_615))||((!_615)&&(!dojo.lang.isFunction(init)))){
var temp=_615;
_615=init;
init=temp;
}
var _617=[];
if(dojo.lang.isArray(_613)){
_617=_613;
_613=_617.shift();
}
if(!init){
init=dojo.evalObjPath(_612,false);
if((init)&&(!dojo.lang.isFunction(init))){
init=null;
}
}
var ctor=dojo.lang.declare._makeConstructor();
var scp=(_613?_613.prototype:null);
if(scp){
scp.prototyping=true;
ctor.prototype=new _613();
scp.prototyping=false;
}
ctor.superclass=scp;
ctor.mixins=_617;
for(var i=0,l=_617.length;i<l;i++){
dojo.lang.extend(ctor,_617[i].prototype);
}
ctor.prototype.initializer=null;
ctor.prototype.declaredClass=_612;
if(dojo.lang.isArray(_615)){
dojo.lang.extend.apply(dojo.lang,[ctor].concat(_615));
}else{
dojo.lang.extend(ctor,(_615)||{});
}
dojo.lang.extend(ctor,dojo.lang.declare._common);
ctor.prototype.constructor=ctor;
ctor.prototype.initializer=(ctor.prototype.initializer)||(init)||(function(){
});
var _61c=dojo.parseObjPath(_612,null,true);
_61c.obj[_61c.prop]=ctor;
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
},_contextMethod:function(_622,_623,args){
var _625,_626=this.___proto;
this.___proto=_622;
try{
_625=_622[_623].apply(this,(args||[]));
}
catch(e){
throw e;
}
finally{
this.___proto=_626;
}
return _625;
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
dojo.ns={namespaces:{},failed:{},loading:{},loaded:{},register:function(name,_62d,_62e,_62f){
if(!_62f||!this.namespaces[name]){
this.namespaces[name]=new dojo.ns.Ns(name,_62d,_62e);
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
dojo.ns.Ns=function(name,_636,_637){
this.name=name;
this.module=_636;
this.resolver=_637;
this._loaded=[];
this._failed=[];
};
dojo.ns.Ns.prototype.resolve=function(name,_639,_63a){
if(!this.resolver||djConfig["skipAutoRequire"]){
return false;
}
var _63b=this.resolver(name,_639);
if((_63b)&&(!this._loaded[_63b])&&(!this._failed[_63b])){
var req=dojo.require;
req(_63b,false,true);
if(dojo.hostenv.findModule(_63b,false)){
this._loaded[_63b]=true;
}else{
if(!_63a){
dojo.raise("dojo.ns.Ns.resolve: module '"+_63b+"' not found after loading via namespace '"+this.name+"'");
}
this._failed[_63b]=true;
}
}
return Boolean(this._loaded[_63b]);
};
dojo.registerNamespace=function(name,_63e,_63f){
dojo.ns.register.apply(dojo.ns,arguments);
};
dojo.registerNamespaceResolver=function(name,_641){
var n=dojo.ns.namespaces[name];
if(n){
n.resolver=_641;
}
};
dojo.registerNamespaceManifest=function(_643,path,name,_646,_647){
dojo.registerModulePath(name,path);
dojo.registerNamespace(name,_646,_647);
};
dojo.registerNamespace("dojo","dojo.widget");
dojo.provide("dojo.widget.Manager");
dojo.widget.manager=new function(){
this.widgets=[];
this.widgetIds=[];
this.topWidgets={};
var _648={};
var _649=[];
this.getUniqueId=function(_64a){
var _64b;
do{
_64b=_64a+"_"+(_648[_64a]!=undefined?++_648[_64a]:_648[_64a]=0);
}while(this.getWidgetById(_64b));
return _64b;
};
this.add=function(_64c){
this.widgets.push(_64c);
if(!_64c.extraArgs["id"]){
_64c.extraArgs["id"]=_64c.extraArgs["ID"];
}
if(_64c.widgetId==""){
if(_64c["id"]){
_64c.widgetId=_64c["id"];
}else{
if(_64c.extraArgs["id"]){
_64c.widgetId=_64c.extraArgs["id"];
}else{
_64c.widgetId=this.getUniqueId(_64c.ns+"_"+_64c.widgetType);
}
}
}
if(this.widgetIds[_64c.widgetId]){
dojo.debug("widget ID collision on ID: "+_64c.widgetId);
}
this.widgetIds[_64c.widgetId]=_64c;
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
this.remove=function(_64e){
if(dojo.lang.isNumber(_64e)){
var tw=this.widgets[_64e].widgetId;
delete this.topWidgets[tw];
delete this.widgetIds[tw];
this.widgets.splice(_64e,1);
}else{
this.removeById(_64e);
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
var _655=(type.indexOf(":")<0?function(x){
return x.widgetType.toLowerCase();
}:function(x){
return x.getNamespacedType();
});
var ret=[];
dojo.lang.forEach(this.widgets,function(x){
if(_655(x)==lt){
ret.push(x);
}
});
return ret;
};
this.getWidgetsByFilter=function(_65a,_65b){
var ret=[];
dojo.lang.every(this.widgets,function(x){
if(_65a(x)){
ret.push(x);
if(_65b){
return false;
}
}
return true;
});
return (_65b?ret[0]:ret);
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
var _661={};
var _662=["dojo.widget"];
for(var i=0;i<_662.length;i++){
_662[_662[i]]=true;
}
this.registerWidgetPackage=function(_664){
if(!_662[_664]){
_662[_664]=true;
_662.push(_664);
}
};
this.getWidgetPackageList=function(){
return dojo.lang.map(_662,function(elt){
return (elt!==true?elt:undefined);
});
};
this.getImplementation=function(_666,_667,_668,ns){
var impl=this.getImplementationName(_666,ns);
if(impl){
var ret=_667?new impl(_667):new impl();
return ret;
}
};
function buildPrefixCache(){
for(var _66c in dojo.render){
if(dojo.render[_66c]["capable"]===true){
var _66d=dojo.render[_66c].prefixes;
for(var i=0;i<_66d.length;i++){
_649.push(_66d[i].toLowerCase());
}
}
}
}
var _66f=function(_670,_671){
if(!_671){
return null;
}
for(var i=0,l=_649.length,_674;i<=l;i++){
_674=(i<l?_671[_649[i]]:_671);
if(!_674){
continue;
}
for(var name in _674){
if(name.toLowerCase()==_670){
return _674[name];
}
}
}
return null;
};
var _676=function(_677,_678){
var _679=dojo.evalObjPath(_678,false);
return (_679?_66f(_677,_679):null);
};
this.getImplementationName=function(_67a,ns){
var _67c=_67a.toLowerCase();
ns=ns||"dojo";
var imps=_661[ns]||(_661[ns]={});
var impl=imps[_67c];
if(impl){
return impl;
}
if(!_649.length){
buildPrefixCache();
}
var _67f=dojo.ns.get(ns);
if(!_67f){
dojo.ns.register(ns,ns+".widget");
_67f=dojo.ns.get(ns);
}
if(_67f){
_67f.resolve(_67a);
}
impl=_676(_67c,_67f.module);
if(impl){
return (imps[_67c]=impl);
}
_67f=dojo.ns.require(ns);
if((_67f)&&(_67f.resolver)){
_67f.resolve(_67a);
impl=_676(_67c,_67f.module);
if(impl){
return (imps[_67c]=impl);
}
}
dojo.deprecated("dojo.widget.Manager.getImplementationName","Could not locate widget implementation for \""+_67a+"\" in \""+_67f.module+"\" registered to namespace \""+_67f.name+"\". "+"Developers must specify correct namespaces for all non-Dojo widgets","0.5");
for(var i=0;i<_662.length;i++){
impl=_676(_67c,_662[i]);
if(impl){
return (imps[_67c]=impl);
}
}
throw new Error("Could not locate widget implementation for \""+_67a+"\" in \""+_67f.module+"\" registered to namespace \""+_67f.name+"\"");
};
this.resizing=false;
this.onWindowResized=function(){
if(this.resizing){
return;
}
try{
this.resizing=true;
for(var id in this.topWidgets){
var _682=this.topWidgets[id];
if(_682.checkSize){
_682.checkSize();
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
var g=function(_687,_688){
dw[(_688||_687)]=h(_687);
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
var _68a=dwm.getAllWidgets.apply(dwm,arguments);
if(arguments.length>0){
return _68a[n];
}
return _68a;
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
var _68c=null;
if(window.getComputedStyle){
var _68d=getComputedStyle(div,"");
_68c=_68d.getPropertyValue("background-image");
}else{
_68c=div.currentStyle.backgroundImage;
}
var _68e=false;
if(_68c!=null&&(_68c=="none"||_68c=="url(invalid-url:)")){
this.accessible=true;
}
dojo.body().removeChild(div);
}
return this.accessible;
},setCheckAccessible:function(_68f){
this.doAccessibleCheck=_68f;
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
var _691=this.children[i];
if(_691.onResized){
_691.onResized();
}
}
},create:function(args,_693,_694,ns){
if(ns){
this.ns=ns;
}
this.satisfyPropertySets(args,_693,_694);
this.mixInProperties(args,_693,_694);
this.postMixInProperties(args,_693,_694);
dojo.widget.manager.add(this);
this.buildRendering(args,_693,_694);
this.initialize(args,_693,_694);
this.postInitialize(args,_693,_694);
this.postCreate(args,_693,_694);
return this;
},destroy:function(_696){
if(this.parent){
this.parent.removeChild(this);
}
this.destroyChildren();
this.uninitialize();
this.destroyRendering(_696);
dojo.widget.manager.removeById(this.widgetId);
},destroyChildren:function(){
var _697;
var i=0;
while(this.children.length>i){
_697=this.children[i];
if(_697 instanceof dojo.widget.Widget){
this.removeChild(_697);
_697.destroy();
continue;
}
i++;
}
},getChildrenOfType:function(type,_69a){
var ret=[];
var _69c=dojo.lang.isFunction(type);
if(!_69c){
type=type.toLowerCase();
}
for(var x=0;x<this.children.length;x++){
if(_69c){
if(this.children[x] instanceof type){
ret.push(this.children[x]);
}
}else{
if(this.children[x].widgetType.toLowerCase()==type){
ret.push(this.children[x]);
}
}
if(_69a){
ret=ret.concat(this.children[x].getChildrenOfType(type,_69a));
}
}
return ret;
},getDescendants:function(){
var _69e=[];
var _69f=[this];
var elem;
while((elem=_69f.pop())){
_69e.push(elem);
if(elem.children){
dojo.lang.forEach(elem.children,function(elem){
_69f.push(elem);
});
}
}
return _69e;
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
var _6a6;
var _6a7=dojo.widget.lcArgsCache[this.widgetType];
if(_6a7==null){
_6a7={};
for(var y in this){
_6a7[((new String(y)).toLowerCase())]=y;
}
dojo.widget.lcArgsCache[this.widgetType]=_6a7;
}
var _6a9={};
for(var x in args){
if(!this[x]){
var y=_6a7[(new String(x)).toLowerCase()];
if(y){
args[y]=args[x];
x=y;
}
}
if(_6a9[x]){
continue;
}
_6a9[x]=true;
if((typeof this[x])!=(typeof _6a6)){
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
var _6ab=args[x].split(";");
for(var y=0;y<_6ab.length;y++){
var si=_6ab[y].indexOf(":");
if((si!=-1)&&(_6ab[y].length>si)){
this[x][_6ab[y].substr(0,si).replace(/^\s+|\s+$/g,"")]=_6ab[y].substr(si+1);
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
},postMixInProperties:function(args,frag,_6af){
},initialize:function(args,frag,_6b2){
return false;
},postInitialize:function(args,frag,_6b5){
return false;
},postCreate:function(args,frag,_6b8){
return false;
},uninitialize:function(){
return false;
},buildRendering:function(args,frag,_6bb){
dojo.unimplemented("dojo.widget.Widget.buildRendering, on "+this.toString()+", ");
return false;
},destroyRendering:function(){
dojo.unimplemented("dojo.widget.Widget.destroyRendering");
return false;
},addedTo:function(_6bc){
},addChild:function(_6bd){
dojo.unimplemented("dojo.widget.Widget.addChild");
return false;
},removeChild:function(_6be){
for(var x=0;x<this.children.length;x++){
if(this.children[x]===_6be){
this.children.splice(x,1);
_6be.parent=null;
break;
}
}
return _6be;
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
dojo.widget.tags["dojo:propertyset"]=function(_6c3,_6c4,_6c5){
var _6c6=_6c4.parseProperties(_6c3["dojo:propertyset"]);
};
dojo.widget.tags["dojo:connect"]=function(_6c7,_6c8,_6c9){
var _6ca=_6c8.parseProperties(_6c7["dojo:connect"]);
};
dojo.widget.buildWidgetFromParseTree=function(type,frag,_6cd,_6ce,_6cf,_6d0){
dojo.a11y.setAccessibleMode();
var _6d1=type.split(":");
_6d1=(_6d1.length==2)?_6d1[1]:type;
var _6d2=_6d0||_6cd.parseProperties(frag[frag["ns"]+":"+_6d1]);
var _6d3=dojo.widget.manager.getImplementation(_6d1,null,null,frag["ns"]);
if(!_6d3){
throw new Error("cannot find \""+type+"\" widget");
}else{
if(!_6d3.create){
throw new Error("\""+type+"\" widget object has no \"create\" method and does not appear to implement *Widget");
}
}
_6d2["dojoinsertionindex"]=_6cf;
var ret=_6d3.create(_6d2,frag,_6ce,frag["ns"]);
return ret;
};
dojo.widget.defineWidget=function(_6d5,_6d6,_6d7,init,_6d9){
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
dojo.widget._defineWidget=function(_6dc,_6dd,_6de,init,_6e0){
var _6e1=_6dc.split(".");
var type=_6e1.pop();
var regx="\\.("+(_6dd?_6dd+"|":"")+dojo.widget.defineWidget.renderers+")\\.";
var r=_6dc.search(new RegExp(regx));
_6e1=(r<0?_6e1.join("."):_6dc.substr(0,r));
dojo.widget.manager.registerWidgetPackage(_6e1);
var pos=_6e1.indexOf(".");
var _6e6=(pos>-1)?_6e1.substring(0,pos):_6e1;
_6e0=(_6e0)||{};
_6e0.widgetType=type;
if((!init)&&(_6e0["classConstructor"])){
init=_6e0.classConstructor;
delete _6e0.classConstructor;
}
dojo.declare(_6dc,_6de,init,_6e0);
};
dojo.provide("dojo.widget.Parse");
dojo.widget.Parse=function(_6e7){
this.propertySetsList=[];
this.fragment=_6e7;
this.createComponents=function(frag,_6e9){
var _6ea=[];
var _6eb=false;
try{
if(frag&&frag.tagName&&(frag!=frag.nodeRef)){
var _6ec=dojo.widget.tags;
var tna=String(frag.tagName).split(";");
for(var x=0;x<tna.length;x++){
var ltn=tna[x].replace(/^\s+|\s+$/g,"").toLowerCase();
frag.tagName=ltn;
var ret;
if(_6ec[ltn]){
_6eb=true;
ret=_6ec[ltn](frag,this,_6e9,frag.index);
_6ea.push(ret);
}else{
if(ltn.indexOf(":")==-1){
ltn="dojo:"+ltn;
}
ret=dojo.widget.buildWidgetFromParseTree(ltn,frag,this,_6e9,frag.index);
if(ret){
_6eb=true;
_6ea.push(ret);
}
}
}
}
}
catch(e){
dojo.debug("dojo.widget.Parse: error:",e);
}
if(!_6eb){
_6ea=_6ea.concat(this.createSubComponents(frag,_6e9));
}
return _6ea;
};
this.createSubComponents=function(_6f1,_6f2){
var frag,_6f4=[];
for(var item in _6f1){
frag=_6f1[item];
if(frag&&typeof frag=="object"&&(frag!=_6f1.nodeRef)&&(frag!=_6f1.tagName)&&(!dojo.dom.isNode(frag))){
_6f4=_6f4.concat(this.createComponents(frag,_6f2));
}
}
return _6f4;
};
this.parsePropertySets=function(_6f6){
return [];
};
this.parseProperties=function(_6f7){
var _6f8={};
for(var item in _6f7){
if((_6f7[item]==_6f7.tagName)||(_6f7[item]==_6f7.nodeRef)){
}else{
var frag=_6f7[item];
if(frag.tagName&&dojo.widget.tags[frag.tagName.toLowerCase()]){
}else{
if(frag[0]&&frag[0].value!=""&&frag[0].value!=null){
try{
if(item.toLowerCase()=="dataprovider"){
var _6fb=this;
this.getDataProvider(_6fb,frag[0].value);
_6f8.dataProvider=this.dataProvider;
}
_6f8[item]=frag[0].value;
var _6fc=this.parseProperties(frag);
for(var _6fd in _6fc){
_6f8[_6fd]=_6fc[_6fd];
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
if(typeof _6f8[item]!="boolean"){
_6f8[item]=true;
}
break;
}
}
}
return _6f8;
};
this.getDataProvider=function(_6fe,_6ff){
dojo.io.bind({url:_6ff,load:function(type,_701){
if(type=="load"){
_6fe.dataProvider=_701;
}
},mimetype:"text/javascript",sync:true});
};
this.getPropertySetById=function(_702){
for(var x=0;x<this.propertySetsList.length;x++){
if(_702==this.propertySetsList[x]["id"][0].value){
return this.propertySetsList[x];
}
}
return "";
};
this.getPropertySetsByType=function(_704){
var _705=[];
for(var x=0;x<this.propertySetsList.length;x++){
var cpl=this.propertySetsList[x];
var cpcc=cpl.componentClass||cpl.componentType||null;
var _709=this.propertySetsList[x]["id"][0].value;
if(cpcc&&(_709==cpcc[0].value)){
_705.push(cpl);
}
}
return _705;
};
this.getPropertySets=function(_70a){
var ppl="dojo:propertyproviderlist";
var _70c=[];
var _70d=_70a.tagName;
if(_70a[ppl]){
var _70e=_70a[ppl].value.split(" ");
for(var _70f in _70e){
if((_70f.indexOf("..")==-1)&&(_70f.indexOf("://")==-1)){
var _710=this.getPropertySetById(_70f);
if(_710!=""){
_70c.push(_710);
}
}else{
}
}
}
return this.getPropertySetsByType(_70d).concat(_70c);
};
this.createComponentFromScript=function(_711,_712,_713,ns){
_713.fastMixIn=true;
var ltn=(ns||"dojo")+":"+_712.toLowerCase();
if(dojo.widget.tags[ltn]){
return [dojo.widget.tags[ltn](_713,this,null,null,_713)];
}
return [dojo.widget.buildWidgetFromParseTree(ltn,_713,this,null,null,_713)];
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
dojo.widget.createWidget=function(name,_718,_719,_71a){
var _71b=false;
var _71c=(typeof name=="string");
if(_71c){
var pos=name.indexOf(":");
var ns=(pos>-1)?name.substring(0,pos):"dojo";
if(pos>-1){
name=name.substring(pos+1);
}
var _71f=name.toLowerCase();
var _720=ns+":"+_71f;
_71b=(dojo.byId(name)&&!dojo.widget.tags[_720]);
}
if((arguments.length==1)&&(_71b||!_71c)){
var xp=new dojo.xml.Parse();
var tn=_71b?dojo.byId(name):name;
return dojo.widget.getParser().createComponents(xp.parseElement(tn,null,true))[0];
}
function fromScript(_723,name,_725,ns){
_725[_720]={dojotype:[{value:_71f}],nodeRef:_723,fastMixIn:true};
_725.ns=ns;
return dojo.widget.getParser().createComponentFromScript(_723,name,_725,ns);
}
_718=_718||{};
var _727=false;
var tn=null;
var h=dojo.render.html.capable;
if(h){
tn=document.createElement("span");
}
if(!_719){
_727=true;
_719=tn;
if(h){
dojo.body().appendChild(_719);
}
}else{
if(_71a){
dojo.dom.insertAtPosition(tn,_719,_71a);
}else{
tn=_719;
}
}
var _729=fromScript(tn,name.toLowerCase(),_718,ns);
if((!_729)||(!_729[0])||(typeof _729[0].widgetType=="undefined")){
throw new Error("createWidget: Creation of \""+name+"\" widget failed.");
}
try{
if(_727&&_729[0].domNode.parentNode){
_729[0].domNode.parentNode.removeChild(_729[0].domNode);
}
}
catch(e){
dojo.debug(e);
}
return _729[0];
};
dojo.provide("dojo.widget.DomWidget");
dojo.widget._cssFiles={};
dojo.widget._cssStrings={};
dojo.widget._templateCache={};
dojo.widget.defaultStrings={dojoRoot:dojo.hostenv.getBaseScriptUri(),dojoWidgetModuleUri:dojo.uri.moduleUri("dojo.widget"),baseScriptUri:dojo.hostenv.getBaseScriptUri()};
dojo.widget.fillFromTemplateCache=function(obj,_72b,_72c,_72d){
var _72e=_72b||obj.templatePath;
var _72f=dojo.widget._templateCache;
if(!_72e&&!obj["widgetType"]){
do{
var _730="__dummyTemplate__"+dojo.widget._templateCache.dummyCount++;
}while(_72f[_730]);
obj.widgetType=_730;
}
var wt=_72e?_72e.toString():obj.widgetType;
var ts=_72f[wt];
if(!ts){
_72f[wt]={"string":null,"node":null};
if(_72d){
ts={};
}else{
ts=_72f[wt];
}
}
if((!obj.templateString)&&(!_72d)){
obj.templateString=_72c||ts["string"];
}
if(obj.templateString){
obj.templateString=this._sanitizeTemplateString(obj.templateString);
}
if((!obj.templateNode)&&(!_72d)){
obj.templateNode=ts["node"];
}
if((!obj.templateNode)&&(!obj.templateString)&&(_72e)){
var _733=this._sanitizeTemplateString(dojo.hostenv.getText(_72e));
obj.templateString=_733;
if(!_72d){
_72f[wt]["string"]=_733;
}
}
if((!ts["string"])&&(!_72d)){
ts.string=obj.templateString;
}
};
dojo.widget._sanitizeTemplateString=function(_734){
if(_734){
_734=_734.replace(/^\s*<\?xml(\s)+version=[\'\"](\d)*.(\d)*[\'\"](\s)*\?>/im,"");
var _735=_734.match(/<body[^>]*>\s*([\s\S]+)\s*<\/body>/im);
if(_735){
_734=_735[1];
}
}else{
_734="";
}
return _734;
};
dojo.widget._templateCache.dummyCount=0;
dojo.widget.attachProperties=["dojoAttachPoint","id"];
dojo.widget.eventAttachProperty="dojoAttachEvent";
dojo.widget.onBuildProperty="dojoOnBuild";
dojo.widget.waiNames=["waiRole","waiState"];
dojo.widget.wai={waiRole:{name:"waiRole","namespace":"http://www.w3.org/TR/xhtml2",alias:"x2",prefix:"wairole:"},waiState:{name:"waiState","namespace":"http://www.w3.org/2005/07/aaa",alias:"aaa",prefix:""},setAttr:function(node,ns,attr,_739){
if(dojo.render.html.ie){
node.setAttribute(this[ns].alias+":"+attr,this[ns].prefix+_739);
}else{
node.setAttributeNS(this[ns]["namespace"],attr,this[ns].prefix+_739);
}
},getAttr:function(node,ns,attr){
if(dojo.render.html.ie){
return node.getAttribute(this[ns].alias+":"+attr);
}else{
return node.getAttributeNS(this[ns]["namespace"],attr);
}
},removeAttr:function(node,ns,attr){
var _740=true;
if(dojo.render.html.ie){
_740=node.removeAttribute(this[ns].alias+":"+attr);
}else{
node.removeAttributeNS(this[ns]["namespace"],attr);
}
return _740;
}};
dojo.widget.attachTemplateNodes=function(_741,_742,_743){
var _744=dojo.dom.ELEMENT_NODE;
function trim(str){
return str.replace(/^\s+|\s+$/g,"");
}
if(!_741){
_741=_742.domNode;
}
if(_741.nodeType!=_744){
return;
}
var _746=_741.all||_741.getElementsByTagName("*");
var _747=_742;
for(var x=-1;x<_746.length;x++){
var _749=(x==-1)?_741:_746[x];
var _74a=[];
if(!_742.widgetsInTemplate||!_749.getAttribute("dojoType")){
for(var y=0;y<this.attachProperties.length;y++){
var _74c=_749.getAttribute(this.attachProperties[y]);
if(_74c){
_74a=_74c.split(";");
for(var z=0;z<_74a.length;z++){
if(dojo.lang.isArray(_742[_74a[z]])){
_742[_74a[z]].push(_749);
}else{
_742[_74a[z]]=_749;
}
}
break;
}
}
var _74e=_749.getAttribute(this.eventAttachProperty);
if(_74e){
var evts=_74e.split(";");
for(var y=0;y<evts.length;y++){
if((!evts[y])||(!evts[y].length)){
continue;
}
var _750=null;
var tevt=trim(evts[y]);
if(evts[y].indexOf(":")>=0){
var _752=tevt.split(":");
tevt=trim(_752[0]);
_750=trim(_752[1]);
}
if(!_750){
_750=tevt;
}
var tf=function(){
var ntf=new String(_750);
return function(evt){
if(_747[ntf]){
_747[ntf](dojo.event.browser.fixEvent(evt,this));
}
};
}();
dojo.event.browser.addListener(_749,tevt,tf,false,true);
}
}
for(var y=0;y<_743.length;y++){
var _756=_749.getAttribute(_743[y]);
if((_756)&&(_756.length)){
var _750=null;
var _757=_743[y].substr(4);
_750=trim(_756);
var _758=[_750];
if(_750.indexOf(";")>=0){
_758=dojo.lang.map(_750.split(";"),trim);
}
for(var z=0;z<_758.length;z++){
if(!_758[z].length){
continue;
}
var tf=function(){
var ntf=new String(_758[z]);
return function(evt){
if(_747[ntf]){
_747[ntf](dojo.event.browser.fixEvent(evt,this));
}
};
}();
dojo.event.browser.addListener(_749,_757,tf,false,true);
}
}
}
}
var _75b=_749.getAttribute(this.templateProperty);
if(_75b){
_742[_75b]=_749;
}
dojo.lang.forEach(dojo.widget.waiNames,function(name){
var wai=dojo.widget.wai[name];
var val=_749.getAttribute(wai.name);
if(val){
if(val.indexOf("-")==-1){
dojo.widget.wai.setAttr(_749,wai.name,"role",val);
}else{
var _75f=val.split("-");
dojo.widget.wai.setAttr(_749,wai.name,_75f[0],_75f[1]);
}
}
},this);
var _760=_749.getAttribute(this.onBuildProperty);
if(_760){
eval("var node = baseNode; var widget = targetObj; "+_760);
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
},{templateNode:null,templateString:null,templateCssString:null,preventClobber:false,domNode:null,containerNode:null,widgetsInTemplate:false,addChild:function(_768,_769,pos,ref,_76c){
if(!this.isContainer){
dojo.debug("dojo.widget.DomWidget.addChild() attempted on non-container widget");
return null;
}else{
if(_76c==undefined){
_76c=this.children.length;
}
this.addWidgetAsDirectChild(_768,_769,pos,ref,_76c);
this.registerChild(_768,_76c);
}
return _768;
},addWidgetAsDirectChild:function(_76d,_76e,pos,ref,_771){
if((!this.containerNode)&&(!_76e)){
this.containerNode=this.domNode;
}
var cn=(_76e)?_76e:this.containerNode;
if(!pos){
pos="after";
}
if(!ref){
if(!cn){
cn=dojo.body();
}
ref=cn.lastChild;
}
if(!_771){
_771=0;
}
_76d.domNode.setAttribute("dojoinsertionindex",_771);
if(!ref){
cn.appendChild(_76d.domNode);
}else{
if(pos=="insertAtIndex"){
dojo.dom.insertAtIndex(_76d.domNode,ref.parentNode,_771);
}else{
if((pos=="after")&&(ref===cn.lastChild)){
cn.appendChild(_76d.domNode);
}else{
dojo.dom.insertAtPosition(_76d.domNode,cn,pos);
}
}
}
},registerChild:function(_773,_774){
_773.dojoInsertionIndex=_774;
var idx=-1;
for(var i=0;i<this.children.length;i++){
if(this.children[i].dojoInsertionIndex<=_774){
idx=i;
}
}
this.children.splice(idx+1,0,_773);
_773.parent=this;
_773.addedTo(this,idx+1);
delete dojo.widget.manager.topWidgets[_773.widgetId];
},removeChild:function(_777){
dojo.dom.removeNode(_777.domNode);
return dojo.widget.DomWidget.superclass.removeChild.call(this,_777);
},getFragNodeRef:function(frag){
if(!frag){
return null;
}
if(!frag[this.getNamespacedType()]){
dojo.raise("Error: no frag for widget type "+this.getNamespacedType()+", id "+this.widgetId+" (maybe a widget has set it's type incorrectly)");
}
return frag[this.getNamespacedType()]["nodeRef"];
},postInitialize:function(args,frag,_77b){
var _77c=this.getFragNodeRef(frag);
if(_77b&&(_77b.snarfChildDomOutput||!_77c)){
_77b.addWidgetAsDirectChild(this,"","insertAtIndex","",args["dojoinsertionindex"],_77c);
}else{
if(_77c){
if(this.domNode&&(this.domNode!==_77c)){
this._sourceNodeRef=dojo.dom.replaceNode(_77c,this.domNode);
}
}
}
if(_77b){
_77b.registerChild(this,args.dojoinsertionindex);
}else{
dojo.widget.manager.topWidgets[this.widgetId]=this;
}
if(this.widgetsInTemplate){
var _77d=new dojo.xml.Parse();
var _77e;
var _77f=this.domNode.getElementsByTagName("*");
for(var i=0;i<_77f.length;i++){
if(_77f[i].getAttribute("dojoAttachPoint")=="subContainerWidget"){
_77e=_77f[i];
}
if(_77f[i].getAttribute("dojoType")){
_77f[i].setAttribute("isSubWidget",true);
}
}
if(this.isContainer&&!this.containerNode){
if(_77e){
var src=this.getFragNodeRef(frag);
if(src){
dojo.dom.moveChildren(src,_77e);
frag["dojoDontFollow"]=true;
}
}else{
dojo.debug("No subContainerWidget node can be found in template file for widget "+this);
}
}
var _782=_77d.parseElement(this.domNode,null,true);
dojo.widget.getParser().createSubComponents(_782,this);
var _783=[];
var _784=[this];
var w;
while((w=_784.pop())){
for(var i=0;i<w.children.length;i++){
var _786=w.children[i];
if(_786._processedSubWidgets||!_786.extraArgs["issubwidget"]){
continue;
}
_783.push(_786);
if(_786.isContainer){
_784.push(_786);
}
}
}
for(var i=0;i<_783.length;i++){
var _787=_783[i];
if(_787._processedSubWidgets){
dojo.debug("This should not happen: widget._processedSubWidgets is already true!");
return;
}
_787._processedSubWidgets=true;
if(_787.extraArgs["dojoattachevent"]){
var evts=_787.extraArgs["dojoattachevent"].split(";");
for(var j=0;j<evts.length;j++){
var _78a=null;
var tevt=dojo.string.trim(evts[j]);
if(tevt.indexOf(":")>=0){
var _78c=tevt.split(":");
tevt=dojo.string.trim(_78c[0]);
_78a=dojo.string.trim(_78c[1]);
}
if(!_78a){
_78a=tevt;
}
if(dojo.lang.isFunction(_787[tevt])){
dojo.event.kwConnect({srcObj:_787,srcFunc:tevt,targetObj:this,targetFunc:_78a});
}else{
alert(tevt+" is not a function in widget "+_787);
}
}
}
if(_787.extraArgs["dojoattachpoint"]){
this[_787.extraArgs["dojoattachpoint"]]=_787;
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
var _790=args["templateCssPath"]||this.templateCssPath;
if(_790&&!dojo.widget._cssFiles[_790.toString()]){
if((!this.templateCssString)&&(_790)){
this.templateCssString=dojo.hostenv.getText(_790);
this.templateCssPath=null;
}
dojo.widget._cssFiles[_790.toString()]=true;
}
if((this["templateCssString"])&&(!dojo.widget._cssStrings[this.templateCssString])){
dojo.html.insertCssText(this.templateCssString,null,_790);
dojo.widget._cssStrings[this.templateCssString]=true;
}
if((!this.preventClobber)&&((this.templatePath)||(this.templateNode)||((this["templateString"])&&(this.templateString.length))||((typeof ts!="undefined")&&((ts["string"])||(ts["node"]))))){
this.buildFromTemplate(args,frag);
}else{
this.domNode=this.getFragNodeRef(frag);
}
this.fillInTemplate(args,frag);
},buildFromTemplate:function(args,frag){
var _793=false;
if(args["templatepath"]){
args["templatePath"]=args["templatepath"];
}
dojo.widget.fillFromTemplateCache(this,args["templatePath"],null,_793);
var ts=dojo.widget._templateCache[this.templatePath?this.templatePath.toString():this.widgetType];
if((ts)&&(!_793)){
if(!this.templateString.length){
this.templateString=ts["string"];
}
if(!this.templateNode){
this.templateNode=ts["node"];
}
}
var _795=false;
var node=null;
var tstr=this.templateString;
if((!this.templateNode)&&(this.templateString)){
_795=this.templateString.match(/\$\{([^\}]+)\}/g);
if(_795){
var hash=this.strings||{};
for(var key in dojo.widget.defaultStrings){
if(dojo.lang.isUndefined(hash[key])){
hash[key]=dojo.widget.defaultStrings[key];
}
}
for(var i=0;i<_795.length;i++){
var key=_795[i];
key=key.substring(2,key.length-1);
var kval=(key.substring(0,5)=="this.")?dojo.lang.getObjPathValue(key.substring(5),this):hash[key];
var _79c;
if((kval)||(dojo.lang.isString(kval))){
_79c=new String((dojo.lang.isFunction(kval))?kval.call(this,key,this.templateString):kval);
while(_79c.indexOf("\"")>-1){
_79c=_79c.replace("\"","&quot;");
}
tstr=tstr.replace(_795[i],_79c);
}
}
}else{
this.templateNode=this.createNodesFromText(this.templateString,true)[0];
if(!_793){
ts.node=this.templateNode;
}
}
}
if((!this.templateNode)&&(!_795)){
dojo.debug("DomWidget.buildFromTemplate: could not create template");
return false;
}else{
if(!_795){
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
},attachTemplateNodes:function(_79e,_79f){
if(!_79e){
_79e=this.domNode;
}
if(!_79f){
_79f=this;
}
return dojo.widget.attachTemplateNodes(_79e,_79f,dojo.widget.getDojoEventsFromStr(this.templateString));
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
dojo.html.getElementWindow=function(_7a0){
return dojo.html.getDocumentWindow(_7a0.ownerDocument);
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
var _7a8=dojo.html.getCursorPosition(e);
with(dojo.html){
var _7a9=getAbsolutePosition(node,true);
var bb=getBorderBox(node);
var _7ab=_7a9.x+(bb.width/2);
var _7ac=_7a9.y+(bb.height/2);
}
with(dojo.html.gravity){
return ((_7a8.x<_7ab?WEST:EAST)|(_7a8.y<_7ac?NORTH:SOUTH));
}
};
dojo.html.gravity.NORTH=1;
dojo.html.gravity.SOUTH=1<<1;
dojo.html.gravity.EAST=1<<2;
dojo.html.gravity.WEST=1<<3;
dojo.html.overElement=function(_7ad,e){
_7ad=dojo.byId(_7ad);
var _7af=dojo.html.getCursorPosition(e);
var bb=dojo.html.getBorderBox(_7ad);
var _7b1=dojo.html.getAbsolutePosition(_7ad,true,dojo.html.boxSizing.BORDER_BOX);
var top=_7b1.y;
var _7b3=top+bb.height;
var left=_7b1.x;
var _7b5=left+bb.width;
return (_7af.x>=left&&_7af.x<=_7b5&&_7af.y>=top&&_7af.y<=_7b3);
};
dojo.html.renderedTextContent=function(node){
node=dojo.byId(node);
var _7b7="";
if(node==null){
return _7b7;
}
for(var i=0;i<node.childNodes.length;i++){
switch(node.childNodes[i].nodeType){
case 1:
case 5:
var _7b9="unknown";
try{
_7b9=dojo.html.getStyle(node.childNodes[i],"display");
}
catch(E){
}
switch(_7b9){
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
_7b7+="\n";
_7b7+=dojo.html.renderedTextContent(node.childNodes[i]);
_7b7+="\n";
break;
case "none":
break;
default:
if(node.childNodes[i].tagName&&node.childNodes[i].tagName.toLowerCase()=="br"){
_7b7+="\n";
}else{
_7b7+=dojo.html.renderedTextContent(node.childNodes[i]);
}
break;
}
break;
case 3:
case 2:
case 4:
var text=node.childNodes[i].nodeValue;
var _7bb="unknown";
try{
_7bb=dojo.html.getStyle(node,"text-transform");
}
catch(E){
}
switch(_7bb){
case "capitalize":
var _7bc=text.split(" ");
for(var i=0;i<_7bc.length;i++){
_7bc[i]=_7bc[i].charAt(0).toUpperCase()+_7bc[i].substring(1);
}
text=_7bc.join(" ");
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
switch(_7bb){
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
if(/\s$/.test(_7b7)){
text.replace(/^\s/,"");
}
break;
}
_7b7+=text;
break;
default:
break;
}
}
return _7b7;
};
dojo.html.createNodesFromText=function(txt,trim){
if(trim){
txt=txt.replace(/^\s+|\s+$/g,"");
}
var tn=dojo.doc().createElement("div");
tn.style.visibility="hidden";
dojo.body().appendChild(tn);
var _7c0="none";
if((/^<t[dh][\s\r\n>]/i).test(txt.replace(/^\s+/))){
txt="<table><tbody><tr>"+txt+"</tr></tbody></table>";
_7c0="cell";
}else{
if((/^<tr[\s\r\n>]/i).test(txt.replace(/^\s+/))){
txt="<table><tbody>"+txt+"</tbody></table>";
_7c0="row";
}else{
if((/^<(thead|tbody|tfoot)[\s\r\n>]/i).test(txt.replace(/^\s+/))){
txt="<table>"+txt+"</table>";
_7c0="section";
}
}
}
tn.innerHTML=txt;
if(tn["normalize"]){
tn.normalize();
}
var _7c1=null;
switch(_7c0){
case "cell":
_7c1=tn.getElementsByTagName("tr")[0];
break;
case "row":
_7c1=tn.getElementsByTagName("tbody")[0];
break;
case "section":
_7c1=tn.getElementsByTagName("table")[0];
break;
default:
_7c1=tn;
break;
}
var _7c2=[];
for(var x=0;x<_7c1.childNodes.length;x++){
_7c2.push(_7c1.childNodes[x].cloneNode(true));
}
tn.style.display="none";
dojo.html.destroyNode(tn);
return _7c2;
};
dojo.html.placeOnScreen=function(node,_7c5,_7c6,_7c7,_7c8,_7c9,_7ca){
if(_7c5 instanceof Array||typeof _7c5=="array"){
_7ca=_7c9;
_7c9=_7c8;
_7c8=_7c7;
_7c7=_7c6;
_7c6=_7c5[1];
_7c5=_7c5[0];
}
if(_7c9 instanceof String||typeof _7c9=="string"){
_7c9=_7c9.split(",");
}
if(!isNaN(_7c7)){
_7c7=[Number(_7c7),Number(_7c7)];
}else{
if(!(_7c7 instanceof Array||typeof _7c7=="array")){
_7c7=[0,0];
}
}
var _7cb=dojo.html.getScroll().offset;
var view=dojo.html.getViewport();
node=dojo.byId(node);
var _7cd=node.style.display;
node.style.display="";
var bb=dojo.html.getBorderBox(node);
var w=bb.width;
var h=bb.height;
node.style.display=_7cd;
if(!(_7c9 instanceof Array||typeof _7c9=="array")){
_7c9=["TL"];
}
var _7d1,_7d2,_7d3=Infinity,_7d4;
for(var _7d5=0;_7d5<_7c9.length;++_7d5){
var _7d6=_7c9[_7d5];
var _7d7=true;
var tryX=_7c5-(_7d6.charAt(1)=="L"?0:w)+_7c7[0]*(_7d6.charAt(1)=="L"?1:-1);
var tryY=_7c6-(_7d6.charAt(0)=="T"?0:h)+_7c7[1]*(_7d6.charAt(0)=="T"?1:-1);
if(_7c8){
tryX-=_7cb.x;
tryY-=_7cb.y;
}
if(tryX<0){
tryX=0;
_7d7=false;
}
if(tryY<0){
tryY=0;
_7d7=false;
}
var x=tryX+w;
if(x>view.width){
x=view.width-w;
_7d7=false;
}else{
x=tryX;
}
x=Math.max(_7c7[0],x)+_7cb.x;
var y=tryY+h;
if(y>view.height){
y=view.height-h;
_7d7=false;
}else{
y=tryY;
}
y=Math.max(_7c7[1],y)+_7cb.y;
if(_7d7){
_7d1=x;
_7d2=y;
_7d3=0;
_7d4=_7d6;
break;
}else{
var dist=Math.pow(x-tryX-_7cb.x,2)+Math.pow(y-tryY-_7cb.y,2);
if(_7d3>dist){
_7d3=dist;
_7d1=x;
_7d2=y;
_7d4=_7d6;
}
}
}
if(!_7ca){
node.style.left=_7d1+"px";
node.style.top=_7d2+"px";
}
return {left:_7d1,top:_7d2,x:_7d1,y:_7d2,dist:_7d3,corner:_7d4};
};
dojo.html.placeOnScreenPoint=function(node,_7de,_7df,_7e0,_7e1){
dojo.deprecated("dojo.html.placeOnScreenPoint","use dojo.html.placeOnScreen() instead","0.5");
return dojo.html.placeOnScreen(node,_7de,_7df,_7e0,_7e1,["TL","TR","BL","BR"]);
};
dojo.html.placeOnScreenAroundElement=function(node,_7e3,_7e4,_7e5,_7e6,_7e7){
var best,_7e9=Infinity;
_7e3=dojo.byId(_7e3);
var _7ea=_7e3.style.display;
_7e3.style.display="";
var mb=dojo.html.getElementBox(_7e3,_7e5);
var _7ec=mb.width;
var _7ed=mb.height;
var _7ee=dojo.html.getAbsolutePosition(_7e3,true,_7e5);
_7e3.style.display=_7ea;
for(var _7ef in _7e6){
var pos,_7f1,_7f2;
var _7f3=_7e6[_7ef];
_7f1=_7ee.x+(_7ef.charAt(1)=="L"?0:_7ec);
_7f2=_7ee.y+(_7ef.charAt(0)=="T"?0:_7ed);
pos=dojo.html.placeOnScreen(node,_7f1,_7f2,_7e4,true,_7f3,true);
if(pos.dist==0){
best=pos;
break;
}else{
if(_7e9>pos.dist){
_7e9=pos.dist;
best=pos;
}
}
}
if(!_7e7){
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
var _7f5=node.parentNode;
var _7f6=_7f5.scrollTop+dojo.html.getBorderBox(_7f5).height;
var _7f7=node.offsetTop+dojo.html.getMarginBox(node).height;
if(_7f6<_7f7){
_7f5.scrollTop+=(_7f7-_7f6);
}else{
if(_7f5.scrollTop>node.offsetTop){
_7f5.scrollTop-=(_7f5.scrollTop-node.offsetTop);
}
}
}
}
};
dojo.provide("dojo.lfx.toggle");
dojo.lfx.toggle.plain={show:function(node,_7f9,_7fa,_7fb){
dojo.html.show(node);
if(dojo.lang.isFunction(_7fb)){
_7fb();
}
},hide:function(node,_7fd,_7fe,_7ff){
dojo.html.hide(node);
if(dojo.lang.isFunction(_7ff)){
_7ff();
}
}};
dojo.lfx.toggle.fade={show:function(node,_801,_802,_803){
dojo.lfx.fadeShow(node,_801,_802,_803).play();
},hide:function(node,_805,_806,_807){
dojo.lfx.fadeHide(node,_805,_806,_807).play();
}};
dojo.lfx.toggle.wipe={show:function(node,_809,_80a,_80b){
dojo.lfx.wipeIn(node,_809,_80a,_80b).play();
},hide:function(node,_80d,_80e,_80f){
dojo.lfx.wipeOut(node,_80d,_80e,_80f).play();
}};
dojo.lfx.toggle.explode={show:function(node,_811,_812,_813,_814){
dojo.lfx.explode(_814||{x:0,y:0,width:0,height:0},node,_811,_812,_813).play();
},hide:function(node,_816,_817,_818,_819){
dojo.lfx.implode(node,_819||{x:0,y:0,width:0,height:0},_816,_817,_818).play();
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
},destroyRendering:function(_820){
try{
if(this.bgIframe){
this.bgIframe.remove();
delete this.bgIframe;
}
if(!_820&&this.domNode){
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
var _824=w||wh.width;
var _825=h||wh.height;
if(this.width==_824&&this.height==_825){
return false;
}
this.width=_824;
this.height=_825;
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
dojo.lang.forEach(this.children,function(_828){
if(_828.checkSize){
_828.checkSize();
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
var _829=dojo.global();
var _82a=dojo.doc();
try{
if(_829["getSelection"]){
if(dojo.render.html.safari){
_829.getSelection().collapse();
}else{
_829.getSelection().removeAllRanges();
}
}else{
if(_82a.selection){
if(_82a.selection.empty){
_82a.selection.empty();
}else{
if(_82a.selection.clear){
_82a.selection.clear();
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
dojo.html.disableSelection=function(_82b){
_82b=dojo.byId(_82b)||dojo.body();
var h=dojo.render.html;
if(h.mozilla){
_82b.style.MozUserSelect="none";
}else{
if(h.safari){
_82b.style.KhtmlUserSelect="none";
}else{
if(h.ie){
_82b.unselectable="on";
}else{
return false;
}
}
}
return true;
};
dojo.html.enableSelection=function(_82d){
_82d=dojo.byId(_82d)||dojo.body();
var h=dojo.render.html;
if(h.mozilla){
_82d.style.MozUserSelect="";
}else{
if(h.safari){
_82d.style.KhtmlUserSelect="";
}else{
if(h.ie){
_82d.unselectable="off";
}else{
return false;
}
}
}
return true;
};
dojo.html.selectElement=function(_82f){
dojo.deprecated("dojo.html.selectElement","replaced by dojo.html.selection.selectElementChildren",0.5);
};
dojo.html.selectInputText=function(_830){
var _831=dojo.global();
var _832=dojo.doc();
_830=dojo.byId(_830);
if(_832["selection"]&&dojo.body()["createTextRange"]){
var _833=_830.createTextRange();
_833.moveStart("character",0);
_833.moveEnd("character",_830.value.length);
_833.select();
}else{
if(_831["getSelection"]){
var _834=_831.getSelection();
_830.setSelectionRange(0,_830.value.length);
}
}
_830.focus();
};
dojo.html.isSelectionCollapsed=function(){
dojo.deprecated("dojo.html.isSelectionCollapsed","replaced by dojo.html.selection.isCollapsed",0.5);
return dojo.html.selection.isCollapsed();
};
dojo.lang.mixin(dojo.html.selection,{getType:function(){
if(dojo.doc()["selection"]){
return dojo.html.selectionType[dojo.doc().selection.type.toUpperCase()];
}else{
var _835=dojo.html.selectionType.TEXT;
var oSel;
try{
oSel=dojo.global().getSelection();
}
catch(e){
}
if(oSel&&oSel.rangeCount==1){
var _837=oSel.getRangeAt(0);
if(_837.startContainer==_837.endContainer&&(_837.endOffset-_837.startOffset)==1&&_837.startContainer.nodeType!=dojo.dom.TEXT_NODE){
_835=dojo.html.selectionType.CONTROL;
}
}
return _835;
}
},isCollapsed:function(){
var _838=dojo.global();
var _839=dojo.doc();
if(_839["selection"]){
return _839.selection.createRange().text=="";
}else{
if(_838["getSelection"]){
var _83a=_838.getSelection();
if(dojo.lang.isString(_83a)){
return _83a=="";
}else{
return _83a.isCollapsed||_83a.toString()=="";
}
}
}
},getSelectedElement:function(){
if(dojo.html.selection.getType()==dojo.html.selectionType.CONTROL){
if(dojo.doc()["selection"]){
var _83b=dojo.doc().selection.createRange();
if(_83b&&_83b.item){
return dojo.doc().selection.createRange().item(0);
}
}else{
var _83c=dojo.global().getSelection();
return _83c.anchorNode.childNodes[_83c.anchorOffset];
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
var _83e=dojo.global().getSelection();
if(_83e){
var node=_83e.anchorNode;
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
var _840=dojo.global().getSelection();
if(_840){
return _840.toString();
}
}
},getSelectedHtml:function(){
if(dojo.doc()["selection"]){
if(dojo.html.selection.getType()==dojo.html.selectionType.CONTROL){
return null;
}
return dojo.doc().selection.createRange().htmlText;
}else{
var _841=dojo.global().getSelection();
if(_841&&_841.rangeCount){
var frag=_841.getRangeAt(0).cloneContents();
var div=document.createElement("div");
div.appendChild(frag);
return div.innerHTML;
}
return null;
}
},hasAncestorElement:function(_844){
return (dojo.html.selection.getAncestorElement.apply(this,arguments)!=null);
},getAncestorElement:function(_845){
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
},selectElement:function(_84a){
var _84b=dojo.global();
var _84c=dojo.doc();
_84a=dojo.byId(_84a);
if(_84c.selection&&dojo.body().createTextRange){
try{
var _84d=dojo.body().createControlRange();
_84d.addElement(_84a);
_84d.select();
}
catch(e){
dojo.html.selection.selectElementChildren(_84a);
}
}else{
if(_84b["getSelection"]){
var _84e=_84b.getSelection();
if(_84e["removeAllRanges"]){
var _84d=_84c.createRange();
_84d.selectNode(_84a);
_84e.removeAllRanges();
_84e.addRange(_84d);
}
}
}
},selectElementChildren:function(_84f){
var _850=dojo.global();
var _851=dojo.doc();
_84f=dojo.byId(_84f);
if(_851.selection&&dojo.body().createTextRange){
var _852=dojo.body().createTextRange();
_852.moveToElementText(_84f);
_852.select();
}else{
if(_850["getSelection"]){
var _853=_850.getSelection();
if(_853["setBaseAndExtent"]){
_853.setBaseAndExtent(_84f,0,_84f,_84f.innerText.length-1);
}else{
if(_853["selectAllChildren"]){
_853.selectAllChildren(_84f);
}
}
}
}
},getBookmark:function(){
var _854;
var _855=dojo.doc();
if(_855["selection"]){
var _856=_855.selection.createRange();
_854=_856.getBookmark();
}else{
var _857;
try{
_857=dojo.global().getSelection();
}
catch(e){
}
if(_857){
var _856=_857.getRangeAt(0);
_854=_856.cloneRange();
}else{
dojo.debug("No idea how to store the current selection for this browser!");
}
}
return _854;
},moveToBookmark:function(_858){
var _859=dojo.doc();
if(_859["selection"]){
var _85a=_859.selection.createRange();
_85a.moveToBookmark(_858);
_85a.select();
}else{
var _85b;
try{
_85b=dojo.global().getSelection();
}
catch(e){
}
if(_85b&&_85b["removeAllRanges"]){
_85b.removeAllRanges();
_85b.addRange(_858);
}else{
dojo.debug("No idea how to restore selection for this browser!");
}
}
},collapse:function(_85c){
if(dojo.global()["getSelection"]){
var _85d=dojo.global().getSelection();
if(_85d.removeAllRanges){
if(_85c){
_85d.collapseToStart();
}else{
_85d.collapseToEnd();
}
}else{
dojo.global().getSelection().collapse(_85c);
}
}else{
if(dojo.doc().selection){
var _85e=dojo.doc().selection.createRange();
_85e.collapse(_85c);
_85e.select();
}
}
},remove:function(){
if(dojo.doc().selection){
var _85f=dojo.doc().selection;
if(_85f.type.toUpperCase()!="NONE"){
_85f.clear();
}
return _85f;
}else{
var _85f=dojo.global().getSelection();
for(var i=0;i<_85f.rangeCount;i++){
_85f.getRangeAt(i).deleteContents();
}
return _85f;
}
}});
dojo.provide("dojo.Deferred");
dojo.Deferred=function(_861){
this.chain=[];
this.id=this._nextId();
this.fired=-1;
this.paused=0;
this.results=[null,null];
this.canceller=_861;
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
var _863=new dojo.Deferred();
_863.callback();
return _863;
},repr:function(){
var _864;
if(this.fired==-1){
_864="unfired";
}else{
if(this.fired==0){
_864="success";
}else{
_864="error";
}
}
return "Deferred("+this.id+", "+_864+")";
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
var _86c=this.getFunctionFromArgs(cb,cbfn);
if(arguments.length>2){
_86c=dojo.lang.curryArguments(null,_86c,arguments,2);
}
return this.addCallbacks(_86c,_86c);
},addCallback:function(cb,cbfn){
var _86f=this.getFunctionFromArgs(cb,cbfn);
if(arguments.length>2){
_86f=dojo.lang.curryArguments(null,_86f,arguments,2);
}
return this.addCallbacks(_86f,null);
},addErrback:function(cb,cbfn){
var _872=this.getFunctionFromArgs(cb,cbfn);
if(arguments.length>2){
_872=dojo.lang.curryArguments(null,_872,arguments,2);
}
return this.addCallbacks(null,_872);
return this.addCallbacks(null,cbfn);
},addCallbacks:function(cb,eb){
this.chain.push([cb,eb]);
if(this.fired>=0){
this._fire();
}
return this;
},_fire:function(){
var _875=this.chain;
var _876=this.fired;
var res=this.results[_876];
var self=this;
var cb=null;
while(_875.length>0&&this.paused==0){
var pair=_875.shift();
var f=pair[_876];
if(f==null){
continue;
}
try{
res=f(res);
_876=((res instanceof Error)?1:0);
if(res instanceof dojo.Deferred){
cb=function(res){
self._continue(res);
};
this._pause();
}
}
catch(err){
_876=1;
res=err;
}
}
this.fired=_876;
this.results[_876]=res;
if((cb)&&(this.paused)){
res.addBoth(cb);
}
}});
dojo.provide("dojo.widget.RichText");
if(!djConfig["useXDomain"]||djConfig["allowXdRichTextSave"]){
if(dojo.hostenv.post_load_){
(function(){
var _87d=dojo.doc().createElement("textarea");
_87d.id="dojo.widget.RichText.savedContent";
_87d.style="display:none;position:absolute;top:-100px;left:-100px;height:3px;width:3px;overflow:hidden;";
dojo.body().appendChild(_87d);
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
},events:["onBlur","onFocus","onKeyPress","onKeyDown","onKeyUp","onClick"],open:function(_882){
if(this.onLoadDeferred.fired>=0){
this.onLoadDeferred=new dojo.Deferred();
}
var h=dojo.render.html;
if(!this.isClosed){
this.close();
}
dojo.event.topic.publish("dojo.widget.RichText::open",this);
this._content="";
if((arguments.length==1)&&(_882["nodeName"])){
this.domNode=_882;
}
if((this.domNode["nodeName"])&&(this.domNode.nodeName.toLowerCase()=="textarea")){
this.textarea=this.domNode;
var html=this._preFilterContent(this.textarea.value);
this.domNode=dojo.doc().createElement("div");
dojo.html.copyStyle(this.domNode,this.textarea);
var _885=dojo.lang.hitch(this,function(){
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
setTimeout(_885,10);
}else{
_885();
}
if(!h.safari){
dojo.html.insertBefore(this.domNode,this.textarea);
}
if(this.textarea.form){
dojo.event.connect("before",this.textarea.form,"onsubmit",dojo.lang.hitch(this,function(){
this.textarea.value=this.getEditorContent();
}));
}
var _886=this;
dojo.event.connect(this,"postCreate",function(){
dojo.html.insertAfter(_886.textarea,_886.domNode);
});
}else{
var html=this._preFilterContent(dojo.string.trim(this.domNode.innerHTML));
}
if(html==""){
html="&nbsp;";
}
var _887=dojo.html.getContentBox(this.domNode);
this._oldHeight=_887.height;
this._oldWidth=_887.width;
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
var _888=dojo.doc().getElementById("dojo.widget.RichText.savedContent");
if(_888.value!=""){
var _889=_888.value.split(this._SEPARATOR);
for(var i=0;i<_889.length;i++){
var data=_889[i].split(":");
if(data[0]==this.saveName){
html=data[1];
_889.splice(i,1);
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
var _88d=["p","pre","address","h1","h2","h3","h4","h5","h6","ol","div","ul"];
var _88e="";
for(var i in _88d){
if(_88d[i].charAt(1)!="l"){
_88e+="<"+_88d[i]+"><span>content</span></"+_88d[i]+">";
}else{
_88e+="<"+_88d[i]+"><li>content</li></"+_88d[i]+">";
}
}
with(this.editNode.style){
position="absolute";
left="-2000px";
top="-2000px";
}
this.editNode.innerHTML=_88e;
var node=this.editNode.firstChild;
while(node){
dojo.withGlobal(this.window,"selectElement",dojo.html.selection,[node.firstChild]);
var _890=node.tagName.toLowerCase();
this._local2NativeFormatNames[_890]=this.queryCommandValue("formatblock");
this._native2LocalFormatNames[this._local2NativeFormatNames[_890]]=_890;
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
},_hasCollapseableMargin:function(_892,side){
if(dojo.html.getPixelValue(_892,"border-"+side+"-width",false)){
return false;
}else{
if(dojo.html.getPixelValue(_892,"padding-"+side,false)){
return false;
}else{
return true;
}
}
},_getContributingMargin:function(_894,_895){
if(_895=="top"){
var _896="previousSibling";
var _897="nextSibling";
var _898="firstChild";
var _899="margin-top";
var _89a="margin-bottom";
}else{
var _896="nextSibling";
var _897="previousSibling";
var _898="lastChild";
var _899="margin-bottom";
var _89a="margin-top";
}
var _89b=dojo.html.getPixelValue(_894,_899,false);
function isSignificantNode(_89c){
return !(_89c.nodeType==3&&dojo.string.isBlank(_89c.data))&&dojo.html.getStyle(_89c,"display")!="none"&&!dojo.html.isPositionAbsolute(_89c);
}
var _89d=0;
var _89e=_894[_898];
while(_89e){
while((!isSignificantNode(_89e))&&_89e[_897]){
_89e=_89e[_897];
}
_89d=Math.max(_89d,dojo.html.getPixelValue(_89e,_899,false));
if(!this._hasCollapseableMargin(_89e,_895)){
break;
}
_89e=_89e[_898];
}
if(!this._hasCollapseableMargin(_894,_895)){
return parseInt(_89d);
}
var _89f=0;
var _8a0=_894[_896];
while(_8a0){
if(isSignificantNode(_8a0)){
_89f=dojo.html.getPixelValue(_8a0,_89a,false);
break;
}
_8a0=_8a0[_896];
}
if(!_8a0){
_89f=dojo.html.getPixelValue(_894.parentNode,_899,false);
}
if(_89d>_89b){
return parseInt(Math.max((_89d-_89b)-_89f,0));
}else{
return 0;
}
},_drawIframe:function(html){
var _8a2=Boolean(dojo.render.html.moz&&(typeof window.XML=="undefined"));
if(!this.iframe){
var _8a3=(new dojo.uri.Uri(dojo.doc().location)).host;
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
this.iframe.src=(djConfig["dojoRichTextFrameUrl"]||dojo.uri.moduleUri("dojo.widget","templates/richtextframe.html"))+((dojo.doc().domain!=_8a3)?("#"+dojo.doc().domain):"");
this.iframe.width=this.inheritWidth?this._oldWidth:"100%";
if(this.height){
this.iframe.style.height=this.height;
}else{
var _8a4=this._oldHeight;
if(this._hasCollapseableMargin(this.domNode,"top")){
_8a4+=this._firstChildContributingMargin;
}
if(this._hasCollapseableMargin(this.domNode,"bottom")){
_8a4+=this._lastChildContributingMargin;
}
this.iframe.height=_8a4;
}
var _8a5=dojo.doc().createElement("div");
_8a5.innerHTML=html;
this.editingArea.appendChild(_8a5);
if(this.relativeImageUrls){
var imgs=_8a5.getElementsByTagName("img");
for(var i=0;i<imgs.length;i++){
imgs[i].src=(new dojo.uri.Uri(dojo.global().location,imgs[i].src)).toString();
}
html=_8a5.innerHTML;
}
var _8a8=dojo.html.firstElement(_8a5);
var _8a9=dojo.html.lastElement(_8a5);
if(_8a8){
_8a8.style.marginTop=this._firstChildContributingMargin+"px";
}
if(_8a9){
_8a9.style.marginBottom=this._lastChildContributingMargin+"px";
}
this.editingArea.appendChild(this.iframe);
if(dojo.render.html.safari){
this.iframe.src=this.iframe.src;
}
var _8aa=false;
var _8ab=dojo.lang.hitch(this,function(){
if(!_8aa){
_8aa=true;
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
var _8ac=(function(_8ad){
return function(_8ae){
return dojo.html.getStyle(_8ad,_8ae);
};
})(this.domNode);
var font=_8ac("font-weight")+" "+_8ac("font-size")+" "+_8ac("font-family");
var _8b0="1.0";
var _8b1=dojo.html.getUnitValue(this.domNode,"line-height");
if(_8b1.value&&_8b1.units==""){
_8b0=_8b1.value;
}
dojo.html.insertCssText("body,html{background:transparent;padding:0;margin:0;}"+"body{top:0;left:0;right:0;"+(((this.height)||(dojo.render.html.opera))?"":"position:fixed;")+"font:"+font+";"+"min-height:"+this.minHeight+";"+"line-height:"+_8b0+"}"+"p{margin: 1em 0 !important;}"+"body > *:first-child{padding-top:0 !important;margin-top:"+this._firstChildContributingMargin+"px !important;}"+"body > *:last-child{padding-bottom:0 !important;margin-bottom:"+this._lastChildContributingMargin+"px !important;}"+"li > ul:-moz-first-node, li > ol:-moz-first-node{padding-top:1.2em;}\n"+"li{min-height:1.2em;}"+"",this.document);
dojo.html.removeNode(_8a5);
this.document.body.innerHTML=html;
if(_8a2||dojo.render.html.safari){
this.document.designMode="on";
}
this.onLoad();
}else{
dojo.html.removeNode(_8a5);
this.editNode.innerHTML=html;
this.onDisplayChanged();
}
});
if(this.editNode){
_8ab();
}else{
if(dojo.render.html.moz){
this.iframe.onload=function(){
setTimeout(_8ab,250);
};
}else{
this.iframe.onload=_8ab;
}
}
},_applyEditingAreaStyleSheets:function(){
var _8b2=[];
if(this.styleSheets){
_8b2=this.styleSheets.split(";");
this.styleSheets="";
}
_8b2=_8b2.concat(this.editingAreaStyleSheets);
this.editingAreaStyleSheets=[];
if(_8b2.length>0){
for(var i=0;i<_8b2.length;i++){
var url=_8b2[i];
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
var _8b8=this.document.createElement("link");
with(_8b8){
rel="stylesheet";
type="text/css";
href=url;
}
head.appendChild(_8b8);
}
},removeStyleSheet:function(uri){
var url=uri.toString();
if(url.charAt(0)=="."||(url.charAt(0)!="/"&&!uri.host)){
url=(new dojo.uri.Uri(dojo.global().location,url)).toString();
}
var _8bb=dojo.lang.find(this.editingAreaStyleSheets,url);
if(_8bb==-1){
dojo.debug("dojo.widget.RichText.removeStyleSheet: Style sheet "+url+" is not applied to the editing area so it can not be removed!");
return;
}
delete this.editingAreaStyleSheets[_8bb];
var _8bc=this.document.getElementsByTagName("link");
for(var i=0;i<_8bc.length;i++){
if(_8bc[i].href==url){
if(dojo.render.html.ie){
_8bc[i].href="";
}
dojo.html.removeNode(_8bc[i]);
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
var _8c1=false;
if(!obj){
try{
obj=dojo.html.createExternalElement(dojo.doc(),"object");
obj.classid="clsid:2D360201-FFF5-11D1-8D03-00A0C959BC0A";
dojo.body().appendChild(obj);
obj.DocumentHTML="<html><head></head><body></body></html>";
}
catch(e){
_8c1=true;
}
}
try{
var _8c2=new ActiveXObject("DEGetBlockFmtNamesParam.DEGetBlockFmtNamesParam");
obj.ExecCommand(this._activeX.command["getblockformatnames"],0,_8c2);
var _8c3=new VBArray(_8c2.Names);
var _8c4=_8c3.toArray();
var _8c5=["p","pre","address","h1","h2","h3","h4","h5","h6","ol","ul","","","","","div"];
for(var i=0;i<_8c5.length;++i){
if(_8c5[i].length>0){
this._local2NativeFormatNames[_8c4[i]]=_8c5[i];
this._native2LocalFormatNames[_8c5[i]]=_8c4[i];
}
}
}
catch(e){
_8c1=true;
}
if(obj&&!this.object){
dojo.body().removeChild(obj);
}
}
return !_8c1;
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
var _8c9=dojo.event.browser.addListener;
var self=this;
dojo.lang.forEach(this.events,function(e){
var l=_8c9(self.document,e.substr(2).toLowerCase(),dojo.lang.hitch(self,e));
if(e=="onBlur"){
var _8cd={unBlur:function(e){
dojo.event.browser.removeListener(doc,"blur",l);
}};
dojo.event.connect("before",self,"close",_8cd,"unBlur");
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
var _8d2=e.ctrlKey?this.KEY_CTRL:0;
if(this._keyHandlers[e.key]){
var _8d3=this._keyHandlers[e.key],i=0,_8d5;
while(_8d5=_8d3[i++]){
if(_8d2==_8d5.modifiers){
e.preventDefault();
_8d5.handler.call(this);
break;
}
}
}
dojo.lang.setTimeout(this,this.onKeyPressed,1,e);
},addKeyHandler:function(key,_8d7,_8d8){
if(!(this._keyHandlers[key] instanceof Array)){
this._keyHandlers[key]=[];
}
this._keyHandlers[key].push({modifiers:_8d7||0,handler:_8d8});
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
var _8e0=cmd.toLowerCase();
if(_8e0=="formatblock"){
if(drh.safari){
_8e0="heading";
}
}else{
if(this.object){
switch(_8e0){
case "createlink":
_8e0="hyperlink";
break;
case "insertimage":
_8e0="image";
break;
}
}else{
if(_8e0=="hilitecolor"&&!drh.mozilla){
_8e0="backcolor";
}
}
}
return _8e0;
},_safariIsLeopard:function(){
var _8e1=false;
if(dojo.render.html.safari){
var tmp=dojo.render.html.UA.split("AppleWebKit/")[1];
var ver=parseFloat(tmp.split(" ")[0]);
if(ver>=420){
_8e1=true;
}
}
return _8e1;
},queryCommandAvailable:function(_8e4){
var ie=1;
var _8e6=1<<1;
var _8e7=1<<2;
var _8e8=1<<3;
var _8e9=1<<4;
var _8ea=this._safariIsLeopard();
function isSupportedBy(_8eb){
return {ie:Boolean(_8eb&ie),mozilla:Boolean(_8eb&_8e6),safari:Boolean(_8eb&_8e7),safari420:Boolean(_8eb&_8e9),opera:Boolean(_8eb&_8e8)};
}
var _8ec=null;
switch(_8e4.toLowerCase()){
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
_8ec=isSupportedBy(_8e6|ie|_8e7|_8e8);
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
_8ec=isSupportedBy(_8e6|ie|_8e8|_8e9);
break;
case "blockdirltr":
case "blockdirrtl":
case "dirltr":
case "dirrtl":
case "inlinedirltr":
case "inlinedirrtl":
_8ec=isSupportedBy(ie);
break;
case "cut":
case "copy":
case "paste":
_8ec=isSupportedBy(ie|_8e6|_8e9);
break;
case "inserttable":
_8ec=isSupportedBy(_8e6|(this.object?ie:0));
break;
case "insertcell":
case "insertcol":
case "insertrow":
case "deletecells":
case "deletecols":
case "deleterows":
case "mergecells":
case "splitcell":
_8ec=isSupportedBy(this.object?ie:0);
break;
default:
return false;
}
return (dojo.render.html.ie&&_8ec.ie)||(dojo.render.html.mozilla&&_8ec.mozilla)||(dojo.render.html.safari&&_8ec.safari)||(_8ea&&_8ec.safari420)||(dojo.render.html.opera&&_8ec.opera);
},execCommand:function(_8ed,_8ee){
var _8ef;
this.focus();
_8ed=this._normalizeCommand(_8ed);
if(_8ee!=undefined){
if(_8ed=="heading"){
throw new Error("unimplemented");
}else{
if(_8ed=="formatblock"){
if(this.object){
_8ee=this._native2LocalFormatNames[_8ee];
}else{
if(dojo.render.html.ie){
_8ee="<"+_8ee+">";
}
}
}
}
}
if(this.object){
switch(_8ed){
case "hilitecolor":
_8ed="setbackcolor";
break;
case "forecolor":
case "backcolor":
case "fontsize":
case "fontname":
_8ed="set"+_8ed;
break;
case "formatblock":
_8ed="setblockformat";
}
if(_8ed=="strikethrough"){
_8ed="inserthtml";
var _8f0=this.document.selection.createRange();
if(!_8f0.htmlText){
return;
}
_8ee=_8f0.htmlText.strike();
}else{
if(_8ed=="inserthorizontalrule"){
_8ed="inserthtml";
_8ee="<hr>";
}
}
if(_8ed=="inserthtml"){
var _8f0=this.document.selection.createRange();
if(this.document.selection.type.toUpperCase()=="CONTROL"){
for(var i=0;i<_8f0.length;i++){
_8f0.item(i).outerHTML=_8ee;
}
}else{
_8f0.pasteHTML(_8ee);
_8f0.select();
}
_8ef=true;
}else{
if(arguments.length==1){
_8ef=this.object.ExecCommand(this._activeX.command[_8ed],this._activeX.ui.noprompt);
}else{
_8ef=this.object.ExecCommand(this._activeX.command[_8ed],this._activeX.ui.noprompt,_8ee);
}
}
}else{
if(_8ed=="inserthtml"){
if(dojo.render.html.ie){
var _8f2=this.document.selection.createRange();
_8f2.pasteHTML(_8ee);
_8f2.select();
return true;
}else{
return this.document.execCommand(_8ed,false,_8ee);
}
}else{
if((_8ed=="unlink")&&(this.queryCommandEnabled("unlink"))&&(dojo.render.html.mozilla)){
var _8f3=this.window.getSelection();
var _8f4=_8f3.getRangeAt(0);
var _8f5=_8f4.startContainer;
var _8f6=_8f4.startOffset;
var _8f7=_8f4.endContainer;
var _8f8=_8f4.endOffset;
var a=dojo.withGlobal(this.window,"getAncestorElement",dojo.html.selection,["a"]);
dojo.withGlobal(this.window,"selectElement",dojo.html.selection,[a]);
_8ef=this.document.execCommand("unlink",false,null);
var _8f4=this.document.createRange();
_8f4.setStart(_8f5,_8f6);
_8f4.setEnd(_8f7,_8f8);
_8f3.removeAllRanges();
_8f3.addRange(_8f4);
return _8ef;
}else{
if((_8ed=="hilitecolor")&&(dojo.render.html.mozilla)){
this.document.execCommand("useCSS",false,false);
_8ef=this.document.execCommand(_8ed,false,_8ee);
this.document.execCommand("useCSS",false,true);
}else{
if((dojo.render.html.ie)&&((_8ed=="backcolor")||(_8ed=="forecolor"))){
_8ee=arguments.length>1?_8ee:null;
_8ef=this.document.execCommand(_8ed,false,_8ee);
}else{
_8ee=arguments.length>1?_8ee:null;
if(_8ee||_8ed!="createlink"){
_8ef=this.document.execCommand(_8ed,false,_8ee);
}
}
}
}
}
}
this.onDisplayChanged();
return _8ef;
},queryCommandEnabled:function(_8fa){
_8fa=this._normalizeCommand(_8fa);
if(this.object){
switch(_8fa){
case "hilitecolor":
_8fa="setbackcolor";
break;
case "forecolor":
case "backcolor":
case "fontsize":
case "fontname":
_8fa="set"+_8fa;
break;
case "formatblock":
_8fa="setblockformat";
break;
case "strikethrough":
_8fa="bold";
break;
case "inserthorizontalrule":
return true;
}
if(typeof this._activeX.command[_8fa]=="undefined"){
return false;
}
var _8fb=this.object.QueryStatus(this._activeX.command[_8fa]);
return ((_8fb!=this._activeX.status.notsupported)&&(_8fb!=this._activeX.status.disabled));
}else{
if(dojo.render.html.mozilla){
if(_8fa=="unlink"){
return dojo.withGlobal(this.window,"hasAncestorElement",dojo.html.selection,["a"]);
}else{
if(_8fa=="inserttable"){
return true;
}
}
}
var elem=(dojo.render.html.ie)?this.document.selection.createRange():this.document;
return elem.queryCommandEnabled(_8fa);
}
},queryCommandState:function(_8fd){
_8fd=this._normalizeCommand(_8fd);
if(this.object){
if(_8fd=="forecolor"){
_8fd="setforecolor";
}else{
if(_8fd=="backcolor"){
_8fd="setbackcolor";
}else{
if(_8fd=="strikethrough"){
return dojo.withGlobal(this.window,"hasAncestorElement",dojo.html.selection,["strike"]);
}else{
if(_8fd=="inserthorizontalrule"){
return false;
}
}
}
}
if(typeof this._activeX.command[_8fd]=="undefined"){
return null;
}
var _8fe=this.object.QueryStatus(this._activeX.command[_8fd]);
return ((_8fe==this._activeX.status.latched)||(_8fe==this._activeX.status.ninched));
}else{
return this.document.queryCommandState(_8fd);
}
},queryCommandValue:function(_8ff){
_8ff=this._normalizeCommand(_8ff);
if(this.object){
switch(_8ff){
case "forecolor":
case "backcolor":
case "fontsize":
case "fontname":
_8ff="get"+_8ff;
return this.object.execCommand(this._activeX.command[_8ff],this._activeX.ui.noprompt);
case "formatblock":
var _900=this.object.execCommand(this._activeX.command["getblockformat"],this._activeX.ui.noprompt);
if(_900){
return this._local2NativeFormatNames[_900];
}
}
}else{
if(dojo.render.html.ie&&_8ff=="formatblock"){
return this._local2NativeFormatNames[this.document.queryCommandValue(_8ff)]||this.document.queryCommandValue(_8ff);
}
return this.document.queryCommandValue(_8ff);
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
var _90c=dojo.html.getBorderBox(this.editNode).height;
if(!_90c){
_90c=dojo.html.getBorderBox(this.document.body).height;
}
if(_90c==0){
dojo.debug("Can not figure out the height of the editing area!");
return;
}
this._lastHeight=_90c;
this.editorObject.style.height=this._lastHeight+"px";
this.window.scrollTo(0,0);
},_saveContent:function(e){
var _90e=dojo.doc().getElementById("dojo.widget.RichText.savedContent");
_90e.value+=this._SEPARATOR+this.saveName+":"+this.getEditorContent();
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
var _911=dojo.global().location.protocol+"//"+dojo.global().location.host;
var _912=dojo.global().location.pathname;
if(_912.match(/\/$/)){
}else{
var _913=_912.split("/");
if(_913.length){
_913.pop();
}
_912=_913.join("/")+"/";
}
var _914=new RegExp("(<img[^>]* src=[\"'])("+_911+"("+_912+")?)","ig");
ec=ec.replace(_914,"$1");
}
return ec;
},close:function(save,_916){
if(this.isClosed){
return false;
}
if(arguments.length==0){
save=true;
}
this._content=this._postFilterContent(this.editNode.innerHTML);
var _917=(this.savedContent!=this._content);
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
return _917;
},destroyRendering:function(){
},destroy:function(){
this.destroyRendering();
if(!this.isClosed){
this.close(false);
}
dojo.widget.RichText.superclass.destroy.call(this);
},connect:function(_919,_91a,_91b){
dojo.event.connect(_919,_91a,this,_91b);
},disconnect:function(_91c,_91d,_91e){
dojo.event.disconnect(_91c,_91d,this,_91e);
},disconnectAllWithRoot:function(_91f){
dojo.deprecated("disconnectAllWithRoot","is deprecated. No need to disconnect manually","0.5");
},_fixContentForMoz:function(html){
html=html.replace(/<strong([ \>])/gi,"<b$1");
html=html.replace(/<\/strong>/gi,"</b>");
html=html.replace(/<em([ \>])/gi,"<i$1");
html=html.replace(/<\/em>/gi,"</i>");
return html;
}});
dojo.provide("dojo.lang.type");
dojo.lang.whatAmI=function(_921){
dojo.deprecated("dojo.lang.whatAmI","use dojo.lang.getType instead","0.5");
return dojo.lang.getType(_921);
};
dojo.lang.whatAmI.custom={};
dojo.lang.getType=function(_922){
try{
if(dojo.lang.isArray(_922)){
return "array";
}
if(dojo.lang.isFunction(_922)){
return "function";
}
if(dojo.lang.isString(_922)){
return "string";
}
if(dojo.lang.isNumber(_922)){
return "number";
}
if(dojo.lang.isBoolean(_922)){
return "boolean";
}
if(dojo.lang.isAlien(_922)){
return "alien";
}
if(dojo.lang.isUndefined(_922)){
return "undefined";
}
for(var name in dojo.lang.whatAmI.custom){
if(dojo.lang.whatAmI.custom[name](_922)){
return name;
}
}
if(dojo.lang.isObject(_922)){
return "object";
}
}
catch(e){
}
return "unknown";
};
dojo.lang.isNumeric=function(_924){
return (!isNaN(_924)&&isFinite(_924)&&(_924!=null)&&!dojo.lang.isBoolean(_924)&&!dojo.lang.isArray(_924)&&!/^\s*$/.test(_924));
};
dojo.lang.isBuiltIn=function(_925){
return (dojo.lang.isArray(_925)||dojo.lang.isFunction(_925)||dojo.lang.isString(_925)||dojo.lang.isNumber(_925)||dojo.lang.isBoolean(_925)||(_925==null)||(_925 instanceof Error)||(typeof _925=="error"));
};
dojo.lang.isPureObject=function(_926){
return ((_926!=null)&&dojo.lang.isObject(_926)&&_926.constructor==Object);
};
dojo.lang.isOfType=function(_927,type,_929){
var _92a=false;
if(_929){
_92a=_929["optional"];
}
if(_92a&&((_927===null)||dojo.lang.isUndefined(_927))){
return true;
}
if(dojo.lang.isArray(type)){
var _92b=type;
for(var i in _92b){
var _92d=_92b[i];
if(dojo.lang.isOfType(_927,_92d)){
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
return dojo.lang.isArray(_927);
case Function:
case "function":
return dojo.lang.isFunction(_927);
case String:
case "string":
return dojo.lang.isString(_927);
case Number:
case "number":
return dojo.lang.isNumber(_927);
case "numeric":
return dojo.lang.isNumeric(_927);
case Boolean:
case "boolean":
return dojo.lang.isBoolean(_927);
case Object:
case "object":
return dojo.lang.isObject(_927);
case "pureobject":
return dojo.lang.isPureObject(_927);
case "builtin":
return dojo.lang.isBuiltIn(_927);
case "alien":
return dojo.lang.isAlien(_927);
case "undefined":
return dojo.lang.isUndefined(_927);
case null:
case "null":
return (_927===null);
case "optional":
dojo.deprecated("dojo.lang.isOfType(value, [type, \"optional\"])","use dojo.lang.isOfType(value, type, {optional: true} ) instead","0.5");
return ((_927===null)||dojo.lang.isUndefined(_927));
default:
if(dojo.lang.isFunction(type)){
return (_927 instanceof type);
}else{
dojo.raise("dojo.lang.isOfType() was passed an invalid type");
}
}
}
dojo.raise("If we get here, it means a bug was introduced above.");
};
dojo.lang.getObject=function(str){
var _92f=str.split("."),i=0,obj=dj_global;
do{
obj=obj[_92f[i++]];
}while(i<_92f.length&&obj);
return (obj!=dj_global)?obj:null;
};
dojo.lang.doesObjectExist=function(str){
var _933=str.split("."),i=0,obj=dj_global;
do{
obj=obj[_933[i++]];
}while(i<_933.length&&obj);
return (obj&&obj!=dj_global);
};
dojo.provide("dojo.lang.assert");
dojo.lang.assert=function(_936,_937){
if(!_936){
var _938="An assert statement failed.\n"+"The method dojo.lang.assert() was called with a 'false' value.\n";
if(_937){
_938+="Here's the assert message:\n"+_937+"\n";
}
throw new Error(_938);
}
};
dojo.lang.assertType=function(_939,type,_93b){
if(dojo.lang.isString(_93b)){
dojo.deprecated("dojo.lang.assertType(value, type, \"message\")","use dojo.lang.assertType(value, type) instead","0.5");
}
if(!dojo.lang.isOfType(_939,type,_93b)){
if(!dojo.lang.assertType._errorMessage){
dojo.lang.assertType._errorMessage="Type mismatch: dojo.lang.assertType() failed.";
}
dojo.lang.assert(false,dojo.lang.assertType._errorMessage);
}
};
dojo.lang.assertValidKeywords=function(_93c,_93d,_93e){
var key;
if(!_93e){
if(!dojo.lang.assertValidKeywords._errorMessage){
dojo.lang.assertValidKeywords._errorMessage="In dojo.lang.assertValidKeywords(), found invalid keyword:";
}
_93e=dojo.lang.assertValidKeywords._errorMessage;
}
if(dojo.lang.isArray(_93d)){
for(key in _93c){
if(!dojo.lang.inArray(_93d,key)){
dojo.lang.assert(false,_93e+" "+key);
}
}
}else{
for(key in _93c){
if(!(key in _93d)){
dojo.lang.assert(false,_93e+" "+key);
}
}
}
};
dojo.provide("dojo.AdapterRegistry");
dojo.AdapterRegistry=function(_940){
this.pairs=[];
this.returnWrappers=_940||false;
};
dojo.lang.extend(dojo.AdapterRegistry,{register:function(name,_942,wrap,_944,_945){
var type=(_945)?"unshift":"push";
this.pairs[type]([name,_942,wrap,_944]);
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
dojo.lang.registerRepr=function(name,_94d,wrap,_94f){
dojo.lang.reprRegistry.register(name,_94d,wrap,_94f);
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
dojo.html.iframeContentWindow=function(_955){
var win=dojo.html.getDocumentWindow(dojo.html.iframeContentDocument(_955))||dojo.html.iframeContentDocument(_955).__parent__||(_955.name&&document.frames[_955.name])||null;
return win;
};
dojo.html.iframeContentDocument=function(_957){
var doc=_957.contentDocument||((_957.contentWindow)&&(_957.contentWindow.document))||((_957.name)&&(document.frames[_957.name])&&(document.frames[_957.name].document))||null;
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
var _95b=dojo.html.getMarginBox(this.domNode);
if(_95b.width==0||_95b.height==0){
dojo.lang.setTimeout(this,this.onResized,100);
return;
}
this.iframe.style.width=_95b.width+"px";
this.iframe.style.height=_95b.height+"px";
}
},size:function(node){
if(!this.iframe){
return;
}
var _95d=dojo.html.toCoordinateObject(node,true,dojo.html.boxSizing.BORDER_BOX);
with(this.iframe.style){
width=_95d.width+"px";
height=_95d.height+"px";
left=_95d.left+"px";
top=_95d.top+"px";
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
},open:function(x,y,_962,_963,_964,_965){
if(this.isShowingNow){
return;
}
if(this.animationInProgress){
this.queueOnAnimationFinish.push(this.open,arguments);
return;
}
this.aboutToShow();
var _966=false,node,_968;
if(typeof x=="object"){
node=x;
_968=_963;
_963=_962;
_962=y;
_966=true;
}
this.parent=_962;
dojo.body().appendChild(this.domNode);
_963=_963||_962["domNode"]||[];
var _969=null;
this.isTopLevel=true;
while(_962){
if(_962!==this&&(_962.setOpenedSubpopup!=undefined&&_962.applyPopupBasicStyle!=undefined)){
_969=_962;
this.isTopLevel=false;
_969.setOpenedSubpopup(this);
break;
}
_962=_962.parent;
}
this.parentPopup=_969;
this.popupIndex=_969?_969.popupIndex+1:1;
if(this.isTopLevel){
var _96a=dojo.html.isNode(_963)?_963:null;
dojo.widget.PopupManager.opened(this,_96a);
}
if(this.isTopLevel&&!dojo.withGlobal(this.openedForWindow||dojo.global(),dojo.html.selection.isCollapsed)){
this._bookmark=dojo.withGlobal(this.openedForWindow||dojo.global(),dojo.html.selection.getBookmark);
}else{
this._bookmark=null;
}
if(_963 instanceof Array){
_963={left:_963[0],top:_963[1],width:0,height:0};
}
with(this.domNode.style){
display="";
zIndex=this.beginZIndex+this.popupIndex;
}
if(_966){
this.move(node,_965,_968);
}else{
this.move(x,y,_965,_964);
}
this.domNode.style.display="none";
this.explodeSrc=_963;
this.show();
this.isShowingNow=true;
},move:function(x,y,_96d,_96e){
var _96f=(typeof x=="object");
if(_96f){
var _970=_96d;
var node=x;
_96d=y;
if(!_970){
_970={"BL":"TL","TL":"BL"};
}
dojo.html.placeOnScreenAroundElement(this.domNode,node,_96d,this.aroundBox,_970);
}else{
if(!_96e){
_96e="TL,TR,BL,BR";
}
dojo.html.placeOnScreen(this.domNode,x,y,_96d,true,_96e);
}
},close:function(_972){
if(_972){
this.domNode.style.display="none";
}
if(this.animationInProgress){
this.queueOnAnimationFinish.push(this.close,[]);
return;
}
this.closeSubpopup(_972);
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
},closeAll:function(_973){
if(this.parentPopup){
this.parentPopup.closeAll(_973);
}else{
this.close(_973);
}
},setOpenedSubpopup:function(_974){
this.currentSubpopup=_974;
},closeSubpopup:function(_975){
if(this.currentSubpopup==null){
return;
}
this.currentSubpopup.close(_975);
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
this.registerAllWindows=function(_979){
if(!_979){
_979=dojo.html.getDocumentWindow(window.top&&window.top.document||window.document);
}
this.registerWin(_979);
for(var i=0;i<_979.frames.length;i++){
try{
var win=dojo.html.getDocumentWindow(_979.frames[i].document);
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
this.opened=function(menu,_980){
if(menu==this.currentMenu){
return;
}
if(this.currentMenu){
this.currentMenu.close();
}
this.currentMenu=menu;
this.currentFocusMenu=menu;
this.currentButton=_980;
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
var _985=dojo.html.getScroll().offset;
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
var _988=this._palettes[this.palette];
for(var i=0;i<_988.length;i++){
var tr=this.domNode.insertRow(-1);
for(var j=0;j<_988[i].length;j++){
if(_988[i][j].length==3){
_988[i][j]=_988[i][j].replace(/(.)(.)(.)/,"$1$1$2$2$3$3");
}
var td=tr.insertCell(-1);
with(td.style){
backgroundColor="#"+_988[i][j];
border="1px solid gray";
width=height="15px";
fontSize="1px";
}
td.color="#"+_988[i][j];
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
},onColorSelect:function(_990){
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
},{isContainer:true,adjustPaths:true,href:"",extractContent:true,parseContent:true,cacheContent:true,preload:false,refreshOnShow:false,handler:"",executeScripts:false,scriptSeparation:true,loadingMessage:"Loading...",isLoaded:false,postCreate:function(args,frag,_993){
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
},_downloadExternalContent:function(url,_997){
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
}},_997));
},_cacheSetting:function(_99d,_99e){
for(var x in this.bindArgs){
if(dojo.lang.isUndefined(_99d[x])){
_99d[x]=this.bindArgs[x];
}
}
if(dojo.lang.isUndefined(_99d.useCache)){
_99d.useCache=_99e;
}
if(dojo.lang.isUndefined(_99d.preventCache)){
_99d.preventCache=!_99e;
}
if(dojo.lang.isUndefined(_99d.mimetype)){
_99d.mimetype="text/html";
}
return _99d;
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
},_runStack:function(_9a3){
var st=this[_9a3];
var err="";
var _9a6=this.scriptScope||window;
for(var i=0;i<st.length;i++){
try{
st[i].call(_9a6);
}
catch(e){
err+="\n"+st[i]+" failed: "+e.description;
}
}
this[_9a3]=[];
if(err.length){
var name=(_9a3=="_onLoadStack")?"addOnLoad":"addOnUnLoad";
this._handleDefaults(name+" failure\n "+err,"onExecError","debug");
}
},addOnLoad:function(obj,func){
this._pushOnStack(this._onLoadStack,obj,func);
},addOnUnload:function(obj,func){
this._pushOnStack(this._onUnloadStack,obj,func);
},addOnUnLoad:function(){
dojo.deprecated(this.widgetType+".addOnUnLoad, use addOnUnload instead. (lowercased Load)",0.5);
this.addOnUnload.apply(this,arguments);
},_pushOnStack:function(_9ad,obj,func){
if(typeof func=="undefined"){
_9ad.push(obj);
}else{
_9ad.push(function(){
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
},_handleDefaults:function(e,_9b7,_9b8){
if(!_9b7){
_9b7="onContentError";
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
this[_9b7](e);
if(e.returnValue){
switch(_9b8){
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
var _9bb=[],_9bc=[],tmp=[];
var _9be=[],_9bf=[],attr=[],_9c1=[];
var str="",path="",fix="",_9c5="",tag="",_9c7="";
if(!url){
url="./";
}
if(s){
var _9c8=/<title[^>]*>([\s\S]*?)<\/title>/i;
while(_9be=_9c8.exec(s)){
_9bb.push(_9be[1]);
s=s.substring(0,_9be.index)+s.substr(_9be.index+_9be[0].length);
}
if(this.adjustPaths){
var _9c9=/<[a-z][a-z0-9]*[^>]*\s(?:(?:src|href|style)=[^>])+[^>]*>/i;
var _9ca=/\s(src|href|style)=(['"]?)([\w()\[\]\/.,\\'"-:;#=&?\s@]+?)\2/i;
var _9cb=/^(?:[#]|(?:(?:https?|ftps?|file|javascript|mailto|news):))/;
while(tag=_9c9.exec(s)){
str+=s.substring(0,tag.index);
s=s.substring((tag.index+tag[0].length),s.length);
tag=tag[0];
_9c5="";
while(attr=_9ca.exec(tag)){
path="";
_9c7=attr[3];
switch(attr[1].toLowerCase()){
case "src":
case "href":
if(_9cb.exec(_9c7)){
path=_9c7;
}else{
path=(new dojo.uri.Uri(url,_9c7).toString());
}
break;
case "style":
path=dojo.html.fixPathsInCssText(_9c7,url);
break;
default:
path=_9c7;
}
fix=" "+attr[1]+"="+attr[2]+path+attr[2];
_9c5+=tag.substring(0,attr.index)+fix;
tag=tag.substring((attr.index+attr[0].length),tag.length);
}
str+=_9c5+tag;
}
s=str+s;
}
_9c8=/(?:<(style)[^>]*>([\s\S]*?)<\/style>|<link ([^>]*rel=['"]?stylesheet['"]?[^>]*)>)/i;
while(_9be=_9c8.exec(s)){
if(_9be[1]&&_9be[1].toLowerCase()=="style"){
_9c1.push(dojo.html.fixPathsInCssText(_9be[2],url));
}else{
if(attr=_9be[3].match(/href=(['"]?)([^'">]*)\1/i)){
_9c1.push({path:attr[2]});
}
}
s=s.substring(0,_9be.index)+s.substr(_9be.index+_9be[0].length);
}
var _9c8=/<script([^>]*)>([\s\S]*?)<\/script>/i;
var _9cc=/src=(['"]?)([^"']*)\1/i;
var _9cd=/.*(\bdojo\b\.js(?:\.uncompressed\.js)?)$/;
var _9ce=/(?:var )?\bdjConfig\b(?:[\s]*=[\s]*\{[^}]+\}|\.[\w]*[\s]*=[\s]*[^;\n]*)?;?|dojo\.hostenv\.writeIncludes\(\s*\);?/g;
var _9cf=/dojo\.(?:(?:require(?:After)?(?:If)?)|(?:widget\.(?:manager\.)?registerWidgetPackage)|(?:(?:hostenv\.)?setModulePrefix|registerModulePath)|defineNamespace)\((['"]).*?\1\)\s*;?/;
while(_9be=_9c8.exec(s)){
if(this.executeScripts&&_9be[1]){
if(attr=_9cc.exec(_9be[1])){
if(_9cd.exec(attr[2])){
dojo.debug("Security note! inhibit:"+attr[2]+" from  being loaded again.");
}else{
_9bc.push({path:attr[2]});
}
}
}
if(_9be[2]){
var sc=_9be[2].replace(_9ce,"");
if(!sc){
continue;
}
while(tmp=_9cf.exec(sc)){
_9bf.push(tmp[0]);
sc=sc.substring(0,tmp.index)+sc.substr(tmp.index+tmp[0].length);
}
if(this.executeScripts){
_9bc.push(sc);
}
}
s=s.substr(0,_9be.index)+s.substr(_9be.index+_9be[0].length);
}
if(this.extractContent){
_9be=s.match(/<body[^>]*>\s*([\s\S]+)\s*<\/body>/im);
if(_9be){
s=_9be[1];
}
}
if(this.executeScripts&&this.scriptSeparation){
var _9c8=/(<[a-zA-Z][a-zA-Z0-9]*\s[^>]*?\S=)((['"])[^>]*scriptScope[^>]*>)/;
var _9d1=/([\s'";:\(])scriptScope(.*)/;
str="";
while(tag=_9c8.exec(s)){
tmp=((tag[3]=="'")?"\"":"'");
fix="";
str+=s.substring(0,tag.index)+tag[1];
while(attr=_9d1.exec(tag[2])){
tag[2]=tag[2].substring(0,attr.index)+attr[1]+"dojo.widget.byId("+tmp+this.widgetId+tmp+").scriptScope"+attr[2];
}
str+=tag[2];
s=s.substr(tag.index+tag[0].length);
}
s=str+s;
}
}
return {"xml":s,"styles":_9c1,"titles":_9bb,"requires":_9bf,"scripts":_9bc,"url":url};
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
var _9d7=this;
function asyncParse(){
if(_9d7.executeScripts){
_9d7._executeScripts(data.scripts);
}
if(_9d7.parseContent){
var node=_9d7.containerNode||_9d7.domNode;
var _9d9=new dojo.xml.Parse();
var frag=_9d9.parseElement(node,null,true);
dojo.widget.getParser().createSubComponents(frag,_9d7);
}
_9d7.onResized();
_9d7.onLoad();
}
if(dojo.hostenv.isXDomain&&data.requires.length){
dojo.addOnLoad(asyncParse);
}else{
asyncParse();
}
}
},setHandler:function(_9db){
var fcn=dojo.lang.isFunction(_9db)?_9db:window[_9db];
if(!dojo.lang.isFunction(fcn)){
this._handleDefaults("Unable to set handler, '"+_9db+"' not a function.","onExecError",true);
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
},_executeScripts:function(_9de){
var self=this;
var tmp="",code="";
for(var i=0;i<_9de.length;i++){
if(_9de[i].path){
dojo.io.bind(this._cacheSetting({"url":_9de[i].path,"load":function(type,_9e4){
dojo.lang.hitch(self,tmp=";"+_9e4);
},"error":function(type,_9e6){
_9e6.text=type+" downloading remote script";
self._handleDefaults.call(self,_9e6,"onExecError","debug");
},"mimetype":"text/plain","sync":true},this.cacheContent));
code+=tmp;
}else{
code+=_9de[i];
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
},{create:function(node,_9f4,_9f5){
this._domNode=node;
var cmd=_9f4.parent.getCommand(this._name);
if(cmd){
this._domNode.title=cmd.getText();
}
this.disableSelection(this._domNode);
this._parentToolbar=_9f4;
dojo.event.connect(this._domNode,"onclick",this,"onClick");
if(!_9f5){
dojo.event.connect(this._domNode,"onmouseover",this,"onMouseOver");
dojo.event.connect(this._domNode,"onmouseout",this,"onMouseOut");
}
},disableSelection:function(_9f7){
dojo.html.disableSelection(_9f7);
var _9f8=_9f7.all||_9f7.getElementsByTagName("*");
for(var x=0;x<_9f8.length;x++){
dojo.html.disableSelection(_9f8[x]);
}
},onMouseOver:function(){
var _9fa=dojo.widget.Editor2Manager.getCurrentInstance();
if(_9fa){
var _9fb=_9fa.getCommand(this._name);
if(_9fb&&_9fb.getState()!=dojo.widget.Editor2Manager.commandState.Disabled){
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
var _9fd=dojo.widget.Editor2Manager.getCurrentInstance();
if(_9fd){
var _9fe=_9fd.getCommand(this._name);
if(_9fe){
_9fe.execute();
}
}
}
},refreshState:function(){
var _9ff=dojo.widget.Editor2Manager.getCurrentInstance();
var em=dojo.widget.Editor2Manager;
if(_9ff){
var _a01=_9ff.getCommand(this._name);
if(_a01){
var _a02=_a01.getState();
if(_a02!=this._lastState){
switch(_a02){
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
this._lastState=_a02;
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
},setColor:function(_a03){
this._dropdown.close();
var _a04=dojo.widget.Editor2Manager.getCurrentInstance();
if(_a04){
var _a05=_a04.getCommand(this._name);
if(_a05){
_a05.execute(_a03);
}
}
}});
dojo.declare("dojo.widget.Editor2ToolbarFormatBlockPlainSelect",dojo.widget.Editor2ToolbarButton,{create:function(node,_a07){
this._domNode=node;
this._parentToolbar=_a07;
this._domNode=node;
this.disableSelection(this._domNode);
dojo.event.connect(this._domNode,"onchange",this,"onChange");
},destroy:function(){
this._domNode=null;
},onChange:function(){
if(this._parentToolbar.checkAvailability()){
var sv=this._domNode.value.toLowerCase();
var _a09=dojo.widget.Editor2Manager.getCurrentInstance();
if(_a09){
var _a0a=_a09.getCommand(this._name);
if(_a0a){
_a0a.execute(sv);
}
}
}
},refreshState:function(){
if(this._domNode){
dojo.widget.Editor2ToolbarFormatBlockPlainSelect.superclass.refreshState.call(this);
var _a0b=dojo.widget.Editor2Manager.getCurrentInstance();
if(_a0b){
var _a0c=_a0b.getCommand(this._name);
if(_a0c){
var _a0d=_a0c.getValue();
if(!_a0d){
_a0d="";
}
dojo.lang.forEach(this._domNode.options,function(item){
if(item.value.toLowerCase()==_a0d.toLowerCase()){
item.selected=true;
}
});
}
}
}
}});
dojo.declare("dojo.widget.Editor2ToolbarComboItem",dojo.widget.Editor2ToolbarDropDownButton,{href:null,create:function(node,_a10){
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
var _a15=dojo.widget.Editor2Manager.getCurrentInstance();
if(_a15){
var _a16=_a15.getCommand(this._name);
if(_a16){
_a16.execute(name);
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
var _a19=this._contentPane.domNode.all||this._contentPane.domNode.getElementsByTagName("*");
this._blockNames={};
this._blockDisplayNames={};
for(var x=0;x<_a19.length;x++){
var node=_a19[x];
dojo.html.disableSelection(node);
var name=node.getAttribute("dropDownItemName");
if(name){
this._blockNames[name]=node;
var _a1d=node.getElementsByTagName(name);
this._blockDisplayNames[name]=_a1d[_a1d.length-1].innerHTML;
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
var _a1f=dojo.widget.Editor2Manager.getCurrentInstance();
if(_a1f){
var _a20=_a1f.getCommand(this._name);
if(_a20){
var _a21=_a20.getValue();
if(_a21==this._lastSelectedFormat&&this._blockDisplayNames){
return this._lastState;
}
this._lastSelectedFormat=_a21;
var _a22=this._domNode.getElementsByTagName("label")[0];
var _a23=false;
if(this._blockDisplayNames){
for(var name in this._blockDisplayNames){
if(name==_a21){
_a22.innerHTML=this._blockDisplayNames[name];
_a23=true;
break;
}
}
if(!_a23){
_a22.innerHTML="&nbsp;";
}
}
}
}
}
return this._lastState;
}});
dojo.declare("dojo.widget.Editor2ToolbarFontSizeSelect",dojo.widget.Editor2ToolbarComboItem,{href:dojo.uri.moduleUri("dojo.widget","templates/Editor2/EditorToolbar_FontSize.html"),setup:function(){
dojo.widget.Editor2ToolbarFormatBlockSelect.superclass.setup.call(this);
var _a25=this._contentPane.domNode.all||this._contentPane.domNode.getElementsByTagName("*");
this._fontsizes={};
this._fontSizeDisplayNames={};
for(var x=0;x<_a25.length;x++){
var node=_a25[x];
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
var _a2a=dojo.widget.Editor2Manager.getCurrentInstance();
if(_a2a){
var _a2b=_a2a.getCommand(this._name);
if(_a2b){
var size=_a2b.getValue();
if(size==this._lastSelectedSize&&this._fontSizeDisplayNames){
return this._lastState;
}
this._lastSelectedSize=size;
var _a2d=this._domNode.getElementsByTagName("label")[0];
var _a2e=false;
if(this._fontSizeDisplayNames){
for(var name in this._fontSizeDisplayNames){
if(name==size){
_a2d.innerHTML=this._fontSizeDisplayNames[name];
_a2e=true;
break;
}
}
if(!_a2e){
_a2d.innerHTML="&nbsp;";
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
},{templatePath:dojo.uri.moduleUri("dojo.widget","templates/EditorToolbar.html"),templateCssPath:dojo.uri.moduleUri("dojo.widget","templates/EditorToolbar.css"),ToolbarLatchedItemStyle:"ToolbarButtonLatched",ToolbarEnabledItemStyle:"ToolbarButtonEnabled",ToolbarDisabledItemStyle:"ToolbarButtonDisabled",ToolbarHighlightedItemStyle:"ToolbarButtonHighlighted",ToolbarHighlightedSelectStyle:"ToolbarSelectHighlighted",ToolbarHighlightedSelectItemStyle:"ToolbarSelectHighlightedItem",postCreate:function(){
var _a30=dojo.html.getElementsByClass("dojoEditorToolbarItem",this.domNode);
this.items={};
for(var x=0;x<_a30.length;x++){
var node=_a30[x];
var _a33=node.getAttribute("dojoETItemName");
if(_a33){
var item=dojo.widget.Editor2ToolbarItemManager.getToolbarItem(_a33);
if(item){
item.create(node,this);
this.items[_a33.toLowerCase()]=item;
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
var _a36=dojo.widget.Editor2Manager.getCurrentInstance();
if(this.shareGroup==_a36.toolbarGroup){
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
dojo.uri.cache={_cache:{},set:function(uri,_a39){
this._cache[uri.toString()]=_a39;
return uri;
},remove:function(uri){
delete this._cache[uri.toString()];
},get:function(uri){
var key=uri.toString();
var _a3d=this._cache[key];
if(!_a3d){
_a3d=dojo.hostenv.getText(key);
if(_a3d){
this._cache[key]=_a3d;
}
}
return _a3d;
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
},_makePiece:function(name,_a45,_a46,_a47,_a48,_a49){
var img;
var url=this.shadowPng+name.toUpperCase()+".png";
if(dojo.render.html.ie55||dojo.render.html.ie60){
img=dojo.doc().createElement("div");
img.style.filter="progid:DXImageTransform.Microsoft.AlphaImageLoader(src='"+url+"'"+(_a49?", sizingMethod='"+_a49+"'":"")+")";
}else{
img=dojo.doc().createElement("img");
img.src=url;
}
img.style.position="absolute";
img.style[_a45]=_a46+"px";
img.style[_a47]=_a48+"px";
img.style.width=this.shadowThickness+"px";
img.style.height=this.shadowThickness+"px";
this.pieces[name]=img;
this.node.appendChild(img);
},size:function(_a4c,_a4d){
var _a4e=_a4d-(this.shadowOffset+this.shadowThickness+1);
if(_a4e<0){
_a4e=0;
}
if(_a4d<1){
_a4d=1;
}
if(_a4c<1){
_a4c=1;
}
with(this.pieces){
l.style.height=_a4e+"px";
r.style.height=_a4e+"px";
b.style.width=(_a4c-1)+"px";
bl.style.top=(_a4d-1)+"px";
b.style.top=(_a4d-1)+"px";
br.style.top=(_a4d-1)+"px";
tr.style.left=(_a4c-1)+"px";
r.style.left=(_a4c-1)+"px";
br.style.left=(_a4c-1)+"px";
}
}});
dojo.provide("dojo.widget.html.layout");
dojo.widget.html.layout=function(_a4f,_a50,_a51){
dojo.html.addClass(_a4f,"dojoLayoutContainer");
_a50=dojo.lang.filter(_a50,function(_a52,idx){
_a52.idx=idx;
return dojo.lang.inArray(["top","bottom","left","right","client","flood"],_a52.layoutAlign);
});
if(_a51&&_a51!="none"){
var rank=function(_a55){
switch(_a55.layoutAlign){
case "flood":
return 1;
case "left":
case "right":
return (_a51=="left-right")?2:3;
case "top":
case "bottom":
return (_a51=="left-right")?3:2;
default:
return 4;
}
};
_a50.sort(function(a,b){
return (rank(a)-rank(b))||(a.idx-b.idx);
});
}
var f={top:dojo.html.getPixelValue(_a4f,"padding-top",true),left:dojo.html.getPixelValue(_a4f,"padding-left",true)};
dojo.lang.mixin(f,dojo.html.getContentBox(_a4f));
dojo.lang.forEach(_a50,function(_a59){
var elm=_a59.domNode;
var pos=_a59.layoutAlign;
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
if(_a59.onResized){
_a59.onResized();
}
}else{
if(pos=="left"||pos=="right"){
var w=dojo.html.getMarginBox(elm).width;
if(_a59.resizeTo){
_a59.resizeTo(w,f.height);
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
if(_a59.resizeTo){
_a59.resizeTo(f.width,f.height);
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
},accepts:function(_a68){
if(!dojo.lang.inArray(this.acceptedTypes,"*")){
for(var i=0;i<_a68.length;i++){
if(!dojo.lang.inArray(this.acceptedTypes,_a68[i].type)){
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
dojo.declare("dojo.dnd.DragManager",null,{selectedSources:[],dragObjects:[],dragSources:[],registerDragSource:function(_a6f){
},dropTargets:[],registerDropTarget:function(_a70){
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
var _a74=dp+"Idx_"+(this.dsCounter++);
ds.dragSourceId=_a74;
this.dragSources[_a74]=ds;
ds.domNode.setAttribute(dp,_a74);
if(dojo.render.html.ie){
dojo.event.browser.addListener(ds.domNode,"ondragstart",this.cancelEvent);
}
}
},unregisterDragSource:function(ds){
if(ds["domNode"]){
var dp=this.dsPrefix;
var _a77=ds.dragSourceId;
delete ds.dragSourceId;
delete this.dragSources[_a77];
ds.domNode.setAttribute(dp,null);
if(dojo.render.html.ie){
dojo.event.browser.removeListener(ds.domNode,"ondragstart",this.cancelEvent);
}
}
},registerDropTarget:function(dt){
this.dropTargets.push(dt);
},unregisterDropTarget:function(dt){
var _a7a=dojo.lang.find(this.dropTargets,dt,true);
if(_a7a>=0){
this.dropTargets.splice(_a7a,1);
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
var _a80=e.target.nodeType==dojo.html.TEXT_NODE?e.target.parentNode:e.target;
if(dojo.html.isTag(_a80,"button","textarea","input","select","option")){
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
},onMouseUp:function(e,_a83){
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
dojo.lang.forEach(this.dragObjects,function(_a84){
var ret=null;
if(!_a84){
return;
}
if(this.currentDropTarget){
e.dragObject=_a84;
var ce=this.currentDropTarget.domNode.childNodes;
if(ce.length>0){
e.dropTarget=ce[0];
while(e.dropTarget==_a84.domNode){
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
_a84.dragSource.onDragEnd(e);
}
catch(err){
var _a87={};
for(var i in e){
if(i=="type"){
_a87.type="mouseup";
continue;
}
_a87[i]=e[i];
}
_a84.dragSource.onDragEnd(_a87);
}
},function(){
_a84.onDragEnd(e);
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
dojo.lang.forEach(this.dropTargets,function(_a90){
var tn=_a90.domNode;
if(!tn||!_a90.accepts([this.dragSource])){
return;
}
var abs=dojo.html.getAbsolutePosition(tn,true);
var bb=dojo.html.getBorderBox(tn);
this.dropTargetDimensions.push([[abs.x,abs.y],[abs.x+bb.width,abs.y+bb.height],_a90]);
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
dojo.lang.forEach(this.selectedSources,function(_a97){
if(!_a97){
return;
}
var tdo=_a97.onDragStart(e);
if(tdo){
tdo.onDragStart(e);
tdo.dragOffset.y+=dy;
tdo.dragOffset.x+=dx;
tdo.dragSource=_a97;
this.dragObjects.push(tdo);
}
},this);
this.previousDropTarget=null;
this.cacheTargetLocations();
}
dojo.lang.forEach(this.dragObjects,function(_a99){
if(_a99){
_a99.onDragMove(e);
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
var _a9c=this.findBestTarget(e);
if(_a9c.target===null){
if(this.currentDropTarget){
this.currentDropTarget.onDragOut(e);
this.previousDropTarget=this.currentDropTarget;
this.currentDropTarget=null;
}
this.dropAcceptable=false;
return;
}
if(this.currentDropTarget!==_a9c.target){
if(this.currentDropTarget){
this.previousDropTarget=this.currentDropTarget;
this.currentDropTarget.onDragOut(e);
}
this.currentDropTarget=_a9c.target;
e.dragObjects=this.dragObjects;
this.dropAcceptable=this.currentDropTarget.onDragOver(e);
}else{
if(this.dropAcceptable){
this.currentDropTarget.onDragMove(e,this.dragObjects);
}
}
}
},findBestTarget:function(e){
var _a9e=this;
var _a9f=new Object();
_a9f.target=null;
_a9f.points=null;
dojo.lang.every(this.dropTargetDimensions,function(_aa0){
if(!_a9e.isInsideBox(e,_aa0)){
return true;
}
_a9f.target=_aa0[2];
_a9f.points=_aa0;
return Boolean(_a9e.nestedTargets);
});
return _a9f;
},isInsideBox:function(e,_aa2){
if((e.pageX>_aa2[0][0])&&(e.pageX<_aa2[1][0])&&(e.pageY>_aa2[0][1])&&(e.pageY<_aa2[1][1])){
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
var _aa7=new dojo.dnd.HtmlDragObject(this.dragObject,this.type);
if(this.dragClass){
_aa7.dragClass=this.dragClass;
}
if(this.constrainToContainer){
_aa7.constrainTo(this.constrainingContainer||this.domNode.parentNode);
}
return _aa7;
},setDragHandle:function(node){
node=dojo.byId(node);
dojo.dnd.dragManager.unregisterDragSource(this);
this.domNode=node;
dojo.dnd.dragManager.registerDragSource(this);
},setDragTarget:function(node){
this.dragObject=node;
},constrainTo:function(_aaa){
this.constrainToContainer=true;
if(_aaa){
this.constrainingContainer=_aaa;
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
var _ab4=doc.createElement("table");
if(isTr){
var _ab5=doc.createElement("tbody");
_ab4.appendChild(_ab5);
_ab5.appendChild(node);
}else{
_ab4.appendChild(node);
}
var _ab6=((isTr)?this.domNode:this.domNode.firstChild);
var _ab7=((isTr)?node:node.firstChild);
var _ab8=_ab6.childNodes;
var _ab9=_ab7.childNodes;
for(var i=0;i<_ab8.length;i++){
if((_ab9[i])&&(_ab9[i].style)){
_ab9[i].style.width=dojo.html.getContentBox(_ab8[i]).width+"px";
}
}
node=_ab4;
}
if((dojo.render.html.ie55||dojo.render.html.ie60)&&this.createIframe){
with(node.style){
top="0px";
left="0px";
}
var _abb=document.createElement("div");
_abb.appendChild(node);
this.bgIframe=new dojo.html.BackgroundIframe(_abb);
_abb.appendChild(this.bgIframe.iframe);
node=_abb;
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
var _abd=dojo.html.getViewport();
var _abe=_abd.width;
var _abf=_abd.height;
var _ac0=dojo.html.getScroll().offset;
var x=_ac0.x;
var y=_ac0.y;
}else{
var _ac3=dojo.html.getContentBox(this.constrainingContainer);
_abe=_ac3.width;
_abf=_ac3.height;
x=this.containingBlockPosition.x+dojo.html.getPixelValue(this.constrainingContainer,"padding-left",true)+dojo.html.getBorderExtent(this.constrainingContainer,"left");
y=this.containingBlockPosition.y+dojo.html.getPixelValue(this.constrainingContainer,"padding-top",true)+dojo.html.getBorderExtent(this.constrainingContainer,"top");
}
var mb=dojo.html.getMarginBox(this.domNode);
return {minX:x,minY:y,maxX:x+_abe-mb.width,maxY:y+_abf-mb.height};
},updateDragOffset:function(){
var _ac5=dojo.html.getScroll().offset;
if(_ac5.y!=this.scrollOffset.y){
var diff=_ac5.y-this.scrollOffset.y;
this.dragOffset.y+=diff;
this.scrollOffset.y=_ac5.y;
}
if(_ac5.x!=this.scrollOffset.x){
var diff=_ac5.x-this.scrollOffset.x;
this.dragOffset.x+=diff;
this.scrollOffset.x=_ac5.x;
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
var _acd=dojo.html.getAbsolutePosition(this.dragClone,true);
var _ace={left:this.dragStartPosition.x+1,top:this.dragStartPosition.y+1};
var anim=dojo.lfx.slideTo(this.dragClone,_ace,300);
var _ad0=this;
dojo.event.connect(anim,"onEnd",function(e){
dojo.html.removeNode(_ad0.dragClone);
_ad0.dragClone=null;
});
anim.play();
break;
}
dojo.event.topic.publish("dragEnd",{source:this});
},constrainTo:function(_ad2){
this.constrainToContainer=true;
if(_ad2){
this.constrainingContainer=_ad2;
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
for(var i=0,_ad7;i<this.domNode.childNodes.length;i++){
_ad7=this.domNode.childNodes[i];
if(_ad7.nodeType!=dojo.html.ELEMENT_NODE){
continue;
}
var pos=dojo.html.getAbsolutePosition(_ad7,true);
var _ad9=dojo.html.getBorderBox(_ad7);
this.childBoxes.push({top:pos.y,bottom:pos.y+_ad9.height,left:pos.x,right:pos.x+_ad9.width,height:_ad9.height,width:_ad9.width,node:_ad7});
}
return true;
},_getNodeUnderMouse:function(e){
for(var i=0,_adc;i<this.childBoxes.length;i++){
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
},onDragMove:function(e,_ade){
var i=this._getNodeUnderMouse(e);
if(!this.dropIndicator){
this.createDropIndicator();
}
var _ae0=this.vertical?dojo.html.gravity.WEST:dojo.html.gravity.NORTH;
var hide=false;
if(i<0){
if(this.childBoxes.length){
var _ae2=(dojo.html.gravity(this.childBoxes[0].node,e)&_ae0);
if(_ae2){
hide=true;
}
}else{
var _ae2=true;
}
}else{
var _ae3=this.childBoxes[i];
var _ae2=(dojo.html.gravity(_ae3.node,e)&_ae0);
if(_ae3.node===_ade[0].dragSource.domNode){
hide=true;
}else{
var _ae4=_ae2?(i>0?this.childBoxes[i-1]:_ae3):(i<this.childBoxes.length-1?this.childBoxes[i+1]:_ae3);
if(_ae4.node===_ade[0].dragSource.domNode){
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
this.placeIndicator(e,_ade,i,_ae2);
if(!dojo.html.hasParent(this.dropIndicator)){
dojo.body().appendChild(this.dropIndicator);
}
},placeIndicator:function(e,_ae6,_ae7,_ae8){
var _ae9=this.vertical?"left":"top";
var _aea;
if(_ae7<0){
if(this.childBoxes.length){
_aea=_ae8?this.childBoxes[0]:this.childBoxes[this.childBoxes.length-1];
}else{
this.dropIndicator.style[_ae9]=dojo.html.getAbsolutePosition(this.domNode,true)[this.vertical?"x":"y"]+"px";
}
}else{
_aea=this.childBoxes[_ae7];
}
if(_aea){
this.dropIndicator.style[_ae9]=(_ae8?_aea[_ae9]:_aea[this.vertical?"right":"bottom"])+"px";
if(this.vertical){
this.dropIndicator.style.height=_aea.height+"px";
this.dropIndicator.style.top=_aea.top+"px";
}else{
this.dropIndicator.style.width=_aea.width+"px";
this.dropIndicator.style.left=_aea.left+"px";
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
var _aee=this.vertical?dojo.html.gravity.WEST:dojo.html.gravity.NORTH;
if(i<0){
if(this.childBoxes.length){
if(dojo.html.gravity(this.childBoxes[0].node,e)&_aee){
return this.insert(e,this.childBoxes[0].node,"before");
}else{
return this.insert(e,this.childBoxes[this.childBoxes.length-1].node,"after");
}
}
return this.insert(e,this.domNode,"append");
}
var _aef=this.childBoxes[i];
if(dojo.html.gravity(_aef.node,e)&_aee){
return this.insert(e,_aef.node,"before");
}else{
return this.insert(e,_aef.node,"after");
}
},insert:function(e,_af1,_af2){
var node=e.dragObject.domNode;
if(_af2=="before"){
return dojo.html.insertBefore(node,_af1);
}else{
if(_af2=="after"){
return dojo.html.insertAfter(node,_af1);
}else{
if(_af2=="append"){
_af1.appendChild(node);
return true;
}
}
}
return false;
}},function(node,_af5){
if(arguments.length==0){
return;
}
this.domNode=dojo.byId(node);
dojo.dnd.DropTarget.call(this);
if(_af5&&dojo.lang.isString(_af5)){
_af5=[_af5];
}
this.acceptedTypes=_af5||[];
dojo.dnd.dragManager.registerDropTarget(this);
});
dojo.kwCompoundRequire({common:["dojo.dnd.DragAndDrop"],browser:["dojo.dnd.HtmlDragAndDrop"],dashboard:["dojo.dnd.HtmlDragAndDrop"]});
dojo.provide("dojo.dnd.*");
dojo.provide("dojo.dnd.HtmlDragMove");
dojo.declare("dojo.dnd.HtmlDragMoveSource",dojo.dnd.HtmlDragSource,{onDragStart:function(){
var _af6=new dojo.dnd.HtmlDragMoveObject(this.dragObject,this.type);
if(this.constrainToContainer){
_af6.constrainTo(this.constrainingContainer);
}
return _af6;
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
var _b01=this;
setTimeout(function(){
_b01._fromTrap=false;
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
},setBackgroundColor:function(_b03){
if(arguments.length>=3){
_b03=new dojo.gfx.color.Color(arguments[0],arguments[1],arguments[2]);
}else{
_b03=new dojo.gfx.color.Color(_b03);
}
this.bg.style.backgroundColor=_b03.toString();
return this.bgColor=_b03;
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
var _b05=dojo.html.getViewport();
var h=_b05.height;
var w=_b05.width;
with(this.bg.style){
width=w+"px";
height=h+"px";
}
var _b08=dojo.html.getScroll().offset;
this.bg.style.top=_b08.y+"px";
this.bg.style.left=_b08.x+"px";
var _b05=dojo.html.getViewport();
if(_b05.width!=w){
this.bg.style.width=_b05.width+"px";
}
if(_b05.height!=h){
this.bg.style.height=_b05.height+"px";
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
var _b09=dojo.html.getScroll().offset;
var _b0a=dojo.html.getViewport();
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
var x=_b09.x+(_b0a.width-mb.width)/2;
var y=_b09.y+(_b0a.height-mb.height)/2;
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
var _b10=dojo.html.getScroll().offset;
this.bg.style.top=_b10.y+"px";
this.bg.style.left=_b10.x+"px";
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
dojo.widget.defineWidget("dojo.widget.Dialog",[dojo.widget.ContentPane,dojo.widget.ModalDialogBase],{templatePath:dojo.uri.moduleUri("dojo.widget","templates/Dialog.html"),blockDuration:0,lifetime:0,closeNode:"",postMixInProperties:function(){
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
dojo.widget.defineWidget("dojo.widget.ResizeHandle",dojo.widget.HtmlWidget,{targetElmId:"",templateCssPath:dojo.uri.moduleUri("dojo.widget","templates/ResizeHandle.css"),templateString:"<div class=\"dojoHtmlResizeHandle\"><div></div></div>",postCreate:function(){
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
dojo.declare("dojo.widget.FloatingPaneBase",null,{title:"",iconSrc:"",hasShadow:false,constrainToContainer:false,taskBarId:"",resizable:true,titleBarDisplay:true,windowState:"normal",displayCloseAction:false,displayMinimizeAction:false,displayMaximizeAction:false,_max_taskBarConnectAttempts:5,_taskBarConnectAttempts:0,templatePath:dojo.uri.moduleUri("dojo.widget","templates/FloatingPane.html"),templateCssPath:dojo.uri.moduleUri("dojo.widget","templates/FloatingPane.css"),fillInFloatingPaneTemplate:function(args,frag){
var _b1f=this.getFragNodeRef(frag);
dojo.html.copyStyle(this.domNode,_b1f);
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
var _b24=dojo.html.getViewport();
var _b25=dojo.html.getPadding(dojo.body());
this.resizeTo(_b24.width-_b25.width,_b24.height-_b25.height);
}else{
var _b26=dojo.html.getContentBox(this.domNode.parentNode);
this.resizeTo(_b26.width,_b26.height);
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
var _b2d=dojo.widget.manager.getWidgetsByType(this.widgetType);
var _b2e=[];
for(var x=0;x<_b2d.length;x++){
if(this.widgetId!=_b2d[x].widgetId){
_b2e.push(_b2d[x]);
}
}
_b2e.sort(function(a,b){
return a.domNode.style.zIndex-b.domNode.style.zIndex;
});
_b2e.push(this);
var _b32=100;
for(x=0;x<_b2e.length;x++){
_b2e[x].domNode.style.zIndex=_b32+x*2;
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
var _b34=dojo.widget.getWidgetById(this.taskBarId);
if(!_b34){
if(this._taskBarConnectAttempts<this._max_taskBarConnectAttempts){
dojo.lang.setTimeout(this,this._taskBarSetup,50);
this._taskBarConnectAttempts++;
}else{
dojo.debug("Unable to connect to the taskBar");
}
return;
}
_b34.addChild(this);
},showFloatingPane:function(){
this.bringToTop();
},onFloatingPaneShow:function(){
var mb=dojo.html.getMarginBox(this.domNode);
this.resizeTo(mb.width,mb.height);
},resizeTo:function(_b36,_b37){
dojo.html.setMarginBox(this.domNode,{width:_b36,height:_b37});
dojo.widget.html.layout(this.domNode,[{domNode:this.titleBar,layoutAlign:"top"},{domNode:this.resizeBar,layoutAlign:"bottom"},{domNode:this.containerNode,layoutAlign:"client"}]);
dojo.widget.html.layout(this.containerNode,this.children,"top-bottom");
this.bgIframe.onResized();
if(this.shadow){
this.shadow.size(_b36,_b37);
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
dojo.event.topic.subscribe("dojo.widget.Editor2::onLoad",function(_b3a){
if(_b3a.toolbarAlwaysVisible){
var p=new dojo.widget.Editor2Plugin.AlwaysShowToolbar(_b3a);
}
});
dojo.declare("dojo.widget.Editor2Plugin.AlwaysShowToolbar",null,function(_b3c){
this.editor=_b3c;
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
var _b42=dh.getMarginBox(this.editor.domNode).width;
this._scrollThreshold=dh.abs(tdn,true).y;
if((isIE)&&(db)&&(dh.getStyle(db,"background-image")=="none")){
with(db.style){
backgroundImage="url("+dojo.uri.moduleUri("dojo.widget","templates/images/blank.gif")+")";
backgroundAttachment="fixed";
}
}
}
var _b43=(window["pageYOffset"])?window["pageYOffset"]:(document["documentElement"]||document["body"]).scrollTop;
if(_b43>this._scrollThreshold){
if(!this._fixEnabled){
var _b44=dojo.html.getMarginBox(tdn);
this.editor.editorObject.style.marginTop=_b44.height+"px";
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
tdn.style.width=_b44.width+"px";
tdn.style.zIndex=1000;
this._fixEnabled=true;
}
if(!dojo.render.html.safari){
var _b45=(this.height)?parseInt(this.editor.height):this.editor._lastHeight;
if(_b43>(this._scrollThreshold+_b45)){
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
},getCommand:function(_b47,name){
var _b49;
name=name.toLowerCase();
for(var i=0;i<this._registeredHandlers.length;i++){
_b49=this._registeredHandlers[i](_b47,name);
if(_b49){
return _b49;
}
}
switch(name){
case "htmltoggle":
_b49=new dojo.widget.Editor2BrowserCommand(_b47,name);
break;
case "formatblock":
_b49=new dojo.widget.Editor2FormatBlockCommand(_b47,name);
break;
case "anchor":
_b49=new dojo.widget.Editor2Command(_b47,name);
break;
case "createlink":
_b49=new dojo.widget.Editor2DialogCommand(_b47,name,{contentFile:"dojo.widget.Editor2Plugin.CreateLinkDialog",contentClass:"Editor2CreateLinkDialog",title:"Insert/Edit Link",width:"300px",height:"200px"});
break;
case "insertimage":
_b49=new dojo.widget.Editor2DialogCommand(_b47,name,{contentFile:"dojo.widget.Editor2Plugin.InsertImageDialog",contentClass:"Editor2InsertImageDialog",title:"Insert/Edit Image",width:"400px",height:"270px"});
break;
default:
var _b4b=this.getCurrentInstance();
if((_b4b&&_b4b.queryCommandAvailable(name))||(!_b4b&&dojo.widget.Editor2.prototype.queryCommandAvailable(name))){
_b49=new dojo.widget.Editor2BrowserCommand(_b47,name);
}else{
dojo.debug("dojo.widget.Editor2Manager.getCommand: Unknown command "+name);
return;
}
}
return _b49;
},destroy:function(){
this._currentInstance=null;
dojo.widget.HandlerManager.prototype.destroy.call(this);
}});
dojo.addOnUnload(dojo.widget.Editor2Manager,"destroy");
dojo.lang.declare("dojo.widget.Editor2Command",null,function(_b4c,name){
this._editor=_b4c;
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
dojo.lang.declare("dojo.widget.Editor2BrowserCommand",dojo.widget.Editor2Command,function(_b4f,name){
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
dojo.widget.defineWidget("dojo.widget.Editor2Dialog",[dojo.widget.HtmlWidget,dojo.widget.FloatingPaneBase,dojo.widget.ModalDialogBase],{templatePath:dojo.uri.moduleUri("dojo.widget","templates/Editor2/EditorDialog.html"),modal:true,width:"",height:"",windowState:"minimized",displayCloseAction:true,contentFile:"",contentClass:"",fillInTemplate:function(args,frag){
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
dojo.lang.declare("dojo.widget.Editor2DialogCommand",dojo.widget.Editor2BrowserCommand,function(_b55,name,_b57){
this.dialogParas=_b57;
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
},{toolbarAlwaysVisible:false,toolbarWidget:null,scrollInterval:null,toolbarTemplatePath:dojo.uri.cache.allow(dojo.uri.moduleUri("dojo.widget","templates/EditorToolbarOneline.html")),toolbarTemplateCssPath:null,toolbarPlaceHolder:"",_inSourceMode:false,_htmlEditNode:null,toolbarGroup:"",shareToolbar:false,contextMenuGroupSet:"",editorOnLoad:function(){
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
var _b58={shareGroup:this.toolbarGroup,parent:this};
_b58.templateString=dojo.uri.cache.get(this.toolbarTemplatePath);
if(this.toolbarTemplateCssPath){
_b58.templateCssPath=this.toolbarTemplateCssPath;
_b58.templateCssString=dojo.uri.cache.get(this.toolbarTemplateCssPath);
}
if(this.toolbarPlaceHolder){
this.toolbarWidget=dojo.widget.createWidget("Editor2Toolbar",_b58,dojo.byId(this.toolbarPlaceHolder),"after");
}else{
this.toolbarWidget=dojo.widget.createWidget("Editor2Toolbar",_b58,this.domNode.firstChild,"before");
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
},execCommand:function(_b5c,_b5d){
switch(_b5c.toLowerCase()){
case "htmltoggle":
this.toggleHtmlEditing();
break;
default:
dojo.widget.Editor2.superclass.execCommand.apply(this,arguments);
}
},queryCommandEnabled:function(_b5e,_b5f){
switch(_b5e.toLowerCase()){
case "htmltoggle":
return true;
default:
if(this._inSourceMode){
return false;
}
return dojo.widget.Editor2.superclass.queryCommandEnabled.apply(this,arguments);
}
},queryCommandState:function(_b60,_b61){
switch(_b60.toLowerCase()){
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
},_updateToolbarLastRan:null,_updateToolbarTimer:null,_updateToolbarFrequency:500,updateToolbar:function(_b64){
if((!this.isLoaded)||(!this.toolbarWidget)){
return;
}
var diff=new Date()-this._updateToolbarLastRan;
if((!_b64)&&(this._updateToolbarLastRan)&&((diff<this._updateToolbarFrequency))){
clearTimeout(this._updateToolbarTimer);
var _b66=this;
this._updateToolbarTimer=setTimeout(function(){
_b66.updateToolbar();
},this._updateToolbarFrequency/2);
return;
}else{
this._updateToolbarLastRan=new Date();
}
if(dojo.widget.Editor2Manager.getCurrentInstance()!==this){
return;
}
this.toolbarWidget.update();
},destroy:function(_b67){
this._htmlEditNode=null;
dojo.event.disconnect(this,"close",this.toolbarWidget,"hide");
if(!_b67){
this.toolbarWidget.destroy();
}
dojo.widget.Editor2.superclass.destroy.call(this);
},_lastStateTimestamp:0,onDisplayChanged:function(e,_b69){
this._lastStateTimestamp=(new Date()).getTime();
dojo.widget.Editor2.superclass.onDisplayChanged.call(this,e);
this.updateToolbar(_b69);
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
dojo.provide("struts.widget.Bind");
dojo.widget.defineWidget("struts.widget.Bind",dojo.widget.HtmlWidget,{widgetType:"Bind",executeScripts:false,targets:"",targetsArray:null,href:"",handler:"",loadingText:"Loading...",errorText:"",showError:true,showLoading:false,listenTopics:"",notifyTopics:"",notifyTopicsArray:null,beforeNotifyTopics:"",beforeNotifyTopicsArray:null,afterNotifyTopics:"",afterNotifyTopicsArray:null,errorNotifyTopics:"",errorNotifyTopicsArray:null,formId:"",formFilter:"",formNode:null,events:"",indicator:"",parseContent:true,highlightColor:"",highlightDuration:2000,validate:false,ajaxAfterValidation:false,postCreate:function(){
var self=this;
if(!dojo.string.isBlank(this.listenTopics)){
this.log("Listening to "+this.listenTopics+" to refresh");
var _b73=this.listenTopics.split(",");
if(_b73){
dojo.lang.forEach(_b73,function(_b74){
dojo.event.topic.subscribe(_b74,self,"reloadContents");
});
}
}
if(!dojo.string.isBlank(this.notifyTopics)){
this.notifyTopicsArray=this.notifyTopics.split(",");
}
if(!dojo.string.isBlank(this.beforeNotifyTopics)){
this.beforeNotifyTopicsArray=this.beforeNotifyTopics.split(",");
}
if(!dojo.string.isBlank(this.afterNotifyTopics)){
this.afterNotifyTopicsArray=this.afterNotifyTopics.split(",");
}
if(!dojo.string.isBlank(this.errorNotifyTopics)){
this.errorNotifyTopicsArray=this.errorNotifyTopics.split(",");
}
if(!dojo.string.isBlank(this.targets)){
this.targetsArray=this.targets.split(",");
}
if(!dojo.string.isBlank(this.events)){
var _b75=this.events.split(",");
if(_b75&&this.domNode){
dojo.lang.forEach(_b75,function(_b76){
dojo.event.connect(self.domNode,_b76,function(evt){
evt.preventDefault();
evt.stopPropagation();
self.reloadContents();
});
});
}
}
if(dojo.string.isBlank(this.href)&&dojo.string.isBlank(this.formId)){
this.formNode=dojo.dom.getFirstAncestorByTag(this.domNode,"form");
}else{
this.formNode=dojo.byId(this.formId);
}
if(this.formNode&&dojo.string.isBlank(this.href)){
this.href=this.formNode.action;
}
if(!dojo.string.isBlank(this.formId)){
this.formNode=dojo.byId(this.formId);
}
},highlight:function(){
if(!dojo.string.isBlank(this.highlightColor)){
var _b78=[];
dojo.lang.forEach(this.targetsArray,function(_b79){
var node=dojo.byId(_b79);
if(node){
_b78.push(node);
}
});
var _b7b=dojo.lfx.html.highlight(_b78,this.highlightColor,this.highlightDuration);
_b7b.play();
}
},log:function(text){
dojo.debug("["+(this.widgetId?this.widgetId:"unknown")+"] "+text);
},setContent:function(text){
if(this.targetsArray){
var self=this;
var _b7f=new dojo.xml.Parse();
dojo.lang.forEach(this.targetsArray,function(_b80){
var node=dojo.byId(_b80);
if(node){
node.innerHTML=text;
if(self.parseContent&&text!=self.loadingText){
var frag=_b7f.parseElement(node,null,true);
dojo.widget.getParser().createSubComponents(frag,dojo.widget.byId(_b80));
}
}else{
self.log("Unable to find target: "+node);
}
});
}
},bindHandler:function(type,data,e){
dojo.html.hide(this.indicator);
this.notify(data,type,e);
if(type=="load"){
if(this.validate){
StrutsUtils.clearValidationErrors(this.formNode);
var _b86=StrutsUtils.getValidationErrors(data);
if(_b86&&_b86.fieldErrors){
StrutsUtils.showValidationErrors(this.formNode,_b86);
return;
}else{
if(!this.ajaxAfterValidation&&this.formNode){
this.formNode.submit();
return;
}
}
}
if(this.executeScripts){
var _b87=this.parse(data);
if(_b87.scripts&&_b87.scripts.length>0){
var _b88="";
for(var i=0;i<_b87.scripts.length;i++){
_b88+=_b87.scripts[i];
}
(new Function("_container_",_b88+"; return this;"))(this);
}
this.setContent(_b87.text);
}else{
this.setContent(data);
}
this.highlight();
}else{
if(this.showError){
var _b8a=dojo.string.isBlank(this.errorText)?e.message:this.errorText;
this.setContent(_b8a);
}
}
},notify:function(data,type,e){
var self=this;
if(this.notifyTopicsArray){
dojo.lang.forEach(this.notifyTopicsArray,function(_b8f){
try{
dojo.event.topic.publish(_b8f,data,type,e);
}
catch(ex){
self.log(ex);
}
});
}
var _b90=null;
switch(type){
case "before":
_b90=this.beforeNotifyTopicsArray;
break;
case "load":
_b90=this.afterNotifyTopicsArray;
break;
case "error":
_b90=this.errorNotifyTopicsArray;
break;
}
this.notifyTo(_b90,data,type,e);
},notifyTo:function(_b91,data,type,e){
var self=this;
if(_b91){
dojo.lang.forEach(_b91,function(_b96){
try{
dojo.event.topic.publish(_b96,data,type,e);
}
catch(ex){
self.log(ex);
}
});
}
},onDownloadStart:function(_b97){
if(this.showLoading&&!dojo.string.isBlank(this.loadingText)){
_b97.text=this.loadingText;
}
},reloadContents:function(evt){
if(!dojo.string.isBlank(this.handler)){
this.log("Invoking handler: "+this.handler);
window[this.handler](this,this.domNode);
}else{
try{
var self=this;
var _b9a={cancel:false};
this.notify(this.widgetId,"before",_b9a);
if(_b9a.cancel){
this.log("Request canceled");
return;
}
if(dojo.string.isBlank(this.href)){
return;
}
if(!this.validate&&this.formNode&&this.formNode.onsubmit!=null){
var _b9b=this.formNode.onsubmit.call(evt);
if(_b9b!=null&&!_b9b){
this.log("Request canceled by 'onsubmit' of the form");
return;
}
}
dojo.html.show(this.indicator);
if(this.showLoading){
this.setContent(this.loadingText);
}
var _b9c=this.href;
if(!this.ajaxAfterValidation){
_b9c=_b9c+(_b9c.indexOf("?")>-1?"&":"?")+"validateOnly=true";
}
dojo.io.bind({url:_b9c,useCache:false,preventCache:true,formNode:self.formNode,formFilter:window[self.formFilter],handler:function(type,data,e){
dojo.lang.hitch(self,"bindHandler")(type,data,e);
},mimetype:"text/html"});
}
catch(ex){
if(this.showError){
var _ba0=dojo.string.isBlank(this.errorText)?ex:this.errorText;
this.setContent(_ba0);
}
}
}
},parse:function(s){
this.log("Parsing: "+s);
var _ba2=[];
var tmp=[];
var _ba4=[];
while(_ba2){
_ba2=s.match(/<script([^>]*)>([\s\S]*?)<\/script>/i);
if(!_ba2){
break;
}
if(_ba2[1]){
attr=_ba2[1].match(/src=(['"]?)([^"']*)\1/i);
if(attr){
var tmp2=attr[2].search(/.*(\bdojo\b(?:\.uncompressed)?\.js)$/);
if(tmp2>-1){
this.log("Security note! inhibit:"+attr[2]+" from  beeing loaded again.");
}
}
}
if(_ba2[2]){
var sc=_ba2[2].replace(/(?:var )?\bdjConfig\b(?:[\s]*=[\s]*\{[^}]+\}|\.[\w]*[\s]*=[\s]*[^;\n]*)?;?|dojo\.hostenv\.writeIncludes\(\s*\);?/g,"");
if(!sc){
continue;
}
tmp=[];
while(tmp){
tmp=sc.match(/dojo\.(?:(?:require(?:After)?(?:If)?)|(?:widget\.(?:manager\.)?registerWidgetPackage)|(?:(?:hostenv\.)?setModulePrefix))\((['"]).*?\1\)\s*;?/);
if(!tmp){
break;
}
sc=sc.replace(tmp[0],"");
}
_ba4.push(sc);
}
s=s.replace(/<script[^>]*>[\s\S]*?<\/script>/i,"");
}
return {text:s,scripts:_ba4};
}});
dojo.provide("dojo.lang.timing.Timer");
dojo.lang.timing.Timer=function(_ba7){
this.timer=null;
this.isRunning=false;
this.interval=_ba7;
this.onStart=null;
this.onStop=null;
};
dojo.extend(dojo.lang.timing.Timer,{onTick:function(){
},setInterval:function(_ba8){
if(this.isRunning){
dj_global.clearInterval(this.timer);
}
this.interval=_ba8;
if(this.isRunning){
this.timer=dj_global.setInterval(dojo.lang.hitch(this,"onTick"),this.interval);
}
},start:function(){
if(typeof this.onStart=="function"){
this.onStart();
}
this.isRunning=true;
this.timer=dj_global.setInterval(dojo.lang.hitch(this,"onTick"),this.interval);
},stop:function(){
if(typeof this.onStop=="function"){
this.onStop();
}
this.isRunning=false;
dj_global.clearInterval(this.timer);
}});
dojo.provide("struts.widget.BindDiv");
dojo.widget.defineWidget("struts.widget.BindDiv",dojo.widget.ContentPane,{widgetType:"BindDiv",href:"",extractContent:false,parseContent:false,cacheContent:false,refreshOnShow:false,executeScripts:false,preload:true,updateFreq:0,delay:0,autoStart:true,timer:null,loadingText:"Loading...",showLoading:false,errorText:"",showError:true,listenTopics:"",notifyTopics:"",notifyTopicsArray:null,stopTimerListenTopics:"",startTimerListenTopics:"",beforeNotifyTopics:"",beforeNotifyTopicsArray:null,afterNotifyTopics:"",afterNotifyTopicsArray:null,errorNotifyTopics:"",errorNotifyTopicsArray:null,beforeLoading:"",afterLoading:"",formId:"",formFilter:"",indicator:"",parseContent:true,highlightColor:"",highlightDuration:2000,onDownloadStart:function(_ba9){
if(!this.showLoading){
_ba9.returnValue=false;
return;
}
if(this.showLoading&&!dojo.string.isBlank(this.loadingText)){
_ba9.text=this.loadingText;
}
},highlight:function(){
if(!dojo.string.isBlank(this.highlightColor)){
var _baa=dojo.lfx.html.highlight([this.domNode],this.highlightColor,this.highlightDuration);
_baa.play();
}
},onDownloadError:function(_bab){
this.onError(_bab);
},onContentError:function(_bac){
this.onError(_bac);
},onExecError:function(_bad){
this.onError(_bad);
},onError:function(_bae){
if(this.showError){
if(!dojo.string.isBlank(this.errorText)){
_bae.text=this.errorText;
}
}else{
_bae.text="";
}
},notify:function(data,type,e){
if(this.notifyTopicsArray){
var self=this;
dojo.lang.forEach(this.notifyTopicsArray,function(_bb3){
try{
dojo.event.topic.publish(_bb3,data,type,e);
}
catch(ex){
self.log(ex);
}
});
}
var _bb4=null;
switch(type){
case "before":
_bb4=this.beforeNotifyTopicsArray;
break;
case "load":
_bb4=this.afterNotifyTopicsArray;
break;
case "error":
_bb4=this.errorNotifyTopicsArray;
break;
}
this.notifyTo(_bb4,data,type,e);
},notifyTo:function(_bb5,data,type,e){
var self=this;
if(_bb5){
dojo.lang.forEach(_bb5,function(_bba){
try{
dojo.event.topic.publish(_bba,data,type,e);
}
catch(ex){
self.log(ex);
}
});
}
},postCreate:function(args,frag){
if(this.handler!==""){
this.setHandler(this.handler);
}
var self=this;
var _bbe=function(){
dojo.lang.hitch(self,"refresh")();
};
var _bbf=function(){
dojo.lang.hitch(self,"startTimer")();
};
if(this.updateFreq>0){
this.timer=new dojo.lang.timing.Timer(this.updateFreq);
this.timer.onTick=_bbe;
if(this.autoStart){
if(this.delay>0){
dojo.lang.setTimeout(_bbf,this.delay);
}else{
this.startTimer();
}
}
}else{
if(this.delay>0){
dojo.lang.setTimeout(_bbe,this.delay);
}
}
if(!dojo.string.isBlank(this.listenTopics)){
this.log("Listening to "+this.listenTopics+" to refresh");
var _bc0=this.listenTopics.split(",");
if(_bc0){
dojo.lang.forEach(_bc0,function(_bc1){
dojo.event.topic.subscribe(_bc1,self,"refresh");
});
}
}
if(!dojo.string.isBlank(this.stopTimerListenTopics)){
this.log("Listening to "+this.stopTimerListenTopics+" to stop timer");
var _bc2=this.stopTimerListenTopics.split(",");
if(_bc2){
dojo.lang.forEach(_bc2,function(_bc3){
dojo.event.topic.subscribe(_bc3,self,"stopTimer");
});
}
}
if(!dojo.string.isBlank(this.startTimerListenTopics)){
this.log("Listening to "+this.stopTimerListenTopics+" to start timer");
var _bc4=this.startTimerListenTopics.split(",");
if(_bc4){
dojo.lang.forEach(_bc4,function(_bc5){
dojo.event.topic.subscribe(_bc5,self,"startTimer");
});
}
}
if(!dojo.string.isBlank(this.notifyTopics)){
this.notifyTopicsArray=this.notifyTopics.split(",");
}
if(!dojo.string.isBlank(this.beforeNotifyTopics)){
this.beforeNotifyTopicsArray=this.beforeNotifyTopics.split(",");
}
if(!dojo.string.isBlank(this.afterNotifyTopics)){
this.afterNotifyTopicsArray=this.afterNotifyTopics.split(",");
}
if(!dojo.string.isBlank(this.errorNotifyTopics)){
this.errorNotifyTopicsArray=this.errorNotifyTopics.split(",");
}
if(this.isShowing()&&this.preload&&this.updateFreq<=0&&this.delay<=0){
this.refresh();
}
},_downloadExternalContent:function(url,_bc7){
var _bc8={cancel:false};
this.notify(this.widgetId,"before",_bc8);
if(_bc8.cancel){
return;
}
dojo.html.show(this.indicator);
this._handleDefaults("Loading...","onDownloadStart");
var self=this;
dojo.io.bind({url:url,useCache:_bc7,preventCache:!_bc7,mimetype:"text/html",formNode:dojo.byId(self.formId),formFilter:window[self.formFilter],handler:function(type,data,e){
dojo.html.hide(self.indicator);
self.notify(data,type,e);
if(type=="load"){
self.onDownloadEnd.call(self,url,data);
self.highlight();
}else{
self._handleDefaults.call(self,"Error loading '"+url+"' ("+e.status+" "+e.statusText+")","onDownloadError");
self.onLoad();
}
}});
},log:function(text){
dojo.debug("["+this.widgetId+"] "+text);
},stopTimer:function(){
if(this.timer&&this.timer.isRunning){
this.log("stopping timer");
this.timer.stop();
}
},startTimer:function(){
if(this.timer&&!this.timer.isRunning){
this.log("starting timer with update interval "+this.updateFreq);
this.timer.start();
}
},splitAndFixPaths:function(s,url){
var _bd0=[],_bd1=[],tmp=[];
var _bd3=[],_bd4=[],attr=[],_bd6=[];
var str="",path="",fix="",_bda="",tag="",_bdc="";
if(!url){
url="./";
}
if(s){
var _bdd=/<title[^>]*>([\s\S]*?)<\/title>/i;
while(_bd3=_bdd.exec(s)){
_bd0.push(_bd3[1]);
s=s.substring(0,_bd3.index)+s.substr(_bd3.index+_bd3[0].length);
}
if(this.adjustPaths){
var _bde=/<[a-z][a-z0-9]*[^>]*\s(?:(?:src|href|style)=[^>])+[^>]*>/i;
var _bdf=/\s(src|href|style)=(['"]?)([\w()\[\]\/.,\\'"-:;#=&?\s@!]+?)\2/i;
var _be0=/^(?:[#]|(?:(?:https?|ftps?|file|javascript|mailto|news):))/;
while(tag=_bde.exec(s)){
str+=s.substring(0,tag.index);
s=s.substring((tag.index+tag[0].length),s.length);
tag=tag[0];
_bda="";
while(attr=_bdf.exec(tag)){
path="";
_bdc=attr[3];
switch(attr[1].toLowerCase()){
case "src":
case "href":
if(_be0.exec(_bdc)){
path=_bdc;
}else{
path=(new dojo.uri.Uri(url,_bdc).toString());
}
break;
case "style":
path=dojo.html.fixPathsInCssText(_bdc,url);
break;
default:
path=_bdc;
}
fix=" "+attr[1]+"="+attr[2]+path+attr[2];
_bda+=tag.substring(0,attr.index)+fix;
tag=tag.substring((attr.index+attr[0].length),tag.length);
}
str+=_bda+tag;
}
s=str+s;
}
_bdd=/(?:<(style)[^>]*>([\s\S]*?)<\/style>|<link ([^>]*rel=['"]?stylesheet['"]?[^>]*)>)/i;
while(_bd3=_bdd.exec(s)){
if(_bd3[1]&&_bd3[1].toLowerCase()=="style"){
_bd6.push(dojo.html.fixPathsInCssText(_bd3[2],url));
}else{
if(attr=_bd3[3].match(/href=(['"]?)([^'">]*)\1/i)){
_bd6.push({path:attr[2]});
}
}
s=s.substring(0,_bd3.index)+s.substr(_bd3.index+_bd3[0].length);
}
var _bdd=/<script([^>]*)>([\s\S]*?)<\/script>/i;
var _be1=/src=(['"]?)([^"']*)\1/i;
var _be2=/.*(\bdojo\b\.js(?:\.uncompressed\.js)?)$/;
var _be3=/(?:var )?\bdjConfig\b(?:[\s]*=[\s]*\{[^}]+\}|\.[\w]*[\s]*=[\s]*[^;\n]*)?;?|dojo\.hostenv\.writeIncludes\(\s*\);?/g;
var _be4=/dojo\.(?:(?:require(?:After)?(?:If)?)|(?:widget\.(?:manager\.)?registerWidgetPackage)|(?:(?:hostenv\.)?setModulePrefix|registerModulePath)|defineNamespace)\((['"]).*?\1\)\s*;?/;
while(_bd3=_bdd.exec(s)){
if(this.executeScripts&&_bd3[1]){
if(attr=_be1.exec(_bd3[1])){
if(_be2.exec(attr[2])){
dojo.debug("Security note! inhibit:"+attr[2]+" from  being loaded again.");
}else{
_bd1.push({path:attr[2]});
}
}
}
if(_bd3[2]){
var sc=_bd3[2].replace(_be3,"");
if(!sc){
continue;
}
while(tmp=_be4.exec(sc)){
_bd4.push(tmp[0]);
sc=sc.substring(0,tmp.index)+sc.substr(tmp.index+tmp[0].length);
}
if(this.executeScripts){
_bd1.push(sc);
}
}
s=s.substr(0,_bd3.index)+s.substr(_bd3.index+_bd3[0].length);
}
if(this.extractContent){
_bd3=s.match(/<body[^>]*>\s*([\s\S]+)\s*<\/body>/im);
if(_bd3){
s=_bd3[1];
}
}
if(this.executeScripts&&this.scriptSeparation){
var _bdd=/(<[a-zA-Z][a-zA-Z0-9]*\s[^>]*?\S=)((['"])[^>]*scriptScope[^>]*>)/;
var _be6=/([\s'";:\(])scriptScope(.*)/;
str="";
while(tag=_bdd.exec(s)){
tmp=((tag[3]=="'")?"\"":"'");
fix="";
str+=s.substring(0,tag.index)+tag[1];
while(attr=_be6.exec(tag[2])){
tag[2]=tag[2].substring(0,attr.index)+attr[1]+"dojo.widget.byId("+tmp+this.widgetId+tmp+").scriptScope"+attr[2];
}
str+=tag[2];
s=s.substr(tag.index+tag[0].length);
}
s=str+s;
}
}
return {"xml":s,"styles":_bd6,"titles":_bd0,"requires":_bd4,"scripts":_bd1,"url":url};
}});
dojo.provide("struts.widget.BindAnchor");
dojo.widget.defineWidget("struts.widget.BindAnchor",struts.widget.Bind,{widgetType:"BindAnchor",events:"onclick",postCreate:function(){
struts.widget.BindAnchor.superclass.postCreate.apply(this);
this.domNode.href="#";
}});
dojo.provide("dojo.widget.html.stabile");
dojo.widget.html.stabile={_sqQuotables:new RegExp("([\\\\'])","g"),_depth:0,_recur:false,depthLimit:2};
dojo.widget.html.stabile.getState=function(id){
dojo.widget.html.stabile.setup();
return dojo.widget.html.stabile.widgetState[id];
};
dojo.widget.html.stabile.setState=function(id,_be9,_bea){
dojo.widget.html.stabile.setup();
dojo.widget.html.stabile.widgetState[id]=_be9;
if(_bea){
dojo.widget.html.stabile.commit(dojo.widget.html.stabile.widgetState);
}
};
dojo.widget.html.stabile.setup=function(){
if(!dojo.widget.html.stabile.widgetState){
var text=dojo.widget.html.stabile._getStorage().value;
dojo.widget.html.stabile.widgetState=text?dj_eval("("+text+")"):{};
}
};
dojo.widget.html.stabile.commit=function(_bec){
dojo.widget.html.stabile._getStorage().value=dojo.widget.html.stabile.description(_bec);
};
dojo.widget.html.stabile.description=function(v,_bee){
var _bef=dojo.widget.html.stabile._depth;
var _bf0=function(){
return this.description(this,true);
};
try{
if(v===void (0)){
return "undefined";
}
if(v===null){
return "null";
}
if(typeof (v)=="boolean"||typeof (v)=="number"||v instanceof Boolean||v instanceof Number){
return v.toString();
}
if(typeof (v)=="string"||v instanceof String){
var v1=v.replace(dojo.widget.html.stabile._sqQuotables,"\\$1");
v1=v1.replace(/\n/g,"\\n");
v1=v1.replace(/\r/g,"\\r");
return "'"+v1+"'";
}
if(v instanceof Date){
return "new Date("+d.getFullYear+","+d.getMonth()+","+d.getDate()+")";
}
var d;
if(v instanceof Array||v.push){
if(_bef>=dojo.widget.html.stabile.depthLimit){
return "[ ... ]";
}
d="[";
var _bf3=true;
dojo.widget.html.stabile._depth++;
for(var i=0;i<v.length;i++){
if(_bf3){
_bf3=false;
}else{
d+=",";
}
d+=arguments.callee(v[i],_bee);
}
return d+"]";
}
if(v.constructor==Object||v.toString==_bf0){
if(_bef>=dojo.widget.html.stabile.depthLimit){
return "{ ... }";
}
if(typeof (v.hasOwnProperty)!="function"&&v.prototype){
throw new Error("description: "+v+" not supported by script engine");
}
var _bf3=true;
d="{";
dojo.widget.html.stabile._depth++;
for(var key in v){
if(v[key]==void (0)||typeof (v[key])=="function"){
continue;
}
if(_bf3){
_bf3=false;
}else{
d+=", ";
}
var kd=key;
if(!kd.match(/^[a-zA-Z_][a-zA-Z0-9_]*$/)){
kd=arguments.callee(key,_bee);
}
d+=kd+": "+arguments.callee(v[key],_bee);
}
return d+"}";
}
if(_bee){
if(dojo.widget.html.stabile._recur){
var _bf7=Object.prototype.toString;
return _bf7.apply(v,[]);
}else{
dojo.widget.html.stabile._recur=true;
return v.toString();
}
}else{
throw new Error("Unknown type: "+v);
return "'unknown'";
}
}
finally{
dojo.widget.html.stabile._depth=_bef;
}
};
dojo.widget.html.stabile._getStorage=function(){
if(dojo.widget.html.stabile.dataField){
return dojo.widget.html.stabile.dataField;
}
var form=document.forms._dojo_form;
return dojo.widget.html.stabile.dataField=form?form.stabile:{value:""};
};
dojo.provide("dojo.widget.ComboBox");
dojo.declare("dojo.widget.incrementalComboBoxDataProvider",null,function(_bf9){
this.searchUrl=_bf9.dataUrl;
this._cache={};
this._inFlight=false;
this._lastRequest=null;
this.allowCache=false;
},{_addToCache:function(_bfa,data){
if(this.allowCache){
this._cache[_bfa]=data;
}
},startSearch:function(_bfc,_bfd){
if(this._inFlight){
}
var tss=encodeURIComponent(_bfc);
var _bff=dojo.string.substituteParams(this.searchUrl,{"searchString":tss});
var _c00=this;
var _c01=this._lastRequest=dojo.io.bind({url:_bff,method:"get",mimetype:"text/json",load:function(type,data,evt){
_c00._inFlight=false;
if(!dojo.lang.isArray(data)){
var _c05=[];
for(var key in data){
_c05.push([data[key],key]);
}
data=_c05;
}
_c00._addToCache(_bfc,data);
if(_c01==_c00._lastRequest){
_bfd(data);
}
}});
this._inFlight=true;
}});
dojo.declare("dojo.widget.basicComboBoxDataProvider",null,function(_c07,node){
this._data=[];
this.searchLimit=30;
this.searchType="STARTSTRING";
this.caseSensitive=false;
if(!dj_undef("dataUrl",_c07)&&!dojo.string.isBlank(_c07.dataUrl)){
this._getData(_c07.dataUrl);
}else{
if((node)&&(node.nodeName.toLowerCase()=="select")){
var opts=node.getElementsByTagName("option");
var ol=opts.length;
var data=[];
for(var x=0;x<ol;x++){
var text=opts[x].textContent||opts[x].innerText||opts[x].innerHTML;
var _c0e=[String(text),String(opts[x].value)];
data.push(_c0e);
if(opts[x].selected){
_c07.setAllValues(_c0e[0],_c0e[1]);
}
}
this.setData(data);
}
}
},{_getData:function(url){
dojo.io.bind({url:url,load:dojo.lang.hitch(this,function(type,data,evt){
if(!dojo.lang.isArray(data)){
var _c13=[];
for(var key in data){
_c13.push([data[key],key]);
}
data=_c13;
}
this.setData(data);
}),mimetype:"text/json"});
},startSearch:function(_c15,_c16){
this._performSearch(_c15,_c16);
},_performSearch:function(_c17,_c18){
var st=this.searchType;
var ret=[];
if(!this.caseSensitive){
_c17=_c17.toLowerCase();
}
for(var x=0;x<this._data.length;x++){
if((this.searchLimit>0)&&(ret.length>=this.searchLimit)){
break;
}
var _c1c=new String((!this.caseSensitive)?this._data[x][0].toLowerCase():this._data[x][0]);
if(_c1c.length<_c17.length){
continue;
}
if(st=="STARTSTRING"){
if(_c17==_c1c.substr(0,_c17.length)){
ret.push(this._data[x]);
}
}else{
if(st=="SUBSTRING"){
if(_c1c.indexOf(_c17)>=0){
ret.push(this._data[x]);
}
}else{
if(st=="STARTWORD"){
var idx=_c1c.indexOf(_c17);
if(idx==0){
ret.push(this._data[x]);
}
if(idx<=0){
continue;
}
var _c1e=false;
while(idx!=-1){
if(" ,/(".indexOf(_c1c.charAt(idx-1))!=-1){
_c1e=true;
break;
}
idx=_c1c.indexOf(_c17,idx+1);
}
if(!_c1e){
continue;
}else{
ret.push(this._data[x]);
}
}
}
}
}
_c18(ret);
},setData:function(_c1f){
this._data=_c1f;
}});
dojo.widget.defineWidget("dojo.widget.ComboBox",dojo.widget.HtmlWidget,{forceValidOption:false,searchType:"stringstart",dataProvider:null,autoComplete:true,searchDelay:100,dataUrl:"",fadeTime:200,maxListLength:8,mode:"local",selectedResult:null,dataProviderClass:"",buttonSrc:dojo.uri.moduleUri("dojo.widget","templates/images/combo_box_arrow.png"),dropdownToggle:"fade",templatePath:dojo.uri.moduleUri("dojo.widget","templates/ComboBox.html"),templateCssPath:dojo.uri.moduleUri("dojo.widget","templates/ComboBox.css"),setValue:function(_c20){
this.comboBoxValue.value=_c20;
if(this.textInputNode.value!=_c20){
this.textInputNode.value=_c20;
dojo.widget.html.stabile.setState(this.widgetId,this.getState(),true);
this.onValueChanged(_c20);
}
},onValueChanged:function(_c21){
},getValue:function(){
return this.comboBoxValue.value;
},getState:function(){
return {value:this.getValue()};
},setState:function(_c22){
this.setValue(_c22.value);
},enable:function(){
this.disabled=false;
this.textInputNode.removeAttribute("disabled");
},disable:function(){
this.disabled=true;
this.textInputNode.setAttribute("disabled",true);
},_getCaretPos:function(_c23){
if(dojo.lang.isNumber(_c23.selectionStart)){
return _c23.selectionStart;
}else{
if(dojo.render.html.ie){
var tr=document.selection.createRange().duplicate();
var ntr=_c23.createTextRange();
tr.move("character",0);
ntr.move("character",0);
try{
ntr.setEndPoint("EndToEnd",tr);
return String(ntr.text).replace(/\r/g,"").length;
}
catch(e){
return 0;
}
}
}
},_setCaretPos:function(_c26,_c27){
_c27=parseInt(_c27);
this._setSelectedRange(_c26,_c27,_c27);
},_setSelectedRange:function(_c28,_c29,end){
if(!end){
end=_c28.value.length;
}
if(_c28.setSelectionRange){
_c28.focus();
_c28.setSelectionRange(_c29,end);
}else{
if(_c28.createTextRange){
var _c2b=_c28.createTextRange();
with(_c2b){
collapse(true);
moveEnd("character",end);
moveStart("character",_c29);
select();
}
}else{
_c28.value=_c28.value;
_c28.blur();
_c28.focus();
var dist=parseInt(_c28.value.length)-end;
var _c2d=String.fromCharCode(37);
var tcc=_c2d.charCodeAt(0);
for(var x=0;x<dist;x++){
var te=document.createEvent("KeyEvents");
te.initKeyEvent("keypress",true,true,null,false,false,false,false,tcc,tcc);
_c28.dispatchEvent(te);
}
}
}
},_handleKeyEvents:function(evt){
if(evt.ctrlKey||evt.altKey||!evt.key){
return;
}
this._prev_key_backspace=false;
this._prev_key_esc=false;
var k=dojo.event.browser.keys;
var _c33=true;
switch(evt.key){
case k.KEY_DOWN_ARROW:
if(!this.popupWidget.isShowingNow){
this._startSearchFromInput();
}
this._highlightNextOption();
dojo.event.browser.stopEvent(evt);
return;
case k.KEY_UP_ARROW:
this._highlightPrevOption();
dojo.event.browser.stopEvent(evt);
return;
case k.KEY_TAB:
if(!this.autoComplete&&this.popupWidget.isShowingNow&&this._highlighted_option){
dojo.event.browser.stopEvent(evt);
this._selectOption({"target":this._highlighted_option,"noHide":false});
this._setSelectedRange(this.textInputNode,this.textInputNode.value.length,null);
}else{
this._selectOption();
return;
}
break;
case k.KEY_ENTER:
if(this.popupWidget.isShowingNow){
dojo.event.browser.stopEvent(evt);
}
if(this.autoComplete){
this._selectOption();
return;
}
case " ":
if(this.popupWidget.isShowingNow&&this._highlighted_option){
dojo.event.browser.stopEvent(evt);
this._selectOption();
this._hideResultList();
return;
}
break;
case k.KEY_ESCAPE:
this._hideResultList();
this._prev_key_esc=true;
return;
case k.KEY_BACKSPACE:
this._prev_key_backspace=true;
if(!this.textInputNode.value.length){
this.setAllValues("","");
this._hideResultList();
_c33=false;
}
break;
case k.KEY_RIGHT_ARROW:
case k.KEY_LEFT_ARROW:
_c33=false;
break;
default:
if(evt.charCode==0){
_c33=false;
}
}
if(this.searchTimer){
clearTimeout(this.searchTimer);
}
if(_c33){
this._blurOptionNode();
this.searchTimer=setTimeout(dojo.lang.hitch(this,this._startSearchFromInput),this.searchDelay);
}
},compositionEnd:function(evt){
evt.key=evt.keyCode;
this._handleKeyEvents(evt);
},onKeyUp:function(evt){
this.setValue(this.textInputNode.value);
},setSelectedValue:function(_c36){
this.comboBoxSelectionValue.value=_c36;
},setAllValues:function(_c37,_c38){
this.setSelectedValue(_c38);
this.setValue(_c37);
},_focusOptionNode:function(node){
if(this._highlighted_option!=node){
this._blurOptionNode();
this._highlighted_option=node;
dojo.html.addClass(this._highlighted_option,"dojoComboBoxItemHighlight");
}
},_blurOptionNode:function(){
if(this._highlighted_option){
dojo.html.removeClass(this._highlighted_option,"dojoComboBoxItemHighlight");
this._highlighted_option=null;
}
},_highlightNextOption:function(){
if((!this._highlighted_option)||!this._highlighted_option.parentNode){
this._focusOptionNode(this.optionsListNode.firstChild);
}else{
if(this._highlighted_option.nextSibling){
this._focusOptionNode(this._highlighted_option.nextSibling);
}
}
dojo.html.scrollIntoView(this._highlighted_option);
},_highlightPrevOption:function(){
if(this._highlighted_option&&this._highlighted_option.previousSibling){
this._focusOptionNode(this._highlighted_option.previousSibling);
}else{
this._highlighted_option=null;
this._hideResultList();
return;
}
dojo.html.scrollIntoView(this._highlighted_option);
},_itemMouseOver:function(evt){
if(evt.target===this.optionsListNode){
return;
}
this._focusOptionNode(evt.target);
dojo.html.addClass(this._highlighted_option,"dojoComboBoxItemHighlight");
},_itemMouseOut:function(evt){
if(evt.target===this.optionsListNode){
return;
}
this._blurOptionNode();
},onResize:function(){
var _c3c=dojo.html.getContentBox(this.textInputNode);
if(_c3c.height<=0){
dojo.lang.setTimeout(this,"onResize",100);
return;
}
var _c3d={width:_c3c.height,height:_c3c.height};
dojo.html.setContentBox(this.downArrowNode,_c3d);
},fillInTemplate:function(args,frag){
dojo.html.applyBrowserClass(this.domNode);
var _c40=this.getFragNodeRef(frag);
if(!this.name&&_c40.name){
this.name=_c40.name;
}
this.comboBoxValue.name=this.name;
this.comboBoxSelectionValue.name=this.name+"_selected";
dojo.html.copyStyle(this.domNode,_c40);
dojo.html.copyStyle(this.textInputNode,_c40);
dojo.html.copyStyle(this.downArrowNode,_c40);
with(this.downArrowNode.style){
width="0px";
height="0px";
}
var _c41;
if(this.dataProviderClass){
if(typeof this.dataProviderClass=="string"){
_c41=dojo.evalObjPath(this.dataProviderClass);
}else{
_c41=this.dataProviderClass;
}
}else{
if(this.mode=="remote"){
_c41=dojo.widget.incrementalComboBoxDataProvider;
}else{
_c41=dojo.widget.basicComboBoxDataProvider;
}
}
this.dataProvider=new _c41(this,this.getFragNodeRef(frag));
this.popupWidget=new dojo.widget.createWidget("PopupContainer",{toggle:this.dropdownToggle,toggleDuration:this.toggleDuration});
dojo.event.connect(this,"destroy",this.popupWidget,"destroy");
this.optionsListNode=this.popupWidget.domNode;
this.domNode.appendChild(this.optionsListNode);
dojo.html.addClass(this.optionsListNode,"dojoComboBoxOptions");
dojo.event.connect(this.optionsListNode,"onclick",this,"_selectOption");
dojo.event.connect(this.optionsListNode,"onmouseover",this,"_onMouseOver");
dojo.event.connect(this.optionsListNode,"onmouseout",this,"_onMouseOut");
dojo.event.connect(this.optionsListNode,"onmouseover",this,"_itemMouseOver");
dojo.event.connect(this.optionsListNode,"onmouseout",this,"_itemMouseOut");
},_openResultList:function(_c42){
if(this.disabled){
return;
}
this._clearResultList();
if(!_c42.length){
this._hideResultList();
}
if((this.autoComplete)&&(_c42.length)&&(!this._prev_key_backspace)&&(this.textInputNode.value.length>0)){
var cpos=this._getCaretPos(this.textInputNode);
if((cpos+1)>this.textInputNode.value.length){
this.textInputNode.value+=_c42[0][0].substr(cpos);
this._setSelectedRange(this.textInputNode,cpos,this.textInputNode.value.length);
}
}
var even=true;
while(_c42.length){
var tr=_c42.shift();
if(tr){
var td=document.createElement("div");
td.appendChild(document.createTextNode(tr[0]));
td.setAttribute("resultName",tr[0]);
td.setAttribute("resultValue",tr[1]);
td.className="dojoComboBoxItem "+((even)?"dojoComboBoxItemEven":"dojoComboBoxItemOdd");
even=(!even);
this.optionsListNode.appendChild(td);
}
}
this._showResultList();
},_onFocusInput:function(){
this._hasFocus=true;
},_onBlurInput:function(){
this._hasFocus=false;
this._handleBlurTimer(true,500);
},_handleBlurTimer:function(_c47,_c48){
if(this.blurTimer&&(_c47||_c48)){
clearTimeout(this.blurTimer);
}
if(_c48){
this.blurTimer=dojo.lang.setTimeout(this,"_checkBlurred",_c48);
}
},_onMouseOver:function(evt){
if(!this._mouseover_list){
this._handleBlurTimer(true,0);
this._mouseover_list=true;
}
},_onMouseOut:function(evt){
var _c4b=evt.relatedTarget;
try{
if(!_c4b||_c4b.parentNode!=this.optionsListNode){
this._mouseover_list=false;
this._handleBlurTimer(true,100);
this._tryFocus();
}
}
catch(e){
}
},_isInputEqualToResult:function(_c4c){
var _c4d=this.textInputNode.value;
if(!this.dataProvider.caseSensitive){
_c4d=_c4d.toLowerCase();
_c4c=_c4c.toLowerCase();
}
return (_c4d==_c4c);
},_isValidOption:function(){
var tgt=dojo.html.firstElement(this.optionsListNode);
var _c4f=false;
while(!_c4f&&tgt){
if(this._isInputEqualToResult(tgt.getAttribute("resultName"))){
_c4f=true;
}else{
tgt=dojo.html.nextElement(tgt);
}
}
return _c4f;
},_checkBlurred:function(){
if(!this._hasFocus&&!this._mouseover_list){
this._hideResultList();
if(!this.textInputNode.value.length){
this.setAllValues("","");
return;
}
var _c50=this._isValidOption();
if(this.forceValidOption&&!_c50){
this.setAllValues("","");
return;
}
if(!_c50){
this.setSelectedValue("");
}
}
},_selectOption:function(evt){
var tgt=null;
if(!evt){
evt={target:this._highlighted_option};
}
if(!dojo.html.isDescendantOf(evt.target,this.optionsListNode)){
if(!this.textInputNode.value.length){
return;
}
tgt=dojo.html.firstElement(this.optionsListNode);
if(!tgt||!this._isInputEqualToResult(tgt.getAttribute("resultName"))){
return;
}
}else{
tgt=evt.target;
}
while((tgt.nodeType!=1)||(!tgt.getAttribute("resultName"))){
tgt=tgt.parentNode;
if(tgt===dojo.body()){
return false;
}
}
this.selectedResult=[tgt.getAttribute("resultName"),tgt.getAttribute("resultValue")];
this.setAllValues(tgt.getAttribute("resultName"),tgt.getAttribute("resultValue"));
if(!evt.noHide){
this._hideResultList();
this._setSelectedRange(this.textInputNode,0,null);
}
this._tryFocus();
},_clearResultList:function(){
if(this.optionsListNode.innerHTML){
this.optionsListNode.innerHTML="";
}
},_hideResultList:function(){
this.popupWidget.close();
},_showResultList:function(){
var _c53=this.optionsListNode.childNodes;
if(_c53.length){
var _c54=Math.min(_c53.length,this.maxListLength);
with(this.optionsListNode.style){
display="";
if(_c54==_c53.length){
height="";
}else{
height=_c54*dojo.html.getMarginBox(_c53[0]).height+"px";
}
width=(dojo.html.getMarginBox(this.domNode).width-2)+"px";
}
this.popupWidget.open(this.domNode,this,this.downArrowNode);
}else{
this._hideResultList();
}
},handleArrowClick:function(){
this._handleBlurTimer(true,0);
this._tryFocus();
if(this.popupWidget.isShowingNow){
this._hideResultList();
}else{
this._startSearch("");
}
},_tryFocus:function(){
try{
this.textInputNode.focus();
}
catch(e){
}
},_startSearchFromInput:function(){
this._startSearch(this.textInputNode.value);
},_startSearch:function(key){
this.dataProvider.startSearch(key,dojo.lang.hitch(this,"_openResultList"));
},postCreate:function(){
this.onResize();
dojo.event.connect(this.textInputNode,"onblur",this,"_onBlurInput");
dojo.event.connect(this.textInputNode,"onfocus",this,"_onFocusInput");
if(this.disabled){
this.disable();
}
var s=dojo.widget.html.stabile.getState(this.widgetId);
if(s){
this.setState(s);
}
}});
dojo.provide("struts.widget.ComboBox");
struts.widget.ComboBoxDataProvider=function(_c57,node){
this.data=[];
this.searchLimit=_c57.searchLimit;
this.searchType="STARTSTRING";
this.caseSensitive=false;
this._lastSearch="";
this._lastSearchResults=null;
this.firstRequest=true;
this.cbox=_c57;
this.formId=this.cbox.formId;
this.formFilter=this.cbox.formFilter;
this.getData=function(url){
dojo.html.show(this.cbox.indicator);
dojo.io.bind({url:url,formNode:dojo.byId(this.formId),formFilter:window[this.formFilter],handler:dojo.lang.hitch(this,function(type,data,evt){
dojo.html.hide(this.cbox.indicator);
if(!this.firstRequest){
this.firstRequest=false;
this.cbox.notify.apply(this.cbox,[data,type,evt]);
}
var _c5d=null;
var _c5e=data[this.cbox.dataFieldName];
if(!dojo.lang.isArray(data)){
if(_c5e){
if(dojo.lang.isArray(_c5e)){
_c5d=_c5e;
}else{
if(dojo.lang.isObject(_c5e)){
_c5d=[];
for(var key in _c5e){
_c5d.push([_c5e[key],key]);
}
}
}
}else{
var _c60=[];
for(var key in data){
if(dojo.string.startsWith(key,this.cbox.name)){
_c5d=data[key];
break;
}else{
_c60.push([data[key],key]);
}
if(!_c5d&&dojo.lang.isArray(data[key])&&!dojo.lang.isString(data[key])){
_c5d=data[key];
}
}
if(!_c5d){
_c5d=_c60;
}
}
data=_c5d;
}
this.setData(data);
}),mimetype:"text/json"});
};
this.startSearch=function(_c61,_c62){
this._preformSearch(_c61,_c62);
};
this._preformSearch=function(_c63,_c64){
var st=this.searchType;
var ret=[];
if(!this.caseSensitive){
_c63=_c63.toLowerCase();
}
for(var x=0;x<this.data.length;x++){
if(!this.data[x]){
continue;
}
if((this.searchLimit>0)&&(ret.length>=this.searchLimit)){
break;
}
var _c68=new String((!this.caseSensitive)?this.data[x][0].toLowerCase():this.data[x][0]);
if(_c68.length<_c63.length){
continue;
}
if(st=="STARTSTRING"){
if(_c63==_c68.substr(0,_c63.length)){
ret.push(this.data[x]);
}
}else{
if(st=="SUBSTRING"){
if(_c68.indexOf(_c63)>=0){
ret.push(this.data[x]);
}
}else{
if(st=="STARTWORD"){
var idx=_c68.indexOf(_c63);
if(idx==0){
ret.push(this.data[x]);
}
if(idx<=0){
continue;
}
var _c6a=false;
while(idx!=-1){
if(" ,/(".indexOf(_c68.charAt(idx-1))!=-1){
_c6a=true;
break;
}
idx=_c68.indexOf(_c63,idx+1);
}
if(!_c6a){
continue;
}else{
ret.push(this.data[x]);
}
}
}
}
}
_c64(ret);
};
this.addData=function(_c6b){
this.data=this.data.concat(_c6b);
};
this.setData=function(_c6c){
this.data=_c6c;
for(var i=0;i<this.data.length;i++){
var _c6e=this.data[i];
if(!dojo.lang.isArray(_c6e)){
this.data[i]=[_c6e,_c6e];
}
}
};
if(!dojo.string.isBlank(this.cbox.dataUrl)){
this.getData(this.cbox.dataUrl);
}else{
if((node)&&(node.nodeName.toLowerCase()=="select")){
var opts=node.getElementsByTagName("option");
var ol=opts.length;
var data=[];
for(var x=0;x<ol;x++){
var text=opts[x].textContent||opts[x].innerText||opts[x].innerHTML;
var _c74=[String(text),String(opts[x].value)];
data.push(_c74);
if(opts[x].selected){
this.cbox.setAllValues(_c74[0],_c74[1]);
}
}
this.setData(data);
}
}
};
dojo.widget.defineWidget("struts.widget.ComboBox",dojo.widget.ComboBox,{widgetType:"ComboBox",dropdownHeight:120,dropdownWidth:0,itemHeight:0,listenTopics:"",notifyTopics:"",notifyTopicsArray:null,beforeNotifyTopics:"",beforeNotifyTopicsArray:null,afterNotifyTopics:"",afterNotifyTopicsArray:null,errorNotifyTopics:"",errorNotifyTopicsArray:null,valueNotifyTopics:"",valueNotifyTopicsArray:null,indicator:"",formId:"",formFilter:"",dataProviderClass:"struts.widget.ComboBoxDataProvider",loadOnType:false,loadMinimum:3,initialValue:"",initialKey:"",visibleDownArrow:true,fadeTime:100,searchType:"STARTSTRING",dataFieldName:"",keyName:"",templateCssPath:dojo.uri.dojoUri("struts/ComboBox.css"),searchLimit:30,showResultList:function(){
var _c75=this.optionsListNode.childNodes;
if(_c75.length){
this.optionsListNode.style.width=this.dropdownWidth===0?(dojo.html.getMarginBox(this.domNode).width-2)+"px":this.dropdownWidth+"px";
if(this.itemHeight===0||dojo.string.isBlank(this.textInputNode.value)){
this.optionsListNode.style.height=this.dropdownHeight+"px";
this.optionsListNode.style.display="";
this.itemHeight=dojo.html.getMarginBox(_c75[0]).height;
}
var _c76=this.itemHeight*_c75.length;
if(_c76<this.dropdownHeight){
this.optionsListNode.style.height=_c76+2+"px";
}
this.popupWidget.open(this.domNode,this,this.downArrowNode);
}else{
this._hideResultList();
}
},_openResultList:function(_c77){
if(this.disabled){
return;
}
this._clearResultList();
if(!_c77.length){
this._hideResultList();
}
if((this.autoComplete)&&(_c77.length)&&(!this._prev_key_backspace)&&(this.textInputNode.value.length>0)){
var cpos=this._getCaretPos(this.textInputNode);
if((cpos+1)>this.textInputNode.value.length){
this.textInputNode.value+=_c77[0][0].substr(cpos);
this._setSelectedRange(this.textInputNode,cpos,this.textInputNode.value.length);
}
}
var _c79=this.textInputNode.value;
var even=true;
while(_c77.length){
var tr=_c77.shift();
if(tr){
var td=document.createElement("div");
var text=tr[0];
var i=text.toLowerCase().indexOf(_c79.toLowerCase());
if(i>=0){
var pre=text.substring(0,i);
var _c80=text.substring(i,i+_c79.length);
var post=text.substring(i+_c79.length);
if(!dojo.string.isBlank(pre)){
td.appendChild(document.createTextNode(pre));
}
var _c82=document.createElement("b");
td.appendChild(_c82);
_c82.appendChild(document.createTextNode(_c80));
td.appendChild(document.createTextNode(post));
}else{
td.appendChild(document.createTextNode(tr[0]));
}
td.setAttribute("resultName",tr[0]);
td.setAttribute("resultValue",tr[1]);
td.className="dojoComboBoxItem "+((even)?"dojoComboBoxItemEven":"dojoComboBoxItemOdd");
even=(!even);
this.optionsListNode.appendChild(td);
}
}
this.showResultList();
},postCreate:function(){
struts.widget.ComboBox.superclass.postCreate.apply(this);
var self=this;
if(!dojo.string.isBlank(this.listenTopics)){
var _c84=this.listenTopics.split(",");
for(var i=0;i<_c84.length;i++){
dojo.event.topic.subscribe(_c84[i],function(){
var _c86={cancel:false};
self.notify(this.widgetId,"before",_c86);
if(_c86.cancel){
return;
}
self.clearValues();
self.dataProvider.getData(self.dataUrl);
});
}
}
if(!dojo.string.isBlank(this.notifyTopics)){
this.notifyTopicsArray=this.notifyTopics.split(",");
}
if(!dojo.string.isBlank(this.beforeNotifyTopics)){
this.beforeNotifyTopicsArray=this.beforeNotifyTopics.split(",");
}
if(!dojo.string.isBlank(this.afterNotifyTopics)){
this.afterNotifyTopicsArray=this.afterNotifyTopics.split(",");
}
if(!dojo.string.isBlank(this.errorNotifyTopics)){
this.errorNotifyTopicsArray=this.errorNotifyTopics.split(",");
}
if(!dojo.string.isBlank(this.valueNotifyTopics)){
this.valueNotifyTopicsArray=this.valueNotifyTopics.split(",");
}
this.comboBoxSelectionValue.name=dojo.string.isBlank(this.keyName)?this.name+"Key":this.keyName;
this.comboBoxValue.value=this.initialValue;
this.comboBoxSelectionValue.value=this.initialKey;
this.textInputNode.value=this.initialValue;
if(!this.visibleDownArrow){
dojo.html.hide(this.downArrowNode);
}
if(!dojo.string.isBlank(this.searchType)){
this.dataProvider.searchType=this.searchType.toUpperCase();
}
},clearValues:function(){
this.comboBoxValue.value="";
this.comboBoxSelectionValue.value="";
this.textInputNode.value="";
},onValueChanged:function(data){
this.notify(data,"valuechanged",null);
},notify:function(data,type,e){
if(this.notifyTopicsArray){
var self=this;
dojo.lang.forEach(this.notifyTopicsArray,function(_c8c){
try{
dojo.event.topic.publish(_c8c,data,type,e);
}
catch(ex){
self.log(ex);
}
});
}
var _c8d=null;
switch(type){
case "before":
_c8d=this.beforeNotifyTopicsArray;
break;
case "load":
_c8d=this.afterNotifyTopicsArray;
break;
case "error":
_c8d=this.errorNotifyTopicsArray;
break;
case "valuechanged":
_c8d=this.valueNotifyTopicsArray;
break;
}
this.notifyTo(_c8d,data,type,e);
},notifyTo:function(_c8e,data,type,e){
var self=this;
if(_c8e){
dojo.lang.forEach(_c8e,function(_c93){
try{
dojo.event.topic.publish(_c93,data,type,e);
}
catch(ex){
self.log(ex);
}
});
}
},log:function(text){
dojo.debug("["+(this.widgetId?this.widgetId:"unknown")+"] "+text);
},_startSearchFromInput:function(){
var _c95=this.textInputNode.value;
if(this.loadOnType){
if(_c95.length>=this.loadMinimum){
var _c96=this.dataUrl+(this.dataUrl.indexOf("?")>-1?"&":"?");
_c96+=this.name+"="+_c95;
this.dataProvider.getData(_c96);
this._startSearch(_c95);
}else{
this._hideResultList();
}
}else{
this._startSearch(_c95);
}
}});
dojo.provide("dojo.widget.DropdownContainer");
dojo.widget.defineWidget("dojo.widget.DropdownContainer",dojo.widget.HtmlWidget,{inputWidth:"7em",id:"",inputId:"",inputName:"",iconURL:dojo.uri.moduleUri("dojo.widget","templates/images/combo_box_arrow.png"),copyClasses:false,iconAlt:"",containerToggle:"plain",containerToggleDuration:150,templateString:"<span style=\"white-space:nowrap\"><input type=\"hidden\" name=\"\" value=\"\" dojoAttachPoint=\"valueNode\" /><input name=\"\" type=\"text\" value=\"\" style=\"vertical-align:middle;\" dojoAttachPoint=\"inputNode\" autocomplete=\"off\" /> <img src=\"${this.iconURL}\" alt=\"${this.iconAlt}\" dojoAttachEvent=\"onclick:onIconClick\" dojoAttachPoint=\"buttonNode\" style=\"vertical-align:middle; cursor:pointer; cursor:hand\" /></span>",templateCssPath:"",isContainer:true,attachTemplateNodes:function(){
dojo.widget.DropdownContainer.superclass.attachTemplateNodes.apply(this,arguments);
this.popup=dojo.widget.createWidget("PopupContainer",{toggle:this.containerToggle,toggleDuration:this.containerToggleDuration});
this.containerNode=this.popup.domNode;
},fillInTemplate:function(args,frag){
this.domNode.appendChild(this.popup.domNode);
if(this.id){
this.domNode.id=this.id;
}
if(this.inputId){
this.inputNode.id=this.inputId;
}
if(this.inputName){
this.inputNode.name=this.inputName;
}
this.inputNode.style.width=this.inputWidth;
this.inputNode.disabled=this.disabled;
if(this.copyClasses){
this.inputNode.style="";
this.inputNode.className=this.getFragNodeRef(frag).className;
}
dojo.event.connect(this.inputNode,"onchange",this,"onInputChange");
},onIconClick:function(evt){
if(this.disabled){
return;
}
if(!this.popup.isShowingNow){
this.popup.open(this.inputNode,this,this.buttonNode);
}else{
this.popup.close();
}
},hideContainer:function(){
if(this.popup.isShowingNow){
this.popup.close();
}
},onInputChange:function(){
},enable:function(){
this.inputNode.disabled=false;
dojo.widget.DropdownContainer.superclass.enable.apply(this,arguments);
},disable:function(){
this.inputNode.disabled=true;
dojo.widget.DropdownContainer.superclass.disable.apply(this,arguments);
}});
dojo.provide("dojo.date.serialize");
dojo.date.setIso8601=function(_c9a,_c9b){
var _c9c=(_c9b.indexOf("T")==-1)?_c9b.split(" "):_c9b.split("T");
_c9a=dojo.date.setIso8601Date(_c9a,_c9c[0]);
if(_c9c.length==2){
_c9a=dojo.date.setIso8601Time(_c9a,_c9c[1]);
}
return _c9a;
};
dojo.date.fromIso8601=function(_c9d){
return dojo.date.setIso8601(new Date(0,0),_c9d);
};
dojo.date.setIso8601Date=function(_c9e,_c9f){
var _ca0="^([0-9]{4})((-?([0-9]{2})(-?([0-9]{2}))?)|"+"(-?([0-9]{3}))|(-?W([0-9]{2})(-?([1-7]))?))?$";
var d=_c9f.match(new RegExp(_ca0));
if(!d){
dojo.debug("invalid date string: "+_c9f);
return null;
}
var year=d[1];
var _ca3=d[4];
var date=d[6];
var _ca5=d[8];
var week=d[10];
var _ca7=d[12]?d[12]:1;
_c9e.setFullYear(year);
if(_ca5){
_c9e.setMonth(0);
_c9e.setDate(Number(_ca5));
}else{
if(week){
_c9e.setMonth(0);
_c9e.setDate(1);
var gd=_c9e.getDay();
var day=gd?gd:7;
var _caa=Number(_ca7)+(7*Number(week));
if(day<=4){
_c9e.setDate(_caa+1-day);
}else{
_c9e.setDate(_caa+8-day);
}
}else{
if(_ca3){
_c9e.setDate(1);
_c9e.setMonth(_ca3-1);
}
if(date){
_c9e.setDate(date);
}
}
}
return _c9e;
};
dojo.date.fromIso8601Date=function(_cab){
return dojo.date.setIso8601Date(new Date(0,0),_cab);
};
dojo.date.setIso8601Time=function(_cac,_cad){
var _cae="Z|(([-+])([0-9]{2})(:?([0-9]{2}))?)$";
var d=_cad.match(new RegExp(_cae));
var _cb0=0;
if(d){
if(d[0]!="Z"){
_cb0=(Number(d[3])*60)+Number(d[5]);
_cb0*=((d[2]=="-")?1:-1);
}
_cb0-=_cac.getTimezoneOffset();
_cad=_cad.substr(0,_cad.length-d[0].length);
}
var _cb1="^([0-9]{2})(:?([0-9]{2})(:?([0-9]{2})(.([0-9]+))?)?)?$";
d=_cad.match(new RegExp(_cb1));
if(!d){
dojo.debug("invalid time string: "+_cad);
return null;
}
var _cb2=d[1];
var mins=Number((d[3])?d[3]:0);
var secs=(d[5])?d[5]:0;
var ms=d[7]?(Number("0."+d[7])*1000):0;
_cac.setHours(_cb2);
_cac.setMinutes(mins);
_cac.setSeconds(secs);
_cac.setMilliseconds(ms);
if(_cb0!==0){
_cac.setTime(_cac.getTime()+_cb0*60000);
}
return _cac;
};
dojo.date.fromIso8601Time=function(_cb6){
return dojo.date.setIso8601Time(new Date(0,0),_cb6);
};
dojo.date.toRfc3339=function(_cb7,_cb8){
if(!_cb7){
_cb7=new Date();
}
var _=dojo.string.pad;
var _cba=[];
if(_cb8!="timeOnly"){
var date=[_(_cb7.getFullYear(),4),_(_cb7.getMonth()+1,2),_(_cb7.getDate(),2)].join("-");
_cba.push(date);
}
if(_cb8!="dateOnly"){
var time=[_(_cb7.getHours(),2),_(_cb7.getMinutes(),2),_(_cb7.getSeconds(),2)].join(":");
var _cbd=_cb7.getTimezoneOffset();
time+=(_cbd>0?"-":"+")+_(Math.floor(Math.abs(_cbd)/60),2)+":"+_(Math.abs(_cbd)%60,2);
_cba.push(time);
}
return _cba.join("T");
};
dojo.date.fromRfc3339=function(_cbe){
if(_cbe.indexOf("Tany")!=-1){
_cbe=_cbe.replace("Tany","");
}
var _cbf=new Date();
return dojo.date.setIso8601(_cbf,_cbe);
};
dojo.provide("dojo.date.common");
dojo.date.setDayOfYear=function(_cc0,_cc1){
_cc0.setMonth(0);
_cc0.setDate(_cc1);
return _cc0;
};
dojo.date.getDayOfYear=function(_cc2){
var _cc3=_cc2.getFullYear();
var _cc4=new Date(_cc3-1,11,31);
return Math.floor((_cc2.getTime()-_cc4.getTime())/86400000);
};
dojo.date.setWeekOfYear=function(_cc5,week,_cc7){
if(arguments.length==1){
_cc7=0;
}
dojo.unimplemented("dojo.date.setWeekOfYear");
};
dojo.date.getWeekOfYear=function(_cc8,_cc9){
if(arguments.length==1){
_cc9=0;
}
var _cca=new Date(_cc8.getFullYear(),0,1);
var day=_cca.getDay();
_cca.setDate(_cca.getDate()-day+_cc9-(day>_cc9?7:0));
return Math.floor((_cc8.getTime()-_cca.getTime())/604800000);
};
dojo.date.setIsoWeekOfYear=function(_ccc,week,_cce){
if(arguments.length==1){
_cce=1;
}
dojo.unimplemented("dojo.date.setIsoWeekOfYear");
};
dojo.date.getIsoWeekOfYear=function(_ccf,_cd0){
if(arguments.length==1){
_cd0=1;
}
dojo.unimplemented("dojo.date.getIsoWeekOfYear");
};
dojo.date.shortTimezones=["IDLW","BET","HST","MART","AKST","PST","MST","CST","EST","AST","NFT","BST","FST","AT","GMT","CET","EET","MSK","IRT","GST","AFT","AGTT","IST","NPT","ALMT","MMT","JT","AWST","JST","ACST","AEST","LHST","VUT","NFT","NZT","CHAST","PHOT","LINT"];
dojo.date.timezoneOffsets=[-720,-660,-600,-570,-540,-480,-420,-360,-300,-240,-210,-180,-120,-60,0,60,120,180,210,240,270,300,330,345,360,390,420,480,540,570,600,630,660,690,720,765,780,840];
dojo.date.getDaysInMonth=function(_cd1){
var _cd2=_cd1.getMonth();
var days=[31,28,31,30,31,30,31,31,30,31,30,31];
if(_cd2==1&&dojo.date.isLeapYear(_cd1)){
return 29;
}else{
return days[_cd2];
}
};
dojo.date.isLeapYear=function(_cd4){
var year=_cd4.getFullYear();
return (year%400==0)?true:(year%100==0)?false:(year%4==0)?true:false;
};
dojo.date.getTimezoneName=function(_cd6){
var str=_cd6.toString();
var tz="";
var _cd9;
var pos=str.indexOf("(");
if(pos>-1){
pos++;
tz=str.substring(pos,str.indexOf(")"));
}else{
var pat=/([A-Z\/]+) \d{4}$/;
if((_cd9=str.match(pat))){
tz=_cd9[1];
}else{
str=_cd6.toLocaleString();
pat=/ ([A-Z\/]+)$/;
if((_cd9=str.match(pat))){
tz=_cd9[1];
}
}
}
return tz=="AM"||tz=="PM"?"":tz;
};
dojo.date.getOrdinal=function(_cdc){
var date=_cdc.getDate();
if(date%100!=11&&date%10==1){
return "st";
}else{
if(date%100!=12&&date%10==2){
return "nd";
}else{
if(date%100!=13&&date%10==3){
return "rd";
}else{
return "th";
}
}
}
};
dojo.date.compareTypes={DATE:1,TIME:2};
dojo.date.compare=function(_cde,_cdf,_ce0){
var dA=_cde;
var dB=_cdf||new Date();
var now=new Date();
with(dojo.date.compareTypes){
var opt=_ce0||(DATE|TIME);
var d1=new Date((opt&DATE)?dA.getFullYear():now.getFullYear(),(opt&DATE)?dA.getMonth():now.getMonth(),(opt&DATE)?dA.getDate():now.getDate(),(opt&TIME)?dA.getHours():0,(opt&TIME)?dA.getMinutes():0,(opt&TIME)?dA.getSeconds():0);
var d2=new Date((opt&DATE)?dB.getFullYear():now.getFullYear(),(opt&DATE)?dB.getMonth():now.getMonth(),(opt&DATE)?dB.getDate():now.getDate(),(opt&TIME)?dB.getHours():0,(opt&TIME)?dB.getMinutes():0,(opt&TIME)?dB.getSeconds():0);
}
if(d1.valueOf()>d2.valueOf()){
return 1;
}
if(d1.valueOf()<d2.valueOf()){
return -1;
}
return 0;
};
dojo.date.dateParts={YEAR:0,MONTH:1,DAY:2,HOUR:3,MINUTE:4,SECOND:5,MILLISECOND:6,QUARTER:7,WEEK:8,WEEKDAY:9};
dojo.date.add=function(dt,_ce8,incr){
if(typeof dt=="number"){
dt=new Date(dt);
}
function fixOvershoot(){
if(sum.getDate()<dt.getDate()){
sum.setDate(0);
}
}
var sum=new Date(dt);
with(dojo.date.dateParts){
switch(_ce8){
case YEAR:
sum.setFullYear(dt.getFullYear()+incr);
fixOvershoot();
break;
case QUARTER:
incr*=3;
case MONTH:
sum.setMonth(dt.getMonth()+incr);
fixOvershoot();
break;
case WEEK:
incr*=7;
case DAY:
sum.setDate(dt.getDate()+incr);
break;
case WEEKDAY:
var dat=dt.getDate();
var _cec=0;
var days=0;
var strt=0;
var trgt=0;
var adj=0;
var mod=incr%5;
if(mod==0){
days=(incr>0)?5:-5;
_cec=(incr>0)?((incr-5)/5):((incr+5)/5);
}else{
days=mod;
_cec=parseInt(incr/5);
}
strt=dt.getDay();
if(strt==6&&incr>0){
adj=1;
}else{
if(strt==0&&incr<0){
adj=-1;
}
}
trgt=(strt+days);
if(trgt==0||trgt==6){
adj=(incr>0)?2:-2;
}
sum.setDate(dat+(7*_cec)+days+adj);
break;
case HOUR:
sum.setHours(sum.getHours()+incr);
break;
case MINUTE:
sum.setMinutes(sum.getMinutes()+incr);
break;
case SECOND:
sum.setSeconds(sum.getSeconds()+incr);
break;
case MILLISECOND:
sum.setMilliseconds(sum.getMilliseconds()+incr);
break;
default:
break;
}
}
return sum;
};
dojo.date.diff=function(dtA,dtB,_cf4){
if(typeof dtA=="number"){
dtA=new Date(dtA);
}
if(typeof dtB=="number"){
dtB=new Date(dtB);
}
var _cf5=dtB.getFullYear()-dtA.getFullYear();
var _cf6=(dtB.getMonth()-dtA.getMonth())+(_cf5*12);
var _cf7=dtB.getTime()-dtA.getTime();
var _cf8=_cf7/1000;
var _cf9=_cf8/60;
var _cfa=_cf9/60;
var _cfb=_cfa/24;
var _cfc=_cfb/7;
var _cfd=0;
with(dojo.date.dateParts){
switch(_cf4){
case YEAR:
_cfd=_cf5;
break;
case QUARTER:
var mA=dtA.getMonth();
var mB=dtB.getMonth();
var qA=Math.floor(mA/3)+1;
var qB=Math.floor(mB/3)+1;
qB+=(_cf5*4);
_cfd=qB-qA;
break;
case MONTH:
_cfd=_cf6;
break;
case WEEK:
_cfd=parseInt(_cfc);
break;
case DAY:
_cfd=_cfb;
break;
case WEEKDAY:
var days=Math.round(_cfb);
var _d03=parseInt(days/7);
var mod=days%7;
if(mod==0){
days=_d03*5;
}else{
var adj=0;
var aDay=dtA.getDay();
var bDay=dtB.getDay();
_d03=parseInt(days/7);
mod=days%7;
var _d08=new Date(dtA);
_d08.setDate(_d08.getDate()+(_d03*7));
var _d09=_d08.getDay();
if(_cfb>0){
switch(true){
case aDay==6:
adj=-1;
break;
case aDay==0:
adj=0;
break;
case bDay==6:
adj=-1;
break;
case bDay==0:
adj=-2;
break;
case (_d09+mod)>5:
adj=-2;
break;
default:
break;
}
}else{
if(_cfb<0){
switch(true){
case aDay==6:
adj=0;
break;
case aDay==0:
adj=1;
break;
case bDay==6:
adj=2;
break;
case bDay==0:
adj=1;
break;
case (_d09+mod)<0:
adj=2;
break;
default:
break;
}
}
}
days+=adj;
days-=(_d03*2);
}
_cfd=days;
break;
case HOUR:
_cfd=_cfa;
break;
case MINUTE:
_cfd=_cf9;
break;
case SECOND:
_cfd=_cf8;
break;
case MILLISECOND:
_cfd=_cf7;
break;
default:
break;
}
}
return Math.round(_cfd);
};
dojo.provide("dojo.date.supplemental");
dojo.date.getFirstDayOfWeek=function(_d0a){
var _d0b={mv:5,ae:6,af:6,bh:6,dj:6,dz:6,eg:6,er:6,et:6,iq:6,ir:6,jo:6,ke:6,kw:6,lb:6,ly:6,ma:6,om:6,qa:6,sa:6,sd:6,so:6,tn:6,ye:6,as:0,au:0,az:0,bw:0,ca:0,cn:0,fo:0,ge:0,gl:0,gu:0,hk:0,ie:0,il:0,is:0,jm:0,jp:0,kg:0,kr:0,la:0,mh:0,mo:0,mp:0,mt:0,nz:0,ph:0,pk:0,sg:0,th:0,tt:0,tw:0,um:0,us:0,uz:0,vi:0,za:0,zw:0,et:0,mw:0,ng:0,tj:0,gb:0,sy:4};
_d0a=dojo.hostenv.normalizeLocale(_d0a);
var _d0c=_d0a.split("-")[1];
var dow=_d0b[_d0c];
return (typeof dow=="undefined")?1:dow;
};
dojo.date.getWeekend=function(_d0e){
var _d0f={eg:5,il:5,sy:5,"in":0,ae:4,bh:4,dz:4,iq:4,jo:4,kw:4,lb:4,ly:4,ma:4,om:4,qa:4,sa:4,sd:4,tn:4,ye:4};
var _d10={ae:5,bh:5,dz:5,iq:5,jo:5,kw:5,lb:5,ly:5,ma:5,om:5,qa:5,sa:5,sd:5,tn:5,ye:5,af:5,ir:5,eg:6,il:6,sy:6};
_d0e=dojo.hostenv.normalizeLocale(_d0e);
var _d11=_d0e.split("-")[1];
var _d12=_d0f[_d11];
var end=_d10[_d11];
if(typeof _d12=="undefined"){
_d12=6;
}
if(typeof end=="undefined"){
end=0;
}
return {start:_d12,end:end};
};
dojo.date.isWeekend=function(_d14,_d15){
var _d16=dojo.date.getWeekend(_d15);
var day=(_d14||new Date()).getDay();
if(_d16.end<_d16.start){
_d16.end+=7;
if(day<_d16.start){
day+=7;
}
}
return day>=_d16.start&&day<=_d16.end;
};
dojo.provide("dojo.i18n.common");
dojo.i18n.getLocalization=function(_d18,_d19,_d1a){
dojo.hostenv.preloadLocalizations();
_d1a=dojo.hostenv.normalizeLocale(_d1a);
var _d1b=_d1a.split("-");
var _d1c=[_d18,"nls",_d19].join(".");
var _d1d=dojo.hostenv.findModule(_d1c,true);
var _d1e;
for(var i=_d1b.length;i>0;i--){
var loc=_d1b.slice(0,i).join("_");
if(_d1d[loc]){
_d1e=_d1d[loc];
break;
}
}
if(!_d1e){
_d1e=_d1d.ROOT;
}
if(_d1e){
var _d21=function(){
};
_d21.prototype=_d1e;
return new _d21();
}
dojo.raise("Bundle not found: "+_d19+" in "+_d18+" , locale="+_d1a);
};
dojo.i18n.isLTR=function(_d22){
var lang=dojo.hostenv.normalizeLocale(_d22).split("-")[0];
var RTL={ar:true,fa:true,he:true,ur:true,yi:true};
return !RTL[lang];
};
dojo.provide("dojo.date.format");
(function(){
dojo.date.format=function(_d25,_d26){
if(typeof _d26=="string"){
dojo.deprecated("dojo.date.format","To format dates with POSIX-style strings, please use dojo.date.strftime instead","0.5");
return dojo.date.strftime(_d25,_d26);
}
function formatPattern(_d27,_d28){
return _d28.replace(/([a-z])\1*/ig,function(_d29){
var s;
var c=_d29.charAt(0);
var l=_d29.length;
var pad;
var _d2e=["abbr","wide","narrow"];
switch(c){
case "G":
if(l>3){
dojo.unimplemented("Era format not implemented");
}
s=info.eras[_d27.getFullYear()<0?1:0];
break;
case "y":
s=_d27.getFullYear();
switch(l){
case 1:
break;
case 2:
s=String(s).substr(-2);
break;
default:
pad=true;
}
break;
case "Q":
case "q":
s=Math.ceil((_d27.getMonth()+1)/3);
switch(l){
case 1:
case 2:
pad=true;
break;
case 3:
case 4:
dojo.unimplemented("Quarter format not implemented");
}
break;
case "M":
case "L":
var m=_d27.getMonth();
var _d31;
switch(l){
case 1:
case 2:
s=m+1;
pad=true;
break;
case 3:
case 4:
case 5:
_d31=_d2e[l-3];
break;
}
if(_d31){
var type=(c=="L")?"standalone":"format";
var prop=["months",type,_d31].join("-");
s=info[prop][m];
}
break;
case "w":
var _d34=0;
s=dojo.date.getWeekOfYear(_d27,_d34);
pad=true;
break;
case "d":
s=_d27.getDate();
pad=true;
break;
case "D":
s=dojo.date.getDayOfYear(_d27);
pad=true;
break;
case "E":
case "e":
case "c":
var d=_d27.getDay();
var _d31;
switch(l){
case 1:
case 2:
if(c=="e"){
var _d36=dojo.date.getFirstDayOfWeek(_d26.locale);
d=(d-_d36+7)%7;
}
if(c!="c"){
s=d+1;
pad=true;
break;
}
case 3:
case 4:
case 5:
_d31=_d2e[l-3];
break;
}
if(_d31){
var type=(c=="c")?"standalone":"format";
var prop=["days",type,_d31].join("-");
s=info[prop][d];
}
break;
case "a":
var _d37=(_d27.getHours()<12)?"am":"pm";
s=info[_d37];
break;
case "h":
case "H":
case "K":
case "k":
var h=_d27.getHours();
switch(c){
case "h":
s=(h%12)||12;
break;
case "H":
s=h;
break;
case "K":
s=(h%12);
break;
case "k":
s=h||24;
break;
}
pad=true;
break;
case "m":
s=_d27.getMinutes();
pad=true;
break;
case "s":
s=_d27.getSeconds();
pad=true;
break;
case "S":
s=Math.round(_d27.getMilliseconds()*Math.pow(10,l-3));
break;
case "v":
case "z":
s=dojo.date.getTimezoneName(_d27);
if(s){
break;
}
l=4;
case "Z":
var _d39=_d27.getTimezoneOffset();
var tz=[(_d39<=0?"+":"-"),dojo.string.pad(Math.floor(Math.abs(_d39)/60),2),dojo.string.pad(Math.abs(_d39)%60,2)];
if(l==4){
tz.splice(0,0,"GMT");
tz.splice(3,0,":");
}
s=tz.join("");
break;
case "Y":
case "u":
case "W":
case "F":
case "g":
case "A":
dojo.debug(_d29+" modifier not yet implemented");
s="?";
break;
default:
dojo.raise("dojo.date.format: invalid pattern char: "+_d28);
}
if(pad){
s=dojo.string.pad(s,l);
}
return s;
});
}
_d26=_d26||{};
var _d3b=dojo.hostenv.normalizeLocale(_d26.locale);
var _d3c=_d26.formatLength||"full";
var info=dojo.date._getGregorianBundle(_d3b);
var str=[];
var _d3e=dojo.lang.curry(this,formatPattern,_d25);
if(_d26.selector!="timeOnly"){
var _d3f=_d26.datePattern||info["dateFormat-"+_d3c];
if(_d3f){
str.push(_processPattern(_d3f,_d3e));
}
}
if(_d26.selector!="dateOnly"){
var _d40=_d26.timePattern||info["timeFormat-"+_d3c];
if(_d40){
str.push(_processPattern(_d40,_d3e));
}
}
var _d41=str.join(" ");
return _d41;
};
dojo.date.parse=function(_d42,_d43){
_d43=_d43||{};
var _d44=dojo.hostenv.normalizeLocale(_d43.locale);
var info=dojo.date._getGregorianBundle(_d44);
var _d46=_d43.formatLength||"full";
if(!_d43.selector){
_d43.selector="dateOnly";
}
var _d47=_d43.datePattern||info["dateFormat-"+_d46];
var _d48=_d43.timePattern||info["timeFormat-"+_d46];
var _d49;
if(_d43.selector=="dateOnly"){
_d49=_d47;
}else{
if(_d43.selector=="timeOnly"){
_d49=_d48;
}else{
if(_d43.selector=="dateTime"){
_d49=_d47+" "+_d48;
}else{
var msg="dojo.date.parse: Unknown selector param passed: '"+_d43.selector+"'.";
msg+=" Defaulting to date pattern.";
dojo.debug(msg);
_d49=_d47;
}
}
}
var _d4b=[];
var _d4c=_processPattern(_d49,dojo.lang.curry(this,_buildDateTimeRE,_d4b,info,_d43));
var _d4d=new RegExp("^"+_d4c+"$");
var _d4e=_d4d.exec(_d42);
if(!_d4e){
return null;
}
var _d4f=["abbr","wide","narrow"];
var _d50=new Date(1972,0);
var _d51={};
for(var i=1;i<_d4e.length;i++){
var grp=_d4b[i-1];
var l=grp.length;
var v=_d4e[i];
switch(grp.charAt(0)){
case "y":
if(l!=2){
_d50.setFullYear(v);
_d51.year=v;
}else{
if(v<100){
v=Number(v);
var year=""+new Date().getFullYear();
var _d57=year.substring(0,2)*100;
var _d58=Number(year.substring(2,4));
var _d59=Math.min(_d58+20,99);
var num=(v<_d59)?_d57+v:_d57-100+v;
_d50.setFullYear(num);
_d51.year=num;
}else{
if(_d43.strict){
return null;
}
_d50.setFullYear(v);
_d51.year=v;
}
}
break;
case "M":
if(l>2){
if(!_d43.strict){
v=v.replace(/\./g,"");
v=v.toLowerCase();
}
var _d5b=info["months-format-"+_d4f[l-3]].concat();
for(var j=0;j<_d5b.length;j++){
if(!_d43.strict){
_d5b[j]=_d5b[j].toLowerCase();
}
if(v==_d5b[j]){
_d50.setMonth(j);
_d51.month=j;
break;
}
}
if(j==_d5b.length){
dojo.debug("dojo.date.parse: Could not parse month name: '"+v+"'.");
return null;
}
}else{
_d50.setMonth(v-1);
_d51.month=v-1;
}
break;
case "E":
case "e":
if(!_d43.strict){
v=v.toLowerCase();
}
var days=info["days-format-"+_d4f[l-3]].concat();
for(var j=0;j<days.length;j++){
if(!_d43.strict){
days[j]=days[j].toLowerCase();
}
if(v==days[j]){
break;
}
}
if(j==days.length){
dojo.debug("dojo.date.parse: Could not parse weekday name: '"+v+"'.");
return null;
}
break;
case "d":
_d50.setDate(v);
_d51.date=v;
break;
case "a":
var am=_d43.am||info.am;
var pm=_d43.pm||info.pm;
if(!_d43.strict){
v=v.replace(/\./g,"").toLowerCase();
am=am.replace(/\./g,"").toLowerCase();
pm=pm.replace(/\./g,"").toLowerCase();
}
if(_d43.strict&&v!=am&&v!=pm){
dojo.debug("dojo.date.parse: Could not parse am/pm part.");
return null;
}
var _d60=_d50.getHours();
if(v==pm&&_d60<12){
_d50.setHours(_d60+12);
}else{
if(v==am&&_d60==12){
_d50.setHours(0);
}
}
break;
case "K":
if(v==24){
v=0;
}
case "h":
case "H":
case "k":
if(v>23){
dojo.debug("dojo.date.parse: Illegal hours value");
return null;
}
_d50.setHours(v);
break;
case "m":
_d50.setMinutes(v);
break;
case "s":
_d50.setSeconds(v);
break;
case "S":
_d50.setMilliseconds(v);
break;
default:
dojo.unimplemented("dojo.date.parse: unsupported pattern char="+grp.charAt(0));
}
}
if(_d51.year&&_d50.getFullYear()!=_d51.year){
dojo.debug("Parsed year: '"+_d50.getFullYear()+"' did not match input year: '"+_d51.year+"'.");
return null;
}
if(_d51.month&&_d50.getMonth()!=_d51.month){
dojo.debug("Parsed month: '"+_d50.getMonth()+"' did not match input month: '"+_d51.month+"'.");
return null;
}
if(_d51.date&&_d50.getDate()!=_d51.date){
dojo.debug("Parsed day of month: '"+_d50.getDate()+"' did not match input day of month: '"+_d51.date+"'.");
return null;
}
return _d50;
};
function _processPattern(_d61,_d62,_d63,_d64){
var _d65=function(x){
return x;
};
_d62=_d62||_d65;
_d63=_d63||_d65;
_d64=_d64||_d65;
var _d67=_d61.match(/(''|[^'])+/g);
var _d68=false;
for(var i=0;i<_d67.length;i++){
if(!_d67[i]){
_d67[i]="";
}else{
_d67[i]=(_d68?_d63:_d62)(_d67[i]);
_d68=!_d68;
}
}
return _d64(_d67.join(""));
}
function _buildDateTimeRE(_d6a,info,_d6c,_d6d){
return _d6d.replace(/([a-z])\1*/ig,function(_d6e){
var s;
var c=_d6e.charAt(0);
var l=_d6e.length;
switch(c){
case "y":
s="\\d"+((l==2)?"{2,4}":"+");
break;
case "M":
s=(l>2)?"\\S+":"\\d{1,2}";
break;
case "d":
s="\\d{1,2}";
break;
case "E":
s="\\S+";
break;
case "h":
case "H":
case "K":
case "k":
s="\\d{1,2}";
break;
case "m":
case "s":
s="[0-5]\\d";
break;
case "S":
s="\\d{1,3}";
break;
case "a":
var am=_d6c.am||info.am||"AM";
var pm=_d6c.pm||info.pm||"PM";
if(_d6c.strict){
s=am+"|"+pm;
}else{
s=am;
s+=(am!=am.toLowerCase())?"|"+am.toLowerCase():"";
s+="|";
s+=(pm!=pm.toLowerCase())?pm+"|"+pm.toLowerCase():pm;
}
break;
default:
dojo.unimplemented("parse of date format, pattern="+_d6d);
}
if(_d6a){
_d6a.push(_d6e);
}
return "\\s*("+s+")\\s*";
});
}
})();
dojo.date.strftime=function(_d74,_d75,_d76){
var _d77=null;
function _(s,n){
return dojo.string.pad(s,n||2,_d77||"0");
}
var info=dojo.date._getGregorianBundle(_d76);
function $(_d7b){
switch(_d7b){
case "a":
return dojo.date.getDayShortName(_d74,_d76);
case "A":
return dojo.date.getDayName(_d74,_d76);
case "b":
case "h":
return dojo.date.getMonthShortName(_d74,_d76);
case "B":
return dojo.date.getMonthName(_d74,_d76);
case "c":
return dojo.date.format(_d74,{locale:_d76});
case "C":
return _(Math.floor(_d74.getFullYear()/100));
case "d":
return _(_d74.getDate());
case "D":
return $("m")+"/"+$("d")+"/"+$("y");
case "e":
if(_d77==null){
_d77=" ";
}
return _(_d74.getDate());
case "f":
if(_d77==null){
_d77=" ";
}
return _(_d74.getMonth()+1);
case "g":
break;
case "G":
dojo.unimplemented("unimplemented modifier 'G'");
break;
case "F":
return $("Y")+"-"+$("m")+"-"+$("d");
case "H":
return _(_d74.getHours());
case "I":
return _(_d74.getHours()%12||12);
case "j":
return _(dojo.date.getDayOfYear(_d74),3);
case "k":
if(_d77==null){
_d77=" ";
}
return _(_d74.getHours());
case "l":
if(_d77==null){
_d77=" ";
}
return _(_d74.getHours()%12||12);
case "m":
return _(_d74.getMonth()+1);
case "M":
return _(_d74.getMinutes());
case "n":
return "\n";
case "p":
return info[_d74.getHours()<12?"am":"pm"];
case "r":
return $("I")+":"+$("M")+":"+$("S")+" "+$("p");
case "R":
return $("H")+":"+$("M");
case "S":
return _(_d74.getSeconds());
case "t":
return "\t";
case "T":
return $("H")+":"+$("M")+":"+$("S");
case "u":
return String(_d74.getDay()||7);
case "U":
return _(dojo.date.getWeekOfYear(_d74));
case "V":
return _(dojo.date.getIsoWeekOfYear(_d74));
case "W":
return _(dojo.date.getWeekOfYear(_d74,1));
case "w":
return String(_d74.getDay());
case "x":
return dojo.date.format(_d74,{selector:"dateOnly",locale:_d76});
case "X":
return dojo.date.format(_d74,{selector:"timeOnly",locale:_d76});
case "y":
return _(_d74.getFullYear()%100);
case "Y":
return String(_d74.getFullYear());
case "z":
var _d7c=_d74.getTimezoneOffset();
return (_d7c>0?"-":"+")+_(Math.floor(Math.abs(_d7c)/60))+":"+_(Math.abs(_d7c)%60);
case "Z":
return dojo.date.getTimezoneName(_d74);
case "%":
return "%";
}
}
var _d7d="";
var i=0;
var _d7f=0;
var _d80=null;
while((_d7f=_d75.indexOf("%",i))!=-1){
_d7d+=_d75.substring(i,_d7f++);
switch(_d75.charAt(_d7f++)){
case "_":
_d77=" ";
break;
case "-":
_d77="";
break;
case "0":
_d77="0";
break;
case "^":
_d80="upper";
break;
case "*":
_d80="lower";
break;
case "#":
_d80="swap";
break;
default:
_d77=null;
_d7f--;
break;
}
var _d81=$(_d75.charAt(_d7f++));
switch(_d80){
case "upper":
_d81=_d81.toUpperCase();
break;
case "lower":
_d81=_d81.toLowerCase();
break;
case "swap":
var _d82=_d81.toLowerCase();
var _d83="";
var j=0;
var ch="";
while(j<_d81.length){
ch=_d81.charAt(j);
_d83+=(ch==_d82.charAt(j))?ch.toUpperCase():ch.toLowerCase();
j++;
}
_d81=_d83;
break;
default:
break;
}
_d80=null;
_d7d+=_d81;
i=_d7f;
}
_d7d+=_d75.substring(i);
return _d7d;
};
(function(){
var _d86=[];
dojo.date.addCustomFormats=function(_d87,_d88){
_d86.push({pkg:_d87,name:_d88});
};
dojo.date._getGregorianBundle=function(_d89){
var _d8a={};
dojo.lang.forEach(_d86,function(desc){
var _d8c=dojo.i18n.getLocalization(desc.pkg,desc.name,_d89);
_d8a=dojo.lang.mixin(_d8a,_d8c);
},this);
return _d8a;
};
})();
dojo.date.addCustomFormats("dojo.i18n.calendar","gregorian");
dojo.date.addCustomFormats("dojo.i18n.calendar","gregorianExtras");
dojo.date.getNames=function(item,type,use,_d90){
var _d91;
var _d92=dojo.date._getGregorianBundle(_d90);
var _d93=[item,use,type];
if(use=="standAlone"){
_d91=_d92[_d93.join("-")];
}
_d93[1]="format";
return (_d91||_d92[_d93.join("-")]).concat();
};
dojo.date.getDayName=function(_d94,_d95){
return dojo.date.getNames("days","wide","format",_d95)[_d94.getDay()];
};
dojo.date.getDayShortName=function(_d96,_d97){
return dojo.date.getNames("days","abbr","format",_d97)[_d96.getDay()];
};
dojo.date.getMonthName=function(_d98,_d99){
return dojo.date.getNames("months","wide","format",_d99)[_d98.getMonth()];
};
dojo.date.getMonthShortName=function(_d9a,_d9b){
return dojo.date.getNames("months","abbr","format",_d9b)[_d9a.getMonth()];
};
dojo.date.toRelativeString=function(_d9c){
var now=new Date();
var diff=(now-_d9c)/1000;
var end=" ago";
var _da0=false;
if(diff<0){
_da0=true;
end=" from now";
diff=-diff;
}
if(diff<60){
diff=Math.round(diff);
return diff+" second"+(diff==1?"":"s")+end;
}
if(diff<60*60){
diff=Math.round(diff/60);
return diff+" minute"+(diff==1?"":"s")+end;
}
if(diff<60*60*24){
diff=Math.round(diff/3600);
return diff+" hour"+(diff==1?"":"s")+end;
}
if(diff<60*60*24*7){
diff=Math.round(diff/(3600*24));
if(diff==1){
return _da0?"Tomorrow":"Yesterday";
}else{
return diff+" days"+end;
}
}
return dojo.date.format(_d9c);
};
dojo.date.toSql=function(_da1,_da2){
return dojo.date.strftime(_da1,"%F"+!_da2?" %T":"");
};
dojo.date.fromSql=function(_da3){
var _da4=_da3.split(/[\- :]/g);
while(_da4.length<6){
_da4.push(0);
}
return new Date(_da4[0],(parseInt(_da4[1],10)-1),_da4[2],_da4[3],_da4[4],_da4[5]);
};
dojo.provide("dojo.widget.TimePicker");
dojo.widget.defineWidget("dojo.widget.TimePicker",dojo.widget.HtmlWidget,function(){
this.time="";
this.useDefaultTime=false;
this.useDefaultMinutes=false;
this.storedTime="";
this.currentTime={};
this.classNames={selectedTime:"selectedItem"};
this.any="any";
this.selectedTime={hour:"",minute:"",amPm:"",anyTime:false};
this.hourIndexMap=["",2,4,6,8,10,1,3,5,7,9,11,0];
this.minuteIndexMap=[0,2,4,6,8,10,1,3,5,7,9,11];
},{isContainer:false,templatePath:dojo.uri.moduleUri("dojo.widget","templates/TimePicker.html"),templateCssPath:dojo.uri.moduleUri("dojo.widget","templates/TimePicker.css"),postMixInProperties:function(_da5,frag){
dojo.widget.TimePicker.superclass.postMixInProperties.apply(this,arguments);
this.calendar=dojo.i18n.getLocalization("dojo.i18n.calendar","gregorian",this.lang);
this.widgetStrings=dojo.i18n.getLocalization("dojo.widget","TimePicker",this.lang);
},fillInTemplate:function(args,frag){
var _da9=this.getFragNodeRef(frag);
dojo.html.copyStyle(this.domNode,_da9);
if(args.value){
if(args.value instanceof Date){
this.storedTime=dojo.date.toRfc3339(args.value);
}else{
this.storedTime=args.value;
}
}
this.initData();
this.initUI();
},initData:function(){
if(this.storedTime.indexOf("T")!=-1&&this.storedTime.split("T")[1]&&this.storedTime!=" "&&this.storedTime.split("T")[1]!="any"){
this.time=dojo.widget.TimePicker.util.fromRfcDateTime(this.storedTime,this.useDefaultMinutes,this.selectedTime.anyTime);
}else{
if(this.useDefaultTime){
this.time=dojo.widget.TimePicker.util.fromRfcDateTime("",this.useDefaultMinutes,this.selectedTime.anyTime);
}else{
this.selectedTime.anyTime=true;
this.time=dojo.widget.TimePicker.util.fromRfcDateTime("",0,1);
}
}
},initUI:function(){
if(!this.selectedTime.anyTime&&this.time){
var _daa=dojo.widget.TimePicker.util.toAmPmHour(this.time.getHours());
var hour=_daa[0];
var isAm=_daa[1];
var _dad=this.time.getMinutes();
var _dae=parseInt(_dad/5);
this.onSetSelectedHour(this.hourIndexMap[hour]);
this.onSetSelectedMinute(this.minuteIndexMap[_dae]);
this.onSetSelectedAmPm(isAm);
}else{
this.onSetSelectedAnyTime();
}
},setTime:function(date){
if(date){
this.selectedTime.anyTime=false;
this.setDateTime(dojo.date.toRfc3339(date));
}else{
this.selectedTime.anyTime=true;
}
this.initData();
this.initUI();
},setDateTime:function(_db0){
this.storedTime=_db0;
},onClearSelectedHour:function(evt){
this.clearSelectedHour();
},onClearSelectedMinute:function(evt){
this.clearSelectedMinute();
},onClearSelectedAmPm:function(evt){
this.clearSelectedAmPm();
},onClearSelectedAnyTime:function(evt){
this.clearSelectedAnyTime();
if(this.selectedTime.anyTime){
this.selectedTime.anyTime=false;
this.time=dojo.widget.TimePicker.util.fromRfcDateTime("",this.useDefaultMinutes);
this.initUI();
}
},clearSelectedHour:function(){
var _db5=this.hourContainerNode.getElementsByTagName("td");
for(var i=0;i<_db5.length;i++){
dojo.html.setClass(_db5.item(i),"");
}
},clearSelectedMinute:function(){
var _db7=this.minuteContainerNode.getElementsByTagName("td");
for(var i=0;i<_db7.length;i++){
dojo.html.setClass(_db7.item(i),"");
}
},clearSelectedAmPm:function(){
var _db9=this.amPmContainerNode.getElementsByTagName("td");
for(var i=0;i<_db9.length;i++){
dojo.html.setClass(_db9.item(i),"");
}
},clearSelectedAnyTime:function(){
dojo.html.setClass(this.anyTimeContainerNode,"anyTimeContainer");
},onSetSelectedHour:function(evt){
this.onClearSelectedAnyTime();
this.onClearSelectedHour();
this.setSelectedHour(evt);
this.onSetTime();
},setSelectedHour:function(evt){
if(evt&&evt.target){
if(evt.target.nodeType==dojo.dom.ELEMENT_NODE){
var _dbd=evt.target;
}else{
var _dbd=evt.target.parentNode;
}
dojo.event.browser.stopEvent(evt);
dojo.html.setClass(_dbd,this.classNames.selectedTime);
this.selectedTime["hour"]=_dbd.innerHTML;
}else{
if(!isNaN(evt)){
var _dbe=this.hourContainerNode.getElementsByTagName("td");
if(_dbe.item(evt)){
dojo.html.setClass(_dbe.item(evt),this.classNames.selectedTime);
this.selectedTime["hour"]=_dbe.item(evt).innerHTML;
}
}
}
this.selectedTime.anyTime=false;
},onSetSelectedMinute:function(evt){
this.onClearSelectedAnyTime();
this.onClearSelectedMinute();
this.setSelectedMinute(evt);
this.selectedTime.anyTime=false;
this.onSetTime();
},setSelectedMinute:function(evt){
if(evt&&evt.target){
if(evt.target.nodeType==dojo.dom.ELEMENT_NODE){
var _dc1=evt.target;
}else{
var _dc1=evt.target.parentNode;
}
dojo.event.browser.stopEvent(evt);
dojo.html.setClass(_dc1,this.classNames.selectedTime);
this.selectedTime["minute"]=_dc1.innerHTML;
}else{
if(!isNaN(evt)){
var _dc2=this.minuteContainerNode.getElementsByTagName("td");
if(_dc2.item(evt)){
dojo.html.setClass(_dc2.item(evt),this.classNames.selectedTime);
this.selectedTime["minute"]=_dc2.item(evt).innerHTML;
}
}
}
},onSetSelectedAmPm:function(evt){
this.onClearSelectedAnyTime();
this.onClearSelectedAmPm();
this.setSelectedAmPm(evt);
this.selectedTime.anyTime=false;
this.onSetTime();
},setSelectedAmPm:function(evt){
var _dc5=evt.target;
if(evt&&_dc5){
if(_dc5.nodeType!=dojo.dom.ELEMENT_NODE){
_dc5=_dc5.parentNode;
}
dojo.event.browser.stopEvent(evt);
this.selectedTime.amPm=_dc5.id;
dojo.html.setClass(_dc5,this.classNames.selectedTime);
}else{
evt=evt?0:1;
var _dc6=this.amPmContainerNode.getElementsByTagName("td");
if(_dc6.item(evt)){
this.selectedTime.amPm=_dc6.item(evt).id;
dojo.html.setClass(_dc6.item(evt),this.classNames.selectedTime);
}
}
},onSetSelectedAnyTime:function(evt){
this.onClearSelectedHour();
this.onClearSelectedMinute();
this.onClearSelectedAmPm();
this.setSelectedAnyTime();
this.onSetTime();
},setSelectedAnyTime:function(evt){
this.selectedTime.anyTime=true;
dojo.html.setClass(this.anyTimeContainerNode,this.classNames.selectedTime+" "+"anyTimeContainer");
},onClick:function(evt){
dojo.event.browser.stopEvent(evt);
},onSetTime:function(){
if(this.selectedTime.anyTime){
this.time=new Date();
var _dca=dojo.widget.TimePicker.util.toRfcDateTime(this.time);
this.setDateTime(_dca.split("T")[0]);
}else{
var hour=12;
var _dcc=0;
var isAm=false;
if(this.selectedTime["hour"]){
hour=parseInt(this.selectedTime["hour"],10);
}
if(this.selectedTime["minute"]){
_dcc=parseInt(this.selectedTime["minute"],10);
}
if(this.selectedTime["amPm"]){
isAm=(this.selectedTime["amPm"].toLowerCase()=="am");
}
this.time=new Date();
this.time.setHours(dojo.widget.TimePicker.util.fromAmPmHour(hour,isAm));
this.time.setMinutes(_dcc);
this.setDateTime(dojo.widget.TimePicker.util.toRfcDateTime(this.time));
}
this.onValueChanged(this.time);
},onValueChanged:function(date){
}});
dojo.widget.TimePicker.util=new function(){
this.toRfcDateTime=function(_dcf){
if(!_dcf){
_dcf=new Date();
}
_dcf.setSeconds(0);
return dojo.date.strftime(_dcf,"%Y-%m-%dT%H:%M:00%z");
};
this.fromRfcDateTime=function(_dd0,_dd1,_dd2){
var _dd3=new Date();
if(!_dd0||_dd0.indexOf("T")==-1){
if(_dd1){
_dd3.setMinutes(Math.floor(_dd3.getMinutes()/5)*5);
}else{
_dd3.setMinutes(0);
}
}else{
var _dd4=_dd0.split("T")[1].split(":");
var _dd3=new Date();
_dd3.setHours(_dd4[0]);
_dd3.setMinutes(_dd4[1]);
}
return _dd3;
};
this.toAmPmHour=function(hour){
var _dd6=hour;
var isAm=true;
if(_dd6==0){
_dd6=12;
}else{
if(_dd6>12){
_dd6=_dd6-12;
isAm=false;
}else{
if(_dd6==12){
isAm=false;
}
}
}
return [_dd6,isAm];
};
this.fromAmPmHour=function(_dd8,isAm){
var hour=parseInt(_dd8,10);
if(isAm&&hour==12){
hour=0;
}else{
if(!isAm&&hour<12){
hour=hour+12;
}
}
return hour;
};
};
dojo.provide("dojo.widget.DropdownTimePicker");
dojo.widget.defineWidget("dojo.widget.DropdownTimePicker",dojo.widget.DropdownContainer,{iconURL:dojo.uri.moduleUri("dojo.widget","templates/images/timeIcon.gif"),formatLength:"short",displayFormat:"",timeFormat:"",saveFormat:"",value:"",name:"",postMixInProperties:function(){
dojo.widget.DropdownTimePicker.superclass.postMixInProperties.apply(this,arguments);
var _ddb=dojo.i18n.getLocalization("dojo.widget","DropdownTimePicker",this.lang);
this.iconAlt=_ddb.selectTime;
if(typeof (this.value)=="string"&&this.value.toLowerCase()=="today"){
this.value=new Date();
}
if(this.value&&isNaN(this.value)){
var orig=this.value;
this.value=dojo.date.fromRfc3339(this.value);
if(!this.value){
var d=dojo.date.format(new Date(),{selector:"dateOnly",datePattern:"yyyy-MM-dd"});
var c=orig.split(":");
for(var i=0;i<c.length;++i){
if(c[i].length==1){
c[i]="0"+c[i];
}
}
orig=c.join(":");
this.value=dojo.date.fromRfc3339(d+"T"+orig);
dojo.deprecated("dojo.widget.DropdownTimePicker","time attributes must be passed in Rfc3339 format","0.5");
}
}
if(this.value&&!isNaN(this.value)){
this.value=new Date(this.value);
}
},fillInTemplate:function(){
dojo.widget.DropdownTimePicker.superclass.fillInTemplate.apply(this,arguments);
var _de0="";
if(this.value instanceof Date){
_de0=this.value;
}else{
if(this.value){
var orig=this.value;
var d=dojo.date.format(new Date(),{selector:"dateOnly",datePattern:"yyyy-MM-dd"});
var c=orig.split(":");
for(var i=0;i<c.length;++i){
if(c[i].length==1){
c[i]="0"+c[i];
}
}
orig=c.join(":");
_de0=dojo.date.fromRfc3339(d+"T"+orig);
}
}
var _de5={widgetContainerId:this.widgetId,lang:this.lang,value:_de0};
this.timePicker=dojo.widget.createWidget("TimePicker",_de5,this.containerNode,"child");
dojo.event.connect(this.timePicker,"onValueChanged",this,"_updateText");
if(this.value){
this._updateText();
}
this.containerNode.style.zIndex=this.zIndex;
this.containerNode.explodeClassName="timeContainer";
this.valueNode.name=this.name;
},getValue:function(){
return this.valueNode.value;
},getTime:function(){
return this.timePicker.storedTime;
},setValue:function(_de6){
this.setTime(_de6);
},setTime:function(_de7){
var _de8="";
if(_de7 instanceof Date){
_de8=_de7;
}else{
if(this.value){
var orig=this.value;
var d=dojo.date.format(new Date(),{selector:"dateOnly",datePattern:"yyyy-MM-dd"});
var c=orig.split(":");
for(var i=0;i<c.length;++i){
if(c[i].length==1){
c[i]="0"+c[i];
}
}
orig=c.join(":");
_de8=dojo.date.fromRfc3339(d+"T"+orig);
}
}
this.timePicker.setTime(_de8);
this._syncValueNode();
},_updateText:function(){
if(this.timePicker.selectedTime.anyTime){
this.inputNode.value="";
}else{
if(this.timeFormat){
dojo.deprecated("dojo.widget.DropdownTimePicker","Must use displayFormat attribute instead of timeFormat.  See dojo.date.format for specification.","0.5");
this.inputNode.value=dojo.date.strftime(this.timePicker.time,this.timeFormat,this.lang);
}else{
this.inputNode.value=dojo.date.format(this.timePicker.time,{formatLength:this.formatLength,timePattern:this.displayFormat,selector:"timeOnly",locale:this.lang});
}
}
this._syncValueNode();
this.onValueChanged(this.getTime());
this.hideContainer();
},onValueChanged:function(_ded){
},onInputChange:function(){
if(this.dateFormat){
dojo.deprecated("dojo.widget.DropdownTimePicker","Cannot parse user input.  Must use displayFormat attribute instead of dateFormat.  See dojo.date.format for specification.","0.5");
}else{
var _dee=dojo.string.trim(this.inputNode.value);
if(_dee){
var _def=dojo.date.parse(_dee,{formatLength:this.formatLength,timePattern:this.displayFormat,selector:"timeOnly",locale:this.lang});
if(_def){
this.setTime(_def);
}
}else{
this.valueNode.value=_dee;
}
}
if(_dee){
this._updateText();
}
},_syncValueNode:function(){
var time=this.timePicker.time;
var _df1;
switch(this.saveFormat.toLowerCase()){
case "rfc":
case "iso":
case "":
_df1=dojo.date.toRfc3339(time,"timeOnly");
break;
case "posix":
case "unix":
_df1=Number(time);
break;
default:
_df1=dojo.date.format(time,{datePattern:this.saveFormat,selector:"timeOnly",locale:this.lang});
}
this.valueNode.value=_df1;
},destroy:function(_df2){
this.timePicker.destroy(_df2);
dojo.widget.DropdownTimePicker.superclass.destroy.apply(this,arguments);
}});
dojo.provide("struts.widget.StrutsTimePicker");
dojo.widget.defineWidget("struts.widget.StrutsTimePicker",dojo.widget.DropdownTimePicker,{widgetType:"StrutsTimePicker",inputName:"",name:"",valueNotifyTopics:"",valueNotifyTopicsArray:null,postCreate:function(){
struts.widget.StrutsTimePicker.superclass.postCreate.apply(this,arguments);
this.inputNode.name=this.name;
if(this.extraArgs["class"]){
dojo.html.setClass(this.inputNode,this.extraArgs["class"]);
}
if(this.extraArgs.style){
dojo.html.setStyleText(this.inputNode,this.extraArgs.style);
}
this.valueNode.name=this.inputName;
if(!dojo.string.isBlank(this.valueNotifyTopics)){
this.valueNotifyTopicsArray=this.valueNotifyTopics.split(",");
}
},_syncValueNode:function(){
var time=this.timePicker.time;
var _df4;
switch(this.saveFormat.toLowerCase()){
case "rfc":
case "iso":
case "":
_df4=dojo.date.toRfc3339(time);
break;
case "posix":
case "unix":
_df4=Number(time);
break;
default:
_df4=dojo.date.format(time,{datePattern:this.saveFormat,selector:"timeOnly",locale:this.lang});
}
this.valueNode.value=_df4;
},_updateText:function(){
struts.widget.StrutsTimePicker.superclass._updateText.apply(this,arguments);
if(this.valueNotifyTopicsArray!=null){
for(var i=0;i<this.valueNotifyTopicsArray.length;i++){
var _df6=this.valueNotifyTopicsArray[i];
if(!dojo.string.isBlank(_df6)){
try{
dojo.event.topic.publish(_df6,this.inputNode.value);
}
catch(ex){
dojo.debug(ex);
}
}
}
}
}});
dojo.provide("dojo.widget.DatePicker");
dojo.widget.defineWidget("dojo.widget.DatePicker",dojo.widget.HtmlWidget,{value:"",name:"",displayWeeks:6,adjustWeeks:false,startDate:"1492-10-12",endDate:"2941-10-12",weekStartsOn:"",staticDisplay:false,dayWidth:"narrow",classNames:{previous:"previousMonth",disabledPrevious:"previousMonthDisabled",current:"currentMonth",disabledCurrent:"currentMonthDisabled",next:"nextMonth",disabledNext:"nextMonthDisabled",currentDate:"currentDate",selectedDate:"selectedDate"},templatePath:dojo.uri.moduleUri("dojo.widget","templates/DatePicker.html"),templateCssPath:dojo.uri.moduleUri("dojo.widget","templates/DatePicker.css"),postMixInProperties:function(){
dojo.widget.DatePicker.superclass.postMixInProperties.apply(this,arguments);
if(!this.weekStartsOn){
this.weekStartsOn=dojo.date.getFirstDayOfWeek(this.lang);
}
this.today=new Date();
this.today.setHours(0,0,0,0);
if(typeof (this.value)=="string"&&this.value.toLowerCase()=="today"){
this.value=new Date();
}else{
if(this.value&&(typeof this.value=="string")&&(this.value.split("-").length>2)){
this.value=dojo.date.fromRfc3339(this.value);
this.value.setHours(0,0,0,0);
}
}
},fillInTemplate:function(args,frag){
dojo.widget.DatePicker.superclass.fillInTemplate.apply(this,arguments);
var _df9=this.getFragNodeRef(frag);
dojo.html.copyStyle(this.domNode,_df9);
this.weekTemplate=dojo.dom.removeNode(this.calendarWeekTemplate);
this._preInitUI(this.value?this.value:this.today,false,true);
var _dfa=dojo.lang.unnest(dojo.date.getNames("days",this.dayWidth,"standAlone",this.lang));
if(this.weekStartsOn>0){
for(var i=0;i<this.weekStartsOn;i++){
_dfa.push(_dfa.shift());
}
}
var _dfc=this.dayLabelsRow.getElementsByTagName("td");
for(i=0;i<7;i++){
_dfc.item(i).innerHTML=_dfa[i];
}
if(this.value){
this.setValue(this.value);
}
},getValue:function(){
return dojo.date.toRfc3339(new Date(this.value),"dateOnly");
},getDate:function(){
return this.value;
},setValue:function(_dfd){
this.setDate(_dfd);
},setDate:function(_dfe){
if(_dfe==""){
this.value="";
this._preInitUI(this.curMonth,false,true);
}else{
if(typeof _dfe=="string"){
this.value=dojo.date.fromRfc3339(_dfe);
this.value.setHours(0,0,0,0);
}else{
this.value=new Date(_dfe);
this.value.setHours(0,0,0,0);
}
}
if(this.selectedNode!=null){
dojo.html.removeClass(this.selectedNode,this.classNames.selectedDate);
}
if(this.clickedNode!=null){
dojo.debug("adding selectedDate");
dojo.html.addClass(this.clickedNode,this.classNames.selectedDate);
this.selectedNode=this.clickedNode;
}else{
this._preInitUI(this.value,false,true);
}
this.clickedNode=null;
this.onValueChanged(this.value);
},_preInitUI:function(_dff,_e00,_e01){
if(typeof (this.startDate)=="string"){
this.startDate=dojo.date.fromRfc3339(this.startDate);
}
if(typeof (this.endDate)=="string"){
this.endDate=dojo.date.fromRfc3339(this.endDate);
}
this.startDate.setHours(0,0,0,0);
this.endDate.setHours(24,0,0,-1);
if(_dff<this.startDate||_dff>this.endDate){
_dff=new Date((_dff<this.startDate)?this.startDate:this.endDate);
}
this.firstDay=this._initFirstDay(_dff,_e00);
this.selectedIsUsed=false;
this.currentIsUsed=false;
var _e02=new Date(this.firstDay);
var _e03=_e02.getMonth();
this.curMonth=new Date(_e02);
this.curMonth.setDate(_e02.getDate()+6);
this.curMonth.setDate(1);
if(this.displayWeeks==""||this.adjustWeeks){
this.adjustWeeks=true;
this.displayWeeks=Math.ceil((dojo.date.getDaysInMonth(this.curMonth)+this._getAdjustedDay(this.curMonth))/7);
}
var days=this.displayWeeks*7;
if(dojo.date.diff(this.startDate,this.endDate,dojo.date.dateParts.DAY)<days){
this.staticDisplay=true;
if(dojo.date.diff(_e02,this.endDate,dojo.date.dateParts.DAY)>days){
this._preInitUI(this.startDate,true,false);
_e02=new Date(this.firstDay);
}
this.curMonth=new Date(_e02);
this.curMonth.setDate(_e02.getDate()+6);
this.curMonth.setDate(1);
var _e05=(_e02.getMonth()==this.curMonth.getMonth())?"current":"previous";
}
if(_e01){
this._initUI(days);
}
},_initUI:function(days){
dojo.dom.removeChildren(this.calendarDatesContainerNode);
for(var i=0;i<this.displayWeeks;i++){
this.calendarDatesContainerNode.appendChild(this.weekTemplate.cloneNode(true));
}
var _e08=new Date(this.firstDay);
this._setMonthLabel(this.curMonth.getMonth());
this._setYearLabels(this.curMonth.getFullYear());
var _e09=this.calendarDatesContainerNode.getElementsByTagName("td");
var _e0a=this.calendarDatesContainerNode.getElementsByTagName("tr");
var _e0b;
for(i=0;i<days;i++){
_e0b=_e09.item(i);
_e0b.innerHTML=_e08.getDate();
_e0b.setAttribute("djDateValue",_e08.valueOf());
var _e0c=(_e08.getMonth()!=this.curMonth.getMonth()&&Number(_e08)<Number(this.curMonth))?"previous":(_e08.getMonth()==this.curMonth.getMonth())?"current":"next";
var _e0d=_e0c;
if(this._isDisabledDate(_e08)){
var _e0e={previous:"disabledPrevious",current:"disabledCurrent",next:"disabledNext"};
_e0d=_e0e[_e0c];
}
dojo.html.setClass(_e0b,this._getDateClassName(_e08,_e0d));
if(dojo.html.hasClass(_e0b,this.classNames.selectedDate)){
this.selectedNode=_e0b;
}
_e08=dojo.date.add(_e08,dojo.date.dateParts.DAY,1);
}
this.lastDay=dojo.date.add(_e08,dojo.date.dateParts.DAY,-1);
this._initControls();
},_initControls:function(){
var d=this.firstDay;
var d2=this.lastDay;
var _e11,_e12,_e13,_e14,_e15,_e16;
_e11=_e12=_e13=_e14=_e15=_e16=!this.staticDisplay;
with(dojo.date.dateParts){
var add=dojo.date.add;
if(_e11&&add(d,DAY,(-1*(this._getAdjustedDay(d)+1)))<this.startDate){
_e11=_e13=_e15=false;
}
if(_e12&&d2>this.endDate){
_e12=_e14=_e16=false;
}
if(_e13&&add(d,DAY,-1)<this.startDate){
_e13=_e15=false;
}
if(_e14&&add(d2,DAY,1)>this.endDate){
_e14=_e16=false;
}
if(_e15&&add(d2,YEAR,-1)<this.startDate){
_e15=false;
}
if(_e16&&add(d,YEAR,1)>this.endDate){
_e16=false;
}
}
function enableControl(node,_e19){
dojo.html.setVisibility(node,_e19?"":"hidden");
}
enableControl(this.decreaseWeekNode,_e11);
enableControl(this.increaseWeekNode,_e12);
enableControl(this.decreaseMonthNode,_e13);
enableControl(this.increaseMonthNode,_e14);
enableControl(this.previousYearLabelNode,_e15);
enableControl(this.nextYearLabelNode,_e16);
},_incrementWeek:function(evt){
var d=new Date(this.firstDay);
switch(evt.target){
case this.increaseWeekNode.getElementsByTagName("img").item(0):
case this.increaseWeekNode:
var _e1c=dojo.date.add(d,dojo.date.dateParts.WEEK,1);
if(_e1c<this.endDate){
d=dojo.date.add(d,dojo.date.dateParts.WEEK,1);
}
break;
case this.decreaseWeekNode.getElementsByTagName("img").item(0):
case this.decreaseWeekNode:
if(d>=this.startDate){
d=dojo.date.add(d,dojo.date.dateParts.WEEK,-1);
}
break;
}
this._preInitUI(d,true,true);
},_incrementMonth:function(evt){
var d=new Date(this.curMonth);
var _e1f=new Date(this.firstDay);
switch(evt.currentTarget){
case this.increaseMonthNode.getElementsByTagName("img").item(0):
case this.increaseMonthNode:
_e1f=dojo.date.add(_e1f,dojo.date.dateParts.DAY,this.displayWeeks*7);
if(_e1f<this.endDate){
d=dojo.date.add(d,dojo.date.dateParts.MONTH,1);
}else{
var _e20=true;
}
break;
case this.decreaseMonthNode.getElementsByTagName("img").item(0):
case this.decreaseMonthNode:
if(_e1f>this.startDate){
d=dojo.date.add(d,dojo.date.dateParts.MONTH,-1);
}else{
var _e21=true;
}
break;
}
if(_e21){
d=new Date(this.startDate);
}else{
if(_e20){
d=new Date(this.endDate);
}
}
this._preInitUI(d,false,true);
},_incrementYear:function(evt){
var year=this.curMonth.getFullYear();
var _e24=new Date(this.firstDay);
switch(evt.target){
case this.nextYearLabelNode:
_e24=dojo.date.add(_e24,dojo.date.dateParts.YEAR,1);
if(_e24<this.endDate){
year++;
}else{
var _e25=true;
}
break;
case this.previousYearLabelNode:
_e24=dojo.date.add(_e24,dojo.date.dateParts.YEAR,-1);
if(_e24>this.startDate){
year--;
}else{
var _e26=true;
}
break;
}
var d;
if(_e26){
d=new Date(this.startDate);
}else{
if(_e25){
d=new Date(this.endDate);
}else{
d=new Date(year,this.curMonth.getMonth(),1);
}
}
this._preInitUI(d,false,true);
},onIncrementWeek:function(evt){
evt.stopPropagation();
if(!this.staticDisplay){
this._incrementWeek(evt);
}
},onIncrementMonth:function(evt){
evt.stopPropagation();
if(!this.staticDisplay){
this._incrementMonth(evt);
}
},onIncrementYear:function(evt){
evt.stopPropagation();
if(!this.staticDisplay){
this._incrementYear(evt);
}
},_setMonthLabel:function(_e2b){
this.monthLabelNode.innerHTML=dojo.date.getNames("months","wide","standAlone",this.lang)[_e2b];
},_setYearLabels:function(year){
var y=year-1;
var that=this;
function f(n){
that[n+"YearLabelNode"].innerHTML=dojo.date.format(new Date(y++,0),{formatLength:"yearOnly",locale:that.lang});
}
f("previous");
f("current");
f("next");
},_getDateClassName:function(date,_e31){
var _e32=this.classNames[_e31];
if((!this.selectedIsUsed&&this.value)&&(Number(date)==Number(this.value))){
_e32=this.classNames.selectedDate+" "+_e32;
this.selectedIsUsed=true;
}
if((!this.currentIsUsed)&&(Number(date)==Number(this.today))){
_e32=_e32+" "+this.classNames.currentDate;
this.currentIsUsed=true;
}
return _e32;
},onClick:function(evt){
dojo.event.browser.stopEvent(evt);
},_handleUiClick:function(evt){
var _e35=evt.target;
if(_e35.nodeType!=dojo.dom.ELEMENT_NODE){
_e35=_e35.parentNode;
}
dojo.event.browser.stopEvent(evt);
this.selectedIsUsed=this.todayIsUsed=false;
if(dojo.html.hasClass(_e35,this.classNames["disabledPrevious"])||dojo.html.hasClass(_e35,this.classNames["disabledCurrent"])||dojo.html.hasClass(_e35,this.classNames["disabledNext"])){
return;
}
this.clickedNode=_e35;
this.setDate(new Date(Number(dojo.html.getAttribute(_e35,"djDateValue"))));
},onValueChanged:function(date){
},_isDisabledDate:function(_e37){
if(_e37<this.startDate||_e37>this.endDate){
return true;
}
return this.isDisabledDate(_e37,this.lang);
},isDisabledDate:function(_e38,_e39){
return false;
},_initFirstDay:function(_e3a,adj){
var d=new Date(_e3a);
if(!adj){
d.setDate(1);
}
d.setDate(d.getDate()-this._getAdjustedDay(d,this.weekStartsOn));
d.setHours(0,0,0,0);
return d;
},_getAdjustedDay:function(_e3d){
var days=[0,1,2,3,4,5,6];
if(this.weekStartsOn>0){
for(var i=0;i<this.weekStartsOn;i++){
days.unshift(days.pop());
}
}
return days[_e3d.getDay()];
},destroy:function(){
dojo.widget.DatePicker.superclass.destroy.apply(this,arguments);
dojo.html.destroyNode(this.weekTemplate);
}});
dojo.provide("dojo.widget.DropdownDatePicker");
dojo.widget.defineWidget("dojo.widget.DropdownDatePicker",dojo.widget.DropdownContainer,{iconURL:dojo.uri.moduleUri("dojo.widget","templates/images/dateIcon.gif"),formatLength:"short",displayFormat:"",saveFormat:"",value:"",name:"",displayWeeks:6,adjustWeeks:false,startDate:"1492-10-12",endDate:"2941-10-12",weekStartsOn:"",staticDisplay:false,postMixInProperties:function(_e40,frag){
dojo.widget.DropdownDatePicker.superclass.postMixInProperties.apply(this,arguments);
var _e42=dojo.i18n.getLocalization("dojo.widget","DropdownDatePicker",this.lang);
this.iconAlt=_e42.selectDate;
if(typeof (this.value)=="string"&&this.value.toLowerCase()=="today"){
this.value=new Date();
}
if(this.value&&isNaN(this.value)){
var orig=this.value;
this.value=dojo.date.fromRfc3339(this.value);
if(!this.value){
this.value=new Date(orig);
dojo.deprecated("dojo.widget.DropdownDatePicker","date attributes must be passed in Rfc3339 format","0.5");
}
}
if(this.value&&!isNaN(this.value)){
this.value=new Date(this.value);
}
},fillInTemplate:function(args,frag){
dojo.widget.DropdownDatePicker.superclass.fillInTemplate.call(this,args,frag);
var _e46={widgetContainerId:this.widgetId,lang:this.lang,value:this.value,startDate:this.startDate,endDate:this.endDate,displayWeeks:this.displayWeeks,weekStartsOn:this.weekStartsOn,adjustWeeks:this.adjustWeeks,staticDisplay:this.staticDisplay};
this.datePicker=dojo.widget.createWidget("DatePicker",_e46,this.containerNode,"child");
dojo.event.connect(this.datePicker,"onValueChanged",this,"_updateText");
dojo.event.connect(this.inputNode,"onChange",this,"_updateText");
if(this.value){
this._updateText();
}
this.containerNode.explodeClassName="calendarBodyContainer";
this.valueNode.name=this.name;
},getValue:function(){
return this.valueNode.value;
},getDate:function(){
return this.datePicker.value;
},setValue:function(_e47){
this.setDate(_e47);
},setDate:function(_e48){
this.datePicker.setDate(_e48);
this._syncValueNode();
},_updateText:function(){
this.inputNode.value=this.datePicker.value?dojo.date.format(this.datePicker.value,{formatLength:this.formatLength,datePattern:this.displayFormat,selector:"dateOnly",locale:this.lang}):"";
if(this.value<this.datePicker.startDate||this.value>this.datePicker.endDate){
this.inputNode.value="";
}
this._syncValueNode();
this.onValueChanged(this.getDate());
this.hideContainer();
},onValueChanged:function(_e49){
},onInputChange:function(){
var _e4a=dojo.string.trim(this.inputNode.value);
if(_e4a){
var _e4b=dojo.date.parse(_e4a,{formatLength:this.formatLength,datePattern:this.displayFormat,selector:"dateOnly",locale:this.lang});
if(!this.datePicker._isDisabledDate(_e4b)){
this.setDate(_e4b);
}
}else{
if(_e4a==""){
this.datePicker.setDate("");
}
this.valueNode.value=_e4a;
}
if(_e4a){
this._updateText();
}
},_syncValueNode:function(){
var date=this.datePicker.value;
var _e4d="";
switch(this.saveFormat.toLowerCase()){
case "rfc":
case "iso":
case "":
_e4d=dojo.date.toRfc3339(date,"dateOnly");
break;
case "posix":
case "unix":
_e4d=Number(date);
break;
default:
if(date){
_e4d=dojo.date.format(date,{datePattern:this.saveFormat,selector:"dateOnly",locale:this.lang});
}
}
this.valueNode.value=_e4d;
},destroy:function(_e4e){
this.datePicker.destroy(_e4e);
dojo.widget.DropdownDatePicker.superclass.destroy.apply(this,arguments);
}});
dojo.provide("struts.widget.StrutsDatePicker");
dojo.widget.defineWidget("struts.widget.StrutsDatePicker",dojo.widget.DropdownDatePicker,{widgetType:"StrutsDatePicker",valueNotifyTopics:"",valueNotifyTopicsArray:null,postCreate:function(){
struts.widget.StrutsDatePicker.superclass.postCreate.apply(this,arguments);
if(this.extraArgs["class"]){
dojo.html.setClass(this.inputNode,this.extraArgs["class"]);
}
if(this.extraArgs.style){
dojo.html.setStyleText(this.inputNode,this.extraArgs.style);
}
if(!dojo.string.isBlank(this.valueNotifyTopics)){
this.valueNotifyTopicsArray=this.valueNotifyTopics.split(",");
}
},_updateText:function(){
struts.widget.StrutsDatePicker.superclass._updateText.apply(this,arguments);
if(this.valueNotifyTopicsArray!=null){
for(var i=0;i<this.valueNotifyTopicsArray.length;i++){
var _e50=this.valueNotifyTopicsArray[i];
if(!dojo.string.isBlank(_e50)){
try{
dojo.event.topic.publish(_e50,this.inputNode.value);
}
catch(ex){
dojo.debug(ex);
}
}
}
}
}});
dojo.provide("struts.widget.BindEvent");
dojo.widget.defineWidget("struts.widget.BindEvent",struts.widget.Bind,{widgetType:"BindEvent",sources:"",postCreate:function(){
struts.widget.BindEvent.superclass.postCreate.apply(this);
var self=this;
if(!dojo.string.isBlank(this.events)&&!dojo.string.isBlank(this.sources)){
var _e52=this.events.split(",");
var _e53=this.sources.split(",");
if(_e52&&this.domNode){
dojo.lang.forEach(_e52,function(_e54){
dojo.lang.forEach(_e53,function(_e55){
var _e56=dojo.byId(_e55);
if(_e56){
dojo.event.connect(_e56,_e54,function(evt){
evt.preventDefault();
evt.stopPropagation();
self.reloadContents();
});
}
});
});
}
}
}});
dojo.provide("dojo.widget.TreeSelector");
dojo.widget.defineWidget("dojo.widget.TreeSelector",dojo.widget.HtmlWidget,function(){
this.eventNames={};
this.listenedTrees=[];
},{widgetType:"TreeSelector",selectedNode:null,dieWithTree:false,eventNamesDefault:{select:"select",destroy:"destroy",deselect:"deselect",dblselect:"dblselect"},initialize:function(){
for(name in this.eventNamesDefault){
if(dojo.lang.isUndefined(this.eventNames[name])){
this.eventNames[name]=this.widgetId+"/"+this.eventNamesDefault[name];
}
}
},destroy:function(){
dojo.event.topic.publish(this.eventNames.destroy,{source:this});
return dojo.widget.HtmlWidget.prototype.destroy.apply(this,arguments);
},listenTree:function(tree){
dojo.event.topic.subscribe(tree.eventNames.titleClick,this,"select");
dojo.event.topic.subscribe(tree.eventNames.iconClick,this,"select");
dojo.event.topic.subscribe(tree.eventNames.collapse,this,"onCollapse");
dojo.event.topic.subscribe(tree.eventNames.moveFrom,this,"onMoveFrom");
dojo.event.topic.subscribe(tree.eventNames.removeNode,this,"onRemoveNode");
dojo.event.topic.subscribe(tree.eventNames.treeDestroy,this,"onTreeDestroy");
this.listenedTrees.push(tree);
},unlistenTree:function(tree){
dojo.event.topic.unsubscribe(tree.eventNames.titleClick,this,"select");
dojo.event.topic.unsubscribe(tree.eventNames.iconClick,this,"select");
dojo.event.topic.unsubscribe(tree.eventNames.collapse,this,"onCollapse");
dojo.event.topic.unsubscribe(tree.eventNames.moveFrom,this,"onMoveFrom");
dojo.event.topic.unsubscribe(tree.eventNames.removeNode,this,"onRemoveNode");
dojo.event.topic.unsubscribe(tree.eventNames.treeDestroy,this,"onTreeDestroy");
for(var i=0;i<this.listenedTrees.length;i++){
if(this.listenedTrees[i]===tree){
this.listenedTrees.splice(i,1);
break;
}
}
},onTreeDestroy:function(_e5b){
this.unlistenTree(_e5b.source);
if(this.dieWithTree){
this.destroy();
}
},onCollapse:function(_e5c){
if(!this.selectedNode){
return;
}
var node=_e5c.source;
var _e5e=this.selectedNode.parent;
while(_e5e!==node&&_e5e.isTreeNode){
_e5e=_e5e.parent;
}
if(_e5e.isTreeNode){
this.deselect();
}
},select:function(_e5f){
var node=_e5f.source;
var e=_e5f.event;
if(this.selectedNode===node){
if(e.ctrlKey||e.shiftKey||e.metaKey){
this.deselect();
return;
}
dojo.event.topic.publish(this.eventNames.dblselect,{node:node});
return;
}
if(this.selectedNode){
this.deselect();
}
this.doSelect(node);
dojo.event.topic.publish(this.eventNames.select,{node:node});
},onMoveFrom:function(_e62){
if(_e62.child!==this.selectedNode){
return;
}
if(!dojo.lang.inArray(this.listenedTrees,_e62.newTree)){
this.deselect();
}
},onRemoveNode:function(_e63){
if(_e63.child!==this.selectedNode){
return;
}
this.deselect();
},doSelect:function(node){
node.markSelected();
this.selectedNode=node;
},deselect:function(){
var node=this.selectedNode;
this.selectedNode=null;
node.unMarkSelected();
dojo.event.topic.publish(this.eventNames.deselect,{node:node});
}});
dojo.provide("struts.widget.StrutsTreeSelector");
dojo.widget.defineWidget("struts.widget.StrutsTreeSelector",dojo.widget.TreeSelector,{widgetType:"StrutsTreeSelector",selectedNotifyTopics:"",collapsedNotifyTopics:"",expandedNotifyTopics:"",selectedNotifyTopicsArray:null,collapsedNotifyTopicsArray:null,expandedNotifyTopicsArray:null,eventNamesDefault:{select:"select",destroy:"destroy",deselect:"deselect",dblselect:"dblselect",expand:"expand",collapse:"collapse"},initialize:function(){
struts.widget.StrutsTreeSelector.superclass.initialize.apply(this);
if(!dojo.string.isBlank(this.selectedNotifyTopics)){
this.selectedNotifyTopicsArray=this.selectedNotifyTopics.split(",");
}
if(!dojo.string.isBlank(this.selectedNotifyTopics)){
this.collapsedNotifyTopicsArray=this.collapsedNotifyTopics.split(",");
}
if(!dojo.string.isBlank(this.selectedNotifyTopics)){
this.expandedNotifyTopicsArray=this.expandedNotifyTopics.split(",");
}
},listenTree:function(tree){
dojo.event.topic.subscribe(tree.eventNames.collapse,this,"collapse");
dojo.event.topic.subscribe(tree.eventNames.expand,this,"expand");
struts.widget.StrutsTreeSelector.superclass.listenTree.apply(this,[tree]);
},unlistenTree:function(tree){
dojo.event.topic.unsubscribe(tree.eventNames.collapse,this,"collapse");
dojo.event.topic.unsubscribe(tree.eventNames.expand,this,"expand");
struts.widget.StrutsTreeSelector.superclass.unlistenTree.apply(this,[tree]);
},publishTopics:function(_e68,node){
if(_e68!=null){
for(var i=0;i<_e68.length;i++){
var _e6b=_e68[i];
if(!dojo.string.isBlank(_e6b)){
try{
dojo.event.topic.publish(_e6b,node);
}
catch(ex){
dojo.debug(ex);
}
}
}
}
},select:function(_e6c){
var node=_e6c.source;
var e=_e6c.event;
if(this.selectedNode===node){
if(e.ctrlKey||e.shiftKey||e.metaKey){
this.deselect();
return;
}
dojo.event.topic.publish(this.eventNames.dblselect,{node:node});
return;
}
if(this.selectedNode){
this.deselect();
}
this.doSelect(node);
this.publishTopics(this.selectedNotifyTopicsArray,{node:node});
},expand:function(_e6f){
var node=_e6f.source;
this.publishTopics(this.expandedNotifyTopicsArray,{node:node});
},collapse:function(_e71){
var node=_e71.source;
this.publishTopics(this.collapsedNotifyTopicsArray,{node:node});
},});
dojo.kwCompoundRequire({common:["struts.widget.Bind","struts.widget.BindDiv","struts.widget.BindAnchor","struts.widget.ComboBox","struts.widget.StrutsTimePicker","struts.widget.StrutsDatePicker","struts.widget.BindEvent","struts.widget.StrutsTreeSelector"]});
dojo.provide("struts.widget.*");

