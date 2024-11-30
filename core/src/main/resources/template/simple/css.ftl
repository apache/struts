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
<#assign hasFieldErrors = attributes.name?? && fieldErrors?? && fieldErrors.get(attributes.name)??/>
<#if attributes.cssClass?has_content && !(hasFieldErrors && attributes.cssErrorClass??)>
 class="${attributes.cssClass}"<#rt/>
<#elseif attributes.cssClass?has_content && (hasFieldErrors && attributes.cssErrorClass??)>
 class="${attributes.cssClass} ${attributes.cssErrorClass}"<#rt/>
<#elseif !(attributes.cssClass?has_content) && (hasFieldErrors && attributes.cssErrorClass??)>
 class="${attributes.cssErrorClass}"<#rt/>
</#if>
<#if attributes.cssStyle?has_content && !(hasFieldErrors && (attributes.cssErrorStyle?? || attributes.cssErrorClass??))>
 style="${attributes.cssStyle}"<#rt/>
<#elseif hasFieldErrors && attributes.cssErrorStyle??>
 style="${attributes.cssErrorStyle}"<#rt/>
</#if>