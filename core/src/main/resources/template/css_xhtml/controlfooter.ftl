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
${attributes.after!}<#t/>
<#if !attributes.labelPosition?? && (attributes.form.labelPosition)??>
<#assign labelPos = attributes.form.labelPosition/>
<#elseif attributes.labelPosition??>
<#assign labelPos = attributes.labelPosition/>
</#if>
<#if (labelPos!"top") == 'top'>
</div><#rt/>
<#else>
</span><#rt/>
</#if>
<#if (attributes.errorposition!"top") == 'bottom'>
<#assign hasFieldErrors = attributes.name?? && fieldErrors?? && fieldErrors.get(attributes.name)??/>
<#if hasFieldErrors>
<div <#rt/><#if attributes.id??>id="wwerr_${attributes.id}"<#rt/></#if> class="wwerr">
<#list fieldErrors.get(attributes.name) as error>
<div<#rt/>
<#if attributes.id??>
 errorFor="${attributes.id}"<#rt/>
</#if>
 class="errorMessage">${error}</div><#rt/>
</#list>
</div><#t/>
</#if>
</#if>
</div>
