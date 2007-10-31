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

dojo.provide("struts.widget.BindEvent");

dojo.require("struts.widget.Bind");

dojo.widget.defineWidget(
  "struts.widget.BindEvent",
  struts.widget.Bind, {
  widgetType : "BindEvent",

  sources: "",

  postCreate : function() {
    struts.widget.BindEvent.superclass.postCreate.apply(this);
    var self = this;

    if(!dojo.string.isBlank(this.events) && !dojo.string.isBlank(this.sources)) {
      var eventsArray = this.events.split(",");
      var sourcesArray = this.sources.split(",");

      if(eventsArray && this.domNode) {
        //events
        dojo.lang.forEach(eventsArray, function(event) {
          //sources
          dojo.lang.forEach(sourcesArray, function(source) {
            var sourceObject = dojo.byId(source);
            if(sourceObject) {
              dojo.event.connect(sourceObject, event, function(evt) {
                evt.preventDefault();
                evt.stopPropagation();
                self.reloadContents();
              });
            }
          });
        });
      }
    }
  }
});