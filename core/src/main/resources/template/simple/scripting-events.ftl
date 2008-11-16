<#--
/*
 * $Id: Action.java 502296 2007-02-01 17:33:39Z niallp $
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
<#if parameters.onclick?exists>
 onclick="${parameters.onclick?html}"<#rt/>
</#if>
<#if parameters.ondblclick?exists>
 ondblclick="${parameters.ondblclick?html}"<#rt/>
</#if>
<#if parameters.onmousedown?exists>
 onmousedown="${parameters.onmousedown?html}"<#rt/>
</#if>
<#if parameters.onmouseup?exists>
 onmouseup="${parameters.onmouseup?html}"<#rt/>
</#if>
<#if parameters.onmouseover?exists>
 onmouseover="${parameters.onmouseover?html}"<#rt/>
</#if>
<#if parameters.onmousemove?exists>
 onmousemove="${parameters.onmousemove?html}"<#rt/>
</#if>
<#if parameters.onmouseout?exists>
 onmouseout="${parameters.onmouseout?html}"<#rt/>
</#if>
<#if parameters.onfocus?exists>
 onfocus="${parameters.onfocus?html}"<#rt/>
</#if>
<#if parameters.onblur?exists>
 onblur="${parameters.onblur?html}"<#rt/>
</#if>
<#if parameters.onkeypress?exists>
 onkeypress="${parameters.onkeypress?html}"<#rt/>
</#if>
<#if parameters.onkeydown?exists>
 onkeydown="${parameters.onkeydown?html}"<#rt/>
</#if>
<#if parameters.onkeyup?exists>
 onkeyup="${parameters.onkeyup?html}"<#rt/>
</#if>
<#if parameters.onselect?exists>
 onselect="${parameters.onselect?html}"<#rt/>
</#if>
<#if parameters.onchange?exists>
 onchange="${parameters.onchange?html}"<#rt/>
</#if>