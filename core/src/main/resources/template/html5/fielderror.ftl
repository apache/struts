<#--
/*
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
<@s.compress>
    <#if fieldErrors??>
        <#assign eKeys = fieldErrors.keySet()>
        <#assign eKeysSize = eKeys.size()>
        <#assign doneStartUlTag=false>
        <#assign doneEndUlTag=false>
        <#assign haveMatchedErrorField=false>
        <#if (fieldErrorFieldNames?size > 0) >
            <#list fieldErrorFieldNames as fieldErrorFieldName>
                <#list eKeys as eKey>
                    <#if (eKey = fieldErrorFieldName)>
                        <#assign haveMatchedErrorField=true>
                        <#assign eValue = fieldErrors.get(fieldErrorFieldName)>
                        <#if (haveMatchedErrorField && (!doneStartUlTag))>
                            <ul
                            <#if attributes.id?has_content>
                                id="${attributes.id}"
                            </#if>
                            <#if attributes.cssClass?has_content>
                                class="${attributes.cssClass}"
                            <#else>
                                class="errorMessage"
                            </#if>
                            <#if attributes.cssStyle?has_content>
                                style="${attributes.cssStyle}"
                            </#if>
                            >
                            <#assign doneStartUlTag=true>
                        </#if>
                        <#list eValue as eEachValue>
                            <li><span><#if attributes.escape>${eEachValue!}<#else>${eEachValue!?no_esc}</#if></span>
                            </li>
                        </#list>
                    </#if>
                </#list>
            </#list>
            <#if (haveMatchedErrorField && (!doneEndUlTag))>
                </ul>
                <#assign doneEndUlTag=true>
            </#if>
        <#else>
            <#if (eKeysSize > 0)>
                <ul
                        <#if attributes.cssClass?has_content>
                            class="${attributes.cssClass}"
                        <#else>
                            class="errorMessage"
                        </#if>
                        <#if attributes.cssStyle?has_content>
                            style="${attributes.cssStyle}"
                        </#if>
                >
                    <#list eKeys as eKey>
                        <#assign eValue = fieldErrors.get(eKey)>
                        <#list eValue as eEachValue>
                            <li><span><#if attributes.escape>${eEachValue!}<#else>${eEachValue!?no_esc}</#if></span>
                            </li>
                        </#list>
                    </#list>
                </ul>
            </#if>
        </#if>
    </#if>
</@s.compress>
