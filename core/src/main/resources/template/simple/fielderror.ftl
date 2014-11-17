<#--
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
-->
<#if fieldErrors??><#t/>
    <#assign eKeys = fieldErrors.keySet()><#t/>
    <#assign eKeysSize = eKeys.size()><#t/>
    <#assign doneStartUlTag=false><#t/>
    <#assign doneEndUlTag=false><#t/>
    <#assign haveMatchedErrorField=false><#t/>
    <#if (fieldErrorFieldNames?size > 0) ><#t/>
        <#list fieldErrorFieldNames as fieldErrorFieldName><#t/>
            <#list eKeys as eKey><#t/>
                <#if (eKey = fieldErrorFieldName)><#t/>
                    <#assign haveMatchedErrorField=true><#t/>
                    <#assign eValue = fieldErrors[fieldErrorFieldName]><#t/>
                    <#if (haveMatchedErrorField && (!doneStartUlTag))><#t/>
                    <ul<#rt/>
                        <#if parameters.id?has_content>
                                id="${parameters.id?html}"<#rt/>
                        </#if>
                        <#if parameters.cssClass?has_content>
                                class="${parameters.cssClass?html}"<#rt/>
                            <#else>
                                class="errorMessage"<#rt/>
                        </#if>
                        <#if parameters.cssStyle?has_content>
                                style="${parameters.cssStyle?html}"<#rt/>
                        </#if>
                            >
                        <#assign doneStartUlTag=true><#t/>
                    </#if><#t/>
                    <#list eValue as eEachValue><#t/>
                        <li><span><#if parameters.escape>${eEachValue!?html}<#else>${eEachValue!}</#if></span></li>
                    </#list><#t/>
                </#if><#t/>
            </#list><#t/>
        </#list><#t/>
        <#if (haveMatchedErrorField && (!doneEndUlTag))><#t/>
        </ul>
            <#assign doneEndUlTag=true><#t/>
        </#if><#t/>
        <#else><#t/>
        <#if (eKeysSize > 0)><#t/>
        <ul<#rt/>
            <#if parameters.cssClass?has_content>
                    class="${parameters.cssClass?html}"<#rt/>
                <#else>
                    class="errorMessage"<#rt/>
            </#if>
            <#if parameters.cssStyle?has_content>
                    style="${parameters.cssStyle?html}"<#rt/>
            </#if>
                >
            <#list eKeys as eKey><#t/>
                <#assign eValue = fieldErrors[eKey]><#t/>
                <#list eValue as eEachValue><#t/>
                    <li><span><#if parameters.escape>${eEachValue!?html}<#else>${eEachValue!}</#if></span></li>
                </#list><#t/>
            </#list><#t/>
        </ul>
        </#if><#t/>
    </#if><#t/>
</#if><#t/>