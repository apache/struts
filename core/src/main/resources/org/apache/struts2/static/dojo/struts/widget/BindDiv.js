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
    updateInterval : 0,
    delay : 0,
    autoStart : true,
    timer : null,

    //messages
    loadingText : "Loading...",
    errorText : "",

    //pub/sub events
    refreshListenTopic : "",
    stopTimerListenTopic : "",
    startTimerListenTopic : "",

    //callbacks
    beforeLoading : "",
    afterLoading : "",

    formId : "",
    formFilter : "",

    onDownloadStart : function(event) {
      if(!dojo.string.isBlank(this.beforeLoading)) {
        this.log("Executing " + this.beforeLoading);
        eval(this.beforeLoading);
      }
      if(!dojo.string.isBlank(this.loadingText)) {
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
      if(!dojo.string.isBlank(this.errorText)) {
        event.text = this.errorText;
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

      if(this.updateInterval > 0) {
        this.timer = new dojo.lang.timing.Timer(this.updateInterval);
        this.timer.onTick = hitchedRefresh;
      }

      var delay = this.delay >= 0 ? this.delay : 0;
      //start the timer
      if(this.autoStart) {
        //start after delay
        dojo.lang.setTimeout(delay, hitchedStartTimer);
        if(delay === 0) {
          //load content now
          this.refresh();
        }
      }

      //attach listeners
      if(!dojo.string.isBlank(this.refreshListenTopic)) {
        this.log("Listening to " + this.refreshListenTopic + " to refresh");
        dojo.event.topic.subscribe(this.refreshListenTopic, this, "refresh");
      }
      if(!dojo.string.isBlank(this.stopTimerListenTopic)) {
        this.log("Listening to " + this.stopTimerListenTopic + " to stop timer");
        dojo.event.topic.subscribe(this.stopTimerListenTopic, this, "stopTimer");
      }
      if(!dojo.string.isBlank(this.startTimerListenTopic)) {
        this.log("Listening to " + this.startTimerListenTopic + " to start timer");
        dojo.event.topic.subscribe(this.startTimerListenTopic, this, "startTimer");
      }

      if(!dojo.string.isBlank(this.afterLoading)) {
        dojo.event.connect("after", this, "onDownloadEnd", function(){
          self.log("Executing " + self.afterLoading);
          eval(self.afterLoading);
        });
      }
    },

    _downloadExternalContent: function(url, useCache) {
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
        this.log("starting timer with update interval " + this.updateInterval);
        this.timer.start();
      }
    }
  }
);
