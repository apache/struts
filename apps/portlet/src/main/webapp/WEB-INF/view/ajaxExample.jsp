<%--
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
--%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<s:head theme="ajax"/>
<link rel="stylesheet" type="text/css" href="<s:url value="/struts/tabs.css"/>">
<b>This is a tabbed pane with two panels that fetches data from a remote action via ajax</b>

<s:tabbedPanel id="test2" theme="simple" >
      <s:div id="left" label="left" theme="ajax">
          This is the left pane<br/>
          <s:form >
              <s:textfield name="tt" label="Test Text" />  <br/>
              <s:textfield name="tt2" label="Test Text2" />
          </s:form>
      </s:div>
      <s:div href="<s:url action="ajaxData"/>" id="ryh1" theme="ajax" label="remote one" />
      <s:div id="middle" label="middle" theme="ajax">
          middle tab<br/>
          <s:form >
              <s:textfield name="tt" label="Test Text44" />  <br/>
              <s:textfield name="tt2" label="Test Text442" />
          </s:form>
      </s:div>
      <s:div href="<s:url action="ajaxData"/>"  id="ryh21" theme="ajax" label="remote right" />
  </s:tabbedPanel>
  
<p/>
A DIV that waits for 5 seconds before loading the contents
<s:div
        id="once"
        theme="ajax"
        cssStyle="border: 1px solid yellow;"
        href="<s:url action="ajaxData"/>"
        delay="5000"
        loadingText="loading...">
    Waiting for data</s:div>
<p/>
A DIV that is updated every 2 seconds
<s:div
            id="twoseconds"
            cssStyle="border: 1px solid yellow;"
            href="<s:url action="ajaxData"/>"
            theme="ajax"
            delay="2000"
            updateFreq="2000"
            errorText="There was an error"
            loadingText="loading...">Initial Content
    </s:div>
<p/>
<a href="<s:url action="index"/>">Back to front page</a>
