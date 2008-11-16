/*
 * $Id: pom.xml 560558 2007-07-28 15:47:10Z apetrelli $
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

dojo.provide("struts.widget.ComboBox");

dojo.require("dojo.html.*");
dojo.require("dojo.widget.ComboBox");

struts.widget.ComboBoxDataProvider = function(/*Array*/ dataPairs, /*Number*/ limit, /*Number*/ timeout){
  // NOTE: this data provider is designed as a naive reference
  // implementation, and as such it is written more for readability than
  // speed. A deployable data provider would implement lookups, search
  // caching (and invalidation), and a significantly less naive data
  // structure for storage of items.

  this.data = [];
  this.searchTimeout = timeout || 500;
  this.searchLimit = limit || 30;
  this.searchType = "STARTSTRING"; // may also be "STARTWORD" or "SUBSTRING"
  this.caseSensitive = false;
  // for caching optimizations
  this._lastSearch = "";
  this._lastSearchResults = null;

  this.formId = "";
  this.formFilter = "";
  this.firstRequest = true;

  this.cbox = null;
  this.init = function(/*Widget*/ cbox, /*DomNode*/ node){
    this.cbox = cbox;
    this.formId = cbox.formId;
    this.formFilter = cbox.formFilter;
    this.searchLimit = cbox.searchLimit;
    
    if(!dojo.string.isBlank(cbox.dataUrl)){
      this.getData(cbox.dataUrl);
    }else{
      // check to see if we can populate the list from <option> elements
      if((node)&&(node.nodeName.toLowerCase() == "select")){
        // NOTE: we're not handling <optgroup> here yet
        var opts = node.getElementsByTagName("option");
        var ol = opts.length;
        var data = [];
        for(var x=0; x<ol; x++){
          var text = opts[x].textContent || opts[x].innerText || opts[x].innerHTML;
          var keyValArr = [String(text), String(opts[x].value)];
          data.push(keyValArr);
          if(opts[x].selected){
            cbox.setAllValues(keyValArr[0], keyValArr[1]);
          }
        }
        this.setData(data);
      }
    }
  };

  this.getData = function(/*String*/ url){
    //show indicator
    dojo.html.show(this.cbox.indicator);

    dojo.io.bind({
      url: url,
      formNode: dojo.byId(this.formId),
      formFilter: window[this.formFilter],
      load: dojo.lang.hitch(this, function(type, data, evt) {
        //show indicator
        dojo.html.hide(this.cbox.indicator);

        //if notifyTopics is published on the first request (onload)
        //the value of listeners will be reset
        if(!this.firstRequest) {
          this.cbox.notify.apply(this.cbox, [data, type, evt]);
        }
        var arrData = null;
        var dataByName = data[this.cbox.dataFieldName];
        if(!dojo.lang.isArray(data)) {
           //if there is a dataFieldName, take it
           if(dataByName) {
             if(dojo.lang.isArray(dataByName)) {
                //ok, it is an array
                arrData = dataByName;
             } else if(dojo.lang.isObject(dataByName)) {
                //it is an object, treat it like a map
                arrData = [];
                for(var key in dataByName){
                    arrData.push([dataByName[key], key]);
                }
             }
           } else {
             //try to find a match
             var tmpArrData = [];
             for(var key in data){
               //does it start with the field name? take it
               if(dojo.string.startsWith(key, this.cbox.name)) {
                 arrData = data[key];
                 break;
               } else {
                 //if nathing else is found, we will use values in this 
                 //object as the data
                 tmpArrData.push([data[key], key]);
               }
               //grab the first array found, we will use it if nothing else
               //is found
               if(!arrData && dojo.lang.isArray(data[key]) && !dojo.lang.isString(data[key])) {
                 arrData = data[key];
               }
             }
             if(!arrData) {
               arrData = tmpArrData;
             }
           }
           
           data = arrData;
        }
        this.setData(data);
        this.firstRequest = false;
      }),
      mimetype: "text/json"
    });
  };

  this.startSearch = function(/*String*/ searchStr, /*String*/ type, /*Boolean*/ ignoreLimit){
    // FIXME: need to add timeout handling here!!
    this._preformSearch(searchStr, type, ignoreLimit);
  };

  this._preformSearch = function(/*String*/ searchStr, /*String*/ type, /*Boolean*/ ignoreLimit){
    //
    //  NOTE: this search is LINEAR, which means that it exhibits perhaps
    //  the worst possible speed characteristics of any search type. It's
    //  written this way to outline the responsibilities and interfaces for
    //  a search.
    //
    var st = type||this.searchType;
    // FIXME: this is just an example search, which means that we implement
    // only a linear search without any of the attendant (useful!) optimizations
    var ret = [];
    if(!this.caseSensitive){
      searchStr = searchStr.toLowerCase();
    }
    for(var x=0; x<this.data.length; x++){
      if(!this.data[x]) {
        //needed for IE
        continue;
      }
      if((!ignoreLimit)&&(ret.length >= this.searchLimit)){
        break;
      }
      // FIXME: we should avoid copies if possible!
      var dataLabel = new String((!this.caseSensitive) ? this.data[x][0].toLowerCase() : this.data[x][0]);
      if(dataLabel.length < searchStr.length){
        // this won't ever be a good search, will it? What if we start
        // to support regex search?
        continue;
      }

      if(st == "STARTSTRING"){
        if(searchStr == dataLabel.substr(0, searchStr.length)){
          ret.push(this.data[x]);
        }
      }else if(st == "SUBSTRING"){
        // this one is a gimmie
        if(dataLabel.indexOf(searchStr) >= 0){
          ret.push(this.data[x]);
        }
      }else if(st == "STARTWORD"){
        // do a substring search and then attempt to determine if the
        // preceeding char was the beginning of the string or a
        // whitespace char.
        var idx = dataLabel.indexOf(searchStr);
        if(idx == 0){
          // implicit match
          ret.push(this.data[x]);
        }
        if(idx <= 0){
          // if we didn't match or implicily matched, march onward
          continue;
        }
        // otherwise, we have to go figure out if the match was at the
        // start of a word...
        // this code is taken almost directy from nWidgets
        var matches = false;
        while(idx!=-1){
          // make sure the match either starts whole string, or
          // follows a space, or follows some punctuation
          if(" ,/(".indexOf(dataLabel.charAt(idx-1)) != -1){
            // FIXME: what about tab chars?
            matches = true; break;
          }
          idx = dataLabel.indexOf(searchStr, idx+1);
        }
        if(!matches){
          continue;
        }else{
          ret.push(this.data[x]);
        }
      }
    }
    this.provideSearchResults(ret);
  };

  this.provideSearchResults = function(/*Array*/ resultsDataPairs){
  };

  this.addData = function(/*Array*/ pairs){
    // FIXME: incredibly naive and slow!
    this.data = this.data.concat(pairs);
  };

  this.setData = function(/*Array*/ pdata){
    // populate this.data and initialize lookup structures
    this.data = pdata;
    //all ellements must be a key and value pair
    for(var i = 0; i < this.data.length; i++) {
      var element = this.data[i];
      if(!dojo.lang.isArray(element)) {
        this.data[i] = [element, element];
      }
    }
  };

  if(dataPairs){
    this.setData(dataPairs);
  }
};

dojo.widget.defineWidget(
  "struts.widget.ComboBox",
  dojo.widget.ComboBox, {
  widgetType : "ComboBox",

  dropdownHeight: 120,
  dropdownWidth: 0,
  itemHeight: 0,

  listenTopics : "",
  notifyTopics : "",
  notifyTopicsArray : null,

  indicator : "",

  formId : "",
  formFilter : "",
  dataProviderClass: "struts.widget.ComboBoxDataProvider",

  loadOnType : false,
  loadMinimum : 3,

  initialValue : "",
  initialKey : "",

  visibleDownArrow : true,
  fadeTime : 100,

  //dojo has "stringstart" which is invalid
  searchType: "STARTSTRING",

  dataFieldName : ""  ,
  keyName: "",
  templateCssPath: dojo.uri.dojoUri("struts/ComboBox.css"),
  
  searchLimit : 30,
  //from Dojo's  ComboBox
  showResultList: function() {
  // Our dear friend IE doesnt take max-height so we need to calculate that on our own every time
    var childs = this.optionsListNode.childNodes;
    if(childs.length){

      this.optionsListNode.style.width = this.dropdownWidth === 0 ? (dojo.html.getMarginBox(this.domNode).width-2)+"px" : this.dropdownWidth + "px";

      if(this.itemHeight === 0 || dojo.string.isBlank(this.textInputNode.value)) {
        this.optionsListNode.style.height = this.dropdownHeight + "px";
        this.optionsListNode.style.display = "";
        this.itemHeight = dojo.html.getMarginBox(childs[0]).height;
      }

      //if there is extra space, adjust height
      var totalHeight = this.itemHeight * childs.length;
      if(totalHeight < this.dropdownHeight) {
        this.optionsListNode.style.height = totalHeight + 2 + "px";
      }

      this.popupWidget.open(this.domNode, this, this.downArrowNode);
    } else {
        this.hideResultList();
    }
  },

  openResultList: function(/*Array*/ results){
    if (!this.isEnabled){
        return;
    }
    this.clearResultList();
    if(!results.length){
        this.hideResultList();
    }

    if( (this.autoComplete)&&
        (results.length)&&
        (!this._prev_key_backspace)&&
        (this.textInputNode.value.length > 0)){
        var cpos = this.getCaretPos(this.textInputNode);
        // only try to extend if we added the last character at the end of the input
        if((cpos+1) > this.textInputNode.value.length){
            // only add to input node as we would overwrite Capitalisation of chars
            this.textInputNode.value += results[0][0].substr(cpos);
            // build a new range that has the distance from the earlier
            // caret position to the end of the first string selected
            this.setSelectedRange(this.textInputNode, cpos, this.textInputNode.value.length);
        }
    }
    var typedText = this.textInputNode.value;
    var even = true;
    while(results.length){
        var tr = results.shift();
        if(tr){
            var td = document.createElement("div");
            var text = tr[0];
            var i = text.toLowerCase().indexOf(typedText.toLowerCase());
            if(i >= 0) {
                var pre = text.substring(0, i);
                var matched = text.substring(i, i + typedText.length);
                var post = text.substring(i + typedText.length);

                if(!dojo.string.isBlank(pre)) {
                  td.appendChild(document.createTextNode(pre));
                }
                var boldNode = document.createElement("b");
                td.appendChild(boldNode);
                boldNode.appendChild(document.createTextNode(matched));
                td.appendChild(document.createTextNode(post));
            } else {
                td.appendChild(document.createTextNode(tr[0]));
            }

            td.setAttribute("resultName", tr[0]);
            td.setAttribute("resultValue", tr[1]);
            td.className = "dojoComboBoxItem "+((even) ? "dojoComboBoxItemEven" : "dojoComboBoxItemOdd");
            even = (!even);
            this.optionsListNode.appendChild(td);
        }
    }

    // show our list (only if we have content, else nothing)
    this.showResultList();
  },

  postCreate : function() {
    struts.widget.ComboBox.superclass.postCreate.apply(this);
    var self = this;
    //events
    if(!dojo.string.isBlank(this.listenTopics)) {
      var topics = this.listenTopics.split(",");
      for(var i = 0; i < topics.length; i++) {
        dojo.event.topic.subscribe(topics[i], function() {
          var request = {cancel: false};
	      self.notify(this.widgetId, "before", request);
	      if(request.cancel) {
	        return;
	      }
          self.clearValues();
          self.dataProvider.getData(self.dataUrl);
        });
      }
    }

    if(!dojo.string.isBlank(this.notifyTopics)) {
      this.notifyTopicsArray = this.notifyTopics.split(",");
    }

    //better name
    this.comboBoxSelectionValue.name = dojo.string.isBlank(this.keyName) ? this.name + "Key" : this.keyName;

    //init values
    this.comboBoxValue.value = this.initialValue;
    this.comboBoxSelectionValue.value = this.initialKey;
    this.textInputNode.value = this.initialValue;

    //hide arrow?
    if(!this.visibleDownArrow) {
      dojo.html.hide(this.downArrowNode);
    }

    //search type
    if(!dojo.string.isBlank(this.searchType)) {
      this.dataProvider.searchType = this.searchType.toUpperCase();
    }
  },

  clearValues : function() {
  	this.comboBoxValue.value = "";
    this.comboBoxSelectionValue.value = "";
    this.textInputNode.value = "";
  },

  onValueChanged : function(data) {
    this.notify(data, "valuechanged", null);
  },

  notify : function(data, type, e) {
    if(this.notifyTopicsArray) {
      dojo.lang.forEach(this.notifyTopicsArray, function(topic) {
        try {
          dojo.event.topic.publish(topic, data, type, e);
        } catch(ex) {
          dojo.debug(ex);
        }
      });
    }
  },

  startSearchFromInput: function() {
    var searchStr = this.textInputNode.value;
    if(this.loadOnType) {
    	if(searchStr.length >= this.loadMinimum) {
    	    var nuHref = this.dataUrl + (this.dataUrl.indexOf("?") > -1 ? "&" : "?");
   		nuHref += this.name + '=' + searchStr;
   		this.dataProvider.getData(nuHref);
   		this.startSearch(searchStr);
    	} else {
           this.hideResultList();
        }
    }
    else {
	  this.startSearch(searchStr);
	}
  }
});
