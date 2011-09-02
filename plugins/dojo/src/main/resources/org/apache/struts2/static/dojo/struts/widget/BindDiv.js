/*
 * $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

dojo.provide("struts.widget.BindDiv");

dojo.require("dojo.io.*");
dojo.require("dojo.widget.ContentPane");
dojo.require("dojo.lang.timing.Timer");

dojo.widget.defineWidget(
  "struts.widget.BindDiv",
  dojo.widget.ContentPane, {
    widgetType : "BindDiv",

    //from ContentPane
    href : "",
    extractContent : false,
    parseContent : false,
    cacheContent : false,
    refreshOnShow : false,
    executeScripts : false,
    preload : true,
    
    //update times
    updateFreq : 0,
    delay : 0,
    autoStart : true,
    timer : null,

    //messages
    loadingText : "Loading...",
    showLoading : false,
    errorText : "",
    showError : true,

    //pub/sub events
    listenTopics : "",
    notifyTopics : "",
    notifyTopicsArray : null,
    stopTimerListenTopics : "",
    startTimerListenTopics : "",
    beforeNotifyTopics : "",
    beforeNotifyTopicsArray : null,
    afterNotifyTopics : "",
    afterNotifyTopicsArray : null,
    errorNotifyTopics : "",
    errorNotifyTopicsArray : null,
    

    //callbacks
    beforeLoading : "",
    afterLoading : "",

    formId : "",
    formFilter : "",

    indicator: "",

	//make dojo process the content
	parseContent : true,

    highlightColor : "",
    highlightDuration : 2000,
    
    //only used when inside a tabbedpanel
    disabled : false,
    
    transport : "",
    
    onDownloadStart : function(event) {
      if(!this.showLoading) {
        event.returnValue = false;
        return;
      }
      if(this.showLoading && !dojo.string.isBlank(this.loadingText)) {
        event.text = this.loadingText;
      }
    },
    
    highlight : function() {
      if(!dojo.string.isBlank(this.highlightColor)) {
        var effect = dojo.lfx.html.highlight([this.domNode], this.highlightColor, this.highlightDuration);
        effect.play();    
      }        
    },

    onDownloadError : function(event) {
      this.onError(event);
    },

    onContentError : function(event) {
      this.onError(event);
    },

    onExecError : function(event) {
      this.onError(event);
    },

    onError : function(event) {
      if(this.showError) {
        if(!dojo.string.isBlank(this.errorText)) {
          event.text = this.errorText;
        }
      } else {
        event.text = "";
      }
    },

    notify : function(data, type, e) {
      if(this.notifyTopicsArray) {
        var self = this;
        dojo.lang.forEach(this.notifyTopicsArray, function(topic) {
          try {
            dojo.event.topic.publish(topic, data, type, e, self);
          } catch(ex) {
            self.log(ex);
          }
        });
      }
      
      //before, after and error topics
      var topicsArray = null;
      switch(type) {
        case "before":
          this.notifyTo(this.beforeNotifyTopicsArray, null, e);
          break;
        case "load":
          this.notifyTo(this.afterNotifyTopicsArray, data, e);
          break;
        case "error":
          this.notifyTo(this.errorNotifyTopicsArray, data, e);
          break;
      }
    },
    
    notifyTo : function(topicsArray, data, e) {
      var self = this;
      if(topicsArray) {
        dojo.lang.forEach(topicsArray, function(topic) {
        try {
          if(data != null) {
            dojo.event.topic.publish(topic, data, e, self);
          } else {
            dojo.event.topic.publish(topic, e, self);
          }
        } catch(ex){
          self.log(ex);
        }
        });
      }
    },

    postCreate : function(args, frag) {
      if (this.handler !== "") {
          this.setHandler(this.handler);
      }

      var self = this;
      var hitchedRefresh = function() {
        dojo.lang.hitch(self, "refresh")();
      };
      var hitchedStartTimer = function() {
        dojo.lang.hitch(self, "startTimer")();
      };

      if(this.updateFreq > 0) {
        //there is a timer
        this.timer = new dojo.lang.timing.Timer(this.updateFreq);
        this.timer.onTick = hitchedRefresh;

        if(this.autoStart) {
          //start the timer
          if(this.delay > 0) {
            //start time after delay
            dojo.lang.setTimeout(hitchedStartTimer, this.delay);
          } else {
            //start timer now
            this.startTimer();
          }
        }
      } else {
        //no timer
        if(this.delay > 0) {
          //load after delay
          dojo.lang.setTimeout(hitchedRefresh, this.delay);
        }
      }

      //attach listeners
      if(!dojo.string.isBlank(this.listenTopics)) {
        this.log("Listening to " + this.listenTopics + " to refresh");
        var topics = this.listenTopics.split(",");
        if(topics) {
          dojo.lang.forEach(topics, function(topic){
            dojo.event.topic.subscribe(topic, self, "refresh");
          });
        }
      }

      if(!dojo.string.isBlank(this.stopTimerListenTopics)) {
        this.log("Listening to " + this.stopTimerListenTopics + " to stop timer");
        var stopTopics = this.stopTimerListenTopics.split(",");
        if(stopTopics) {
          dojo.lang.forEach(stopTopics, function(topic){
            dojo.event.topic.subscribe(topic, self, "stopTimer");
          });
        }
      }

      if(!dojo.string.isBlank(this.startTimerListenTopics)) {
        this.log("Listening to " + this.stopTimerListenTopics + " to start timer");
        var startTopics = this.startTimerListenTopics.split(",");
        if(startTopics) {
          dojo.lang.forEach(startTopics, function(topic){
            dojo.event.topic.subscribe(topic, self, "startTimer");
          });
        }
      }
     
      //notify topics
      if(!dojo.string.isBlank(this.notifyTopics)) {
        this.notifyTopicsArray = this.notifyTopics.split(",");
      }
      
      //before topics
      if(!dojo.string.isBlank(this.beforeNotifyTopics)) {
        this.beforeNotifyTopicsArray = this.beforeNotifyTopics.split(",");
      }
      
      //after topics
      if(!dojo.string.isBlank(this.afterNotifyTopics)) {
        this.afterNotifyTopicsArray = this.afterNotifyTopics.split(",");
      }
      
      //error topics
      if(!dojo.string.isBlank(this.errorNotifyTopics)) {
        this.errorNotifyTopicsArray = this.errorNotifyTopics.split(",");
      }
      
      if(this.isShowing() && this.preload && this.updateFreq <= 0 && this.delay <= 0) {
        this.refresh();
      }
    },

    _downloadExternalContent: function(url, useCache) {
      
      var request = {cancel: false};
      this.notify(this.widgetId, "before", request);
      if(request.cancel) {
        return;
      }

      //show indicator
      dojo.html.show(this.indicator);

      this._handleDefaults("Loading...", "onDownloadStart");
      var self = this;
      dojo.io.bind({
        url: url,
        useCache: useCache,
        preventCache: !useCache,
        mimetype: "text/html",
        formNode: dojo.byId(self.formId),
        formFilter: window[self.formFilter],
        transport: self.transport,
        handler: function(type, data, e) {
          //hide indicator
          dojo.html.hide(self.indicator);

          self.notify(data, type, e);

          if(type == "load") {
            self.onDownloadEnd.call(self, url, data);
            self.highlight();
          } else {
            // works best when from a live server instead of from file system
            self._handleDefaults.call(self, "Error loading '" + url + "' (" + e.status + " "+  e.statusText + ")", "onDownloadError");
            self.onLoad();
          }
        }
      });
     },

    log : function(text) {
      dojo.debug("[" + this.widgetId + "] " + text);
    },

    stopTimer : function() {
      if(this.timer && this.timer.isRunning) {
        this.log("stopping timer");
        this.timer.stop();
      }
    },

    startTimer : function() {
      if(this.timer && !this.timer.isRunning) {
        this.log("starting timer with update interval " + this.updateFreq);
        this.timer.start();
      }
    },
    
    //from Dojo's ContentPane
    //TODO: remove when fixed on Dojo (WW-1869)
    splitAndFixPaths:function (s, url) {
      var titles = [], scripts = [], tmp = [];
      var match = [], requires = [], attr = [], styles = [];
      var str = "", path = "", fix = "", tagFix = "", tag = "", origPath = "";
      if (!url) {
        url = "./";
      }
      if (s) {
        var regex = /<title[^>]*>([\s\S]*?)<\/title>/i;
        while (match = regex.exec(s)) {
          titles.push(match[1]);
          s = s.substring(0, match.index) + s.substr(match.index + match[0].length);
        }
        if (this.adjustPaths) {
          var regexFindTag = /<[a-z][a-z0-9]*[^>]*\s(?:(?:src|href|style)=[^>])+[^>]*>/i;
          var regexFindAttr = /\s(src|href|style)=(['"]?)([\w()\[\]\/.,\\'"-:;#=&?\s@!]+?)\2/i;
          var regexProtocols = /^(?:[#]|(?:(?:https?|ftps?|file|javascript|mailto|news):))/;
          while (tag = regexFindTag.exec(s)) {
            str += s.substring(0, tag.index);
            s = s.substring((tag.index + tag[0].length), s.length);
            tag = tag[0];
            tagFix = "";
            while (attr = regexFindAttr.exec(tag)) {
              path = "";
              origPath = attr[3];
              switch (attr[1].toLowerCase()) {
                case "src":
                case "href":
                if (regexProtocols.exec(origPath)) {
                  path = origPath;
                } else {
                  path = (new dojo.uri.Uri(url, origPath).toString());
                }
                break;
                case "style":
                path = dojo.html.fixPathsInCssText(origPath, url);
                break;
                default:
                path = origPath;
              }
              fix = " " + attr[1] + "=" + attr[2] + path + attr[2];
              tagFix += tag.substring(0, attr.index) + fix;
              tag = tag.substring((attr.index + attr[0].length), tag.length);
            }
            str += tagFix + tag;
          }
          s = str + s;
        }
        regex = /(?:<(style)[^>]*>([\s\S]*?)<\/style>|<link ([^>]*rel=['"]?stylesheet['"]?[^>]*)>)/i;
        while (match = regex.exec(s)) {
          if (match[1] && match[1].toLowerCase() == "style") {
            styles.push(dojo.html.fixPathsInCssText(match[2], url));
          } else {
            if (attr = match[3].match(/href=(['"]?)([^'">]*)\1/i)) {
              styles.push({path:attr[2]});
            }
          }
          s = s.substring(0, match.index) + s.substr(match.index + match[0].length);
        }
        var regex = /<script([^>]*)>([\s\S]*?)<\/script>/i;
        var regexSrc = /src=(['"]?)([^"']*)\1/i;
        var regexDojoJs = /.*(\bdojo\b\.js(?:\.uncompressed\.js)?)$/;
        var regexInvalid = /(?:var )?\bdjConfig\b(?:[\s]*=[\s]*\{[^}]+\}|\.[\w]*[\s]*=[\s]*[^;\n]*)?;?|dojo\.hostenv\.writeIncludes\(\s*\);?/g;
        var regexRequires = /dojo\.(?:(?:require(?:After)?(?:If)?)|(?:widget\.(?:manager\.)?registerWidgetPackage)|(?:(?:hostenv\.)?setModulePrefix|registerModulePath)|defineNamespace)\((['"]).*?\1\)\s*;?/;
        while (match = regex.exec(s)) {
          if (this.executeScripts && match[1]) {
            if (attr = regexSrc.exec(match[1])) {
              if (regexDojoJs.exec(attr[2])) {
                dojo.debug("Security note! inhibit:" + attr[2] + " from  being loaded again.");
              } else {
                scripts.push({path:attr[2]});
              }
            }
          }
          if (match[2]) {
            var sc = match[2].replace(regexInvalid, "");
            if (!sc) {
              continue;
            }
            while (tmp = regexRequires.exec(sc)) {
              requires.push(tmp[0]);
              sc = sc.substring(0, tmp.index) + sc.substr(tmp.index + tmp[0].length);
            }
            if (this.executeScripts) {
              scripts.push(sc);
            }
          }
          s = s.substr(0, match.index) + s.substr(match.index + match[0].length);
        }
        if (this.extractContent) {
          match = s.match(/<body[^>]*>\s*([\s\S]+)\s*<\/body>/im);
          if (match) {
            s = match[1];
          }
        }
        if (this.executeScripts && this.scriptSeparation) {
          var regex = /(<[a-zA-Z][a-zA-Z0-9]*\s[^>]*?\S=)((['"])[^>]*scriptScope[^>]*>)/;
          var regexAttr = /([\s'";:\(])scriptScope(.*)/;
          str = "";
          while (tag = regex.exec(s)) {
            tmp = ((tag[3] == "'") ? "\"" : "'");
            fix = "";
            str += s.substring(0, tag.index) + tag[1];
            while (attr = regexAttr.exec(tag[2])) {
              tag[2] = tag[2].substring(0, attr.index) + attr[1] + "dojo.widget.byId(" + tmp + this.widgetId + tmp + ").scriptScope" + attr[2];
            }
            str += tag[2];
            s = s.substr(tag.index + tag[0].length);
          }
          s = str + s;
        }
      }
      return {"xml":s, "styles":styles, "titles":titles, "requires":requires, "scripts":scripts, "url":url};
    }
});
