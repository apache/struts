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

<#-- 
 Code that will add javascript needed for tooltips
--><#t/>
<#if (parameters.hasTooltip?default(false))><#t/>
	<#lt/><!-- javascript that is needed for tooltips -->
	<#lt/><script type="text/javascript" src='<@s.url value="/struts/domTT.js" includeParams="none" encode="false" />'></script>
	<#lt/><link rel="stylesheet" type="text/css" href="<@s.url value="/struts/domTT.css" includeParams="none" encode="false" />"/>
	
</#if><#t/>