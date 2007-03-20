dojo.provide("struts.widget.BindDiv");

dojo.require("dojo.io");
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

    //update times
    updateFreq : 0,
    delay : 0,
    autoStart : true,
    timer : null,

    //messages
    loadingText : "Loading...",
    showLoading : true,
    errorText : "",
    showError : true,

    //pub/sub events
    listenTopics : "",
    notifyTopics : "",
    notifyTopicsArray : null,
    stopTimerListenTopics : "",
    startTimerListenTopics : "",

    //callbacks
    beforeLoading : "",
    afterLoading : "",

    formId : "",
    formFilter : "",
    firstTime : true,

    indicator: "",

	//make dojo process the content
	parseContent : true,

    onDownloadStart : function(event) {
      if(!dojo.string.isBlank(this.beforeLoading)) {
        this.log("Executing " + this.beforeLoading);
        var result = eval(this.beforeLoading);
        if(result !== null && !result) {
          return;
        }
      }
      if(!this.showLoading) {
        event.returnValue = false;
        return;
      }
      if(this.showLoading && !dojo.string.isBlank(this.loadingText)) {
        event.text = this.loadingText;
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
            dojo.event.topic.publish(topic, data, type, e);
          } catch(ex) {
            self.log(ex);
          }
        });
      }
    },

    postCreate : function(args, frag) {
      struts.widget.BindDiv.superclass.postCreate.apply(this);

      var self = this;
      var hitchedRefresh = function() {
        dojo.lang.hitch(self, "refresh")();
      };
      var hitchedStartTimer = function() {
        dojo.lang.hitch(self, "startTimer")();
      };

      if(this.updateFreq > 0) {
        this.timer = new dojo.lang.timing.Timer(this.updateFreq);
        this.timer.onTick = hitchedRefresh;

        //start the timer
        if(this.autoStart) {
          //start after delay
          if(this.delay > 0) {
            //start time after delay
            dojo.lang.setTimeout(this.delay, hitchedStartTimer);
          } else {
            //start timer now
            this.startTimer();
          }
        }
      } else {
        //no timer
        if(this.delay > 0) {
          //load after delay
          dojo.lang.setTimeout(this.delay, hitchedRefresh);
        }
      }

      //start the timer
      if(this.autoStart) {
        //start after delay
        if(this.delay > 0) {
          if(this.updateFreq > 0) {
            //start time after delay
          	dojo.lang.setTimeout(this.delay, hitchedStartTimer);
          } else {
            //load after delay
            dojo.lang.setTimeout(this.delay, hitchedRefresh);
          }
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

      if(!dojo.string.isBlank(this.notifyTopics)) {
        this.notifyTopicsArray = this.notifyTopics.split(",");
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
    },

    _downloadExternalContent: function(url, useCache) {
      if(this.firstTime) {
        this.firstTime = false;
        if(this.delay > 0) {
          return;
        }
      }

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
        handler: function(type, data, e) {
          //hide indicator
          dojo.html.hide(self.indicator);

          if(!dojo.string.isBlank(self.afterLoading)) {
            self.log("Executing " + self.afterLoading);
            eval(self.afterLoading);
          }

          self.notify(data, type, null);

          if(type == "load") {
            self.onDownloadEnd.call(self, url, data);
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
    }
  }
);
