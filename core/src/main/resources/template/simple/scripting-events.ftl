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
<#if parameters.onclick??>
 onclick="<#outputformat 'JavaScript'>${parameters.onclick}</#outputformat>"<#rt/>
</#if>
<#if parameters.ondblclick??>
 ondblclick="<#outputformat 'JavaScript'>${parameters.ondblclick}</#outputformat>"<#rt/>
</#if>
<#if parameters.onmousedown??>
 onmousedown="<#outputformat 'JavaScript'>${parameters.onmousedown}</#outputformat>"<#rt/>
</#if>
<#if parameters.onmouseup??>
 onmouseup="<#outputformat 'JavaScript'>${parameters.onmouseup}</#outputformat>"<#rt/>
</#if>
<#if parameters.onmouseover??>
 onmouseover="<#outputformat 'JavaScript'>${parameters.onmouseover}</#outputformat>"<#rt/>
</#if>
<#if parameters.onmousemove??>
 onmousemove="<#outputformat 'JavaScript'>${parameters.onmousemove}</#outputformat>"<#rt/>
</#if>
<#if parameters.onmouseout??>
 onmouseout="<#outputformat 'JavaScript'>${parameters.onmouseout}</#outputformat>"<#rt/>
</#if>
<#if parameters.onfocus??>
 onfocus="<#outputformat 'JavaScript'>${parameters.onfocus}</#outputformat>"<#rt/>
</#if>
<#if parameters.onblur??>
 onblur="<#outputformat 'JavaScript'>${parameters.onblur}</#outputformat>"<#rt/>
</#if>
<#if parameters.onkeypress??>
 onkeypress="<#outputformat 'JavaScript'>${parameters.onkeypress}</#outputformat>"<#rt/>
</#if>
<#if parameters.onkeydown??>
 onkeydown="<#outputformat 'JavaScript'>${parameters.onkeydown}</#outputformat>"<#rt/>
</#if>
<#if parameters.onkeyup??>
 onkeyup="<#outputformat 'JavaScript'>${parameters.onkeyup}</#outputformat>"<#rt/>
</#if>
<#if parameters.onselect??>
 onselect="<#outputformat 'JavaScript'>${parameters.onselect}</#outputformat>"<#rt/>
</#if>
<#if parameters.onchange??>
 onchange="<#outputformat 'JavaScript'>${parameters.onchange}</#outputformat>"<#rt/>
</#if>