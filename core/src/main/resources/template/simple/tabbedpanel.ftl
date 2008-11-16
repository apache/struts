<#--
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
-->
<script type="text/javascript">
  dojo.require("dojo.widget.TabContainer");
  dojo.require("dojo.widget.LinkPane");
  dojo.require("dojo.widget.ContentPane");
  <#if parameters.useSelectedTabCookie?exists && parameters.useSelectedTabCookie=="true">
  dojo.require("dojo.io.cookie");
  dojo.addOnLoad (
        function() {
            var tabContainer = dojo.widget.byId("${parameters.escapedId?html}");

            <#if !(parameters.selectedTab?if_exists != "")>
            var selectedTab = dojo.io.cookie.getCookie("Struts2TabbedPanel_selectedTab_${parameters.escapedId?html}");
            if (selectedTab) {
                tabContainer.selectChild(selectedTab, tabContainer.correspondingPageButton);
            }

            </#if>
            dojo.event.connect(tabContainer, "selectChild",
                    function (evt) {
                        dojo.io.cookie.setCookie("Struts2TabbedPanel_selectedTab_${parameters.escapedId?html}", evt.widgetId, 1, null, null, null);
                    }
                )
            }
        );
  </#if>
</script>

<div dojoType="TabContainer"
  <#if parameters.cssStyle?if_exists != "">
    style="${parameters.cssStyle?html}"<#rt/>
  </#if>
  <#if parameters.id?if_exists != "">
    id="${parameters.id?html}"<#rt/>
  </#if>
  <#if parameters.cssClass?if_exists != "">
    class="${parameters.cssClass?html}"<#rt/>
  </#if>
  <#if parameters.selectedTab?if_exists != "">
    selectedTab="${parameters.selectedTab?html}"<#rt/>
  </#if>
  <#if parameters.labelPosition?if_exists != "">
    labelPosition="${parameters.labelPosition?html}"<#rt/>
  </#if>
  <#if parameters.closeButton?if_exists != "">
    closeButton="${parameters.closeButton?html}"<#rt/>
  </#if>
  <#if parameters.doLayout?exists>
    doLayout="${parameters.doLayout?string?html}"<#rt/>
  </#if>
  <#if parameters.templateCssPath?exists>
	templateCssPath="<@s.url value='${parameters.templateCssPath}' encode="false" includeParams='none'/>"
  </#if>
>