//Version: 0.7.3
var domLib_userAgent=navigator.userAgent.toLowerCase();var domLib_isMac=navigator.appVersion.indexOf("Mac")!=-1;var domLib_isWin=domLib_userAgent.indexOf("windows")!=-1;var domLib_isOpera=domLib_userAgent.indexOf("opera")!=-1;var domLib_isOpera7up=domLib_userAgent.match(/opera.(7|8)/i);var domLib_isSafari=domLib_userAgent.indexOf("safari")!=-1;var domLib_isKonq=domLib_userAgent.indexOf("konqueror")!=-1;var domLib_isKHTML=(domLib_isKonq||domLib_isSafari||domLib_userAgent.indexOf("khtml")!=-1);var domLib_isIE=(!domLib_isKHTML&&!domLib_isOpera&&(domLib_userAgent.indexOf("msie 5")!=-1||domLib_userAgent.indexOf("msie 6")!=-1||domLib_userAgent.indexOf("msie 7")!=-1));var domLib_isIE5up=domLib_isIE;var domLib_isIE50=(domLib_isIE&&domLib_userAgent.indexOf("msie 5.0")!=-1);var domLib_isIE55=(domLib_isIE&&domLib_userAgent.indexOf("msie 5.5")!=-1);var domLib_isIE5=(domLib_isIE50||domLib_isIE55);var domLib_isGecko=domLib_userAgent.indexOf("gecko/")!=-1;var domLib_isMacIE=(domLib_isIE&&domLib_isMac);var domLib_isIE55up=domLib_isIE5up&&!domLib_isIE50&&!domLib_isMacIE;var domLib_isIE6up=domLib_isIE55up&&!domLib_isIE55;var domLib_standardsMode=(document.compatMode&&document.compatMode=="CSS1Compat");var domLib_useLibrary=(domLib_isOpera7up||domLib_isKHTML||domLib_isIE5up||domLib_isGecko||domLib_isMacIE||document.defaultView);var domLib_hasBrokenTimeout=(domLib_isMacIE||(domLib_isKonq&&domLib_userAgent.match(/konqueror\/3.([2-9])/)==null));var domLib_canFade=(domLib_isGecko||domLib_isIE||domLib_isSafari||domLib_isOpera);var domLib_canDrawOverSelect=(domLib_isMac||domLib_isOpera||domLib_isGecko);var domLib_canDrawOverFlash=(domLib_isMac||domLib_isWin);var domLib_eventTarget=domLib_isIE?"srcElement":"currentTarget";var domLib_eventButton=domLib_isIE?"button":"which";var domLib_eventTo=domLib_isIE?"toElement":"relatedTarget";var domLib_stylePointer=domLib_isIE?"hand":"pointer";var domLib_styleNoMaxWidth=domLib_isOpera?"10000px":"none";var domLib_autoId=1;var domLib_zIndex=100;var domLib_collisionElements;var domLib_collisionsCached=false;var domLib_timeoutStateId=0;var domLib_timeoutStates=new Hash();if(!document.ELEMENT_NODE){document.ELEMENT_NODE=1;document.DOCUMENT_NODE=9;}
function domLib_clone(_1){var _2={};for(var i in _1){var _4=_1[i];try{if(_4!=null&&typeof(_4)=="object"&&_4!=window&&!_4.nodeType){_2[i]=domLib_clone(_4);}else{_2[i]=_4;}}
catch(e){_2[i]=_4;}}
return _2;}
function Hash(){this.length=0;this.numericLength=0;this.elementData=[];for(var i=0;i<arguments.length;i+=2){if(typeof(arguments[i+1])!="undefined"){this.elementData[arguments[i]]=arguments[i+1];this.length++;if(arguments[i]==parseInt(arguments[i])){this.numericLength++;}}}}
Hash.prototype.get=function(_6){if(typeof(this.elementData[_6])!="undefined"){return this.elementData[_6];}
return null;};Hash.prototype.set=function(_7,_8){if(typeof(_8)!="undefined"){if(typeof(this.elementData[_7])=="undefined"){this.length++;if(_7==parseInt(_7)){this.numericLength++;}}
return this.elementData[_7]=_8;}
return false;};Hash.prototype.remove=function(_9){var _a;if(typeof(this.elementData[_9])!="undefined"){this.length--;if(_9==parseInt(_9)){this.numericLength--;}
_a=this.elementData[_9];delete this.elementData[_9];}
return _a;};Hash.prototype.size=function(){return this.length;};Hash.prototype.has=function(_b){return typeof(this.elementData[_b])!="undefined";};function domLib_isDescendantOf(_c,_d,_e){if(_c==null){return false;}
if(_c==_d){return true;}
if(typeof(_e)!="undefined"&&(","+_e.join(",")+",").indexOf(","+_c.tagName+",")!=-1){return false;}
while(_c!=document.documentElement){try{if((tmp_object=_c.offsetParent)&&tmp_object==_d){return true;}else{if((tmp_object=_c.parentNode)==_d){return true;}else{_c=tmp_object;}}}
catch(e){return false;}}
return false;}
function domLib_detectCollisions(_f,_10,_11){if(!domLib_collisionsCached){var _12=[];if(!domLib_canDrawOverFlash){_12[_12.length]="object";}
if(!domLib_canDrawOverSelect){_12[_12.length]="select";}
domLib_collisionElements=domLib_getElementsByTagNames(_12,true);domLib_collisionsCached=_11;}
if(_10){for(var cnt=0;cnt<domLib_collisionElements.length;cnt++){var _14=domLib_collisionElements[cnt];if(!_14.hideList){_14.hideList=new Hash();}
_14.hideList.remove(_f.id);if(!_14.hideList.length){domLib_collisionElements[cnt].style.visibility="visible";if(domLib_isKonq){domLib_collisionElements[cnt].style.display="";}}}
return;}else{if(domLib_collisionElements.length==0){return;}}
var _15=domLib_getOffsets(_f);for(var cnt=0;cnt<domLib_collisionElements.length;cnt++){var _14=domLib_collisionElements[cnt];if(domLib_isDescendantOf(_14,_f)){continue;}
if(domLib_isKonq&&_14.tagName=="SELECT"&&(_14.size<=1&&!_14.multiple)){continue;}
if(!_14.hideList){_14.hideList=new Hash();}
var _16=domLib_getOffsets(_14);var _17=Math.sqrt(Math.pow(_16.get("leftCenter")-_15.get("leftCenter"),2)+Math.pow(_16.get("topCenter")-_15.get("topCenter"),2));var _18=_16.get("radius")+_15.get("radius");if(_17<_18){if((_15.get("leftCenter")<=_16.get("leftCenter")&&_15.get("right")<_16.get("left"))||(_15.get("leftCenter")>_16.get("leftCenter")&&_15.get("left")>_16.get("right"))||(_15.get("topCenter")<=_16.get("topCenter")&&_15.get("bottom")<_16.get("top"))||(_15.get("topCenter")>_16.get("topCenter")&&_15.get("top")>_16.get("bottom"))){_14.hideList.remove(_f.id);if(!_14.hideList.length){_14.style.visibility="visible";if(domLib_isKonq){_14.style.display="";}}}else{_14.hideList.set(_f.id,true);_14.style.visibility="hidden";if(domLib_isKonq){_14.style.display="none";}}}}}
function domLib_getOffsets(_19,_1a){if(typeof(_1a)=="undefined"){_1a=false;}
var _1b=_19;var _1c=_19.offsetWidth;var _1d=_19.offsetHeight;var _1e=0;var _1f=0;while(_19){_1e+=_19.offsetLeft;_1f+=_19.offsetTop;_19=_19.offsetParent;if(_19&&!_1a){_1e-=_19.scrollLeft;_1f-=_19.scrollTop;}}
if(domLib_isMacIE){_1e+=10;_1f+=10;}
return new Hash("left",_1e,"top",_1f,"right",_1e+_1c,"bottom",_1f+_1d,"leftCenter",_1e+_1c/2,"topCenter",_1f+_1d/2,"radius",Math.max(_1c,_1d));}
function domLib_setTimeout(_20,_21,_22){if(typeof(_22)=="undefined"){_22=[];}
if(_21==-1){return 0;}else{if(_21==0){_20(_22);return 0;}}
var _23=domLib_clone(_22);if(!domLib_hasBrokenTimeout){return setTimeout(function(){_20(_23);},_21);}else{var id=domLib_timeoutStateId++;var _25=new Hash();_25.set("function",_20);_25.set("args",_23);domLib_timeoutStates.set(id,_25);_25.set("timeoutId",setTimeout("domLib_timeoutStates.get("+id+").get('function')(domLib_timeoutStates.get("+id+").get('args')); domLib_timeoutStates.remove("+id+");",_21));return id;}}
function domLib_clearTimeout(_26){if(!domLib_hasBrokenTimeout){if(_26>0){clearTimeout(_26);}}else{if(domLib_timeoutStates.has(_26)){clearTimeout(domLib_timeoutStates.get(_26).get("timeoutId"));domLib_timeoutStates.remove(_26);}}}
function domLib_getEventPosition(_27){var _28=new Hash("x",0,"y",0,"scrollX",0,"scrollY",0);if(domLib_isIE){var doc=(domLib_standardsMode?document.documentElement:document.body);if(doc){_28.set("x",_27.clientX+doc.scrollLeft);_28.set("y",_27.clientY+doc.scrollTop);_28.set("scrollX",doc.scrollLeft);_28.set("scrollY",doc.scrollTop);}}else{_28.set("x",_27.pageX);_28.set("y",_27.pageY);_28.set("scrollX",_27.pageX-_27.clientX);_28.set("scrollY",_27.pageY-_27.clientY);}
return _28;}
function domLib_getIFrameReference(_2a){if(domLib_isGecko||domLib_isIE){return _2a.frameElement;}else{var _2b=_2a.name;if(!_2b||!_2a.parent){return null;}
var _2c=_2a.parent.document.getElementsByTagName("iframe");for(var i=0;i<_2c.length;i++){if(_2c[i].name==_2b){return _2c[i];}}
return null;}}
function domLib_getElementsByTagNames(_2e,_2f){var _30=[];for(var i=0;i<_2e.length;i++){var _32=document.getElementsByTagName(_2e[i]);for(var j=0;j<_32.length;j++){if(_32[j].tagName=="OBJECT"&&domLib_isGecko){var _34=_32[j].childNodes;var _35=false;for(var k=0;k<_34.length;k++){if(_34[k].tagName=="EMBED"){_35=true;break;}}
if(_35){continue;}}
if(_2f&&domLib_getComputedStyle(_32[j],"visibility")=="hidden"){continue;}
_30[_30.length]=_32[j];}}
return _30;}
function domLib_getComputedStyle(_37,_38){if(domLib_isIE){var _39=_38.replace(/-(.)/,function(a,b){return b.toUpperCase();});return eval("in_obj.currentStyle."+_39);}else{if(domLib_isKonq){return eval("in_obj.style."+_38);}else{return document.defaultView.getComputedStyle(_37,null).getPropertyValue(_38);}}}
var domTT_offsetX=(domLib_isIE?-2:0);var domTT_offsetY=(domLib_isIE?4:2);var domTT_direction="southeast";var domTT_mouseHeight=domLib_isIE?13:19;var domTT_closeLink="X";var domTT_closeAction="hide";var domTT_activateDelay=500;var domTT_maxWidth=false;var domTT_styleClass="domTT";var domTT_fade="neither";var domTT_lifetime=0;var domTT_grid=0;var domTT_trailDelay=200;var domTT_useGlobalMousePosition=true;var domTT_postponeActivation=false;var domTT_tooltipIdPrefix="[domTT]";var domTT_screenEdgeDetection=true;var domTT_screenEdgePadding=4;var domTT_oneOnly=false;var domTT_cloneNodes=false;var domTT_detectCollisions=true;var domTT_bannedTags=["OPTION"];var domTT_draggable=false;if(typeof(domTT_dragEnabled)=="undefined"){domTT_dragEnabled=false;}
var domTT_predefined=new Hash();var domTT_tooltips=new Hash();var domTT_lastOpened=0;var domTT_documentLoaded=false;var domTT_mousePosition=null;if(domLib_useLibrary&&domTT_useGlobalMousePosition){document.onmousemove=function(_3c){if(typeof(_3c)=="undefined"){_3c=window.event;}
domTT_mousePosition=domLib_getEventPosition(_3c);if(domTT_dragEnabled&&domTT_dragMouseDown){domTT_dragUpdate(_3c);}};}
function domTT_activate(_3d,_3e){if(!domLib_useLibrary||(domTT_postponeActivation&&!domTT_documentLoaded)){return false;}
if(typeof(_3e)=="undefined"){_3e=window.event;}
if(_3e!=null){var _3f=_3e.srcElement?_3e.srcElement:_3e.target;if(_3f!=null&&(","+domTT_bannedTags.join(",")+",").indexOf(","+_3f.tagName+",")!=-1){return false;}}
var _40=document.body;if(_3e!=null&&_3e.type.match(/key|mouse|click|contextmenu/i)){if(_3d.nodeType&&_3d.nodeType!=document.DOCUMENT_NODE){_40=_3d;}}else{if(typeof(_3d)!="object"&&!(_40=domTT_tooltips.get(_3d))){var _41=document.createElement("div");_40=document.body.appendChild(_41);_40.style.display="none";_40.id=_3d;}}
if(!_40.id){_40.id="__autoId"+domLib_autoId++;}
if(domTT_oneOnly&&domTT_lastOpened){domTT_deactivate(domTT_lastOpened);}
domTT_lastOpened=_40.id;var _42=domTT_tooltips.get(_40.id);if(_42){if(_42.get("eventType")!=_3e.type){if(_42.get("type")=="greasy"){_42.set("closeAction","destroy");domTT_deactivate(_40.id);}else{if(_42.get("status")!="inactive"){return _40.id;}}}else{if(_42.get("status")=="inactive"){_42.set("status","pending");_42.set("activateTimeout",domLib_setTimeout(domTT_runShow,_42.get("delay"),[_40.id,_3e]));return _40.id;}else{return _40.id;}}}
var _43=new Hash("caption","","content","","clearMouse",true,"closeAction",domTT_closeAction,"closeLink",domTT_closeLink,"delay",domTT_activateDelay,"direction",domTT_direction,"draggable",domTT_draggable,"fade",domTT_fade,"fadeMax",100,"grid",domTT_grid,"id",domTT_tooltipIdPrefix+_40.id,"inframe",false,"lifetime",domTT_lifetime,"offsetX",domTT_offsetX,"offsetY",domTT_offsetY,"parent",document.body,"position","absolute","styleClass",domTT_styleClass,"type","greasy","trail",false,"lazy",false);for(var i=2;i<arguments.length;i+=2){if(arguments[i]=="predefined"){var _45=domTT_predefined.get(arguments[i+1]);for(var j in _45.elementData){_43.set(j,_45.get(j));}}else{_43.set(arguments[i],arguments[i+1]);}}
_43.set("eventType",_3e!=null?_3e.type:null);if(_43.has("statusText")){try{window.status=_43.get("statusText");}
catch(e){}}
if(!_43.has("content")||_43.get("content")==""||_43.get("content")==null){if(typeof(_40.onmouseout)!="function"){_40.onmouseout=function(_47){domTT_mouseout(this,_47);};}
return _40.id;}
_43.set("owner",_40);domTT_create(_43);_43.set("delay",(_3e!=null&&_3e.type.match(/click|mousedown|contextmenu/i))?0:parseInt(_43.get("delay")));domTT_tooltips.set(_40.id,_43);domTT_tooltips.set(_43.get("id"),_43);_43.set("status","pending");_43.set("activateTimeout",domLib_setTimeout(domTT_runShow,_43.get("delay"),[_40.id,_3e]));return _40.id;}
function domTT_create(_48){var _49=_48.get("owner");var _4a=_48.get("parent");var _4b=_4a.ownerDocument||_4a.document;var _4c=_4b.createElement("div");var _4d=_4a.appendChild(_4c);_4d.style.position="absolute";_4d.style.left="0px";_4d.style.top="0px";_4d.style.visibility="hidden";_4d.id=_48.get("id");_4d.className=_48.get("styleClass");var _4e;var _4f=false;if(_48.get("caption")||(_48.get("type")=="sticky"&&_48.get("caption")!==false)){_4f=true;var _50=_4d.appendChild(_4b.createElement("table"));_50.style.borderCollapse="collapse";if(domLib_isKHTML){_50.cellSpacing=0;}
var _51=_50.appendChild(_4b.createElement("tbody"));var _52=0;var _53=_51.appendChild(_4b.createElement("tr"));var _54=_53.appendChild(_4b.createElement("td"));_54.style.padding="0px";var _55=_54.appendChild(_4b.createElement("div"));_55.className="caption";if(domLib_isIE50){_55.style.height="100%";}
if(_48.get("caption").nodeType){_55.appendChild(domTT_cloneNodes?_48.get("caption").cloneNode(1):_48.get("caption"));}else{_55.innerHTML=_48.get("caption");}
if(_48.get("type")=="sticky"){var _52=2;var _56=_53.appendChild(_4b.createElement("td"));_56.style.padding="0px";var _57=_56.appendChild(_4b.createElement("div"));_57.className="caption";if(domLib_isIE50){_57.style.height="100%";}
_57.style.textAlign="right";_57.style.cursor=domLib_stylePointer;_57.style.borderLeftWidth=_55.style.borderRightWidth="0px";_57.style.paddingLeft=_55.style.paddingRight="0px";_57.style.marginLeft=_55.style.marginRight="0px";if(_48.get("closeLink").nodeType){_57.appendChild(_48.get("closeLink").cloneNode(1));}else{_57.innerHTML=_48.get("closeLink");}
_57.onclick=function(){domTT_deactivate(_49.id);};_57.onmousedown=function(_58){if(typeof(_58)=="undefined"){_58=window.event;}
_58.cancelBubble=true;};if(domLib_isMacIE){_56.appendChild(_4b.createTextNode("\n"));}}
if(domLib_isMacIE){_54.appendChild(_4b.createTextNode("\n"));}
var _59=_51.appendChild(_4b.createElement("tr"));var _5a=_59.appendChild(_4b.createElement("td"));_5a.style.padding="0px";if(_52){if(domLib_isIE||domLib_isOpera){_5a.colSpan=_52;}else{_5a.setAttribute("colspan",_52);}}
_4e=_5a.appendChild(_4b.createElement("div"));if(domLib_isIE50){_4e.style.height="100%";}}else{_4e=_4d.appendChild(_4b.createElement("div"));}
_4e.className="contents";var _5b=_48.get("content");if(typeof(_5b)=="function"){_5b=_5b(_48.get("id"));}
if(_5b!=null&&_5b.nodeType){_4e.appendChild(domTT_cloneNodes?_5b.cloneNode(1):_5b);}else{_4e.innerHTML=_5b;}
if(_48.has("width")){_4d.style.width=parseInt(_48.get("width"))+"px";}
var _5c=domTT_maxWidth;if(_48.has("maxWidth")){if((_5c=_48.get("maxWidth"))===false){_4d.style.maxWidth=domLib_styleNoMaxWidth;}else{_5c=parseInt(_48.get("maxWidth"));_4d.style.maxWidth=_5c+"px";}}
if(_5c!==false&&(domLib_isIE||domLib_isKHTML)&&_4d.offsetWidth>_5c){_4d.style.width=_5c+"px";}
_48.set("offsetWidth",_4d.offsetWidth);_48.set("offsetHeight",_4d.offsetHeight);if(domLib_isKonq&&_4f&&!_4d.style.width){var _5d=document.defaultView.getComputedStyle(_4d,"").getPropertyValue("border-left-width");var _5e=document.defaultView.getComputedStyle(_4d,"").getPropertyValue("border-right-width");_5d=_5d.substring(_5d.indexOf(":")+2,_5d.indexOf(";"));_5e=_5e.substring(_5e.indexOf(":")+2,_5e.indexOf(";"));var _5f=2*((_5d?parseInt(_5d):0)+(_5e?parseInt(_5e):0));_4d.style.width=(_4d.offsetWidth-_5f)+"px";}
if(domLib_isIE||domLib_isOpera){if(!_4d.style.width){_4d.style.width=(_4d.offsetWidth-2)+"px";}
_4d.style.height=(_4d.offsetHeight-2)+"px";}
var _60,_61;if(_48.get("position")=="absolute"&&!(_48.has("x")&&_48.has("y"))){switch(_48.get("direction")){case"northeast":_60=_48.get("offsetX");_61=0-_4d.offsetHeight-_48.get("offsetY");break;case"northwest":_60=0-_4d.offsetWidth-_48.get("offsetX");_61=0-_4d.offsetHeight-_48.get("offsetY");break;case"north":_60=0-parseInt(_4d.offsetWidth/2);_61=0-_4d.offsetHeight-_48.get("offsetY");break;case"southwest":_60=0-_4d.offsetWidth-_48.get("offsetX");_61=_48.get("offsetY");break;case"southeast":_60=_48.get("offsetX");_61=_48.get("offsetY");break;case"south":_60=0-parseInt(_4d.offsetWidth/2);_61=_48.get("offsetY");break;}
if(_48.get("inframe")){var _62=domLib_getIFrameReference(window);if(_62){var _63=domLib_getOffsets(_62);_60+=_63.get("left");_61+=_63.get("top");}}}else{_60=0;_61=0;_48.set("trail",false);}
_48.set("offsetX",_60);_48.set("offsetY",_61);if(_48.get("clearMouse")&&_48.get("direction").indexOf("south")!=-1){_48.set("mouseOffset",domTT_mouseHeight);}else{_48.set("mouseOffset",0);}
if(domLib_canFade&&typeof(Fadomatic)=="function"){if(_48.get("fade")!="neither"){var _64=new Fadomatic(_4d,10,0,0,_48.get("fadeMax"));_48.set("fadeHandler",_64);}}else{_48.set("fade","neither");}
if(_48.get("trail")&&typeof(_49.onmousemove)!="function"){_49.onmousemove=function(_65){domTT_mousemove(this,_65);};}
if(typeof(_49.onmouseout)!="function"){_49.onmouseout=function(_66){domTT_mouseout(this,_66);};}
if(_48.get("type")=="sticky"){if(_48.get("position")=="absolute"&&domTT_dragEnabled&&_48.get("draggable")){if(domLib_isIE){_53.onselectstart=function(){return false;};}
_53.onmousedown=function(_67){domTT_dragStart(_4d,_67);};_53.onmousemove=function(_68){domTT_dragUpdate(_68);};_53.onmouseup=function(){domTT_dragStop();};}}else{if(_48.get("type")=="velcro"){_4d.onmouseout=function(_69){if(typeof(_69)=="undefined"){_69=window.event;}
if(!domLib_isDescendantOf(_69[domLib_eventTo],_4d,domTT_bannedTags)){domTT_deactivate(_49.id);}};_4d.onclick=function(_6a){domTT_deactivate(_49.id);};}}
if(_48.get("position")=="relative"){_4d.style.position="relative";}
_48.set("node",_4d);_48.set("status","inactive");}
function domTT_show(_6b,_6c){var _6d=domTT_tooltips.get(_6b);var _6e=_6d.get("status");var _6f=_6d.get("node");if(_6d.get("position")=="absolute"){var _70,_71;if(_6d.has("x")&&_6d.has("y")){_70=_6d.get("x");_71=_6d.get("y");}else{if(!domTT_useGlobalMousePosition||domTT_mousePosition==null||_6e=="active"||_6d.get("delay")==0){var _72=domLib_getEventPosition(_6c);var _73=_72.get("x");var _74=_72.get("y");if(_6d.get("inframe")){_73-=_72.get("scrollX");_74-=_72.get("scrollY");}
if(_6e=="active"&&_6d.get("trail")!==true){var _75=_6d.get("trail");if(_75=="x"){_70=_73;_71=_6d.get("mouseY");}else{if(_75=="y"){_70=_6d.get("mouseX");_71=_74;}}}else{_70=_73;_71=_74;}}else{_70=domTT_mousePosition.get("x");_71=domTT_mousePosition.get("y");if(_6d.get("inframe")){_70-=domTT_mousePosition.get("scrollX");_71-=domTT_mousePosition.get("scrollY");}}}
if(_6d.get("grid")){if(_6c.type!="mousemove"||(_6e=="active"&&(Math.abs(_6d.get("lastX")-_70)>_6d.get("grid")||Math.abs(_6d.get("lastY")-_71)>_6d.get("grid")))){_6d.set("lastX",_70);_6d.set("lastY",_71);}else{return false;}}
_6d.set("mouseX",_70);_6d.set("mouseY",_71);var _76;if(domTT_screenEdgeDetection){_76=domTT_correctEdgeBleed(_6d.get("offsetWidth"),_6d.get("offsetHeight"),_70,_71,_6d.get("offsetX"),_6d.get("offsetY"),_6d.get("mouseOffset"),_6d.get("inframe")?window.parent:window);}else{_76={"x":_70+_6d.get("offsetX"),"y":_71+_6d.get("offsetY")+_6d.get("mouseOffset")};}
_6f.style.left=_76.x+"px";_6f.style.top=_76.y+"px";_6f.style.zIndex=domLib_zIndex++;}
if(_6e=="pending"){_6d.set("status","active");_6f.style.display="";_6f.style.visibility="visible";var _77=_6d.get("fade");if(_77!="neither"){var _78=_6d.get("fadeHandler");if(_77=="out"||_77=="both"){_78.haltFade();if(_77=="out"){_78.halt();}}
if(_77=="in"||_77=="both"){_78.fadeIn();}}
if(_6d.get("type")=="greasy"&&_6d.get("lifetime")!=0){_6d.set("lifetimeTimeout",domLib_setTimeout(domTT_runDeactivate,_6d.get("lifetime"),[_6f.id]));}}
if(_6d.get("position")=="absolute"&&domTT_detectCollisions){domLib_detectCollisions(_6f,false,true);}}
function domTT_deactivate(_79){var _7a=domTT_tooltips.get(_79);if(_7a){var _7b=_7a.get("status");if(_7b=="pending"){domLib_clearTimeout(_7a.get("activateTimeout"));_7a.set("status","inactive");}else{if(_7b=="active"){if(_7a.get("lifetime")){domLib_clearTimeout(_7a.get("lifetimeTimeout"));}
var _7c=_7a.get("node");if(_7a.get("closeAction")=="hide"){var _7d=_7a.get("fade");if(_7d!="neither"){var _7e=_7a.get("fadeHandler");if(_7d=="out"||_7d=="both"){_7e.fadeOut();}else{_7e.hide();}}else{_7c.style.display="none";}}else{_7a.get("parent").removeChild(_7c);domTT_tooltips.remove(_7a.get("owner").id);domTT_tooltips.remove(_7a.get("id"));}
_7a.set("status","inactive");if(domTT_detectCollisions){domLib_detectCollisions(_7c,true,true);}}}}}
function domTT_mouseout(_7f,_80){if(!domLib_useLibrary){return false;}
if(typeof(_80)=="undefined"){_80=window.event;}
var _81=domLib_isDescendantOf(_80[domLib_eventTo],_7f,domTT_bannedTags);var _82=domTT_tooltips.get(_7f.id);if(_82&&(_82.get("type")=="greasy"||_82.get("status")!="active")){if(!_81){domTT_deactivate(_7f.id);try{window.status=window.defaultStatus;}
catch(e){}}}else{if(!_81){try{window.status=window.defaultStatus;}
catch(e){}}}}
function domTT_mousemove(_83,_84){if(!domLib_useLibrary){return false;}
if(typeof(_84)=="undefined"){_84=window.event;}
var _85=domTT_tooltips.get(_83.id);if(_85&&_85.get("trail")&&_85.get("status")=="active"){if(_85.get("lazy")){domLib_setTimeout(domTT_runShow,domTT_trailDelay,[_83.id,_84]);}else{domTT_show(_83.id,_84);}}}
function domTT_correctEdgeBleed(_86,_87,_88,_89,_8a,_8b,_8c,_8d){var win,doc;var _90,_91;var _92,_93,_94,_95;var x=_88+_8a;var y=_89+_8b+_8c;win=(typeof(_8d)=="undefined"?window:_8d);doc=((domLib_standardsMode&&(domLib_isIE||domLib_isGecko))?win.document.documentElement:win.document.body);if(domLib_isIE){_92=doc.clientHeight;_93=doc.clientWidth;_94=doc.scrollTop;_95=doc.scrollLeft;}else{_92=doc.clientHeight;_93=doc.clientWidth;if(domLib_isKHTML){_92=win.innerHeight;}
_94=win.pageYOffset;_95=win.pageXOffset;}
if((_90=(x-_95)+_86-(_93-domTT_screenEdgePadding))>0){x-=_90;}
if((x-_95)<domTT_screenEdgePadding){x=domTT_screenEdgePadding+_95;}
if((_91=(y-_94)+_87-(_92-domTT_screenEdgePadding))>0){y=_89-_87-_8b;}
if((y-_94)<domTT_screenEdgePadding){y=_89+domTT_mouseHeight+_8b;}
return{"x":x,"y":y};}
function domTT_runDeactivate(_98){domTT_deactivate(_98[0]);}
function domTT_runShow(_99){domTT_show(_99[0],_99[1]);}